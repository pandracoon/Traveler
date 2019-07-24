package com.example.tripscheduler.Place;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.tripscheduler.Travel.TravelAddFirstFragment.AUTOCOMPLETE_REQUEST_CODE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tripscheduler.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.Place.Field;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class PlaceAddMenuActivity extends AppCompatActivity {

  Button addButton1, addButton2;
  PlacesClient placeClient;
  List<Field> placeFields = Arrays.asList(Place.Field.ID, Field.NAME, Field.ADDRESS, Field.LAT_LNG);
  Context context = this;
  String name, strLatLng, label;
  byte[] imageArray;

  public final static int ADD_REQUEST = 2;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_placeadd_menu);

    addButton1 = findViewById(R.id.button2);
    addButton2 = findViewById(R.id.button6);

    initPlaces();

    addButton1.setOnClickListener(new OnClickListener() { //Todo 사진찍기,,
      @Override
      public void onClick(View view) {

      }
    });

    addButton2.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
            placeFields).build(context);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
      }
    });


  }

  private void initPlaces() {
    Places
        .initialize(context.getApplicationContext(), getString(R.string.places_api_key));
    placeClient = Places.createClient(context);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    switch (requestCode) {
      case AUTOCOMPLETE_REQUEST_CODE:
        if (resultCode == RESULT_OK) {
          Place place = Autocomplete.getPlaceFromIntent(intent);
          Intent placeIntent = new Intent(context, PlaceAddActivity.class);
          placeIntent.putExtra("place", place);
          placeIntent.putExtra("state", 2);
          startActivityForResult(placeIntent, ADD_REQUEST);

        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
          Status status = Autocomplete.getStatusFromIntent(intent);
          Log.i(TAG, status.getStatusMessage());
        } else if (resultCode == RESULT_CANCELED) {
          // The user canceled the operation.
        }
        break;
      case ADD_REQUEST:
        if (resultCode == RESULT_OK) {
          name = intent.getStringExtra("name");
          strLatLng = intent.getStringExtra("latLng");
          label = intent.getStringExtra("label");
          imageArray = intent.getByteArrayExtra("image");

          System.out.println(1);
          Intent addIntent = new Intent();
          System.out.println(2);
          addIntent.putExtra("name", name);
          addIntent.putExtra("latLng", strLatLng);
          addIntent.putExtra("label", label);
          addIntent.putExtra("image", imageArray);
          System.out.println(3);
          setResult(RESULT_OK, addIntent);
          System.out.println(4);
          finish();
          System.out.println(5);
        } else {

        }

        break;
    }
  }
}
