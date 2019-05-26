package android.shashi_sule.com.notifyme.utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * @author by Shashi on 10/21/2017.
 */

public class Utils {

    public static final String INTENT_PHONE_STATE = "android.intent.action.PHONE_STATE";
    public static final String INTENT_PHONE_STATE_CHANGED = "android.intent.action.ACTION_PHONE_STATE_CHANGED";
    public static final int REQUEST_CONTACT_PERMISSION = 1001;
    public static final int REQUEST_PHONE_STATE_PERMISSION = 1002;
    public static final int REQUEST_RECORD_AUDIO_PERMISSION = 1003;
    public static final int REQUEST_BLUETOOTH_PERMISSION = 1004;
    private static String TAG = Utils.class.getSimpleName();

    /**
     * @param activity   - Context
     * @param permission - Permission to check
     * @param reqCode    - Request code
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestPermission(Activity activity, final String permission,
                                         final int reqCode) {
        if (activity.checkSelfPermission(permission) !=
                PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{permission}, reqCode);
        }
    }

    /**
     * @param phoneNumber - Phone number
     * @param context - Context
     * @return Contact name if the number is present in Contact list, otherwise number
     */
    public static String getContactName(final String phoneNumber, Context context) {

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
        String contactName = "";

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0);
            }
            cursor.close();
        }

        if (contactName.isEmpty()) {
            contactName = "Unknown person ";
        }

        return contactName;
    }

    /**
     * @param context - Context
     * @return true if headsets plugged otherwise false
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isHeadsetsConnected(Context context) {

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        boolean wiredHeadsetOn = false;

        if (audioManager != null) {

            /*
            Below code requires API M */

            /*AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);
            for (AudioDeviceInfo device : devices) {
                if (device.getType() == 3 || device.getType() == 4) {
                    Log.e("ConnectedDevices", device.getProductName().toString());
                    return true;
                }
            }
            Log.e("ConnectedDevices", Arrays.toString(devices));*/

            wiredHeadsetOn = audioManager.isWiredHeadsetOn();

            /* Check for Bluetooth earphones*/
            if (!wiredHeadsetOn && checkBluetoothPermission(context)) {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                adapter.getProfileProxy(context, new BluetoothProfile.ServiceListener() {
                    @Override
                    public void onServiceConnected(final int profile, final BluetoothProfile proxy) {

                    }

                    @Override
                    public void onServiceDisconnected(final int profile) {

                    }
                }, BluetoothProfile.HEADSET);
            }
        }
        return wiredHeadsetOn;
    }

    private static boolean checkBluetoothPermission(Context context) {
        String permission = Manifest.permission.BLUETOOTH;
        int res = context.checkCallingOrSelfPermission(permission);

        return (res == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * @param number - Phone number
     * @return true if the number is valid phone number, otherwise false
     */
    public static boolean isNumberValid(final String number) {
        // TODO: 8/24/2018 Add check for SPAM callers

        String actualNumber = number;
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        if (number.contains("+91") || number.length() == 13) {
            actualNumber = number.substring(3, number.length() - 1);
        }

        char digitAtZero = actualNumber.charAt(0);
        if (digitAtZero != '7' && digitAtZero != '8' && digitAtZero != '9') {
            Log.e(TAG, "Error#Number not starting from 7/8/9");
            return false;
        }

        /* Check for Internet numbers */
        if (digitAtZero == '1' && actualNumber.charAt(1) == '4' && actualNumber.charAt(2) == '0') {
            Log.e(TAG, "Error#Number starting from 140");
            return false;
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void fun() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

        List<String> strings = Collections.synchronizedList(new ArrayList<String>());

        for (Iterator i = list.iterator(); i.hasNext(); ) {
            String next = (String) i.next();
        }

        list.iterator().forEachRemaining(new Consumer<String>() {
            @Override
            public void accept(final String s) {

            }
        });

        list.forEach(new Consumer<String>() {
            @Override
            public void accept(final String s) {
            }
        });
    }

    private int stringLength(String s) {
        int len = 0;

        for (char c : s.toCharArray()) {
            len++;
        }
        return len;
    }

    private int noOfLettersInPara(String para) {
        int letters = 0;

        for (char c : para.toCharArray()) {

//            if (isNotLetter()) continue;
//
//            if (isSpecialCharacter()) continue;

            letters++;
        }
        return letters;
    }

    private String reverse(String str) {
        int len = str.length() - 1;

        return str.substring(len) + str.charAt(0);
    }

}
