package com.example.ckrao.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by CKRAO on 2016/9/25.
 */

public class RcycleAdapter extends RecyclerView.Adapter<RcycleAdapter.ViewHolder> {
    private List<DataBean> mDataBean;

    public RcycleAdapter(List<DataBean>dataBean) {
        mDataBean = dataBean;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_recycle, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView1.setText("23～32℃");
        holder.mTextView2.setText("晴");
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView1;
        private TextView mTextView2;
        private ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView1 = (TextView) itemView.findViewById(R.id.wendu);
            mTextView2 = (TextView) itemView.findViewById(R.id.tianqi);
            mImageView = (ImageView) itemView.findViewById(R.id.img);
        }
    }

}
