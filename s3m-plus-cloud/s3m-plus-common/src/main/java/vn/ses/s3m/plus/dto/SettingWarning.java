package vn.ses.s3m.plus.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class SettingWarning {
    private Integer settingId;
    private Integer type;
    private Integer projectId;
    private Integer customerId;
    private Integer warningTypeId;
    private String warningTypeName;
    private Integer warningLevel;
    private String settingValue;
    private String description;
    private String descriptionMst;
    private Timestamp updateDate;
}
