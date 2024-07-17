package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class Warning {
    private Long id;

    private Long warningId;

    private Integer warningType;

    private String warningTypeName;

    private Integer warningCategory;

    private Integer warningLevel;

    private Integer warningNo;

    private Long warningDuration;

    private Long deviceId;

    private String deviceName;

    private Integer deviceType;

    private String deviceTypeName;

    private Long totalDevice;

    private Integer systemTypeId;

    private String systemTypeName;

    private String fromDate;

    private String toDate;

    private Integer handleFlag;

    private String projectName;

    private String projectId;

    private String handleName;

    private String staffName;

    private String phone;

    private Integer settingHistoryId;

    private String description;

    private Integer deleteFlag;

    private Integer createId;

    private Integer status;

    private String createDate;

    private String sentDate;

    private Integer updateId;

    private String updateDate;

    private Integer systemMapId;

    private String systemMapName;

    private Integer layer;

    private Integer total;

    private Long ep;

    private String viewTime;

    private String settingValueHistory;

    private String settingValueHistory2;


}
