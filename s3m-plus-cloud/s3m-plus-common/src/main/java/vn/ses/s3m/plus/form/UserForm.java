package vn.ses.s3m.plus.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import vn.ses.s3m.plus.common.Constants;

@Data
public class UserForm {
    private Integer id;

    @NotBlank (message = Constants.UserValidation.USERNAME_NOT_BLANK)
    @Size (max = Constants.UserValidation.USERNAME_MAX_SIZE, message = Constants.UserValidation.USERNAME_MAX_SIZE_ERROR)
    private String username;

    @Pattern (regexp = Constants.UserValidation.PASSWORD_PATTERN,
        message = Constants.UserValidation.PASSWORD_PATTERN_ERROR)
    @NotBlank (message = Constants.UserValidation.PASSWORD_NOT_BLANK)
    @Size (max = Constants.UserValidation.PASSWORD_MAX_SIZE, message = Constants.UserValidation.PASSWORD_MAX_SIZE_ERROR)
    @Size (min = Constants.UserValidation.PASSWORD_MIN_SIZE, message = Constants.UserValidation.PASSWORD_MIN_SIZE_ERROR)
    private String password;

    private String newPassword;

    @NotBlank (message = Constants.UserValidation.STAFF_NAME_NOT_BLANK)
    @Size (max = Constants.UserValidation.STAFF_NAME_MAX_SIZE,
        message = Constants.UserValidation.STAFF_NAME_MAX_SIZE_ERROR)
    private String staffName;

    @NotBlank (message = Constants.UserValidation.EMAIL_NOT_BLANK)
    @Size (max = Constants.UserValidation.EMAIL_MAX_SIZE, message = Constants.UserValidation.EMAIL_MAX_SIZE_ERROR)
    @Email (message = Constants.UserValidation.EMAIL_IS_INVALID)
    private String email;

    private Integer userType;

    private String company;

    private String role;

    private Integer roleId;

    private Integer customerId;

    private String customerName;

    private Integer superManagerId;

    private String superManagerName;

    private Integer managerId;

    private String managerName;

    private Integer areaId;

    private String areaName;

    private Integer projectId;

    private String projectName;

    private Integer systemTypeId;

    private String systemTypeName;

    private String updateDate;

    private String img;

    private Integer failedAttempts;

    private Integer lockFlag;

    private String resetPasswordToken;

    private Long resetPasswordTokenExpire;

    private Integer firstLoginFlag;

    private Integer authorized;

    private Integer targetId;

    private Integer createId;

    private String customerIds;

    private String projectIds;
}
