package android.shashi_sule.com.notifyme.extra;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class MyService extends NotificationListenerService {

    private static final String TAG = "NotificationListenerService";

    public MyService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressLint("LongLogTag")
    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.e(TAG, "onStartCommand: Service started");

        return Service.START_STICKY;
    }

    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {
        Log.e("onNotificationPosted: ", String.valueOf(sbn.getNotification().tickerText));

        TextToSpeech.getInstance().readNotification(sbn);
    }

    @Override
    public void onNotificationRemoved(final StatusBarNotification sbn) {
        Log.e("onNotificationRemoved: ", String.valueOf(sbn.getNotification().tickerText));
        TextToSpeech.getInstance().readNotification(sbn);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: Service stopped");
    }

}
