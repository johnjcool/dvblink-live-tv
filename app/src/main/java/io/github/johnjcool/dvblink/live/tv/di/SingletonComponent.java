package io.github.johnjcool.dvblink.live.tv.di;

import android.content.SharedPreferences;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import io.github.johnjcool.dvblink.live.tv.remote.DvbLinkClient;

@Singleton
@Component(modules = {ContextModule.class, ServiceModule.class})
public interface SingletonComponent {

    @Named("host")
    String host();

    DvbLinkClient dvbLinkClient();

    SharedPreferences sharedPreferences();
}
