package vn.ses.s3m.plus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ses.s3m.plus.dao.SettingWarningMapper;
import vn.ses.s3m.plus.dto.SettingWarning;

import java.util.List;
import java.util.Map;

@Service
public class SettingWarningServiceImpl implements SettingWarningService{
    @Autowired
    private SettingWarningMapper mapper;
    @Override
    public List<SettingWarning> getSettingWarningByDeviceType(Map<String, Object> condition) {
        return this.mapper.getSettingWarningByDeviceType(condition);
    }

    @Override
    public void updateSettingValue(Map<String, String> condition) {
        this.mapper.addHistorySetting(condition);
        this.mapper.updateSettingValue(condition);
    }

    @Override
    public SettingWarning getSettingByDeviceAndWarningType(Map<String, String> condition) {
        return this.mapper.getSettingByDeviceAndWarningType(condition);
    }
}
