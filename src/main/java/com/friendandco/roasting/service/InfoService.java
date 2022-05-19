package com.friendandco.roasting.service;


import com.friendandco.roasting.component.Translator;
import com.friendandco.roasting.multiThread.ThreadPoolFix;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InfoService {
    private final ThreadPoolFix threadPool;
    private final Translator translator;

    private TextField textField;
    private boolean isShow = false;

    @Getter
    private boolean isInitDone = false;

    public void init(TextField textField) {
        this.textField = textField;
        this.isInitDone = true;
    }

    //TODO переделать просто в полосу с текстом она будет отображаться только для термопары
    public void showError(String message, long time) {
        Task<Void> draw = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    textField.setText(translator.getMessage("error") + " " + message);
                    textField.setStyle("-fx-background-color: rgba(255, 69, 0)");
                    isShow = true;
                });
                try {
                    Thread.sleep(time);
                    clear();
                } catch (InterruptedException e) {
                    log.error("Error in show error", e);
                }
                return null;
            }
        };
        threadPool.getService().submit(draw);
    }

    public void showWarning(String message, long time) {
        Task<Void> draw = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    textField.setText(translator.getMessage("warning") + " " + message);
                    textField.setStyle("-fx-background-color: rgb(255,234,0)");
                    isShow = true;
                });
                try {
                    Thread.sleep(time);
                    clear();
                } catch (InterruptedException e) {
                    log.error("Error in show warning", e);
                }
                return null;
            }
        };
        threadPool.getService().submit(draw);
    }

    public void showOk(String message, long time) {
        Task<Void> draw = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    textField.setText(translator.getMessage("successfully") + " " + message);
                    textField.setStyle("-fx-background-color: rgb(5,197,60)");
                    isShow = true;
                });
                try {
                    Thread.sleep(time); //TODO  выпадает ошибка разобраться
                    clear();
                } catch (InterruptedException e) {
                    log.error("Error in show ok", e);
                }
                return null;
            }
        };
        threadPool.getService().submit(draw);
    }

    public boolean isShowing() {
        if (isInitDone) {
            return isShow;
        } else {
            return false;
        }
    }

    public void clear() {
        if (isShowing()) {
            Platform.runLater(() -> {
                textField.clear();
                textField.setStyle("-fx-background-color: rgb(218,221,224)");
                isShow = false;
            });
        }
    }
}
