package vn.ses.s3m.plus.response;

import lombok.Data;
import vn.ses.s3m.plus.dto.WarningCar;

@Data
public class WarningCarResponse {
	private Integer id;

    private Integer systemTypeId;
    
    private Integer projectId;

    private Integer deviceId;
    
    private Integer status;

    private Integer createId;
    
    private String organizationCreate;
    
    private String content;
    
    private String reasonMethod;
    
    private String organizationExecution;
    
    private String completionTime;
    
    private String resultExecution;
    
    private String organizationTest;
    
    private String createDate;

    private String updateDate;

	public WarningCarResponse() {
		
	}

	public WarningCarResponse(WarningCar warningCar) {
		this.id = warningCar.getId();
		this.systemTypeId = warningCar.getSystemTypeId();
		this.projectId = warningCar.getProjectId();
		this.deviceId = warningCar.getDeviceId();
		this.status = warningCar.getStatus();
		this.createId = warningCar.getCreateId();
		this.organizationCreate = warningCar.getOrganizationCreate();
		this.content = warningCar.getContent();
		this.reasonMethod = warningCar.getReasonMethod();
		this.organizationExecution = warningCar.getOrganizationExecution();
		this.completionTime = warningCar.getCompletionTime();
		this.resultExecution = warningCar.getResultExecution();
		this.organizationTest = warningCar.getOrganizationTest();
		this.createDate = warningCar.getCreateDate();
		this.updateDate = warningCar.getUpdateDate();
	}
}
