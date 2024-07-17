package vn.ses.s3m.plus.form;


import java.sql.Date;

import org.springframework.boot.jackson.JsonComponent;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class SimulationDataForm {
	private Integer year;
	private Integer jan;
	private Integer mar;
	private Integer apr;
	private Integer may;
	private Integer jun;
	private Integer feb;
	private Integer jul;
	private Integer aug;
	private Integer sep;
	private Integer oct;
	private Integer nov;
	private Integer dec;
	private Date updateDate;
	private Integer projectId;
	private Integer systemTypeId;
	private String customer;
}
