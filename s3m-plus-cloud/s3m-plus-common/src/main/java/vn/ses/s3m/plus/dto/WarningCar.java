package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class WarningCar {
	
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
    
    private String area;
    
    private String deviceName;
    
    private String objectTypeName;

    private String loadTypeName;  
    
    private Integer countStatus;
}
