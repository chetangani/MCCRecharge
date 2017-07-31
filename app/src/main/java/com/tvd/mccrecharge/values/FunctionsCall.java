package com.tvd.mccrecharge.values;

import android.graphics.Typeface;
import android.util.Log;

import com.lvrenyang.io.Canvas;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public void logStatus(String msg) {
        Log.d("debug", msg);
    }

    public String currentDateandTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String cdt = sdf.format(new Date());
        return cdt;
    }

    public String space(String s, int length) {
        int temp;
        StringBuilder spaces = new StringBuilder();
        temp = length - s.length();
        for (int i = 0; i < temp; i++) {
            spaces.append(" ");
        }
        return (s + spaces);
    }

    public String centeralign(String text, int width) {
        int count = text.length();
        int value = width - count;
        int append = (value / 2);
        return space(" ", append) + text;
    }

    public void splitString(String msg, int lineSize, ArrayList<String> arrayList) {
        arrayList.clear();
        Pattern p = Pattern.compile("\\b.{0," + (lineSize-1) + "}\\b\\W?");
        Matcher m = p.matcher(msg);
        while(m.find()) {
            arrayList.add(m.group().trim());
        }
    }

    public String line(int length) {
        StringBuilder sb5 = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb5.append("-");
        }
        return (sb5.toString());
    }
}
