package vn.ses.s3m.plus.response;

import java.sql.Timestamp;

import lombok.Data;
import vn.ses.s3m.plus.dto.Manager;

@Data
public class ManagerResponse {

    private Integer managerId;

    private String managerName;

    private String managerCode;

    private Integer superManagerId;

    private String superManagerName;

    private String description;

    private Timestamp updateDate;

    private Integer customerId;

    private Double latitude;

    private Double longitude;

    public ManagerResponse(final Manager manager) {
        this.managerId = manager.getManagerId();
        this.managerName = manager.getManagerName();
        this.managerCode = manager.getManagerCode();
        this.superManagerId = manager.getSuperManagerId();
        this.superManagerName = manager.getSuperManagerName();
        this.description = manager.getDescription();
        this.updateDate = manager.getUpdateDate();
        this.latitude = manager.getLatitude();
        this.longitude = manager.getLongitude();
    }
}
