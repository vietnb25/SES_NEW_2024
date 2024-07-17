package vn.ses.s3m.plus.response;

import java.sql.Timestamp;

import lombok.Data;
import vn.ses.s3m.plus.dto.SuperManager;

@Data
public class SuperManagerResponse {

    private Integer superManagerId;

    private String superManagerName;

    private String description;

    private Double latitude;

    private Double longitude;

    private Timestamp updateDate;

    public SuperManagerResponse(final SuperManager sm) {
        this.superManagerId = sm.getSuperManagerId();
        this.superManagerName = sm.getSuperManagerName();
        this.description = sm.getDescription();
        this.updateDate = sm.getUpdateDate();
        this.longitude = sm.getLongitude();
        this.latitude = sm.getLatitude();
    }

}
