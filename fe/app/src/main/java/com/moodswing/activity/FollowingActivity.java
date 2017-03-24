package com.moodswing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.moodswing.MoodSwingApplication;
import com.moodswing.R;
import com.moodswing.injector.component.ApplicationComponent;
import com.moodswing.injector.component.DaggerFollowingComponent;
import com.moodswing.injector.component.FollowingComponent;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.FollowingModule;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.mvp.presenter.FollowingPresenter;
import com.moodswing.mvp.mvp.view.FollowingView;
import com.moodswing.widget.SearchAdapter;

import java.util.List;

import javax.inject.Inject2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by daniel on 23/03/17.
 */
public class FollowingActivity extends MoodSwingActivity implements FollowingView {
    @Inject2
    FollowingPresenter _followingPresenter;

    @Inject2
    SharedPreferencesManager _sharedPreferencesManager;

    @BindView(R.id.following_list_view)
    ListView listView;

    private SearchAdapter searchAdapter;

    private FollowingComponent _followingComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        ButterKnife.bind(this);
        ApplicationComponent applicationComponent = ((MoodSwingApplication) getApplication()).getApplicationComponent();

        _followingComponent = DaggerFollowingComponent.builder()
                .followingModule(new FollowingModule())
                .activityModule(new ActivityModule(this))
                .applicationComponent(applicationComponent)
                .build();

        _followingComponent.inject(this);

        initializePresenter();
        initializeBottomNavigationView();

        _followingPresenter.getFollowing();
    }

    private void initializePresenter() {
        _followingPresenter.attachView(this);
        _followingPresenter.attachSharedPreferencesManager(_sharedPreferencesManager);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getApplicationContext(), error , Toast.LENGTH_LONG).show();
    }

    @Override
    public void initializeListView(List<User> following) {
        searchAdapter = new SearchAdapter(following, FollowingActivity.this);
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
        searchView.setQueryHint("Search following...");

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
    public void initializeBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search:
                        Intent intent1 = new Intent(getApplicationContext(), SearchActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.action_camera:
                        Intent intent2 = new Intent(getApplicationContext(), CameraActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.action_follows:
                        // Do nothing
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }
}
