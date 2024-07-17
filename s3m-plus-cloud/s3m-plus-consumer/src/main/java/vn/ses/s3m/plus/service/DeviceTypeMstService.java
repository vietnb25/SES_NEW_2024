package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DeviceTypeMst;

public interface DeviceTypeMstService {
    List<DeviceTypeMst> getDeviceTypesBySystemTypeAndCustomerAndProject(Map<String, String> condition);

    List<DeviceTypeMst> getDeviceTypes();

    List<DeviceTypeMst> searchDeviceType(Map<String, String> condtion);

    void deleteDeviceType(Integer id);

    void update(DeviceTypeMst deviceType);

    void add(DeviceTypeMst deviceType);

    DeviceTypeMst getDeviceTypeByName(Map<String, String> condtion);

    DeviceTypeMst getDeviceTypeById(Map<String, Object> condtion);

}
