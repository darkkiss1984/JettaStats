package ro.vw.aa;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.github.martoreto.aauto.vex.CarStatsClient;
import com.google.android.apps.auto.sdk.notification.CarNotificationExtender;

import java.util.Date;
import java.util.Map;

/**
 * Created by mihnea.radulescu on 1/11/2018.
 */

public class GearMonitor implements CarStatsClient.Listener {
    private static final String TAG = "GearMonitor";

    private static final String PREF_ENABLED = "gearMonitoringActive";
    private static final String EXLAP_KEY_CURRENT_GEAR = "currentGear";
    private static final String EXLAP_KEY_RECOMMENDED_GEAR = "recommendedGear";

    private static final int NOTIFICATION_ID = 2;

    private static final int NOTIFICATION_TIMEOUT_MS = 60000;

    private final Handler mHandler;
    private final NotificationManager mNotificationManager;

    private final Context mContext;
    private boolean mIsEnabled;



    enum State {
        UNKNOWN,
        CHANGE_NEEDED,
        CHANGE_NOT_NEEDED
    }

    private State mState = State.UNKNOWN;

    GearMonitor(Context context, Handler handler) {
        super();

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mHandler = handler;
        mContext = context;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.registerOnSharedPreferenceChangeListener(mPreferencesListener);
        readPreferences(sharedPreferences);
    }

    private void readPreferences(SharedPreferences preferences) {
        mIsEnabled = preferences.getBoolean(PREF_ENABLED, true);
        if (!mIsEnabled) {
            mHandler.post(mDismissNotification);
            mState = State.UNKNOWN;
        }
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener mPreferencesListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            readPreferences(sharedPreferences);
        }
    };

    private final Runnable mDismissNotification = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Dismissing gear change notification");
            mNotificationManager.cancel(TAG, NOTIFICATION_ID);
        }
    };

    private void notifyGearChange(String currentGear, String recommendedGear) {
        CarNotificationSoundPlayer soundPlayer = new CarNotificationSoundPlayer(mContext, R.raw.bubble);
        soundPlayer.play();
    }

    @Override
    public void onNewMeasurements(String provider, Date timestamp, Map<String, Object> values) {
        if (!mIsEnabled) {
            return;
        }
        if (values.containsKey(EXLAP_KEY_CURRENT_GEAR) && values.containsKey(EXLAP_KEY_RECOMMENDED_GEAR)) {
            String currentGear = (String) values.get(EXLAP_KEY_CURRENT_GEAR);
            String recommendedGear = (String) values.get(EXLAP_KEY_RECOMMENDED_GEAR);

            if (currentGear == null || recommendedGear == null) {
                mState = State.UNKNOWN;
            } else if (mState == State.UNKNOWN && !currentGear.equalsIgnoreCase(recommendedGear) && !"NoRecommendation".equalsIgnoreCase(recommendedGear)) {
                mState = State.CHANGE_NEEDED;
                notifyGearChange(currentGear,recommendedGear);
            } else if (mState == State.UNKNOWN) {
                mState = State.CHANGE_NOT_NEEDED;
            } else if (mState == State.CHANGE_NOT_NEEDED && !currentGear.equalsIgnoreCase(recommendedGear) && !"NoRecommendation".equalsIgnoreCase(recommendedGear)) {
                mState = State.CHANGE_NEEDED;
                notifyGearChange(currentGear,recommendedGear);
            } else if (mState == State.CHANGE_NEEDED && (currentGear.equalsIgnoreCase(recommendedGear) || !"NoRecommendation".equalsIgnoreCase(recommendedGear))) {
                mState = State.CHANGE_NOT_NEEDED;
            }
        }
    }

    public synchronized void close() {
        mHandler.post(mDismissNotification);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(mPreferencesListener);

    }
}
