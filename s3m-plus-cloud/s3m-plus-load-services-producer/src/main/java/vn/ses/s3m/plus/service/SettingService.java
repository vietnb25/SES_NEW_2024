package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Setting;

public interface SettingService {
    String getSettingValue(Map<String, Object> condition);

    List<Setting> getSettings(Map<String, Object> condition);
}
