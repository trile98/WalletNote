package com.trile.walletnote.Services;

import android.content.Context;
import android.content.Intent;

import com.trile.walletnote.DAOS.DetailDao;
import com.trile.walletnote.DAOS.DetailDaoImpl;
import com.trile.walletnote.Jobs.MasterJob;
import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.model.ReturnData;
import com.trile.walletnote.sharePreferencces.CurrentStatusPrefs;
import com.trile.walletnote.sharePreferencces.PeriodFinancialInformationPrefs;

public class DetailFragmentServiceImpl implements  DetailFragmentService{
    DetailDao detailDao;
    PeriodFinancialInformationPrefs periodFinancialInformationPrefs;
    CurrentStatusPrefs currentStatusPrefs;

    Context context;

    public DetailFragmentServiceImpl(Context context){
        this.context = context;
        detailDao = new DetailDaoImpl(context);
        periodFinancialInformationPrefs = new PeriodFinancialInformationPrefs(context);
        currentStatusPrefs = new CurrentStatusPrefs(context);
    }

    @Override
    public ReturnData updateFinancialInformation(FinancialInformation info) {
        return detailDao.updateFinancialInformation(info);
    }

    @Override
    public ReturnData deleteFinancialInformation(int id) {
        return detailDao.deleteFinancialInformation(id);
    }

    @Override
    public ReturnData deletePeriodicFinancialInformation(int id, boolean addNewType) {
        ReturnData returnData = periodFinancialInformationPrefs.deletePeriodInfo(id,addNewType);

        if(periodFinancialInformationPrefs.checkPrefsEmpty()){
            Intent intentService = new Intent(context, MasterJob.class);
            context.stopService(intentService);

            currentStatusPrefs.setMaxIdIncome(1);
            currentStatusPrefs.setMaxIdOutgo(1);
        }

        return returnData;
    }
}
