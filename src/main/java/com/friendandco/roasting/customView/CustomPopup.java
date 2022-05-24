package com.friendandco.roasting.customView;

import com.friendandco.roasting.component.CssStyleProvider;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;

import static com.friendandco.roasting.utils.ViewUtils.creatPopup;

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
}
