package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class DataPanel1 {

    private Long id;

    private Long deviceId;

    private String deviceName;

    private Integer deviceType;

    private String deviceCode;

    private Integer Temp_panel;

    private Integer V;

    private Integer JA_SOLAR;

    private Integer EA_SOLAR;

    private Integer LONGI_SOLAR;

    private Float P;

    private Float T;

    private Float I;

    private Float U;

    private String sentDate;

    private Long transactionDate;

}
