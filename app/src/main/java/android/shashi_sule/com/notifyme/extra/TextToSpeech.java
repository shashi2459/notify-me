package android.shashi_sule.com.notifyme.extra;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

class TextToSpeech implements android.speech.tts.TextToSpeech.OnInitListener {

    private Context mContext;

    private static TextToSpeech sInstance;
    private static android.speech.tts.TextToSpeech sTextToSpeech;
    private final String TAG = "TextToSpeech";

    private TextToSpeech() {

    }

    static TextToSpeech getInstance() {
        if (sInstance == null) {
            sInstance = new TextToSpeech();
        }
        return sInstance;
    }

    void init(final Context context) {
        mContext = context;
        if (sTextToSpeech == null) {
            sTextToSpeech = new android.speech.tts.TextToSpeech(mContext, this);
        }
    }

    void readNotification(StatusBarNotification notification) {
        if (sTextToSpeech == null) return;

        Notification not = notification.getNotification();
        CharSequence text = not.tickerText;
        int speak = sTextToSpeech.speak(text, android.speech.tts.TextToSpeech.QUEUE_ADD, Bundle.EMPTY, "1111");

        if (speak < 0) {
            Toast.makeText(mContext, "Unable to speak!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "readNotification: Unable to speak!");
        }
    }

    void readNotification(String text) {
        if (sTextToSpeech == null) return;

        int speak = sTextToSpeech.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH,
                null, "111");

        if (speak < 0) {
            Toast.makeText(mContext, "Unable to speak!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "readNotification: Unable to speak!");
        }
    }

    void stop() {
        if (sTextToSpeech != null)
            sTextToSpeech.stop();
    }

    @Override
    public void onInit(final int status) {
        if (status != android.speech.tts.TextToSpeech.SUCCESS) {
            Toast.makeText(mContext, "Text to speech not initiated!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "not initiated!");
        }
    }

}