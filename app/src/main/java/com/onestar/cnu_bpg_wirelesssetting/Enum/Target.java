package com.onestar.cnu_bpg_wirelesssetting.Enum;

public enum Target {

    PRESSURE1("PRESSURE1"), PRESSURE2("PRESSURE2"),
    RGB("RGB"), IRY("IRY"), ACCGYRO("ACCGYRO"), TIMESTAMP("TIMESTAMP");

    public String value="";
    public boolean parseFlag;

    Target(String value){
        this.value = value;
    }

    public void setParseFlag(boolean flag) {
        parseFlag = flag;
    }
}
