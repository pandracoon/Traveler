package com.example.tripscheduler.Travel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tripscheduler.Place.Place;
import com.example.tripscheduler.R;
import com.example.tripscheduler.Server.IAppService;
import com.example.tripscheduler.Server.RetrofitClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class TravelAddActivity extends AppCompatActivity {

  private final String SERVER = "http://143.248.36.205:3000";
  private CompositeDisposable compositeDisposable = new CompositeDisposable();
  private IAppService iAppService;

  Button nextButton;
  StepView stepView;
  private FragmentManager fragmentManager;
  String title, area, startDate, endDate;
  int state = 0;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_traveladd);

    Retrofit retrofitClient = RetrofitClient.getInstance();
    iAppService = retrofitClient.create(IAppService.class);

    area = "";
    startDate = "";
    endDate = "";
    title = "";
    fragmentManager = getSupportFragmentManager();
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.add(R.id.tripAddFrame, new TravelAddFirstFragment("")).commit();

    stepView = findViewById(R.id.step_view);
    stepView.getState()
        // You should specify only stepsNumber or steps array of strings.
        // In case you specify both steps array is chosen.
        .steps(new ArrayList<String>() {{
          add("Area");
          add("Date");
          add("Title");
        }})
        // You should specify only steps number or steps array of strings.
        // In case you specify both steps array is chosen.
        .stepsNumber(4)
        .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
        .stepLineWidth(getResources().getDimensionPixelSize(R.dimen.dp1))
        .textSize(getResources().getDimensionPixelSize(R.dimen.sp14))
        .stepNumberTextSize(getResources().getDimensionPixelSize(R.dimen.sp16))
        // other state methods are equal to the corresponding xml attributes
        .commit();


    nextButton = findViewById(R.id.button5);
    nextButton.setText("NEXT");
    nextButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        switch (state) {
          case 0:
            if (area == "") {
              Toast.makeText(getApplicationContext(), "여행지를 선택하세요.", Toast.LENGTH_SHORT)
                  .show();

            } else {
              stepView.go(1, true);
              state++;
              FragmentTransaction transaction = fragmentManager.beginTransaction();
              transaction
                  .replace(R.id.tripAddFrame, new TravelAddSecondFragment(startDate, endDate))
                  .commit();
            }
            break;
          case 1:
            if (startDate == "" || endDate == "") {
              Toast.makeText(getApplicationContext(), "여행 기간을 선택하세요.", Toast.LENGTH_SHORT)
                  .show();
            } else {
              stepView.go(2, true);
              state++;
              FragmentTransaction transaction = fragmentManager.beginTransaction();
              transaction
                  .replace(R.id.tripAddFrame,
                      new TravelAddThirdFragment(title, area, startDate, endDate))
                  .commit();
              nextButton.setText("DONE");
            }
            break;
          case 2:
            stepView.done(true);
            if (title == "") {
              Toast.makeText(getApplicationContext(), "여행 이름을 입력하세요.", Toast.LENGTH_SHORT)
                  .show();

            } else {
              String[] travelData = new String[4];
              travelData[0] = title;
              travelData[1] = area;
              travelData[2] = startDate;
              travelData[3] = endDate;

              Intent backIntent = new Intent();
              backIntent.putExtra("title", travelData);
              setResult(RESULT_OK, backIntent);
              finish();

              break;
            }
        }
      }
    });

  }

  public void setData(String key, String data) {
    switch (key) {
      case "area":
        area = data;
        break;
      case "startDate":
        startDate = data;
        break;
      case "endDate":
        endDate = data;
        break;
      case "title":
        title = data;
        break;
    }
  }

  @Override
  public void onBackPressed() {
    switch (state) {
      case 0:
        Intent backIntent = new Intent();
        setResult(RESULT_CANCELED, backIntent);
        finish();
        break;
      case 1:
        stepView.go(0, true);
        state--;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.tripAddFrame, new TravelAddFirstFragment(area)).commit();
        break;
      case 2:
        stepView.go(1, true);
        state--;
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.tripListView, new TravelAddSecondFragment(startDate, endDate))
            .commit();
        nextButton.setText("NEXT");
        break;
    }
  }
}
