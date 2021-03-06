package ru.ssau.mobile.ssau_mobile_task3.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import ru.ssau.mobile.ssau_mobile_task3.model.Photo;

/**
 * Created by Pavel on 15.12.2016.
 */
public class PhotoOperations {
    private DBHelper dbHelper;
    private String[] columns = {DBHelper.PHOTO_ID, DBHelper.PHOTO_DATA};
    private SQLiteDatabase db;

    private static PhotoOperations instance;

    public static final String TAG = "PhotoOperations";

    public static PhotoOperations getInstance(Context context) {
        if (instance == null)
            instance = new PhotoOperations(context);
        return instance;
    }

    private PhotoOperations(Context context) {
        dbHelper = DBHelper.getInstance(context);
        open();
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        db.close();
    }

    public Photo addPhoto(byte[] image) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.PHOTO_DATA, image);
        long id = db.insert(DBHelper.PHOTO_TABLE, null, values);
        Photo photo = new Photo(id, image);
        return photo;
    }

    public Photo getPhoto(long id) {
        Cursor cursor = db.query(DBHelper.PHOTO_TABLE, columns, DBHelper.PHOTO_ID+ "=" + id,
                null, null, null, null);
        cursor.moveToFirst();
        Log.d(TAG, "getPhoto: with ID "+id);
        Photo photo = parsePhoto(cursor);
        Log.d(TAG, "getPhoto: success!");
        cursor.close();
        return photo;
    }

    public void deletePhoto(Photo photo) {
        long id = photo.getId();
        Log.d(TAG, "deletePhoto: with ID "+id);
        db.delete(DBHelper.PHOTO_TABLE, DBHelper.PHOTO_ID + "=" + id, null);
    }

    private Photo parsePhoto(Cursor cursor) {
        Photo photo = new Photo();
        photo.setId(cursor.getLong(0));
        photo.setImage(cursor.getBlob(1));
        return photo;
    }
}
