package com.onestar.cnu_bpg_wirelesssetting.previous;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.onestar.cnu_bpg_wirelesssetting.R;
import com.onestar.cnu_bpg_wirelesssetting.SettingsActivity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BluetoothConnectionActivity extends AppCompatActivity {
    // BluetoothAdapter
    private BluetoothAdapter mBluetoothAdapter;
    private final static int BLUETOOTH_REQUEST_CODE = 100;

    private TextView txtState;
    private Button btnSearch;
    private ListView listDevice;

    // Adapter
    private SimpleAdapter adapterPaired;
    private SimpleAdapter adapterDevice;

    // Device list
    private List<Map<String, String>> dataDevice;
    private List<BluetoothDevice> bluetoothDevices;
    private int selectDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtState = (TextView) findViewById(R.id.stateTextView);
        btnSearch = (Button) findViewById(R.id.searchButton);
        listDevice = (ListView) findViewById(android.R.id.list);

        //Adapter2
        dataDevice = new ArrayList<>();
        adapterDevice = new SimpleAdapter(this, dataDevice, android.R.layout.simple_list_item_2, new String[]{"name", "address"}, new int[]{android.R.id.text1, android.R.id.text2});
        listDevice.setAdapter(adapterDevice);

        //검색된 블루투스 디바이스 데이터
        bluetoothDevices = new ArrayList<>();
        //선택한 디바이스 없음
        selectDevice = -1;

        // check the device can support bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "THIS DEVICE CANNOT SUPPORT BLUETOOTH", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // register bluetooth broadcast receiver
        //receiver1
        IntentFilter stateFilter = new IntentFilter();
        stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //BluetoothAdapter.ACTION_STATE_CHANGED : 블루투스 상태변화 액션
        registerReceiver(mBluetoothStateReceiver, stateFilter);
        // receiver2
        IntentFilter searchFilter = new IntentFilter();
        searchFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); //BluetoothAdapter.ACTION_DISCOVERY_STARTED : 블루투스 검색 시작
        searchFilter.addAction(BluetoothDevice.ACTION_FOUND); //BluetoothDevice.ACTION_FOUND : 블루투스 디바이스 찾음
        searchFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //BluetoothAdapter.ACTION_DISCOVERY_FINISHED : 블루투스 검색 종료
        searchFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBluetoothSearchReceiver, searchFilter);


        //1. 블루투스가 꺼져있으면 활성화
//        if(!mBluetoothAdapter.isEnabled()){
//            mBluetoothAdapter.enable(); //강제 활성화
//        }

        //2. 블루투스가 꺼져있으면 사용자에게 활성화 요청하기
        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BLUETOOTH_REQUEST_CODE);
        } else {
        }

        //검색된 디바이스목록 클릭시 페어링 요청
        listDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = bluetoothDevices.get(position);

                try {
                    //선택한 디바이스 페어링 요청
                    Method method = device.getClass().getMethod("createBond", (Class[]) null);
                    method.invoke(device, (Object[]) null);
                    selectDevice = position;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    // Bluetooth status BroadcastReceiver
    BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //BluetoothAdapter.EXTRA_STATE : 블루투스의 현재상태 변화
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

            //블루투스 활성화
            if (state == BluetoothAdapter.STATE_ON) {
                txtState.setText("Activate Bluetooth");
            }
            //블루투스 활성화 중
            else if (state == BluetoothAdapter.STATE_TURNING_ON) {
                txtState.setText("Activating Bluetooth...");
            }
            //블루투스 비활성화
            else if (state == BluetoothAdapter.STATE_OFF) {
                txtState.setText("Deactivate Bluetooth");
            }
            //블루투스 비활성화 중
            else if (state == BluetoothAdapter.STATE_TURNING_OFF) {
                txtState.setText("Deactivating Bluetooth...");
            }
        }
    };

    // Bluetooth search result BroadcastReceiver
    BroadcastReceiver mBluetoothSearchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                // Start searching device
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    dataDevice.clear();
                    bluetoothDevices.clear();
                    Toast.makeText(BluetoothConnectionActivity.this, "Start to searching devices", Toast.LENGTH_SHORT).show();
                    break;
                // Searched device
                case BluetoothDevice.ACTION_FOUND:
                    // get device object
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getName().startsWith("CNU_BPG")) {
                        // save the datat
                        Map map = new HashMap();
                        map.put("name", device.getName()); //device.getName() : device  name
                        map.put("address", device.getAddress()); //device.getAddress() : device MAC address
                        dataDevice.add(map);
                        // update device list
                        adapterDevice.notifyDataSetChanged();

                        // save device
                        bluetoothDevices.add(device);
                    }
                    break;
                // finish searching devices
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Toast.makeText(BluetoothConnectionActivity.this, "Finish searching devices", Toast.LENGTH_SHORT).show();
                    btnSearch.setText(R.string.bluetooth_search_default);
                    btnSearch.setEnabled(true);
                    break;
                // pairing status changed
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    BluetoothDevice paired = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    startActivity(new Intent(BluetoothConnectionActivity.this, SettingsActivity.class));
                    if (paired.getBondState() == BluetoothDevice.BOND_BONDED) {
                        // save data
                        Map map2 = new HashMap();
                        map2.put("name", paired.getName()); //device.getName()
                        map2.put("address", paired.getAddress()); //device.getAddress()
                        // update device list
                        adapterPaired.notifyDataSetChanged();

                        // searched list
                        if (selectDevice != -1) {
                            bluetoothDevices.remove(selectDevice);

                            dataDevice.remove(selectDevice);
                            adapterDevice.notifyDataSetChanged();
                            selectDevice = -1;
                        }
                        //startActivity(new Intent(BluetoothConnectionActivity.this, SettingsActivity.class));
                    } else {
                        Toast.makeText(BluetoothConnectionActivity.this, "Fail to connect", Toast.LENGTH_SHORT).show();
                        Log.d("BOND_STATE_CHANGED: ", paired.getBondState() + " ?= " + BluetoothDevice.BOND_BONDED);
                    }
                    break;
            }
        }
    };

    // search button onClick
    public void mOnBluetoothSearch(View v) {
        // deactivate search button
        btnSearch.setText(R.string.bluetooth_search_ing);
        btnSearch.setEnabled(false);

        // check if already searching
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery(); // cancel searching
        }
        // start to search
        mBluetoothAdapter.startDiscovery();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BLUETOOTH_REQUEST_CODE:
                // approve bluetooth activation
                if (resultCode == Activity.RESULT_OK) {
                }
                // refuse bluetooth activation
                else {
                    Toast.makeText(this, "Activate Bluetooth", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBluetoothStateReceiver);
        unregisterReceiver(mBluetoothSearchReceiver);
        super.onDestroy();
    }
}
