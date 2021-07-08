package com.trile.walletnote.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.trile.walletnote.Services.CustomDialog;
import com.trile.walletnote.Services.CustomDialogImpl;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "WalletNote.db";
    private static int DB_VERSION = 3;
    private static String DB_PATH = "";

    private String SQL_STATEMENT_FIN_INFO ="";
    private String SQL_STATEMENT_FIN_DETAIL ="";

    Context context;

    CustomDialog customDialog;

    public DatabaseHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
        this.context = context;
        customDialog = new CustomDialogImpl(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            try {
                setUpDbCreateStatement();
                db.execSQL(SQL_STATEMENT_FIN_INFO);
                db.execSQL(SQL_STATEMENT_FIN_DETAIL);
                Toast.makeText(context, "create success", Toast.LENGTH_SHORT);
            } catch (Exception e) {
                customDialog.warningDialog("DatabaseHelper.onCreate: " + e.toString());
            }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion){
            if(newVersion == 2){
                db.execSQL("ALTER TABLE FINANCIAL_INFORMATION ADD COLUMN 'FIN_INFO_UPDATE_DATE' TEXT DEFAULT '';");
            }
            if(newVersion==3 && oldVersion == 2){
                db.execSQL("ALTER TABLE FINANCIAL_INFORMATION ADD COLUMN 'FIN_INFO_CREATE_TIME' TEXT DEFAULT ''; ");
                db.execSQL("ALTER TABLE FINANCIAL_INFORMATION ADD COLUMN 'FIN_INFO_UPDATE_TIME' TEXT");
            }
            if(newVersion == 3 && oldVersion == 1){
                db.execSQL("ALTER TABLE FINANCIAL_INFORMATION ADD COLUMN 'FIN_INFO_UPDATE_DATE' TEXT DEFAULT '';");
                db.execSQL("ALTER TABLE FINANCIAL_INFORMATION ADD COLUMN 'FIN_INFO_CREATE_TIME' TEXT DEFAULT '';");
                db.execSQL("ALTER TABLE FINANCIAL_INFORMATION ADD COLUMN 'FIN_INFO_UPDATE_TIME' TEXT DEFAULT '';");
            }
        }

    }

    void setUpDbCreateStatement(){
        String FinancialDetailSql = "CREATE TABLE 'FINANCIAL_DETAIL' ("+
                "'FIN_DETAIL_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
                "'FIN_DETAIL_CONTENT' TEXT DEFAULT '',"+
                "'FIN_DETAIL_IMAGE' BLOB, "+
                "'FIN_DETAIL_FININFO_REF' INTEGER NOT NULL ,"+
                //add ON DELETE CASCADE to delete record when reference key is deleted
                "FOREIGN KEY('FIN_DETAIL_FININFO_REF') REFERENCES 'FINANCIAL_INFORMATION'('FIN_INFO_ID') ON DELETE CASCADE"+
                ");";
        String FinancialInformationSql = "";
        if(DB_VERSION == 1)
            FinancialInformationSql = "CREATE TABLE 'FINANCIAL_INFORMATION' ("+
                "'FIN_INFO_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
                "'FIN_INFO_TYPE' INTEGER NOT NULL,"+
                "'FIN_INFO_CHOSEN_DATE' TEXT NOT NULL,"+
                "'FIN_INFO_AMOUNT' REAL NOT NULL,"+
                "'FIN_INFO_CREATE_DATE' TEXT NOT NULL,"+
                "'FIN_INFO_REASON' INTEGER NOT NULL"+
                ");";
        else if(DB_VERSION == 2)
            FinancialInformationSql = "CREATE TABLE 'FINANCIAL_INFORMATION' ("+
                    "'FIN_INFO_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
                    "'FIN_INFO_TYPE' INTEGER NOT NULL,"+
                    "'FIN_INFO_CHOSEN_DATE' TEXT NOT NULL,"+
                    "'FIN_INFO_AMOUNT' REAL NOT NULL,"+
                    "'FIN_INFO_CREATE_DATE' TEXT NOT NULL,"+
                    "'FIN_INFO_REASON' INTEGER NOT NULL,"+
                    "'FIN_INFO_UPDATE_DATE' TEXT DEFAULT ''"+
                    ");";
        else if(DB_VERSION == 3)
            FinancialInformationSql = "CREATE TABLE 'FINANCIAL_INFORMATION' ("+
                    "'FIN_INFO_ID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
                    "'FIN_INFO_TYPE' INTEGER NOT NULL,"+
                    "'FIN_INFO_CHOSEN_DATE' TEXT NOT NULL,"+
                    "'FIN_INFO_AMOUNT' REAL NOT NULL,"+
                    "'FIN_INFO_CREATE_DATE' TEXT NOT NULL,"+
                    "'FIN_INFO_REASON' INTEGER NOT NULL,"+
                    "'FIN_INFO_UPDATE_DATE' TEXT DEFAULT '',"+
                    "'FIN_INFO_CREATE_TIME' TEXT DEFAULT '',"+
                    "'FIN_INFO_UPDATE_TIME' TEXT DEFAULT ''"+
                    ");";

        SQL_STATEMENT_FIN_INFO = FinancialInformationSql ;
        SQL_STATEMENT_FIN_DETAIL = FinancialDetailSql;
    }

}
