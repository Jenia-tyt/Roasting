package com.friendandco.roasting.utils;

import org.yaml.snakeyaml.DumperOptions;

public class SnakeyamlUtils {

    private SnakeyamlUtils() {
    }

    public static DumperOptions getDumperOptions () {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return dumperOptions;
    }
}
