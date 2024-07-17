package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Setting;

public interface SettingService {

    void updateSetting(Map<String, String> condition);

    Setting getSetting(Integer settingId);

    List<Setting> getSettings(Map<String, String> condition);

    List<Setting> getSettingHistory(Map<String, Object> condition);

    List <Setting> getSettingValueHistory(Map<String, Object> condition);

    String getSettingValue(Map<String, Object> condition);

    List<Setting> getSettingValues(Map<String, Object> condition);

    Setting getSettingByDevice(Map<String, Object> condition);

    Setting getSettingByDeviceId(Map<String, Object> condition);
    List<Setting> getSettingByDeviceIds(Map<String, String> condition);

}
