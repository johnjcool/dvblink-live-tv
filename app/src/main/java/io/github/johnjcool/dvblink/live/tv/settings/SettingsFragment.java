package io.github.johnjcool.dvblink.live.tv.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v17.preference.LeanbackSettingsFragment;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;

import io.github.johnjcool.dvblink.live.tv.Constants;
import io.github.johnjcool.dvblink.live.tv.R;


public class SettingsFragment extends LeanbackSettingsFragment implements DialogPreference.TargetFragment {
    private static final String TAG = SettingsFragment.class.getName();

    private PreferenceFragment mPreferenceFragment;

    @Override
    public void onPreferenceStartInitialScreen() {
        mPreferenceFragment = buildPreferenceFragment(null);
        startPreferenceFragment(mPreferenceFragment);
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragment preferenceFragment, Preference preference) {
        return false;
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragment preferenceFragment, PreferenceScreen preferenceScreen) {
        PreferenceFragment fragment = buildPreferenceFragment(preferenceScreen.getKey());

        startPreferenceFragment(fragment);

        return true;
    }

    private PreferenceFragment buildPreferenceFragment(String root) {
        PreferenceFragment fragment = new LocalLeanbackPreferenceFragment();

        Bundle args = new Bundle();
        args.putString(PreferenceFragment.ARG_PREFERENCE_ROOT, root);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Preference findPreference(CharSequence charSequence) {
        return mPreferenceFragment.findPreference(charSequence);
    }

    public static class LocalLeanbackPreferenceFragment extends LeanbackPreferenceFragment {

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            getPreferenceManager().setSharedPreferencesName(Constants.PREFERENCE_TVHEADEND);
            getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);

            String root = getArguments().getString(PreferenceFragment.ARG_PREFERENCE_ROOT, null);

            if (root == null) {
                addPreferencesFromResource(R.xml.preferences);
            } else {
                setPreferencesFromResource(R.xml.preferences, root);
            }
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            Log.d(TAG, "Test: onPreferenceTreeClick");

            return super.onPreferenceTreeClick(preference);
        }
    }
}
