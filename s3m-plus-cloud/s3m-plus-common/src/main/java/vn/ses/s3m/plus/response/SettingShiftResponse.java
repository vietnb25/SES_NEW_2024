package vn.ses.s3m.plus.response;

import lombok.Data;
import vn.ses.s3m.plus.dto.SettingShift;

import java.sql.Time;
import java.sql.Timestamp;


@Data
public class SettingShiftResponse {
//      private String message;
//      private Object data;
    private Integer id;
    private Integer projectId;
    private String shiftName;
    private String startTime;
    private String endTime;
    private String createDate;
    private String updateDate;
    private Integer status;

    public SettingShiftResponse(SettingShift s) {
        this.id = s.getId();
        this.projectId = s.getProjectId();
        this.shiftName = s.getShiftName();
        this.startTime = s.getStartTime();
        this.endTime = s.getEndTime();
        this.createDate = s.getCreateDate();
        this.updateDate = s.getUpdateDate();
        this.status = s.getStatus();
    }
}
