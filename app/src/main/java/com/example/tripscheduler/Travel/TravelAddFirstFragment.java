package com.example.tripscheduler.Travel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tripscheduler.R;

public class TravelAddFirstFragment extends Fragment {
    private String area;

    public TravelAddFirstFragment(String area) {
        this.area = area;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_traveladd_first, container, false);

        return rootView;
    }
}
