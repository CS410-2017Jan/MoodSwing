package com.moodswing.mvp.mvp.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.moodswing.R;
import com.moodswing.activity.CaptureActivity;
import com.moodswing.mvp.mvp.presenter.JournalPresenter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Matthew on 2017-03-08.
 */

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.MyViewHolder>{

    private List<DateBlock> dBlocks;
    private List<Capture> captures;
    private static RecyclerView _cRecyclerView;
    Context jActivity;
    //Context context1;
    JournalPresenter _journalPresenter;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, date;
        private CaptureAdapter cAdapter;

        public MyViewHolder(View view) {
            super(view);
            final Context context = itemView.getContext();

            title = (TextView) view.findViewById(R.id.title);
            date = (TextView) view.findViewById(R.id.date);

            title.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(jActivity);
                    builder.setTitle("Edit Title");

                    final EditText input = new EditText(jActivity);

                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            title.setText(input.getText().toString());
                            DateBlock dateBlock = dBlocks.get(cAdapter.getRowIndex());
                            _journalPresenter.setTitle(input.getText().toString(), dateBlock.getId());
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();
                    return true;
                }
            });

            cAdapter = new CaptureAdapter();
            _cRecyclerView = (RecyclerView) itemView.findViewById(R.id.capture_recycler_view);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            _cRecyclerView.setLayoutManager(layoutManager);
            _cRecyclerView.addItemDecoration(new CaptureDivider(context, LinearLayoutManager.VERTICAL));
            _cRecyclerView.setAdapter(cAdapter);

            _cRecyclerView.addOnItemTouchListener(new CaptureTouchListener(jActivity, _cRecyclerView, new CaptureTouchListener.ClickListener() {
                int capturePos = 0;
                @Override
                public void onClick(View view, int position) {
                    capturePos = getCaptureIndexInDateBlock(cAdapter, position);
                    Capture capture = captures.get(capturePos);
                    DateBlock dateBlock = dBlocks.get(cAdapter.getRowIndex());

                    openCapture(capture, dateBlock);
                }

                @Override
                public void onLongClick(final View view, final int position) {
                    capturePos = getCaptureIndexInDateBlock(cAdapter, position);
                    final Capture capture = captures.get(capturePos);

                    PopupMenu popup = new PopupMenu(jActivity, view);
                    popup.getMenuInflater().inflate(R.menu.entry_popup_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getTitle().equals("Delete")){
                                displayDeleteWarning("Are you sure you want to delete your post?", capture);
                            }else{
                                Toast.makeText(jActivity, item.getTitle(), Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            }));
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


    }


    public DateAdapter(List<DateBlock> dBlocks, List<Capture> captures, Context c, Context context, JournalPresenter _journalPresenter) {
        this.dBlocks = dBlocks;
        this.captures = captures;
        this.jActivity = c;
        //this.context1 = context;
        this._journalPresenter = _journalPresenter;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_list_container, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DateBlock db = dBlocks.get(position);
        holder.date.setText(db.getDate());
        holder.title.setText(db.getTitle());

        ArrayList<Capture> temp = new ArrayList<>();
        for(Capture c: captures){
            if(c.getDate().equals(db.getDate())){
                temp.add(c);
            }
        }

        holder.cAdapter.setData(temp);
        holder.cAdapter.setRowIndex(position);
    }

    @Override
    public int getItemCount() {
        return dBlocks.size();
    }

    private int getCaptureIndexInDateBlock(CaptureAdapter ca, int posInDateBlock){
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
        Intent intent = new Intent(jActivity, CaptureActivity.class);
        intent.putExtra("EXTRA_TITLE", capTitle);
        intent.putExtra("EXTRA_DATE", capDate);
        intent.putExtra("EXTRA_TEXT", capText);
        jActivity.startActivity(intent);
    }
}