package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class User {

    private Integer id;

    private String username;

    private String password;

    private String staffName;

    private String email;

    private String phone;

    private String img;

    private String company;

    private Integer customerId;

    private Integer superManagerId;

    private Integer managerId;

    private Integer areaId;

    private Integer projectId;

    private Integer systemTypeId;

    private Integer userType;

    private Integer deleteFlag;

    private Integer failedAttempts;

    private Integer lockFlag;

    private String description;

    private Integer createId;

    private String createDate;

    private Integer updateId;

    private String updateDate;

    private String resetPasswordToken;

    private Long resetPasswordTokenExpire;

    private Integer firstLoginFlag;

    private Integer authorized;

    private String customerIds;

    private String projectIds;

    private Integer targetId;

    private String newPassword;
    
    private Integer prioritySystem;
    
    private String priorityIngredients;
    
    private String priorityLoad;
    
    private String prioritySolar;
    
    private String priorityGrid;
    
    private String priorityBattery;
    
    private String priorityWind;
}
