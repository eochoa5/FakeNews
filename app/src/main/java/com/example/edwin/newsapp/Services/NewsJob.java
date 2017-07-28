package com.example.edwin.newsapp.Services;

/**
 * Created by Edwin on 7/27/2017.
 */

import android.os.AsyncTask;
import android.widget.Toast;

import com.example.edwin.newsapp.Utils.RefreshTasks;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;



public class NewsJob extends JobService {
    AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        mBackgroundTask = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Object doInBackground(Object[] params) {
                RefreshTasks.refreshArticles(NewsJob.this);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(job, false);
                super.onPostExecute(o);

                Toast.makeText(getApplicationContext(), "New articles added", Toast.LENGTH_LONG).show();

            }
        };


        mBackgroundTask.execute();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {

        if (mBackgroundTask != null) mBackgroundTask.cancel(false);

        return true;
    }
}