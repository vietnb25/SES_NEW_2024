package vn.ses.s3m.plus.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DataPower {
    private String dateOfMonth;

    private Float power;
    
    private Float powerAccumulated;

    private Double planEnergy;

    private Double targetEnergy;

    private String viewTime;

    private Double sumLandmark;

    private Double sumEnergy;
    
    private String dateOfWeek;
    
    private Integer valueEnergy;

//    private Double sumT1;
//
//    private Double sumT2;
//
//    private Double sumT3;
//
//    private Double sumT4;
//
//    private Double sumT5;
//
//    private Double sumT6;
//
//    private Double sumT7;
//
//    private Double sumT8;
//
//    private Double sumT9;
//
//    private Double sumT10;
//
//    private Double sumT11;
//
//    private Double sumT12;
//
//    private Double sumT1Plan;
//
//    private Double sumT2Plan;
//
//    private Double sumT3Plan;
//
//    private Double sumT4Plan;
//
//    private Double sumT5Plan;
//
//    private Double sumT6Plan;
//
//    private Double sumT7Plan;
//
//    private Double sumT8Plan;
//
//    private Double sumT9Plan;
//
//    private Double sumT10Plan;
//
//    private Double sumT11Plan;
//
//    private Double sumT12Plan;
    private String name;

    private List<DataPower> data;
    
    public DataPower(Float power, Object o, Object o1, String viewTime) {

    }
    
    public DataPower(String viewTime) {
    	 this.viewTime = viewTime;
    }
}
