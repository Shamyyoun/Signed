package com.mahmoudelshamy.signed;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Locale;

import beacons.BeaconsService;
import beacons.RememberSyncLogsReceiver;
import beacons.SignTask;
import datamodels.Cache;
import datamodels.Constants;
import datamodels.SignLog;
import datamodels.User;
import json.UserHandler;
import utils.DateTimeUtil;
import utils.NotificationUtil;

/**
 * Created by Shamyyoun on 3/15/2015.
 */
public class AppController extends Application {
    public static final String END_POINT = "http://signed.mahmoudelshamy.com/services";
    private User activeUser;

    public AppController() {
        super();
    }

    /**
     * overriden method
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // get active user
        activeUser = getActiveUser();

        // check if there is an active user
        if (activeUser != null) {
            // check if service is not running
            if (!isBeaconsServiceRunning()) {
                // get cached flags
                boolean startedNormally = Cache.isStartedNormally(getApplicationContext());
                boolean stoppedNormally = Cache.isStoppedNormally(getApplicationContext());

                // check if started normally and not stopped normally
                if (startedNormally && !stoppedNormally) {
                    // check stop service time
                    long stopServiceTime = Cache.getStopServiceTime(getApplicationContext());
                    if (System.currentTimeMillis() > stopServiceTime + Constants.SIGN_OUT_AFTER_STOP_SERVICE_DELAY) {
                        // --sign out user--
                        // update cached flags
                        Cache.setLoggedIn(getApplicationContext(), false);
                        Cache.setWaitingLogin(getApplicationContext(), false);

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
                        String msg = getString(R.string.you_signed_out_at) + " " + DateTimeUtil.getTimeInUserFormat(calendar);
                        NotificationUtil.show(this, Constants.NOTI_STATE_CHANGED, msg);

                        // send sign out request to server
                        SignLog signLog = new SignLog(activeUser.getName(), activeUser.getPassword(),
                                activeUser.getId(), day, time, SignLog.TYPE_SIGN_OUT);
                        new SignTask(getApplicationContext(), signLog).execute();

                        // update cached flags
                        Cache.setStoppedNormally(getApplicationContext(), true);
                    } else {
                        // --continue monitoring user--
                        Intent serviceIntent = new Intent(getApplicationContext(), BeaconsService.class);
                        startService(serviceIntent);
                    }
                }
            }

            // start remember sync logs receiver at static time every day
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.set(Calendar.HOUR_OF_DAY, Constants.REMEMBER_SYNC_LOGS_DAILY_HOUR);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            if (calendar.getTimeInMillis() < System.currentTimeMillis())
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            Intent mIntent = new Intent(getApplicationContext(), RememberSyncLogsReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Constants.RECEIVER_REMEMBER_SYNC_LOGS, mIntent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    /**
     * method, used to get active user from runtime or from SP
     */
    public User getActiveUser() {
        if (activeUser == null) {
            // get cached user if exists
            String response = Cache.getActiveUserResponse(getApplicationContext());
            if (response != null) {
                UserHandler handler = new UserHandler(response);
                activeUser = handler.handle();
            }
        }

        return activeUser;
    }

    /**
     * method, used to set active user
     */
    public void setActiveUser(User user) {
        this.activeUser = user;
    }

    /**
     * method used to return current application instance
     */
    public static AppController getInstance(Context context) {
        return (AppController) context.getApplicationContext();
    }

    /*
     * used to check if beacons service is running
	 */
    private boolean isBeaconsServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().equals(BeaconsService.class.getName())) {
                return true;
            }

        }
        return false;
    }
}
