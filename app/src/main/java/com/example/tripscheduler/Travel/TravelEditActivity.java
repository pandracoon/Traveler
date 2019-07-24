package com.example.tripscheduler.Travel;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import com.example.tripscheduler.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.Place.Field;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class TravelEditActivity extends AppCompatActivity {

  Toolbar toolbar;
  TextView areaTextView, startDateTextView, endDateTextView;
  EditText titleEditText;
  ImageView imageView;
  String title, area, startDate, endDate;
  PlacesClient placeClient;
  List<Field> placeFields = Arrays.asList(Place.Field.ID, Field.NAME, Field.ADDRESS);
  Travel travel;

  public static final int AUTOCOMPLETE_REQUEST_CODE = 1;
  Context context = this;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_traveledit);

    toolbar = findViewById(R.id.tripEditToolBar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("여행 정보");

    travel = (Travel) getIntent().getSerializableExtra("travel");
    titleEditText = findViewById(R.id.editText10);
    areaTextView = findViewById(R.id.textView70);
    startDateTextView = findViewById(R.id.textView71);
    endDateTextView = findViewById(R.id.textView72);
    imageView = findViewById(R.id.imageView7);
    title = travel.getTitle();
    area = travel.getArea();
    startDate = travel.getData("startDate");
    endDate = travel.getData("endDate");

    titleEditText.setText(title);
    areaTextView.setText(area);
    startDateTextView.setText(startDate);
    endDateTextView.setText(endDate);

    initPlaces();

    areaTextView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
            placeFields)
            .build(context);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
      }
    });

    imageView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
            placeFields)
            .build(context);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
      }
    });

    startDateTextView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Calendar pickedDate = Calendar.getInstance();
        Calendar minDate = Calendar.getInstance();

        pickedDate.setTimeInMillis(System.currentTimeMillis());
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            context,
            new DatePickerDialog.OnDateSetListener() {
              @Override
              public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                startDateTextView.setText(year + "." + month + "." + dayOfMonth);
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

  }

  private void initPlaces() {
    Places
        .initialize(this.getApplicationContext(), getString(R.string.places_api_key));
    placeClient = Places.createClient(this);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);

    if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        Place place = Autocomplete.getPlaceFromIntent(intent);
        areaTextView.setText(place.getName());
        area = place.getName();
      } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
        Status status = Autocomplete.getStatusFromIntent(intent);
        Log.i(TAG, status.getStatusMessage());
      } else if (resultCode == RESULT_CANCELED) {
        // The user canceled the operation.
      }
    }
  }

  private void setEndDateListener(final int Syear, final int Smonth, final int SdayOfMonth) {

    endDateTextView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Calendar pickedDate = Calendar.getInstance();
        Calendar minDate = Calendar.getInstance();

        pickedDate.set(Syear, Smonth - 1, SdayOfMonth);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            context,
            new DatePickerDialog.OnDateSetListener() {
              @Override
              public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;

                endDateTextView.setText(year + "." + month + "." + dayOfMonth);
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

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.info_menu, menu);

    MenuItem editMenu = menu.findItem(R.id.edit);
    tintMenuIcon(context, editMenu, R.color.textColorPrimary);
    MenuItem deleteMenu = menu.findItem(R.id.delete);
    tintMenuIcon(context, deleteMenu, R.color.textColorPrimary);
    return true;
  }

  public static void tintMenuIcon(Context context, MenuItem item, @ColorRes int color) {
    Drawable normalDrawable = item.getIcon();
    Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
    DrawableCompat.setTint(wrapDrawable, context.getResources().getColor(color));

    item.setIcon(wrapDrawable);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.edit:
        title = titleEditText.getText().toString();
        Travel newTravel = new Travel(travel.getData("email"), title, area, startDate, endDate);
        Intent editIntent = new Intent();
        editIntent.putExtra("travel", travel);
        editIntent.putExtra("newTravel", newTravel);
        editIntent.putExtra("state", 1);
        setResult(RESULT_OK, editIntent);
        finish();
        break;
      case R.id.delete:
        Intent deleteIntent = new Intent();
        deleteIntent.putExtra("travel", travel);
        deleteIntent.putExtra("state", 2);
        setResult(RESULT_OK, deleteIntent);
        finish();
        break;
    }
    return true;
  }
}
