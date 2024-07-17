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
public class ManagerForm {

    private Integer managerId;

    @NotBlank (message = Constants.ManagerValidation.MANAGER_NAME_NOT_BLANK)
    @Size (max = Constants.ManagerValidation.MANAGER_NAME_SIZE,
        message = Constants.ManagerValidation.MANAGER_NAME_MAX_LENTH)
    private String managerName;

    private String managerCode;

    private Integer superManagerId;

    private String superManagerName;

    @Size (max = Constants.ManagerValidation.DESCRIPTION_SIZE,
        message = Constants.ManagerValidation.DESCRIPTION_MAX_LENGTH)
    private String description;

    private Timestamp updateDate;

    private Integer customerId;

    @NotNull (message = Constants.ManagerValidation.LATITUDE_NOT_BLANK)
    @Min (value = (long) Constants.ManagerValidation.LATITUDE_MIN_SIZE,
        message = Constants.ManagerValidation.LATITUDE_MIN_VALUE)
    @Max (value = (long) Constants.ManagerValidation.LATITUDE_MAX_SIZE,
        message = Constants.ManagerValidation.LATITUDE_MAX_VALUE)
    private Double latitude;

    @NotNull (message = Constants.ManagerValidation.LONGTITUDE_NOT_BLANK)
    @Min (value = (long) Constants.ManagerValidation.LONGTITUDE_MIN_SIZE,
        message = Constants.ManagerValidation.LONGTITUDE_MIN_VALUE)
    @Max (value = (long) Constants.ManagerValidation.LONGTITUDE_MAX_SIZE,
        message = Constants.ManagerValidation.LONGTITUDE_MAX_VALUE)
    private Double longitude;

    private Integer areaNumber;

    private Integer pvNumber;

    private Long cspvTotal;

    private Integer loadNumber;
}
