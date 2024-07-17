package vn.ses.s3m.plus.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class OverviewGridTotalPower {

    private String time;

    private Float energy;

    private Float power;

    private Long forecast;

    private Timestamp viewTime;

    private Long deviceId;

    private Forecast forecastObject;

}
