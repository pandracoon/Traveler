package com.example.tripscheduler.Place;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import retrofit2.Retrofit;

public class PlaceMapActivity extends AppCompatActivity implements OnMapReadyCallback {

  GoogleMap gMap;
  private final String SERVER = "http://143.248.36.205:3000";
  private CompositeDisposable compositeDisposable = new CompositeDisposable();
  private IAppService iAppService;
  Context context = this;
  private ArrayList<TPlace> placeItemList = new ArrayList<>();

  String email, title;

  boolean isLoaded = false;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_map);

    Retrofit retrofitClient = RetrofitClient.getInstance();
    iAppService = retrofitClient.create(IAppService.class);

    email = getIntent().getStringExtra("email");
    title = getIntent().getStringExtra("title");

    getPlaceList();

    Toolbar toolbar = findViewById(R.id.placeMapToolBar);
    toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("장소 리스트");

    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.placeMap);
    mapFragment.getMapAsync(this);

  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    gMap = googleMap;

    if (isLoaded) {

      LatLng latLng = new LatLng(1, 1);

      System.out.println(placeItemList.size());

      for (int i = 0; i < placeItemList.size(); i++) {

        String name = placeItemList.get(i).getData("name").replace("\"", "");
        latLng = convertLatLng(placeItemList.get(i).getData("location"));
        googleMap.addMarker(new MarkerOptions().position(latLng).title(name));

      }
      googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
      googleMap.setMinZoomPreference(10.0f);

    }

  }

  private LatLng convertLatLng(String strLatLng) {
    Double lat = Double.parseDouble(strLatLng.split("\"")[1]);
    Double lng = Double.parseDouble(strLatLng.split("\"")[3]);

    return new LatLng(lat, lng);
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
              isLoaded = true;
              onMapReady(gMap);
            }
          }
        }));
  }
}
