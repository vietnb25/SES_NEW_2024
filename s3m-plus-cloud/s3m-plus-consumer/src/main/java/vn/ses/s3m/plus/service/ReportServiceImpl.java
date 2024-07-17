package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.ReportMapper;
import vn.ses.s3m.plus.dto.DataPqs;
import vn.ses.s3m.plus.dto.SettingShiftEp;

/**
 * Xử lý lấy thông tin cài đặt từ database author Arius Vietnam JSC
 *
 * @since 2022-01-01
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    /**
     * Lấy ra danh sách năng lượng tổng theo thời gian.
     *
     * @return danh sách pqs.
     */
    @Override
    public List<DataPqs> getEnergyTotal(final Map<String, Object> condition) {
        return reportMapper.getEnergyTotal(condition);
    }

    /**
     * Lấy ra tiền điện tổng.
     *
     * @return pqs tiền điện tổng.
     */
    @Override
    public List<DataPqs> getCostTotal(final Map<String, Object> condition) {
        return reportMapper.getCostTotal(condition);
    }

    /**
     * Lấy ra danh sách năng lượng tổng theo ca làm việc trong ngày.
     *
     * @return danh sách pqs.
     */
    @Override
    public List<DataPqs> getEnergyTotalByShift(final Map<String, Object> condition) {
        return reportMapper.getEnergyTotalByShift(condition);
    }

    @Override
    public List<DataPqs> getStrengthTotal(final Map<String, Object> condition) {
        return reportMapper.getStrengthTotal(condition);
    }

    @Override
    public List<DataPqs> getUseEnergyCompare(final Map<String, Object> condition) {
        return reportMapper.getUseEnergyCompare(condition);
    }

    @Override
    public List<DataPqs> getWarningTotal(Map<String, Object> condition) {
        return reportMapper.getWarningTotal(condition);
    }

    @Override
    public List<DataPqs> getPTotalByDeviceId(Map<String, Object> condition) {
        return reportMapper.getPTotalByDeviceId(condition);
    }

    @Override
    public List<DataPqs> getListPTotalByDeviceIds(Map<String, Object> condition) {
        return reportMapper.getListPTotalByDeviceIds(condition);
    }

    @Override
    public List<DataPqs> getMaxPTotalByDeviceIds(Map<String, Object> condition) {
        return reportMapper.getMaxPTotalByDeviceIds(condition);
    }

    @Override
    public List<DataPqs> getMinPTotalByDeviceIds(Map<String, Object> condition) {
        return reportMapper.getMinPTotalByDeviceIds(condition);
    }

    @Override
    public DataPqs getCostForCycle(Map<String, Object> condition) {
        return this.reportMapper.getCostForCycle(condition);
    }

    @Override
    public List<DataPqs> getDeviceByWarningType(String fromDate, String toDate, Integer warningType, Integer typeTime,String deviceId,String schema,Integer projectId, Integer systemTypeId) {
        return reportMapper.getDeviceByWarningType(fromDate, toDate, warningType, typeTime,deviceId,schema,projectId, systemTypeId);
    }

    @Override
    public SettingShiftEp getEpByShiftAndViewTime(Map<String, Object> condition) {
        return this.reportMapper.getEpByShiftAndViewTime(condition);
    }

    @Override
    public List<DataPqs> getComparingEnergyUsageByLoadType(Map<String, Object> condition) {
        return this.reportMapper.getComparingEnergyUsageByLoadType(condition);
    }

    @Override
    public List<DataPqs> getEpByDevicesAndViewTime(Map<String, Object> condition) {
        return this.reportMapper.getEpByDevicesAndViewTime(condition);
    }

}
