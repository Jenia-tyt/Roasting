package com.friendandco.roasting;

import com.friendandco.roasting.constant.BeanName;
import com.friendandco.roasting.model.settings.Settings;
import com.friendandco.roasting.multiThread.ThreadPoolFix;
import com.friendandco.roasting.service.SettingsLoaderService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

public class CharApplication extends Application {
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(RoastingApplication.class).run();
    }

    @Override
    public void start(Stage stage) {
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void stop() {
        Settings settings = applicationContext.getBean(BeanName.SETTINGS, Settings.class);
        SettingsLoaderService settingsLoaderService = applicationContext.getBean(BeanName.SETTINGS_LOADER, SettingsLoaderService.class);
        settingsLoaderService.writeSettings(settings);

        ThreadPoolFix threadPool = applicationContext.getBean(BeanName.THREAD_POOL, ThreadPoolFix.class);
        threadPool.getService().shutdownNow();

        applicationContext.close();
        Platform.exit();
    }

    static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }

        public Stage getStage() {
            return (Stage) getSource();
        }
    }
}
