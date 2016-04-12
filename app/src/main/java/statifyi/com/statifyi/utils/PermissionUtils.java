package statifyi.com.statifyi.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by KT on 11/04/16.
 */
public class PermissionUtils {

    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 101;

    private static final int READ_CALL_LOG_PERMISSIONS_REQUEST = 102;

    private static final int PROCESS_OUTGOING_CALLS_PERMISSIONS_REQUEST = 103;

    private static final int SYSTEM_ALERT_WINDOW_PERMISSIONS_REQUEST = 104;

    public static void getPermissionToReadUserContacts(Activity mContext) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(mContext, android.Manifest.permission.READ_CONTACTS)) {
                    // Show our own UI to explain to the user why we need to read the contacts
                    // before actually requesting the permission and showing the default UI
                }

                ActivityCompat.requestPermissions(mContext, new String[]{android.Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSIONS_REQUEST);
            }
        }
    }

    public static void getPermissionToReadCallLog(Activity mContext) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(mContext, Manifest.permission.READ_CALL_LOG)) {
                    // Show our own UI to explain to the user why we need to read the contacts
                    // before actually requesting the permission and showing the default UI
                }

                ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.READ_CALL_LOG}, READ_CALL_LOG_PERMISSIONS_REQUEST);
            }
        }
    }

    public static void getPermissionToProcessOutgoingCalls(Activity mContext) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.PROCESS_OUTGOING_CALLS)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(mContext, Manifest.permission.PROCESS_OUTGOING_CALLS)) {
                    // Show our own UI to explain to the user why we need to read the contacts
                    // before actually requesting the permission and showing the default UI
                }

                ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS}, PROCESS_OUTGOING_CALLS_PERMISSIONS_REQUEST);
            }
        }
    }

    public static void getPermissionToSystemAlertWindow(Activity mContext) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.SYSTEM_ALERT_WINDOW)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(mContext, Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                    // Show our own UI to explain to the user why we need to read the contacts
                    // before actually requesting the permission and showing the default UI
                }

                ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, SYSTEM_ALERT_WINDOW_PERMISSIONS_REQUEST);
            }
        }
    }

    public static boolean onPermissionResult(int requestCode, String permissions[], int[] grantResults, Context mContext) {
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == READ_CALL_LOG_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Read Call log permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Read Call log permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PROCESS_OUTGOING_CALLS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Process out going calls permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Process out going calls permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == SYSTEM_ALERT_WINDOW_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "System alert window permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "System alert window permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            return false;
        }
        return true;
    }

}
