package vn.ses.s3m.plus.auth.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import vn.ses.s3m.plus.common.Constants;

@Data
public class ForgotPasswordRequest {
    @NotBlank (message = Constants.UserValidation.EMAIL_NOT_BLANK)
    @Email (message = Constants.UserValidation.EMAIL_IS_INVALID)
    private String email;
}
