package vn.ses.s3m.plus.dto;

import java.util.Map;

import lombok.Data;

@Data
public class DataPressure {
	private Long id;
    
    private Integer deviceId;
	
	private Float p;

    private String crc;

    private String sentDate;
    
    private Long transactionDate;
    
    private Double imccb;
    
    private String viewTime;
}
