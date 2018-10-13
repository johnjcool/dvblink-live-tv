package io.github.johnjcool.dvblink.live.tv;

import android.content.Context;
import android.content.SharedPreferences;

import io.github.johnjcool.dvblink.live.tv.remote.DvbLinkClient;
import io.github.johnjcool.dvblink.live.tv.tv.TvUtils;

public class Application extends android.app.Application {

    private static DvbLinkClient mDvbLinkClient;

    public void onCreate() {
        super.onCreate();
        if (TvUtils.isSetupComplete(this)) {
            SharedPreferences sharedPreferences = getSharedPreferences(
                    Constants.PREFERENCE_TVHEADEND, Context.MODE_PRIVATE);
            mDvbLinkClient = new DvbLinkClient(
                    sharedPreferences.getString(Constants.KEY_HOSTNAME, "192.168.178.26"),
                    Integer.parseInt(sharedPreferences.getString(Constants.KEY_PORT, "80")),
                    sharedPreferences.getString(Constants.KEY_USERNAME, "user"),
                    sharedPreferences.getString(Constants.KEY_PASSWORD, "admin"));
        }
    }

    public static DvbLinkClient getDvbLinkClient() {
        return mDvbLinkClient;
    }
}
