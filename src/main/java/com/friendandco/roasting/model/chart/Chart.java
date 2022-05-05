package com.friendandco.roasting.model.chart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.LinkedList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chart implements Serializable {
    private String name;
    private LinkedList<Point> points = new LinkedList<>();
}
