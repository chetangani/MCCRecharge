package com.tvd.mccrecharge.posting;

import android.os.Handler;

import com.tvd.mccrecharge.values.GetSetValues;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.tvd.mccrecharge.fragments.Collection_Fragment.CUSTOMER_DETAILS;
import static com.tvd.mccrecharge.fragments.Collection_Fragment.NO_DETAILS;

/**
 * Created by tvd on 07/25/2017.
 */

public class Receiving_Data {

    public String parseServerXML(String result) {
        String value="";
        XmlPullParserFactory pullParserFactory;
        InputStream res;
        try {
            res = new ByteArrayInputStream(result.getBytes());
            pullParserFactory = XmlPullParserFactory.newInstance();
            pullParserFactory.setNamespaceAware(true);
            XmlPullParser parser = pullParserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(res, null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        switch (name) {
                            case "string":
                                value =  parser.nextText();
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public void collection_Consumer_details(String result, GetSetValues values, Handler handler) {
        try {
            JSONObject object = new JSONObject(parseServerXML(result));
            String message = object.getString("message");
            if (message.equals("Success")) {
                handler.sendEmptyMessage(CUSTOMER_DETAILS);
                values.setCollection_name(object.getString("NAME"));
                values.setCollection_RRNO(object.getString("RRNO"));
                values.setCollection_CONS_ID(object.getString("CONSUMER_ID"));
                values.setCollection_Tariff(object.getString("TARIFF_NAME"));
                values.setCollection_Amt_due(object.getString("PAYABLE_AMOUNT"));
            } else handler.sendEmptyMessage(NO_DETAILS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
