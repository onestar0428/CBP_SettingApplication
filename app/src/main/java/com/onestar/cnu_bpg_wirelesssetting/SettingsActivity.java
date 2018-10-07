package com.onestar.cnu_bpg_wirelesssetting;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.onestar.cnu_bpg_wirelesssetting.Enum.Command;
import com.onestar.cnu_bpg_wirelesssetting.Enum.ConnectionStatus;
import com.onestar.cnu_bpg_wirelesssetting.databinding.ActivitySettingsBinding;

import javax.sql.CommonDataSource;

public class SettingsActivity extends AppCompatActivity implements DialogBuilder.dialogBuilderListener {

    private final static String TAG = SettingsActivity.class.getSimpleName();

    private static ActivitySettingsBinding binding;
    private static BluetoothLEService mBluetoothLEService;
    private static ValueManager mValueManager;
    private static DialogBuilder mDialogBuilder;

    private static String commandKey = Command.QUERY.value; // TODO: Use StringBuilder instead

    private String mDeviceName = "DEVICE_NAME";
    private String mDeviceAddress = "DEVICE_ADDRESS";

    private ConnectionStatus mGattConnected = ConnectionStatus.STATE_DISCONNECTED;
    private ConnectionStatus mServiceConnected = ConnectionStatus.STATE_DISCONNECTED;

    private ProgressDialog mDialog;


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();

            if (mDialog.isShowing()) {
                mDialog.hide();
            }

            if (action.equals(BluetoothLEService.ACTION_DATA_AVAILABLE)) {
                // Get notify (response) of command
                if (extras.containsKey(BluetoothLEService.NOTIFY_KEY)) {
                    String response = extras.getString(BluetoothLEService.NOTIFY_KEY);
                    mValueManager.update(commandKey, response);

                    if (response.contains("reboot") || response.contains("Rebooted")
                            || response.contains("Rebooting")) {
                        mBluetoothLEService.reboot();
                        commandKey = Command.QUERY.value;
                    }

                    // Toast.makeText(SettingsActivity.this, commandKey + " is updated with " + response, Toast.LENGTH_SHORT).show();
                }
            } else if (action.equals(BluetoothLEService.ACTION_GATT_SERVICES_DISCOVERED)) {
                Log.d(TAG, "onReceive: Gatt Services Discovered");

                // Set MTU size (maximum ~512 byte)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    BluetoothLEService.exchangeGattMtu();
                }
            } else if (action.equals(BluetoothLEService.ACTION_GATT_DISCONNECTED)) {
                // Disconnect all the things and exit activity
                Log.d(TAG, "onReceive: Gatt is Disconnected");

                mGattConnected = ConnectionStatus.STATE_DISCONNECTED;

                if (mBluetoothLEService != null) {
                    mBluetoothLEService.disconnect();
                    Toast.makeText(SettingsActivity.this, "Disconnecting BLE Service", Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(SettingsActivity.this, "BLE Service is disconnected", Toast.LENGTH_SHORT).show();
                finish();
            } else if (action.equals(BluetoothLEService.ACTION_GATT_CONNECTED)) {
                mGattConnected = ConnectionStatus.STATE_CONNECTED;

                if (mBluetoothLEService != null) {
                    if (mValueManager == null) {
                        Log.d(TAG, "onBLEServiceConnected(new)");

                        // Bind ValueManager to be observable
                        mValueManager = new ValueManager();
                        binding.setValue(mValueManager);

                        Toast.makeText(SettingsActivity.this, "BLE Service is connected", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "onBLEServiceConnected(re)");
                        commandKey = Command.QUERY.value;

                        Toast.makeText(SettingsActivity.this, "BLE Service is re-connected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLEService = ((BluetoothLEService.LocalBinder) service).getService();

            if (!mBluetoothLEService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");

                // Fail to connect Service, exit the activity
                finish();
            } else {
                mDialog = ProgressDialog.show(SettingsActivity.this, mDeviceName, "Connecting ...", true, true);

                mServiceConnected = ConnectionStatus.STATE_CONNECTED;
                Log.d(TAG, "ServiceConnection=" + mServiceConnected);

                mBluetoothLEService.connect(mDeviceAddress, SettingsActivity.this);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // BLE Service is disconnected, deallocate objects and change state

            mValueManager = null;
            mDialogBuilder = null;
            mServiceConnected = ConnectionStatus.STATE_DISCONNECTED;
        }
    };

    /**
     * BluetoothLEService action filter for BroadcastReceiver.
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(BluetoothLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLEService.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get connected device information
        mDeviceName = getIntent().getExtras().getString("name");
        mDeviceAddress = getIntent().getExtras().getString("address");

        // Binding UI with observable ValueManager object
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        binding.setActivity(this);
        binding.setValue(mValueManager);

        // Bind Service to connect BLE Service
        bindService(new Intent(this, BluetoothLEService.class), mServiceConnection, BIND_AUTO_CREATE);

        try {
            Log.d(TAG, "Try to register Broadcast Receiver");
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Fail to register Broadcast Receiver");
        }
    }

    /**
     * onClick event callback method
     * Send START/STOP command
     *
     * @param view Id of each control button
     */
    public void onControlButtonClick(View view) {
        String key = ((Button) view).getText().toString();
        String command = "";

        if (key.equals(Command.START.value)) {
            ((Button) view).setText(Command.RESUME.value);
            command = Command.START.command;
        } else if (key.equals(Command.RESUME.value)) {
            ((Button) view).setText(Command.START.value);
            command = Command.RESUME.command;
        } else if (key.equals(Command.STOP.value)) {
            command = Command.STOP.command;
        }

        sendCommand(command);
    }

    /**
     * onClick event callback method.
     * Change view layout into ControlLayout <-> SettingLayout.
     *
     * @param view Id of each button changes view layout
     */
    public void onLayoutButtonClick(View view) {
        if (view.getId() == R.id.changeToControlButton) {
            binding.buttonLayout.setVisibility(View.INVISIBLE);
            binding.controlLayout.setVisibility(View.VISIBLE);
        } else if (view.getId() == R.id.changeToSettingButton) {
            binding.buttonLayout.setVisibility(View.VISIBLE);
            binding.buttonLayout.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * onClick event callback method.
     * Build a dialog for user input according to option value.
     *
     * @param view Id of each setting button
     */
    public void onSettingButtonClick(View view) {

        String key = ((Button) view).getText().toString();

        if (mDialogBuilder == null) {
            mDialogBuilder = new DialogBuilder(this, this, binding);
        }

        mDialogBuilder.makeDialog(key);
    }

    /**
     * Interface.
     * Called when user pressed POSITIVE button in dialog.
     *
     * @param key    Header for command
     * @param params Values for command which user inputs
     */
    @Override
    public void onDialogValueChanged(String key, String params) {
        Log.d(TAG, "DialogValueChanged");

        if (!key.equals("") && !params.equals("")) {
            sendCommand(key + ":" + params + ":");
        }
    }

    /**
     * Interface.
     * Called when user pressed POSITIVE("Yes") button in dialog.
     *
     * @param command Header for command
     * @return Returns boolean according to result of sending command
     */
    private boolean sendCommand(String command) {
        boolean result = false;
        Log.d(TAG, "sendCommand: " + command);

        if (!command.equals("")) {
            result = mBluetoothLEService.sendCommand(command);

            Toast.makeText(SettingsActivity.this, "Success to send command", Toast.LENGTH_SHORT).show();

            if (result) {
                commandKey = command.split(":")[0];
            }

            return result;
        }

        Toast.makeText(SettingsActivity.this, "Fail to send command", Toast.LENGTH_SHORT).show();

        return result;
    }

    /**
     * Deallocate static objects, Service, BroadcastReceiver before exit activity.
     */
    private void activity_finish() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.hide();
            mDialog.dismiss();
        }

        if (mDialogBuilder == null ||
                (mDialogBuilder != null && !mDialogBuilder.isShowing())) {
            Log.d(TAG, "onPause");

            try {
                if (mGattUpdateReceiver != null && mGattConnected == ConnectionStatus.STATE_CONNECTED) {
                    Log.d(TAG, "Unregister BroadcastReceiver");
                    unregisterReceiver(mGattUpdateReceiver);
                }
            } catch (IllegalArgumentException e) {
                Log.d(TAG, "There is no BroadcastReceiver to unregister.");
            }
            try {
                if (mServiceConnected == ConnectionStatus.STATE_CONNECTED) {
                    Log.d(TAG, "Unbind Service Connection");
                    unbindService(mServiceConnection);
                }
            } catch (IllegalArgumentException e) {
                Log.d(TAG, "There is no Service to unbind.");
            }

        }
    }

    @Override
    protected void onPause() {
        activity_finish();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}