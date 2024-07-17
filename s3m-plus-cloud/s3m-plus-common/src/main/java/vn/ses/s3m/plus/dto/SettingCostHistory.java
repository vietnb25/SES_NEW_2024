package vn.ses.s3m.plus.dto;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class SettingCostHistory {
    private Integer projectId;
    private Double peakHour;
    private Double nonPeakHour;
    private Double normalHour;
    private Double vat;
    private Timestamp toDate;
    private Timestamp fromDate;
}
