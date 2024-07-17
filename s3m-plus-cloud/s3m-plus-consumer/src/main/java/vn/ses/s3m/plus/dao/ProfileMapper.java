package vn.ses.s3m.plus.dao;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.User;

@Mapper
public interface ProfileMapper {
    User getUser(String username);

    User getUserByid(int id);

    void updateProfile(User user);

    void changePassword(User user);
}
