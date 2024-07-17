package vn.ses.s3m.plus.response;

import lombok.Data;

@Data
public class ProjectLocation {
    private Integer projectId;

    private String projectName;

    private Integer statusPower;

    private Double latitude;

    private Double longitude;

    private String areaName;

    private Integer areaId;

    private String managerName;

    private Integer managerId;

    private String superManagerName;

    private Integer superManagerId;

    private String type;

    private Integer customerId;

    private String loadStatus;

    private String pvStatus;

    private String gridStatus;

    private String statusColor;

    private String systemTypeName;

    private String windStatus;

    private Double windPower;

    private String batteryStatus;

    private Double batteryPower;

    private String currentTime;

    private Float pTotal;

    private Float qTotal;

    private Float pSolar;

    private Float qSolar;

    private Float pGrid;

    private Float qGrid;

    private Float pWind;

    private Float qWind;

    private Float pBattery;

    private Float qBattery;

}
