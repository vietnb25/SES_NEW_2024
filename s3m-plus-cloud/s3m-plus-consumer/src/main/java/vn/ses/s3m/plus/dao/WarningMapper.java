package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.Warning;

@Mapper
public interface WarningMapper {
    Integer countProjectWarning(Map<String, String> condition);

    List<Warning> getWarningByProject(Map<String, Object> condition);

    List<Warning> getWarningsByCustomerId(Map<String, Object> condition);

    List<Warning> getCountListWarning(Map<String, Object> condition);

    List<Warning> getWarningInstance(Map<String, String> condition);

    List<Warning> getTotalWarning(Map<String, Object> condition);

    Integer getAllDeviceHasWarning(Map<String, Object> condition);

    List<Device> getWarnedDevice(Map<String, Object> condition);

    Warning getListWarning(Map<String, String> condition);
}
