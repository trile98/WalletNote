package com.trile.walletnote.Jobs;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.trile.walletnote.R;

import java.util.Calendar;

import static com.trile.walletnote.Activities.MainActivity.CHANNEL_ID;

public class MasterJob extends Service { //this job wil run forever (after adding first periodic info) handle other jobs


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle(getString(R.string.master_job_notification_title))
                .setContentText(getString(R.string.master_job_notification_content))
                .build();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);

        long startupTime = cal.getTimeInMillis();

        if(System.currentTimeMillis() > startupTime)
            startupTime += 24 * 60 *60 *1000;

        Intent durationIntent = new Intent(this,DurationFinHandleJob.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,durationIntent,0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, startupTime, 24 * 60 *60 *1000, pendingIntent);

        startForeground(1,notification);

        return START_STICKY;
    }
}
