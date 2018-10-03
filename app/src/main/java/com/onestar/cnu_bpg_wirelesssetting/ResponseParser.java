package com.onestar.cnu_bpg_wirelesssetting;


import com.onestar.cnu_bpg_wirelesssetting.Enum.Command;

import java.util.AbstractMap;

public class ResponseParser {

    public ResponseParser() {
    }

    public static String parse(String key, String response) {
        String newValue = "";

        if (key.equals(Command.FREQ.value)) {
            // NO RESPONSE TO PARSE FOR FREQ COMMAND
        } else if (key.equals(Command.LED.value)) {
            newValue = parseLED(response);
        } else if (key.equals(Command.TARGET.value)) {
            newValue = parseTarget(response);
        } else if (key.equals(Command.REPORT.value)) {
            newValue = parseReport(response);
        } else if (key.equals(Command.PROTOCOL.value)) {
            // newValue = parseProtocol(response);
            // NO RESPONSE TO PARSE FOR PROTOCOL COMMAND
        } else if (key.equals(Command.SET_TIME.value)) {
            newValue = parseTime(response);
        } else if (key.equals(Command.WIFI.value)) {
            // NO RESPONSE TO PARSE FOR FREQ COMMAND
        } else if (key.equals(Command.REBOOT.value)) {
            newValue = parseReboot(response);
        }

        return newValue;
    }

    private static String parseLED(String response) {
        return response.replace(" mA", "").replace("\n", "");
    }

    private static String parseTarget(String response) {
        return response.replace(".", "").replace("\n", "");
    }

    public static AbstractMap.SimpleEntry<String, String> parseQuery(String header, String response) {
        String key = "", value = "";

        if (response.startsWith(" * ")) {
            String[] parseAsterisk = response.replace(" * ", "").split(":");

            if (parseAsterisk.length > 1) {
                key = parseAsterisk[0];
                if (parseAsterisk[0].contains(" ")) {
                    key = parseAsterisk[0].split(" ")[0];
                }

                value = response.replaceAll("\n", "");
                if (parseAsterisk[1].contains(" ")) {
                    value = parseAsterisk[1].replace(" ", "").replaceAll("\n", "");
                }
            }
        }
        return new AbstractMap.SimpleEntry<>(key, value);
    }

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

        if (response.startsWith(": ")) {
            if (response.contains("console") || response.contains("nw")
                    || response.contains("both") || response.contains("none")) {
                result = response.replace(" ", "");
            }
        }
        return result;
    }

    private static String parseReboot(String response) {
        // !! System will be Rebooted in 3 secs to apply new parameters.
        // !! Send "CANCEL:" to cancel the rebooting.

        return response.split("secs")[0].split("in")[1].replace(" ", "");
    }

    private static String parseTime(String response) {
        // !! Updated Current time : Mon, 03 Sep. 2018, 16:32:56
        return response.split(" : ")[1].replace("\n", "");
    }
}
