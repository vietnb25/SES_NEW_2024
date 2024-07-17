package vn.ses.s3m.plus.service;

import vn.ses.s3m.plus.dto.DataPDAMS01;

import java.util.List;
import java.util.Map;

public interface DataPDAMSService {
    List<DataPDAMS01> getListAMSIndicatorByDeviceId(Map<String, Object> con);
    List<DataPDAMS01> getInforDeviceByWarningAMS01(Map<String, Object> con);
}
