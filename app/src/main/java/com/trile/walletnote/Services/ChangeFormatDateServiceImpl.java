package com.trile.walletnote.Services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChangeFormatDateServiceImpl implements ChangeFormatDateService{

    CustomDialog customDialog;
    SimpleDateFormat formatDateForSaving;
    SimpleDateFormat formatTimeForSaving;
    SimpleDateFormat formatForShowing;
    SimpleDateFormat formatDateTime;

    @SuppressLint("SimpleDateFormat")
    public ChangeFormatDateServiceImpl(Context context){
        customDialog = new CustomDialogImpl(context);
        formatDateForSaving = new SimpleDateFormat("yyyyMMdd");
        formatTimeForSaving = new SimpleDateFormat("HHmmss");
        formatForShowing = new SimpleDateFormat("dd/MM/yyyy");
        formatDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    }

    @SuppressLint("SimpleDateFormat")
    public ChangeFormatDateServiceImpl(){
        formatDateForSaving = new SimpleDateFormat("yyyyMMdd");
        formatTimeForSaving = new SimpleDateFormat("HHmmss");
        formatForShowing = new SimpleDateFormat("dd/MM/yyyy");
        formatDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    }

    @Override
    public String changeFormatDateForSaving(String date) {
        try {
            Date DateResult = formatForShowing.parse(date);
            String stringResult = formatDateForSaving.format(DateResult);

            return stringResult;
        }catch (Exception e){
            customDialog.warningDialog("ChangeFormatDateServiceImpl.ChangeFormatDateForSaving: "+e.toString());
            return "";
        }
    }

    @Override
    public String getCurrentDateForSaving() {
        String stringResult = formatDateForSaving.format(Calendar.getInstance().getTime());
        return stringResult;
    }

    @Override
    public String getCurrentTimeForSaving() {
        String stringResult = formatTimeForSaving.format(Calendar.getInstance().getTime());
        Log.e("time",stringResult);
        return stringResult;
    }

    @Override
    public String getCurrentDateForShowing() {
        String stringResult = formatForShowing.format(Calendar.getInstance().getTime());
        return stringResult;
    }

    @Override
    public String getCurrentDateTime() {
        String stringResult = formatDateTime.format(Calendar.getInstance().getTime());
        return stringResult;
    }

    @Override
    public String changeFormatDateForShowing(String date) {
        try {
            Date DateResult = formatDateForSaving.parse(date);
            String stringResult = formatForShowing.format(DateResult);

            return stringResult;
        }catch (Exception e){
            customDialog.warningDialog("ChangeFormatDateServiceImpl.ChangeFormatDateForShowing: "+e.toString());
            return "";
        }
    }

    @Override
    public String formatMonthYear(int month, int year) {
        if(month<10)
            return year+"0"+month;
        else
            return year+""+month;
    }
}
