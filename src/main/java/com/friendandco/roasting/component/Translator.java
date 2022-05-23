package com.friendandco.roasting.component;

import com.friendandco.roasting.constant.GlobalConstant;
import com.friendandco.roasting.model.settings.Settings;
import javafx.scene.control.ChoiceBox;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@AllArgsConstructor
public class Translator {
    private final ConfigurableApplicationContext applicationContext;
    private final Settings settings;

    public void init(ChoiceBox<String> locale) {
        List<String> nameLanguage = getAllLocale()
                .stream()
                .map(Locale::getLanguage)
                .collect(Collectors.toList());

        locale.getItems().addAll(nameLanguage);
        locale.setValue(settings.getLocale().getLanguage());
    }

    public String getMessage(String key) {
        ResourceBundle locale = getResourceBundleFromSettings();
        return locale.getString(key);
    }

    public List<Locale> getAllLocale() {
        try(Stream<Path> paths = Files.walk(Paths.get("src/main/resources"))) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(file -> file.getName().startsWith("locale"))
                    .map(file -> getLocaleProp(file.getName()))
                    .filter(Objects::nonNull)
                    .map(Locale::new)
                    .collect(Collectors.toList());


        } catch (IOException ioe) {
            log.error(ioe.getMessage());
            return new ArrayList<>();
        }
    }

    public ResourceBundle getResourceBundleFromSettings() {
        Settings settings = applicationContext.getBean("settings", Settings.class);
        return ResourceBundle.getBundle(
                "locale_" + settings.getLocale().getLanguage(),
                settings.getLocale()
        );
    }

    public ResourceBundle getResourceBundle(String language) {
        return ResourceBundle.getBundle(
                "locale_" + language,
                Locale.forLanguageTag(language)
        );
    }

    private String getLocaleProp(String nameProp) {
        Matcher matcher = GlobalConstant.LOCALE_PROP.matcher(nameProp);
        if (matcher.find()) {
            return matcher.group(GlobalConstant.GROUP_LOCALE);
        }
        return null;
    }
}
