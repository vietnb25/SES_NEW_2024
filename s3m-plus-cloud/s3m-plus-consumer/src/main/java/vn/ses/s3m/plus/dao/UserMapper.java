package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.dto.SystemUser;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.dto.UserDto;
import vn.ses.s3m.plus.dto.UserRole;

@Mapper
public interface UserMapper {
    SystemUser getUser(String username);

    User getUserByUsername(String username);

    List<String> getRoleNames(Long userId);

    List<UserRole> getRole();

    List<User> getAllUser();

    List<UserDto> getAllUsers();

    List<UserDto> findAllUsers();

    UserDto getUserById(int id);

    void insertUser(User user);

    int getLastUserId();

    void insertUserRole(User user);

    void updateUser(User user);

    void updateUserWithoutPass(User user);

    void updateUserRole(User user);

    void deleteUser(int userId);

    void deleteUserRole(int userId);

    void updateProfile(User user);

    List<UserDto> searchUser(String keyword);

    void updateFailedAttempts(Map<String, String> condition);

    void updateLockedUser(Map<String, String> condition);

    User getUserByEmail(String email);

    List<UserDto> usersByCustomerIds(@Param ("customerIds") String[] customerIds); 
    
    void updatePriorityIngredients(User user);

}
