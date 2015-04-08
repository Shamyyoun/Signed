package com.mahmoudelshamy.signed;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.devspark.appmsg.AppMsg;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import adapters.LogsAdapter;
import datamodels.Log;
import datamodels.User;
import json.JsonReader;
import json.LogsHandler;
import utils.InternetUtil;
import views.DividerItemDecoration;
import views.ProgressActivity;


public class LogActivity extends ProgressActivity {
    private TextView textDay;
    private TextView textLoginTime;
    private TextView textLogoutTime;
    private RecyclerView recyclerLogs;

    private List<AsyncTask> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initComponents();
    }

    /**
     * method, used to init components
     */
    private void initComponents() {
        textDay = (TextView) findViewById(R.id.text_day);
        textLoginTime = (TextView) findViewById(R.id.text_loginTime);
        textLogoutTime = (TextView) findViewById(R.id.text_logoutTime);
        recyclerLogs = (RecyclerView) findViewById(R.id.recycler_log);

        tasks = new ArrayList<AsyncTask>();

        // customize fonts
        Typeface typeface = Typeface.createFromAsset(getAssets(), "ara_hamah.ttf");
        textDay.setTypeface(typeface);
        textLoginTime.setTypeface(typeface);
        textLogoutTime.setTypeface(typeface);

        // customize recycler view
        recyclerLogs.setHasFixedSize(true);
        recyclerLogs.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        recyclerLogs.setLayoutManager(new LinearLayoutManager(this));
        recyclerLogs.setItemAnimator(new DefaultItemAnimator());

        // load data
        new LogTask().execute();
    }

    /**
     * overriden method, used to return activity layout resource
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_log;
    }

    /**
     * overriden method, used to refresh content
     */
    @Override
    protected void onRefresh() {
        new LogTask().execute();
    }

    /**
     * overriden method
     */
    @Override
    protected void onDestroy() {
        for (AsyncTask task : tasks) {
            task.cancel(true);
        }

        // cancel all app msgs
        AppMsg.cancelAll();

        super.onDestroy();
    }

    /**
     * overriden method
     */
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.main_enter2, R.anim.logs_exit);
    }

    /**
     * sub class used to load logs from server
     */
    private class LogTask extends AsyncTask<Void, Void, Void> {
        private LogActivity activity;
        private String name;
        private String password;
        private String id;

        private String response;

        private LogTask() {
            activity = LogActivity.this;
            User user = AppController.getInstance(activity.getApplicationContext()).getActiveUser();
            name = user.getName();
            password = user.getPassword();
            id = user.getId();

            tasks.add(this); // hold reference to this task, to destroy it if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // check internet connection
            if (!InternetUtil.isConnected(activity)) {
                showError(R.string.no_internet_connection);

                cancel(true);
                return;
            }

            // show progress
            showProgress();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // create json reader
            String url = AppController.END_POINT + "/get-report.php";
            JsonReader jsonReader = new JsonReader(url);

            // prepare parameters
            List<NameValuePair> parameters = new ArrayList<>(3);
            parameters.add(new BasicNameValuePair("name", name));
            parameters.add(new BasicNameValuePair("password", password));
            parameters.add(new BasicNameValuePair("id", id));

            // execute request
            response = jsonReader.sendPostRequest(parameters);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // validate response
            if (response == null) {
                // show error msg
                showError(R.string.unexpected_error_try_again);

                return;
            }

            // ---response is valid---
            // handle it in log array
            LogsHandler handler = new LogsHandler(response);
            final Log[] logs = handler.handle();

            // check handling operation result
            if (logs == null) {
                // show error msg
                showError(R.string.unexpected_error_try_again);

                return;
            }

            // set recycler adapter
            LogsAdapter adapter = new LogsAdapter(activity.getApplicationContext(), logs, R.layout.recycler_log_item);
            recyclerLogs.setAdapter(adapter);

            showMain();
        }
    }
}
