package com.example.tripscheduler.Schedule;

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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.tripscheduler.Place.PlaceListViewAdapter;
import com.example.tripscheduler.Place.TPlace;
import com.example.tripscheduler.R;
import com.example.tripscheduler.Server.IAppService;
import com.example.tripscheduler.Server.RetrofitClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import retrofit2.Retrofit;

public class ScheduleOptimizeActivity extends AppCompatActivity {

  TextView[] placeTextView = new TextView[10];
  EditText[] timeEditText = new EditText[10];
  Button placeButton, scheduleButton;
  String[] place = new String[10];
  ArrayList<Integer> time = new ArrayList<Integer>();
  String email, title;
  PlaceListViewAdapter adapter;
  Toolbar toolbar;
  int cnt = 0;

  ArrayList<TPlace> selectedPlaceList = new ArrayList<>();

  private ArrayList<TPlace> placeItemList = new ArrayList<>();

  private final String SERVER = "http://143.248.36.205:3000";
  private CompositeDisposable compositeDisposable = new CompositeDisposable();
  private IAppService iAppService;
  Context context = this;
  ListView listView;
  TPlace selectedPlace;
  int isClicked;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_schedule_optimize);

    email = getIntent().getStringExtra("email");
    title = getIntent().getStringExtra("title");

    Retrofit retrofitClient = RetrofitClient.getInstance();
    iAppService = retrofitClient.create(IAppService.class);

    toolbar = findViewById(R.id.optimizeToolBar);
    toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("최적화 일정 생성");

    placeTextView[0] = findViewById(R.id.textView37);
    placeTextView[1] = findViewById(R.id.textView38);
    placeTextView[2] = findViewById(R.id.textView39);
    placeTextView[3] = findViewById(R.id.textView41);
    placeTextView[4] = findViewById(R.id.textView42);
    placeTextView[5] = findViewById(R.id.textView43);
    placeTextView[6] = findViewById(R.id.textView44);
    placeTextView[7] = findViewById(R.id.textView45);
    placeTextView[8] = findViewById(R.id.textView46);
    placeTextView[9] = findViewById(R.id.textView47);

    timeEditText[0] = findViewById(R.id.editText11);
    timeEditText[1] = findViewById(R.id.editText12);
    timeEditText[2] = findViewById(R.id.editText13);
    timeEditText[3] = findViewById(R.id.editText14);
    timeEditText[4] = findViewById(R.id.editText15);
    timeEditText[5] = findViewById(R.id.editText16);
    timeEditText[6] = findViewById(R.id.editText17);
    timeEditText[7] = findViewById(R.id.editText18);
    timeEditText[8] = findViewById(R.id.editText19);
    timeEditText[9] = findViewById(R.id.editText20);

    placeButton = findViewById(R.id.button9);
    scheduleButton = findViewById(R.id.button10);

    getPlaceList();

    placeButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if (cnt == 10) {
          Toast.makeText(getApplicationContext(), "장소를 최대로 추가하였습니다.", Toast.LENGTH_SHORT)
              .show();
          return;
        }
        showAlertDialog();
      }
    });

    scheduleButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(
          View view) { //Todo 누르면 입력받은 장소들(selectedPlaceList), 소요시간들(time)전부 모아짐. ArrayList꼴로 방았음
        for (int i = 0; i < timeEditText.length; i++) {
          if (!timeEditText[i].getText().toString().equals("")) {
            time.add(Integer.parseInt(timeEditText[i].getText().toString()));
          }
        }

        //selectedPlaceList
        Intent intent = new Intent();
        intent.putExtra("placeList", selectedPlaceList);
        intent.putExtra("timeList", time);
        setResult(RESULT_OK, intent);
        finish();


      }
    });

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


  private void showAlertDialog() {

    Builder builder = new Builder(this);
    LayoutInflater inflater = getLayoutInflater();
    View view = inflater.inflate(R.layout.layout_placelist_dialog, null);
    builder.setView(view);

    listView = (ListView) view.findViewById(R.id.listview_alterdialog_list);
    final AlertDialog dialog = builder.create();
    Button button = view.findViewById(R.id.button8);
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if (selectedPlace == null) {
          Toast.makeText(getApplicationContext(), "장소를 선택하세요.", Toast.LENGTH_SHORT)
              .show();
        } else if (isContained(selectedPlace)) {
          Toast.makeText(getApplicationContext(), "이미 추가되어 있는 장소입니다.", Toast.LENGTH_SHORT)
              .show();
        } else {
          selectedPlaceList.add(selectedPlace);
          place[cnt] = selectedPlace.getData("name").replace("\"", "");
          placeTextView[cnt].setText(place[cnt]);
          placeTextView[cnt].setVisibility(View.VISIBLE);
          timeEditText[cnt].setVisibility(View.VISIBLE);

          isClicked = 1;
          dialog.dismiss();
          selectedPlace = null;
          cnt++;
        }

      }
    });

    adapter = new PlaceListViewAdapter(placeItemList);

    listView.setAdapter(adapter);
    listView.setOnItemClickListener(new OnItemClickListener() {

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

  public boolean isContained(TPlace place) {
    String name = place.getData("name");
    for (int i = 0; i < selectedPlaceList.size(); i++) {
      if (selectedPlaceList.get(i).getData("name").equals(name)) {
        return true;
      }
    }
    return false;
  }
}
