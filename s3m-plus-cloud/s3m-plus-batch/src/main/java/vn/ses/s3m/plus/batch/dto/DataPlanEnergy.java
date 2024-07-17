package vn.ses.s3m.plus.batch.dto;

import lombok.Data;

@Data
public class DataPlanEnergy {

    private Integer projectId;

    private Long deviceId;

    private Integer systemTypeId;

    private String timeStart;

    private String timeEnd;

    private Float ep;

}
