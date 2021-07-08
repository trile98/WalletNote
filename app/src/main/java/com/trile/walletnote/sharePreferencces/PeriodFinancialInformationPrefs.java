package com.trile.walletnote.sharePreferencces;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.SharedPreferences;

import com.trile.walletnote.R;
import com.trile.walletnote.Services.ChangeFormatDateService;
import com.trile.walletnote.Services.ChangeFormatDateServiceImpl;
import com.trile.walletnote.Services.CustomDialog;
import com.trile.walletnote.Services.CustomDialogImpl;
import com.trile.walletnote.model.FinancialDetail;
import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.model.ReturnData;

import java.util.ArrayList;

public class PeriodFinancialInformationPrefs {
    private static final String PREF_NAME ="PERIOD_FINANCIAL_INFO_PREF";

    private static final String KEY_PREFIX ="PERIOD_FIN_";

    private static final String KEY_INCOME_SUFFIX ="_INCOME";

    private static final String KEY_OUTGO_SUFFIX ="_OUTGO";

    private static SharedPreferences sharedPreferences;

    private static SharedPreferences.Editor editor;

    private static final String KEY_MAX_ID = "MAX_ID";
//    private static final String KEY_ID ="_ID"; //need id to get id?
    private static final String KEY_TYPE ="_TYPE";
    private static final String KEY_CHOSENDATE ="_CHOSENDATE";
    private static final String KEY_AMOUNT ="_AMOUNT";
    private static final String KEY_REASON ="_REASON";
    private static final String KEY_DURATION ="_DURATION";
    private static final String KEY_DETAIL = "_DETAIL";
    private static final String KEY_JOBSERVICE_ID ="_JOB_ID";

    CustomDialog customDialog;
    DurationPrefs durationPrefs;
    ReasonPrefs reasonPrefs;
    ChangeFormatDateService changeFormatDateService;
    Context context;

    public PeriodFinancialInformationPrefs(Context context){
        customDialog = new CustomDialogImpl(context);
        durationPrefs   = new DurationPrefs(context);
        reasonPrefs = new ReasonPrefs(context);
        changeFormatDateService = new ChangeFormatDateServiceImpl(context);

        this.context = context;

        if(sharedPreferences==null){
            sharedPreferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        }
        editor = sharedPreferences.edit();
    }

    String getKeyField(int id,String field,String suffix){
        return KEY_PREFIX+id+ field+suffix;
    }

    public int getPeriodIncomeTotal(){
        int total = 0;
        int maxId = sharedPreferences.getInt(KEY_PREFIX + KEY_MAX_ID + KEY_INCOME_SUFFIX,1);

        for(int i = 1;i<maxId;i++){
            total += sharedPreferences.getInt(getKeyField(i,KEY_AMOUNT,KEY_INCOME_SUFFIX),0);
        }
        return total;
    }

    public int getPeriodOutgoTotal(){
        int total = 0;
        int maxId = sharedPreferences.getInt(KEY_PREFIX + KEY_MAX_ID +KEY_OUTGO_SUFFIX,1);

        for(int i = 1;i<maxId;i++){
            total += sharedPreferences.getInt(getKeyField(i,KEY_AMOUNT,KEY_OUTGO_SUFFIX),0);
        }
        return total;
    }

    public int getPeriodCustomTotal(ArrayList<Integer> listId, boolean addNewType){
        int total = 0;

        if(addNewType) {
            for (int item : listId) {
                total += sharedPreferences.getInt(getKeyField(item, KEY_AMOUNT, KEY_OUTGO_SUFFIX), 0);
            }
        }
        else{
            for (int item : listId) {
                total += sharedPreferences.getInt(getKeyField(item, KEY_AMOUNT, KEY_INCOME_SUFFIX), 0);
            }
        }
        return total;
    }

    public void addNewPeriodInformation(FinancialInformation info, boolean addNewType){
        try{
            String suffix = "";
            int type= 0;
            if(addNewType){
                suffix = KEY_OUTGO_SUFFIX;
                type = 1;
            }else{
                suffix = KEY_INCOME_SUFFIX;
                type = 0;
            }
            int maxId = sharedPreferences.getInt(KEY_PREFIX + KEY_MAX_ID + suffix,1);

            editor.putInt(getKeyField(maxId,KEY_TYPE,suffix),type);
            editor.putString(getKeyField(maxId,KEY_CHOSENDATE,suffix), changeFormatDateService.changeFormatDateForSaving(info.getChosenDate()));
            editor.putInt(getKeyField(maxId,KEY_AMOUNT,suffix),info.getAmount());
            editor.putString(getKeyField(maxId,KEY_REASON,suffix),String.valueOf(reasonPrefs.getReasonIdForSaving( info.getReason())));
            editor.putInt(getKeyField(maxId,KEY_DURATION,suffix),info.getDurationType());
            editor.putString(getKeyField(maxId,KEY_DETAIL,suffix),info.getDetail().getFinDetContent());

            maxId++;
            //update maxId value
            editor.putInt(KEY_PREFIX + KEY_MAX_ID + suffix,maxId);

            editor.apply();

        }catch (Exception e){
            customDialog.warningDialog("PeriodFinancialInformation.addNewPeriodInformation: "+e.toString());
        }

    }

    public int getCurrentId(boolean addNewType){
        String suffix = "";
        if(addNewType){
            suffix = KEY_OUTGO_SUFFIX;
        }else{
            suffix = KEY_INCOME_SUFFIX;
        }
        int result = sharedPreferences.getInt(KEY_PREFIX + KEY_MAX_ID + suffix,1);
        return result-1;
    }

    private FinancialInformation getFinInfo(int id, String suffix){
        FinancialInformation info = new FinancialInformation();
        boolean check = sharedPreferences.contains(getKeyField(id,KEY_AMOUNT,suffix));
        if(check){
            info.setID(id);
            info.setType(sharedPreferences.getInt(getKeyField(id, KEY_TYPE, suffix), 0) == 1);
            info.setChosenDate(changeFormatDateService.changeFormatDateForShowing(sharedPreferences.getString(getKeyField(id,KEY_CHOSENDATE,suffix),"")));
            info.setAmount(sharedPreferences.getInt(getKeyField(id,KEY_AMOUNT,suffix),0));
            info.setReason(sharedPreferences.getString(getKeyField(id,KEY_REASON,suffix),"1"));
            info.setDurationType(sharedPreferences.getInt(getKeyField(id,KEY_DURATION,suffix),1));
            info.setDetail(new FinancialDetail(sharedPreferences.getString(getKeyField(id,KEY_DETAIL,suffix),"")));

            return info;
        }else
            return null;
    }

    public ArrayList<FinancialInformation> getListPeriodIncoming(){
        ArrayList<FinancialInformation> result = new ArrayList<>();

        int maxIncomingId = sharedPreferences.getInt(KEY_PREFIX + KEY_MAX_ID + KEY_INCOME_SUFFIX,1);

        FinancialInformation info = new FinancialInformation();
        for(int i= maxIncomingId; i >0 ; i --){
            info = getFinInfo(i,KEY_INCOME_SUFFIX);
            if(info != null)
                result.add(info);
        }
        return result;
    }

    public ArrayList<FinancialInformation> getListPeriodOutgoing(){
        ArrayList<FinancialInformation> result = new ArrayList<>();

        int maxOutgoingId = sharedPreferences.getInt(KEY_PREFIX + KEY_MAX_ID + KEY_OUTGO_SUFFIX,1);

        FinancialInformation info = new FinancialInformation();
        for(int i= maxOutgoingId; i >0 ; i --){
            info = getFinInfo(i,KEY_OUTGO_SUFFIX);
            if(info != null)
                result.add(info);
        }
        return result;
    }

    public void setJobId(int id, boolean addNewType){
        String suffix ="";
        if(addNewType)
            suffix = KEY_OUTGO_SUFFIX;
        else
            suffix = KEY_INCOME_SUFFIX;

        editor.putInt(getKeyField(id,KEY_JOBSERVICE_ID,suffix),Integer.parseInt(context.getString(R.string.duration_auto_job_id_prefix)+""+id));

        editor.apply();
    }

    public ReturnData deletePeriodInfo(int id, boolean addNewType){
        ReturnData returnData = new ReturnData();
        try {
            String suffix = "";
            if (addNewType)
                suffix = KEY_OUTGO_SUFFIX;
            else
                suffix = KEY_INCOME_SUFFIX;

            //cancel job
            int jobId = sharedPreferences.getInt(getKeyField(id, KEY_JOBSERVICE_ID, suffix), 0);
            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (scheduler != null) {
                scheduler.cancel(jobId);
            }

            editor.remove(getKeyField(id, KEY_TYPE, suffix));
            editor.remove(getKeyField(id, KEY_CHOSENDATE, suffix));
            editor.remove(getKeyField(id, KEY_AMOUNT, suffix));
            editor.remove(getKeyField(id, KEY_REASON, suffix));
            editor.remove(getKeyField(id, KEY_DURATION, suffix));
            editor.remove(getKeyField(id, KEY_DETAIL, suffix));
            editor.remove(getKeyField(id, KEY_JOBSERVICE_ID, suffix));

            editor.apply();

            returnData.setResult(1);
            returnData.setMessage("");
            return returnData;
        }catch (Exception e){
            returnData.setResult(2);
            returnData.setMessage("PeriodFinancialInformationPrefs.deletePeriodInfo: "+e.toString());
            return returnData;
        }
    }

    public ReturnData deletePeriodInfo(ArrayList<Integer> listId, boolean addNewType){
        ReturnData returnData = new ReturnData();
        int successDeleteCount=0;
        try {
            for(int item:listId) {
                String suffix = "";
                if (addNewType)
                    suffix = KEY_OUTGO_SUFFIX;
                else
                    suffix = KEY_INCOME_SUFFIX;

                //cancel job
                int jobId = sharedPreferences.getInt(getKeyField(item, KEY_JOBSERVICE_ID, suffix), 0);
                JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                if (scheduler != null) {
                    scheduler.cancel(jobId);
                }

                editor.remove(getKeyField(item, KEY_TYPE, suffix));
                editor.remove(getKeyField(item, KEY_CHOSENDATE, suffix));
                editor.remove(getKeyField(item, KEY_AMOUNT, suffix));
                editor.remove(getKeyField(item, KEY_REASON, suffix));
                editor.remove(getKeyField(item, KEY_DURATION, suffix));
                editor.remove(getKeyField(item, KEY_DETAIL, suffix));
                editor.remove(getKeyField(item, KEY_JOBSERVICE_ID, suffix));

                editor.apply();
                successDeleteCount++;
            }
        }catch (Exception e){
            returnData.setResult(2);
            returnData.setMessage("PeriodFinancialInformationPrefs.deletePeriodInfo: "+e.toString());
            return returnData;
        }

        returnData.setResult(1);
        returnData.setMessage(context.getString(R.string.history_delete_successfully_message,successDeleteCount));
        return returnData;
    }
}
