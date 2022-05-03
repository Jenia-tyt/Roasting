package com.friendandco.roasting.component;

import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Optional;

@Component
public class CssStyleProvider {
    public Optional<String> getListViewCss() {
        return gerUrl("/css/viewlist.css").map(URL::toExternalForm);
    }

    private Optional<URL> gerUrl(String url) {
        return Optional.ofNullable(this.getClass().getResource((url)));
    }
}
