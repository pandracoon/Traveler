package com.example.tripscheduler.Schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.tripscheduler.R;

public class ScheduleFragment extends Fragment {

  String title;

  public ScheduleFragment(String title) {
    this.title = title;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.layout_fragmentschedule, container, false);

    TextView textView = rootView.findViewById(R.id.textView15);
    textView.setText(title + "Schedule");

    return rootView;
  }
}
