package com.friendandco.roasting.controller;

import com.friendandco.roasting.StageInitializer;
import com.friendandco.roasting.component.Translator;
import com.friendandco.roasting.model.settings.Settings;
import com.friendandco.roasting.service.InfoService;
import com.friendandco.roasting.service.LineChartService;
import com.friendandco.roasting.service.TemperatureDrawService;
import com.friendandco.roasting.service.TimerService;
import com.friendandco.roasting.utils.DateTimeUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Setter
@Getter
@Component
@NoArgsConstructor
@SuppressWarnings("all")
public class MainController implements Initializable {
    @FXML private TextField info;
    @FXML private BorderPane root;
    @FXML private TextField timerArea;
    @FXML private TextField tempField;
    @FXML private ChoiceBox<String> locale;
    @FXML private LineChart<Number, Number> chart;

    @Autowired private ConfigurableApplicationContext applicationContext;
    @Autowired private TemperatureDrawService temperatureDrawService;
    @Autowired private StageInitializer stageInitializer;
    @Autowired private LineChartService lineChartService;
    @Autowired private TimerService timerService;
    @Autowired private Translator translator;
    @Autowired private InfoService infoService;
    @Autowired private Settings settings;

    private LocalTime timer = LocalTime.MIDNIGHT;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> nameLanguage = translator.getAllLocale()
                .stream()
                .map(Locale::getLanguage)
                .collect(Collectors.toList());

        locale.getItems().addAll(nameLanguage);
        locale.setValue(settings.getLocale().getLanguage());

        lineChartService.init(chart);

        timerArea.setText(DateTimeUtils.getTimerTime(timer));
        timerService.init(timerArea);

        temperatureDrawService.init(tempField);

        infoService.init(info);
    }

    @FXML
    public void onChoseLocale() throws IOException {
        if (root.getScene() == null) {
            return;
        }
        Stage stage = (Stage) root.getScene().getWindow();
        settings.setLocale(locale.getValue());
        ResourceBundle bundle = translator.getResourceBundleFromSettings();

        stageInitializer.initRootStage(stage, bundle);
    }

    //TODO туда передавать шаг который можно выставить в настройках, по умолчнию надо выставить одну секунду
    @FXML
    public void startShowChart() {
        timerService.start();
        lineChartService.start();
    }

    @FXML
    public void pauseShowChart() {
        timerService.pause();
        lineChartService.pause();
    }

    @FXML
    public void stopShowChart() {
        timerService.stop();
        lineChartService.stop();
    }
}
