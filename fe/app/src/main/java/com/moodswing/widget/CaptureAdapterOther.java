package com.moodswing.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
        private ImageView entryPic, entryEmotion;
        private ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.descriptionother);
            entryPic = (ImageView) view.findViewById(R.id.listViewImageother);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBarother);
            entryEmotion = (ImageView) view.findViewById(R.id.listViewEmotionother);
        }
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
        if (capture.getWaitingForImageResponse()){
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.entryPic.setVisibility(View.INVISIBLE);
        }else{
            holder.progressBar.setVisibility(View.GONE);
            if (capture.getHasImage()){
                String emotion = capture.getEmotion();
                if (emotion.equals("UNKNOWN")){
                    holder.entryEmotion.setVisibility(View.GONE);
                }else{
                    holder.entryEmotion.setVisibility(View.VISIBLE);
                    holder.entryEmotion.setBackground(setEmoji(emotion));
                }
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.entryPic.setVisibility(View.VISIBLE);
                holder.entryPic.setImageBitmap(capture.getImage());
                holder.text.setPadding(0,0,0,0);
            }else {
                holder.entryEmotion.setVisibility(View.GONE);
                holder.entryPic.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = holder.text.getLayoutParams();
                if (capture.getText().length() > 200) {
                    params.height = dpToPx(92);
                    holder.text.setPadding(0, 0, 0, 0);
                } else {
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    holder.text.setPadding(0, 50, 0, 50);
                }
                holder.text.setLayoutParams(params);
            }
        }
        holder.itemView.setTag(position);
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = jActivityOther.getResources().getDisplayMetrics();
        return (int)((dp * displayMetrics.density) + 0.5);
    }

    @Override
    public int getItemCount() {
        return captures.size();
    }

    public int getRowIndex() {
        return blockIndex;
    }

    public Drawable setEmoji(String emotion) {
        switch (emotion) {
            case "RELAXED":
                return ResourcesCompat.getDrawable(jActivityOther.getResources(), R.drawable.relaxed_emoji, null);
            case "SMILEY":
                return ResourcesCompat.getDrawable(jActivityOther.getResources(), R.drawable.smiley_emoji, null);
            case "LAUGHING":
                return ResourcesCompat.getDrawable(jActivityOther.getResources(), R.drawable.laughing_emoji, null);
            case "WINK":
                return ResourcesCompat.getDrawable(jActivityOther.getResources(), R.drawable.wink_emoji, null);
            case "SMIRK":
                return ResourcesCompat.getDrawable(jActivityOther.getResources(), R.drawable.smirk_emoji, null);
            case "KISSING":
                return ResourcesCompat.getDrawable(jActivityOther.getResources(), R.drawable.kissing_emoji, null);
            case "STUCK_OUT_TONGUE":
                return ResourcesCompat.getDrawable(jActivityOther.getResources(), R.drawable.stuck_out_tongue_emoji, null);
            case "STUCK_OUT_TONGUE_WINKING_EYE":
                return ResourcesCompat.getDrawable(jActivityOther.getResources(), R.drawable.stuck_out_tongue_winking_eye_emoji, null);
            case "DISAPPOINTED":
                return ResourcesCompat.getDrawable(jActivityOther.getResources(), R.drawable.disappointed_emoji, null);
            case "RAGE":
                return ResourcesCompat.getDrawable(jActivityOther.getResources(), R.drawable.rage_emoji, null);
            case "SCREAM":
                return ResourcesCompat.getDrawable(jActivityOther.getResources(), R.drawable.scream_emoji, null);
            case "FLUSHED":
                return ResourcesCompat.getDrawable(jActivityOther.getResources(), R.drawable.flushed_emoji, null);
            default: // UNKNOWN
                return ResourcesCompat.getDrawable(jActivityOther.getResources(), R.drawable.blank_emoji, null);
        }
    }
}
