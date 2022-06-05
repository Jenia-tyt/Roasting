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
@SuppressWarnings("all")
public class MainController implements Initializable {
    @FXML private BorderPane root;

    @FXML private Menu topMenu;

    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;

    @FXML private Label info;

    @FXML private TextField delta;
    @FXML private TextField timerArea;
    @FXML private TextField tempField;
    @FXML private TextField tempNormlChart;

    @FXML private ChoiceBox<String> locale;
    @FXML private ChoiceBox<String> normalChart;

    @FXML private ListView<ItemChart> listCharts;

    @FXML private LineChart<Double, Double> chart;

    @FXML private Spinner<Double> temperatureCoeff;
    @FXML private Spinner<Integer> xStart;
    @FXML private Spinner<Integer> yStart;
    @FXML private Spinner<Integer> xEnd;
    @FXML private Spinner<Integer> yEnd;

    @FXML private Tooltip tooltip;

    @Autowired private DifferenceCalculationService differenceCalculationService;
    @Autowired private ConfigurableApplicationContext applicationContext;
    @Autowired private TemperatureDrawService temperatureDrawService;
    @Autowired private LineChartDrawService lineChartDrawService;
    @Autowired private SettingsDrawService settingsDrawService;
    @Autowired private InfoArduinoService infoArduinoService;
    @Autowired private CssStyleProvider cssStyleProvider;
    @Autowired private StageInitializer stageInitializer;
    @Autowired private ChartLoadService chartLoadService;
    @Autowired private TopMenuService topMenuService;
    @Autowired private ArduinoService arduinoService;
    @Autowired private TimerService timerService;
    @Autowired private Translator translator;
    @Autowired private Settings settings;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        translator.init(locale);
        chartLoadService.init(listCharts, chart, xAxis, yAxis);
        lineChartDrawService.init(chart, xAxis, yAxis);
        timerService.init(timerArea);
        temperatureDrawService.init(tempField);
        infoArduinoService.init(info);
        differenceCalculationService.init(delta, tempNormlChart, normalChart);
        settingsDrawService.init(
                temperatureCoeff,
                xStart,
                yStart,
                xEnd,
                yEnd
        );
        tooltip.setText(translator.getMessage("tooltip"));
        topMenuService.init(topMenu, root);

        setUpCss();
    }

    @FXML
    public void onChoseLocale() throws IOException {
        if (root.getScene() == null) {
            return;
        }
        Stage stage = (Stage) root.getScene().getWindow();
        settings.setLanguageTag(locale.getValue());
        ResourceBundle bundle = translator.getResourceBundleFromSettings();

        stageInitializer.initRootStage(stage, bundle);

        differenceCalculationService.reload();
        infoArduinoService.reload();
        chartLoadService.reload();
    }

    @FXML
    public void startShowChart() {
        if (arduinoService.isConnect()) {
            timerService.start();
            lineChartDrawService.start();
            if (differenceCalculationService.isLoadChart()) {
                differenceCalculationService.drawDeltaAndNormTemp();
            }
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

    //TODO
    // сделать загрузку портов
    // сделать авто подключение ардуино
    // Сделать проверку лицензии
    // Сделать автообновление приложение через лаунчер (проверять версию а потом проверять есть ли там новая версия, если есть то просто скачивать и заменять нужный jat так же там сделать обновление лицензии)
    // Слделать групповое удаление
    // Падает лебел отклонения при разворачиании на полный экран
    // сделать что бы настройки лежали внутри jar
    // Призапске программы должны загружаться графики которые были уже загружены
    // Разобраться с тем что не выпадает язык
    // Проблема с четением графиков
    // Надо подумать как автоматом менять кофициент как менять отборажение графика уже понятно зщарелодить сцену наврено будет проблема в том что переключается язык
    @FXML
    public void saveSettings() {
        settingsDrawService.save();
    }

    private void setUpCss() {
        cssStyleProvider.getListViewCss().ifPresent(css -> listCharts.getStylesheets().add(css));
        cssStyleProvider.getChartCss().ifPresent(css -> chart.getStylesheets().add(css));
        cssStyleProvider.getDifferenceTextArea().ifPresent(css -> {
            delta.getStylesheets().add(css);
            tempNormlChart.getStylesheets().add(css);
        });
    }
}
