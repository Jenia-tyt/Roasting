package com.friendandco.roasting.service;

import com.fazecast.jSerialComm.SerialPort;
import com.friendandco.roasting.component.Translator;
import com.friendandco.roasting.multiThread.ThreadPoolFix;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Scanner;

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
    protected Void call()  {
        //TODO реализовтаь
        // тут метод который уставливает конет

        test();


        Double value = 0.0;
        while (true) {

            try {


                if (connect) {
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

            } catch (Exception e) {
                System.out.println("ERROR_FIRST" + e);
            }
        }
    }

    private void test() {
        try {
            SerialPort[] commPorts = SerialPort.getCommPorts();
            System.out.println("Select a port:");
            int i = 1;
            for(SerialPort port : commPorts) {
                System.out.println(i++ + ". " + port.getSystemPortName());
            }
            Scanner s = new Scanner(System.in);
            int chosenPort = s.nextInt();

            SerialPort port = commPorts[chosenPort - 1];
            if(port.openPort()) {
                System.out.println("Successfully opened the port.");
            } else {
                System.out.println("Unable to open the port.");
                return;
            }
            port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

            Scanner data = new Scanner(port.getInputStream());
            System.out.println(data.next());
            while (true) {
                System.out.println(data.nextLine());
            }
        } catch (Exception e) {
            System.out.println("ERROR_SECOND : " + e);
        }
    }
}
