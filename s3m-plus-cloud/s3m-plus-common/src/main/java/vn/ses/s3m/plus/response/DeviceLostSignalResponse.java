package vn.ses.s3m.plus.response;

import lombok.Data;

@Data
public class DeviceLostSignalResponse {

    private Long id;

    private Long deviceId;

    private String deviceName;

    private String sentDateInstance;

    private String deviceCount;
}
