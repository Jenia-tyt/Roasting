package com.friendandco.roasting.utils;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;

import java.util.Arrays;

public class ViewUtils {

    public static HBox creatHBox(Node ... nodes) {
        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER);
        Arrays.stream(nodes)
                .forEach(node -> hBox.getChildren().add(node));
        return hBox;
    }

    public static VBox creatVBox(Node ... nodes) {
        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER);
        Arrays.stream(nodes)
                .forEach(node -> vBox.getChildren().add(node));
        return vBox;
    }

    public static Popup creatPopup(String title, String message, VBox dialogVbox) {
        Popup popup = new Popup();

        dialogVbox.setAlignment(Pos.CENTER);
        Button button = new Button();
        button.setAlignment(Pos.BOTTOM_CENTER);
        button.setText("Ok");
        button.setOnAction(event -> popup.hide());

        dialogVbox.getChildren().add(new Text(title));
        dialogVbox.getChildren().add(new Text(message));
        dialogVbox.getChildren().add(button);
        popup.getContent().add(dialogVbox);

        return popup;
    }
}
