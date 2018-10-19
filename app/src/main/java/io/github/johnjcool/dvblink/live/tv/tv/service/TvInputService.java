package io.github.johnjcool.dvblink.live.tv.tv.service;

import android.support.annotation.Nullable;

import com.google.android.media.tv.companionlibrary.BaseTvInputService;

import io.github.johnjcool.dvblink.live.tv.tv.session.LiveSession;

public class TvInputService extends BaseTvInputService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public final Session onCreateSession(String inputId) {
        LiveSession session = new LiveSession(this, inputId);
        session.setOverlayViewEnabled(true);
        return super.sessionCreated(session);
    }

    @Nullable
    @Override
    public TvInputService.RecordingSession onCreateRecordingSession(String inputId) {
        return new io.github.johnjcool.dvblink.live.tv.tv.session.RecordingSession(this, inputId);
    }
}
