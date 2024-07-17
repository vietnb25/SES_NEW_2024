package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.ProjectMapper;
import vn.ses.s3m.plus.dto.Project;
import vn.ses.s3m.plus.dto.Setting;

/**
 * Service xử lý thông tin dự án.
 *
 * @author Arius Vietnam JSC
 * @since 2022-10-31
 */
@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectMapper projectMapper;

    /**
     * Lấy danh sách dự án.
     *
     * @param condition Điều kiện truy vấn
     * @return Danh sách dự án
     */
    @Override
    public List<Project> getProjectList(final Map<String, String> condition) {
        return projectMapper.getProjectList(condition);
    }

    /**
     * Tìm kiếm dự án theo từ khóa.
     *
     * @param condition Từ khóa chứa thông tin tìm kiếm
     * @return Danh sách dự án
     */
    @Override
    public List<Project> searchProject(final Map<String, String> condition) {
        return projectMapper.searchProject(condition);
    }

    /**
     * Thêm mới dự án.
     *
     * @param project Thông tin dự án mới
     */
    @Override
    public void addProject(final Project project) {
        projectMapper.addProject(project);
    }

    /**
     * Lấy thông tin dự án.
     *
     * @param condition ID dự án
     * @return Thông tin dự án
     */
    @Override
    public Project getProject(final Map<String, String> condition) {
        return projectMapper.getProject(condition);
    }

    /**
     * Cập nhật thông tin dự án.
     *
     * @param project Thông tin dự án
     */
    @Override
    public void updateProject(final Project project) {
        projectMapper.updateProject(project);
    }

    /**
     * Xóa thông tin dự án khỏi danh sách.
     *
     * @param projectId ID dự án
     */
    @Override
    public void deleteProject(final Map<String, String> condition) {
        projectMapper.deleteProject(condition);
    }

    /**
     * Lấy dự án theo areaId và customerId.
     *
     * @param customerId ID của customer.
     * @param areaId ID của area.
     * @return Danh sách dự án.
     */
    @Override
    public List<Project> getListProjectByAreaIdAndCustomerId(final Integer areaId, final Integer customerId) {
        return projectMapper.getListProjectByAreaIdAndCustomerId(areaId, customerId);
    }

    /**
     * Kiểm tra có đối tượng khác phụ thuộc vào đối tượng dự án không.
     *
     * @param condition Chứa ID dự án
     */
    @Override
    public Boolean checkProjectToDelete(final Map<String, String> condition) {
        // CHECKSTYLE:OFF
        if (projectMapper.checkProjectIdInDeviceTable(condition)
            .size() > 0
            || projectMapper.checkProjectIdInSystemMapTable(condition)
                .size() > 0
            || projectMapper.checkProjectIdInUserTable(condition)
                .size() > 0) {
            // CHECKSTYLE:ON
            return false;
        } else {
            return true;
        }
    }

    /**
     * Lấy ra danh sách dự án theo khách hàng
     *
     * @param customerId Mã khách hàng
     * @return Danh sách dự án theo khách hàng
     */
    @Override
    public List<Project> getListProjectByCustomerId(final String customerId, final String ids) {
        return projectMapper.getListProjectByCustomerId(customerId, ids);
    }

    /**
     * Lấy ra danh sách dự án theo khu vực
     *
     * @param superManagerId Mã khu vực
     * @return Danh sách dự án theo khu vực
     */
    @Override
    public List<Project> getListProjectBySuperManagerId(final Integer superManagerId) {
        return projectMapper.getListProjectBySuperManagerId(superManagerId);
    }

    /**
     * Lấy ra danh sách dự án theo nhiều id
     */
    @Override
    public List<Project> getProjectByIds(final Map<String, String> condition) {
        return projectMapper.getProjectByIds(condition);
    }

    /**
     * Lấy ra tổng công suất theo dự án
     *
     * @param projectId Mã dự án
     * @return Tổng công suất theo dự án
     */
    @Override
    public Long getPowerByProjectId(final Integer projectId) {
        return projectMapper.getPowerByProjectId(projectId);
    }

    /**
     * Thêm thông tin setting
     *
     * @param condition thông tin setting
     */
    @Override
    public void addSetting(final Map<String, Object> condition) {
        projectMapper.addSetting(condition);
    }

    /**
     * Thêm thông tin lịch sử setting
     *
     * @param condition thông tin lịch sử setting
     */
    @Override
    public void addSettingHistory(final Map<String, Object> condition) {
        projectMapper.addSettingHistory(condition);
    }

    /**
     * Lấy thông tin setting để thêm vào lịch sử setting
     *
     * @param condition thông tin projectId
     * @return danh sách thông tin setting
     */
    @Override
    public List<Setting> getSettingByProjectId(final Map<String, Object> condition) {
        return projectMapper.getSettingByProjectId(condition);
    }

    /**
     * Lấy thông tin project để thêm vào setting
     */
    @Override
    public Project getProjectIdToAddSetting(final Map<String, Object> condition) {
        return projectMapper.getProjectIdToAddSetting(condition);
    }

    /**
     *
     */
    @Override
    public List<Project> searchListProject(final String customerId, final String projectName) {
        return projectMapper.searchListProject(customerId, projectName);
    }

    /**
     * Lấy ra danh sách dự án theo mã khách hàng và miền
     */
    @Override
    public List<Project> getProjectsByCustomerAndSuperManager(final Map<String, String> condition) {
        return projectMapper.getProjectsByCustomerAndSuperManager(condition);
    }

    /**
     * Lấy ra danh sách dự án theo mã khách hàng, miền và tỉnh thành
     */
    @Override
    public List<Project> getProjectsByManagerIdToCustomer(final Map<String, String> condition) {
        return projectMapper.getProjectsByManagerIdToCustomer(condition);
    }

    /**
     * Lấy ra tất cả dự án
     */
    @Override
    public List<Project> getAllProjects() {
        return projectMapper.getAllProjects();
    }

    /**
     * Lấy ra thông tin dự án
     */
    @Override
    public Map<String, String> getInformationProject(final Map<String, Object> condition) {
        return projectMapper.getInformationProject(condition);
    }

    /**
     *
     */
    @Override
    public void addSettingGrid(final Map<String, Object> condition) {
        // TODO Auto-generated method stub
        projectMapper.addSettingGrid(condition);
    }

    /**
     *
     */
    @Override
    public void addSettingPV(final Map<String, Object> condition) {
        // TODO Auto-generated method stub
        projectMapper.addSettingPV(condition);
    }

    /**
     *
     */
    @Override
    public void addSettingLoad(final Map<String, Object> condition) {
        // TODO Auto-generated method stub
        projectMapper.addSettingLoad(condition);
    }

    /**
     *
     */
    @Override
    public List<Setting> getSettingByProjectIdAndSystem(final Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return projectMapper.getSettingByProjectIdAndSystem(condition);
    }

    /**
     *
     */
    @Override
    public List<Project> listProjectByCondition(final Map<String, String> condition) {
        return projectMapper.listProjectByCondition(condition);
    }

    /**
     *
     */
    @Override
    public List<Project> getProjectBySystemType(final Map<String, String> condition) {
        return projectMapper.getProjectBySystemType(condition);
    }

    @Override
    public Project getProjectByDeviceId(Map<String, String> condition) {
        return projectMapper.getProjectByDeviceId(condition);
    }

    @Override
    public Double getDataPlanEnergyBatch(String schema, Integer projectId, Integer systemTypeId) {
        return projectMapper.getDataPlanEnergyBatch(schema, projectId, systemTypeId);
    }

    @Override
    public String getListProjectIds(String userName) {
        return projectMapper.getListProjectIds(userName);
    }

    @Override
    public List<Project> getListPro(final Map<String, String> condition) {
        return projectMapper.getListPro(condition);
    }

}
