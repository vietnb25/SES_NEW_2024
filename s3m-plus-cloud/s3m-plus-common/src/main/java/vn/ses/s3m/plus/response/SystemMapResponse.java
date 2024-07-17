package vn.ses.s3m.plus.response;

import java.sql.Timestamp;

import lombok.Data;
import vn.ses.s3m.plus.dto.SystemMap;

@Data
public class SystemMapResponse {
	
	private Integer id;

	private String name;

	private Integer projectId;

	private Integer systemTypeId;
	
	private Integer layer;

	private String jsonData;
	
	private String color;

	private Integer mainFlag;

	private Integer connectTo;

	private String description;

	private Integer createId;

	private Integer updateId;

    private Timestamp createDate;

    private Timestamp updateDate;
    
    public SystemMapResponse (SystemMap systemMap) {
    	this.id = systemMap.getId();
    	this.name = systemMap.getName();
    	this.projectId = systemMap.getProjectId();
    	this.systemTypeId = systemMap.getSystemTypeId();
    	this.layer = systemMap.getLayer();
    	this.jsonData = systemMap.getJsonData();
    	this.color = systemMap.getColor();
    	this.mainFlag = systemMap.getMainFlag();
    	this.connectTo = systemMap.getConnectTo();
    	this.description = systemMap.getDescription();
    	this.createId = systemMap.getCreateId();
    	this.updateId = systemMap.getUpdateId();
    	this.createDate = systemMap.getCreateDate();
    	this.updateDate = systemMap.getUpdateDate();
    }

}
