package vn.ses.s3m.plus.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.PermissionCategoryData;
import vn.ses.s3m.plus.dto.PermissionMapData;
import vn.ses.s3m.plus.dto.PermissionTreeData;

@Mapper
public interface PermissionMapper {
    void addTreeDataPermission(Map<String, Object> condition);

    void addMapDataPermission(Map<String, Object> condition);

    PermissionTreeData getTreeData(Map<String, Object> condition);

    PermissionMapData getMapData(Map<String, Object> condition);

    PermissionCategoryData getCategoryPermission(Map<String, Object> condition);
}
