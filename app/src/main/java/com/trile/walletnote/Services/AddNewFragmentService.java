package com.trile.walletnote.Services;

import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.model.ReturnData;

import java.util.ArrayList;

public interface AddNewFragmentService {
    void PutMoreAddNewItem(ArrayList<FinancialInformation> FinList);
    ReturnData SaveAddNewInfo(ArrayList<FinancialInformation> FinList);
}
