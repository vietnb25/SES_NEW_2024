package vn.ses.s3m.plus.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Production {
    private Integer id;
    private Integer projectId;
    private String projectName;
    private Integer productionId;
    private String productionName;
    private Integer productionStepId;
    private String productionStepName;
    private String unit;
    private String deviceIds;
    private Timestamp updateDate;
}
