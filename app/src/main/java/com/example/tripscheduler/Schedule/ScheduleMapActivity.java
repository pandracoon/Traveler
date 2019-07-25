package com.example.tripscheduler.Schedule;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.tripscheduler.R;
import com.example.tripscheduler.Server.IAppService;
import com.example.tripscheduler.Server.RetrofitClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Collections;
import retrofit2.Retrofit;

public class ScheduleMapActivity extends AppCompatActivity implements OnMapReadyCallback {

  GoogleMap gMap;
  String email, title;
  int sectionNumber;
  ArrayList<Schedule> scheduleList = new ArrayList<>();
  IAppService apiService;
  private final String SERVER = "http://143.248.36.205:3000";
  private CompositeDisposable compositeDisposable = new CompositeDisposable();
  private IAppService iAppService;


  boolean isFilled = false;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_map);

    Retrofit retrofitClient = RetrofitClient.getInstance();
    iAppService = retrofitClient.create(IAppService.class);

    email = getIntent().getStringExtra("email");
    title = getIntent().getStringExtra("title");
    sectionNumber = getIntent().getIntExtra("sectionNumber", 0);
    String titleText = ((Integer) (sectionNumber + 1)) + "일차 일정";

    getScheduleList();

    Toolbar toolbar = findViewById(R.id.placeMapToolBar);
    toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(titleText);

    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.placeMap);
    mapFragment.getMapAsync(this);
  }


  @Override
  public void onMapReady(GoogleMap googleMap) {
    gMap = googleMap;

    if (isFilled) {
      scheduleList = excludeTransportation(scheduleList);
      PolylineOptions polylineOptions = new PolylineOptions();
      String firstName = scheduleList.get(0).getData("name").replace("\"", "");
      LatLng latLng = convertLatLng(scheduleList.get(0).getData("location"));
      googleMap.addMarker(
          new MarkerOptions().position(latLng).title(firstName).icon(BitmapDescriptorFactory
              .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
      polylineOptions.add(latLng);
      googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
      googleMap.setMinZoomPreference(10.0f);

      for (int i = 1; i < scheduleList.size(); i++) {
        String name = scheduleList.get(i).getData("name").replace("\"", "");
        latLng = convertLatLng(scheduleList.get(i).getData("location"));
        googleMap.addMarker(new MarkerOptions().position(latLng).title(name));
        polylineOptions.add(latLng);

      }

      Polyline polyline = googleMap.addPolyline(polylineOptions);


    }
  }

  public ArrayList<Schedule> excludeTransportation(ArrayList<Schedule> schedules) {

    ArrayList<Schedule> newScheduleList = new ArrayList<>();

    for (int i = 0; i < schedules.size(); i++) {
      if (!schedules.get(i).getData("label").replace("\"", "").equals("이동")) {
        newScheduleList.add(schedules.get(i));
      }
    }

    return newScheduleList;
  }


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

  private LatLng convertLatLng(String strLatLng) {
    Double lat = Double.parseDouble(strLatLng.split("\"")[1]);
    Double lng = Double.parseDouble(strLatLng.split("\"")[3]);

    return new LatLng(lat, lng);
  }

  private void getScheduleList() {
    compositeDisposable.add(iAppService.schedule_get_one(email.replace("\"", ""), title)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .retry()
        .subscribe(new Consumer<String>() {
          @Override
          public void accept(String data) throws Exception {
            Log.e("schedule_get_one", data);

            if (data.equals("0")) {
              System.out.println("No data existed");
            } else {

              JsonParser jsonParser = new JsonParser();
              JsonArray jsonArray = (JsonArray) jsonParser.parse(data);

              for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject object = (JsonObject) jsonArray.get(i);
                System.out.println(object.get("date").toString().replace("-", "."));
                System.out.println(object.get("schedule"));

                JsonArray array = (JsonArray) object.get("schedule");
                for (int j = 0; j < array.size(); j++) {
                  JsonObject object2 = (JsonObject) array.get(j);
                  System.out.println(object2.get("name"));
                  System.out.println(object2.get("location"));
                  System.out.println(object2.get("label"));
                  System.out.println(object2.get("memo"));
                  System.out.println(object2.get("start"));
                  System.out.println(object2.get("duration"));

                  JsonArray location = (JsonArray) object2.get("location");
                  System.out.println(location.get(0));
                  System.out.println(location.get(1));

                  if (sectionNumber == i) {
                    scheduleList.add(new Schedule(email, title, object2.get("name").toString(),
                        object2.get("location").toString(),
                        object2.get("label").toString(), object2.get("memo").toString(),
                        object2.get("start").toString(),
                        object2.get("duration").toString()));
                    Collections.sort(scheduleList);

                  }
                }
              }
              isFilled = true;
              onMapReady(gMap);
            }
          }
        }));
  }
}
