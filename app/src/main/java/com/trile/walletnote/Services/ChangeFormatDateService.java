package com.trile.walletnote.Services;

public interface ChangeFormatDateService {

    String changeFormatDateForSaving(String date);
    String getCurrentDateForSaving();
    String getCurrentTimeForSaving();
    String getCurrentDateForShowing();
    String getCurrentDateTime();
    String changeFormatDateForShowing(String date);
    String formatMonthYear(int month, int year);
}
