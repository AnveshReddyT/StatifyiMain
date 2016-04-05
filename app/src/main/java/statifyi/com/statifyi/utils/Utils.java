package statifyi.com.statifyi.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import statifyi.com.statifyi.api.model.User;
import statifyi.com.statifyi.data.DBHelper;
import statifyi.com.statifyi.model.Contact;

/**
 * Created by KT on 12/3/15.
 */
public class Utils {

    public static int getScreenWidth(Context mContext) {
        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int getScreenHeight(Context mContext) {
        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public static Typeface selectTypeface(Context context, String fontName, int textStyle) {
        if (fontName.contentEquals("Oswald")) {
            switch (textStyle) {
                case 1: // bold
                    return Typeface.createFromAsset(context.getAssets(), "fonts/Oswald-Bold.ttf");

                case 2: // italic
                    return Typeface.createFromAsset(context.getAssets(), "fonts/Oswald-Italic.ttf");

                case 0: // regular
                default:
                    return Typeface.createFromAsset(context.getAssets(), "fonts/Oswald-Regular.ttf");
            }
        } else {
            return null;
        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static int getDrawableResByName(Context context, String name) {
        String uri = name.replace(" ", "_");
        uri = uri.toLowerCase();
        return context.getResources().getIdentifier(uri, "drawable", context.getPackageName());
    }

    public static List<Contact> readPhoneContacts(Context cntx) {
        List<Contact> contacts = new ArrayList<>();
        Cursor cursor = cntx.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        Integer contactsCount = cursor.getCount();
        if (contactsCount > 0) {
            while (cursor.moveToNext()) {
                Contact mContact = new Contact();
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String imageUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCursor = cntx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if (pCursor.moveToNext()) {
                        int phoneType = pCursor.getInt(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        String phoneNo = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        mContact.setMobile(phoneNo);
                        switch (phoneType) {
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                                break;
                            default:
                                break;
                        }
                    }
                    mContact.setName(contactName);
                    mContact.setPhoto(imageUri);
                    contacts.add(mContact);
                    pCursor.close();
                }
            }
            cursor.close();
        }
        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact lhs, Contact rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        return contacts;
    }

    public static String getLastTenDigits(String number) {
        String replace = number.replaceAll("[^0-9]", "");
        int length = replace.length();
        replace = length > 10 ? replace.substring(length - 10, length) : replace;
        return replace.length() == 10 ? replace : null;
    }

    public static List<String> getPhoneNumbersFromContacts(Context cntx) {
        List<String> contacts = new ArrayList<>();
        Cursor cursor = cntx.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        Integer contactsCount = cursor.getCount();
        if (contactsCount > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCursor = cntx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if (pCursor.moveToNext()) {
                        String phoneNo = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (!contacts.contains(phoneNo)) {
                            contacts.add(phoneNo);
                        }
                    }
                    pCursor.close();
                }
            }
            cursor.close();
        }
        return contacts;
    }

    public static ArrayList<statifyi.com.statifyi.model.CallLog> getCallLogs(Activity mContext) {
        ArrayList<statifyi.com.statifyi.model.CallLog> callLogs = new ArrayList<>();
        String strOrder = CallLog.Calls.DATE + " DESC";
        Cursor managedCursor = mContext.managedQuery(CallLog.Calls.CONTENT_URI, null,
                null, null, strOrder);
        int cached_name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int phone = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        while (managedCursor.moveToNext()) {
            String phNum = managedCursor.getString(phone);
            String callTypeCode = managedCursor.getString(type);
            String strcallDate = managedCursor.getString(date);
            String name = managedCursor.getString(cached_name);
            long callDate = Long.valueOf(strcallDate);
            String callDuration = managedCursor.getString(duration);
            statifyi.com.statifyi.model.CallLog.CallType callType = null;
            int callcode = Integer.parseInt(callTypeCode);
            switch (callcode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callType = statifyi.com.statifyi.model.CallLog.CallType.OUTGOING;
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    callType = statifyi.com.statifyi.model.CallLog.CallType.INCOMING;
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callType = statifyi.com.statifyi.model.CallLog.CallType.MISSED;
                    break;
            }
            statifyi.com.statifyi.model.CallLog callLog = new statifyi.com.statifyi.model.CallLog();
            callLog.setName(name);
            callLog.setPhone(phNum);
            callLog.setDate(callDate);
            callLog.setDuration(callDuration);
            callLog.setType(callType);
            callLogs.add(callLog);
        }
        return callLogs;
    }

    public static String timeAgo(String utcTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        try {
            Date date = sdf.parse(utcTime);
            Log.d("STAT", date.toString());
            return timeAgo(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String timeAgo(long millis) {
        long ago = System.currentTimeMillis() - millis;
        long ago_mins = ago / 1000 / 60;
        if (ago_mins == 0) {
            return "min ago";
        } else if (ago_mins < 60) {
            return ago_mins + " mins ago";
        } else if (ago_mins > 60 && ago_mins < 60 * 24) {
            long ago_hrs = ago_mins / 60;
            return ago_hrs == 1 ? " an hour ago" : ago_hrs + " hrs ago";
        } else if (ago_mins > 60 * 24) {
            long ago_days = ago_mins / 60 / 24;
            return ago_days == 1 ? " a day ago" : ago_days + " days ago";
        } else {
            return "";
        }
    }

    public static String getContactName(Context context, String telNum) {
        String name = null;
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(telNum));
            Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
            if (cursor == null)
                return null;
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }

            if (!cursor.isClosed())
                cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name == null ? telNum : name;
    }

    public static void saveUserStatusToLocal(String status, String icon, String phoneNumber, DBHelper dbHelper) {
        User user = new User();
        user.setMobile(phoneNumber);
        user.setStatus(status);
        user.setIcon(icon);
        dbHelper.insertOrUpdateUser(user);
    }

    public static void inviteFriends(Context mContext) {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Try StatiFYI for Android!");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "I'm using StatiFYI for Android and I recommend it. Click here: http://www.statifyi.com");

        Intent chooserIntent = Intent.createChooser(shareIntent, "Share with");
        chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(chooserIntent);
    }
}
