package com.friendandco.roasting.service;

import com.friendandco.roasting.multiThread.ThreadPoolFix;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemperatureDrawService {
    private final ThreadPoolFix threadPool;

    private TextField tempField;

    @Getter
    private boolean initDone = false;

    public void init(TextField tempField) {
        this.tempField = tempField;
        initDone = true;
    }

    public void draw(int temp) {
        Task<Void> drawTempTask = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> tempField.setText(
                        String.valueOf(
                                temp
                        )
                ));
                return null;
            }
        };
        threadPool.getService().submit(drawTempTask);
    }

    public void clear() {
        Task<Void> drawTempTask = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() ->
                        tempField.setText("")
                );
                return null;
            }
        };
        threadPool.getService().submit(drawTempTask);
    }
}
