package android.shashi_sule.com.notifyme.extra;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;

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

    static String getContactName(final String phoneNumber, Context context) {
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
}
