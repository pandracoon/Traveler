package com.example.tripscheduler.Place;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripscheduler.R;

import java.util.ArrayList;

import ru.nikartm.support.ImageBadgeView;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private Context context;
    private ArrayList<TPlace> mList;

    public PlaceAdapter(Context context, ArrayList<TPlace> list) {
        this.context = context;
        this.mList = list;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_placetile, parent, false);
        PlaceViewHolder placeView = new PlaceViewHolder(layoutView);

        return placeView;
    }

    @Override
    public synchronized void onBindViewHolder(PlaceViewHolder holder, int position) {

        if (mList.get(position).getData("label").replace("\"", "").equals("볼거리")) {
            holder.imageBadgeView.setBadgeColor(Color.WHITE);
            holder.imageBadgeView.setBadgeTextColor(Color.WHITE);
            holder.imageBadgeView2.setBadgeBackground(context.getDrawable(R.drawable.attraction));
        }

        else if (mList.get(position).getData("label").replace("\"", "").equals("식당")) {
            holder.imageBadgeView.setBadgeColor(Color.WHITE);
            holder.imageBadgeView.setBadgeTextColor(Color.WHITE);
            holder.imageBadgeView2.setBadgeBackground(context.getDrawable(R.drawable.restaurant));
        }

        else if (mList.get(position).getData("label").replace("\"", "").equals("숙박")) {
            holder.imageBadgeView.setBadgeColor(Color.WHITE);
            holder.imageBadgeView.setBadgeTextColor(Color.WHITE);
            holder.imageBadgeView2.setBadgeBackground(context.getDrawable(R.drawable.baseline_hotel_black_18dp));
        }

        holder.nameView.setText(mList.get(position).getData("name").replace("\"", ""));
//        holder.locationView.setText(mList.get(position).getData("location"));
//        holder.labelView.setText(mList.get(position).getData("label"));

        Glide.with(context)
                .load(mList.get(position).getImage())
                .fitCenter()
                .into(holder.imageView);

        //badgeView.unbind();
        System.out.println("End of function");

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameView;
        TextView locationView;
        TextView labelView;
        ImageBadgeView imageBadgeView;
        ImageBadgeView imageBadgeView2;

        public PlaceViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.img);
            nameView = (TextView) itemView.findViewById(R.id.img_name);
//            locationView = (TextView) itemView.findViewById(R.id.location);
//            labelView = (TextView) itemView.findViewById(R.id.label);
            imageBadgeView = (ImageBadgeView) itemView.findViewById(R.id.badge);
            imageBadgeView2 = (ImageBadgeView) itemView.findViewById(R.id.badge2);

        }
    }
}
