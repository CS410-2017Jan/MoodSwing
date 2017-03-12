package com.moodswing.mvp.mvp.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moodswing.R;

import java.util.List;

/**
 * Created by Matthew on 2017-03-10.
 */

public class CaptureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Capture> captures;
    private int blockIndex;

    public CaptureAdapter() {
    }

    public void setData(List<Capture> captures) {
            this.captures = captures;
            notifyDataSetChanged();
    }

    public void setRowIndex(int index) {
        blockIndex = index;
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView text;

        public MyViewHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.description);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.capture_list_row, parent, false);
        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rHolder, int position) {
        MyViewHolder holder = (MyViewHolder) rHolder;
        Capture capture = captures.get(position);
        holder.text.setText(capture.getText());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return captures.size();
    }

    public int getRowIndex() {
        return blockIndex;
    }
}
