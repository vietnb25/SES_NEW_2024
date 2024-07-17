package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class WarningInfor {

    private Integer deviceId;

    private Integer receiverId;

    private Integer warningType;
}
