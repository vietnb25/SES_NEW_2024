package vn.ses.s3m.plus.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class DataPDHTR02 {
    private Integer id;
    private Integer deviceId;
    private Long alarmStatusBit;
    private Double lfbRatio;
    private Double mfbRatio;
    private  Double lfbEppc;
    private Double mfbEppc;
    private Double meanRatio;
    private Double meanEppc;
    private Double hfbRatio;
    private Double hfbEppc;
    private Integer indicator;
    private Double ratioEppcHi;
    private Double ratioEppcLo;
    private Timestamp sentDate;
    private Integer settingValue;
    private String deviceName;
    private Timestamp viewTime;

}
