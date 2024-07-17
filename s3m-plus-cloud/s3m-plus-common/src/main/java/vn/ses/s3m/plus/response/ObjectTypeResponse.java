package vn.ses.s3m.plus.response;

import lombok.Data;
import vn.ses.s3m.plus.dto.ObjectType;

@Data
public class ObjectTypeResponse {

    private Integer objectTypeId;

    private String objectTypeName;

    private Integer systemTypeId;

    private String img;

    private String createDate;

    private String updateDate;

    public ObjectTypeResponse(final ObjectType objectType) {
        this.objectTypeId = objectType.getObjectTypeId();
        this.objectTypeName = objectType.getObjectTypeName();
        this.systemTypeId = objectType.getObjectTypeId();
        this.img = objectType.getImg();
        this.createDate = objectType.getCreateDate();
        this.updateDate = objectType.getUpdateDate();
    }
}
