package com.example.tripscheduler.Place;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.example.tripscheduler.R;
import com.example.tripscheduler.Travel.Travel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaceInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    ArrayAdapter adapter;
    Spinner spinner;
    String name;
    LatLng latLng;
    String address;
    EditText titleEditText;
    String placeID;
    PlacesClient placeClient;
    Context context = this;
    List<Place.Field> fields = Arrays.asList(Place.Field.PHOTO_METADATAS);
    ImageView imageView;
    Button button;
    String label;
    Bitmap resizedImage, scaledBitmap;
    ArrayList<String> arrayList = new ArrayList<String>();

    String image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_placeadd);

        TPlace place = (TPlace) getIntent().getSerializableExtra("place");

        name = place.getData("name");
        label = place.getData("label");
        image = place.getImage();

        Toolbar toolbar = findViewById(R.id.placeAddToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("장소 정보");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        arrayList.add("볼거리");
        arrayList.add("식당");
        arrayList.add("숙박");


        imageView = findViewById(R.id.imageView8);
        Glide.with(context)
                .load(image)
                .fitCenter()
                .into(imageView);

        titleEditText = findViewById(R.id.editText8);
        titleEditText.setText(name);
        spinner = findViewById(R.id.spinner2);
        if (label.equals("식당")) {
            spinner.setSelection(1);
        } else if (label.equals("숙박")) {
            spinner.setSelection(2);
        }


        adapter = new ArrayAdapter(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, arrayList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                label = arrayList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        TPlace place = (TPlace) getIntent().getSerializableExtra("place");
        String strLatLng = place.getData("location");
        System.out.println(strLatLng);
        String lat = strLatLng.split("\"")[1];
        String lng = strLatLng.split("\"")[3];
        latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

        googleMap.addMarker(new MarkerOptions().position(latLng).title(name));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.setMinZoomPreference(15.0f);
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
                name = titleEditText.getText().toString();
                TPlace place =(TPlace)getIntent().getSerializableExtra("place");

                TPlace newPlace = new TPlace(name,place.getData("location"),label,place.getImage());
                Intent editIntent = new Intent();
                editIntent.putExtra("place", place);
                editIntent.putExtra("newPlace", newPlace);
                editIntent.putExtra("state", 1);
                setResult(RESULT_OK, editIntent);
                finish();
                break;
            case R.id.delete:
                TPlace delplace =(TPlace)getIntent().getSerializableExtra("place");
                Intent deleteIntent = new Intent();
                deleteIntent.putExtra("place", delplace);
                deleteIntent.putExtra("state", 2);
                setResult(RESULT_OK, deleteIntent);
                finish();
                break;
        }
        return true;
    }
}
