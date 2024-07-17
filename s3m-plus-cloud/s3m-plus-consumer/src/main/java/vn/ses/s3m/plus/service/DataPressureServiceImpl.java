package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.DataFlowMapper;
import vn.ses.s3m.plus.dao.DataLoadFrame1Mapper;
import vn.ses.s3m.plus.dao.DataPressureMapper;
import vn.ses.s3m.plus.dao.DataTempHumidityMapper;
import vn.ses.s3m.plus.dto.DataFlow;
import vn.ses.s3m.plus.dto.DataLoadFrame1;
import vn.ses.s3m.plus.dto.DataPressure;
import vn.ses.s3m.plus.dto.DataTempHumidity;

@Service
public class DataPressureServiceImpl implements DataPressureService {

    @Autowired
    private DataPressureMapper mapper;

    @Override
    public DataPressure getInforDataPressureByTime(Map<String, Object> condition) {
        return mapper.getInforDataPressureByTime(condition);
    }

    @Override
    public List<DataPressure> getListWarnedData(Map<String, Object> condition) {
        return mapper.getListWarnedData(condition);
    }

}
