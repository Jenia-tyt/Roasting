package com.friendandco.roasting.service;

import com.friendandco.roasting.component.CssStyleProvider;
import com.friendandco.roasting.component.Translator;
import com.friendandco.roasting.customView.CustomPopup;
import com.friendandco.roasting.model.settings.Settings;
import com.friendandco.roasting.model.settings.SettingsAxis;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SettingsDrawService {
    private final Settings settings;
    private final Translator translator;
    private final CssStyleProvider cssStyleProvider;

    private Spinner<Double> tempCoefficient;
    private Spinner<Integer> xStart;
    private Spinner<Integer> yStart;
    private Spinner<Integer> xEnd;
    private Spinner<Integer> yEnd;

    private SpinnerValueFactory.IntegerSpinnerValueFactory rangeXStart;
    private SpinnerValueFactory.IntegerSpinnerValueFactory rangeYStart;

    public void init(
            Spinner<Double> tempCoefficient,
            Spinner<Integer> xStart,
            Spinner<Integer> yStart,
            Spinner<Integer> xEnd,
            Spinner<Integer> yEnd
    ) {
        this.tempCoefficient = tempCoefficient;
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;

        SpinnerValueFactory.DoubleSpinnerValueFactory coefficientRange = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0);
        coefficientRange.setAmountToStepBy(0.1);
        coefficientRange.setValue(settings.getCorrectionFactor());
        tempCoefficient.setValueFactory(coefficientRange);

        SettingsAxis xAxis = settings.getXAxis();
        rangeXStart = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120);
        rangeXStart.setValue(xAxis.getLowerBound());
        xStart.setValueFactory(rangeXStart);

        rangeYStart = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120);
        rangeYStart.setValue(xAxis.getUpperBound());
        xEnd.setValueFactory(rangeYStart);

        SettingsAxis yAxis = settings.getYAxis();
        SpinnerValueFactory.IntegerSpinnerValueFactory rangeXEnd = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 600);
        rangeXEnd.setValue(yAxis.getLowerBound());
        yStart.setValueFactory(rangeXEnd);

        SpinnerValueFactory.IntegerSpinnerValueFactory rangeYEnd = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 600);
        rangeYEnd.setValue(yAxis.getUpperBound());
        yEnd.setValueFactory(rangeYEnd);
    }

    public void save() {
        settings.setCorrectionFactor(tempCoefficient.getValue());
        SettingsAxis xAxis = settings.getXAxis();
        SettingsAxis yAxis = settings.getYAxis();

        if (!checkAixs()) {
            return;
        }

        xAxis.setLowerBound(xStart.getValue());
        xAxis.setUpperBound(xEnd.getValue());

        yAxis.setLowerBound(yStart.getValue());
        yAxis.setUpperBound(yEnd.getValue());
    }

    private boolean checkAixs() {
        if (xStart.getValue() >= xEnd.getValue()) {
            CustomPopup customPopup = new CustomPopup();
            customPopup.createPopupWarning(
                    translator.getMessage("warning"),
                    String.format(translator.getMessage("warning.axis"), "X"),
                    tempCoefficient.getScene().getWindow(),
                    cssStyleProvider
            );
            rangeXStart.setValue(xEnd.getValue() - 1);
            return false;
        }
        if (yStart.getValue() >= yEnd.getValue()) {
            CustomPopup customPopup = new CustomPopup();
            customPopup.createPopupWarning(
                    translator.getMessage("warning"),
                    String.format(translator.getMessage("warning.axis"), "Y"),
                    tempCoefficient.getScene().getWindow(),
                    cssStyleProvider
            );
            rangeYStart.setValue(yEnd.getValue() - 1);
            return false;
        }
        return true;
    }
}
