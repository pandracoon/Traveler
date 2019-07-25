package com.example.tripscheduler.Schedule;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.tripscheduler.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ScheduleVertexMapActivity extends AppCompatActivity implements OnMapReadyCallback {

  GoogleMap gMap;
  Schedule schedule;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_map);

    schedule = (Schedule) getIntent().getSerializableExtra("schedule");
    String name = schedule.getData("name");
    Toolbar toolbar = findViewById(R.id.placeMapToolBar);
    toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(name);

    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.placeMap);
    mapFragment.getMapAsync(this);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    gMap = googleMap;

    Schedule schedule = (Schedule) getIntent().getSerializableExtra("schedule");

    String name =schedule.getData("name").replace("\"", "");
    LatLng latLng = convertLatLng(schedule.getData("location"));
    googleMap.addMarker(new MarkerOptions().position(latLng).title(name));
    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    googleMap.setMinZoomPreference(15.0f);

  }

  private LatLng convertLatLng(String strLatLng) {
    Double lat = Double.parseDouble(strLatLng.split("\"")[1]);
    Double lng = Double.parseDouble(strLatLng.split("\"")[3]);

    return new LatLng(lat, lng);
  }
}
