package vn.ses.s3m.plus.dto;

import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;

@Data
public class SettingShift {
    private Integer id;
    private Integer projectId;
    private String shiftName;
    private String startTime;
    private String endTime;
    private String createDate;
    private String updateDate;
    private Integer status;
}
