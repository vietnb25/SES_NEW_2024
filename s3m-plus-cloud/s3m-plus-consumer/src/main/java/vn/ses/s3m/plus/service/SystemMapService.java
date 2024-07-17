package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.SystemMap;

public interface SystemMapService {

    List<SystemMap> getListSystemMapById(Map<String, String> condition);

    List<SystemMap> getSystemMapByProject(Map<String, String> condition);

    List<SystemMap> getListSystemMapByProjectId(Integer projectId);

    List<SystemMap> getListSystemMapByProjectIdAndSystemTypeId(Map<String, String> condition);

    void addSystemMap(SystemMap condition);

    void updateSystemMap(SystemMap condition);

    void deleteSystemMap(String systemMapId);

    SystemMap getMainSystem(Map<String, String> condition);

    SystemMap getSystemMapById(Integer systemMapId);

    List<SystemMap> getSystemMapByCustomer(Map<String, String> condition);

    List<SystemMap> getAllSystemMap();

    List<SystemMap> checkSystemMap(Map<String, String> condition);

    List<SystemMap> getSystemMapByCustomerAndProject(Map<String, String> condition);

    List<SystemMap> getSystemMapByProjectId();

    int getCountModun(Map<String, Object> condition);

    int getCountDeviceModule(Map<String, String> condition);
}
