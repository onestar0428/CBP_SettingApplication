package com.onestar.cnu_bpg_wirelesssetting;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;

import com.android.databinding.library.baseAdapters.BR;
import com.onestar.cnu_bpg_wirelesssetting.Enum.Command;
import com.onestar.cnu_bpg_wirelesssetting.Enum.Led;
import com.onestar.cnu_bpg_wirelesssetting.Enum.Target;

import java.util.AbstractMap;

public class ValueManager extends BaseObservable {
    private final static String TAG = ValueManager.class.getSimpleName();

    private static ResponseParser mResponseParser;

    // initialize each value into Default Enum value
    // If connection is abnormal, UI shown these Default Enum values
    private String freq = Default.DEFAULT_FREQ.value,
            delay = Default.DEFAULT_DELAY.value,
            red = Default.DEFAULT_LED.value,
            blue = Default.DEFAULT_LED.value,
            green = Default.DEFAULT_LED.value,
            yellow = Default.DEFAULT_LED.value,
            ir = Default.DEFAULT_LED.value,
            pressure1 = Default.DEFAULT_BOOL.value,
            pressure2 = Default.DEFAULT_BOOL.value,
            rgb = Default.DEFAULT_BOOL.value,
            iry = Default.DEFAULT_BOOL.value,
            accgyro = Default.DEFAULT_BOOL.value,
            timestamp = Default.DEFAULT_BOOL.value,
            report = Default.DEFAULT_REPORT.value,
            ssid = Default.DEFAULT_STRING.value,
            password = Default.DEFAULT_STRING.value,
            protocol = Default.DEFAULT_STRING.value,
            port = Default.DEFAULT_STRING.value,
            time = Default.DEFAULT_TIME.value;

    //TODO: static factory or singleton

    /**
     * Allocate ResponseParser object in constructor
     */
    public ValueManager() {
        Log.d(TAG, "ValueManager Constructor");
        if (mResponseParser == null) {
            mResponseParser = new ResponseParser();
        }
    }

    /**
     * Updates each option value according to parsed result of command response.
     * <p>
     * (CAUTION) Some setting options won't be updated even though successful sending command.
     * - FREQUENCY: get un-proper result to extract updated value
     * - WIFI: (TODO) don't know how to test about it
     * - PROTOCOL: get un-proper result to extract updated value
     *
     * @param key      command header
     * @param response response of command
     */
    public void update(String key, String response) {
        if (key.equals(Command.FREQ.value)) {
            // NO RESPONSE TO PARSE FOR FREQ COMMAND
        } else if (key.equals(Command.LED.value)) {
            updateLed(key, response);
        } else if (key.equals(Command.TARGET.value)) {
            updateTarget(key, response);
        } else if (key.equals(Command.REPORT.value)) {
            String newValue = mResponseParser.parse(key, response);

            if (!newValue.equals("")) {
                setReport(newValue);
            }
        } else if (key.equals(Command.PROTOCOL.value)) {
            String newValue = mResponseParser.parse(key, response);
            String[] value = newValue.split(":");

            if (value.length == 2) {
                setProtocol(value[0]);
                setPort(value[1]);
            } else {
                Log.d(TAG, "update(protocol) gets abnormal new value: " + newValue);
            }
        } else if (key.equals(Command.SET_TIME.value)) {
            String newValue = mResponseParser.parse(key, response);
            if (!newValue.equals("")) {
                setTime(newValue);
            }
        } else if (key.equals(Command.WIFI.value)) {
            // THERE IS NO OPTION VALUE TO PARSE OUT IN RESPONSE
            // !! Rebooting MCU to apply new AP Connection in 3 secs !!
        } else if (key.equals(Command.REBOOT.value)) {
            String newValue = mResponseParser.parse(key, response);

            if (!newValue.equals("")) {
                setReport(newValue); //response = delay
            } else {
                Log.d(TAG, "update(reboot) gets abnormal new value: " + newValue);
            }
        } else if (key.equals(Command.QUERY.value)) {
            updateQuery(response);
        }
    }

    /**
     * Update extracted value from LED response.
     * Each LED option gets 2 lines of response String.
     * e.g.) !! New current for LED_RED    :
     * 29.01 mA
     * Therefore, set flag of LED Enum true if response String starts with " !! "
     * is detected, and parse real updated value when next response line comes in.
     *
     * @param header   command header
     * @param response command response
     */
    private void updateLed(String header, String response) {
        if (response.startsWith(" !! ")) {
            response = response.replace(" !! ", "");
            if (response.contains("LED_RED")) {
                Led.RED.setParseFlag(true);
            } else if (response.contains("LED_GREEN")) {
                Led.GREEN.setParseFlag(true);
            } else if (response.contains("LED_BLUE")) {
                Led.BLUE.setParseFlag(true);
            } else if (response.contains("LED_YELLOW")) {
                Led.YELLOW.setParseFlag(true);
            } else if (response.contains("LED_IR")) {
                Led.IR.setParseFlag(true);
            }
        } else {
            if (response.contains("mA")) {
                if (Led.RED.parseFlag) {
                    setRed(mResponseParser.parse(header, response));
                    Led.RED.setParseFlag(false);
                }
                if (Led.GREEN.parseFlag) {
                    setGreen(mResponseParser.parse(header, response));
                    Led.GREEN.setParseFlag(false);
                }
                if (Led.BLUE.parseFlag) {
                    setBlue(mResponseParser.parse(header, response));
                    Led.BLUE.setParseFlag(false);
                }
                if (Led.YELLOW.parseFlag) {
                    setYellow(mResponseParser.parse(header, response));
                    Led.YELLOW.setParseFlag(false);
                }
                if (Led.IR.parseFlag) {
                    setIr(mResponseParser.parse(header, response));
                    Led.IR.setParseFlag(false);
                }
            }
        }
    }

    /**
     * Update extracted value from TARGET response.
     * Each TARGET option gets 2 lines of response String.
     * e.g.) !! Pressure_1st Measurement is
     * ON.
     * Therefore, set flag of TARGET Enum true if response String starts with " !! "
     * is detected, and parse real updated value when next response line comes in.
     *
     * @param header   command header
     * @param response command response
     */
    private void updateTarget(String header, String response) {
        if (response.startsWith("!! ")) {
            response = response.replace("!! ", "");
            // LED: 49.59 mA

            if (response.contains("Pressure_1st")) {
                Target.PRESSURE1.setParseFlag(true);
            } else if (response.contains("Pressure_2nd")) {
                Target.PRESSURE2.setParseFlag(true);
            } else if (response.contains("Optical_RGB")) {
                Target.RGB.setParseFlag(true);
            } else if (response.contains("Optical_IrY")) {
                Target.IRY.setParseFlag(true);
            } else if (response.contains("Accel/Gyro")) {
                Target.ACCGYRO.setParseFlag(true);
            } else if (response.contains("Stamp")) {
                Target.TIMESTAMP.setParseFlag(true);
            }
            Log.d(TAG, "TARGET:" + response + "," + Target.RGB.parseFlag);
        } else {
            if (response.contains(".")) {
                if (Target.PRESSURE1.parseFlag) {
                    setPressure1(mResponseParser.parse(header, response));
                    Target.PRESSURE1.setParseFlag(false);
                }
                if (Target.PRESSURE2.parseFlag) {
                    setPressure2(mResponseParser.parse(header, response));
                    Target.PRESSURE2.setParseFlag(false);
                }
                if (Target.RGB.parseFlag) {
                    setRgb(mResponseParser.parse(header, response));
                    Target.RGB.setParseFlag(false);
                }
                if (Target.IRY.parseFlag) {
                    setIry(mResponseParser.parse(header, response));
                    Target.IRY.setParseFlag(false);
                }
                if (Target.ACCGYRO.parseFlag) {
                    setAccgyro(mResponseParser.parse(header, response));
                    Target.ACCGYRO.setParseFlag(false);
                }
                if (Target.TIMESTAMP.parseFlag) {
                    setTimestamp(mResponseParser.parse(header, response));
                    Target.TIMESTAMP.setParseFlag(false);
                }
            }
        }
    }

    /**
     * Update extracted value from TARGET response.
     * Each option which contains value starts with " * " and contains " : ".
     * e.g.) * Pulse Rep. freq. :   100 Hz
     * Therefore, remove " * " and divide into key and value with " : ", and then set each option.
     *
     * @param response
     */
    private void updateQuery(String response) {

        AbstractMap.SimpleEntry<String, String> pair = mResponseParser.parseQuery(response);
        String responseKey = pair.getKey();
        String responseValue = pair.getValue();

        if (!responseKey.equals("")) {
            switch (responseKey) {
                case "Pulse":
                    setFreq(responseValue.replaceAll("Hz", "").replaceAll("\n", ""));
                    break;
                case "RED":
                    setRed(responseValue.replaceAll("mA", "").replaceAll("\n", ""));
                    break;
                case "GRN":
                    setGreen(responseValue.replaceAll("mA", "").replaceAll("\n", ""));
                    break;
                case "BLU":
                    setBlue(responseValue.replaceAll("mA", "").replaceAll("\n", ""));
                    break;
                case "YEL":
                    setYellow(responseValue.replaceAll("mA", "").replaceAll("\n", ""));
                    break;
                case "IR":
                    setIr(responseValue.replaceAll("mA", "").replaceAll("\n", ""));
                    break;
                case "Pressure_1st":
                    setPressure1(responseValue);
                    break;
                case "Pressure_2nd":
                    setPressure2(responseValue);
                    break;
                case "Optical_RGB":
                    setRgb(responseValue);
                    break;
                case "Optical_IrY":
                    setIry(responseValue);
                    break;
                case "Acc/Gyro":
                    setAccgyro(responseValue);
                    break;
                case "Include":
                    setTimestamp(responseValue);
                    break;
                case "Report":
                    setReport(responseValue.replaceAll("\n", ""));
                    break;
                case "Current":
                    setTime(responseValue.replaceAll("\n", ""));
                    break;
                case "UDP/TCP":
                    setProtocol(responseValue.replaceAll("\n", ""));
                    break;
                case "Port":
                    setPort(responseValue.replaceAll("\n", ""));
                    break;
            }
        }
    }

    // -----------------------------------------------------------
    // --------------- GETTER & SETTER for Binding ---------------
    // -----------------------------------------------------------

    @Bindable
    public String getFreq() {
        return freq;
    }

    private void setFreq(String freq) {
        this.freq = freq;
        notifyPropertyChanged(BR.freq);
    }

    @Bindable
    public String getRed() {
        return red;
    }

    private void setRed(String red) {
        this.red = red;
        notifyPropertyChanged(BR.red);
    }

    @Bindable
    public String getBlue() {
        return blue + "";
    }

    private void setBlue(String blue) {
        this.blue = blue;
        notifyPropertyChanged(BR.blue);
    }

    @Bindable
    public String getGreen() {
        return green + "";
    }

    private void setGreen(String green) {
        this.green = green;
        notifyPropertyChanged(BR.green);
    }

    @Bindable
    public String getYellow() {
        return yellow + "";
    }

    private void setYellow(String yellow) {
        this.yellow = yellow;
        notifyPropertyChanged(BR.yellow);
    }

    @Bindable
    public String getIr() {
        return ir + "";
    }

    private void setIr(String ir) {
        this.ir = ir;
        notifyPropertyChanged(BR.ir);
    }

    @Bindable
    public String getDelay() {
        return delay + "";
    }

    private void setDelay(String delay) {
        this.delay = delay;
        notifyPropertyChanged(BR.delay);
    }

    @Bindable
    public String getPressure1() {
        return pressure1;
    }

    private void setPressure1(String pressure1) {
        this.pressure1 = pressure1;

        if (pressure1.equals("ON")) {
            this.pressure1 = "Yes";
        } else if (pressure1.equals("OFF")) {
            this.pressure1 = "No";
        }
        notifyPropertyChanged(BR.pressure1);
    }

    @Bindable
    public String getPressure2() {
        return pressure2;
    }

    private void setPressure2(String pressure2) {
        this.pressure2 = pressure2;

        if (pressure2.equals("ON")) {
            this.pressure2 = "Yes";
        } else if (pressure2.equals("OFF")) {
            this.pressure2 = "No";
        }
        notifyPropertyChanged(BR.pressure2);
    }

    @Bindable
    public String getRgb() {
        return rgb;
    }

    private void setRgb(String rgb) {
        this.rgb = rgb;

        if (rgb.equals("ON")) {
            this.rgb = "Yes";
        } else if (rgb.equals("OFF")) {
            this.rgb = "No";
        }
        notifyPropertyChanged(BR.rgb);
    }

    @Bindable
    public String getIry() {
        return iry;
    }

    public void setIry(String iry) {
        this.iry = iry;

        if (iry.equals("ON")) {
            this.iry = "Yes";
        } else if (iry.equals("OFF")) {
            this.iry = "No";
        }
        notifyPropertyChanged(BR.iry);
    }

    @Bindable
    public String getAccgyro() {
        return accgyro;
    }

    private void setAccgyro(String accgyro) {
        this.accgyro = accgyro;

        if (accgyro.equals("ON")) {
            this.accgyro = "Yes";
        } else if (accgyro.equals("OFF")) {
            this.accgyro = "No";
        }
        notifyPropertyChanged(BR.accgyro);
    }

    @Bindable
    public String getTimestamp() {
        return timestamp;
    }

    private void setTimestamp(String timestamp) {
        if (timestamp.equals("NOT included")) {
            this.timestamp = "No";
        } else {
            this.timestamp = "Yes";
        }
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
        // SET DATA EX: 01 Jan. 2017, 00:03:55
        // Just set the response as displaying data

        this.time = time;
        notifyPropertyChanged(BR.time);
    }

    private enum Default {
        DEFAULT_LED("50"), DEFAULT_FREQ("100"), DEFAULT_DELAY("0"),
        DEFAULT_BOOL("false"),
        DEFAULT_REPORT("console"), DEFAULT_STRING(""), DEFAULT_TIME("2017:01:01:00:01:59");

        private String value;

        Default(String value) {
            this.value = value;
        }
    }
}

