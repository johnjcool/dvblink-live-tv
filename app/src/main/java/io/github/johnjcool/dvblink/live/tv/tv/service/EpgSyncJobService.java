package io.github.johnjcool.dvblink.live.tv.tv.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.tv.TvContract;
import android.net.Uri;
import android.util.Log;

import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.model.InternalProviderData;
import com.google.android.media.tv.companionlibrary.model.Program;
import com.google.android.media.tv.companionlibrary.utils.TvContractUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.johnjcool.dvblink.live.tv.Constants;
import io.github.johnjcool.dvblink.live.tv.remote.DvbLinkClient;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.StreamInfo;

public class EpgSyncJobService extends com.google.android.media.tv.companionlibrary.sync.EpgSyncJobService {

    private static final String TAG = EpgSyncJobService.class.getName();
    public static final long DEFAULT_IMMEDIATE_EPG_DURATION_MILLIS = 1000 * 60 * 60; // 1 Hour

    private DvbLinkClient client;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(
                com.google.android.media.tv.companionlibrary.sync.EpgSyncJobService.PREFERENCE_EPG_SYNC, Context.MODE_PRIVATE);
        client = new DvbLinkClient(
                sharedPreferences.getString(Constants.KEY_HOSTNAME, "192.168.178.26"),
                Integer.parseInt(sharedPreferences.getString(Constants.KEY_PORT, "80")),
                sharedPreferences.getString(Constants.KEY_USERNAME, "user"),
                sharedPreferences.getString(Constants.KEY_PASSWORD, "admin"));
    }

    @Override
    public List<Channel> getChannels() {
        try {
            List<io.github.johnjcool.dvblink.live.tv.remote.model.response.Channel> channels = client.getChannels();
            Function<io.github.johnjcool.dvblink.live.tv.remote.model.response.Channel, Channel> channelTransform =
                    new Function<io.github.johnjcool.dvblink.live.tv.remote.model.response.Channel, Channel>() {
                        public Channel apply(io.github.johnjcool.dvblink.live.tv.remote.model.response.Channel channel) {
                            InternalProviderData internalProviderData = new InternalProviderData();
                            internalProviderData.setRepeatable(false);

                            String serviceType = TvContract.Channels.SERVICE_TYPE_AUDIO_VIDEO;
                            if (channel.getType() == io.github.johnjcool.dvblink.live.tv.remote.model.response.Channel.Type.RD_CHANNEL_RADIO) {
                                serviceType = TvContract.Channels.SERVICE_TYPE_AUDIO;
                            }
                            if (channel.getType() == io.github.johnjcool.dvblink.live.tv.remote.model.response.Channel.Type.RD_CHANNEL_OTHER) {
                                serviceType = TvContract.Channels.SERVICE_TYPE_OTHER;
                            }

                            Channel.Builder builder = new Channel.Builder()
                                    .setDisplayName(channel.getName())
                                    .setDisplayNumber(String.valueOf(channel.getNumber()))
                                    .setOriginalNetworkId(channel.getDvbLinkId())
                                    .setServiceType(serviceType)
                                    .setChannelLogo(getChannelLogo(channel.getChannelLogo()))
                                    .setInternalProviderData(internalProviderData)
                                    .setTransportStreamId(0)
                                    .setServiceId(0);

                            return builder.build();
                        }
                    };
            return channels.stream().map(channelTransform).collect(Collectors.<Channel>toList());
        } catch (Exception e) {
            Log.e(TAG, "Failed to get channels", e);
        }
        return null;
    }

    @Override
    public List<Program> getProgramsForChannel(Uri channelUri, final Channel channel, long startMs,
                                               long endMs) {
        try {
            List<io.github.johnjcool.dvblink.live.tv.remote.model.response.Program> programs = client
                    .getPrograms(String.valueOf(channel.getOriginalNetworkId()), startMs/1000, endMs/1000);

            if (programs.isEmpty()) {
                return new ArrayList<>();
            }

            Function<io.github.johnjcool.dvblink.live.tv.remote.model.response.Program, Program> programTransform =
                    new Function<io.github.johnjcool.dvblink.live.tv.remote.model.response.Program, Program>() {
                        public Program apply(io.github.johnjcool.dvblink.live.tv.remote.model.response.Program program) {
                            InternalProviderData internalProviderData = new InternalProviderData();
                            internalProviderData.setVideoType(TvContractUtils.SOURCE_TYPE_HTTP_PROGRESSIVE);
                            internalProviderData.setVideoUrl(getProgramVideoSrc(Long.valueOf(channel.getOriginalNetworkId())));

                            Log.d(TAG,"Start Time in Seconds: " + program.getStartTime());
                            Log.d(TAG,"End Time in Seconds: " + (program.getStartTime() + program.getDuration()));
                            Log.d(TAG,new Date(program.getStartTime()*1000).toString());
                            Log.d(TAG,new Date((program.getStartTime() + program.getDuration())*1000).toString());

                            return new Program.Builder()
                                    .setChannelId(channel.getId())
                                    .setTitle(program.getName())
                                    .setDescription(program.getShortDesc())
                                    .setPosterArtUri(program.getImage())
                                    .setCanonicalGenres(program.getCategories() != null ?
                                            program.getCategories().split(",") :
                                            new String [] {""})
                                    .setStartTimeUtcMillis(program.getStartTime()*1000)
                                    .setEndTimeUtcMillis((program.getStartTime() + program.getDuration())*1000)
                                    .setAudioLanguages(program.getLanguage())
                                    //.setContentRatings(rating.toArray(new TvContentRating[rating.size()]))
                                    // NOTE: {@code COLUMN_INTERNAL_PROVIDER_DATA} is a private field
                                    // where TvInputService can store anything it wants. Here, we store
                                    // video type and video URL so that TvInputService can play the
                                    // video later with this field.
                                    .setInternalProviderData(internalProviderData)
                                    .build();
                        }
                    };

            return programs.stream().map(programTransform).collect(Collectors.<Program>toList());
        } catch (Exception e) {
            Log.e(TAG, "Failed to get programs", e);
        }
        return null;
    }


    private String getProgramVideoSrc(Long dvbLinkId) {
        try {
            List<StreamInfo.Channel> channels = client.getStreamInfo(dvbLinkId).getChannels();
            if (!channels.isEmpty() && channels.size() == 1) {
                return channels.get(0).getUrl().replace(":8101","");
            }
            throw new Exception("wrong channel size (" + channels.size() + ")");
        } catch (Exception e) {
            Log.e(TAG, "Failed to get program video source", e);
        }
        return null;
    }

    private String getChannelLogo(String channelLogoSrc) {
        if (channelLogoSrc != null && channelLogoSrc.contains("localhost")) {
            channelLogoSrc = channelLogoSrc.replace("localhost", sharedPreferences.getString(Constants.KEY_HOSTNAME, "192.168.178.26"));
        }
        return channelLogoSrc;
    }
}
