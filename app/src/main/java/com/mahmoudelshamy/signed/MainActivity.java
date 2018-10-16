package com.mahmoudelshamy.signed;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.devspark.appmsg.AppMsg;

import java.util.Calendar;
import java.util.Locale;

import beacons.BeaconsService;
import datamodels.Cache;
import datamodels.Constants;
import utils.BluetoothUtil;


public class MainActivity extends ActionBarActivity {
    private ImageButton buttonLog;
    private ImageButton buttonSyncLogs;
    private TextView textTime;
    private View layoutConfirm;
    private ImageButton buttonConfirm;
    private TextView textConfirm;

    private Animation animation;

    private Calendar calendar;
    private Handler handler;
    private Runnable runnable;

    // message receiver objects
    private MessageReceiver mReceiver;
    private boolean mReceiverIsRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
    }

    /**
     * method used to init components
     */
    private void initComponents() {
        buttonLog = (ImageButton) findViewById(R.id.button_log);
        buttonSyncLogs = (ImageButton) findViewById(R.id.button_sync_logs);
        textTime = (TextView) findViewById(R.id.text_time);
        layoutConfirm = findViewById(R.id.layout_confirm);
        buttonConfirm = (ImageButton) findViewById(R.id.button_confirm);
        textConfirm = (TextView) findViewById(R.id.text_confirm);

        animation = AnimationUtils.loadAnimation(this, R.anim.button_rotation);
        mReceiver = new MessageReceiver();

        // init time components
        calendar = Calendar.getInstance(Locale.getDefault());
        runnable = new Runnable() {
            @Override
            public void run() {
                calendar.setTimeInMillis(System.currentTimeMillis());
                String hour = ("" + calendar.get(Calendar.HOUR)).length() == 1
                        ? ("0" + calendar.get(Calendar.HOUR))
                        : ("" + calendar.get(Calendar.HOUR));
                String minute = ("" + calendar.get(Calendar.MINUTE)).length() == 1
                        ? ("0" + calendar.get(Calendar.MINUTE))
                        : ("" + calendar.get(Calendar.MINUTE));
                String second = ("" + calendar.get(Calendar.SECOND)).length() == 1
                        ? ("0" + calendar.get(Calendar.SECOND))
                        : ("" + calendar.get(Calendar.SECOND));
                String am = calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";

                textTime.setText(hour + ":" + minute + ":" + second + "  " + am);

                handler.postDelayed(runnable, 1000);
            }
        };
        handler = new Handler();
        handler.post(runnable);

        // customize fonts
        Typeface typeface = Typeface.createFromAsset(getAssets(), "ara_hamah.ttf");
        textTime.setTypeface(typeface);
        textConfirm.setTypeface(typeface);

        // add listeners
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check state
                if (Cache.isLoggedIn(getApplicationContext())) {
                    // ---sign out user---
                    Intent intent = new Intent(getApplicationContext(), BeaconsService.class);
                    intent.putExtra(Constants.KEY_MESSAGE, Constants.MESSAGE_SIGN_OUT);
                    startService(intent);
                } else {
                    // --- sign in user ---
                    onButtonSignIn();
                }
            }
        });
        buttonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LogActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.logs_enter, R.anim.main_exit2);
            }
        });
        buttonSyncLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SyncLogsActivity.class);
                intent.putExtra(Constants.KEY_OVERRIDE_PENDING_TRANSITION, true);
                startActivity(intent);
                overridePendingTransition(R.anim.sync_logs_enter, R.anim.main_exit1);
            }
        });
    }

    /**
     * method, used to sign in user
     */
    private void onButtonSignIn() {
        // check bluetooth
        if (!BluetoothUtil.hasBluetooth()) {
            // not support bluetooth
            showMsg(R.string.not_support_bluetooth, AppMsg.STYLE_CONFIRM);

            return;
        }

        // --support--
        // check bluetooth state
        if (!BluetoothUtil.isEnabled()) {
            // not enabled >> ask user to enable it
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Constants.REQUEST_CODE_ENABLE_BLUETOOTH);

            return;
        } else {
            // enabled >> start service
            Intent serviceIntent = new Intent(getApplicationContext(), BeaconsService.class);
            startService(serviceIntent);

            // start progress
            startProgress(true);

            // update waiting login flag in sp
            Cache.setWaitingLogin(this, true);
        }
    }

    /**
     * method, used to inform user that he signed in
     */
    private void signedIn() {
        // stop progress
        startProgress(false);

        // show success msg
        showMsg(R.string.signin_successful, AppMsg.STYLE_INFO);

        // change confirm button state
        textConfirm.setText(R.string.sign_out);
        buttonConfirm.setScaleX(-1);
    }

    /**
     * method, used to inform user that he signed out
     */
    private void signedOut() {
        // rotate confirm button
        animation.setRepeatCount(0);
        layoutConfirm.startAnimation(animation);

        // change confirm UI
        buttonConfirm.setScaleX(1);
        textConfirm.setText(R.string.sign_in);

        // show success msg
        showMsg(R.string.signout_successful, AppMsg.STYLE_INFO);
    }

    /**
     * overriden method
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                // start service
                Intent serviceIntent = new Intent(getApplicationContext(), BeaconsService.class);
                startService(serviceIntent);

                // start progress
                startProgress(true);

                // update waiting login flag in sp
                Cache.setWaitingLogin(this, true);
            } else {
                showMsg(R.string.bluetooth_not_enabled, AppMsg.STYLE_CONFIRM);
            }
        }
    }

    /**
     * overriden method
     */
    @Override
    protected void onResume() {
        super.onResume();

        // check if user waiting login
        if (Cache.isWaitingLogin(this)) {
            // resume progress
            startProgress(true);
        } else {
            // stop progress
            startProgress(false);
        }

        // check if user logged in
        if (Cache.isLoggedIn(getApplicationContext())) {
            // update UI
            buttonConfirm.setScaleX(-1);
            textConfirm.setText(R.string.sign_out);
        }

        if (!mReceiverIsRegistered) {
            registerReceiver(mReceiver, new IntentFilter(getPackageName() + ".MONITORING_CHANGED"));
            mReceiverIsRegistered = true;
        }
    }

    /**
     * overriden method
     */
    @Override
    protected void onPause() {
        super.onPause();

        if (mReceiverIsRegistered) {
            unregisterReceiver(mReceiver);
            mReceiverIsRegistered = false;
        }
    }

    /**
     * overriden method
     */
    @Override
    protected void onDestroy() {
        // stop all running tasks
        handler.removeCallbacks(runnable);

        // cancel all app msgs
        AppMsg.cancelAll();

        super.onDestroy();
    }

    /**
     * method, used to show app msg
     */
    private void showMsg(int msg, AppMsg.Style style) {
        AppMsg appMsg = AppMsg.makeText(this, msg, style);
        appMsg.setParent(R.id.layout_content);
        AppMsg.cancelAll();
        appMsg.show();
    }

    /**
     * method, used to start or stop progress
     */
    private void startProgress(boolean start) {
        if (start) {
            animation.setRepeatCount(Animation.INFINITE);
            layoutConfirm.startAnimation(animation);
            buttonConfirm.setEnabled(false);
        } else {
            animation.setRepeatCount(0);
            buttonConfirm.setEnabled(true);
        }
    }

    /*
     * sub class, used to listen for messages from service
	 */
    private class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                // get message
                int message = intent.getExtras().getInt(Constants.KEY_MESSAGE);

                // switch message to do the suitable action
                switch (message) {
                    case Constants.MESSAGE_SIGNED_IN:
                        signedIn();
                        break;

                    case Constants.MESSAGE_SIGNED_OUT:
                        signedOut();
                        break;

                    case Constants.MESSAGE_STOP_LOGIN_WAITING:
                        startProgress(false);
                        break;
                }
            }
        }
    }
}
