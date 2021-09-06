package com.trile.walletnote.model;

import android.content.ContentValues;
import android.content.Context;

import com.trile.walletnote.Services.ChangeFormatDateService;
import com.trile.walletnote.Services.ChangeFormatDateServiceImpl;
import com.trile.walletnote.Services.HandleErrorService;
import com.trile.walletnote.Services.HandleErrorServiceImpl;
import com.trile.walletnote.sharePreferencces.ReasonPrefs;

import java.io.Serializable;

@SuppressWarnings("serial")
public class FinancialInformation implements Serializable, Cloneable {
    private int ID;
    private Boolean Type;
    private String ChosenDate;
    private int Amount;
    private String CreateDate;
    private String UpdateDate;
    private String Reason;
    private int DurationType;
    /*
     * 0 - default when var is not set
     * 1 - daily
     * 2 - weekly
     * 3 - monthly
     * */

    private boolean SelectValue;

    private FinancialDetail detail;

    public void setAmount(int amount) {
        Amount = amount;
    }

    public int getAmount() {
        return Amount;
    }

    public boolean isSelectValue() {
        return SelectValue;
    }

    public void setSelectValue(boolean selectValue) {
        SelectValue = selectValue;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Boolean getType() {
        return Type;
    }

    public void setType(Boolean type) {
        Type = type;
    }

    public String getChosenDate() {
        return ChosenDate;
    }

    public void setChosenDate(String chosenDate) {ChosenDate = chosenDate;}

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }

    public FinancialDetail getDetail() {
        return detail;
    }

    public void setDetail(FinancialDetail detail) {
        this.detail = detail;
    }

    public int getDurationType() {
        return DurationType;
    }

    public void setDurationType(int durationType) {
        DurationType = durationType;
    }

    public String getUpdateDate() {
        return UpdateDate;
    }

    public void setUpdateDate(String updateDate) {
        UpdateDate = updateDate;
    }

    public FinancialInformation(){
        this.detail = new FinancialDetail();
    }

    public FinancialInformation(int ID, Boolean type, String chosenDate, int amount, String createDate, String reason, int durationType , FinancialDetail detail) {
        this.ID = ID;
        Type = type;
        ChosenDate = chosenDate;
        Amount = amount;
        CreateDate = createDate;
        Reason = reason;
        DurationType = durationType;
        this.detail = detail;
    }

    public FinancialInformation(int ID, Boolean type, String chosenDate, int amount, String createDate, String reason, FinancialDetail detail) {
        this.ID = ID;
        Type = type;
        ChosenDate = chosenDate;
        Amount = amount;
        CreateDate = createDate;
        Reason = reason;
        this.detail = detail;
    }

    public FinancialInformation(int ID, Boolean type, String chosenDate, int amount, String reason, FinancialDetail detail) {
        this.ID = ID;
        Type = type;
        ChosenDate = chosenDate;
        Amount = amount;
        Reason = reason;
        this.detail = detail;
    }

    public FinancialInformation(Boolean type, String chosenDate, Integer amount,int durationType, FinancialDetail detail) {
        Type = type;
        ChosenDate = chosenDate;
        Amount = amount;
        DurationType = durationType;
        this.detail = detail;
    }

    public FinancialInformation(Boolean type, String chosenDate, Integer amount, String reason) {
        Type = type;
        ChosenDate = chosenDate;
        Amount = amount;
        Reason = reason;
    }

    public FinancialInformation(int id, Boolean type, String chosenDate, Integer amount, String reason) {
        ID = id;
        Type = type;
        ChosenDate = chosenDate;
        Amount = amount;
        Reason = reason;
    }

    public FinancialInformation(Boolean type, String chosenDate, Integer amount, String reason, FinancialDetail detail) {
        Type = type;
        ChosenDate = chosenDate;
        Amount = amount;
        Reason = reason;
        this.detail = detail;
    }

    public FinancialInformation(Boolean type, String chosenDate, Integer amount, FinancialDetail detail) {
        Type = type;
        ChosenDate = chosenDate;
        Amount = amount;
        this.detail = detail;
    }

    public String setInsertSqlForAutoJob(int reasonId){
        HandleErrorService handleErrorService = new HandleErrorServiceImpl();
        ChangeFormatDateService formatDateService = new ChangeFormatDateServiceImpl();

        try {
            String result = "INSERT INTO FINANCIAL_INFORMATION VALUES(null";

            if (this.Type) {
                result += ",1";
            } else {
                result += ",0";
            }

            result += ",'" +  formatDateService.getCurrentDateForSaving() + "','" + Amount + "'";

            this.CreateDate = formatDateService.getCurrentDateForSaving();
            result += ",'" + this.CreateDate + "'";


            result += ",'" + reasonId + "'";

            result += ");";
            return result;
        }
        catch (Exception e){
            handleErrorService.appendErrorTextForAutoJob("FinancialInformation.setInsertSqlForAutoJob: "+e.toString());
            return "";
        }
    }

    public ContentValues setContentValuesForSave(Context context){
        ChangeFormatDateService changeFormatDateService = new ChangeFormatDateServiceImpl(context);
        ReasonPrefs reasonPrefs = new ReasonPrefs(context);
        ContentValues contentValues = new ContentValues();

        contentValues.putNull("FIN_INFO_ID");

        if (this.getType())
            contentValues.put("FIN_INFO_TYPE",1);
        else
            contentValues.put("FIN_INFO_TYPE",0);

        contentValues.put("FIN_INFO_CHOSEN_DATE",changeFormatDateService.changeFormatDateForSaving(this.getChosenDate()));

        contentValues.put("FIN_INFO_AMOUNT" ,this.getAmount());

        contentValues.put("FIN_INFO_CREATE_DATE",changeFormatDateService.getCurrentDateForSaving());

        contentValues.put( "FIN_INFO_CREATE_TIME" ,changeFormatDateService.getCurrentTimeForSaving());

        contentValues.put("FIN_INFO_REASON",reasonPrefs.getReasonIdForSaving(this.getReason()));

        contentValues.put("FIN_INFO_UPDATE_DATE",changeFormatDateService.getCurrentDateForSaving());

        contentValues.put( "FIN_INFO_UPDATE_TIME" ,changeFormatDateService.getCurrentTimeForSaving());

        return contentValues;
    }

    public ContentValues setContentValuesForPeriodJobSave(Context context){
        ChangeFormatDateService changeFormatDateService = new ChangeFormatDateServiceImpl(context);
        ReasonPrefs reasonPrefs = new ReasonPrefs(context);
        ContentValues contentValues = new ContentValues();

        contentValues.putNull("FIN_INFO_ID");

        if (this.getType())
            contentValues.put("FIN_INFO_TYPE",1);
        else
            contentValues.put("FIN_INFO_TYPE",0);

        contentValues.put("FIN_INFO_CHOSEN_DATE",changeFormatDateService.getCurrentDateStringForPeriodJob());

        contentValues.put("FIN_INFO_AMOUNT" ,this.getAmount());

        contentValues.put("FIN_INFO_CREATE_DATE",changeFormatDateService.getCurrentDateForSaving());

        contentValues.put( "FIN_INFO_CREATE_TIME" ,changeFormatDateService.getCurrentTimeForSaving());

        contentValues.put("FIN_INFO_REASON",reasonPrefs.getReasonIdForSaving(this.getReason()));

        contentValues.put("FIN_INFO_UPDATE_DATE",changeFormatDateService.getCurrentDateForSaving());

        contentValues.put( "FIN_INFO_UPDATE_TIME" ,changeFormatDateService.getCurrentTimeForSaving());

        return contentValues;
    }

    public ContentValues setContentValuesForUpdate(Context context){
        ChangeFormatDateService changeFormatDateService = new ChangeFormatDateServiceImpl(context);
        ReasonPrefs reasonPrefs = new ReasonPrefs(context);
        ContentValues contentValues = new ContentValues();

        if (this.getType())
            contentValues.put("FIN_INFO_TYPE",1);
        else
            contentValues.put("FIN_INFO_TYPE",0);

        contentValues.put("FIN_INFO_CHOSEN_DATE",changeFormatDateService.changeFormatDateForSaving(this.getChosenDate()));

        contentValues.put("FIN_INFO_AMOUNT" ,this.getAmount());

        contentValues.put("FIN_INFO_REASON",reasonPrefs.getReasonIdForSaving(this.getReason()));

        contentValues.put("FIN_INFO_UPDATE_DATE",changeFormatDateService.getCurrentDateForSaving());

        contentValues.put( "FIN_INFO_UPDATE_TIME" ,changeFormatDateService.getCurrentTimeForSaving());

        return contentValues;
    }

}
