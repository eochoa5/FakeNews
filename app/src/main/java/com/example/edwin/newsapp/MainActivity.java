package com.example.edwin.newsapp;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.edwin.newsapp.Utils.DatabaseUtils;
import com.example.edwin.newsapp.Utils.RefreshTasks;
import com.example.edwin.newsapp.Utils.ScheduleUtilities;

import org.json.JSONException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NewsRecyclerAdapter.NewsAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Void>{


    private RecyclerView mRecyclerView;
    private NewsRecyclerAdapter mNewsAdapter;
    private static final int NEWS_LOADER = 1;
    private Cursor cursor;
    private SQLiteDatabase db;
    private ProgressDialog mProgressDialog;
    private SharedPreferences sharedPreferences;
    public static final String PREFS = "Prefs" ;

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

        sharedPreferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        /*if it's the first app run, call news API and insert results into db immediately
        instead of waiting 60 seconds for the job to do it bc onstart will soon try to pull from an empty db and we don't
        want that
         */

        if(!sharedPreferences.contains("HasRan")){
            load();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("HasRan", true);
            editor.commit();
        }

        ScheduleUtilities.scheduleRefresh(this);

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

        //get all stories from db
        db = new DBHelper(MainActivity.this).getReadableDatabase();
        cursor = DatabaseUtils.getAll(db);

        mNewsAdapter = new NewsRecyclerAdapter(this, cursor);
        mRecyclerView.setAdapter(mNewsAdapter);


    }

    //AsynctaskLoader
    @Override
    public Loader<Void> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Void>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                mProgressDialog = new ProgressDialog(MainActivity.this);
                mProgressDialog.setMessage("Loading articles...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.show();

            }

            @Override
            public Void loadInBackground() {

                //delete all from db, call news API, insert all results into db
                RefreshTasks.refreshArticles(MainActivity.this);
                return null;
            }

        };
    }

    @Override
    public void onLoadFinished(Loader<Void> loader, Void data) {
        db = new DBHelper(MainActivity.this).getReadableDatabase();
        cursor = DatabaseUtils.getAll(db);

        mNewsAdapter = new NewsRecyclerAdapter(this, cursor);
        mRecyclerView.setAdapter(mNewsAdapter);
        mNewsAdapter.notifyDataSetChanged();

        mProgressDialog.dismiss();

    }

    @Override
    public void onLoaderReset(Loader<Void> loader) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {

            ////delete all from db, call news API, insert all results into db
            //refresh cursor to fetch all results from db
            load();
        }

        return super.onOptionsItemSelected(item);
    }

    public void load() {
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.restartLoader(NEWS_LOADER, null, this).forceLoad();

    }
}
