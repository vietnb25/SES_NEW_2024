package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.DataRmuDrawer1Mapper;
import vn.ses.s3m.plus.dto.DataRmuDrawer1;

@Service
public class DataRmuDrawer1ServiceImpl implements DataRmuDrawer1Service {

    @Autowired
    private DataRmuDrawer1Mapper dataRmuDrawer1Mapper;

    /**
     * Lấy ra công suất năng lượng và điện năng tổng của grid
     */
    @Override
    public List<DataRmuDrawer1> getTotalPowerGrid(final String schema, final String[] deviceIds, final String today) {
        return dataRmuDrawer1Mapper.getTotalPowerGrid(schema, deviceIds, today);
    }

    /**
     * Lấy ra điện năng tổng của grid
     */
    @Override
    public List<DataRmuDrawer1> getDataChartGrid(final Map<String, String> condition) {
        return dataRmuDrawer1Mapper.getDataChartGrid(condition);
    }

    /**
     * Lấy ra số lượng data của grid
     */
    @Override
    public Integer countCurrentData(final Map<String, String> condition) {
        return dataRmuDrawer1Mapper.countCurrentData(condition);
    }

    /**
     * Lấy ra tổng công suất tức thời của grid
     */
    @Override
    public DataRmuDrawer1 getTotalPowerByProjectId(final Map<String, String> condition) {
        return dataRmuDrawer1Mapper.getTotalPowerByProjectId(condition);
    }

    @Override
    public Float getMaxTotalPower(final Map<String, String> condition) {
        return dataRmuDrawer1Mapper.getMaxTotalPower(condition);
    }

    @Override
    public Float getAvgTotalPower(final Map<String, Object> condition) {
        return dataRmuDrawer1Mapper.getAvgTotalPower(condition);
    }

    @Override
    public Float getSumTotalPower(final Map<String, Object> condition) {
        return dataRmuDrawer1Mapper.getSumTotalPower(condition);
    }

    @Override
    public List<DataRmuDrawer1> getPowerDeviceByProjectId(Map<String, String> condition) {
        return dataRmuDrawer1Mapper.getPowerDeviceByProjectId(condition);
    }

    @Override
    public Long getSumEpInDay(Map<String, String> condition) {
        return dataRmuDrawer1Mapper.getSumEpInDay(condition);
    }

    @Override
    public Long getSumEpInMonth(Map<String, String> condition) {
        return dataRmuDrawer1Mapper.getSumEpInMonth(condition);
    }

    @Override
    public Long getSumEp(Map<String, String> condition) {
        return dataRmuDrawer1Mapper.getSumEp(condition);
    }

    @Override
    public Long getPtotalInDay(Map<String, String> condition) {
        return dataRmuDrawer1Mapper.getPtotalInDay(condition);
    }

    @Override
    public Float getAvgTotalPowerInDay(Map<String, String> condition) {
        return dataRmuDrawer1Mapper.getAvgTotalPowerInDay(condition);
    }

    @Override
    public Float getMinTotalPowerInDay(Map<String, String> condition) {
        return dataRmuDrawer1Mapper.getMinTotalPowerInDay(condition);
    }

    @Override
    public Float getMaxTotalPowerInDay(Map<String, String> condition) {
        return dataRmuDrawer1Mapper.getMaxTotalPowerInDay(condition);
    }

    // Warning: START
    @Override
    public List<DataRmuDrawer1> getWarningDataRMUByDeviceId(Map<String, Object> condition) {
        return dataRmuDrawer1Mapper.getWarningDataRMUByDeviceId(condition);
    }

    // Warning: END
    @Override
    public DataRmuDrawer1 getInforDataGridByTime(Map<String, Object> condition) {
        return dataRmuDrawer1Mapper.getInforDataGridByTime(condition);
    }

    @Override
    public List<DataRmuDrawer1> getListWarnedData(Map<String, Object> condition) {
        return dataRmuDrawer1Mapper.getListWarnedData(condition);
    }
}
