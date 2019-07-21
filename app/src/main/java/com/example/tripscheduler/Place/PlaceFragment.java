package com.example.tripscheduler.Place;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.tripscheduler.R;

public class PlaceFragment extends Fragment {

  Integer a = 0;
  TextView textView;
  String title;

  public PlaceFragment(String title) {
    this.title = title;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.layout_fragmentplace, container, false);

    textView = rootView.findViewById(R.id.textView5);
    textView.setText(title + "Place");

    Button button = rootView.findViewById(R.id.button2);
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        a++;
        textView.setText(a.toString());
      }
    });

    return rootView;
  }
}
