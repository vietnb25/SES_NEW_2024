package vn.ses.s3m.plus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.ProfileMapper;
import vn.ses.s3m.plus.dto.User;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileMapper profileMaper;

    /**
     * Lấy thông tin của người dùng theo tên đăng nhập.
     *
     * @param user thông tin người dùng, id mã của người dùng
     * @return Trả về thông tin người dùng
     */
    @Override
    public User getUser(final String username) {
        return profileMaper.getUser(username);
    }

    /**
     * Cập nhật thông tin của người dùng
     *
     * @param user thông tin người dùng, id mã của người dùng
     * @return Trả về thông tin người dùng mới cập nhật
     */

    @Override
    public void updateProfile(final User user) {
        profileMaper.updateProfile(user);

    }

    /**
     * Lấy thông tin của người dùng theo tên mã
     *
     * @param id mã người dùng
     * @return Trả về thông tin người dùng theo mã
     */

    @Override
    public User getUserByid(final int id) {
        return profileMaper.getUserByid(id);
    }

    /**
     * Xử lý đổi mật khẩu của người dùng .
     *
     * @param user thông tin người dùng
     * @return Cập nhật mật khẩu mới
     */
    @Override
    public void changePassword(final User user) {
        profileMaper.changePassword(user);

    }

}
