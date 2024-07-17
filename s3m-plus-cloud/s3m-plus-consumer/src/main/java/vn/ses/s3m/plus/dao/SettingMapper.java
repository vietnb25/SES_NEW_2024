package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Setting;

@Mapper
public interface SettingMapper {

    List<Setting> getSettings(Map<String, String> condition);

    List<Setting> getSettingHistory(Map<String, Object> condition);

    List <Setting> getSettingValueHistory(Map<String, Object> condition);

    Setting getSetting(Integer settingId);

    void updateSetting(Map<String, String> condition);

    String getSettingValue(Map<String, Object> condition);

    List<Setting> getSettingValues(Map<String, Object> condition);

    Setting getSettingByDevice(Map<String, Object> condition);

    Setting getSettingByDeviceId(Map<String, Object> condition);
    List<Setting> getSettingByDeviceIds(Map<String, String> condition);
}
