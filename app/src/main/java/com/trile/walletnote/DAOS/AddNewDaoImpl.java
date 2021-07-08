package com.trile.walletnote.DAOS;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.PersistableBundle;

import com.trile.walletnote.R;
import com.trile.walletnote.Services.ChangeFormatDateService;
import com.trile.walletnote.Services.ChangeFormatDateServiceImpl;
import com.trile.walletnote.Services.CustomDialog;
import com.trile.walletnote.Services.CustomDialogImpl;
import com.trile.walletnote.Services.DurationFinAutoJobServiceImpl;
import com.trile.walletnote.model.Database;
import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.model.ReturnData;
import com.trile.walletnote.sharePreferencces.PeriodFinancialInformationPrefs;
import com.trile.walletnote.sharePreferencces.ReasonPrefs;
import com.google.gson.Gson;

import java.util.ArrayList;

public class AddNewDaoImpl implements AddNewDao {

    Context context;

    CustomDialog customDialog;
    SQLiteDatabase db;
    Database dbHelper;
    ChangeFormatDateService changeFormatDateService;

    ReasonPrefs reasonPrefs;
    PeriodFinancialInformationPrefs periodFinancialInformationPrefs;

    public AddNewDaoImpl(Context context){
        this.context = context;
        dbHelper = new Database(context);
        db = dbHelper.getDatabase();
        customDialog = new CustomDialogImpl(context);
        changeFormatDateService = new ChangeFormatDateServiceImpl();
        reasonPrefs = new ReasonPrefs(context);
        periodFinancialInformationPrefs = new PeriodFinancialInformationPrefs(context);
    }


    @Override
    public ReturnData saveNormalFinInfo(ArrayList<FinancialInformation> list) {
        ReturnData returnData = new ReturnData();
        try {
            int currentId = 1;

            Cursor stringIdCursor = db.rawQuery("SELECT FIN_INFO_ID FROM FINANCIAL_INFORMATION ORDER BY FIN_INFO_ID DESC LIMIT 1", null);
            if (stringIdCursor.moveToFirst()) {
                currentId = stringIdCursor.getInt(0) + 1;
            }

            ContentValues FinInfoContentValue = new ContentValues();
            ContentValues FinDetailContentValue = new ContentValues();

            int insertResult = 0;
            long checkFinInfoInsert = 0L , checkFinDetailInsert = 0L;

            for (FinancialInformation item : list) {
                FinInfoContentValue = item.setContentValuesForSave(context);
                FinDetailContentValue = item.getDetail().setDetailContentValues(currentId);

                checkFinInfoInsert = db.insert("FINANCIAL_INFORMATION",null,FinInfoContentValue);

                checkFinDetailInsert = db.insert("FINANCIAL_DETAIL", null, FinDetailContentValue);

                if(checkFinInfoInsert!= -1 && checkFinDetailInsert != -1){
                    insertResult++;
                }else{
                    returnData.setResult(2);
                    returnData.setMessage(context.getString(R.string.save_fail_temporature,insertResult));

                    return returnData;
                }

                currentId++;

            }
            returnData.setResult(1);
            returnData.setMessage("");
            return returnData;
        }catch (Exception e){
            returnData.setResult(2);
            returnData.setMessage("AddNewDaoImpl.saveNormalFinInfo: "+e.toString());
            return returnData;
        }
    }

    FinancialInformation objectForService = new FinancialInformation();
    int currentDurationTime = 0;
    int currentId = 0;

    @Override
    public ReturnData savePeriodFinInfo(ArrayList<FinancialInformation> list, boolean addNewType) {
        ReturnData returnData = new ReturnData();
        try {
            for (FinancialInformation item : list) {

                periodFinancialInformationPrefs.addNewPeriodInformation(item, addNewType);
                objectForService = item;
                currentDurationTime = item.getDurationType();
                currentId = periodFinancialInformationPrefs.getCurrentId(addNewType);
                periodFinancialInformationPrefs.setJobId(currentId,addNewType);
                StartAutoJob();
            }
            returnData.setResult(1);
            returnData.setMessage("");
            return returnData;
        }catch (Exception e){
            returnData.setResult(2);
            returnData.setMessage("AddNewDaoImpl.savePeriodFinInfo: "+e.toString());
            return returnData;
        }
    }

    @Override
    public void StartAutoJob() {
        String objectString = getJsonObject(objectForService);

        long TimePeriod = 60L*1000L;

        if(currentDurationTime == 1)//daily
//            TimePeriod = TimePeriod*(60*24);
            TimePeriod *= 15L;
        else if(currentDurationTime == 2)//weekly
//            TimePeriod = TimePeriod * (60*24*7);
            TimePeriod *= 20L;
        else
//            TimePeriod = TimePeriod * (60*24*30);//monthly
            TimePeriod *= 25L;

//        Log.i("TimePeriod",TimePeriod+"");
//
        int delayTime = handleDelayTime();
//
        if(delayTime == 1) {
            ComponentName componentName = new ComponentName(context, DurationFinAutoJobServiceImpl.class);

            PersistableBundle bundle = new PersistableBundle();
            bundle.putString("object",objectString);

            JobInfo info = new JobInfo.Builder(Integer.parseInt(context.getString(R.string.duration_auto_job_id_prefix)+""+currentId),componentName)
                    .setRequiresCharging(false)
                    .setPersisted(true)
//                .setMinimumLatency(60*1000)
                    .setPeriodic(TimePeriod,JobInfo.getMinFlexMillis()) //flexmillis: wait for next job, minmillis: 5'
                    .setExtras(bundle)
                    .build();

            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            scheduler.schedule(info);
        }else{

            Handler handler = new Handler();
            long finalTimePeriod = TimePeriod;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ComponentName componentName = new ComponentName(context, DurationFinAutoJobServiceImpl.class);

                    PersistableBundle bundle = new PersistableBundle();
                    bundle.putString("object",objectString);

                    JobInfo info = new JobInfo.Builder(Integer.parseInt(context.getString(R.string.duration_auto_job_id_prefix)+""+currentId),componentName)
                            .setRequiresCharging(false)
                            .setPersisted(true)
//                .setMinimumLatency(60*1000)
                            .setPeriodic(finalTimePeriod,JobInfo.getMinFlexMillis()) //flexmillis: wait for next job, minmillis: 5'
                            .setExtras(bundle)
                            .build();

                    JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                    scheduler.schedule(info);
                }
            },delayTime*24*60*60*1000);
        }

    }

    private int handleDelayTime(){
        int currentDate = Integer.parseInt(changeFormatDateService.getCurrentDateForSaving());
        int chosenDateInt = Integer.parseInt(changeFormatDateService.changeFormatDateForSaving(objectForService.getChosenDate()));

        int differentDate;

        if(chosenDateInt == currentDate){
            differentDate = 1;
        }else{
            differentDate = currentDate - chosenDateInt;

            if(differentDate<0)
                differentDate *= -1;
        }

        return  differentDate;
    }


    private String getJsonObject(FinancialInformation info){
        Gson gson = new Gson();
        String result = "";
        result = gson.toJson(info);
        return  result;
    }

    String generateSqlStatement(FinancialInformation info){
        String result = "INSERT INTO FINANCIAL_INFORMATION VALUES(null";

        if (info.getType()) {
            result += ",1";
        } else {
            result += ",0";
        }

        result += ",'" + changeFormatDateService.changeFormatDateForSaving(info.getChosenDate());
        result += "','" + info.getAmount() + "'";

        result += ",'" + changeFormatDateService.getCurrentDateForSaving()+ "'";


        result += ",'" + reasonPrefs.getReasonIdForSaving(info.getReason()) + "'";

        result += "," + changeFormatDateService.getCurrentDateForSaving()+ "', '"+changeFormatDateService.getCurrentTimeForSaving()+"', '"+changeFormatDateService.getCurrentTimeForSaving()+"'";//update date = create date in order to sort when select

        result += ");";
        return result;
    }
}
