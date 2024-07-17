package vn.ses.s3m.plus.dto;

import java.math.BigInteger;

import lombok.Data;

@Data
public class OverviewGridPower {
    private Long deviceId;

    private String deviceName;

    private Integer deviceType;

    private Long pTotal;

    private Integer indicator;

    private Float temp;

    private Float humidity;

    private Integer systemMapId;

    private Integer systemTypeId;

    private Integer deviceStatus;

    private Integer projectId;

    private String loadStatus;

    private String layer;

    private String systemMapName;

    private Integer warningCount;

    private BigInteger id;

    private Long transactionDate;

    private Float maxPtotal;

    private Float minPtotal;

    private Float avgPtotal;

    private Float realTime;

    private Long sumEnergyGrid;

    private Long sumEnergyCurrentYearGrid;

    private Long sumEnergyLastYearGrid;

    private Long sumEnergyTodayGrid;

    private Long sumEnergyPredayGrid;

    private Long sumEnergyCurMonthGrid;

    private Long sumEnergyPreMonthGrid;

    private Integer gridCount;

    private String address;

    private String image;
}
