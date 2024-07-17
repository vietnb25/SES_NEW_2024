package vn.ses.s3m.plus.response;

import java.sql.Timestamp;

import lombok.Data;
import vn.ses.s3m.plus.dto.Project;

@Data
public class ProjectResponse {
    private Integer projectId;
    private String projectName;
    private String customerName;
    private String address;
    private Timestamp updateDate;
    private Integer solarNum;
    private Integer windNum;
    private Integer evNum;
    private Integer utilityNum;
    private Integer loadNum;
    private String areaName;
    private String managerName;
    private String superManagerName;
    private Integer areaId;
    private Integer customerId;
    private Double latitude;
    private Double longitude;
    private Integer amountOfPeople;
    private Double emissionFactorCo2Electric;
    private Double emissionFactorCo2Gasoline;
    private Double emissionFactorCo2Charcoal;
    private Double areaOfFloor;

    public ProjectResponse(Project project) {
        this.projectId = project.getProjectId();
        this.projectName = project.getProjectName();
        this.areaName = project.getAreaName();
        this.customerName = project.getCustomerName();
        this.address = project.getAddress();
        this.solarNum = project.getSolarNum();
        this.windNum = project.getWindNum();
        this.evNum = project.getEvNum();
        this.utilityNum = project.getUtilityNum();
        this.loadNum = project.getLoadNum();
        this.updateDate = project.getUpdateDate();
        this.areaId = project.getAreaId();
        this.customerId = project.getCustomerId();
        this.latitude = project.getLatitude();
        this.longitude = project.getLongitude();
        this.areaOfFloor = project.getAreaOfFloor();
        this.amountOfPeople = project.getAmountOfPeople();
        this.emissionFactorCo2Electric = project.getEmissionFactorCo2Electric();
        this.emissionFactorCo2Gasoline = project.getEmissionFactorCo2Gasoline();
        this.emissionFactorCo2Charcoal = project.getEmissionFactorCo2Charcoal();

    }
}
