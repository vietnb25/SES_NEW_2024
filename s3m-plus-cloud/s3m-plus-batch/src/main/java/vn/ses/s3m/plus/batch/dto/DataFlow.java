package vn.ses.s3m.plus.batch.dto;

import lombok.Data;

@Data
public class DataFlow {
	private Long id;

	private Long deviceId;

	private String deviceName;

	private Integer deviceType;

	private String deviceCode;

	private Float fs;

	private Float fm;

	private Float fh;

	private Float v;

	private Float t;

	private String crc;
	
	private String sentDate;

    private Long transactionDate;

    private String viewTime;

}
