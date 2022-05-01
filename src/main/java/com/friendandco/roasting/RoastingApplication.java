package com.friendandco.roasting;

import com.friendandco.roasting.model.settings.Settings;
import com.friendandco.roasting.multiThread.ThreadPoolFix;
import com.friendandco.roasting.service.SettingsLoaderService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;

import java.io.IOException;

@SpringBootApplication
public class RoastingApplication {
    @Value("classpath:/chart.fxml")
    private Resource chartResource;

    public static void main(String[] args) {
        Application.launch(CharApplication.class, args);
    }

    @Bean
    public Settings settings(){
        SettingsLoaderService settingsLoaderService = settingsLoader();
        return settingsLoaderService.load();
    }

    @Bean
    public SettingsLoaderService settingsLoader() {
        return new SettingsLoaderService();
    }

    @Bean
    public ThreadPoolFix threadPoolFix() {
        return new ThreadPoolFix();
    }

    @Bean
    @Scope("prototype")
    public FXMLLoader fxmlLoader() throws IOException {
        return new FXMLLoader(chartResource.getURL());
    }

}
