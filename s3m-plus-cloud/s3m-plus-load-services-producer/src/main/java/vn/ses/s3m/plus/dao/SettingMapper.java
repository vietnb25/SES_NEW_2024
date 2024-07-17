package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Setting;

@Mapper
public interface SettingMapper {
    String getSettingValue(Map<String, Object> condition);

    List<Setting> getSettings(Map<String, Object> condition);
}
