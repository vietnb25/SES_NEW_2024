package vn.ses.s3m.plus.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.InverterMapper;
import vn.ses.s3m.plus.dto.Inverter;

@Service
public class InverterServiceImp implements InverterService {

    @Autowired
    private InverterMapper inverterMapper;

    @Override
    public Inverter getDataInverterByDeviceId(Map<String, Object> condition) {
        return inverterMapper.getDataInverterByDeviceId(condition);
    }

}
