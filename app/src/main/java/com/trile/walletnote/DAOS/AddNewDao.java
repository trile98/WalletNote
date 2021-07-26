package com.trile.walletnote.DAOS;

import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.model.ReturnData;

import java.util.ArrayList;

public interface AddNewDao {
    ReturnData saveNormalFinInfo(ArrayList<FinancialInformation> list);
    ReturnData savePeriodFinInfo(ArrayList<FinancialInformation> list, boolean addNewType);
}
