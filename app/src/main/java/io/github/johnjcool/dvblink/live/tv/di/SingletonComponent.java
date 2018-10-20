package io.github.johnjcool.dvblink.live.tv.di;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import io.github.johnjcool.dvblink.live.tv.remote.DvbLinkClient;

@Singleton
@Component(modules = {AndroidModule.class, ServiceModule.class})
public interface SingletonComponent {

    @Named("host")
    String host();

    DvbLinkClient dvbLinkClient();

    ObjectMapper objectMapper();
}
