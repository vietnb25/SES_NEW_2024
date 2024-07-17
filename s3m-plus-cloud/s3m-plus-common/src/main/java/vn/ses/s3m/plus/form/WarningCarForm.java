package vn.ses.s3m.plus.form;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import vn.ses.s3m.plus.common.Constants;
@Data
public class WarningCarForm {
    @NotBlank (message = Constants.WarningCarValidate.ORGANIZATION_CREATE_NOT_BLANK)
	@Size (max = Constants.WarningCarValidate.ORGANIZATION_CREATE_MAX_SIZE, message = Constants.WarningCarValidate.ORGANIZATION_CREATE_MAX)
    private String organizationCreate;

    @NotBlank (message = Constants.WarningCarValidate.CONTENT_NOT_BLANK)
	@Size (max = Constants.WarningCarValidate.CONTENT_MAX_SIZE, message = Constants.WarningCarValidate.CONTENT_MAX)
    private String content;
    
    @NotBlank (message = Constants.WarningCarValidate.REASON_METHOD_NOT_BLANK)
   	@Size (max = Constants.WarningCarValidate.REASON_METHOD_MAX_SIZE, message = Constants.WarningCarValidate.REASON_METHOD_MAX)   
    private String reasonMethod;
    
    @NotBlank (message = Constants.WarningCarValidate.ORGANIZATION_EXECUTION_NOT_BLANK)
   	@Size (max = Constants.WarningCarValidate.ORGANIZATION_EXECUTION_MAX_SIZE, message = Constants.WarningCarValidate.ORGANIZATION_EXECUTION_MAX)
    private String organizationExecution;
    
    private String completionTime;
    
    @NotBlank (message = Constants.WarningCarValidate.RESULT_EXECUTION_NOT_BLANK)
   	@Size (max = Constants.WarningCarValidate.RESULT_EXECUTION_SIZE, message = Constants.WarningCarValidate.RESULT_EXECUTION_MAX)
    private String resultExecution;
    
    @NotBlank (message = Constants.WarningCarValidate.ORGANIZATION_TEST_NOT_BLANK)
   	@Size (max = Constants.WarningCarValidate.ORGANIZATION_TEST_SIZE, message = Constants.WarningCarValidate.ORGANIZATION_TEST_MAX)
    private String organizationTest;

	private Integer id;

    private Integer systemTypeId;
    
    private Integer projectId;

    private Integer deviceId;
    
    private Integer status;

    private Integer createId;
    
    private String createDate;

    private String updateDate;
  
}
