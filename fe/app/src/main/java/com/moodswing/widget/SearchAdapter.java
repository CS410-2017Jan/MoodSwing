package com.moodswing.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.moodswing.R;
import com.moodswing.mvp.mvp.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by daniel on 24/03/17.
 */

public class SearchAdapter extends BaseAdapter {

    protected class ViewHolder {
        TextView textUsername;
        TextView textDisplayName;
    }

    protected Context context;
    protected List<User> usersList;
    protected List<User> parkingList;

    public SearchAdapter(List<User> users, Context context) {
        parkingList = users;
        this.usersList = new ArrayList<>();
        this.usersList.addAll(users);
        this.context = context;
    }

    @Override
    public int getCount() {
        return parkingList.size();
    }

    @Override
    public Object getItem(int position) {
        return parkingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder viewHolder;

        if (rowView == null) {
            LayoutInflater  inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.search_item, null);
            // configure view holder
            viewHolder = new ViewHolder();
            viewHolder.textUsername = (TextView) rowView.findViewById(R.id.user_name);
            viewHolder.textDisplayName = (TextView) rowView.findViewById(R.id.display_name);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textUsername.setText(parkingList.get(position).getUsername());
        viewHolder.textDisplayName.setText(parkingList.get(position).getDisplayName());
        return rowView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        parkingList.clear();

        for (User user : usersList)  {
            if (charText.length() != 0 && user.getUsername().
                    toLowerCase(Locale.getDefault()).contains(charText)){
                parkingList.add(user);
            } else if (charText.length() != 0 && user.getDisplayName().
                    toLowerCase(Locale.getDefault()).contains(charText)){
                parkingList.add(user);
            }
        }
        notifyDataSetChanged();
    }
}
