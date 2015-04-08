package beacons;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mahmoudelshamy.signed.AppController;
import com.mahmoudelshamy.signed.R;

import java.util.Calendar;
import java.util.Locale;

import datamodels.Cache;
import datamodels.Constants;
import datamodels.SignLog;
import datamodels.User;
import utils.DateTimeUtil;
import utils.NotificationUtil;

public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // get active user
        User activeUser = AppController.getInstance(context).getActiveUser();

        // check if there is an active user
        if (activeUser != null) {
            // get cached flags
            boolean startedNormally = Cache.isStartedNormally(context);
            boolean stoppedNormally = Cache.isStoppedNormally(context);

            // check if started normally and not stopped normally
            if (startedNormally && !stoppedNormally) {
                // check stop service time
                long stopServiceTime = Cache.getStopServiceTime(context);
                if (System.currentTimeMillis() > stopServiceTime + Constants.SIGN_OUT_AFTER_STOP_SERVICE_DELAY) {
                    // --sign out user--
                    // update cached flags
                    Cache.setLoggedIn(context, false);
                    Cache.setWaitingLogin(context, false);

                    // get date and time from cached stop service time
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(stopServiceTime);
                    String day = calendar.get(Calendar.YEAR) + "-"
                            + (calendar.get(Calendar.MONTH) + 1) + "-"
                            + calendar.get(Calendar.DAY_OF_MONTH);

                    String time = calendar.get(Calendar.HOUR_OF_DAY) + ":"
                            + calendar.get(Calendar.MINUTE) + ":"
                            + calendar.get(Calendar.SECOND);

                    // show notification
                    String msg = context.getString(R.string.you_signed_out_at) + " " + DateTimeUtil.getTimeInUserFormat(calendar);
                    NotificationUtil.show(context, Constants.NOTI_STATE_CHANGED, msg);

                    // send sign out request to server
                    SignLog signLog = new SignLog(activeUser.getName(), activeUser.getPassword(),
                            activeUser.getId(), day, time, SignLog.TYPE_SIGN_OUT);
                    new SignTask(context, signLog).execute();

                    // update cached flags
                    Cache.setStoppedNormally(context, true);
                } else {
                    // --continue monitoring user--
                    Intent serviceIntent = new Intent(context, BeaconsService.class);
                    context.startService(serviceIntent);
                }
            }

            // start remember sync logs receiver at static time every day
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.set(Calendar.HOUR_OF_DAY, Constants.REMEMBER_SYNC_LOGS_DAILY_HOUR);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Intent mIntent = new Intent(context, RememberSyncLogsReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Constants.RECEIVER_REMEMBER_SYNC_LOGS, mIntent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}
