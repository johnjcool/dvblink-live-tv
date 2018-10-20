package io.github.johnjcool.dvblink.live.tv;

import android.database.ContentObserver;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import io.github.johnjcool.dvblink.live.tv.di.AndroidModule;
import io.github.johnjcool.dvblink.live.tv.di.DaggerSingletonComponent;
import io.github.johnjcool.dvblink.live.tv.di.Injector;
import io.github.johnjcool.dvblink.live.tv.di.ServiceModule;
import io.github.johnjcool.dvblink.live.tv.di.SingletonComponent;
import io.github.johnjcool.dvblink.live.tv.tv.TvUtils;

public class Application extends android.app.Application {

    private static String TAG = Application.class.getName();
    private static Application INSTANCE;

    private SingletonComponent mSingletonComponent;

    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        mSingletonComponent = initSingletonComponent();
        getContentResolver()
                .registerContentObserver(
                        TvContract.RecordedPrograms.CONTENT_URI,
                        true,
                        contentObserver()
                );
    }

    public SingletonComponent getSingletonComponent() {
        return mSingletonComponent != null ? mSingletonComponent : initSingletonComponent();
    }

    private SingletonComponent initSingletonComponent() {
        return DaggerSingletonComponent.builder()
                .androidModule(new AndroidModule(this))
                .serviceModule(new ServiceModule())
                .build();
    }

    public void resetComponents() {
        mSingletonComponent = null;
    }

    public static Application get() {
        return INSTANCE;
    }

    private ContentObserver contentObserver() {
        return new ContentObserver(new Handler(Looper.getMainLooper())) {
            public void onChange(boolean flag, Uri uri) {
                if (uri == null) {
                    return;
                }

                Log.d(TAG, (new StringBuilder())
                        .append("onChange, recordedProgramUri: ")
                        .append(uri)
                        .toString()
                );

                Map<String, Uri> recordedProgramUriMapFromTif =
                        TvUtils.getRecordedProgramUriMapFromTif(getContentResolver(), uri);

                Map<String, Uri> recordedProgramUriMapFromSharedPreferences =
                        TvUtils.getRecordedProgramUriMapFromSharedPreferences(Application.this);

                final AtomicBoolean added = new AtomicBoolean(false);
                recordedProgramUriMapFromTif.entrySet()
                        .stream()
                        .filter(e -> !recordedProgramUriMapFromSharedPreferences.containsKey(e.getKey()))
                        .forEach(e -> {
                            recordedProgramUriMapFromSharedPreferences.put(e.getKey(), e.getValue());
                            added.set(true);
                        });

                if (added.get() && TvUtils.updateRecordedProgramUriMapFromSharedPreferences(
                        Application.this,
                        recordedProgramUriMapFromSharedPreferences)) {
                    Log.i(TAG, (new StringBuilder())
                            .append("onChange, recordedProgramUri: ")
                            .append(uri)
                            .append(" added!")
                            .toString()
                    );
                } else {
                    Log.w(TAG, (new StringBuilder())
                            .append("onChange, recordedProgramUri: ")
                            .append(uri)
                            .append(" not added!!!!!")
                            .toString()
                    );
                }

                Map<String, Uri> toDeleteMap = recordedProgramUriMapFromSharedPreferences.entrySet()
                        .stream()
                        .filter(e -> !recordedProgramUriMapFromTif.containsKey(e.getKey()))
                        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

                Log.i(TAG, (new StringBuilder())
                        .append("onChange, delete ")
                        .append(toDeleteMap.size())
                        .append(" records!")
                        .toString()
                );


                toDeleteMap.entrySet().forEach(entry -> {
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                Injector.get().dvbLinkClient().removeRecordedProgram(entry.getKey());
                                Log.i(TAG, (new StringBuilder())
                                        .append("onChange, object removed from server ")
                                        .append(entry.getKey())
                                        .toString()
                                );
                            } catch (Exception e) {
                                Log.e(TAG, new StringBuilder()
                                        .append("onChange, object could not be removed from server ")
                                        .append(entry.getKey())
                                        .toString(), e
                                );
                            }
                        }
                    }.start();
                    // TODO: remove also from map because of inconsistency
                    if (recordedProgramUriMapFromSharedPreferences.remove(entry.getKey()) != null) {
                        TvUtils.updateRecordedProgramUriMapFromSharedPreferences(Application.this, recordedProgramUriMapFromSharedPreferences);
                    }
                });
            }
        };
    }
}
