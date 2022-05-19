package com.friendandco.roasting.component;

import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Optional;

@Component
public class CssStyleProvider {
    public Optional<String> getListViewCss() {
        return gerUrl("/css/viewlist.css").map(URL::toExternalForm);
    }

    public Optional<String> getPopupWarningCss() {
        return gerUrl("/css/popupWarning.css").map(URL::toExternalForm);
    }

    public Optional<String> getPopupErrorCss() {
        return gerUrl("/css/popupError.css").map(URL::toExternalForm);
    }

    public Optional<String> getPopupInfoCss() {
        return gerUrl("/css/popupInfo.css").map(URL::toExternalForm);
    }

    public Optional<String> getChartCss() {
        return gerUrl("/css/chart.css").map(URL::toExternalForm);
    }

    private Optional<URL> gerUrl(String url) {
        return Optional.ofNullable(this.getClass().getResource((url)));
    }
}
