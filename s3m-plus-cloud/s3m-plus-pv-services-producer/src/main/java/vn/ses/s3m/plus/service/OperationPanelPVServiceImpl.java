package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.DataPanelMapper;
import vn.ses.s3m.plus.dto.DataPanel1;

@Service
public class OperationPanelPVServiceImpl implements OperationPanelPVService {

    @Autowired
    private DataPanelMapper dataPanelMapper;

    /**
     * Lấy dữ liệu vận hành Panel tức thời PV
     *
     * @param condition Điều kiện truy vấn
     * @return Dữ liệu tức thời
     */
    @Override
    public DataPanel1 getInstantOperationPanelPV(final Map<String, Object> condition) {
        return dataPanelMapper.getInstantOperationPanelPV(condition);
    }

    /**
     * Lấy danh sách dữ liệu vận hành Panel PV
     *
     * @param condition Điều kiện truy vấn
     * @return Danh sách dữ liệu tức thời
     */
    @Override
    public List<DataPanel1> getOperationPanelPV(final Map<String, Object> condition) {
        return dataPanelMapper.getOperationPanelPV(condition);
    }

    /**
     * Lấy tổng số dữ liệu vận hành Panel
     *
     * @param condition Điều kiện truy vấn
     * @return Số lượng dữ liệu
     */
    @Override
    public Integer countDataOperationPanelPV(final Map<String, Object> condition) {
        return dataPanelMapper.countDataOperationPanelPV(condition);
    }

}
