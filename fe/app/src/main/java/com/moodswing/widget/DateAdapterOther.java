package com.moodswing.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.moodswing.R;
import com.moodswing.activity.CaptureActivity;
import com.moodswing.activity.CaptureActivityOther;
import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.CaptureDivider;
import com.moodswing.mvp.mvp.model.Comment;
import com.moodswing.mvp.mvp.presenter.JournalPresenter;
import com.moodswing.mvp.mvp.presenter.JournalPresenterOther;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Matthew on 2017-03-08.
 */

public class DateAdapterOther extends RecyclerView.Adapter<DateAdapterOther.MyViewHolder>{

    private List<DateBlock> dBlocks;
    private List<Capture> captures;
    private static RecyclerView _cRecyclerView;
    Context jActivityOther;
    JournalPresenterOther _journalPresenter;
    public static Intent captureIntent;
    public static String capUsername;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, date, numComments;
        private CaptureAdapterOther cAdapter;

        public MyViewHolder(View view) {
            super(view);
            final Context context = itemView.getContext();

            title = (TextView) view.findViewById(R.id.titleother);
            date = (TextView) view.findViewById(R.id.dateother);
            numComments = (TextView) view.findViewById(R.id.num_commentsother);

            cAdapter = new CaptureAdapterOther();
            _cRecyclerView = (RecyclerView) itemView.findViewById(R.id.capture_recycler_viewother);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context){
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            _cRecyclerView.setLayoutManager(layoutManager);
            _cRecyclerView.addItemDecoration(new CaptureDivider(context, LinearLayoutManager.VERTICAL));
            _cRecyclerView.setAdapter(cAdapter);

            _cRecyclerView.addOnItemTouchListener(new CaptureTouchListener(jActivityOther, _cRecyclerView, new CaptureTouchListener.ClickListener() {
                int capturePos = 0;
                @Override
                public void onClick(View view, int position) {
                    capturePos = getCaptureIndexInDateBlock(cAdapter, position);
                    Capture capture = captures.get(capturePos);
                    DateBlock dateBlock = dBlocks.get(cAdapter.getRowIndex());
                    openCapture(capture, dateBlock);
                }
            }));
        }
    }


    public DateAdapterOther(List<DateBlock> dBlocks, List<Capture> captures, Context c, JournalPresenterOther _journalPresenter) {
        this.dBlocks = dBlocks;
        this.captures = captures;
        this.jActivityOther = c;
        this._journalPresenter = _journalPresenter;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_list_containerother, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DateBlock db = dBlocks.get(position);
        List<Comment> comments = db.getComments();
        holder.date.setText(db.getDate());
        holder.title.setText(db.getTitle());

        int commentsSize = comments.size();
        String comText = " comments";
        if (commentsSize == 1) { comText = " comment"; }

        holder.numComments.setText(Integer.toString(commentsSize) + comText);

        ArrayList<Capture> temp = new ArrayList<>();
        for(Capture c: captures){
            if(c.getDate().equals(db.getDate())){
                temp.add(c);
            }
        }

        holder.cAdapter.setData(temp, _journalPresenter, jActivityOther);
        holder.cAdapter.setRowIndex(position);
    }

    @Override
    public int getItemCount() {
        return dBlocks.size();
    }

    private int getCaptureIndexInDateBlock(CaptureAdapterOther ca, int posInDateBlock){
        int numCapturesBeforeDateBlock = 0;
        DateBlock dateBlock = dBlocks.get(ca.getRowIndex());
        String sbDate = dateBlock.getDate();
        DateFormat df = new SimpleDateFormat("MMM.d, yyyy");
        try{
            Date dbDate = df.parse(sbDate);
            for(Capture c: captures){
                Date dcDate = df.parse(c.getDate());
                if (dcDate.after(dbDate)){
                    numCapturesBeforeDateBlock += 1;
                }
            }
        }catch(ParseException e) {
            e.printStackTrace();
        }
        return posInDateBlock + numCapturesBeforeDateBlock;
    }

    private void openCapture(Capture capture, DateBlock dBlock){
        String capTitle = dBlock.getTitle();
        String capDate = dBlock.getDate();
        String capText = capture.getText();
        String dateId = dBlock.getId();
        String capId = capture.getId();
        String capEmotion = capture.getEmotion();
        if (capEmotion == null){
            capEmotion = "UNKNOWN";
        }
        capUsername = dBlock.getUsername();
        captureIntent = new Intent(jActivityOther, CaptureActivityOther.class);
        captureIntent.putExtra("EXTRA_TITLE", capTitle);
        captureIntent.putExtra("EXTRA_DATE", capDate);
        captureIntent.putExtra("EXTRA_TEXT", capText);
        captureIntent.putExtra("EXTRA_DATEID", dateId);
        captureIntent.putExtra("EXTRA_CAPID", capId);
        captureIntent.putExtra("EXTRA_CAPEMOTION", capEmotion);

        _journalPresenter.getUser(capUsername);
    }

    public static Intent getCaptureIntent(){
        return captureIntent;
    }

    public static String getCapUsername(){
        return capUsername;
    }
}
