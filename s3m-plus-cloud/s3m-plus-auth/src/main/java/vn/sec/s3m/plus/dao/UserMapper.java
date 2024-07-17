package vn.sec.s3m.plus.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.dto.UserRole;

/**
 * UserMapper.
 *
 * @author Arius Vietnam JSC.
 * @since 2022-01-01.
 */
@Mapper
public interface UserMapper {
    User getUserByUsername(String username);

    List<String> getRoleNames(Long userId);

    List<UserRole> getRole();

    List<User> getAllUser();

    List<User> getAllUsers();

    User getUserById(int id);

    void insertUser(User user);

    int getLastUserId();

    void insertUserRole(User user);

    void updateUser(User user);

    void updateUserWithoutPass(User user);

    void updateUserRole(User user);

    void deleteUser(int userId);

    void deleteUserRole(int userId);

    void updateProfile(User user);
}
