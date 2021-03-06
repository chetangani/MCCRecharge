package com.tvd.mccrecharge.posting;

import android.os.AsyncTask;
import android.os.Handler;

import com.tvd.mccrecharge.values.FunctionsCall;
import com.tvd.mccrecharge.values.GetSetValues;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by tvd on 07/25/2017.
 */

public class Sending_Data {
    FunctionsCall functionCalls = new FunctionsCall();
    Receiving_Data receivingData = new Receiving_Data();

    private String UrlPostConnection(String Post_Url, HashMap<String, String> datamap) throws IOException {
        String response = "";
        functionCalls.logStatus("Connecting URL: "+Post_Url);
        URL url = new URL(Post_Url);
        functionCalls.logStatus("URL Connection 1");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        functionCalls.logStatus("URL Connection 2");
        conn.setReadTimeout(15000);
        functionCalls.logStatus("URL Connection 3");
        conn.setConnectTimeout(15000);
        functionCalls.logStatus("URL Connection 4");
        conn.setRequestMethod("POST");
        functionCalls.logStatus("URL Connection 5");
        conn.setDoInput(true);
        functionCalls.logStatus("URL Connection 6");
        conn.setDoOutput(true);
        functionCalls.logStatus("URL Connection 7");

        OutputStream os = conn.getOutputStream();
        functionCalls.logStatus("URL Connection 8");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        functionCalls.logStatus("URL Connection 9");
        writer.write(getPostDataString(datamap));
        functionCalls.logStatus("URL Connection 10");
        writer.flush();
        functionCalls.logStatus("URL Connection 11");
        writer.close();
        functionCalls.logStatus("URL Connection 12");
        os.close();
        functionCalls.logStatus("URL Connection 13");
        int responseCode=conn.getResponseCode();
        functionCalls.logStatus("URL Connection 14");
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            functionCalls.logStatus("URL Connection 15");
            String line;
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            functionCalls.logStatus("URL Connection 16");
            while ((line=br.readLine()) != null) {
                response+=line;
            }
            functionCalls.logStatus("URL Connection 17");
        }
        else {
            response="";
            functionCalls.logStatus("URL Connection 18");
        }
        functionCalls.logStatus("URL Connection Response: "+response);
        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            functionCalls.logStatus(result.toString());
        }

        return result.toString();
    }

    private String UrlGetConnection(String Get_Url) throws IOException {
        String response = "";
        functionCalls.logStatus("Connecting URL: "+Get_Url);
        URL url = new URL(Get_Url);
        functionCalls.logStatus("URL Get Connection 1");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        functionCalls.logStatus("URL Get Connection 2");
        conn.setReadTimeout(15000);
        functionCalls.logStatus("URL Get Connection 3");
        conn.setConnectTimeout(15000);
        functionCalls.logStatus("URL Get Connection 4");
        int responseCode=conn.getResponseCode();
        functionCalls.logStatus("URL Get Connection 5");
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            functionCalls.logStatus("URL Get Connection 6");
            String line;
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            functionCalls.logStatus("URL Get Connection 7");
            while ((line=br.readLine()) != null) {
                response+=line;
            }
            functionCalls.logStatus("URL Get Connection 8");
        }
        else {
            response="";
            functionCalls.logStatus("URL Get Connection 9");
        }
        functionCalls.logStatus("URL Get Connection Response: "+response);
        return response;
    }

    public class GetCollection_Consumer_details extends AsyncTask<String, String, String> {
        String response="";
        GetSetValues values;
        Handler handler;

        public GetCollection_Consumer_details(GetSetValues values, Handler handler) {
            this.values = values;
            this.handler = handler;
        }

        @Override
        protected String doInBackground(String... params) {
            functionCalls.logStatus("BASE_URL: "+"http://106.51.57.253:8086/Android_Upload_Download.asmx");
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("RRNO", params[0]);
            datamap.put("CONSID", params[1]);
            try {
                response = UrlPostConnection("http://106.51.57.253:8086/Android_Upload_Download.asmx/"+"MissingCollection", datamap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            receivingData.collection_Consumer_details(result, values, handler);
        }
    }

}
