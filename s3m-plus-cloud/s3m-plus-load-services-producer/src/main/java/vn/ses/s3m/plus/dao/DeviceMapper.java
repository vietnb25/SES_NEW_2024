package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Device;

@Mapper
public interface DeviceMapper {
    Device getDeviceById(Map<String, Object> map);

    List<Device> getDevicePowerByProjectId(Map<String, Object> map);

    List<Device> getDevicesByProjectIdAndSystemTypeId(Map<String, Object> map);

    List<Device> getDevices(Map<String, Object> condition);

    List<Device> getDevicesControl(Map<String, Object> condition);

    Device getDeviceByDeviceIdPV(Map<String, Object> map);

    List<Device> getDevicePowerByProjectIdPV(Map<String, Object> map);

    List<Device> getDevicesByProjectIdAndSystemTypeIdPV(Map<String, Object> map);

    List<Device> getDevicePowerByProjectIdGrid(Map<String, Object> map);

}
