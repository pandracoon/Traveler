package com.example.tripscheduler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.tripscheduler.Place.FragmentPlace;
import com.example.tripscheduler.Schedule.FragmentSchedule;
import com.example.tripscheduler.Trip.FragmentTrip;
import com.example.tripscheduler.UI.CurvedBottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

  TextView titleText;
  private FragmentManager fragmentManager;
  private FragmentPlace fragmentPlace;
  private FragmentSchedule fragmentSchedule;
  private FragmentTrip fragmentTrip;
  FloatingActionButton fab;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_main);

    Toolbar toolbar = findViewById(R.id.mainToolBar);
    toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    titleText = findViewById(R.id.titleTextView);
    titleText.setText("유럽여행");

    CurvedBottomNavigationView curvedBottomNavigationView = findViewById(R.id.customBottomBar);
    curvedBottomNavigationView.inflateMenu(R.menu.navigation);

    fragmentManager = getSupportFragmentManager();
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.add(R.id.frameLayout, new FragmentSchedule()).commit();

    fab = findViewById(R.id.floatingActionButton);
    fab.bringToFront();
    fab.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.frameLayout, new FragmentTrip()).commit();
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
                transaction.replace(R.id.frameLayout, new FragmentPlace()).commit();
                break;
              case R.id.schedule:
                transaction.replace(R.id.frameLayout, new FragmentSchedule()).commit();
                break;
              case R.id.trip:
//                transaction.replace(R.id.frameLayout, new FragmentTrip()).commit();
                break;
            }
            return true;
          }
        });

  }

  private void showDiag() {

    final View dialogView = View.inflate(this, R.layout.layout_fragmenttrip, null);

    final Dialog dialog = new Dialog(this, R.style.MyAlertDialogStyle);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(dialogView);

    ImageView imageView = (ImageView) dialog.findViewById(R.id.closeDialogImg);
    imageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        revealShow(dialogView, false, dialog);
      }
    });

    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
      @Override
      public void onShow(DialogInterface dialogInterface) {
        revealShow(dialogView, true, null);
      }
    });

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

    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    dialog.show();
  }

  private void revealShow(View dialogView, boolean b, final Dialog dialog) {

    final View view = dialogView.findViewById(R.id.dialog1);

    int w = view.getWidth();
    int h = view.getHeight();

    int endRadius = (int) Math.hypot(w, h);

    int cx = (int) (fab.getX() + (fab.getWidth() / 2));
    int cy = (int) (fab.getY()) + fab.getHeight() - 100;

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


}

