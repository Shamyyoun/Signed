package com.mahmoudelshamy.signed;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.devspark.appmsg.AppMsg;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import beacons.RememberSyncLogsReceiver;
import datamodels.Cache;
import datamodels.Constants;
import datamodels.User;
import json.JsonReader;
import json.UserHandler;
import utils.InternetUtil;
import utils.ViewUtil;


public class LoginActivity extends ActionBarActivity {
    private ViewGroup layoutLoginForm;
    private EditText textUsername;
    private EditText textPassword;
    private Button buttonLogin;
    private ProgressBar progressBar;

    private List<AsyncTask> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComponents();
    }

    private void initComponents() {
        layoutLoginForm = (ViewGroup) findViewById(R.id.layout_loginForm);
        textUsername = (EditText) findViewById(R.id.text_username);
        textPassword = (EditText) findViewById(R.id.text_password);
        buttonLogin = (Button) findViewById(R.id.button_login);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        tasks = new ArrayList<AsyncTask>();

        // customize fonts
        Typeface typeface = Typeface.createFromAsset(getAssets(), "ara_hamah.ttf");
        textUsername.setTypeface(typeface);
        textPassword.setTypeface(typeface);
        buttonLogin.setTypeface(typeface);

        // customize hints
        String color = String.format("#%06X", 0xFFFFFF & getResources().getColor(R.color.white));
        textUsername.setHint(Html.fromHtml("<font color='" + color + "'>" + getString(R.string.username) + "</font>"));
        textPassword.setHint(Html.fromHtml("<font color='" + color + "'>" + getString(R.string.password) + "</font>"));

        // add listeners
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = textUsername.getText().toString();
                String password = textPassword.getText().toString();
                new LoginTask(username, password).execute();
            }
        });
    }

    /**
     * overriden method
     */
    @Override
    protected void onDestroy() {
        // stop all running tasks
        for (AsyncTask task : tasks) {
            task.cancel(true);
        }

        // cancel all app msgs
        AppMsg.cancelAll();

        super.onDestroy();
    }

    /**
     * sub class, used to send login request
     */
    private class LoginTask extends AsyncTask<Void, Void, Void> {
        private String name;
        private String password;

        private LoginActivity activity;

        private String response;

        private LoginTask(String name, String password) {
            this.name = name;
            this.password = password;

            activity = LoginActivity.this;

            tasks.add(this); // save reference to this task to destroy it if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // validate inputs
            if (name.isEmpty()) {
                textUsername.setError(getString(R.string.username_cant_be_empty));

                cancel(true);
                return;
            }
            if (password.isEmpty()) {
                textPassword.setError(getString(R.string.password_cant_be_empty));

                cancel(true);
                return;
            }

            // check internet connection
            if (!InternetUtil.isConnected(activity)) {
                showError(R.string.no_internet_connection);

                cancel(true);
                return;
            }

            // all conditions is true >> show progress
            showProgress(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // create json reader
            String url = AppController.END_POINT + "/login.php";
            JsonReader jsonReader = new JsonReader(url);

            // prepare parameters
            List<NameValuePair> parameters = new ArrayList<>(2);
            parameters.add(new BasicNameValuePair("name", name));
            parameters.add(new BasicNameValuePair("password", password));

            // execute request
            response = jsonReader.sendPostRequest(parameters);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // hide progress
            showProgress(false);

            // ensure response is not null
            if (response == null) {
                showError(R.string.unexpected_error_try_again);

                return;
            }

            // not null >> check result
            if (response.equals(Constants.JSON_MSG_ERROR)) {
                // invalid username or password
                showError(R.string.invalid_username_or_password);

                return;
            }

            // --response is valid, handle it--
            UserHandler handler = new UserHandler(response);
            User user = handler.handle();

            // check handling operation
            if (user == null) {
                showError(R.string.unexpected_error_try_again);

                return;
            }

            // --user object is valid >> save it & cache response--
            AppController.getInstance(activity.getApplicationContext()).setActiveUser(user);
            Cache.updateActiveUserResponse(activity.getApplicationContext(), response);

            // goto main activity
            Intent intent = new Intent(activity, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.sync_logs_enter, R.anim.main_exit1);
        }

        private void showProgress(boolean show) {
            ViewUtil.showView(progressBar, show);
            ViewUtil.showView(layoutLoginForm, !show, View.INVISIBLE);
        }

        private void showError(int errorMsgRes) {
            AppMsg.cancelAll();
            AppMsg.makeText(activity, errorMsgRes, AppMsg.STYLE_ALERT).show();
        }
    }
}
