package com.friendandco.roasting.customView;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Window;

public class CustomPopup {

    public void createPopupWarning(String titel, String message, Window window) {
        VBox dialogVbox = new VBox(10);
        dialogVbox.setStyle(
                "-fx-background-color:#f6e33a" + ";"
                        + " -fx-font-size: " + 20 + ";"
                        + " -fx-padding: 10px;"
                        + " -fx-background-radius: 10;"
                        + " -fx-border-color: #131313" +  ";"
                        + " -fx-border-width: 2" + ";"
                        + " -fx-border-radius: 10" + ";"
        );

        Popup popup = creatPopup(titel, message, dialogVbox);

        popup.show(window);
    }

    public void createPopupError(String titel, String message, Window window) {
        VBox dialogVbox = new VBox(10);
        dialogVbox.setStyle(
                "-fx-background-color:#f83030" + ";"
                        + " -fx-font-size: " + 20 + ";"
                        + " -fx-padding: 10px;"
                        + " -fx-background-radius: 10;"
                        + " -fx-border-color: #131313" +  ";"
                        + " -fx-border-width: 2" + ";"
                        + " -fx-border-radius: 10" + ";"
        );

        Popup popup = creatPopup(titel, message, dialogVbox);

        popup.show(window);
    }

    public void createPopupInfo(String titel, String message, Window window) {
        VBox dialogVbox = new VBox(10);
        dialogVbox.setStyle(
                "-fx-background-color:#67b919" + ";"
                        + " -fx-font-size: " + 20 + ";"
                        + " -fx-padding: 10px;"
                        + " -fx-background-radius: 10;"
                        + " -fx-border-color: #131313" +  ";"
                        + " -fx-border-width: 2" + ";"
                        + " -fx-border-radius: 10" + ";"
        );

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
        button.setStyle(
                "-fx-background-color:#01b604" + ";"
       +" -fx-background-radius:0;"
        + "-fx-border-color:black;"
        + "-fx-border-width: 0 3 3 0;"
        + "-fx-background-insets: 0;"
        );

        dialogVbox.getChildren().add(new Text(titel));
        dialogVbox.getChildren().add( new Text(message));
        dialogVbox.getChildren().add(button);
        popup.getContent().add(dialogVbox);

        return popup;
    }
}
