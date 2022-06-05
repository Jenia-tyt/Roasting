package com.friendandco.roasting.service;

import com.friendandco.roasting.component.Translator;
import com.friendandco.roasting.model.settings.Settings;
import com.friendandco.roasting.multiThread.ThreadPoolFix;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class DifferenceCalculationService {
    private final ChartLoadService chartLoadService;
    private final ArduinoService arduinoService;
    private final ThreadPoolFix threadPoolFix;
    private final TimerService timerService;
    private final Translator translator;
    private final Settings settings;

    @Getter
    private final Map<Integer, Double> dataChart = new HashMap<>();
    @Getter
    private boolean loadChart = false;

    private TextField delta;
    private TextField normTemp;
    private ChoiceBox<String> normalChart;
    private boolean stop = false;
    @Getter
    private String nameLoadCharts;

    public void init(TextField delta, TextField normTemp, ChoiceBox<String> normalChart) {
        this.delta = delta;
        this.normTemp = normTemp;
        this.normalChart = normalChart;
        normalChart.setOnAction(event -> loadNormalChart());
        if (settings.getResultChart() != null) {
            Optional.ofNullable(settings.getResultChart())
                    .ifPresent(nameChart ->
                            Optional.ofNullable(chartLoadService.getLoadCharts().get(nameChart))
                                    .ifPresent(data -> {
                                        loadData(data);
                                        normalChart.setValue(nameChart);
                                        nameLoadCharts = nameChart;
                                    })
                    );
        } else {
            fillDefaultValue();
        }
    }

    public void chooseNormalChart() {
        fillItems();
    }

    public void drawDeltaAndNormTemp() {
        stop = false;
        Task<Void> draw = new Task<>() {
            @Override
            protected Void call() throws Exception {
                while (!stop) {
                    Platform.runLater(() -> {
                        Optional<Double> deltaValue = getDelta();
                        if (deltaValue.isPresent()) {
                            delta.setText(String.format("%.1f", deltaValue.get()).replace(',', '.'));
                        } else {
                            delta.setText("No");
                        }

                        Optional<Double> norma = getNorma();
                        if (norma.isPresent()) {
                            normTemp.setText(norma.get().toString());
                        } else {
                            delta.setText("No");
                        }
                    });
                    Thread.sleep(1000);
                }
                return null;
            }
        };
        threadPoolFix.getService().submit(draw);
    }

    private void loadNormalChart() {
        ConcurrentHashMap<String, XYChart.Series<Double, Double>> loadCharts = chartLoadService.getLoadCharts();
        if (loadCharts == null) {
            return;
        }

        if (loadCharts.isEmpty()) {
            fillDefaultValue();
            return;
        }

        Optional.ofNullable(normalChart.getSelectionModel().getSelectedItem())
                .flatMap(item -> {
                    nameLoadCharts = item;
                    return Optional.ofNullable(loadCharts.get(item));
                })
                .ifPresent(this::loadData);
    }

    public void clear() {
        Task<Void> clear = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    delta.setText("");
                    normTemp.setText("");
                    dataChart.clear();
                    loadChart = false;
                    fillDefaultValue();
                    nameLoadCharts = null;
                });
                return null;
            }
        };
        threadPoolFix.getService().submit(clear);
    }

    public void stop() {
        stop = true;
    }

    public void reload () {
        if (nameLoadCharts != null) {
            normalChart.setValue(nameLoadCharts);
        }
    }

    private Optional<Double> getDelta() {
        return Optional.ofNullable(dataChart.get(timerService.getCount().get()))
                .map(normalTemp -> arduinoService.getCurrentTemperature() - normalTemp);
    }

    private Optional<Double> getNorma() {
        return Optional.ofNullable(dataChart.get(timerService.getCount().get()));
    }

    private void fillDefaultValue() {
        normalChart.setValue(translator.getMessage("normal.chart.choose.box.title"));
    }

    private void fillItems() {
        normalChart.getItems().clear();
        ConcurrentHashMap<String, XYChart.Series<Double, Double>> loadCharts = chartLoadService.getLoadCharts();
        if (!loadCharts.isEmpty()) {
            Enumeration<String> keys = loadCharts.keys();
            while (keys.hasMoreElements()) {
                normalChart.getItems().add(keys.nextElement());
            }
        } else {
            fillDefaultValue();
        }
    }

    private void loadData(XYChart.Series<Double, Double> chr) {
        AtomicInteger count = new AtomicInteger(0);
        chr.getData().forEach(data ->
                this.dataChart.put(count.getAndIncrement(), data.getYValue()
                )
        );
        loadChart = true;
    }
}
