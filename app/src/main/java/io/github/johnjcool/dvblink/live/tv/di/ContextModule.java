package io.github.johnjcool.dvblink.live.tv.di;

import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

    private final Context mContext;

    public ContextModule(Application application) {
        mContext = application.getApplicationContext();
    }

    @Provides
    Context providesContext() {
        return mContext;
    }
}
