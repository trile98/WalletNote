package com.trile.walletnote.Services;

import android.content.Context;

import com.trile.walletnote.Adapters.AddNewAdapter;
import com.trile.walletnote.DAOS.AddNewDao;
import com.trile.walletnote.DAOS.AddNewDaoImpl;
import com.trile.walletnote.model.FinancialDetail;
import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.model.ReturnData;
import com.trile.walletnote.sharePreferencces.CurrentStatusPrefs;
import com.trile.walletnote.sharePreferencces.ReasonPrefs;

import java.util.ArrayList;

public class AddNewFragmentServiceImpl implements AddNewFragmentService {
    Context context;
    AddNewAdapter adapter;

    Boolean addNewType;

    CustomDialog customDialog;
    ChangeFormatDateService changeFormatDateService;

    CurrentStatusPrefs currentStatusPrefs;
    ReasonPrefs reasonPrefs;

    String durationType;

    AddNewDao addNewDao;

    public AddNewFragmentServiceImpl(Context context, AddNewAdapter adapter) {
        this.context = context;
        this.adapter = adapter;

        currentStatusPrefs = new CurrentStatusPrefs(this.context);
        reasonPrefs = new ReasonPrefs(this.context);
        addNewType = currentStatusPrefs.getAddNewStatus();
        customDialog = new CustomDialogImpl(context);
        changeFormatDateService = new ChangeFormatDateServiceImpl(this.context);
        durationType = currentStatusPrefs.getDurationStatus();

        addNewDao = new AddNewDaoImpl(context);
    }

    @Override
    public void PutMoreAddNewItem(ArrayList<FinancialInformation> FinList) {
        if(durationType.equals("normal"))
            FinList.add(new FinancialInformation(currentStatusPrefs.getAddNewStatus(),changeFormatDateService.getCurrentDateForShowing(),0,new FinancialDetail("")));
        else
            FinList.add(new FinancialInformation(currentStatusPrefs.getAddNewStatus(),changeFormatDateService.getCurrentDateForShowing(),0,1,new FinancialDetail("")));

        adapter.listAmountForCheckEmpty.add(0);
        adapter.notifyDataSetChanged();
    }




    @Override
    public ReturnData SaveAddNewInfo(ArrayList<FinancialInformation> FinList) {
        ReturnData returnData = new ReturnData();
        if(!durationType.isEmpty()) {
            if (FinList.size() > 0) {
//                    add normal
                if(durationType.equals("normal")) {
                   returnData=addNewDao.saveNormalFinInfo(FinList);

                }
//                    add period
                else if(durationType.equals("period")){
                    returnData = addNewDao.savePeriodFinInfo(FinList,addNewType);
                }
            }
            return returnData;
        }

        returnData.setResult(2);
        returnData.setMessage("");

        return returnData;
    }


}
