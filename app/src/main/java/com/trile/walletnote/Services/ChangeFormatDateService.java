package com.trile.walletnote.Services;

public interface ChangeFormatDateService {

    String changeFormatDateForSaving(String date);
    String getCurrentDateForSaving();
    String getCurrentDateStringForPeriodJob();
    String getCurrentTimeForSaving();
    String getCurrentDateForShowing();
    String getCurrentDateTime();
    String changeFormatDateForShowing(String date);
    String formatMonthYear(int month, int year);
    int calculateDaysToCurrentDate(String fromDate);
}
