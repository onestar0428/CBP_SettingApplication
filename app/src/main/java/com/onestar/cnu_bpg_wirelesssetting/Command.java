package com.onestar.cnu_bpg_wirelesssetting;

public enum Command {
    QUERY("QUERY:"), FREQUENCY("FREQUENCY:"), LED("LED:"), TARGET("TARGET:"),
    REPORT_TO("REPORT_TO:"), WIFI("WIFI:"), PROTOCOL("PROTOCOL:"), SET_TIME("SET_TIME:"),
    REBOOT("REBOOT:"), START("START:"), RESUME("RESUME"), STOP("STOP");

    public String header;

    Command(String header) {
        this.header = header;
    }

    public String makeCommand(String body) {
        return header + body + ":";
    }
}


