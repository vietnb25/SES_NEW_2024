
package vn.ses.s3m.plus.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.service.ProfileService;

/**
 * Xử lý về thông tin cá nhân.
 *
 * @author Arius Vietnam JSC
 * @since 2022-01-01
 */

@RestController
@RequestMapping (value = "/common/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    /**
     * Lấy ra thông tin cá nhân của người dùng.
     *
     * @param username Tên đăng nhập.
     * @return Thông tin người dùng 200(Lấy thông tin thành công).
     */
    @GetMapping ("/{username}")
    ResponseEntity<User> profile(@PathVariable final String username) {

        User user = profileService.getUser(username);

        return new ResponseEntity<User>(user, HttpStatus.OK);
    };

    /**
     * Xử lý cập nhật thông tin cá nhân người dùng.
     *
     * @param user Thông tin người dùng.
     * @param id Mã của người dùng.
     * @return Thông tin người dùng, 200(Cập nhật thành công).
     */
    @PostMapping (value = "/updateProfile/{id}")
    ResponseEntity<User> uploadFile(@RequestBody final User user, @PathVariable ("id") final int id)
        throws IOException {

        User users = profileService.getUserByid(id);
        users.setStaffName(user.getStaffName());
        users.setEmail(user.getEmail());
        users.setImg(user.getImg());
        profileService.updateProfile(users);

        return new ResponseEntity<User>(users, HttpStatus.OK);

    }

    /**
     * Xử lý đổi mật khẩu của người dùng.
     *
     * @param user Thông tin người dùng.
     * @param id Mã của người dùng.
     * @return Trả về thông báo 200 (Đổi mật khẩu thành công) 400(Đổi mật khẩu thất bại).
     */
    @PostMapping ("/changePassword/{id}")
    ResponseEntity<Void> updatePassword(@RequestBody final User user, @PathVariable ("id") final int id) {

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        User u = profileService.getUserByid(id);
        boolean check = bCryptPasswordEncoder.matches(user.getPassword(), u.getPassword());
        if (check) {
            u.setNewPassword(bCryptPasswordEncoder.encode(user.getNewPassword()));
            profileService.changePassword(u);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } else {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
    }
}
