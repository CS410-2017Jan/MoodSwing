package com.moodswing.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.moodswing.R;
import com.moodswing.activity.EditEntryActivity;
import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.presenter.JournalPresenter;
import com.moodswing.mvp.mvp.presenter.JournalPresenterOther;

import java.util.List;

/**
 * Created by Matthew on 2017-03-10.
 */

public class CaptureAdapterOther extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Capture> captures;
    private int blockIndex;
    JournalPresenterOther _journalPresenter;
    Context jActivityOther;

    public CaptureAdapterOther() {
    }

    public void setData(List<Capture> captures, JournalPresenterOther journalPresenter, Context jActivity) {
        this.captures = captures;
        this._journalPresenter = journalPresenter;
        this.jActivityOther = jActivity;
        notifyDataSetChanged();
    }

    public void setRowIndex(int index) {
        blockIndex = index;
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView text;
        private ImageView entryPic;

        public MyViewHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.descriptionother);
            entryPic = (ImageView) view.findViewById(R.id.listViewImageother);
        }
    }

    private void StartEditActivity(String text, String id){
        Intent intent = new Intent(jActivityOther, EditEntryActivity.class);
        intent.putExtra("TEXT", text);
        intent.putExtra("ID", id);
        jActivityOther.startActivity(intent);
    }

    private void displayDeleteWarning(String s, final Capture capture) {
        AlertDialog.Builder builder = new AlertDialog.Builder(jActivityOther);
        builder.setTitle("Warning");
        builder.setMessage(s);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                _journalPresenter.deleteCapture(capture);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.capture_list_rowother, parent, false);
        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rHolder, int position) {
        MyViewHolder holder = (MyViewHolder) rHolder;
        Capture capture = captures.get(position);
        holder.text.setText(capture.getText());
        holder.text.setTag(position);
        if (capture.getHasImage()){
            holder.entryPic.setVisibility(View.VISIBLE);
            holder.entryPic.setImageBitmap(capture.getImage());
            holder.text.setPadding(0,0,0,0);
        }else{
            holder.entryPic.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = holder.text.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.text.setPadding(0,50,0,50);
            holder.text.setLayoutParams(params);
        }
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
