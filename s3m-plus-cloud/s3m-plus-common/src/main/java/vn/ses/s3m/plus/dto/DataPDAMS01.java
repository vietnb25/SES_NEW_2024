package vn.ses.s3m.plus.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class DataPDAMS01 {
    private Integer id;
    private Integer deviceId;
    private Integer indicator;
    private Double ratio;
    private Double eppc;
    private Double pdLevel;
    private Double notifier;
    private Timestamp sentDate;
    private Integer settingValue;
    private String deviceName;
    private Timestamp viewTime;

}
