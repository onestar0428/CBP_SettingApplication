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


public class TempSettingsActivity extends AppCompatActivity implements Button.OnClickListener,
        ValueManager.ValueManagerListener, DialogBuilder.dialogBuilderListener, SettingsActivityListener {
    private final static String TAG = TempSettingsActivity.class.getSimpleName();

    private static BluetoothLEService mBluetoothLEService;
    private ValueManager mValueManager;
    private DialogBuilder mDialogBuilder;

    private String mDeviceName = "DEVICE_NAME";
    private String mDeviceAddress = "DEVICE_ADDRESS";
    private boolean mConnected = false;

    private ProgressDialog mDialog;
    private Button freqBtn, setTimeBtn, rebootBtn, reportBtn,
            wifiBtn, ledBtn, targetBtn, protocolBtn, startBtn, stopBtn;
    private TextView freqView, timeView, delayView, reportView, ssidView, passwordView,
            redView, greenView, blueView, yellowView, irView,
            pressure1View, pressure2View, rgbView, iryView, accgyroView, timestampView,
            protocolView, portView;

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (mDialog.isShowing())
                mDialog.hide();

            switch (action) {
                case BluetoothLEService.ACTION_DATA_AVAILABLE:
                case BluetoothLEService.ACTION_GATT_SERVICES_DISCOVERED:
                    mConnected = true;
                    break;
                case BluetoothLEService.ACTION_GATT_DISCONNECTED:
                    mConnected = false;
                    mBluetoothLEService.disconnect();

                    Toast.makeText(TempSettingsActivity.this, "BLE Connection is Unstable.", Toast.LENGTH_SHORT).show();

                    finish();
                    break;
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

                if (result && mValueManager == null) {
                    mValueManager = new ValueManager(TempSettingsActivity.this, mBluetoothLEService, TempSettingsActivity.this);
                    mBluetoothLEService.setValueManagerListener(TempSettingsActivity.this);
                }

                Log.d(TAG, "Connect request result=" + result);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLEService = null;
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
        setContentView(R.layout.activity_temp);

        mDeviceName = getIntent().getExtras().getString("name");
        mDeviceAddress = getIntent().getExtras().getString("address");

        bindService(new Intent(this, BluetoothLEService.class), mServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        //------ UI -------
        freqView = (TextView) findViewById(R.id.frequencyTextView);
        timeView = (TextView) findViewById(R.id.setTimeTextView);
        delayView = (TextView) findViewById(R.id.rebootTextView);
        reportView = (TextView) findViewById(R.id.reportTextView);
        ssidView = (TextView) findViewById(R.id.ssidTextView);
        passwordView = (TextView) findViewById(R.id.passwordTextView);
        redView = (TextView) findViewById(R.id.redTextView);
        greenView = (TextView) findViewById(R.id.greenTextView);
        blueView = (TextView) findViewById(R.id.blueTextView);
        yellowView = (TextView) findViewById(R.id.yellowTextView);
        irView = (TextView) findViewById(R.id.irTextView);
        pressure1View = (TextView) findViewById(R.id.pressure1TextView);
        pressure2View = (TextView) findViewById(R.id.pressure2TextView);
        rgbView = (TextView) findViewById(R.id.rgbTextView);
        iryView = (TextView) findViewById(R.id.iryTextView);
        accgyroView = (TextView) findViewById(R.id.accgyroTextView);
        timestampView = (TextView) findViewById(R.id.timestampTextView);
        protocolView = (TextView) findViewById(R.id.protocolTextView);
        portView = (TextView) findViewById(R.id.portTextView);

        freqBtn = (Button) findViewById(R.id.frequencyButton);
        setTimeBtn = (Button) findViewById(R.id.setTimeButton);
        rebootBtn = (Button) findViewById(R.id.rebootButton);
        reportBtn = (Button) findViewById(R.id.reportButton);
        ledBtn = (Button) findViewById(R.id.ledButton);
        targetBtn = (Button) findViewById(R.id.targetButton);
        wifiBtn = (Button) findViewById(R.id.wifiButton);
        protocolBtn = (Button) findViewById(R.id.protocolButton);
        startBtn = (Button) findViewById(R.id.start);
        stopBtn = (Button) findViewById(R.id.stop);

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
        boolean result = mValueManager.setValues(key, params);
        //Toast result
    }

    @Override
    public void onBLEResponseReceived(String response) {
        mValueManager.update(response);
        //Toast result
    }

    @Override
    public void onValueUpdated(String key, String newValue) {
        if (!key.equals("")) {
            switch (key) {
                case "Pulse Rep. freq.":
                    freqView.setText(newValue);
                    break;
                case "RED Current":
                    redView.setText(newValue);
                    break;
                case "GRN Current":
                    greenView.setText(newValue);
                    break;
                case "BLU Current":
                    blueView.setText(newValue);
                    break;
                case "YEL Current":
                    yellowView.setText(newValue);
                    break;
                case "IR  Current":
                    irView.setText(newValue);
                    break;
                case "Pressure_1st Measurement":
                    pressure1View.setText(newValue);
                    break;
                case "Pressure_2nd Measurement":
                    pressure2View.setText(newValue);
                    break;
                case "Optical_RGB Measurement":
                    rgbView.setText(newValue);
                    break;
                case "Optical_IrY Measurement":
                    iryView.setText(newValue);
                    break;
                case "Acc/Gyro Measurement":
                    accgyroView.setText(newValue);
                    break;
                case "Include TimeStamp":
                    timestampView.setText(newValue);
                    break;
                case "Report to Console or NW":
                    reportView.setText(newValue);
                    break;
                case "Current Time":
                    timeView.setText(newValue);
                    break;
                case "UDP/TCP":
                    protocolView.setText(newValue);
                    break;
                case "Port No.":
                    portView.setText(newValue);
                    break;
            }
            Toast.makeText(TempSettingsActivity.this, "Set " + key  + " to new value: " + newValue, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mConnected == false) {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();

        if (mBluetoothLEService != null)
            mBluetoothLEService.disconnect();

        if (mGattUpdateReceiver != null)
            unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();

//        if (mGattUpdateReceiver != null)
//            unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();

        unbindService(mServiceConnection);
        mBluetoothLEService.disconnect();
        mBluetoothLEService = null;
    }

}

interface SettingsActivityListener {
    void onValueUpdated(String key, String newValue);
}