package com.onestar.cnu_bpg_wirelesssetting.Enum;

public enum Command {
    START("START"), RESUME("RESUME"), STOP("STOP"), QUERY("QUERY"),
    FREQ("FREQ"), LED("LED"), TARGET("TARGET"),
    REPORT("REPORT"), PROTOCOL("PROTOCOL"), SET_TIME("SET_TIME"), WIFI("WIFI"), REBOOT("REBOOT");

    public String value="";
    public String command="";

    Command(String value){
        this.value = value;
        this.command = value + ":";
    }

}
