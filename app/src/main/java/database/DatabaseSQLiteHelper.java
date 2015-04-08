package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseSQLiteHelper extends SQLiteOpenHelper {
    private Context context;

    // database info
    private static final String DATABASE_NAME = "signed.db";
    private static final int DATABASE_VERSION = 1;

    // table sign_logs
    public static final String TABLE_SIGN_LOGS = "sign_logs";
    public static final String LOGS_ID = "_id";
    public static final String LOGS_NAME = "_name";
    public static final String LOGS_PASSWORD = "password";
    public static final String LOGS_USER_ID = "user_id";
    public static final String LOGS_DAY = "day";
    public static final String LOGS_TIME = "time";
    public static final String LOGS_TYPE = "type";

    // tables creation
    private static final String SERVICE_ITEMS_CREATE = "CREATE TABLE " + TABLE_SIGN_LOGS
            + "("
            + LOGS_ID + " INTEGER PRIMARY KEY, "
            + LOGS_NAME + " TEXT NOT NULL, "
            + LOGS_PASSWORD + " TEXT NOT NULL, "
            + LOGS_USER_ID + " TEXT NOT NULL, "
            + LOGS_DAY + " TEXT NOT NULL, "
            + LOGS_TIME + " TEXT NOT NULL, "
            + LOGS_TYPE + " INTEGER NOT NULL"
            + ");";

    public DatabaseSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        // create tables
        database.execSQL(SERVICE_ITEMS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SIGN_LOGS);
        onCreate(db);
    }

}
