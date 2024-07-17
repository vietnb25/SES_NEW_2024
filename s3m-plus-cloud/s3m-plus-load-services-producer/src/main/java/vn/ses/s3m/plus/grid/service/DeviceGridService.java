package vn.ses.s3m.plus.grid.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Device;

public interface DeviceGridService {

    List<Device> getDeviceByProjectId(Map<String, Object> condition);
}
