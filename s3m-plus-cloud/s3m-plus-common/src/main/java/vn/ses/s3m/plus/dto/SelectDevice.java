package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class SelectDevice {
    private String location;
    private Integer objectTypeId;
    private String objectTypeName;
}
