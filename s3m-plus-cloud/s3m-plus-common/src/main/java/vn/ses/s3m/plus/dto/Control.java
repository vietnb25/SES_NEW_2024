package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class Control {

    private String historyId;

    private String deviceId;

    private String deviceName;

    private Double csdm;

    private Double cstg;

    private Double cscp;

    private Double congSuat;

    private String parentId;

    private String fromTime;

    private String toTime;

    private String timeViewFrom;

    private String timeViewTo;

    private String timeSetting;

    private String status;
}
