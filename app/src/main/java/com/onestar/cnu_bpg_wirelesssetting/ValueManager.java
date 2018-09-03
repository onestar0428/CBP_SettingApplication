package com.onestar.cnu_bpg_wirelesssetting;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingConversion;

import com.android.databinding.library.baseAdapters.BR;

public class ValueManager extends BaseObservable {
    //    private static ValueManager.Commander mCommander;
    private static Context mContext;
    private SettingsActivityListener settingsActivityListener;
    private BluetoothLEService mBluetoothLEService;

    //TODO: try to transform values into ENUM with setter or OBSERVABLEFIELD
    private int freq = Default.DEFAULT_FREQ.defaultInt, delay = Default.DEFAULT_DELAY.defaultInt,
            red = Default.DEFAULT_LED.defaultInt, blue = Default.DEFAULT_LED.defaultInt,
            green = Default.DEFAULT_LED.defaultInt, yellow = Default.DEFAULT_LED.defaultInt,
            ir = Default.DEFAULT_LED.defaultInt;
    private boolean pressure1 = Default.DEFAULT_BOOL.defaultBoolean, pressure2 = Default.DEFAULT_BOOL.defaultBoolean,
            rgb = Default.DEFAULT_BOOL.defaultBoolean, iry = Default.DEFAULT_BOOL.defaultBoolean,
            accgyro = Default.DEFAULT_BOOL.defaultBoolean, timestamp = Default.DEFAULT_BOOL.defaultBoolean;
    private String report = Default.DEFAULT_REPORT.defaultString,
            ssid = Default.DEFAULT_STRING.defaultString, password = Default.DEFAULT_STRING.defaultString,
            protocol = Default.DEFAULT_STRING.defaultString, port = Default.DEFAULT_STRING.defaultString,
            time = Default.DEFAULT_TIME.defaultString;

    private enum Default {
        DEFAULT_LED(50), DEFAULT_FREQ(100), DEFAULT_DELAY(0),
        DEFAULT_BOOL(false),
        DEFAULT_REPORT("console"), DEFAULT_STRING(""), DEFAULT_TIME("2017:01:01:00:01:59");

        //TODO: right member variable?
        private int defaultInt;
        private boolean defaultBoolean;
        private String defaultString;

        Default(int value) {
            this.defaultInt = value;
        }

        Default(boolean value) {
            this.defaultBoolean = value;
        }

        Default(String value) {
            this.defaultString = value;
        }
    }

    //TODO: static factory or singleton
    public ValueManager(Context context, BluetoothLEService service, SettingsActivityListener listener) {
        mContext = context;
//        mCommander = new ValueManager.Commander(service);
        mBluetoothLEService = service;
        settingsActivityListener = listener;
    }

    public boolean initialize(){
        return sendCommand(Command.QUERY.header);
    }

    public void update(String response) {
        if (response.startsWith("!!")){
            //TODO: need new parser..
        }
        else{
            parseQuery(response);
        }
    }

    private void parseQuery(String response) {
        String parseAsterisk = response.replace(" * ", "");
        String key = parseAsterisk.split(":")[0].split(" ")[0];
        String value = parseAsterisk.split(":")[1].replace(" ", "");

        updateValues(key, value);
    }

    //TODO: Consider how to reduce code lines
    private void updateValues(String key, String value) {
        String newValue = "";

        if (!key.equals("")) {
            switch (key) {
                case "Pulse":
                    value = value.replaceAll("Hz", "").replaceAll("\n", "");
                    setFreq(Integer.parseInt(value));
                    settingsActivityListener.onValueUpdated(key, getFreq());
                    break;
                case "RED":
                    value = value.replaceAll("mA", "").replaceAll("\n", "");
                    setRed(Integer.parseInt(value));
                    settingsActivityListener.onValueUpdated(key, getRed());
                    break;
                case "GRN":
                    value = value.replaceAll("mA", "").replaceAll("\n", "");
                    setGreen(Integer.parseInt(value));
                    settingsActivityListener.onValueUpdated(key, getGreen());
                    break;
                case "BLU":
                    value = value.replaceAll("mA", "").replaceAll("\n", "");
                    setBlue(Integer.parseInt(value));
                    settingsActivityListener.onValueUpdated(key, getBlue());
                    break;
                case "YEL":
                    value = value.replaceAll("mA", "").replaceAll("\n", "");
                    setYellow(Integer.parseInt(value));
                    settingsActivityListener.onValueUpdated(key, getYellow());
                    break;
                case "IR":
                    value = value.replaceAll("mA", "").replaceAll("\n", "");
                    setIr(Integer.parseInt(value));
                    settingsActivityListener.onValueUpdated(key, getIr());
                    break;
                case "Pressure_1st":
                    setPressure1(stringToBool(value));
                    settingsActivityListener.onValueUpdated(key, getPressure1());
                    break;
                case "Pressure_2nd":
                    setPressure2(stringToBool(value));
                    settingsActivityListener.onValueUpdated(key, getPressure2());
                    break;
                case "Optical_RGB":
                    setRgb(stringToBool(value));
                    settingsActivityListener.onValueUpdated(key, getRgb());
                    break;
                case "Optical_IrY":
                    setIry(stringToBool(value));
                    settingsActivityListener.onValueUpdated(key, getIry());
                    break;
                case "Acc/Gyro":
                    setAccgyro(stringToBool(value));
                    settingsActivityListener.onValueUpdated(key, getAccgyro());
                    break;
                case "Include":
                    setTimestamp(stringToBool(value));
                    settingsActivityListener.onValueUpdated(key, getTimestamp());
                    break;
                case "Report":
                    setReport(value.replaceAll("\n", ""));
                    settingsActivityListener.onValueUpdated(key, getReport());
                    break;
                case "Current":
                    setTime(value.replaceAll("\n", ""));
                    settingsActivityListener.onValueUpdated(key, getTime());
                    break;
                case "UDP/TCP":
                    setProtocol(value.replaceAll("\n", ""));
                    settingsActivityListener.onValueUpdated(key, getProtocol());
                    break;
                case "Port":
                    setPort(value.replaceAll("\n", ""));
                    settingsActivityListener.onValueUpdated(key, getPort());
                    break;
            }
        }
    }

    public boolean setValues(String key, String params) {
        boolean result = false;

        if (!key.equals("") && !params.equals("")) {
            String command = "";

            if (key.equals(mContext.getResources().getString(R.string.freq))) {
                command = Command.FREQUENCY.makeCommand(params);
            } else if (key.equals(mContext.getResources().getString(R.string.led))) {
                command = Command.LED.makeCommand(params);
            } else if (key.equals(mContext.getResources().getString(R.string.target))) {
                command = Command.TARGET.makeCommand(params);
            } else if (key.equals(mContext.getResources().getString(R.string.report_to))) {
                command = Command.REPORT_TO.makeCommand(params);
            } else if (key.equals(mContext.getResources().getString(R.string.wifi))) {
                command = Command.WIFI.makeCommand(params);
            } else if (key.equals(mContext.getResources().getString(R.string.protocol))) {
                command = Command.PROTOCOL.makeCommand(params);
            } else if (key.equals(mContext.getResources().getString(R.string.set_time))) {
                command = Command.SET_TIME.makeCommand(params);
            } else if (key.equals(mContext.getResources().getString(R.string.reboot))) {
                command = Command.REBOOT.makeCommand(params);
            }

            result = sendCommand(command);
        }
        return result;
    }

    private boolean sendCommand(String command) {
        //TODO: Handle about QUERY more efficient
        //send command
        if (!command.equals("")) {
            boolean result = mBluetoothLEService.sendCommand(command);

            if (result && !command.startsWith(Command.QUERY.header)) {
                return mBluetoothLEService.sendCommand(Command.QUERY.header);
            }
        }
        return false;
    }

    private boolean stringToBool(String value) {
        value = value.replaceAll("\n", "");
        if (value.equals("Yes")) {
            return true;
        } else {
            return false;
        }
    }

    // -----------------------------------------------------------
    // --------------------- GETTER & SETTER ---------------------
    // -----------------------------------------------------------

    @Bindable
    public String getFreq() {
        return freq + "";
    }

    private void setFreq(int freq) {
        this.freq = freq;
        notifyPropertyChanged(BR.freq);
    }

    @Bindable
    public String getRed() {
        return red + "";
    }

    private void setRed(int red) {
        this.red = red;
        notifyPropertyChanged(BR.red);
    }

    @Bindable
    public String getBlue() {
        return blue + "";
    }

    private void setBlue(int blue) {
        this.blue = blue;
        notifyPropertyChanged(BR.blue);
    }

    @Bindable
    public String getGreen() {
        return green + "";
    }

    private void setGreen(int green) {
        this.green = green;
        notifyPropertyChanged(BR.green);
    }

    @Bindable
    public String getYellow() {
        return yellow + "";
    }

    private void setYellow(int yellow) {
        this.yellow = yellow;
        notifyPropertyChanged(BR.yellow);
    }

    @Bindable
    public String getIr() {
        return ir + "";
    }

    private void setIr(int ir) {
        this.ir = ir;
        notifyPropertyChanged(BR.ir);
    }

    @Bindable
    public String getDelay() {
        return delay + "";
    }

    private void setDelay(int delay) {
        this.delay = delay;
        notifyPropertyChanged(BR.delay);
    }

    @Bindable
    public String getPressure1() {
        if (pressure1) {
            return "ON";
        }
        return "OFF";
    }

    private void setPressure1(boolean pressure1) {
        this.pressure1 = pressure1;
        notifyPropertyChanged(BR.pressure1);
    }

    @Bindable
    public String getPressure2() {
        if (pressure2) {
            return "ON";
        }
        return "OFF";
    }

    private void setPressure2(boolean pressure2) {
        this.pressure2 = pressure2;
        notifyPropertyChanged(BR.pressure2);
    }

    @Bindable
    public String getRgb() {
        if (rgb) {
            return "ON";
        }
        return "OFF";
    }

    private void setRgb(boolean rgb) {
        this.rgb = rgb;
        notifyPropertyChanged(BR.rgb);
    }

    @Bindable
    public String getIry() {
        if (iry) {
            return "ON";
        }
        return "OFF";
    }

    public void setIry(boolean iry) {
        this.iry = iry;
        notifyPropertyChanged(BR.iry);
    }

    @Bindable
    public String  getAccgyro() {
        if (accgyro) {
            return "ON";
        }
        return "OFF";
    }

    private void setAccgyro(boolean accgyro) {
        this.accgyro = accgyro;
        notifyPropertyChanged(BR.accgyro);
    }

    @Bindable
    public String getTimestamp() {
        if (timestamp) {
            return "ON";
        }
        return "OFF";
    }

    private void setTimestamp(boolean timestamp) {
        this.timestamp = timestamp;
        notifyPropertyChanged(BR.timestamp);
    }

    @Bindable
    public String getReport() {
        return report;
    }

    private void setReport(String report) {
        this.report = report;
        notifyPropertyChanged(BR.report);
    }

    @Bindable
    public String getSsid() {
        return ssid;
    }

    private void setSsid(String ssid) {
        this.ssid = ssid;
        notifyPropertyChanged(BR.ssid);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    private void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    @Bindable
    public String getProtocol() {
        return protocol;
    }

    private void setProtocol(String protocol) {
        this.protocol = protocol;
        notifyPropertyChanged(BR.protocol);
    }

    @Bindable
    public String getPort() {
        return port;
    }

    private void setPort(String port) {
        this.port = port;
        notifyPropertyChanged(BR.port);
    }

    @Bindable
    public String getTime() {
        return time;
    }

    private void setTime(String time) {
//                        command = "SET_TIME:" + new SimpleDateFormat("yyyy:MM:dd:hh:mm:ss").format(new Date()).toString();
//        new SimpleDateFormat("yyyy.MM.dd").format(date);
        this.time = time;
        notifyPropertyChanged(BR.time);
    }

    private String boolToString(boolean bool) {
        if (bool) {
            return 1 + "";
        } else {
            return 0 + "";
        }
    }

//    private class Commander {
//        private BluetoothLEService mBluetoothLEService;
//
//        public Commander(BluetoothLEService mService) {
//            if (mBluetoothLEService == null) {
//                mBluetoothLEService = mService;
//            }
//
//            if (mBluetoothLEService != null) {
//                sendCommand(QUERY);
//            }
//        }
//
//        private boolean sendCommand(String command) {
//            boolean result = mBluetoothLEService.sendCommand(command + ":");
//
//            if (result && !command.equals(QUERY)) {
//                return mBluetoothLEService.sendCommand(QUERY + ":");
//            }
//
//            return result;
//        }
//    }

    private enum Command {
        QUERY("QUERY:"), FREQUENCY("FREQUENCY:"), LED("LED:"), TARGET("TARGET:"),
        REPORT_TO("REPORT_TO:"), WIFI("WIFI:"), PROTOCOL("PROTOCOL:"), SET_TIME("SET_TIME:"),
        REBOOT("REBOOT:"), START("START:"), RESUME("RESUME"), STOP("STOP");

        private String header;

        Command(String header) {
            this.header = header;
        }

        private String makeCommand(String body) {
            return header + body + ":";
        }
    }

    interface ValueManagerListener {
        void onBLEResponseReceived(String response);

        void onBLEServiceConnected();
    }
}
