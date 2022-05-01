package com.friendandco.roasting.model.settings;

import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Data
public class LineChartSettings implements Serializable {
    private String valueTemperature;

    private List<Point> points = new LinkedList<>();
}
