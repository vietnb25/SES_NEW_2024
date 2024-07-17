package vn.ses.s3m.plus.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class LandmarksPlansEnergy {
    private Integer id;

    private String dateOfWeek;
    
    private String year;

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

    private Integer valueEnergy;

    private Double sumT1;

    private Double sumT2;

    private Double sumT3;

    private Double sumT4;

    private Double sumT5;

    private Double sumT6;

    private Double sumT7;

    private Double sumT8;

    private Double sumT9;

    private Double sumT10;

    private Double sumT11;

    private Double sumT12;

    private Double sumT1Plan;

    private Double sumT2Plan;

    private Double sumT3Plan;

    private Double sumT4Plan;

    private Double sumT5Plan;

    private Double sumT6Plan;

    private Double sumT7Plan;

    private Double sumT8Plan;

    private Double sumT9Plan;

    private Double sumT10Plan;

    private Double sumT11Plan;

    private Double sumT12Plan;

    private String dateOfMonth;

    private Double sumLandmark;

    private Double sumEnergy;

    private Double targetEnergy;

    private Double planEnergy;

    private Float power;

    private String viewTime;
    
    private String day;


}
