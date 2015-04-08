package beacons;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mahmoudelshamy.signed.AppController;
import com.mahmoudelshamy.signed.R;
import com.mahmoudelshamy.signed.SyncLogsActivity;

import java.util.Calendar;
import java.util.Locale;

import database.SignLogDAO;
import datamodels.Constants;
import utils.NotificationUtil;

/**
 * Created by Shamyyoun on 4/3/2015.
 */
public class RememberSyncLogsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // check if user has logs in DB
        SignLogDAO logDAO = new SignLogDAO(context);
        logDAO.open();
        boolean hasLogs = logDAO.hasLogs(AppController.getInstance(context).getActiveUser().getId());
        logDAO.close();

        if (hasLogs) {
            // show notification
            NotificationUtil.show(context, Constants.NOTI_REMEMBER_SYNC_LOGS, R.string.remember_to_sync_your_logs, SyncLogsActivity.class);
        }

        // repeat the alarm in the next day
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, Constants.REMEMBER_SYNC_LOGS_DAILY_HOUR);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Intent mIntent = new Intent(context, RememberSyncLogsReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Constants.RECEIVER_REMEMBER_SYNC_LOGS, mIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
