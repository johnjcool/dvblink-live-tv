package io.github.johnjcool.dvblink.live.tv.tv;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.media.tv.companionlibrary.model.InternalProviderData;
import com.google.android.media.tv.companionlibrary.model.ModelUtils;
import com.google.android.media.tv.companionlibrary.model.Program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.github.johnjcool.dvblink.live.tv.Constants;
import io.github.johnjcool.dvblink.live.tv.di.Injector;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.VideoInfo;

public class TvUtils {

    public static final long INVALID_CHANNEL_ID = -1;

    private TvUtils() {
        throw new IllegalAccessError("Utility class");
    }

    private static final String TAG = TvUtils.class.getName();

    public static void setSetupComplete(Context context, boolean isSetupComplete) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.PREFERENCE_TVHEADEND, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.KEY_SETUP_COMPLETE, isSetupComplete);
        editor.apply();
    }

    public static boolean isSetupComplete(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.PREFERENCE_TVHEADEND, Context.MODE_PRIVATE);

        return sharedPreferences.getBoolean(Constants.KEY_SETUP_COMPLETE, false);
    }



    public static void removeChannels(Context context) {
        Uri channelsUri = TvContract.buildChannelsUriForInput(getInputId());

        ContentResolver resolver = context.getContentResolver();

        String[] projection = {TvContract.Channels._ID, TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID};

        try (Cursor cursor = resolver.query(channelsUri, projection, null, null, null)) {
            while (cursor != null && cursor.moveToNext()) {
                long rowId = cursor.getLong(0);
                Log.d(TAG, "Deleting channel: " + rowId);
                resolver.delete(TvContract.buildChannelUri(rowId), null, null);
            }
        }
    }

    public static long transformToMillis(long seconds) {
        return TimeUnit.MILLISECONDS.convert(seconds, TimeUnit.SECONDS);
    }

    public static long transformToSeconds(long milliseconds) {
        return TimeUnit.SECONDS.convert(milliseconds, TimeUnit.MILLISECONDS);
    }

    public static String getInputId() {
        ComponentName componentName = new ComponentName(
                "io.github.johnjcool.dvblink.live.tv",
                ".tv.service.TvInputService");
        return TvContract.buildInputId(componentName);
    }

    public static Program getRecordingProgram(ContentResolver resolver, Uri channelUri, Uri programUri) {
        List<Program> programs = ModelUtils.getPrograms(resolver, channelUri);
        if (programs == null) {
            return null;
        }
        long l = Long.valueOf(programUri.getLastPathSegment()).longValue();
        for (Program program : programs) {
            if (program.getId() == l) {
                return program;
            }
        }
        return null;
    }

    public static Map<String, Uri> getRecordedProgramUriMapFromTif(ContentResolver resolver, Uri recordedProgramsUri) {
        // Create a map from original network ID to channel row ID for existing channels.
        Map<String, Uri> recordedProgramMap = new HashMap<>();
//        Uri recordedProgramsUri = TvContract.RecordedPrograms.CONTENT_URI;
        String[] projection = {TvContract.RecordedPrograms._ID, TvContract.RecordedPrograms.COLUMN_INTERNAL_PROVIDER_DATA};
        try (Cursor cursor = resolver.query(recordedProgramsUri, projection, null, null, null)) {
            while (cursor != null && cursor.moveToNext()) {
                long rowId = cursor.getLong(0);
                InternalProviderData internalProviderData = new InternalProviderData(cursor.getBlob(1));
                recordedProgramMap.put(String.valueOf(internalProviderData.get(Constants.KEY_ORGINAL_OBJECT_ID)), TvContract.buildRecordedProgramUri(rowId));
            }
        } catch (InternalProviderData.ParseException e) {
            Log.e(TAG, "Error in methode getRecordedProgramUriMapFromTif", e);
        }
        return recordedProgramMap;
    }

    public static long getChannelId(ContentResolver resolver, long orginalNetworkId) {
        Uri channelsUri = TvContract.buildChannelsUriForInput(getInputId());
        String[] projection = {TvContract.Channels._ID, TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID};
        try (Cursor cursor = resolver.query(channelsUri, projection, null, null, null)) {
            while (cursor != null && cursor.moveToNext()) {
                if (cursor.getLong(1) == orginalNetworkId) {
                    return cursor.getLong(0);
                }
            }
        }
        return INVALID_CHANNEL_ID;
    }

    public static String[] transformToGenres(VideoInfo videoInfo) {
        List<String> genres = new ArrayList<>();
        if (videoInfo.isCatAction()) {
            genres.add(TvContract.Programs.Genres.MOVIES);
        }
        if (videoInfo.isCatComedy()) {
            genres.add(TvContract.Programs.Genres.COMEDY);
        }
        if (videoInfo.isCatDocumentary()) {
            genres.add(TvContract.Programs.Genres.ANIMAL_WILDLIFE);
        }
        if (videoInfo.isCatDrama()) {
            genres.add(TvContract.Programs.Genres.DRAMA);
        }
        if (videoInfo.isCatEducational()) {
            genres.add(TvContract.Programs.Genres.EDUCATION);
        }
        if (videoInfo.isCatHorror()) {
            genres.add(TvContract.Programs.Genres.MOVIES);
        }
        if (videoInfo.isCatKids()) {
            genres.add(TvContract.Programs.Genres.FAMILY_KIDS);
        }
        if (videoInfo.isCatMovie()) {
            genres.add(TvContract.Programs.Genres.MOVIES);
        }
        if (videoInfo.isCatMusic()) {
            genres.add(TvContract.Programs.Genres.MUSIC);
        }
        if (videoInfo.isCatNews()) {
            genres.add(TvContract.Programs.Genres.NEWS);
        }
        if (videoInfo.isCatReality()) {
            genres.add(TvContract.Programs.Genres.LIFE_STYLE);
        }
        if (videoInfo.isCatRomance()) {
            genres.add(TvContract.Programs.Genres.MOVIES);
        }
        if (videoInfo.isCatScifi()) {
            genres.add(TvContract.Programs.Genres.MOVIES);
        }
        if (videoInfo.isCatSerial()) {
            genres.add(TvContract.Programs.Genres.ENTERTAINMENT);
        }
        if (videoInfo.isCatSoap()) {
            genres.add(TvContract.Programs.Genres.ENTERTAINMENT);
        }
        if (videoInfo.isCatSpecial()) {
            genres.add(TvContract.Programs.Genres.ENTERTAINMENT);
        }
        if (videoInfo.isCatThriller()) {
            genres.add(TvContract.Programs.Genres.MOVIES);
        }
        if (videoInfo.isCatAdult()) {
            genres.add(TvContract.Programs.Genres.MOVIES);
        }
        return genres.stream().toArray(String[]::new);
    }

    public static String transformLocalhostToHost(String source, String host) {
        if (source != null && source.contains("localhost")) {
            source = source.replace("localhost", host);
        }
        return source;
    }

    public static Map<String, Uri> getRecordedProgramUriMapFromSharedPreferences(Context context) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    Constants.PREFERENCE_TVHEADEND, Context.MODE_PRIVATE);
            String cachedRecordedUri = sharedPreferences.getString(Constants.KEY_CACHED_RECODINGS_MAP, null);
            ObjectMapper objectMapper = Injector.get().objectMapper();
            TypeReference<HashMap<String, Uri>> typeRef
                    = new TypeReference<HashMap<String, Uri>>() {
            };
            return objectMapper.readValue(cachedRecordedUri, typeRef);
        } catch (Exception e) {
            Log.w(TAG, "Exception reading recorded progam uri map from shared preferences", e);
            return new HashMap<>();
        }
    }

    public static boolean updateRecordedProgramUriMapFromSharedPreferences(Context context
            , Map<String,Uri> recordedProgramUriMapFromSharedPreferences) {
        try {
            ObjectMapper objectMapper = Injector.get().objectMapper();

            TypeReference<HashMap<String, Uri>> typeRef =
                    new TypeReference<HashMap<String, Uri>>() {
            };

            String cachedRecordedUri = objectMapper
                    .writeValueAsString(recordedProgramUriMapFromSharedPreferences);

            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    Constants.PREFERENCE_TVHEADEND, Context.MODE_PRIVATE);

            return sharedPreferences
                    .edit()
                    .putString(Constants.PREFERENCE_TVHEADEND, cachedRecordedUri)
                    .commit();
        } catch (Exception e) {
            Log.w(TAG, "Exception reading recorded progam uri map from shared preferences", e);
            return false;
        }
    }
}
