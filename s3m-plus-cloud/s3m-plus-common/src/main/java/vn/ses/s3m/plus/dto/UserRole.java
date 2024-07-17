package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class UserRole {
    private Long id;

    private Long userId;

    private Long roleId;

    private String roleName;

    private String roleCode;
}
