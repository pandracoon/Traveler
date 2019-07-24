package com.example.tripscheduler.Place;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.tripscheduler.R;
import com.example.tripscheduler.Server.BitmapArithmetic;
import com.example.tripscheduler.Server.StringConversion;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.Place.Field;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaceAddActivity extends AppCompatActivity implements OnMapReadyCallback {


  int state;
  ArrayList<String> arrayList = new ArrayList<String>();
  ArrayAdapter adapter;
  Spinner spinner;
  String name;
  LatLng latLng;
  String address;
  EditText titleEditText;
  String placeID;
  PlacesClient placeClient;
  Context context = this;
  List<Field> fields = Arrays.asList(Place.Field.PHOTO_METADATAS);
  ImageView imageView;
  Button button;
  String label;
  Bitmap resizedImage;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_placeadd);

    Toolbar toolbar = findViewById(R.id.placeAddToolBar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("회원가입");

    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    initPlaces();

    state = getIntent().getIntExtra("state", 0);

    arrayList.add("볼거리");
    arrayList.add("식당");
    arrayList.add("숙박");

    imageView = findViewById(R.id.imageView8);
    titleEditText = findViewById(R.id.editText8);
    spinner = findViewById(R.id.spinner2);
    adapter = new ArrayAdapter(getApplicationContext(),
        android.R.layout.simple_spinner_dropdown_item, arrayList);
    spinner.setAdapter(adapter);
    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        label = arrayList.get(i);
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });

    switch (state) {
      case 1:

        break;
      case 2:
        Place place = getIntent().getParcelableExtra("place");
        name = place.getName();
        address = place.getAddress();
        placeID = place.getId();
        Log.e("dd", "dd");
        FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeID, fields);
        placeClient.fetchPlace(placeRequest).addOnSuccessListener(
            new OnSuccessListener<FetchPlaceResponse>() {
              @Override
              public void onSuccess(FetchPlaceResponse response) {
                Place place = response.getPlace();
                PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
                String attributions = photoMetadata.getAttributions();
                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build();
                placeClient.fetchPhoto(photoRequest).addOnSuccessListener(
                    new OnSuccessListener<FetchPhotoResponse>() {
                      @Override
                      public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                        Bitmap bitmap = fetchPhotoResponse.getBitmap();
                        Bitmap scaledBitmap = Bitmap
                            .createScaledBitmap(bitmap, imageView.getWidth(), imageView.getHeight(),
                                true);
                        imageView.setImageBitmap(scaledBitmap);
                        resizedImage = BitmapArithmetic.resizeBitmap(scaledBitmap);

                      }
                    }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                    if (e instanceof ApiException) {
                      ApiException apiException = (ApiException) e;
                      int statusCode = apiException.getStatusCode();
                      Log.e("에러", "TPlace not found" + e.getMessage());
                    }
                  }
                });

              }
            });

        break;
      case 3:
        break;

    }

    titleEditText.setText(name);

    button = findViewById(R.id.button7);
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        name = titleEditText.getText().toString();
        String strLatLng = StringConversion.doubleToString_2(latLng.latitude, latLng.longitude);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bmp = resizedImage;
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();

        System.out.println(name);
        System.out.println(strLatLng);
        System.out.println(label);
        System.out.println(bytes);

        Intent intent = new Intent();
        System.out.println(1);
        intent.putExtra("name", name);
        System.out.println(2);
        intent.putExtra("latLng", strLatLng);
        System.out.println(3);
        intent.putExtra("label", label);
        System.out.println(4);
        intent.putExtra("image", bytes);
        System.out.println(5);
        setResult(RESULT_OK, intent);
        System.out.println(6);
        finish();
        System.out.println(7);
      }
    });

  }

  public void onMapReady(GoogleMap googleMap) {

    int state_map = getIntent().getIntExtra("state", 0);
    switch (state_map) {
      case 1:
        break;
      case 2:
        Place place = getIntent().getParcelableExtra("place");
        latLng = place.getLatLng();
        googleMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.setMinZoomPreference(15.0f);
        break;
      case 3:
        break;
    }

  }

  private void initPlaces() {
    Places
        .initialize(context.getApplicationContext(), getString(R.string.places_api_key));
    placeClient = Places.createClient(context);
  }


  @Override
  public void onBackPressed() {
    setResult(RESULT_CANCELED);
    super.onBackPressed();
  }
}
