package vn.ses.s3m.plus.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class DeviceTypeMst {

    private Integer id;

    private String name;

    private String objectTypeIds;

    private String objectTypeName;

    private String img;

    private String description;

    private Integer createId;

    private Timestamp createDate;

    private Integer updateId;

    private Timestamp updateDate;
}
