package com.example.aporath.todoish;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class SchedulingService extends IntentService {
    public SchedulingService() {
        super("SchedulingService");
    }
    
    // An ID used to post the notification.
    public static final int NOTIFICATION_ID = 1;

    @Override
    protected void onHandleIntent(Intent intent) {
        // BEGIN_INCLUDE(service_onhandle)
        // The URL from which to fetch content.

        MainActivity mAct = MainActivity.getM_activity();

        /*
        check if a day/week/month has passed
        go through the tasks and reset what needs resetting
         */

        //todo remove comments from below "if" code
        Log.d("SchedulingService ", "received alarm in day " + mAct.m_cal.get(Calendar.DAY_OF_WEEK));
        if (mAct.m_cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            Log.d("SchedulingService ", "resetting weekly tasks");
            mAct.tasksTableResetRepeating("Weekly");
        }

        int dayOfMonth = mAct.m_cal.get(Calendar.DAY_OF_MONTH);
        if (dayOfMonth == 1) {
            Log.d("SchedulingService ", "resetting monthly tasks");
            mAct.tasksTableResetRepeating("Monthly");
        }

        // Release the wake lock provided by the BroadcastReceiver.
        AlarmReceiver.completeWakefulIntent(intent);
        // END_INCLUDE(service_onhandle)
    }
 
}
