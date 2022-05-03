package com.friendandco.roasting.constant;

import java.util.regex.Pattern;

public class GlobalConstant {
    public final static String GROUP_LOCALE = "locale";
    public final static Pattern LOCALE_PROP = Pattern.compile(String.format("^([a-z]{6})_(?<%s>[a-z]{2})_(.)*$", GROUP_LOCALE));
    public final static int SECONDS_IN_MINUTE = 60;
    public final static String EMPTY_STRING = "";
}
