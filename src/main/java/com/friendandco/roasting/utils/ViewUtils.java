package com.friendandco.roasting.utils;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
}
