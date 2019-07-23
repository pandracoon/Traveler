package com.example.tripscheduler.Travel;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.tripscheduler.R;

public class TravelAddThirdFragment extends Fragment {

  String title, area, startDate, endDate;

  public TravelAddThirdFragment(String title, String area, String startDate, String endDate) {
    this.title = title;
    this.area = area;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.layout_traveladd_third, container, false);

    TextView dateTextView = rootView.findViewById(R.id.textView23);
    TextView areaTextView = rootView.findViewById(R.id.textView24);
    dateTextView.setText(startDate + " ~ " + endDate);
    areaTextView.setText(area);

    EditText editText = rootView.findViewById(R.id.editText7);
    editText.setText(title);
    editText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        title = charSequence.toString();
        ((TravelAddActivity) getActivity()).setData("title", title);
      }

      @Override
      public void afterTextChanged(Editable editable) {

      }
    });


    return rootView;
  }
}
