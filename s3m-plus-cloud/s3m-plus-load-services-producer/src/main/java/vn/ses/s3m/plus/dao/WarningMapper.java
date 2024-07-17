package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Warning;

@Mapper
public interface WarningMapper {

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

    void updateWarningCache(Map<String, Object> condition);

    Integer getAllDeviceHasWarning(Map<String, Object> condition);

    Integer countWarningByWarningType(Map<String, Object> condition);

    // PV
    List<Warning> getTotalWarningPV(Map<String, Object> condition);

    Integer getAllDeviceHasWarningPV(Map<String, Object> condition);

    List<Warning> countWarningInverter(Map<String, Object> condition);

    List<Warning> countWarningCombiner(Map<String, Object> condition);

    List<Warning> countWarningString(Map<String, Object> condition);

    // START: Grid

    List<Warning> getTotalWarningGrid(Map<String, Object> condition);

    Integer getAllDeviceHasWarningGrid(Map<String, Object> condition);

    List<Warning> countWarningRMU(Map<String, Object> condition);

    // --
    List<Warning> getDetailWarningType(Map<String, Object> condition);

    List<Warning> getListWarningByWarningType(Map<String, Object> condition);

    Warning getDetailWarningCachePV(Map<String, Object> condition);

    void updateWarningCachePV(Map<String, Object> condition);

    // END

}
