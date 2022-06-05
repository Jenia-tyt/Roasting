package com.friendandco.roasting.model.settings;

import com.friendandco.roasting.model.units.TemperatureUnits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Locale;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Settings implements Serializable {
    private String languageTag;
    private TemperatureUnits temperatureUnits;
    private SettingsAxis x;
    private SettingsAxis y;
    private double correctionFactor;

    public Locale getLocale(){
        return Locale.forLanguageTag(languageTag);
    }

    public void fileDefaultValue() {
        languageTag = "ru";
        temperatureUnits = TemperatureUnits.CELSIUS;
        x = new SettingsAxis(
                "x",
                0,
                15,
                1,
                false
        );

        y = new SettingsAxis(
                "y",
                0,
                500,
                25,
                false
        );

        correctionFactor = 1.0;
    }
}
