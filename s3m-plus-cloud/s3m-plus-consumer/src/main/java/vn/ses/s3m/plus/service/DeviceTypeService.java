package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DeviceType;
import vn.ses.s3m.plus.dto.DeviceTypeMst;

public interface DeviceTypeService {

    List<DeviceTypeMst> getDeviceTypes();

    List<DeviceType> getDeviceTypesBySystemTypeId(Map<String, String> condition);

}
