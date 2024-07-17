package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class DataCombiner1 {

    private Long id;

    private Long deviceId;

    private String deviceName;

    private Integer deviceType;

    private String deviceCode;

    private Integer DCAMax;

    private Integer N;

    private Integer Evt;

    private Integer LOW_VOLTAGE;

    private Integer LOW_POWER;

    private Integer LOW_EFFICIENCY;

    private Integer CURRENT;

    private Integer VOLTAGE;

    private Integer POWER;

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

    private Float IdcCombiner;

    private Float DCAh;

    private Float VdcCombiner;

    private Float T;

    private Float PdcCombiner;

    private Float PR;

    private Float EpCombiner;

    private String sentDate;

    private Long transactionDate;

    private String viewTime;

    private Integer viewType;
}
