package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.DataInverterMapper;
import vn.ses.s3m.plus.dto.DataInverter1;
import vn.ses.s3m.plus.dto.DataLoadFrame1;
import vn.ses.s3m.plus.dto.Device;

@Service
public class DataInverterServiceImpl implements DataInverterService {

    @Autowired
    private DataInverterMapper dataInverterMapper;

    /**
     * Lấy ra sản lượng điện năng của PV
     */
    @Override
    public List<DataInverter1> getDataChartPV(final Map<String, String> condition) {
        return dataInverterMapper.getDataChartPV(condition);
    }

    /**
     * Lấy ra tổng công suất PV
     */
    @Override
    public List<DataInverter1> getTotalPowerPV(final String schema, final String[] deviceIds, final String today) {
        return dataInverterMapper.getTotalPowerPV(schema, deviceIds, today);
    }

    /**
     * Lấy ra tổng công suất Pv theo mã dự án
     */
    @Override
    public Device getTotalPowerByProjectId(final Map<String, String> condition) {
        return dataInverterMapper.getTotalPowerByProjectId(condition);
    }

    @Override
    public int countCurrentData(final Map<String, String> condition) {
        return dataInverterMapper.countCurrentData(condition);
    }

    @Override
    public Float getMaxTotalPower(final Map<String, String> condition) {
        return dataInverterMapper.getMaxTotalPower(condition);
    }

    @Override
    public Float getMinTotalPower(final Map<String, String> condition) {
        return dataInverterMapper.getMinTotalPower(condition);
    }

    @Override
    public Float getAvgTotalPower(final Map<String, String> condition) {
        return dataInverterMapper.getAvgTotalPower(condition);
    }

    @Override
    public Float getSumTotalPower(final Map<String, Object> condition) {
        return dataInverterMapper.getSumTotalPower(condition);
    }

    @Override
    public List<DataInverter1> getPowerDeviceByProjectId(Map<String, String> condition) {
        return dataInverterMapper.getPowerDeviceByProjectId(condition);
    }

    @Override
    public Long getSumEpInDay(Map<String, String> condition) {
        return dataInverterMapper.getSumEpInDay(condition);
    }

    @Override
    public Long getSumEpInMonth(Map<String, String> condition) {
        return dataInverterMapper.getSumEpInMonth(condition);
    }

    @Override
    public Long getSumEp(Map<String, String> condition) {
        return dataInverterMapper.getSumEp(condition);
    }

    @Override
    public Float getPtotalInDay(Map<String, String> condition) {
        return dataInverterMapper.getPtotalInDay(condition);
    }

    @Override
    public Float getMaxTotalPowerInDay(Map<String, String> condition) {
        return dataInverterMapper.getMaxTotalPowerInDay(condition);
    }

    @Override
    public Float getMinTotalPowerInDay(Map<String, String> condition) {
        return dataInverterMapper.getMinTotalPowerInDay(condition);
    }

    @Override
    public Float getAvgTotalPowerInDay(Map<String, String> condition) {
        return dataInverterMapper.getAvgTotalPowerInDay(condition);
    }

    // Warning: START
    @Override
    public List<DataInverter1> getWarningDataInverterByDeviceId(Map<String, Object> condition) {
        return dataInverterMapper.getWarningDataInverterByDeviceId(condition);
    }
    // Warning: END
    
    @Override
    public DataInverter1 getInforDataInverterByTime(Map<String, Object> condition) {
        return dataInverterMapper.getInforDataInverterByTime(condition);
    }

    @Override
    public List<DataInverter1> getListWarnedData(Map<String, Object> condition) {
        return dataInverterMapper.getListWarnedData(condition);
    }

}
