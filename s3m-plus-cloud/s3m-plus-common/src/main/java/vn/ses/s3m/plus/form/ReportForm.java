package vn.ses.s3m.plus.form;

import lombok.Data;

@Data
public class ReportForm {
    private String reportName;

    private Integer reportTemplate;

    private Integer reportModule;

    private Integer reportSite;

    private String reportSiteName;

    private String[] reportDevices;

    private String reportArea;

    private String reportLoad;

    private Integer reportDeviceType;

    private String reportCa1;

    private String reportCa2;

    private String reportCa3;

    private String reportUnit;

    private Integer reportTypeTime;

    private String reportFromDate;

    private String reportToDate;

    private Integer customerId;
    private Integer projectId;
    private Integer systemTypeId;

    private String customerName;

    private String customerDescription;

    private Integer type;
}