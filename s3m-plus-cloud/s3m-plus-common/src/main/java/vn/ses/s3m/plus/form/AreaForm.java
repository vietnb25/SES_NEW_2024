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
public class AreaForm {

    @NotBlank (message = Constants.AreaValidate.AREA_NAME_NOT_BLANK)
    @Size (max = Constants.AreaValidate.AREA_NAME_MAX_SIZE, message = Constants.AreaValidate.AREA_NAME_MAX)
    private String areaName;

    @NotNull (message = Constants.AreaValidate.MANAGER_ID_NOT_BLANK)
    private Integer managerId;

    @Size (max = Constants.AreaValidate.DESCRIPTION_MAX_SIZE, message = Constants.AreaValidate.DESCRIPTION_MAX)
    private String description;

    @NotNull (message = Constants.AreaValidate.LATITUDE_NOT_BLANK)
    @Max (value = (long) Constants.AreaValidate.LATITUDE_MAX_SIZE, message = Constants.AreaValidate.LATITUDE_MAX)
    @Min (value = (long) Constants.AreaValidate.LATITUDE_MIN_SIZE, message = Constants.AreaValidate.LATIUDE_MIN)
    private Double latitude;

    @NotNull (message = Constants.AreaValidate.LONGITUDE_MAX)
    @Max (value = (long) Constants.AreaValidate.LONGITUDE_MAX_SIZE, message = Constants.AreaValidate.LONGITUDE_MAX)
    @Min (value = (long) Constants.AreaValidate.LONGITUDE_MIN_SIZE, message = Constants.AreaValidate.LONGITUDE_MAX)
    private Double longitude;

    private Integer areaId;

    private Integer createId;

    private Timestamp createDate;

    private Integer updateId;

    private Timestamp updateDate;

    private Integer projectNumber;

    private Integer pvNumber;

    private Long cspvTotal;

    private Integer loadNumber;

    private Long loadEnnergy;

    private Integer stt;

    private String managerName;
}
