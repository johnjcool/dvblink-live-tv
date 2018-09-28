/*
 * Copyright (c) 2016 Kiall Mac Innes <kiall@macinnes.ie>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.github.johnjcool.dvblink.live.tv.tv;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.net.Uri;
import android.util.Log;

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

    public static String getInputId() {
        ComponentName componentName = new ComponentName(
                "io.github.johnjcool.dvblink.live.tv",
                ".tv.service.TvInputService");

        return TvContract.buildInputId(componentName);
    }
}
