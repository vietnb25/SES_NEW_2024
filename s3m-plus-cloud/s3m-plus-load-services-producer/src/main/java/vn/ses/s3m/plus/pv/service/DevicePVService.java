package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Device;

public interface DevicePVService {

    Device getDeviceByDeviceId(Map<String, Object> map);

    List<Device> getDeviceByProjectId(Map<String, Object> condition);

    List<Device> getDevicesByProjectIdAndSystemTypeId(Map<String, Object> map);

}
