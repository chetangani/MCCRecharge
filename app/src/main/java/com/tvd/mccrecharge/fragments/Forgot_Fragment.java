package com.tvd.mccrecharge.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tvd.mccrecharge.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Forgot_Fragment extends Fragment {
    View view;
    Button forgot_btn;

    public Forgot_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_forgot, container, false);

        forgot_btn = (Button) view.findViewById(R.id.forgot_submit_btn);

        forgot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, new Login_Fragment()).commit();
            }
        });

        return view;
    }

}
