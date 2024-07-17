package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class UserDto {
    private Integer id;

    private String staffName;

    private String customerName;

    private String customerId;

    private String superManagerName;

    private String superManagerId;

    private String managerName;

    private String managerId;

    private String areaName;

    private String areaId;

    private String projectName;

    private String projectId;

    private String username;

    private Integer lockFlag;

    private String updateDate;

    private Integer userType;

    private Integer roleId;

    private String email;

    private Integer authorized;

    private Integer createId;

    private String customerIds;

    private String targetManager;

    private Integer targetId;
    
    private String projectIds;
    
    private Integer prioritySystem;
    
    private String priorityIngredients;
    
    private String priorityLoad;
    
    private String prioritySolar;
    
    private String priorityGrid;
    
    private String priorityBattery;
    
    private String priorityWind;
}
