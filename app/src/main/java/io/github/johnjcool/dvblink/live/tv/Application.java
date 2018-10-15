package io.github.johnjcool.dvblink.live.tv;

import io.github.johnjcool.dvblink.live.tv.di.ContextModule;
import io.github.johnjcool.dvblink.live.tv.di.DaggerSingletonComponent;
import io.github.johnjcool.dvblink.live.tv.di.ServiceModule;
import io.github.johnjcool.dvblink.live.tv.di.SingletonComponent;

public class Application extends android.app.Application {

    private SingletonComponent mSingletonComponent;

    private static Application INSTANCE;

    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        mSingletonComponent = initSingletonComponent();
    }

    public SingletonComponent getSingletonComponent() {
        return mSingletonComponent != null ? mSingletonComponent : initSingletonComponent();
    }

    private SingletonComponent initSingletonComponent() {
        return DaggerSingletonComponent.builder()
                .contextModule(new ContextModule(this))
                .serviceModule(new ServiceModule())
                .build();
    }

    public void resetComponents() {
        mSingletonComponent = null;
    }

    public static Application get() {
        return INSTANCE;
    }
}
