package vn.ses.s3m.plus.auth.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.UserMapper;
import vn.ses.s3m.plus.dto.User;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * Hàm lấy User theo username.
     *
     * @param String username.
     * @return User user.
     */
    @Override
    public vn.ses.s3m.plus.dto.User getUserByUsername(final String username) {
        return this.userMapper.getUserByUsername(username);
    }

    /**
     * Hàm lấy User theo username.
     *
     * @param Long userId.
     * @return List<String>.
     */
    @Override
    public List<String> getRoleNames(final Long userId) {
        return this.userMapper.getRoleNames(userId);
    }

    /**
     * Hàm cập nhật số lần đăng nhập sai thông tin.
     *
     * @param Map<String, String> condition.
     * @return void.
     */
    @Override
    public void updateFailedAttempts(final Map<String, String> condition) {
        userMapper.updateFailedAttempts(condition);
    }

    /**
     * Hàm cập nhật khóa hoặc mở tài khoản.
     *
     * @param Map<String, String> condition.
     * @return void.
     */
    @Override
    public void updateLockedUser(final Map<String, String> condition) {
        userMapper.updateLockedUser(condition);
    }

    /**
     * Hàm lấy user theo email.
     *
     * @param String email.
     * @return User user.
     */
    @Override
    public User getUserByEmail(final String email) {
        return userMapper.getUserByEmail(email);
    }

    /**
     * cập nhật token reset password cho user.
     *
     * @param Map<String, String> condition.
     * @return void.
     */
    @Override
    public void updateTokenResetPassword(final Map<String, String> condition) {
        userMapper.updateTokenResetPassword(condition);
    }

    /**
     * lấy user theo token reset password.
     *
     * @param String token.
     * @return User user.
     */
    @Override
    public User selectUserByTokenReset(final String token) {
        return userMapper.selectUserByTokenReset(token);
    }

    /**
     * lấy reset password.
     *
     * @param Map<String, String> condition.
     * @return void.
     */
    @Override
    public void resetPassword(final Map<String, String> condition) {
        userMapper.resetPassword(condition);
    }

    /**
     * lấy đổi mật khẩu sau khi login lần đầu.
     *
     * @param Map<String, String> condition.
     * @return void.
     */
    @Override
    public void updatePasswordFirstLogin(final Map<String, String> condition) {
        userMapper.updatePasswordFirstLogin(condition);
    }
}
