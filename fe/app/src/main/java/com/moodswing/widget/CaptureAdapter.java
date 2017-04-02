package com.moodswing.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.moodswing.R;
import com.moodswing.activity.EditEntryActivity;
import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.presenter.JournalPresenter;

import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Matthew on 2017-03-10.
 */

public class CaptureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Capture> captures;
    private int blockIndex;
    JournalPresenter _journalPresenter;
    Context jActivity;

    public CaptureAdapter() {
    }

    public void setData(List<Capture> captures, JournalPresenter journalPresenter, Context jActivity) {
        this.captures = captures;
        this._journalPresenter = journalPresenter;
        this.jActivity = jActivity;
        notifyDataSetChanged();
    }

    public void setRowIndex(int index) {
        blockIndex = index;
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView text;
        private ImageView entryPic, entryEmotion;
        private ProgressBar progressBar;
        private ImageButton _options;

        public MyViewHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.description);
            entryPic = (ImageView) view.findViewById(R.id.listViewImage);
            entryEmotion = (ImageView) view.findViewById(R.id.listViewEmotion);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            _options = (ImageButton) view.findViewById(R.id.entry_options);

            _options.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = (int) text.getTag();
                    final Capture capture = captures.get(pos);

                    PopupMenu popup = new PopupMenu(jActivity, v);
                    popup.getMenuInflater().inflate(R.menu.entry_popup_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getTitle().equals("Delete")){
                                displayDeleteWarning("Are you sure you want to delete your post?", capture);
                            }if(item.getTitle().equals("Edit")){
                                StartEditActivity(capture.getText(), capture.getId());
                            }
                            return true;
                        }
                    });
                    popup.show();
                    return true;
                }
            });
        }
    }

    private void StartEditActivity(String text, String id){
        Intent intent = new Intent(jActivity, EditEntryActivity.class);
        intent.putExtra("TEXT", text);
        intent.putExtra("ID", id);
        jActivity.startActivity(intent);
    }

    private void displayDeleteWarning(String s, final Capture capture) {
        AlertDialog.Builder builder = new AlertDialog.Builder(jActivity);
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.capture_list_row, parent, false);
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
            }else{
                holder.entryEmotion.setVisibility(View.GONE);
                holder.entryPic.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = holder.text.getLayoutParams();
                if (capture.getText().length() > 200){
                    params.height = dpToPx(92);
                    holder.text.setPadding(0,0,0,0);
                } else{
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    holder.text.setPadding(0,50,0,50);
                }
                holder.text.setLayoutParams(params);
            }
        }
        holder.itemView.setTag(position);
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = jActivity.getResources().getDisplayMetrics();
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
                return ResourcesCompat.getDrawable(jActivity.getResources(), R.drawable.relaxed_emoji, null);
            case "SMILEY":
                return ResourcesCompat.getDrawable(jActivity.getResources(), R.drawable.smiley_emoji, null);
            case "LAUGHING":
                return ResourcesCompat.getDrawable(jActivity.getResources(), R.drawable.laughing_emoji, null);
            case "WINK":
                return ResourcesCompat.getDrawable(jActivity.getResources(), R.drawable.wink_emoji, null);
            case "SMIRK":
                return ResourcesCompat.getDrawable(jActivity.getResources(), R.drawable.smirk_emoji, null);
            case "KISSING":
                return ResourcesCompat.getDrawable(jActivity.getResources(), R.drawable.kissing_emoji, null);
            case "STUCK_OUT_TONGUE":
                return ResourcesCompat.getDrawable(jActivity.getResources(), R.drawable.stuck_out_tongue_emoji, null);
            case "STUCK_OUT_TONGUE_WINKING_EYE":
                return ResourcesCompat.getDrawable(jActivity.getResources(), R.drawable.stuck_out_tongue_winking_eye_emoji, null);
            case "DISAPPOINTED":
                return ResourcesCompat.getDrawable(jActivity.getResources(), R.drawable.disappointed_emoji, null);
            case "RAGE":
                return ResourcesCompat.getDrawable(jActivity.getResources(), R.drawable.rage_emoji, null);
            case "SCREAM":
                return ResourcesCompat.getDrawable(jActivity.getResources(), R.drawable.scream_emoji, null);
            case "FLUSHED":
                return ResourcesCompat.getDrawable(jActivity.getResources(), R.drawable.flushed_emoji, null);
            default: // UNKNOWN
                return ResourcesCompat.getDrawable(jActivity.getResources(), R.drawable.blank_emoji, null);
        }
    }
}
