package com.trile.walletnote.Services;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import com.trile.walletnote.Activities.MainActivity;
import com.trile.walletnote.model.FinancialInformation;

public interface CustomDialog {
    void warningDialog(String message);

    void datePickDialog(TextView masterView, FinancialInformation item);


    void totalDialog(String totalString);

    void updateDeleteSuccessDialog(Intent intent, String title, String content, Activity currentActivity);

    void saveSuccessDialog(MainActivity activity);
}
