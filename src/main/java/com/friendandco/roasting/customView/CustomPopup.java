package com.friendandco.roasting.customView;

import com.friendandco.roasting.component.CssStyleProvider;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Window;

public class CustomPopup {

    public void createPopupWarning(String titel, String message, Window window, CssStyleProvider provider) {
        VBox dialogVbox = new VBox(10);
        provider.getPopupWarningCss().ifPresent(css -> dialogVbox.getStylesheets().add(css));

        Popup popup = creatPopup(titel, message, dialogVbox);
        popup.show(window);
    }

    public void createPopupError(String titel, String message, Window window, CssStyleProvider provider) {
        VBox dialogVbox = new VBox(10);
        provider.getPopupErrorCss().ifPresent(css -> dialogVbox.getStylesheets().add(css));

        Popup popup = creatPopup(titel, message, dialogVbox);
        popup.show(window);
    }

    public void createPopupInfo(String titel, String message, Window window, CssStyleProvider provider) {
        VBox dialogVbox = new VBox(10);
        provider.getPopupInfoCss().ifPresent(css -> dialogVbox.getStylesheets().add(css));

        Popup popup = creatPopup(titel, message, dialogVbox);
        popup.show(window);
    }

    private Popup creatPopup(String titel, String message, VBox dialogVbox) {
        Popup popup = new Popup();

        dialogVbox.setAlignment(Pos.CENTER);
        Button button = new Button();
        button.setAlignment(Pos.BOTTOM_CENTER);
        button.setText("Ok");
        button.setOnAction(event -> popup.hide());

        dialogVbox.getChildren().add(new Text(titel));
        dialogVbox.getChildren().add(new Text(message));
        dialogVbox.getChildren().add(button);
        popup.getContent().add(dialogVbox);

        return popup;
    }
}
