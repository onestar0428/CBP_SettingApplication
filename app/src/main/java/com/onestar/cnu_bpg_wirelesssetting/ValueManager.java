package com.onestar.cnu_bpg_wirelesssetting;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;

public class ValueManager extends BaseObservable {
    private SettingsActivityListener settingsActivityListener;
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
    //TODO: delete listener after verifying DataBinding works well
    public ValueManager(SettingsActivityListener listener) {
        settingsActivityListener = listener;
    }

    public void update(String key, String value) {
        //TODO: Consider how to reduce code lines
        String newValue = "";
        if (!key.equals("")) {
            switch (key) {
                case "Pulse":
                    value = value.replaceAll("Hz", "").replaceAll("\n", "");
                    setFreq(value);
                    newValue = getFreq();
                    settingsActivityListener.onValueUpdated(key, getFreq());
                    break;
                case "RED":
                    value = value.replaceAll("mA", "").replaceAll("\n", "");
                    setRed(value);
                    newValue = getRed();
                    settingsActivityListener.onValueUpdated(key, getRed());
                    break;
                case "GRN":
                    value = value.replaceAll("mA", "").replaceAll("\n", "");
                    setGreen(value);
                    newValue = getGreen();
                    settingsActivityListener.onValueUpdated(key, getGreen());
                    break;
                case "BLU":
                    value = value.replaceAll("mA", "").replaceAll("\n", "");
                    setBlue(value);
                    newValue = getBlue();
                    settingsActivityListener.onValueUpdated(key, getBlue());
                    break;
                case "YEL":
                    value = value.replaceAll("mA", "").replaceAll("\n", "");
                    setYellow(value);
                    newValue = getYellow();
                    settingsActivityListener.onValueUpdated(key, getYellow());
                    break;
                case "IR":
                    value = value.replaceAll("mA", "").replaceAll("\n", "");
                    setIr(value);
                    newValue = getIr();
                    settingsActivityListener.onValueUpdated(key, getIr());
                    break;
                case "Pressure_1st":
                    setPressure1(stringToBool(value));
                    newValue = getPressure1();
                    settingsActivityListener.onValueUpdated(key, getPressure1());
                    break;
                case "Pressure_2nd":
                    setPressure2(stringToBool(value));
                    newValue = getPressure2();
                    settingsActivityListener.onValueUpdated(key, getPressure2());
                    break;
                case "Optical_RGB":
                    setRgb(stringToBool(value));
                    newValue = getRgb();
                    settingsActivityListener.onValueUpdated(key, getRgb());
                    break;
                case "Optical_IrY":
                    setIry(stringToBool(value));
                    newValue = getIry();
                    settingsActivityListener.onValueUpdated(key, getIry());
                    break;
                case "Acc/Gyro":
                    setAccgyro(stringToBool(value));
                    newValue = getAccgyro();
                    settingsActivityListener.onValueUpdated(key, getAccgyro());
                    break;
                case "Include":
                    setTimestamp(stringToBool(value));
                    newValue = getTimestamp();
                    settingsActivityListener.onValueUpdated(key, getTimestamp());
                    break;
                case "Report":
                    setReport(value);
                    newValue = getReport();
                    setReport(value.replaceAll("\n", ""));
                    settingsActivityListener.onValueUpdated(key, getReport());
                    break;
                case "Current":
                    setTime(value);
                    newValue = getTime();
                    setTime(value.replaceAll("\n", ""));
                    settingsActivityListener.onValueUpdated(key, getTime());
                    break;
                case "UDP/TCP":
                    setProtocol(value);
                    newValue = getProtocol();
                    setProtocol(value.replaceAll("\n", ""));
                    settingsActivityListener.onValueUpdated(key, getProtocol());
                    break;
                case "Port":
                    setPort(value);
                    newValue = getPort();
                    setPort(value.replaceAll("\n", ""));
                    settingsActivityListener.onValueUpdated(key, getPort());
                    break;
            }
        }
        settingsActivityListener.onValueUpdated(key, newValue);
    }

    private String stringToBool(String value) {
        value = value.replaceAll("\n", "");
        if (value.equals("Yes")) {
            return "ON";
        } else {
            return "OFF";
        }
    }

    private String boolToString(String bool) {
        if (bool.equals("true")) {
            return "ON";
        } else {
            return "OFF";
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
        return boolToString(pressure1);
    }

    private void setPressure1(String pressure1) {
        this.pressure1 = pressure1;
        notifyPropertyChanged(BR.pressure1);
    }

    @Bindable
    public String getPressure2() {
        return boolToString(pressure2);
    }

    private void setPressure2(String pressure2) {
        this.pressure2 = pressure2;
        notifyPropertyChanged(BR.pressure2);
    }

    @Bindable
    public String getRgb() {
        return boolToString(rgb);
    }

    private void setRgb(String rgb) {
        this.rgb = rgb;
        notifyPropertyChanged(BR.rgb);
    }

    @Bindable
    public String getIry() {
        return boolToString(iry);
    }

    public void setIry(String iry) {
        this.iry = iry;
        notifyPropertyChanged(BR.iry);
    }

    @Bindable
    public String getAccgyro() {
        return boolToString(accgyro);
    }

    private void setAccgyro(String accgyro) {
        this.accgyro = accgyro;
        notifyPropertyChanged(BR.accgyro);
    }

    @Bindable
    public String getTimestamp() {
        return boolToString(timestamp);
    }

    private void setTimestamp(String timestamp) {
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

    interface ValueManagerListener {
        void onBLEResponseReceived(String response);

        void onBLEServiceConnected();
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

