package android.shashi_sule.com.notifyme.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.shashi_sule.com.notifyme.tts.SpeechListener;
import android.shashi_sule.com.notifyme.tts.TextToSpeech;
import android.shashi_sule.com.notifyme.utils.Utils;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static android.shashi_sule.com.notifyme.tts.MainActivity.DELAY_MILLIS;

public class MyService extends NotificationListenerService {

    private static final String TAG = "NotificationListenerService";
//    private android.speech.tts.TextToSpeech sTextToSpeech;

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

//        sTextToSpeech = new android.speech.tts.TextToSpeech(getApplicationContext(), this);

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

//    private void speak(final Intent intent, final String state) {
//        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
//            // TODO: 12/3/2017 Ignoring this case for now
//            Log.i(TAG, "handleCallIntent: EXTRA_STATE_IDLE");
//        } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
//            if (intent == null || intent.getExtras() == null) {
//                Log.e(TAG, "speak()->Intent NULL");
//                return;
//            }
//            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
//            Log.i(TAG, "handleCallIntent: EXTRA_STATE_RINGING");
//            Log.i(TAG, "Mobile No: " + number);
//            if (number != null) {
//                String contactName = Utils.getContactName(number, this);
//                Log.i(TAG, "Mobile contact: " + contactName);
//                String text = contactName + " is calling you. Would u like to pick it up?";
//
//                // TODO: 12/3/2017 Pause music if running
//
//                sTextToSpeech = new android.speech.tts.TextToSpeech(this, this);
//                int speak = sTextToSpeech.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH,
//                        Bundle.EMPTY, "11111");
//
//                if (speak == android.speech.tts.TextToSpeech.ERROR) {
//                    Log.e(TAG, "Error while reading text!");
//                    Toast.makeText(this, "Error while reading text!", Toast.LENGTH_SHORT)
//                            .show();
//                } else if (speak == android.speech.tts.TextToSpeech.SUCCESS) {
//                    new Handler().postDelayed(new Runnable() {
//                        public void run() {
//                            initSpeechRecognizer();
//                        }
//                    }, 3000);
//                } else {
//                    Log.e(TAG, "Error: " + speak);
//                }
//
//            } else {
//                Log.e(TAG, "number is null or empty");
//            }
//        } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
//            Log.i(TAG, "handleCallIntent: EXTRA_STATE_OFFHOOK");
//            // TODO: 12/3/2017 Play music if paused previously
//        }
//    }
//
//    private void initSpeechRecognizer() {
//        Log.e(TAG, "initSpeechRecognizer: ");
//
//        Handler handler = getHandler();
//
//        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
//        final Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        recognizerIntent.putExtra(
//                RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
//                getPackageName());
//
//        mSpeechRecognizer.setRecognitionListener(new SpeechListener(handler));
//        mSpeechRecognizer.startListening(recognizerIntent);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mSpeechRecognizer.stopListening();
//            }
//        }, DELAY_MILLIS);
//    }
//
//    @Override
//    public void onInit(final int status) {
//        if (status != android.speech.tts.TextToSpeech.SUCCESS) {
//            Toast.makeText(this, "Text to speech not initiated!", Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "not initiated!");
//        } else {
//            service.putExtra("state", state);
//
//            String state = mIntent.getStringExtra("state");
//            speak(mIntent, state);
//        }
//    }

//    @NonNull
//    private Handler getHandler() {
//        return new Handler(Looper.getMainLooper()) {
//            @Override
//            public void handleMessage(final Message msg) {
//                super.handleMessage(msg);
//                switch (msg.what) {
//                    case 1:
//                        if (msg.obj instanceof ArrayList) {
//                            mSpeechRecognizer.stopListening();
//                            ArrayList<String> list = (ArrayList<String>) msg.obj;
//                            Log.e(TAG, "Words: " + list.toString());
//                            if (list.contains("Ignore") || list.contains("ignore")) {
//                                if (!hangUpIncomingCall()) {
//                                    Log.e(TAG, "Unable to end Call!");
//                                }
//                            } else if (list.contains("Answer") || list.contains("answer")) {
//                                if (!receiveIncomingCall()) {
//                                    Log.i(TAG, "Unable to receive Call!");
//                                }
//                            }
//                        }
//
//                        break;
//                    case 0:
//                        mSpeechRecognizer.stopListening();
//                        if (msg.obj instanceof Integer)
//                            if (msg.obj == (Integer) 7) { // Error code: 7
//                                initSpeechRecognizer();
//                            }
//                        break;
//                }
//            }
//        };
//    }
//
//    public boolean hangUpIncomingCall() {
//        Log.e(TAG, "hangUpIncomingCall: ");
//        try {
//            // Get the boring old TelephonyManager
//            TelephonyManager telephonyManager =
//                    (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//
//            if (telephonyManager == null) {
//                return false;
//            }
//
//            // Get the getITelephony() method
//            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
//            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");
//
//            // Ignore that the method is supposed to be private
//            methodGetITelephony.setAccessible(true);
//
//            // Invoke getITelephony() to get the ITelephony interface
//            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);
//
//            // Get the endCall method from ITelephony
//            Class telephonyInterfaceClass =
//                    Class.forName(telephonyInterface.getClass().getName());
//            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
//
//            // Invoke endCall()
//            methodEndCall.invoke(telephonyInterface);
//
//        } catch (Exception ex) { // Many things can go wrong with reflection calls
//            Log.d(TAG, "PhoneStateReceiver **" + ex.toString());
//            return false;
//        }
//        return true;
//    }
//
//    public boolean receiveIncomingCall() {
//        Log.e(TAG, "receiveIncomingCall: ");
//        try {
//            // Get the boring old TelephonyManager
//            TelephonyManager telephonyManager =
//                    (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//
//            if (telephonyManager == null) {
//                return false;
//            }
//
//            // Get the getITelephony() method
//            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
//            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");
//
//            // Ignore that the method is supposed to be private
//            methodGetITelephony.setAccessible(true);
//
//            // Invoke getITelephony() to get the ITelephony interface
//            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);
//
//            // Get the endCall method from ITelephony
//            Class telephonyInterfaceClass =
//                    Class.forName(telephonyInterface.getClass().getName());
//            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("answerRingingCall");
//
//            // Invoke endCall()
//            methodEndCall.invoke(telephonyInterface);
//
//        } catch (Exception ex) { // Many things can go wrong with reflection calls
//            Log.d(TAG, "PhoneStateReceiver **" + ex.toString());
//            return false;
//        }
//        return true;
//    }

}
