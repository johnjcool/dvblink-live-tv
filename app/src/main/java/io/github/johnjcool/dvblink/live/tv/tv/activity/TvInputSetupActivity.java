package io.github.johnjcool.dvblink.live.tv.tv.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.tv.TvInputInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v17.leanback.widget.GuidedActionsStylist;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

import io.github.johnjcool.dvblink.live.tv.Application;
import io.github.johnjcool.dvblink.live.tv.Constants;
import io.github.johnjcool.dvblink.live.tv.R;
import io.github.johnjcool.dvblink.live.tv.account.AccountUtils;
import io.github.johnjcool.dvblink.live.tv.di.Injector;
import io.github.johnjcool.dvblink.live.tv.settings.SettingsActivity;
import io.github.johnjcool.dvblink.live.tv.tv.TvUtils;
import io.github.johnjcool.dvblink.live.tv.tv.service.EpgSyncJobService;

public class TvInputSetupActivity extends Activity {
    private static final String TAG = TvInputSetupActivity.class.getName();

    public static final long FULL_SYNC_FREQUENCY_MILLIS = 1000 * 60 * 60 * 24;  // 24 hour
    private static final long FULL_SYNC_WINDOW_SEC = 1000 * 60 * 60 * 24 * 14;  // 2 weeks

    static AccountManager mAccountManager;
    static Account sAccount;
    static boolean mErrorFound;
    static String mInputId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GuidedStepFragment fragment = new IntroFragment();
        fragment.setArguments(getIntent().getExtras());
        GuidedStepFragment.addAsRoot(this, fragment, android.R.id.content);
    }

    static abstract class BaseGuidedStepFragment extends GuidedStepFragment {

        @Override
        public int onProvideTheme() {
            return R.style.Theme_Wizard_Setup;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            String inputId = getActivity().getIntent().getStringExtra(TvInputInfo.EXTRA_INPUT_ID);

            if (!inputId.equals(TvUtils.getInputId())) {
                // Ensure the provided ID matches what we expect, as we only have a single input.
                throw new RuntimeException(
                        "Setup Activity called for unknown inputId: " + inputId + " (expected: " + TvUtils.getInputId() + ")");
            }

            mAccountManager = AccountManager.get(getActivity());
        }

        Account getAccountByName(String name) {
            Log.d(TAG, "getAccountByName(" + name + ")");

            Account[] accounts = AccountUtils.getAllAccounts(getActivity());

            Log.d(TAG, "Checking " + Integer.toString(accounts.length) + " accounts");

            for (Account account : accounts) {
                Log.d(TAG, "Checking Account: " + account.name);

                if (account.name.equals(name)) {
                    Log.d(TAG, "Found account");
                    return account;
                }
            }

            Log.d(TAG, "Failed to find account, no accounts with matching name");
            return null;
        }
    }

    public static class IntroFragment extends BaseGuidedStepFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Context context = getActivity();

            // Stop the EPG service
            Intent intent = new Intent(context, EpgSyncJobService.class);
            context.stopService(intent);
        }

        @NonNull
        @Override
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {

            return new GuidanceStylist.Guidance(
                    getString(R.string.setup_intro_title),
                    getString(R.string.setup_intro_body),
                    getString(R.string.account_label),
                    null);
        }

        @Override
        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            GuidedAction action = new GuidedAction.Builder(getActivity())
                    .title(R.string.setup_begin_title)
                    .description(R.string.setup_begin_body)
                    .editable(false)
                    .build();

            actions.add(action);
        }

        @Override
        public void onGuidedActionClicked(GuidedAction action) {
            // Move onto the next step
            GuidedStepFragment fragment = new AccountSelectorFragment();
            fragment.setArguments(getArguments());
            add(getFragmentManager(), fragment);
        }
    }

    public static class AccountSelectorFragment extends BaseGuidedStepFragment {
        private static final int ACTION_ID_CONFIRM = 1;
        private static final int ACTION_ID_SELECT_ACCOUNT = 2;
        private static final int ACTION_ID_NEW_ACCOUNT = 3;

        @NonNull
        @Override
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {

            return new GuidanceStylist.Guidance(
                    getString(R.string.setup_account_title),
                    getString(R.string.setup_account_body),
                    getString(R.string.account_label),
                    null);
        }

        @Override
        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            List<GuidedAction> subActions = new ArrayList<>();

            GuidedAction action = new GuidedAction.Builder(getActivity())
                    .id(ACTION_ID_SELECT_ACCOUNT)
                    .title(R.string.setup_account_action_title)
                    .editTitle("")
                    .description(R.string.setup_account_title)
                    .subActions(subActions)
                    .build();

            actions.add(action);

            action = new GuidedAction.Builder(getActivity())
                    .id(ACTION_ID_CONFIRM)
                    .title(R.string.setup_confirm)
                    .description(R.string.setup_confirm_body)
                    .editable(false)
                    .build();
            action.setEnabled(false);

            actions.add(action);
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.d(TAG, "onResume()");
            GuidedAction accountAction = findActionById(ACTION_ID_SELECT_ACCOUNT);

            List<GuidedAction> accountSubActions = accountAction.getSubActions();
            accountSubActions.clear();

            Account[] accounts = AccountUtils.getAllAccounts(getActivity());

            for (Account account : accounts) {
                GuidedAction action = new GuidedAction.Builder(getActivity())
                        .title(account.name)
                        .description(mAccountManager.getUserData(account, Constants.KEY_HOSTNAME))
                        .checkSetId(GuidedAction.DEFAULT_CHECK_SET_ID)
                        .build();

                accountSubActions.add(action);
            }

            accountSubActions.add(new GuidedAction.Builder(getActivity())
                    .id(ACTION_ID_NEW_ACCOUNT)
                    .title(R.string.setup_account_create)
                    .description("")
                    .editable(false)
                    .build()
            );

            if (sAccount != null) {
                accountAction.setDescription(sAccount.name);
                findActionById(ACTION_ID_CONFIRM).setEnabled(true);
            } else {
                findActionById(ACTION_ID_CONFIRM).setEnabled(false);
            }

            notifyActionChanged(findActionPositionById(ACTION_ID_CONFIRM));
        }

        @Override
        public boolean onSubGuidedActionClicked(GuidedAction action) {
            if (action.isChecked()) {
                sAccount = getAccountByName(action.getTitle().toString());

                findActionById(ACTION_ID_SELECT_ACCOUNT).setDescription(sAccount.name);
                notifyActionChanged(findActionPositionById(ACTION_ID_SELECT_ACCOUNT));

                findActionById(ACTION_ID_CONFIRM).setEnabled(true);
                notifyActionChanged(findActionPositionById(ACTION_ID_CONFIRM));

                return true;
            } else {

                mAccountManager.addAccount(Constants.ACCOUNT_TYPE, null, null, new Bundle(), getActivity(), new AddAccountCallback(), null);
                return true;
            }
        }

        private class AddAccountCallback implements AccountManagerCallback<Bundle> {
            @Override
            public void run(AccountManagerFuture<Bundle> result) {
                onResume();
            }
        }

        @Override
        public void onGuidedActionClicked(GuidedAction action) {
            if (ACTION_ID_CONFIRM == action.getId()) {
                // Move onto the next step
                GuidedStepFragment fragment = new SyncingFragment();
                fragment.setArguments(getArguments());
                add(getFragmentManager(), fragment);
            }
        }
    }

    public static class SyncingFragment extends BaseGuidedStepFragment {

        private boolean mFinishedScan;

        private final BroadcastReceiver mSyncStatusChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mFinishedScan) {
                            return;
                        }
                        String syncStatusChangedInputId = intent.getStringExtra(
                                EpgSyncJobService.BUNDLE_KEY_INPUT_ID);
                        if (syncStatusChangedInputId != null
                                && syncStatusChangedInputId.equals(mInputId)) {
                            String syncStatus = intent.getStringExtra(EpgSyncJobService.SYNC_STATUS);
                            if (syncStatus.equals(EpgSyncJobService.SYNC_STARTED)) {

                                Log.d(TAG, "Sync status: Started");

                            } else if (syncStatus.equals(EpgSyncJobService.SYNC_SCANNED)) {
                                int channelsScanned = intent.
                                        getIntExtra(EpgSyncJobService.BUNDLE_KEY_CHANNELS_SCANNED, 0);
                                int channelCount = intent.
                                        getIntExtra(EpgSyncJobService.BUNDLE_KEY_CHANNEL_COUNT, 0);
                                updateScanProgress(++channelsScanned, channelCount);
                                String channelDisplayName = intent.getStringExtra(
                                        EpgSyncJobService.BUNDLE_KEY_SCANNED_CHANNEL_DISPLAY_NAME);
                                String channelDisplayNumber = intent.getStringExtra(
                                        EpgSyncJobService.BUNDLE_KEY_SCANNED_CHANNEL_DISPLAY_NUMBER);

                                Log.d(TAG, "Sync status: Channel Scanned");
                                Log.d(TAG, "Scanned " + channelsScanned + " out of " + channelCount);

                                onScannedChannel(channelDisplayName, channelDisplayNumber);

                            } else if (syncStatus.equals(EpgSyncJobService.SYNC_FINISHED)) {

                                Log.d(TAG, "Sync status: Finished");

                                finishScan(true);
                            } else if (syncStatus.equals(EpgSyncJobService.SYNC_ERROR)) {
                                int errorCode =
                                        intent.getIntExtra(EpgSyncJobService.BUNDLE_KEY_ERROR_REASON,
                                                0);

                                Log.d(TAG, "Error occurred: " + errorCode);
                            }
                        }
                    }
                });
            }
        };

        private void finishScan(boolean scanCompleted) {
            // Hides the cancel button.
            mFinishedScan = true;
            Log.d(TAG, "Initial Sync Completed");

            // Move to the CompletedFragment
            GuidedStepFragment fragment = new CompletedFragment();
            fragment.setArguments(getArguments());
            add(getFragmentManager(), fragment);
        }

        /**
         * This method will be called when a channel has been completely scanned. It can be overriden
         * to display custom information about this channel to the user.
         *
         * @param displayName   {@link com.google.android.media.tv.companionlibrary.model.Channel#getDisplayName()} for the scanned channel.
         * @param displayNumber {@link com.google.android.media.tv.companionlibrary.model.Channel#getDisplayNumber()} ()} for the scanned channel.
         */
        public void onScannedChannel(CharSequence displayName, CharSequence displayNumber) {
            Log.d(TAG, "Scanned channel data: " + displayName + ", " + displayNumber);
        }


        private void updateScanProgress(int channelsScanned, int channelCount) {
            Log.d(TAG, "Scanned channel data: " + channelsScanned + ", " + channelCount);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mInputId = getActivity().getIntent().getStringExtra(TvInputInfo.EXTRA_INPUT_ID);
        }

        @Override
        public void onStart() {
            super.onStart();

            EpgSyncJobService.cancelAllSyncRequests(getActivity());

            // Set up SharedPreference to share inputId. If there is not periodic sync job after reboot,
            // RichBootReceiver can use the shared inputId to set up periodic sync job.
            Account account = AccountUtils.getActiveAccount(getActivity().getBaseContext());

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                    EpgSyncJobService.PREFERENCE_EPG_SYNC, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(EpgSyncJobService.BUNDLE_KEY_INPUT_ID, mInputId);
            editor.putString(Constants.KEY_HOSTNAME, mAccountManager.getUserData(account, Constants.KEY_HOSTNAME));
            editor.putString(Constants.KEY_PORT, mAccountManager.getUserData(account, Constants.KEY_PORT));
            editor.putString(Constants.KEY_USERNAME, account.name);
            editor.putString(Constants.KEY_PASSWORD, mAccountManager.getPassword(account));
            editor.apply();

            SharedPreferences test = Injector.get().sharedPreferences();

            ((Application) getActivity().getApplication()).resetComponents();
            EpgSyncJobService.requestImmediateSync(getActivity(), mInputId,
                    new ComponentName(getActivity(), EpgSyncJobService.class));

            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                    mSyncStatusChangedReceiver,
                    new IntentFilter(EpgSyncJobService.ACTION_SYNC_STATUS_CHANGED));
        }


        @Override
        public GuidedActionsStylist onCreateActionsStylist() {
            return new GuidedActionsStylist() {
                @Override
                public int onProvideItemLayoutId() {
                    return R.layout.setup_progress;
                }

            };
        }

        @Override
        public int onProvideTheme() {
            return R.style.Theme_Wizard_Setup_NoSelector;
        }

        @NonNull
        @Override
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {

            return new GuidanceStylist.Guidance(
                    getString(R.string.setup_sync_title),
                    getString(R.string.setup_sync_body),
                    getString(R.string.account_label),
                    null);
        }

        @Override
        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            GuidedAction action = new GuidedAction.Builder(getActivity())
                    .title(R.string.setup_progress_body)
                    .infoOnly(true)
                    .build();
            actions.add(action);
        }
    }

    public static class CompletedFragment extends BaseGuidedStepFragment {
        private static final int ACTION_ID_SETTINGS = 1;
        private static final int ACTION_ID_COMPLETE = 2;

        @NonNull
        @Override
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {

            return new GuidanceStylist.Guidance(
                    getString(R.string.setup_complete_title),
                    getString(R.string.setup_complete_body),
                    getString(R.string.account_label),
                    null);
        }

        @Override
        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            GuidedAction action = new GuidedAction.Builder(getActivity())
                    .id(ACTION_ID_SETTINGS)
                    .title(R.string.setup_settings_title)
                    .description(R.string.setup_settings_body)
                    .editable(false)
                    .build();

            actions.add(action);

            action = new GuidedAction.Builder(getActivity())
                    .id(ACTION_ID_COMPLETE)
                    .title(R.string.setup_complete_action_title)
                    .description(R.string.setup_account_complete_body)
                    .editable(false)
                    .build();

            actions.add(action);
        }

        @Override
        public void onGuidedActionClicked(GuidedAction action) {
            if (action.getId() == ACTION_ID_SETTINGS) {
                startActivity(SettingsActivity.getPreferencesIntent(getActivity()));
            } else if (action.getId() == ACTION_ID_COMPLETE) {
                // Store the Setup Complete preference
                TvUtils.setSetupComplete(getActivity(), true);


                EpgSyncJobService.cancelAllSyncRequests(getActivity());
                EpgSyncJobService.setUpPeriodicSync(getActivity(), mInputId,
                        new ComponentName(getActivity(), EpgSyncJobService.class),
                        FULL_SYNC_FREQUENCY_MILLIS, FULL_SYNC_WINDOW_SEC);
                getActivity().setResult(Activity.RESULT_OK);

                getActivity().finish();
            }
        }
    }
}