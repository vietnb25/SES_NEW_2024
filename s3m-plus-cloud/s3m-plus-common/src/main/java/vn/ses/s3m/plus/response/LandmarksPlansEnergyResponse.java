package vn.ses.s3m.plus.response;

import lombok.Data;
import vn.ses.s3m.plus.dto.LandmarksPlansEnergy;

import java.sql.Date;
@Data
public class LandmarksPlansEnergyResponse {
    private Integer id;
    private String dateOfWeek;
    private Integer jan;
    private Integer feb;
    private Integer mar;
    private Integer may;
    private Integer apr;
    private Integer jun;
    private Integer jul;
    private Integer aug;
    private Integer sep;
    private Integer oct;
    private Integer nov;
    private Integer dec;
    private Date updateDate;
    private Integer status;

    public LandmarksPlansEnergyResponse (LandmarksPlansEnergy e) {
        this.dateOfWeek = e.getDateOfWeek();
        this.jan = e.getJan();
        this.feb = e.getFeb();
        this.mar = e.getMar();
        this.apr = e.getApr();
        this.may = e.getMay();
        this.jun = e.getJun();
        this.jul = e.getJul();
        this.aug = e.getAug();
        this.sep = e.getSep();
        this.oct = e.getOct();
        this.nov = e.getNov();
        this.dec = e.getDec();
        this.updateDate = e.getUpdateDate();
        this.status = e.getStatus();
        this.id = e.getId();
    }
}
