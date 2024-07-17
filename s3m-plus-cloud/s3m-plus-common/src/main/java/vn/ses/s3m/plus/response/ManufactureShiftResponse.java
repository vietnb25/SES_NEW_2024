package vn.ses.s3m.plus.response;

import lombok.Data;
import vn.ses.s3m.plus.dto.DeviceName;
import vn.ses.s3m.plus.dto.Production;

import java.sql.Timestamp;
import java.util.List;

@Data
public class ManufactureShiftResponse {
    private Integer id;
    private Integer projectId;
    private String projectName;
    private Integer productionId;
    private String productionName;
    private Integer productionStepId;
    private String productionStepName;
    private String unit;
    private List<DeviceName> devices;
    private Timestamp updateDate;

    public ManufactureShiftResponse(Production pro, List<DeviceName> ls) {
        this.id = pro.getId();
        this.devices = ls;
        this.projectId = pro.getProjectId();
        this.projectName = pro.getProjectName();
        this.productionId = pro.getProductionId();
        this.productionName = pro.getProductionName();
        this.productionStepId = pro.getProductionStepId();
        this.productionStepName = pro.getProductionStepName();
        this.updateDate = pro.getUpdateDate();
        this.unit = pro.getUnit();
    }

}
