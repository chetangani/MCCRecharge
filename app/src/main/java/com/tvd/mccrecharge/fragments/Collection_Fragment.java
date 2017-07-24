package com.tvd.mccrecharge.fragments;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lvrenyang.io.Canvas;
import com.tvd.mccrecharge.MainActivity;
import com.tvd.mccrecharge.R;
import com.tvd.mccrecharge.database.DataBase;
import com.tvd.mccrecharge.services.BluetoothService;
import com.tvd.mccrecharge.values.NumtoWords;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class Collection_Fragment extends Fragment {
    View view;
    Button search_btn, submit_btn;
    AutoCompleteTextView search_result;
    TextInputLayout search_result_layout;
    TextView tv_name, tv_rrno, tv_acc_id, tv_tariff, tv_amt_due;
    EditText et_payable_amt;
    LinearLayout collection_details;
    float yaxis=0;
    Canvas mCanvas;
    ExecutorService es;
    ArrayList<String> res;
    DataBase dataBase;
    NumtoWords numtoWords;
    boolean consumerid = false, rrno = false;
    String Search_Value="";

    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;

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
                search_result.setText("");
                search_result.setEnabled(true);
                search_result.setInputType(InputType.TYPE_CLASS_NUMBER);
                search_result_layout.setHint(getResources().getString(R.string.collect_accountid));
                search_btn.setVisibility(View.VISIBLE);
                collection_details.setVisibility(View.GONE);
                break;

            case R.id.menu_collect_rrno:
                consumerid = false;
                rrno = true;
                search_result.setText("");
                search_result.setEnabled(true);
                search_result.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                search_result_layout.setHint(getResources().getString(R.string.collect_rrno));
                search_btn.setVisibility(View.VISIBLE);
                collection_details.setVisibility(View.GONE);
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

        res = new ArrayList<>();

        dataBase = ((MainActivity) getActivity()).getDataBase();
        numtoWords = new NumtoWords();

        BluetoothService service = new BluetoothService();
        mCanvas = service.getCanvas();
        es = service.getEs();
        consumerid = true;

        payableAmt();
        searchbtn();
        submitbtn();
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
                search_result.setEnabled(false);
                search_btn.setVisibility(View.GONE);
                collection_details.setVisibility(View.VISIBLE);
            }
        });
    }

    private void submitbtn() {
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_payable_amt.getText().toString().matches("")) {
                    String amount = numtoWords.convert(Integer.parseInt(et_payable_amt.getText().toString()));
                    splitString(amount.substring(0, 1).toUpperCase() + amount.substring(1), 47);
//                    es.submit(new TaskPrint(mCanvas));
                } else Snackbar.make(submit_btn, "Enter Amount", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void searchcustomerdetails() {
        Cursor result = null;
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
                    Cursor searchresult = null;
                    if (consumerid)
                        searchresult = dataBase.getresultbyconsumerid(Search_Value);
                    else searchresult = dataBase.getresultbyrrno(Search_Value);
                    appendSearchedValues(searchresult);
                }
            });
        }
    }

    private void appendSearchedValues(Cursor cursor) {
        tv_name.setText(cursor.getString(cursor.getColumnIndex("NAME")));
        tv_rrno.setText(cursor.getString(cursor.getColumnIndex("RRNO")));
        tv_acc_id.setText(cursor.getString(cursor.getColumnIndex("CONSUMER_ID")));
        tv_tariff.setText(cursor.getString(cursor.getColumnIndex("TARIFF_NAME")));
        tv_amt_due.setText(cursor.getString(cursor.getColumnIndex("PAYABLE_AMOUNT")));
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

    private String space(String s, int length) {
        int temp;
        StringBuilder spaces = new StringBuilder();
        temp = length - s.length();
        for (int i = 0; i < temp; i++) {
            spaces.append(" ");
        }
        return (s + spaces);
    }

    private String centeralign(String text, int width) {
        int count = text.length();
        int value = width - count;
        int append = (value / 2);
        return space(" ", append) + text;
    }

    private String rightspacing(String s1, int len) {
        for (int i = 0; i < len - s1.length(); i++) {
            s1 = " " + s1;
        }
        return (s1);
    }

    private void splitString(String msg, int lineSize) {
        res.clear();
        Pattern p = Pattern.compile("\\b.{0," + (lineSize-1) + "}\\b\\W?");
        Matcher m = p.matcher(msg);
        while(m.find()) {
            res.add(m.group().trim());
        }
    }

    public class TaskPrint implements Runnable {
        Canvas canvas = null;

        public TaskPrint(Canvas canvas) {
            this.canvas = canvas;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            final boolean bPrintResult = PrintTicket(getActivity(), canvas, 576, 500);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(getActivity(), bPrintResult ? getResources().getString(R.string.printsuccess) : getResources().getString(R.string.printfailed), Toast.LENGTH_SHORT).show();
                }
            });
        }

        public boolean PrintTicket(Context ctx, Canvas canvas, int nPrintWidth, int nPrintHeight) {
            boolean bPrintResult = false;

            Typeface tfNumber = Typeface.createFromAsset(ctx.getAssets(), "DroidSansMono.ttf");
            canvas.CanvasBegin(nPrintWidth, nPrintHeight);
            canvas.SetPrintDirection(0);

            canvas.CanvasEnd();
            canvas.CanvasPrint(1, 0);

            bPrintResult = canvas.GetIO().IsOpened();
            return bPrintResult;
        }
    }
}
