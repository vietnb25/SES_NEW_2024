package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.DataLoadFrame1Mapper;
import vn.ses.s3m.plus.dao.DataTempHumidityMapper;
import vn.ses.s3m.plus.dto.DataLoadFrame1;
import vn.ses.s3m.plus.dto.DataTempHumidity;

@Service
public class DataTempHumidityServiceImpl implements DataTempHumidityService {

    @Autowired
    private DataTempHumidityMapper mapper;

    @Override
    public DataTempHumidity getInforDataTempHumidityByTime(Map<String, Object> condition) {
        return mapper.getInforDataTempHumidityByTime(condition);
    }

    @Override
    public List<DataTempHumidity> getListWarnedData(Map<String, Object> condition) {
        return mapper.getListWarnedData(condition);
    }

}
