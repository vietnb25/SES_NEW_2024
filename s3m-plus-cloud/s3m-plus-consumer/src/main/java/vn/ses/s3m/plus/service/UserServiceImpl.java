package vn.ses.s3m.plus.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.UserMapper;
import vn.ses.s3m.plus.dao.UserRoleMapper;
import vn.ses.s3m.plus.dto.SystemUser;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.dto.UserDto;
import vn.ses.s3m.plus.dto.UserRole;

/**
 * Service xử lý tài khoản user
 *
 * @author Arius Vietnam JSC
 * @since 28 thg 10, 2022
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    /**
     * Lấy quyền người dùng
     *
     * @param Long userId
     * @return List<String> roles
     */
    @Override
    public List<String> getRoleNames(final Long userId) {
        return userMapper.getRoleNames(userId);
    }

    /**
     * Lấy quyền người dùng
     *
     * @param Long userId
     * @return List<String> roles
     */
    @Override
    public List<UserRole> getRole() {
        return userMapper.getRole();
    }

    /**
     * Lấy tất cả tài khoản user
     *
     * @param null
     * @return List<User> users
     */
    @Override
    public List<User> getAllUser() {
        return userMapper.getAllUser();
    }

    /**
     * Lấy tất cả tài khoản user
     *
     * @param null
     * @return List<User> users
     */
    @Override
    public List<UserDto> getAllUsers() {
        return userMapper.getAllUsers();
    }

    /**
     * Lấy user theo id
     *
     * @param int id
     * @return User user
     */
    @Override
    public UserDto getUserById(final int id) {
        return userMapper.getUserById(id);
    }

    /**
     * Thêm mới user
     *
     * @param User user
     * @return void
     */
    @Override
    public void insertUser(final User user) {
        userMapper.insertUser(user);
    }

    /**
     * lấy id user mới nhất
     *
     * @param null
     * @return int id
     */
    @Override
    public int getLastUserId() {
        return userMapper.getLastUserId();
    }

    /**
     * Thêm mới role cho tài khoản
     *
     * @param User user
     * @return void
     */
    @Override
    public void insertUserRole(final User user) {
        userMapper.insertUserRole(user);
    }

    /**
     * Cập nhật user
     *
     * @param User user
     * @return void
     */
    @Override
    public void updateUser(final User user) {
        userMapper.updateUser(user);
    }

    @Override
    public void updateUserWithoutPass(final User user) {
        // TODO Auto-generated method stub

    }

    /**
     * Cập nhật user role
     *
     * @param User user
     * @return void
     */
    @Override
    public void updateUserRole(final User user) {
        userMapper.updateUserRole(user);
    }

    /**
     * Xóa user
     *
     * @param int userId
     * @return void
     */
    @Override
    public void deleteUser(final int userId) {
        userMapper.deleteUser(userId);
        // delete user role
        Map<String, String> condition = new HashMap<>();
        condition.put("userId", String.valueOf(userId));
        userRoleMapper.deleteUserRole(condition);
    }

    /**
     * Cập nhật profile
     *
     * @param User user
     * @return void
     */
    @Override
    public void updateProfile(final User user) {
        userMapper.updateProfile(user);
    }

    /**
     * Tìm kiếm users
     *
     * @param String search
     * @return List<User> users
     */
    @Override
    public List<UserDto> searchUser(final String keyword) {
        return userMapper.searchUser(keyword);
    }

    /**
     * Lấy user theo username
     *
     * @param String username
     * @return User
     */
    @Override
    public SystemUser getUser(final String username) {
        return userMapper.getUser(username);
    }

    /**
     * Lấy user theo username
     *
     * @param String username
     * @return User
     */
    @Override
    public User getUserByUsername(final String username) {
        return userMapper.getUserByUsername(username);
    }

    /**
     * Cập nhật tổng số lần đăng nhập sai mật khẩu
     *
     * @param Map<String, String> condition
     * @return void
     */
    @Override
    public void updateFailedAttempts(final Map<String, String> condition) {
        userMapper.updateFailedAttempts(condition);
    }

    /**
     * Cập nhật trạng thái khóa/mở khóa tài khoản
     *
     * @param Map<String, String> condition
     * @return void
     */
    @Override
    public void updateLockedUser(final Map<String, String> condition) {
        userMapper.updateLockedUser(condition);
    }

    /**
     * Lấy user theo email
     *
     * @param String email
     * @return User user
     */
    @Override
    public User getUserByEmail(final String email) {
        return userMapper.getUserByEmail(email);
    }

    @Override
    public List<UserDto> usersByCustomerIds(String[] customerIds) {
        return userMapper.usersByCustomerIds(customerIds);
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userMapper.findAllUsers();
    }


	@Override
	public void updatePriorityIngredients(User user) {
		 userMapper.updatePriorityIngredients(user);;
		
	}
}
