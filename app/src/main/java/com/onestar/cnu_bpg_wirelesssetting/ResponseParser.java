package com.onestar.cnu_bpg_wirelesssetting;

public class ResponseParser {
    //TODO: StringBuilder?
    private String key, value;
    private boolean keyFlag = false, valueFlag = false;

    ResponseParser() {
        key = "";
        value = "";
    }

    public void parse(String response) {
        // TODO: FUCKING PARSING
        // TODO: Some commands don't receive their response, like FREQUENCY..
        // So unify the way of updating values
        // by using response of QUERY (necessary to send it after sending command)

        if (response.startsWith(" * ")) {
            parseQuery(response);
        }
    }

    //*********  CNU BPG v1.5 (Jul.31, 2018)  *********
    //************  Configuration Summary  ************
    //-------------------------------------------------
    // * Pulse Rep. freq. :   100 Hz
    // * RED Current      :  11.96 mA
    // * GRN Current      :  49.98 mA
    // * BLU Current      :  11.96 mA
    // * YEL Current      :  49.98 mA
    // * IR  Current      :  10.00 mA
    //-------------------------------------------------
    // * Pressure_1st Measurement : Yes
    // * Pressure_2nd Measurement : Yes
    // * Optical_RGB Measurement  : Yes
    // * Optical_IrY Measurement  : No
    // * Acc/Gyro Measurement     : Yes
    // * Include TimeStamp        : Yes
    // * Report to Console or NW  : Console only
    //-------------------------------------------------
    // * Current Time : Sun, 01 Jan. 2017, 00:00:02
    //-------------------------------------------------
    // * Currently any AP is NOT connected yet.
    // * UDP/TCP  :  UDP
    // * Port No. :  500
    //-------------------------------------------------
    private void parseQuery(String response) {
        String parseAsterisk = response.replace(" * ", "");
        key = parseAsterisk.split(":")[0].split(" ")[0];
        value = parseAsterisk.split(":")[1].replace(" ", "");
    }

    //!! New current for LED_RED     :
    //  0.00 mA
    //!! New current for LED_GREEN   :
    //  0.00 mA
    //!! New current for LED_BLUE    :
    //  0.00 mA
    // !! New current for LED_YELLOW :
    //  0.00 mA
    //!! New current for LED_IR      :
    //  0.00 mA

    //!! Pressure_1st Measurement is
    //ON.
    //!! Pressure_2nd Measurement is
    //OFF.
    //!! Optical_RGB Measurement is
    //ON.
    //!! Optical_IrY Measurement is
    //OFF.
    //!! Accel/Gyro Measurement is
    //OFF.
    //!! Time Stamp is
    //NOT included.
    private void parseResponse(String response){
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
