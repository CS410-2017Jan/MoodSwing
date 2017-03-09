package com.moodswing.mvp.mvp.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moodswing.R;

import java.util.List;

/**
 * Created by Matthew on 2017-03-08.
 */

public class CapturesAdapter extends RecyclerView.Adapter<CapturesAdapter.MyViewHolder>{

    private List<Capture> captures;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView description;

        public MyViewHolder(View view) {
            super(view);
            description = (TextView) view.findViewById(R.id.description);
        }
    }


    public CapturesAdapter(List<Capture> captures) {
        this.captures = captures;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.capture_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Capture capture = captures.get(position);
        holder.description.setText(capture.getText());
    }

    @Override
    public int getItemCount() {
        return captures.size();
    }
}
