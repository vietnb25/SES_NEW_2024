package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.dto.UserRole;

@Mapper
public interface UserMapper {
    User getUserByUsername(String username);

    List<String> getRoleNames(Long userId);

    List<UserRole> getRole();

    User getUserById(int id);

    void updateUser(User user);

    void updateFailedAttempts(Map<String, String> condition);

    void updateLockedUser(Map<String, String> condition);

    User getUserByEmail(String email);

    void updateTokenResetPassword(Map<String, String> condition);

    User selectUserByTokenReset(String token);

    void resetPassword(Map<String, String> condition);

    void updatePasswordFirstLogin(Map<String, String> condition);
}
