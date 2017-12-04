package android.shashi_sule.com.notifyme.extra;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

/**
 * @author by SULES on 12/4/2017.
 */

public class SpeechListener implements RecognitionListener {

    private static final String TAG = "SpeechListener";
    private static final int SUCCESS = 1;
    private static final int FAILURE = 0;
    private Handler mHandler;

    public SpeechListener(final Handler handler) {
        mHandler = handler;
    }

    @Override
    public void onReadyForSpeech(final Bundle bundle) {
        Log.i(TAG, "onReadyForSpeech: ");
        Message message = mHandler.obtainMessage(SUCCESS, "ReadyForSpeech");
        mHandler.sendMessage(message);
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(TAG, "onBeginningOfSpeech: ");
        Message message = mHandler.obtainMessage(SUCCESS, "BeginningOfSpeech");
        mHandler.sendMessage(message);
    }

    @Override
    public void onRmsChanged(final float v) {

    }

    @Override
    public void onBufferReceived(final byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        Log.i(TAG, "onEndOfSpeech: ");
        Message message = mHandler.obtainMessage(SUCCESS, 1, 0);
        mHandler.sendMessage(message);
    }

    @Override
    public void onError(final int i) {
        Log.e(TAG, "onError: " + i);
        Message message = mHandler.obtainMessage(FAILURE, i);
        mHandler.sendMessage(message);
    }

    @Override
    public void onResults(final Bundle bundle) {
        Log.i(TAG, "onResults: ");
        if (bundle != null) {
            ArrayList<String> list = bundle
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            assert list != null;
            Message message = mHandler.obtainMessage(SUCCESS, list);
            mHandler.sendMessage(message);
        }
    }

    @Override
    public void onPartialResults(final Bundle bundle) {

    }

    @Override
    public void onEvent(final int i, final Bundle bundle) {

    }

}
