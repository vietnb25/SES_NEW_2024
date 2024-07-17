package vn.ses.s3m.plus.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class SimulationDataEP {
	private Integer id;
	private Integer year;
	private Integer jan;
	private Integer mar;
	private Integer feb;
	private Integer apr;
	private Integer may;
	private Integer jun;
	private Integer jul;
	private Integer aug;
	private Integer sep;
	private Integer oct;
	private Integer nov;
	private Integer dec;
	private Date updateDate;
	private Integer projectId;
	private Integer systemTypeId;
}
