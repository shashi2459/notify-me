package android.shashi_sule.com.notifyme.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalPreferences {

    private static LocalPreferences sInstance;
    private static String PREF_NAME = "com.notifyme.prefs";

    private String PREF_KEY_AUDIO_NOTIFICATIONS = "com.notifyme.prefs.KEY_AUDIO_NOTIFICATION";

    private LocalPreferences() {
    }

    public static LocalPreferences getInstance() {
        if (sInstance == null) {
            sInstance = new LocalPreferences();
        }

        return sInstance;
    }

    public boolean isNotificationsEnabled(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PREF_KEY_AUDIO_NOTIFICATIONS, true);
    }

    public void put(boolean notify, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(PREF_KEY_AUDIO_NOTIFICATIONS, notify).apply();
    }


}