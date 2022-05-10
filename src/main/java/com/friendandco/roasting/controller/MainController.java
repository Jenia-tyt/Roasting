package com.friendandco.roasting.controller;

import com.friendandco.roasting.StageInitializer;
import com.friendandco.roasting.component.CssStyleProvider;
import com.friendandco.roasting.component.Translator;
import com.friendandco.roasting.model.chart.ItemChart;
import com.friendandco.roasting.model.settings.Settings;
import com.friendandco.roasting.service.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
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
import java.util.ResourceBundle;

@Setter
@Getter
@Component
@NoArgsConstructor
@SuppressWarnings("all") //TODO убрать только одно предупреждение не все
public class MainController implements Initializable {
    @FXML private BorderPane root;

    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;

    @FXML private TextField info;
    @FXML private TextField delta;
    @FXML private TextField timerArea;
    @FXML private TextField tempField;
    @FXML private TextField tempNormlChart;

    @FXML private ChoiceBox<String> locale;
    @FXML private ChoiceBox<String> normalChart;

    @FXML private ListView<ItemChart> listCharts;
    @FXML private LineChart<Double, Integer> chart;

    @FXML private Spinner<Double> temperatureCoeff;
    @FXML private Spinner<Integer> xStart;
    @FXML private Spinner<Integer> yStart;
    @FXML private Spinner<Integer> xEnd;
    @FXML private Spinner<Integer> yEnd;
//TODO переделать в нормальную кнопку слайдер
    @FXML private Button temperatureUtils;

    @Autowired private DifferenceCalculationService differenceCalculationService;
    @Autowired private ConfigurableApplicationContext applicationContext;
    @Autowired private TemperatureDrawService temperatureDrawService;
    @Autowired private LineChartDrawService lineChartDrawService;
    @Autowired private SettingsDrawService settingsDrawService;
    @Autowired private CssStyleProvider cssStyleProvider;
    @Autowired private StageInitializer stageInitializer;
    @Autowired private ChartLoadService chartLoadService;
    @Autowired private TimerService timerService;
    @Autowired private InfoService infoService;
    @Autowired private Translator translator;
    @Autowired private Settings settings;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        translator.init(locale);
        chartLoadService.init(listCharts, chart, xAxis, yAxis);
        lineChartDrawService.init(chart, xAxis, yAxis);
        timerService.init(timerArea);
        temperatureDrawService.init(tempField);
        infoService.init(info);
        differenceCalculationService.init(delta, tempNormlChart, normalChart);
        settingsDrawService.init(
                temperatureUtils,
                temperatureCoeff,
                xStart,
                yStart,
                xEnd,
                yEnd
        );

        setUpCss();
    }

    //TODO
    // При локализации пропадает график надо  брать данные
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

    @FXML
    public void startShowChart() {
        timerService.start();
        lineChartDrawService.start();
        if (differenceCalculationService.isLoadChart()) {
            differenceCalculationService.drawDeltaAndNormTemp();
        }
    }

    @FXML
    public void pauseShowChart() {
        timerService.pause();
        lineChartDrawService.pause();
    }

    @FXML
    public void stopShowChart() {
        timerService.stop();
        lineChartDrawService.stop();
        differenceCalculationService.stop();
    }

    @FXML
    public void clearChart() {
        timerService.stop();
        timerService.clear();
        lineChartDrawService.stop();
        lineChartDrawService.clear();
        chartLoadService.loadItems();
        differenceCalculationService.clear();
        chartLoadService.clearLoadChartsUuids();
    }

    @FXML
    public void chooseNormalChart() {
        differenceCalculationService.chooseNormalChart();
    }

    @FXML
    public void save(){
        stopShowChart();
        lineChartDrawService.save();
    }

    @FXML
    public void saveSettings() {
        settingsDrawService.save();
    }

    private void setUpCss() {
        cssStyleProvider.getListViewCss().ifPresent(css -> listCharts.getStylesheets().add(css));
    }
}
