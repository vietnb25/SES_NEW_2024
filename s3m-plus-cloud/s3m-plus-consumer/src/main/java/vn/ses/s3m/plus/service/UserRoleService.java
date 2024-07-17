package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.UserRole;

public interface UserRoleService {
    List<String> getRoleNames(Map<String, String> condition);

    List<UserRole> getRole();

    void insertUserRole(Map<String, String> condition);

    void updateUserRole(Map<String, String> condition);

    void deleteUserRole(Map<String, String> condition);
}
