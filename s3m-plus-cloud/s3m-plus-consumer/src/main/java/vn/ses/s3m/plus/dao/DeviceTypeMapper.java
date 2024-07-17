package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.DeviceType;
import vn.ses.s3m.plus.dto.DeviceTypeMst;

@Mapper
public interface DeviceTypeMapper {

    List<DeviceTypeMst> getDeviceTypes();

    List<DeviceType> getDeviceTypesBySystemTypeId(Map<String, String> condition);

    List<DeviceTypeMst> getDeviceTypesBySystemTypeAndCustomerAndProject(Map<String, String> condition);

    List<DeviceTypeMst> searchDeviceType(Map<String, String> condtion);

    void deleteDeviceType(Integer id);

    void add(DeviceTypeMst deviceType);

    void update(DeviceTypeMst deviceType);

    DeviceTypeMst getDeviceTypeById(Map<String, Object> condtion);

    DeviceTypeMst getDeviceTypeByName(Map<String, String> condtion);
}
