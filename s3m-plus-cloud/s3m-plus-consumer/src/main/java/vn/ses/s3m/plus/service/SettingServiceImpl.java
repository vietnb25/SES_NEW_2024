package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.SettingMapper;
import vn.ses.s3m.plus.dto.Setting;

/**
 * Xử lý lấy thông tin cài đặt từ database author Arius Vietnam JSC
 *
 * @since 2022-01-01
 */
@Service
public class SettingServiceImpl implements SettingService {

    @Autowired
    private SettingMapper settingMapper;

    /**
     * Lấy ra danh sách cài đặt.
     *
     * @return danh sách cài đặt.
     */
    @Override
    public List<Setting> getSettings(final Map<String, String> condition) {
        return settingMapper.getSettings(condition);
    }

    @Override
    public List<Setting> getSettingHistory(Map<String, Object> condition) {
        return settingMapper.getSettingHistory(condition);
    }

    @Override
    public List <Setting> getSettingValueHistory(Map<String, Object> condition) {
        return settingMapper.getSettingValueHistory(condition);
    }

    /**
     * Lấy ra danh sách cài đặt theo Id.
     *
     * @param settingId Id cài đặt.
     * @return Trả về thông tin cài đặt theo Id.
     */
    @Override
    public Setting getSetting(final Integer settingId) {
        return settingMapper.getSetting(settingId);
    }

    /**
     * Chỉnh sửa thông tin cài đặt.
     *
     * @param condition Điều kiện truyền vào để chỉnh sửa.
     * @return Trả về kết quả sau khi chỉnh sửa.
     */
    @Override
    public void updateSetting(final Map<String, String> condition) {
        settingMapper.updateSetting(condition);
    }

    /**
     * Lấy giá trị cài đặt cảnh báo.
     *
     * @param condition Điều kiện.
     * @return Giá trị cảnh báo.
     */
    @Override
    public String getSettingValue(final Map<String, Object> condition) {
        return settingMapper.getSettingValue(condition);
    }

    /**
     * Lấy danh sách cảnh báo
     *
     * @param condition Điều kiện.
     * @return Danh sách.
     */
    @Override
    public List<Setting> getSettingValues(final Map<String, Object> condition) {
        return settingMapper.getSettingValues(condition);
    }

    @Override
    public Setting getSettingByDevice(final Map<String, Object> condition) {
        return settingMapper.getSettingByDevice(condition);
    }

    @Override
    public Setting getSettingByDeviceId(final Map<String, Object> condition) {
        return settingMapper.getSettingByDeviceId(condition);
    }

    @Override
    public List<Setting> getSettingByDeviceIds(Map<String, String> condition) {
        return this.settingMapper.getSettingByDeviceIds(condition);
    }
}
