package com.onestar.cnu_bpg_wirelesssetting;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.onestar.cnu_bpg_wirelesssetting.databinding.ActivitySettingsBinding;


public class SettingsActivity extends AppCompatActivity
        implements ValueManager.ValueManagerListener, DialogBuilder.dialogBuilderListener, SettingsActivityListener {
    private final static String TAG = SettingsActivity.class.getSimpleName();
    private ActivitySettingsBinding binding;

    private static BluetoothLEService mBluetoothLEService;
    private ValueManager mValueManager;
    private DialogBuilder mDialogBuilder;

    private String mDeviceName = "DEVICE_NAME";
    private String mDeviceAddress = "DEVICE_ADDRESS";

    private ConnectionStatus mGattConnected = ConnectionStatus.STATE_DISCONNECTED;
    private ConnectionStatus mServiceConnected = ConnectionStatus.STATE_DISCONNECTED;

    private ProgressDialog mDialog;
    private static int tryConnect = 0;

    //TODO: Use StringBuilder instead of String
    //TODO: add a refresh button which sends QUERY command
    //TODO: HANDLE DISCONNECTION WHEN REBOOT..

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (mDialog.isShowing())
                mDialog.hide();

            if (action.equals(BluetoothLEService.GattStatus.ACTION_DATA_AVAILABLE.getStatus())
                    || action.equals(BluetoothLEService.GattStatus.ACTION_GATT_SERVICES_DISCOVERED.getStatus())) {
                mGattConnected = ConnectionStatus.STATE_CONNECTED;
                tryConnect = 0;

                onBLEServiceConnected();
            } else if (action.equals(BluetoothLEService.GattStatus.ACTION_GATT_DISCONNECTED.getStatus())) {
                if (++tryConnect > 3) {
                    mGattConnected = ConnectionStatus.STATE_DISCONNECTED;
                    mBluetoothLEService.disconnect();

                    Toast.makeText(SettingsActivity.this, "BLE Connection is Unstable.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    //TODO:reconnect
//                    mDialog.setMessage("Connecting ... (" + tryConnect + " / 3)");
                    Toast.makeText(SettingsActivity.this, "Reconnecting Service ... (" + tryConnect + "/3)", Toast.LENGTH_SHORT).show();
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
                finish();
            }

            if (mBluetoothLEService != null) {
                mDialog = ProgressDialog.show(SettingsActivity.this, mDeviceName, "Connecting ...", true, true);
                final boolean result = mBluetoothLEService.connect(mDeviceAddress);

                mBluetoothLEService.setValueManagerListener(SettingsActivity.this);

                mServiceConnected = ConnectionStatus.STATE_CONNECTED;
                Log.d(TAG, "Connect request result=" + result);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLEService = null;
            mValueManager = null;
            mDialogBuilder = null;
            mServiceConnected = ConnectionStatus.STATE_DISCONNECTED;
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(BluetoothLEService.GattStatus.ACTION_GATT_CONNECTED.getStatus());
        intentFilter.addAction(BluetoothLEService.GattStatus.ACTION_GATT_DISCONNECTED.getStatus());
        intentFilter.addAction(BluetoothLEService.GattStatus.ACTION_GATT_SERVICES_DISCOVERED.getStatus());
        intentFilter.addAction(BluetoothLEService.GattStatus.ACTION_DATA_AVAILABLE.getStatus());

        return intentFilter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDeviceName = getIntent().getExtras().getString("name");
        mDeviceAddress = getIntent().getExtras().getString("address");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        binding.setActivity(this);
        binding.setValue(mValueManager);

        bindService(new Intent(this, BluetoothLEService.class), mServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    //TODO: Add onClick event of "->" button

    public void onButtonClick(View view) {
        String key = ((Button) view).getText().toString();

        if (mDialogBuilder == null) {
            mDialogBuilder = new DialogBuilder(this, this);
        }

        //TODO: send with params to set default values in dialog
        //TODO: add function returns params in a string in ValueManager
        mDialogBuilder.makeDialog(key);
    }

    @Override
    public void onBLEServiceConnected() {
        if (mBluetoothLEService != null && mValueManager == null) {

            mValueManager = new ValueManager(SettingsActivity.this);
            binding.setValue(mValueManager);

            // request for initialize the setting values for UI
            sendCommand(Value.Command.QUERY.makeCommand());
        }
    }

    @Override
    public void onBLEResponseReceived(String response) {
        String key = "", value = "";
//        Toast.makeText(SettingsActivity.this, "BLEResponseReceived", Toast.LENGTH_SHORT).show();

        if (response.startsWith(" * ")) {
            String parseAsterisk = response.replace(" * ", "");
            key = parseAsterisk.split(":")[0].split(" ")[0];
            value = parseAsterisk.split(":")[1].replace(" ", "");
        }

        mValueManager.update(key, value);
    }

    @Override
    public void onDialogValueChanged(String key, String params) {
//        mDialog.show(SettingsActivity.this, mDeviceName, "Setting Values ...", true, true);
        Toast.makeText(SettingsActivity.this, "DialogValueChanged", Toast.LENGTH_SHORT).show();

//        boolean result = sendCommand(mValueManager.getCommand(key, params));
        sendCommand(key + ":" + params + ":");
    }

    private boolean sendCommand(String command) {
        //TODO: Handle about QUERY more efficient

        if (!command.equals("")) {
            boolean result = mBluetoothLEService.sendCommand(command);

            if (result && !command.startsWith(Value.Command.QUERY.makeCommand())) {
                return mBluetoothLEService.sendCommand(Value.Command.QUERY.makeCommand());
            }
        }
        return false;
    }

    //TODO: delete function after verifying DATABINDING works well
    @Override
    public void onValueUpdated(String key, String newValue) {
        if (mDialog.isShowing())
            mDialog.hide();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGattConnected == ConnectionStatus.STATE_DISCONNECTED) {
//            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        }
        if (mServiceConnected == ConnectionStatus.STATE_DISCONNECTED) {
//            bindService(new Intent(this, BluetoothLEService.class), mServiceConnection, BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();

//        if (mGattUpdateReceiver != null && mGattConnected == ConnectionStatus.STATE_CONNECTED){
//            unregisterReceiver(mGattUpdateReceiver);
//        }
//
//        if(mServiceConnected == ConnectionStatus.STATE_CONNECTED){
//            unbindService(mServiceConnection);
//        }

        if (mBluetoothLEService != null) {
            mBluetoothLEService.disconnect();
            mBluetoothLEService = null;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mDialog.isShowing()) {
            mDialog.hide();
            mDialog.dismiss();
        }

        if (mDialogBuilder == null ||
                (mDialogBuilder != null && !mDialogBuilder.isShowing())) {
            if (mGattUpdateReceiver != null && mGattConnected == ConnectionStatus.STATE_CONNECTED) {
                unregisterReceiver(mGattUpdateReceiver);
            }

            if (mServiceConnected == ConnectionStatus.STATE_CONNECTED) {
                unbindService(mServiceConnection);
            }

            if (mBluetoothLEService != null) {
                mBluetoothLEService.disconnect();
                mBluetoothLEService = null;
            }
        }

    }

}

interface SettingsActivityListener {
    void onValueUpdated(String key, String newValue);
}