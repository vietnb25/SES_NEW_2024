package vn.ses.s3m.plus.response;

import lombok.Data;

@Data
public class ProjectInfo {
    private Integer projectId;

    private String projectName;

    private String address;

    private Double latitude;

    private Double longitude;

    private Double loadPower;

    private Double pvPower;

    private String loadStatus;

    private String pvStatus;

    private String currentTime;

    private Integer customerId;

    private Float gridPower;

    private String gridStatus;

    private String windStatus;

    private Double windPower;

    private String batteryStatus;

    private Double batteryPower;

    private String description;
}
