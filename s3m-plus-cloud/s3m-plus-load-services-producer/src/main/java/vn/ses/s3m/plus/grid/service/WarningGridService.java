package vn.ses.s3m.plus.grid.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Warning;

public interface WarningGridService {

    List<Warning> getWarnings(Map<String, Object> condition);

    List<Warning> getTotalWarningGrid(Map<String, Object> condition);

    Integer getAllDeviceHasWarningGrid(Map<String, Object> condition);

    List<Warning> getDetailWarningType(Map<String, Object> condition);

    List<Warning> countWarningRMU(Map<String, Object> condition);

    /*
     * chưa có frame List<Warning> getDetailWarningTypeSTMV(Map<String, Object> condition); List<Warning>
     * getDetailWarningTypeSGMV(Map<String, Object> condition);
     */
    List<Warning> getListWarningByWarningType(Map<String, Object> condition);

    Warning getDetailWarningCacheGrid(Map<String, Object> condition);

    boolean updateWarningCacheGrid(Map<String, Object> condition);
}
