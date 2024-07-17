package vn.ses.s3m.plus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ses.s3m.plus.dao.DataPDHTRMapper;
import vn.ses.s3m.plus.dto.DataPDHTR02;

import java.util.List;
import java.util.Map;

@Service
public class DataPDHTRServiceImpl implements DataPDHTRService{

    @Autowired
    private DataPDHTRMapper mapper;

    @Override
    public List<DataPDHTR02> getListHTRIndicatorByDeviceId(Map<String, Object> con) {
        return this.mapper.getListHTRIndicatorByDeviceId(con);
    }

    @Override
    public DataPDHTR02 getInforDeviceByWarningHTR02(Map<String, Object> con) {
        return this.mapper.getInforDeviceByWarningHTR02(con);
    }
}
