package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.dto.Project;
import vn.ses.s3m.plus.dto.Setting;

@Mapper
public interface ProjectMapper {
    List<Project> getProjectList(Map<String, String> condition);

    List<Project> searchProject(Map<String, String> condition);

    void addProject(Project project);

    Project getProject(Map<String, String> condition);

    void updateProject(Project project);

    void deleteProject(Map<String, String> condition);

    List<Project> getListProjectByAreaIdAndCustomerId(@Param ("areaId") Integer areaId,
        @Param ("customerId") Integer customerId);

    List<Project> checkProjectIdInDeviceTable(Map<String, String> condition);

    List<Project> checkProjectIdInUserTable(Map<String, String> condition);

    List<Project> checkProjectIdInSystemMapTable(Map<String, String> condition);

    List<Project> getListProjectByCustomerId(@Param ("customerId") String customerId, @Param ("ids") String ids);

    List<Project> getListProjectBySuperManagerId(@Param ("superManagerId") Integer superManagerId);

    List<Project> getProjectByIds(Map<String, String> condition);

    Long getPowerByProjectId(Integer projectId);

    void addSetting(Map<String, Object> condition);

    void addSettingHistory(Map<String, Object> condition);

    List<Setting> getSettingByProjectId(Map<String, Object> condition);

    Project getProjectIdToAddSetting(Map<String, Object> condition);

    List<Project> searchListProject(@Param ("customerId") String customerId, @Param ("projectName") String projectName);

    List<Project> getProjectsByCustomerAndSuperManager(Map<String, String> condition);

    List<Project> getProjectsByManagerIdToCustomer(Map<String, String> condition);

    List<Project> getAllProjects();

    Map<String, String> getInformationProject(Map<String, Object> condition);

    void addSettingGrid(Map<String, Object> condition);

    void addSettingPV(Map<String, Object> condition);

    void addSettingLoad(Map<String, Object> condition);

    List<Setting> getSettingByProjectIdAndSystem(Map<String, Object> condition);

    List<Project> listProjectByCondition(Map<String, String> condition);

    List<Project> getProjectBySystemType(Map<String, String> condition);

    Project getProjectByDeviceId(Map<String, String> condition);

    Double getDataPlanEnergyBatch(@Param ("schema") String schema, @Param ("projectId") Integer projectId,
        @Param ("systemTypeId") Integer systemTypeId);

    String getListProjectIds(String userName);

    List<Project> getListPro(Map<String, String> condition);
}
