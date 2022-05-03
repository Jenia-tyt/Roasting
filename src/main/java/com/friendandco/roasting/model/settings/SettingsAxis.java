package com.friendandco.roasting.model.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingsAxis implements Serializable {
    private String nameAxis;
    private int lowerBound;
    private int upperBound;
    private int tickUnit;
    private boolean autoRanging;
}
