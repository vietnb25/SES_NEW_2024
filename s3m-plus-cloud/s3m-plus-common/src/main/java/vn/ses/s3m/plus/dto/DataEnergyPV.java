package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class DataEnergyPV {
    private Float wh;

    private Float rad;

    private Float temp;

    private Float tStr;

    private Float whDay;

    private Float whMonth;

    private Float whYear;

    private Float whPrevDay;

    private Float whPrevMonth;

    private Float whPrevYear;

    private String infor;
}
