package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class DataWeather {

    private Long id;

    private Long device_id;

    private String device_name;

    private Integer device_type;

    private String device_code;

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
