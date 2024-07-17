package vn.ses.s3m.plus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ses.s3m.plus.dao.DataLoadFrame2Mapper;
import vn.ses.s3m.plus.dto.DataLoadFrame2;

import java.util.List;
import java.util.Map;
@Service
public class DataLoadFrame2ServiceImpl implements DataLoadFrame2Service{
    @Autowired
    private DataLoadFrame2Mapper mapper;
    @Override
    public DataLoadFrame2 getInforDataFrame2LoadByTime(Map<String, Object> condition) {
        return mapper.getInforDataFrame2LoadByTime(condition);
    }

    @Override
    public List<DataLoadFrame2> getListWarnedDataFrame2(Map<String, Object> condition) {
        return mapper.getListWarnedDataFrame2(condition);
    }
}
