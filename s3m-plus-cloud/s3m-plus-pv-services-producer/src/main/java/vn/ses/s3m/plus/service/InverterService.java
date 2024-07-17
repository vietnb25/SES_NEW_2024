package vn.ses.s3m.plus.service;

import java.util.Map;

import vn.ses.s3m.plus.dto.Inverter;

public interface InverterService {

    public Inverter getDataInverterByDeviceId(Map<String, Object> condition);
}
