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
import android.content.res.Resources;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.UUID;


public class BluetoothLEService extends Service {
    private final static String TAG = BluetoothLEService.class.getSimpleName();
    private static Context mContext;

    private static BluetoothManager mBluetoothManager;
    private static BluetoothAdapter mBluetoothAdapter;
    private static BluetoothGattService mBluetoothGattService;
    private static BluetoothGatt mBluetoothGatt;
    private static BluetoothGattCharacteristic mWriteCharacteristic, mNotifyCharacteristic;

    private static ConnectionStatus mConnectionState = ConnectionStatus.STATE_DISCONNECTED;
    private static String mBluetoothDeviceAddress;

    private final IBinder mBinder = new LocalBinder();

    private final static int MAX_MTU = 150;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_CONNECTING =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTING";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_DISCONNECTED_CAROUSEL =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED_CAROUSEL";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String ACTION_OTA_DATA_AVAILABLE =
            "com.cysmart.bluetooth.le.ACTION_OTA_DATA_AVAILABLE";
    public final static String ACTION_GATT_DISCONNECTED_OTA =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED_OTA";
    public final static String ACTION_GATT_CONNECT_OTA =
            "com.example.bluetooth.le.ACTION_GATT_CONNECT_OTA";
    public final static String ACTION_GATT_SERVICES_DISCOVERED_OTA =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED_OTA";
    public final static String ACTION_GATT_CHARACTERISTIC_ERROR =
            "com.example.bluetooth.le.ACTION_GATT_CHARACTERISTIC_ERROR";
    public final static String ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL =
            "com.example.bluetooth.le.ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL";
    public final static String ACTION_PAIR_REQUEST =
            "android.bluetooth.device.action.PAIRING_REQUEST";
    public final static String ACTION_WRITE_COMPLETED =
            "android.bluetooth.device.action.ACTION_WRITE_COMPLETED";
    public final static String ACTION_WRITE_FAILED =
            "android.bluetooth.device.action.ACTION_WRITE_FAILED";
    public final static String ACTION_WRITE_SUCCESS =
            "android.bluetooth.device.action.ACTION_WRITE_SUCCESS";

    private final static UUID SERVICE_UUID = UUID.fromString("0000ABF0-0000-1000-8000-00805F9B34FB");
    private final static UUID DATA_RECEIVE_UUID = UUID.fromString("0000ABF1-0000-1000-8000-00805F9B34FB");
    private final static UUID DATA_NOTIFY_UUID = UUID.fromString("0000ABF2-0000-1000-8000-00805F9B34FB");
    private final static UUID DATA_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public final static String NOTIFY_KEY = "notify";

    private final static BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                synchronized (mGattCallback) {
                    mConnectionState = ConnectionStatus.STATE_CONNECTED;
                }

                broadcastUpdate(intentAction);
                Log.d(TAG, "Gatt is connected, Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                synchronized (mGattCallback) {
                    mConnectionState = ConnectionStatus.STATE_DISCONNECTED;
                }

                broadcastUpdate(intentAction);
                Log.d(TAG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                if (gatt != null) {
                    mBluetoothGattService = gatt.getService(SERVICE_UUID);
                }

                if (mBluetoothGattService != null) {
                    mWriteCharacteristic = mBluetoothGattService.getCharacteristic(DATA_RECEIVE_UUID);
                    mNotifyCharacteristic = mBluetoothGattService.getCharacteristic(DATA_NOTIFY_UUID);
                    Log.w(TAG, "onServicesDiscovered: " + mWriteCharacteristic + "," + mNotifyCharacteristic);
                }

                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered: " + status);
                if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION ||
                        status == BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION) {
                    bondDevice();
                }
                broadcastUpdate(ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            // NOTIFY
            if (characteristic.getUuid().equals(DATA_NOTIFY_UUID)) {
                String response = new String(characteristic.getValue());
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);

                Log.d(TAG, "onCharacteristicChanged(Notify): " + response);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                Log.d(TAG, "onCharacteristicRead(Read)");
            } else {
                Log.d(TAG, "onCharacteristicRead(Read): fail");
                if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION
                        || status == BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION) {
                    bondDevice();
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            if (status == 0) {
                if ((mNotifyCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    if (mNotifyCharacteristic != null) {
                        setCharacteristicNotification(mNotifyCharacteristic, true);
                    }

                    Log.d(TAG, "onCharacteristicWrite");
                    mBluetoothGatt.readCharacteristic(mNotifyCharacteristic);
                }
            }

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if ((mNotifyCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    if (mNotifyCharacteristic != null) {
                        setCharacteristicNotification(mNotifyCharacteristic, true);
                    }

                    Log.d(TAG, "onCharacteristicWrite");
                    mBluetoothGatt.readCharacteristic(mNotifyCharacteristic);
                }
            } else {
                Intent intent = new Intent(ACTION_GATT_CHARACTERISTIC_ERROR);
                mContext.sendBroadcast(intent);
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            // request for initialize the setting values for UI
            sendCommand(SettingsActivity.QUERY);
            Log.d(TAG, "MTU changed: " + mtu);
        }
    };

    public static void exchangeGattMtu() {
        int retry = 5;
        boolean status = false;
        while (!status && retry > 0) {
            status = mBluetoothGatt.requestMtu(MAX_MTU);
            retry--;
        }
        if (status) {
            Log.d(TAG, "requestMtu " + MAX_MTU + " success");
        } else {
            Log.d(TAG, "requestMtu " + MAX_MTU + " fail");
        }
    }

    public static void bondDevice() {
        try {
            Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
            Method createBondMethod = class1.getMethod("createBond");
            Boolean returnValue = (Boolean) createBondMethod.invoke(mBluetoothGatt.getDevice());
//            Logger.e("Pair initates status-->" + returnValue);
        } catch (Exception e) {
//            Logger.e("Exception Pair" + e.getMessage());
        }

    }

    private static void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);

        Log.d(TAG, "broadcastUpdate");
        mContext.sendBroadcast(intent);
    }

    private static void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        if (DATA_NOTIFY_UUID.equals(characteristic.getUuid())) {
            final byte[] data = characteristic.getValue();

            if (data != null && data.length > 0) {
                Bundle mBundle = new Bundle();
                mBundle.putString(NOTIFY_KEY, new String(characteristic.getValue()));
                intent.putExtras(mBundle);
            }
        }

        mContext.sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        BluetoothLEService getService() {
            return BluetoothLEService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        close();
        return super.onUnbind(intent);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public static void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        Log.d(TAG, "setCharacteristicNotification: " + characteristic.getUuid());
        if (DATA_NOTIFY_UUID.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(DATA_DESCRIPTOR_UUID);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            Log.d(TAG, "setCharacteristicNotification");

            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    public static boolean sendCommand(String command) {
        boolean response = false;

        Log.d(TAG, "sendCommand");
        if (mWriteCharacteristic == null) {
            Log.w(TAG, "BluetoothCharacteristic is null");

            return response;
        }
        //TODO: check Connection after send command which need to reboot
        //TODO: do reboot for specific commands
        if (mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothGatt is null and there is no info for device address");

            return response;
        } else {
            byte[] value = command.getBytes();
            mWriteCharacteristic.setValue(value);
            response = mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);

            Log.d(TAG, "sendCommand Result: " + command);
            Log.d(TAG, "sendCommand Result: " + response);
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
    public boolean connect(final String address, Context context) {
        mContext = context;

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

        Log.d(TAG, "connect");

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

    public static void reConnect() {
        BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(mBluetoothDeviceAddress);
        if (device == null) {
            return;
        }
        mBluetoothGatt = null;//Creating a new instance of GATT before connect
        mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
    }

    /**
     * Reconnect method to connect to already connected device
     */
    public static void reDiscoverServices() {
        BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(mBluetoothDeviceAddress);
        if (device == null) {
            return;
        }

        if (mBluetoothGatt != null)
            mBluetoothGatt.disconnect();

        mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
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
        Log.d(TAG, "disconnect");

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
}