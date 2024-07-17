package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Warning;

public interface WarningPVService {
    List<Warning> getWarnings(Map<String, Object> condition);

    List<Warning> getTotalWarningPV(Map<String, Object> condition);

    Integer getAllDeviceHasWarningPV(Map<String, Object> condition);

    List<Warning> countWarningInverter(Map<String, Object> condition);

    List<Warning> countWarningCombiner(Map<String, Object> condition);

    List<Warning> countWarningString(Map<String, Object> condition);

    List<Warning> countWarnings(Map<String, Object> condition);

    List<Warning> getDetailWarningType(Map<String, Object> condition);

    List<Warning> getListWarningByWarningType(Map<String, Object> condition);

    Warning getDetailWarningCachePV(Map<String, Object> condition);

    boolean updateWarningCachePV(Map<String, Object> condition);

}