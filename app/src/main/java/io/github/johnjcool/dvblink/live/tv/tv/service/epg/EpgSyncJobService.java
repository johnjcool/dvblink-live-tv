package io.github.johnjcool.dvblink.live.tv.tv.service.epg;

import android.media.tv.TvContract;
import android.net.Uri;
import android.util.Log;

import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.model.InternalProviderData;
import com.google.android.media.tv.companionlibrary.model.Program;
import com.google.android.media.tv.companionlibrary.utils.TvContractUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.johnjcool.dvblink.live.tv.Constants;
import io.github.johnjcool.dvblink.live.tv.di.Injector;
import io.github.johnjcool.dvblink.live.tv.remote.DvbLinkClient;
import io.github.johnjcool.dvblink.live.tv.remote.model.response.StreamInfo;
import io.github.johnjcool.dvblink.live.tv.tv.TvUtils;

public class EpgSyncJobService extends com.google.android.media.tv.companionlibrary.sync.EpgSyncJobService {

    private static final String TAG = EpgSyncJobService.class.getName();

    private DvbLinkClient mDvbLinkClient;
    private String mHost;

    public EpgSyncJobService() {
        mDvbLinkClient = Injector.get().dvbLinkClient();
        mHost = Injector.get().host();
    }

    @Override
    public List<Channel> getChannels() {
        try {
            List<io.github.johnjcool.dvblink.live.tv.remote.model.response.Channel> channels = mDvbLinkClient.getChannels();
            Function<io.github.johnjcool.dvblink.live.tv.remote.model.response.Channel, Channel> channelTransform =
                    new Function<io.github.johnjcool.dvblink.live.tv.remote.model.response.Channel, Channel>() {
                        public Channel apply(io.github.johnjcool.dvblink.live.tv.remote.model.response.Channel channel) {
                            int originalNetworkId = channel.getDvbLinkId();

                            InternalProviderData internalProviderData = new InternalProviderData();
                            internalProviderData.setRepeatable(false);
                            internalProviderData.setVideoType(TvContractUtils.SOURCE_TYPE_HTTP_PROGRESSIVE);
                            internalProviderData.setVideoUrl(getProgramVideoSrc(Long.valueOf(originalNetworkId)));

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
                                    .setOriginalNetworkId(originalNetworkId)
                                    .setServiceType(serviceType)
                                    .setChannelLogo(TvUtils.transformLocalhostToHost(channel.getChannelLogo(), mHost))
                                    .setInternalProviderData(internalProviderData)
                                    .setServiceId(0)
                                    .setSearchable(true);

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
            List<io.github.johnjcool.dvblink.live.tv.remote.model.response.Program> programs = mDvbLinkClient
                    .getPrograms(String.valueOf(channel.getOriginalNetworkId()), startMs / 1000, endMs / 1000);

            if (programs.isEmpty()) {
                return new ArrayList<>();
            }

            Function<io.github.johnjcool.dvblink.live.tv.remote.model.response.Program, Program> programTransform =
                    new Function<io.github.johnjcool.dvblink.live.tv.remote.model.response.Program, Program>() {
                        public Program apply(io.github.johnjcool.dvblink.live.tv.remote.model.response.Program program) {
                            InternalProviderData data = null;
                            try {
                                data = new InternalProviderData(channel.getInternalProviderDataByteArray());
                                data.put(Constants.KEY_ORGINAL_OBJECT_ID, program.getId());
                            } catch (InternalProviderData.ParseException e) {
                                Log.e(TAG, "Error parsing orginal program id.", e);
                            }
                            return new Program.Builder()
                                    .setChannelId(channel.getId())
                                    .setThumbnailUri(channel.getChannelLogo())
                                    .setDescription(program.getShortDesc())
                                    .setTitle(program.getName())
                                    .setPosterArtUri(program.getImage())
                                    .setEpisodeNumber(1)
                                    .setCanonicalGenres(TvUtils.transformToGenres(program))
                                    .setStartTimeUtcMillis(program.getStartTime() * 1000)
                                    .setEndTimeUtcMillis((program.getStartTime() + program.getDuration()) * 1000)
                                    .setAudioLanguages(program.getLanguage())
                                    .setInternalProviderData(data)
                                    .build();
                        }
                    };

            return programs.stream().map(programTransform).collect(Collectors.<Program>toList());
        } catch (Exception e) {
            Log.e(TAG, "Failed to get programs", e);
        }
        return null;
    }

    public boolean shouldUpdateProgramMetadata(Program oldProgram, Program newProgram) {
        if (oldProgram == null || newProgram == null) {
            return false;
        }
        return super.shouldUpdateProgramMetadata(oldProgram, newProgram);
    }


    private String getProgramVideoSrc(Long dvbLinkId) {
        try {
            List<StreamInfo.Channel> channels = mDvbLinkClient.getStreamInfo(dvbLinkId).getChannels();
            if (!channels.isEmpty() && channels.size() == 1) {
                return channels.get(0).getUrl().replace(":8101", "");
            }
            throw new Exception("wrong channel size (" + channels.size() + ")");
        } catch (Exception e) {
            Log.e(TAG, "Failed to get program video source", e);
        }
        return null;
    }
}
