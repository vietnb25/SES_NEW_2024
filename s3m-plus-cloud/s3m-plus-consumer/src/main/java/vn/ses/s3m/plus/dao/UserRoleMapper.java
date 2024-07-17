package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.UserRole;

@Mapper
public interface UserRoleMapper {
    List<String> getRoleNames(Map<String, String> condition);

    List<UserRole> getRole();

    void insertUserRole(Map<String, String> condition);

    void updateUserRole(Map<String, String> condition);

    void deleteUserRole(Map<String, String> condition);
}
