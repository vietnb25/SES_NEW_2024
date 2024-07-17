package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Warning;

public interface WarningService {
    List<Warning> getWarnings(Map<String, Object> condition);

    List<String> getListDeviceId(Map<String, Object> condition);

    List<Warning> getTotalWarning(Map<String, Object> condition);

    List<Warning> getDetailWarningByWarningType(Map<String, Object> condition);

    List<Warning> getWarningList(Map<String, Object> condition);

    List<Warning> getWarningCaches(Map<String, Object> condition);

    Warning getStatusWarningDescription(Map<String, Object> condition);

    List<Warning> getWarningByDevice(Map<String, Object> condition);

    List<Warning> countWarnings(Map<String, Object> condition);

    Warning getDetailWarningCache(Map<String, Object> condition);

    boolean updateWarningCache(Map<String, Object> condition);

    Integer getAllDeviceHasWarning(Map<String, Object> condition);

    Integer countWarningByWarningType(Map<String, Object> condition);
}
