package com.trile.walletnote.Services;

import android.content.Context;

import com.trile.walletnote.DAOS.DetailDao;
import com.trile.walletnote.DAOS.DetailDaoImpl;
import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.model.ReturnData;
import com.trile.walletnote.sharePreferencces.PeriodFinancialInformationPrefs;

public class DetailFragmentServiceImpl implements  DetailFragmentService{
    DetailDao detailDao;
    PeriodFinancialInformationPrefs periodFinancialInformationPrefs;

    public DetailFragmentServiceImpl(Context context){
        detailDao = new DetailDaoImpl(context);
        periodFinancialInformationPrefs = new PeriodFinancialInformationPrefs(context);
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
        return periodFinancialInformationPrefs.deletePeriodInfo(id,addNewType);
    }
}
