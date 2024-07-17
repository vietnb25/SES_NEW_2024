package vn.ses.s3m.plus.dto;

import java.math.BigInteger;

import lombok.Data;

@Data
public class OverviewLoadPower {

    private Long deviceId;

    private String deviceName;

    private Float pTotal;

    private Long eP;

    private Integer systemMapId;

    private Integer systemTypeId;

    private Integer projectId;

    private String loadStatus;

    private String layer;

    private String systemMapName;

    private Integer warningCount;

    private BigInteger id;

    private Long transactionDate;

    private Integer loadCount;

    private Float maxPtotal;

    private Float minPtotal;

    private Float avgTotal;

    private Float realTime;

    private Long sumEnergy;

    private Long sumEnergyCurrentYear;

    private Long sumEnergyLastYear;

    private Long sumEnergyToday;

    private Long sumEnergyPreday;

    private Long sumEnergyCurMonth;

    private Long sumEnergyPreMonth;

    private String address;

    private String image;

}
