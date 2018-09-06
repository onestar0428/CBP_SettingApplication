package com.onestar.cnu_bpg_wirelesssetting;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;

public class ValueManager extends BaseObservable {
    private SettingsActivityListener settingsActivityListener;
    private String freq = Value.FREQ.value,
            delay = Value.DELAY.value,
            red = Value.RED.value,
            blue = Value.BLUE.value,
            green = Value.GREEN.value,
            yellow = Value.YELLOW.value,
            ir = Value.IR.value,
            pressure1 = Value.PRESSURE1.value,
            pressure2 = Value.PRESSURE2.value,
            rgb = Value.RGB.value,
            iry = Value.IRY.value,
            accgyro = Value.ACCGYRO.value,
            timestamp = Value.TIMESTAMP.value,
            report = Value.REPORT.value,
            ssid = Value.SSID.value,
            password = Value.PASSWORD.value,
            protocol = Value.PROTOCOL.value,
            port = Value.PORT.value,
            time = Value.TIME.value;

    //TODO: static factory or singleton
    public ValueManager(SettingsActivityListener listener) {
        settingsActivityListener = listener;
    }

    public void update(String key, String value) {

    }

//    private boolean stringToBool(String value) {
//        value = value.replaceAll("\n", "");
//        if (value.equals("Yes")) {
//            return true;
//        } else {
//            return false;
//        }
//    }

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

    //TODO: change method name
    private String boolToString(String bool) {
        if (bool.equals("true")) {
            return "ON";
        } else {
            return "OFF";
        }
    }

    interface ValueManagerListener {
        void onBLEResponseReceived(String response);

        void onBLEServiceConnected();
    }
}
