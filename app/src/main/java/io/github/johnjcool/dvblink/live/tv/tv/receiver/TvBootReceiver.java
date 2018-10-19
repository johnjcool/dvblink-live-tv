package io.github.johnjcool.dvblink.live.tv.tv.receiver;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import io.github.johnjcool.dvblink.live.tv.tv.TvUtils;
import io.github.johnjcool.dvblink.live.tv.tv.service.dvr.DvrSyncService;
import io.github.johnjcool.dvblink.live.tv.tv.service.epg.EpgSyncJobService;

/**
 * This BroadcastReceiver is set up to make sure sync job can schedule after reboot. Because
 * JobScheduler doesn't work well on reboot scheduler on L/L-MR1.
 */
public class TvBootReceiver extends BroadcastReceiver {

    private static String TAG = TvBootReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) & TvUtils.isSetupComplete(context)) {
            Log.d(TAG, (new StringBuilder()).append("onReceive: ").append(intent).toString());
            JobScheduler jobScheduler =
                    (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            // If there are not pending jobs. Create a sync job and schedule it.
            List<JobInfo> pendingJobs = jobScheduler.getAllPendingJobs();
            Log.d(TAG, (new StringBuilder()).append("onReceive, pendingJobs: ").append(pendingJobs.size()).toString());

            if (pendingJobs.isEmpty()) {
                String inputId = context.getSharedPreferences(EpgSyncJobService.PREFERENCE_EPG_SYNC,
                        Context.MODE_PRIVATE).getString(EpgSyncJobService.BUNDLE_KEY_INPUT_ID, null);
                Log.d(TAG, (new StringBuilder()).append("onReceive, inputId: ").append(intent).toString());
                if (inputId != null) {
                    startEpgSync(context, inputId);
                    startRecordsSync(context, inputId);
                }
            }
        }
    }

    private void startEpgSync(Context context, String inputId) {
        Log.d(TAG, (new StringBuilder()).append("startEpgSync: ").append(inputId).toString());
        EpgSyncJobService.setUpPeriodicSync(context, inputId, new ComponentName(context, DvrSyncService.class));
    }

    private void startRecordsSync(Context context, String inputId) {
        Log.d(TAG, (new StringBuilder()).append("startRecordsSync: ").append(inputId).toString());
        DvrSyncService.setUpPeriodicSync(context, inputId, new ComponentName(context, DvrSyncService.class));
    }
}