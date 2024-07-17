package vn.ses.s3m.plus.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class SettingShiftEp {
    private Double epTotal;
    private Timestamp viewTime;
    private Integer shiftId;
    private  Double lowCost;
    private  Double normalCost;
    private  Double highCost;
}
