package com.trile.walletnote.DAOS;

import com.trile.walletnote.model.FinancialInformation;

import java.util.ArrayList;

public interface HomeDao {
    int getGrandTotal();
    ArrayList<FinancialInformation> getTopFive();
}
