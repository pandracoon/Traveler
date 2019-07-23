package com.example.tripscheduler.Travel;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.tripscheduler.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TravelAddSecondFragment extends Fragment {

  TextView startText, endText;
  String startDate, endDate;

  public TravelAddSecondFragment(String startDate, String endDate){
    this.startDate = startDate;
    this.endDate = endDate;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.layout_traveladd_second, container, false);

    startText = rootView.findViewById(R.id.textView20);
    endText = rootView.findViewById(R.id.textView21);

    startText.setText(startDate);
    endText.setText(endDate);

    startText.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Calendar pickedDate = Calendar.getInstance();
        Calendar minDate = Calendar.getInstance();

        pickedDate.setTimeInMillis(System.currentTimeMillis());
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            getActivity(),
            new DatePickerDialog.OnDateSetListener() {
              @Override
              public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                ((TravelAddActivity) getActivity())
                    .setData("startDate", year + "." + month + "." + dayOfMonth);
                startText.setText(year + "." + month + "." + dayOfMonth);
                startDate = year + "." + month + "." + dayOfMonth;

                setEndDateListener(year, month, dayOfMonth);
              }
            },
            pickedDate.get(Calendar.YEAR),
            pickedDate.get(Calendar.MONTH),
            pickedDate.get(Calendar.DATE)
        );

        minDate.set(2018, 2, 10);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTime().getTime());

        datePickerDialog.show();
      }
    });

    endText.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast
            .makeText(getActivity().getApplicationContext(), "출발일을 먼저 설정하세요.", Toast.LENGTH_SHORT)
            .show();
      }
    });

    return rootView;
  }

  private void setEndDateListener(final int Syear, final int Smonth, final int SdayOfMonth) {

    endText.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Calendar pickedDate = Calendar.getInstance();
        Calendar minDate = Calendar.getInstance();

        if (startText.getText().toString() == null) {
          Toast
              .makeText(getActivity().getApplicationContext(), "출발일을 먼저 설정하세요.", Toast.LENGTH_SHORT)
              .show();
          return;
        }

        pickedDate.set(Syear, Smonth - 1, SdayOfMonth);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            getActivity(),
            new DatePickerDialog.OnDateSetListener() {
              @Override
              public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                ((TravelAddActivity) getActivity())
                    .setData("endDate", year + "." + month + "." + dayOfMonth);
                endText.setText(year + "." + month + "." + dayOfMonth);
                endDate = year + "." + month + "." + dayOfMonth;
              }
            },
            pickedDate.get(Calendar.YEAR),
            pickedDate.get(Calendar.MONTH),
            pickedDate.get(Calendar.DATE)
        );

        minDate.set(Syear, Smonth - 1, SdayOfMonth);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTime().getTime());

        datePickerDialog.show();
      }
    });

  }
}
