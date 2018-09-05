package com.onestar.cnu_bpg_wirelesssetting;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.UUID;


public class BluetoothLEService extends Service {
    private final static String TAG = BluetoothLEService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGattService mBluetoothGattService;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mWriteCharacteristic, mNotifyCharacteristic;

    private ValueManager.ValueManagerListener valueManagerListener;
    private ConnectionStatus mConnectionState = ConnectionStatus.STATE_DISCONNECTED;

    private final IBinder mBinder = new LocalBinder();

    private String mBluetoothDeviceAddress;
    private static final int REQUEST_MAX_MTU = 100;

    public enum GattStatus {
        ACTION_GATT_CONNECTED("com.example.bluetooth.le.ACTION_GATT_CONNECTED"),
        ACTION_GATT_DISCONNECTED("com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"),
        ACTION_GATT_SERVICES_DISCOVERED("com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"),
        ACTION_DATA_AVAILABLE("com.example.bluetooth.le.ACTION_DATA_AVAILABLE"),
        EXTRA_DATA("com.example.bluetooth.le.EXTRA_DATA");

        private String status;

        GattStatus(String status) {
            this.status = status;
        }

        public String getStatus(){
            return this.status;
        }
    }

    private enum Uuid {
        SERVICE_UUID(UUID.fromString("0000ABF0-0000-1000-8000-00805F9B34FB")),
        DATA_RECEIVE_UUID(UUID.fromString("0000ABF1-0000-1000-8000-00805F9B34FB")),
        DATA_NOTIFY_UUID(UUID.fromString("0000ABF2-0000-1000-8000-00805F9B34FB")),
        DATA_DESCRIPTOR_UUID(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));

        private UUID uuid;

        Uuid(UUID uuid) {
            this.uuid = uuid;
        }
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = GattStatus.ACTION_GATT_CONNECTED.status;
                mConnectionState = ConnectionStatus.STATE_CONNECTED;

                Log.i(TAG, "Connected to GATT server.");
                broadcastUpdate(intentAction);
                gatt.requestMtu(REQUEST_MAX_MTU);

                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = GattStatus.ACTION_GATT_DISCONNECTED.status;
                mConnectionState = ConnectionStatus.STATE_DISCONNECTED;

                Log.i(TAG, "Disconnected from GATT server.");

                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(GattStatus.ACTION_GATT_SERVICES_DISCOVERED.status);

                if (gatt != null) {
                    mBluetoothGattService = gatt.getService(Uuid.SERVICE_UUID.uuid);

                    Log.i(TAG, "Service characteristic UUID found: " + mBluetoothGattService.getUuid().toString());
                }

                if (mBluetoothGattService != null) {
                    mWriteCharacteristic = mBluetoothGattService.getCharacteristic(Uuid.DATA_RECEIVE_UUID.uuid);
                    mNotifyCharacteristic = mBluetoothGattService.getCharacteristic(Uuid.DATA_NOTIFY_UUID.uuid);

                    valueManagerListener.onBLEServiceConnected();
                }
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(GattStatus.ACTION_DATA_AVAILABLE.status, characteristic);

            // NOTIFY
            if (characteristic.getUuid().equals(Uuid.DATA_NOTIFY_UUID.uuid)) {
                String response = new String(characteristic.getValue());
                valueManagerListener.onBLEResponseReceived(response);

                Log.i(TAG, "COMMAND_RESULT: " + response);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(GattStatus.ACTION_DATA_AVAILABLE.status, characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic,
                                          int status) {
            if (status == 0) {
                //Toast "Send command successfully"

                if ((mNotifyCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    if (mNotifyCharacteristic != null) {
                        setCharacteristicNotification(mNotifyCharacteristic, true);
                    }

                    mBluetoothGatt.readCharacteristic(mNotifyCharacteristic);
                }
            }
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        if (Uuid.DATA_NOTIFY_UUID.uuid.equals(characteristic.getUuid())) {
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(GattStatus.EXTRA_DATA.status, new String(data) + "\n" + stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        BluetoothLEService getService() {
            return BluetoothLEService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        if (Uuid.DATA_NOTIFY_UUID.uuid.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(Uuid.DATA_DESCRIPTOR_UUID.uuid);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    public boolean sendCommand(String command) {
        boolean response = false;

        if (mWriteCharacteristic == null) {
            Log.w(TAG, "BluetoothCharacteristic is null");
            return response;
        }

        if ((mWriteCharacteristic.getProperties() & (BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) == 0) {
            mWriteCharacteristic.setWriteType(BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE);
        }

        if (mBluetoothGatt == null) {
            if ((mBluetoothDeviceAddress != null && !mBluetoothDeviceAddress.equals(""))) {
                connect(mBluetoothDeviceAddress);
            } else {
                Log.w(TAG, "BluetoothGatt is null and there is no info for device address");
                return response;
            }
        } else {
            byte[] value = command.getBytes();
            mWriteCharacteristic.setValue(value);
            response = mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
            //TODO: try to send at most 3 times when failed
        }

        return response;
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");

                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");

            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = ConnectionStatus.STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        mBluetoothDeviceAddress = address;
        mConnectionState = ConnectionStatus.STATE_CONNECTING;

        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothAdapter = null;
        mBluetoothGatt.disconnect();
        mBluetoothGatt = null;
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }

        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void setValueManagerListener(ValueManager.ValueManagerListener listener) {
        this.valueManagerListener = listener;
    }
}