package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class ObjectName {

    private Integer id;

    private String objectName;

    private Integer objectTypeId;

    private Integer projectId;

    private String objectTypeName;

    private String projectName;

    private String createDate;

    private String updateDate;
}
