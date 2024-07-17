package vn.ses.s3m.plus.response;

import lombok.Data;
import vn.ses.s3m.plus.dto.ObjectName;

@Data
public class ObjectResponse {

    private Integer id;

    private String objectName;

    private Integer objectTypeId;

    private String objectTypeName;

    private Integer projectId;

    private String projcetName;

    private String createDate;

    private String updateDate;

    public ObjectResponse(final ObjectName objectName) {
        this.objectTypeId = objectName.getObjectTypeId();
        this.objectTypeName = objectName.getObjectTypeName();
        this.objectName = objectName.getObjectName();
        this.projectId = objectName.getProjectId();
        this.projcetName = objectName.getProjectName();
        this.id = objectName.getId();
        this.createDate = objectName.getCreateDate();
        this.updateDate = objectName.getUpdateDate();
    }
}
