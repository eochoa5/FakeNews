package com.example.edwin.newsapp.Utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.edwin.newsapp.Services.NewsJob;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

/**
 * Created by Edwin on 7/27/2017.
 */
public class ScheduleUtilities {
    private static final int INTERVAL = 60;
    private static final String NEWS_JOB_TAG = "news_job_tag";

    private static boolean sInitialized;

    synchronized public static void scheduleRefresh(@NonNull final Context context){
        if(sInitialized) return;

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job constraintRefreshJob = dispatcher.newJobBuilder()
                //job dispatcher will execute code in NewsJob service every minute
                .setService(NewsJob.class)
                .setTag(NEWS_JOB_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)

                //execute NewsJob every minute if possible or every 2 minutes max
                .setTrigger(Trigger.executionWindow(INTERVAL, INTERVAL*2))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(constraintRefreshJob);
        sInitialized = true;

    }

}