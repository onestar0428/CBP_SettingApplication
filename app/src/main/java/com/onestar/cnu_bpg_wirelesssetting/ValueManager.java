package com.onestar.cnu_bpg_wirelesssetting;

import android.content.Context;

public class ValueManager {
    private static ValueManager.Commander mCommander;
    private static Context mContext;
    private static SettingsActivityListener settingsActivityListener;

    //TODO: change to ENUM
    private final static int DEFAULT_RED = 50, DEFAULT_BLUE = 50, DEFAULT_GREEN = 50, DEFAULT_YELLOW = 50, DEFAULT_IR = 50;
    private final static int DEFAULT_FREQ = 100, DEFAULT_DELAY = 0;
    private final static boolean DEFAULT_BOOL = false;
    private final static String DEFAULT_REPORT = "console", DEFAULT_STRING = "", DEFAULT_TIME = "2017:01:01:00:01:59";

    private int freq = DEFAULT_FREQ, delay = DEFAULT_DELAY,
            red = DEFAULT_RED, blue = DEFAULT_BLUE, green = DEFAULT_GREEN, yellow = DEFAULT_YELLOW, ir = DEFAULT_IR;
    private boolean pressure1 = DEFAULT_BOOL, pressure2 = DEFAULT_BOOL,
            rgb = DEFAULT_BOOL, iry = DEFAULT_BOOL, accgyro = DEFAULT_BOOL, timestamp = DEFAULT_BOOL;
    private String report = DEFAULT_REPORT,
            ssid = DEFAULT_STRING, password = DEFAULT_STRING,
            protocol = DEFAULT_STRING, port = DEFAULT_STRING,
            time = DEFAULT_TIME;

    private final static String QUERY = "QUERY";
    private final static String FREQUENCY = "FREQUENCY:";
    private final static String LED = "LED:";
    private final static String TARGET = "TARGET:";
    private final static String REPORT_TO = "REPORT_TO:";
    private final static String WIFI = "WIFI:";
    private final static String PROTOCOL = "PROTOCOL:";
    private final static String SET_TIME = "SET_TIME:";
    private final static String REBOOT = "REBOOT:";

    private final static String START = "START:";
    private final static String RESUME = "RESUME";
    private final static String STOP = "STOP";


    public ValueManager(Context context, BluetoothLEService service, SettingsActivityListener listener) {
        mContext = context;
        mCommander = new ValueManager.Commander(service);
        settingsActivityListener = listener;
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
            String header = "";

            if (key.equals(mContext.getResources().getString(R.string.freq))) {
                header = FREQUENCY;
            } else if (key.equals(mContext.getResources().getString(R.string.led))) {
                header = LED;
            } else if (key.equals(mContext.getResources().getString(R.string.target))) {
                header = TARGET;
            } else if (key.equals(mContext.getResources().getString(R.string.report_to))) {
                header = REPORT_TO;
            } else if (key.equals(mContext.getResources().getString(R.string.wifi))) {
                header = WIFI;
            } else if (key.equals(mContext.getResources().getString(R.string.protocol))) {
                header = PROTOCOL;
            } else if (key.equals(mContext.getResources().getString(R.string.set_time))) {
                header = SET_TIME;
            } else if (key.equals(mContext.getResources().getString(R.string.reboot))) {
                header = REBOOT;
            }

            if (!header.equals("")) {
                result = mCommander.sendCommand(header + params);
            }
        }
        return result;
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

    private class Commander {
        private BluetoothLEService mBluetoothLEService;

        public Commander(BluetoothLEService mService) {
            if (mBluetoothLEService == null) {
                mBluetoothLEService = mService;
            }

            if (mBluetoothLEService != null) {
                sendCommand(QUERY);
            }
        }

        private boolean sendCommand(String command) {
            boolean result = mBluetoothLEService.sendCommand(command);

            if (result && !command.equals(QUERY)) {
                return mBluetoothLEService.sendCommand(QUERY + ":");
            }

            return result;
        }

    }

    interface ValueManagerListener {
        void onBLEResponseReceived(String response);
    }
}
