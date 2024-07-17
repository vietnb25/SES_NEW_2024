package vn.ses.s3m.plus.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.OverviewPowerConsumerMapper;

@Service
public class OverviewPowerConsumerServiceImpl implements OverviewPowerConsumerService {

    @Autowired
    private OverviewPowerConsumerMapper mapper;

    @Override
    public Long getSumEnergy(Map<String, Object> condition) {
        return mapper.getSumEnergy(condition);
    }

    @Override
    public Long getSumEnergyByYear(Map<String, Object> condition) {
        return mapper.getSumEnergyByYear(condition);
    }

    @Override
    public Long getSumEnergyByMonth(Map<String, Object> condition) {
        return mapper.getSumEnergyByMonth(condition);
    }

    @Override
    public Long getSumEnergyByDay(Map<String, Object> condition) {
        return mapper.getSumEnergyByDay(condition);
    }

    @Override
    public int getCountWarningLoad(Map<String, Object> condition) {
        return mapper.getCountWarningLoad(condition);
    }

    @Override
    public Long getSumEnergyPV(Map<String, Object> condition) {
        return mapper.getSumEnergyPV(condition);
    }

    @Override
    public Long getSumEnergyByYearPV(Map<String, Object> condition) {
        return mapper.getSumEnergyByYearPV(condition);
    }

    @Override
    public Long getSumEnergyByMonthPV(Map<String, Object> condition) {
        return mapper.getSumEnergyByMonthPV(condition);
    }

    @Override
    public Long getSumEnergyByDayPV(Map<String, Object> condition) {
        return mapper.getSumEnergyByDayPV(condition);
    }

    @Override
    public int getCountWarningPV(Map<String, Object> condition) {
        return mapper.getCountWarningPV(condition);
    }

    @Override
    public Long getSumEnergyGrid(Map<String, Object> condition) {
        return mapper.getSumEnergyGrid(condition);
    }

    @Override
    public Long getSumEnergyByYearGrid(Map<String, Object> condition) {
        return mapper.getSumEnergyByYearGrid(condition);
    }

    @Override
    public Long getSumEnergyByMonthGrid(Map<String, Object> condition) {
        return mapper.getSumEnergyByMonthGrid(condition);
    }

    @Override
    public Long getSumEnergyByDayGrid(Map<String, Object> condition) {
        return mapper.getSumEnergyByDayGrid(condition);
    }

    @Override
    public int getCountWarningGrid(Map<String, Object> condition) {
        return mapper.getCountWarningGrid(condition);
    }

}
