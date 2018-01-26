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

        /**
         * Logging level for HTTP requests/responses.
         *
         * <p>
         * To turn on, set to {@link Level#CONFIG} or {@link Level#ALL} and run this from command line:
         * </p>
         *
         * <pre>
         adb shell setprop log.tag.HttpTransport DEBUG
         * </pre>
         */
        Logger.getLogger("com.google.api.client").setLevel(Level.OFF);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        // Google Accounts
        mCredential =
                GoogleAccountCredential.usingOAuth2(this,
                        Arrays.asList(BigqueryScopes.BIGQUERY, BigqueryScopes.BIGQUERY_INSERTDATA));
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        mCredential.setSelectedAccountName(settings.getString(App.PREF_ACCOUNT_NAME, null));

        if ( isExternalStorageWritable() ) {

            File appDirectory = new File( Environment.getExternalStorageDirectory() + "/Carlogs" );
            File logDirectory = new File( appDirectory + "/log" );
            File logFile = new File( logDirectory, "logcat" + System.currentTimeMillis() + ".txt" );

            // create app folder
            if ( !appDirectory.exists() ) {
                appDirectory.mkdir();
            }

            // create log folder
            if ( !logDirectory.exists() ) {
                logDirectory.mkdir();
            }

            // clear the previous logcat and then write the new one to the file
            try {
                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile);
            } catch ( IOException e ) {
                e.printStackTrace();
            }

        } else if ( isExternalStorageReadable() ) {
            // only readable
        } else {
            // not accessible
        }


    }

    public GoogleAccountCredential getGoogleCredential() {
        return mCredential;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals( state ) ) {
            return true;
        }
        return false;
    }

}
