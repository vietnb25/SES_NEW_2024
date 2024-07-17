package vn.ses.s3m.plus.dto.evn;

import java.util.Map;

import lombok.Data;

@Data
public class JsonSystemTree {
	
	private String id;
	
	private String type;
	
	private String parent;
	
	private String text;
	
	private String mainName;
	
	private Map<String, String> data;

}
