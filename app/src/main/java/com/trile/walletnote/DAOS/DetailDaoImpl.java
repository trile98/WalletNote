package com.trile.walletnote.DAOS;

import android.content.ContentValues;
import android.content.Context;
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

public class DetailDaoImpl implements DetailDao{
    CustomDialog customDialog;
    SQLiteDatabase db;
    Database dbHelper;
    ChangeFormatDateService changeFormatDateService;
    ReasonPrefs reasonPrefs;
    PeriodFinancialInformationPrefs periodFinancialInformationPrefs;

    Context context;

    public DetailDaoImpl(Context context){
        this.context = context;
        dbHelper = new Database(context);
        db = dbHelper.getDatabase();
        customDialog = new CustomDialogImpl(context);
        changeFormatDateService = new ChangeFormatDateServiceImpl(context);
        reasonPrefs = new ReasonPrefs(context);
        periodFinancialInformationPrefs = new PeriodFinancialInformationPrefs(context);
    }


    @Override
    public ReturnData updateFinancialInformation(FinancialInformation info) {
        ReturnData returnData = new ReturnData();
        try {

            ContentValues infoContentValue = info.setContentValuesForUpdate(context);

            int infoResult = db.update("FINANCIAL_INFORMATION",infoContentValue,"FIN_INFO_ID = ?", new String[]{String.valueOf(info.getID())});

            ContentValues detailContentValue = info.getDetail().setDetailContentValuesForUpdate();
            int detailResult = db.update("FINANCIAL_DETAIL", detailContentValue, "FIN_DETAIL_ID = ? ", new String[]{String.valueOf(info.getDetail().getFinDetId())});

            if(detailResult != 0 && infoResult !=0) {
                returnData.setResult(1);
                returnData.setMessage("");
            }else{
                returnData.setResult(2);
                returnData.setMessage(context.getString(R.string.update_fail));
            }

        }catch (Exception e){
            returnData.setResult(2);
            returnData.setMessage("DetailDaoImpl.updateFinancialInformation: "+e.toString());
        }

        return  returnData;
    }

    @Override
    public ReturnData deleteFinancialInformation(int id) {
        ReturnData returnData = new ReturnData();
        try {

            int result = db.delete("FINANCIAL_INFORMATION", "FIN_INFO_ID = ?", new String[]{ String.valueOf(id) } );

            if (result != 0) {
                returnData.setMessage("");
                returnData.setResult(1);
            } else {
                returnData.setResult(2);
                returnData.setMessage(context.getString(R.string.delete_fail));
            }
        }catch (Exception e){
            returnData.setResult(2);
            returnData.setMessage("DetailDaoImpl.deleteFinancialInformation: "+e.toString());
        }

        return returnData;
    }
}
