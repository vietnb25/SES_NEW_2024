package vn.ses.s3m.plus.dto;

import java.util.Map;

import lombok.Data;

@Data
public class DataTempHumidity {

    private Long id;
    
    private Integer deviceId;

    private Float t;

    private Float h;

    private Integer online;

    private Integer battery;

    private Float current;

    private String crc16;

    private boolean isNhietDoCao = false;

    private boolean isNhietDoThap = false;

    private boolean isDoAmCao = false;

    private boolean isDoAmThap = false;

    private String sentDate;
    
    private Long transactionDate;
    
    private Double imccb;
    
    private String viewTime;
    
    private Map<Integer, Integer> warningsTypeSetting;

}
