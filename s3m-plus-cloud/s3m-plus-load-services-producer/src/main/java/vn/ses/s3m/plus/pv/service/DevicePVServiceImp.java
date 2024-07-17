package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.DeviceMapper;
import vn.ses.s3m.plus.dto.Device;

@Service
public class DevicePVServiceImp implements DevicePVService {

    @Autowired
    private DeviceMapper deviceMapper;

    /**
     * Lấy thiết bị theo deviceId
     *
     * @param map Điều kiện truy vấn
     * @return Đối tượng thiết bị
     */
    @Override
    public Device getDeviceByDeviceId(final Map<String, Object> map) {
        return deviceMapper.getDeviceByDeviceIdPV(map);
    }

    /**
     * Lấy danh sách thiết bị theo dự án
     *
     * @param map Điều kiện truy vấn
     * @return Danh sách thiết bị
     */
    @Override
    public List<Device> getDeviceByProjectId(final Map<String, Object> condition) {
        return deviceMapper.getDevicePowerByProjectIdPV(condition);
    }

    /**
     * Lấy danh sách thiết bị theo dự án và hệ thống
     *
     * @param map Điều kiện truy vấn
     * @return Danh sách thiết bị
     */
    @Override
    public List<Device> getDevicesByProjectIdAndSystemTypeId(final Map<String, Object> map) {
        return deviceMapper.getDevicesByProjectIdAndSystemTypeIdPV(map);
    }

}
