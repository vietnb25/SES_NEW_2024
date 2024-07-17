package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.DataInverterMapper;
import vn.ses.s3m.plus.dto.DataInverter1;

@Service
public class OperationPVServiceImpl implements OperationPVService {

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

}
