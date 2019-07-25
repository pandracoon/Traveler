package com.example.tripscheduler.Schedule;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.tripscheduler.Place.PlaceListViewAdapter;
import com.example.tripscheduler.Place.TPlace;
import com.example.tripscheduler.R;
import com.example.tripscheduler.Server.IAppService;
import com.example.tripscheduler.Server.RetrofitClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class ScheduleAddActivity extends AppCompatActivity implements OnMapReadyCallback {

  String email, title;
  String name, type, strLatLng, startTime, endTime, memo;
  LatLng latLng;
  TextView nameTextView, typeTextView, startTimeTextView, endTimeTextView;
  EditText memoEditText;
  Button placeButton, scheduleButton;
  Toolbar toolbar;
  private ArrayList<TPlace> placeItemList = new ArrayList<>();
  TPlace selectedPlace;
  int isClicked;

  PlaceListViewAdapter adapter;

  GoogleMap gMap;

  private final String SERVER = "http://143.248.36.205:3000";
  private CompositeDisposable compositeDisposable = new CompositeDisposable();
  private IAppService iAppService;
  Context context = this;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_scheduleadd);

    Retrofit retrofitClient = RetrofitClient.getInstance();
    iAppService = retrofitClient.create(IAppService.class);

    email = getIntent().getStringExtra("email");
    title = getIntent().getStringExtra("title");

    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.scheduleMap);
    mapFragment.getMapAsync(this);

    toolbar = findViewById(R.id.scheduleToolBar);
    toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("일정 추가");

    nameTextView = findViewById(R.id.textView31);
    typeTextView = findViewById(R.id.textView32);
    startTimeTextView = findViewById(R.id.textView33);
    endTimeTextView = findViewById(R.id.textView34);
    memoEditText = findViewById(R.id.editText9);
    placeButton = findViewById(R.id.button20);
    scheduleButton = findViewById(R.id.button21);
    getPlaceList();

    placeButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        showAlertDialog();
      }
    });

    startTimeTextView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        TimePickerDialog picker = new TimePickerDialog(context,
            android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            new TimePickerDialog.OnTimeSetListener() {
              @Override
              public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                StringBuffer strBuf = new StringBuffer();
                strBuf.append(sHour);
                strBuf.append(":");
                if (sMinute < 10) {
                  strBuf.append(0);
                }
                strBuf.append(sMinute);
                startTime = strBuf.toString();
                startTimeTextView.setText(startTime);
              }
            }, 12, 0, true);
        picker.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        picker.show();
      }
    });

    endTimeTextView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        TimePickerDialog picker = new TimePickerDialog(context,
            android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            new TimePickerDialog.OnTimeSetListener() {
              @Override
              public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                StringBuffer strBuf = new StringBuffer();
                strBuf.append(sHour);
                strBuf.append(":");
                if (sMinute < 10) {
                  strBuf.append(0);
                }
                strBuf.append(sMinute);
                endTime = strBuf.toString();
                endTimeTextView.setText(endTime);
              }
            }, 12, 0, true);
        picker.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        picker.show();
      }
    });

    scheduleButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        int startMin = Integer.parseInt(startTime.split(":")[0]) * 60 + Integer
            .parseInt(startTime.split(":")[1]);

        int endMin = Integer.parseInt(endTime.split(":")[0]) * 60 + Integer
            .parseInt(endTime.split(":")[1]);

        memo = memoEditText.getText().toString();

        Integer duration = endMin - startMin;
        if (duration < 0) {
          Toast.makeText(getApplicationContext(), "시간을 올바르게 설정하세요.", Toast.LENGTH_SHORT)
              .show();
        }

        Schedule schedule = new Schedule(email, title, name, strLatLng, type, memo,
            startTime.replace(":", " "), duration.toString());

        Intent intent = new Intent();
        intent.putExtra("schedule", schedule);
        setResult(RESULT_OK, intent);
        finish();

      }

    });

  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    gMap = googleMap;

    if (isClicked == 1) {
      googleMap.addMarker(new MarkerOptions().position(latLng).title(name));
      googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
      googleMap.setMinZoomPreference(15.0f);
    }
  }

//  @Override
//  public void onBackPressed() {
//    toolbar.setVisibility(View.GONE);
//
//    getSupportActionBar().hide();
//    super.onBackPressed();
//  }

  @Override
  public boolean onOptionsItemSelected(
      @NonNull MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void showAlertDialog() {

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    LayoutInflater inflater = getLayoutInflater();
    View view = inflater.inflate(R.layout.layout_placelist_dialog, null);
    builder.setView(view);

    final ListView listview = (ListView) view.findViewById(R.id.listview_alterdialog_list);
    final AlertDialog dialog = builder.create();
    Button button = view.findViewById(R.id.button8);
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if (selectedPlace == null) {
          Toast.makeText(getApplicationContext(), "장소를 선택하세요.", Toast.LENGTH_SHORT)
              .show();
        } else {
          name = selectedPlace.getData("name").replace("\"", "");
          nameTextView.setText(name);
          type = selectedPlace.getData("label").replace("\"", "");

          typeTextView.setText(type);
          strLatLng = selectedPlace.getData("location");
          String lat = strLatLng.split("\"")[1];
          String lng = strLatLng.split("\"")[3];
          latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

          isClicked = 1;
          onMapReady(gMap);
          dialog.dismiss();
        }

      }
    });

    adapter = new PlaceListViewAdapter(placeItemList);

    listview.setAdapter(adapter);
    listview.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedPlace = (TPlace) parent.getItemAtPosition(position);
      }
    });

    dialog.setCancelable(true);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    dialog.show();
  }

  public void getPlaceList() {
    compositeDisposable.add(iAppService.places_get_one(email.replace("\"", ""), title)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .retry()
        .subscribe(new Consumer<String>() {
          @Override
          public void accept(String data) throws Exception {
            Log.e("places_get_one", data);

            if (data.equals("0")) {
              System.out.println("No data existed");
            } else {
              JsonParser jsonParser = new JsonParser();
              JsonArray jsonArray = (JsonArray) jsonParser.parse(data);
              for (int i = 0; i < jsonArray.size(); i++) {
                final JsonObject object = (JsonObject) jsonArray.get(i);
                System.out.println(object.get("name"));
                System.out.println(object.get("location"));
                System.out.println(object.get("label"));
                System.out.println(object.get("path"));

                JsonArray location = (JsonArray) object.get("location");
                System.out.println(location.get(0));
                System.out.println(location.get(1));

                System.out.println(SERVER + "/" + object.get("path").toString().replace("\"", ""));

                String url = SERVER + "/" + object.get("path").toString().replace("\"", "");

                placeItemList.add(
                    new TPlace(object.get("name").toString(), object.get("location").toString(),
                        object.get("label").toString(), url));

              }
            }
          }
        }));
  }
}
