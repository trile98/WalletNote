package com.trile.walletnote.Services;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import com.trile.walletnote.Activities.MainActivity;

public interface CustomDialog {
    void warningDialog(String message);

    void datePickDialog(TextView masterView);


    void totalDialog(String totalString);

    void updateDeleteSuccessDialog(Intent intent, String title, String content, Activity currentActivity);

    void saveSuccessDialog(MainActivity activity);
}
