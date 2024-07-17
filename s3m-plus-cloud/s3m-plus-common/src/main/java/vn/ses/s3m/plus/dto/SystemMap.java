package vn.ses.s3m.plus.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class SystemMap {

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

    private Integer deviceNumber;

    private Long powerTotal;

    private Long energyTotal;

    private String systemTypeName;

    private Float maxPtotal;

    private Float minPtotal;

    private Float realTime;

    private Long sumEnergy;

    private Long sumEnergyCurrentYear;

    private Long sumEnergyLastYear;

    private Long sumEnergyToday;

    private Long sumEnergyPreday;

    private Long sumEnergyCurMonth;

    private Long sumEnergyPreMonth;

    private Integer warningCount;

    private Float avgPtotal;

    private String projectName;

}
