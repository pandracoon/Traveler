package com.example.tripscheduler.Schedule;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.tripscheduler.R;
import com.example.tripscheduler.Server.IAppService;
import com.example.tripscheduler.Server.Model;
import com.example.tripscheduler.Server.Model.DirectionResults;
import com.example.tripscheduler.Server.Model.OverviewPolyLine;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ScheduleEdgeMapActivity extends AppCompatActivity implements OnMapReadyCallback {

  GoogleMap gMap;
  Schedule schedule1, schedule2;
  String polyLineCode;
  boolean isContained = false;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_map);

    schedule1 = (Schedule) getIntent().getSerializableExtra("schedule1");
    schedule2 = (Schedule) getIntent().getSerializableExtra("schedule2");

    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.placeMap);
    mapFragment.getMapAsync(this);

    getPolyLineCode(schedule1, schedule2);

    Toolbar toolbar = findViewById(R.id.placeMapToolBar);
    toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("이동 정보");

  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    gMap = googleMap;

    if (isContained) {

      googleMap.addMarker(new MarkerOptions().position(convertLatLng(schedule1.getData("location"))).title(schedule1.getData("name").replace("\"","")).icon(
          BitmapDescriptorFactory
              .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
      googleMap.addMarker(new MarkerOptions().position(convertLatLng(schedule2.getData("location"))).title(schedule2.getData("name").replace("\"","")));
      googleMap.moveCamera(CameraUpdateFactory.newLatLng(convertLatLng(schedule1.getData("location"))));
      googleMap.setMinZoomPreference(12.0f);


      List<LatLng> decodedPath = PolyUtil.decode(polyLineCode);

      googleMap.addPolyline(new PolylineOptions().addAll(decodedPath));


    }
  }

  private LatLng convertLatLng(String strLatLng) {
    Double lat = Double.parseDouble(strLatLng.split("\"")[1]);
    Double lng = Double.parseDouble(strLatLng.split("\"")[3]);

    return new LatLng(lat, lng);
  }

  private void getPolyLineCode(Schedule schedule1, Schedule schedule2) {
    LatLng latLng1 = convertLatLng(schedule1.getData("location"));
    LatLng latLng2 = convertLatLng(schedule2.getData("location"));

    String base_url = "https://maps.googleapis.com/";

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(base_url)
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    IAppService request = retrofit.create(IAppService.class);

    String key = "AIzaSyAp5fwKE7aSSRG-Yclw9aNXI0quf8Hj7qA";

//    System.out.println(latLng1.toString());
//    System.out.println(latLng2.toString());
//
//    latLng1.latitude

    Call<DirectionResults> req = request
        .getJson(latLng1.latitude + "," + latLng1.longitude,latLng2.latitude + "," + latLng2.longitude, key, "transit");
    req.enqueue(new Callback<DirectionResults>() {
      @Override
      public void onResponse(Call<Model.DirectionResults> call,
          Response<DirectionResults> response) {
        Log.d("CallBack", " response is " + response);
        Model.Route route = response.body().getRoutes().get(0);

        Model.OverviewPolyLine overviewPolyLine = route.getOverviewPolyLine();

        polyLineCode = overviewPolyLine.getPoints();
        isContained = true;

        onMapReady(gMap);
      }

      @Override
      public void onFailure(Call<Model.DirectionResults> call,
          Throwable t) {
        Log.d("CallBack", " Throwable is " + t);
      }
    });
  }
}
