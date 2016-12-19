package ru.ssau.mobile.ssau_mobile_task3.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

import ru.ssau.mobile.ssau_mobile_task3.model.Category;
import ru.ssau.mobile.ssau_mobile_task3.model.Photo;
import ru.ssau.mobile.ssau_mobile_task3.model.Record;

/**
 * Created by Pavel on 15.12.2016.
 */

public class RecordOperations {
    private DBHelper dbHelper;
    private String[] columns = {DBHelper.RECORD_ID, DBHelper.RECORD_CATEGORY, DBHelper.RECORD_START,
        DBHelper.RECORD_END, DBHelper.RECORD_MINUTES, DBHelper.RECORD_SUMMARY, DBHelper.RECORD_PHOTO};
    private SQLiteDatabase db;
    private CategoryOperations categoryOperations;
    private PhotoOperations photoOperations;

    private static RecordOperations instance;

    public static RecordOperations getInstance(Context context) {
        if (instance == null)
            instance = new RecordOperations(context);
        return instance;
    }

    private RecordOperations(Context context) {
        dbHelper = DBHelper.getInstance(context);
        categoryOperations = CategoryOperations.getInstance(context);
        photoOperations = PhotoOperations.getInstance(context);
        open();
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        db.close();
    }

    public Record addRecord(long catId, long start, long end, long minutes, String summary,
                            String photos)
    {
        ContentValues values = new ContentValues();
        values.put(DBHelper.RECORD_CATEGORY, catId);
        values.put(DBHelper.RECORD_START, start);
        values.put(DBHelper.RECORD_END, end);
        values.put(DBHelper.RECORD_MINUTES, minutes);
        values.put(DBHelper.RECORD_SUMMARY, summary);
        values.put(DBHelper.RECORD_PHOTO, photos);
        long id = db.insert(DBHelper.RECORD_TABLE, null, values);
        ArrayList<Photo> photoList = getAttachedPhotos(photos);
        Category category = categoryOperations.getCategory(catId);
        return new Record(id, category, start, end, minutes, summary, photoList);
    }

    @Nullable
    public ArrayList<Photo> getAttachedPhotos(String photos) {
        ArrayList<Photo> photoList = null;
        if (photos != null && photos.length() > 0) {
            photoList = new ArrayList<>();
            for (String s : photos.split(",")) {
                long pId = Integer.parseInt(s);
                Photo p = photoOperations.getPhoto(pId);
                photoList.add(p);
            }
        }
        return photoList;
    }

    @Nullable
    public Record getUnfinishedRecord() {
        Cursor cursor = db.query(DBHelper.RECORD_TABLE, columns, DBHelper.RECORD_END+"=0", null, null, null, null);
        Record out = null;
        if(cursor.moveToFirst())
            out = parseRecord(cursor);
        cursor.close();
        return out;
    }

    public ArrayList<Record> getRecordsRange(long from, long to) {
        String q = DBHelper.RECORD_START+">="+from+" and "+DBHelper.RECORD_START+"<="+to;
        Cursor cursor = db.query(DBHelper.RECORD_TABLE, columns, q, null, null, null, null);
        return getAllRecords(cursor);
    }

    public HashMap<String, Long> getGroupedRecordsTime(long from, long to) {
        String q = DBHelper.RECORD_START+">="+from+" and "+DBHelper.RECORD_START+"<="+to;
        Cursor cursor = db.query(DBHelper.RECORD_TABLE, new String[]{DBHelper.RECORD_CATEGORY,
                "SUM("+DBHelper.RECORD_MINUTES+")"}, q, null, DBHelper.RECORD_CATEGORY, null, null);
        HashMap<String, Long> out = new HashMap<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Category cat = categoryOperations.getCategory(cursor.getLong(0));
            out.put(cat.getName(), cursor.getLong(1));
            cursor.moveToNext();
        }
        cursor.close();
        return out;
    }

    public HashMap<String, Long> getGroupedRecordsCount(long from, long to) {
        String q = DBHelper.RECORD_START+">="+from+" and "+DBHelper.RECORD_START+"<="+to;
        Cursor cursor = db.query(DBHelper.RECORD_TABLE, new String[]{DBHelper.RECORD_CATEGORY,
                "COUNT(*)"}, q, null, DBHelper.RECORD_CATEGORY, null, null);
        HashMap<String, Long> out = new HashMap<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Category cat = categoryOperations.getCategory(cursor.getLong(0));
            out.put(cat.getName(), cursor.getLong(1));
            cursor.moveToNext();
        }
        cursor.close();
        return out;
    }

    public void updateRecord(Record oldRec, Record newRec) {
        //deleteRecord(oldRec);
        long catId = newRec.getCategory().getId();
        String photos = getPhotoString(newRec);
        ContentValues values = new ContentValues();
        values.put(DBHelper.RECORD_CATEGORY, catId);
        values.put(DBHelper.RECORD_START, newRec.getStart());
        values.put(DBHelper.RECORD_END, newRec.getEnd());
        values.put(DBHelper.RECORD_MINUTES, newRec.getMinutes());
        values.put(DBHelper.RECORD_SUMMARY, newRec.getSummary());
        values.put(DBHelper.RECORD_PHOTO, photos);
        db.update(DBHelper.RECORD_TABLE, values, DBHelper.CATEGORY_ID+"="+newRec.getId(), null);

//        addRecord(catId, newRec.getStart(), newRec.getEnd(), newRec.getMinutes(), newRec.getSummary(),
//                photos);
    }

    @Nullable
    public static String getPhotoString(Record newRec) {
        String photos = null;
        ArrayList<Photo> photoList = newRec.getPhotos();
        if (photoList != null) {
            StringBuilder sb = new StringBuilder();
            for (Photo p : photoList) {
                sb.append(p.getId()).append(",");
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
                photos = sb.toString();
            }
        }
        return photos;
    }

    public ArrayList<Record> getAllRecords() {
        Cursor cursor = db.query(DBHelper.RECORD_TABLE, columns, null, null, null, null,
                DBHelper.RECORD_START+" DESC");
        return getAllRecords(cursor);
    }

    private ArrayList<Record> getAllRecords(Cursor cursor) {
        //cursor = db.query(DBHelper.RECORD_TABLE, columns, q, null, null, null, null);
        ArrayList<Record> out = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Record record = parseRecord(cursor);
            out.add(record);
            cursor.moveToNext();
        }
        cursor.close();
        return out;
    }

    public void deleteRecord(Record record) {
        long id = record.getId();
        ArrayList<Photo> photos = record.getPhotos();
        //TODO error here
        if (photos!=null)
            for (Photo p : photos)
                photoOperations.deletePhoto(p);
        db.delete(DBHelper.RECORD_TABLE, DBHelper.RECORD_ID+ "=" + id, null);
    }

    private Record parseRecord(Cursor cursor) {
        Record record = new Record();
        record.setId(cursor.getLong(0));
        record.setCategory(categoryOperations.getCategory(cursor.getLong(1)));
        record.setStart(cursor.getLong(2));
        record.setEnd(cursor.getLong(3));
        record.setMinutes(cursor.getLong(4));
        record.setSummary(cursor.getString(5));
        String photos = cursor.getString(6);
        record.setPhotos(getAttachedPhotos(photos));
        return record;
    }
}
