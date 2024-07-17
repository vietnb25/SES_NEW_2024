package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.SettingMapper;
import vn.ses.s3m.plus.dto.Setting;

@Service
public class SettingServiceImp implements SettingService {
    @Autowired
    private SettingMapper settingMapper;

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
    public List<Setting> getSettings(final Map<String, Object> condition) {
        return settingMapper.getSettings(condition);
    }

}
