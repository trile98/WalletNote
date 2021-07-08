package com.trile.walletnote.Services;

import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.model.ReturnData;

import java.util.ArrayList;

public interface HistoryFragmentService {
    ArrayList<FinancialInformation> getListHistoryFinancialInformation(int month, int year,String contentForSearch);
    void clickFinTypeBtns(int type);
    void clickPeriodBtns(int type);
    int getTotal(ArrayList<Integer> listId);
    void setFilterMode(boolean filterMode);
    void setSearchMode(boolean searchMode);

    ReturnData deleteFinancialInformations(ArrayList<Integer> listId);
}
