package com.example.tripscheduler.Travel;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import com.example.tripscheduler.R;
import com.shuhart.stepview.StepView;
import java.util.ArrayList;

public class TravelAddActivity extends AppCompatActivity {

  StepView stepView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_traveladd);

    stepView = findViewById(R.id.step_view);
    stepView.getState()
        .selectedTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        .animationType(StepView.ANIMATION_CIRCLE)
        .selectedCircleColor(ContextCompat.getColor(this, R.color.colorAccent))
        .selectedCircleRadius(getResources().getDimensionPixelSize(R.dimen.dp14))
        .selectedStepNumberColor(ContextCompat.getColor(this, R.color.textColor))
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
        .typeface(ResourcesCompat.getFont(getApplicationContext(), R.font.mainfont_light))
        // other state methods are equal to the corresponding xml attributes
        .commit();

    Button button = findViewById(R.id.button5);
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        stepView.go(1,true);
      }
    });
  }
}
