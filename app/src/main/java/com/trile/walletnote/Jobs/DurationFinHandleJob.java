package com.trile.walletnote.Jobs;

import android.app.Notification;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.trile.walletnote.R;
import com.trile.walletnote.Services.ChangeFormatDateService;
import com.trile.walletnote.Services.ChangeFormatDateServiceImpl;
import com.trile.walletnote.model.Database;
import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.sharePreferencces.PeriodFinancialInformationPrefs;

import java.util.ArrayList;

import static com.trile.walletnote.Activities.MainActivity.CHANNEL_ID;

public class DurationFinHandleJob extends Service {

    PeriodFinancialInformationPrefs periodFinancialInformationPrefs;
    SQLiteDatabase db;
    Database dbHelper;
    ChangeFormatDateService changeFormatDateService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        periodFinancialInformationPrefs= new PeriodFinancialInformationPrefs(this);
        changeFormatDateService = new ChangeFormatDateServiceImpl();
        dbHelper = new Database(this);
        db = dbHelper.getDatabase();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        runBackground();
        return START_NOT_STICKY;
    }

    private void runBackground(){

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle(getString(R.string.master_job_notification_title))
                .setContentText(getString(R.string.master_job_notification_content))
                .build();

        ArrayList<FinancialInformation> infoList = new ArrayList<>();
        infoList.addAll(periodFinancialInformationPrefs.getListDailyFI());
        infoList.addAll(periodFinancialInformationPrefs.getListWeeklyFI());
        infoList.addAll(periodFinancialInformationPrefs.getListMonthlyFI());


        for (FinancialInformation info : infoList){
            addNewFinancialInfo(info);
        }
        startForeground(1,notification);
        super.onDestroy();
    }


    private void addNewFinancialInfo(FinancialInformation info) {
        int currentId = 1;

        Cursor stringIdCursor = db.rawQuery("SELECT seq from sqlite_sequence where name = 'FINANCIAL_INFORMATION'", null);
        if(stringIdCursor.moveToFirst()) {
            currentId = stringIdCursor.getInt(0)+1;
        }
        ContentValues FinInfoContentValue = info.setContentValuesForPeriodJobSave(this);
        ContentValues FinDetailContentValue = info.getDetail().setDetailContentValues(currentId);

        db.insert("FINANCIAL_INFORMATION",null, FinInfoContentValue);
        db.insert("FINANCIAL_DETAIL",null,FinDetailContentValue);

    }
}
