package com.tvd.mccrecharge;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Login_Fragment extends Fragment implements View.OnClickListener {
    View view;
    TextView tv_forgot;
    Button login_btn;
    FragmentTransaction fragmentTransaction;

    public Login_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);

        tv_forgot = (TextView) view.findViewById(R.id.main_forgot_password);
        tv_forgot.setOnClickListener(this);
        login_btn = (Button) view.findViewById(R.id.main_login_btn);
        login_btn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_forgot_password:
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, new Forgot_Fragment()).addToBackStack(null).commit();
                break;

            case R.id.main_login_btn:
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, new Collection_Fragment()).addToBackStack(null).commit();
                break;
        }
    }
}
