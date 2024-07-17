package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Warning;

@Mapper
public interface WarningMapper {

    List<Warning> getWarnings(Map<String, Object> condition);

    List<Warning> getTotalWarningPV(Map<String, Object> condition);

    Integer getAllDeviceHasWarningPV(Map<String, Object> condition);

    List<Warning> getDetailWarningType(Map<String, Object> condition);

    List<Warning> countWarnings(Map<String, Object> condition);

    Integer countWarningByWarningType(Map<String, Object> condition);

}
