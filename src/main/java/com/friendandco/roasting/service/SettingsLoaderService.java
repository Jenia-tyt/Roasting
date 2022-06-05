package com.friendandco.roasting.service;

import com.friendandco.roasting.model.settings.Settings;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import static com.friendandco.roasting.utils.SnakeyamlUtils.getDumperOptions;

@Slf4j
@NoArgsConstructor
public class SettingsLoaderService {
    private final String nameSettingsFile = "settings/settings.yaml";

    public Settings load() {
        InputStream settingsFileStream = getSettingsFileStream();
        if (settingsFileStream == null) {
            log.info("Load default settings");
            return getDefaultSettings();
        }
        return load(settingsFileStream);
    }

    public void writeSettings(Settings settings) {
        String filePath = "./src/main/resources/settings/settings.yaml";
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                if (file.createNewFile()) {
                    log.info("Create file: " + file.getName());
                } else {
                    log.warn("File " + file.getName() + "was not create");
                }
            }
            PrintWriter printWriter = new PrintWriter(file);
            Yaml yaml = new Yaml(getDumperOptions());
            yaml.dump(settings, printWriter);
        } catch (IOException e) {
            log.error(String.format("Settings can't write in file %s", nameSettingsFile), e);
        }
    }

    private Settings load(InputStream stream) {
        Yaml yaml = new Yaml(new Constructor(Settings.class));
        yaml.setBeanAccess(BeanAccess.FIELD);
        try {
            return yaml.load(stream);
        } catch (Exception e) {
            log.error(String.format("Settings can't load from file %s", nameSettingsFile), e);
            return getDefaultSettings();
        }
    }

    private InputStream getSettingsFileStream() {
        return this.getClass()
                .getClassLoader()
                .getResourceAsStream(nameSettingsFile);
    }

    private Settings getDefaultSettings() {
        Settings settings = new Settings();
        settings.fileDefaultValue();
        return settings;
    }
}
