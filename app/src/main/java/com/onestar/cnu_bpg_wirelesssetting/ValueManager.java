package com.onestar.cnu_bpg_wirelesssetting;

import android.content.Context;

public class ValueManager {
//    private static ValueManager.Commander mCommander;
    private static Context mContext;
    private SettingsActivityListener settingsActivityListener;
    private BluetoothLEService mBluetoothLEService;

    private enum Default{
        DEFAULT_LED(50), DEFAULT_FREQ(100), DEFAULT_DELAY(0),
        DEFAULT_BOOL(false),
        DEFAULT_REPORT("console"), DEFAULT_STRING(""), DEFAULT_TIME("2017:01:01:00:01:59");

        //TODO: right member variable?
        private int defaultInt;
        private boolean defaultBoolean;
        private String defaultString;

        Default(int value){
            this.defaultInt = value;
        }
        Default(boolean value){
            this.defaultBoolean = value;
        }
        Default(String value){
            this.defaultString = value;
        }
    }

    //TODO: try to transform values into ENUM with setter
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

    //TODO: static factory or singleton
    public ValueManager(Context context, BluetoothLEService service, SettingsActivityListener listener) {
        mContext = context;
//        mCommander = new ValueManager.Commander(service);
        mBluetoothLEService = service;
        settingsActivityListener = listener;

        sendCommand(Command.QUERY.header);
    }

    public void update(String response) {
        parseResponse(response);
    }

    private void parseResponse(String response) {
        String parseAsterisk = response.replace(" * ", "");
        String key = parseAsterisk.split(":")[0].split(" ")[0];
        String value = parseAsterisk.split(":")[1].replace(" ", "");

        updateValue(key, value);
    }

    private void updateValue(String key, String value) {
        String newValue = "";

        if (!key.equals("")) {
            switch (key) {
                case "Pulse":
                    value = value.replaceAll("Hz", "");
                    setFreq(Integer.parseInt(value));
                    newValue = getFreq();
                    break;
                case "RED":
                    value = value.replaceAll("mA", "");
                    setRed(Integer.parseInt(value));
                    newValue = getRed();
                    break;
                case "GRN":
                    value = value.replaceAll("mA", "");
                    setGreen(Integer.parseInt(value));
                    newValue = getGreen();
                    break;
                case "BLU":
                    value = value.replaceAll("mA", "");
                    setBlue(Integer.parseInt(value));
                    newValue = getBlue();
                    break;
                case "YEL":
                    value = value.replaceAll("mA", "");
                    setYellow(Integer.parseInt(value));
                    newValue = getYellow();
                    break;
                case "IR":
                    value = value.replaceAll("mA", "");
                    setIr(Integer.parseInt(value));
                    newValue = getIr();
                    break;
                case "Pressure_1st":
                    setPressure1(stringToBool(value));
                    newValue = isPressure1();
                    break;
                case "Pressure_2nd":
                    setPressure2(stringToBool(value));
                    newValue = isPressure2();
                    break;
                case "Optical_RGB":
                    setRgb(stringToBool(value));
                    newValue = isRgb();
                    break;
                case "Optical_IrY":
                    setIry(stringToBool(value));
                    newValue = isIry();
                    break;
                case "Acc/Gyro":
                    setAccgyro(stringToBool(value));
                    newValue = isAccgyro();
                    break;
                case "Include":
                    setTimestamp(stringToBool(value));
                    newValue = isTimestamp();
                    break;
                case "Report":
                    setReport(value);
                    newValue = getReport();
                    break;
                case "Current":
                    setTime(value);
                    newValue = getTime();
                    break;
                case "UDP/TCP":
                    setProtocol(value);
                    newValue = getProtocol();
                    break;
                case "Port":
                    setPort(value);
                    newValue = getPort();
                    break;
            }
        }
        settingsActivityListener.onValueUpdated(key, newValue);
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

    private boolean sendCommand(String command){
        //TODO: Handle about QUERY more efficient
        //send command
        if (!command.equals("")) {
            boolean result = mBluetoothLEService.sendCommand(command + ":");

            if (result && !command.startsWith(Command.QUERY.header)) {
                return mBluetoothLEService.sendCommand(Command.QUERY.header + ":");
            }
        }
        return false;
    }

    private boolean stringToBool(String value) {
        if (value.equals("Yes")) {
            return true;
        } else {
            return false;
        }
    }

    // -----------------------------------------------------------
    // --------------------- GETTER & SETTER ---------------------
    // -----------------------------------------------------------

    public String getFreq() {
        return freq + "";
    }

    private void setFreq(int freq) {
        this.freq = freq;
    }

    public String getRed() {
        return red + "";
    }

    private void setRed(int red) {
        this.red = red;
    }

    public String getBlue() {
        return blue + "";
    }

    private void setBlue(int blue) {
        this.blue = blue;
    }

    public String getGreen() {
        return green + "";
    }

    private void setGreen(int green) {
        this.green = green;
    }

    public String getYellow() {
        return yellow + "";
    }

    private void setYellow(int yellow) {
        this.yellow = yellow;
    }

    public String getIr() {
        return ir + "";
    }

    private void setIr(int ir) {
        this.ir = ir;
    }

    public String getDelay() {
        return delay + "";
    }

    private void setDelay(int delay) {
        this.delay = delay;
    }

    public String isPressure1() {
        return boolToString(pressure1);
    }

    private void setPressure1(boolean pressure1) {
        this.pressure1 = pressure1;
    }

    public String isPressure2() {
        return boolToString(pressure2);
    }

    private void setPressure2(boolean pressure2) {
        this.pressure2 = pressure2;
    }

    public String isRgb() {
        return boolToString(rgb);
    }

    private void setRgb(boolean rgb) {
        this.rgb = rgb;
    }

    public String isIry() {
        return boolToString(iry);
    }

    private void setIry(boolean iry) {
        this.iry = iry;
    }

    public String isAccgyro() {
        return boolToString(accgyro);
    }

    private void setAccgyro(boolean accgyro) {
        this.accgyro = accgyro;
    }

    public String isTimestamp() {
        return boolToString(timestamp);
    }

    private void setTimestamp(boolean timestamp) {
        this.timestamp = timestamp;
    }

    public String getReport() {
        return report;
    }

    private void setReport(String report) {
        this.report = report;
    }

    public String getSsid() {
        return ssid;
    }

    private void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    public String getProtocol() {
        return protocol;
    }

    private void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getPort() {
        return port;
    }

    private void setPort(String port) {
        this.port = port;
    }

    public String getTime() {
        return time;
    }

    private void setTime(String time) {
//                        command = "SET_TIME:" + new SimpleDateFormat("yyyy:MM:dd:hh:mm:ss").format(new Date()).toString();
        this.time = time;
    }

    private String boolToString(boolean bool) {
        if (bool) {
            return 1 + "";
        } else {
            return 0 + "";
        }
    }

    //TODO: transform to enum
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

    private enum Command{
        QUERY("QUERY"), FREQUENCY("FREQUENCY:"), LED("LED:"), TARGET("TARGET:"),
        REPORT_TO("REPORT_TO:"), WIFI("WIFI:"), PROTOCOL("PROTOCOL:"), SET_TIME("SET_TIME:"),
        REBOOT("REBOOT:"), START("START:"), RESUME("RESUME"), STOP("STOP");

        private String header;

        Command (String header){
            this.header = header;
        }

        private String makeCommand(String body){
            return header + body + ":";
        }
    }

    interface ValueManagerListener {
        void onBLEResponseReceived(String response);
        void onBLEServiceConnected();
    }
}
