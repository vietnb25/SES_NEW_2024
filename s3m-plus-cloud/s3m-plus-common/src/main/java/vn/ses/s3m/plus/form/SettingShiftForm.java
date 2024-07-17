package vn.ses.s3m.plus.form;

import lombok.Data;

import java.sql.Time;

@Data
public class SettingShiftForm {

    private  Integer projectId;

    private  Integer id;

    private  String shiftName;

    private String startTime;

    private  String endTime;

    private  Integer status;

}
