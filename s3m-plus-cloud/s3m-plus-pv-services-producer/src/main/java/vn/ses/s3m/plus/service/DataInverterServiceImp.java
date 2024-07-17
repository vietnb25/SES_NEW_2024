package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.DataInverterMapper;
import vn.ses.s3m.plus.dto.DataInverter;

@Service
public class DataInverterServiceImp implements DataInverterService {
    @Autowired
    private DataInverterMapper dataInverterMapper;

    /**
     * Lấy thông tin bản tin cảnh báo.
     *
     * @param condition Điều kiện lấy cảnh báo (cảnh báo theo ngày, theo projectId, ...).
     * @return Danh sách bản tin cảnh báo theo điều kiện.
     */
    @Override
    public List<DataInverter> getDataInverterByDevice(final Map<String, Object> condition) {
        return dataInverterMapper.getDataInverterByDevice(condition);
    }
}
