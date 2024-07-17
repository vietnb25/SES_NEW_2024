package vn.ses.s3m.plus.response;

import java.sql.Timestamp;

import lombok.Data;
import vn.ses.s3m.plus.dto.DeviceTypeMst;

@Data
public class DeviceTypeMstResonse {
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

    public DeviceTypeMstResonse(DeviceTypeMst d) {
        this.id = d.getId();
        this.name = d.getName();
        this.objectTypeIds = d.getObjectTypeIds();
        this.objectTypeName = d.getObjectTypeName();
        this.img = d.getImg();
        this.description = d.getDescription();
        this.createId = d.getCreateId();
        this.updateId = d.getUpdateId();
        this.createDate = d.getCreateDate();
        this.updateDate = d.getCreateDate();
    }
}
