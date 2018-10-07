package com.onestar.cnu_bpg_wirelesssetting;


import com.onestar.cnu_bpg_wirelesssetting.Enum.Command;

import java.util.AbstractMap;

public class ResponseParser {

    /**
     * Call proper parsing method according to header value.
     *
     * @param key      Command header for identifying option to be updated
     * @param response Command response
     * @return New option value from parsing response String
     */
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

    /**
     * Extract updated LED value.
     * e.g.) 29.01 mA -> 29.01
     *
     * @param response (Updated LED value) mA
     * @return (Updated LED value)
     */
    private static String parseLED(String response) {
        return response.replace(" mA", "").replace("\n", "");
    }

    /**
     * Extract updated TARGET value.
     * e.g.) ON.
     *
     * @param response (ON / OFF).
     * @return (ON / OFF)
     */
    private static String parseTarget(String response) {
        return response.replace(".", "").replace("\n", "");
    }

    /**
     * Extract updated value by parsing result lines by QUERY command which starts with " * ".
     * e.g.) * Pulse Rep. freq. :   100 Hz
     * (NOT all of the result line follow this format)
     *
     * @param response * (Key) : (Updated value)
     * @return Pair of key and updated value
     */
    public static AbstractMap.SimpleEntry<String, String> parseQuery(String response) {
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

    /**
     * Extract updated PROTOCOL value.
     * e.g.) !! Please wait until reboot is completed and re-connect to UDP Server(0.0.0.0:5000)
     *
     * @param response !! Please wait until reboot is completed and re-connect to (Updated protocol number) Server(0.0.0.0:(Updated port number))
     * @return (Updated protocol):(Updated port number)
     */
    private static String parseProtocol(String response) {
        String protocol = response.split("to")[1].split("Server")[0].replace(" ", "");
        String port = response.split("\\(")[1].split("\\)")[0];

        return protocol + ":" + port;
    }

    /**
     * Extract updated WIFI value.
     * e.g.) !! Rebooting MCU to apply new AP Connection in 3 secs !!
     *
     * @param response !! Rebooting MCU to apply new AP Connection in 3 secs !!
     * @return TODO
     */
    private static String parseWifi(String response) {
        return response;
    }

    /**
     * Extract updated REPORT_TO value.
     * e.g.) (both / console / nw / none)
     *
     * @param response (both / console / nw / none)
     * @return Updated report value
     */
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

    /**
     * Extract updated REBOOT value.
     * e.g.) !! System will be Rebooted in 3 secs to apply new parameters.
     * !! Send "CANCEL:" to cancel the rebooting.
     *
     * @param response !! System will be Rebooted in (Updated delay time) secs to apply new parameters.
     * @return Updated delay time
     */
    private static String parseReboot(String response) {


        return response.split("secs")[0].split("in")[1].replace(" ", "");
    }

    /**
     * Extract updated SET_TIME value.
     * e.g.) !! Updated Current time : Mon, 03 Sep. 2018, 16:32:56
     *
     * @param response !! Updated Current time : (Updated time value)
     * @return Updated time value
     */
    private static String parseTime(String response) {
        return response.split("time : ")[1].replace("\n", "");
    }
}
