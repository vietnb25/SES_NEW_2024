package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Warning;

public interface WarningService {
    Integer countProjectWarning(Map<String, String> condition);

    List<Warning> getWarningByProject(Map<String, Object> condition);

    List<Warning> getWarningsByCustomerId(Map<String, Object> condition);

    List<Warning> getCountListWarning(Map<String, Object> condition);

    List<Warning> getWarningInstance(Map<String, String> condition);

    List<Warning> getTotalWarning(Map<String, Object> condition);

    Integer getAllDeviceHasWarning(Map<String, Object> condition);

    Warning getListWarning(Map<String, String> condition);
}
