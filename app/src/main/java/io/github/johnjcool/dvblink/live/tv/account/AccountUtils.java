package io.github.johnjcool.dvblink.live.tv.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import io.github.johnjcool.dvblink.live.tv.Constants;
import io.github.johnjcool.dvblink.live.tv.di.ServiceModule;
import io.github.johnjcool.dvblink.live.tv.remote.DvbLinkApi;
import io.github.johnjcool.dvblink.live.tv.remote.DvbLinkClient;

public class AccountUtils {
    // TODO: Suppressing warnings is bad... But, the Android linter is broken. getAccountsByType
    // does not require the GET_ACCOUNTS permission, it simply will return other apps accounts if
    // that permission has been granted. Remove the supression once Android's linter is smarter.

    @SuppressLint({"MissingPermission"})
    public static Account getActiveAccount(Context context) {
        // TODO: We want to support multiple accounts, allowing you to switch between accounts,
        //       which would force a Channel+EPG purge and resync.
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);

        if (accounts.length > 0) {
            return accounts[0];
        }

        return null;
    }

    @SuppressLint({"MissingPermission"})
    public static Account[] getAllAccounts(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        return accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
    }

    @SuppressLint({"MissingPermission"})
    public static void addOnAccountsUpdatedListener(Context context, final OnAccountsUpdateListener listener, Handler handler, boolean updateImmediately) {
        AccountManager accountManager = AccountManager.get(context);
        accountManager.addOnAccountsUpdatedListener(listener, handler, updateImmediately);
    }

    public static DvbLinkClient getDvbLinkClient(String host, int port, String username, String password) {
        ServiceModule module = new ServiceModule();
        XmlMapper mapper = module.provideXmlMapper();

        DvbLinkApi api = module.provideDvbLinkApi(
                module.provideRetrofit(
                        host,
                        port,
                        mapper,
                        module.provideOkHttpClient(username, password)
                )
        );
        return new DvbLinkClient(api, mapper, host);
    }
}
