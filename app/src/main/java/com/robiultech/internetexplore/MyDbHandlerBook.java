package com.robiultech.internetexplore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyDbHandlerBook extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bookmarks.db";
    private static final String TABLE_BOOKMARKS = "bookmarks";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "url";

    public MyDbHandlerBook(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_BOOKMARKS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT " + ")";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARKS);
        onCreate(db);
    }

    public void addUrl(Websites websites) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, websites.get_url());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_BOOKMARKS, null, values);
        db.close();

    }

    public void deleteUrl(String urlName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_BOOKMARKS + " WHERE " + COLUMN_NAME + "=\"" + urlName + "\";");

    }

    public List<String> databaseToString() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_BOOKMARKS;
        List<String> dbString = new ArrayList<>();
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int i = 0;
        if (c.moveToNext()) {
            do {
                if (c.getString(c.getColumnIndex(COLUMN_NAME)) != null) {
                    String bstring = "";
                    bstring += c.getString(c.getColumnIndex("url"));
                    dbString.add(bstring);

                }

            } while (c.moveToNext());

        }

        return dbString;
    }
}
