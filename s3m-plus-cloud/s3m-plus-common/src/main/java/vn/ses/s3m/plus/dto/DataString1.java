package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class DataString1 {

    private Long id;

    private Long deviceId;

    private String deviceName;

    private Integer deviceType;

    private String deviceCode;

    private Integer InID;

    private Integer InEvt;

    private Integer LOW_VOLTAGE;

    private Integer LOW_POWER;

    private Integer LOW_EFFICIENCY;

    private Integer CURRENT;

    private Integer VOLTAGE;

    private Integer POWER;

    private Integer PR;

    private Integer DISCONNECTED;

    private Integer FUSE_FAULT;

    private Integer COMBINER_FUSE_FAULT;

    private Integer COMBINER_CABINET_OPEN;

    private Integer TEMP;

    private Integer GROUNDFAULT;

    private Integer REVERSED_POLARITY;

    private Integer INCOMPATIBLE;

    private Integer COMM_ERROR;

    private Integer INTERNAL_ERROR;

    private Integer THEFT;

    private Integer ARC_DETECTED;

    private Float IdcStr;

    private Float InDCAhr;

    private Float VdcStr;

    private Float PdcStr;

    private Float EpStr;

    private Float InDCWh;

    private Float InDCPR;

    private Float InN;

    private Float Tstr;

    private String sentDate;

    private Long transactionDate;

}
