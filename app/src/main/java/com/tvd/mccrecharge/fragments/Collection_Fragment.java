package com.tvd.mccrecharge.fragments;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lvrenyang.io.BTPrinting;
import com.lvrenyang.io.Canvas;
import com.lvrenyang.io.IOCallBack;
import com.tvd.mccrecharge.MainActivity;
import com.tvd.mccrecharge.R;
import com.tvd.mccrecharge.database.DataBase;
import com.tvd.mccrecharge.posting.Sending_Data;
import com.tvd.mccrecharge.posting.Sending_Data.GetCollection_Consumer_details;
import com.tvd.mccrecharge.services.BluetoothService;
import com.tvd.mccrecharge.values.FunctionsCall;
import com.tvd.mccrecharge.values.GetSetValues;
import com.tvd.mccrecharge.values.NumtoWords;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.tvd.mccrecharge.MainActivity.DEVICE_NAME;


/**
 * A simple {@link Fragment} subclass.
 */
public class Collection_Fragment extends Fragment {
    public static final int CUSTOMER_DETAILS = 1;
    public static final int NO_DETAILS = 2;

    private static final int DLG_REPRINT = 11;
    private static final int DLG_EXIT = 12;
    private static final int DLG_PRINT_FAIL = 13;

    View view;
    Button search_btn, submit_btn;
    AutoCompleteTextView search_result;
    TextInputLayout search_result_layout;
    TextView tv_name, tv_rrno, tv_acc_id, tv_tariff, tv_amt_due;
    EditText et_payable_amt;
    LinearLayout collection_details;
    float yaxis=0;
    ArrayList<String> amountinwords, namelist;
    DataBase dataBase;
    NumtoWords numtoWords;
    boolean consumerid = false, rrno = false, dealersprint=false, customersprint=false;
    String Search_Value="", cons_name="", cons_rrno="", cons_id="", cons_tariff="", cons_amtdue="";
    String amt_words_1="", amt_words_2="", name_1="", name_2="";
    GetSetValues getSetValues;
    Sending_Data data;
    FunctionsCall functionsCall;
    DecimalFormat num;
    static ProgressDialog progressDialog = null;

    Canvas mCanvas = BluetoothService.mCanvas;
    ExecutorService es = BluetoothService.es;

    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;

    private final Handler mHandler;
    {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CUSTOMER_DETAILS:
                        appendSearchedJSONValues();
                        break;

                    case NO_DETAILS:
                        Snackbar.make(view, "Details not found", Snackbar.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }

    public Collection_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_collection, container, false);

        initialize(view);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.collection_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_collect_acc_id:
                consumerid = true;
                rrno = false;
                searchcustomerdetails();
                search_result_layout.setHint(getResources().getString(R.string.collect_accountid));
                cleartextView();
                search_result.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;

            case R.id.menu_collect_rrno:
                consumerid = false;
                rrno = true;
                searchcustomerdetails();
                search_result_layout.setHint(getResources().getString(R.string.collect_rrno));
                cleartextView();
                search_result.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialize(View view) {

        search_btn = (Button) view.findViewById(R.id.collect_search_btn);
        submit_btn = (Button) view.findViewById(R.id.collect_submit_btn);

        search_result = (AutoCompleteTextView) view.findViewById(R.id.search_id);
        search_result_layout = (TextInputLayout) view.findViewById(R.id.layout_search_id);

        tv_name = (TextView) view.findViewById(R.id.collect_name);
        tv_rrno = (TextView) view.findViewById(R.id.collect_rrno);
        tv_acc_id = (TextView) view.findViewById(R.id.collect_account_id);
        tv_tariff = (TextView) view.findViewById(R.id.collect_tariff);
        tv_amt_due = (TextView) view.findViewById(R.id.collect_amt);

        et_payable_amt = (EditText) view.findViewById(R.id.collect_payable_amt);

        collection_details = (LinearLayout) view.findViewById(R.id.collection_details);

        amountinwords = new ArrayList<>();
        namelist = new ArrayList<>();
        arrayList = new ArrayList<>();

        dataBase = ((MainActivity) getActivity()).getDataBase();
        numtoWords = new NumtoWords();
        getSetValues = ((MainActivity) getActivity()).getvalues();
        data = new Sending_Data();
        functionsCall = new FunctionsCall();

        consumerid = true;
        num = new DecimalFormat("##.00");

        payableAmt();
        searchbtn();
        submitbtn();
        searchcustomerdetails();
    }

    private void payableAmt() {
        et_payable_amt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    et_payable_amt.setText(getResources().getString(R.string.rupee)+" "+s);
                    et_payable_amt.setSelection(et_payable_amt.length());
                }
                if (before == 1) {
                    if (start == 2) {
                        et_payable_amt.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void searchbtn() {
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!search_result.getText().toString().matches("")) {
                    GetCollection_Consumer_details consumer_details = data.new GetCollection_Consumer_details(getSetValues, mHandler);
                    if (consumerid)
                        consumer_details.execute("", search_result.getText().toString());
                    else consumer_details.execute(search_result.getText().toString(), "");
                    search_result.setEnabled(false);
                    search_btn.setVisibility(View.GONE);
                    collection_details.setVisibility(View.VISIBLE);
                } else {
                    if (consumerid)
                        Snackbar.make(search_btn, "Enter Consumer ID", Snackbar.LENGTH_SHORT).show();
                    else Snackbar.make(search_btn, "Enter RRNO", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        search_result.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!search_result.getText().toString().matches("")) {
                        GetCollection_Consumer_details consumer_details = data.new GetCollection_Consumer_details(getSetValues, mHandler);
                        if (consumerid)
                            consumer_details.execute("", search_result.getText().toString());
                        else consumer_details.execute(search_result.getText().toString(), "");
                        search_result.setEnabled(false);
                        search_btn.setVisibility(View.GONE);
                        collection_details.setVisibility(View.VISIBLE);
                    } else {
                        if (consumerid)
                            Snackbar.make(search_btn, "Enter Consumer ID", Snackbar.LENGTH_SHORT).show();
                        else Snackbar.make(search_btn, "Enter RRNO", Snackbar.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
    }

    private void submitbtn() {
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_payable_amt.getText().toString().matches("")) {
                    String amount = functionsCall.space("Amount", 7) + functionsCall.space(":", 2) + "Rs. " + numtoWords.convert(Integer.parseInt(et_payable_amt.getText().toString().substring(2))) + " only";
                    getSetValues.setAmt_Words(amount.substring(0, 13)+amount.substring(13, 14).toUpperCase()+amount.substring(14));
                    getSetValues.setPaid_Amount(getResources().getString(R.string.rupee)+" "+num.format(Double.parseDouble(et_payable_amt.getText().toString().substring(2))));
                    if (cons_amtdue.substring(2, cons_amtdue.length() - 3).matches("0"))
                        getSetValues.setBill_Amount(getResources().getString(R.string.rupee)+" "+"0.00");
                    else getSetValues.setBill_Amount(getResources().getString(R.string.rupee)+" "+num.format(Double.parseDouble(cons_amtdue.substring(2, cons_amtdue.length() - 3))));
                    getSetValues.setReceipt_Time(functionsCall.currentDateandTime());
                    functionsCall.splitString(getSetValues.getName(), 47, namelist);
                    functionsCall.splitString(getSetValues.getAmt_Words(), 47, amountinwords);
                    dealersprint = true;
                    if (amountinwords.size() == 1) {
                        amt_words_1 = amountinwords.get(0);
                    } else {
                        if (amountinwords.size() > 1) {
                            amt_words_1 = amountinwords.get(0);
                            amt_words_2 = amountinwords.get(1);
                        }
                    }
                    if (namelist.size() == 1) {
                        name_1 = namelist.get(0);
                    } else if (namelist.size() > 1) {
                        name_1 = namelist.get(0);
                        name_2 = namelist.get(1);
                    }
                    dealersprint = true;
                    progressDialog = ProgressDialog.show(getActivity(), "", getResources().getString(R.string.dealerprogressmsg), true);
                    es.submit(new TaskPrint(mCanvas));
                } else Snackbar.make(submit_btn, "Enter Amount", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void searchcustomerdetails() {
        Cursor result;
        if (consumerid) {
            result = dataBase.getconsumerids();
        } else result = dataBase.getrrnos();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, arrayList);
        search_result.setAdapter(adapter);
        arrayList.clear();
        adapter.notifyDataSetChanged();
        if (result.getCount() > 0) {
            while (result.moveToNext()) {
                if (consumerid)
                    arrayList.add(result.getString(result.getColumnIndex("CONSUMER_ID")));
                else arrayList.add(result.getString(result.getColumnIndex("RRNO")));
            }
            HashSet<String> hashSet = new HashSet<>();
            hashSet.addAll(arrayList);
            arrayList.clear();
            arrayList.addAll(hashSet);
            result.close();
            adapter.notifyDataSetChanged();
            search_result.setThreshold(2);
            search_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Search_Value = (String) parent.getItemAtPosition(position);
                    Cursor searchresult;
                    if (consumerid)
                        searchresult = dataBase.getresultbyconsumerid(Search_Value);
                    else searchresult = dataBase.getresultbyrrno(Search_Value);
                    appendSearchedValues(searchresult);
                }
            });
        }
    }

    private void appendSearchedValues(Cursor cursor) {
        cursor.moveToNext();
        cons_name = cursor.getString(cursor.getColumnIndex("NAME"));
        tv_name.setText(cons_name);
        cons_rrno = cursor.getString(cursor.getColumnIndex("RRNO"));
        tv_rrno.setText(cons_rrno);
        cons_id = cursor.getString(cursor.getColumnIndex("CONSUMER_ID"));
        tv_acc_id.setText(cons_id);
        cons_tariff = cursor.getString(cursor.getColumnIndex("TARIFF_NAME"));
        tv_tariff.setText(cons_tariff);
        cons_amtdue = getResources().getString(R.string.rupee)+" "+cursor.getString(cursor.getColumnIndex("PAYABLE_AMOUNT"))+" /-";
        tv_amt_due.setText(cons_amtdue);
        search_result.setEnabled(false);
        search_btn.setVisibility(View.GONE);
        collection_details.setVisibility(View.VISIBLE);
        setvalues();
    }

    private void appendSearchedJSONValues() {
        cons_name = getSetValues.getCollection_name();
        tv_name.setText(cons_name);
        cons_rrno = getSetValues.getCollection_RRNO();
        tv_rrno.setText(cons_rrno);
        cons_id = getSetValues.getCollection_CONS_ID();
        tv_acc_id.setText(cons_id);
        cons_tariff = getSetValues.getCollection_Tariff();
        tv_tariff.setText(cons_tariff);
        cons_amtdue = getResources().getString(R.string.rupee)+" "+getSetValues.getCollection_Amt_due()+" /-";
        tv_amt_due.setText(cons_amtdue);
        search_result.setEnabled(false);
        search_btn.setVisibility(View.GONE);
        collection_details.setVisibility(View.VISIBLE);
        setvalues();
    }

    private void setvalues() {
        getSetValues.setName(cons_name);
        getSetValues.setConsumer_ID(cons_id);
        getSetValues.setRRNo(cons_rrno);
        getSetValues.setReceipt_No("0012");
        getSetValues.setBill_Amount(cons_amtdue);
        getSetValues.setCounter_ID("1234567890");
        getSetValues.setTariff(cons_tariff);
    }

    private void cleartextView() {
        tv_name.setText("");
        tv_rrno.setText("");
        tv_acc_id.setText("");
        tv_tariff.setText("");
        tv_amt_due.setText("");
        et_payable_amt.setText("");
        search_result.setText("");
        search_result.setEnabled(true);
        search_btn.setVisibility(View.VISIBLE);
        collection_details.setVisibility(View.GONE);
    }

    public class TaskPrint implements Runnable {
        Canvas canvas = null;

        public TaskPrint(Canvas canvas) {
            this.canvas = canvas;
        }

        @Override
        public void run() {
            final boolean bPrintResult = PrintTicket(getActivity().getApplicationContext(), canvas, 576, 760);
            final boolean bIsOpened = canvas.GetIO().IsOpened();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), bPrintResult ? getResources().getString(R.string.printsuccess) : getResources().getString(R.string.printfailed), Toast.LENGTH_SHORT).show();
                    if (bIsOpened) {
                        yaxis = 0;
                        progressDialog.dismiss();
                        if (dealersprint) {
                            dealersprint = false;
                            showDialog(DLG_REPRINT);
                        } else if (customersprint) {
                            customersprint = false;
                            showDialog(DLG_EXIT);
                        }
                    } else {
                        progressDialog.dismiss();
                        showDialog(DLG_PRINT_FAIL);
                    }
                }
            });
        }

        public boolean PrintTicket(Context ctx, Canvas canvas, int nPrintWidth, int nPrintHeight) {
            boolean bPrintResult;

            Typeface tfNumber = Typeface.createFromAsset(ctx.getAssets(), "DroidSansMono.ttf");
            canvas.CanvasBegin(nPrintWidth, nPrintHeight);
            canvas.SetPrintDirection(0);

            printboldtext(canvas, functionsCall.centeralign("HESCOM", 26), tfNumber, 35);
            printtext(canvas, "", tfNumber, 25);
            printtext(canvas, functionsCall.line(63), tfNumber, 15);
            if (dealersprint)
                printtext(canvas, functionsCall.centeralign("*OFFICE COPY*", 37), tfNumber, 25);
            else printtext(canvas, functionsCall.centeralign("*CUSTOMER COPY*", 37), tfNumber, 25);
            printtext(canvas, functionsCall.centeralign("PAYMENT RECEIPT", 37), tfNumber, 25);
            printtext(canvas, functionsCall.line(63), tfNumber, 15);
            printtext(canvas, name_1, tfNumber, 20);
            if (!name_2.matches(""))
                printtext(canvas, name_2, tfNumber, 20);
            else printtext(canvas, " ", tfNumber, 20);
            printtext(canvas, functionsCall.space("Purpose", 17) + functionsCall.space(":", 2) + "Revenue", tfNumber, 25);
            printtext(canvas, functionsCall.space("Account ID", 17) + functionsCall.space(":", 2) + getSetValues.getConsumer_ID(), tfNumber, 25);
            printtext(canvas, functionsCall.space("RRNo", 12) + functionsCall.space(":", 2) + getSetValues.getRRNo(), tfNumber, 35);
            printtext(canvas, functionsCall.space("Receipt No", 17) + functionsCall.space(":", 2) + getSetValues.getReceipt_No(), tfNumber, 25);
            printtext(canvas, functionsCall.space("Bill Amount", 17) + functionsCall.space(":", 2) + getSetValues.getBill_Amount(), tfNumber, 25);
            printboldtext(canvas, functionsCall.space("Paid Amount", 12) + functionsCall.space(":", 2) + getSetValues.getPaid_Amount(), tfNumber, 35);
            printtext(canvas, amt_words_1, tfNumber, 20);
            if (amt_words_2.matches(""))
                printtext(canvas, " ", tfNumber, 20);
            else printtext(canvas, amt_words_2, tfNumber, 20);
            printtext(canvas, functionsCall.space("Paid Date", 17) + functionsCall.space(":", 2) + getSetValues.getReceipt_Time(), tfNumber, 25);
            printtext(canvas, functionsCall.space("CID", 5) + functionsCall.space(":", 2) + getSetValues.getCounter_ID(), tfNumber, 25);
            printtext(canvas, functionsCall.space(" ", 28), tfNumber, 25);
            printtext(canvas, functionsCall.space(" ", 28), tfNumber, 25);
            printtext(canvas, functionsCall.space(" ", 28) + "Sign", tfNumber, 25);
            printtext(canvas, functionsCall.line(45), tfNumber, 21);

            canvas.CanvasPrint(1, 0);

            bPrintResult = canvas.GetIO().IsOpened();
            return bPrintResult;
        }
    }

    private void printtext(Canvas canvas, String text, Typeface tfNumber, float textsize) {
        yaxis++;
        canvas.DrawText(text+"\r\n", 0, yaxis, 0, tfNumber, textsize, Canvas.DIRECTION_LEFT_TO_RIGHT);
        if (textsize == 20) {
            yaxis = yaxis + textsize + 8;
        } else yaxis = yaxis + textsize + 6;
    }

    private void printboldtext(Canvas canvas, String text, Typeface tfNumber, float textsize) {
        yaxis++;
        canvas.DrawText(text+"\r\n", 0, yaxis, 0, tfNumber, textsize, Canvas.FONTSTYLE_BOLD);
        if (textsize == 20) {
            yaxis = yaxis + textsize + 8;
        } else yaxis = yaxis + textsize + 6;
    }

    private void showDialog(int id) {
        AlertDialog print;
        switch (id) {
            case DLG_REPRINT:
                AlertDialog.Builder reprint = new AlertDialog.Builder(getActivity());
                reprint.setTitle(getResources().getString(R.string.paymentprint));
                reprint.setMessage(getResources().getString(R.string.customerprint));
                reprint.setCancelable(false);
                reprint.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customersprint = true;
                        progressDialog = ProgressDialog.show(getActivity(), "", getResources().getString(R.string.customerprogressmsg), true);
                        es.submit(new TaskPrint(mCanvas));
                    }
                });
                reprint.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refreshScreen();
                    }
                });
                print = reprint.create();
                print.show();
                print.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                print.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.RED);
                break;

            case DLG_EXIT:
                AlertDialog.Builder exitdlg = new AlertDialog.Builder(getActivity());
                exitdlg.setTitle(getResources().getString(R.string.paymentprint));
                exitdlg.setMessage(getResources().getString(R.string.exitdialogmsg));
                exitdlg.setCancelable(false);
                exitdlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refreshScreen();
                    }
                });
                print = exitdlg.create();
                print.show();
                break;

            case DLG_PRINT_FAIL:
                AlertDialog.Builder printer_fail = new AlertDialog.Builder(getActivity());
                printer_fail.setTitle(getResources().getString(R.string.paymentprint));
                printer_fail.setMessage(getResources().getString(R.string.printfail));
                printer_fail.setCancelable(false);
                printer_fail.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                print = printer_fail.create();
                print.show();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void refreshScreen() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_main, new Collection_Fragment()).commit();
    }
}
