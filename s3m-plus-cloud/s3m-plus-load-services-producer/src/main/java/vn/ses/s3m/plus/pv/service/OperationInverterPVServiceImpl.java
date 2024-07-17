package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dto.DataInverter1;
import vn.ses.s3m.plus.pv.dao.DataInverterMapper;

@Service
public class OperationInverterPVServiceImpl implements OperationInverterPVService {

    @Autowired
    private DataInverterMapper dataInverterMapper;

    /**
     * Lấy dữ liệu vận hành Inverter tức thời PV
     *
     * @param condition Điều kiện truy vấn
     * @return Dữ liệu tức thời
     */
    @Override
    public DataInverter1 getInstantOperationInverterPV(final Map<String, Object> condition) {
        return dataInverterMapper.getInstantOperationInverterPV(condition);
    }

    /**
     * Lấy danh sách dữ liệu vận hành Inverter PV
     *
     * @param condition Điều kiện truy vấn
     * @return Danh sách dữ liệu tức thời
     */
    @Override
    public List<DataInverter1> getOperationInverterPV(final Map<String, Object> condition) {
        return dataInverterMapper.getOperationInverterPV(condition);
    }

    /**
     * Lấy tổng số dữ liệu vận hành Inverter
     *
     * @param condition Điều kiện truy vấn
     * @return Số lượng dữ liệu
     */
    @Override
    public Integer countDataOperationInverterPV(final Map<String, Object> condition) {
        return dataInverterMapper.countDataOperationInverterPV(condition);
    }

    /**
     * Lấy danh sách dữ liệu vận hành Inverter PV mới nhất
     *
     * @param condition Điều kiện truy vấn
     * @return Danh sách dữ liệu tức thời
     */
    @Override
    public DataInverter1 getDataInverterByDeviceIdInFifMinute(Map<String, Object> condition) {
        return dataInverterMapper.getDataInverterByDeviceIdInFifMinute(condition);
    }

    /**
     * Lấy danh sách dữ liệu Inverter theo thiết bị
     *
     * @param condition Điều kiện truy vấn
     * @return Danh sách dữ liệu
     */
    @Override
    public List<DataInverter1> getInverterEveryYearByDeviceId(final Map<String, Object> condition) {
        return dataInverterMapper.getInverterEveryYearByDeviceId(condition);
    }

    /**
     * Lấy dữ liệu điện năng Inverter PV
     *
     * @param condition Điều kiện truy vấn
     * @return Danh sách dữ liệu
     */
    @Override
    public List<DataInverter1> getDataPQSByMonthInverter(final Map<String, Object> condition) {
        return dataInverterMapper.getDataPQSByMonthInverter(condition);
    }

    /**
     * Lấy thông số cài đặt thiết bị
     *
     * @param condition Điều kiện truy vấn
     * @return Dữ liệu thông số cài đặt
     */
    @Override
    public DataInverter1 getOperationSettingInverter(final Map<String, Object> condition) {
        return dataInverterMapper.getOperationSettingInverter(condition);
    }

    @Override
    public List<DataInverter1> getInverterInDayByDeviceId(Map<String, Object> condition) {
        return dataInverterMapper.getInverterInDayByDeviceId(condition);
    }

    @Override
    public List<DataInverter1> getInverterInMonthByDeviceId(Map<String, Object> condition) {
        return dataInverterMapper.getInverterInMonthByDeviceId(condition);
    }

    @Override
    public List<DataInverter1> getInverterInYearByDeviceId(Map<String, Object> condition) {
        return dataInverterMapper.getInverterInYearByDeviceId(condition);
    }

    @Override
    public List<DataInverter1> getInverterInPrevDayByDeviceId(Map<String, Object> condition) {
        return dataInverterMapper.getInverterInPrevDayByDeviceId(condition);
    }

    @Override
    public List<DataInverter1> getInverterInPrevMonthByDeviceId(Map<String, Object> condition) {
        return dataInverterMapper.getInverterInPrevMonthByDeviceId(condition);
    }

    @Override
    public List<DataInverter1> getInverterInPrevYearByDeviceId(Map<String, Object> condition) {
        return dataInverterMapper.getInverterInPrevYearByDeviceId(condition);
    }

}
