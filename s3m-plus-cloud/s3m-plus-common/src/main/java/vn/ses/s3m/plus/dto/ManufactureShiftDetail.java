package vn.ses.s3m.plus.dto;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

@Data
public class ManufactureShiftDetail {
    private Integer id;
    private Double epTotal;
    private Integer manufactureId;
    private Integer shiftId;
    private Date viewTime;
    private Integer productionNumber;
    private Double totalRevenue;
    private Timestamp updateDate;

}
