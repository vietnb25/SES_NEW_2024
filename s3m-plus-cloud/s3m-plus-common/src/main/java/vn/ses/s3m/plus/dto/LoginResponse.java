package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String id;
    private String username;
    private String role;
}
