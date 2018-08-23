package android.shashi_sule.com.notifyme.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author by Shashi on 10/21/2017.
 */

public class Utils {

    public static final String INTENT_PHONE_STATE = "android.intent.action.PHONE_STATE";
    public static final int REQUEST_CONTACT_PERMISSION = 1001;
    public static final int REQUEST_PHONE_STATE_PERMISSION = 1002;
    public static final int REQUEST_RECORD_AUDIO_PERMISSION = 1003;

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

    public void fun() {

        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("Sule", "Shashi");
        map.put("Sule", "Shubham");
        map.put("Sule", "Shital");
        map.put("Mane", "Shital");

        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        do {
            System.out.println(iterator.next());
        } while (iterator.hasNext());

        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getKey());
        }

        for (String s : map.keySet()) {
            System.out.println(s);
        }
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
//        char[] chars = number.toCharArray();
//        for (char c : chars) {
//
//        }

        // TODO: 8/24/2018 Check for SPAM callers

        int indexOf91 = number.indexOf("+91");
        if (indexOf91 == -1) return false;

        String actualNumber = number.substring(indexOf91 + 1, number.length() - 1);
        if (actualNumber.charAt(0) != '7' || actualNumber.charAt(0) != '8' || actualNumber.charAt(0) != '9') {
            return false;
        }

        /*
        Check for Internet numbers
        */
        if (actualNumber.charAt(0) == '1' && actualNumber.charAt(1) == '4' && actualNumber.charAt(2) == '0') {
            return false;
        }

        return true;
    }
}
