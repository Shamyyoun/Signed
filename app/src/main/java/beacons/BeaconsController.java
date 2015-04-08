package beacons;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.RemoteException;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.BeaconManager.ServiceReadyCallback;
import com.estimote.sdk.Region;
import com.mahmoudelshamy.signed.AppController;
import com.mahmoudelshamy.signed.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import datamodels.Cache;
import datamodels.Constants;
import datamodels.SignLog;
import datamodels.User;
import utils.DateTimeUtil;
import utils.NotificationUtil;

public class BeaconsController {
    // main objects
    private Context context;
    private BeaconsListener listener;
    private User user;
    private boolean shouldSignIn;

    // fields
    private long scanPeriod = 5 * 1000;
    private long waitTime = 20 * 60 * 1000;

    // beacon manager vars
    private BeaconManager beaconManager;
    private Region region;

    // used to sign user out after static time
    private long exitRangeTime;
    private Handler handlerSignOutAfterExitRange;
    private Handler handlerSignOutAfterStop;
    private Runnable runnableSignOut;

    // used to stop service after static time
    private Handler handlerStopService;
    private Runnable runnableStopService;

    // used to save stop time periodically
    private Handler handlerSaveStopTime;
    private Runnable runnableSaveStopTime;

    /*
     * default constructor
     */
    public BeaconsController(final Context context, final BeaconsListener listener) {
        this.context = context;
        this.listener = listener;
        user = AppController.getInstance(context).getActiveUser();
        shouldSignIn = !Cache.isLoggedIn(context); // if user is not logged in, signin him .. or if logged in continue monitoring

        // init regions
        region = new Region(context.getString(R.string.app_name), user.getBeaconId(), user.getBeaconMajor(), null);

        // init beacon manager
        beaconManager = new BeaconManager(context);
        beaconManager.setBackgroundScanPeriod(1000, 0);

        // init handlers & runnables
        handlerSignOutAfterExitRange = new Handler();
        handlerSignOutAfterStop = new Handler();
        runnableSignOut = new Runnable() {
            @Override
            public void run() {
                signOut();
            }
        };
        handlerStopService = new Handler();
        runnableStopService = new Runnable() {
            @Override
            public void run() {
                // stop service
                Intent intent = new Intent(context, BeaconsService.class);
                context.stopService(intent);

                // update cached values
                Cache.setWaitingLogin(context, false);

                // trigger listener
                listener.onStopLoginWaiting();

                // show notification
                NotificationUtil.show(context, Constants.NOTI_OUT_OF_RANGE, R.string.you_are_out_of_range);
            }
        };
        handlerSaveStopTime = new Handler();
        runnableSaveStopTime = new Runnable() {
            @Override
            public void run() {
                // Cache stop service time
                Cache.updateStopServiceTime(context, System.currentTimeMillis());

                // do same task every static time
                handlerSaveStopTime.postDelayed(this, Constants.SAVE_LAST_RUNNING_SERVICE_TIME_PERIOD);
            }
        };

        // add monitoring listener
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> beacons) {
                if (shouldSignIn) {
                    signIn();

                    // cancel stop service thread before start
                    handlerStopService.removeCallbacks(runnableStopService);

                    // update beacon manager wait time
                    beaconManager.setBackgroundScanPeriod(scanPeriod, waitTime);

                    // update flags
                    shouldSignIn = false;
                } else if (System.currentTimeMillis() <= exitRangeTime + Constants.SIGN_OUT_AFTER_EXIT_RANGE_DELAY) { // check last exit range event time
                    // user exited range and then entered range within allowed delay
                    // so, cancel sign out thread before start
                    handlerSignOutAfterExitRange.removeCallbacks(runnableSignOut);
                }

                // user entered range, so stop sign out after stop task before starting
                handlerSignOutAfterStop.removeCallbacks(runnableSignOut);
            }

            @Override
            public void onExitedRegion(Region region) {
                // sign user out after SIGN_OUT_DELAY
                handlerSignOutAfterExitRange.postDelayed(runnableSignOut, Constants.SIGN_OUT_AFTER_EXIT_RANGE_DELAY);

                // save exit range time
                exitRangeTime = System.currentTimeMillis();
            }
        });
    }

    /*
     * method, used to start service
     */
    public void startService() {
        // connect to beacon manager
        beaconManager.connect(new ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    // start monitoring regions
                    beaconManager.startMonitoring(region);

                    // check if should sign in user
                    if (shouldSignIn) {
                        // stop service after STOP_DELAY if not entered region
                        handlerStopService.postDelayed(runnableStopService, Constants.STOP_SERVICE_IF_NOT_ENTERED_RANGE_DELAY);
                    } else {
                        // service stopped and then started to continue monitoring
                        // so, sign out user after STOP_DELAY if not entered region
                        handlerSignOutAfterStop.postDelayed(runnableSignOut, Constants.STOP_SERVICE_IF_NOT_ENTERED_RANGE_DELAY);
                    }

                    // start periodic task to save stop service time
                    handlerSaveStopTime.post(runnableSaveStopTime);

                    // update cached started&stopped_normally flag
                    Cache.setStartedNormally(context, true);
                    Cache.setStoppedNormally(context, false);
                } catch (RemoteException e) {
                    Toast.makeText(context, R.string.error_starting_service, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * method, used to stop service
     */
    public void stopService() {
        // disconnect service
        try {
            // stop monitoring regions
            beaconManager.stopMonitoring(region);

            // disconnect beacon manager
            beaconManager.disconnect();

            // stop save stop time task
            handlerSaveStopTime.removeCallbacks(runnableSaveStopTime);

            // update cached started&stopped_normally flag
            Cache.setStartedNormally(context, false);
            Cache.setStoppedNormally(context, true);
        } catch (RemoteException e) {
            e.printStackTrace();
            listener.onError(context.getString(R.string.error_stopping_service));
        }
    }

    /**
     * method, used to sign user in when enter region
     */
    private void signIn() {
        // trigger listener
        listener.onSignedIn();

        // update user logged in flag
        Cache.setLoggedIn(context, true);

        // update waiting login flag in sp
        Cache.setWaitingLogin(context, false);

        // get date and time
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        String day = calendar.get(Calendar.YEAR) + "-"
                + (calendar.get(Calendar.MONTH) + 1) + "-"
                + calendar.get(Calendar.DAY_OF_MONTH);

        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":"
                + calendar.get(Calendar.MINUTE) + ":"
                + calendar.get(Calendar.SECOND);

        // show notification
        String msg = context.getString(R.string.you_signed_in_at) + " " + DateTimeUtil.getTimeInUserFormat(calendar);
        NotificationUtil.show(context, Constants.NOTI_STATE_CHANGED, msg);

        // send sign in request to server
        SignLog signLog = new SignLog(user.getName(), user.getPassword(), user.getId(), day, time, SignLog.TYPE_SIGN_IN);
        new SignTask(context, signLog).execute();
    }

    /**
     * method, used to sign user out when exit region
     */
    public void signOut() {
        // trigger listener
        listener.onSignedOut();

        // update user logged in flag
        Cache.setLoggedIn(context, false);

        // get date and time
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
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
        SignLog signLog = new SignLog(user.getName(), user.getPassword(), user.getId(), day, time, SignLog.TYPE_SIGN_OUT);
        new SignTask(context, signLog).execute();
    }
}
