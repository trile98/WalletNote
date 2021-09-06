package com.trile.walletnote.sharePreferencces;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class CurrentStatusPrefs {
    private static String PREF_NAME ="CURRENT_STATUS_PREF";

    private static String KEY_PREFIX ="CURRENT_STATUS_";


    private static SharedPreferences sharedPreferences;

    private static SharedPreferences.Editor editor;

    private static String KEY_ADD_NEW_TYPE ="_ADD_NEW_TYPE";
    private static String KEY_DURATION_TYPE ="_DURATION_TYPE";

    private static String KEY_FINANCIAL_TYPE_FOR_HISTORY ="_FINANCIAL_TYPE_FOR_HISTORY";
    private static String KEY_DURATION_TYPE_FOR_HISTORY ="_DURATION_TYPE_FOR_HISTORY";

    private static final String KEY_MAX_ID_INCOME = "PERIOD_FIN_MAX_ID_INCOME";
    private static final String KEY_MAX_ID_OUTGO = "PERIOD_FIN_MAX_ID_OUTGO";

    private static final String KEY_CHECK_MASTER_SERVICE = "CHECK_MASTER_SERVICE_RUN";

    private Context context;


    public CurrentStatusPrefs(Context context){
        this.context = context;
        if(sharedPreferences==null){
            sharedPreferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        }
        editor = sharedPreferences.edit();
    }

    String getKeyField(String field){
        return KEY_PREFIX+ field;
    }

    public int setAddNewStatus(Boolean addNewType, String durationType){
        try {
            editor.putBoolean(getKeyField(KEY_ADD_NEW_TYPE), addNewType);
            editor.putString(getKeyField(KEY_DURATION_TYPE), durationType);
            editor.apply();
            return 1;
        }
        catch (Exception e){
            Toast.makeText(this.context,"CurrentStatusPrefs.setAddNewStatus: "+e.toString(),Toast.LENGTH_LONG);
        }
        return 0;
    }

    public String getDurationStatus(){
        String durationType = sharedPreferences.getString(getKeyField(KEY_DURATION_TYPE),"");
        return  durationType;
    }

    public boolean getAddNewStatus(){
        boolean addNewType = sharedPreferences.getBoolean(getKeyField(KEY_ADD_NEW_TYPE),true);
        return addNewType;
    }

    public void setStatusForShowingHistory(int financialType, int durationType){
        try {
            editor.putInt(getKeyField(KEY_FINANCIAL_TYPE_FOR_HISTORY), financialType);
            editor.putInt(getKeyField(KEY_DURATION_TYPE_FOR_HISTORY), durationType);
            editor.apply();
        }
        catch (Exception e){
            Toast.makeText(this.context,"CurrentStatusPrefs.setStatusForShowingHistory: "+e.toString(),Toast.LENGTH_LONG);
        }
    }

    public int getFinancialTypeForHistory(){
        int finType = sharedPreferences.getInt(getKeyField(KEY_FINANCIAL_TYPE_FOR_HISTORY),1);
        return finType;
    }

    public int getDurationTypeForHistory(){
        int durationType = sharedPreferences.getInt(getKeyField(KEY_DURATION_TYPE_FOR_HISTORY),1);
        return durationType;
    }

    public int getMaxIdIncome(){
        return sharedPreferences.getInt(getKeyField(KEY_MAX_ID_INCOME),1);
    }

    public int getMaxIdOutgo(){
        return sharedPreferences.getInt(getKeyField(KEY_MAX_ID_OUTGO),1);
    }

    public void setMaxIdIncome(int i){
        editor.putInt(getKeyField(KEY_MAX_ID_INCOME),i);
        editor.apply();
    }

    public void setMaxIdOutgo(int i){
        editor.putInt(getKeyField(KEY_MAX_ID_OUTGO),i);
        editor.apply();
    }

    public boolean getCheckMasterServiceRun(){ return sharedPreferences.getBoolean(getKeyField(KEY_CHECK_MASTER_SERVICE),false);}

    public void setCheckMasterServiceRun(boolean check) {
        editor.putBoolean(getKeyField(KEY_CHECK_MASTER_SERVICE),check);
        editor.apply();
    }
}
