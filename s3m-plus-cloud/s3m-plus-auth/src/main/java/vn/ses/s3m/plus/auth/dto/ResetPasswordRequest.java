package vn.ses.s3m.plus.auth.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import vn.ses.s3m.plus.common.Constants;

@Data
public class ResetPasswordRequest {
    private String userId;

    @Pattern (regexp = Constants.UserValidation.PASSWORD_PATTERN,
        message = Constants.UserValidation.PASSWORD_PATTERN_ERROR)
    @NotBlank (message = Constants.UserValidation.PASSWORD_NOT_BLANK)
    @Size (max = Constants.UserValidation.PASSWORD_MAX_SIZE, message = Constants.UserValidation.PASSWORD_MAX_SIZE_ERROR)
    @Size (min = Constants.UserValidation.PASSWORD_MIN_SIZE, message = Constants.UserValidation.PASSWORD_MIN_SIZE_ERROR)
    private String password;
}
