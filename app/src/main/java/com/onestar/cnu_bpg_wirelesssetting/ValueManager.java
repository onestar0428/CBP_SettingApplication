package com.onestar.cnu_bpg_wirelesssetting;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;

import com.android.databinding.library.baseAdapters.BR;

public class ValueManager extends BaseObservable {
    private final static String TAG = ValueManager.class.getSimpleName();

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

    public ValueManager(){
        Log.d(TAG, "ValueManager Constructor");
    }

    public void updateQuery(String key, String value) {
        //TODO: Consider how to refactor
        Log.d(TAG, "updateQuery" );

        if (!key.equals("")) {
            switch (key) {
                case "Pulse":
                    setFreq(value.replaceAll("Hz", "").replaceAll("\n", ""));
                    break;
                case "RED":
                    setRed(value.replaceAll("mA", "").replaceAll("\n", ""));
                    break;
                case "GRN":
                    setGreen(value.replaceAll("mA", "").replaceAll("\n", ""));
                    break;
                case "BLU":
                    setBlue(value.replaceAll("mA", "").replaceAll("\n", ""));
                    break;
                case "YEL":
                    setYellow(value.replaceAll("mA", "").replaceAll("\n", ""));
                    break;
                case "IR":
                    setIr(value.replaceAll("mA", "").replaceAll("\n", ""));
                    break;
                case "Pressure_1st":
                    setPressure1(value);
                    break;
                case "Pressure_2nd":
                    setPressure2(value);
                    break;
                case "Optical_RGB":
                    setRgb(value);
                    break;
                case "Optical_IrY":
                    setIry(value);
                    break;
                case "Acc/Gyro":
                    setAccgyro(value);
                    break;
                case "Include":
                    setTimestamp(value);
                    break;
                case "Report":
                    setReport(value.replaceAll("\n", ""));
                    break;
                case "Current":
                    setTime(value.replaceAll("\n", ""));
                    break;
                case "UDP/TCP":
                    setProtocol(value.replaceAll("\n", ""));
                    break;
                case "Port":
                    setPort(value.replaceAll("\n", ""));
                    break;
                case "Pressure_1st Measurement is":
                    break;
                case "Pressure_2nd Measurement is":
                    break;
                case "Optical_RGB Measurement is":
                    break;
                case "Optical_IrY Measurement is":
                    break;
            }
        }
    }
    public void updateResponse(String key, String value) {
        //TODO: Consider how to refactor
        Log.d(TAG, "updateResponse" );

        if (!key.equals("")) {
            switch (key) {
                // FREQUENCY: response x
                // LED: 49.59 mA
                // Target: OFF
                case "New current for LED_RED":
                    setRed(value.replace(" mA", ""));
                    break;
                case "New current for LED_GREEN":
                    setGreen(value.replace(" mA", ""));
                    break;
                case "New current for LED_BLUE":
                    setBlue(value.replace(" mA", ""));
                    break;
                case "New current for LED_YELLOW":
                    setYellow(value.replace(" mA", ""));
                    break;
                case "New current for LED_IR":
                    setIr(value.replace(" mA", ""));
                    break;
                case "Pressure_1st Measurement is":
                    setPressure1(value.replace(".", ""));
                    break;
                case "Pressure_2nd Measurement is":
                    setPressure2(value.replace(".", ""));
                    break;
                case "Optical_RGB Measurement is":
                    setRgb(value.replace(".", ""));
                    break;
                case "Optical_IrY Measurement is":
                    setIry(value.replace(".", ""));
                    break;
                case "Accel/Gyro Measurement is":
                    setAccgyro(value.replace(".", ""));
                    break;
                case "Time Stamp is"://NOT included.
                    setTimestamp(value.replace(".", ""));
                    break;

            }
        }
    }
    // -----------------------------------------------------------
    // --------------------- GETTER & SETTER ---------------------
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
        notifyPropertyChanged(BR.pressure1);
    }

    @Bindable
    public String getPressure2() {
        return pressure2;
    }

    private void setPressure2(String pressure2) {
        this.pressure2 = pressure2;
        notifyPropertyChanged(BR.pressure2);
    }

    @Bindable
    public String getRgb() {
        return rgb;
    }

    private void setRgb(String rgb) {
        this.rgb = rgb;
        notifyPropertyChanged(BR.rgb);
    }

    @Bindable
    public String getIry() {
        return iry;
    }

    public void setIry(String iry) {
        this.iry = iry;
        notifyPropertyChanged(BR.iry);
    }

    @Bindable
    public String getAccgyro() {
        return accgyro;
    }

    private void setAccgyro(String accgyro) {
        this.accgyro = accgyro;
        notifyPropertyChanged(BR.accgyro);
    }

    @Bindable
    public String getTimestamp() {
        return timestamp;
    }

    private void setTimestamp(String timestamp) {
        if(timestamp.equals("NOT included")){
            this.timestamp = "No";
        } else{
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

