package com.friendandco.roasting.service;

import com.friendandco.roasting.model.settings.Settings;
import com.friendandco.roasting.model.settings.SettingsAxis;
import com.friendandco.roasting.model.units.TemperatureUnits;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SettingsDrawService {
    private final Settings settings;

    private Button tempUnits;
    private Spinner<Double> tempCoefficient;
    private Spinner<Integer> xStart;
    private Spinner<Integer> yStart;
    private Spinner<Integer> xEnd;
    private Spinner<Integer> yEnd;

    private SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(true);

    public void init(
            Button tempUnits,
            Spinner<Double> tempCoefficient,
            Spinner<Integer> xStart,
            Spinner<Integer> yStart,
            Spinner<Integer> xEnd,
            Spinner<Integer> yEnd
    ) {
        this.tempUnits = tempUnits;
        this.tempCoefficient = tempCoefficient;
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;


        tempUnits.setOnAction(event -> switchedOn.set(!switchedOn.get()));

        switchedOn.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                drawCelsius();
            } else {
                drawFahrenheit();
            }
        });

        TemperatureUnits temperatureUnits = settings.getTemperatureUnits();
        switch (temperatureUnits) {
            case CELSIUS:
                drawCelsius();
                break;
            case FAHRENHEIT:
                drawFahrenheit();
                break;
        }


        SpinnerValueFactory.DoubleSpinnerValueFactory coefficientRange = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0);
        coefficientRange.setAmountToStepBy(0.1);
        coefficientRange.setValue(settings.getCorrectionFactor());
        tempCoefficient.setValueFactory(coefficientRange);

        SettingsAxis xAxis = settings.getXAxis();
        SpinnerValueFactory.IntegerSpinnerValueFactory rangeXStart = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120);
        rangeXStart.setValue(xAxis.getLowerBound());
        xStart.setValueFactory(rangeXStart);

        SpinnerValueFactory.IntegerSpinnerValueFactory rangeYStart = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120);
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
        xAxis.setLowerBound(xStart.getValue());
        xAxis.setUpperBound(xEnd.getValue());

        SettingsAxis yAxis = settings.getYAxis();
        yAxis.setLowerBound(yStart.getValue());
        yAxis.setUpperBound(yEnd.getValue());

        for (TemperatureUnits units : TemperatureUnits.values()) {
            if (units.getDesignation().equals(tempUnits.getText())) {
                settings.setTemperatureUnits(units);
                return;
            }
        }
    }

    private void drawCelsius() {
        tempUnits.setText(TemperatureUnits.CELSIUS.getDesignation());
        tempUnits.setStyle("-fx-background-color: #6bc90e;-fx-text-fill:white;");
        tempUnits.setContentDisplay(ContentDisplay.RIGHT);
    }

    private void drawFahrenheit() {
        tempUnits.setText(TemperatureUnits.FAHRENHEIT.getDesignation());
        tempUnits.setStyle("-fx-background-color: #bd2322;-fx-text-fill:white;");
        tempUnits.setContentDisplay(ContentDisplay.LEFT);
    }
}
// TODO добавить валидацию что начало оси раньше конца в случае вызывать поп
