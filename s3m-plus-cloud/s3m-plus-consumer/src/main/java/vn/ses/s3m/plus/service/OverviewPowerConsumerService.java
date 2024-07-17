package vn.ses.s3m.plus.service;

import java.util.Map;

public interface OverviewPowerConsumerService {

    Long getSumEnergyByDay(Map<String, Object> condition);

    Long getSumEnergyByMonth(Map<String, Object> condition);

    Long getSumEnergyByYear(Map<String, Object> condition);

    Long getSumEnergy(Map<String, Object> condition);

    int getCountWarningLoad(Map<String, Object> condition);

    int getCountWarningPV(Map<String, Object> condition);

    Long getSumEnergyByDayPV(Map<String, Object> condition);

    Long getSumEnergyByMonthPV(Map<String, Object> condition);

    Long getSumEnergyByYearPV(Map<String, Object> condition);

    Long getSumEnergyPV(Map<String, Object> condition);

    int getCountWarningGrid(Map<String, Object> condition);

    Long getSumEnergyByDayGrid(Map<String, Object> condition);

    Long getSumEnergyByMonthGrid(Map<String, Object> condition);

    Long getSumEnergyByYearGrid(Map<String, Object> condition);

    Long getSumEnergyGrid(Map<String, Object> condition);

}
