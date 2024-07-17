package vn.ses.s3m.plus.form;

import java.sql.Timestamp;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import vn.ses.s3m.plus.common.Constants;

@Data
public class CableForm {

    private Integer no;

    private Integer cableId;

    @NotBlank (message = Constants.CableValidation.CABLE_NAME_NOT_BLANK)
    @Size (max = Constants.CableValidation.CABLE_NAME_SIZE, message = Constants.CableValidation.CABLE_NAME_MAX_LENGTH)
    private String cableName;

    @NotNull (message = Constants.CableValidation.CURRENT_NOT_BLANK)
    @Max (value = Constants.CableValidation.CURRENT_SIZE, message = Constants.CableValidation.CURRENT_MAX_VALUE)
    private Integer current;

    @Size (max = Constants.CableValidation.DESCRIPTION_SIZE, message = Constants.CableValidation.DESCRIPTION_MAX_LENGTH)
    private String description;

    private Integer createId;

    private Timestamp createDate;

    private Integer updateId;

    private Timestamp updateDate;
}
