package io.github.johnjcool.dvblink.live.tv.di;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.johnjcool.dvblink.live.tv.Constants;
import io.github.johnjcool.dvblink.live.tv.remote.DvbLinkApi;
import io.github.johnjcool.dvblink.live.tv.remote.DvbLinkClient;
import io.github.johnjcool.dvblink.live.tv.tv.service.EpgSyncJobService;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Module
public class ServiceModule {

    private static final String URL_TMPL = "http://%s:%d/";

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Context context) {
        return context.getSharedPreferences(EpgSyncJobService.PREFERENCE_EPG_SYNC, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    @Named("host")
    public String provideHost(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(Constants.KEY_HOSTNAME, "localhost");
    }

    @Provides
    @Singleton
    @Named("port")
    int providePort(SharedPreferences sharedPreferences) {
        return Integer.parseInt(sharedPreferences.getString(Constants.KEY_PORT, "8100"));
    }

    @Provides
    @Singleton
    @Named("username")
    String provideUsername(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(Constants.KEY_USERNAME, "user");
    }

    @Provides
    @Singleton
    @Named("password")
    String providePassword(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(Constants.KEY_PASSWORD, "admin");
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(@Named("host") String host, @Named("port") int port, XmlMapper xmlMapper, OkHttpClient okHttpClient) {
        String baseUrl = String.format(URL_TMPL, host, port);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create(xmlMapper))
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build();
        return retrofit;
    }

    @Provides
    @Singleton
    public XmlMapper provideXmlMapper() {
        XmlMapper mapper = new XmlMapper();
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        return mapper;
    }

    @Provides
    @Singleton
    public DvbLinkApi provideDvbLinkApi(Retrofit retrofit) {
        return retrofit.create(DvbLinkApi.class);
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(@Named("username") String username, @Named("password") String password) {
        return new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request originalRequest = chain.request();

                okhttp3.Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                        Credentials.basic(username, password));

                okhttp3.Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        }).readTimeout(60, TimeUnit.SECONDS).build();
    }

    @Provides
    @Singleton
    public DvbLinkClient provideDvbLinkClient(DvbLinkApi dvbLinkApi, XmlMapper mapper, @Named("host") String host) {
        return new DvbLinkClient(dvbLinkApi, mapper, host);
    }
}
