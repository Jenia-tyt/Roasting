package com.friendandco.roasting.component;

import com.friendandco.roasting.constant.GlobalConstant;
import com.friendandco.roasting.model.settings.Settings;
import javafx.scene.control.ChoiceBox;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;

@Slf4j
@Component
@AllArgsConstructor
public class Translator {
    private final ConfigurableApplicationContext applicationContext;
    private final Settings settings;

    public void init(ChoiceBox<String> locale) {
        locale.getItems().addAll(Arrays.asList("ru", "en"));
        locale.setValue(settings.getLocale().getLanguage());
    }

    public String getMessage(String key) {
        ResourceBundle locale = getResourceBundleFromSettings();
        return locale.getString(key);
    }

    public ResourceBundle getResourceBundleFromSettings() {
        Settings settings = applicationContext.getBean("settings", Settings.class);
        String nameLocale = settings.getLocale().getLanguage();
        return ResourceBundle.getBundle(
                String.format(
                        "locale_%s_%s",
                        nameLocale,
                        nameLocale.toUpperCase()
                )
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
