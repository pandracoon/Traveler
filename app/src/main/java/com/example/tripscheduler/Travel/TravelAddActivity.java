package com.example.tripscheduler.Travel;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tripscheduler.R;
import com.example.tripscheduler.Schedule.ScheduleFragment;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;

public class TravelAddActivity extends AppCompatActivity {

    StepView stepView;
    private FragmentManager fragmentManager;
    Button button;
    String title, area, startDate, endDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_traveladd);


        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.frameLayout, new TravelAddFirstFragment(null)).commit();

        stepView = findViewById(R.id.step_view);
        stepView.getState()
                // You should specify only stepsNumber or steps array of strings.
                // In case you specify both steps array is chosen.
                .steps(new ArrayList<String>() {{
                    add("First step");
                    add("Second step");
                    add("Third step");
                }})
                // You should specify only steps number or steps array of strings.
                // In case you specify both steps array is chosen.
                .stepsNumber(4)
                .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .stepLineWidth(getResources().getDimensionPixelSize(R.dimen.dp1))
                .textSize(getResources().getDimensionPixelSize(R.dimen.sp14))
                .stepNumberTextSize(getResources().getDimensionPixelSize(R.dimen.sp16))
                // other state methods are equal to the corresponding xml attributes
                .commit();

    }
}
