package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class DataInstant {
    public Float powerInstantLoad;

    public Float powerInstantSolar;

    public Float powerInstantGrid;

    public Float powerInstantWind;

    public Float powerInstantBattery;

    public Float energyInDayLoad;

    public Float energyInDaySolar;

    public Float energyInDayGrid;

    public Float energyInDayWind;

    public Float energyInDayBattery;

    public Float energyInMonthLoad;

    public Float energyInMonthSolar;

    public Float energyInMonthGrid;

    public Float energyInMonthWind;

    public Float energyInMonthBattery;

    public Float energyInYearLoad;

    public Float energyInYearSolar;

    public Float energyInYearGrid;

    public Float energyInYearWind;

    public Float energyInYearBattery;

    public Integer energyTotalLoad;

    public Integer energyTotalSolar;

    public Integer energyTotalGrid;

    public Integer energyTotalWind;

    public Integer energyTotalBattery;
    
    public String sentDate;
}
