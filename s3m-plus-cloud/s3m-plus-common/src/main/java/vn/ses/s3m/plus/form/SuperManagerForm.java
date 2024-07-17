package vn.ses.s3m.plus.form;

import java.sql.Timestamp;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import vn.ses.s3m.plus.common.Constants;

@Data
public class SuperManagerForm {

    private Integer superManagerId;

    @NotBlank (message = Constants.SuperManagerValidation.SUPER_MANAGER_NAME_NOT_BLANK)
    @Size (max = Constants.SuperManagerValidation.MAX_SIZE,
        message = Constants.SuperManagerValidation.MAX_SIZE_ERROR_SUPERMANAGERNAME)
    private String superManagerName;

    @NotNull (message = Constants.SuperManagerValidation.NOT_BLANK_LONGITUDE)
    @Min (value = Constants.SuperManagerValidation.LONGITUDE_MIN_VALUE,
        message = Constants.SuperManagerValidation.MIN_VALUE_ERROR_LONGITUDE)
    @Max (value = Constants.SuperManagerValidation.LONGITUDE_MAX_VALUE,
        message = Constants.SuperManagerValidation.MAX_VALUE_ERROR_LONGITUDE)
    private Double longitude;

    @NotNull (message = Constants.SuperManagerValidation.NOT_BLANK_LATITUDE)
    @Min (value = Constants.SuperManagerValidation.LATITUDE_MIN_VALUE,
        message = Constants.SuperManagerValidation.MIN_VALUE_ERROR_LATITUDE)
    @Max (value = Constants.SuperManagerValidation.LATITUDE_MAX_VALUE,
        message = Constants.SuperManagerValidation.MAX_VALUE_ERROR_LATITUDE)
    private Double latitude;

    private String description;

    private Timestamp updateDate;
}
