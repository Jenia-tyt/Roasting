package com.friendandco.roasting.service;


import com.friendandco.roasting.component.CssStyleProvider;
import com.friendandco.roasting.component.Translator;
import com.friendandco.roasting.multiThread.ThreadPoolFix;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InfoArduinoService {
    private final CssStyleProvider cssStyleProvider;
    private final ThreadPoolFix threadPool;
    private final Translator translator;

    private Label label;
    boolean connect = false;

    public void init(Label textField) {
        this.label = textField;
    }

    public void notConnectInfo() {
        Task<Void> draw = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    label.setText(translator.getMessage("thermocouple.not_connect"));
                    label.getStylesheets().clear();
                    cssStyleProvider.getNotConnectArduinoInfoCss().ifPresent(css -> label.getStylesheets().add(css));
                    connect = false;
                });
                return null;
            }
        };
        threadPool.getService().submit(draw);
    }

    public void connectInfo() {
        Task<Void> draw = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    label.setText(translator.getMessage("thermocouple.connect"));
                    label.getStylesheets().clear();
                    cssStyleProvider.getConnectArduinoInfoCss().ifPresent(css -> label.getStylesheets().add(css));
                    connect = true;
                });
                return null;
            }
        };
        threadPool.getService().submit(draw);
    }

    public void reload() {
        if (connect) {
            connectInfo();
        } else {
            notConnectInfo();
        }
    }
}
