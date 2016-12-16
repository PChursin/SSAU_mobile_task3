package ru.ssau.mobile.ssau_mobile_task3.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Pavel on 14.12.2016.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Tracker.db";
    public static final int DATABASE_VERSION = 2;

    public static final String CATEGORY_TABLE = "Category";
    public static final String CATEGORY_ID = "_id";
    public static final String CATEGORY_NAME = "name";
    public static final String CREATE_CATEGORY = String.format(
            "create table %s(%s integer primary key autoincrement, " +
            "%s text not null);", CATEGORY_TABLE, CATEGORY_ID, CATEGORY_NAME);

    public static final String RECORD_TABLE = "Record";
    public static final String RECORD_ID = "_id";
    public static final String RECORD_CATEGORY = "category_id";
    public static final String RECORD_START = "stime";
    public static final String RECORD_END = "etime";
    public static final String RECORD_MINUTES = "minutes";
    public static final String RECORD_SUMMARY = "summary";
    public static final String RECORD_PHOTO = "photo";
    /*public static final String CREATE_RECORD = String.format(
            "create table %s(%s integer primary key autoincrement, " +
            "%s integer, foreign key(%s) references %s(%s) on delete cascade, " +
            "%s integer not null, %s integer not null, %s integer " +
            "not null, %s text not null, %s text);", RECORD_TABLE,
            RECORD_ID, RECORD_CATEGORY, RECORD_CATEGORY, CATEGORY_TABLE,
            CATEGORY_ID, RECORD_START, RECORD_END, RECORD_MINUTES, RECORD_SUMMARY,
            RECORD_PHOTO);*/

    public static final String CREATE_RECORD = String.format(
            "create table %s(%s integer primary key autoincrement, " +
                    "%s integer not null, " +
                    "%s integer not null, %s integer not null, %s integer " +
                    "not null, %s text, %s text);", RECORD_TABLE,
            RECORD_ID, RECORD_CATEGORY, RECORD_START, RECORD_END, RECORD_MINUTES, RECORD_SUMMARY,
            RECORD_PHOTO);

    public static final String PHOTO_TABLE = "Photo";
    public static final String PHOTO_ID = "_id";
    public static final String PHOTO_DATA = "image";
    public static final String CREATE_PHOTO = String.format(
            "create table %s(%s integer primary key autoincrement, " +
            "%s blob not null);", PHOTO_TABLE, PHOTO_ID, PHOTO_DATA);

    public static final String TAG = "DBHelper";
    private static DBHelper instance;

    public static DBHelper getInstance(Context context) {
        if (instance == null)
            instance = new DBHelper(context);
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        db.execSQL(CREATE_CATEGORY);
        db.execSQL(CREATE_PHOTO);
        db.execSQL(CREATE_RECORD);

        String[] cats = {"Работа", "Обед", "Отдых", "Уборка", "Сон"};
        for (String cat : cats) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.CATEGORY_NAME, cat);
            db.insert(DBHelper.CATEGORY_TABLE, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.w(TAG, "onUpgrade: from "+i+" to "+i1);
        db.execSQL("drop table if exists " + RECORD_TABLE);
        db.execSQL("drop table if exists " + CATEGORY_TABLE);
        db.execSQL("drop table if exists " + PHOTO_TABLE);
        onCreate(db);
    }
}
