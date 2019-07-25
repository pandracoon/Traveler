package com.example.tripscheduler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.tripscheduler.Place.PlaceAddMenuActivity;
import com.example.tripscheduler.Place.PlaceFragment;
import com.example.tripscheduler.Place.TPlace;
import com.example.tripscheduler.Schedule.Schedule;
import com.example.tripscheduler.Schedule.ScheduleAddActivity;
import com.example.tripscheduler.Schedule.ScheduleFragment;
import com.example.tripscheduler.Schedule.ScheduleOptimizeActivity;
import com.example.tripscheduler.Server.BitmapArithmetic;
import com.example.tripscheduler.Server.IAppService;
import com.example.tripscheduler.Server.Model;
import com.example.tripscheduler.Server.RetrofitClient;
import com.example.tripscheduler.Server.StringConversion;
import com.example.tripscheduler.Travel.Travel;
import com.example.tripscheduler.Travel.TravelAddActivity;
import com.example.tripscheduler.Travel.TravelEditActivity;
import com.example.tripscheduler.Travel.TravelListViewAdapter;
import com.example.tripscheduler.UI.CurvedBottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.tripscheduler.Server.StringConversion.arrayListToString_1;
import static com.example.tripscheduler.Server.StringConversion.arrayListToString_2;

public class MainActivity extends AppCompatActivity {

  public static final int ADD_TRAVEL_REQUEST = 1;
  public static final int EDIT_TRAVEL_REQUEST = 2;
  public static final int ADD_PLACE_REQUEST = 3;
  public static final int ADD_SCHEDULE_REQUEST = 4;
  public static final int OPTIMIZE_SCHEDULE_REQUEST = 5;

  private final String SERVER = "http://143.248.36.205:3000";
  private CompositeDisposable compositeDisposable = new CompositeDisposable();
  private IAppService iAppService;

  Menu outMenu;
  Toolbar mainToolBar;

  TextView titleText;
  private FragmentManager fragmentManager;
  private PlaceFragment fragmentPlace;
  private ScheduleFragment fragmentSchedule;
  FloatingActionButton fab1;
  FloatingActionButton fab2;
  String currentTravel = "Trip to Seoul";
  String selectedTravel;
  int fragmentState;
  TravelListViewAdapter adapter;
  ArrayList<Travel> travelList;// 여기서 travel들을 보관했다가, 여정 선택을 띄울때마다 for문으로 adapter에 넣는 구조 => 213줄에서 추가됨.

  String email;

  Integer day;

  int currentCount = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_main);

    Retrofit retrofitClient = RetrofitClient.getInstance();
    iAppService = retrofitClient.create(IAppService.class);

    email = getIntent().getStringExtra("email");
    travelList = new ArrayList<>();

    mainToolBar = findViewById(R.id.mainToolBar);
    mainToolBar.setBackgroundColor(Color.parseColor("#FFFFFF"));
    setSupportActionBar(mainToolBar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    titleText = findViewById(R.id.titleTextView);

    compositeDisposable.add(iAppService.user_get_recent_one(email.replace("\"", ""))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .retry()
        .subscribe(new Consumer<String>() {
          @Override
          public void accept(String data) throws Exception {
            Log.e("user_get_recent_one", data);
            currentTravel = data.replace("\"", "");
            titleText.setText(currentTravel);

            fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            fragmentSchedule = new ScheduleFragment(currentTravel, email);
            transaction.add(R.id.frameLayout, fragmentSchedule).commit();
          }
        }));

//    titleText.setText(currentTravel);

    CurvedBottomNavigationView curvedBottomNavigationView = findViewById(R.id.customBottomBar);
    curvedBottomNavigationView.inflateMenu(R.menu.navigation);
    curvedBottomNavigationView.setSelectedItemId(R.id.schedule);
    fragmentState = 2;

//    fragmentManager = getSupportFragmentManager();
//    FragmentTransaction transaction = fragmentManager.beginTransaction();
//    transaction.add(R.id.frameLayout, new ScheduleFragment(currentTravel)).commit();

    fab1 = findViewById(R.id.fab1);
    fab1.bringToFront();
    fab1.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        showDiag();

      }
    });

    curvedBottomNavigationView.setOnNavigationItemSelectedListener(
        new OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch (menuItem.getItemId()) {
              case R.id.place:

                compositeDisposable.add(iAppService.user_get_recent_one(email.replace("\"", ""))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .retry()
                    .subscribe(new Consumer<String>() {
                      @Override
                      public void accept(String data) throws Exception {
                        Log.e("user_get_recent_one", data);
                        currentTravel = data.replace("\"", "");
                        titleText.setText(currentTravel);

                        fragmentState = 1;

                        fragmentManager = getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        fragmentPlace = new PlaceFragment(currentTravel, email.replace("\"", ""));
                        transaction.replace(R.id.frameLayout, fragmentPlace).commit();
                        MenuItem item = outMenu.findItem(R.id.optimize);
                        item.setVisible(false);
                      }
                    }));

//                fragmentState = 1;
//                transaction.replace(R.id.frameLayout, new PlaceFragment(currentTravel, email.replace("\"", ""))).commit();
                break;
              case R.id.schedule:
                compositeDisposable.add(iAppService.user_get_recent_one(email.replace("\"", ""))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .retry()
                    .subscribe(new Consumer<String>() {
                      @Override
                      public void accept(String data) throws Exception {
                        Log.e("user_get_recent_one", data);
                        currentTravel = data.replace("\"", "");

                        fragmentState = 2;

                        fragmentManager = getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        fragmentSchedule = new ScheduleFragment(currentTravel, email);

                        transaction
                            .replace(R.id.frameLayout, fragmentSchedule).commit();
                        MenuItem item = outMenu.findItem(R.id.optimize);
                        item.setVisible(true);
                      }
                    }));
                break;
              case R.id.trip:
                break;
            }
            return true;
          }
        });

    compositeDisposable.add(iAppService.user_get_recent_one(email.replace("\"", ""))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .retry()
        .subscribe(new Consumer<String>() {
          @Override
          public void accept(String data) throws Exception {
            Log.e("user_get_recent_one", data);
            currentTravel = data.replace("\"", "");
          }
        }));

  }

  private void showDiag() {

    final View dialogView = View.inflate(this, R.layout.layout_tripdialog, null);

    final Dialog dialog = new Dialog(this, R.style.MyAlertDialogStyle);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(dialogView);

//    MenuItem item = outMenu.findItem(R.id.optimize);
//    MenuItem item2 = outMenu.findItem(R.id.addSchedule);
//    item.setVisible(false);
//    item2.setVisible(false);
//    outMenu.close();
//    outMenu.clear();

    mainToolBar.setVisibility(View.GONE);
    Toolbar tripToolbar = dialog.findViewById(R.id.tripToolBar);
    tripToolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
    setSupportActionBar(tripToolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    ListView tripListView = dialog.findViewById(R.id.tripListView);
//    SwipeMenuListView tripListView = dialog.findViewById(R.id.tripListView);
    adapter = new TravelListViewAdapter();

    tripListView.setAdapter(adapter);

//    SwipeMenuCreator creator = new SwipeMenuCreator() {
//      @Override
//      public void create(SwipeMenu menu) {
//        SwipeMenuItem editItem = new SwipeMenuItem(getApplicationContext());
//        // set item background
//        editItem.setBackground(new ColorDrawable(Color.parseColor("#2196F3")));
//        // set item width
//        editItem.setWidth(170);
//        // set a icon
//        editItem.setIcon(R.drawable.edit_icon);
//        // add to menu
//        menu.addMenuItem(editItem);
//
//        SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
//        // set item background
//        deleteItem.setBackground(new ColorDrawable(Color.RED));
//        // set item width
//        deleteItem.setWidth(170);
//        // set a icon
//        deleteItem.setIcon(R.drawable.delete_icon);
//        // add to menu
//        menu.addMenuItem(deleteItem);
//      }
//    };
//
//    tripListView.setMenuCreator(creator);
//
//    tripListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//      @Override
//      public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
//        switch (index) {
//          case 0:
//            Log.e("dd", "onMenuItemClick: clicked item " + index);
//            break;
//          case 1:
//            Log.e("dd", "onMenuItemClick: clicked item " + index);
//            break;
//        }
//        // false : close the menu; true : not close the menu
//        return false;
//      }
//    });

    tripListView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView parent, View v, int position, long id) {
        Travel travel = (Travel) parent.getItemAtPosition(position);
        selectedTravel = travel.getTitle();
      }
    });

    fab2 = dialog.findViewById(R.id.fab2);
    fab2.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if (selectedTravel == null) {
          Toast.makeText(getApplicationContext(), "변경할 여정을 선택하세요.", Toast.LENGTH_SHORT)
              .show();
          return;
        }
        titleText.setText(selectedTravel);
        currentTravel = selectedTravel;
        selectedTravel = null;

        compositeDisposable
            .add(iAppService.user_set_recent_one(email.replace("\"", ""), currentTravel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
                .subscribe(new Consumer<String>() {
                  @Override
                  public void accept(String data) throws Exception {
                    Log.e("user_set_recent_one", data);
                  }
                }));

        revealShow(dialogView, false, dialog);
        if (fragmentState == 1) {
          compositeDisposable.add(iAppService.user_get_recent_one(email.replace("\"", ""))
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .retry()
              .subscribe(new Consumer<String>() {
                @Override
                public void accept(String data) throws Exception {
                  Log.e("user_get_recent_one", data);
                  currentTravel = data.replace("\"", "");
                }
              }));

          FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

          fragmentPlace = new PlaceFragment(currentTravel, email.replace("\"", ""));
          transaction.replace(R.id.frameLayout, fragmentPlace).commit();

        } else {
          compositeDisposable.add(iAppService.user_get_recent_one(email.replace("\"", ""))
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .retry()
              .subscribe(new Consumer<String>() {
                @Override
                public void accept(String data) throws Exception {
                  Log.e("user_get_recent_one", data);
                  currentTravel = data.replace("\"", "");
                }
              }));

          FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
          fragmentSchedule = new ScheduleFragment(currentTravel, email);
          transaction.replace(R.id.frameLayout, fragmentSchedule)
              .commit();
        }
      }
    });

    compositeDisposable.add(iAppService.travel_get_all(email.replace("\"", ""))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .retry()
        .subscribe(new Consumer<String>() {
          @Override
          public void accept(String data) throws Exception {
            Log.e("travel_get_all", data);

            if (data.equals("0")) {
              System.out.println("No data existed");
            } else {
              JsonParser jsonParser = new JsonParser();
              JsonArray jsonArray = (JsonArray) jsonParser.parse(data);

              for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject object = (JsonObject) jsonArray.get(i);

                System.out.println(object.get("title"));
                System.out.println(object.get("area"));
                System.out.println(object.get("start_date"));
                System.out.println(object.get("end_date"));

//                    adapter.addItem(new Travel(email, object.get("title").toString().replace("\"", ""), object.get("area").toString().replace("\"", ""),
//                            object.get("start_date").toString().replace("\"", "").replace("-", "."),
//                            object.get("end_date").toString().replace("\"", "").replace("-", ".")));

                travelList.add(new Travel(email, object.get("title").toString().replace("\"", ""),
                    object.get("area").toString().replace("\"", ""),
                    object.get("start_date").toString().replace("\"", "").replace("-", "."),
                    object.get("end_date").toString().replace("\"", "").replace("-", ".")));

                adapter.addItem(travelList.get(travelList.size() - 1));
                adapter.notifyDataSetChanged();
              }
            }
          }
        }));

    ImageView addButton = dialog.findViewById(R.id.imageView3);
    addButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent addIntent = new Intent(getApplicationContext(), TravelAddActivity.class);
        startActivityForResult(addIntent, ADD_TRAVEL_REQUEST);
      }
    });

    //종료 버튼 눌러서 종료
    ImageView imageView = (ImageView) dialog.findViewById(R.id.closeDialogImg);
    imageView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mainToolBar.setVisibility(View.VISIBLE);
        revealShow(dialogView, false, dialog);
      }
    });

    //가운데 버튼 눌러서 시작
    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
      @Override
      public void onShow(DialogInterface dialogInterface) {
        revealShow(dialogView, true, null);
      }
    });

    //뒤로가기로 종료
    dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
      @Override
      public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_BACK) {
          mainToolBar.setVisibility(View.VISIBLE);
          revealShow(dialogView, false, dialog);
          return true;
        }

        return false;
      }
    });

    tripListView.setOnItemLongClickListener(new OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView parent, View v, int position, long id) {
        Travel travel = (Travel) parent.getItemAtPosition(position);
        Intent intent = new Intent(getApplicationContext(), TravelEditActivity.class);
        intent.putExtra("travel", travel);
        startActivityForResult(intent, EDIT_TRAVEL_REQUEST);

        return false;
      }
    });

    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    dialog.show();
  }

  private void revealShow(View dialogView, boolean b, final Dialog dialog) {

    final View view = dialogView.findViewById(R.id.dialog1);

    int w = view.getWidth();
    int h = view.getHeight();

    int endRadius = (int) Math.hypot(w, h);

    int cx = (int) (fab1.getX() + (fab1.getWidth() / 2));
    int cy = (int) (fab1.getY()) + fab1.getHeight() - 100;

    if (b) {
      Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, endRadius);

      view.setVisibility(View.VISIBLE);
      revealAnimator.setDuration(400);
      revealAnimator.start();

    } else {

      Animator anim =
          ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

      anim.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          super.onAnimationEnd(animation);
          dialog.dismiss();
          view.setVisibility(View.INVISIBLE);

        }
      });
      anim.setDuration(400);
      anim.start();
    }

//    MenuItem item = outMenu.findItem(R.id.optimize);
//    MenuItem item2 = outMenu.findItem(R.id.addSchedule);
//    item.setVisible(true);
//    item2.setVisible(true);

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
      @Nullable Intent intent) { //여기서 data 추가 수정 삭제가 발생. adapter랑 travelList에 둘다 추가하고 삭제해줌.
    super.onActivityResult(requestCode, resultCode, intent);

    switch (requestCode) {
      case ADD_TRAVEL_REQUEST:
        if (resultCode == RESULT_OK) {
          String[] travelData = intent.getStringArrayExtra("title");
          Travel travel = new Travel(email, travelData[0], travelData[1], travelData[2],
              travelData[3]);
          adapter.addItem(travel);
          travelList.add(travel);
          adapter.notifyDataSetChanged();

          // Insert Travel to the server
          compositeDisposable.add(iAppService
              .travel_insert_one(email.replace("\"", ""), travelData[0], travelData[1],
                  travelData[2], travelData[3])
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .retry()
              .subscribe(new Consumer<String>() {
                @Override
                public void accept(String data) throws Exception {
                  Log.e("travel_insert_one", data);

                }
              }));
        }
        break;
      case EDIT_TRAVEL_REQUEST:
        if (resultCode == RESULT_OK) {
          int state = intent.getIntExtra("state", 0);
          if (state == 1) {
            Travel travel = (Travel) intent.getSerializableExtra("travel");
            Travel newTravel = (Travel) intent.getSerializableExtra("newTravel");
            adapter.deleteItem(travel);
            adapter.addItem(newTravel);
            for (int i = 0; i < travelList.size(); i++) {
              if (travelList.get(i).getTitle().equals(travel.getTitle())) {
                travelList.remove(i);
              }
            }
            travelList.add(newTravel);

          } else {
            Travel travel = (Travel) intent.getSerializableExtra("travel");

            adapter.deleteItem(travel);
            for (int i = 0; i < travelList.size(); i++) {
              if (travelList.get(i).getTitle().equals(travel.getTitle())) {
                travelList.remove(i);
              }

              compositeDisposable
                  .add(iAppService.travel_delete_one(email.replace("\"", ""), travel.getTitle())
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .retry()
                      .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String data) throws Exception {
                          Log.e("travel_delete_one", data);
                        }
                      }));
            }
          }
          adapter.notifyDataSetChanged();
          break;
        }
      case ADD_PLACE_REQUEST:
        System.out.println(123);

        if (resultCode == RESULT_OK) {
          System.out.println(1);
          System.out.println(intent.getStringExtra("name"));
          System.out.println(intent.getStringExtra("latLng"));
          System.out.println(intent.getStringExtra("label"));

          byte[] bytes = intent.getByteArrayExtra("image");
          Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

          FragmentTransaction transaction = getSupportFragmentManager()
              .beginTransaction();//Todo 없애보고 해보고 그냥 해보고
          fragmentPlace = new PlaceFragment(currentTravel, email.replace("\"", ""),
              intent.getStringExtra("name"), intent.getStringExtra("latLng"),
              intent.getStringExtra("label"), BitmapArithmetic.resizeBitmap(image));
          transaction.replace(R.id.frameLayout, fragmentPlace).commit();

        } else {
          System.out.println("else");
        }

        break;

      case ADD_SCHEDULE_REQUEST:

        if (resultCode == RESULT_OK) {
          Schedule schedule = (Schedule) intent.getSerializableExtra("schedule");

          compositeDisposable.add(iAppService
              .schedule_insert_one(email.replace("\"", ""), currentTravel, day.toString(),
                  schedule.getData("name"),
                  schedule.getData("location").replace("\"", "").replace("[", "").replace("]", "")
                      .replace(",", " "),
                  schedule.getData("label"), schedule.getData("memo"), schedule.getData("start"),
                  schedule.getData("duration"))
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .retry()
              .subscribe(new Consumer<String>() {
                @Override
                public void accept(String data) throws Exception {
                  Log.e("schedule_insert_one", data);
                }
              }));

        }
        break;

      case OPTIMIZE_SCHEDULE_REQUEST:
        if(resultCode == RESULT_OK){
          final ArrayList<TPlace> placeList = (ArrayList<TPlace>)intent.getSerializableExtra("placeList");
          final ArrayList<Integer> timeList = intent.getIntegerArrayListExtra("timeList");
          System.out.println(placeList);
          System.out.println(timeList);

          // DO IT HERE
          compositeDisposable.add(iAppService.schedule_get_one(email.replace("\"", ""), currentTravel)
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .retry()
                  .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String data) throws Exception {
                      Log.e("schedule_get_one", data);

                      if (data.equals("0")) {
                        System.out.println("No data existed");
                      }

                      else {

                        JsonParser jsonParser = new JsonParser();
                        JsonArray tempArray = (JsonArray) jsonParser.parse(data);

                        Integer day = fragmentSchedule.currentTabPostion();

                        JsonObject jsonObject = (JsonObject) tempArray.get(day);
                        final JsonArray jsonArray = (JsonArray) jsonObject.get("schedule");

                        final int size = jsonArray.size();

                        final Integer[][] array = new Integer[size + timeList.size()][size + timeList.size()];

                        // Key Points Duration
                        for (int i = 0; i < size; i ++) {
                          JsonObject object = (JsonObject) jsonArray.get(i);
                          array[i][i] = Integer.parseInt(object.get("duration").toString().replace("\"", "")) * 60;
                          System.out.println(array[i][i]);
                        }

                        // Places Duration
                        for (int i = size; i < size + timeList.size(); i ++) {
                          array[i][i] = timeList.get(i - size) * 60;
                          System.out.println(array[i][i]);
                        }

                        // Rest Weights
                        String base_url = "https://maps.googleapis.com/";

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(base_url)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        IAppService request = retrofit.create(IAppService.class);

                        final int maxCount = (size + timeList.size()) * (size + timeList.size()) - (size + timeList.size());

                        for (int i = 0; i < size + timeList.size(); i ++) {
                          for (int j = 0; j < size + timeList.size(); j ++) {
                            if (i == j) {
                              continue;
                            }

                            final int temp_i = i;
                            final int temp_j = j;

                            String origin = "41.2198254,16.3076254";
                            String dest = "41.2199,16.3062";

                            if (i < size) {
                              JsonObject temp = (JsonObject) jsonArray.get(i);
                              origin = temp.get("location").toString().split("\"")[1] + "," + temp.get("location").toString().split("\"")[3];
                              System.out.println("schedule origin");
                              System.out.println(origin);
                            }
                            else {
                              origin = placeList.get(i - size).getData("location").split("\"")[1] + "," + placeList.get(i - size).getData("location").split("\"")[3];
                              System.out.println("place origin");
                              System.out.println(origin);
                            }

                            if (j < size) {
                              JsonObject temp = (JsonObject) jsonArray.get(j);
                              dest = temp.get("location").toString().split("\"")[1] + "," + temp.get("location").toString().split("\"")[3];;
                              System.out.println("schedule dest");
                              System.out.println(dest);
                            }
                            else {
                              dest = placeList.get(j - size).getData("location").split("\"")[1] + "," + placeList.get(j - size).getData("location").split("\"")[3];
                              System.out.println("place dest");
                              System.out.println(dest);
                            }

                            String key = "AIzaSyAp5fwKE7aSSRG-Yclw9aNXI0quf8Hj7qA";

                            Call<Model.DirectionResults> req = request.getJson(origin, dest, key, "transit");
                            req.enqueue(new Callback<Model.DirectionResults>() {
                              @Override
                              public void onResponse(Call<Model.DirectionResults> call, Response<Model.DirectionResults> response) {
                                Log.d("CallBack", " response is " + response);
                                Model.Route route = response.body().getRoutes().get(0);
                                Model.Legs leg = route.getLegses().get(0);

                                Integer weight = leg.getDuration().getValue();

                                array[temp_i][temp_j] = weight;

                                for (int j = 0; j < response.body().getRoutes().size(); j ++) {
                                  for (int i = 0; i < response.body().getRoutes().get(j).getLegses().size(); i++) {
                                    System.out.println(route.getLegses().get(i).getDistance().getText());
                                    System.out.println(route.getLegses().get(i).getDistance().getValue());
                                    System.out.println(route.getLegses().get(i).getDuration().getText());
                                    System.out.println(route.getLegses().get(i).getDuration().getValue());
                                  }
                                }

                                System.out.println("Coordinate : ");
                                System.out.println(temp_i);
                                System.out.println(temp_j);

                                currentCount = currentCount + 1;

                                if (currentCount == maxCount) {
                                    System.out.println("It's almost an end game");

                                    currentCount = 0;

                                    // Convert Array to ArrayList
                                    ArrayList<ArrayList<Integer>> arrayList = new ArrayList<>();
                                    for (Integer[] ints : array) {
                                      ArrayList<Integer> list = new ArrayList<>();
                                      for (int i : ints) {
                                        list.add(i);
                                      }
                                      arrayList.add(list);
                                    }

                                    System.out.println(arrayList);

                                    // Request Optimization
                                    Integer totalLength = size + timeList.size();
                                    String graph = arrayListToString_2(arrayList);

                                    ArrayList<Integer> limitsList = new ArrayList<>();

                                    for (int i = 0; i < size - 1; i ++) {
                                      JsonObject object = (JsonObject) jsonArray.get(i);
                                      JsonObject object2 = (JsonObject) jsonArray.get(i + 1);

                                      String tempTime1 = object.get("start").toString().split(" ")[0].split("\"")[1];
                                      String tempMinute1 = object.get("start").toString().split(" ")[1].split("\"")[0];
                                      Integer startTime1 = (Integer.parseInt(tempTime1) * 60 + Integer.parseInt(tempMinute1)) * 60;

                                      String tempTime2 = object2.get("start").toString().split(" ")[0].split("\"")[1];
                                      String tempMinute2 = object2.get("start").toString().split(" ")[1].split("\"")[0];
                                      Integer startTime2 = (Integer.parseInt(tempTime2) * 60 + Integer.parseInt(tempMinute2)) * 60;

                                      limitsList.add(startTime2 - startTime1);

                                      System.out.println(startTime2 - startTime1);
                                    }

                                    String limits = arrayListToString_1(limitsList);

                                    String keyPoints = Integer.toString(size);

                                    System.out.println(totalLength.toString());
                                    System.out.println(graph);
                                    System.out.println(limits);
                                    System.out.println(keyPoints);

                                    compositeDisposable.add(iAppService.schedule_optimize(totalLength.toString(), graph, limits, keyPoints)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .retry()
                                            .subscribe(new Consumer<String>() {
                                              @Override
                                              public void accept(String data) throws Exception {
                                                Log.e("schedule_optimize", data);

                                                JsonParser jsonParser = new JsonParser();
                                                JsonArray jsonArray = (JsonArray) jsonParser.parse(data);

                                                for (int i = 0; i < jsonArray.size(); i ++) {
                                                  JsonArray array = (JsonArray) jsonArray.get(i);
                                                  System.out.println((i + 1)  + " th path");

                                                  for (int j = 0; j < array.size(); j ++) {
                                                    System.out.println(array.get(j));
                                                  }
                                                }
                                              }
                                            }));
                                  }

                              }

                              @Override
                              public void onFailure(Call<Model.DirectionResults> call, Throwable t) {
                                Log.d("CallBack", " Throwable is " +t);
                              }
                            });
                          }
                        }
                      }
                    }
                  }));
        }

        break;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    outMenu = menu;

    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.schedule_menu, outMenu);

    MenuItem addMenu = outMenu.findItem(R.id.addSchedule);
    tintMenuIcon(getApplicationContext(), addMenu, R.color.textColorPrimary);

    MenuItem optimizeMenu = outMenu.findItem(R.id.optimize);
    tintMenuIcon(getApplicationContext(), optimizeMenu, R.color.yellow);
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
      case R.id.addSchedule:
        if (fragmentState == 1) {
          Intent placeAddIntent = new Intent(this, PlaceAddMenuActivity.class);
          startActivityForResult(placeAddIntent, ADD_PLACE_REQUEST);

        } else if (fragmentState == 2) {
          Intent scheduleAddIntent = new Intent(this, ScheduleAddActivity.class);
          scheduleAddIntent.putExtra("email", email);
          scheduleAddIntent.putExtra("title", currentTravel);
          if (fragmentSchedule == null) {
            day = 0;
          } else {
            day = fragmentSchedule.currentTabPostion();
          }
          startActivityForResult(scheduleAddIntent, ADD_SCHEDULE_REQUEST);
        }
        break;
      case R.id.optimize:
        Intent scheduleOptimizeIntent = new Intent(this, ScheduleOptimizeActivity.class);
        scheduleOptimizeIntent.putExtra("email",email);
        scheduleOptimizeIntent.putExtra("title",currentTravel);
        startActivityForResult(scheduleOptimizeIntent,OPTIMIZE_SCHEDULE_REQUEST);
        break;
    }
    return true;
  }

}

