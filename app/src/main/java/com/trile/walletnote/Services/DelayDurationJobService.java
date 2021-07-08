package com.trile.walletnote.Services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;

import com.trile.walletnote.R;

public class DelayDurationJobService extends JobService {
    Context context;

    @Override
    public boolean onStartJob(JobParameters params) {
        context = this;
        runBackground(params);
        return true;
    }

    void runBackground(JobParameters params){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String durationInfoString = params.getExtras().getString("durationJobInfo");
                int delayTime = params.getExtras().getInt("delayTime");
                int currentId = params.getExtras().getInt("currentPeriodicId");

                ComponentName componentName = new ComponentName(context, DurationFinAutoJobServiceImpl.class);

                PersistableBundle bundle = new PersistableBundle();
                bundle.putString("object",durationInfoString);

                JobInfo info = new JobInfo.Builder(Integer.parseInt(getString(R.string.duration_auto_job_id_prefix)+""+currentId),componentName)
                        .setRequiresCharging(false)
                        .setPersisted(true)
                        .setPeriodic(60*1000*60*24*delayTime)
                        .setExtras(bundle)
                        .build();

                JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                scheduler.schedule(info);

                jobFinished(params,false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
