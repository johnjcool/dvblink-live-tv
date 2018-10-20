package io.github.johnjcool.dvblink.live.tv;

import android.database.ContentObserver;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.media.tv.companionlibrary.model.InternalProviderData;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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

                try {
                    String[] projection = {TvContract.RecordedPrograms._ID, TvContract.RecordedPrograms.COLUMN_INTERNAL_PROVIDER_DATA};
                    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                    if (cursor == null) { // deleted
                        deleteRecordedProgramFromRemoteServerAndSharedPreferences(uri);
                    } else { // added
                        cursor.moveToNext();
                        InternalProviderData internalProviderData = new InternalProviderData(cursor.getBlob(1));
                        addRecordedProgramToSharedPreferences(String.valueOf(internalProviderData.get(Constants.KEY_ORGINAL_OBJECT_ID)), uri);
                    }
                } catch (InternalProviderData.ParseException e) {
                    Log.e(TAG, "Error in method getRecordedProgramUriMapFromTif", e);
                }
            }

            private void addRecordedProgramToSharedPreferences(String key, Uri uri) {
                Map<String, Uri> recordedProgramUriMapFromSharedPreferences =
                        TvUtils.getRecordedProgramUriMapFromSharedPreferences(Application.this);
                recordedProgramUriMapFromSharedPreferences.put(key, uri);
                TvUtils.updateRecordedProgramUriMapFromSharedPreferences(
                        Application.this,
                        recordedProgramUriMapFromSharedPreferences
                );
            }

            private void deleteRecordedProgramFromRemoteServerAndSharedPreferences(final Uri uri) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Map<String, Uri> recordedProgramUriMapFromSharedPreferences =
                                    TvUtils.getRecordedProgramUriMapFromSharedPreferences(
                                            Application.this);

                            Optional<String> objectId = recordedProgramUriMapFromSharedPreferences
                                    .entrySet()
                                    .stream()
                                    .filter(entry -> Objects.equals(entry.getValue(), uri))
                                    .map(Map.Entry::getKey)
                                    .findFirst();

                            if (objectId.isPresent()) {
                                Injector.get().dvbLinkClient().removeRecordedProgram(objectId.get());
                                recordedProgramUriMapFromSharedPreferences.remove(objectId.get());
                                TvUtils.updateRecordedProgramUriMapFromSharedPreferences(
                                        Application.this,
                                        recordedProgramUriMapFromSharedPreferences
                                );
                            }
                        } catch (Exception e) {
                            Log.e(TAG, new StringBuilder()
                                    .append("onChange, object could not be removed from server ")
                                    .append(uri)
                                    .toString(), e
                            );
                        }
                    }
                }.start();
            }
        };
    }
}
