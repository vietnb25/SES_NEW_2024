package vn.ses.s3m.plus.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ses.s3m.plus.dto.Customer;
import vn.ses.s3m.plus.dto.Plan;

import java.sql.Timestamp;
@Data
@NoArgsConstructor
public class PlanResponse {

    private Integer planId;

    private String startDate;

    private String endDate;

    private String organizationCreate;

    private String content;

    private String organizationExecution;

    private String completionTime;

    private String resultExecution;

    private String organizationTest;

    public PlanResponse(Plan plan) {
        this.planId = plan.getPlanId();
        this.startDate = plan.getStartDate();
        this.endDate = plan.getEndDate();
        this.organizationCreate = plan.getOrganizationCreate();
        this.content = plan.getContent();
        this.organizationExecution = plan.getOrganizationExecution();
        this.completionTime = plan.getCompletionTime();
        this.resultExecution = plan.getResultExecution();
        this.organizationTest = plan.getOrganizationTest();
    }

}
