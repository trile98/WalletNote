package com.trile.walletnote.sharePreferencces;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;


/*
 * 0 - don't have duration (normal insert)
 * 1 - daily
 * 2 - monthly
 * */

public class DurationPrefs {
    private static String PREF_NAME ="DURATION_PREF";

    private static String KEY_PREFIX ="DURATION_";

    private static String KEY_VIET_SUFFIX ="_VIET";
    private static String KEY_ENG_SUFFIX ="_ENG";

    private static SharedPreferences sharedPreferences;

    private static SharedPreferences.Editor editor;

    private static String KEY_MAX_ID ="MAX_ID";

    private static String KEY_CONTENT ="_CONTENT";
    private static String KEY_ID ="_ID";
    private static String KEY_NOTE ="_NOTE";

    private String suffix = KEY_VIET_SUFFIX;

    private Context context;

    public DurationPrefs(Context context){
        this.context = context;
        if(sharedPreferences==null){
            sharedPreferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        }

        editor = sharedPreferences.edit();

        if(!sharedPreferences.contains("initialized"+suffix)){
            editor.putBoolean("initalized"+suffix,true);
            initializeDurationFref();
        }
    }

    String getKeyField(int id, String field){
        return KEY_PREFIX + id + field + suffix;
    }

    void initializeDurationFref(){
        editor.putInt(getKeyField(1,KEY_ID),1);
        editor.putString(getKeyField(1,KEY_CONTENT),"Hằng Ngày");

        editor.putInt(getKeyField(2,KEY_ID),2);
        editor.putString(getKeyField(2,KEY_CONTENT),"Hằng Tuần (7 ngày)");

        editor.putInt(getKeyField(3,KEY_ID),3);
        editor.putString(getKeyField(3,KEY_CONTENT),"Hằng Tháng");

        editor.putInt(KEY_PREFIX+KEY_MAX_ID+suffix,4);

        editor.apply();
    }

    public ArrayList<String> getDurationContentList(){
        ArrayList<String> result = new ArrayList<>();

        int maxId = sharedPreferences.getInt(KEY_PREFIX+KEY_MAX_ID+suffix,1);

        for(int i = 1; i<=maxId;i++){
            result.add(sharedPreferences.getString(getKeyField(i,KEY_CONTENT),""));
        }
        return result;
    }

    //    --------> get id to save to db
    public int getDurationIdForSaving(String reasonContent){
        try {
            Map<String, ?> reasonMap = sharedPreferences.getAll();
            String resultKey = "";
            for (Map.Entry<String, ?> item : reasonMap.entrySet()) {
                if (item.getValue().toString().equals(reasonContent)) {
                    resultKey = item.getKey();
                    break;
                }
            }

            if (resultKey != "") {
                int beginIndex = KEY_PREFIX.length();
                int id = Integer.parseInt(resultKey.substring(beginIndex, beginIndex + 1));
                return sharedPreferences.getInt(getKeyField(id, KEY_ID), 0);
            } else
                return 0;
        }
        catch (Exception e){
            Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
            return 0;
        }
    }

    public String getContentForShowing(int id){
        return sharedPreferences.getString(getKeyField(id,KEY_CONTENT),"");
    }
}
