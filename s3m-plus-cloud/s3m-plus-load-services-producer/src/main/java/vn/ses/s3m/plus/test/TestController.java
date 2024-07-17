package vn.ses.s3m.plus.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.dto.User;

@RestController
@RequestMapping ("/load/test")
public class TestController {

    @GetMapping ("/users")
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setId(i);
            user.setUsername("username" + i);
            users.add(user);
        }

        return users;
    }
}
