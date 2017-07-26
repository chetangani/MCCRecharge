package com.tvd.mccrecharge.values;

import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tvd on 07/24/2017.
 */

public class FunctionsCall {

    public String Appfoldername() {
        String name = "MCC_Recharge";
        return name;
    }

    public String filestorepath(String file) {
        File dir = new File(android.os.Environment.getExternalStorageDirectory(), Appfoldername());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.toString()+File.separator + file;
    }

    public void LogStatus(String msg) {
        Log.d("debug", msg);
    }

    public String currentDateandTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String cdt = sdf.format(new Date());
        return cdt;
    }
}
