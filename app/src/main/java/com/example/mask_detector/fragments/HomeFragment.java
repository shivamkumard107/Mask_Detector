package com.example.mask_detector.fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mask_detector.R;

public class HomeFragment extends Fragment {
    private CardView cv1, cv2, cv3;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        cv1 = v.findViewById(R.id.cardView2);
        cv2 = v.findViewById(R.id.cardView3);
        cv3 = v.findViewById(R.id.helpCard3);

        cv1.setOnClickListener(appointment ->{

        });
        return v;
    }
}