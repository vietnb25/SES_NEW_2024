package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class DataPqs {

    private Integer id;

    private Long deviceId;

    private String deviceName;

    private Integer systemTypeId;

    private Integer deviceType;

    private Integer viewType;

    private String viewTime;

    private Float pTotal;

    private Float ep;

    private Float ep1;

    private Float ep2;

    private Float ep3;
    private Float ep4;
    private Float ep5;

    private Float epCache;

    private Float epAtATime;

    private Float lowEp;

    private Float normalEp;

    private Float highEp;

    private Float lowCostIn;

    private Float normalCostIn;

    private Float highCostIn;

    private Float lowCostOut;

    private Float normalCostOut;

    private Float highCostOut;

    private String shift1;

    private String shift2;

    private String shift3;

    private Float valueShift1;

    private Float valueShift2;

    private Float valueShift3;

    private Float totalUnit;

    private String sentDate;

    private Integer warningId;
    private Integer warningTypeId;
    private String warningTypeName;
    private Integer numberOfEquiment;
    private Integer quantityWarning;
    private String area;
    private Integer loadType;
    private Integer deviceTypeId;
    private Integer loadTypeId;
    private String loadTypeName;
    private Float t;
    private Float tCache;
    private Float tAtATime;
}
