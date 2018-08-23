package com.onestar.cnu_bpg_wirelesssetting;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatDelegate;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;
import java.util.UUID;

public class SettingsActivity extends PreferenceActivity {
    private final static String TAG = SettingsActivity.class.getSimpleName();

    private static SharedPreferences prefs;
    private static AppCompatDelegate mDelegate;
    private static BluetoothLEService mBluetoothLEService;
    private static CommandManager commandManager;

    private ProgressDialog mDialog;
    private String mDeviceName = "DEVICE_NAME";
    private String mDeviceAddress = "DEVICE_ADDRESS";
    private boolean mConnected = false;


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            mDialog.hide();

            if (action.equals(BluetoothLEService.ACTION_GATT_CONNECTED)) {
                mConnected = true;
            } else if (action.equals(BluetoothLEService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mConnected = true;
            } else {
                if (BluetoothLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                    mConnected = false;
                    mBluetoothLEService.disconnect();
                    Toast.makeText(SettingsActivity.this, "BLE Connection is Unstable.", Toast.LENGTH_SHORT).show();
                    finish();
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

                if (result && commandManager == null) {
                    commandManager = new CommandManager(SettingsActivity.this, mBluetoothLEService);
                }

                Log.d(TAG, "Connect request result=" + result);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLEService = null;
        }
    };

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings_preference);

            Preference query = findPreference(getResources().getString(R.string.query));
            query.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
//                    commandManager.sendCommand(preference.getKey());
                    return true;
                }
            });

            Preference reboot = findPreference(getResources().getString(R.string.reboot));
            reboot.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
//                    commandManager.sendCommand(preference.getKey());
                    return true;
                }
            });
            // help
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);

        mDeviceName = getIntent().getExtras().getString("name");
        mDeviceAddress = getIntent().getExtras().getString("address");

        bindService(new Intent(this, BluetoothLEService.class), mServiceConnection, BIND_AUTO_CREATE);

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content,
                        new MyPreferenceFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.next, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send_settings:
                if (mConnected) {
                    Intent intent = new Intent(getApplicationContext(), SensorDataControlActivity.class);
//                    intent.putExtra("OBJECT", commandManager);

                    startActivity(intent);
                    return true;
                }
                return false;
        }
        //return super.onOptionsItemSelected(item);
        return true;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLEService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();

        prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
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

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

                    if (mBluetoothLEService == null) {
                        finish();
                    }
//                    commandManager.sendCommand(key);
                }
            };


}
