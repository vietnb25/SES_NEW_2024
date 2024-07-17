package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class Chart {

    private int stt;

    private Long id;

    private Long deviceId;

    private String deviceCode;

    private String deviceName;

    private Integer deviceType;

    private String deviceTypeName;

    private Integer systemTypeId;

    private String systemTypeName;

    private Integer managerId;

    private String managerName;

    private Integer areaId;

    private String areaName;

    private Integer projectId;

    private String projectName;

    private String address;

    private Float pTotal;

    private Integer epIn;

    private Integer epOut;

    private String viewTime;

    private Integer viewType;

    private Float costHighIn;

    private Float costMediumIn;

    private Float costLowIn;

    private Float costHighOut;

    private Float costMediumOut;

    private Float costLowOut;

    private Float ratio;

    private Float power;

    private String hour;

    private String time;
    
    private Float cost;
    
    private Float lowEp;

    private Float normalEp;

    private Float highEp;
    
    private Float t;

    private Float h;
    
    private Float indicator;
    
    private String name;
    
    private String day;
}
