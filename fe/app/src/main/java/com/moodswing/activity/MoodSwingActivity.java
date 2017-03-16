package com.moodswing.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.moodswing.R;

import butterknife.BindView;

/**
 * Created by daniel on 17/03/17.
 *
 * Wrapper class to AppCompatActivity to centralize BottomNavigationView
 */

public class MoodSwingActivity extends AppCompatActivity {
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

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