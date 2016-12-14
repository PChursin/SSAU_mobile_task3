package ru.ssau.mobile.ssau_mobile_task3.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Pavel on 14.12.2016.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Tracker.db";
    public static final int DATABASE_VERSION = 1;

    public static final String CATEGORY_TABLE = "Category";
    public static final String CATEGORY_ID = "_id";
    public static final String CATEGORY_NAME = "name";

    public static final String RECORD_TABLE = "Record";
    public static final String RECORD_ID = "_id";
    public static final String RECORD_CATEGORY = "category_id";
    public static final String RECORD_START = "start";
    public static final String RECORD_END = "end";
    public static final String RECORD_SUMMARY = "summary";
    public static final String RECORD_PHOTO = "photo";

    public static final String PHOTO_TABLE = "Photo";


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
