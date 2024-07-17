package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.DataLoadFrame1Mapper;
import vn.ses.s3m.plus.dto.DataLoadFrame1;

@Service
public class DataLoadFrame1ServiceImp implements DataLoadFrame1Service {

    @Autowired
    private DataLoadFrame1Mapper mapper;

    /**
     * Lấy danh sách dữ liệu từ bảng s3m_data_load_frame1 theo projectId.
     *
     * @param condition Điều kiện.
     * @return List<DataLoadFrame1> danh sách dữ liệu từ bảng data_load_frame1 theo projectId
     */
    @Override
    public DataLoadFrame1 getTotalPowerByProjectId(final Map<String, String> condition) {
        return mapper.getTotalPowerByProjectId(condition);
    }

    /**
     * Lây ra ngày của bản tin mới nhất
     */
    @Override
    public DataLoadFrame1 getDateNewDevice(final String schema) {
        return mapper.getDateNewDevice(schema);
    }

    /**
     * Lây ra data sản lượng điện năng.
     */
    @Override
    public List<DataLoadFrame1> getDataChartPower(final Map<String, String> condition) {
        return mapper.getDataChartPower(condition);
    }

    /**
     * Lấy ra tổng công suất Load
     */
    @Override
    public List<DataLoadFrame1> getTotalPower(final String schema, final String[] deviceIds, final String today) {
        return mapper.getTotalPower(schema, deviceIds, today);
    }

    @Override
    public Integer countCurrentData(final Map<String, String> condition) {
        return mapper.countCurrentData(condition);
    }

    @Override
    public Float getMaxTotalPower(final Map<String, String> condition) {
        return mapper.getMaxTotalPower(condition);
    }

    @Override
    public Float getMinTotalPower(final Map<String, String> condition) {
        return mapper.getMinTotalPower(condition);
    }

    @Override
    public Float getAvgTotalPower(final Map<String, String> condition) {
        return mapper.getAvgTotalPower(condition);
    }

    @Override
    public Float getSumTotalPower(final Map<String, Object> condition) {
        return mapper.getSumTotalPower(condition);
    }

    @Override
    public List<DataLoadFrame1> getPowerDeviceByProjectId(Map<String, String> condition) {
        return mapper.getPowerDeviceByProjectId(condition);
    }

    @Override
    public Long getSumEpInDay(Map<String, String> condition) {
        return mapper.getSumEpInDay(condition);
    }

    @Override
    public Long getSumEpInMonth(Map<String, String> condition) {
        return mapper.getSumEpInMonth(condition);
    }

    @Override
    public Long getSumEp(Map<String, String> condition) {
        return mapper.getSumEp(condition);
    }

    @Override
    public Float getPtotalInDay(Map<String, String> condition) {
        return mapper.getPtotalInDay(condition);
    }

    @Override
    public Float getMaxTotalPowerInDay(Map<String, String> condition) {
        return mapper.getMaxTotalPowerInDay(condition);
    }

    @Override
    public Float getMinTotalPowerInDay(Map<String, String> condition) {
        return mapper.getMinTotalPowerInDay(condition);
    }

    @Override
    public Float getAvgTotalPowerInDay(Map<String, String> condition) {
        return mapper.getAvgTotalPowerInDay(condition);
    }

    @Override
    public List<DataLoadFrame1> getWarningDataLoadByDeviceId(Map<String, Object> condition) {
        return mapper.getWarningDataLoadByDeviceId(condition);
    }

    @Override
    public DataLoadFrame1 getInforDataLoadByTime(Map<String, Object> condition) {
        return mapper.getInforDataLoadByTime(condition);
    }

    @Override
    public List<DataLoadFrame1> getListWarnedData(Map<String, Object> condition) {
        return mapper.getListWarnedData(condition);
    }

}
