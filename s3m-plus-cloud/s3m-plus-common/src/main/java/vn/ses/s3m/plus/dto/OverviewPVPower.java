package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class OverviewPVPower {

    private Long deviceId;

    private Integer deviceType;

    private String deviceName;

    private Float Wh;

    private Float W;

    private Float DCW;

    private Integer systemMapId;

    private Integer systemTypeId;

    private Integer projectId;

    private String loadStatus;

    private String layer;

    private String systemMapName;

    private Float efficiency;

    private Integer warningCount;

    private Float InDCPR;

    private Float PR;

    private Float TEMP;

    private Float H;

    private Float Rad;

    private Integer pvCount;

    private Float maxPtotal;

    private Float minPtotal;

    private Float avgPtotal;

    private Float realTime;

    private Long sumEnergyPV;

    private Long sumEnergyCurrentYearPV;

    private Long sumEnergyLastYearPV;

    private Long sumEnergyTodayPV;

    private Long sumEnergyPredayPV;

    private Long sumEnergyCurMonthPV;

    private Long sumEnergyPreMonthPV;

    private String address;

    private String image;
}
