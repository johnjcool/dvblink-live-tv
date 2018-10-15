package io.github.johnjcool.dvblink.live.tv.remote;

import io.github.johnjcool.dvblink.live.tv.remote.model.response.Response;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface DvbLinkApi {

    @FormUrlEncoded
    @POST("cs/")
    Call<Response> post(@Field("command") String command, @Field("xml_param") String xmlParam);

    @FormUrlEncoded
    @POST("cs/")
    Call<Response> post(@Field("command") String command);
}