package io.github.johnjcool.dvblink.live.tv.tv.service;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.tv.TvContentRating;
import android.media.tv.TvContract;
import android.media.tv.TvInputManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.media.tv.companionlibrary.BaseTvInputService;
import com.google.android.media.tv.companionlibrary.model.Advertisement;
import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.model.InternalProviderData;
import com.google.android.media.tv.companionlibrary.model.Program;
import com.google.android.media.tv.companionlibrary.model.RecordedProgram;
import com.google.android.media.tv.companionlibrary.utils.TvContractUtils;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.concurrent.ExecutionException;

import io.github.johnjcool.dvblink.live.tv.R;
import io.github.johnjcool.dvblink.live.tv.player.MediaSourceFactory;
import io.github.johnjcool.dvblink.live.tv.player.TvPlayer;
import io.github.johnjcool.dvblink.live.tv.player.WebPlayer;

public class TvInputService extends BaseTvInputService {

    private static final String TAG = TvInputService.class.getSimpleName();
    private static final boolean DEBUG = false;
    private static final long EPG_SYNC_DELAYED_PERIOD_MS = 1000 * 60; // 2 Seconds

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public final Session onCreateSession(String inputId) {
        LiveTvInputSession session = new LiveTvInputSession(this, inputId);
        session.setOverlayViewEnabled(true);
        return super.sessionCreated(session);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public TvInputService.RecordingSession onCreateRecordingSession(String inputId) {
        return null;
    }

    class LiveTvInputSession extends BaseTvInputService.Session {

        private TvPlayer mPlayer;
        private String mInputId;
        private Context mContext;
        private boolean stillTuning;

        private Channel mCurrentChannel;
        private Program mCurrentProgram;

        private long tuneTime;
        private boolean isWeb;

        LiveTvInputSession(Context context, String inputId) {
            super(context, inputId);
            mContext = context;
            mInputId = inputId;
        }

        @Override
        public View onCreateOverlayView() {
            Log.d(TAG, "Create overlay view");
            LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            try {
                final View v = inflater.inflate(R.layout.loading, null);

                if (mCurrentChannel == null) {
                    if (DEBUG) {
                        Log.w(TAG, "Cannot find channel");
                    }
                    ((TextView) v.findViewById(R.id.channel)).setText("");
                    ((TextView) v.findViewById(R.id.title)).setText("");
                }

                if(!stillTuning && mCurrentChannel.getServiceType().equals(TvContract.Channels.SERVICE_TYPE_AUDIO)) {
                    if (DEBUG) {
                        Log.d(TAG, "Audio-only stream, show a foreground");
                    }
                    ((TextView) v.findViewById(R.id.channel_msg)).setText(R.string.streaming_audio);
                }

                if (DEBUG) {
                    Log.d(TAG, "Trying to load some visual display");
                }

                if (isWeb) {
                    WebPlayer wv = new WebPlayer(getApplicationContext(),
                            new WebPlayer.WebViewListener() {
                                @Override
                                public void onPageFinished() {
                                    //Don't do anything
                                }
                            });
                    wv.load(mCurrentProgram.getInternalProviderData().getVideoUrl());
                    return wv;
                } else if (mCurrentProgram.getPosterArtUri() != null && !mCurrentProgram.getPosterArtUri().isEmpty()) {
                    if (DEBUG) {
                        Log.d(TAG, "User supplied splashscreen");
                    }
                    ImageView iv = new ImageView(getApplicationContext());
                    Glide.with(getApplicationContext()).load(mCurrentProgram.getPosterArtUri()).into(iv);
                    return iv;
                } else {
                    if (DEBUG) {
                        Log.d(TAG, "Manually create a splashscreen");
                    }
                    ((TextView) v.findViewById(R.id.channel)).setText(mCurrentChannel.getDisplayNumber());
                    ((TextView) v.findViewById(R.id.title)).setText(mCurrentChannel.getDisplayName());
                    if (mCurrentChannel.getChannelLogo() != null && !mCurrentChannel.getChannelLogo().isEmpty()) {
                        final Bitmap[] bitmap = {null};
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Handler h = new Handler(Looper.getMainLooper()) {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        super.handleMessage(msg);
                                        ((ImageView) v.findViewById(R.id.thumnail))
                                                .setImageBitmap(bitmap[0]);

                                        //Use Palette to grab colors
                                        Palette p = Palette.from(bitmap[0])
                                                .generate();
                                        if (p.getVibrantSwatch() != null) {
                                            Log.d(TAG, "Use vibrant");
                                            Palette.Swatch s = p.getVibrantSwatch();
                                            v.setBackgroundColor(s.getRgb());
                                            ((TextView) v.findViewById(R.id.channel))
                                                    .setTextColor(s.getTitleTextColor());
                                            ((TextView) v.findViewById(R.id.title))
                                                    .setTextColor(s.getTitleTextColor());
                                            ((TextView) v.findViewById(R.id.channel_msg))
                                                    .setTextColor(s.getTitleTextColor());

                                            //Now style the progress bar
                                            if (p.getDarkVibrantSwatch() != null) {
                                                Palette.Swatch dvs = p.getDarkVibrantSwatch();
                                                ((ProgressWheel) v.findViewById(
                                                        R.id.indeterminate_progress_large_library))
                                                        .setBarColor(dvs.getRgb());
                                            }
                                        } else if (p.getDarkVibrantSwatch() != null) {
                                            Log.d(TAG, "Use dark vibrant");
                                            Palette.Swatch s = p.getDarkVibrantSwatch();
                                            v.setBackgroundColor(s.getRgb());
                                            ((TextView) v.findViewById(R.id.channel))
                                                    .setTextColor(s.getTitleTextColor());
                                            ((TextView) v.findViewById(R.id.title))
                                                    .setTextColor(s.getTitleTextColor());
                                            ((TextView) v.findViewById(R.id.channel_msg))
                                                    .setTextColor(s.getTitleTextColor());
                                            ((ProgressWheel) v.findViewById(
                                                    R.id.indeterminate_progress_large_library))
                                                    .setBarColor(s.getRgb());
                                        } else if (p.getSwatches().size() > 0) {
                                            // Go with default if no vibrant swatch exists
                                            if (DEBUG) {
                                                Log.d(TAG, "No vibrant swatch, " +
                                                        p.getSwatches().size() + " others");
                                            }
                                            Palette.Swatch s = p.getSwatches().get(0);
                                            v.setBackgroundColor(s.getRgb());
                                            ((TextView) v.findViewById(R.id.channel))
                                                    .setTextColor(s.getTitleTextColor());
                                            ((TextView) v.findViewById(R.id.title))
                                                    .setTextColor(s.getTitleTextColor());
                                            ((TextView) v.findViewById(R.id.channel_msg))
                                                    .setTextColor(s.getTitleTextColor());
                                            ((ProgressWheel) v.findViewById(
                                                    R.id.indeterminate_progress_large_library))
                                                    .setBarColor(s.getBodyTextColor());
                                        }
                                    }
                                };
                                try {
                                    bitmap[0] = Glide.with(getApplicationContext()).asBitmap()
                                            .load("https://raw.githubusercontent.com/Fleker/CumulusTV/master/app/src/main/res/drawable-xhdpi/livechannels2.jpg")
                                            .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                            .get();
                                    h.sendEmptyMessage(0);
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
                if (DEBUG) {
                    Log.d(TAG, "Overlay " + v.toString());
                }
                return v;
            } catch (Exception e) {
                if (DEBUG) {
                    Log.d(TAG, "Failure to open: " + e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public long onTimeShiftGetCurrentPosition() {
            if (mPlayer == null) {
                return TvInputManager.TIME_SHIFT_INVALID_TIME;
            }
            long currentMs = tuneTime + mPlayer.getCurrentPosition();
            if (DEBUG) {
                Log.d(TAG, currentMs + "  " + onTimeShiftGetStartPosition() + " start position");
                Log.d(TAG, (currentMs - onTimeShiftGetStartPosition()) + " diff start position");
            }
            return currentMs;
        }

        @Override
        public void onPlayChannel(Channel channel) {
            mCurrentChannel = channel;
        }

        @Override
        public boolean onPlayProgram(Program program, long startPosMs) {
            if (program == null) {
                Log.d(TAG, "Play only channel " + mCurrentChannel.getDisplayName());
                return play(mCurrentChannel.getInternalProviderData());
            }
            mCurrentProgram = program;
            Log.d(TAG, "Play program " + program.getTitle());
            return play(program.getInternalProviderData());
        }

        private boolean play(InternalProviderData internalProviderData) {
            Log.d(TAG, "Play url " +
                    internalProviderData.getVideoUrl());
            if (internalProviderData.getVideoUrl() == null) {
                Toast.makeText(mContext, getString(R.string.msg_no_url_found), Toast.LENGTH_SHORT).show();
                notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN);
                return false;
            } else {
                createPlayer(internalProviderData.getVideoType(),
                        Uri.parse(internalProviderData.getVideoUrl()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    notifyTimeShiftStatusChanged(TvInputManager.TIME_SHIFT_STATUS_AVAILABLE);
                }
                mPlayer.play();
                notifyVideoAvailable();
                Log.d(TAG, "The video should start playing");
                return true;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public boolean onPlayRecordedProgram(RecordedProgram recordedProgram) {
            createPlayer(recordedProgram.getInternalProviderData().getVideoType(),
                    Uri.parse(recordedProgram.getInternalProviderData().getVideoUrl()));

            long recordingStartTime = recordedProgram.getInternalProviderData()
                    .getRecordedProgramStartTime();
            mPlayer.seekTo(recordingStartTime - recordedProgram.getStartTimeUtcMillis());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notifyTimeShiftStatusChanged(TvInputManager.TIME_SHIFT_STATUS_AVAILABLE);
            }
            mPlayer.play();
            notifyVideoAvailable();
            return true;
        }

        @Override
        public long onTimeShiftGetStartPosition() {
            return tuneTime;
        }

        public TvPlayer getTvPlayer() {
            return mPlayer;
        }

        @Override
        public boolean onTune(Uri channelUri) {
            if (DEBUG) {
                Log.d(TAG, "Tune to " + channelUri.toString());
            }
            notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_TUNING);
            releasePlayer();
            tuneTime = System.currentTimeMillis();
            stillTuning = true;
            notifyVideoAvailable();
            setOverlayViewEnabled(false);
            setOverlayViewEnabled(true);
            return super.onTune(channelUri);
        }

        @Override
        public void onSetCaptionEnabled(boolean enabled) {
            // Captions currently unsupported
        }

        @Override
        public void onPlayAdvertisement(Advertisement advertisement) {
            createPlayer(TvContractUtils.SOURCE_TYPE_HTTP_PROGRESSIVE,
                    Uri.parse(advertisement.getRequestUrl()));
        }

        private void createPlayer(int videoType, Uri videoUrl) {
            releasePlayer();

            mPlayer = new TvPlayer(mContext);
            mPlayer.registerCallback(new TvPlayer.Callback() {
                @Override
                public void onStarted() {
                    super.onStarted();
                    Log.d(TAG, "Video available");
                    stillTuning = false;
                    notifyVideoAvailable();
                    setOverlayViewEnabled(false);
                    if (mCurrentChannel!= null && mCurrentChannel.getServiceType().equals(TvContract.Channels.SERVICE_TYPE_AUDIO)) {
                        setOverlayViewEnabled(true);
                    }
                }
            });
            mPlayer.registerErrorListener(new TvPlayer.ErrorListener() {
                @Override
                public void onError(Exception error) {
                    Log.e(TAG, error.getClass().getSimpleName() + " " + error.getMessage());
                    if (error instanceof MediaSourceFactory.NotMediaException) {
                        isWeb = true;
                        setOverlayViewEnabled(false);
                        setOverlayViewEnabled(true);
                    }
                }
            });
            Log.d(TAG, "Create player for " + videoUrl);
            mPlayer.startPlaying(videoUrl);
        }

        private void releasePlayer() {
            if (mPlayer != null) {
                mPlayer.setSurface(null);
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
            }
        }

        @Override
        public void onRelease() {
            super.onRelease();
            releasePlayer();
        }

        @Override
        public void onBlockContent(TvContentRating rating) {
            super.onBlockContent(rating);
            releasePlayer();
        }

//        private void requestEpgSync(final Uri channelUri) {
//            EpgSyncJobService.requestImmediateSync(TvInputService.this, mInputId,
//                    new ComponentName(TvInputService.this, EpgSyncJobService.class));
//            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    onTune(channelUri);
//                }
//            }, EPG_SYNC_DELAYED_PERIOD_MS);
//        }
    }
}
