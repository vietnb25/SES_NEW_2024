package vn.ses.s3m.plus.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class Project {

    private Integer projectId;

    private Integer stt;

    private String projectName;

    private String projectCode;

    private Integer radiation;

    private String address;

    private Double latitude;

    private Double longitude;

    private Integer customerId;

    private Integer areaId;

    private Integer managerId;

    private String managerName;

    private Integer superManagerId;

    private String description;

    private Integer statusPower;

    private Integer createId;

    private Integer updateId;

    private Timestamp updateDate;

    private String customerName;

    private Integer pMax;

    private Integer loadNumber;

    private Integer pvNumber;

    private Integer evNumber;

    private Long loadValue;

    private Long pvValue;

    private Long evValue;

    private String areaName;

    private Integer solarNum;

    private Integer windNum;

    private Integer evNum;

    private Integer utilityNum;

    private Integer loadNum;

    private Integer isViewRadiation;

    private Integer isViewForecast;

    private Integer deviceNumber;

    private Long cspTotal;

    private Long solarTotal;

    private Long gridTotal;

    private Long gridEp;

    private String infoProject;

    private String superManagerName;

    private String systemTypeName;

    private Float pTotal;

    private Float qTotal;

    private String imgLoad;

    private String imgPv;

    private String imgGrid;

    private String imgBattery;

    private String imgWind;

    private String shift1;

    private String shift2;

    private String shift3;

    private Integer amountOfPeople;
    private Double emissionFactorCo2Electric;
    private Double emissionFactorCo2Gasoline;
    private Double emissionFactorCo2Charcoal;
    private Double areaOfFloor;
}
