package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class Manufacture {

    private Integer id;

    private Integer systemTypeId;

    private Integer projectId;

    private String deviceIds;

    private String area;

    private Integer loadTypeId;

    private Integer objectTypeId;

    private Integer viewType;

    private String viewTime;

    private Integer unitId;

    private Float shift1;

    private Float shift2;

    private Float shift3;

    private String shift1Date;

    private String shift2Date;

    private String shift3Date;

    private Float totalUnit;

    private Float ravenue;

    private Integer productionId;

    private Integer productionStepId;

    private String updateDate;

    private Integer ep1;

    private Integer ep2;

    private Integer ep3;

}
