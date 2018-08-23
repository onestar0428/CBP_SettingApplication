package com.onestar.cnu_bpg_wirelesssetting;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

public class BluetoothGatt extends BluetoothGattCallback {
    public BluetoothGatt(){

    }

    @Override
    public void onConnectionStateChange(android.bluetooth.BluetoothGatt gatt, int status, int newState) {
        //Connection established
        if (status == android.bluetooth.BluetoothGatt.GATT_SUCCESS
                && newState == BluetoothProfile.STATE_CONNECTED) {

            //Discover services
            gatt.discoverServices();

        } else if (status == android.bluetooth.BluetoothGatt.GATT_SUCCESS
                && newState == BluetoothProfile.STATE_DISCONNECTED) {

            //Handle a disconnect event

        }
    }

    @Override
    public void onServicesDiscovered(android.bluetooth.BluetoothGatt gatt, int status) {
        if (status == android.bluetooth.BluetoothGatt.GATT_SUCCESS) {
            boolean connected = false;
//
//            BluetoothGattService service = gatt.getService(SERVICE_UUID);
//            if (service != null) {
//                BluetoothGattCharacteristic characteristic = service.getCharacteristic(CHARACTERISTIC_COUNTER_UUID);
//                if (characteristic != null) {
//                    gatt.setCharacteristicNotification(characteristic, true);
//
//                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(DESCRIPTOR_CONFIG);
//                    if (descriptor != null) {
//                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
//                        connected = gatt.writeDescriptor(descriptor);
//                    }
//                }
//            }
//            mListener.onConnected(connected);

/*      if(mBluetoothAdapter == null)
    {
        mBluetoothManager = (BluetoothManager) mContext.getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }
    BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
    EventBus.getDefault().post(new Events.Interac(bluetoothDevice));*/


        } else {
//            Log.w(TAG, "onServicesDiscovered received: " + status);
        }
    }
}