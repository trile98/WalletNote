package com.trile.walletnote.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.trile.walletnote.Services.CustomDialog;
import com.trile.walletnote.Services.CustomDialogImpl;

import java.io.File;

public class Database {

    private static String DB_NAME = "WalletNote.db";
    private static int DB_VERSION = 1;
    private static String DB_PATH = "";

    private String SQL_STATEMENT_FOR_FIRST_TIME ="";

    private SQLiteDatabase database;
    private Context mContext;

    private DatabaseHelper helper;

    CustomDialog customDialog;


    public Database(Context context){
        mContext = context;
        helper = new DatabaseHelper(mContext);
        customDialog = new CustomDialogImpl(mContext);
        if(Build.VERSION.SDK_INT>=17)
            DB_PATH = context.getApplicationInfo().dataDir + "/";
        else
            DB_PATH = "data/data/"+ context.getPackageName() + "/databases/";

        try{
            openDB();
        }
        catch (Exception e){
            customDialog.warningDialog("Database.Database: "+e.toString());
        }

//        else{
//            getExistDB();
//        }
    }

    public SQLiteDatabase getDatabase(){return database;}

    public Boolean checkDbExist(){
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }


    void openDB(){
        database = helper.getWritableDatabase();
    }

   /* public void getExistDB(){
        this.getWritableDatabase();

        try {
            copyDB();
        }

        catch (IOException e) {e.printStackTrace();}
    }

    public void copyDB() throws IOException {
        AssetManager dirPATH = mContext.getAssets();
        InputStream myInput = dirPATH.open(DB_NAME);
        String outFileName = DB_PATH+DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;

        while ((length = myInput.read(buffer))>0)
        {
            myOutput.write(buffer,0,length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }*/
}
