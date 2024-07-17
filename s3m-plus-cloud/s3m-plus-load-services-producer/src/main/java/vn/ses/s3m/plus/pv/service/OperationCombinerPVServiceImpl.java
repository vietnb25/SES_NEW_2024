package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dto.DataCombiner1;
import vn.ses.s3m.plus.pv.dao.DataCombinerMapper;

@Service
public class OperationCombinerPVServiceImpl implements OperationCombinerPVService {

    @Autowired
    private DataCombinerMapper dataCombinerMapper;

    /**
     * Lấy dữ liệu vận hành Combiner tức thời PV
     *
     * @param condition Điều kiện truy vấn
     * @return Dữ liệu tức thời
     */
    @Override
    public DataCombiner1 getInstantOperationCombinerPV(final Map<String, Object> condition) {
        return dataCombinerMapper.getInstantOperationCombinerPV(condition);
    }

    /**
     * Lấy danh sách dữ liệu vận hành Combiner PV
     *
     * @param condition Điều kiện truy vấn
     * @return Danh sách dữ liệu tức thời
     */
    @Override
    public List<DataCombiner1> getOperationCombinerPV(final Map<String, Object> condition) {
        return dataCombinerMapper.getOperationCombinerPV(condition);
    }

    /**
     * Lấy tổng số dữ liệu vận hành Combiner
     *
     * @param condition Điều kiện truy vấn
     * @return Số lượng dữ liệu
     */
    @Override
    public Integer countDataOperationCombinerPV(final Map<String, Object> condition) {
        return dataCombinerMapper.countDataOperationCombinerPV(condition);
    }

    @Override
    public DataCombiner1 getDataCombinerByDeviceIdInFifMinute(Map<String, Object> condition) {
        return dataCombinerMapper.getDataCombinerByDeviceIdInFifMinute(condition);
    }

    /**
     * Lấy danh sách dữ liệu vận hành Combiner PV
     *
     * @param condition Điều kiện truy vấn
     * @return Danh sách dữ liệu tức thời
     */
    @Override
    public List<DataCombiner1> getDataCombinerPV(final Map<String, Object> condition) {
        return dataCombinerMapper.getDataCombinerPV(condition);
    }

}
