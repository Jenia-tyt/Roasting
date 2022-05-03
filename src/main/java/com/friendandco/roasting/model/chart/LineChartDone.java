package com.friendandco.roasting.model.chart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineChartDone implements Serializable {
    private UUID uuid = UUID.randomUUID();
    private String name;
    private Chart chart;
}
