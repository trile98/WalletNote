package com.trile.walletnote.Services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class ChangeFormatDateServiceImpl implements ChangeFormatDateService{

    CustomDialog customDialog;
    SimpleDateFormat formatDateForSaving;
    SimpleDateFormat formatTimeForSaving;
    SimpleDateFormat formatForShowing;
    SimpleDateFormat formatDateTime;

    private static final ArrayList<Integer> thirtyDayMonth = new ArrayList<>(Arrays.asList(4,6,9,11));
    private static final ArrayList<Integer> thirtyOneDayMonth = new ArrayList<>(Arrays.asList(1,3,5,7,8,10, 12 ));

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
    public String getCurrentDateStringForPeriodJob() {
        Calendar cal = Calendar.getInstance();

        if(cal.get(Calendar.DAY_OF_MONTH)==1){
            //if current month is 1, cal will go back 1 year automatically
            cal.add(Calendar.MONTH,-1);
        }

        cal.add(Calendar.DAY_OF_MONTH,-1);

        String stringResult = formatDateForSaving.format(cal.getTime());
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

    @Override
    public int calculateDaysToCurrentDate(String fromDate) {
        int date = Integer.parseInt(fromDate.substring(6,8));
        int month = Integer.parseInt(fromDate.substring(4,6));
        int year = Integer.parseInt(fromDate.substring(0,4));

        String toDate = getCurrentDateStringForPeriodJob();

        int cDate = Integer.parseInt(toDate.substring(6,8));
        int cMonth = Integer.parseInt(toDate.substring(4,6));
        int cYear = Integer.parseInt(toDate.substring(0,4));

        int result = 0;

        if(year == cYear){
            if(month == cMonth){
                result += (cDate - date);
            }else{
                result += cDate;

                result += getDaysOfMonth(month, year) - date;

                for(int i = month + 1; i< cMonth; i++){
                    result += getDaysOfMonth(i,year);
                }

            }
        }else{
            result += cDate;

            result += getDaysOfMonth(month, year) - date;

            for(int i = month +1; i<=12; i++){
                result += getDaysOfMonth(i, year);
            }

            for(int j = 1; j < cMonth; j++)
                result += getDaysOfMonth(j,cYear);

            for(int x = year +1; x<cYear; x++){
                if(x % 4 == 0)
                    result += 366;
                else
                    result += 365;
            }
        }

        return result;
    }


    private int getDaysOfMonth(int month, int year) {
        if (thirtyDayMonth.contains(month))
            return 30;
        else if (thirtyOneDayMonth.contains(month))
            return 31;
        else {
            if (year % 4 == 0)
                return 29;
            else
                return 28;
        }
    }

}
