package vn.ses.s3m.plus.batch.dto;

import lombok.Data;

@Data
public class DataInverter1View {

    private Integer id;

    private Long deviceId;

    private Integer viewType;

    private String viewTime;

    private Float Ptotal;

    private Float Pdc;

    private Float Ep;

    private String sentDate;
}
