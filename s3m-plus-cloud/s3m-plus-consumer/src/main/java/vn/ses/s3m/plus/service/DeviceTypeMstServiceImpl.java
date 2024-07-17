package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.DeviceTypeMapper;
import vn.ses.s3m.plus.dto.DeviceTypeMst;

@Service
public class DeviceTypeMstServiceImpl implements DeviceTypeMstService {

    @Autowired
    private DeviceTypeMapper mapper;

    @Override
    public List<DeviceTypeMst> getDeviceTypesBySystemTypeAndCustomerAndProject(Map<String, String> condition) {
        return this.mapper.getDeviceTypesBySystemTypeAndCustomerAndProject(condition);
    }

    @Override
    public List<DeviceTypeMst> getDeviceTypes() {
        return mapper.getDeviceTypes();
    }

    @Override
    public List<DeviceTypeMst> searchDeviceType(Map<String, String> condtion) {
        return mapper.searchDeviceType(condtion);
    }

    @Override
    public void deleteDeviceType(Integer id) {
        mapper.deleteDeviceType(id);
    }

    @Override
    public void add(DeviceTypeMst deviceType) {
        mapper.add(deviceType);
    }

    @Override
    public void update(DeviceTypeMst deviceType) {
        mapper.update(deviceType);
    }

    @Override
    public DeviceTypeMst getDeviceTypeByName(Map<String, String> condtion) {
        return mapper.getDeviceTypeByName(condtion);
    }

    @Override
    public DeviceTypeMst getDeviceTypeById(Map<String, Object> condtion) {
        return mapper.getDeviceTypeById(condtion);
    }

}
