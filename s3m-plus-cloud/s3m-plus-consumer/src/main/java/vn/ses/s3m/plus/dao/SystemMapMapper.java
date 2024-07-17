package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.dto.SystemMap;

@Mapper
public interface SystemMapMapper {

    List<SystemMap> getListSystemMapById(Map<String, String> condition);

    List<SystemMap> getSystemMapByProject(Map<String, String> condition);

    List<SystemMap> getListSystemMapByProjectId(Integer projectId);

    List<SystemMap> getListSystemMapByProjectIdAndSystemTypeId(Map<String, String> condition);

    void addSystemMap(@Param ("systemMap") SystemMap condition);

    void updateSystemMap(@Param ("systemMap") SystemMap condition);

    void deleteSystemMap(String systemMapId);

    SystemMap getMainSystem(Map<String, String> condition);

    SystemMap getSystemMapById(@Param ("systemMapId") Integer systemMapId);

    List<SystemMap> getSystemMapByCustomer(Map<String, String> condition);

    List<SystemMap> getAllSystemMap();

    List<SystemMap> checkSystemMap(Map<String, String> condition);

    List<SystemMap> getSystemMapByCustomerAndProject(Map<String, String> condition);

    List<SystemMap> getSystemMapByProjectId();

    int getCountModun(Map<String, Object> condition);

    int getCountDeviceModule(Map<String, String> condition);
}
