package vn.ses.s3m.plus.form;

import lombok.Data;

@Data
public class ObjectTypeForm {

    private Integer objectTypeId;

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

    private Integer projectId;

    private Integer typeDefault;

    private Integer deleteFlag;

}