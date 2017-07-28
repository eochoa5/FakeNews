package com.example.edwin.newsapp.Utils;

/**
 * Created by Edwin on 7/27/2017.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.edwin.newsapp.Models.NewsItem;
import com.example.edwin.newsapp.NetworkUtils;


import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class RefreshTasks {

    public static final String ACTION_REFRESH = "refresh";


    public static void refreshArticles(Context context) {
        ArrayList<NewsItem> result = null;
        URL url = NetworkUtils.buildUrl();

        SQLiteDatabase db = new DBHelper(context).getWritableDatabase();

        try {

            //delete all articles before inserting
            DatabaseUtils.deleteAll(db);
            String json = NetworkUtils.getResponseFromHttpUrl(url);
            result = NetworkUtils.parseJSON(json);

            //loop through all news items and insert them
            DatabaseUtils.bulkInsert(db, result);

        } catch (IOException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        db.close();
    }
}