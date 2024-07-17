package vn.ses.s3m.plus.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class DeviceType {

    private Integer id;

    private Integer deviceTypeId;

    private String deviceTypeName;

    private String deviceInitial;

    private String description;

    private Integer createId;

    private Timestamp createDate;

    private Integer updateId;

    private Timestamp updateDate;
}
