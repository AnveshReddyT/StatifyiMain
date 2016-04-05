package statifyi.com.statifyi.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

import statifyi.com.statifyi.api.model.User;

/**
 * Created by KT on 22/01/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "statifyi.db";

    public static final String USERS_TABLE_NAME = "users";

    public static final String USERS_COLUMN_MOBILE = "mobile";
    public static final String USERS_COLUMN_STATUS = "status";
    public static final String USERS_COLUMN_ACTIVE = "active";
    public static final String USERS_COLUMN_ICON = "icon";
    public static final String USERS_COLUMN_UPDATED = "updated";
    private static final String CREATE_TABLE_USERS = "create table " + USERS_TABLE_NAME + " (" +
            USERS_COLUMN_MOBILE + " text," +
            USERS_COLUMN_STATUS + " text," +
            USERS_COLUMN_ACTIVE + " integer," +
            USERS_COLUMN_ICON + " text," +
            USERS_COLUMN_UPDATED + " text)";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertOrUpdateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        User existingUser = getUser(user.getMobile());
        ContentValues contentValues = getContentValues(user);
        if (existingUser == null) {
            db.insert(USERS_TABLE_NAME, null, contentValues);
        } else {
            db.update(USERS_TABLE_NAME, contentValues, USERS_COLUMN_MOBILE + " = ?", new String[]{user.getMobile()});
        }
        return true;
    }

    @NonNull
    private ContentValues getContentValues(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERS_COLUMN_MOBILE, user.getMobile());
        contentValues.put(USERS_COLUMN_STATUS, user.getStatus());
        contentValues.put(USERS_COLUMN_ACTIVE, user.isActive() ? 1 : 0);
        contentValues.put(USERS_COLUMN_ICON, user.getIcon());
        contentValues.put(USERS_COLUMN_UPDATED, user.getUpdated());
        return contentValues;
    }

    public User getUser(String mobile) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + USERS_TABLE_NAME + " where " + USERS_COLUMN_MOBILE + "=" + mobile, null);
        if (cursor != null && cursor.moveToFirst()) {
            return getUserFromCursor(cursor);
        }
        return null;
    }

    @NonNull
    private User getUserFromCursor(Cursor cursor) {
        User user = new User();
        user.setMobile(cursor.getString(cursor.getColumnIndex(USERS_COLUMN_MOBILE)));
        user.setStatus(cursor.getString(cursor.getColumnIndex(USERS_COLUMN_STATUS)));
        user.setActive(cursor.getInt(cursor.getColumnIndex(USERS_COLUMN_ACTIVE)) == 1);
        user.setIcon(cursor.getString(cursor.getColumnIndex(USERS_COLUMN_ICON)));
        user.setUpdated(Long.parseLong(cursor.getString(cursor.getColumnIndex(USERS_COLUMN_UPDATED))));
        return user;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, USERS_TABLE_NAME);
    }

    public Integer deleteUser(String mobile) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(USERS_TABLE_NAME, USERS_COLUMN_MOBILE + " = ?", new String[]{mobile});
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
        return array_list;
    }
}