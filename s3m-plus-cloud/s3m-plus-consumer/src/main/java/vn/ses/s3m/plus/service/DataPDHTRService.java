package vn.ses.s3m.plus.service;

import vn.ses.s3m.plus.dto.DataPDHTR02;

import java.util.List;
import java.util.Map;

public interface DataPDHTRService {
    List<DataPDHTR02> getListHTRIndicatorByDeviceId(Map<String, Object> con);
    DataPDHTR02 getInforDeviceByWarningHTR02(Map<String, Object> con);



}
