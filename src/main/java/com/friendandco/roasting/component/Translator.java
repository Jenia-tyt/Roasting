package com.friendandco.roasting.component;

import com.friendandco.roasting.constant.GlobalConstant;
import com.friendandco.roasting.model.settings.Settings;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

@Log4j2
@Component
@AllArgsConstructor
public class Translator {
    private final ConfigurableApplicationContext applicationContext;

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
            log.atError().log(ioe.getMessage());
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
