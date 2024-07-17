package vn.ses.s3m.plus.dto;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class SettingCost {
    private Integer id;
    private Integer projectId;
    private Integer settingCostMstId;
    private Double settingValue;
    private String description;
    private String descriptionMst;
    private Timestamp updateDate;
    private Timestamp createDate;
}
