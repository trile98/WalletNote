package com.trile.walletnote.DAOS;

import com.trile.walletnote.model.FinancialInformation;
import com.trile.walletnote.model.ReturnData;

public interface DetailDao {
    ReturnData updateFinancialInformation(FinancialInformation info);

    ReturnData deleteFinancialInformation(int id);
}
