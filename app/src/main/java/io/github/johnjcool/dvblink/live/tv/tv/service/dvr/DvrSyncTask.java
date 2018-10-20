package io.github.johnjcool.dvblink.live.tv.tv.service.dvr;

import android.content.Context;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.media.tv.companionlibrary.model.InternalProviderData;
import com.google.android.media.tv.companionlibrary.model.Program;
import com.google.android.media.tv.companionlibrary.model.RecordedProgram;
import com.google.android.media.tv.companionlibrary.utils.TvContractUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.johnjcool.dvblink.live.tv.Constants;
import io.github.johnjcool.dvblink.live.tv.di.Injector;
import io.github.johnjcool.dvblink.live.tv.remote.DvbLinkClient;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.RecordedTV;
import io.github.johnjcool.dvblink.live.tv.tv.TvUtils;

public class DvrSyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = DvrSyncTask.class.getSimpleName();

    private Context mContext;

    private DvbLinkClient mDvbLinkClient;
    private String mHost;
    private String mInputId;

    public DvrSyncTask(Context context, String inputId) {
        mContext = context;
        mInputId = inputId;
        mDvbLinkClient = Injector.get().dvbLinkClient();
        mHost = Injector.get().host();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Log.d(TAG, "doInBackground: START");
            List<RecordedTV> recordedTVs = mDvbLinkClient.getRecordedPrograms();
            if (recordedTVs == null || recordedTVs.isEmpty()) {
                Log.d(TAG, "doInBackground: ERROR FETCHING RECORDS LIST");
            } else {
                syncRecordedRecords(recordedTVs);
                Log.d(TAG, "doInBackground: FINISH");
            }
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ERROR SYNCHING RECORDS.", e);
        } finally {
            return null;
        }
    }

    private void syncRecordedRecords(List<RecordedTV> recordedTVs) {
        Map<String, RecordedTV> remoteRecordedProgramMap = buildRemoteRecordedProgramMap(recordedTVs);
        Map<String, Uri> tifRecordedProgramUriMap = TvUtils.getRecordedProgramUriMapFromTif(
                mContext.getContentResolver(),
                TvContract.RecordedPrograms.CONTENT_URI
        );

        // DELETE FROM TIF
        Log.d(TAG, new StringBuilder()
                .append("doInBackground, DvbLink recorded records: ")
                .append(remoteRecordedProgramMap.size())
                .append(", TIF recorded records: ")
                .append(tifRecordedProgramUriMap.size())
                .toString());

        Map<String, Uri> toDeleteTifRecordedProgramUriMap = tifRecordedProgramUriMap
                .entrySet()
                .stream()
                .filter(k -> !remoteRecordedProgramMap.containsKey(k.getKey()))
                .collect(Collectors.toMap(k -> k.getKey(), k -> k.getValue()));

        Log.d(TAG, new StringBuilder()
                .append("syncRecordedRecords, REMOVING INVALID KEYS FROM RECORDED MAP: ")
                .append(toDeleteTifRecordedProgramUriMap.size())
                .toString());

        toDeleteTifRecordedProgramUriMap.entrySet().forEach(k -> {
            mContext.getContentResolver().delete(k.getValue(), null, null);
        });

        // ADD TO TIF
        Map<String, RecordedTV> toAddRecordedProgramMap = remoteRecordedProgramMap
                .entrySet()
                .stream()
                .filter(k -> !tifRecordedProgramUriMap.containsKey(k.getKey()))
                .collect(Collectors.toMap(k -> k.getKey(), k -> k.getValue()));

        Log.d(TAG, new StringBuilder()
                .append("syncRecordedRecords, NEW DVBLINK RECORDS: ")
                .append(toAddRecordedProgramMap.size())
                .toString());

        Function<RecordedTV, RecordedProgram> programTransform =
                new Function<RecordedTV, RecordedProgram>() {
                    public RecordedProgram apply(RecordedTV recordedTV) {
                        InternalProviderData data = null;
                        try {
                            data = new InternalProviderData();
                            data.put(Constants.KEY_ORGINAL_OBJECT_ID, recordedTV.getObjectId());
                            data.setRecordingStartTime(TvUtils.transformToMillis(recordedTV.getVideoInfo().getStartTime()));
                            data.setVideoUrl(TvUtils.transformLocalhostToHost(recordedTV.getUrl(), mHost));
                            data.setVideoType(TvContractUtils.SOURCE_TYPE_HTTP_PROGRESSIVE);
                        } catch (InternalProviderData.ParseException e) {
                            Log.e(TAG, "Error parsing orginal program id.", e);
                        }

                        Program program = new Program.Builder()
                                .setAudioLanguages(recordedTV.getVideoInfo().getLanguage())
                                .setCanonicalGenres(TvUtils.transformToGenres(recordedTV.getVideoInfo()))
                                .setChannelId(TvUtils.getChannelId(mContext.getContentResolver(), Long.valueOf(recordedTV.getChannelId())))
                                .setEpisodeNumber(recordedTV.getVideoInfo().getEpisodeNum())
                                .setEpisodeTitle(recordedTV.getScheduleName())
                                .setInternalProviderData(data)
                                .setDescription(recordedTV.getVideoInfo().getShortDesc())
                                .setPosterArtUri(TvUtils
                                        .transformLocalhostToHost(recordedTV.getVideoInfo().getImage(), mHost))
                                .setSearchable(true)
                                .setSeasonNumber(recordedTV.getVideoInfo().getSeasonNum())
                                .setStartTimeUtcMillis(TvUtils
                                        .transformToMillis(recordedTV.getVideoInfo().getStartTime()))
                                .setEndTimeUtcMillis(TvUtils
                                        .transformToMillis(recordedTV.getVideoInfo().getStartTime() + recordedTV.getVideoInfo().getDuration()))
                                .setThumbnailUri(recordedTV.getThumbnail())
                                .setTitle(recordedTV.getScheduleName())
                                .build();
                        return new RecordedProgram.Builder(program).setInputId(mInputId).build();
                    }
                };
        List<RecordedProgram> recordedPrograms = toAddRecordedProgramMap.values().stream().map(programTransform).collect(Collectors.<RecordedProgram>toList());
        recordedPrograms.forEach(record -> {
            mContext.getContentResolver().insert(android.media.tv.TvContract.RecordedPrograms.CONTENT_URI, record.toContentValues());
        });

        // Make app list actual
        TvUtils.updateRecordedProgramUriMapFromSharedPreferences(
                mContext,
                TvUtils.getRecordedProgramUriMapFromTif(
                        mContext.getContentResolver(),
                        TvContract.RecordedPrograms.CONTENT_URI
                )
        );
    }

    private Map<String, RecordedTV> buildRemoteRecordedProgramMap(List<RecordedTV> recordedTVs) {
        return recordedTVs
                .stream()
                .collect(Collectors.toMap(recordedTV -> recordedTV.getObjectId(),
                        recordedTV -> recordedTV));
    }
}
