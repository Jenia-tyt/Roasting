package com.friendandco.roasting.service;

import com.friendandco.roasting.component.Translator;
import com.friendandco.roasting.model.settings.Settings;
import com.friendandco.roasting.multiThread.ThreadPoolFix;
import com.friendandco.roasting.utils.DateTimeUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LineChartService {
    private final Settings settings;
    private final Translator translator;
    private final ThreadPoolFix threadPool;
    private final TimerService timerService;
    private final ArduinoService arduinoService;

    private LineChart<Number, Number> lineChart;
    private boolean pause = false;
    private boolean stop = true;
    private Task<Void> drawChart;


    public void init(LineChart<Number, Number> lineChart) {
        this.lineChart = lineChart;
        //TODO сюда добавить название кофе которые жарится
        lineChart.setTitle(DateTimeUtils.today());
        Axis<Number> xAxis = lineChart.getXAxis();
        xAxis.setLabel(String.format(translator.getMessage("chart.x"), translator.getMessage("time.value")));

        Axis<Number> yAxis = lineChart.getYAxis();
        yAxis.setLabel(String.format(translator.getMessage("chart.y"), settings.getValueTemperature()));
    }

    public void start() {
        if (stop) {
            stop = false;
            drawChart = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    while (!stop) {
                        if (!pause) {
                            Platform.runLater(() -> {
                                double currentTemperature = arduinoService.getCurrentTemperature();
//                                timerService


                                //TODO
                                // тут надо отрисовывать график
                            });
                            Thread.sleep(1000);
                        }
                    }
                    return null;
                }
            };
            threadPool.getService().submit(drawChart);
        }
    }

    public void pause() {
        pause = !pause;
    }

    public void stop() {
        stop = !stop;
        drawChart.cancel();
    }
}
