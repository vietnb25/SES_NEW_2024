package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class OtpData {

    private Integer id;

    private Integer userId;

    private Integer customerId;

    private String createDate;

    private Integer status;

    private Integer otpCode;

    private String userName;

}
