package vn.ses.s3m.plus.batch.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.batch.dto.Setting;

@Mapper
public interface SettingMapper {

    Setting getSetting(Map<String, Object> condition);

    Setting getSettingProject(Map<String, Object> condition);

}
