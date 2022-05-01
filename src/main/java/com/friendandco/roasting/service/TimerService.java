package com.friendandco.roasting.service;

import com.friendandco.roasting.multiThread.ThreadPoolFix;
import com.friendandco.roasting.utils.DateTimeUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@RequiredArgsConstructor
public class TimerService {
    private final ThreadPoolFix threadPool;

    private Task<Void> showTimer;
    private TextField textArea;

    private boolean pause = false;
    private boolean stop = true;

    public void init(TextField textArea) {
        this.textArea = textArea;
    }

    public void start() {
        if (stop) {
            stop = false;
            showTimer = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    AtomicInteger count = new AtomicInteger(0);
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
    }

    public void pause() {
        pause = !pause;
    }

    public void stop() {
        stop = !stop;
        showTimer.cancel();
    }
}
