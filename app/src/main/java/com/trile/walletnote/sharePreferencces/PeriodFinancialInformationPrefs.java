package com.trile.walletnote.sharePreferencces;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
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
import java.util.Arrays;

public class PeriodFinancialInformationPrefs {
    private static final String PREF_NAME ="PERIOD_FINANCIAL_INFO_PREF";

    private static final String KEY_PREFIX ="PERIOD_FIN_";

    private static final String KEY_INCOME_SUFFIX ="_INCOME";

    private static final String KEY_OUTGO_SUFFIX ="_OUTGO";

    private static SharedPreferences sharedPreferences;

    private static SharedPreferences.Editor editor;



//    private static final String KEY_ID ="_ID"; //need id to get id?
    private static final String KEY_TYPE ="_TYPE";
    private static final String KEY_CHOSENDATE ="_CHOSENDATE";
    private static final String KEY_AMOUNT ="_AMOUNT";
    private static final String KEY_REASON ="_REASON";
    private static final String KEY_DURATION ="_DURATION";
    private static final String KEY_DETAIL = "_DETAIL";

    CustomDialog customDialog;
    DurationPrefs durationPrefs;
    ReasonPrefs reasonPrefs;
    CurrentStatusPrefs currentStatusPrefs;
    ChangeFormatDateService changeFormatDateService;
    Context context;

    public PeriodFinancialInformationPrefs(Context context){
        customDialog = new CustomDialogImpl(context);
        durationPrefs   = new DurationPrefs(context);
        reasonPrefs = new ReasonPrefs(context);
        changeFormatDateService = new ChangeFormatDateServiceImpl(context);
        currentStatusPrefs = new CurrentStatusPrefs(context);

        this.context = context;

        if(sharedPreferences==null){
            sharedPreferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        }
        editor = sharedPreferences.edit();
    }

    public boolean checkPrefsEmpty(){
        return sharedPreferences.getAll().isEmpty();
    }

    String getKeyField(int id,String field,String suffix){
        return KEY_PREFIX+id+ field+suffix;
    }

    public int getPeriodIncomeTotal(){
        int total = 0;
        int maxId = currentStatusPrefs.getMaxIdIncome();

        for(int i = 1;i<maxId;i++){
            total += sharedPreferences.getInt(getKeyField(i,KEY_AMOUNT,KEY_INCOME_SUFFIX),0);
        }
        return total;
    }

    public int getPeriodOutgoTotal(){
        int total = 0;
        int maxId = currentStatusPrefs.getMaxIdOutgo();

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

    public ReturnData addNewPeriodInformation(FinancialInformation info, boolean addNewType){
        ReturnData returnData = new ReturnData();
        try{
            String suffix = "";
            int maxId = 1;
            int type= 0;
            if(addNewType){
                suffix = KEY_OUTGO_SUFFIX;
                type = 1;
                maxId = currentStatusPrefs.getMaxIdOutgo();
            }else{
                suffix = KEY_INCOME_SUFFIX;
                maxId = currentStatusPrefs.getMaxIdIncome();
            }


            editor.putInt(getKeyField(maxId,KEY_TYPE,suffix),type);
            editor.putString(getKeyField(maxId,KEY_CHOSENDATE,suffix), changeFormatDateService.changeFormatDateForSaving(info.getChosenDate()));
            editor.putInt(getKeyField(maxId,KEY_AMOUNT,suffix),info.getAmount());
            editor.putString(getKeyField(maxId,KEY_REASON,suffix),String.valueOf(reasonPrefs.getReasonIdForSaving( info.getReason())));
            editor.putInt(getKeyField(maxId,KEY_DURATION,suffix),info.getDurationType());
            editor.putString(getKeyField(maxId,KEY_DETAIL,suffix),info.getDetail().getFinDetContent());

            int newId = maxId;

            maxId++;

            //update maxId value
            if(addNewType)
                currentStatusPrefs.setMaxIdOutgo(maxId);
            else
                currentStatusPrefs.setMaxIdIncome(maxId);

            editor.apply();

            returnData.setResult(1);
            returnData.setMessage(newId+"");

            return returnData;

        }catch (Exception e){
            returnData.setResult(2);
            returnData.setMessage("PeriodFinancialInformation.addNewPeriodInformation: "+e.toString());
            return returnData;
        }

    }

    public int getCurrentId(boolean addNewType){
        String suffix = "";
        int result = 0;
        if(addNewType){
            suffix = KEY_OUTGO_SUFFIX;
            result = currentStatusPrefs.getMaxIdOutgo();
        }else{
            suffix = KEY_INCOME_SUFFIX;
            result = currentStatusPrefs.getMaxIdIncome();
        }
        return result-1;
    }

    public FinancialInformation getFinInfo(int id, String suffix){
        FinancialInformation info = new FinancialInformation();
        if(sharedPreferences.contains(getKeyField(id,KEY_AMOUNT,suffix))){

            info.setID(id);

            info.setType(KEY_OUTGO_SUFFIX.equals(suffix));

            info.setChosenDate(changeFormatDateService.changeFormatDateForShowing(sharedPreferences.getString(getKeyField(id,KEY_CHOSENDATE,suffix),"")));
            info.setAmount(sharedPreferences.getInt(getKeyField(id,KEY_AMOUNT,suffix),0));
            info.setReason(sharedPreferences.getString(getKeyField(id,KEY_REASON,suffix),"1"));
            info.setDurationType(sharedPreferences.getInt(getKeyField(id,KEY_DURATION,suffix),1));
            info.setDetail(new FinancialDetail(sharedPreferences.getString(getKeyField(id,KEY_DETAIL,suffix),"")));

            return info;
        }
        return null;
    }

    public ArrayList<FinancialInformation> getListPeriodIncoming(){
        ArrayList<FinancialInformation> result = new ArrayList<>();

        int maxIncomingId = currentStatusPrefs.getMaxIdIncome();

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

        int maxOutgoingId = currentStatusPrefs.getMaxIdOutgo();

        FinancialInformation info = new FinancialInformation();
        for(int i= maxOutgoingId; i >0 ; i --){
            info = getFinInfo(i,KEY_OUTGO_SUFFIX);
            if(info != null)
                result.add(info);
        }
        return result;
    }

    public ReturnData updatePeriodInfo(FinancialInformation info){
        ReturnData returnData = new ReturnData();
        int id = info.getID();

        String suffix ="";
        if(info.getType())
            suffix = KEY_OUTGO_SUFFIX;
        else
            suffix = KEY_INCOME_SUFFIX;

        try {

            if (sharedPreferences.contains(getKeyField(id, KEY_AMOUNT, KEY_INCOME_SUFFIX))) {

                if (!info.getType()) { // in case change type to outgo
//                    editor.putInt(getKeyField(id,KEY_JOBSERVICE_ID,suffix),sharedPreferences.getInt(getKeyField(id,KEY_JOBSERVICE_ID,KEY_INCOME_SUFFIX),0));
//
//                    ReturnData deleteResult = deletePeriodInfo(id, false);//delete exist income
//
//                    if(deleteResult.getResult() == 2){
//                        returnData.setResult(2);
//                        returnData.setMessage(deleteResult.getMessage());
//                        return returnData;
//                    }
//                    cannot do that because it will overwrite other info in periodic file
                }

                editor.putInt(getKeyField(id, KEY_TYPE, suffix), (info.getType() ? 1 : 0));
                editor.putString(getKeyField(id, KEY_CHOSENDATE, suffix), changeFormatDateService.changeFormatDateForSaving(info.getChosenDate()));
                editor.putInt(getKeyField(id, KEY_AMOUNT, suffix), info.getAmount());
                editor.putString(getKeyField(id, KEY_REASON, suffix), String.valueOf(reasonPrefs.getReasonIdForSaving(info.getReason())));
                editor.putInt(getKeyField(id, KEY_DURATION, suffix), info.getDurationType());
                editor.putString(getKeyField(id, KEY_DETAIL, suffix), info.getDetail().getFinDetContent());

                editor.apply();

                returnData.setResult(1);
                returnData.setMessage("");
                return returnData;
            }
            if (sharedPreferences.contains(getKeyField(id, KEY_AMOUNT, KEY_OUTGO_SUFFIX))) {

                if (info.getType()) { // in case change type to income
//                    editor.putInt(getKeyField(id,KEY_JOBSERVICE_ID,suffix),sharedPreferences.getInt(getKeyField(id,KEY_JOBSERVICE_ID,KEY_OUTGO_SUFFIX),0));
//
//                    ReturnData deleteResult = deletePeriodInfo(id, true);//delete exist outgo
//
//                    if(deleteResult.getResult() == 2){
//                        returnData.setResult(2);
//                        returnData.setMessage(deleteResult.getMessage());
//                        return returnData;
//                    }
//                  cannot do that because it will be overwrite orther info in periodic file
                }

                editor.putInt(getKeyField(id, KEY_TYPE, suffix), (info.getType() ? 1 : 0));
                editor.putString(getKeyField(id, KEY_CHOSENDATE, suffix), changeFormatDateService.changeFormatDateForSaving(info.getChosenDate()));
                editor.putInt(getKeyField(id, KEY_AMOUNT, suffix), info.getAmount());
                editor.putString(getKeyField(id, KEY_REASON, suffix), String.valueOf(reasonPrefs.getReasonIdForSaving(info.getReason())));
                editor.putInt(getKeyField(id, KEY_DURATION, suffix), info.getDurationType());
                editor.putString(getKeyField(id, KEY_DETAIL, suffix), info.getDetail().getFinDetContent());

                editor.apply();

                returnData.setResult(1);
                returnData.setMessage("");
                return returnData;
            }

            returnData.setResult(2);
            returnData.setMessage("PeriodFinancialInformation.updatePeriodInfo: This info doesn't exist");
            return returnData;

        }catch (Exception e){
            returnData.setResult(2);
            returnData.setMessage("PeriodFinancialInformation.updatePeriodInfo: "+e.toString());
            return returnData;
        }

    }

    public ReturnData deletePeriodInfo(int id, boolean addNewType){
        ReturnData returnData = new ReturnData();
        try {
            String suffix = "";
            if (addNewType)
                suffix = KEY_OUTGO_SUFFIX;
            else
                suffix = KEY_INCOME_SUFFIX;


            editor.remove(getKeyField(id, KEY_TYPE, suffix));
            editor.remove(getKeyField(id, KEY_CHOSENDATE, suffix));
            editor.remove(getKeyField(id, KEY_AMOUNT, suffix));
            editor.remove(getKeyField(id, KEY_REASON, suffix));
            editor.remove(getKeyField(id, KEY_DURATION, suffix));
            editor.remove(getKeyField(id, KEY_DETAIL, suffix));

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

                editor.remove(getKeyField(item, KEY_TYPE, suffix));
                editor.remove(getKeyField(item, KEY_CHOSENDATE, suffix));
                editor.remove(getKeyField(item, KEY_AMOUNT, suffix));
                editor.remove(getKeyField(item, KEY_REASON, suffix));
                editor.remove(getKeyField(item, KEY_DURATION, suffix));
                editor.remove(getKeyField(item, KEY_DETAIL, suffix));

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

    public ArrayList<FinancialInformation> getListDailyFI(){
        ArrayList<FinancialInformation> result = new ArrayList<>();

        int maxIncomingId = currentStatusPrefs.getMaxIdIncome();

        FinancialInformation info = new FinancialInformation();
        for(int i= maxIncomingId; i >0 ; i --){
            if(sharedPreferences.getInt(getKeyField(i,KEY_DURATION,KEY_INCOME_SUFFIX),0)==1 && checkItemTime(i,1,KEY_INCOME_SUFFIX)) {
                info = getFinInfo(i,KEY_INCOME_SUFFIX);
                if (info != null)
                    result.add(info);
            }
        }

        int maxOutgoingId = currentStatusPrefs.getMaxIdOutgo();

        for(int i= maxOutgoingId; i >0 ; i --){
            if(sharedPreferences.getInt(getKeyField(i,KEY_DURATION,KEY_OUTGO_SUFFIX),0)==1 && checkItemTime(i,1,KEY_OUTGO_SUFFIX)) {
                info = getFinInfo(i,KEY_OUTGO_SUFFIX);
                if (info != null)
                    result.add(info);
            }
        }

        return result;
    }

    public ArrayList<FinancialInformation> getListWeeklyFI(){
        ArrayList<FinancialInformation> result = new ArrayList<>();

        int maxIncomingId = currentStatusPrefs.getMaxIdIncome();
        FinancialInformation info = new FinancialInformation();
        for(int i= maxIncomingId; i >0 ; i --){
            if(sharedPreferences.getInt(getKeyField(i,KEY_DURATION,KEY_INCOME_SUFFIX),0)==2 && checkItemTime(i,2,KEY_INCOME_SUFFIX)) {
                info = getFinInfo(i,KEY_INCOME_SUFFIX);
                if (info != null)
                    result.add(info);
            }
        }

        int maxOutgoingId = currentStatusPrefs.getMaxIdOutgo();
        for(int i= maxOutgoingId; i >0 ; i --){
            if(sharedPreferences.getInt(getKeyField(i,KEY_DURATION,KEY_OUTGO_SUFFIX),0)==2 && checkItemTime(i, 2, KEY_OUTGO_SUFFIX)) {
                info = getFinInfo(i,KEY_OUTGO_SUFFIX);
                if (info != null)
                    result.add(info);
            }
        }

        return result;
    }


    public ArrayList<FinancialInformation> getListMonthlyFI(){
        ArrayList<FinancialInformation> result = new ArrayList<>();

        int maxIncomingId = currentStatusPrefs.getMaxIdIncome();
        FinancialInformation info = new FinancialInformation();
        for(int i= maxIncomingId; i >0 ; i --){
            if(sharedPreferences.getInt(getKeyField(i,KEY_DURATION,KEY_INCOME_SUFFIX),0)==3 && checkItemTime(i,3,KEY_INCOME_SUFFIX)) {
                info = getFinInfo(i,KEY_INCOME_SUFFIX);
                if (info != null)
                    result.add(info);
            }
        }

        int maxOutgoingId = currentStatusPrefs.getMaxIdOutgo();
        for(int i= maxOutgoingId; i >0 ; i --){
            if(sharedPreferences.getInt(getKeyField(i,KEY_DURATION,KEY_OUTGO_SUFFIX),0)==3 && checkItemTime(i, 3, KEY_OUTGO_SUFFIX)) {
                info = getFinInfo(i,KEY_OUTGO_SUFFIX);
                if (info != null)
                    result.add(info);
            }
        }

        return result;
    }

    private boolean checkItemTime(int id, int durationType, String suffix){
        int currentDate = Integer.parseInt(changeFormatDateService.getCurrentDateForSaving());

        int currentChosenDate = Integer.parseInt(sharedPreferences.getString(getKeyField(id,KEY_CHOSENDATE,suffix),"0"));

        if(currentChosenDate != 0 && (currentChosenDate <= currentDate)) {
            if(durationType == 1)
                return true;

            if(durationType == 2  && changeFormatDateService.calculateDaysToCurrentDate(String.valueOf(currentChosenDate))%7 == 0)
                return true;

            if(durationType == 3){
                if(checkLastDayOfMonth(currentChosenDate)){
                    return checkLastDayOfMonth(currentDate);
                }
                else{ // if chosen date is not last day of month, just check day equal to current date
                    return getDateFromString(currentDate) == getDateFromString(currentChosenDate);
                }
            }
        }
        return false;
    }

    private int getMonthFromString (int date){
        String stringDate = String.valueOf(date);
        return Integer.parseInt(stringDate.substring(4,6));
    }

    private int getDateFromString (int date){
        String stringDate = String.valueOf(date);
        return Integer.parseInt(stringDate.substring(6,8));
    }

    private int getYearFromString (int date){
        String stringDate = String.valueOf(date);
        return Integer.parseInt(stringDate.substring(0,4));
    }

    private static final ArrayList<Integer> thirtyDayMonth = new ArrayList<>(Arrays.asList(4,6,9,11));
    private static final ArrayList<Integer> thirtyOneDayMonth = new ArrayList<>(Arrays.asList(1,3,5,7,8,10, 12 ));

    private boolean checkLastDayOfMonth(int date){
        if( (thirtyOneDayMonth.contains(getMonthFromString(date)) && getDateFromString(date) == 31)
                || (thirtyDayMonth.contains(getMonthFromString(date)) && getDateFromString(date) == 30))
            return true;
        if(getMonthFromString(date)==2){
            if(getYearFromString(date) % 4 == 0 && getDateFromString(date) == 29)
                return true;
            if(getYearFromString(date) % 4 != 0 && getDateFromString(date) == 28)
                return true;
        }

        return false;
    }
}
