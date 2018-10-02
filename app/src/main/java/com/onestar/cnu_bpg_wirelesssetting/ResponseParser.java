package com.onestar.cnu_bpg_wirelesssetting;

import android.content.Context;

import java.util.AbstractMap;

public class ResponseParser {

    private static Context mContext;

    public ResponseParser(Context context) {
        mContext = context;
    }

    public static String parse(String key, String response) {
        String newValue = "";

        if (key.equals(mContext.getResources().getString(R.string.freq))) {
            // NO RESPONSE TO PARSE FOR FREQ COMMAND
        } else if (key.equals(mContext.getResources().getString(R.string.led))) {
            newValue = parseLED(response);
        } else if (key.equals(mContext.getResources().getString(R.string.target))) {
            newValue = parseTarget(response);
        } else if (key.equals(mContext.getResources().getString(R.string.report_to))) {
            newValue = parseReport(response);
        } else if (key.equals(mContext.getResources().getString(R.string.protocol))) {
            newValue = parseProtocol(response);
        } else if (key.equals(mContext.getResources().getString(R.string.set_time))) {
            // NO RESPONSE TO PARSE FOR FREQ COMMAND
        } else if (key.equals(mContext.getResources().getString(R.string.wifi))) {
            // NO RESPONSE TO PARSE FOR FREQ COMMAND
        } else if (key.equals(mContext.getResources().getString(R.string.reboot))) {
            newValue = parseReboot(response);
        }

        return newValue;
    }

    private static String parseLED(String response) {
        String red = "", green = "", blue = "", yellow = "", ir = "";

        //TODO
        switch (response) {
            // LED: 49.59 mA
            case "New current for LED_RED":
                //setRed(value.replace(" mA", ""));
                break;
            case "New current for LED_GREEN":
                //setGreen(value.replace(" mA", ""));
                break;
            case "New current for LED_BLUE":
                //setBlue(value.replace(" mA", ""));
                break;
            case "New current for LED_YELLOW":
                //setYellow(value.replace(" mA", ""));
                break;
            case "New current for LED_IR":
                //setIr(value.replace(" mA", ""));
                break;
        }


        return red + ":" + green + ":" + blue + ":" + yellow + ":" + ir;
    }

    private static String parseTarget(String response) {
        String pressure1="", pressure2="", rgb="", iry="", timestamp="";

        //TODO
        switch (response) {
            // Target: OFF
            case "Pressure_1st Measurement is":
//                setPressure1(value.replace(".", ""));
                break;
            case "Pressure_2nd Measurement is":
//                setPressure2(value.replace(".", ""));
                break;
            case "Optical_RGB Measurement is":
//                setRgb(value.replace(".", ""));
                break;
            case "Optical_IrY Measurement is":
//                setIry(value.replace(".", ""));
                break;
            case "Accel/Gyro Measurement is":
//                setAccgyro(value.replace(".", ""));
                break;
            case "Time Stamp is"://NOT included.
//                setTimestamp(value.replace(".", ""));
                break;
        }

        return pressure1 + ":" + pressure2 + ":" + rgb + ":" + iry + ":" + timestamp;
    }

    public static AbstractMap.SimpleEntry<String, String> parseQuery(String response) {
        String key = "", value="";

        if (response.startsWith(" * ")) {
            String[] parseAsterisk = response.replace(" * ", "").split(":");

            if (parseAsterisk.length > 1) {
                key = parseAsterisk[0];
                if (parseAsterisk[0].contains(" ")) {
                    key= parseAsterisk[0].split(" ")[0];
                }

                value = response.replaceAll("\n", "");
                if (parseAsterisk[1].contains(" ")) {
                    value = parseAsterisk[1].replace(" ", "").replaceAll("\n", "");
                }
            }
        }
        return new AbstractMap.SimpleEntry<>(key, value);
    }

//    private static String parseOther(String response){
//        // parsing response of other commands
//        if (response.startsWith(" !! ")) {
//            String[] parseAsterisk = response.split(" !! ");
//
//            if (parseAsterisk.length > 1) {
//                commandResponseKey = parseAsterisk[1].replace(" ", "");
//            }
//            if (!commandResponseKey.equals("No parameters are changed.")) {
//                commandResponseFlag = true;
//            }
//            if (!commandResponseKey.equals("The system will be rebooted in 3 secs.")) {
//                // PROTOCOL:
//                // TODO: Reconnect & verify
//                mBluetoothLEService.reConnect();
//            }
//        }
//        if (commandResponseFlag && !commandResponseKey.equals("")) {
//            commandResponseValue = response.replace(" ", "");
//            mValueManager.updateResponse(commandResponseKey, commandResponseValue);
//
//            commandResponseFlag = false;
//            commandResponseKey = "";
//        }
//    }

    private static String parseProtocol(String response) {
        // !! Please wait until reboot is completed and re-connect to UDP Server(0.0.0.0:5000)

        String protocol = response.split("to")[1].split("Server")[0].replace(" ", "");
        String port = response.split("\\(")[1].split("\\)")[0];

        return protocol + ":" + port;
    }

    private static String parseWifi(String response) {
        //!! Rebooting MCU to apply new AP Connection in 3 secs !!

        return response.split("in")[1].split("secs")[0].replace(" ", "");
    }

    private static String parseReport(String response) {
        String result = "";
        //TODO
        return result;
    }

    private static String parseReboot(String response) {
        // !! System will be Rebooted in 3 secs to apply new parameters.
        // !! Send "CANCEL:" to cancel the rebooting.

        return response.split("secs")[0].split("in")[1].replace(" ", "");
    }
}
