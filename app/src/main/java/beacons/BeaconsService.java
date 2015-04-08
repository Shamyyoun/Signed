package beacons;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.mahmoudelshamy.signed.MainActivity;
import com.mahmoudelshamy.signed.R;

import datamodels.Constants;
import utils.NotificationUtil;

public class BeaconsService extends Service implements BeaconsListener {
    private BeaconsController beaconsController;

    @Override
    public void onCreate() {
        super.onCreate();

        beaconsController = new BeaconsController(getApplicationContext(), this);

        // show on going notification to keep service alive as possible
        NotificationUtil.showOnGoing(getApplicationContext(), Constants.NOTI_MONITORING_RUNNING, R.string.monitoring_is_running, MainActivity.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // get passed message
        int message = intent.getIntExtra(Constants.KEY_MESSAGE, -1);

        switch (message) {
            case Constants.MESSAGE_SIGN_OUT:
                beaconsController.signOut();
                break;

            default:
                beaconsController.startService();
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        beaconsController.stopService();

        // cancel running notification
        NotificationUtil.cancel(getApplicationContext(), Constants.NOTI_MONITORING_RUNNING);

        super.onDestroy();
    }

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSignedIn() {
        sendMessage(Constants.MESSAGE_SIGNED_IN);
    }

    @Override
    public void onSignedOut() {
        sendMessage(Constants.MESSAGE_SIGNED_OUT);
        stopSelf();
    }

    @Override
    public void onStopLoginWaiting() {
        sendMessage(Constants.MESSAGE_STOP_LOGIN_WAITING);
    }

    /**
     * method, used to send message to activity
     */
    public void sendMessage(int message) {
        Intent intent = new Intent(getPackageName() + ".MONITORING_CHANGED");
        intent.putExtra(Constants.KEY_MESSAGE, message);
        sendBroadcast(intent);
    }
}
