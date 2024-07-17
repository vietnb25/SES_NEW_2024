package vn.ses.s3m.plus.form;

import lombok.Data;

@Data
public class PlanForm {

    private Integer planId;

    private Integer systemTypeId;

    private Integer projectId;

    private String customerId;

    private String organizationCreate;

    private String content;

    private String organizationExecution;

    private String organizationTest;

    private String resultExecution;

    private String completionTime;

    private Integer status;

    private String createDate;

    private String updateDate;

    private String startDate;

    private String endDate;
}

