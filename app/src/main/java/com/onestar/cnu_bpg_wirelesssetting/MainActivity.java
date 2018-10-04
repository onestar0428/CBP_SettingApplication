package com.onestar.cnu_bpg_wirelesssetting;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private static final long SCAN_PERIOD = 8000; // Stops scanning after 8 seconds
    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBLEScanner;
    private SimpleAdapter mLeDeviceListAdapter;
    private List<Map<String, String>> mLeDevices;
    private HashMap<String, String> mDeviceList;
    private Handler mHandler;

    private boolean mScanning = false;
    private String mDeviceName = "DEVICE_NAME";
    private String mDeviceAddress = "DEVICE_ADDRESS";

    private Button btnSearch;
    private ListView listDevice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSearch = (Button) findViewById(R.id.searchButton);
        btnSearch.setText(R.string.bluetooth_search_default);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeDevices.clear();
                mDeviceList.clear();
                btnSearch.setText(R.string.bluetooth_search_ing);
                btnSearch.setEnabled(false);

                mScanning = true;
                scanLeDevice(mScanning);
            }
        });

        mHandler = new Handler();

        // Initializes list view adapter
        listDevice = (ListView) findViewById(R.id.device_list);
        mLeDevices = new ArrayList<>();
        mDeviceList = new HashMap<>();
        mLeDeviceListAdapter = new SimpleAdapter(MainActivity.this, mLeDevices, android.R.layout.simple_list_item_2, new String[]{"name", "address"}, new int[]{android.R.id.text1, android.R.id.text2});
        listDevice.setAdapter(mLeDeviceListAdapter);

        // Check if device supports BLE
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Check if device supports Bluetooth
        mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Start SettingsActivity when one of a list item in listDevice is clicked
        listDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mScanning = false;
                scanLeDevice(mScanning);

                mDeviceName = mLeDevices.get(position).get("name");
                mDeviceAddress = mLeDevices.get(position).get("address");

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("name", mDeviceName);
                intent.putExtra("address", mDeviceAddress);

                startActivity(intent);
            }
        });
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined period
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBLEScanner.stopScan(mLeScanCallback);

                    btnSearch.setText(R.string.bluetooth_search_default);
                    btnSearch.setEnabled(true);
                    mScanning = false;
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBLEScanner.startScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBLEScanner.stopScan(mLeScanCallback);
        }
    }

    // Device Scan callback
    private ScanCallback mLeScanCallback = new ScanCallback() {
        Map map = new HashMap();

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            processResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {

            for (ScanResult result : results) {
                processResult(result);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
        }

        private void processResult(final ScanResult result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String name = result.getDevice().getName();
                    String address = result.getDevice().getAddress();
                    map.put("name", name); //device.getName() : device  name
                    map.put("address", address); //device.getAddress() : device MAC address

                    if (!mDeviceList.containsKey(name) && name != null && name.startsWith("CNU_BPG")) {
                        mLeDevices.add(map);
                        mDeviceList.put(name, address);
                        mLeDeviceListAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

}