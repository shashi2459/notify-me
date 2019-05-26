package android.shashi_sule.com.notifyme.service;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.shashi_sule.com.notifyme.storage.LocalPreferences;
import android.shashi_sule.com.notifyme.tts.SpeechListener;
import android.shashi_sule.com.notifyme.utils.Utils;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

import static android.shashi_sule.com.notifyme.tts.MainActivity.DELAY_MILLIS;
import static android.shashi_sule.com.notifyme.utils.Utils.INTENT_PHONE_STATE;
import static android.shashi_sule.com.notifyme.utils.Utils.INTENT_PHONE_STATE_CHANGED;
import static android.shashi_sule.com.notifyme.utils.Utils.REQUEST_BLUETOOTH_PERMISSION;
import static android.shashi_sule.com.notifyme.utils.Utils.REQUEST_CONTACT_PERMISSION;
import static android.shashi_sule.com.notifyme.utils.Utils.REQUEST_PHONE_STATE_PERMISSION;
import static android.shashi_sule.com.notifyme.utils.Utils.REQUEST_RECORD_AUDIO_PERMISSION;

/**
 * @author Shashi on 10/21/2017.
 */

public class HeadPhoneListener extends BroadcastReceiver implements
        android.speech.tts.TextToSpeech.OnInitListener {

    public static final String UTTERANCE_ID = "11111";
    private static final String TAG = "HeadPhoneListener";
    private static android.speech.tts.TextToSpeech sTextToSpeech;
    private Context mContext;
    private Intent mIntent;
    private SpeechRecognizer mSpeechRecognizer;
    private boolean mHangedUp;
    private boolean mDisconnected;
    private boolean mReceived;

    private Handler mMainHandler;

    private Looper mSpeechLooper;
    private Looper mMainLooper;


    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        String action = intent.getAction();
        Log.e(TAG, "onReceive#STATE: " + action);

        if (action == null) {
            return;
        }

        mIntent = intent;

        if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
//            handleHeadsetIntent(context, intent);
        } else if (action.equals(INTENT_PHONE_STATE) || action.equals(INTENT_PHONE_STATE_CHANGED)) {
            Log.e(TAG, "onReceive#STATE: " + action);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mContext.checkSelfPermission(Manifest.permission.READ_CONTACTS) ==
                        PackageManager.PERMISSION_GRANTED && mContext.checkSelfPermission(
                        Manifest.permission.READ_PHONE_STATE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    String number = Objects.requireNonNull(mIntent.getExtras()).getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    if (!TextUtils.isEmpty(number))
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utils.requestPermission((Activity) mContext, Manifest.permission.BLUETOOTH,
                    REQUEST_BLUETOOTH_PERMISSION);
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
                 * Stop TEXT TO SPEECH service here
                 */
                context.stopService(service);
                Log.e(TAG, "onReceive: Stopping Text to speech!!!");
                break;

            default:
                Log.i(TAG, "onReceive: Invalid state!!!");
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void handleCallIntent() {
        final String state = mIntent.getStringExtra("state");
        Log.e(TAG, "handleCallIntent#STATE: " + state);

        String number = Objects.requireNonNull(mIntent.getExtras()).getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
        if (!Utils.isNumberValid(number)) {
            hangUpIncomingCall();
        }

        /*
         * Check for
         * Notifications enabled from app settings
         */
        boolean notificationsEnabled = LocalPreferences
                .getInstance().isNotificationsEnabled(mContext);
        if (!notificationsEnabled) {
            Log.d(TAG, "handleCallIntent: Notifications not enabled!");
            return;
        }

        if (!Utils.isHeadsetsConnected(mContext)) {
            Log.d(TAG, "handleCallIntent: Headsets are not plugged!");
            return;
        }

        /* Check if Music is active */
        /*if (mAudioManager != null && mAudioManager.isMusicActive()) {

         *//* Get Stream volume value *//*
            sStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

            *//* Mute Steam and unMute later *//*
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);

        }*/


        if (sTextToSpeech == null) {

//            IBinder iBinder = peekService(mContext, mIntent);

//            Intent serviceIntent = new Intent(mContext, TextToSpeechService.class);
//            mContext.getApplicationContext().bindService(serviceIntent, new ServiceConnection() {
//                @Override
//                public void onServiceConnected(final ComponentName name, final IBinder service) {
//                    Log.e(TAG, "onServiceConnected: ");
//                    sTextToSpeech = new android.speech.tts.TextToSpeech(mContext.getApplicationContext(), HeadPhoneListener.this);
//
//                    try {
//                        speak(mIntent, state);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onServiceDisconnected(final ComponentName name) {
//                    Log.e(TAG, "onServiceDisconnected: ");
//                }
//            }, Context.BIND_AUTO_CREATE);

            sTextToSpeech = new android.speech.tts.TextToSpeech(mContext.getApplicationContext(), HeadPhoneListener.this);

        }

    }

    private void speak(final Intent intent, final String state) {
        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            // TODO: 12/3/2017 Ignoring this case for now
            Log.i(TAG, "handleCallIntent: EXTRA_STATE_IDLE");
        } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            if (intent == null || intent.getExtras() == null) {
                Log.e(TAG, "speak()->Intent NULL");
                return;
            }
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.i(TAG, "handleCallIntent: EXTRA_STATE_RINGING");
            Log.i(TAG, "Mobile No: " + number);
            if (!TextUtils.isEmpty(number)) {

                String contactName = Utils.getContactName(number, mContext);
                Log.i(TAG, "Mobile contact: " + contactName);

                String text = contactName + " is calling you. Would you like to receive?";

                boolean validNumber = true;
                if (!Utils.isNumberValid(number)) {
                    text = "Rejecting a call which is coming from an invalid number";
                    validNumber = false;
                }

                // TODO: 12/3/2017 Pause music if running

                int speak = sTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH,
                        Bundle.EMPTY, UTTERANCE_ID);

                if (speak == TextToSpeech.ERROR) {
                    Log.e(TAG, "Error while reading text!");
                    Toast.makeText(mContext, "Error while reading text!", Toast.LENGTH_SHORT)
                            .show();
                    mDisconnected = true;
                } else if (speak == TextToSpeech.SUCCESS) {
                    if (!validNumber) {
                        Log.e(TAG, "Error#Invalid number!");
                        return;
                    }

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            initSpeechRecognizer();
                        }
                    }, 3000);
                } else {
                    Log.e(TAG, "Error# " + speak);
                }

            } else {
                Log.e(TAG, "Error#Number is null or empty");
            }
        } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            Log.i(TAG, "handleCallIntent: EXTRA_STATE_OFFHOOK");
            // TODO: 12/3/2017 Play music if paused previously
            mDisconnected = true;
        }
    }

    private void initSpeechRecognizer() {
        Log.e(TAG, "initSpeechRecognizer: ");

        Handler handler = getHandler();
        mSpeechLooper = handler.getLooper();

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext.getApplicationContext());
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

                            // TODO: 8/26/2018 End this Thread and wake up main thread

/*
                            mSpeechLooper.getThread().suspend();
                            mMainLooper.getThread().notify();
*/

                            ArrayList<String> list = (ArrayList<String>) msg.obj;
                            Log.d(TAG, "Words: " + list.toString());
                            if (list.contains("Ignore") || list.contains("ignore") || list.contains("No") || list.contains("no")) {
                                if (!mHangedUp && !hangUpIncomingCall()) {
                                    Log.e(TAG, "Unable to end Call!");
                                } else if (mHangedUp) {
                                    Log.e(TAG, "The Call was already hanged up!");
                                }
                            } else if (list.contains("Answer") || list.contains("answer") || list.contains("Yes") || list.contains("yes")) {
                                if (!receiveIncomingCall()) {
                                    Log.i(TAG, "Unable to receive Call!");
                                } else if (mReceived) {
                                    Log.e(TAG, "The Call was already received!");
                                }
                            }
                        }

                        break;
                    case 0:
                        mSpeechRecognizer.stopListening();

                        /* Adjusting Stream volume to last value */
//                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, sStreamVolume);

//                        if (msg.obj instanceof Integer)
//                            if (msg.obj == (Integer) 7) { // Error code: 7
//                                initSpeechRecognizer();
//                            }
                        break;
                }
            }
        };
    }

    @Override
    public void onInit(final int status) {
        if (status != android.speech.tts.TextToSpeech.SUCCESS) {
            Toast.makeText(mContext, "Text to speech not initiated!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "not initiated!");
        } else {
//            while (true) {
            String state = mIntent.getStringExtra("state");
            if (mDisconnected || mHangedUp || mReceived) {
                sTextToSpeech.shutdown();
                sTextToSpeech.stop();
//                    break;
            }

//                mMainHandler = new Handler();
            mMainLooper = Looper.getMainLooper();
            speak(mIntent, state);

/*                try {
                    Thread.sleep(4000);
//                    Thread thread = Thread.currentThread();
//                    long mainThreadID = thread.getId();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
//            }
        }
    }

    public boolean hangUpIncomingCall() {
        Log.i(TAG, "hangUpIncomingCall: ");
        try {
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager =
                    (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

            if (telephonyManager == null) {
                Log.e(TAG, "Error#PhoneStateReceiver: " + null);
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
        mHangedUp = true;
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
            mReceived = true;

        } catch (Exception ex) { // Many things can go wrong with reflection calls
            Log.d(TAG, "PhoneStateReceiver **" + ex.toString());
            return false;
        }
        return true;
    }
}
