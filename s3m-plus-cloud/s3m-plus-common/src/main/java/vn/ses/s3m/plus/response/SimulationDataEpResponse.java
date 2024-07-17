package vn.ses.s3m.plus.response;

import java.sql.Date;

import lombok.Data;
import vn.ses.s3m.plus.dto.SimulationDataEP;

@Data
public class SimulationDataEpResponse {
	private Integer id;
	private Integer year;
	private Integer jan;
	private Integer mar;
	private Integer apr;
	private Integer may;
	private Integer jun;
	private Integer jul;
	private Integer aug;
	private Integer sep;
	private Integer oct;
	private Integer nov;
	private Integer dec;
	private Integer feb;
	private Date updateDate;
	private Integer projectId;
	private Integer systemTypeId;
	
	public SimulationDataEpResponse() {
		
	}
	public SimulationDataEpResponse(SimulationDataEP e) {
		this.id = e.getId();
		this.year = e.getYear();
		this.jan = e.getJan();
		this.mar = e.getMar();
		this.apr = e.getMar();
		this.may = e.getMay();
		this.jun = e.getJun();
		this.jul = e.getJul();
		this.aug = e.getAug();
		this.sep = e.getSep();
		this.oct = e.getOct();
		this.nov = e.getNov();
		this.dec = e.getDec();
		this.feb = e.getFeb();
		this.updateDate = e.getUpdateDate();
		this.projectId = e.getProjectId();
		this.systemTypeId = e.getSystemTypeId();
	}
}
