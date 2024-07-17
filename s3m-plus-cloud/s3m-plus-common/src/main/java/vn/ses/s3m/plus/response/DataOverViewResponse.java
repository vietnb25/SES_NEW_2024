package vn.ses.s3m.plus.response;

import java.math.BigInteger;

import lombok.Data;

@Data
public class DataOverViewResponse {

    private Long deviceId;

    private String deviceName;

    private Float pTotal;

    private Integer warningCount;

    private BigInteger id;

    private Long transactionDate;

    private Integer loadCount;

    private Float maxPtotal;

    private Float minPtotal;

    private Float realTime;

    private Long sumEnergy;

    private Long sumEnergyCurrentYear;

    private Long sumEnergyLastYear;

    private Long sumEnergyToday;

    private Long sumEnergyPreday;

    private Long sumEnergyCurMonth;

    private Long sumEnergyPreMonth;

}
