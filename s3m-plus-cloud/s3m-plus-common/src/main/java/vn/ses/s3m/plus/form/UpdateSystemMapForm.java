package vn.ses.s3m.plus.form;

import lombok.Data;
import vn.ses.s3m.plus.dto.SystemMap;

@Data
public class UpdateSystemMapForm {
	
	private SystemMap systemMap;
	
	private String listDeviceUpdate;
	
	private String deviceJsonList;

}
