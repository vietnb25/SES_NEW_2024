package vn.ses.s3m.plus.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;
import vn.ses.s3m.plus.common.Constants;

@Data
public class CustomerForm {
    private Integer customerId;

    @NotBlank (message = Constants.CustomerValidate.CUSTOMER_NAME_NOT_BLANK)
    @Size (max = Constants.CustomerValidate.CUSTOMER_NAME_MAX,
        message = Constants.CustomerValidate.CUSTOMER_NAME_MAX_LENGTH)
    private String customerName;

    @NotBlank (message = Constants.CustomerValidate.CUSTOMER_CODE_NOT_BLANK)
    @Size (max = Constants.CustomerValidate.CUSTOMER_CODE_MAX,
        message = Constants.CustomerValidate.CUSTOMER_CODE_MAX_LENGTH)
    private String customerCode;

    private String description;
}
