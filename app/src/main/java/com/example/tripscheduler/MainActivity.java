package com.example.tripscheduler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.example.tripscheduler.Place.PlaceFragment;
import com.example.tripscheduler.Schedule.ScheduleFragment;
import com.example.tripscheduler.Server.IAppService;
import com.example.tripscheduler.Server.RetrofitClient;
import com.example.tripscheduler.Travel.Travel;
import com.example.tripscheduler.Travel.TravelAddActivity;
import com.example.tripscheduler.Travel.TravelEditActivity;
import com.example.tripscheduler.Travel.TravelListViewAdapter;
import com.example.tripscheduler.UI.CurvedBottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

  public static final int ADD_REQUEST = 1;
  public static final int EDIT_REQUEST = 2;

  TextView titleText;
  private FragmentManager fragmentManager;
  private PlaceFragment fragmentPlace;
  private ScheduleFragment fragmentSchedule;
  FloatingActionButton fab1;
  FloatingActionButton fab2;
  String currentTravel = "서울여행";
  String selectedTravel;
  int fragmentState;
  TravelListViewAdapter adapter;
  ArrayList<Travel> travelList;// 여기서 travel들을 보관했다가, 여정 선택을 띄울때마다 for문으로 adapter에 넣는 구조 => 213줄에서 추가됨.

  String email;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_main);

    email = getIntent().getStringExtra("email");
    travelList = new ArrayList<>();

    titleText = findViewById(R.id.titleTextView);
    titleText.setText(currentTravel);

    CurvedBottomNavigationView curvedBottomNavigationView = findViewById(R.id.customBottomBar);
    curvedBottomNavigationView.inflateMenu(R.menu.navigation);
    curvedBottomNavigationView.setSelectedItemId(R.id.schedule);
    fragmentState = 2;

    fragmentManager = getSupportFragmentManager();
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.add(R.id.frameLayout, new ScheduleFragment(currentTravel)).commit();

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
                fragmentState = 1;
                transaction.replace(R.id.frameLayout, new PlaceFragment(currentTravel)).commit();
                break;
              case R.id.schedule:
                fragmentState = 2;
                transaction.replace(R.id.frameLayout, new ScheduleFragment(currentTravel)).commit();
                break;
              case R.id.trip:
                break;
            }
            return true;
          }
        });

  }

  private void showDiag() {

    final View dialogView = View.inflate(this, R.layout.layout_tripdialog, null);

    final Dialog dialog = new Dialog(this, R.style.MyAlertDialogStyle);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(dialogView);

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

        revealShow(dialogView, false, dialog);
        if (fragmentState == 1) {
          FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
          transaction.replace(R.id.frameLayout, new PlaceFragment(currentTravel)).commit();
        } else {
          FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
          transaction.replace(R.id.frameLayout, new ScheduleFragment(currentTravel)).commit();
        }
      }
    });

    for (int i = 0; i < travelList.size(); i++) {
      adapter.addItem(travelList.get(i));
    }
    // adapter add items based, guess based on server response
    adapter.addItem(new Travel(email, "로마여행", "Rome, Italy", "2015.06.13", "2015.06.21"));
    adapter.addItem(new Travel(email, "서울여행", "Seoul, Korea", "2018.05.02", "2018.05.05"));
    adapter.addItem(new Travel(email, "파리여행", "Paris, France", "2019.02.22", "2019.03.05"));
    adapter.addItem(new Travel(email, "하노이여행", "Hanoi, Vietnam", "2019.10.02", "2019.10.19"));

    ImageView addButton = dialog.findViewById(R.id.imageView3);
    addButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent addIntent = new Intent(getApplicationContext(), TravelAddActivity.class);
        startActivityForResult(addIntent, ADD_REQUEST);
      }
    });

    //종료 버튼 눌러서 종료
    ImageView imageView = (ImageView) dialog.findViewById(R.id.closeDialogImg);
    imageView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {

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
        startActivityForResult(intent, EDIT_REQUEST);

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

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) { //여기서 data 추가 수정 삭제가 발생. adapter랑 travelList에 둘다 추가하고 삭제해줌.
    super.onActivityResult(requestCode, resultCode, intent);

    switch (requestCode) {
      case ADD_REQUEST:
        if (resultCode == RESULT_OK) {
          String[] travelData = intent.getStringArrayExtra("title");
          Travel travel = new Travel(email, travelData[0], travelData[1], travelData[2],
              travelData[3]);
          adapter.addItem(travel);
          travelList.add(travel);
          adapter.notifyDataSetChanged();
        }
        break;
      case EDIT_REQUEST:
        if (resultCode == RESULT_OK) {
          int state = intent.getIntExtra("state", 0);
          if(state == 1){
            Travel travel = (Travel)intent.getSerializableExtra("travel");
            Travel newTravel = (Travel)intent.getSerializableExtra("newTravel");
            adapter.deleteItem(travel);
            adapter.addItem(newTravel);
            for (int i = 0; i < travelList.size(); i++) {
              if(travelList.get(i).getTitle().equals(travel.getTitle())){
                travelList.remove(i);
              }
            }
            travelList.add(newTravel);
          }else{
            Travel travel = (Travel)intent.getSerializableExtra("travel");
            adapter.deleteItem(travel);
            for (int i = 0; i < travelList.size(); i++) {
              if(travelList.get(i).getTitle().equals(travel.getTitle())){
                travelList.remove(i);
              }
            }
          }
          adapter.notifyDataSetChanged();
          break;
        }
    }
  }

}

