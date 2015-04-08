package datamodels;

/**
 * Created by Shamyyoun on 2/8/2015.
 */
public class Constants {
    // json response constants
    public static final String JSON_MSG_ERROR = "error";
    public static final String JSON_MSG_SUCCESS = "success";

    // sp constants
    public static final String SP_RESPONSES = "responses";
    public static final String SP_KEY_ACTIVE_USER_RESPONSE = "active_user_response";
    public static final String SP_CONFIG = "config";
    public static final String SP_KEY_LOGGED_IN = "logged_in";
    public static final String SP_KEY_WAITING_LOGIN = "waiting_login";
    public static final String SP_KEY_STOPPED_NORMALLY = "stopped_normally";
    public static final String SP_KEY_STARTED_NORMALLY = "started_normally";
    public static final String SP_KEY_DISABLE_BLUETOOTH_TIME = "disable_bluetooth_time";
    public static final String SP_KEY_STOP_SERVICE_TIME = "update_service_time";

    // keys constants
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_OVERRIDE_PENDING_TRANSITION = "override_pending_transition";

    // messages constants
    public static final int MESSAGE_SIGNED_IN = 1;
    public static final int MESSAGE_SIGNED_OUT = 2;
    public static final int MESSAGE_STOP_LOGIN_WAITING = 3;
    public static final int MESSAGE_SIGN_OUT = 4;

    // notification constans
    public static final int NOTI_STATE_CHANGED = 1;
    public static final int NOTI_ENABLE_BLUETOOTH = 2;
    public static final int NOTI_OUT_OF_RANGE = 3;
    public static final int NOTI_MONITORING_RUNNING = 4;
    public static final int NOTI_REMEMBER_SYNC_LOGS = 5;

    // requests constants
    public static final int REQUEST_CODE_ENABLE_BLUETOOTH = 1;

    // broadcast receiver IDs
    public static final int RECEIVER_SIGN_OUT = 1;
    public static final int RECEIVER_REMEMBER_SYNC_LOGS = 2;

    // App logic constants
    public static final long SIGN_OUT_AFTER_EXIT_RANGE_DELAY = 30 * 60 * 1000;
    public static final long STOP_SERVICE_IF_NOT_ENTERED_RANGE_DELAY = 1 * 60 * 1000;
    public static final long SAVE_LAST_RUNNING_SERVICE_TIME_PERIOD = 15 * 60 * 1000;
    public static final long SIGN_OUT_AFTER_STOP_SERVICE_DELAY = 30 * 60 * 1000;
    public static final long STOP_SERVICE_AFTER_DISABLE_BLUETOOTH_DELAY = 30 * 60 * 1000;
    public static final int REMEMBER_SYNC_LOGS_DAILY_HOUR = 19;
}