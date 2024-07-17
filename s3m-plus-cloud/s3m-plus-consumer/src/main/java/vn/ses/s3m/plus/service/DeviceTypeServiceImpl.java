package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.DeviceTypeMapper;
import vn.ses.s3m.plus.dto.DeviceType;
import vn.ses.s3m.plus.dto.DeviceTypeMst;

@Service
public class DeviceTypeServiceImpl implements DeviceTypeService {

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;

    /**
     * Lấy danh sách loại thiết bị
     */
    @Override
    public List<DeviceTypeMst> getDeviceTypes() {
        return deviceTypeMapper.getDeviceTypes();
    }

    /**
     * Lấy danh loại thiết bị theo id hệ thống
     */
    @Override
    public List<DeviceType> getDeviceTypesBySystemTypeId(Map<String, String> condition) {
        return deviceTypeMapper.getDeviceTypesBySystemTypeId(condition);
    }

}
