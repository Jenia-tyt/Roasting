package com.friendandco.roasting.service;

import com.friendandco.roasting.component.Translator;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class ArduinoService extends Task<Void> {
    private final TemperatureDrawService temperatureDrawService;
    private final Translator translator;
    private final InfoService infoService;

    private volatile double currentTemperature;
    private boolean connect = false;

    @Override
    protected Void call() throws Exception {
        //TODO реализовтаь
        // тут метод который уставливает конет

        Double value = 0.0;
        while (true) {
            if (connect) {
                if (infoService.isShowing()) {
                    infoService.clear();
                }
                currentTemperature = value;
                log.info("It is work, T = {}", currentTemperature);
                if (temperatureDrawService.isInitDone()) {
                    temperatureDrawService.draw(currentTemperature);
                }
                value = value + 0.1;
            } else {
                //TODO
                // мы показываем попа
                // пытаемся установить конект снова
                if (!infoService.isShowing()) {
                    infoService.showWarning(
                            translator.getMessage("thermocouple.not_connect")
                    );
                }
            }
            Thread.sleep(10000);
            connect = !connect;
        }
    }
}
