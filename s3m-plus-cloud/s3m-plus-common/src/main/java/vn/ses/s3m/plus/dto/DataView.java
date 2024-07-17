package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class DataView {

    private Float powerLoad;

    private Float powerPV;

    private Float powerGrid;

    private Float powerWind;

    private Float powerBattery;

    private String time;

}
