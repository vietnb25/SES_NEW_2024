package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class PermissionCategoryData {
    private Integer id;
    private String content;
    private Integer userId;
    private String createdAt;
}
