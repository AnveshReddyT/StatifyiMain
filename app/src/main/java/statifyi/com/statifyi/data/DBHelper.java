package statifyi.com.statifyi.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import statifyi.com.statifyi.api.model.CustomCall;
import statifyi.com.statifyi.api.model.User;
import statifyi.com.statifyi.model.Status;

/**
 * Created by KT on 22/01/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "statifyi.db";

    public static final String USERS_TABLE_NAME = "users";
    public static final String CUSTOM_CALLS_TABLE_NAME = "custom_calls";
    public static final String CALL_LOGS_TABLE_NAME = "call_logs";
    public static final String CUSTOM_STATUS_TABLE_NAME = "custom_status";

    public static final String USERS_COLUMN_MOBILE = "mobile";
    public static final String USERS_COLUMN_NAME = "name";
    public static final String USERS_COLUMN_STATUS = "status";
    public static final String USERS_COLUMN_ACTIVE = "active";
    public static final String USERS_COLUMN_ICON = "icon";
    public static final String USERS_COLUMN_UPDATED = "updated";

    public static final String CUSTOM_CALLS_COLUMN_MOBILE = "mobile";
    public static final String CUSTOM_CALLS_COLUMN_MESSAGE = "message";
    public static final String CUSTOM_CALLS_COLUMN_TIME = "time";

    public static final String CALL_LOGS_COLUMN_DATE = "date";
    public static final String CALL_LOGS_COLUMN_MESSAGE = "message";

    public static final String CUSTOM_STATUS_COLUMN_NAME = "status";
    public static final String CUSTOM_STATUS_COLUMN_ICON = "icon";
    public static final String CUSTOM_STATUS_COLUMN_DATE = "date";

    private static final String CREATE_TABLE_USERS = "create table " + USERS_TABLE_NAME + " (" +
            USERS_COLUMN_MOBILE + " text," +
            USERS_COLUMN_NAME + " text," +
            USERS_COLUMN_STATUS + " text," +
            USERS_COLUMN_ACTIVE + " integer," +
            USERS_COLUMN_ICON + " text," +
            USERS_COLUMN_UPDATED + " text)";

    private static final String CREATE_TABLE_CUSTOM_CALLS = "create table " + CUSTOM_CALLS_TABLE_NAME + " (" +
            CUSTOM_CALLS_COLUMN_MOBILE + " text," +
            CUSTOM_CALLS_COLUMN_MESSAGE + " text," +
            CUSTOM_CALLS_COLUMN_TIME + " integer)";

    private static final String CREATE_TABLE_CALL_LOGS = "create table " + CALL_LOGS_TABLE_NAME + " (" +
            CALL_LOGS_COLUMN_DATE + " integer," +
            CALL_LOGS_COLUMN_MESSAGE + " text)";

    private static final String CREATE_TABLE_CUSTOM_STATUS = "create table " + CUSTOM_STATUS_TABLE_NAME + " (" +
            CUSTOM_STATUS_COLUMN_NAME + " text," +
            CUSTOM_STATUS_COLUMN_ICON + " text," +
            CUSTOM_STATUS_COLUMN_DATE + " integer)";

    private static DBHelper mInstance = null;

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public static DBHelper getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new DBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_CUSTOM_CALLS);
        db.execSQL(CREATE_TABLE_CALL_LOGS);
        db.execSQL(CREATE_TABLE_CUSTOM_STATUS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CUSTOM_CALLS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CALL_LOGS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CUSTOM_STATUS_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertOrUpdateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        User existingUser = getUser(user.getMobile());
        ContentValues contentValues = getUserContentValues(user);
        if (existingUser == null) {
            db.insert(USERS_TABLE_NAME, null, contentValues);
        } else {
            db.update(USERS_TABLE_NAME, contentValues, USERS_COLUMN_MOBILE + " = ?", new String[]{user.getMobile()});
        }
        return true;
    }

    @NonNull
    private ContentValues getUserContentValues(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERS_COLUMN_MOBILE, user.getMobile());
        contentValues.put(USERS_COLUMN_NAME, user.getName());
        contentValues.put(USERS_COLUMN_STATUS, user.getStatus());
        contentValues.put(USERS_COLUMN_ACTIVE, user.isActive() ? 1 : 0);
        contentValues.put(USERS_COLUMN_ICON, user.getIcon());
        contentValues.put(USERS_COLUMN_UPDATED, user.getUpdated());
        return contentValues;
    }

    public User getUser(String mobile) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + USERS_TABLE_NAME + " where " + USERS_COLUMN_MOBILE + "=" + mobile, null);
        User mUser = null;
        if (cursor != null && cursor.moveToFirst()) {
            mUser = getUserFromCursor(cursor);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return mUser;
    }

    public String getName(String mobile) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select name from " + USERS_TABLE_NAME + " where " + USERS_COLUMN_MOBILE + "=" + mobile, null);
        String name = null;
        if (cursor != null && cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(USERS_COLUMN_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return name;
    }

    @NonNull
    private User getUserFromCursor(Cursor cursor) {
        User user = new User();
        user.setMobile(cursor.getString(cursor.getColumnIndex(USERS_COLUMN_MOBILE)));
        user.setName(cursor.getString(cursor.getColumnIndex(USERS_COLUMN_NAME)));
        user.setStatus(cursor.getString(cursor.getColumnIndex(USERS_COLUMN_STATUS)));
        user.setActive(cursor.getInt(cursor.getColumnIndex(USERS_COLUMN_ACTIVE)) == 1);
        user.setIcon(cursor.getString(cursor.getColumnIndex(USERS_COLUMN_ICON)));
        user.setUpdated(Long.parseLong(cursor.getString(cursor.getColumnIndex(USERS_COLUMN_UPDATED))));
        return user;
    }

    public int numberOfUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, USERS_TABLE_NAME);
    }

    public Integer deleteUser(String mobile) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(USERS_TABLE_NAME, USERS_COLUMN_MOBILE + " = ?", new String[]{mobile});
    }

    public Integer deleteAllUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(USERS_TABLE_NAME, null, null);
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> array_list = new ArrayList<User>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + USERS_TABLE_NAME, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            array_list.add(getUserFromCursor(cursor));
            cursor.moveToNext();
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return array_list;
    }

    public HashMap<String, User> getAllUsersMap() {
        HashMap<String, User> array_list = new HashMap<String, User>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + USERS_TABLE_NAME, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            array_list.put(cursor.getString(cursor.getColumnIndex(USERS_COLUMN_MOBILE)), getUserFromCursor(cursor));
            cursor.moveToNext();
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return array_list;
    }

    public boolean insertOrUpdateCustomCall(CustomCall customCall) {
        SQLiteDatabase db = this.getWritableDatabase();
        CustomCall existingCall = getCustomCall(customCall.getMobile());
        ContentValues contentValues = getCustomCallContentValues(customCall);
        if (existingCall == null) {
            db.insert(CUSTOM_CALLS_TABLE_NAME, null, contentValues);
        } else {
            db.update(CUSTOM_CALLS_TABLE_NAME, contentValues, CUSTOM_CALLS_COLUMN_MOBILE + " = ?", new String[]{customCall.getMobile()});
        }
        return true;
    }

    @NonNull
    private ContentValues getCustomCallContentValues(CustomCall customCall) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CUSTOM_CALLS_COLUMN_MOBILE, customCall.getMobile());
        contentValues.put(CUSTOM_CALLS_COLUMN_MESSAGE, customCall.getMessage());
        contentValues.put(CUSTOM_CALLS_COLUMN_TIME, customCall.getTime());
        return contentValues;
    }

    public CustomCall getCustomCall(String mobile) {
        Log.d("STAT", "Deleted calls:  " + deleteExpiredCustomCalls());
        CustomCall customCallFromCursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + CUSTOM_CALLS_TABLE_NAME + " where " + CUSTOM_CALLS_COLUMN_MOBILE + "=" + mobile, null);
        if (cursor != null && cursor.moveToFirst()) {
            customCallFromCursor = getCustomCallFromCursor(cursor);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return customCallFromCursor;
    }

    @NonNull
    private CustomCall getCustomCallFromCursor(Cursor cursor) {
        CustomCall customCall = new CustomCall();
        customCall.setMobile(cursor.getString(cursor.getColumnIndex(CUSTOM_CALLS_COLUMN_MOBILE)));
        customCall.setMessage(cursor.getString(cursor.getColumnIndex(CUSTOM_CALLS_COLUMN_MESSAGE)));
        customCall.setTime(cursor.getLong(cursor.getColumnIndex(CUSTOM_CALLS_COLUMN_TIME)));
        return customCall;
    }

    public Integer deletedCustomCall(String mobile) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CUSTOM_CALLS_TABLE_NAME, CUSTOM_CALLS_COLUMN_MOBILE + " = ?", new String[]{mobile});
    }

    public Integer deleteExpiredCustomCalls() {
        long expiryTime = System.currentTimeMillis() - 2 * 60 * 1000;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CUSTOM_CALLS_TABLE_NAME, CUSTOM_CALLS_COLUMN_TIME + " < ?", new String[]{String.valueOf(expiryTime)});
    }

    public boolean insertOrUpdateCallLog(long date, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = getCustomCallLogContentValues(date, message);
        db.insert(CALL_LOGS_TABLE_NAME, null, contentValues);
        return true;
    }

    @NonNull
    private ContentValues getCustomCallLogContentValues(long date, String message) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CALL_LOGS_COLUMN_DATE, date);
        contentValues.put(CALL_LOGS_COLUMN_MESSAGE, message);
        return contentValues;
    }

    public String getCustomCallLog(long date) {
        String customCallLogFromCursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + CALL_LOGS_TABLE_NAME + " where " + CALL_LOGS_COLUMN_DATE + "=" + date, null);
        if (cursor != null && cursor.moveToFirst()) {
            customCallLogFromCursor = cursor.getString(cursor.getColumnIndex(CALL_LOGS_COLUMN_MESSAGE));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return customCallLogFromCursor;
    }

    public boolean insertOrUpdateCustomStatus(Status status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = getCustomStatusContentValues(status);
        if (numberOfCustomStatuses() == 6) {
            Cursor cursor = db.rawQuery("select * from " + CUSTOM_STATUS_TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                String statusName = cursor.getString(cursor.getColumnIndex(CUSTOM_STATUS_COLUMN_NAME));
                deletedCustomStatus(statusName);
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        db.insert(CUSTOM_STATUS_TABLE_NAME, null, contentValues);
        return true;
    }

    @NonNull
    private ContentValues getCustomStatusContentValues(Status status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CUSTOM_STATUS_COLUMN_DATE, status.getDate());
        contentValues.put(CUSTOM_STATUS_COLUMN_NAME, status.getStatus());
        contentValues.put(CUSTOM_STATUS_COLUMN_ICON, status.getIcon());
        return contentValues;
    }

    public int numberOfCustomStatuses() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, CUSTOM_STATUS_TABLE_NAME);
    }

    public ArrayList<Status> getCustomStatusList() {
        ArrayList<Status> statuses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + CUSTOM_STATUS_TABLE_NAME, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            statuses.add(getCustomStatusFromCursor(cursor));
            cursor.moveToNext();
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        Collections.sort(statuses, new Comparator<Status>() {
            @Override
            public int compare(Status lhs, Status rhs) {
                if (lhs.getDate() < rhs.getDate())
                    return 1;
                else if (lhs.getDate() == rhs.getDate())
                    return 0;
                else return -1;
            }
        });
        return statuses;
    }

    @NonNull
    private Status getCustomStatusFromCursor(Cursor cursor) {
        Status status = new Status();
        status.setStatus(cursor.getString(cursor.getColumnIndex(CUSTOM_STATUS_COLUMN_NAME)));
        status.setIcon(cursor.getString(cursor.getColumnIndex(CUSTOM_STATUS_COLUMN_ICON)));
        status.setDate(cursor.getLong(cursor.getColumnIndex(CUSTOM_STATUS_COLUMN_DATE)));
        return status;
    }

    public Integer deletedCustomStatus(String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CUSTOM_STATUS_TABLE_NAME, CUSTOM_STATUS_COLUMN_NAME + " = ?", new String[]{status});
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}