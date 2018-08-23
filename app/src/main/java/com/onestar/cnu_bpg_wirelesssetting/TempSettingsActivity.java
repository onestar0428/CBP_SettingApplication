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


public class TempSettingsActivity extends AppCompatActivity implements Button.OnClickListener, ValueManager.ValueManagerListener, DialogBuilder.dialogBuilderListener {
    private final static String TAG = TempSettingsActivity.class.getSimpleName();

    private static BluetoothLEService mBluetoothLEService;
    private static ValueManager mValueManager;
    private static DialogBuilder mDialogBuilder;

    private String mDeviceName = "DEVICE_NAME";
    private String mDeviceAddress = "DEVICE_ADDRESS";
    private boolean mConnected = false;

    private ProgressDialog mDialog;
    private Button queryBtn, freqBtn, setTimeBtn, rebootBtn, reportBtn,
            wifiBtn, ledBtn, targetBtn, protocolBtn, startBtn, stopBtn;
    private TextView ssidView;

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

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

                Log.d(TAG, "Connect request result=" + result);

                if (result) {
                    if (mValueManager == null) {
                        mValueManager = new ValueManager(TempSettingsActivity.this, mBluetoothLEService);
                        mBluetoothLEService.setValueManagerListener(TempSettingsActivity.this);
                    }
                }
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
        ssidView = (TextView) findViewById(R.id.ssidTextView);

        queryBtn = (Button) findViewById(R.id.wifiButton);
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

        queryBtn.setOnClickListener(this);
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
    public void onDialogValueChanged(String key, String... args) {
        boolean result = mValueManager.setValues(key, args);
        //Toast result
    }

    @Override
    public void onBLEResponseReceived(String response) {
        mValueManager.update(response);
        updateViews();

        //Toast result
    }

    private void updateViews() {
        ssidView.setText(mValueManager.getSsid());
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
