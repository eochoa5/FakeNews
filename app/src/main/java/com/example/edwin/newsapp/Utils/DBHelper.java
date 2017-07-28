package com.example.edwin.newsapp.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Edwin on 7/26/2017.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "items.db";
    private static final String TAG = "dbhelper";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryString = "CREATE TABLE " + Contract.TABLE_NEWS.TABLE_NAME + " ("+
                Contract.TABLE_NEWS._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Contract.TABLE_NEWS.COLUMN_NAME_TITLE + " TEXT NOT NULL UNIQUE, " +
                Contract.TABLE_NEWS.COLUMN_NAME_AUTHOR + " TEXT NOT NULL, " +
                Contract.TABLE_NEWS.COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL, " +
                Contract.TABLE_NEWS.COLUMN_NAME_PUBLISHED_AT + " TEXT NOT NULL, " +
                Contract.TABLE_NEWS.COLUMN_NAME_URL + " TEXT NOT NULL, " +
                Contract.TABLE_NEWS.COLUMN_NAME_THUMBNAIL + " TEXT " + "); ";



        db.execSQL(queryString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       // db.execSQL("drop table " + Contract.TABLE_NEWS.TABLE_NAME + " if exists;");
    }
}
