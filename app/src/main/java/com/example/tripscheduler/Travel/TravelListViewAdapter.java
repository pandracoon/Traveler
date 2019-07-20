package com.example.tripscheduler.Travel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.tripscheduler.R;
import java.util.ArrayList;

public class TravelListViewAdapter extends BaseAdapter {

  private ArrayList<Travel> travelItemList = new ArrayList<>();

  public TravelListViewAdapter() {

  }

  @Override
  public int getCount() {
    return travelItemList.size();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final Context context = parent.getContext();

    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater) context
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.layout_travelitem, parent, false);

    }

    TextView titleTextView = convertView.findViewById(R.id.textView14);
    TextView dateTextView = convertView.findViewById(R.id.textView16);
    TextView areaTextView = convertView.findViewById(R.id.textView17);

    Travel travel = travelItemList.get(position);

    titleTextView.setText(travel.getTitle());
    dateTextView.setText(travel.getDate());
    areaTextView.setText(travel.getArea());

    return convertView;
  }

  @Override
  public Object getItem(int position) {
    return travelItemList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  public void addItem(Travel travel) {
    travelItemList.add(travel);
  }

}
