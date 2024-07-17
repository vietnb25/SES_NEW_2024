package vn.ses.s3m.plus.dao;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.User;

@Mapper
public interface UserMapper {
    User getUserByUsername(String username);
}
