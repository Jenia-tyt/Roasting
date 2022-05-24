package com.friendandco.roasting.service;

import com.friendandco.roasting.component.CssStyleProvider;
import com.friendandco.roasting.component.Translator;
import com.friendandco.roasting.utils.ViewUtils;
import javafx.scene.control.Menu;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TopMenuService {
    private final Translator translator;
    private final CssStyleProvider provider;

    private Menu topMenu;
    private BorderPane root;

    public void init(Menu menu, BorderPane root) {
        this.topMenu = menu;
        this.root = root;

        topMenu.getItems().get(0)
                .setOnAction(event -> showAbout());
    }

    //TODO нормально наполнить попап инфой
    private void showAbout() {
        VBox dialogVbox = new VBox(10);
        provider.getPopupInfoCss().ifPresent(css -> dialogVbox.getStylesheets().add(css));

        Popup popup = ViewUtils.creatPopup("TEST", "message", dialogVbox);
        popup.show(root.getScene().getWindow());
    }
}
