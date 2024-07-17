package vn.ses.s3m.plus.auth.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.User;

/**
 * Interface định nghĩa các hàm xử lý.
 *
 * @author Arius Vietnam JSC.
 * @since 2022-01-01.
 */
public interface UserService {
    User getUserByUsername(String username);

    List<String> getRoleNames(Long userId);

    void updateFailedAttempts(Map<String, String> condition);

    void updateLockedUser(Map<String, String> condition);

    User getUserByEmail(String email);

    void updateTokenResetPassword(Map<String, String> condition);

    User selectUserByTokenReset(String token);

    void resetPassword(Map<String, String> condition);

    void updatePasswordFirstLogin(Map<String, String> condition);
}
