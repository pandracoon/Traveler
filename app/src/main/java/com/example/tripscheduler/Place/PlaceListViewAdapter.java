package com.example.tripscheduler.Place;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.tripscheduler.R;
import com.example.tripscheduler.Server.IAppService;
import com.example.tripscheduler.Travel.Travel;
import io.reactivex.disposables.CompositeDisposable;
import java.util.ArrayList;

public class PlaceListViewAdapter extends BaseAdapter {


  private ArrayList<TPlace> placeItemList = new ArrayList<>();

  public PlaceListViewAdapter(ArrayList<TPlace> placeItemList) {
    this.placeItemList = placeItemList;
  }

  @Override
  public int getCount() {
    return placeItemList.size();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final Context context = parent.getContext();

    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater) context
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.layout_placelist_item, parent, false);

    }

    ImageView view = convertView.findViewById(R.id.view3);
    TextView titleTextView = convertView.findViewById(R.id.textView35);

    TPlace place = placeItemList.get(position);
    titleTextView.setText(place.getData("name"));
    String label = place.getData("label");
    if (label.equals("\"볼거리\"")) {
      view.setBackgroundResource(R.drawable.attraction);
    }
    if (label.equals("\"식당\"")) {
      view.setBackgroundColor(R.drawable.restaurant);
    }
    if (label.equals("\"숙박\"")) {
      view.setBackgroundResource(R.drawable.baseline_hotel_black_18dp);
    }

    return convertView;
  }

  @Override
  public Object getItem(int position) {
    return placeItemList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }


}
