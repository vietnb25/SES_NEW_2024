package vn.ses.s3m.plus.service;

import vn.ses.s3m.plus.dto.User;

public interface ProfileService {

    User getUser(String username);

    User getUserByid(int id);

    void updateProfile(User user);

    void changePassword(User user);
}
