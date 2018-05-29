package ro.vw.aa;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;


import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.bigquery.BigqueryScopes;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App extends Application {
    static final String PREF_ACCOUNT_NAME = "accountName";

    private GoogleAccountCredential mCredential;

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.getLogger("com.google.api.client").setLevel(Level.OFF);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        // Google Accounts
        mCredential =
                GoogleAccountCredential.usingOAuth2(this,
                        Arrays.asList(BigqueryScopes.BIGQUERY, BigqueryScopes.BIGQUERY_INSERTDATA));
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        mCredential.setSelectedAccountName(settings.getString(App.PREF_ACCOUNT_NAME, null));

      }

    public GoogleAccountCredential getGoogleCredential() {
        return mCredential;
    }

}
