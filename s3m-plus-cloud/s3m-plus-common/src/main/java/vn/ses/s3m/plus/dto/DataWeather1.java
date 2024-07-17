package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class DataWeather1 {

    private Long id;

    private Long deviceId;

    private String deviceName;

    private Integer deviceType;

    private String deviceCode;

    private Integer Wind_sp;

    private Integer Wind_dir;

    private Integer Rad;

    private Integer Temp;

    private Integer H;

    private Integer Rain;

    private Integer Atmos;

    private String sentDate;

    private Long transactionDate;

}
