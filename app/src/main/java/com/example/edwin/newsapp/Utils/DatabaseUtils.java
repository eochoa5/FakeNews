package com.example.edwin.newsapp.Utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.edwin.newsapp.Models.NewsItem;

import java.util.ArrayList;

/**
 * Created by Edwin on 7/27/2017.
 */
public class DatabaseUtils {

    public static Cursor getAll(SQLiteDatabase db) {
        Cursor cursor = db.query(
                Contract.TABLE_NEWS.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    public static void bulkInsert(SQLiteDatabase db, ArrayList<NewsItem> articles) {

        db.beginTransaction();
        try {
            for (NewsItem a : articles) {
                ContentValues cv = new ContentValues();
                cv.put(Contract.TABLE_NEWS.COLUMN_NAME_TITLE, a.getTitle());
                cv.put(Contract.TABLE_NEWS.COLUMN_NAME_AUTHOR, a.getAuthor());
                cv.put(Contract.TABLE_NEWS.COLUMN_NAME_DESCRIPTION, a.getDescription());
                cv.put(Contract.TABLE_NEWS.COLUMN_NAME_PUBLISHED_AT, a.getPublishedAt());
                cv.put(Contract.TABLE_NEWS.COLUMN_NAME_URL, a.getUrl());
                cv.put(Contract.TABLE_NEWS.COLUMN_NAME_THUMBNAIL, a.getUrlToImage());

                db.insert(Contract.TABLE_NEWS.TABLE_NAME, null, cv);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public static void deleteAll(SQLiteDatabase db) {
        db.delete(Contract.TABLE_NEWS.TABLE_NAME, null, null);
    }

}