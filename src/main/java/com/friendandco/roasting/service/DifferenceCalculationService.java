package com.friendandco.roasting.service;

import com.friendandco.roasting.component.Translator;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class DifferenceCalculationService {
    private final ChartLoadService chartLoadService;
    private final ArduinoService arduinoService;
    private final ThreadPoolFix threadPoolFix;
    private final TimerService timerService;
    private final Translator translator;

    @Getter
    private final Map<Double, Integer> dataMap = new HashMap<>();
    @Getter
    private boolean loadChart = false;

    private TextField textField;
    private ChoiceBox<String> normalChart;

    public void init(TextField textField, ChoiceBox<String> normalChart) {
        this.textField = textField;
        this.normalChart = normalChart;
        normalChart.setOnAction(event -> loadNormalChart());
        fillDefaultValue();
    }

    public void chooseNormalChart() {
        fillItems();
    }

    public void drawDelta() {
        Task<Void> draw = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    if (getDelta().isPresent()) {
                        textField.setText(getDelta().get().toString());
                    } else {
                        textField.setText("No");
                    }
                });
                return null;
            }
        };
        threadPoolFix.getService().submit(draw);
    }

    private void loadNormalChart() {
        ConcurrentHashMap<String, XYChart.Series<Double, Integer>> loadCharts = chartLoadService.getLoadCharts();
        if (loadCharts.isEmpty()) {
            fillDefaultValue();
            return;
        }

        String selectedItem = normalChart.getSelectionModel().getSelectedItem();
        Optional.ofNullable(loadCharts.get(selectedItem))
                .ifPresent(dataChart -> {
                    dataChart.getData().forEach(
                            data -> dataMap.put(data.getXValue(), data.getYValue()
                            )
                    );
                    loadChart = true;
                });
    }

    private Optional<Integer> getDelta() {
        //ту надо доставать нужную позицию
        return Optional.ofNullable(dataMap.get(0))
                .map(normalTemp -> arduinoService.getCurrentTemperature() - normalTemp);
    }

    public void clear() {
        Task<Void> clear = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                        textField.setText("");
                        dataMap.clear();
                        loadChart = false;
                });
                return null;
            }
        };
        threadPoolFix.getService().submit(clear);
    }

    private void fillDefaultValue() {
        normalChart.setValue(translator.getMessage("normal.chart.choose.box.title"));
    }

    private void fillItems() {
        normalChart.getItems().clear();
        ConcurrentHashMap<String, XYChart.Series<Double, Integer>> loadCharts = chartLoadService.getLoadCharts();
        if (!loadCharts.isEmpty()) {
            Enumeration<String> keys = loadCharts.keys();
            while (keys.hasMoreElements()) {
                normalChart.getItems().add(keys.nextElement());
            }
        } else {
            fillDefaultValue();
        }
    }
}
//TODO
// 1 Стираются голочки
// 2 заполнять данными по умолчанию нормально
