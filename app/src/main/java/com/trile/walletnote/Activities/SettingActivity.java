package com.trile.walletnote.Activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.trile.walletnote.Jobs.MasterJob;
import com.trile.walletnote.R;
import com.trile.walletnote.sharePreferencces.CurrentStatusPrefs;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setUpComponent();
    }

    void setUpComponent(){
        Toolbar toolbar = findViewById(R.id.setting_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.super.onBackPressed();
            }
        });

        Intent masterServiceIntent = new Intent(this, MasterJob.class);

        CurrentStatusPrefs currentStatusPrefs = new CurrentStatusPrefs(this);

        SwitchCompat serviceSwitch = findViewById(R.id.setting_service_check_switch);

        serviceSwitch.setChecked(false);

        //if satisfy the condition, switch is set as true
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo info : activityManager.getRunningServices(Integer.MAX_VALUE)){
            if( (MasterJob.class.getName().equals(info.service.getClassName())) && currentStatusPrefs.getCheckMasterServiceRun()){
                serviceSwitch.setChecked(true);
                break;
            }
        }

        serviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    startService(masterServiceIntent);
                }else{
                    stopService(masterServiceIntent);
                }
            }
        });
    }
}