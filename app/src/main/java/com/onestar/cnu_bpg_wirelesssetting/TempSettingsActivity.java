package com.onestar.cnu_bpg_wirelesssetting;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TempSettingsActivity extends AppCompatActivity implements Button.OnClickListener,
        ValueManager.ValueManagerListener, DialogBuilder.dialogBuilderListener, SettingsActivityListener {
    private final static String TAG = TempSettingsActivity.class.getSimpleName();

    private static BluetoothLEService mBluetoothLEService;
    private ValueManager mValueManager;
    private DialogBuilder mDialogBuilder;

    private String mDeviceName = "DEVICE_NAME";
    private String mDeviceAddress = "DEVICE_ADDRESS";

    private ConnectionStatus mGattConnected = ConnectionStatus.STATE_DISCONNECTED;
    private ConnectionStatus mServiceConnected = ConnectionStatus.STATE_DISCONNECTED;

    private ProgressDialog mDialog;

    @BindView(R.id.frequencyButton) Button freqBtn;
    @BindView(R.id.setTimeButton) Button setTimeBtn;
    @BindView(R.id.rebootButton) Button rebootBtn;
    @BindView(R.id.reportButton) Button reportBtn;
    @BindView(R.id.ledButton) Button ledBtn;
    @BindView(R.id.targetButton) Button targetBtn;
    @BindView(R.id.wifiButton) Button wifiBtn;
    @BindView(R.id.protocolButton) Button protocolBtn;
    @BindView(R.id.startButton) Button startBtn;
    @BindView(R.id.stopButton) Button stopBtn;

    @BindView(R.id.frequencyTextView) TextView freqView;
    @BindView(R.id.setTimeTextView) TextView timeView;
    @BindView(R.id.rebootTextView) TextView delayView;
    @BindView(R.id.reportTextView) TextView reportView;
    @BindView(R.id.ssidTextView) TextView ssidView;
    @BindView(R.id.passwordTextView) TextView passwordView;
    @BindView(R.id.redTextView) TextView redView;
    @BindView(R.id.greenTextView) TextView greenView;
    @BindView(R.id.blueTextView) TextView blueView;
    @BindView(R.id.yellowTextView) TextView yellowView;
    @BindView(R.id.irTextView) TextView irView;
    @BindView(R.id.pressure1TextView) TextView pressure1View;
    @BindView(R.id.pressure2TextView) TextView pressure2View;
    @BindView(R.id.rgbTextView) TextView rgbView;
    @BindView(R.id.iryTextView) TextView iryView;
    @BindView(R.id.accgyroTextView) TextView accgyroView;
    @BindView(R.id.timestampTextView) TextView timestampView;
    @BindView(R.id.protocolTextView) TextView protocolView;
    @BindView(R.id.protocolTextView) TextView portView;


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

                Toast.makeText(TempSettingsActivity.this, "BLE Connection is Unstable.", Toast.LENGTH_SHORT).show();
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
                mDialog = ProgressDialog.show(TempSettingsActivity.this, mDeviceName, "Connecting ...", true, true);
                final boolean result = mBluetoothLEService.connect(mDeviceAddress);

                mBluetoothLEService.setValueManagerListener(TempSettingsActivity.this);

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
        setContentView(R.layout.activity_temp);

        mDeviceName = getIntent().getExtras().getString("name");
        mDeviceAddress = getIntent().getExtras().getString("address");

        bindService(new Intent(this, BluetoothLEService.class), mServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        ButterKnife.bind(this);

        freqBtn.setOnClickListener(this);
        setTimeBtn.setOnClickListener(this);
        rebootBtn.setOnClickListener(this);
        reportBtn.setOnClickListener(this);
        ledBtn.setOnClickListener(this);
        targetBtn.setOnClickListener(this);
        wifiBtn.setOnClickListener(this);
        protocolBtn.setOnClickListener(this);
        startBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String key = ((Button) view).getText().toString();

        if (mDialogBuilder == null) {
            mDialogBuilder = new DialogBuilder(this, this);
        }

        mDialogBuilder.makeDialog(key);
    }

    @Override
    public void onDialogValueChanged(String key, String params) {
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
            mValueManager = new ValueManager(TempSettingsActivity.this, mBluetoothLEService, TempSettingsActivity.this);
        }
    }

    @Override
    public void onValueUpdated(String key, String newValue) {
        if (!key.equals("")) {
            switch (key) {
                case "Pulse":
                    freqView.setText(newValue);
                    break;
                case "RED":
                    redView.setText(newValue);
                    break;
                case "GRN":
                    greenView.setText(newValue);
                    break;
                case "BLU":
                    blueView.setText(newValue);
                    break;
                case "YEL":
                    yellowView.setText(newValue);
                    break;
                case "IR":
                    irView.setText(newValue);
                    break;
                case "Pressure_1st":
                    pressure1View.setText(newValue);
                    break;
                case "Pressure_2nd":
                    pressure2View.setText(newValue);
                    break;
                case "Optical_RGB":
                    rgbView.setText(newValue);
                    break;
                case "Optical_IrY":
                    iryView.setText(newValue);
                    break;
                case "Acc/Gyro":
                    accgyroView.setText(newValue);
                    break;
                case "Include":
                    timestampView.setText(newValue);
                    break;
                case "Report":
                    reportView.setText(newValue);
                    break;
                case "Current":
                    timeView.setText(newValue);
                    break;
                case "UDP/TCP":
                    protocolView.setText(newValue);
                    break;
                case "Port":
                    portView.setText(newValue);
                    break;
                //TODO: case for wifi (ssid, pw)
            }
            Toast.makeText(TempSettingsActivity.this, "Set " + key + " to new value: " + newValue, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGattConnected == ConnectionStatus.STATE_DISCONNECTED) {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        }
        if (mServiceConnected == ConnectionStatus.STATE_DISCONNECTED){
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

        if (mGattUpdateReceiver != null && mGattConnected == ConnectionStatus.STATE_CONNECTED){
            unregisterReceiver(mGattUpdateReceiver);
        }

        if(mServiceConnected == ConnectionStatus.STATE_CONNECTED){
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