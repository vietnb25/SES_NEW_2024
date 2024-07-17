package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class ObjectType {
    private Integer objectTypeId;

    private Integer objectId;

    private String objectTypeName;

    private Integer countDevice;

    private Integer typeClass;

    private String status;

    private Integer systemTypeId;

    private Integer deviceTypeId;

    private String area;

    private String img;

    private String createDate;

    private String updateDate;

    private String customerId;

    private String projectId;

    private Integer typeDefault;

    private Integer deleteFlag;

    private String objectName;

    private String projectName;
    
    private String customerName;

}
