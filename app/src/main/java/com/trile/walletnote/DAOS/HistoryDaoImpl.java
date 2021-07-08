package com.trile.walletnote.DAOS;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.trile.walletnote.R;
import com.trile.walletnote.Services.ChangeFormatDateService;
import com.trile.walletnote.Services.ChangeFormatDateServiceImpl;
import com.trile.walletnote.Services.CustomDialog;
import com.trile.walletnote.Services.CustomDialogImpl;
import com.trile.walletnote.model.Database;
import com.trile.walletnote.model.FinancialDetail;
import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.model.ReturnData;

import java.util.ArrayList;

public class HistoryDaoImpl implements HistoryDao{
    CustomDialog customDialog;
    SQLiteDatabase db;
    Database dbHelper;
    ChangeFormatDateService changeFormatDateService;
    Context context;

    public HistoryDaoImpl(Context context){
        this.context = context;
        dbHelper = new Database(context);
        db = dbHelper.getDatabase();
        customDialog = new CustomDialogImpl(context);
        changeFormatDateService = new ChangeFormatDateServiceImpl();
    }

    @Override
    public ArrayList<FinancialInformation> getListNormalAll(int month, int year, boolean filterMode, String contentForSearch, boolean searchMode) {
        ArrayList<FinancialInformation> returnList = new ArrayList<>();
        String sql = "select i.FIN_INFO_ID, i.FIN_INFO_TYPE,i.FIN_INFO_CHOSEN_DATE,i.FIN_INFO_AMOUNT,i.FIN_INFO_REASON, d.FIN_DETAIL_CONTENT, d.FIN_DETAIL_IMAGE, d.FIN_DETAIL_ID " +
                "from FINANCIAL_INFORMATION i left join FINANCIAL_DETAIL d on (i.FIN_INFO_ID=d.FIN_DETAIL_FININFO_REF) "+
                "where 1=1 ";
        if(filterMode)
            sql += "and substr(i.FIN_INFO_CHOSEN_DATE,0,7) = '"+ changeFormatDateService.formatMonthYear(month,year)+"' ";

        if(searchMode)
            sql += "and d.FIN_DETAIL_CONTENT LIKE '%"+contentForSearch+"%' ";

        sql += "order by FIN_INFO_CHOSEN_DATE DESC, FIN_INFO_ID DESC ;";

        Log.i("getListNormalAll:sql ",sql);

        try {
            Cursor infoData = db.rawQuery(sql,null);
            boolean typeVar = false;
            if(infoData!=null){
                while (infoData.moveToNext()){
                    if(infoData.getInt(1)==1)
                        typeVar = true;
                    else typeVar = false;
                    returnList.add(new FinancialInformation(infoData.getInt(0),typeVar,changeFormatDateService.changeFormatDateForShowing(infoData.getString(2)),infoData.getInt(3),infoData.getString(4),new FinancialDetail(infoData.getString(5), infoData.getBlob(6),infoData.getInt(7))));
                }
            }
        }catch (Exception e){
            customDialog.warningDialog("HistoryDaoImpl.getListNormalAll: "+e.toString());
        }
        return returnList;
    }

    @Override
    public ArrayList<FinancialInformation> getListNormalIncoming(int month, int year, boolean filterMode, String contentForSearch, boolean searchMode) {
        ArrayList<FinancialInformation> returnList = new ArrayList<>();
        String sql = "select i.FIN_INFO_ID, i.FIN_INFO_TYPE,i.FIN_INFO_CHOSEN_DATE,i.FIN_INFO_AMOUNT,i.FIN_INFO_REASON, d.FIN_DETAIL_CONTENT, d.FIN_DETAIL_IMAGE,d.FIN_DETAIL_ID " +
                "from FINANCIAL_INFORMATION i left join FINANCIAL_DETAIL d on (i.FIN_INFO_ID=d.FIN_DETAIL_FININFO_REF) " +
                "where i.FIN_INFO_TYPE = 0 ";
        if(filterMode)
            sql += "and substr(i.FIN_INFO_CHOSEN_DATE,0,7) = '"+ changeFormatDateService.formatMonthYear(month,year)+"' ";

        if(searchMode)
            sql += "and d.FIN_DETAIL_CONTENT LIKE '%"+contentForSearch+"%' ";

        sql+="order by FIN_INFO_CHOSEN_DATE DESC, FIN_INFO_ID DESC ;";

        Log.i("getListNormalIncoming:sql ",sql);
        try {
            Cursor infoData = db.rawQuery(sql,null);
            if(infoData!=null){
                while (infoData.moveToNext()){
                    returnList.add(new FinancialInformation(infoData.getInt(0),false,changeFormatDateService.changeFormatDateForShowing(infoData.getString(2)),infoData.getInt(3),infoData.getString(4),new FinancialDetail(infoData.getString(5), infoData.getBlob(6),infoData.getInt(7))));
                }
            }
        }catch (Exception e){
            customDialog.warningDialog("HistoryDaoImpl.getListNormalIncoming: "+e.toString());
        }
        return returnList;
    }

    @Override
    public ArrayList<FinancialInformation> getListNormalOutgoing(int month, int year, boolean filterMode, String contentForSearch, boolean searchMode) {
        ArrayList<FinancialInformation> returnList = new ArrayList<>();
        String sql = "select i.FIN_INFO_ID, i.FIN_INFO_TYPE,i.FIN_INFO_CHOSEN_DATE,i.FIN_INFO_AMOUNT,i.FIN_INFO_REASON, d.FIN_DETAIL_CONTENT, d.FIN_DETAIL_IMAGE,d.FIN_DETAIL_ID " +
                "from FINANCIAL_INFORMATION i left join FINANCIAL_DETAIL d on (i.FIN_INFO_ID=d.FIN_DETAIL_FININFO_REF) " +
                "where i.FIN_INFO_TYPE = 1 ";
        if(filterMode)
            sql += "and substr(i.FIN_INFO_CHOSEN_DATE,0,7) = '"+ changeFormatDateService.formatMonthYear(month,year)+"' ";

        if(searchMode)
            sql += "and d.FIN_DETAIL_CONTENT LIKE '%"+contentForSearch+"%' ";

        sql += "order by FIN_INFO_CHOSEN_DATE DESC, FIN_INFO_ID DESC ;";

        Log.i("getListNormalOutgoing:sql ",sql);

        try {
            Cursor infoData = db.rawQuery(sql,null);
            if(infoData!=null){
                while (infoData.moveToNext()){
                    returnList.add(new FinancialInformation(infoData.getInt(0),true,changeFormatDateService.changeFormatDateForShowing(infoData.getString(2)),infoData.getInt(3),infoData.getString(4),new FinancialDetail(infoData.getString(5), infoData.getBlob(6), infoData.getInt(7))));
                }
            }
        }catch (Exception e){
            customDialog.warningDialog("HistoryDaoImpl.getListNormalOutgoing: "+e.toString());
        }
        return returnList;
    }

    @Override
    public int getTotal(String stringOfListId) {
        String sql = "select sum( "+
                " case when FIN_INFO_TYPE = 1 then (-1) * FIN_INFO_AMOUNT "+
                " else FIN_INFO_AMOUNT end "+
                ") as Total "+
                "from FINANCIAL_INFORMATION "+
                "where FIN_INFO_ID in (" +stringOfListId+ ")";

        try {
            Cursor sumCursor = db.rawQuery(sql,null);
            if(sumCursor!=null){
                if(sumCursor.moveToFirst()){
                    int result = sumCursor.getInt(sumCursor.getColumnIndex("Total"));
                    return result;
                }
            }
        }catch (Exception e){
            customDialog.warningDialog("HistoryDaoImpl.getTotal: "+e.toString());
        }

        return 0;
    }

    @Override
    public ReturnData deleteFinancialInformations(String listId) {
        ReturnData returnData = new ReturnData();
Log.e("list",listId);
        int deleteResult = db.delete("FINANCIAL_INFORMATION", "FIN_INFO_ID in ("+listId+")", null);

        if(deleteResult == 0){
            returnData.setResult(2);
            returnData.setMessage(context.getString(R.string.delete_fail));
        }else{
            returnData.setResult(1);
            returnData.setMessage(context.getString(R.string.history_delete_successfully_message,deleteResult));
        }

        return returnData;
    }



}
