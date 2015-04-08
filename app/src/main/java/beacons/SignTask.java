package beacons;

import android.content.Context;
import android.os.AsyncTask;

import com.mahmoudelshamy.signed.AppController;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import database.SignLogDAO;
import datamodels.Constants;
import datamodels.SignLog;
import json.JsonReader;

/**
 * Created by Shamyyoun on 4/2/2015.
 */
public class SignTask extends AsyncTask<Void, Void, Void> {
    private List<SignLog> signLogs;
    private SignLogDAO dao;

    public SignTask(Context context, SignLog log) {
        this.signLogs = new ArrayList<SignLog>(1);
        signLogs.add(log);
        dao = new SignLogDAO(context);
    }

    public SignTask(Context context, List<SignLog> signLogs) {
        this.signLogs = signLogs;
        dao = new SignLogDAO(context);
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (SignLog log : signLogs) {
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
            dao.open();
            if (Constants.JSON_MSG_SUCCESS.equals(response)) {
                // successful request >> delete it from db
                dao.delete(log.getId());
            } else {
                // request failed
                // check log id
                if (log.getId() == -1) {
                    // not exist in DB >> add it to DB
                    dao.add(log);
                }
            }
            dao.close();
        }

        return null;
    }
}
