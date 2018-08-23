//package com.onestar.cnu_bpg_wirelesssetting;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.widget.Toast;
//
//import java.io.Serializable;
//
//public class CommandManager {
//    private static Context mContext;
//    private static BluetoothLEService mBluetoothLEService;
//
//    private final static String QUERY = "QUERY";
//    private final static String FREQUENCY = "FREQUENCY:";
//    private final static String LED = "LED:";
//    private final static String TARGET = "TARGET:";
//    private final static String REPORT_TO = "REPORT_TO:";
//    private final static String WIFI = "WIFI:";
//    private final static String PROTOCOL = "PROTOCOL:";
//    private final static String SET_TIME = "SET_TIME:";
//    private final static String REBOOT = "REBOOT:";
//
//    private final static String START = "STAR:";
//    private final static String RESUME = "RESUME";
//    private final static String STOP = "STOP";
//
//
//    public CommandManager(Context mContext, BluetoothLEService mService) {
//        this.mContext = mContext;
//        this.mBluetoothLEService = mService;
//    }
////
////    private String makeCommand(String key) {
////        String command = "";
////
////        if (key.equals(mContext.getResources().getString(R.string.freq))) {
////            command = FREQUENCY + mValueManager.getFreq();
////            // Reboot manually
////            Toast.makeText(mContext, "Manual Reboot required", Toast.LENGTH_SHORT).show();
////        } else if (key.equals(mContext.getResources().getString(R.string.led_red))) {
////            command = ledCommand();
////        } else if (key.equals(mContext.getResources().getString(R.string.led_green))) {
////            command = ledCommand();
////        } else if (key.equals(mContext.getResources().getString(R.string.led_blue))) {
////            command = ledCommand();
////        } else if (key.equals(mContext.getResources().getString(R.string.led_yellow))) {
////            command = ledCommand();
////        } else if (key.equals(mContext.getResources().getString(R.string.led_ir))) {
////            command = ledCommand();
////        } else if (key.equals(mContext.getResources().getString(R.string.pressure1))) {
////            command = targetCommand();
////        } else if (key.equals(mContext.getResources().getString(R.string.pressure2))) {
////            command = targetCommand();
////        } else if (key.equals(mContext.getResources().getString(R.string.rgb))) {
////            command = targetCommand();
////        } else if (key.equals(mContext.getResources().getString(R.string.iry))) {
////            command = targetCommand();
////        } else if (key.equals(mContext.getResources().getString(R.string.accgyro))) {
////            command = targetCommand();
////        } else if (key.equals(mContext.getResources().getString(R.string.timestamp))) {
////            command = targetCommand();
////        } else if (key.equals(mContext.getResources().getString(R.string.protocol))) {
////            command = communicateCommand();
////        } else if (key.equals(mContext.getResources().getString(R.string.port))) {
////            command = communicateCommand();
////        } else if (key.equals(mContext.getResources().getString(R.string.ssid))) {
////            command = wifiCommand();
////        } else if (key.equals(mContext.getResources().getString(R.string.password))) {
////            command = wifiCommand();
////        } else if (key.equals(mContext.getResources().getString(R.string.set_time))) {
////            command = SET_TIME + mValueManager.getTime();
////        } else if (key.equals(mContext.getResources().getString(R.string.report_to))) {
////            command = REPORT_TO + mValueManager.getReport();
////        } else if (key.equals(mContext.getResources().getString(R.string.reboot))) {
////            command = REBOOT + mValueManager.getDelay();
////        } else if (key.equals(mContext.getResources().getString(R.string.query))) {
////            command = QUERY;
////        } else if (key.equals(mContext.getResources().getString(R.string.start))) {
////            command = START;
////        } else if (key.equals(mContext.getResources().getString(R.string.resume))) {
////            command = RESUME;
////        } else if (key.equals(mContext.getResources().getString(R.string.stop))) {
////            command = STOP;
////        }
////
////        return command + ":";
////    }
//
//    private boolean sendCommand(String command) {
//        return mBluetoothLEService.sendCommand(command + ":");
//    }
//
//    public boolean queryCommand() {
//        String command = QUERY;
//        return sendCommand(command);
//    }
//
//    private boolean ledCommand(int red, int green, int blue, int yellow, int ir) {
//        String command = LED + red + ":" + green + "" + ":" + blue + "" + ":" + yellow + "" + ":" + ir;
//
//        // Reboot manually
//        Toast.makeText(mContext, "Manual Reboot required", Toast.LENGTH_SHORT).show();
//
//        return sendCommand(command);
//    }
//
//    private boolean targetCommand(boolean p1, boolean p2, boolean rgb, boolean iry, boolean accgyro, boolean timestamp) {
//        String command = TARGET + p1 + ":" + p2 + ":" + rgb + ":" + iry + ":" + accgyro + ":" + timestamp;
//
//        return sendCommand(command);
//    }
////
////    private String wifiCommand() {
////        return PROTOCOL + mValueManager.getSsid() + ":" + mValueManager.getPassword();
////    }
////
////    private String communicateCommand() {
////        return PROTOCOL + mValueManager.getProtocol() + ":" + mValueManager.getPort();
////    }
//
//    private String boolToString(boolean bool) {
//        if (bool) {
//            return 1 + "";
//        } else {
//            return 0 + "";
//        }
//    }
//}
//
