package vn.ses.s3m.plus.dto.evn;

import lombok.Data;

@Data
public class Plant {
	private String name;

	private Double congSuatLapDat;

	private Double congSuatTietGiam;

	private	Double congSuatChoPhepPhat;

	private Double congSuatHienTai;

	private String fromDateTime;
	private String toDateTime;

	private String status;
}
