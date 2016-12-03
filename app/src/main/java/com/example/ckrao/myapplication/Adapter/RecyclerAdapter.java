package com.example.ckrao.myapplication.Adapter;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ckrao.myapplication.Model.MoreCityModel;
import com.example.ckrao.myapplication.R;

import java.util.List;

/**
 * Created by clark on 2016/12/4.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private List<MoreCityModel> dataList;

    public RecyclerAdapter(List<MoreCityModel> dataList) {
        this.dataList = dataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.newcity_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mImageView.setBackgroundResource(R.drawable.dialog_bg_sunny);
        holder.city.setText(dataList.get(position).getCity());
        holder.temp.setText(dataList.get(position).getTemp());
        holder.weather.setText(dataList.get(position).getWeather());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView city;
        TextView temp;
        TextView weather;

        public MyViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.item_img);
            city = (TextView) itemView.findViewById(R.id.item_city);
            temp = (TextView) itemView.findViewById(R.id.item_temp);
            weather = (TextView) itemView.findViewById(R.id.item_weather);
        }
    }
}
