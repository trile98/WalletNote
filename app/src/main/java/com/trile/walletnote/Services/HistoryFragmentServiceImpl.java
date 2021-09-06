package com.trile.walletnote.Services;

import android.content.Context;
import android.util.Log;

import com.trile.walletnote.DAOS.HistoryDao;
import com.trile.walletnote.DAOS.HistoryDaoImpl;
import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.model.ReturnData;
import com.trile.walletnote.sharePreferencces.PeriodFinancialInformationPrefs;

import java.util.ArrayList;

public class HistoryFragmentServiceImpl implements HistoryFragmentService{
    int periodStatus; // 1 - normal, 2 - periodic
    int finTypeStatus; // 1 - all, 2 - incoming, 3 - outgoing

    boolean filterMode, searchMode;

    HistoryDao historyDao;
    PeriodFinancialInformationPrefs PFIPrefs;

    public HistoryFragmentServiceImpl(Context context){
        historyDao = new HistoryDaoImpl(context);
        PFIPrefs = new PeriodFinancialInformationPrefs(context);

        periodStatus = 1;
        finTypeStatus = 1;
        filterMode = false;
        searchMode = false;
    }


    @Override
    public ArrayList<FinancialInformation> getListHistoryFinancialInformation(int month, int year, String contentForSearch) {
        ArrayList<FinancialInformation> result = new ArrayList<>();

        switch (periodStatus){
            case 1:{
                if(finTypeStatus == 1){
                    result = historyDao.getListNormalAll(month, year, filterMode, contentForSearch, searchMode);
                }
                else if(finTypeStatus == 2){
                    result = historyDao.getListNormalIncoming(month, year, filterMode, contentForSearch, searchMode);
                }
                else if(finTypeStatus == 3){
                    result = historyDao.getListNormalOutgoing(month, year, filterMode, contentForSearch, searchMode);
                }
                break;
            }
            case 2:{
                if(finTypeStatus == 2){
                    result = PFIPrefs.getListPeriodIncoming();
                }
                else if(finTypeStatus == 3){
                    result = PFIPrefs.getListPeriodOutgoing();
                }
                break;
            }
        }
        return result;
    }

    @Override
    public void clickFinTypeBtns(int type) {
        finTypeStatus = type;
    }

    @Override
    public void clickPeriodBtns(int type) {
        periodStatus = type;
    }

    @Override
    public int getTotal(ArrayList<Integer> listId) {
        if(!listId.isEmpty()) {
            String stringListId = listId.get(0).toString();

            for (int i = 1; i < listId.size(); i++) {
                stringListId += "," + listId.get(i);
            }

            Log.e("stringidlist",stringListId);

            int result = historyDao.getTotal(stringListId);

            return result;
        }
        else
            return 0;
    }

    @Override
    public void setFilterMode(boolean filterMode) {
        this.filterMode = filterMode;
    }

    @Override
    public void setSearchMode(boolean searchMode) {
        this.searchMode = searchMode;
    }

    @Override
    public ReturnData deleteFinancialInformations(ArrayList<Integer> listId) {
        String stringListId = listId.toString().replace('[',' ').replace(']',' ');

        return historyDao.deleteFinancialInformation(stringListId);
    }


}
