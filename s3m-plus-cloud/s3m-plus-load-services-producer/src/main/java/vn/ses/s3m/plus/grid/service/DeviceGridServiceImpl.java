package vn.ses.s3m.plus.grid.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.DeviceMapper;
import vn.ses.s3m.plus.dto.Device;

@Service
public class DeviceGridServiceImpl implements DeviceGridService {

    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    public List<Device> getDeviceByProjectId(Map<String, Object> condition) {
        return deviceMapper.getDevicePowerByProjectIdGrid(condition);
    }

}
