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
import android.widget.TextView;
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

    //TODO: try to use DATABINDING instead of Butterknife
    //TODO: add a refresh button which sends QUERY command

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (mDialog.isShowing())
                mDialog.hide();

            if (action.equals(BluetoothLEService.GattStatus.ACTION_DATA_AVAILABLE.getStatus())
                    || action.equals(BluetoothLEService.GattStatus.ACTION_GATT_SERVICES_DISCOVERED.getStatus())) {
                mGattConnected = ConnectionStatus.STATE_CONNECTED;
            } else if (action.equals(BluetoothLEService.GattStatus.ACTION_GATT_DISCONNECTED.getStatus())) {
                mGattConnected = ConnectionStatus.STATE_DISCONNECTED;
                mBluetoothLEService.disconnect();

                Toast.makeText(SettingsActivity.this, "BLE Connection is Unstable.", Toast.LENGTH_SHORT).show();
                finish();
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        binding.setActivity(this);

        mDeviceName = getIntent().getExtras().getString("name");
        mDeviceAddress = getIntent().getExtras().getString("address");

        bindService(new Intent(this, BluetoothLEService.class), mServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    public void onButtonClick(View view) {
        String key = ((Button) view).getText().toString();

        if (mDialogBuilder == null) {
            mDialogBuilder = new DialogBuilder(this, this);
        }

        mDialogBuilder.makeDialog(key);
    }

    @Override
    public void onDialogValueChanged(String key, String params) {
//        mDialog.show(SettingsActivity.this, mDeviceName, "Setting Values ...", true, true);
        mValueManager.setValues(key, params);
    }

    @Override
    public void onBLEResponseReceived(String response) {
        mValueManager.update(response);
        //Toast result
    }

    @Override
    public void onBLEServiceConnected() {
        if (mBluetoothLEService != null && mValueManager == null) {
            mValueManager = new ValueManager(SettingsActivity.this, mBluetoothLEService, SettingsActivity.this);
            binding.setValue(mValueManager);
            mValueManager.initialize(); //return boolean
        }
    }

    @Override
    public void onValueUpdated(String key, String newValue) {
        if (!key.equals("")) {
//            switch (key) {
//                case "Pulse":
//                    binding.frequencyTextView.setText(newValue);
//                    break;
//                case "RED":
//                    binding.redTextView.setText(newValue);
//                    break;
//                case "GRN":
//                    binding.greenTextView.setText(newValue);
//                    break;
//                case "BLU":
//                    binding.blueTextView.setText(newValue);
//                    break;
//                case "YEL":
//                    binding.yellowTextView.setText(newValue);
//                    break;
//                case "IR":
//                    binding.irTextView.setText(newValue);
//                    break;
//                case "Pressure_1st":
//                    binding.pressure1TextView.setText(newValue);
//                    break;
//                case "Pressure_2nd":
//                    binding.pressure2TextView.setText(newValue);
//                    break;
//                case "Optical_RGB":
//                    binding.rgbTextView.setText(newValue);
//                    break;
//                case "Optical_IrY":
//                    binding.iryTextView.setText(newValue);
//                    break;
//                case "Acc/Gyro":
//                    binding.accgyroTextView.setText(newValue);
//                    break;
//                case "Include":
//                    binding.timestampTextView.setText(newValue);
//                    break;
//                case "Report":
//                    binding.reportTextView.setText(newValue);
//                    break;
//                case "Current":
//                    binding.setTimeTextView.setText(newValue);
//                    break;
//                case "UDP/TCP":
//                    binding.protocolTextView.setText(newValue);
//                    break;
//                case "Port":
//                    binding.portTextView.setText(newValue);
//                    break;
////                TODO: case for wifi (ssid, pw)
//            }

            if(mDialog.isShowing())
                mDialog.hide();

            Toast.makeText(SettingsActivity.this, "Set " + key + " to new value: " + newValue, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGattConnected == ConnectionStatus.STATE_DISCONNECTED) {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        }
        if (mServiceConnected == ConnectionStatus.STATE_DISCONNECTED) {
            bindService(new Intent(this, BluetoothLEService.class), mServiceConnection, BIND_AUTO_CREATE);
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

        //TODO: Handle service disconnection

        if (mGattUpdateReceiver != null && mGattConnected == ConnectionStatus.STATE_CONNECTED) {
            unregisterReceiver(mGattUpdateReceiver);
        }

        if (mServiceConnected == ConnectionStatus.STATE_CONNECTED) {
            unbindService(mServiceConnection);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();

        //TODO: Handle disconnection

        if (mBluetoothLEService != null) {
            mBluetoothLEService.disconnect();
            mBluetoothLEService = null;
        }
    }
}

interface SettingsActivityListener {
    void onValueUpdated(String key, String newValue);
}