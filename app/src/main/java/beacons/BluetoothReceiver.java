package beacons;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.mahmoudelshamy.signed.R;

import datamodels.Cache;
import datamodels.Constants;
import utils.BluetoothUtil;
import utils.NotificationUtil;

/**
 * Created by Shamyyoun on 3/29/2015.
 */
public class BluetoothReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        // check if user is signed in
        if (Cache.isLoggedIn(context)) {
            // check bluetooth state
            if (!BluetoothUtil.isEnabled()) {
                // -- user just disabled bluetooth --
                // show notification
                NotificationUtil.show(context, Constants.NOTI_ENABLE_BLUETOOTH,
                        R.string.you_should_enable_bluetooth, Settings.ACTION_BLUETOOTH_SETTINGS);

                // stop beacons service after SIGN_OUT_DELAY
                Intent mIntent = new Intent(context, SignOutReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Constants.RECEIVER_SIGN_OUT, mIntent, 0);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + Constants.STOP_SERVICE_AFTER_DISABLE_BLUETOOTH_DELAY, pendingIntent);

                // save disable time
                Cache.updateDisableBluetoothTime(context, System.currentTimeMillis());

            } else {
                // -- user just enabled bluetooth --
                // cancel notification
                NotificationUtil.cancel(context, Constants.NOTI_ENABLE_BLUETOOTH);

                // check last disable time
                long disableBluetoothTime = Cache.getDisableBluetoothTime(context);
                if (System.currentTimeMillis() <= disableBluetoothTime + Constants.STOP_SERVICE_AFTER_DISABLE_BLUETOOTH_DELAY) {
                    // user disabled bluetooth and enabled it within allowed delay
                    // so, cancel stop service alarm before start
                    Intent mIntent = new Intent(context, SignOutReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Constants.RECEIVER_SIGN_OUT, mIntent, 0);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                }
            }
        }
    }

    /**
     * sub class, extends BroadcastReceiver, used to stop beacons service when receives the action
     */
    public static class SignOutReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // stop service
            Intent mIntent = new Intent(context, BeaconsService.class);
            mIntent.putExtra(Constants.KEY_MESSAGE, Constants.MESSAGE_SIGN_OUT);
            context.startService(mIntent);
        }
    }
}
