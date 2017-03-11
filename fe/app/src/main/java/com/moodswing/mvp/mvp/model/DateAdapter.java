package com.moodswing.mvp.mvp.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.moodswing.R;
import com.moodswing.activity.JournalActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Matthew on 2017-03-08.
 */

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.MyViewHolder>{

    private List<DateBlock> dBlocks;
    private List<Capture> captures;
    private static RecyclerView _cRecyclerView;
    Context jActivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, date;
        private CaptureAdapter cAdapter;

        public MyViewHolder(View view) {
            super(view);
            Context context = itemView.getContext();
            title = (TextView) view.findViewById(R.id.title);
            date = (TextView) view.findViewById(R.id.date);
            cAdapter = new CaptureAdapter();
            _cRecyclerView = (RecyclerView) itemView.findViewById(R.id.capture_recycler_view);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            _cRecyclerView.setLayoutManager(layoutManager);
            _cRecyclerView.addItemDecoration(new CaptureDivider(context, LinearLayoutManager.VERTICAL));
            _cRecyclerView.setAdapter(cAdapter);


            // TODO: onLongClick selects wrong item right now
            _cRecyclerView.addOnItemTouchListener(new CaptureTouchListener(jActivity, _cRecyclerView, new CaptureTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    // TODO: Will go to separate page to view entry
                }

                @Override
                public void onLongClick(final View view, final int position) {

                    PopupMenu popup = new PopupMenu(jActivity, view);
                    popup.getMenuInflater().inflate(R.menu.entry_popup_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getTitle().equals("Delete")){
                                Log.i("CHECK", "*************************************************************");
                                Capture capture = captures.get(position);
                                Toast.makeText(jActivity, capture.getText() + capture.getDate(), Toast.LENGTH_SHORT).show();
                                displayDeleteWarning("Are you sure you want to delete your post?");
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


        private void displayDeleteWarning(String s) {
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
                    Toast.makeText(jActivity, "Delete", Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    public DateAdapter(List<DateBlock> dBlocks, List<Capture> captures, Context c) {
        this.dBlocks = dBlocks;
        this.captures = captures;
        this.jActivity = c;
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
}