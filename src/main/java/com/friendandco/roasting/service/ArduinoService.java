package com.friendandco.roasting.service;

import com.friendandco.roasting.component.Translator;
import com.friendandco.roasting.multiThread.ThreadPoolFix;
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
    private final Translator translator;
    private final InfoService infoService;
    private final ThreadPoolFix threadPool;
    private final TemperatureDrawService temperatureDrawService;

    private volatile double currentTemperature;
    private boolean connect = true;

    @Override
    protected Void call() throws Exception {
        //TODO реализовтаь
        // тут метод который уставливает конет

        Double value = 0.0;
        while (true) {
            if (connect) {
//                if (infoService.isShowing()) {
//                    infoService.clear();
//                }
                currentTemperature = value;
                if (temperatureDrawService.isInitDone()) {
                    temperatureDrawService.draw(currentTemperature);
                }
                value = value + 1;
            } else {
                //TODO
                // мы показываем попа
                // пытаемся установить конект снова
                if (!infoService.isShowing()) {
                    infoService.showError(
                            translator.getMessage("thermocouple.not_connect"),
                            1000
                    );
                }
            }
            Thread.sleep(1000);
        }
    }
}
