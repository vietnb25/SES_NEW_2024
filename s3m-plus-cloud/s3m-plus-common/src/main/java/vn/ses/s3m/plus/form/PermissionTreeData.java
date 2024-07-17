package vn.ses.s3m.plus.form;

import lombok.Data;

@Data
public class PermissionTreeData {
    private String userId;
    private String dataTree;
    private String dataMarkers;
    private String categoriesPath;
    private String customerIds;
}
