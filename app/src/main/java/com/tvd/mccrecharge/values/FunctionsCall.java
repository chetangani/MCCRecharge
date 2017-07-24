package com.tvd.mccrecharge.values;

import java.io.File;

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
}
