package com.trile.walletnote.DAOS;

import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.model.ReturnData;

import java.util.ArrayList;

public interface HistoryDao {
    ArrayList<FinancialInformation> getListNormalAll(int month, int year, boolean filterMode, String contentForSearch, boolean searchMode);
    ArrayList<FinancialInformation> getListNormalIncoming(int month, int year, boolean filterMode, String contentForSearch, boolean searchMode);
    ArrayList<FinancialInformation> getListNormalOutgoing(int month, int year, boolean filterMode, String contentForSearch, boolean searchMode);
    int getTotal(String stringOfListId);

    ReturnData deleteFinancialInformations(String listId);
}
