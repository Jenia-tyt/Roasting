package com.friendandco.roasting.service;

import com.friendandco.roasting.multiThread.ThreadPoolFix;
import com.friendandco.roasting.utils.DateTimeUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@RequiredArgsConstructor
public class TimerService {
    private final ThreadPoolFix threadPool;

    private TextField textArea;
    @Getter
    private AtomicInteger count = new AtomicInteger(0);

    private boolean pause = false;
    private boolean stop = true;
    private boolean start = false;

    public void init(TextField textArea) {
        this.textArea = textArea;
        textArea.setText(DateTimeUtils.getTimerTime(LocalTime.MIDNIGHT));
    }

    public void start() {
        if(start) {
            return;
        }
        start = true;
        stop = false;
        Task<Void> showTimer = new Task<>() {
            @Override
            protected Void call() throws Exception {
                count = new AtomicInteger(0);
                LocalTime timer = LocalTime.MIDNIGHT;
                while (!stop) {
                    if (!pause) {
                        Platform.runLater(() ->
                                textArea.setText(
                                        DateTimeUtils.getTimerTime(
                                                timer.plusSeconds(
                                                        count.getAndIncrement()
                                                )
                                        )
                                )
                        );
                        Thread.sleep(1000);
                    }
                }
                return null;
            }
        };
        threadPool.getService().submit(showTimer);
    }

    public void pause() {
        pause = !pause;
    }

    public void stop() {
        stop = true;
        start = false;
    }

    public void clear() {
        Task<Void> clear = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() ->
                        textArea.setText(
                                DateTimeUtils.getTimerTime(
                                        LocalTime.MIDNIGHT
                                )
                        )
                );
                return null;
            }
        };
        threadPool.getService().submit(clear);
    }
}
