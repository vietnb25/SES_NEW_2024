package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dto.DataWeather1;
import vn.ses.s3m.plus.pv.dao.DataWeatherMapper;

@Service
public class OperationWeatherPVServiceImpl implements OperationWeatherPVService {

    @Autowired
    private DataWeatherMapper dataWeatherMapper;

    /**
     * Lấy dữ liệu vận hành Weather tức thời PV
     *
     * @param condition Điều kiện truy vấn
     * @return Dữ liệu tức thời
     */
    @Override
    public DataWeather1 getInstantOperationWeatherPV(final Map<String, Object> condition) {
        return dataWeatherMapper.getInstantOperationWeatherPV(condition);
    }

    /**
     * Lấy danh sách dữ liệu vận hành Weather PV
     *
     * @param condition Điều kiện truy vấn
     * @return Danh sách dữ liệu tức thời
     */
    @Override
    public List<DataWeather1> getOperationWeatherPV(final Map<String, Object> condition) {
        return dataWeatherMapper.getOperationWeatherPV(condition);
    }

    /**
     * Lấy tổng số dữ liệu vận hành Weather
     *
     * @param condition Điều kiện truy vấn
     * @return Số lượng dữ liệu
     */
    @Override
    public Integer countDataOperationWeatherPV(final Map<String, Object> condition) {
        return dataWeatherMapper.countDataOperationWeatherPV(condition);
    }

    @Override
    public DataWeather1 getInstantOperationWeatherInProjectId(Map<String, Object> condition) {
        return dataWeatherMapper.getInstantOperationWeatherInProjectId(condition);
    }

}
