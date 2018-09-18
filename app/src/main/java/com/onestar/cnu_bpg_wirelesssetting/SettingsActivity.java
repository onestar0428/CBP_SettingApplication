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

public class SettingsActivity extends AppCompatActivity implements DialogBuilder.dialogBuilderListener {

    private final static String TAG = SettingsActivity.class.getSimpleName();

    private static ActivitySettingsBinding binding;
    private static BluetoothLEService mBluetoothLEService;
    private static ValueManager mValueManager;
    private static DialogBuilder mDialogBuilder;

    public static final String QUERY = "QUERY:", START = "START:", RESUME = "RESUME", STOP = "STOP";

    private int tryConnect = 0;
    private static int TRY_LIMIT = 3;

    private String mDeviceName = "DEVICE_NAME";
    private String mDeviceAddress = "DEVICE_ADDRESS";

    private ConnectionStatus mGattConnected = ConnectionStatus.STATE_DISCONNECTED;
    private ConnectionStatus mServiceConnected = ConnectionStatus.STATE_DISCONNECTED;

    private ProgressDialog mDialog;

    //TODO: Use StringBuilder instead of String
    //TODO: add a refresh button which sends QUERY command
    //TODO: HANDLE DISCONNECTION WHEN REBOOT..

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();

            if (mDialog.isShowing())
                mDialog.hide();

            if (action.equals(BluetoothLEService.ACTION_DATA_AVAILABLE)) {
                mGattConnected = ConnectionStatus.STATE_CONNECTED;
                Log.d(TAG, "onReceive: Data Available");

                if (extras.containsKey("value")) {
                    String key = "", value = "";
                    String response = extras.getString("value");

                    Log.d(TAG, "OnReceive: " + response);

                    if (response.startsWith(" * ")) {
                        String[] parseAsterisk = response.replace(" * ", "").split(":");

                        if (parseAsterisk.length > 1){
                            key = parseAsterisk[0];
                            if (parseAsterisk[0].contains(" ")) {
                                key = parseAsterisk[0].split(" ")[0];
                            }

                            value = value.replaceAll("\n","");
                            if (parseAsterisk[1].contains(" ")){
                                value = parseAsterisk[1].replace(" ", "").replaceAll("\n","");
                            }

                            mValueManager.update(key, value);
                        }
                    }
                }
            } else if (action.equals(BluetoothLEService.ACTION_GATT_SERVICES_DISCOVERED)) {
                Log.d(TAG, "onReceive: Gatt Services Discovered");
                tryConnect = 0;

                if (mBluetoothLEService != null && mValueManager == null) {
                    Log.d(TAG, "onBLEServiceConnected");

                    mValueManager = new ValueManager();
                    binding.setValue(mValueManager);

                    // request for initialize the setting values for UI
                    sendCommand(QUERY);
                }
            } else if (action.equals(BluetoothLEService.ACTION_GATT_DISCONNECTED)) {
                Log.d(TAG, "onReceive: Gatt is Disconnected");

                mGattConnected = ConnectionStatus.STATE_DISCONNECTED;
                if (mBluetoothLEService != null) {
                    mBluetoothLEService.disconnect();
                }

                Toast.makeText(SettingsActivity.this, "BLE Connection is Unstable.", Toast.LENGTH_SHORT).show();
                finish();
//                if (tryConnect >= TRY_LIMIT) {
//                }
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
                final boolean result = mBluetoothLEService.connect(mDeviceAddress, SettingsActivity.this);
                mServiceConnected = ConnectionStatus.STATE_CONNECTED;

                Log.d(TAG, "ServiceConnection=" + result);
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

        intentFilter.addAction(BluetoothLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLEService.ACTION_DATA_AVAILABLE);

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
    }

    public void onControlButtonClick(View view) {
        String key = ((Button) view).getText().toString();
        String command = "";

        if (key.equals(getResources().getString(R.string.start))) {
            ((Button) view).setText(getResources().getString(R.string.resume));
            command = START;
        } else if (key.equals(getResources().getString(R.string.resume))) {
            ((Button) view).setText(getResources().getString(R.string.start));
            command = RESUME;
        } else if (key.equals(getResources().getString(R.string.stop))) {
            command = STOP;
        }

        sendCommand(command);
    }

    public void onButtonClick(View view) {
        if (view.getId() == R.id.changeLayoutButton) {
            if (binding.buttonLayout.getVisibility() == View.VISIBLE) {
                binding.buttonLayout.setVisibility(View.INVISIBLE);
                binding.controlLayout.setVisibility(View.VISIBLE);
                binding.changeLayoutButton.setText("←");
            } else {
                binding.buttonLayout.setVisibility(View.VISIBLE);
                binding.controlLayout.setVisibility(View.INVISIBLE);
                binding.changeLayoutButton.setText("→");
            }
        } else {
            String key = ((Button) view).getText().toString();

            if (mDialogBuilder == null) {
                mDialogBuilder = new DialogBuilder(this, this, binding);
            }

            mDialogBuilder.makeDialog(key);
        }
    }

    @Override
    public void onDialogValueChanged(String key, String params) {
        Log.d(TAG, "DialogValueChanged");
        Toast.makeText(SettingsActivity.this, "DialogValueChanged", Toast.LENGTH_SHORT).show();

        if (!key.equals("") && !params.equals("")) {
            sendCommand(key + ":" + params + ":");
        }
    }

    private boolean sendCommand(String command) {
        boolean result = false;
        Log.d(TAG, "sendCommand: " + command);

        if (!command.equals("")) {
            result = mBluetoothLEService.sendCommand(command);

            if (result && !command.startsWith(QUERY)) {
                result = mBluetoothLEService.sendCommand(QUERY);
            }
        }
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGattConnected == ConnectionStatus.STATE_DISCONNECTED) {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());//TODO: solve leak
        }

        if (mBluetoothLEService != null) {
            //TODO: Reconnect
            while (++tryConnect < TRY_LIMIT && mGattConnected == ConnectionStatus.STATE_DISCONNECTED) {
                final boolean result = mBluetoothLEService.connect(mDeviceAddress, this);
                Log.d(TAG, "Connect request result=" + result);

                Toast.makeText(SettingsActivity.this, "Reconnecting Service ... (" + tryConnect + "/3)", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();

        if (mBluetoothLEService != null) {
            Log.d(TAG, "onStop");
            mBluetoothLEService.disconnect();
            mBluetoothLEService = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.hide();
            mDialog.dismiss();
        }

        if (mDialogBuilder == null ||
                (mDialogBuilder != null && !mDialogBuilder.isShowing())) {
            Log.d(TAG, "onPause");
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