package com.trile.walletnote.sharePreferencces;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.trile.walletnote.R;

import java.util.ArrayList;
import java.util.Map;

public class ReasonPrefs {
    private static String PREF_NAME ="REASON_PREF";

    private static String KEY_PREFIX ="REASON_";

    private static String KEY_VIET_SUFFIX ="_VIET";
    private static String KEY_ENG_SUFFIX ="_ENG";

    private static SharedPreferences sharedPreferences;

    private static SharedPreferences.Editor editor;

    private static String KEY_MAX_ID ="MAX_ID";

    private static String KEY_CONTENT ="_CONTENT";
    private static String KEY_ID ="_ID";

    private String suffix = KEY_VIET_SUFFIX;

    Context context;

    public ReasonPrefs(Context context){
        this.context = context;
        if(sharedPreferences==null){
            sharedPreferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        }

        editor = sharedPreferences.edit();

        if(!sharedPreferences.contains("initialized"+suffix)){
            editor.putBoolean("initalized"+suffix,true);
            initializeReasonFref();
        }
    }

    String getKeyField(int id, String field){
        return KEY_PREFIX+id+field+suffix;
    }

    void initializeReasonFref(){
        editor.putInt(getKeyField(1,KEY_ID),1);
        editor.putString(getKeyField(1,KEY_CONTENT),"Ăn uống");

        editor.putInt(getKeyField(2,KEY_ID),2);
        editor.putString(getKeyField(2,KEY_CONTENT),"Phí");

        editor.putInt(getKeyField(3,KEY_ID),3);
        editor.putString(getKeyField(3,KEY_CONTENT),"Giải Trí");

        editor.putInt(getKeyField(4,KEY_ID),4);
        editor.putString(getKeyField(4,KEY_CONTENT),"Khác");

        editor.putInt(KEY_PREFIX+KEY_MAX_ID+suffix,5);

        editor.apply();
    }

    public ArrayList<String> getReasonContentList(){
        ArrayList<String> result = new ArrayList<>();
        
        int maxId = sharedPreferences.getInt(KEY_PREFIX+KEY_MAX_ID+suffix,1);
        
        for(int i = 1; i<=maxId;i++){
            result.add(sharedPreferences.getString(getKeyField(i,KEY_CONTENT),""));
        }
        return result;
    }

//    --------> get id to save to db
    public int getReasonIdForSaving(String reasonContent){
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

    public int getImage(int id){
        switch (id){
            case 1: return R.drawable.fin_type_food_img;
            case 2: return R.drawable.fin_type_fee_img;
            case 3: return R.drawable.fin_type_entertainment_img;
            case 4: return R.drawable.fin_type_other_img;
            default:return 0;
        }

    }
}
