package android.shashi_sule.com.notifyme.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.util.Log;

/**
 * @author by Shashi on 10/21/2017.
 */

public class Utils {

    public static final String INTENT_PHONE_STATE = "android.intent.action.PHONE_STATE";
    public static final int REQUEST_CONTACT_PERMISSION = 1001;
    public static final int REQUEST_PHONE_STATE_PERMISSION = 1002;
    public static final int REQUEST_RECORD_AUDIO_PERMISSION = 1003;
    private static String TAG = Utils.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestPermission(Activity activity, final String permission,
                                         final int reqCode) {
//        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) !=
//                PackageManager.PERMISSION_GRANTED) {
//
//            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
//                    REQUEST_CONTACT_PERMISSION);
//        }
        if (activity.checkSelfPermission(permission) !=
                PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{permission}, reqCode);
        }
    }

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isHeadsetsConnected(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        boolean wiredHeadsetOn = false;
        if (audioManager != null) {
//            AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);
//            for (AudioDeviceInfo device : devices) {
//                if (device.getType() == 3 || device.getType() == 4) {
//                    Log.e("ConnectedDevices", device.getProductName().toString());
//                    return true;
//                }
//            }
//            Log.e("ConnectedDevices", Arrays.toString(devices));
            wiredHeadsetOn = audioManager.isWiredHeadsetOn();
        }
        return wiredHeadsetOn;
    }

    public static boolean isNumberValid(final String number) {
        // TODO: 8/24/2018 Check for SPAM callers

        String actualNumber = number;
        if (number.contains("+91") || number.length() == 13) {
            actualNumber = number.substring(3, number.length() - 1);
        }

        char digitAtZero = actualNumber.charAt(0);
        if (digitAtZero != '7' && digitAtZero != '8' && digitAtZero != '9') {
            Log.e(TAG, "Error#Number not starting from 7/8/9");
            return false;
        }

        /*
        Check for Internet numbers
        */
        if (digitAtZero == '1' && actualNumber.charAt(1) == '4' && actualNumber.charAt(2) == '0') {
            Log.e(TAG, "Error#Number starting from 140");
            return false;
        }

        return true;
    }
}
