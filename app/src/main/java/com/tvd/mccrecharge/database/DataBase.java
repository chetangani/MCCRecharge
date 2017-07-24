package com.tvd.mccrecharge.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tvd.mccrecharge.values.FunctionsCall;

/**
 * Created by tvd on 07/24/2017.
 */

public class DataBase {
    MyHelper mh ;
    SQLiteDatabase sdb ;

    public DataBase(Context context) {
        FunctionsCall functionsCall = new FunctionsCall();
        String dbname = functionsCall.filestorepath("collection.db");
        mh = new MyHelper(context, dbname, null, 1);
    }

    public void open() {
        sdb = mh.getWritableDatabase();
    }

    public void close() {
        sdb.close();
    }

    public class MyHelper extends SQLiteOpenHelper {
        public MyHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("Create table COLLECTION_INPUT(_id integer primary key, " +
                    "RRNO TEXT, CONSUMER_ID TEXT, LF_NO TEXT, NAME TEXT, TARIFF_NAME TEXT, DEALER_CODE TEXT, PAYABLE_AMOUNT TEXT);");
            db.execSQL("Create table COLLECTION_OUTPUT(_id integer primary key, " +
                    "RRNO TEXT, CONSUMER_ID TEXT, LF_NO TEXT, NAME TEXT, AMOUNT TEXT, MODE_PAYMENT TEXT, RECPT_NO TEXT, " +
                    "TRANSACTION_ID TEXT, MACHINE_ID TEXT, RECPT_DATE_TIME TEXT, RECPT_DATE TEXT, DEALER_CODE TEXT, " +
                    "ONLINE_FLAG TEXT, GPS_LAT TEXT, GPS_LONG TEXT, PAYMENT_PRINTED TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public Cursor getconsumerids(){
        Cursor c = null;
        c = sdb.rawQuery("select CONSUMER_ID from COLLECTION_INPUT", null);
        return c;
    }

    public Cursor getrrnos(){
        Cursor c = null;
        c = sdb.rawQuery("select RRNO from COLLECTION_INPUT", null);
        return c;
    }

    public Cursor getresultbyrrno(String rrno){
        Cursor c = null;
        c = sdb.rawQuery("select * from COLLECTION_INPUT where RRNO = "+"'"+rrno+"'", null);
        return c;
    }

    public Cursor getresultbyconsumerid(String Consumerid){
        Cursor c = null;
        c = sdb.rawQuery("select * from COLLECTION_INPUT where CONSUMER_ID = "+"'"+Consumerid+"'", null);
        return c;
    }
}
