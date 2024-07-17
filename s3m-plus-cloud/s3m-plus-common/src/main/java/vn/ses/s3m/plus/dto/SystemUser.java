package vn.ses.s3m.plus.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class SystemUser {
    private Long userId;

    private String userName;

    private String password;

    private String staffName;

    private String email;

    private String phone;

    private String address;

    private String company;

    private Integer superManagerId;

    private Integer managerId;

    private Integer customerId;

    private Integer areaId;

    private Integer projectId;

    private Integer systemTypeId;

    private Integer userType;

    private String description;

    private Integer createId;

    private Timestamp createDate;

    private Integer updateId;

    private Timestamp updateDate;
}
