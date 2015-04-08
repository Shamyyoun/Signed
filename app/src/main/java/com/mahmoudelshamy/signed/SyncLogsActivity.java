package com.mahmoudelshamy.signed;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.devspark.appmsg.AppMsg;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import database.SignLogDAO;
import datamodels.Constants;
import datamodels.SignLog;
import json.JsonReader;
import utils.InternetUtil;


public class SyncLogsActivity extends ActionBarActivity {
    private boolean overridePendingTransition;

    private TextView textLogs;
    private ImageButton buttonSync;

    private Animation animation;
    private SignLogDAO logDAO;
    private List<SignLog> signLogs;

    private int successfulRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_logs);

        initComponents();
    }

    /**
     * method, used to initialize components
     */
    private void initComponents() {
        overridePendingTransition = getIntent().getBooleanExtra(Constants.KEY_OVERRIDE_PENDING_TRANSITION, false);

        textLogs = (TextView) findViewById(R.id.text_logs);
        buttonSync = (ImageButton) findViewById(R.id.button_sync);

        animation = AnimationUtils.loadAnimation(this, R.anim.button_rotation);
        logDAO = new SignLogDAO(getApplicationContext());

        // customize fonts
        Typeface typeface = Typeface.createFromAsset(getAssets(), "ara_hamah.ttf");
        textLogs.setTypeface(typeface);

        // get has logs or not from DB
        logDAO.open();
        signLogs = logDAO.getAll(AppController.getInstance(getApplicationContext()).getActiveUser().getId());
        logDAO.close();

        // set logs count
        textLogs.setText(signLogs.size() + " " + getString(R.string.logs));

        // add click listener
        buttonSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SignTask().execute();
            }
        });
    }

    /**
     * sub class, used to send unsynced logs to server
     */
    private class SignTask extends AsyncTask<Void, Void, Void> {

        private SignTask() {
            successfulRequests = 0;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // check is has logs
            if (signLogs.size() == 0) {
                showMsg(R.string.no_unsynced_logs, AppMsg.STYLE_CONFIRM);

                cancel(true);
                return;
            }

            // check internet connection
            if (!InternetUtil.isConnected(getApplicationContext())) {
                showMsg(R.string.no_internet_connection, AppMsg.STYLE_CONFIRM);

                cancel(true);
                return;
            }

            // start progress
            startProgress(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (final SignLog log : signLogs) {
                // prepare suitable url and sign parameter
                String url = AppController.END_POINT;
                url += (log.getType() == SignLog.TYPE_SIGN_IN ? "/co-login.php" : "/co-logout.php");
                String signParam = log.getType() == SignLog.TYPE_SIGN_IN ? "login_time" : "logout_time";

                // create json reader
                JsonReader jsonReader = new JsonReader(url);

                // prepare parameters
                List<NameValuePair> parameters = new ArrayList<>(5);
                parameters.add(new BasicNameValuePair("name", log.getName()));
                parameters.add(new BasicNameValuePair("password", log.getPassword()));
                parameters.add(new BasicNameValuePair("id", log.getUserId()));
                parameters.add(new BasicNameValuePair("day", log.getDay()));
                parameters.add(new BasicNameValuePair(signParam, log.getTime()));

                // execute request
                String response = jsonReader.sendPostRequest(parameters);

                // check response
                if (Constants.JSON_MSG_SUCCESS.equals(response)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // successful request >> delete it
                            signLogs.remove(log);
                            logDAO.open();
                            logDAO.delete(log.getId());
                            logDAO.close();

                            // increment successful requests
                            successfulRequests++;

                            // update logs number text
                            textLogs.setText(signLogs.size() + " " + getString(R.string.logs));
                        }
                    });
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // stop progress
            startProgress(false);

            // show msg
            String msg = successfulRequests + " " + getString(R.string.logs) + " " + getString(R.string.synced_sucessfully);
            showMsg(msg, AppMsg.STYLE_INFO);
        }
    }

    /**
     * overloaded method, used to show app msg
     */
    private void showMsg(int msg, AppMsg.Style style) {
        showMsg(getString(msg), style);
    }

    /**
     * overloaded method, used to show app msg
     */
    private void showMsg(String msg, AppMsg.Style style) {
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
            buttonSync.startAnimation(animation);
            buttonSync.setEnabled(false);
        } else {
            animation.setRepeatCount(0);
            buttonSync.setEnabled(true);
        }
    }

    /**
     * overriden method
     */
    @Override
    public void onBackPressed() {
        finish();
        if (overridePendingTransition)
            overridePendingTransition(R.anim.main_enter1, R.anim.sync_logs_exit);
    }

    /**
     * overriden method
     */
    @Override
    protected void onDestroy() {
        // cancel all app msgs
        AppMsg.cancelAll();

        super.onDestroy();
    }
}
