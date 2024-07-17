package vn.ses.s3m.plus.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.dto.*;
import vn.ses.s3m.plus.form.ProjectForm;
import vn.ses.s3m.plus.response.ProjectResponse;
import vn.ses.s3m.plus.service.*;

/**
 * Controller xử lý thông tin dự án.
 *
 * @author Arius Vietnam JSC
 * @since 2022-10-31
 */
@RestController
@RequestMapping("/common/project")
public class ProjectController {

    private static final int MAX_LENGTH_NAME = 255;

    private static final int MAX_LENGTH_DESC = 1000;

    private static final int MAX_LENGTH_ADDRESS = 500;

    // Logging
    private final Log log = LogFactory.getLog(ProjectController.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SystemMapService systemMapService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private SuperManagerService superManagerService;

    @Autowired
    private DeviceService deviceService;


    @Autowired
    private SettingCostService settingCostService;

    @Autowired
    private MaterialTypeService materialTypeService;

    @Autowired
    private MaterialValueService materialValueService;

    @Autowired
    private TypeTimeService typeTimeService;

    /**
     * Lấy danh sách dự án.
     *
     * @return projectList Danh sách dự án
     */
    @GetMapping("/list")
    public ResponseEntity<List<ProjectResponse>> getListProjects() {

        log.info("getListProjects START");
        List<Project> projects = projectService.getProjectList(null);
        List<ProjectResponse> projectList = new ArrayList<>();

        for (Project project : projects) {
            ProjectResponse projectRes = new ProjectResponse(project);
            projectList.add(projectRes);
        }
        log.info("getListProjects END");

        return new ResponseEntity<>(projectList, HttpStatus.OK);
    }

    /**
     * Lấy danh sách dự án theo id khách hàng và id khu vực.
     *
     * @param areaId     ID quận huyện
     * @param customerId ID khách hàng
     * @return projectList Danh sách dự án
     */
    @GetMapping("/list/{areaId}/{customerId}")
    public ResponseEntity<List<ProjectResponse>> getListProjecByAreaIdAndCustomerId(
            @PathVariable("areaId") final String areaId, @PathVariable("customerId") final String customerId) {

        log.info("getListProjecByAreaIdAndCustomerId START");
        Map<String, String> condition = new HashMap<String, String>();
        if (Integer.parseInt(areaId) != 0) {
            condition.put("areaId", areaId);
        }
        condition.put("customerId", customerId);
        List<Project> projects = projectService.getProjectList(condition);
        List<ProjectResponse> projectList = new ArrayList<>();
        for (Project project : projects) {
            ProjectResponse projectRes = new ProjectResponse(project);
            projectList.add(projectRes);
        }

        log.info("getListProjecByAreaIdAndCustomerId END");
        return new ResponseEntity<List<ProjectResponse>>(projectList, HttpStatus.OK);
    }

    /**
     * Tìm kiếm dự án theo từ khóa.
     *
     * @param keyword Từ khóa tìm kiếm theo tên dự án, tên khách hàng, tên quận huyện,...
     * @return projectList danh sách dự án
     */
    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<ProjectResponse>> search(@PathVariable final String keyword) {

        log.info("search START");
        Map<String, String> condition = new HashMap<String, String>();
        condition.put("keyword", keyword);
        List<Project> projects = projectService.searchProject(condition);
        List<ProjectResponse> projectList = new ArrayList<>();
        for (Project project : projects) {
            ProjectResponse projectRes = new ProjectResponse(project);
            projectList.add(projectRes);
        }
        log.info("search END");
        return new ResponseEntity<List<ProjectResponse>>(projectList, HttpStatus.OK);
    }

    /**
     * Thêm mới dự án.
     *
     * @param project Thông tin dự án mới
     * @return Thông báo khi tạo mới dự án thành công
     */
    @PostMapping("/add")
    public ResponseEntity<String> addProject(@Valid @RequestBody final ProjectForm project) {
        // CHECKSTYLE:OFF
        log.info("addProject START");
        if (!project.getProjectName()
                .isBlank()
                && project.getProjectName()
                .length() <= MAX_LENGTH_NAME
                && project.getAddress()
                .length() <= MAX_LENGTH_ADDRESS
                && project.getDescription()
                .length() <= MAX_LENGTH_DESC
                && project.getAreaId() != null && project.getCustomerId() != null) {
            // CHECKSTYLE:ON
            Project newProject = new Project();
            BeanUtils.copyProperties(project, newProject);
            projectService.addProject(newProject);

            // Thêm thông tin setting
            Map<String, Object> condition = new HashMap<>();
            condition.put("projectName", newProject.getProjectName());
            condition.put("customerId", newProject.getCustomerId());
            condition.put("areaId", newProject.getAreaId());
            Project newProjectToSetting = projectService.getProjectIdToAddSetting(condition);
            condition.put("projectId", newProjectToSetting.getProjectId());
            condition.put("customerId", newProjectToSetting.getCustomerId());
            settingCostService.addSettingCost(condition);

//           Thêm cài đặt giá nguyên liệu
            List<MaterialType> lsMaterialType = this.materialTypeService.getListMaterialType();
            List<TypeTime> lsTypeTime = this.typeTimeService.getTypeTime();

            for (MaterialType mtype :lsMaterialType) {
                for (int i = 0; i < lsTypeTime.size(); i++) {
                    MaterialValue value = new MaterialValue();
                    value.setProjectId(newProjectToSetting.getProjectId());
                    value.setMaterialId(mtype.getId());
                    value.setTypeTime(lsTypeTime.get(i).getId());
                    value.setMaterialPrice(1.0);
                    this.materialValueService.addMaterialValue(value);
                }
            }


            log.info("addProject END");
            return new ResponseEntity<String>("", HttpStatus.OK);
        } else {
            log.info("addProject END");
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * Lấy thông tin dự án.
     *
     * @param projectId ID dự án
     * @return project Thông tin dự án
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable final String projectId) {

        log.info("getProject START");
        Map<String, String> condition = new HashMap<>();
        condition.put("projectId", projectId);
        Project project = projectService.getProject(condition);
        ProjectResponse pro = new ProjectResponse(project);
        log.info("getProject END");
        return new ResponseEntity<>(pro, HttpStatus.OK);
    }

    /**
     * Cập nhật thông tin dự án.
     *
     * @param project Thông tin dự án
     * @return Thông báo khi cập nhật thành công
     */
    @PostMapping("/edit")
    public ResponseEntity<String> updateProject(@Valid @RequestBody final ProjectForm project) {
        // CHECKSTYLE:OFF
        log.info("updateProject START");
        if (!project.getProjectName()
                .isBlank()
                && project.getProjectName()
                .length() <= MAX_LENGTH_NAME
                && project.getAddress()
                .length() <= MAX_LENGTH_ADDRESS
                && project.getDescription()
                .length() <= MAX_LENGTH_DESC) {
            // CHECKSTYLE:ON
            Project editProject = new Project();
            BeanUtils.copyProperties(project, editProject);
            projectService.updateProject(editProject);

            log.info("updateProject END");
            return new ResponseEntity<String>("", HttpStatus.OK);
        } else {
            log.info("updateProject END");
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Xoá thông tin dự án.
     *
     * @param projectId ID dự án
     * @return Thông báo khi xóa thành công
     */
    @GetMapping("/delete/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable final String projectId) {

        log.info("deleteProject START");
        Map<String, String> condition = new HashMap<>();
        condition.put("projectId", projectId);
        Map<String, Object> conditionDevice = new HashMap<>();
        conditionDevice.put("projectId", projectId);
        if (projectService.checkProjectToDelete(condition)) {
            projectService.deleteProject(condition);
            deviceService.deleteDevice(conditionDevice);
            log.info("deleteProject END");
            return new ResponseEntity<String>("", HttpStatus.OK);
        } else {
            log.info("deleteProject END");
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * Lấy danh sách dự án theo id khách hàng.
     *
     * @param customerId ID khách hàng
     * @return projectList Danh sách dự án
     */
    @GetMapping("/list-tool/{customerId}")
    public ResponseEntity<List<ProjectResponse>> getListProjecByCustomerId(
            @PathVariable("customerId") final String customerId,
            @RequestParam(value = "ids", required = false) final String ids) {


        log.info("getListProjecByCustomerId START");
        String idsPro = null;
        if (ids == "" && ids == "0") {
            idsPro = null;
        } else {
            idsPro = ids;
        }
        List<Project> projects = projectService.getListProjectByCustomerId(customerId, idsPro);

        List<ProjectResponse> projectList = new ArrayList<>();
        for (Project project : projects) {
            List<SystemMap> systemMaps = systemMapService.getListSystemMapByProjectId(project.getProjectId());
            // CHECKSTYLE:OFF
            project.setSolarNum(systemMaps.stream()
                    .filter(item -> item.getSystemTypeId()
                            .equals(2))
                    .collect(Collectors.toList())
                    .size());
            project.setWindNum(systemMaps.stream()
                    .filter(item -> item.getSystemTypeId()
                            .equals(3))
                    .collect(Collectors.toList())
                    .size());
            project.setEvNum(systemMaps.stream()
                    .filter(item -> item.getSystemTypeId()
                            .equals(4))
                    .collect(Collectors.toList())
                    .size());
            project.setUtilityNum(systemMaps.stream()
                    .filter(item -> item.getSystemTypeId()
                            .equals(5))
                    .collect(Collectors.toList())
                    .size());
            project.setLoadNum(systemMaps.stream()
                    .filter(item -> item.getSystemTypeId()
                            .equals(1))
                    .collect(Collectors.toList())
                    .size());
            // CHECKSTYLE:ON
            ProjectResponse projectRes = new ProjectResponse(project);
            Map<String, String> condition = new HashMap<>();
            condition.put("areaId", String.valueOf(project.getAreaId()));
            Area area = areaService.getArea(condition);
            Manager manager = managerService.getManagerById(area.getManagerId());
            SuperManager superManager = superManagerService
                    .getSuperManagerById(Long.valueOf(manager.getSuperManagerId()));
            projectRes.setAreaName(area.getAreaName());
            projectRes.setManagerName(manager.getManagerName());
            projectRes.setSuperManagerName(superManager.getSuperManagerName());
            projectList.add(projectRes);
        }

        log.info("getListProjecByCustomerId END");
        return new ResponseEntity<List<ProjectResponse>>(projectList, HttpStatus.OK);
    }

    /**
     * Tìm kiếm danh sách dự án.
     *
     * @param customerId  ID khách hàng
     * @param projectName Tên dự án
     * @return response Danh sách dự án
     */
    @GetMapping("/list-search")
    public ResponseEntity<List<ProjectResponse>> searchListProjecs(@RequestParam("customerId") final String customerId,
                                                                   @RequestParam("projectName") final String projectName) {

        log.info("searchListProjecs START");
        List<Project> projects = projectService.searchListProject(customerId, projectName);

        List<ProjectResponse> response = new ArrayList<>();
        if (projects.size() > 0) {
            for (Project project : projects) {

                List<SystemMap> systemMaps = systemMapService.getListSystemMapByProjectId(project.getProjectId());
                // CHECKSTYLE:OFF
                project.setSolarNum(systemMaps.stream()
                        .filter(item -> item.getSystemTypeId()
                                .equals(2))
                        .collect(Collectors.toList())
                        .size());
                project.setWindNum(systemMaps.stream()
                        .filter(item -> item.getSystemTypeId()
                                .equals(3))
                        .collect(Collectors.toList())
                        .size());
                project.setEvNum(systemMaps.stream()
                        .filter(item -> item.getSystemTypeId()
                                .equals(4))
                        .collect(Collectors.toList())
                        .size());
                project.setUtilityNum(systemMaps.stream()
                        .filter(item -> item.getSystemTypeId()
                                .equals(5))
                        .collect(Collectors.toList())
                        .size());
                project.setLoadNum(systemMaps.stream()
                        .filter(item -> item.getSystemTypeId()
                                .equals(1))
                        .collect(Collectors.toList())
                        .size());
                // CHECKSTYLE:ON
                Map<String, String> condition = new HashMap<>();
                condition.put("areaId", String.valueOf(project.getAreaId()));
                Area area = areaService.getArea(condition);
                Manager manager = managerService.getManagerById(area.getManagerId());
                SuperManager superManager = superManagerService
                        .getSuperManagerById(Long.valueOf(manager.getSuperManagerId()));
                ProjectResponse projectRes = new ProjectResponse(project);
                projectRes.setAreaName(area.getAreaName());
                projectRes.setManagerName(manager.getManagerName());
                projectRes.setSuperManagerName(superManager.getSuperManagerName());

                response.add(projectRes);
            }
        }

        log.info("searchListProjecs END");
        return new ResponseEntity<List<ProjectResponse>>(response, HttpStatus.OK);
    }

    /**
     * thêm setting cho dự án cũ
     */
    // CHECKSTYLE:OFF
    @GetMapping("/addSetting/{customerId}/{projectId}/{systemType}")
    public ResponseEntity<?> addSettingForOldProject(@PathVariable final String customerId,
                                                     @PathVariable final String projectId, @PathVariable final String systemType) {

        Map<String, Object> condition = new HashMap<>();
        condition.put("customerId", customerId);
        condition.put("projectId", projectId);
        condition.put("systemType", systemType);
        if (Integer.parseInt(systemType) == 1) {
            projectService.addSettingLoad(condition);
            condition.put("limit", 17);
            List<Setting> settings = projectService.getSettingByProjectIdAndSystem(condition);
            Map<String, Object> settingHistory = new HashMap<>();
            for (int i = settings.size() - 1; i >= 0; i--) {
                settingHistory.put("settingId", settings.get(i)
                        .getSettingId());
                settingHistory.put("settingValue", settings.get(i)
                        .getSettingValue());
                settingHistory.put("type", settings.get(i)
                        .getType());
                settingHistory.put("settingDate", settings.get(i)
                        .getSettingDate());
                projectService.addSettingHistory(settingHistory);
            }
        }
        if (Integer.parseInt(systemType) == 2) {
            projectService.addSettingPV(condition);
            condition.put("limit", 16);
            List<Setting> settings = projectService.getSettingByProjectIdAndSystem(condition);
            Map<String, Object> settingHistory = new HashMap<>();
            for (int i = settings.size() - 1; i >= 0; i--) {
                settingHistory.put("settingId", settings.get(i)
                        .getSettingId());
                settingHistory.put("settingValue", settings.get(i)
                        .getSettingValue());
                settingHistory.put("type", settings.get(i)
                        .getType());
                settingHistory.put("settingDate", settings.get(i)
                        .getSettingDate());
                projectService.addSettingHistory(settingHistory);
            }
        }
        if (Integer.parseInt(systemType) == 5) {
            projectService.addSettingGrid(condition);
            condition.put("limit", 15);
            List<Setting> settings = projectService.getSettingByProjectIdAndSystem(condition);
            Map<String, Object> settingHistory = new HashMap<>();
            for (int i = settings.size() - 1; i >= 0; i--) {
                settingHistory.put("settingId", settings.get(i)
                        .getSettingId());
                settingHistory.put("settingValue", settings.get(i)
                        .getSettingValue());
                settingHistory.put("type", settings.get(i)
                        .getType());
                settingHistory.put("settingDate", settings.get(i)
                        .getSettingDate());
                projectService.addSettingHistory(settingHistory);
            }
        }
        return new ResponseEntity<String>("Ok", HttpStatus.OK);
    }
    // CHECKSTYLE:ON

    /**
     * Cập nhật thông hình ảnh dự án.
     *
     * @param projectId id dự án
     * @param project   Thông tin dự án
     * @return Thông báo khi cập nhật thành công
     */
    @PostMapping("/update-image/{projectId}")
    public ResponseEntity<String> updateImageProject(@PathVariable("projectId") final Integer projectId,
                                                     @RequestBody final Project project) {
        // CHECKSTYLE:OFF
        log.info("updateImageProject START");
        HashMap<String, String> condition = new HashMap<>();
        condition.put("projectId", String.valueOf(projectId));
        if (projectId != null) {
            Project iProject = projectService.getProject(condition);
            if (project.getImgLoad() != null) {
                iProject.setImgLoad(project.getImgLoad());
            }
            if (project.getImgPv() != null) {
                iProject.setImgPv(project.getImgPv());
            }
            if (project.getImgGrid() != null) {
                iProject.setImgGrid(project.getImgGrid());
            }

            projectService.updateProject(iProject);

            return new ResponseEntity<String>("", HttpStatus.OK);
        } else {
            log.info("updateProject END");
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/listIds")
    public ResponseEntity<List<ProjectResponse>> getListProjectIds(
            @RequestParam(value = "userName", required = true) final String userName) {

        log.info("getListProjectIds START");
        List<ProjectResponse> projectList = new ArrayList<>();
        String projectIds = projectService.getListProjectIds(userName);
        System.out.println("projectIds: " + projectIds);
        HashMap<String, String> condition = new HashMap<String, String>();
        condition.put("projectIds", projectIds);
        if (projectIds != null) {
            List<Project> projects = projectService.getListPro(condition);
            for (Project project : projects) {
                ProjectResponse projectRes = new ProjectResponse(project);
                projectList.add(projectRes);
            }
        }

        log.info("getListProjectIds END");
        return new ResponseEntity<List<ProjectResponse>>(projectList, HttpStatus.OK);
    }

    @GetMapping("/ids")
    public ResponseEntity<String> getProIds(@RequestParam(value = "userName", required = true) final String userName) {
        log.info("getListProIds START");
        String projectIds = projectService.getListProjectIds(userName);
        log.info("getListProIds END");
        return new ResponseEntity<String>(projectIds, HttpStatus.OK);
    }
}
