package vn.ses.s3m.plus.form;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MaterialForm {
    private Integer projectId;
    private Integer materialId;
    private Double peakHour;
    private Double nonPeakHour;
    private Double normalHour;
    private Double vat;
}
