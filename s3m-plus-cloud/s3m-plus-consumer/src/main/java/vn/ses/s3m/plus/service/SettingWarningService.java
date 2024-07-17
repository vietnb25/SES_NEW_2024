package vn.ses.s3m.plus.service;

import vn.ses.s3m.plus.dto.SettingWarning;

import java.util.List;
import java.util.Map;

public interface SettingWarningService {
    List<SettingWarning> getSettingWarningByDeviceType(Map<String, Object> condition);

    void updateSettingValue(Map<String, String> condition);

    SettingWarning getSettingByDeviceAndWarningType(Map<String, String> condition);
}
