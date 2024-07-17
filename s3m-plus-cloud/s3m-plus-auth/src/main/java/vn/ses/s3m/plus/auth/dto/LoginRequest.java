package vn.ses.s3m.plus.auth.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import vn.ses.s3m.plus.common.Constants;

@Data
public class LoginRequest {
    @NotBlank (message = Constants.UserValidation.USERNAME_NOT_BLANK)
    private String username;

    @NotBlank (message = Constants.UserValidation.PASSWORD_NOT_BLANK)
    private String password;
}
