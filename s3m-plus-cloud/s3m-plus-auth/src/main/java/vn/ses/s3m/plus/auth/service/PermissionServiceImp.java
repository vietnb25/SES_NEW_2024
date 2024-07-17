package vn.ses.s3m.plus.auth.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.PermissionMapper;
import vn.ses.s3m.plus.dto.PermissionCategoryData;
import vn.ses.s3m.plus.dto.PermissionMapData;
import vn.ses.s3m.plus.dto.PermissionTreeData;

@Service
public class PermissionServiceImp implements PermissionService {
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public void addTreeDataPermission(Map<String, Object> condition) {
        permissionMapper.addTreeDataPermission(condition);
    }

    @Override
    public void addMapDataPermission(Map<String, Object> condition) {
        permissionMapper.addMapDataPermission(condition);
    }

    @Override
    public PermissionTreeData getTreeData(Map<String, Object> condition) {
        return permissionMapper.getTreeData(condition);
    }

    @Override
    public PermissionMapData getMapData(Map<String, Object> condition) {
        return permissionMapper.getMapData(condition);
    }

    @Override
    public PermissionCategoryData getCategoryPermission(Map<String, Object> condition) {
        return permissionMapper.getCategoryPermission(condition);
    }

}
