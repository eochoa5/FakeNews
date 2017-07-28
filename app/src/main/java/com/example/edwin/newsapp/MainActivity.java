package com.example.edwin.newsapp;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.edwin.newsapp.Adapters.NewsRecyclerAdapter;
import com.example.edwin.newsapp.Models.NewsItem;
import com.example.edwin.newsapp.Utils.Contract;
import com.example.edwin.newsapp.Utils.DBHelper;

import org.json.JSONException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NewsRecyclerAdapter.NewsAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<ArrayList<NewsItem>>{

    private ProgressDialog mProgressDialog;
    private static final int NEWS_LOADER = 1;
    private RecyclerView mRecyclerView;
    private NewsRecyclerAdapter mNewsAdapter;
    private LoaderManager loaderManager;
    private Loader<ArrayList<NewsItem>> loader;
    private boolean isLoaderRunning = false;
    private boolean shouldLoaderBeStarted = false;
    private DBHelper helper;
    private Cursor cursor;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_news);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mNewsAdapter = new NewsRecyclerAdapter(this);
        mRecyclerView.setAdapter(mNewsAdapter);

        //initialize or restart the loader
        //flag used to prevent loader from loading onResume
        shouldLoaderBeStarted = true;
        loaderManager = getSupportLoaderManager();
        loader = loaderManager.getLoader(NEWS_LOADER);
        if(loader == null){
            loaderManager.initLoader(NEWS_LOADER, null, this).forceLoad();
        }else{
            loaderManager.restartLoader(NEWS_LOADER, null, this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (db != null) db.close();
        if (cursor != null) cursor.close();
    }

    @Override
    protected void onStart() {
        super.onStart();

        helper = new DBHelper(this);
        db = helper.getWritableDatabase();
        //cursor = getAllItems(db);
        String title = "the title";
        String author = "goku";
        String description = "bad news";
        String published_at = "12/25/16";
        String url = "www.google.com";
        String thumbnail = "img.png";

        addNewsItem(db, title, author, description, published_at, url, thumbnail);
        cursor = getAllItems(db);
        System.out.println("the table has " + cursor.getCount() + " items");

    }

    private Cursor getAllItems(SQLiteDatabase db) {
        return db.query(
                Contract.TABLE_NEWS.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

    }

    private long addNewsItem(SQLiteDatabase db, String title, String author, String description,
                             String published_at, String url, String thumbnail) {

        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_NEWS.COLUMN_NAME_TITLE, title);
        cv.put(Contract.TABLE_NEWS.COLUMN_NAME_AUTHOR, author);
        cv.put(Contract.TABLE_NEWS.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_NEWS.COLUMN_NAME_PUBLISHED_AT, published_at);
        cv.put(Contract.TABLE_NEWS.COLUMN_NAME_URL, url);
        cv.put(Contract.TABLE_NEWS.COLUMN_NAME_THUMBNAIL, thumbnail);

        return db.insert(Contract.TABLE_NEWS.TABLE_NAME, null, cv);
    }

    @Override
    public void onClick(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }



    @Override
    public Loader<ArrayList<NewsItem>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<NewsItem>>(this) {
            @Override
            protected void onStartLoading() {

                super.onStartLoading();
                if(!shouldLoaderBeStarted){return;}
                isLoaderRunning = true;
                mProgressDialog = new ProgressDialog(MainActivity.this);
                mProgressDialog.setMessage("Loading data...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.show();
                System.out.println("loader refreshed");
            }

            @Override
            public ArrayList<NewsItem> loadInBackground() {
                if(!shouldLoaderBeStarted){return null;}
                String jsonResults;
                ArrayList<NewsItem> results = null;
                try {
                    jsonResults = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildUrl());
                    results = NetworkUtils.parseJSON(jsonResults);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return results;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<NewsItem>> loader, ArrayList<NewsItem> data) {
        if(!shouldLoaderBeStarted){return;}
        if(data != null){
            mNewsAdapter.setNewsData(data);
        }
        else{
            Snackbar.make(findViewById(R.id.coordinatorLayout), "An error occurred please try again", Snackbar.LENGTH_LONG)
                    .show();
        }
        mProgressDialog.dismiss();
        isLoaderRunning = false;
        shouldLoaderBeStarted = false;

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<NewsItem>> loader) {

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            shouldLoaderBeStarted = true;
            if(!isLoaderRunning) {

                loaderManager.restartLoader(NEWS_LOADER, null, this).forceLoad();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
