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
    private DataLoadFrame1Mapper dataLoadFrame1Mapper;

    /**
     * Lấy những bản tin bị cảnh báo.
     *
     * @param condition Điều kiện truy vấn theo ngày, thiết bị và dự án.
     * @return Danh sách các bản tin bị cảnh báo.
     */
    @Override
    public List<DataLoadFrame1> getDataLoadWarning(final Map<String, Object> condition) {
        return dataLoadFrame1Mapper.getDataLoadWarning(condition);
    }
}
