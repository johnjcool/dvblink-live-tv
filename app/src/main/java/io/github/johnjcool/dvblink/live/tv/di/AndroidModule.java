package io.github.johnjcool.dvblink.live.tv.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.johnjcool.dvblink.live.tv.tv.service.epg.EpgSyncJobService;

@Module
public class AndroidModule {

    private final Context mContext;

    public AndroidModule(Application application) {
        mContext = application.getApplicationContext();
    }

    @Provides
    @Singleton
    Context provideContext() {
        return mContext;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences(EpgSyncJobService.PREFERENCE_EPG_SYNC, Context.MODE_PRIVATE);
    }
}
