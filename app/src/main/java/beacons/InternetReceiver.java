package beacons;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mahmoudelshamy.signed.AppController;

import java.util.List;

import database.SignLogDAO;
import datamodels.SignLog;
import datamodels.User;
import utils.InternetUtil;

public class InternetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // check internet connection
        if (InternetUtil.isConnected(context)) {
            // get active user
            User activeUser = AppController.getInstance(context).getActiveUser();

            // check active user
            if (activeUser != null) {
                // get stored sign logs from database
                SignLogDAO dao = new SignLogDAO(context);
                dao.open();
                List<SignLog> signLogs = dao.getAll(activeUser.getId());
                dao.close();

                // send them to server
                new SignTask(context, signLogs).execute();
            }
        }
    }
}
