package com.example.tripscheduler.Schedule;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.tripscheduler.R;
import com.example.tripscheduler.Server.IAppService;
import com.example.tripscheduler.Server.Model;
import com.example.tripscheduler.Server.RetrofitClient;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ScheduleFragment extends Fragment {

  IAppService apiService;
  private final String SERVER = "http://143.248.36.205:3000";
  private CompositeDisposable compositeDisposable = new CompositeDisposable();
  private IAppService iAppService;

  String title;
  String email;

  TabLayout tabLayout;
  ViewPager viewPager;
  int numCategories = 8;

  final int SCROLLABLE_MINIMUM = 7;

  int temp;

  public ScheduleFragment(String title, String email) {
    this.title = title;
    this.email = email;
  }

//  public static ScheduleFragment newInstance(int sectionNumber, String title, String email) {
//    ScheduleFragment fragment = new ScheduleFragment(title, email);
//    Bundle args = new Bundle();
//    args.putInt("section_number", sectionNumber);
//    fragment.setArguments(args);
//    return fragment;
//  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.layout_fragmentschedule, container, false);

    Retrofit retrofitClient = RetrofitClient.getInstance();
    iAppService = retrofitClient.create(IAppService.class);

    tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
    viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);

    compositeDisposable.add(iAppService.schedule_get_one(email.replace("\"", ""), title)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .retry()
            .subscribe(new Consumer<String>() {
              @Override
              public void accept(String data) throws Exception {
                Log.e("schedule_get_one", data);

                if (data.equals("0")) {
                  System.out.println("No data existed");

                  numCategories = 0;
                }

                else {

                  JsonParser jsonParser = new JsonParser();
                  JsonArray jsonArray = (JsonArray) jsonParser.parse(data);

                  numCategories = jsonArray.size();

                  if (numCategories != 0) {
                    tabLayout.removeAllTabs();
                  }

                  for (int i = 0; i < numCategories; i++) {
                    JsonObject object = (JsonObject) jsonArray.get(i);
                    tabLayout.addTab(tabLayout.newTab().setText(object.get("date").toString().replace("\"", "")));
                  }

                  if (numCategories >= SCROLLABLE_MINIMUM) {
                    tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                  }

                  ScheduleAdapter adapter = new ScheduleAdapter(getActivity().getSupportFragmentManager(), tabLayout.getTabCount(), title, email);
                  viewPager.setAdapter(adapter);

                  viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                }
              }
            }));

    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {

        temp = tab.getPosition();
        viewPager.setCurrentItem(tab.getPosition());
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {

      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {

      }
    });

    return rootView;
  }

  public Integer currentTabPostion(){
    return temp;
  }
}
