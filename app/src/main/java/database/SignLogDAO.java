package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import datamodels.SignLog;

public class SignLogDAO {

    private SQLiteDatabase database;
    private DatabaseSQLiteHelper dbHelper;

    public SignLogDAO(Context context) {
        dbHelper = new DatabaseSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * method, used to add sign log to database
     */
    public void add(SignLog log) {
        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteHelper.LOGS_NAME, log.getName());
        values.put(DatabaseSQLiteHelper.LOGS_PASSWORD, log.getPassword());
        values.put(DatabaseSQLiteHelper.LOGS_USER_ID, log.getUserId());
        values.put(DatabaseSQLiteHelper.LOGS_DAY, log.getDay());
        values.put(DatabaseSQLiteHelper.LOGS_TIME, log.getTime());
        values.put(DatabaseSQLiteHelper.LOGS_TYPE, log.getType());

        database.insert(DatabaseSQLiteHelper.TABLE_SIGN_LOGS, null, values);
    }

    /**
     * method, used to get all sign logs of a user
     */
    public List<SignLog> getAll(String userId) {
        List<SignLog> signLogs = new ArrayList<SignLog>();

        Cursor cursor = database.query(DatabaseSQLiteHelper.TABLE_SIGN_LOGS, null,
                DatabaseSQLiteHelper.LOGS_USER_ID + " = " + userId, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            SignLog log = cursorToItem(cursor);
            signLogs.add(log);

            cursor.moveToNext();
        }
        cursor.close();
        return signLogs;
    }

    /**
     * method, used to get item values from cursor row
     */
    private SignLog cursorToItem(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseSQLiteHelper.LOGS_ID));
        String name = cursor.getString(cursor.getColumnIndex(DatabaseSQLiteHelper.LOGS_NAME));
        String password = cursor.getString(cursor.getColumnIndex(DatabaseSQLiteHelper.LOGS_PASSWORD));
        String userId = cursor.getString(cursor.getColumnIndex(DatabaseSQLiteHelper.LOGS_USER_ID));
        String day = cursor.getString(cursor.getColumnIndex(DatabaseSQLiteHelper.LOGS_DAY));
        String time = cursor.getString(cursor.getColumnIndex(DatabaseSQLiteHelper.LOGS_TIME));
        int type = cursor.getInt(cursor.getColumnIndex(DatabaseSQLiteHelper.LOGS_TYPE));

        SignLog log = new SignLog(name, password, userId, day, time, type).setId(id);

        return log;
    }

    /**
     * method, used to delete sign log from database
     */
    public void delete(int id) {
        database.delete(DatabaseSQLiteHelper.TABLE_SIGN_LOGS, DatabaseSQLiteHelper.LOGS_ID + " = " + id, null);
    }

    /**
     * method, used to check if database has sign logs of a user or not based on count
     */
    public boolean hasLogs(String userId) {
        int count = getLogsCount(userId);

        if (count == 0)
            return false;
        else
            return true;
    }

    /**
     * method, used to get logs count of a user
     */
    public int getLogsCount(String userId) {
        Cursor cursor = database.rawQuery("SELECT COUNT(" + DatabaseSQLiteHelper.LOGS_ID +
                ") FROM " + DatabaseSQLiteHelper.TABLE_SIGN_LOGS
                + " WHERE " + DatabaseSQLiteHelper.LOGS_USER_ID + " = " + userId, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        return count;
    }
}
