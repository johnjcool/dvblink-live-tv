
package io.github.johnjcool.dvblink.live.tv.tv.session;

import android.content.ContentResolver;
import android.content.Context;
import android.media.tv.TvInputManager;
import android.net.Uri;
import android.util.Log;

import com.google.android.media.tv.companionlibrary.BaseTvInputService;
import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.model.InternalProviderData;
import com.google.android.media.tv.companionlibrary.model.ModelUtils;
import com.google.android.media.tv.companionlibrary.model.Program;
import com.google.android.media.tv.companionlibrary.model.RecordedProgram;
import com.google.android.media.tv.companionlibrary.utils.TvContractUtils;

import java.util.concurrent.TimeUnit;

import io.github.johnjcool.dvblink.live.tv.Constants;
import io.github.johnjcool.dvblink.live.tv.di.Injector;
import io.github.johnjcool.dvblink.live.tv.remote.DvbLinkClient;
import io.github.johnjcool.dvblink.live.tv.remote.model.request.Schedule;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.RecordedTV;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.Recording;
import io.github.johnjcool.dvblink.live.tv.tv.TvUtils;

public class RecordingSession extends BaseTvInputService.RecordingSession {

    private static final long DEFAULT_CHANNEL_RECORDING_DURATION = TimeUnit.HOURS.convert(1, TimeUnit.SECONDS);

    private static final String TAG = RecordingSession.class.getSimpleName();
    private Channel mChannel;
    private Uri mChannelUri;
    private Context mContext;
    private String mInputId;
    private Recording mRecording;
    private DvbLinkClient mDvbLinkClient;
    private String mHost;

    public RecordingSession(Context context, String inputId) {
        super(context, inputId);
        mInputId = inputId;
        mContext = context;
        mDvbLinkClient = Injector.get().dvbLinkClient();
        mHost = Injector.get().host();
    }

    public void onStartRecording(final Uri programUri) {
        super.onStartRecording(programUri);
        new Thread() {
            @Override
            public void run() {
                Log.d(TAG, (new StringBuilder()).append("onStartRecording: ").append(programUri).toString());
                ContentResolver resolver = mContext.getContentResolver();
                mChannel = ModelUtils.getChannel(resolver, mChannelUri);
                if (programUri != null) {
                    Program program = TvUtils.getRecordingProgram(resolver, mChannelUri, programUri);
                    if (program == null) {
                        notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN);
                    } else {
                        startProgramRecording(program);
                    }
                } else {
                    startChannelRecording();
                }
            }
        }.start();
    }

    public void onStopRecording(final Program programToRecord) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "onStopRecording");
                    mDvbLinkClient.removeRecording(mRecording.getRecordingId());
                    createRecordedProgram(programToRecord);
                } catch (Exception e) {
                    Log.e(RecordingSession.TAG, (new StringBuilder())
                            .append("onStopRecording, program: ")
                            .append(programToRecord.getTitle())
                            .append(", channel: ")
                            .append(mChannel.getDisplayName())
                            .append("\n")
                            .append(e)
                            .toString());
                    notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN);
                }
            }
        }.start();
    }

    public void onStopRecordingChannel(final Channel channelToRecord) {
        new Thread() {
            @Override
            public void run() {
                Log.d(TAG, "onStopRecordingChannel");
                try {
                    Log.d(TAG, "onStopRecording");
                    mDvbLinkClient.removeRecording(mRecording.getRecordingId());
                    createRecordedChannel(channelToRecord);
                } catch (Exception e) {
                    Log.e(RecordingSession.TAG, (new StringBuilder())
                            .append("onStopRecording, channel: ")
                            .append(channelToRecord.getDisplayName())
                            .append("\n")
                            .append(e)
                            .toString());
                    notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN);
                }
            }
        }.start();
    }

    public void onTune(Uri uri) {
        super.onTune(uri);
        Log.d(TAG, (new StringBuilder()).append("Tune recording session to: ").append(uri).toString());
        mChannelUri = uri;
        notifyTuned(uri);
    }

    public void notifyRecordingStopped(RecordedProgram recordedprogram) {
        notifyRecordingStopped(mContext.getContentResolver().insert(android.media.tv.TvContract.RecordedPrograms.CONTENT_URI, recordedprogram.toContentValues()));
    }

    public void onRelease() {
        Log.d(TAG, "onRelease");
    }

    // private internal shelper methods
    private void startProgramRecording(final Program program) {
        try {
            Log.d(TAG, (new StringBuilder())
                    .append("startProgramRecording: ")
                    .append(mChannel.getOriginalNetworkId())
                    .append(", program title: ")
                    .append(program.getTitle()).toString());

            Schedule schedule = new Schedule(
                    new Schedule.ByEpg(
                            String.valueOf(mChannel.getOriginalNetworkId()),
                            String.valueOf(program.getInternalProviderData().get(Constants.KEY_ORGINAL_OBJECT_ID))
                    )
            );
            mRecording = mDvbLinkClient.addSchedule(schedule);
            Log.d(TAG, "Recording for channel " + mChannel.getDisplayName() + " and programm " + program.getTitle() + " successfully scheduled.");
        } catch (Exception e) {
            Log.e(TAG, "Exception schedule recording for channel " + mChannel.getDisplayName() + " and programm " + program.getTitle() + ".\n" + e.fillInStackTrace());
            notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN);
        }

    }

    private void createRecordedProgram(final Program programToRecord) throws Exception {
        RecordedTV recordedTV = mDvbLinkClient.getRecordedProgram(mRecording.getScheduleId());

        InternalProviderData data = null;
        try {
            data = new InternalProviderData(programToRecord.getInternalProviderDataByteArray());
            data.put(Constants.KEY_ORGINAL_OBJECT_ID, recordedTV.getObjectId());
            data.setVideoUrl(recordedTV.getUrl());
            data.setVideoType(TvContractUtils.SOURCE_TYPE_HTTP_PROGRESSIVE);
            data.setRecordingStartTime(TvUtils.transformToMillis(recordedTV.getVideoInfo().getStartTime()));
        } catch (InternalProviderData.ParseException e) {
            Log.e(TAG, "Error parsing orginal program id.", e);
        }

        RecordedProgram recordedProgram = new RecordedProgram.Builder(programToRecord)
                .setInternalProviderData(data)
                .setInputId(mInputId)
                .setStartTimeUtcMillis(TvUtils.transformToMillis(recordedTV.getVideoInfo().getStartTime()))
                .setEndTimeUtcMillis(TvUtils.transformToMillis(recordedTV.getVideoInfo().getStartTime() + recordedTV.getVideoInfo().getDuration()))
                .setRecordingDataUri(TvUtils.transformLocalhostToHost(recordedTV.getUrl(), mHost))
                .setThumbnailUri(TvUtils.transformLocalhostToHost(recordedTV.getThumbnail(), mHost))
                .build();

        notifyRecordingStopped(recordedProgram);
    }


    private void startChannelRecording() {
        try {
            // TODO: Disable Channel Recording...
            Log.d(TAG, (new StringBuilder()).append("startChannelRecording: ").append(mChannel.getDisplayName()).toString());
            Schedule schedule = new Schedule(new Schedule.Manual(String.valueOf(mChannel.getOriginalNetworkId()),
                    TvUtils.transformToSeconds(System.currentTimeMillis()),
                    DEFAULT_CHANNEL_RECORDING_DURATION,
                    Schedule.DayMask.DAY_MASK_DAILY));
            mRecording = mDvbLinkClient.addSchedule(schedule);
            Log.d(TAG, "Recording for channel " + mChannel.getDisplayName() + " successfully scheduled.");
        } catch (Exception e) {
            Log.e(TAG, "Exception schedule recording for channel " + mChannel.getDisplayName() + ".\n" + e.fillInStackTrace());
            notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN);
        }
    }

    private void createRecordedChannel(final Channel channelToRecord) throws Exception {
        RecordedTV recordedTV = mDvbLinkClient.getRecordedProgram(mRecording.getScheduleId());

        InternalProviderData internalProviderData = channelToRecord.getInternalProviderData();
        internalProviderData.setVideoUrl(recordedTV.getUrl());
        internalProviderData.setRecordingStartTime(TvUtils.transformToMillis(recordedTV.getCreationTime()));

        long startTimeUtcMillis = TvUtils.transformToMillis(recordedTV.getCreationTime());
        long recordingDurationMillis = TvUtils.transformToMillis(recordedTV.getVideoInfo().getDuration());
        long endTimeUtcMillis = startTimeUtcMillis + recordingDurationMillis;

        RecordedProgram recordedProgram = new RecordedProgram.Builder()
                .setInputId(mInputId)
                .setRecordingDataUri(recordedTV.getUrl())
                .setRecordingDurationMillis(recordingDurationMillis)
                .setStartTimeUtcMillis(startTimeUtcMillis)
                .setEndTimeUtcMillis(endTimeUtcMillis)
                .setInternalProviderData(internalProviderData)
                .setTitle(String.format("%s - %s", channelToRecord.getDisplayName(), recordedTV.getScheduleName()))
                .setThumbnailUri(recordedTV.getThumbnail())
                .build();

        notifyRecordingStopped(recordedProgram);
    }
}
