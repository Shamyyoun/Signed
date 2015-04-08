package com.mahmoudelshamy.signed;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;


public class SplashActivity extends ActionBarActivity {
    private static final int SPLASH_TIME = 1 * 1000;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // init splash handler and runnable
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Class activityClass;

                // check active user
                if (AppController.getInstance(getApplicationContext()).getActiveUser() == null)
                    // no active user >> goto login activity
                    activityClass = LoginActivity.class;
                else
                    // there is an active user >> goto main activity
                    activityClass = MainActivity.class;

                // goto suitable activity
                Intent intent = new Intent(SplashActivity.this, activityClass);
                startActivity(intent);
                SplashActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };

        handler.postDelayed(runnable, SPLASH_TIME);
    }
}
