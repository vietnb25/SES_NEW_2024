package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class ManufactureShiftDevices {
    private  Integer id;
    private Integer deviceId;
    private Double ep;
    private Integer manufactureId;
}
