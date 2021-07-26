package com.trile.walletnote.DAOS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.trile.walletnote.R;
import com.trile.walletnote.Services.ChangeFormatDateService;
import com.trile.walletnote.Services.ChangeFormatDateServiceImpl;
import com.trile.walletnote.Services.CustomDialog;
import com.trile.walletnote.Services.CustomDialogImpl;
import com.trile.walletnote.model.Database;
import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.model.ReturnData;
import com.trile.walletnote.sharePreferencces.PeriodFinancialInformationPrefs;
import com.trile.walletnote.sharePreferencces.ReasonPrefs;

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

            Cursor stringIdCursor = db.rawQuery("SELECT seq from sqlite_sequence where name = 'FINANCIAL_INFORMATION'", null);
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

    //FinancialInformation objectForService = new FinancialInformation();
    int currentDurationTime = 0;
    int currentId = 0;
    String currentChosenDate = "";

    @Override
    public ReturnData savePeriodFinInfo(ArrayList<FinancialInformation> list, boolean addNewType) {
        ReturnData returnData = new ReturnData();
        try {
            for (FinancialInformation item : list) {

                returnData = periodFinancialInformationPrefs.addNewPeriodInformation(item, addNewType);
                if(returnData.getResult()==1) {
                    currentDurationTime = item.getDurationType();
                    currentChosenDate = item.getChosenDate();
                    currentId = Integer.parseInt(returnData.getMessage());
                }
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



}
