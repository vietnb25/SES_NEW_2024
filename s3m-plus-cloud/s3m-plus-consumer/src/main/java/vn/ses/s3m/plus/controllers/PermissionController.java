package vn.ses.s3m.plus.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.dto.PermissionCategoryData;
import vn.ses.s3m.plus.dto.PermissionMapData;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.form.PermissionTreeData;
import vn.ses.s3m.plus.service.PermissionService;
import vn.ses.s3m.plus.service.UserService;

@RestController
@RequestMapping ("/common/permission")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    @PostMapping ("/")
    public ResponseEntity<?> addPermissionTreeData(@RequestBody PermissionTreeData permissionTreeData) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", permissionTreeData.getUserId());
        map.put("treeData", permissionTreeData.getDataTree());
        map.put("mapData", permissionTreeData.getDataMarkers());

        vn.ses.s3m.plus.dto.PermissionTreeData treeData = permissionService.getTreeData(map);
        PermissionMapData mapData = permissionService.getMapData(map);

        if (permissionTreeData.getCustomerIds() != null) {
            User u = new User();
            u.setId(Integer.parseInt(permissionTreeData.getUserId()));
            u.setCustomerIds(permissionTreeData.getCustomerIds());

            userService.updateUser(u);
        }

        try {
            if (treeData != null) {
                permissionService.updateTreeData(map);
            } else {
                permissionService.addTreeDataPermission(map);
            }

            if (mapData != null) {
                permissionService.updateMapData(map);
            } else {
                permissionService.addMapDataPermission(map);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping ("/{userId}")
    public ResponseEntity<?> getPermissionByUser(@PathVariable Integer userId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("userId", userId);
        vn.ses.s3m.plus.dto.PermissionTreeData treeData = permissionService.getTreeData(condition);
        PermissionMapData mapData = permissionService.getMapData(condition);

        Map<String, Object> map = new HashMap<>();
        map.put("treeData", treeData);
        map.put("mapData", mapData);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping ("/category")
    public ResponseEntity<?> addCategoryPermission(@RequestBody PermissionTreeData permissionTreeData) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", permissionTreeData.getUserId());
        map.put("treeData", permissionTreeData.getCategoriesPath());

        PermissionCategoryData categoryData = permissionService.getCategoryPermission(map);

        try {
            if (categoryData != null) {
                permissionService.updatePermissionCategory(map);
            } else {
                permissionService.addPermissionCategory(map);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping ("/category/{userId}")
    public ResponseEntity<?> getCategoryPermission(@PathVariable Integer userId) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            PermissionCategoryData categoryData = permissionService.getCategoryPermission(map);

            return new ResponseEntity<>(categoryData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
