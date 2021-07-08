package com.trile.walletnote.Services;

import android.content.Context;

public class HandleMoneyFormatImpl implements HandleMoneyFormat{

    CustomDialog customDialog;

    public HandleMoneyFormatImpl(Context context){
        customDialog = new CustomDialogImpl(context);
    }


    @Override
    public String FormatMoneyForShowing(String money) {
        try {
            StringBuilder resultBuilder = new StringBuilder();
//        if(Integer.) ----> kiem tra so nhap vao parse duoc integer (tam thoi xai try catch vay)
            String moneyString = String.valueOf(Integer.parseInt(money));
            int stringLength = moneyString.length();
            char[] arrayString = moneyString.toCharArray();

            int countVar = 0;

            for (int i = stringLength - 1; i > 0; i--) {
                resultBuilder.append(arrayString[i]);
                countVar++;
                if (countVar % 3 == 0) {
                    resultBuilder.append(',');
                    countVar = 0;
                }
            }

            //prevent to add comma to last number like this : ",300,000"
            resultBuilder.append(arrayString[0]);

            resultBuilder.reverse();

            return resultBuilder.toString();
        }catch (NumberFormatException e){
            customDialog.warningDialog("So tien qua lon, nhap so khac \n \n Error: "+e.toString());
        }
        return "0";
    }

    @Override
    public String FormatMoneyForSaving(String money) {
        try{
            String result = money.replace(",","");
            Integer.parseInt(result);
            return result;
        }catch (NumberFormatException e){
            customDialog.warningDialog("So tien qua lon, nhap so khac \n \n Error: "+e.toString());
        }
        return "0";
    }
}
