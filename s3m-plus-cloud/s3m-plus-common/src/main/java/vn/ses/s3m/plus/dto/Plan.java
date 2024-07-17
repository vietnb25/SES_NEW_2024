package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class Plan {

    private Integer planId;

    private Integer systemTypeId;

    private String systemTypeName;

    private Integer projectId;

    private String projectName;

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



