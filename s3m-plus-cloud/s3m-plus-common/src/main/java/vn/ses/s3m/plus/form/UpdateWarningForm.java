package vn.ses.s3m.plus.form;

import lombok.Data;

@Data
public class UpdateWarningForm {
    private Integer id;
    private Integer status;
    private String username;
    private String description;
    private Integer customerId;
    private String fromDate;
    private String toDate;
    private String deviceId;
    private String warningType;
}
