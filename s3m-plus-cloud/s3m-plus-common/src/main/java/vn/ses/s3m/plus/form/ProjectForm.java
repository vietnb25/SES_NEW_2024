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
public class ProjectForm {
    private Integer projectId;

    @NotBlank (message = Constants.ProjectValidate.PROJECT_NAME_NOT_BLANK)
    @Size (max = Constants.ProjectValidate.PROJECT_NAME_MAX,
        message = Constants.ProjectValidate.PROJECT_NAME_MAX_LENGTH)
    private String projectName;

    private String address;

    @NotNull (message = Constants.ProjectValidate.PROJECT_LATITUDE_NOT_BLANK)
    @Min (value = Constants.ProjectValidate.PROJECT_LATITUDE_MIN,
        message = Constants.ProjectValidate.PROJECT_LATITUDE_MIN_ERROR)
    @Max (value = Constants.ProjectValidate.PROJECT_LATITUDE_MAX,
        message = Constants.ProjectValidate.PROJECT_LATITUDE_MAX_ERROR)
    private Double latitude;

    @NotNull (message = Constants.ProjectValidate.PROJECT_LONGITUDE_NOT_BLANK)
    @Min (value = Constants.ProjectValidate.PROJECT_LONGITUDE_MIN,
        message = Constants.ProjectValidate.PROJECT_LONGITUDE_MIN_ERROR)
    @Max (value = Constants.ProjectValidate.PROJECT_LONGITUDE_MAX,
        message = Constants.ProjectValidate.PROJECT_LONGITUDE_MAX_ERROR)
    private Double longitude;

    private Integer customerId;

    private Integer areaId;

    private String description;

    private Timestamp updateDate;

    private Integer isViewRadiation;

    private Integer isViewForecast;

    private String shift1;

    private String shift2;

    private String shift3;

    private Integer amountOfPeople;
    private Double emissionFactorCo2Electric;
    private Double emissionFactorCo2Gasoline;
    private Double emissionFactorCo2Charcoal;
    private Double areaOfFloor;

}
