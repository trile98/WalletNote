package com.trile.walletnote.Services;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HandleErrorServiceImpl implements HandleErrorService{
    ChangeFormatDateService formatDateService;

    public HandleErrorServiceImpl(){
        formatDateService = new ChangeFormatDateServiceImpl();
    }

    @Override
    public void appendErrorTextForAutoJob(String text) {
        File filePath = new File(Environment.getExternalStorageDirectory(),"Logs");
        if (!filePath.exists())
        {
            filePath.mkdir();
        }
        File logfile = new File(filePath,"LogAutoJob");

        String currentDateTime = formatDateService.getCurrentDateTime() + " ";

        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logfile, true));
            buf.append(currentDateTime+text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
