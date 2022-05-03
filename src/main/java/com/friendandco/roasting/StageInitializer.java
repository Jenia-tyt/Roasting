package com.friendandco.roasting;

import com.friendandco.roasting.component.Translator;
import com.friendandco.roasting.constant.BeanName;
import com.friendandco.roasting.multiThread.ThreadPoolFix;
import com.friendandco.roasting.service.ArduinoService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ResourceBundle;

import static com.friendandco.roasting.CharApplication.StageReadyEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class StageInitializer implements ApplicationListener<StageReadyEvent> {
    @Value("${name.app:name.app}")
    private String nameApp;

    private final Translator translator;
    private final ThreadPoolFix threadPool;
    private final ArduinoService arduinoService;
    private final ConfigurableApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        try {
            threadPool.init(10);
            threadPool.getService().submit(arduinoService);

            ResourceBundle locale = translator.getResourceBundleFromSettings();
            Stage primaryStage = event.getStage();
            initRootStage(primaryStage, locale);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            log.error("Error in main window", e);
            throw new RuntimeException();
        }
    }

    public void initRootStage(Stage stage, ResourceBundle bundle) throws IOException {
        FXMLLoader fxmlLoader = applicationContext.getBean(BeanName.FXML_LOADER, FXMLLoader.class);
        fxmlLoader.setResources(bundle);
        fxmlLoader.setControllerFactory(applicationContext::getBean);
        Parent parent = fxmlLoader.load();
        stage.setScene(new Scene(parent));
        stage.setTitle(nameApp);
        stage.show();
    }
}
