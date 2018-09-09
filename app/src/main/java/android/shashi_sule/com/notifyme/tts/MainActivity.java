package android.shashi_sule.com.notifyme.tts;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.shashi_sule.com.notifyme.R;
import android.shashi_sule.com.notifyme.service.HeadPhoneListener;
import android.shashi_sule.com.notifyme.setting.SettingActivity;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import static android.shashi_sule.com.notifyme.utils.Utils.INTENT_PHONE_STATE;
import static android.shashi_sule.com.notifyme.utils.Utils.REQUEST_CONTACT_PERMISSION;
import static android.shashi_sule.com.notifyme.utils.Utils.REQUEST_PHONE_STATE_PERMISSION;

public class MainActivity extends AppCompatActivity {

    public static final int DELAY_MILLIS = 5000;
    private static final String TAG = "";//MainActivity
    private HeadPhoneListener mHeadPhoneListener;
    private TextView mSpeechStateView;
    private Handler mHandler;
    private ArrayList<String> mWordList;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHeadPhoneListener = new HeadPhoneListener();
        mSpeechStateView = (TextView) findViewById(R.id.stateTextView);

        updateUI();

        /*
         *  Init Speech Recognizer
         */
        final SpeechRecognizer recognizer = SpeechRecognizer
                .createSpeechRecognizer(MainActivity.this);
        final Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
//        recognizer.setRecognitionListener(this);
        recognizer.setRecognitionListener(new SpeechListener(mHandler));

//        onClick(recognizer, recognizerIntent);
        permissionsCheck();

//        registerForIntent();


        // Test code which can be deleted afterwords

        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... voids) {
                return null;
            }
        };
//        asyncTask.executeOnExecutor()

    }

    private void registerForIntent() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        filter.addAction(INTENT_PHONE_STATE);

        registerReceiver(mHeadPhoneListener, filter);

        // TODO: 6/17/2018 NEW WAY
//        ComponentName component = new ComponentName(this, HeadPhoneListener.class);
//        int status = getPackageManager().getComponentEnabledSetting(component);
//        if(status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
//            getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP);
//        } else if(status == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
//            getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP);
//        }
    }

    private void onClick(final SpeechRecognizer recognizer, final Intent recognizerIntent) {
        findViewById(R.id.notify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                recognizer.startListening(recognizerIntent);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recognizer.stopListening();
                    }
                }, DELAY_MILLIS);
            }
        });
    }

    private void updateUI() {
        mHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(final Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        if (msg.obj instanceof ArrayList) {
                            mWordList = (ArrayList<String>) msg.obj;
                            mSpeechStateView.setText(msg.obj.toString());
                            Log.i(TAG, "WordList: " + mWordList.toString());
                        } else {
                            mSpeechStateView.setText("State: " + msg.obj.toString());
                        }

                        break;
                    case 0:
                        if (msg.obj instanceof Integer) {
                            if (msg.obj == (Integer) 7) { // Error code: 7
                                final SpeechRecognizer recognizer = SpeechRecognizer
                                        .createSpeechRecognizer(MainActivity.this);
                                final Intent recognizerIntent =
                                        new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                                recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                                        getPackageName());
                                recognizer.setRecognitionListener(new SpeechListener(mHandler));
                                recognizer.startListening(recognizerIntent);
                            }
                            Log.i(TAG, "Error: " + msg.obj);
                        } else {
                            mSpeechStateView.setTextColor(getColor(android.R.color.holo_red_dark));
                            mSpeechStateView.setText("Error: " + msg.obj.toString());
                        }
                        break;
                }
            }
        };
    }

    private void permissionsCheck() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Utils.requestPermission(this, Manifest.permission.READ_CONTACTS,
//                    REQUEST_CONTACT_PERMISSION);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//            Utils.requestPermission(this, Manifest.permission.READ_PHONE_STATE,
//                    REQUEST_PHONE_STATE_PERMISSION);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Utils.requestPermission(this, Manifest.permission.RECORD_AUDIO,
//                    REQUEST_RECORD_AUDIO_PERMISSION);
//        }

        requestPermissions(new String[]{
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE},
                REQUEST_CONTACT_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PHONE_STATE_PERMISSION:

                break;
            case REQUEST_CONTACT_PERMISSION:

                break;
        }

    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        Log.e(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        Log.e(TAG, "onRestoreInstanceState");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

//        ArrayList<String> list = new ArrayList<>();
//        list.parallelStream()
//        list.forEach(new Consumer<String>() {
//            @Override
//            public void accept(final String s) {
//
//            }
//        });
        Log.e(TAG, "onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
        Log.e(TAG, "unregister: Headphone listener!!!");
//        unregisterReceiver(mHeadPhoneListener);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
        }
        return true;
    }

}
