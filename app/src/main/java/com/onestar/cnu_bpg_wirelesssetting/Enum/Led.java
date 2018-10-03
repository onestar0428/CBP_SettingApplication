package com.onestar.cnu_bpg_wirelesssetting.Enum;

public enum Led {
    RED("RED"), GREEN("GREEN"), BLUE("BLUE"), YELLOW("YELLOW"), IR("IR");

    public String value = "";
    public boolean parseFlag;

    Led(String value) {
        this.value = value;
        parseFlag = false;
    }

    public void setParseFlag(boolean flag) {
        parseFlag = flag;
    }
}
