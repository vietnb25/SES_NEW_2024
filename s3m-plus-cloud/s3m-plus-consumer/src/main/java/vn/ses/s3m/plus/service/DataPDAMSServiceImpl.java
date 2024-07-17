package vn.ses.s3m.plus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ses.s3m.plus.dao.DataPDAMSSMapper;
import vn.ses.s3m.plus.dto.DataPDAMS01;

import java.util.List;
import java.util.Map;

@Service
public class DataPDAMSServiceImpl implements DataPDAMSService{
    @Autowired
    private DataPDAMSSMapper mapper;

    @Override
    public List<DataPDAMS01> getListAMSIndicatorByDeviceId(Map<String, Object> con) {
        return this.mapper.getListAMSIndicatorByDeviceId(con);
    }

    @Override
    public List<DataPDAMS01> getInforDeviceByWarningAMS01(Map<String, Object> con) {
        return this.mapper.getInforDeviceByWarningAMS01(con);
    }
}
