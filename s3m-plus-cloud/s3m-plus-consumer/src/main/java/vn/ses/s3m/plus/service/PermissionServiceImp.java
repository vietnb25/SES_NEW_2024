package vn.ses.s3m.plus.service;

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
    public void updateTreeData(Map<String, Object> condition) {
        permissionMapper.updateTreeData(condition);
    }

    @Override
    public void updateMapData(Map<String, Object> condition) {
        permissionMapper.updateMapData(condition);
    }

    @Override
    public void addPermissionCategory(Map<String, Object> condition) {
        permissionMapper.addPermissionCategory(condition);
    }

    @Override
    public void updatePermissionCategory(Map<String, Object> condition) {
        permissionMapper.updatePermissionCategory(condition);
    }

    @Override
    public PermissionCategoryData getCategoryPermission(Map<String, Object> condition) {
        return permissionMapper.getCategoryPermission(condition);
    }

    @Override
    public void deleteTreeData(Integer userId) {
        permissionMapper.deleteTreeData(userId);
    }

    @Override
    public void deleteMapData(Integer userId) {
        permissionMapper.deleteMapData(userId);
    }

    @Override
    public void deleteCategoryData(Integer userId) {
        permissionMapper.deleteCategoryData(userId);
    }
}
