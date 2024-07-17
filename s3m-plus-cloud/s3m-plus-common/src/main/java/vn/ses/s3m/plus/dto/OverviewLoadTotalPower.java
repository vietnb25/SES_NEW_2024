package vn.ses.s3m.plus.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class OverviewLoadTotalPower {

    private String time;

    private Long energy;

    private Long power;

    private Long forecast;

    private Timestamp viewTime;

    private Long deviceId;

    private Forecast forecastObject;

    private Long id;
}
