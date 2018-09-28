package io.github.johnjcool.dvblink.live.tv.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import io.github.johnjcool.dvblink.live.tv.R;

public class Authenticator extends AbstractAccountAuthenticator {
    private static final String TAG = Authenticator.class.getName();

    private final Context mContext;
    private final Handler mHandler;

    public Authenticator(Context context) {
        super(context);
        mContext = context;
        mHandler = new Handler();
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        // Do we already have an account?
        final Account account = AccountUtils.getActiveAccount(mContext);

        final Bundle bundle = new Bundle();

        if (account != null) {
            final String message = mContext.getString(R.string.setup_only_one_account);

            bundle.putInt(AccountManager.KEY_ERROR_CODE, 1);
            bundle.putString(AccountManager.KEY_ERROR_MESSAGE, message);

            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            final Intent intent = new Intent(mContext, AuthenticatorActivity.class);

            intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        }

        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }
}
