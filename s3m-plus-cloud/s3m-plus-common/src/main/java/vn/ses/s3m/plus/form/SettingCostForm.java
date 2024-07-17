package vn.ses.s3m.plus.form;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class SettingCostForm {
    private Integer projectId;
    private Double peakHour;
    private Double nonPeakHour;
    private Double normalHour;
    private Double vat;
    private Timestamp create_date;
    private Timestamp toDate;
    private Timestamp fromDate;
}
