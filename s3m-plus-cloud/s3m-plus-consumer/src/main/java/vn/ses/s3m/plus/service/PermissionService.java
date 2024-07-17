package vn.ses.s3m.plus.service;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.dto.PermissionCategoryData;
import vn.ses.s3m.plus.dto.PermissionMapData;
import vn.ses.s3m.plus.dto.PermissionTreeData;

public interface PermissionService {
    void addTreeDataPermission(Map<String, Object> condition);

    void addMapDataPermission(Map<String, Object> condition);

    PermissionTreeData getTreeData(Map<String, Object> condition);

    PermissionMapData getMapData(Map<String, Object> condition);

    void updateTreeData(Map<String, Object> condition);

    void updateMapData(Map<String, Object> condition);

    void addPermissionCategory(Map<String, Object> condition);

    void updatePermissionCategory(Map<String, Object> condition);

    PermissionCategoryData getCategoryPermission(Map<String, Object> condition);

    void deleteTreeData(@Param ("userId") Integer userId);

    void deleteMapData(@Param ("userId") Integer userId);

    void deleteCategoryData(@Param ("userId") Integer userId);
}
