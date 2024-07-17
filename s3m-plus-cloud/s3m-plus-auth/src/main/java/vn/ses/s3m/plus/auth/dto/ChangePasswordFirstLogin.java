package vn.ses.s3m.plus.auth.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import vn.ses.s3m.plus.common.Constants;

@Data
public class ChangePasswordFirstLogin {
    private String username;

    @NotBlank (message = Constants.FirstLoginValidation.CURRENT_PASSWORD_NOT_BLANK)
    private String currentPassword;

    @Pattern (regexp = Constants.FirstLoginValidation.PASSWORD_PATTERN,
        message = Constants.FirstLoginValidation.PASSWORD_PATTERN_ERROR)
    @NotBlank (message = Constants.FirstLoginValidation.PASSWORD_NOT_BLANK)
    @Size (max = Constants.FirstLoginValidation.PASSWORD_MAX_SIZE,
        message = Constants.FirstLoginValidation.PASSWORD_MAX_SIZE_ERROR)
    @Size (min = Constants.FirstLoginValidation.PASSWORD_MIN_SIZE,
        message = Constants.FirstLoginValidation.PASSWORD_MIN_SIZE_ERROR)
    private String password;
}
