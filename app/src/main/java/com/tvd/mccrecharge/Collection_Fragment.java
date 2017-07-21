package com.tvd.mccrecharge;


import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


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
                search_result.setText("");
                search_result.setEnabled(true);
                search_result.setInputType(InputType.TYPE_CLASS_NUMBER);
                search_result_layout.setHint(getResources().getString(R.string.collect_accountid));
                search_btn.setVisibility(View.VISIBLE);
                collection_details.setVisibility(View.GONE);
                break;

            case R.id.menu_collect_rrno:
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

        payableAmt();
        searchbtn();
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
}
