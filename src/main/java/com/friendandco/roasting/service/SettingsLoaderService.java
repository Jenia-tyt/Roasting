package com.friendandco.roasting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.friendandco.roasting.model.settings.Settings;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Log4j2
@NoArgsConstructor
public class SettingsLoaderService {
    private final String nameSettingsFile = "settings/settings.yaml";

    public Settings load() {
        URL settingsUrl = getSettingsUrl();
        if (settingsUrl == null) {
            return getDefaultSettings();
        }
        return read(settingsUrl);
    }

    public void writeSettings(Settings settings) {
        URL settingsUrl = getSettingsUrl();
        if (settingsUrl == null) {
            File file = new File("./src/main/resources/settings/settings.yaml");
            write(file, settings);
        } else {
            write(new File(settingsUrl.getFile()), settings);
        }
    }

    private void write(File file, Settings settings) {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            if (!file.exists()) {
                System.out.println("Create file: " + file.createNewFile());
            }
            objectMapper.writeValue(file, settings);
        } catch (IOException e) {
            log.atError().log(String.format("Settings can't write in file %s", nameSettingsFile), e.getMessage());
        }
    }

    private Settings read(URL settingsUrl) {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            return objectMapper.readValue(settingsUrl, Settings.class);
        } catch (IOException e) {
            log.atError().log(String.format("Settings can't load from file %s", nameSettingsFile), e.getMessage());
            return getDefaultSettings();
        }
    }

    private URL getSettingsUrl() {
        return getClass().getClassLoader().getResource(nameSettingsFile);
    }

    private Settings getDefaultSettings() {
        Settings settings = new Settings();
        settings.fileDefaultValue();
        return settings;
    }
}
