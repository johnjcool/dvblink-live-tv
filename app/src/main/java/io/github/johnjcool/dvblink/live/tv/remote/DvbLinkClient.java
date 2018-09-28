package io.github.johnjcool.dvblink.live.tv.remote;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.github.johnjcool.dvblink.live.tv.remote.model.request.SearchEpg;
import io.github.johnjcool.dvblink.live.tv.remote.model.request.StreamInfo;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.Channel;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.Program;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.Response;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.ServerInfo;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.StatusCode;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class DvbLinkClient {

    private static final String CLIENT_ID = "80ba7c06-a9e9-4e0c-a9ab-0ee329e5537d";
    private static final String URL_TMPL = "http://%s:%d/";

    // DAGGER
    private final XmlMapper mapper;
    private DvbLinkApi dvbLinkApi;
    private String host;

    public DvbLinkClient(String host, int port, String username, String password) {
        this.host = host;
        mapper = new XmlMapper();
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        dvbLinkApi = new Retrofit.Builder()
                .baseUrl(String.format(URL_TMPL, host, port))
                .client(initializeOkHttpClient(username, password))
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build().create(DvbLinkApi.class);
    }

    public ServerInfo getServerInfo() throws Exception {
        Call<Response> call = dvbLinkApi.post("get_server_info");
        retrofit2.Response<Response> response = call.execute();
        if (response.isSuccessful()) {
            Response resp = response.body();
            return mapper.readValue(resp.getXmlResult(), ServerInfo.class);
        } else {
            throw new Exception(response.errorBody().string());
        }
    }

    public List<Channel> getChannels() throws Exception {
        Call<Response> call = dvbLinkApi.post("get_channels");
        retrofit2.Response<Response> response = call.execute();
        if (response.isSuccessful()) {
            Response resp = response.body();
            return mapper.readValue(resp.getXmlResult(), Channel.Channels.class).getChannels();
        } else {
            throw new Exception(response.errorBody().string());
        }
    }

    public io.github.johnjcool.dvblink.live.tv.remote.model.response.StreamInfo getStreamInfo(Long dvbLinkId) throws Exception {
        String xmlData = mapper.writer()
                .with(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .writeValueAsString(new StreamInfo(host, CLIENT_ID, Arrays.asList(dvbLinkId)));
        Call<Response> call = dvbLinkApi.post("get_stream_info", xmlData);
        retrofit2.Response<Response> response = call.execute();
        if (response.isSuccessful()) {
            Response resp = response.body();
            if(resp.getStatusCode() == StatusCode.STATUS_OK) {
                return mapper.readValue(resp.getXmlResult(), io.github.johnjcool.dvblink.live.tv.remote.model.response.StreamInfo.class);
            } else {
                throw new Exception(resp.getStatusCode() + ": " + resp.getStatusCode().name());
            }
        } else {
            throw new Exception(response.errorBody().string());
        }
    }

    public List<Program> getPrograms(String channelId, long startTime, long endTime) throws Exception {
        String xmlData = mapper.writer()
                .with(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .writeValueAsString(new SearchEpg(Arrays.asList(channelId), startTime, endTime));
        Call<Response> call = dvbLinkApi.post("search_epg", xmlData);
        retrofit2.Response<Response> response = call.execute();
        if (response.isSuccessful()) {
            Response resp = response.body();

            System.out.println("Start Time in Seconds: " + startTime);
            System.out.println("End Time in Seconds: " + endTime);
            System.out.println(new Date(startTime*1000));
            System.out.println(new Date(endTime*1000));

            try {
                Program.EpgSearcher epgSearcher = mapper.readValue(resp.getXmlResult(), Program.EpgSearcher.class);

                if (epgSearcher.getChannelEpgs() != null) {


                    return mapper.readValue(resp.getXmlResult(), Program.EpgSearcher.class)
                            .getChannelEpgs().get(0).getPrograms();
                }
            } catch (Exception e) {
                System.out.println(resp.getStatusCode() + " " + resp.getXmlResult());
            }
            return new ArrayList<>();
        } else {
            throw new Exception(response.errorBody().string());
        }
    }

    private OkHttpClient initializeOkHttpClient(final String username, final String password) {
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


    private interface DvbLinkApi {

        @FormUrlEncoded
        @POST("cs/")
        Call<Response> post(@Field("command") String command, @Field("xml_param") String xmlParam);

        @FormUrlEncoded
        @POST("cs/")
        Call<Response> post(@Field("command") String command);
    }
}
