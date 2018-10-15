package io.github.johnjcool.dvblink.live.tv.remote;

import android.util.Log;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import io.github.johnjcool.dvblink.live.tv.remote.model.request.ItemType;
import io.github.johnjcool.dvblink.live.tv.remote.model.request.ObjectRequester;
import io.github.johnjcool.dvblink.live.tv.remote.model.request.ObjectType;
import io.github.johnjcool.dvblink.live.tv.remote.model.request.RemoveRecording;
import io.github.johnjcool.dvblink.live.tv.remote.model.request.Schedule;
import io.github.johnjcool.dvblink.live.tv.remote.model.request.SearchEpg;
import io.github.johnjcool.dvblink.live.tv.remote.model.request.StreamInfo;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.Channel;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.Object;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.Program;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.RecordedTV;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.Recording;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.Response;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.ServerInfo;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.StatusCode;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;

public class DvbLinkClient {

    private static final String TAG = DvbLinkClient.class.getName();

    private static final String CONTAINER_ROOT_ID = "";
    private static final String CONTAINER_WITH_RECORDINGS_SORTED_BY_NAME_ID = "E44367A7-6293-4492-8C07-0E551195B99F";

    private static final String CLIENT_ID = "80ba7c06-a9e9-4e0c-a9ab-0ee329e5537d";

    private DvbLinkApi mDvbLinkApi;
    private XmlMapper mMapper;
    private String mHost;

    public DvbLinkClient(DvbLinkApi dvbLinkApi, XmlMapper mapper, String host) {
        mDvbLinkApi = dvbLinkApi;
        mMapper = mapper;
        mHost = host;
    }

    public ServerInfo getServerInfo() throws Exception {
        Call<Response> call = mDvbLinkApi.post("get_server_info");
        retrofit2.Response<Response> response = call.execute();
        if (response.isSuccessful()) {
            Response resp = response.body();
            return mMapper.readValue(resp.getXmlResult(), ServerInfo.class);
        } else {
            throw new Exception(response.errorBody().string());
        }
    }

    public List<Channel> getChannels() throws Exception {
        Call<Response> call = mDvbLinkApi.post("get_channels");
        retrofit2.Response<Response> response = call.execute();
        if (response.isSuccessful()) {
            Response resp = response.body();
            return mMapper.readValue(resp.getXmlResult(), Channel.Channels.class).getChannels();
        } else {
            throw new Exception(response.errorBody().string());
        }
    }

    public io.github.johnjcool.dvblink.live.tv.remote.model.response.StreamInfo getStreamInfo(Long dvbLinkId) throws Exception {
        String xmlData = mMapper.writer()
                .with(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .writeValueAsString(new StreamInfo(mHost, CLIENT_ID, Arrays.asList(dvbLinkId)));
        Call<Response> call = mDvbLinkApi.post("get_stream_info", xmlData);
        retrofit2.Response<Response> response = call.execute();
        if (response.isSuccessful()) {
            Response resp = response.body();
            if (resp.getStatusCode() == StatusCode.STATUS_OK) {
                return mMapper.readValue(resp.getXmlResult(), io.github.johnjcool.dvblink.live.tv.remote.model.response.StreamInfo.class);
            } else {
                throw new Exception(resp.getStatusCode() + ": " + resp.getStatusCode().name());
            }
        } else {
            throw new Exception(response.errorBody().string());
        }
    }

    public List<Program> getPrograms(String channelId, long startTime, long endTime) throws Exception {
        String xmlData = mMapper.writer()
                .with(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .writeValueAsString(new SearchEpg(Arrays.asList(channelId), startTime, endTime));
        Call<Response> call = mDvbLinkApi.post("search_epg", xmlData);
        retrofit2.Response<Response> response = call.execute();
        if (response.isSuccessful()) {
            Response resp = response.body();
            try {
                Program.EpgSearcher epgSearcher = mMapper.readValue(resp.getXmlResult(), Program.EpgSearcher.class);
                if (epgSearcher.getChannelEpgs() != null) {
                    return mMapper.readValue(resp.getXmlResult(), Program.EpgSearcher.class)
                            .getChannelEpgs().get(0).getPrograms();
                }
            } catch (Exception e) {
                Log.w(TAG, resp.getStatusCode() + " " + resp.getXmlResult());
            }
            return new ArrayList<>();
        } else {
            throw new Exception(response.errorBody().string());
        }
    }

    public Recording addSchedule(Schedule schedule) throws Exception {
        String xmlData = mMapper.writer()
                .with(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .writeValueAsString(schedule);
        Call<Response> call = mDvbLinkApi.post("add_schedule", xmlData);
        retrofit2.Response<Response> response = call.execute();
        if (!response.isSuccessful()) {
            throw new Exception(response.errorBody().string());
        }

        List<Recording> recordings = getRecordings();
        for (Recording recording : recordings) {
            if (recording.isActive()) {
                if (schedule.isByEpg() && recording.getChannelId().equals(schedule.getByEpg().getChannelId())) {
                    return recording;
                }
                if (schedule.isManual() && recording.getChannelId().equals(schedule.getManual().getChannelId())) {
                    return recording;
                }
            }
        }
        throw new Exception("Recording not added...");
    }

    public List<Recording> getRecordings() throws Exception {
        Call<Response> call = mDvbLinkApi.post("get_recordings");
        retrofit2.Response<Response> response = call.execute();
        if (response.isSuccessful()) {
            Response resp = response.body();
            return mMapper.readValue(resp.getXmlResult(), Recording.Recordings.class).getRecordings();
        } else {
            throw new Exception(response.errorBody().string());
        }
    }

    public void removeRecording(String recordingId) throws Exception {
        String xmlParam = mMapper.writer()
                .with(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .writeValueAsString(new RemoveRecording(recordingId));
        Call<Response> call = mDvbLinkApi.post("remove_recording", xmlParam);

        retrofit2.Response<Response> response = call.execute();
        if (!response.isSuccessful()) {
            throw new Exception(response.errorBody().string());
        }
    }

    public RecordedTV getRecordedProgram(String scheduleId) throws Exception {
        List<RecordedTV> recordedTVs = getRecordedPrograms();
        for (RecordedTV recordedTV : recordedTVs) {
            if (recordedTV.getScheduleId().equals(scheduleId)) {
                return recordedTV;
            }
        }
        throw new Exception("Recorded program not found.");
    }

    public List<RecordedTV> getRecordedPrograms() throws Exception {
        Object rootObject = getObject(new ObjectRequester(CONTAINER_ROOT_ID, mHost, ObjectType.OBJECT_CONTAINER, ItemType.ITEM_UNKNOWN));
        if (rootObject.getContainers() == null || rootObject.getContainers().isEmpty()) {
            return Collections.emptyList();
        }
        String recordedProgramsId = String.format("%s%s", rootObject.getContainers().get(0).getObjectId(), CONTAINER_WITH_RECORDINGS_SORTED_BY_NAME_ID);
        Object recordedProgramsObject = getObject(new ObjectRequester(recordedProgramsId, mHost, ObjectType.OBJECT_ITEM, ItemType.ITEM_RECORDED_TV));

        if (rootObject.getRecordedTVs() == null) {
            return Collections.emptyList();
        }
        return recordedProgramsObject.getRecordedTVs();
    }


    public Object getObject(ObjectRequester objectRequester) throws Exception {
        String xmlData = mMapper.writer()
                .with(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .writeValueAsString(objectRequester);
        Call<Response> call = mDvbLinkApi.post("get_object", xmlData);
        retrofit2.Response<Response> response = call.execute();
        if (response.isSuccessful()) {
            Response resp = response.body();
            return mMapper.readValue(resp.getXmlResult(), Object.class);
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
}
