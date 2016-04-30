package statifyi.com.statifyi.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.TextUtils;
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

    public static Drawable changeColor(Context mContext, int drawable, int color) {
        Drawable upArrow = null;
        try {
            upArrow = mContext.getResources().getDrawable(drawable);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        if (upArrow != null) {
            upArrow.setColorFilter(mContext.getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
        }
        return upArrow;
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
        final String[] PROJECTION = new String[]{
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        };
        List<String> temp = new ArrayList<>();
        List<Contact> contacts = new ArrayList<>();
        Cursor cursor = cntx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);
        if (cursor == null) {
            return contacts;
        }
        Integer contactsCount = cursor.getCount();
        if (contactsCount > 0) {
            while (cursor.moveToNext()) {
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String imageUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                String phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneNo = phoneNo.replaceAll(" ", "");
                if (!temp.contains(phoneNo)) {
                    Contact mContact = new Contact();
                    mContact.setMobile(phoneNo);
                    mContact.setName(contactName);
                    mContact.setPhoto(imageUri);
                    contacts.add(mContact);
                    temp.add(phoneNo);
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
        if (TextUtils.isEmpty(number)) {
            return null;
        }
        String replace = number.replaceAll("[^0-9]", "");
        int length = replace.length();
        replace = length > 10 ? replace.substring(length - 10, length) : replace;
        return replace.length() == 10 ? replace : null;
    }

    public static List<String> getPhoneNumbersFromContacts(Context cntx) {
        final String[] PROJECTION = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        List<String> contacts = new ArrayList<>();
        if (cntx == null) {
            return contacts;
        }
        Cursor cursor = cntx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);
        if (cursor == null) {
            return contacts;
        }
        Integer contactsCount = cursor.getCount();
        if (contactsCount > 0) {
            while (cursor.moveToNext()) {
                String phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (!contacts.contains(phoneNo)) {
                    contacts.add(phoneNo);
                }
            }
            cursor.close();
        }
        return contacts;
    }

    public static List<String> get10DigitPhoneNumbersFromContacts(Context cntx) {
        final String[] PROJECTION = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        List<String> contacts = new ArrayList<>();
        if (cntx == null) {
            return contacts;
        }
        Cursor cursor = cntx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);
        if (cursor == null) {
            return contacts;
        }
        Integer contactsCount = cursor.getCount();
        if (contactsCount > 0) {
            while (cursor.moveToNext()) {
                String phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneNo = getLastTenDigits(phoneNo);
                if (phoneNo != null && !contacts.contains(phoneNo)) {
                    contacts.add(phoneNo);
                }
            }
            cursor.close();
        }
        return contacts;
    }

    public static ArrayList<statifyi.com.statifyi.model.CallLog> getCallLogs(Context mContext) {
        ArrayList<statifyi.com.statifyi.model.CallLog> callLogs = new ArrayList<>();
        String PROJECTION[] = {
                CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION
        };
        if (mContext == null) {
            return callLogs;
        }
        DBHelper dbHelper = DBHelper.getInstance(mContext);
        String strOrder = CallLog.Calls.DATE + " DESC";
        Cursor cursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, PROJECTION,
                null, null, strOrder);
        if (cursor != null) {
            int cached_name = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int phone = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = cursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

            while (cursor.moveToNext()) {
                String phNum = cursor.getString(phone);
                String callTypeCode = cursor.getString(type);
                String strcallDate = cursor.getString(date);
                String name = cursor.getString(cached_name);
                long callDate = Long.valueOf(strcallDate);
                String callDuration = cursor.getString(duration);
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
                callLog.setMessage(dbHelper.getCustomCallLog(callDate));
                callLogs.add(callLog);
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        return callLogs;
    }

    public static String timeAgo(String utcTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        try {
            Date date = sdf.parse(utcTime);
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
            return "less than a min ago";
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
                return telNum;
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }

            if (!cursor.isClosed())
                cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (name != null) {
            return name;
        } else {
            DBHelper dbHelper = DBHelper.getInstance(context);
            String statifyName = dbHelper.getName(getLastTenDigits(telNum));
            return statifyName == null ? telNum : statifyName;
        }
    }

    public static boolean isMyServiceRunning(Context mContext, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void saveUserStatusToLocal(String status, String name, String icon, String phoneNumber, long time, DBHelper dbHelper) {
        User user = new User();
        user.setMobile(phoneNumber);
        user.setName(name);
        user.setStatus(status);
        user.setIcon(icon);
        user.setUpdated(time);
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
