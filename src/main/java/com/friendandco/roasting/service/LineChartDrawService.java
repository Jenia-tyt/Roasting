package com.friendandco.roasting.service;

import com.friendandco.roasting.component.Translator;
import com.friendandco.roasting.model.settings.Settings;
import com.friendandco.roasting.model.settings.SettingsAxis;
import com.friendandco.roasting.multiThread.ThreadPoolFix;
import com.friendandco.roasting.utils.DateTimeUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.FileChooser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.friendandco.roasting.constant.GlobalConstant.SECONDS_IN_MINUTE;

@Service
@RequiredArgsConstructor
public class LineChartDrawService {
    private final Settings settings;
    private final Translator translator;
    private final ThreadPoolFix threadPool;
    private final TimerService timerService;
    private final ArduinoService arduinoService;
    private final ChartLoadService chartLoadService;

    private NumberAxis xAxis;
    private NumberAxis yAxis;
    private boolean stop = true;
    private boolean pause = false;
    private LineChart<Double, Double> lineChart;
    private XYChart.Series<Double, Double> dataChart;

    public void init(LineChart<Double, Double> lineChart, NumberAxis xAxis, NumberAxis yAxis) {
        this.lineChart = lineChart;
        this.xAxis = xAxis;
        this.yAxis = yAxis;

        //TODO
        // 1) сюда добавить название кофе которые жарится
        prepareAxisFromSettings();
        lineChart.setCreateSymbols(false);
        lineChart.setTitle(DateTimeUtils.today());
        if (dataChart == null) {
            dataChart = new XYChart.Series<>();
        }
        dataChart.setName(translator.getMessage("chart.name"));
        lineChart.getData().add(dataChart);
    }

    public void start() {
        stop = false;
        clearDate(false);
        Task<Void> drawChart = new Task<>() {
            @Override
            protected Void call() throws Exception {
                while (!stop) {
                    if (!pause) {
                        double xValue = (double) timerService.getCount().get() / SECONDS_IN_MINUTE;
                        double yValue = arduinoService.getCurrentTemperature();
                        Platform.runLater(() ->
                                dataChart.getData().add(
                                        new XYChart.Data<>(
                                                xValue,
                                                yValue
                                        )
                                )
                        );
                        if (xValue >= xAxis.getUpperBound()) {
                            xAxis.setAutoRanging(true);
                        }
                        if (yValue >= yAxis.getUpperBound()) {
                            yAxis.setAutoRanging(true);
                        }
                        Thread.sleep(1000);
                    }

                }
                return null;
            }
        };
        threadPool.getService().submit(drawChart);
    }

    public void pause() {
        pause = !pause;
    }

    public void stop() {
        stop = true;
    }

    public void clear() {
        threadPool.getService().submit(
                new Task<>() {
                    @Override
                    protected Void call() {
                        Platform.runLater(() -> {
                            prepareAxisFromSettings();
                            clearDate(true);
                        });
                        return null;
                    }
                });
    }

    public void save() {
        if (!stop) {
            stop();
        }
        chartLoadService.save(lineChart);
    }

    private void clearDate(boolean clearAll) {
        if (!clearAll) {
            lineChart.getData().remove(dataChart);
        } else {
            lineChart.getData().clear();
        }
        dataChart = new XYChart.Series<>();
        dataChart.setName(translator.getMessage("chart.name"));
        lineChart.getData().add(dataChart);
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
    }

    private void prepareAxisFromSettings() {
        prepareAxis(
                xAxis,
                settings.getXAxis(),
                String.format(translator.getMessage("chart.x"), translator.getMessage("time.value"))
        );

        prepareAxis(
                yAxis,
                settings.getYAxis(),
                String.format(translator.getMessage("chart.y"), settings.getTemperatureUnits().getDesignation())
        );
    }

    private void prepareAxis(
            NumberAxis axis,
            SettingsAxis settingsAxis,
            String label
    ) {
        axis.setLowerBound(settingsAxis.getLowerBound());
        axis.setUpperBound(settingsAxis.getUpperBound());
        axis.setTickUnit(settingsAxis.getTickUnit());
        axis.setLabel(label);
        axis.setAutoRanging(settingsAxis.isAutoRanging());
    }
}
