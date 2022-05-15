package com.friendandco.roasting.constant;

import java.util.regex.Pattern;

public class GlobalConstant {
    public final static String GROUP_LOCALE = "locale";
    public final static String CELSIUS = "celsius";
    public final static String FAHRENHEIT = "fahrenheit";
    public final static Pattern LOCALE_PROP = Pattern.compile(String.format("^([a-z]{6})_(?<%s>[a-z]{2})_(.)*$", GROUP_LOCALE));
    public final static int SECONDS_IN_MINUTE = 60;
    public final static String EMPTY_STRING = "";
    public final static String READY_READ_TEMP = "READY";
    public final static String IO_USB_PORT = "IOUSBHostDevice (Dial-In)";
//    C = 27.00 K = 80.60
    public final static Pattern PATTERN_TEMPERATURE = Pattern.compile(String.format("^(.*C = (?<%s>\\d{2,3}.\\d{1})\\d{1} )(F = (?<%s>\\d{2,3}.\\d{1})\\d{1}.*)$", CELSIUS, FAHRENHEIT));
}
