package com.moodswing.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.moodswing.MoodSwingApplication;
import com.moodswing.R;
import com.moodswing.injector.component.ApplicationComponent;
import com.moodswing.injector.component.DaggerSearchComponent;
import com.moodswing.injector.component.SearchComponent;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.SearchModule;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.mvp.presenter.SearchPresenter;
import com.moodswing.mvp.mvp.view.SearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by daniel on 16/03/17.
 */

public class SearchActivity extends MoodSwingActivity implements SearchView {
    @Inject2
    SearchPresenter _searchPresenter;

    @BindView(R.id.search_list_view)
    ListView listView;

    private SearchAdapter searchAdapter;

    private SearchComponent _searchComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        ApplicationComponent applicationComponent = ((MoodSwingApplication) getApplication()).getApplicationComponent();

        _searchComponent = DaggerSearchComponent.builder()
                .searchModule(new SearchModule())
                .activityModule(new ActivityModule(this))
                .applicationComponent(applicationComponent)
                .build();
        _searchComponent.inject(this);

        initializePresenter();
        // Fetch users
        _searchPresenter.search();

        initializeBottomNavigationView();
    }

    private void initializePresenter() {
        _searchPresenter.attachView(this);
    }

    @Override
    public void initializeListView(List<User> users) {
        searchAdapter = new SearchAdapter(users, SearchActivity.this);
        listView.setAdapter(searchAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) listView.getAdapter().getItem(position);
                // TODO: pass user info to activity to view this user's journal
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView)
                MenuItemCompat.getActionView(searchItem);
        searchItem.expandActionView();
        searchView.setQueryHint("Search...");

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {
                if (searchAdapter != null) {
                    searchAdapter.filter(searchQuery.trim());
                    listView.invalidate();
                }
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getApplicationContext(), "Error encountered while searching: " + error ,Toast.LENGTH_LONG).show();
    }

    private class SearchAdapter extends BaseAdapter {

        private class ViewHolder {
            TextView textUsername;
            TextView textDisplayName;
        }

        private Context context;
        private List<User> usersList;
        private List<User> parkingList;

        private SearchAdapter(List<User> users, Context context) {
            parkingList = new ArrayList<>();
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
                LayoutInflater inflater = getLayoutInflater();
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
                        toLowerCase(Locale.getDefault()).contains(charText)) {
                    parkingList.add(user);
                } else if (charText.length() != 0 && user.getDisplayName().
                        toLowerCase(Locale.getDefault()).contains(charText)) {
                    parkingList.add(user);
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public void initializeBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search:
                        // Do nothing
                        break;
                    case R.id.action_camera:
                        Intent intent2 = new Intent(getApplicationContext(), CameraActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.action_follows:
                        // TODO: Re-direct to follows
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }
}
