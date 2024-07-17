package vn.ses.s3m.plus.auth.service;

import java.util.Map;

import vn.ses.s3m.plus.dto.PermissionCategoryData;
import vn.ses.s3m.plus.dto.PermissionMapData;
import vn.ses.s3m.plus.dto.PermissionTreeData;

public interface PermissionService {
    void addTreeDataPermission(Map<String, Object> condition);

    void addMapDataPermission(Map<String, Object> condition);

    PermissionTreeData getTreeData(Map<String, Object> condition);

    PermissionMapData getMapData(Map<String, Object> condition);

    PermissionCategoryData getCategoryPermission(Map<String, Object> condition);
}
