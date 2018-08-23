package com.onestar.cnu_bpg_wirelesssetting;

import android.content.Context;

public class ValueManager {
    private static ValueManager.Commander mCommander;
    private static Context mContext;

    private final static int DEFAULT_RED = 50, DEFAULT_BLUE = 50, DEFAULT_GREEN = 50, DEFAULT_YELLOW = 50, DEFAULT_IR = 50;
    private final static int DEFAULT_FREQ = 100, DEFAULT_DELAY = 0;
    private final static boolean DEFAULT_BOOL = false;
    private final static String DEFAULT_REPORT = "console", DEFAULT_STRING = "", DEFAULT_TIME = "2017:01:01:00:01:59";

    private int freq = DEFAULT_FREQ,
            red = DEFAULT_RED, blue = DEFAULT_BLUE, green = DEFAULT_GREEN, yellow = DEFAULT_YELLOW, ir = DEFAULT_IR,
            delay = DEFAULT_DELAY;
    private boolean pressure1 = DEFAULT_BOOL, pressure2 = DEFAULT_BOOL,
            rgb = DEFAULT_BOOL, iry = DEFAULT_BOOL, accgyro = DEFAULT_BOOL, timestamp = DEFAULT_BOOL;
    private String report = DEFAULT_REPORT,
            ssid = DEFAULT_STRING, password = DEFAULT_STRING,
            protocol = DEFAULT_STRING, port = DEFAULT_STRING,
            time = DEFAULT_TIME;


    public ValueManager(Context context, BluetoothLEService service) {
        mContext = context;
        mCommander = new ValueManager.Commander(service);
    }

    public void update(String response) {
        parseResponse(response);
    }

    private void parseResponse(String response) {
        String parseAsterisk = response.replace(" * ", "");
        String key = parseAsterisk.split(":")[0].replace(" ", "");
        String value = parseAsterisk.split(":")[1].replace(" ", "");

        updateValue(key, value);
    }

    private void updateValue(String key, String value) {
        if (!key.equals("")) {
            switch (key) {
                case "Pulse Rep. freq.":
                    value = value.replaceAll("Hz", "");
                    setFreq(Integer.parseInt(value));
                    break;
                case "RED Current":
                    value = value.replaceAll("mA", "");
                    setRed(Integer.parseInt(value));
                    break;
                case "GRN Current":
                    value = value.replaceAll("mA", "");
                    setGreen(Integer.parseInt(value));
                    break;
                case "BLU Current":
                    value = value.replaceAll("mA", "");
                    setBlue(Integer.parseInt(value));
                    break;
                case "YEL Current":
                    value = value.replaceAll("mA", "");
                    setYellow(Integer.parseInt(value));
                    break;
                case "IR  Current":
                    value = value.replaceAll("mA", "");
                    setIr(Integer.parseInt(value));
                    break;
                case "Pressure_1st Measurement":
                    setPressure1(stringToBool(value));
                    break;
                case "Pressure_2nd Measurement":
                    setPressure2(stringToBool(value));
                    break;
                case "Optical_RGB Measurement":
                    setRgb(stringToBool(value));
                    break;
                case "Optical_IrY Measurement":
                    setIry(stringToBool(value));
                    break;
                case "Acc/Gyro Measurement":
                    setAccgyro(stringToBool(value));
                    break;
                case "Include TimeStamp":
                    setTimestamp(stringToBool(value));
                    break;
                case "Report to Console or NW":
                    // report = value;
                    break;
                case "Current Time":
                    setTime(value);
                    break;
                case "UDP/TCP":
                    setProtocol(value);
                    break;
                case "Port No.":
                    setPort(value);
                    break;
            }
        }
    }

    public boolean setValues(String key, String... args) {
        boolean result = false;

        if (key.equals(mContext.getResources().getString(R.string.freq))) {
            result = mCommander.freqCommand(args[0]);
        }
        else if (key.equals(mContext.getResources().getString(R.string.led))) {
            result = mCommander.ledCommand(args[0], args[1], args[2], args[3], args[4]);
        }
        else if (key.equals(mContext.getResources().getString(R.string.target))) {
            result = mCommander.targetCommand(args[0], args[1], args[2], args[3], args[4], args[5]);
        }
        else if (key.equals(mContext.getResources().getString(R.string.report_to))) {
            result = mCommander.reportCommand(args[0]);
        }
        else if (key.equals(mContext.getResources().getString(R.string.wifi))) {
            result = mCommander.wifiCommand(args[0], args[1]);
        }
        else if (key.equals(mContext.getResources().getString(R.string.protocol))) {
            result = mCommander.protocolCommand(args[0], args[1]);
        }
        else if (key.equals(mContext.getResources().getString(R.string.set_time))) {
            result = mCommander.setTimeCommand(args[0]);
        }
        else if (key.equals(mContext.getResources().getString(R.string.wifi))) {
            result = mCommander.wifiCommand(args[0], args[1]);
        }
        else if (key.equals(mContext.getResources().getString(R.string.reboot))) {
            result = mCommander.rebootCommand(args[0]);
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

    public int getFreq() {
        return freq;
    }

    private void setFreq(int freq){
        this.freq = freq;
    }

    public int getRed() {
        return red;
    }

    private void setRed(int red) {
        this.red = red;
    }

    public int getBlue() {
        return blue;
    }

    private void setBlue(int blue) {
        this.blue = blue;
    }

    public int getGreen() {
        return green;
    }

    private void setGreen(int green) {
        this.green = green;
    }

    public int getYellow() {
        return yellow;
    }

    private void setYellow(int yellow) {
        this.yellow = yellow;
    }

    public int getIr() {
        return ir;
    }

    private void setIr(int ir) {
        this.ir = ir;
    }

    public int getDelay() {
        return delay;
    }

    private void setDelay(int delay) {
        this.delay = delay;
    }

    public boolean isPressure1() {
        return pressure1;
    }

    private void setPressure1(boolean pressure1) {
        this.pressure1 = pressure1;
    }

    public boolean isPressure2() {
        return pressure2;
    }

    private void setPressure2(boolean pressure2) {
        this.pressure2 = pressure2;
    }

    public boolean isRgb() {
        return rgb;
    }

    private void setRgb(boolean rgb) {
        this.rgb = rgb;
    }

    public boolean isIry() {
        return iry;
    }

    private void setIry(boolean iry) {
        this.iry = iry;
    }

    public boolean isAccgyro() {
        return accgyro;
    }

    private void setAccgyro(boolean accgyro) {
        this.accgyro = accgyro;
    }

    public boolean isTimestamp() {
        return timestamp;
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

    private class Commander {
        private BluetoothLEService mBluetoothLEService;

        private final static String QUERY = "QUERY";
        private final static String FREQUENCY = "FREQUENCY:";
        private final static String LED = "LED:";
        private final static String TARGET = "TARGET:";
        private final static String REPORT_TO = "REPORT_TO:";
        private final static String WIFI = "WIFI:";
        private final static String PROTOCOL = "PROTOCOL:";
        private final static String SET_TIME = "SET_TIME:";
        private final static String REBOOT = "REBOOT:";

        private final static String START = "STAR:";
        private final static String RESUME = "RESUME";
        private final static String STOP = "STOP";


        public Commander(BluetoothLEService mService) {
            if (mBluetoothLEService == null) {
                mBluetoothLEService = mService;
            }

            if (mBluetoothLEService != null) {
                queryCommand();
            }
        }

        private boolean sendCommand(String command) {
            boolean result = mBluetoothLEService.sendCommand(command + ":");

            if (result) {
                return queryCommand();
            }

            return result;
        }

        public boolean queryCommand() {
            return sendCommand(QUERY);
        }

        private boolean freqCommand(String freq) {
            return sendCommand(FREQUENCY + freq);
        }

        private boolean ledCommand(String red, String green, String blue, String yellow, String ir) {
            return sendCommand(LED + red + ":" + green + ":" + blue + ":" + yellow + ":" + ir);
        }

        private boolean targetCommand(String p1, String p2, String rgb, String iry, String accgyro, String timestamp) {
            return sendCommand(TARGET + p1 + ":" + p2 + ":" + rgb + ":" + iry + ":" + accgyro + ":" + timestamp);
        }

        private boolean reportCommand(String report){
            return sendCommand(REPORT_TO + report);
        }

        private boolean wifiCommand(String ssid, String password) {
            return sendCommand(PROTOCOL + ssid + ":" + password);
        }

        private boolean protocolCommand(String protocol, String port) {
            return sendCommand(PROTOCOL + protocol + ":" + port);
        }

        private boolean setTimeCommand(String time){
            return sendCommand(SET_TIME + time);
        }

        private boolean rebootCommand(String delay){
            return sendCommand(REBOOT + delay);
        }

        private String boolToString(boolean bool) {
            if (bool) {
                return 1 + "";
            } else {
                return 0 + "";
            }
        }
    }

    interface ValueManagerListener {
        void onBLEResponseReceived(String response);
    }
}
