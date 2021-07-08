package com.trile.walletnote.DAOS;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.trile.walletnote.Services.ChangeFormatDateService;
import com.trile.walletnote.Services.ChangeFormatDateServiceImpl;
import com.trile.walletnote.Services.CustomDialog;
import com.trile.walletnote.Services.CustomDialogImpl;
import com.trile.walletnote.model.Database;
import com.trile.walletnote.model.FinancialDetail;
import com.trile.walletnote.model.FinancialInformation;

import java.util.ArrayList;

public class HomeDaoImpl implements HomeDao {

    SQLiteDatabase db;
    Database dbHelper;
    Context context;

    CustomDialog customDialog;
    ChangeFormatDateService changeFormatDateService;

    public  HomeDaoImpl (Context context){
        this.context = context;

        customDialog = new CustomDialogImpl(this.context);

        dbHelper = new Database(this.context);
        db = dbHelper.getDatabase();
        changeFormatDateService = new ChangeFormatDateServiceImpl();
    }

    @Override
    public int getGrandTotal() {

            Cursor grandTotalCursor;
            int result=0;
            String sql = "Select SUM(amount) as GRAND_TOTAL from (";
            sql += "select (-1) * FIN_INFO_AMOUNT as amount from FINANCIAL_INFORMATION where FIN_INFO_TYPE = 1";
            sql += "\n UNION ALL \n";
            sql += "select  FIN_INFO_AMOUNT as amount from FINANCIAL_INFORMATION where FIN_INFO_TYPE = 0";
            sql += ")";
            try {
                grandTotalCursor = db.rawQuery(sql,null);
                if(grandTotalCursor.moveToFirst()){
                    result = grandTotalCursor.getInt(grandTotalCursor.getColumnIndex("GRAND_TOTAL"));
                }
            }catch (Exception e){
                customDialog.warningDialog("HomeDaoImpl.getGrandTotal: "+e.toString());
            }
            return result;

    }

    @Override
    public ArrayList<FinancialInformation> getTopFive() {
        ArrayList<FinancialInformation> returnList = new ArrayList<>();
        String sql = "select i.FIN_INFO_ID, i.FIN_INFO_TYPE,i.FIN_INFO_CHOSEN_DATE,i.FIN_INFO_AMOUNT,i.FIN_INFO_REASON, d.FIN_DETAIL_CONTENT, d.FIN_DETAIL_IMAGE,d.FIN_DETAIL_ID " +
                "from FINANCIAL_INFORMATION i left join FINANCIAL_DETAIL d on (i.FIN_INFO_ID=d.FIN_DETAIL_FININFO_REF)" +
                "order by FIN_INFO_UPDATE_DATE||FIN_INFO_UPDATE_TIME DESC LIMIT 5;";
        try {
            Cursor infoData = db.rawQuery(sql,null);
            boolean typeVar = false;
            if(infoData!=null){
                while (infoData.moveToNext()){
                    if(infoData.getInt(1)==1)
                        typeVar = true;
                    else typeVar = false;
                    returnList.add(new FinancialInformation(infoData.getInt(0),typeVar,changeFormatDateService.changeFormatDateForShowing(infoData.getString(2)),infoData.getInt(3),infoData.getString(4),new FinancialDetail(infoData.getString(5),infoData.getBlob(6),infoData.getInt(7))));
                }
            }
        }catch (Exception e){
            customDialog.warningDialog("HomeDaoImpl.getTopFive: "+e.toString());
        }
        return returnList;
    }
}
