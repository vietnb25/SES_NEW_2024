package vn.ses.s3m.plus.form;

import lombok.Data;

@Data
public class ObjectForm {

    private Integer id;

    private String objectName;

    private Integer objectTypeId;

    private Integer projectId;

    private String objectTypeName;

    private String createDate;

    private String updateDate;

}
