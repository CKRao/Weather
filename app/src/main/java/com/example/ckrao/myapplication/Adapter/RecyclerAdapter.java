package com.example.ckrao.myapplication.Adapter;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private RecyclerViewOnItemLongClickListener onItemLongClickListener;

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
        if (dataList.get(position).getWeather().indexOf("雨") != -1) {
            holder.mImageView.setBackgroundResource(R.drawable.dialog_bg_rainy);
        } else if (dataList.get(position).getWeather().indexOf("晴") != -1) {
            holder.mImageView.setBackgroundResource(R.drawable.dialog_bg_sunny);
        } else {
            holder.mImageView.setBackgroundResource(R.drawable.dialog_bg_cloudy);
        }
        holder.city.setText(dataList.get(position).getCity());
        holder.temp.setText(dataList.get(position).getTemp() + "℃");
        holder.weather.setText(dataList.get(position).getWeather());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }



    public interface RecyclerViewOnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemLongClickListener(RecyclerViewOnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnLongClickListener {
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
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if (onItemLongClickListener != null){
                onItemLongClickListener.onItemLongClick(v,getPosition());
            }
            return true;
        }
    }
}
