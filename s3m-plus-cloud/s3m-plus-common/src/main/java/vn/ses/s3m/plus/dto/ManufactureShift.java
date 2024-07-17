package vn.ses.s3m.plus.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class ManufactureShift {
    private Integer id;
    private Integer shiftId;
    private Integer projectId;
    private Integer productionId;
    private Integer productionStepId;
    private Integer productionNumber;
    private Double epTotal;
    private Date viewTime;
    private Timestamp createDate;
    private Timestamp updateDate;
}
