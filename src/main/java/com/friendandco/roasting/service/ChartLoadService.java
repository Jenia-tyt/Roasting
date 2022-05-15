package com.friendandco.roasting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.friendandco.roasting.component.CssStyleProvider;
import com.friendandco.roasting.component.Translator;
import com.friendandco.roasting.customView.CustomPopup;
import com.friendandco.roasting.model.chart.Chart;
import com.friendandco.roasting.model.chart.ItemChart;
import com.friendandco.roasting.model.chart.LineChartDone;
import com.friendandco.roasting.model.chart.Point;
import com.friendandco.roasting.multiThread.ThreadPoolFix;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChartLoadService {
    private final String pathPackageForSave = "./src/main/resources/charts/";
    private final Translator translator;
    private final ThreadPoolFix threadPool;
    private final CssStyleProvider cssStyleProvider;

    private ListView<ItemChart> listView;
    private NumberAxis xAxis;
    private NumberAxis yAxis;
    private LineChart<Double, Double> lineChart;
    private ContextMenu cm;
    @Getter
    private final ConcurrentHashMap<String, XYChart.Series<Double, Double>> loadCharts = new ConcurrentHashMap();


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
    }

    public void save(LineChart<Double, Double> lineChart) {
        LineChartDone data = new LineChartDone();
        if (data.getChart() == null) {
            CustomPopup customPopup = new CustomPopup();
            customPopup.createPopupWarning(
                    translator.getMessage("warning"),
                    translator.getMessage("chart.is.empty"),
                    listView.getScene().getWindow(),
                    cssStyleProvider
            );
            return;
        }
        data.setName(lineChart.getTitle());

        lineChart.getData().forEach(itemData -> {
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
        write(data);
        loadItems();
    }

    private void chooseChart() {
        Optional.ofNullable(listView.getSelectionModel().selectedItemProperty().getValue())
                .ifPresent(itemChart -> itemChart.setOn(!itemChart.isOn()));
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
        if (checkLoadChart(chartDone)) {
            CustomPopup customPopup = new CustomPopup();
            customPopup.createPopupWarning(
                    translator.getMessage("warning"),
                    translator.getMessage("chart.it.is.same"),
                    listView.getScene().getWindow(),
                    cssStyleProvider
            );
            return;
        }
        Task<Void> draw = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                            prepareAxis(chartDone);

                            XYChart.Series<Double, Double> dataChart = new XYChart.Series<>();
                            dataChart.setName(chartDone.getName());
                            chartDone.getChart().getPoints().forEach(point ->
                                    dataChart.getData().add(
                                            new XYChart.Data<>(
                                                    point.getX(),
                                                    point.getY()
                                            )
                                    )
                            );
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
        Optional.ofNullable(listView.getSelectionModel().selectedItemProperty().getValue())
                .ifPresent(name -> {
                    File file = new File(pathPackageForSave + name + ".yaml");
                    if (file.exists()) {
                        file.delete();
                    }
                    loadItems();
                });
    }

    private LineChartDone loadChart(String name) {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            File file = new File(pathPackageForSave + name + ".yaml");
            return objectMapper.readValue(file, LineChartDone.class);
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

    private boolean checkLoadChart(LineChartDone chartDone) {
        return loadCharts.containsKey(chartDone.getName());
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

    //TODO повторяющиесяяимена
    private void write(LineChartDone lineChartDone) {
        Random r = new Random();
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        //TODO на имени завязана логика удаления загурежнных графиков
        String saveName = lineChartDone.getName() + r.nextInt();
        lineChartDone.setName(saveName);
        String fullPath = pathPackageForSave + saveName + ".yaml";
        File file = new File(fullPath);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                if (!file.createNewFile()) {
                    log.warn("File " + file.getName() + "was not create");
                    //TODO тут надо выавать попап
                    // не правильно кладется имя не туда
                }
            }
            objectMapper.writeValue(file, lineChartDone);
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
}
