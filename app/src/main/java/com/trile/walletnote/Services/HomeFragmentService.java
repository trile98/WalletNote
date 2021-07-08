package com.trile.walletnote.Services;

import com.trile.walletnote.model.FinancialInformation;

import java.util.ArrayList;

public interface HomeFragmentService {

    ArrayList<Integer> getTotal();
    ArrayList<FinancialInformation> getTopFive();

}
