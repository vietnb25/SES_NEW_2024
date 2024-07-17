package vn.ses.s3m.plus.common;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncode {
    public static String encodePassword(final String rawPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String passwordEncode = encoder.encode(rawPassword);
        return passwordEncode;
    }
}
