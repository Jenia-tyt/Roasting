package com.friendandco.roasting.model.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String locale;
    private String valueTemperature;
    @JsonProperty("xAxis")
    private SettingsAxis xAxis;
    @JsonProperty("yAxis")
    private SettingsAxis yAxis;

    public Locale getLocale(){
        return Locale.forLanguageTag(locale);
    }

    public void fileDefaultValue() {
        locale = "ru";
        valueTemperature = "°C";
        xAxis = new SettingsAxis(
                "x",
                0,
                15,
                1,
                false
        );

        yAxis = new SettingsAxis(
                "y",
                0,
                500,
                25,
                false
        );
    }
}
