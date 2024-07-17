package vn.ses.s3m.plus.form;

import lombok.Data;

import java.sql.Date;
@Data
public class LandmarksPlansEnergyForm {
    private Integer id;
    private String dateOfWeek;
    private Integer jan;
    private Integer feb;
    private Integer mar;
    private Integer may;
    private Integer apr;
    private Integer jun;
    private Integer jul;
    private Integer aug;
    private Integer sep;
    private Integer oct;
    private Integer nov;
    private Integer dec;
    private Date updateDate;
    private Integer status;
    private String schema;
}
