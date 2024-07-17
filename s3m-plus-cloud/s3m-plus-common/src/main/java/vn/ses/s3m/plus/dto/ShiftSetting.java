package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class ShiftSetting {

    private Integer id;

    private Integer customerId;

    private Integer systemTypeId;

    private Integer projectId;

    private String shift1;

    private String shift2;

    private String shift3;

    private String shiftHistoryCode;

    private String createDate;

    private String fromDate;

    private String toDate;

    private String updateDate;

    private String shiftName;

    private String startTime;

    private String endTime;

    private Integer status;
}
