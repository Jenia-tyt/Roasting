package com.friendandco.roasting.service;

import com.friendandco.roasting.component.CssStyleProvider;
import com.friendandco.roasting.component.Translator;
import com.friendandco.roasting.customView.CustomPopup;
import com.friendandco.roasting.model.chart.Chart;
import com.friendandco.roasting.model.chart.ItemChart;
import com.friendandco.roasting.model.chart.LineChartDone;
import com.friendandco.roasting.model.chart.Point;
import com.friendandco.roasting.model.settings.Settings;
import com.friendandco.roasting.multiThread.ThreadPoolFix;
import com.friendandco.roasting.utils.ViewUtils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.friendandco.roasting.utils.SnakeyamlUtils.getDumperOptions;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChartLoadService {
    private final String pathPackageForSave = "./charts/";
    private final Translator translator;
    private final BeanFactory beanFactory;
    private final ThreadPoolFix threadPool;
    private final Settings settings;
    private final CssStyleProvider cssStyleProvider;

    private ListView<ItemChart> listView;
    private LineChart<Double, Double> lineChart;
    private NumberAxis xAxis;
    private NumberAxis yAxis;
    private ContextMenu cm;
    @Getter
    private final ConcurrentHashMap<String, XYChart.Series<Double, Double>> loadCharts = new ConcurrentHashMap<>();

    public void init(
            ListView<ItemChart> listView,
            LineChart<Double, Double> lineChart,
            NumberAxis xAxis,
            NumberAxis yAxis
    ) {
        this.listView = listView;
        this.lineChart = lineChart;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        initContextMenu();

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setCellFactory(CheckBoxListCell.forListView(ItemChart::onProperty));
        listView.setOnMouseClicked(event -> {
            if (MouseButton.PRIMARY == event.getButton() && event.getClickCount() == 2) {
                chooseChart();
            } else if (MouseButton.SECONDARY == event.getButton() && isChooseItem()) {
                cm.show(listView, event.getScreenX(), event.getScreenY());
            } else if (MouseButton.PRIMARY == event.getButton() && event.getClickCount() == 1) {
                if (cm.isShowing()) {
                    cm.hide();
                }
            }
        });
        loadItems();
        settings.getLoadCharts().forEach(nameChart ->
                Optional.ofNullable(loadChart(nameChart)).ifPresent(lineChartDone -> {
                    loadCharts.put(nameChart, lineChartDoneToXYChart(lineChartDone));
                    chooseChart(nameChart);
                }));
    }

    public void save(LineChart<Double, Double> saveChart) {
        ObservableList<XYChart.Series<Double, Double>> chartData = saveChart.getData();
        if (chartData.get(0).getData().isEmpty()) {
            CustomPopup customPopup = new CustomPopup();
            customPopup.createPopupWarning(
                    translator.getMessage("warning"),
                    translator.getMessage("chart.is.empty"),
                    listView.getScene().getWindow(),
                    cssStyleProvider
            );
            return;
        }
        fillChartName(saveChart);
    }

    public void reload() {
        loadCharts.keySet().forEach(this::setOnForReloadChart);
    }

    private void fillChartName(LineChart<Double, Double> saveChart) {
        Popup popup = new Popup();

        TextField textField = new TextField();
        textField.setText(saveChart.getTitle());

        Label label = new Label(translator.getMessage("chart.name.for.save"));

        Button save = new Button(translator.getMessage("button.save"));
        save.setOnAction(event -> {
            popup.hide();
            String nameChart = textField.getText();
            if (StringUtils.isBlank(nameChart)) {
                empty(saveChart);
            } else if (isNormalName(nameChart)) {
                saveData(saveChart, textField.getText());
            } else {
                confirmation(saveChart, textField.getText());
            }
        });
        Button cansel = new Button(translator.getMessage("button.cansel"));
        cansel.setOnAction(event -> popup.hide());

        HBox hBox = ViewUtils.creatHBox(cansel, save);
        VBox dialogVbox = ViewUtils.creatVBox(label, textField, hBox);

        cssStyleProvider.getButtonCss().ifPresent(css -> dialogVbox.getStylesheets().add(css));
        cssStyleProvider.getFillChartNameCss().ifPresent(css -> dialogVbox.getStylesheets().add(css));

        popup.getContent().add(dialogVbox);
        popup.show(lineChart.getScene().getWindow());
    }

    private void empty(LineChart<Double, Double> saveChart) {
        Popup popup = new Popup();

        Label message = new Label(translator.getMessage("empty.name.chart"));

        Button ok = new Button("Ok");
        ok.setOnAction(event -> {
            popup.hide();
            fillChartName(saveChart);
        });
        ok.setAlignment(Pos.CENTER);

        VBox dialogVbox = ViewUtils.creatVBox(message, ok);

        cssStyleProvider.getButtonCss().ifPresent(css -> dialogVbox.getStylesheets().add(css));
        cssStyleProvider.getEmptyCss().ifPresent(css -> dialogVbox.getStylesheets().add(css));

        popup.getContent().add(dialogVbox);
        popup.show(listView.getScene().getWindow());
    }

    private void confirmation(LineChart<Double, Double> saveChart, String nameChart) {
        Popup popup = new Popup();

        Label message = new Label(translator.getMessage("confirmation"));

        Button replace = new Button(translator.getMessage("button.replace"));

        replace.setOnAction(event -> {
            saveData(saveChart, nameChart);
            popup.hide();
        });

        Button cansel = new Button(translator.getMessage("button.cansel"));
        cansel.setOnAction(event -> {
            fillChartName(saveChart);
            popup.hide();
        });

        HBox hBox = ViewUtils.creatHBox(cansel, replace);
        VBox dialogVbox = ViewUtils.creatVBox(message, hBox);

        cssStyleProvider.getButtonCss().ifPresent(css -> dialogVbox.getStylesheets().add(css));
        cssStyleProvider.getConfirmationCss().ifPresent(css -> dialogVbox.getStylesheets().add(css));

        popup.getContent().add(dialogVbox);
        popup.show(listView.getScene().getWindow());
    }

    private boolean isNormalName(String nameChart) {
        return getNameCharts().stream()
                .map(ItemChart::getName)
                .noneMatch(nameChart::equals);
    }

    private void saveData(LineChart<Double, Double> saveChart, String name) {
        LineChartDone data = new LineChartDone();
        data.setName(name);

        saveChart.getData().forEach(itemData -> {
            Chart chart = new Chart();
            chart.setName(itemData.getName());
            itemData.getData().forEach(intData ->
                    chart.getPoints().add(
                            new Point(
                                    intData.getXValue(),
                                    intData.getYValue()
                            )
                    )
            );
            data.setChart(chart);
        });
        write(data, name);
        addSaveItem(new ItemChart(name));
    }

    private void setOnForReloadChart(String nameChart) {
        listView.getItems().forEach(chartItem -> {
            if (nameChart.equals(chartItem.getName())) {
                chartItem.setOn(true);
            }
        });
    }

    private void addSaveItem(ItemChart itemChart) {
        listView.getItems().add(itemChart);
    }

    private void chooseChart() {
        Optional.ofNullable(listView.getSelectionModel().selectedItemProperty().getValue())
                .ifPresent(itemChart -> itemChart.setOn(!itemChart.isOn()));
    }

    private void chooseChart(String nameChart) {
        listView.getItems().stream()
                .filter(name -> name.getName().equals(nameChart))
                .findFirst()
                .ifPresent(itemChart -> itemChart.setOn(true));
    }

    private void removeLoadChart(String name) {
        Task<Void> test = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    lineChart.getData().remove(loadCharts.get(name));
                    loadCharts.remove(name);
                });
                return null;
            }
        };
        threadPool.getService().submit(test);
    }

    private void fillChart(LineChartDone chartDone) {
        Task<Void> draw = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                            prepareAxis(chartDone);

                            XYChart.Series<Double, Double> dataChart = lineChartDoneToXYChart(chartDone);
                            lineChart.getData().add(dataChart);
                            loadCharts.put(chartDone.getName(), dataChart);
                        }
                );
                return null;
            }
        };
        threadPool.getService().submit(draw);
    }

    private void removeItem() {
        listView.getSelectionModel().getSelectedItems().forEach(itemChart -> {
            File file = new File(pathPackageForSave + itemChart.getName() + ".yaml");
            if (file.exists()) {
                file.delete();
            }
            removeDifferenceChart();
        });
        loadItems();
    }

    private LineChartDone loadChart(String name) {
        try {
            Yaml yaml = new Yaml(new Constructor(LineChartDone.class));
            File file = new File(pathPackageForSave + name + ".yaml");
            return yaml.load(new FileInputStream(file));
        } catch (Exception e) {
            log.error("Chart can't load " + e.getMessage());
            return null;
        }
    }

    public void loadItems() {
        listView.getItems().clear();
        listView.getItems().addAll(getNameCharts());
    }

    public void clearLoadChartsUuids() {
        loadCharts.clear();
    }

    private List<ItemChart> getNameCharts() {
        try (Stream<Path> paths = Files.walk(Paths.get(pathPackageForSave))) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.endsWith(".yaml"))
                    .map(name -> name.replace(".yaml", ""))
                    .map(name -> new ItemChart(name, false))
                    .peek(itemChart ->
                            itemChart.onProperty().addListener(
                                    (observable, oldValue, newValue) -> {
                                        if (newValue) {
                                            fillChart(loadChart(itemChart.getName()));
                                        } else {
                                            removeLoadChart(itemChart.getName());
                                            removeDifferenceChart();
                                        }
                                    }
                            )
                    )
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Package can't read" + pathPackageForSave);
            return new ArrayList<>();
        }
    }

    private void write(LineChartDone lineChartDone, String name) {
        lineChartDone.setName(name);
        String fullPath = pathPackageForSave + name + ".yaml";
        File file = new File(fullPath);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                if (!file.createNewFile()) {
                    log.warn("File " + file.getName() + "was not create");
                }
            }
            PrintWriter printWriter = new PrintWriter(file);
            Yaml yaml = new Yaml(getDumperOptions());
            yaml.dump(lineChartDone, printWriter);
        } catch (IOException e) {
            log.error(String.format("Settings can't write in file %s", fullPath), e.getMessage());
        }
    }

    private boolean isChooseItem() {
        return Optional.ofNullable(listView.getSelectionModel().selectedItemProperty().getValue()).isPresent();
    }

    private void initContextMenu() {
        cm = new ContextMenu();
        MenuItem delete = new MenuItem();
        delete.setText(translator.getMessage("button.delete"));
        delete.setOnAction(event -> removeItem());

        cm.getItems().add(delete);
        cm.setAutoFix(true);
    }

    private void prepareAxis(LineChartDone chartDone) {
        LinkedList<Point> points = chartDone.getChart().getPoints();
        Optional.ofNullable(points.getLast())
                .ifPresent(point -> {
                    if (point.getX() > xAxis.getUpperBound()) {
                        xAxis.setUpperBound(point.getX());
                    }

                    if (point.getY() > yAxis.getUpperBound()) {
                        yAxis.setUpperBound(point.getY());
                    }
                });
    }

    private void removeDifferenceChart() {
        DifferenceCalculationService differenceCalculationService =
                beanFactory.getBean(
                        "differenceCalculationService",
                        DifferenceCalculationService.class
                );
        if (differenceCalculationService.isLoadChart()) {
            differenceCalculationService.clear();
        }
    }

    private XYChart.Series<Double, Double> lineChartDoneToXYChart(LineChartDone chartDone) {
        XYChart.Series<Double, Double> dataChart = new XYChart.Series<>();
        dataChart.setName(chartDone.getName());
        chartDone.getChart().getPoints().forEach(point ->
                dataChart.getData().add(new XYChart.Data<>(
                        point.getX(),
                        point.getY()
                ))
        );
        return dataChart;
    }
}
