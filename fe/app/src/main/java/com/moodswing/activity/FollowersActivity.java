package com.moodswing.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.moodswing.injector.component.DaggerFollowersComponent;
import com.moodswing.injector.component.FollowersComponent;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.FollowersModule;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.mvp.presenter.FollowersPresenter;
import com.moodswing.mvp.mvp.view.FollowersView;
import com.moodswing.widget.SearchAdapter;

import java.util.List;

import javax.inject.Inject2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by daniel on 24/03/17.
 */

public class FollowersActivity extends MoodSwingActivity implements FollowersView {
    @Inject2
    FollowersPresenter _followersPresenter;

    @Inject2
    SharedPreferencesManager _sharedPreferencesManager;

    @BindView(R.id.followers_list_view)
    ListView listView;

    private SearchAdapter searchAdapter;
    private FollowersComponent _followersComponent;
    private List<String> following;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        ButterKnife.bind(this);
        ApplicationComponent applicationComponent = ((MoodSwingApplication) getApplication()).getApplicationComponent();

        _followersComponent = DaggerFollowersComponent.builder()
                .followersModule(new FollowersModule())
                .activityModule(new ActivityModule(this))
                .applicationComponent(applicationComponent)
                .build();

        _followersComponent.inject(this);

        initializePresenter();
        initializeBottomNavigationView();

        _followersPresenter.getFollowers();
    }

    private void initializePresenter() {
        _followersPresenter.attachView(this);
        _followersPresenter.attachSharedPreferencesManager(_sharedPreferencesManager);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getApplicationContext(), error , Toast.LENGTH_LONG).show();
    }

    @Override
    public void initializeListView(final List<User> followers, final List<String> following) {
        searchAdapter = new SearchAdapter(followers, FollowersActivity.this);
        listView.setAdapter(searchAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) listView.getAdapter().getItem(position);
                Intent intent = new Intent(getApplicationContext(), JournalActivityOther.class);
                intent.putExtra("USER_DISPLAYNAME", user.getDisplayName());
                intent.putExtra("USER_USERNAME", user.getUsername());
                if (following != null) {
                    String[] followingArr = following.toArray(new String[following.size()]);
                    intent.putExtra("USER_FOLLOWING", followingArr);
                }
                startActivity(intent);
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
        searchView.setQueryHint("Search followers...");

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
}
