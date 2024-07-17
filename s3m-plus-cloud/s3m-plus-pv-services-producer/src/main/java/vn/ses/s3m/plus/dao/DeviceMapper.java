package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Device;

@Mapper
public interface DeviceMapper {

    List<Device> getDevices(Map<String, Object> condition);

    Device getDeviceByDeviceId(Map<String, Object> map);

    List<Device> getDevicePowerByProjectId(Map<String, Object> map);

    List<Device> getDevicesByProjectIdAndSystemTypeId(Map<String, Object> map);

}
