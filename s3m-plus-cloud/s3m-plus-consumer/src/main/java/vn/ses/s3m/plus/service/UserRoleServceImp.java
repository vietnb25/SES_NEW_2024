package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.UserRoleMapper;
import vn.ses.s3m.plus.dto.UserRole;

@Service
public class UserRoleServceImp implements UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    /**
     * Lấy danh sách Role Name.
     *
     * @param condition Điều kiện truy vấn lấy Role Name.
     * @return Danh sách Role Name.
     */
    @Override
    public List<String> getRoleNames(final Map<String, String> condition) {
        return userRoleMapper.getRoleNames(condition);
    }

    /**
     * Lấy danh sách Role.
     *
     * @param condition Điều kiện truy vấn lấy Role.
     * @return Danh sách Role.
     */
    @Override
    public List<UserRole> getRole() {
        return userRoleMapper.getRole();
    }

    /**
     * Thêm mới Role User.
     *
     * @param condition Dữ liệu thêm mới Role User.
     */
    @Override
    public void insertUserRole(final Map<String, String> condition) {
        userRoleMapper.insertUserRole(condition);
    }

    /**
     * Cập nhật Role User.
     *
     * @param condition Dữ liệu Cập nhật Role User.
     */
    @Override
    public void updateUserRole(final Map<String, String> condition) {
        userRoleMapper.updateUserRole(condition);
    }

    /**
     * Xóa Role User.
     *
     * @param condition Điều kiện xóa Role User.
     */
    @Override
    public void deleteUserRole(final Map<String, String> condition) {
        userRoleMapper.deleteUserRole(condition);
    }

}
