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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
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
import java.util.ResourceBundle;

@Setter
@Getter
@Component
@NoArgsConstructor
@SuppressWarnings("all")
public class MainController implements Initializable {
    @FXML private TextField info;
    @FXML private BorderPane root;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private TextField timerArea;
    @FXML private TextField tempField;
    @FXML private ChoiceBox<String> locale;
    @FXML private LineChart<Double, Integer> chart;
    @FXML private ListView<ItemChart> listCharts;

    @Autowired private ConfigurableApplicationContext applicationContext;
    @Autowired private TemperatureDrawService temperatureDrawService;
    @Autowired private LineChartDrawService lineChartDrawService;
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
    }

    @FXML
    public void clearChart() {
        timerService.stop();
        timerService.clear();
        lineChartDrawService.stop();
        lineChartDrawService.clear();
        chartLoadService.loadItems();
        chartLoadService.clearLoadChartsUuids();
    }

    @FXML
    public void save(){
        stopShowChart();
        lineChartDrawService.save();
    }

    private void setUpCss() {
        cssStyleProvider.getListViewCss().ifPresent(css -> listCharts.getStylesheets().add(css));
    }
}
