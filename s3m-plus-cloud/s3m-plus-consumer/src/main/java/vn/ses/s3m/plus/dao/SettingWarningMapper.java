package vn.ses.s3m.plus.dao;

import org.apache.ibatis.annotations.Mapper;
import vn.ses.s3m.plus.dto.SettingWarning;

import java.util.List;
import java.util.Map;

@Mapper
public interface SettingWarningMapper {
    List<SettingWarning> getSettingWarningByDeviceType(Map<String, Object> condition);
    void addHistorySetting(Map<String, String> condition);
    void updateSettingValue(Map<String, String> condition);
    SettingWarning getSettingByDeviceAndWarningType(Map<String, String> condition);
}
