package android.shashi_sule.com.notifyme.extra;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.service.notification.StatusBarNotification;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static android.shashi_sule.com.notifyme.extra.Utils.INTENT_PHONE_STATE;
import static android.shashi_sule.com.notifyme.extra.Utils.REQUEST_CONTACT_PERMISSION;
import static android.shashi_sule.com.notifyme.extra.Utils.REQUEST_PHONE_STATE_PERMISSION;
import static android.shashi_sule.com.notifyme.extra.Utils.REQUEST_RECORD_AUDIO_PERMISSION;
import static android.shashi_sule.com.notifyme.ui.MainActivity.DELAY_MILLIS;

/**
 * @author Shashi on 10/21/2017.
 */

public class HeadPhoneListener extends BroadcastReceiver implements
        android.speech.tts.TextToSpeech.OnInitListener {

    private static final String TAG = "HeadPhoneListener";
    private static android.speech.tts.TextToSpeech sTextToSpeech;
    private Context mContext;
    private Intent mIntent;
    SpeechRecognizer mSpeechRecognizer;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        if (intent.getAction() == null) {
            return;
        }

        mIntent = intent;

        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            handleHeadsetIntent(context, intent);
        } else if (intent.getAction().equals(INTENT_PHONE_STATE)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mContext.checkSelfPermission(Manifest.permission.READ_CONTACTS) ==
                        PackageManager.PERMISSION_GRANTED && mContext.checkSelfPermission(
                        Manifest.permission.READ_PHONE_STATE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    handleCallIntent();
                } else {
                    checkPermissions();
                    Toast.makeText(mContext, "Insufficient permissions!", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utils.requestPermission((Activity) mContext, Manifest.permission.READ_CONTACTS,
                    REQUEST_CONTACT_PERMISSION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utils.requestPermission((Activity) mContext, Manifest.permission.READ_PHONE_STATE,
                    REQUEST_PHONE_STATE_PERMISSION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utils.requestPermission((Activity) mContext, Manifest.permission.RECORD_AUDIO,
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    private void handleHeadsetIntent(final Context context, final Intent intent) {
        int status = intent.getIntExtra("state", -1);
        Intent service = new Intent(context, MyService.class);

        switch (status) {
            case 1:
                Log.i(TAG, "onReceive: Headset plug!!!");

                /*
                 * Start TEXT TO SPEECH service here
                 */
                Log.e(TAG, "onReceive: Starting Text to speech!!!");
                context.startService(service);

                break;

            case 0:
                Log.i(TAG, "onReceive: Headphone disconnected!!!");

                /*
                    Stop TEXT TO SPEECH service here
                 */
                context.stopService(service);
                Log.e(TAG, "onReceive: Stopping Text to speech!!!");
                break;

            default:
                Log.i(TAG, "onReceive: Invalid state!!!");
                break;
        }
    }

    private void handleCallIntent() {
//        String state = mIntent.getStringExtra("state");

        if (sTextToSpeech == null) {
            sTextToSpeech = new android.speech.tts.TextToSpeech(mContext, this);
        }

//        speak(intent, state);

//        Intent service = new Intent(context, MyService.class);
//        context.startService(service);

    }

    private void speak(final Intent intent, final String state) {
        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            // TODO: 12/3/2017 Ignoring this case for now
            Log.i(TAG, "handleCallIntent: EXTRA_STATE_IDLE");
        } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.i(TAG, "handleCallIntent: EXTRA_STATE_RINGING");
            Log.i(TAG, "Mobile No: " + number);
            if (number != null) {

                String contactName = Utils.getContactName(number, mContext);
                Log.i(TAG, "Mobile contact: " + contactName);
                String text = contactName + " is calling you. Would u like to pick it up?";

                // TODO: 12/3/2017 Pause music if running

                int speak = sTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH,
                        Bundle.EMPTY, "11111");

                if (speak == TextToSpeech.ERROR) {
                    Log.e("TextToSpeech", "Error while reading text!");
                    Toast.makeText(mContext, "Error while reading text!", Toast.LENGTH_SHORT)
                            .show();
                } else if (speak == TextToSpeech.SUCCESS) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            initSpeechRecognizer();
                        }
                    }, 3000);
                } else {
                    Log.e("TextToSpeech", "Error: " + speak);
                }

            }
        } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            Log.i(TAG, "handleCallIntent: EXTRA_STATE_OFFHOOK");
            // TODO: 12/3/2017 Play music if paused previously
        }
    }

    private void initSpeechRecognizer() {
        Log.e(TAG, "initSpeechRecognizer: ");

        Handler handler = getHandler();

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
        final Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                mContext.getPackageName());

        mSpeechRecognizer.setRecognitionListener(new SpeechListener(handler));
        mSpeechRecognizer.startListening(recognizerIntent);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSpeechRecognizer.stopListening();
            }
        }, DELAY_MILLIS);
    }

    @NonNull
    private Handler getHandler() {
        return new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(final Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        if (msg.obj instanceof ArrayList) {
                            mSpeechRecognizer.stopListening();
                            ArrayList<String> list = (ArrayList<String>) msg.obj;
                            Log.e(TAG, "Words: " + list.toString());
                            if (list.contains("Ignore") || list.contains("ignore")) {
                                if (!hangUpIncomingCall()) {
                                    Log.e(TAG, "Unable to end Call!");
                                }
                            } else if (list.contains("Answer") || list.contains("answer")) {
                                if (!receiveIncomingCall()) {
                                    Log.i(TAG, "Unable to receive Call!");
                                }
                            }
                        }

                        break;
                    case 0:
                        mSpeechRecognizer.stopListening();
                        if (msg.obj instanceof Integer)
                            if (msg.obj == (Integer) 7) { // Error code: 7
                                initSpeechRecognizer();
                            }
                        break;
                }
            }
        };
    }

    void readNotification(StatusBarNotification notification) {
        if (sTextToSpeech == null) return;

        Notification not = notification.getNotification();
        CharSequence text = not.tickerText;
        int speak = sTextToSpeech.speak(text, android.speech.tts.TextToSpeech.QUEUE_ADD, Bundle.EMPTY, "1111");

        if (speak < 0) {
            Toast.makeText(mContext, "Unable to speak!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "readNotification: Unable to speak!");
        }
    }

    @Override
    public void onInit(final int status) {
        if (status != android.speech.tts.TextToSpeech.SUCCESS) {
            Toast.makeText(mContext, "Text to speech not initiated!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "not initiated!");
        } else {
            String state = mIntent.getStringExtra("state");
            speak(mIntent, state);
        }
    }

    public boolean hangUpIncomingCall() {
        Log.e(TAG, "hangUpIncomingCall: ");
        try {
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager =
                    (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

            if (telephonyManager == null) {
                return false;
            }

            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

            // Get the endCall method from ITelephony
            Class telephonyInterfaceClass =
                    Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);

        } catch (Exception ex) { // Many things can go wrong with reflection calls
            Log.d(TAG, "PhoneStateReceiver **" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean receiveIncomingCall() {
        Log.e(TAG, "receiveIncomingCall: ");
        try {
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager =
                    (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

            if (telephonyManager == null) {
                return false;
            }

            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

            // Get the endCall method from ITelephony
            Class telephonyInterfaceClass =
                    Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("answerRingingCall");

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);

        } catch (Exception ex) { // Many things can go wrong with reflection calls
            Log.d(TAG, "PhoneStateReceiver **" + ex.toString());
            return false;
        }
        return true;
    }
}
