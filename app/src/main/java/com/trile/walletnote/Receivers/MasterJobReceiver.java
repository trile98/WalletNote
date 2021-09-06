package com.trile.walletnote.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.trile.walletnote.Jobs.MasterJob;
import com.trile.walletnote.sharePreferencces.CurrentStatusPrefs;

public class MasterJobReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
           CurrentStatusPrefs currentStatusPrefs = new CurrentStatusPrefs(context);
           if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) || Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(intent.getAction()) || Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction()) || Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
               Toast.makeText(context,"test receiver",Toast.LENGTH_LONG).show();
               if (currentStatusPrefs.getCheckMasterServiceRun()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(new Intent(context, MasterJob.class));
                    } else {
                        context.startService(new Intent(context, MasterJob.class));
                    }
               }
           }
    }
}
