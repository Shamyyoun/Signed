package utils;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by Shamyyoun on 3/29/2015.
 */
public class BluetoothUtil {
    /**
     * method, used to check if device has bluetooth or not
     */
    public static boolean hasBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)
            return false;
        else
            return true;
    }

    /**
     * method, used to check bluetooth state
     */
    public static boolean isEnabled() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * method, used to disable bluetooth
     */
    public static void disable() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }
}
