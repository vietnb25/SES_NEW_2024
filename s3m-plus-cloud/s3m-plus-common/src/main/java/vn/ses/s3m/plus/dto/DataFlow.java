package vn.ses.s3m.plus.dto;

import java.util.Map;

import lombok.Data;

@Data
public class DataFlow {
	private Long id;
    
    private Integer deviceId;
	
	private Float fs;

    private Float fm;

    private Float fh;

    private Float v;

    private Float t;

    private String crc;

    private String sentDate;
    
    private Long transactionDate;
    
    private Double imccb;
    
    private String viewTime;
}
