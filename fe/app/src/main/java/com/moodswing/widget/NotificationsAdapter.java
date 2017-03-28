package com.moodswing.widget;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moodswing.R;
import com.moodswing.activity.CaptureActivity;
import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.presenter.NotificationsPresenter;

import java.util.List;

/**
 * Created by Kenny on 2017-03-27.
 */

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder> {
    private List<Capture> captures;
    private NotificationsPresenter _notificationsPresenter;
    private int blockIndex;
    Context nActivity;

    public void setData(List<Capture> captures, NotificationsPresenter notificationsPresenter, Context nActivity) {
        this.captures = captures;
        this._notificationsPresenter = notificationsPresenter;
        this.nActivity = nActivity;
        notifyDataSetChanged();
    }

    public NotificationsAdapter(List<Capture> captures) {
        this.captures = captures;
    }

    public void setRowIndex(int index) {
        blockIndex = index;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView _displayName;
        public TextView _date;
        public TextView _entryTitle;
        public TextView _entryText;


        public MyViewHolder(View view) {
            super(view);
            _displayName = (TextView) view.findViewById(R.id.notification_display_name);
            _date = (TextView) view.findViewById(R.id.notification_date);
            _entryTitle = (TextView) view.findViewById(R.id.notification_title);
            _entryText = (TextView) view.findViewById(R.id.notification_text);

        }
    }

    @Override
    public NotificationsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notifications_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotificationsAdapter.MyViewHolder holder, int position) {
        Capture capture = captures.get(position);
        holder._displayName.setText(capture.getDisplayName());
        holder._date.setText(capture.getDate());
        holder._entryTitle.setText(capture.getNotificationTitle());
        holder._entryText.setText(capture.getText());
        this.setRowIndex(position);

    }

    @Override
    public int getItemCount() {
        return captures.size();
    }

}
