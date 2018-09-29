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

import com.onestar.cnu_bpg_wirelesssetting.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity implements DialogBuilder.dialogBuilderListener {

    private final static String TAG = SettingsActivity.class.getSimpleName();

    private static ActivitySettingsBinding binding;
    private static BluetoothLEService mBluetoothLEService;
    private static ValueManager mValueManager;
    private static DialogBuilder mDialogBuilder;

    public static final String QUERY = "QUERY:", START = "START:", RESUME = "RESUME", STOP = "STOP";

    private static boolean commandResponseFlag = false;
    private static String commandResponseKey = "", commandResponseValue = "";

    private String mDeviceName = "DEVICE_NAME";
    private String mDeviceAddress = "DEVICE_ADDRESS";

    private ConnectionStatus mGattConnected = ConnectionStatus.STATE_DISCONNECTED;
    private ConnectionStatus mServiceConnected = ConnectionStatus.STATE_DISCONNECTED;

    private ProgressDialog mDialog;

    //TODO: Use StringBuilder instead of String
    //TODO: add a refresh button which sends QUERY command
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();

            if (mDialog.isShowing()) {
                mDialog.hide();
            }

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                final int previousBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);

                if (state == BluetoothDevice.BOND_BONDING) {
                    if (!mDialog.isShowing()) {
                        mDialog.show();
                    }
                } else if (state == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "onReceive: bonded");
                } else if (state == BluetoothDevice.BOND_NONE) {
                    if (bondState == BluetoothDevice.BOND_NONE && previousBondState == BluetoothDevice.BOND_BONDED) {
                        Log.d(TAG, "onReceive: bonded->bond_none");
                    } else {
                        Log.d(TAG, "onReceive: bond_none");
                    }
                } else {
                    Log.d(TAG, "onReceive: error");
                }
            } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                // Logger.i("BluetoothAdapter.ACTION_STATE_CHANGED.");
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) ==
                        BluetoothAdapter.STATE_OFF) {
                    Log.d(TAG, "BluetoothAdapter.STATE_OFF");
                } else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) ==
                        BluetoothAdapter.STATE_ON) {
                    Log.d(TAG, "BluetoothAdapter.STATE_ON");
                }

            } else if (action.equals(BluetoothLEService.ACTION_DATA_AVAILABLE)) {
                mGattConnected = ConnectionStatus.STATE_CONNECTED;

                if (extras.containsKey(BluetoothLEService.NOTIFY_KEY)) {
                    String response = extras.getString(BluetoothLEService.NOTIFY_KEY);

                    // parsing response of QUERY
                    if (response.startsWith(" * ")) {
                        String[] parseAsterisk = response.replace(" * ", "").split(":");

                        if (parseAsterisk.length > 1) {
                            commandResponseKey = parseAsterisk[0];
                            if (parseAsterisk[0].contains(" ")) {
                                commandResponseKey = parseAsterisk[0].split(" ")[0];
                            }

                            commandResponseValue = commandResponseValue.replaceAll("\n", "");
                            if (parseAsterisk[1].contains(" ")) {
                                commandResponseValue = parseAsterisk[1].replace(" ", "").replaceAll("\n", "");
                            }

                            mValueManager.updateQuery(commandResponseKey, commandResponseValue);
                        }
                    }
                    // parsing response of other commands
                    if (response.startsWith(" !! ")) {
                        String[] parseAsterisk = response.split(" !! ");

                        if (parseAsterisk.length > 1) {
                            commandResponseKey = parseAsterisk[1].replace(" ", "");
                        }
                        if (!commandResponseKey.equals("No parameters are changed.")) {
                            commandResponseFlag = true;
                        }
                        if (!commandResponseKey.equals("The system will be rebooted in 3 secs.")) {
                            // PROTOCOL:
                            // TODO: Reconnect
                            mBluetoothLEService.reConnect();
                        }
                    }
                    if (commandResponseFlag && !commandResponseKey.equals("")) {
                        commandResponseValue = response.replace(" ", "");
                        mValueManager.updateResponse(commandResponseKey, commandResponseValue);

                        commandResponseFlag = false;
                    }
                }
            } else if (action.equals(BluetoothLEService.ACTION_GATT_SERVICES_DISCOVERED)) {
                Log.d(TAG, "onReceive: Gatt Services Discovered");

                if (mBluetoothLEService != null && mValueManager == null) {
                    Log.d(TAG, "onBLEServiceConnected");

                    mValueManager = new ValueManager();
                    binding.setValue(mValueManager);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        BluetoothLEService.exchangeGattMtu(50);
                    }
                }
            } else if (action.equals(BluetoothLEService.ACTION_GATT_DISCONNECTED)) {
                Log.d(TAG, "onReceive: Gatt is Disconnected");

                mGattConnected = ConnectionStatus.STATE_DISCONNECTED;
                if (mBluetoothLEService != null) {
                    mBluetoothLEService.disconnect();
                }

                Toast.makeText(SettingsActivity.this, "BLE Connection is Unstable.", Toast.LENGTH_SHORT).show();
                finish();
            }

            if (mGattConnected == ConnectionStatus.STATE_DISCONNECTED) {
                registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());//TODO: solve leak
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
            } else {
                mDialog = ProgressDialog.show(SettingsActivity.this, mDeviceName, "Connecting ...", true, true);
                if (mBluetoothLEService != null) {
                    Log.d(TAG, "Gets BluetoothLEService");
                }
                mServiceConnected = ConnectionStatus.STATE_CONNECTED;
                mBluetoothLEService.connect(mDeviceAddress, SettingsActivity.this);

                registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());//TODO: solve leak

                Log.d(TAG, "ServiceConnection=" + mServiceConnected);
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

        //TODO: Ensure it to be worked
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
        }
        return result;
    }

    @Override
    protected void onResume() {
//        if (mGattConnected != ConnectionStatus.STATE_CONNECTED) {
//            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());//TODO: solve leak
//        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBluetoothLEService != null) {
            Log.d(TAG, "onStop");
            mBluetoothLEService.disconnect();
            mBluetoothLEService = null;
        }
    }

    @Override
    protected void onPause() {
        activity_finish();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        activity_finish();
        finish();
        super.onBackPressed();
    }

    private void activity_finish() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.hide();
            mDialog.dismiss();
        }

        if (mDialogBuilder == null ||
                (mDialogBuilder != null && !mDialogBuilder.isShowing())) {
            Log.d(TAG, "onPause");

            if (mBluetoothLEService != null) {
                mBluetoothLEService.disconnect();
                mBluetoothLEService = null;
            }

            try {
                if (mGattUpdateReceiver != null && mGattConnected == ConnectionStatus.STATE_CONNECTED) {
                    unregisterReceiver(mGattUpdateReceiver);
                }
            } catch (IllegalArgumentException e) {
                Log.d(TAG, "There is no BroadcastReceiver to unregister.");
            }

            try {
                if (mServiceConnected == ConnectionStatus.STATE_CONNECTED) {
                    unbindService(mServiceConnection);
                }
            } catch (IllegalArgumentException e) {
                Log.d(TAG, "There is no Service to unbind.");
            }
        }
    }
}