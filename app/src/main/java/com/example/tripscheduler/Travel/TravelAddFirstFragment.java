package com.example.tripscheduler.Travel;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tripscheduler.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.Place.Field;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import java.util.Arrays;
import java.util.List;

public class TravelAddFirstFragment extends Fragment {

  private String area;
  private TextView textView;

  public TravelAddFirstFragment(String area) {
    this.area = area;
  }

  PlacesClient placeClient;
  List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Field.NAME, Field.ADDRESS);

  public static final int AUTOCOMPLETE_REQUEST_CODE = 1;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.layout_traveladd_first, container, false);

    initPlaces();

    final Context context = this.getActivity().getApplicationContext();

    textView = rootView.findViewById(R.id.textView19);
    textView.setText(area);
    textView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
            placeFields)
            .build(context);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
      }

    });

    ImageView imageView = rootView.findViewById(R.id.imageView4);
    textView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
            placeFields)
            .build(context);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
      }

    });

    return rootView;
  }

  private void initPlaces() {
    Places
        .initialize(this.getActivity().getApplicationContext(), getString(R.string.places_api_key));
    placeClient = Places.createClient(this.getActivity());
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);

    if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        Place place = Autocomplete.getPlaceFromIntent(intent);
        textView.setText(place.getName());
        ((TravelAddActivity) getActivity()).setData("area", place.getName());
      } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
        Status status = Autocomplete.getStatusFromIntent(intent);
        Log.i(TAG, status.getStatusMessage());
      } else if (resultCode == RESULT_CANCELED) {
        // The user canceled the operation.
      }
    }
  }
}
