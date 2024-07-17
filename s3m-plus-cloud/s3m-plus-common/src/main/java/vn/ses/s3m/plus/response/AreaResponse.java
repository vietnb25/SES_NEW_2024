package vn.ses.s3m.plus.response;

import java.sql.Timestamp;

import lombok.Data;
import vn.ses.s3m.plus.dto.Area;

@Data
public class AreaResponse {

    private Integer areaId;

    private String areaName;

    private Integer managerId;

    private String description;

    private Integer createId;

    private Double latitude;

    private Double longitude;

    private String managerName;

    private Timestamp updateDate;

    public AreaResponse(Area area) {
        this.areaId = area.getAreaId();
        this.areaName = area.getAreaName();
        this.managerId = area.getManagerId();
        this.description = area.getDescription();
        this.createId = area.getCreateId();
        this.latitude = area.getLatitude();
        this.longitude = area.getLongitude();
        this.managerName = area.getManagerName();
    }

    public AreaResponse() {
    }
}
