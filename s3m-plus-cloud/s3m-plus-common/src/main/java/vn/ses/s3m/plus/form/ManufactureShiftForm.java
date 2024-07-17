package vn.ses.s3m.plus.form;

import lombok.Data;

import java.util.Date;

@Data
public class ManufactureShiftForm {
    private Integer id;
    private Integer projectId;
    private Integer shiftId;
    private Integer productionId;
    private Integer productionStepId;
    private Integer productionNumber;
    private String  devices ;
    private Date viewTime;
}
