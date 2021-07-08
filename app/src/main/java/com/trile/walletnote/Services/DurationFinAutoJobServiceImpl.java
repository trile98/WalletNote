package com.trile.walletnote.Services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.trile.walletnote.model.Database;
import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.sharePreferencces.ReasonPrefs;
import com.google.gson.Gson;

public class DurationFinAutoJobServiceImpl extends JobService implements DurationFinAutoJobService{
    SQLiteDatabase db;
    Database dbHelper;
    ReasonPrefs reasonPrefs;
    ChangeFormatDateService changeFormatDateService;


    @Override
    public boolean onStartJob(JobParameters params) {
        dbHelper = new Database(this);
        db = dbHelper.getDatabase();
        reasonPrefs = new ReasonPrefs(this);
        changeFormatDateService = new ChangeFormatDateServiceImpl();
        runBackground(params);
        return true;
    }

    void runBackground(JobParameters params){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String objectString = params.getExtras().getString("object");
                Gson gson = new Gson();
                FinancialInformation object = gson.fromJson(objectString,FinancialInformation.class);
//                Log.e("test job object: ","testy here: "+object.getReason()+" , "+object.getAmount()+", "+ object.getDurationType()+", "+Calendar.getInstance().getTime());
                addNewFinancialInfo(object);
                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }


    @Override
    public void addNewFinancialInfo(FinancialInformation info) {
        int currentId = 1;

        Cursor stringIdCursor = db.rawQuery("SELECT FIN_INFO_ID FROM FINANCIAL_INFORMATION ORDER BY FIN_INFO_ID DESC LIMIT 1", null);
        if(stringIdCursor.moveToFirst()) {
            currentId = stringIdCursor.getInt(0)+1;
        }
        ContentValues FinInfoContentValue = info.setContentValuesForPeriodJobSave(this);
        ContentValues FinDetailContentValue = info.getDetail().setDetailContentValues(currentId);

        db.insert("FINANCIAL_INFORMATION",null, FinInfoContentValue);
        db.insert("FINANCIAL_DETAIL",null,FinDetailContentValue);

    }
}
