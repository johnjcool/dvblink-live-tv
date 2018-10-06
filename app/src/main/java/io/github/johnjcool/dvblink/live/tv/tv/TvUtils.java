package io.github.johnjcool.dvblink.live.tv.tv;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.net.Uri;
import android.util.Log;

import com.google.android.media.tv.companionlibrary.model.ModelUtils;
import com.google.android.media.tv.companionlibrary.model.Program;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.github.johnjcool.dvblink.live.tv.Constants;

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
}
