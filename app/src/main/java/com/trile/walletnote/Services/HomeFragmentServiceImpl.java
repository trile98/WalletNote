package com.trile.walletnote.Services;

import android.content.Context;

import com.trile.walletnote.DAOS.HomeDao;
import com.trile.walletnote.DAOS.HomeDaoImpl;
import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.sharePreferencces.PeriodFinancialInformationPrefs;

import java.util.ArrayList;

public class HomeFragmentServiceImpl implements HomeFragmentService{

    HomeDao homeDao;
    PeriodFinancialInformationPrefs periodFIPREF;

    public HomeFragmentServiceImpl(Context context){
        homeDao = new HomeDaoImpl(context);
        periodFIPREF = new PeriodFinancialInformationPrefs(context);
    }

    @Override
    public ArrayList<Integer> getTotal() {
        ArrayList<Integer> result = new ArrayList<>();

        int balance = homeDao.getGrandTotal();
        int periodIncomeTotal = periodFIPREF.getPeriodIncomeTotal();
        int periodOutgoTotal = periodFIPREF.getPeriodOutgoTotal();


        result.add(balance);
        result.add(periodIncomeTotal);
        result.add(periodOutgoTotal);

        return result;
    }

    @Override
    public ArrayList<FinancialInformation> getTopFive() {
        ArrayList<FinancialInformation> result = new ArrayList<>();
        result = homeDao.getTopFive();
        return result;
    }
}
