package com.trile.walletnote.Services;

import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.model.ReturnData;

public interface DetailFragmentService {

    ReturnData updateFinancialInformation(FinancialInformation info);

    ReturnData deleteFinancialInformation(int id);

    ReturnData deletePeriodicFinancialInformation(int id, boolean addNewType);
}
