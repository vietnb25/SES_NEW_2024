package vn.ses.s3m.plus.auth.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.auth.dto.ChangePasswordFirstLogin;
import vn.ses.s3m.plus.auth.dto.ForgotPasswordRequest;
import vn.ses.s3m.plus.auth.dto.LoginRequest;
import vn.ses.s3m.plus.auth.dto.LoginResponse;
import vn.ses.s3m.plus.auth.dto.ResetPasswordRequest;
import vn.ses.s3m.plus.auth.service.JwtService;
import vn.ses.s3m.plus.auth.service.PermissionService;
import vn.ses.s3m.plus.auth.service.SendMailService;
import vn.ses.s3m.plus.auth.service.UserDetailsService;
import vn.ses.s3m.plus.auth.service.UserService;
import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.dto.User;

/**
 * Class xử lý đăng nhập và authen.
 *
 * @author Arius Vietnam JSC.
 * @since 2022-08-22.
 */
@RestController
@RequestMapping ("/auth")
@Validated
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Value ("${MAX_FAILED_ATTEMPTS}")
    private int maxFailedAttempts;

    @Autowired
    private PermissionService permissionService;

    private static final int TOKEN_LENGTH = 50;

    // token hiệu lực 24h
    private static final int RESET_PASSWORD_TOKEN_EXPIRE = 1000 * 60 * 60 * 24;

    /**
     * Hàm xử lý đăng nhập.
     *
     * @param loginRequest dữ liệu username và password
     * @return Trả về kết quả khi đăng nhập.
     */
    @PostMapping ("/login")
    public ResponseEntity<?> login(@Valid @RequestBody final LoginRequest loginRequest) {
        User user = userService.getUserByUsername(loginRequest.getUsername());
        if (user != null) {
            // nếu tài khoản bị khóa bởi admin
            if (user.getLockFlag() == 1 && user.getFailedAttempts() < maxFailedAttempts) {
                Map<String, String> map = new HashMap<>();
                map.put("error", Constants.ResponseMessage.ACCOUNT_IS_LOCKED_BY_ADMIN);
                return new ResponseEntity<Object>(map, HttpStatus.FORBIDDEN);
            }
            // xử lý nếu tài khoản nhập sai quá số lần quy định
            if (user.getFailedAttempts() >= maxFailedAttempts || user.getLockFlag() == 1) {
                Map<String, String> map = new HashMap<>();
                map.put("maxFailedAttempts", String.valueOf(maxFailedAttempts));
                map.put("error", Constants.ResponseMessage.ACCOUNT_IS_LOCKED);
                return new ResponseEntity<Object>(map, HttpStatus.FORBIDDEN);
            }

            // xử lý nếu tài khoản nhập sai chưa quá số lần quy định
            org.springframework.security.core.userdetails.User u = null;
            try {
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
                u = (org.springframework.security.core.userdetails.User) userDetailsService
                    .loadUserByUsername(user.getUsername());

                String jwt = jwtService.createJwt(u);
                LoginResponse response = new LoginResponse();
                response.setJwt(jwt);
                response.setCustomerId(user.getCustomerId());

                if (user.getFirstLoginFlag() == 1) {
                    response.setFirstLoginFlag(1);
                }

                // set failedAttemps = 0 nếu success
                Map<String, String> condition = new HashMap<>();
                condition.put("failedAttempt", String.valueOf(0));
                condition.put("userId", String.valueOf(user.getId()));
                userService.updateFailedAttempts(condition);

                return new ResponseEntity<Object>(response, HttpStatus.OK);
            } catch (BadCredentialsException badCredentialsException) {
                // XỬ LÝ NẾU SAI THÔNG TIN LOGIN
                int failedAttemps = user.getFailedAttempts() + 1;
                Map<String, String> condition = new HashMap<>();
                condition.put("failedAttempt", String.valueOf(failedAttemps));
                condition.put("userId", String.valueOf(user.getId()));
                userService.updateFailedAttempts(condition);

                // xử lý nếu lần login hiện tại là lần sai cuối cùng
                if (failedAttemps == maxFailedAttempts) {
                    condition.put("lockFlag", String.valueOf(1));
                    userService.updateLockedUser(condition);

                    Map<String, String> map = new HashMap<>();
                    map.put("maxFailedAttempts", String.valueOf(maxFailedAttempts));
                    map.put("error", Constants.ResponseMessage.ACCOUNT_IS_LOCKED);

                    return new ResponseEntity<Object>(map, HttpStatus.FORBIDDEN);
                }

                Map<String, String> response = new HashMap<>();
                response.put("error", Constants.ResponseMessage.BAD_CREDITIAL);
                response.put("failedRemains", String.valueOf(maxFailedAttempts - failedAttemps));

                return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST);
    }

    /**
     * Hàm kiểm tra user tồn tại trong DB khi người dùng gửi yêu cầu quên mật khẩu.
     *
     * @param forgotPasswordRequest đối tượng nhận dữ liệu khi người dùng gửi yêu cầu quên mật khẩu.
     * @return Trả về kết quả kiểm tra user.
     */
    @PostMapping ("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody final ForgotPasswordRequest forgotPasswordRequest) {
        // kiểm tra người dùng theo email
        User user = userService.getUserByEmail(forgotPasswordRequest.getEmail());
        String username = user.getStaffName();
        if (user != null) {
            // tạo token reset password
            String randomString = RandomStringUtils.random(TOKEN_LENGTH, true, true);
            System.out.println(forgotPasswordRequest.getEmail());
            // gửi mail kèm token cho người dùng theo email
            SendMailService.sendMail(randomString, forgotPasswordRequest.getEmail(), username);

            // token hiệu lực 24h
            long expire = System.currentTimeMillis() + RESET_PASSWORD_TOKEN_EXPIRE;

            Map<String, String> condition = new HashMap<>();
            condition.put("resetPasswordToken", randomString);
            condition.put("resetPasswordTokenExpire", String.valueOf(expire));
            condition.put("userId", String.valueOf(user.getId()));

            // insert token vào db cho user theo email
            userService.updateTokenResetPassword(condition);

            return new ResponseEntity<Object>(randomString, HttpStatus.OK);
        }
        return new ResponseEntity<Object>(Constants.ResponseMessage.NOT_FOUND_EMAIL, HttpStatus.BAD_REQUEST);
    }

    /**
     * Hàm kiểm tra token trong db.
     *
     * @param token kiểm tra token tồn tại trong db.
     * @return Trả về kết quả kiểm tra user và token.
     */
    @GetMapping ("/reset-password/{token}")
    public ResponseEntity<?> checkExistToken(@PathVariable ("token") final String token) {
        User user = userService.selectUserByTokenReset(token);
        if (user != null) {
            long timeMilisecond = System.currentTimeMillis();
            if (timeMilisecond > user.getResetPasswordTokenExpire()) {
                return new ResponseEntity<Object>(null, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<Object>(user, HttpStatus.OK);
        }
        return new ResponseEntity<Object>(null, HttpStatus.BAD_REQUEST);
    }

    /**
     * Hàm cập nhật lại mật khẩu.
     *
     * @param resetPasswordRequest đối tượng nhận dữ liệu để cập nhật lại mật khẩu được gửi từ client.
     * @return Kết quả cập nhật mật khẩu.
     */
    @PostMapping ("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody final ResetPasswordRequest resetPasswordRequest) {
        try {
            Map<String, String> condition = new HashMap<>();
            condition.put("userId", resetPasswordRequest.getUserId());

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String password = passwordEncoder.encode(resetPasswordRequest.getPassword());
            condition.put("password", password);

            userService.resetPassword(condition);

            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Hàm cập nhật lại mật khẩu khi đăng nhập lần đầu.
     *
     * @param request nhận dữ liệu từ client. Chứa username, password của người dùng.
     * @return ResponseEntity<?>.
     */
    @PostMapping ("/change-password-first-login")
    public ResponseEntity<?> changePasswordFirstLogin(@Valid @RequestBody final ChangePasswordFirstLogin request) {
        User user = userService.getUserByUsername(request.getUsername());
        if (user != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean isMatchPassword = passwordEncoder.matches(request.getCurrentPassword(), user.getPassword());
            if (isMatchPassword) {
                if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                    return new ResponseEntity<Object>(Constants.ResponseMessage.NEW_PASSWORD_SAME_CURRENT_PASSWORD,
                        HttpStatus.BAD_REQUEST);
                }
                String newPassword = passwordEncoder.encode(request.getPassword());
                Map<String, String> condition = new HashMap<>();
                condition.put("password", newPassword);
                condition.put("userId", String.valueOf(user.getId()));
                try {
                    userService.updatePasswordFirstLogin(condition);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<Object>(Constants.ResponseMessage.ERROR_UPDATE_PASSWORD,
                        HttpStatus.BAD_REQUEST);
                }
            }
            return new ResponseEntity<Object>(Constants.ResponseMessage.CURRENT_PASSWORD_NOT_MATCH,
                HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
    }
}
