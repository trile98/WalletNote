package com.trile.walletnote.model;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import java.io.Serializable;

@SuppressWarnings("serial")
public class FinancialDetail implements Serializable {

    private int FinDetId;
    private String FinDetContent;
    private byte[] FinDetImage;
    private int FinDetFinInfRefId;

    public int getFinDetId() {
        return FinDetId;
    }

    public void setFinDetId(int finDetId) {
        FinDetId = finDetId;
    }

    public int getFinDetFinInfRefId() {
        return FinDetFinInfRefId;
    }

    public void setFinDetFinInfRefId(int finDetFinInfRefId) {
        FinDetFinInfRefId = finDetFinInfRefId;
    }

    public String getFinDetContent() {
        return FinDetContent;
    }

    public void setFinDetContent(String FinDetContent) {
        this.FinDetContent = FinDetContent;
    }

    public byte[] getFinDetImage() {
        return FinDetImage;
    }

    public void setFinDetImage(byte[] FinDetImage) {
        this.FinDetImage = FinDetImage;
    }

    public FinancialDetail(String FinDetContent, byte[] FinDetImage) {
        this.FinDetContent = FinDetContent;
        this.FinDetImage = FinDetImage;
    }

    public FinancialDetail( String FinDetContent, byte[] FinDetImage,int FinDetId) {
        this.FinDetId = FinDetId;
        this.FinDetContent = FinDetContent;
        this.FinDetImage = FinDetImage;
    }

    public FinancialDetail(int id, String FinDetContent, int refId){
        this.FinDetId = id;
        this.FinDetContent = FinDetContent;
        this.FinDetFinInfRefId = refId;
    }

    public FinancialDetail(String FinDetContent, int refId){
        this.FinDetContent = FinDetContent;
        this.FinDetFinInfRefId = refId;
    }

    public FinancialDetail(byte[] FinDetImage, int refId){
        this.FinDetImage = FinDetImage;
        this.FinDetFinInfRefId = refId;
    }

    public FinancialDetail(){

    }

    public FinancialDetail(String content){
        this.FinDetContent = content;
    }

//    public String setItemSql(int infoId){
//        String result = "INSERT into FINANCIAL_DETAIL values(null";
//        if(this.FinDetContent.isEmpty()){
//            result += ",null";
//        }
//        else{
//            result += ",'"+this.FinDetContent+"'";
//        }
////        if(this.FinDetImage == null)
////            result += ",null";
////        else
//            result += ",?";
//
//        result += ","+infoId;
//
//        result += ");";
//
//        return result;
//
//    }

    public ContentValues setDetailContentValues(int infoId){
        ContentValues contentValues = new ContentValues();

        contentValues.putNull("FIN_DETAIL_ID");
        if(this.FinDetContent.isEmpty()){
            contentValues.putNull("FIN_DETAIL_CONTENT");
        }
        else{
            contentValues.put("FIN_DETAIL_CONTENT",this.FinDetContent);
        }

        if(this.FinDetImage == null) {
            Log.e("detail", "null");
            contentValues.putNull("FIN_DETAIL_IMAGE");
        }
        else {
            Log.e("detail", "not null");
            contentValues.put("FIN_DETAIL_IMAGE", this.FinDetImage);
        }

        contentValues.put("FIN_DETAIL_FININFO_REF", infoId);

        return contentValues;
    }

    public ContentValues setDetailContentValuesForUpdate(){
        ContentValues contentValues = new ContentValues();

        contentValues.put("FIN_DETAIL_ID",this.getFinDetId());

        if(TextUtils.isEmpty(this.FinDetContent)||this.FinDetContent.equals("")){
            contentValues.put("FIN_DETAIL_CONTENT","");
        }
        else{
            contentValues.put("FIN_DETAIL_CONTENT",this.FinDetContent);
        }

        if(this.FinDetImage == null) {
            contentValues.putNull("FIN_DETAIL_IMAGE");
        }
        else {
            contentValues.put("FIN_DETAIL_IMAGE", this.FinDetImage);
        }


        return contentValues;
    }


}
