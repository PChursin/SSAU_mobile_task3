package ru.ssau.mobile.ssau_mobile_task3.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ru.ssau.mobile.ssau_mobile_task3.model.Category;

/**
 * Created by Pavel on 15.12.2016.
 */

public class CategoryOperations {
    private DBHelper dbHelper;
    private String[] columns = {DBHelper.CATEGORY_ID, DBHelper.CATEGORY_NAME};
    private SQLiteDatabase db;

    public CategoryOperations(Context context) {
        dbHelper = DBHelper.getInstance(context);
        open();
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        db.close();
    }

    public Category addCategory(String name) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.CATEGORY_NAME, name);
        long catId = db.insert(DBHelper.CATEGORY_TABLE, null, values);
        Category cat = new Category(catId, name);
        return cat;
    }

    public Category getCategory(long id) {
        Cursor cursor = db.query(DBHelper.CATEGORY_TABLE, columns, DBHelper.CATEGORY_ID + "=" + id,
                null, null, null, null);
        Category cat = parseCategory(cursor);
        cursor.close();
        return cat;
    }

    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> out = new ArrayList<>();
        Cursor cursor = db.query(DBHelper.CATEGORY_TABLE, columns, null, null,
                null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Category cat = parseCategory(cursor);
            out.add(cat);
            cursor.moveToNext();
        }
        cursor.close();
        return out;
    }

    public void deleteCategory(Category cat) {
        long id = cat.getId();
        db.delete(DBHelper.CATEGORY_TABLE, DBHelper.CATEGORY_ID + "=" + id, null);
    }

    private Category parseCategory(Cursor cursor) {
        Category cat = new Category();
        cat.setId(cursor.getLong(0));
        cat.setName(cursor.getString(1));
        return cat;
    }
}
