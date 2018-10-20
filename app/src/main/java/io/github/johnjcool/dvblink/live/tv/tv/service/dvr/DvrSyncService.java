package io.github.johnjcool.dvblink.live.tv.tv.service.dvr;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.os.PersistableBundle;
import android.util.Log;
import android.util.SparseArray;

import junit.framework.Assert;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.github.johnjcool.dvblink.live.tv.tv.TvUtils;

public class DvrSyncService extends JobService {

    private static final String TAG = DvrSyncService.class.getName();

    /**
     * The key representing the component name for the app's TvInputService.
     */
    public static final String BUNDLE_KEY_INPUT_ID =
            DvrSyncService.class.getPackage().getName() + ".bundle_key_input_id";


    private static final ExecutorService SINGLE_THREAD_EXECUTOR =
            Executors.newSingleThreadExecutor();

    private final SparseArray<DvrSyncTask> mTaskArray = new SparseArray<>();

    private static final int PERIODIC_SYNC_JOB_ID = 100;
    private static final int REQUEST_SYNC_JOB_ID = 101;
    private static final long OVERRIDE_DEADLINE_MILLIS = 1000; // 1 second


    private static final boolean DEBUG = true;
    private static final long DEFAULT_BACKOFF_TIME = TimeUnit.MILLISECONDS.convert(2, TimeUnit.MINUTES);
    private static final long DEFAULT_PERIODIC_RECORDS_SYNC_TIME = TimeUnit.MILLISECONDS.convert(15, TimeUnit.MINUTES);

    private static final Object mContextLock = new Object();
    private Context mContext;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Created DvrSyncService");
        synchronized (mContextLock) {
            if (mContext == null) {
                mContext = getApplicationContext();
            }
        }
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        if (DEBUG) {
            Log.d(TAG, "onStartJob(" + params.getJobId() + ")");
        }

        DvrSyncTask dvrSyncTask = new DvrSyncTask(mContext, params.getExtras().getString(BUNDLE_KEY_INPUT_ID));
        synchronized (mTaskArray) {
            mTaskArray.put(params.getJobId(), dvrSyncTask);
        }
        // Run the task on a single threaded custom executor in order not to block the AsyncTasks
        // running on application side.
        dvrSyncTask.executeOnExecutor(SINGLE_THREAD_EXECUTOR);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        synchronized (mTaskArray) {
            int jobId = params.getJobId();
            DvrSyncTask dvrSyncTask = mTaskArray.get(jobId);
            if (dvrSyncTask != null) {
                dvrSyncTask.cancel(true);
                mTaskArray.delete(params.getJobId());
            }
        }
        return false;
    }

    /**
     * Cancels all pending jobs.
     *
     * @param context Application's context.
     */
    public static void cancelAllSyncRequests(Context context) {
        JobScheduler jobScheduler =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();
    }

    /**
     * Manually requests a job to run now to retrieve EPG content for the next hour.
     *
     * @param context             Application's context.
     * @param inputId             Component name for the app's TvInputService. This can be received through an
     *                            Intent extra parameter {@link android.media.tv.TvInputInfo#EXTRA_INPUT_ID}.
     * @param jobServiceComponent The {@link DvrSyncService} class that will run.
     */
    public static void requestImmediateSync(Context context, String inputId, ComponentName jobServiceComponent) {
        if (jobServiceComponent.getClass().isAssignableFrom(DvrSyncService.class)) {
            throw new IllegalArgumentException("This class does not extend DvrSyncService");
        }
        PersistableBundle persistableBundle = new PersistableBundle();
        persistableBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        persistableBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        persistableBundle.putString(BUNDLE_KEY_INPUT_ID, inputId);

        JobInfo.Builder builder = new JobInfo.Builder(REQUEST_SYNC_JOB_ID, jobServiceComponent);
        JobInfo jobInfo =
                builder.setExtras(persistableBundle)
                        .setOverrideDeadline(OVERRIDE_DEADLINE_MILLIS)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setBackoffCriteria(DEFAULT_BACKOFF_TIME, JobInfo.BACKOFF_POLICY_EXPONENTIAL)
                        .build();
        scheduleJob(context, jobInfo);
        Log.d(TAG, "Single job scheduled");
    }

    /**
     * Send the job to JobScheduler.
     */
    private static void scheduleJob(Context context, JobInfo job) {
        JobScheduler jobScheduler =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int result = jobScheduler.schedule(job);
        Assert.assertEquals(JobScheduler.RESULT_SUCCESS, result);
        if (DEBUG) {
            Log.d(TAG, "Scheduling result is " + result);
        }
    }

    /**
     * Initializes a job that will periodically update the app's channels and programs with a
     * default period of 24 hours.
     *
     * @param context             Application's context.
     * @param inputId             Component name for the app's TvInputService. This can be received through an
     *                            Intent extra parameter {@link android.media.tv.TvInputInfo#EXTRA_INPUT_ID}.
     * @param jobServiceComponent The {@link DvrSyncService} component name that will run.
     */
    public static void setUpPeriodicSync(Context context, String inputId, ComponentName jobServiceComponent) {
        if (jobServiceComponent.getClass().isAssignableFrom(DvrSyncService.class)) {
            throw new IllegalArgumentException("This class does not extend DvrSyncService");
        }

        PersistableBundle persistableBundle = new PersistableBundle();
        persistableBundle.putString(DvrSyncService.BUNDLE_KEY_INPUT_ID, inputId);
        JobInfo.Builder builder = new JobInfo.Builder(PERIODIC_SYNC_JOB_ID, jobServiceComponent);
        JobInfo jobInfo =
                builder.setExtras(persistableBundle)
                        .setPeriodic(DEFAULT_PERIODIC_RECORDS_SYNC_TIME)
                        .setPersisted(true)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .build();
        scheduleJob(context, jobInfo);
        if (DEBUG) {
            Log.d(TAG, "Job has been scheduled for every " + DEFAULT_PERIODIC_RECORDS_SYNC_TIME + "ms");
        }
    }

    public static void syncRecordings(Context context) {
        setUpPeriodicSync(context, TvUtils.getInputId(), new ComponentName(context, DvrSyncService.class));
    }
}
