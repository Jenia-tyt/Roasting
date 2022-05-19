package com.friendandco.roasting.service;

import com.fazecast.jSerialComm.SerialPort;
import com.friendandco.roasting.component.Translator;
import com.friendandco.roasting.constant.GlobalConstant;
import com.friendandco.roasting.model.settings.Settings;
import com.friendandco.roasting.multiThread.ThreadPoolFix;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;

import static com.friendandco.roasting.constant.GlobalConstant.IO_USB_PORT;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class ArduinoService extends Task<Void> {
    private final Settings settings;
    private final Translator translator;
    private final InfoService infoService;
    private final ThreadPoolFix threadPool;
    private final TemperatureDrawService temperatureDrawService;

    private volatile double currentTemperature;
    private boolean connect = false;
    private SerialPort port;
    private InputStream inputStream;
    private boolean readyReadTemp = false;

    @Override
    protected Void call() {
        try {
            infoService.showError(
                    translator.getMessage("thermocouple.not_connect"),
                    10000
            );
            createConnect();
            if (connect) {
                readTemperature();
            } else {
                createConnect();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Error in ArduinoService", e);
        }
        return null;
    }

    private void createConnect() throws Exception {
        SerialPort[] commPorts = SerialPort.getCommPorts();
        for (SerialPort serialPort : commPorts) {
            if (IO_USB_PORT.equals(serialPort.getDescriptivePortName())) {
                if (serialPort.openPort()) {
                    port = serialPort;
                    connect = true;
                    serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
                    inputStream = serialPort.getInputStream();
                    readTemperature();
                    return;
                }
            }
        }
        connect = false;
        infoService.showError(
                translator.getMessage("thermocouple.not_connect"),
                2000
        );
        Thread.sleep(2000);
        createConnect();
    }

    private void readTemperature() throws Exception {
        double correctionFactor = settings.getCorrectionFactor();
        try (Scanner scanner = new Scanner(inputStream)) {
            while (connect) {
                String value = scanner.nextLine();
                Matcher matcher = GlobalConstant.PATTERN_TEMPERATURE.matcher(value);
                if (!readyReadTemp && GlobalConstant.READY_READ_TEMP.equals(value)) {
                    readyReadTemp = true;
                    infoService.showOk(
                            translator.getMessage("thermocouple.connect"),
                            5000
                    );
                }
                if (matcher.find() && temperatureDrawService.isInitDone() && readyReadTemp) {
                    currentTemperature = correctionFactor * Double.parseDouble(matcher.group(GlobalConstant.CELSIUS));
                    temperatureDrawService.draw(currentTemperature);
                }
                Thread.sleep(1000);
            }
            createConnect();
        } catch (NoSuchElementException e) {
            port.closePort();
            inputStream.close();
            temperatureDrawService.clear();
            currentTemperature = 0;
            connect = false;
            readyReadTemp = false;
            infoService.showError(
                    translator.getMessage("thermocouple.not_connect"),
                    2000
            );
            log.error("Data scanner thermocouple can not read value", e);
            createConnect();
        }
    }
}
