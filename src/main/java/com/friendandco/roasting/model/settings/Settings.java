package com.friendandco.roasting.model.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Locale;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Settings implements Serializable {
    private String locale;
    @Getter
    private String valueTemperature;

    public Locale getLocale(){
        return Locale.forLanguageTag(locale);
    }

    public void fileDefaultValue() {
        locale = "ru";
        valueTemperature = "C";
    }
}
