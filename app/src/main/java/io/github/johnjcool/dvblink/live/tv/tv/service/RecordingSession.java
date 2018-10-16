
package io.github.johnjcool.dvblink.live.tv.tv.service;

import android.content.ContentResolver;
import android.content.Context;
import android.media.tv.TvInputManager;
import android.net.Uri;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.android.media.tv.companionlibrary.BaseTvInputService;
import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.model.InternalProviderData;
import com.google.android.media.tv.companionlibrary.model.ModelUtils;
import com.google.android.media.tv.companionlibrary.model.Program;
import com.google.android.media.tv.companionlibrary.model.RecordedProgram;

import java.util.concurrent.TimeUnit;

import io.github.johnjcool.dvblink.live.tv.Constants;
import io.github.johnjcool.dvblink.live.tv.di.Injector;
import io.github.johnjcool.dvblink.live.tv.remote.DvbLinkClient;
import io.github.johnjcool.dvblink.live.tv.remote.model.request.Schedule;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.RecordedTV;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.Recording;
import io.github.johnjcool.dvblink.live.tv.tv.TvUtils;

class RecordingSession extends BaseTvInputService.RecordingSession {

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

    @WorkerThread
    public void onStartRecording(Uri programUri) {
        super.onStartRecording(programUri);
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

    @WorkerThread
    public void onStopRecording(final Program programToRecord) {
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

    @WorkerThread
    public void onStopRecordingChannel(final Channel channelToRecord) {
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
    private void startProgramRecording(Program program) {
        try {
            Log.d(TAG, (new StringBuilder())
                    .append("startProgramRecording: ")
                    .append(mChannel.getOriginalNetworkId())
                    .append(", program title: ")
                    .append(program.getTitle()).toString());

            Schedule schedule = new Schedule(
                    new Schedule.ByEpg(
                            String.valueOf(mChannel.getOriginalNetworkId()),
                            String.valueOf(program.getInternalProviderData().get(Constants.KEY_ORGINAL_PROGRAM_ID))
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

        programToRecord.getInternalProviderData().setVideoUrl(recordedTV.getUrl());
        programToRecord.getInternalProviderData().setRecordingStartTime(TvUtils.transformToMillis(recordedTV.getCreationTime()));

        RecordedProgram recordedProgram = new RecordedProgram.Builder(programToRecord)
                .setInputId(mInputId)
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
