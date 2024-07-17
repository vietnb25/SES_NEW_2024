package vn.ses.s3m.plus.auth.dto;

import java.util.Map;

import lombok.Data;

@Data
public class LoginResponse {
    private String jwt;
    private Integer firstLoginFlag;
    private String treeData;
    private String mapData;
    private String categoryPaths;
    private Map<String, Object> userData;
    private Integer customerId;
}
