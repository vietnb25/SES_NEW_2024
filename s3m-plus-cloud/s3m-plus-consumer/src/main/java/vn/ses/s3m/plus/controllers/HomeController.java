package vn.ses.s3m.plus.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zeroturnaround.zip.ZipUtil;

import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.common.DateUtils;
import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.Area;
import vn.ses.s3m.plus.dto.Customer;
import vn.ses.s3m.plus.dto.DataInverter1;
import vn.ses.s3m.plus.dto.DataLoadFrame1;
import vn.ses.s3m.plus.dto.DataRmuDrawer1;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.Manager;
import vn.ses.s3m.plus.dto.Project;
import vn.ses.s3m.plus.dto.SuperManager;
import vn.ses.s3m.plus.dto.SystemMap;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.dto.evn.JsonSystemTree;
import vn.ses.s3m.plus.response.JsonElectricPower;
import vn.ses.s3m.plus.response.ProjectInfo;
import vn.ses.s3m.plus.response.ProjectLocation;
import vn.ses.s3m.plus.response.ProjectTreeTotalPower;
import vn.ses.s3m.plus.response.SystemTreeData;
import vn.ses.s3m.plus.service.AreaService;
import vn.ses.s3m.plus.service.CustomerService;
import vn.ses.s3m.plus.service.DataInverterService;
import vn.ses.s3m.plus.service.DataLoadFrame1Service;
import vn.ses.s3m.plus.service.DataRmuDrawer1Service;
import vn.ses.s3m.plus.service.DeviceService;
import vn.ses.s3m.plus.service.ManagerService;
import vn.ses.s3m.plus.service.ProjectService;
import vn.ses.s3m.plus.service.SuperManagerService;
import vn.ses.s3m.plus.service.SystemMapService;
import vn.ses.s3m.plus.service.UserRoleService;
import vn.ses.s3m.plus.service.UserService;
import vn.ses.s3m.plus.service.WarningService;

/**
 * Home Controller xử lý project tree và project map
 *
 * @author Arius Vietnam JSC
 * @since 2022-11-15
 */
@RestController
@RequestMapping ("/common/home")

public class HomeController {

    private final Log log = LogFactory.getLog(HomeController.class);
    static final int MILLISECOND = 1000;

    static final int FIFTEEN_MINUTE = 900;

    static final int MINUTES_MILLIS = 5;
    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SuperManagerService superManagerService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private SystemMapService systemMapService;

    @Autowired
    private DataLoadFrame1Service dataLoadFrame1Service;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private WarningService warningService;

    @Autowired
    private DataInverterService dataInverterService;

    @Autowired
    private DataRmuDrawer1Service dataRmuDrawer1Service;

    @Value ("${time-active-module}")
    private Long timeActiveModule;

    @Value ("${consumer.producer.export-folder}")
    private String folderName;

    @GetMapping ("/init/{usernameLogin}")
    public ResponseEntity<?> home(@PathVariable ("usernameLogin") final String usernameLogin) {
        User user = this.userService.getUserByUsername(usernameLogin);

        List<SystemTreeData> dataTree = this.systemTree(user);
        List<ProjectLocation> dataMarkers = this.getMarkers(user);

        Map<String, Object> mapData = new HashMap<>();

        mapData.put("tree", dataTree);
        mapData.put("markers", dataMarkers);

        return new ResponseEntity<>(mapData, HttpStatus.OK);
    }

    /**
     * Lấy Project Tree Data.
     *
     * @param usernameLogin Username của tài khoản đang đăng nhập
     * @return Danh sách ProjectTree
     */
    @GetMapping ("/{usernameLogin}/project-tree")
    public ResponseEntity<?> getProjectTree(@PathVariable ("usernameLogin") final String usernameLogin) {
        User user = this.userService.getUserByUsername(usernameLogin);
        // List<ProjectTree> projectTrees = this.getProjectTree(user);
        List<SystemTreeData> data = this.systemTree(user);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    /**
     * Lấy Project Tree Data EVN.
     *
     * @param usernameLogin Username của tài khoản đang đăng nhập
     * @return Danh sách ProjectTree
     */
    @GetMapping ("/{usernameLogin}/project-tree-evn")
    public ResponseEntity<?> getProjectTreeEVN(@PathVariable ("usernameLogin") final String usernameLogin) {
        User user = this.userService.getUserByUsername(usernameLogin);
        // List<ProjectTree> projectTrees = this.getProjectTree(user);
        List<JsonSystemTree> data = this.systemTreeEVN(user);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    private List<JsonSystemTree> systemTreeEVN(User user) {
        List<String> jsons = new ArrayList<String>();

        String userType = String.valueOf(user.getUserType());

        List<JsonSystemTree> itemList = new ArrayList<>();

        if (StringUtils.equals(userType, "6")) {

            Map<String, String> areaCondition = new HashMap<String, String>();
            areaCondition.put("areaId", String.valueOf(user.getTargetId()));

            List<Area> areaList = new ArrayList<Area>();
            Area ar = areaService.getArea(areaCondition);
            areaList.add(ar);

            JsonSystemTree itemUserAll = new JsonSystemTree();
            itemUserAll.setId("All-P");
            itemUserAll.setType("project");
            itemUserAll.setParent("#");
            itemUserAll.setText("All");

            Map<String, String> dataUserAll = new HashMap<>();
            dataUserAll.put("position", "-4");
            itemUserAll.setData(dataUserAll);
            itemList.add(itemUserAll);

            if (areaList.size() > 0) {
                for (int m = 0; m < areaList.size(); m++) {
                    Area area = areaList.get(m);
                    Map<String, String> conditionProject = new HashMap<>();
                    conditionProject.put("areaId", String.valueOf(area.getAreaId()));
                    List<Project> projectList = projectService.getProjectList(conditionProject);

                    if (projectList.size() > 0) {
                        for (int n = 0; n < projectList.size(); n++) {
                            Project project = projectList.get(n);
                            JsonSystemTree itemProject = new JsonSystemTree();
                            itemProject.setId("P-" + project.getProjectId());
                            itemProject.setType("project");
                            itemProject.setParent("#");
                            itemProject.setText(project.getProjectName());

                            Map<String, String> dataProject = new HashMap<>();
                            dataProject.put("position", String.valueOf(n));
                            itemProject.setData(dataProject);

                            itemList.add(itemProject);
                        }
                    }

                }
            }
        } else if (StringUtils.equals(userType, "5")) {
            List<Manager> managerList = new ArrayList<Manager>();
            Manager mng = managerService.getManagerById(user.getTargetId());
            managerList.add(mng);

            JsonSystemTree itemUserAll = new JsonSystemTree();
            itemUserAll.setId("All-A");
            itemUserAll.setType("area");
            itemUserAll.setParent("#");
            itemUserAll.setText("All");

            Map<String, String> dataUserAll = new HashMap<>();
            dataUserAll.put("position", "-3");
            itemUserAll.setData(dataUserAll);
            itemList.add(itemUserAll);

            for (int j = 0; j < managerList.size(); j++) {
                Manager manager = managerList.get(j);

                Map<String, String> customerCondition = new HashMap<String, String>();
                customerCondition.put("managerId", String.valueOf(manager.getManagerId()));

                List<Area> areaList = areaService.getAreas(customerCondition);

                if (areaList.size() > 0) {
                    for (int m = 0; m < areaList.size(); m++) {
                        Area area = areaList.get(m);
                        JsonSystemTree itemArea = new JsonSystemTree();
                        itemArea.setId("A-" + String.valueOf(area.getAreaId()));
                        itemArea.setType("area");
                        itemArea.setParent("#");
                        itemArea.setText(area.getAreaName());

                        Map<String, String> dataArea = new HashMap<>();
                        dataArea.put("position", String.valueOf(m));
                        itemArea.setData(dataArea);

                        itemList.add(itemArea);
                    }
                }

            }
        } else if (StringUtils.equals(userType, "4")) {
            JsonSystemTree itemUserAll = new JsonSystemTree();
            itemUserAll.setId("All-M");
            itemUserAll.setType("manager");
            itemUserAll.setParent("#");
            itemUserAll.setText("All");

            Map<String, String> dataUserAll = new HashMap<>();
            dataUserAll.put("position", "-3");
            itemUserAll.setData(dataUserAll);
            itemList.add(itemUserAll);

            Map<String, String> managerCondition = new HashMap<String, String>();
            managerCondition.put("superManagerId", String.valueOf(user.getTargetId()));

            List<Manager> managerList = managerService.getManagers(managerCondition);

            for (int j = 0; j < managerList.size(); j++) {
                Manager manager = managerList.get(j);
                JsonSystemTree itemManager = new JsonSystemTree();
                itemManager.setId("M-" + String.valueOf(manager.getManagerId()));
                itemManager.setType("manager");
                itemManager.setParent("#");
                itemManager.setText(manager.getManagerName());

                Map<String, String> dataManager = new HashMap<>();
                dataManager.put("position", String.valueOf(j));
                itemManager.setData(dataManager);

                itemList.add(itemManager);
            }

        } else {
            Map<String, String> conditionSuperManager = new HashMap<>();
            List<SuperManager> superManagerList = superManagerService.getSuperManagers(conditionSuperManager);

            JsonSystemTree itemUserAll = new JsonSystemTree();
            itemUserAll.setId("All-S");
            itemUserAll.setType("superManager");
            itemUserAll.setParent("#");
            itemUserAll.setText("All");

            Map<String, String> dataUserAll = new HashMap<>();
            dataUserAll.put("position", "-2");
            itemUserAll.setData(dataUserAll);
            itemList.add(itemUserAll);

            for (int i = 0; i < superManagerList.size(); i++) {
                SuperManager superManager = superManagerList.get(i);
                JsonSystemTree itemSuperManager = new JsonSystemTree();
                itemSuperManager.setId("S-" + String.valueOf(superManager.getSuperManagerId()));
                itemSuperManager.setType("superManager");
                itemSuperManager.setParent("#");
                itemSuperManager.setText(superManager.getSuperManagerName());

                Map<String, String> dataSuperManager = new HashMap<>();
                dataSuperManager.put("position", String.valueOf(i));
                itemSuperManager.setData(dataSuperManager);

                itemList.add(itemSuperManager);
            }
        }

        return itemList;

    }

    private List<SystemTreeData> systemTree(User user) {
        List<SystemTreeData> systemTree = new ArrayList<>();
        // data to create tree
        Map<String, String> condition = new HashMap<>();

        if (user != null && user.getCustomerId() != null) {
            condition.put("customerId", String.valueOf(user.getCustomerId()));
        }

        List<Customer> customers = customerService.getListCustomer(condition);
        List<SuperManager> superManagers = superManagerService.getSuperManagersActive(condition);
        List<Manager> managers = managerService.getManagersActive(condition);
        List<Area> areas = areaService.getAreasActive(condition);
        List<Project> projects = projectService.getProjectList(condition);
        List<SystemMap> systemMaps = systemMapService.getSystemMapByCustomer(condition);

        if (user.getSuperManagerId() != null) {
            superManagers = superManagers.stream()
                .filter(item -> item.getSuperManagerId() == user.getSuperManagerId())
                .collect(Collectors.toList());
        }

        if (user.getManagerId() != null) {
            managers = managers.stream()
                .filter(item -> item.getManagerId() == user.getManagerId())
                .collect(Collectors.toList());
        }

        if (user.getAreaId() != null) {
            areas = areas.stream()
                .filter(item -> item.getAreaId() == user.getAreaId())
                .collect(Collectors.toList());
        }

        if (user.getProjectId() != null) {
            projects = projects.stream()
                .filter(item -> item.getProjectId() == user.getProjectId())
                .collect(Collectors.toList());
        }

        if (user.getSystemTypeId() != null) {
            systemMaps = systemMaps.stream()
                .filter(item -> item.getSystemTypeId() == user.getSystemTypeId())
                .collect(Collectors.toList());
        }

        if (user != null && user.getUserType() != null) {
            Integer userType = user.getUserType();

            if (userType == 1) {
                // init item tree
                SystemTreeData treeDataAll = new SystemTreeData();
                treeDataAll.setKey("All-C");
                treeDataAll.setType("customer");
                treeDataAll.setIcon("tree-customer");
                treeDataAll.setLabel("ALL");

                Map<String, Object> superManagerData = new HashMap<>();
                superManagerData.put("position", "-1");
                treeDataAll.setData(superManagerData);

                // add to list tree project
                systemTree.add(treeDataAll);

                for (int i = 0; i < customers.size(); i++) {
                    Customer customer = customers.get(i);

                    SystemTreeData treeDataCustomer = new SystemTreeData();

                    treeDataCustomer.setKey("C-" + String.valueOf(customer.getCustomerId()));
                    treeDataCustomer.setType("customer");
                    treeDataCustomer.setIcon("tree-customer");
                    treeDataCustomer.setLabel(customer.getCustomerName());

                    Map<String, Object> dataCustomer = new HashMap<>();
                    dataCustomer.put("position", String.valueOf(i));
                    dataCustomer.put("customerId", String.valueOf(customer.getCustomerId()));

                    treeDataCustomer.setData(dataCustomer);

                    for (int j = 0; j < superManagers.size(); j++) {
                        SuperManager superManager = superManagers.get(j);

                        SystemTreeData treeDataSuperManager = new SystemTreeData();

                        treeDataSuperManager.setKey("S-" + String.valueOf(superManager.getSuperManagerId()) + "-" + i);
                        treeDataSuperManager.setType("superManager");
                        treeDataSuperManager.setIcon("tree-superManager");
                        treeDataSuperManager.setLabel(superManager.getSuperManagerName());

                        Map<String, Object> dataSuperManager = new HashMap<>();
                        dataSuperManager.put("position", String.valueOf(j));
                        dataSuperManager.put("customerId", String.valueOf(customer.getCustomerId()));
                        dataSuperManager.put("superManagerId", String.valueOf(superManager.getSuperManagerId()));
                        treeDataSuperManager.setData(dataSuperManager);

                        for (int k = 0; k < managers.size(); k++) {
                            Manager manager = managers.get(k);

                            if (manager.getSuperManagerId() == superManager.getSuperManagerId()) {
                                SystemTreeData treeDataManager = new SystemTreeData();

                                treeDataManager.setKey("M-" + String.valueOf(manager.getManagerId()) + "-" + i);
                                treeDataManager.setType("manager");
                                treeDataManager.setIcon("tree-manager");
                                treeDataManager.setLabel(manager.getManagerName());

                                Map<String, Object> dataManager = new HashMap<>();
                                dataManager.put("position", String.valueOf(j));
                                dataManager.put("managerId", String.valueOf(manager.getManagerId()));
                                dataManager.put("customerId", String.valueOf(customer.getCustomerId()));
                                dataManager.put("superManagerId", String.valueOf(superManager.getSuperManagerId()));

                                treeDataManager.setData(dataManager);

                                for (int m = 0; m < areas.size(); m++) {
                                    Area area = areas.get(m);

                                    if (area.getManagerId() == manager.getManagerId()) {
                                        SystemTreeData treeDataArea = new SystemTreeData();

                                        treeDataArea.setKey("A-" + String.valueOf(area.getAreaId()) + "-" + i);
                                        treeDataArea.setType("area");
                                        treeDataArea.setIcon("tree-area");
                                        treeDataArea.setLabel(area.getAreaName());

                                        Map<String, Object> dataArea = new HashMap<>();
                                        dataArea.put("position", String.valueOf(m));
                                        dataArea.put("areaId", String.valueOf(area.getAreaId()));
                                        dataArea.put("managerId", String.valueOf(manager.getManagerId()));
                                        dataArea.put("customerId", String.valueOf(customer.getCustomerId()));
                                        dataArea.put("superManagerId",
                                            String.valueOf(superManager.getSuperManagerId()));

                                        treeDataArea.setData(dataArea);

                                        for (int n = 0; n < projects.size(); n++) {
                                            Project project = projects.get(n);
                                            if (project.getCustomerId() == customer.getCustomerId()) {
                                                if (project.getAreaId() == area.getAreaId()) {
                                                    SystemTreeData treeDataProject = new SystemTreeData();

                                                    treeDataProject
                                                        .setKey("P-" + project.getProjectId() + "-" + m + "-" + i);
                                                    treeDataProject.setType("project");
                                                    treeDataProject.setIcon("tree-project");
                                                    treeDataProject.setLabel(project.getProjectName());

                                                    Map<String, Object> dataProject = new HashMap<>();
                                                    dataProject.put("position", String.valueOf(m));
                                                    dataProject.put("projectId",
                                                        String.valueOf(project.getProjectId()));
                                                    dataProject.put("areaId", String.valueOf(area.getAreaId()));
                                                    dataProject.put("managerId",
                                                        String.valueOf(manager.getManagerId()));
                                                    dataProject.put("customerId",
                                                        String.valueOf(customer.getCustomerId()));
                                                    dataProject.put("superManagerId",
                                                        String.valueOf(superManager.getSuperManagerId()));

                                                    treeDataProject.setData(dataProject);

                                                    for (int g = 0; g < systemMaps.size(); g++) {
                                                        SystemMap systemMap = systemMaps.get(g);

                                                        if (systemMap.getProjectId()
                                                            .equals(project.getProjectId())) {
                                                            SystemTreeData treeDataSystemMap = new SystemTreeData();

                                                            treeDataSystemMap.setKey(project.getProjectId() + "-S" + "-"
                                                                + g + "-" + n + "-" + m + "-" + i);

                                                            String typeName = "";
                                                            String type = "";
                                                            String icon = "";
                                                            int piority = 0;

                                                            switch (systemMap.getSystemTypeId()) {
                                                                case 1:
                                                                    typeName = "LOAD";
                                                                    type = "load";
                                                                    icon = "tree-load";
                                                                    piority = 1;
                                                                    break;
                                                                case 2:
                                                                    typeName = "SOLAR";
                                                                    type = "solar";
                                                                    icon = "tree-solar";
                                                                    piority = 2;
                                                                    break;
                                                                // CHECKSTYLE:OFF
                                                                case 3:
                                                                    typeName = "WIND";
                                                                    type = "wind";
                                                                    icon = "tree-wind";
                                                                    piority = 3;
                                                                    break;
                                                                case 4:
                                                                    typeName = "BATTERY";
                                                                    type = "battery";
                                                                    icon = "tree-battery";
                                                                    piority = 4;
                                                                    break;
                                                                case 5:
                                                                    typeName = "GRID";
                                                                    type = "grid";
                                                                    icon = "tree-grid";
                                                                    piority = 5;
                                                                    break;
                                                                default:
                                                                    break;
                                                            }

                                                            treeDataSystemMap.setType(type);
                                                            treeDataSystemMap.setIcon(icon);
                                                            treeDataSystemMap.setLabel(typeName);

                                                            Map<String, Object> dataSystemMap = new HashMap<>();
                                                            dataSystemMap.put("position", String.valueOf(g));
                                                            dataSystemMap.put("piority", piority);
                                                            dataSystemMap.put("projectId",
                                                                String.valueOf(project.getProjectId()));
                                                            dataSystemMap.put("systemTypeId",
                                                                String.valueOf(systemMap.getSystemTypeId()));
                                                            dataSystemMap.put("customerId",
                                                                String.valueOf(project.getCustomerId()));
                                                            dataProject.put("areaId", String.valueOf(area.getAreaId()));
                                                            dataProject.put("managerId",
                                                                String.valueOf(manager.getManagerId()));
                                                            dataProject.put("customerId",
                                                                String.valueOf(customer.getCustomerId()));
                                                            dataProject.put("superManagerId",
                                                                String.valueOf(superManager.getSuperManagerId()));

                                                            treeDataSystemMap.setData(dataSystemMap);

                                                            treeDataProject.getChildren()
                                                                .add(treeDataSystemMap);
                                                        }

                                                    }

                                                    treeDataArea.getChildren()
                                                        .add(treeDataProject);
                                                }
                                            }
                                        }
                                        if (treeDataArea.getChildren()
                                            .size() > 0) {
                                            treeDataManager.getChildren()
                                                .add(treeDataArea);
                                        }
                                    }
                                }

                                if (treeDataManager.getChildren()
                                    .size() > 0) {
                                    treeDataSuperManager.getChildren()
                                        .add(treeDataManager);
                                }
                            }
                        }
                        if (treeDataSuperManager.getChildren()
                            .size() > 0) {
                            treeDataCustomer.getChildren()
                                .add(treeDataSuperManager);
                        }
                    }
                    if (treeDataCustomer.getChildren()
                        .size() > 0) {
                        systemTree.add(treeDataCustomer);
                    }
                }
            }
        } else {
            return null;
        }
        return systemTree;
    }

    private List<ProjectLocation> getMarkers(User user) {
        // get user role
        Map<String, String> condition = new HashMap<>();
        condition.put("userId", String.valueOf(user.getId()));
        List<String> roles = this.userRoleService.getRoleNames(condition);
        String userRole = roles.get(0);
        Integer userType = user.getUserType();

        // list projects
        List<Project> projects = new ArrayList<>();

        // list project response to client
        List<ProjectLocation> projectMarkers = new ArrayList<>();

        // if role = ROLE_ADMIN
        if (userRole.equals("ROLE_ADMIN")) {
            projects = this.projectService.getProjectList(null);
        } else {
            if (userType == 2) {
                Integer customerId = user.getCustomerId();
                condition.put("customerId", String.valueOf(customerId));
                System.out.println(condition);
            } else if (userType == 3) {
                Integer superManagerId = user.getSuperManagerId();
                Integer customerId = user.getCustomerId();
                condition.put("customerId", String.valueOf(customerId));
                condition.put("superManagerId", String.valueOf(superManagerId));
            } else if (userType == 4) {
                Integer managerId = user.getManagerId();
                Integer superManagerId = user.getSuperManagerId();
                Integer customerId = user.getCustomerId();
                condition.put("managerId", String.valueOf(managerId));
                condition.put("customerId", String.valueOf(customerId));
                condition.put("superManagerId", String.valueOf(superManagerId));
            } else if (userType == 5) {
                Integer areaId = user.getAreaId();
                Integer managerId = user.getManagerId();
                Integer superManagerId = user.getSuperManagerId();
                Integer customerId = user.getCustomerId();
                condition.put("managerId", String.valueOf(managerId));
                condition.put("customerId", String.valueOf(customerId));
                condition.put("superManagerId", String.valueOf(superManagerId));
                condition.put("areaId", String.valueOf(areaId));
            } else if (userType == 6) {
                Integer projectId = user.getProjectId();
                Integer areaId = user.getAreaId();
                Integer managerId = user.getManagerId();
                Integer superManagerId = user.getSuperManagerId();
                Integer customerId = user.getCustomerId();
                condition.put("managerId", String.valueOf(managerId));
                condition.put("customerId", String.valueOf(customerId));
                condition.put("superManagerId", String.valueOf(superManagerId));
                condition.put("areaId", String.valueOf(areaId));
                condition.put("projectId", String.valueOf(projectId));
            }
            projects = this.projectService.getProjectList(condition);
        }
        // get project marker to response
        for (Project project : projects) {
            ProjectLocation projectMarker = new ProjectLocation();
            BeanUtils.copyProperties(project, projectMarker);
            projectMarkers.add(projectMarker);
        }
        return projectMarkers;
    }

    /**
     * Lấy Project Marker.
     *
     * @param usernameLogin Username của tài khoản đang đăng nhập
     * @return Danh sách Project Marker
     */
    @GetMapping ("/{usernameLogin}/project-marker")
    public ResponseEntity<?> getProjectMarker(@PathVariable ("usernameLogin") final String usernameLogin) {
        User user = this.userService.getUserByUsername(usernameLogin);
        if (user != null) {

            // get user role
            Map<String, String> condition = new HashMap<>();
            condition.put("userId", String.valueOf(user.getId()));
            List<String> roles = this.userRoleService.getRoleNames(condition);
            String userRole = roles.get(0);
            Integer userType = user.getUserType();

            // list projects
            List<Project> projects = new ArrayList<>();

            // list project response to client
            List<ProjectLocation> projectMarkers = new ArrayList<>();

            // if role = ROLE_ADMIN
            if (userRole.equals("ROLE_ADMIN")) {
                projects = this.projectService.getProjectList(null);
            } else {
                if (userType == 2) {
                    String ids = user.getCustomerIds();
                    condition.put("customerIds", ids);
                } else if (userType == 3) {
                    Integer superManagerId = user.getSuperManagerId();
                    String ids = user.getCustomerIds();
                    condition.put("customerIds", ids);
                    condition.put("superManagerId", String.valueOf(superManagerId));
                } else if (userType == 4) {
                    Integer managerId = user.getManagerId();
                    Integer superManagerId = user.getSuperManagerId();
                    String ids = user.getCustomerIds();
                    condition.put("managerId", String.valueOf(managerId));
                    condition.put("customerIds", ids);
                    condition.put("superManagerId", String.valueOf(superManagerId));
                } else if (userType == 5) {
                    Integer areaId = user.getAreaId();
                    Integer managerId = user.getManagerId();
                    Integer superManagerId = user.getSuperManagerId();
                    String ids = user.getCustomerIds();
                    condition.put("managerId", String.valueOf(managerId));
                    condition.put("customerIds", ids);
                    condition.put("superManagerId", String.valueOf(superManagerId));
                    condition.put("areaId", String.valueOf(areaId));
                } else if (userType == 6) {
                    Integer projectId = user.getProjectId();
                    Integer areaId = user.getAreaId();
                    Integer managerId = user.getManagerId();
                    Integer superManagerId = user.getSuperManagerId();
                    String ids = user.getCustomerIds();
                    condition.put("managerId", String.valueOf(managerId));
                    condition.put("customerIds", ids);
                    condition.put("superManagerId", String.valueOf(superManagerId));
                    condition.put("areaId", String.valueOf(areaId));
                    condition.put("projectId", String.valueOf(projectId));
                }
                projects = this.projectService.listProjectByCondition(condition);
            }

            // get project marker to response
            for (Project project : projects) {
                ProjectLocation projectMarker = new ProjectLocation();
                BeanUtils.copyProperties(project, projectMarker);
                projectMarkers.add(projectMarker);
            }

            Calendar currentTime = Calendar.getInstance();
            int minute = currentTime.get(Calendar.MINUTE);
            // CHECKSTYLE:OFF
            minute = ( (minute / 5) - 1) * 5 - 10;
            if (minute >= 60) {
                currentTime.add(Calendar.HOUR_OF_DAY, 1);
                currentTime.set(Calendar.MINUTE, 0);
            } else {
                currentTime.set(Calendar.MINUTE, minute);
            }

            // CHECKSTYLE:ON

            for (int i = 0; i < projectMarkers.size(); i++) {
                Map<String, String> conditions = new HashMap<>();
                conditions.put("projectId", String.valueOf(projectMarkers.get(i)
                    .getProjectId()));
                conditions.put("customerId", String.valueOf(projectMarkers.get(i)
                    .getCustomerId()));
                conditions.put("systemTypeId", String.valueOf(1));
                String[] deviceIds = deviceService.getDeviceIdByProjectIdAndSystemTypeId(conditions);
                if (deviceIds.length == 0) {
                    conditions.put("deviceId", "0");
                } else {
                    String deviceId = String.join(",", deviceIds);
                    conditions.put("deviceId", deviceId);
                }

                String dateNow = DateUtils.toString(new Date(), Constants.ES.DATETIME_FORMAT_YMDHMS);
                SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String fifteenDate = dateFormatWithTime.format(currentTime.getTime());

                String today = DateUtils.toString(new Date(), Constants.ES.DATE_FORMAT_YMD);
                String schema = Schema.getSchemas(projectMarkers.get(i)
                    .getCustomerId());
                conditions.put("today", fifteenDate);
                conditions.put("currentTime", dateNow);
                conditions.put("schema", schema);
                DataLoadFrame1 dataLoadFrame = this.dataLoadFrame1Service.getTotalPowerByProjectId(conditions);

                Map<String, String> map = new HashMap<>();
                map.put("projectId", String.valueOf(projectMarkers.get(i)
                    .getProjectId()));
                map.put("customerId", String.valueOf(projectMarkers.get(i)
                    .getCustomerId()));
                map.put("systemTypeId", String.valueOf(2));
                String[] ids = deviceService.getDeviceIdByProjectIdAndSystemTypeId(map);
                if (ids.length == 0) {
                    map.put("deviceId", "0");
                } else {
                    map.put("deviceId", String.join(",", ids));
                }
                map.put("today", fifteenDate);
                map.put("currentTime", dateNow);
                map.put("schema", schema);
                Device dataPvFrame = this.dataInverterService.getTotalPowerByProjectId(map);

                Map<String, String> con = new HashMap<>();
                con.put("projectId", String.valueOf(projectMarkers.get(i)
                    .getProjectId()));
                con.put("customerId", String.valueOf(projectMarkers.get(i)
                    .getCustomerId()));
                con.put("systemTypeId", String.valueOf(5));
                String[] idGrid = deviceService.getDeviceIdByProjectIdAndSystemTypeId(con);
                if (idGrid.length == 0) {
                    con.put("deviceId", "0");
                } else {
                    con.put("deviceId", String.join(",", idGrid));
                }
                con.put("today", fifteenDate);
                con.put("currentTime", dateNow);
                con.put("schema", schema);
                DataRmuDrawer1 dataGridFrame = this.dataRmuDrawer1Service.getTotalPowerByProjectId(con);
                // LOAD MODULE
                List<SystemMap> systemMaps = systemMapService.checkSystemMap(conditions);

                boolean checkLoadSystem = false;
                boolean checkPvSystem = false;

                boolean checkGridSystem = false;

                for (SystemMap systemMap : systemMaps) {
                    if (systemMap.getSystemTypeId() == 1) {
                        checkLoadSystem = true;
                    }
                    if (systemMap.getSystemTypeId() == 2) {
                        checkPvSystem = true;
                    }
                    if (systemMap.getSystemTypeId() == 5) {
                        checkGridSystem = true;
                    }
                }

                if (checkLoadSystem) {
                    if (dataLoadFrame != null) {
                        projectMarkers.get(i)
                            .setLoadStatus(Constants.ModuleStatus.ACTIVE);
                        conditions.put("startDate", today + " 00:00:00");
                        conditions.put("endDate", dateNow);
                        Integer totalWarning = warningService.countProjectWarning(conditions);

                        if (totalWarning > 0) {
                            projectMarkers.get(i)
                                .setLoadStatus(Constants.ModuleStatus.WARNING);
                        }

                        if (dataLoadFrame != null) {
                            // CHECKSTYLE:OFF
                            Date lastSendDate = DateUtils.toDate(dataLoadFrame.getSentDate(),
                                Constants.ES.DATETIME_FORMAT_YMDHMS);
                            Long currentTimes = new Date().getTime();
                            if (currentTimes
                                - (lastSendDate != null ? lastSendDate.getTime() : 0) >= timeActiveModule) {
                                projectMarkers.get(i)
                                    .setLoadStatus(Constants.ModuleStatus.OFFLINE);
                            }
                            // CHECKSTYLE:ON
                        }
                    } else {
                        int count = dataLoadFrame1Service.countCurrentData(conditions);
                        if (count > 0) {
                            projectMarkers.get(i)
                                .setLoadStatus(Constants.ModuleStatus.OFFLINE);
                        } else {
                            projectMarkers.get(i)
                                .setLoadStatus(Constants.ModuleStatus.IN_ACTIVE);
                        }
                    }
                } else {
                    projectMarkers.get(i)
                        .setLoadStatus(Constants.ModuleStatus.IN_ACTIVE);
                }

                if (checkPvSystem) {
                    if (dataPvFrame != null) {
                        projectMarkers.get(i)
                            .setPvStatus(Constants.ModuleStatus.ACTIVE);
                        map.put("startDate", today + " 00:00:00");
                        map.put("endDate", dateNow);
                        Integer totalWarning = warningService.countProjectWarning(map);

                        if (totalWarning > 0) {
                            projectMarkers.get(i)
                                .setPvStatus(Constants.ModuleStatus.WARNING);
                        }

                    } else {
                        int count = dataInverterService.countCurrentData(conditions);
                        if (count > 0) {
                            projectMarkers.get(i)
                                .setPvStatus(Constants.ModuleStatus.OFFLINE);
                        } else {
                            projectMarkers.get(i)
                                .setPvStatus(Constants.ModuleStatus.IN_ACTIVE);
                        }
                    }
                } else {
                    projectMarkers.get(i)
                        .setPvStatus(Constants.ModuleStatus.IN_ACTIVE);
                }

                if (checkGridSystem) {
                    if (dataGridFrame != null) {
                        projectMarkers.get(i)
                            .setGridStatus(Constants.ModuleStatus.ACTIVE);
                        con.put("startDate", today + " 00:00:00");
                        con.put("endDate", dateNow);
                        Integer totalWarning = warningService.countProjectWarning(con);

                        if (totalWarning > 0) {
                            projectMarkers.get(i)
                                .setGridStatus(Constants.ModuleStatus.WARNING);
                        }

                        if (dataGridFrame != null) {
                            // CHECKSTYLE:OFF
                            Date lastSendDate = DateUtils.toDate(dataGridFrame.getSentDate(),
                                Constants.ES.DATETIME_FORMAT_YMDHMS);
                            Long currentTimes = new Date().getTime();
                            if (currentTimes
                                - (lastSendDate != null ? lastSendDate.getTime() : 0) >= timeActiveModule) {
                                projectMarkers.get(i)
                                    .setGridStatus(Constants.ModuleStatus.OFFLINE);
                            }
                            // CHECKSTYLE:ON
                        }
                    } else {
                        int count = dataRmuDrawer1Service.countCurrentData(conditions);
                        if (count > 0) {
                            projectMarkers.get(i)
                                .setGridStatus(Constants.ModuleStatus.OFFLINE);
                        } else {
                            projectMarkers.get(i)
                                .setGridStatus(Constants.ModuleStatus.IN_ACTIVE);
                        }
                    }
                } else {
                    projectMarkers.get(i)
                        .setGridStatus(Constants.ModuleStatus.IN_ACTIVE);
                }
            }

            for (int i = 0; i < projectMarkers.size(); i++) {
                // CHECKSTYLE:OFF
                if (projectMarkers.get(i)
                    .getLoadStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "warning"
                    && projectMarkers.get(i)
                        .getGridStatus() == "warning") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                    // 1 warning 2 color
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "inactive"
                    && projectMarkers.get(i)
                        .getGridStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getPvStatus() == "warning"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "inactive"
                    && projectMarkers.get(i)
                        .getGridStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getGridStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "inactive"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getPvStatus() == "warning"
                    && projectMarkers.get(i)
                        .getGridStatus() == "offline"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "offline") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getGridStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "offline"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "offline") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "offline"
                    && projectMarkers.get(i)
                        .getGridStatus() == "offline") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "active"
                    && projectMarkers.get(i)
                        .getGridStatus() == "active") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getPvStatus() == "warning"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "active"
                    && projectMarkers.get(i)
                        .getGridStatus() == "active") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getGridStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "active"
                    && projectMarkers.get(i)
                        .getGridStatus() == "active") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                    // 1 warning 3 color
                }// c,x,d
                else if (projectMarkers.get(i)
                    .getLoadStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "active"
                    && projectMarkers.get(i)
                        .getGridStatus() == "offline") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "warning"
                    && projectMarkers.get(i)
                        .getGridStatus() == "active"
                    && projectMarkers.get(i)
                        .getPvStatus() == "offline") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getPvStatus() == "warning"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "active"
                    && projectMarkers.get(i)
                        .getGridStatus() == "offline") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getPvStatus() == "warning"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "offline"
                    && projectMarkers.get(i)
                        .getGridStatus() == "active") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getGridStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "active"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "offline") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                    // c,x,g
                } else if (projectMarkers.get(i)
                    .getGridStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "offline"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "active") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                    // c,x,g
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "active"
                    && projectMarkers.get(i)
                        .getGridStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "warning"
                    && projectMarkers.get(i)
                        .getGridStatus() == "active"
                    && projectMarkers.get(i)
                        .getPvStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getPvStatus() == "warning"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "active"
                    && projectMarkers.get(i)
                        .getGridStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getGridStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "active"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                    // c,d,g
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "offline"
                    && projectMarkers.get(i)
                        .getGridStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "warning"
                    && projectMarkers.get(i)
                        .getGridStatus() == "offline"
                    && projectMarkers.get(i)
                        .getPvStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getPvStatus() == "warning"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "offline"
                    && projectMarkers.get(i)
                        .getGridStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getPvStatus() == "warning"
                    && projectMarkers.get(i)
                        .getGridStatus() == "offline"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getGridStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "offline"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getGridStatus() == "warning"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "offline"
                    && projectMarkers.get(i)
                        .getPvStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                    // 2 warning
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "warning"
                    && projectMarkers.get(i)
                        .getGridStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "warning"
                    && projectMarkers.get(i)
                        .getGridStatus() == "offline") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "warning"
                    && projectMarkers.get(i)
                        .getGridStatus() == "active") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getGridStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "warning"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getGridStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "warning"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "offline") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getGridStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "warning"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "active") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "warning"
                    && projectMarkers.get(i)
                        .getGridStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "warning"
                    && projectMarkers.get(i)
                        .getGridStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "offline") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "warning"
                    && projectMarkers.get(i)
                        .getGridStatus() == "warning"
                    && projectMarkers.get(i)
                        .getPvStatus() == "active") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.WARNING);
                } // 3 active
                else if (projectMarkers.get(i)
                    .getLoadStatus() == "active"
                    && projectMarkers.get(i)
                        .getPvStatus() == "active"
                    && projectMarkers.get(i)
                        .getGridStatus() == "active") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.ON);
                    // 2 active
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "active"
                    && projectMarkers.get(i)
                        .getPvStatus() == "active"
                    && projectMarkers.get(i)
                        .getGridStatus() == "offline") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.ON);
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "active"
                    && projectMarkers.get(i)
                        .getPvStatus() == "active"
                    && projectMarkers.get(i)
                        .getGridStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.ON);
                } else if (projectMarkers.get(i)
                    .getGridStatus() == "active"
                    && projectMarkers.get(i)
                        .getPvStatus() == "active"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "offline") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.ON);
                } else if (projectMarkers.get(i)
                    .getGridStatus() == "active"
                    && projectMarkers.get(i)
                        .getPvStatus() == "active"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.ON);
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "active"
                    && projectMarkers.get(i)
                        .getGridStatus() == "active"
                    && projectMarkers.get(i)
                        .getPvStatus() == "offline") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.ON);
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "active"
                    && projectMarkers.get(i)
                        .getGridStatus() == "active"
                    && projectMarkers.get(i)
                        .getPvStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.ON);
                    // 1 active 1 red
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "active"
                    && projectMarkers.get(i)
                        .getPvStatus() == "offline"
                    && projectMarkers.get(i)
                        .getGridStatus() == "offline") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.ON);
                } else if (projectMarkers.get(i)
                    .getPvStatus() == "active"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "offline"
                    && projectMarkers.get(i)
                        .getGridStatus() == "offline") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.ON);
                } else if (projectMarkers.get(i)
                    .getGridStatus() == "active"
                    && projectMarkers.get(i)
                        .getPvStatus() == "offline"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "offline") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.ON);
                    // 1 active 1 gray
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "active"
                    && projectMarkers.get(i)
                        .getPvStatus() == "inactive"
                    && projectMarkers.get(i)
                        .getGridStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.ON);
                } else if (projectMarkers.get(i)
                    .getPvStatus() == "active"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "inactive"
                    && projectMarkers.get(i)
                        .getGridStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.ON);
                } else if (projectMarkers.get(i)
                    .getGridStatus() == "active"
                    && projectMarkers.get(i)
                        .getPvStatus() == "inactive"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.ON);
                    // 1 active 2 color
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "active"
                    && projectMarkers.get(i)
                        .getPvStatus() == "offline"
                    && projectMarkers.get(i)
                        .getGridStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.ON);
                } else if (projectMarkers.get(i)
                    .getPvStatus() == "active"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "offline"
                    && projectMarkers.get(i)
                        .getGridStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.ON);
                } else if (projectMarkers.get(i)
                    .getGridStatus() == "active"
                    && projectMarkers.get(i)
                        .getPvStatus() == "offline"
                    && projectMarkers.get(i)
                        .getLoadStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.ON);
                } else if (projectMarkers.get(i)
                    .getLoadStatus() == "active"
                    && projectMarkers.get(i)
                        .getGridStatus() == "offline"
                    && projectMarkers.get(i)
                        .getPvStatus() == "inactive") {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.ON);
                } else {
                    projectMarkers.get(i)
                        .setStatusColor(Constants.ModuleStatus.OFF);
                }
                // CHECKSTYLE:ON
            }

            return new ResponseEntity<>(projectMarkers, HttpStatus.OK);
        }
        return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Lấy dữ liệu Project theo projectId.
     *
     * @param projectId Id của Project.
     * @return Dữ liệu của Project.
     * @throws Exception
     */
    @GetMapping ("/project/{customerId}/{projectId}")
    public ResponseEntity<?> getProjectById(@PathVariable ("customerId") final Integer customerId,
        @PathVariable ("projectId") final Integer projectId) throws Exception {
        // get project by projectId
        Map<String, String> condition = new HashMap<>();
        condition.put("projectId", String.valueOf(projectId));
        condition.put("customerId", String.valueOf(customerId));
        Project project = projectService.getProject(condition);

        Calendar currentTime = Calendar.getInstance();
        int minute = currentTime.get(Calendar.MINUTE);
        // CHECKSTYLE:OFF
        minute = ( (minute / 5) - 1) * 5 - 10;
        if (minute >= 60) {
            currentTime.add(Calendar.HOUR_OF_DAY, 1);
            currentTime.set(Calendar.MINUTE, 0);
        } else {
            currentTime.set(Calendar.MINUTE, minute);
        }

        // CHECKSTYLE:ON

        if (project != null) {
            ProjectInfo projectInfo = new ProjectInfo();
            BeanUtils.copyProperties(project, projectInfo);
            projectInfo.setLoadStatus(Constants.ModuleStatus.IN_ACTIVE);

            condition.put("systemTypeId", String.valueOf(1));
            String[] deviceIds = deviceService.getDeviceIdByProjectIdAndSystemTypeId(condition);
            if (deviceIds.length == 0) {
                condition.put("deviceId", "0");
            } else {
                String deviceId = String.join(",", deviceIds);
                condition.put("deviceId", deviceId);
            }

            SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:59");
            String fifteenDate = dateFormatWithTime.format(currentTime.getTime());
            String dateNow = dateFormatWithTime.format(new Date());

            String schema = Schema.getSchemas(customerId);
            condition.put("today", fifteenDate);
            condition.put("currentTime", dateNow);
            condition.put("schema", schema);
            DataLoadFrame1 dataLoadFrame = this.dataLoadFrame1Service.getTotalPowerByProjectId(condition);

            Map<String, String> map = new HashMap<>();
            map.put("projectId", String.valueOf(projectId));
            map.put("customerId", String.valueOf(customerId));
            map.put("systemTypeId", String.valueOf(2));
            String[] ids = deviceService.getDeviceIdByProjectIdAndSystemTypeId(map);
            if (ids.length == 0) {
                map.put("deviceId", "0");
            } else {
                map.put("deviceId", String.join(",", ids));
            }
            map.put("today", fifteenDate);
            map.put("currentTime", dateNow);
            map.put("schema", schema);
            Device dataPvFrame = this.dataInverterService.getTotalPowerByProjectId(map);

            Map<String, String> con = new HashMap<>();
            con.put("projectId", String.valueOf(projectId));
            con.put("customerId", String.valueOf(customerId));
            con.put("systemTypeId", String.valueOf(5));
            String[] idGrid = deviceService.getDeviceIdByProjectIdAndSystemTypeId(con);
            if (idGrid.length == 0) {
                con.put("deviceId", "0");
            } else {
                con.put("deviceId", String.join(",", idGrid));
            }
            con.put("today", fifteenDate);
            con.put("currentTime", dateNow);
            con.put("schema", schema);
            DataRmuDrawer1 dataGridFrame = this.dataRmuDrawer1Service.getTotalPowerByProjectId(con);
            // LOAD MODULE
            List<SystemMap> systemMaps = systemMapService.checkSystemMap(condition);

            boolean checkLoadSystem = false;
            boolean checkPvSystem = false;
            boolean checkWindSystem = false;
            boolean checkBatterySystem = false;
            boolean checkGridSystem = false;

            for (SystemMap systemMap : systemMaps) {
                if (systemMap.getSystemTypeId() == 1) {
                    checkLoadSystem = true;
                }
                if (systemMap.getSystemTypeId() == 2) {
                    checkPvSystem = true;
                }
                if (systemMap.getSystemTypeId() == 3) {
                    checkWindSystem = true;
                }
                if (systemMap.getSystemTypeId() == 4) {
                    checkBatterySystem = true;
                }
                if (systemMap.getSystemTypeId() == 5) {
                    checkGridSystem = true;
                }
            }

            if (checkLoadSystem) {
                if (dataLoadFrame != null) {
                    Integer deviceStatus = warningService.countProjectWarning(condition);

                    if (deviceStatus > 0) {
                        projectInfo.setLoadStatus(Constants.ModuleStatus.WARNING);
                    } else {
                        projectInfo.setLoadStatus(Constants.ModuleStatus.ACTIVE);
                    }

                    if (dataLoadFrame != null) {
                        projectInfo
                            .setLoadPower((double) (dataLoadFrame.getPTotal() != null ? dataLoadFrame.getPTotal() : 0));
                        projectInfo
                            .setCurrentTime(dataLoadFrame.getSentDate() != null ? dataLoadFrame.getSentDate() : null);
                        // CHECKSTYLE:OFF
                        Date lastSendDate = DateUtils.toDate(dataLoadFrame.getSentDate(),
                            Constants.ES.DATETIME_FORMAT_YMDHMS);
                        Long currentTimes = new Date().getTime();
                        if (currentTimes - (lastSendDate != null ? lastSendDate.getTime() : 0) >= timeActiveModule) {
                            projectInfo.setLoadStatus(Constants.ModuleStatus.OFFLINE);
                        }
                        // CHECKSTYLE:ON
                    }
                } else {
                    int count = dataLoadFrame1Service.countCurrentData(condition);
                    if (count > 0) {
                        projectInfo.setLoadStatus(Constants.ModuleStatus.OFFLINE);
                    } else {
                        projectInfo.setLoadStatus(Constants.ModuleStatus.IN_ACTIVE);
                    }
                }
            } else {
                projectInfo.setLoadStatus(Constants.ModuleStatus.IN_ACTIVE);
            }

            if (checkPvSystem) {
                if (dataPvFrame != null) {
                    Integer deviceStatus = warningService.countProjectWarning(map);

                    if (deviceStatus > 0) {
                        projectInfo.setPvStatus(Constants.ModuleStatus.WARNING);
                    } else {
                        projectInfo.setPvStatus(Constants.ModuleStatus.ACTIVE);
                    }

                    if (dataPvFrame != null) {
                        float ptotal = dataPvFrame.getPTotal() != null ? dataPvFrame.getPTotal() : 0;
                        float pCb = dataPvFrame.getPdcCombiner() != null ? dataPvFrame.getPdcCombiner() : 0;
                        float pStr = dataPvFrame.getPdcStr() != null ? dataPvFrame.getPdcStr() : 0;
                        double pvPower = ptotal + pCb + pStr;
                        projectInfo.setPvPower(pvPower);

                    }
                } else {
                    int count = dataInverterService.countCurrentData(condition);
                    if (count > 0) {
                        projectInfo.setPvStatus(Constants.ModuleStatus.OFFLINE);
                    } else {
                        projectInfo.setPvStatus(Constants.ModuleStatus.IN_ACTIVE);
                    }
                }
            } else {
                projectInfo.setPvStatus(Constants.ModuleStatus.IN_ACTIVE);
            }

            if (checkGridSystem) {
                if (dataGridFrame != null) {
                    Integer deviceStatus = warningService.countProjectWarning(con);

                    if (deviceStatus > 0) {
                        projectInfo.setGridStatus(Constants.ModuleStatus.WARNING);
                    } else {
                        projectInfo.setGridStatus(Constants.ModuleStatus.ACTIVE);
                    }

                    if (dataGridFrame != null) {
                        projectInfo.setGridPower(dataGridFrame.getPTotal() != null ? dataGridFrame.getPTotal() : 0);
                        projectInfo
                            .setCurrentTime(dataGridFrame.getSentDate() != null ? dataGridFrame.getSentDate() : null);
                        // CHECKSTYLE:OFF
                        Date lastSendDate = DateUtils.toDate(dataGridFrame.getSentDate(),
                            Constants.ES.DATETIME_FORMAT_YMDHMS);
                        Long currentTimes = new Date().getTime();
                        if (currentTimes - (lastSendDate != null ? lastSendDate.getTime() : 0) >= timeActiveModule) {
                            projectInfo.setGridStatus(Constants.ModuleStatus.OFFLINE);
                        }
                        // CHECKSTYLE:ON
                    }
                } else {
                    int count = dataRmuDrawer1Service.countCurrentData(condition);
                    if (count > 0) {
                        projectInfo.setGridStatus(Constants.ModuleStatus.OFFLINE);
                    } else {
                        projectInfo.setGridStatus(Constants.ModuleStatus.IN_ACTIVE);
                    }
                }
            } else {
                projectInfo.setGridStatus(Constants.ModuleStatus.IN_ACTIVE);
            }

            if (checkWindSystem) {
                if (dataLoadFrame != null) {
                    Integer deviceStatus = warningService.countProjectWarning(condition);

                    if (deviceStatus > 0) {
                        projectInfo.setWindStatus(Constants.ModuleStatus.WARNING);
                    } else {
                        projectInfo.setWindStatus(Constants.ModuleStatus.ACTIVE);
                    }

                    if (dataLoadFrame != null) {
                        projectInfo
                            .setWindPower((double) (dataLoadFrame.getPTotal() != null ? dataLoadFrame.getPTotal() : 0));
                        projectInfo
                            .setCurrentTime(dataLoadFrame.getSentDate() != null ? dataLoadFrame.getSentDate() : null);
                        // CHECKSTYLE:OFF
                        Date lastSendDate = DateUtils.toDate(dataLoadFrame.getSentDate(),
                            Constants.ES.DATETIME_FORMAT_YMDHMS);
                        Long currentTimes = new Date().getTime();
                        if (currentTimes - (lastSendDate != null ? lastSendDate.getTime() : 0) >= timeActiveModule) {
                            projectInfo.setWindStatus(Constants.ModuleStatus.OFFLINE);
                        }
                        // CHECKSTYLE:ON
                    }
                } else {
                    int count = dataLoadFrame1Service.countCurrentData(condition);
                    if (count > 0) {
                        projectInfo.setWindStatus(Constants.ModuleStatus.OFFLINE);
                    } else {
                        projectInfo.setWindStatus(Constants.ModuleStatus.IN_ACTIVE);
                    }
                }
            } else {
                projectInfo.setWindStatus(Constants.ModuleStatus.IN_ACTIVE);
            }

            if (checkBatterySystem) {
                if (dataLoadFrame != null) {
                    Integer deviceStatus = warningService.countProjectWarning(condition);

                    if (deviceStatus > 0) {
                        projectInfo.setBatteryStatus(Constants.ModuleStatus.WARNING);
                    } else {
                        projectInfo.setBatteryStatus(Constants.ModuleStatus.ACTIVE);
                    }

                    if (dataLoadFrame != null) {
                        projectInfo.setBatteryPower(
                            (double) (dataLoadFrame.getPTotal() != null ? dataLoadFrame.getPTotal() : 0));
                        projectInfo
                            .setCurrentTime(dataLoadFrame.getSentDate() != null ? dataLoadFrame.getSentDate() : null);
                        // CHECKSTYLE:OFF
                        Date lastSendDate = DateUtils.toDate(dataLoadFrame.getSentDate(),
                            Constants.ES.DATETIME_FORMAT_YMDHMS);
                        Long currentTimes = new Date().getTime();
                        if (currentTimes - (lastSendDate != null ? lastSendDate.getTime() : 0) >= timeActiveModule) {
                            projectInfo.setLoadStatus(Constants.ModuleStatus.OFFLINE);
                        }
                        // CHECKSTYLE:ON
                    }
                } else {
                    int count = dataLoadFrame1Service.countCurrentData(condition);
                    if (count > 0) {
                        projectInfo.setBatteryStatus(Constants.ModuleStatus.OFFLINE);
                    } else {
                        projectInfo.setBatteryStatus(Constants.ModuleStatus.IN_ACTIVE);
                    }
                }
            } else {
                projectInfo.setBatteryStatus(Constants.ModuleStatus.IN_ACTIVE);
            }
            return new ResponseEntity<>(projectInfo, HttpStatus.OK);
        }
        return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Hiển thị thông tin, tổng dự án và công suất theo khách hàng
     *
     * @param customerId Mã khách hàng
     * @return Danh sách thông tin theo khách hàng
     */
    @GetMapping ("/tree/superManager/{customerId}")
    public ResponseEntity<?> getTreeSuperManager(@PathVariable ("customerId") final Integer customerId) {
        log.info("getTreeSuperManager START");

        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormatWithTime.format(new Date());
        Map<String, String> condition = new HashMap<>();
        condition.put("customerId", customerId.toString());

        List<Project> projects = projectService.getProjectList(condition);
        for (int i = 0; i < projects.size(); i++) {
            Map<String, String> conditions = new HashMap<>();
            conditions.put("projectId", String.valueOf(projects.get(i)
                .getProjectId()));
            String[] deviceIds = deviceService.getDeviceIdByProjectId(conditions);
            String deviceId = String.join(",", deviceIds);
            String[] id = deviceId.trim()
                .replace("[", "")
                .replace("]", "")
                .split(",");
            if (deviceIds.length == 0) {
                projects.get(i)
                    .setCspTotal((long) 0);
                projects.get(i)
                    .setLoadValue((long) 0);
                projects.get(i)
                    .setSolarTotal((long) 0);
                projects.get(i)
                    .setPvValue((long) 0);
            } else {
                String schema = Schema.getSchemas(customerId);
                List<DataLoadFrame1> dataLoad = dataLoadFrame1Service.getTotalPower(schema, id, today);
                ProjectTreeTotalPower ptp = new ProjectTreeTotalPower();
                dataLoad.forEach(loadFrame -> {
                    double totalLoad = ptp.getTotalPower() != null ? ptp.getTotalPower() : 0;
                    double totalEp = ptp.getTotalEp() != null ? ptp.getTotalEp() : 0;
                    totalLoad += loadFrame.getPTotal();
                    totalEp += loadFrame.getEpTotal();
                    ptp.setTotalPower((long) totalLoad);
                    ptp.setTotalEp((long) totalEp);
                });
                // CHECKSTYLE:OFF
                projects.get(i)
                    .setCspTotal(ptp.getTotalPower() != null ? ptp.getTotalPower() : 0);
                projects.get(i)
                    .setLoadValue(ptp.getTotalEp() != null ? ptp.getTotalEp() : 0);
                // CHECKSTYLE:ON

                List<DataInverter1> dataPv = dataInverterService.getTotalPowerPV(schema, id, today);
                dataPv.forEach(pvFrame -> {
                    double currentPv = ptp.getW() != null ? ptp.getW() : 0;
                    double totalWh = ptp.getWh() != null ? ptp.getWh() : 0;
                    currentPv += pvFrame.getPtotal();
                    totalWh += pvFrame.getEp();
                    ptp.setW((long) currentPv);
                    ptp.setWh((long) totalWh);
                });
                // CHECKSTYLE:OFF
                projects.get(i)
                    .setPvValue(ptp.getW() != null ? ptp.getW() : 0);
                projects.get(i)
                    .setSolarTotal(ptp.getWh() != null ? ptp.getWh() : 0);
                // CHECKSTYLE:ON

                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getTotalPowerGrid(schema, id, today);
                dataGrid.forEach(gridFrame -> {
                    double currentGrid = ptp.getPowerGrid() != null ? ptp.getPowerGrid() : 0;
                    double totalEpGrid = ptp.getEpGrid() != null ? ptp.getEpGrid() : 0;
                    currentGrid += gridFrame.getPTotal();
                    totalEpGrid += gridFrame.getEp();
                    ptp.setPowerGrid((long) currentGrid);
                    ptp.setEpGrid((long) totalEpGrid);
                });
                // CHECKSTYLE:OFF
                projects.get(i)
                    .setGridTotal(ptp.getPowerGrid() == null ? 0 : ptp.getPowerGrid());
                projects.get(i)
                    .setGridEp(ptp.getEpGrid() == null ? 0 : ptp.getEpGrid());
                // CHECKSTYLE:ON
            }
        }
        log.info("getTreeSuperManager END");
        return new ResponseEntity<List<Project>>(projects, HttpStatus.OK);

    }

    /**
     * Hiển thị thông tin, tổng dự án và công suất theo khu vực
     *
     * @param customerId Mã khách hàng
     * @param superManagerId Mã khu vực
     * @return Danh sách thông tin theo khu vực
     */
    @GetMapping ("/tree/manager/{customerId}/{superManagerId}")
    public ResponseEntity<List<Project>> getTreeManager(@PathVariable ("customerId") final Integer customerId,
        @PathVariable ("superManagerId") final Integer superManagerId) {

        log.info("getTreeManager START");
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormatWithTime.format(new Date());

        Map<String, String> condition = new HashMap<String, String>();
        System.out.println(customerId.toString());
        condition.put("customerId", customerId.toString());
        condition.put("superManagerId", superManagerId.toString());
        // get project infor
        Map<String, String> superManagerTree = superManagerService.getInformationSuperManager(condition);
        String superManagerInfor = superManagerTree.get("customerName") + " / "
            + superManagerTree.get("superManagerName");
        List<Project> projects = projectService.getProjectList(condition);

        for (int i = 0; i < projects.size(); i++) {
            projects.get(i)
                .setInfoProject(superManagerInfor);
            Map<String, String> conditions = new HashMap<>();
            conditions.put("projectId", String.valueOf(projects.get(i)
                .getProjectId()));
            String[] deviceIds = deviceService.getDeviceIdByProjectId(conditions);
            String deviceId = String.join(",", deviceIds);
            String[] id = deviceId.trim()
                .replace("[", "")
                .replace("]", "")
                .split(",");
            if (deviceIds.length == 0) {
                projects.get(i)
                    .setCspTotal((long) 0);
                projects.get(i)
                    .setLoadValue((long) 0);
                projects.get(i)
                    .setSolarTotal((long) 0);
                projects.get(i)
                    .setPvValue((long) 0);
            } else {
                String schema = Schema.getSchemas(customerId);
                List<DataLoadFrame1> dataLoad = dataLoadFrame1Service.getTotalPower(schema, id, today);
                ProjectTreeTotalPower ptp = new ProjectTreeTotalPower();
                dataLoad.forEach(loadFrame -> {
                    double totalLoad = ptp.getTotalPower() != null ? ptp.getTotalPower() : 0;
                    double totalEp = ptp.getTotalEp() != null ? ptp.getTotalEp() : 0;
                    totalLoad += loadFrame.getPTotal();
                    totalEp += loadFrame.getEpTotal();
                    ptp.setTotalPower((long) totalLoad);
                    ptp.setTotalEp((long) totalEp);
                });
                // CHECKSTYLE:OFF
                projects.get(i)
                    .setCspTotal(ptp.getTotalPower() != null ? ptp.getTotalPower() : 0);
                projects.get(i)
                    .setLoadValue(ptp.getTotalEp() != null ? ptp.getTotalEp() : 0);
                // CHECKSTYLE:ON

                List<DataInverter1> dataPv = dataInverterService.getTotalPowerPV(schema, id, today);
                dataPv.forEach(pvFrame -> {
                    double currentPv = ptp.getW() != null ? ptp.getW() : 0;
                    double totalWh = ptp.getWh() != null ? ptp.getWh() : 0;
                    currentPv += pvFrame.getPtotal();
                    totalWh += pvFrame.getEp();
                    ptp.setW((long) currentPv);
                    ptp.setWh((long) totalWh);
                });
                // CHECKSTYLE:OFF
                projects.get(i)
                    .setPvValue(ptp.getW() != null ? ptp.getW() : 0);
                projects.get(i)
                    .setSolarTotal(ptp.getWh() != null ? ptp.getWh() : 0);
                // CHECKSTYLE:ON

                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getTotalPowerGrid(schema, id, today);
                dataGrid.forEach(gridFrame -> {
                    double currentGrid = ptp.getPowerGrid() != null ? ptp.getPowerGrid() : 0;
                    double totalEpGrid = ptp.getEpGrid() != null ? ptp.getEpGrid() : 0;
                    currentGrid += gridFrame.getPTotal();
                    totalEpGrid += gridFrame.getEp();
                    ptp.setPowerGrid((long) currentGrid);
                    ptp.setEpGrid((long) totalEpGrid);
                });
                // CHECKSTYLE:OFF
                projects.get(i)
                    .setGridTotal(ptp.getPowerGrid() != null ? ptp.getPowerGrid() : 0);
                projects.get(i)
                    .setGridEp(ptp.getEpGrid() != null ? ptp.getEpGrid() : 0);
                // CHECKSTYLE:ON
            }
        }

        log.info("getTreeManager END");
        return new ResponseEntity<List<Project>>(projects, HttpStatus.OK);
    }

    /**
     * Lấy ra thông tin, tổng dự án và công suất theo quận huyện, tỉnh thành, khu vực , khách hàng
     *
     * @param customerId Mã khách hàng
     * @param superManagerId Mã khu vực
     * @param managerId Mã tỉnh thành
     * @return Danh sách quận huyện được lấy ra
     */
    @GetMapping ("/tree/area/{customerId}/{superManagerId}/{managerId}")
    public ResponseEntity<List<Project>> getTreeArea(@PathVariable ("customerId") final Integer customerId,
        @PathVariable ("superManagerId") final Integer superManagerId,
        @PathVariable ("managerId") final Integer managerId) {

        log.info("getTreeArea START");
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormatWithTime.format(new Date());

        Map<String, String> condition = new HashMap<String, String>();
        condition.put("customerId", customerId.toString());
        condition.put("superManagerId", superManagerId.toString());
        condition.put("managerId", managerId.toString());
        // get project infor
        Map<String, String> managerTree = managerService.getInformationManager(condition);
        String managerInfor = managerTree.get("customerName") + " / " + managerTree.get("superManagerName") + " / "
            + managerTree.get("managerName");
        List<Project> projects = projectService.getProjectList(condition);

        for (int i = 0; i < projects.size(); i++) {
            projects.get(i)
                .setInfoProject(managerInfor);
            Map<String, String> conditions = new HashMap<>();
            conditions.put("projectId", String.valueOf(projects.get(i)
                .getProjectId()));
            String[] deviceIds = deviceService.getDeviceIdByProjectId(conditions);
            String deviceId = String.join(",", deviceIds);
            String[] id = deviceId.trim()
                .replace("[", "")
                .replace("]", "")
                .split(",");
            if (deviceIds.length == 0) {
                projects.get(i)
                    .setCspTotal((long) 0);
                projects.get(i)
                    .setLoadValue((long) 0);
                projects.get(i)
                    .setSolarTotal((long) 0);
                projects.get(i)
                    .setPvValue((long) 0);
            } else {
                String schema = Schema.getSchemas(customerId);
                List<DataLoadFrame1> dataLoad = dataLoadFrame1Service.getTotalPower(schema, id, today);
                ProjectTreeTotalPower ptp = new ProjectTreeTotalPower();
                dataLoad.forEach(loadFrame -> {
                    double totalLoad = ptp.getTotalPower() != null ? ptp.getTotalPower() : 0;
                    double totalEp = ptp.getTotalEp() != null ? ptp.getTotalEp() : 0;
                    totalLoad += loadFrame.getPTotal();
                    totalEp += loadFrame.getEpTotal();
                    ptp.setTotalPower((long) totalLoad);
                    ptp.setTotalEp((long) totalEp);
                });
                // CHECKSTYLE:OFF
                projects.get(i)
                    .setCspTotal(ptp.getTotalPower() != null ? ptp.getTotalPower() : 0);
                projects.get(i)
                    .setLoadValue(ptp.getTotalEp() != null ? ptp.getTotalEp() : 0);
                // CHECKSTYLE:ON

                List<DataInverter1> dataPv = dataInverterService.getTotalPowerPV(schema, id, today);
                dataPv.forEach(pvFrame -> {
                    double currentPv = ptp.getW() != null ? ptp.getW() : 0;
                    double totalWh = ptp.getWh() != null ? ptp.getWh() : 0;
                    currentPv += pvFrame.getPtotal();
                    totalWh += pvFrame.getEp();
                    ptp.setW((long) currentPv);
                    ptp.setWh((long) totalWh);
                });
                // CHECKSTYLE:OFF
                projects.get(i)
                    .setPvValue(ptp.getW() != null ? ptp.getW() : 0);
                projects.get(i)
                    .setSolarTotal(ptp.getWh() != null ? ptp.getWh() : 0);
                // CHECKSTYLE:ON

                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getTotalPowerGrid(schema, id, today);
                dataGrid.forEach(gridFrame -> {
                    double currentGrid = ptp.getPowerGrid() != null ? ptp.getPowerGrid() : 0;
                    double totalEpGrid = ptp.getEpGrid() != null ? ptp.getEpGrid() : 0;
                    currentGrid += gridFrame.getPTotal();
                    totalEpGrid += gridFrame.getEp();
                    ptp.setPowerGrid((long) currentGrid);
                    ptp.setEpGrid((long) totalEpGrid);
                });
                // CHECKSTYLE:OFF
                projects.get(i)
                    .setGridTotal(ptp.getPowerGrid() != null ? ptp.getPowerGrid() : 0);
                projects.get(i)
                    .setGridEp(ptp.getEpGrid() != null ? ptp.getEpGrid() : 0);
                // CHECKSTYLE:ON
            }
        }

        log.info("getTreeArea END");
        return new ResponseEntity<List<Project>>(projects, HttpStatus.OK);
    }

    /**
     * Lấy ra danh sách dự án theo quận huyện tỉnh thành khu vực và khách hàng
     *
     * @param customerId Mã khách hàng
     * @param superManagerId Mã khu vực
     * @param managerId Mã tỉnh thành
     * @param areaId Mã quận huyện
     * @return Danh sách dự án
     */
    @GetMapping ("/tree/project/{customerId}/{superManagerId}/{managerId}/{areaId}")
    public ResponseEntity<List<Project>> getTreeProject(@PathVariable ("customerId") final Integer customerId,
        @PathVariable ("superManagerId") final Integer superManagerId,
        @PathVariable ("managerId") final Integer managerId, @PathVariable ("areaId") final Integer areaId) {

        log.info("getTreeProject START");
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormatWithTime.format(new Date());

        Map<String, String> condition = new HashMap<String, String>();
        condition.put("customerId", customerId.toString());
        condition.put("superManagerId", superManagerId.toString());
        condition.put("managerId", managerId.toString());
        condition.put("areaId", areaId.toString());
        // get project infor
        Map<String, String> areaTree = areaService.getInformationArea(condition);
        String areaInfor = areaTree.get("customerName") + " / " + areaTree.get("superManagerName") + " / "
            + areaTree.get("managerName") + " / " + areaTree.get("areaName");
        List<Project> projectList = projectService.getProjectList(condition);

        for (int i = 0; i < projectList.size(); i++) {
            projectList.get(i)
                .setInfoProject(areaInfor);
            Map<String, String> conditions = new HashMap<>();
            conditions.put("projectId", String.valueOf(projectList.get(i)
                .getProjectId()));
            String[] deviceIds = deviceService.getDeviceIdByProjectId(conditions);
            String deviceId = String.join(",", deviceIds);
            String[] id = deviceId.trim()
                .replace("[", "")
                .replace("]", "")
                .split(",");
            if (deviceIds.length == 0) {
                projectList.get(i)
                    .setCspTotal((long) 0);
                projectList.get(i)
                    .setLoadValue((long) 0);
                projectList.get(i)
                    .setSolarTotal((long) 0);
                projectList.get(i)
                    .setPvValue((long) 0);
            } else {
                String schema = Schema.getSchemas(customerId);
                List<DataLoadFrame1> dataLoad = dataLoadFrame1Service.getTotalPower(schema, id, today);
                ProjectTreeTotalPower ptp = new ProjectTreeTotalPower();
                dataLoad.forEach(loadFrame -> {
                    double totalLoad = ptp.getTotalPower() != null ? ptp.getTotalPower() : 0;
                    double totalEp = ptp.getTotalEp() != null ? ptp.getTotalEp() : 0;
                    totalLoad += loadFrame.getPTotal();
                    totalEp += loadFrame.getEpTotal();
                    ptp.setTotalPower((long) totalLoad);
                    ptp.setTotalEp((long) totalEp);
                });
                // CHECKSTYLE:OFF
                projectList.get(i)
                    .setCspTotal(ptp.getTotalPower() != null ? ptp.getTotalPower() : 0);
                projectList.get(i)
                    .setLoadValue(ptp.getTotalEp() != null ? ptp.getTotalEp() : 0);
                // CHECKSTYLE:ON

                List<DataInverter1> dataPv = dataInverterService.getTotalPowerPV(schema, id, today);
                dataPv.forEach(pvFrame -> {
                    double currentPv = ptp.getW() != null ? ptp.getW() : 0;
                    double totalWh = ptp.getWh() != null ? ptp.getWh() : 0;
                    currentPv += pvFrame.getPtotal();
                    totalWh += pvFrame.getEp();
                    ptp.setW((long) currentPv);
                    ptp.setWh((long) totalWh);
                });
                // CHECKSTYLE:OFF
                projectList.get(i)
                    .setPvValue(ptp.getW() != null ? ptp.getW() : 0);
                projectList.get(i)
                    .setSolarTotal(ptp.getWh() != null ? ptp.getWh() : 0);
                // CHECKSTYLE:ON

                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getTotalPowerGrid(schema, id, today);
                dataGrid.forEach(gridFrame -> {
                    double currentGrid = ptp.getPowerGrid() != null ? ptp.getPowerGrid() : 0;
                    double totalEpGrid = ptp.getEpGrid() != null ? ptp.getEpGrid() : 0;
                    currentGrid += gridFrame.getPTotal();
                    totalEpGrid += gridFrame.getEp();
                    ptp.setPowerGrid((long) currentGrid);
                    ptp.setEpGrid((long) totalEpGrid);
                });
                // CHECKSTYLE:OFF
                projectList.get(i)
                    .setGridTotal(ptp.getPowerGrid() != null ? ptp.getPowerGrid() : 0);
                projectList.get(i)
                    .setGridEp(ptp.getEpGrid() != null ? ptp.getEpGrid() : 0);
                // CHECKSTYLE:ON
            }
        }

        log.info("getTreeProject END");
        return new ResponseEntity<List<Project>>(projectList, HttpStatus.OK);
    }

    /**
     * Lấy ra thông tin khách hàng tổng dự án và công suất
     *
     * @return Danh sách thông tin khách hàng
     */
    @GetMapping ("/tree/customer")
    public ResponseEntity<List<Project>> getTreeCustomer() {
        log.info("getTreeCustomer START");

        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormatWithTime.format(new Date());

        List<Project> projectList = projectService.getProjectList(null);
        for (int i = 0; i < projectList.size(); i++) {
            Map<String, String> condition = new HashMap<>();
            condition.put("projectId", String.valueOf(projectList.get(i)
                .getProjectId()));
            String[] deviceIds = deviceService.getDeviceIdByProjectId(condition);
            String deviceId = String.join(",", deviceIds);
            String[] id = deviceId.trim()
                .replace("[", "")
                .replace("]", "")
                .split(",");

            if (deviceIds.length == 0) {
                projectList.get(i)
                    .setCspTotal((long) 0);
                projectList.get(i)
                    .setLoadValue((long) 0);
                projectList.get(i)
                    .setSolarTotal((long) 0);
                projectList.get(i)
                    .setPvValue((long) 0);
            } else {
                String schema = Schema.getSchemas(projectList.get(i)
                    .getCustomerId());
                List<DataLoadFrame1> dataLoad = dataLoadFrame1Service.getTotalPower(schema, id, today);
                ProjectTreeTotalPower ptp = new ProjectTreeTotalPower();
                dataLoad.forEach(loadFrame -> {
                    double totalLoad = ptp.getTotalPower() != null ? ptp.getTotalPower() : 0;
                    double totalEp = ptp.getTotalEp() != null ? ptp.getTotalEp() : 0;
                    totalLoad += loadFrame.getPTotal();
                    totalEp += loadFrame.getEpTotal();
                    ptp.setTotalPower((long) totalLoad);
                    ptp.setTotalEp((long) totalEp);
                });
                // CHECKSTYLE:OFF
                projectList.get(i)
                    .setCspTotal(ptp.getTotalPower() != null ? ptp.getTotalPower() : 0);
                projectList.get(i)
                    .setLoadValue(ptp.getTotalEp() != null ? ptp.getTotalEp() : 0);
                // CHECKSTYLE:ON

                List<DataInverter1> dataPv = dataInverterService.getTotalPowerPV(schema, id, today);
                dataPv.forEach(pvFrame -> {
                    double currentPv = ptp.getW() != null ? ptp.getW() : 0;
                    double totalWh = ptp.getWh() != null ? ptp.getWh() : 0;
                    currentPv += pvFrame.getPtotal();
                    totalWh += pvFrame.getEp();
                    ptp.setW((long) currentPv);
                    ptp.setWh((long) totalWh);
                });
                // CHECKSTYLE:OFF
                projectList.get(i)
                    .setPvValue(ptp.getW() != null ? ptp.getW() : 0);
                projectList.get(i)
                    .setSolarTotal(ptp.getWh() != null ? ptp.getWh() : 0);
                // CHECKSTYLE:ON

                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getTotalPowerGrid(schema, id, today);
                dataGrid.forEach(gridFrame -> {
                    double currentGrid = ptp.getPowerGrid() != null ? ptp.getPowerGrid() : 0;
                    double totalEpGrid = ptp.getEpGrid() != null ? ptp.getEpGrid() : 0;
                    currentGrid += gridFrame.getPTotal();
                    totalEpGrid += gridFrame.getEp();
                    ptp.setPowerGrid((long) currentGrid);
                    ptp.setEpGrid((long) totalEpGrid);
                });
                // CHECKSTYLE:OFF
                projectList.get(i)
                    .setGridTotal(ptp.getPowerGrid() != null ? ptp.getPowerGrid() : 0);
                projectList.get(i)
                    .setGridEp(ptp.getEpGrid() != null ? ptp.getEpGrid() : 0);
                // CHECKSTYLE:ON
            }
        }

        log.info("getTreeCustomer END");
        return new ResponseEntity<List<Project>>(projectList, HttpStatus.OK);
    }

    /**
     * Lấy ra danh sách thành phần của dự án/ biểu đồ
     *
     * @param customerId Mã khách hàng
     * @param projectId Mã dự án
     * @return Danh sách thành phần
     */
    @GetMapping ("/tree/chart/{customerId}/{projectId}")
    public ResponseEntity<?> getTreeChart(@PathVariable ("customerId") final Integer customerId,
        @PathVariable ("projectId") final Integer projectId) {
        log.info("getTreeChart START");
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormatWithTime.format(new Date());

        // get project infor
        Map<String, Object> cond = new HashMap<>();
        cond.put("schema", Schema.getSchemas(customerId));
        cond.put("projectId", projectId);
        Map<String, String> projectTree = projectService.getInformationProject(cond);
        String projectInfor = projectTree.get("customerName") + " / " + projectTree.get("superManagerName") + " / "
            + projectTree.get("managerName") + " / " + projectTree.get("areaName") + " / "
            + projectTree.get("projectName");

        Map<String, String> condition = new HashMap<>();
        condition.put("customerId", String.valueOf(customerId));
        condition.put("projectId", String.valueOf(projectId));
        List<SystemMap> systemMaps = systemMapService.getSystemMapByCustomerAndProject(condition);
        for (int i = 0; i < systemMaps.size(); i++) {
            systemMaps.get(i)
                .setJsonData(projectInfor);
            Map<String, String> conditions = new HashMap<>();
            conditions.put("projectId", String.valueOf(projectId));
            conditions.put("systemTypeId", String.valueOf(systemMaps.get(i)
                .getSystemTypeId()));
            // CHECKSTYLE:OFF
            systemMaps.get(i)
                .setDeviceNumber(deviceService.getCountDeviceBySystemType(conditions));
            // CHECKSTYLE:ON
            Map<String, String> map = new HashMap<>();
            map.put("projectId", String.valueOf(projectId));
            map.put("systemTypeId", String.valueOf(systemMaps.get(i)
                .getSystemTypeId()));
            String[] deviceIds = deviceService.getDeviceIdByProjectIdAndSystemTypeId(map);
            String deviceId = String.join(",", deviceIds);
            String[] id = deviceId.trim()
                .replace("[", "")
                .replace("]", "")
                .split(",");

            if (deviceIds.length == 0) {
                systemMaps.get(i)
                    .setPowerTotal((long) 0);
                systemMaps.get(i)
                    .setEnergyTotal((long) 0);
            } else {
                String schema = Schema.getSchemas(customerId);
                List<DataLoadFrame1> dataLoad = dataLoadFrame1Service.getTotalPower(schema, id, today);
                List<DataInverter1> dataPv = dataInverterService.getTotalPowerPV(schema, id, today);
                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getTotalPowerGrid(schema, id, today);
                ProjectTreeTotalPower ptp = new ProjectTreeTotalPower();
                if (systemMaps.get(i)
                    .getSystemTypeId() == 1) {

                    dataLoad.forEach(loadFrame -> {
                        double totalLoad = ptp.getTotalPower() != null ? ptp.getTotalPower() : 0;
                        double totalEp = ptp.getTotalEp() != null ? ptp.getTotalEp() : 0;
                        totalLoad += loadFrame.getPTotal();
                        totalEp += loadFrame.getEpTotal();
                        ptp.setTotalPower((long) totalLoad);
                        ptp.setTotalEp((long) totalEp);
                    });

                    // CHECKSTYLE:OFF
                    systemMaps.get(i)
                        .setPowerTotal(ptp.getTotalPower() != null ? ptp.getTotalPower() : 0);
                    systemMaps.get(i)
                        .setEnergyTotal(ptp.getTotalEp() != null ? ptp.getTotalEp() : 0);
                    // CHECKSTYLE:ON
                }
                if (systemMaps.get(i)
                    .getSystemTypeId() == 2) {

                    dataPv.forEach(pvFrame -> {
                        double currentPv = ptp.getW() != null ? ptp.getW() : 0;
                        double totalWh = ptp.getWh() != null ? ptp.getWh() : 0;
                        currentPv += pvFrame.getPtotal();
                        totalWh += pvFrame.getEp();
                        ptp.setW((long) currentPv);
                        ptp.setWh((long) totalWh);
                    });

                    // CHECKSTYLE:OFF
                    systemMaps.get(i)
                        .setPowerTotal(ptp.getW() != null ? ptp.getW() : 0);
                    systemMaps.get(i)
                        .setEnergyTotal(ptp.getWh() != null ? ptp.getWh() : 0);
                }
                if (systemMaps.get(i)
                    .getSystemTypeId() == 3) {
                    dataLoad.forEach(loadFrame -> {
                        double totalLoad = ptp.getTotalPower() != null ? ptp.getTotalPower() : 0;
                        double totalEp = ptp.getTotalEp() != null ? ptp.getTotalEp() : 0;
                        totalLoad += loadFrame.getPTotal();
                        totalEp += loadFrame.getEpTotal();
                        ptp.setTotalPower((long) totalLoad);
                        ptp.setTotalEp((long) totalEp);
                    });

                    // CHECKSTYLE:OFF
                    systemMaps.get(i)
                        .setPowerTotal(ptp.getTotalPower() != null ? ptp.getTotalPower() : 0);
                    systemMaps.get(i)
                        .setEnergyTotal(ptp.getTotalEp() != null ? ptp.getTotalEp() : 0);
                    // CHECKSTYLE:ON
                }
                if (systemMaps.get(i)
                    .getSystemTypeId() == 4) {
                    dataLoad.forEach(loadFrame -> {
                        double totalLoad = ptp.getTotalPower() != null ? ptp.getTotalPower() : 0;
                        double totalEp = ptp.getTotalEp() != null ? ptp.getTotalEp() : 0;
                        totalLoad += loadFrame.getPTotal();
                        totalEp += loadFrame.getEpTotal();
                        ptp.setTotalPower((long) totalLoad);
                        ptp.setTotalEp((long) totalEp);
                    });

                    // CHECKSTYLE:OFF
                    systemMaps.get(i)
                        .setPowerTotal(ptp.getTotalPower() != null ? ptp.getTotalPower() : 0);
                    systemMaps.get(i)
                        .setEnergyTotal(ptp.getTotalEp() != null ? ptp.getTotalEp() : 0);
                    // CHECKSTYLE:ON
                }
                if (systemMaps.get(i)
                    .getSystemTypeId() == 5) {
                    // CHECKSTYLE:ON
                    dataGrid.forEach(gridFrame -> {
                        double currentGrid = ptp.getPowerGrid() != null ? ptp.getPowerGrid() : 0;
                        double totalEpGrid = ptp.getEpGrid() != null ? ptp.getEpGrid() : 0;
                        currentGrid += gridFrame.getPTotal();
                        totalEpGrid += gridFrame.getEp();
                        ptp.setPowerGrid((long) currentGrid);
                        ptp.setEpGrid((long) totalEpGrid);
                    });
                    // CHECKSTYLE:OFF
                    if (ptp.getPowerGrid() >= -60000000 && ptp.getPowerGrid() <= 60000000 && ptp.getEpGrid() >= 0) {
                        systemMaps.get(i)
                            .setPowerTotal(ptp.getPowerGrid() != null ? ptp.getPowerGrid() : 0);
                        systemMaps.get(i)
                            .setEnergyTotal(ptp.getEpGrid() != null ? ptp.getEpGrid() : 0);
                    } else {
                        systemMaps.get(i)
                            .setPowerTotal((long) 0);
                    }
                    // CHECKSTYLE:ON
                }

            }
        }
        log.info("getTreeChart END");
        return new ResponseEntity<List<SystemMap>>(systemMaps, HttpStatus.OK);
    }

    /**
     * Vẽ biểu đồ.
     *
     * @param projectId Mã dự án.
     * @param timeType Kiểu thời gian muốn hiển thị.
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @return Danh sách data chart.
     */
    @GetMapping ("/chart")
    public ResponseEntity<?> drawChart(@RequestParam final Integer customerId, @RequestParam final Integer projectId,
        @RequestParam final Integer timeType, @RequestParam final String fromDate, @RequestParam final String toDate) {
        log.info("drawChart START");
        Map<String, String> condition = new HashMap<>();
        condition.put("projectId", String.valueOf(projectId));
        String[] deviceIds = deviceService.getDeviceIdByProjectId(condition);
        String deviceId = String.join(",", deviceIds);
        if (deviceId.length() != 0) {
            Map<String, String> map = new HashMap<>();
            map.put("schema", Schema.getSchemas(customerId));
            map.put("deviceId", deviceId);
            map.put("viewType", String.valueOf(timeType));
            if (timeType == 1) {
                String from = fromDate;
                String to = toDate;

                map.put("fromDate", from);
                map.put("toDate", to);

                List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);
                List<JsonElectricPower> dataChart = new ArrayList<>();

                List<String> years = new ArrayList<>();

                for (int i = Integer.parseInt(fromDate); i <= Integer.parseInt(toDate); i++) {
                    years.add(String.valueOf(i));
                }

                years.forEach(year -> {
                    int y = Integer.parseInt(year);
                    // CHECKSTYLE:OFF
                    JsonElectricPower json = new JsonElectricPower();
                    for (DataLoadFrame1 item : data) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        if (y == Integer.parseInt(dateTime[0])) {
                            double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                            currentPower += item.getEpTotal();
                            json.setLoad(currentPower);
                            json.setWind(currentPower);
                            json.setEv(currentPower);
                        }
                    }
                    for (DataInverter1 item : dataPv) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        if (y == Integer.parseInt(dateTime[0])) {
                            double currentPv = json.getPv() != null ? json.getPv() : 0;
                            currentPv += item.getEp();
                            json.setPv(currentPv);
                        }
                    }
                    for (DataRmuDrawer1 item : dataGrid) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        if (y == Integer.parseInt(dateTime[0])) {
                            double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                            currentGrid += item.getEpTotal();
                            json.setGrid(currentGrid);
                        }
                    }
                    json.setTime(year);
                    if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                        || json.getWind() != null || json.getEv() != null) {
                        dataChart.add(json);
                    }
                });

                return new ResponseEntity<>(dataChart, HttpStatus.OK);
            } else if (timeType == 2) {
                String from = fromDate;
                String to = toDate;

                map.put("fromDate", from);
                map.put("toDate", to);

                List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

                // CHECKSTYLE:OFF
                int daysInMonth = 28;
                // CHECKSTYLE:ON
                String[] date = fromDate.split(Constants.ES.HYPHEN_CHARACTER);
                YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]));
                daysInMonth = yearMonthObject.lengthOfMonth();

                from = fromDate + Constants.ES.HYPHEN_CHARACTER + "01";
                to = toDate + Constants.ES.HYPHEN_CHARACTER + daysInMonth;

                Calendar beginCalendar = Calendar.getInstance();
                Calendar finishCalendar = Calendar.getInstance();

                Date fDate = DateUtils.toDate(from, "yyyy-MM-dd");
                Date tDate = DateUtils.toDate(to, "yyyy-MM-dd");
                beginCalendar.setTime(fDate);
                finishCalendar.setTime(tDate);

                SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM");

                List<String> months = new ArrayList<>();

                while (beginCalendar.before(finishCalendar)) {
                    // add one month to date per loop
                    String dateMonth = formater.format(beginCalendar.getTime())
                        .toUpperCase();
                    months.add(dateMonth);
                    beginCalendar.add(Calendar.MONTH, 1);
                }

                List<JsonElectricPower> dataChart = new ArrayList<>();

                // CHECKSTYLE:OFF
                months.forEach(m -> {
                    String d[] = m.split(Constants.ES.HYPHEN_CHARACTER);
                    int year = Integer.parseInt(d[0]);
                    int month = Integer.parseInt(d[1]);
                    YearMonth firstYearMonth = YearMonth.of(year, month);
                    JsonElectricPower json = new JsonElectricPower();
                    for (DataLoadFrame1 item : data) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        int yearItem = Integer.parseInt(dateTime[0]);
                        int monthItem = Integer.parseInt(dateTime[1]);
                        YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                        if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                            double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                            currentPower += item.getEpTotal();
                            json.setLoad(currentPower);
                            json.setWind(currentPower);
                            json.setEv(currentPower);
                        }
                    }
                    for (DataInverter1 item : dataPv) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        int yearItem = Integer.parseInt(dateTime[0]);
                        int monthItem = Integer.parseInt(dateTime[1]);
                        YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                        if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                            double currentPv = json.getPv() != null ? json.getPv() : 0;
                            currentPv += item.getEp();
                            json.setLoad(currentPv);
                        }
                    }
                    for (DataRmuDrawer1 item : dataGrid) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        int yearItem = Integer.parseInt(dateTime[0]);
                        int monthItem = Integer.parseInt(dateTime[1]);
                        YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                        if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                            double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                            currentGrid += item.getEpTotal();
                            json.setGrid(currentGrid);
                        }
                    }
                    json.setTime(m);
                    if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                        || json.getWind() != null || json.getEv() != null) {
                        dataChart.add(json);
                    }
                });

                return new ResponseEntity<>(dataChart, HttpStatus.OK);
            } else {
                List<JsonElectricPower> dataChart = new ArrayList<>();
                map.put("fromDate", fromDate);
                map.put("toDate", toDate);

                List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

                LocalDate start = LocalDate.parse(fromDate);
                LocalDate end = LocalDate.parse(toDate);
                List<LocalDate> totalDates = new ArrayList<>();
                while (!start.isAfter(end)) {
                    totalDates.add(start);
                    start = start.plusDays(1);
                }

                totalDates.forEach(date -> {
                    Date d = java.sql.Date.valueOf(date);
                    JsonElectricPower json = new JsonElectricPower();
                    data.forEach(loadFrame -> {
                        // set data chart bar
                        if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                            DateUtils.toDate(loadFrame.getViewTime(), "yyyy-MM-dd"))) {
                            double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                            currentPower += loadFrame.getEpTotal();
                            json.setLoad(currentPower);
                            json.setWind(currentPower);
                            json.setEv(currentPower);
                        }
                    });
                    dataPv.forEach(pvFrame -> {
                        if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                            DateUtils.toDate(pvFrame.getViewTime(), "yyyy-MM-dd"))) {
                            double currentPv = json.getPv() != null ? json.getPv() : 0;
                            currentPv += pvFrame.getEp();
                            json.setPv(currentPv);
                        }
                    });
                    dataGrid.forEach(gridFrame -> {
                        if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                            DateUtils.toDate(gridFrame.getViewTime(), "yyyy-MM-dd"))) {
                            double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                            currentGrid += gridFrame.getEpTotal();
                            json.setGrid(currentGrid);
                        }
                    });
                    json.setTime(DateUtils.toString(d, "yyyy-MM-dd"));
                    if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                        || json.getWind() != null || json.getEv() != null) {
                        dataChart.add(json);
                    }
                });

                log.info("drawChart END");
                return new ResponseEntity<>(dataChart, HttpStatus.OK);
            }
        } else {
            List<JsonElectricPower> data = new ArrayList<>();
            return new ResponseEntity<Object>(data, HttpStatus.OK);
        }
    }

    @GetMapping ("/chartCustomer")
    public ResponseEntity<?> chartCustomer(@RequestParam ("customerId") final Integer customerId,
        @RequestParam final Integer timeType, @RequestParam final String fromDate, @RequestParam final String toDate) {
        log.info("chartCustomer START");
        Map<String, String> condition = new HashMap<>();
        condition.put("customerId", String.valueOf(customerId));
        String[] deviceIds = deviceService.getDeviceIdByCustomerId(condition);
        String deviceId = String.join(",", deviceIds);
        if (deviceId.length() == 0) {
            List<JsonElectricPower> data = new ArrayList<>();
            return new ResponseEntity<Object>(data, HttpStatus.OK);
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("schema", Schema.getSchemas(customerId));
            map.put("deviceId", deviceId);
            map.put("viewType", String.valueOf(timeType));
            if (timeType == 1) {
                String from = fromDate;
                String to = toDate;

                map.put("fromDate", from);
                map.put("toDate", to);

                List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);
                List<JsonElectricPower> dataChart = new ArrayList<>();

                List<String> years = new ArrayList<>();

                for (int i = Integer.parseInt(fromDate); i <= Integer.parseInt(toDate); i++) {
                    years.add(String.valueOf(i));
                }

                years.forEach(year -> {
                    int y = Integer.parseInt(year);
                    // CHECKSTYLE:OFF
                    JsonElectricPower json = new JsonElectricPower();
                    for (DataLoadFrame1 item : data) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        if (y == Integer.parseInt(dateTime[0])) {
                            double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                            currentPower += item.getEpTotal();
                            json.setLoad(currentPower);
                            json.setWind(currentPower);
                            json.setEv(currentPower);
                        }
                    }
                    for (DataInverter1 item : dataPv) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        if (y == Integer.parseInt(dateTime[0])) {
                            double currentPv = json.getPv() != null ? json.getPv() : 0;
                            currentPv += item.getEp();
                            json.setPv(currentPv);
                        }
                    }
                    for (DataRmuDrawer1 item : dataGrid) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        if (y == Integer.parseInt(dateTime[0])) {
                            double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                            currentGrid += item.getEpTotal();
                            json.setGrid(currentGrid);
                        }
                    }
                    json.setTime(year);
                    if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                        || json.getWind() != null || json.getEv() != null) {
                        dataChart.add(json);
                    }
                });

                return new ResponseEntity<>(dataChart, HttpStatus.OK);
            } else if (timeType == 2) {
                String from = fromDate;
                String to = toDate;

                map.put("fromDate", from);
                map.put("toDate", to);

                List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

                // CHECKSTYLE:OFF
                int daysInMonth = 28;
                // CHECKSTYLE:ON
                String[] date = fromDate.split(Constants.ES.HYPHEN_CHARACTER);
                YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]));
                daysInMonth = yearMonthObject.lengthOfMonth();

                from = fromDate + Constants.ES.HYPHEN_CHARACTER + "01";
                to = toDate + Constants.ES.HYPHEN_CHARACTER + daysInMonth;

                Calendar beginCalendar = Calendar.getInstance();
                Calendar finishCalendar = Calendar.getInstance();

                Date fDate = DateUtils.toDate(from, "yyyy-MM-dd");
                Date tDate = DateUtils.toDate(to, "yyyy-MM-dd");
                beginCalendar.setTime(fDate);
                finishCalendar.setTime(tDate);

                SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM");

                List<String> months = new ArrayList<>();

                while (beginCalendar.before(finishCalendar)) {
                    // add one month to date per loop
                    String dateMonth = formater.format(beginCalendar.getTime())
                        .toUpperCase();
                    months.add(dateMonth);
                    beginCalendar.add(Calendar.MONTH, 1);
                }

                List<JsonElectricPower> dataChart = new ArrayList<>();

                // CHECKSTYLE:OFF
                months.forEach(m -> {
                    String d[] = m.split(Constants.ES.HYPHEN_CHARACTER);
                    int year = Integer.parseInt(d[0]);
                    int month = Integer.parseInt(d[1]);
                    YearMonth firstYearMonth = YearMonth.of(year, month);
                    JsonElectricPower json = new JsonElectricPower();
                    for (DataLoadFrame1 item : data) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        int yearItem = Integer.parseInt(dateTime[0]);
                        int monthItem = Integer.parseInt(dateTime[1]);
                        YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                        if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                            double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                            currentPower += item.getEpTotal();
                            json.setLoad(currentPower);
                            json.setWind(currentPower);
                            json.setEv(currentPower);
                        }
                    }
                    for (DataInverter1 item : dataPv) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        int yearItem = Integer.parseInt(dateTime[0]);
                        int monthItem = Integer.parseInt(dateTime[1]);
                        YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                        if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                            double currentPv = json.getPv() != null ? json.getPv() : 0;
                            currentPv += item.getEp();
                            json.setLoad(currentPv);
                        }
                    }
                    for (DataRmuDrawer1 item : dataGrid) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        int yearItem = Integer.parseInt(dateTime[0]);
                        int monthItem = Integer.parseInt(dateTime[1]);
                        YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                        if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                            double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                            currentGrid += item.getEpTotal();
                            json.setGrid(currentGrid);
                        }
                    }
                    json.setTime(m);
                    if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                        || json.getWind() != null || json.getEv() != null) {
                        dataChart.add(json);
                    }
                });

                return new ResponseEntity<>(dataChart, HttpStatus.OK);
            } else {
                List<JsonElectricPower> dataChart = new ArrayList<>();
                map.put("fromDate", fromDate);
                map.put("toDate", toDate);

                List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

                LocalDate start = LocalDate.parse(fromDate);
                LocalDate end = LocalDate.parse(toDate);
                List<LocalDate> totalDates = new ArrayList<>();
                while (!start.isAfter(end)) {
                    totalDates.add(start);
                    start = start.plusDays(1);
                }

                totalDates.forEach(date -> {
                    Date d = java.sql.Date.valueOf(date);
                    JsonElectricPower json = new JsonElectricPower();
                    data.forEach(loadFrame -> {
                        // set data chart bar
                        if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                            DateUtils.toDate(loadFrame.getViewTime(), "yyyy-MM-dd"))) {
                            double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                            currentPower += loadFrame.getEpTotal();
                            json.setLoad(currentPower);
                            json.setWind(currentPower);
                            json.setEv(currentPower);
                        }
                    });
                    dataPv.forEach(pvFrame -> {
                        if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                            DateUtils.toDate(pvFrame.getViewTime(), "yyyy-MM-dd"))) {
                            double currentPv = json.getPv() != null ? json.getPv() : 0;
                            currentPv += pvFrame.getEp();
                            json.setPv(currentPv);
                        }
                    });
                    dataGrid.forEach(gridFrame -> {
                        if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                            DateUtils.toDate(gridFrame.getViewTime(), "yyyy-MM-dd"))) {
                            double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                            currentGrid += gridFrame.getEpTotal();
                            json.setGrid(currentGrid);
                        }
                    });
                    json.setTime(DateUtils.toString(d, "yyyy-MM-dd"));
                    if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                        || json.getWind() != null || json.getEv() != null) {
                        dataChart.add(json);
                    }
                });

                log.info("chartCustomer END");
                return new ResponseEntity<>(dataChart, HttpStatus.OK);
            }
        }
    }

    @GetMapping ("/chartSuperManager")
    public ResponseEntity<?> chartSuperManager(@RequestParam ("customerId") final Integer customerId,
        @RequestParam ("superManagerId") final Integer superManagerId, @RequestParam final Integer timeType,
        @RequestParam final String fromDate, @RequestParam final String toDate) {

        log.info("chartSuperManager START");
        Map<String, String> condition = new HashMap<>();
        condition.put("customerId", String.valueOf(customerId));
        condition.put("superManagerId", String.valueOf(superManagerId));
        String[] deviceIds = deviceService.getDeviceBySuperManager(condition);
        String deviceId = String.join(",", deviceIds);
        if (deviceId.length() == 0) {
            List<JsonElectricPower> data = new ArrayList<>();
            return new ResponseEntity<Object>(data, HttpStatus.OK);
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("schema", Schema.getSchemas(customerId));
            map.put("deviceId", deviceId);
            map.put("viewType", String.valueOf(timeType));
            if (timeType == 1) {
                String from = fromDate;
                String to = toDate;

                map.put("fromDate", from);
                map.put("toDate", to);

                List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);
                List<JsonElectricPower> dataChart = new ArrayList<>();

                List<String> years = new ArrayList<>();

                for (int i = Integer.parseInt(fromDate); i <= Integer.parseInt(toDate); i++) {
                    years.add(String.valueOf(i));
                }

                years.forEach(year -> {
                    int y = Integer.parseInt(year);
                    // CHECKSTYLE:OFF
                    JsonElectricPower json = new JsonElectricPower();
                    for (DataLoadFrame1 item : data) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        if (y == Integer.parseInt(dateTime[0])) {
                            double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                            currentPower += item.getEpTotal();
                            json.setLoad(currentPower);
                            json.setWind(currentPower);
                            json.setEv(currentPower);
                        }
                    }
                    for (DataInverter1 item : dataPv) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        if (y == Integer.parseInt(dateTime[0])) {
                            double currentPv = json.getPv() != null ? json.getPv() : 0;
                            currentPv += item.getEp();
                            json.setPv(currentPv);
                        }
                    }
                    for (DataRmuDrawer1 item : dataGrid) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        if (y == Integer.parseInt(dateTime[0])) {
                            double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                            currentGrid += item.getEpTotal();
                            json.setGrid(currentGrid);
                        }
                    }
                    json.setTime(year);
                    if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                        || json.getWind() != null || json.getEv() != null) {
                        dataChart.add(json);
                    }
                });

                return new ResponseEntity<>(dataChart, HttpStatus.OK);
            } else if (timeType == 2) {
                String from = fromDate;
                String to = toDate;

                map.put("fromDate", from);
                map.put("toDate", to);

                List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

                // CHECKSTYLE:OFF
                int daysInMonth = 28;
                // CHECKSTYLE:ON
                String[] date = fromDate.split(Constants.ES.HYPHEN_CHARACTER);
                YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]));
                daysInMonth = yearMonthObject.lengthOfMonth();

                from = fromDate + Constants.ES.HYPHEN_CHARACTER + "01";
                to = toDate + Constants.ES.HYPHEN_CHARACTER + daysInMonth;

                Calendar beginCalendar = Calendar.getInstance();
                Calendar finishCalendar = Calendar.getInstance();

                Date fDate = DateUtils.toDate(from, "yyyy-MM-dd");
                Date tDate = DateUtils.toDate(to, "yyyy-MM-dd");
                beginCalendar.setTime(fDate);
                finishCalendar.setTime(tDate);

                SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM");

                List<String> months = new ArrayList<>();

                while (beginCalendar.before(finishCalendar)) {
                    // add one month to date per loop
                    String dateMonth = formater.format(beginCalendar.getTime())
                        .toUpperCase();
                    months.add(dateMonth);
                    beginCalendar.add(Calendar.MONTH, 1);
                }

                List<JsonElectricPower> dataChart = new ArrayList<>();

                // CHECKSTYLE:OFF
                months.forEach(m -> {
                    String d[] = m.split(Constants.ES.HYPHEN_CHARACTER);
                    int year = Integer.parseInt(d[0]);
                    int month = Integer.parseInt(d[1]);
                    YearMonth firstYearMonth = YearMonth.of(year, month);
                    JsonElectricPower json = new JsonElectricPower();
                    for (DataLoadFrame1 item : data) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        int yearItem = Integer.parseInt(dateTime[0]);
                        int monthItem = Integer.parseInt(dateTime[1]);
                        YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                        if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                            double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                            currentPower += item.getEpTotal();
                            json.setLoad(currentPower);
                            json.setWind(currentPower);
                            json.setEv(currentPower);
                        }
                    }
                    for (DataInverter1 item : dataPv) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        int yearItem = Integer.parseInt(dateTime[0]);
                        int monthItem = Integer.parseInt(dateTime[1]);
                        YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                        if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                            double currentPv = json.getPv() != null ? json.getPv() : 0;
                            currentPv += item.getEp();
                            json.setLoad(currentPv);
                        }
                    }
                    for (DataRmuDrawer1 item : dataGrid) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        int yearItem = Integer.parseInt(dateTime[0]);
                        int monthItem = Integer.parseInt(dateTime[1]);
                        YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                        if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                            double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                            currentGrid += item.getEpTotal();
                            json.setGrid(currentGrid);
                        }
                    }
                    json.setTime(m);
                    if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                        || json.getWind() != null || json.getEv() != null) {
                        dataChart.add(json);
                    }
                });

                return new ResponseEntity<>(dataChart, HttpStatus.OK);
            } else {
                List<JsonElectricPower> dataChart = new ArrayList<>();
                map.put("fromDate", fromDate);
                map.put("toDate", toDate);

                List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

                LocalDate start = LocalDate.parse(fromDate);
                LocalDate end = LocalDate.parse(toDate);
                List<LocalDate> totalDates = new ArrayList<>();
                while (!start.isAfter(end)) {
                    totalDates.add(start);
                    start = start.plusDays(1);
                }

                totalDates.forEach(date -> {
                    Date d = java.sql.Date.valueOf(date);
                    JsonElectricPower json = new JsonElectricPower();
                    data.forEach(loadFrame -> {
                        // set data chart bar
                        if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                            DateUtils.toDate(loadFrame.getViewTime(), "yyyy-MM-dd"))) {
                            double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                            currentPower += loadFrame.getEpTotal();
                            json.setLoad(currentPower);
                            json.setWind(currentPower);
                            json.setEv(currentPower);
                        }
                    });
                    dataPv.forEach(pvFrame -> {
                        if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                            DateUtils.toDate(pvFrame.getViewTime(), "yyyy-MM-dd"))) {
                            double currentPv = json.getPv() != null ? json.getPv() : 0;
                            currentPv += pvFrame.getEp();
                            json.setPv(currentPv);
                        }
                    });
                    dataGrid.forEach(gridFrame -> {
                        if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                            DateUtils.toDate(gridFrame.getViewTime(), "yyyy-MM-dd"))) {
                            double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                            currentGrid += gridFrame.getEpTotal();
                            json.setGrid(currentGrid);
                        }
                    });
                    json.setTime(DateUtils.toString(d, "yyyy-MM-dd"));
                    if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                        || json.getWind() != null || json.getEv() != null) {
                        dataChart.add(json);
                    }
                });

                log.info("chartSuperManager END");
                return new ResponseEntity<>(dataChart, HttpStatus.OK);
            }
        }

    }

    @GetMapping ("/chartManager")
    public ResponseEntity<?> chartManager(@RequestParam ("customerId") final Integer customerId,
        @RequestParam ("superManagerId") final Integer superManagerId,
        @RequestParam ("managerId") final Integer managerId, @RequestParam final Integer timeType,
        @RequestParam final String fromDate, @RequestParam final String toDate) {
        log.info("chartManager START");
        Map<String, String> condition = new HashMap<>();
        condition.put("customerId", String.valueOf(customerId));
        condition.put("superManagerId", String.valueOf(superManagerId));
        condition.put("managerId", String.valueOf(managerId));
        String[] deviceIds = deviceService.getDeviceByManager(condition);
        String deviceId = String.join(",", deviceIds);
        if (deviceId.length() == 0) {
            List<JsonElectricPower> data = new ArrayList<>();
            return new ResponseEntity<Object>(data, HttpStatus.OK);
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("schema", Schema.getSchemas(customerId));
            map.put("deviceId", deviceId);
            map.put("viewType", String.valueOf(timeType));
            if (timeType == 1) {
                String from = fromDate;
                String to = toDate;

                map.put("fromDate", from);
                map.put("toDate", to);

                List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);
                List<JsonElectricPower> dataChart = new ArrayList<>();

                List<String> years = new ArrayList<>();

                for (int i = Integer.parseInt(fromDate); i <= Integer.parseInt(toDate); i++) {
                    years.add(String.valueOf(i));
                }

                years.forEach(year -> {
                    int y = Integer.parseInt(year);
                    // CHECKSTYLE:OFF
                    JsonElectricPower json = new JsonElectricPower();
                    for (DataLoadFrame1 item : data) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        if (y == Integer.parseInt(dateTime[0])) {
                            double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                            currentPower += item.getEpTotal();
                            json.setLoad(currentPower);
                            json.setWind(currentPower);
                            json.setEv(currentPower);
                        }
                    }
                    for (DataInverter1 item : dataPv) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        if (y == Integer.parseInt(dateTime[0])) {
                            double currentPv = json.getPv() != null ? json.getPv() : 0;
                            currentPv += item.getEp();
                            json.setPv(currentPv);
                        }
                    }
                    for (DataRmuDrawer1 item : dataGrid) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        if (y == Integer.parseInt(dateTime[0])) {
                            double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                            currentGrid += item.getEpTotal();
                            json.setGrid(currentGrid);
                        }
                    }
                    json.setTime(year);
                    if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                        || json.getWind() != null || json.getEv() != null) {
                        dataChart.add(json);
                    }
                });

                return new ResponseEntity<>(dataChart, HttpStatus.OK);
            } else if (timeType == 2) {
                String from = fromDate;
                String to = toDate;

                map.put("fromDate", from);
                map.put("toDate", to);

                List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

                // CHECKSTYLE:OFF
                int daysInMonth = 28;
                // CHECKSTYLE:ON
                String[] date = fromDate.split(Constants.ES.HYPHEN_CHARACTER);
                YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]));
                daysInMonth = yearMonthObject.lengthOfMonth();

                from = fromDate + Constants.ES.HYPHEN_CHARACTER + "01";
                to = toDate + Constants.ES.HYPHEN_CHARACTER + daysInMonth;

                Calendar beginCalendar = Calendar.getInstance();
                Calendar finishCalendar = Calendar.getInstance();

                Date fDate = DateUtils.toDate(from, "yyyy-MM-dd");
                Date tDate = DateUtils.toDate(to, "yyyy-MM-dd");
                beginCalendar.setTime(fDate);
                finishCalendar.setTime(tDate);

                SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM");

                List<String> months = new ArrayList<>();

                while (beginCalendar.before(finishCalendar)) {
                    // add one month to date per loop
                    String dateMonth = formater.format(beginCalendar.getTime())
                        .toUpperCase();
                    months.add(dateMonth);
                    beginCalendar.add(Calendar.MONTH, 1);
                }

                List<JsonElectricPower> dataChart = new ArrayList<>();

                // CHECKSTYLE:OFF
                months.forEach(m -> {
                    String d[] = m.split(Constants.ES.HYPHEN_CHARACTER);
                    int year = Integer.parseInt(d[0]);
                    int month = Integer.parseInt(d[1]);
                    YearMonth firstYearMonth = YearMonth.of(year, month);
                    JsonElectricPower json = new JsonElectricPower();
                    for (DataLoadFrame1 item : data) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        int yearItem = Integer.parseInt(dateTime[0]);
                        int monthItem = Integer.parseInt(dateTime[1]);
                        YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                        if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                            double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                            currentPower += item.getEpTotal();
                            json.setLoad(currentPower);
                            json.setWind(currentPower);
                            json.setEv(currentPower);
                        }
                    }
                    for (DataInverter1 item : dataPv) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        int yearItem = Integer.parseInt(dateTime[0]);
                        int monthItem = Integer.parseInt(dateTime[1]);
                        YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                        if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                            double currentPv = json.getPv() != null ? json.getPv() : 0;
                            currentPv += item.getEp();
                            json.setLoad(currentPv);
                        }
                    }
                    for (DataRmuDrawer1 item : dataGrid) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        int yearItem = Integer.parseInt(dateTime[0]);
                        int monthItem = Integer.parseInt(dateTime[1]);
                        YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                        if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                            double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                            currentGrid += item.getEpTotal();
                            json.setGrid(currentGrid);
                        }
                    }
                    json.setTime(m);
                    if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                        || json.getWind() != null || json.getEv() != null) {
                        dataChart.add(json);
                    }
                });

                return new ResponseEntity<>(dataChart, HttpStatus.OK);
            } else {
                List<JsonElectricPower> dataChart = new ArrayList<>();
                map.put("fromDate", fromDate);
                map.put("toDate", toDate);

                List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

                LocalDate start = LocalDate.parse(fromDate);
                LocalDate end = LocalDate.parse(toDate);
                List<LocalDate> totalDates = new ArrayList<>();
                while (!start.isAfter(end)) {
                    totalDates.add(start);
                    start = start.plusDays(1);
                }

                totalDates.forEach(date -> {
                    Date d = java.sql.Date.valueOf(date);
                    JsonElectricPower json = new JsonElectricPower();
                    data.forEach(loadFrame -> {
                        // set data chart bar
                        if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                            DateUtils.toDate(loadFrame.getViewTime(), "yyyy-MM-dd"))) {
                            double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                            currentPower += loadFrame.getEpTotal();
                            json.setLoad(currentPower);
                            json.setWind(currentPower);
                            json.setEv(currentPower);
                        }
                    });
                    dataPv.forEach(pvFrame -> {
                        if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                            DateUtils.toDate(pvFrame.getViewTime(), "yyyy-MM-dd"))) {
                            double currentPv = json.getPv() != null ? json.getPv() : 0;
                            currentPv += pvFrame.getEp();
                            json.setPv(currentPv);
                        }
                    });
                    dataGrid.forEach(gridFrame -> {
                        if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                            DateUtils.toDate(gridFrame.getViewTime(), "yyyy-MM-dd"))) {
                            double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                            currentGrid += gridFrame.getEpTotal();
                            json.setGrid(currentGrid);
                        }
                    });
                    json.setTime(DateUtils.toString(d, "yyyy-MM-dd"));
                    if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                        || json.getWind() != null || json.getEv() != null) {
                        dataChart.add(json);
                    }
                });

                log.info("chartManager END");
                return new ResponseEntity<>(dataChart, HttpStatus.OK);
            }
        }
    }

    @GetMapping ("/chartArea")
    public ResponseEntity<?> chartArea(@RequestParam ("customerId") final Integer customerId,
        @RequestParam ("superManagerId") final Integer superManagerId,
        @RequestParam ("managerId") final Integer managerId, @RequestParam ("areaId") final Integer areaId,
        @RequestParam final Integer timeType, @RequestParam final String fromDate, @RequestParam final String toDate) {
        log.info("chartArea START");
        Map<String, String> condition = new HashMap<>();
        condition.put("customerId", String.valueOf(customerId));
        condition.put("superManagerId", String.valueOf(superManagerId));
        condition.put("managerId", String.valueOf(managerId));
        condition.put("areaId", String.valueOf(areaId));
        String[] deviceIds = deviceService.getDeviceByArea(condition);
        String deviceId = String.join(",", deviceIds);
        if (deviceId.length() == 0) {
            List<JsonElectricPower> data = new ArrayList<>();
            return new ResponseEntity<Object>(data, HttpStatus.OK);
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("schema", Schema.getSchemas(customerId));
            map.put("deviceId", deviceId);
            map.put("viewType", String.valueOf(timeType));
            if (timeType == 1) {
                String from = fromDate;
                String to = toDate;

                map.put("fromDate", from);
                map.put("toDate", to);

                List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);
                List<JsonElectricPower> dataChart = new ArrayList<>();

                List<String> years = new ArrayList<>();

                for (int i = Integer.parseInt(fromDate); i <= Integer.parseInt(toDate); i++) {
                    years.add(String.valueOf(i));
                }

                years.forEach(year -> {
                    int y = Integer.parseInt(year);
                    // CHECKSTYLE:OFF
                    JsonElectricPower json = new JsonElectricPower();
                    for (DataLoadFrame1 item : data) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        if (y == Integer.parseInt(dateTime[0])) {
                            double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                            currentPower += item.getEpTotal();
                            json.setLoad(currentPower);
                            json.setWind(currentPower);
                            json.setEv(currentPower);
                        }
                    }
                    for (DataInverter1 item : dataPv) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        if (y == Integer.parseInt(dateTime[0])) {
                            double currentPv = json.getPv() != null ? json.getPv() : 0;
                            currentPv += item.getEp();
                            json.setPv(currentPv);
                        }
                    }
                    for (DataRmuDrawer1 item : dataGrid) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        if (y == Integer.parseInt(dateTime[0])) {
                            double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                            currentGrid += item.getEpTotal();
                            json.setGrid(currentGrid);
                        }
                    }
                    json.setTime(year);
                    if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                        || json.getWind() != null || json.getEv() != null) {
                        dataChart.add(json);
                    }
                });

                return new ResponseEntity<>(dataChart, HttpStatus.OK);
            } else if (timeType == 2) {
                String from = fromDate;
                String to = toDate;

                map.put("fromDate", from);
                map.put("toDate", to);

                List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

                // CHECKSTYLE:OFF
                int daysInMonth = 28;
                // CHECKSTYLE:ON
                String[] date = fromDate.split(Constants.ES.HYPHEN_CHARACTER);
                YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]));
                daysInMonth = yearMonthObject.lengthOfMonth();

                from = fromDate + Constants.ES.HYPHEN_CHARACTER + "01";
                to = toDate + Constants.ES.HYPHEN_CHARACTER + daysInMonth;

                Calendar beginCalendar = Calendar.getInstance();
                Calendar finishCalendar = Calendar.getInstance();

                Date fDate = DateUtils.toDate(from, "yyyy-MM-dd");
                Date tDate = DateUtils.toDate(to, "yyyy-MM-dd");
                beginCalendar.setTime(fDate);
                finishCalendar.setTime(tDate);

                SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM");

                List<String> months = new ArrayList<>();

                while (beginCalendar.before(finishCalendar)) {
                    // add one month to date per loop
                    String dateMonth = formater.format(beginCalendar.getTime())
                        .toUpperCase();
                    months.add(dateMonth);
                    beginCalendar.add(Calendar.MONTH, 1);
                }

                List<JsonElectricPower> dataChart = new ArrayList<>();

                // CHECKSTYLE:OFF
                months.forEach(m -> {
                    String d[] = m.split(Constants.ES.HYPHEN_CHARACTER);
                    int year = Integer.parseInt(d[0]);
                    int month = Integer.parseInt(d[1]);
                    YearMonth firstYearMonth = YearMonth.of(year, month);
                    JsonElectricPower json = new JsonElectricPower();
                    for (DataLoadFrame1 item : data) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        int yearItem = Integer.parseInt(dateTime[0]);
                        int monthItem = Integer.parseInt(dateTime[1]);
                        YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                        if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                            double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                            currentPower += item.getEpTotal();
                            json.setLoad(currentPower);
                            json.setWind(currentPower);
                            json.setEv(currentPower);
                        }
                    }
                    for (DataInverter1 item : dataPv) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        int yearItem = Integer.parseInt(dateTime[0]);
                        int monthItem = Integer.parseInt(dateTime[1]);
                        YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                        if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                            double currentPv = json.getPv() != null ? json.getPv() : 0;
                            currentPv += item.getEp();
                            json.setLoad(currentPv);
                        }
                    }
                    for (DataRmuDrawer1 item : dataGrid) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        int yearItem = Integer.parseInt(dateTime[0]);
                        int monthItem = Integer.parseInt(dateTime[1]);
                        YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                        if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                            double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                            currentGrid += item.getEpTotal();
                            json.setGrid(currentGrid);
                        }
                    }
                    json.setTime(m);
                    if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                        || json.getWind() != null || json.getEv() != null) {
                        dataChart.add(json);
                    }
                });

                return new ResponseEntity<>(dataChart, HttpStatus.OK);
            } else {
                List<JsonElectricPower> dataChart = new ArrayList<>();
                map.put("fromDate", fromDate);
                map.put("toDate", toDate);

                List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

                LocalDate start = LocalDate.parse(fromDate);
                LocalDate end = LocalDate.parse(toDate);
                List<LocalDate> totalDates = new ArrayList<>();
                while (!start.isAfter(end)) {
                    totalDates.add(start);
                    start = start.plusDays(1);
                }

                totalDates.forEach(date -> {
                    Date d = java.sql.Date.valueOf(date);
                    JsonElectricPower json = new JsonElectricPower();
                    data.forEach(loadFrame -> {
                        // set data chart bar
                        if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                            DateUtils.toDate(loadFrame.getViewTime(), "yyyy-MM-dd"))) {
                            double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                            currentPower += loadFrame.getEpTotal();
                            json.setLoad(currentPower);
                            json.setWind(currentPower);
                            json.setEv(currentPower);
                        }
                    });
                    dataPv.forEach(pvFrame -> {
                        if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                            DateUtils.toDate(pvFrame.getViewTime(), "yyyy-MM-dd"))) {
                            double currentPv = json.getPv() != null ? json.getPv() : 0;
                            currentPv += pvFrame.getEp();
                            json.setPv(currentPv);
                        }
                    });
                    dataGrid.forEach(gridFrame -> {
                        if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                            DateUtils.toDate(gridFrame.getViewTime(), "yyyy-MM-dd"))) {
                            double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                            currentGrid += gridFrame.getEpTotal();
                            json.setGrid(currentGrid);
                        }
                    });
                    json.setTime(DateUtils.toString(d, "yyyy-MM-dd"));
                    if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                        || json.getWind() != null || json.getEv() != null) {
                        dataChart.add(json);
                    }
                });

                log.info("chartArea END");
                return new ResponseEntity<>(dataChart, HttpStatus.OK);
            }
        }
    }

    @GetMapping ("/chartAll")
    public ResponseEntity<?> chartAll(@RequestParam final Integer timeType, @RequestParam final String fromDate,
        @RequestParam final String toDate) {

        log.info("chartAll START");
        String[] deviceIds = deviceService.getAllDeviceByCalculate();
        String deviceId = String.join(",", deviceIds);
        List<JsonElectricPower> dataChart = new ArrayList<>();
        if (deviceId.length() == 0) {
            List<JsonElectricPower> data = new ArrayList<>();
            return new ResponseEntity<Object>(data, HttpStatus.OK);
        } else {
            Map<String, String> map = new HashMap<>();
            List<Customer> customer = customerService.getListCustomer(null);
            for (Customer cus : customer) {
                map.put("schema", Schema.getSchemas(cus.getCustomerId()));
                map.put("deviceId", deviceId);
                map.put("viewType", String.valueOf(timeType));
                if (timeType == 1) {
                    String from = fromDate;
                    String to = toDate;

                    map.put("fromDate", from);
                    map.put("toDate", to);

                    List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                    List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                    List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

                    List<String> years = new ArrayList<>();

                    for (int i = Integer.parseInt(fromDate); i <= Integer.parseInt(toDate); i++) {
                        years.add(String.valueOf(i));
                    }

                    years.forEach(year -> {
                        int y = Integer.parseInt(year);
                        // CHECKSTYLE:OFF
                        JsonElectricPower json = new JsonElectricPower();
                        for (DataLoadFrame1 item : data) {
                            String dateTime[] = item.getViewTime()
                                .split(Constants.ES.HYPHEN_CHARACTER);
                            if (y == Integer.parseInt(dateTime[0])) {
                                double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                                currentPower += item.getEpTotal();
                                json.setLoad(currentPower);
                                json.setWind(currentPower);
                                json.setEv(currentPower);
                            }
                        }
                        for (DataInverter1 item : dataPv) {
                            String dateTime[] = item.getViewTime()
                                .split(Constants.ES.HYPHEN_CHARACTER);
                            if (y == Integer.parseInt(dateTime[0])) {
                                double currentPv = json.getPv() != null ? json.getPv() : 0;
                                currentPv += item.getEp();
                                json.setPv(currentPv);
                            }
                        }
                        for (DataRmuDrawer1 item : dataGrid) {
                            String dateTime[] = item.getViewTime()
                                .split(Constants.ES.HYPHEN_CHARACTER);
                            if (y == Integer.parseInt(dateTime[0])) {
                                double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                                currentGrid += item.getEpTotal();
                                json.setGrid(currentGrid);
                            }
                        }
                        json.setTime(year);
                        if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                            || json.getWind() != null || json.getEv() != null) {
                            dataChart.add(json);
                        }
                    });

                } else if (timeType == 2) {
                    String from = fromDate;
                    String to = toDate;

                    map.put("fromDate", from);
                    map.put("toDate", to);

                    List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                    List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                    List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

                    // CHECKSTYLE:OFF
                    int daysInMonth = 28;
                    // CHECKSTYLE:ON
                    String[] date = fromDate.split(Constants.ES.HYPHEN_CHARACTER);
                    YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]));
                    daysInMonth = yearMonthObject.lengthOfMonth();

                    from = fromDate + Constants.ES.HYPHEN_CHARACTER + "01";
                    to = toDate + Constants.ES.HYPHEN_CHARACTER + daysInMonth;

                    Calendar beginCalendar = Calendar.getInstance();
                    Calendar finishCalendar = Calendar.getInstance();

                    Date fDate = DateUtils.toDate(from, "yyyy-MM-dd");
                    Date tDate = DateUtils.toDate(to, "yyyy-MM-dd");
                    beginCalendar.setTime(fDate);
                    finishCalendar.setTime(tDate);

                    SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM");

                    List<String> months = new ArrayList<>();

                    while (beginCalendar.before(finishCalendar)) {
                        // add one month to date per loop
                        String dateMonth = formater.format(beginCalendar.getTime())
                            .toUpperCase();
                        months.add(dateMonth);
                        beginCalendar.add(Calendar.MONTH, 1);
                    }

                    // CHECKSTYLE:OFF
                    months.forEach(m -> {
                        String d[] = m.split(Constants.ES.HYPHEN_CHARACTER);
                        int year = Integer.parseInt(d[0]);
                        int month = Integer.parseInt(d[1]);
                        YearMonth firstYearMonth = YearMonth.of(year, month);
                        JsonElectricPower json = new JsonElectricPower();
                        for (DataLoadFrame1 item : data) {
                            String dateTime[] = item.getViewTime()
                                .split(Constants.ES.HYPHEN_CHARACTER);
                            int yearItem = Integer.parseInt(dateTime[0]);
                            int monthItem = Integer.parseInt(dateTime[1]);
                            YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                            if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                                double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                                currentPower += item.getEpTotal();
                                json.setLoad(currentPower);
                                json.setWind(currentPower);
                                json.setEv(currentPower);
                            }
                        }
                        for (DataInverter1 item : dataPv) {
                            String dateTime[] = item.getViewTime()
                                .split(Constants.ES.HYPHEN_CHARACTER);
                            int yearItem = Integer.parseInt(dateTime[0]);
                            int monthItem = Integer.parseInt(dateTime[1]);
                            YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                            if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                                double currentPv = json.getPv() != null ? json.getPv() : 0;
                                currentPv += item.getEp();
                                json.setPv(currentPv);
                            }
                        }
                        for (DataRmuDrawer1 item : dataGrid) {
                            String dateTime[] = item.getViewTime()
                                .split(Constants.ES.HYPHEN_CHARACTER);
                            int yearItem = Integer.parseInt(dateTime[0]);
                            int monthItem = Integer.parseInt(dateTime[1]);
                            YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                            if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                                double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                                currentGrid += item.getEpTotal();
                                json.setGrid(currentGrid);
                            }
                        }
                        json.setTime(m);
                        if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                            || json.getWind() != null || json.getEv() != null) {
                            dataChart.add(json);
                        }
                    });

                } else {
                    map.put("fromDate", fromDate);
                    map.put("toDate", toDate);

                    List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                    List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                    List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

                    LocalDate start = LocalDate.parse(fromDate);
                    LocalDate end = LocalDate.parse(toDate);
                    List<LocalDate> totalDates = new ArrayList<>();
                    while (!start.isAfter(end)) {
                        totalDates.add(start);
                        start = start.plusDays(1);
                    }

                    totalDates.forEach(date -> {
                        Date d = java.sql.Date.valueOf(date);
                        JsonElectricPower json = new JsonElectricPower();
                        data.forEach(loadFrame -> {
                            // set data chart bar
                            if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                                DateUtils.toDate(loadFrame.getViewTime(), "yyyy-MM-dd"))) {
                                double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                                currentPower += loadFrame.getEpTotal();
                                json.setLoad(currentPower);
                                json.setWind(currentPower);
                                json.setEv(currentPower);
                            }
                        });
                        dataPv.forEach(pvFrame -> {
                            if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                                DateUtils.toDate(pvFrame.getViewTime(), "yyyy-MM-dd"))) {
                                double currentPv = json.getPv() != null ? json.getPv() : 0;
                                currentPv += pvFrame.getEp();
                                json.setPv(currentPv);
                            }
                        });
                        dataGrid.forEach(gridFrame -> {
                            if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                                DateUtils.toDate(gridFrame.getViewTime(), "yyyy-MM-dd"))) {
                                double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                                currentGrid += gridFrame.getEpTotal();
                                json.setGrid(currentGrid);
                            }
                        });
                        json.setTime(DateUtils.toString(d, "yyyy-MM-dd"));
                        if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                            || json.getWind() != null || json.getEv() != null) {
                            dataChart.add(json);
                        }
                    });

                }
            }
            List<JsonElectricPower> dataAll = new ArrayList<>();
            if (timeType == 1) {
                String from = fromDate;
                String to = toDate;

                map.put("fromDate", from);
                map.put("toDate", to);

                List<String> years = new ArrayList<>();

                for (int i = Integer.parseInt(fromDate); i <= Integer.parseInt(toDate); i++) {
                    years.add(String.valueOf(i));
                }

                years.forEach(year -> {
                    int y = Integer.parseInt(year);
                    // CHECKSTYLE:OFF
                    JsonElectricPower json = new JsonElectricPower();
                    for (int i = 0; i < dataChart.size(); i++) {
                        String dateTime[] = dataChart.get(i)
                            .getTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        if (y == Integer.parseInt(dateTime[0])) {
                            if (dataChart.get(i)
                                .getLoad() != null) {
                                double totalLoad = json.getLoad() != null ? json.getLoad() : 0;
                                totalLoad += dataChart.get(i)
                                    .getLoad();
                                json.setLoad(totalLoad);
                            }
                            if (dataChart.get(i)
                                .getPv() != null) {
                                double totalPv = json.getPv() != null ? json.getPv() : 0;
                                totalPv += dataChart.get(i)
                                    .getPv();
                                json.setPv(totalPv);
                            }
                            if (dataChart.get(i)
                                .getGrid() != null) {
                                double totalGrid = json.getGrid() != null ? json.getGrid() : 0;
                                totalGrid += dataChart.get(i)
                                    .getGrid();
                                json.setGrid(totalGrid);
                            }
                            if (dataChart.get(i)
                                .getEv() != null) {
                                double totalEv = json.getEv() != null ? json.getEv() : 0;
                                totalEv += dataChart.get(i)
                                    .getEv();
                                json.setEv(totalEv);
                            }
                            if (dataChart.get(i)
                                .getWind() != null) {
                                double totalWind = json.getWind() != null ? json.getWind() : 0;
                                totalWind += dataChart.get(i)
                                    .getWind();
                                json.setWind(totalWind);
                            }
                        }
                    }

                    json.setTime(year);
                    if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                        || json.getWind() != null || json.getEv() != null) {
                        dataAll.add(json);
                    }
                });
                return new ResponseEntity<>(dataAll, HttpStatus.OK);

            } else if (timeType == 2) {
                String from = fromDate;
                String to = toDate;

                map.put("fromDate", from);
                map.put("toDate", to);

                // CHECKSTYLE:OFF
                int daysInMonth = 28;
                // CHECKSTYLE:ON
                String[] date = fromDate.split(Constants.ES.HYPHEN_CHARACTER);
                YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]));
                daysInMonth = yearMonthObject.lengthOfMonth();

                from = fromDate + Constants.ES.HYPHEN_CHARACTER + "01";
                to = toDate + Constants.ES.HYPHEN_CHARACTER + daysInMonth;

                Calendar beginCalendar = Calendar.getInstance();
                Calendar finishCalendar = Calendar.getInstance();

                Date fDate = DateUtils.toDate(from, "yyyy-MM-dd");
                Date tDate = DateUtils.toDate(to, "yyyy-MM-dd");
                beginCalendar.setTime(fDate);
                finishCalendar.setTime(tDate);

                SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM");

                List<String> months = new ArrayList<>();

                while (beginCalendar.before(finishCalendar)) {
                    // add one month to date per loop
                    String dateMonth = formater.format(beginCalendar.getTime())
                        .toUpperCase();
                    months.add(dateMonth);
                    beginCalendar.add(Calendar.MONTH, 1);
                }

                // CHECKSTYLE:OFF
                months.forEach(m -> {
                    String d[] = m.split(Constants.ES.HYPHEN_CHARACTER);
                    int year = Integer.parseInt(d[0]);
                    int month = Integer.parseInt(d[1]);
                    YearMonth firstYearMonth = YearMonth.of(year, month);
                    JsonElectricPower json = new JsonElectricPower();
                    for (int i = 0; i < dataChart.size(); i++) {
                        String dateTime[] = dataChart.get(i)
                            .getTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        int yearItem = Integer.parseInt(dateTime[0]);
                        int monthItem = Integer.parseInt(dateTime[1]);
                        YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                        if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                            if (dataChart.get(i)
                                .getLoad() != null) {
                                double totalLoad = json.getLoad() != null ? json.getLoad() : 0;
                                totalLoad += dataChart.get(i)
                                    .getLoad();
                                json.setLoad(totalLoad);
                            }
                            if (dataChart.get(i)
                                .getPv() != null) {
                                double totalPv = json.getPv() != null ? json.getPv() : 0;
                                totalPv += dataChart.get(i)
                                    .getPv();
                                json.setPv(totalPv);
                            }
                            if (dataChart.get(i)
                                .getGrid() != null) {
                                double totalGrid = json.getGrid() != null ? json.getGrid() : 0;
                                totalGrid += dataChart.get(i)
                                    .getGrid();
                                json.setGrid(totalGrid);
                            }
                            if (dataChart.get(i)
                                .getEv() != null) {
                                double totalEv = json.getEv() != null ? json.getEv() : 0;
                                totalEv += dataChart.get(i)
                                    .getEv();
                                json.setEv(totalEv);
                            }
                            if (dataChart.get(i)
                                .getWind() != null) {
                                double totalWind = json.getWind() != null ? json.getWind() : 0;
                                totalWind += dataChart.get(i)
                                    .getWind();
                                json.setWind(totalWind);
                            }

                        }
                    }
                    json.setTime(m);
                    if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                        || json.getWind() != null || json.getEv() != null) {
                        dataAll.add(json);
                    }
                });
                return new ResponseEntity<>(dataAll, HttpStatus.OK);

            } else {
                map.put("fromDate", fromDate);
                map.put("toDate", toDate);

                LocalDate start = LocalDate.parse(fromDate);
                LocalDate end = LocalDate.parse(toDate);
                List<LocalDate> totalDates = new ArrayList<>();
                while (!start.isAfter(end)) {
                    totalDates.add(start);
                    start = start.plusDays(1);
                }

                totalDates.forEach(date -> {
                    Date d = java.sql.Date.valueOf(date);
                    JsonElectricPower json = new JsonElectricPower();
                    for (int i = 0; i < dataChart.size(); i++) {
                        // set data chart bar
                        if (org.apache.commons.lang.time.DateUtils.isSameDay(d, DateUtils.toDate(dataChart.get(i)
                            .getTime(), "yyyy-MM-dd"))) {
                            if (dataChart.get(i)
                                .getLoad() != null) {
                                double totalLoad = json.getLoad() != null ? json.getLoad() : 0;
                                totalLoad += dataChart.get(i)
                                    .getLoad();
                                json.setLoad(totalLoad);
                            }
                            if (dataChart.get(i)
                                .getPv() != null) {
                                double totalPv = json.getPv() != null ? json.getPv() : 0;
                                totalPv += dataChart.get(i)
                                    .getPv();
                                json.setPv(totalPv);
                            }
                            if (dataChart.get(i)
                                .getGrid() != null) {
                                double totalGrid = json.getGrid() != null ? json.getGrid() : 0;
                                totalGrid += dataChart.get(i)
                                    .getGrid();
                                json.setGrid(totalGrid);
                            }
                            if (dataChart.get(i)
                                .getEv() != null) {
                                double totalEv = json.getEv() != null ? json.getEv() : 0;
                                totalEv += dataChart.get(i)
                                    .getEv();
                                json.setEv(totalEv);
                            }
                            if (dataChart.get(i)
                                .getWind() != null) {
                                double totalWind = json.getWind() != null ? json.getWind() : 0;
                                totalWind += dataChart.get(i)
                                    .getWind();
                                json.setWind(totalWind);
                            }

                        }
                    }
                    ;
                    json.setTime(DateUtils.toString(d, "yyyy-MM-dd"));
                    if (json.getLoad() != null || json.getPv() != null || json.getGrid() != null
                        || json.getWind() != null || json.getEv() != null) {
                        dataAll.add(json);
                    }
                });
                log.info("chartAll END");
                return new ResponseEntity<>(dataAll, HttpStatus.OK);
            }
        }
    }

    /**
     * Download data chart.
     *
     * @param projectId Mã dự án.
     * @param timeType Kiểu thời gian muốn hiển thị.
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @return File zip.
     */
    @GetMapping ("/chart/download")
    public ResponseEntity<Resource> download(@RequestParam final Integer customerId,
        @RequestParam final Integer projectId, @RequestParam final Integer timeType,
        @RequestParam final String fromDate, @RequestParam final String toDate, @RequestParam final String userName)
        throws Exception {
        // get url image
        User user = userService.getUserByUsername(userName);
        String pngImageURL = user.getImg();
        String encodingPrefix = "base64,";
        int contentStartIndex = pngImageURL.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData = org.apache.commons.codec.binary.Base64
            .decodeBase64(pngImageURL.substring(contentStartIndex));

        Map<String, String> condition = new HashMap<>();
        condition.put("projectId", String.valueOf(projectId));
        String[] deviceIds = deviceService.getDeviceIdByProjectId(condition);
        String deviceId = String.join(",", deviceIds);
        Map<String, String> map = new HashMap<>();
        map.put("schema", Schema.getSchemas(customerId));
        map.put("deviceId", deviceId);
        map.put("viewType", String.valueOf(timeType));

        List<JsonElectricPower> dataChart = new ArrayList<>();

        List<DataLoadFrame1> data;
        List<DataInverter1> dataPv;
        List<DataRmuDrawer1> dataGrid;

        // time miliseconds
        long miliseconds = new Date().getTime();

        // path folder
        String path = this.folderName + File.separator + miliseconds;

        if (timeType == 1) {
            String from = fromDate;
            String to = toDate;

            map.put("fromDate", from);
            map.put("toDate", to);

            data = dataLoadFrame1Service.getDataChartPower(map);
            dataPv = dataInverterService.getDataChartPV(map);
            dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

            List<String> years = new ArrayList<>();

            for (int i = Integer.parseInt(fromDate); i <= Integer.parseInt(toDate); i++) {
                years.add(String.valueOf(i));
            }

            years.forEach(year -> {
                int y = Integer.parseInt(year);
                // CHECKSTYLE:OFF
                JsonElectricPower json = new JsonElectricPower();
                for (DataLoadFrame1 item : data) {
                    String dateTime[] = item.getViewTime()
                        .split(Constants.ES.HYPHEN_CHARACTER);
                    if (y == Integer.parseInt(dateTime[0])) {
                        double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                        currentPower += item.getEpTotal();
                        json.setLoad(currentPower);
                    }
                }
                for (DataInverter1 item : dataPv) {
                    String dateTime[] = item.getViewTime()
                        .split(Constants.ES.HYPHEN_CHARACTER);
                    if (y == Integer.parseInt(dateTime[0])) {
                        double currentPv = json.getPv() != null ? json.getPv() : 0;
                        currentPv += item.getEp();
                        json.setPv(currentPv);
                    }
                }
                for (DataRmuDrawer1 item : dataGrid) {
                    String dateTime[] = item.getViewTime()
                        .split(Constants.ES.HYPHEN_CHARACTER);
                    if (y == Integer.parseInt(dateTime[0])) {
                        double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                        currentGrid += item.getEpTotal();
                        json.setGrid(currentGrid);
                    }
                }
                json.setTime(year);
                if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                    || json.getWind() != null) {
                    dataChart.add(json);
                }
            });
        } else if (timeType == 2) {
            String from = fromDate;
            String to = toDate;

            map.put("fromDate", from);
            map.put("toDate", to);

            data = dataLoadFrame1Service.getDataChartPower(map);
            dataPv = dataInverterService.getDataChartPV(map);
            dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

            // CHECKSTYLE:OFF
            int daysInMonth = 28;
            // CHECKSTYLE:ON
            String[] date = fromDate.split(Constants.ES.HYPHEN_CHARACTER);
            YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]));
            daysInMonth = yearMonthObject.lengthOfMonth();

            from = fromDate + Constants.ES.HYPHEN_CHARACTER + "01";
            to = toDate + Constants.ES.HYPHEN_CHARACTER + daysInMonth;
            Calendar beginCalendar = Calendar.getInstance();
            Calendar finishCalendar = Calendar.getInstance();

            Date fDate = DateUtils.toDate(from, "yyyy-MM-dd");
            Date tDate = DateUtils.toDate(to, "yyyy-MM-dd");
            beginCalendar.setTime(fDate);
            finishCalendar.setTime(tDate);

            SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM");

            List<String> months = new ArrayList<>();

            while (beginCalendar.before(finishCalendar)) {
                // add one month to date per loop
                String dateMonth = formater.format(beginCalendar.getTime())
                    .toUpperCase();
                months.add(dateMonth);
                beginCalendar.add(Calendar.MONTH, 1);
            }

            // CHECKSTYLE:OFF
            months.forEach(m -> {
                String d[] = m.split(Constants.ES.HYPHEN_CHARACTER);
                int month = Integer.parseInt(d[1]);
                JsonElectricPower json = new JsonElectricPower();
                for (DataLoadFrame1 item : data) {
                    Date dateItem = DateUtils.toDate(item.getViewTime(), "yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateItem);
                    int monthItem = cal.get(Calendar.MONTH);
                    if (month == (monthItem + 1)) {
                        double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                        currentPower += item.getEpTotal();
                        json.setLoad(currentPower);
                    }
                }
                for (DataInverter1 item : dataPv) {
                    Date dateItem = DateUtils.toDate(item.getViewTime(), "yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateItem);
                    int monthItem = cal.get(Calendar.MONTH);
                    if (month == (monthItem + 1)) {
                        double currentPv = json.getPv() != null ? json.getPv() : 0;
                        currentPv += item.getEp();
                        json.setLoad(currentPv);
                    }
                }
                for (DataRmuDrawer1 item : dataGrid) {
                    Date dateItem = DateUtils.toDate(item.getViewTime(), "yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateItem);
                    int monthItem = cal.get(Calendar.MONTH);
                    if (month == (monthItem + 1)) {
                        double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                        currentGrid += item.getEpTotal();
                        json.setLoad(currentGrid);
                    }
                }
                json.setTime(m);
                if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                    || json.getWind() != null) {
                    dataChart.add(json);
                }
            });
        } else {
            map.put("fromDate", fromDate);
            map.put("toDate", toDate);

            data = dataLoadFrame1Service.getDataChartPower(map);
            dataPv = dataInverterService.getDataChartPV(map);
            dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

            LocalDate start = LocalDate.parse(fromDate);
            LocalDate end = LocalDate.parse(toDate);
            List<LocalDate> totalDates = new ArrayList<>();
            while (!start.isAfter(end)) {
                totalDates.add(start);
                start = start.plusDays(1);
            }

            totalDates.forEach(date -> {
                Date d = java.sql.Date.valueOf(date);
                JsonElectricPower json = new JsonElectricPower();
                for (DataLoadFrame1 loadFrame : data) {
                    // set data chart bar
                    if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                        DateUtils.toDate(loadFrame.getViewTime(), "yyyy-MM-dd"))) {
                        double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                        currentPower += loadFrame.getEpTotal();
                        json.setLoad(currentPower);
                    }
                }
                dataPv.forEach(pvFrame -> {
                    if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                        DateUtils.toDate(pvFrame.getViewTime(), "yyyy-MM-dd"))) {
                        double currentPv = json.getPv() != null ? json.getPv() : 0;
                        currentPv += pvFrame.getEp();
                        json.setPv(currentPv);
                    }
                });
                dataGrid.forEach(gridFrame -> {
                    if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                        DateUtils.toDate(gridFrame.getViewTime(), "yyyy-MM-dd"))) {
                        double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                        currentGrid += gridFrame.getEpTotal();
                        json.setGrid(currentGrid);
                    }
                });
                json.setTime(DateUtils.toString(d, "yyyy-MM-dd"));
                if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                    || json.getWind() != null) {
                    dataChart.add(json);
                }
            });

        }

        // tạo excel
        if (data != null && data.size() > 0) {
            Map<String, String> cond = new HashMap<>();
            cond.put("projectId", String.valueOf(projectId));

            Project project = projectService.getProject(cond);

            createExcel(dataChart, fromDate, toDate, project, path, imageData, miliseconds);

            // gửi zip qua client
            String contentType = "application/zip";
            String headerValue = "attachment; filename=" + miliseconds + ".zip";

            Path realPath = Paths.get(path + ".zip");
            Resource resource = null;
            try {
                resource = new UrlResource(realPath.toUri());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .body(resource);
        } else {
            return ResponseEntity.badRequest()
                .body(null);
        }
    }

    /**
     * Create excel
     */
    private void createExcel(final List<JsonElectricPower> dataChart, final String fromDate, final String toDate,
        final Project project, final String path, final byte[] imageData, final Long miliseconds) throws Exception {
        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(dataChart.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Sản lượng điện năng");

        Row row;
        Cell cell;

        // add image
        int pictureIdx = wb.addPicture(imageData, wb.PICTURE_TYPE_PNG);
        SXSSFDrawing drawingImg = sheet.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(6);
        anchorImg.setCol2(7);
        anchorImg.setRow1(1);
        anchorImg.setRow2(4);

        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);

        // Page orientation
        sheet.getPrintSetup()
            .setLandscape(false);

        // Page margins
        sheet.setMargin(Sheet.RightMargin, 0.5);
        sheet.setMargin(Sheet.LeftMargin, 0.5);
        sheet.setMargin(Sheet.TopMargin, 0.5);
        sheet.setMargin(Sheet.BottomMargin, 0.5);

        // Tạo sheet content
        for (int i = 0; i < 6; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 36; j++) {
                row.createCell(j);
            }
        }

        // set độ rộng của cột
        sheet.setColumnWidth(0, 1300);
        sheet.setColumnWidth(1, 5200);
        sheet.setColumnWidth(2, 5200);
        sheet.setColumnWidth(3, 5200);
        sheet.setColumnWidth(4, 5200);
        sheet.setColumnWidth(5, 5200);
        sheet.setColumnWidth(6, 5200);

        // set độ rộng của hàng
        Row row1 = sheet.getRow(1);
        row1.setHeight((short) -500);
        Row row2 = sheet.getRow(2);
        row2.setHeight((short) -500);
        Row row3 = sheet.getRow(3);
        row3.setHeight((short) -500);

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 6);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(0)
            .getCell(0);
        cell.setCellValue("SẢN LƯỢNG ĐIỆN NĂNG");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(0);
        cell.setCellValue("Mã dự án");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(2, 2, 2, 3);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(2);
        cell.setCellValue(project.getProjectId());
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(3, 3, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(0);
        cell.setCellValue("Tên dự án");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(3, 3, 2, 3);
        cell = sheet.getRow(3)
            .getCell(2);
        cell.setCellValue(project != null && project.getProjectName() != null
            ? project.getProjectName()
                .toUpperCase()
            : "");
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(2, 3, 4, 4);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(4);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 5, 5);
        cell = sheet.getRow(2)
            .getCell(5);
        cell.setCellValue(fromDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        region = new CellRangeAddress(3, 3, 5, 5);
        cell = sheet.getRow(3)
            .getCell(5);
        cell.setCellValue(toDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        // bảng sản lượng điện năng
        region = new CellRangeAddress(5, 5, 0, 0);
        cell = sheet.getRow(5)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 1, 1);
        cell = sheet.getRow(5)
            .getCell(1);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 2, 2);
        cell = sheet.getRow(5)
            .getCell(2);
        cell.setCellValue("LOAD [kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("PV [kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("Grid [kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("Wind [kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 6, 6);
        cell = sheet.getRow(5)
            .getCell(6);
        cell.setCellValue("Battery [kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        int index = 6;

        for (int m = 0; m < dataChart.size(); m++) {
            JsonElectricPower item = dataChart.get(m);

            for (int i = index; i < index + 1; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 36; j++) {
                    Cell c = row.createCell(j, CellType.BLANK);
                    c.setCellStyle(cs);
                }
            }

            // Cột thứ tự
            region = new CellRangeAddress(index, index, 0, 0);
            cell = sheet.getRow(index)
                .getCell(0);
            cell.setCellValue(m + 1);

            // Cột Thời gian
            region = new CellRangeAddress(index, index, 1, 1);
            cell = sheet.getRow(index)
                .getCell(1);
            cell.setCellValue(item.getTime());

            // Cột LOAD
            region = new CellRangeAddress(index, index, 2, 2);
            cell = sheet.getRow(index)
                .getCell(2);
            cell.setCellValue(item.getLoad() != null ? String.valueOf(item.getLoad()) : "-");

            // Cột PV
            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getPv() != null ? String.valueOf(item.getPv()) : "-");

            // Cột GRID
            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue(item.getGrid() != null ? String.valueOf(item.getGrid()) : "-");

            // Cột WIND
            region = new CellRangeAddress(index, index, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue(item.getWind() != null ? String.valueOf(item.getWind()) : "-");

            // Cột Battery
            region = new CellRangeAddress(index, index, 6, 6);
            cell = sheet.getRow(index)
                .getCell(6);
            cell.setCellValue(item.getEv() != null ? String.valueOf(item.getEv()) : "-");

            index += 1;
        }

        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // tạo file excel vào folder export
        String exportFilePath = path + File.separator + miliseconds + ".xlsx";

        File file = new File(exportFilePath);

        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("HomeController: Create file excel success");
        } catch (FileNotFoundException e) {
            log.error("HomeController: ERROR File Not Found while export file excel.");
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                wb.dispose();
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // zip folder
        ZipUtil.pack(folder, new File(path + ".zip"));

        log.info("HomeController.createExcel() end");
    }

    /**
     * Format Header.
     */
    // CHECKSTYLE:OFF
    private void formatHeader(final SXSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet, final Cell cell,
        final short bgColor, final HorizontalAlignment hAlign, final int indent, boolean isFontBold) {

        CellStyle cs = wb.createCellStyle();
        cs.setFillBackgroundColor(bgColor);
        cs.setFillForegroundColor(bgColor);
        cs.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);

        Font font = wb.createFont();
        font.setBold(isFontBold);
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);

        cs.setAlignment(hAlign);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setIndention((short) indent);
        cs.setWrapText(true);
        cell.setCellStyle(cs);

        RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
    }

    /**
     * Download data chart customer
     *
     * @param customerId Mã khách hàng.
     * @param timeType Kiểu thời gian muốn hiển thị.
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @return File zip.
     */
    @GetMapping ("/chartCustomer/download")
    public ResponseEntity<Resource> downloadChartCustomer(@RequestParam ("customerId") final Integer customerId,
        @RequestParam final Integer timeType, @RequestParam final String fromDate, @RequestParam final String toDate,
        @RequestParam final String userName) throws Exception {
        // get url image
        User user = userService.getUserByUsername(userName);
        String pngImageURL = user.getImg();
        String encodingPrefix = "base64,";
        int contentStartIndex = pngImageURL.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData = org.apache.commons.codec.binary.Base64
            .decodeBase64(pngImageURL.substring(contentStartIndex));

        Map<String, String> condition = new HashMap<>();
        condition.put("customerId", String.valueOf(customerId));
        String[] deviceIds = deviceService.getDeviceIdByCustomerId(condition);
        String deviceId = String.join(",", deviceIds);
        Map<String, String> map = new HashMap<>();
        map.put("deviceId", deviceId);
        map.put("schema", Schema.getSchemas(customerId));
        map.put("viewType", String.valueOf(timeType));

        List<JsonElectricPower> dataChart = new ArrayList<>();

        List<DataLoadFrame1> data;

        List<DataInverter1> dataPv;

        List<DataRmuDrawer1> dataGrid;

        // time miliseconds
        long miliseconds = new Date().getTime();

        // path folder
        String path = this.folderName + File.separator + miliseconds;

        if (timeType == 1) {
            String from = fromDate;
            String to = toDate;

            map.put("fromDate", from);
            map.put("toDate", to);

            data = dataLoadFrame1Service.getDataChartPower(map);
            dataPv = dataInverterService.getDataChartPV(map);
            dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

            List<String> years = new ArrayList<>();

            for (int i = Integer.parseInt(fromDate); i <= Integer.parseInt(toDate); i++) {
                years.add(String.valueOf(i));
            }

            years.forEach(year -> {
                int y = Integer.parseInt(year);
                // CHECKSTYLE:OFF
                JsonElectricPower json = new JsonElectricPower();
                for (DataLoadFrame1 item : data) {
                    String dateTime[] = item.getViewTime()
                        .split(Constants.ES.HYPHEN_CHARACTER);
                    if (y == Integer.parseInt(dateTime[0])) {
                        double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                        currentPower += item.getEpTotal();
                        json.setLoad(currentPower);
                    }
                }
                for (DataInverter1 item : dataPv) {
                    String dateTime[] = item.getViewTime()
                        .split(Constants.ES.HYPHEN_CHARACTER);
                    if (y == Integer.parseInt(dateTime[0])) {
                        double currentPv = json.getPv() != null ? json.getPv() : 0;
                        currentPv += item.getEp();
                        json.setPv(currentPv);
                    }
                }
                for (DataRmuDrawer1 item : dataGrid) {
                    String dateTime[] = item.getViewTime()
                        .split(Constants.ES.HYPHEN_CHARACTER);
                    if (y == Integer.parseInt(dateTime[0])) {
                        double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                        currentGrid += item.getEpTotal();
                        json.setGrid(currentGrid);
                    }
                }
                json.setTime(year);
                if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                    || json.getWind() != null) {
                    dataChart.add(json);
                }
            });
        } else if (timeType == 2) {
            String from = fromDate;
            String to = toDate;

            map.put("fromDate", from);
            map.put("toDate", to);

            data = dataLoadFrame1Service.getDataChartPower(map);
            dataPv = dataInverterService.getDataChartPV(map);
            dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

            // CHECKSTYLE:OFF
            int daysInMonth = 28;
            // CHECKSTYLE:ON
            String[] date = fromDate.split(Constants.ES.HYPHEN_CHARACTER);
            YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]));
            daysInMonth = yearMonthObject.lengthOfMonth();

            from = fromDate + Constants.ES.HYPHEN_CHARACTER + "01";
            to = toDate + Constants.ES.HYPHEN_CHARACTER + daysInMonth;
            Calendar beginCalendar = Calendar.getInstance();
            Calendar finishCalendar = Calendar.getInstance();

            Date fDate = DateUtils.toDate(from, "yyyy-MM-dd");
            Date tDate = DateUtils.toDate(to, "yyyy-MM-dd");
            beginCalendar.setTime(fDate);
            finishCalendar.setTime(tDate);

            SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM");

            List<String> months = new ArrayList<>();

            while (beginCalendar.before(finishCalendar)) {
                // add one month to date per loop
                String dateMonth = formater.format(beginCalendar.getTime())
                    .toUpperCase();
                months.add(dateMonth);
                beginCalendar.add(Calendar.MONTH, 1);
            }

            // CHECKSTYLE:OFF
            months.forEach(m -> {
                String d[] = m.split(Constants.ES.HYPHEN_CHARACTER);
                int month = Integer.parseInt(d[1]);
                JsonElectricPower json = new JsonElectricPower();
                for (DataLoadFrame1 item : data) {
                    Date dateItem = DateUtils.toDate(item.getViewTime(), "yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateItem);
                    int monthItem = cal.get(Calendar.MONTH);
                    if (month == (monthItem + 1)) {
                        double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                        currentPower += item.getEpTotal();
                        json.setLoad(currentPower);
                    }
                }
                for (DataInverter1 item : dataPv) {
                    Date dateItem = DateUtils.toDate(item.getViewTime(), "yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateItem);
                    int monthItem = cal.get(Calendar.MONTH);
                    if (month == (monthItem + 1)) {
                        double currentPv = json.getPv() != null ? json.getPv() : 0;
                        currentPv += item.getEp();
                        json.setLoad(currentPv);
                    }
                }
                for (DataRmuDrawer1 item : dataGrid) {
                    Date dateItem = DateUtils.toDate(item.getViewTime(), "yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateItem);
                    int monthItem = cal.get(Calendar.MONTH);
                    if (month == (monthItem + 1)) {
                        double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                        currentGrid += item.getEpTotal();
                        json.setLoad(currentGrid);
                    }
                }
                json.setTime(m);
                if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                    || json.getWind() != null) {
                    dataChart.add(json);
                }
            });
        } else {
            map.put("fromDate", fromDate);
            map.put("toDate", toDate);

            data = dataLoadFrame1Service.getDataChartPower(map);
            dataPv = dataInverterService.getDataChartPV(map);
            dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

            LocalDate start = LocalDate.parse(fromDate);
            LocalDate end = LocalDate.parse(toDate);
            List<LocalDate> totalDates = new ArrayList<>();
            while (!start.isAfter(end)) {
                totalDates.add(start);
                start = start.plusDays(1);
            }

            totalDates.forEach(date -> {
                Date d = java.sql.Date.valueOf(date);
                JsonElectricPower json = new JsonElectricPower();
                for (DataLoadFrame1 loadFrame : data) {
                    // set data chart bar
                    if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                        DateUtils.toDate(loadFrame.getViewTime(), "yyyy-MM-dd"))) {
                        double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                        currentPower += loadFrame.getEpTotal();
                        json.setLoad(currentPower);
                    }
                }
                dataPv.forEach(pvFrame -> {
                    if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                        DateUtils.toDate(pvFrame.getViewTime(), "yyyy-MM-dd"))) {
                        double currentPv = json.getPv() != null ? json.getPv() : 0;
                        currentPv += pvFrame.getEp();
                        json.setPv(currentPv);
                    }
                });
                dataGrid.forEach(gridFrame -> {
                    if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                        DateUtils.toDate(gridFrame.getViewTime(), "yyyy-MM-dd"))) {
                        double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                        currentGrid += gridFrame.getEpTotal();
                        json.setGrid(currentGrid);
                    }
                });
                json.setTime(DateUtils.toString(d, "yyyy-MM-dd"));
                if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                    || json.getWind() != null) {
                    dataChart.add(json);
                }
            });

        }

        // tạo excel
        if (data != null && data.size() > 0) {
            Map<String, String> cond = new HashMap<>();
            cond.put("customerId", String.valueOf(customerId));

            Customer customer = customerService.getCustomer(cond);

            createExcelCustomer(dataChart, fromDate, toDate, customer, path, imageData, miliseconds);

            // gửi zip qua client
            String contentType = "application/zip";
            String headerValue = "attachment; filename=" + miliseconds + ".zip";

            Path realPath = Paths.get(path + ".zip");
            Resource resource = null;
            try {
                resource = new UrlResource(realPath.toUri());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .body(resource);
        } else {
            return ResponseEntity.badRequest()
                .body(null);
        }
    }

    private void createExcelCustomer(final List<JsonElectricPower> dataChart, final String fromDate,
        final String toDate, final Customer customer, final String path, final byte[] imageData, final Long miliseconds)
        throws Exception {
        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(dataChart.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Sản lượng điện năng");
        Row row;
        org.apache.poi.ss.usermodel.Cell cell;
        DataFormat fmt = wb.createDataFormat();
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(fmt.getFormat("@"));

        // add image
        int pictureIdx = wb.addPicture(imageData, wb.PICTURE_TYPE_PNG);
        SXSSFDrawing drawingImg = sheet.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(6);
        anchorImg.setCol2(7);
        anchorImg.setRow1(1);
        anchorImg.setRow2(4);

        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);

        // Page orientation
        sheet.getPrintSetup()
            .setLandscape(false);

        // Page margins
        sheet.setMargin(Sheet.RightMargin, 0.5);
        sheet.setMargin(Sheet.LeftMargin, 0.5);
        sheet.setMargin(Sheet.TopMargin, 0.5);
        sheet.setMargin(Sheet.BottomMargin, 0.5);

        // Tạo sheet content
        for (int i = 0; i < 6; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 36; j++) {
                row.createCell(j);
            }
        }

        // set độ rộng của cột
        sheet.setColumnWidth(0, 1300);
        sheet.setColumnWidth(1, 5200);
        sheet.setColumnWidth(2, 5200);
        sheet.setColumnWidth(3, 5200);
        sheet.setColumnWidth(4, 5200);
        sheet.setColumnWidth(5, 5200);
        sheet.setColumnWidth(6, 5200);

        // set độ rộng của hàng
        Row row1 = sheet.getRow(1);
        row1.setHeight((short) -500);
        Row row2 = sheet.getRow(2);
        row2.setHeight((short) -500);
        Row row3 = sheet.getRow(3);
        row3.setHeight((short) -500);

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 6);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(0)
            .getCell(0);
        cell.setCellValue("SẢN LƯỢNG ĐIỆN NĂNG");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(0);
        cell.setCellValue("Mã khách hàng");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(2, 2, 2, 3);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(2);
        cell.setCellValue(customer.getCustomerId());
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(3, 3, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(0);
        cell.setCellValue("Tên khách hàng");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(3, 3, 2, 3);
        cell = sheet.getRow(3)
            .getCell(2);
        cell.setCellValue(customer != null && customer.getCustomerName() != null
            ? customer.getCustomerName()
                .toUpperCase()
            : "");
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(2, 3, 4, 4);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(4);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 5, 5);
        cell = sheet.getRow(2)
            .getCell(5);
        cell.setCellValue(fromDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        region = new CellRangeAddress(3, 3, 5, 5);
        cell = sheet.getRow(3)
            .getCell(5);
        cell.setCellValue(toDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        // bảng sản lượng điện năng
        region = new CellRangeAddress(5, 5, 0, 0);
        cell = sheet.getRow(5)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 1, 1);
        cell = sheet.getRow(5)
            .getCell(1);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 2, 2);
        cell = sheet.getRow(5)
            .getCell(2);
        cell.setCellValue("LOAD[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("PV[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("Grid[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("Wind[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 6, 6);
        cell = sheet.getRow(5)
            .getCell(6);
        cell.setCellValue("Battery[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        int index = 6;

        for (int m = 0; m < dataChart.size(); m++) {
            JsonElectricPower item = dataChart.get(m);

            for (int i = index; i < index + 1; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 36; j++) {
                    Cell c = row.createCell(j, CellType.BLANK);
                    c.setCellStyle(cs);
                }
            }

            // Cột thứ tự
            region = new CellRangeAddress(index, index, 0, 0);
            cell = sheet.getRow(index)
                .getCell(0);
            cell.setCellValue(m + 1);

            // Cột Thời gian
            region = new CellRangeAddress(index, index, 1, 1);
            cell = sheet.getRow(index)
                .getCell(1);
            cell.setCellValue(item.getTime());

            // Cột LOAD
            region = new CellRangeAddress(index, index, 2, 2);
            cell = sheet.getRow(index)
                .getCell(2);
            cell.setCellValue(item.getLoad() != null ? String.valueOf(item.getLoad()) : "-");

            // Cột PV
            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getPv() != null ? String.valueOf(item.getPv()) : "-");

            // Cột GRID
            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue(item.getGrid() != null ? String.valueOf(item.getGrid()) : "-");

            // Cột WIND
            region = new CellRangeAddress(index, index, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue(item.getWind() != null ? String.valueOf(item.getWind()) : "-");

            // Cột Battery
            region = new CellRangeAddress(index, index, 6, 6);
            cell = sheet.getRow(index)
                .getCell(6);
            cell.setCellValue(item.getEv() != null ? String.valueOf(item.getEv()) : "-");

            index += 1;
        }

        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // tạo file excel vào folder export
        String exportFilePath = path + File.separator + miliseconds + ".xlsx";

        File file = new File(exportFilePath);

        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("HomeController: Create file excel success");
        } catch (FileNotFoundException e) {
            log.error("HomeController: ERROR File Not Found while export file excel.");
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                wb.dispose();
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // zip folder
        ZipUtil.pack(folder, new File(path + ".zip"));

        log.info("HomeController.createExcel() end");
    }

    @GetMapping ("/chartSuperManager/download")
    public ResponseEntity<Resource> downloadChartSuperManager(@RequestParam ("customerId") final Integer customerId,
        @RequestParam ("superManagerId") final Integer superManagerId, @RequestParam final Integer timeType,
        @RequestParam final String fromDate, @RequestParam final String toDate, @RequestParam final String userName)
        throws Exception {
        // get url image
        User user = userService.getUserByUsername(userName);
        String pngImageURL = user.getImg();
        String encodingPrefix = "base64,";
        int contentStartIndex = pngImageURL.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData = org.apache.commons.codec.binary.Base64
            .decodeBase64(pngImageURL.substring(contentStartIndex));
        Map<String, String> condition = new HashMap<>();
        condition.put("customerId", String.valueOf(customerId));
        condition.put("superManagerId", String.valueOf(superManagerId));
        String[] deviceIds = deviceService.getDeviceBySuperManager(condition);
        String deviceId = String.join(",", deviceIds);
        Map<String, String> map = new HashMap<>();
        map.put("schema", Schema.getSchemas(customerId));
        map.put("deviceId", deviceId);
        map.put("viewType", String.valueOf(timeType));

        List<JsonElectricPower> dataChart = new ArrayList<>();

        List<DataLoadFrame1> data;

        List<DataInverter1> dataPv;

        List<DataRmuDrawer1> dataGrid;

        // time miliseconds
        long miliseconds = new Date().getTime();

        // path folder
        String path = this.folderName + File.separator + miliseconds;

        if (timeType == 1) {
            String from = fromDate;
            String to = toDate;

            map.put("fromDate", from);
            map.put("toDate", to);

            data = dataLoadFrame1Service.getDataChartPower(map);
            dataPv = dataInverterService.getDataChartPV(map);
            dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

            List<String> years = new ArrayList<>();

            for (int i = Integer.parseInt(fromDate); i <= Integer.parseInt(toDate); i++) {
                years.add(String.valueOf(i));
            }

            years.forEach(year -> {
                int y = Integer.parseInt(year);
                // CHECKSTYLE:OFF
                JsonElectricPower json = new JsonElectricPower();
                for (DataLoadFrame1 item : data) {
                    String dateTime[] = item.getViewTime()
                        .split(Constants.ES.HYPHEN_CHARACTER);
                    if (y == Integer.parseInt(dateTime[0])) {
                        double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                        currentPower += item.getEpTotal();
                        json.setLoad(currentPower);
                    }
                }
                for (DataInverter1 item : dataPv) {
                    String dateTime[] = item.getViewTime()
                        .split(Constants.ES.HYPHEN_CHARACTER);
                    if (y == Integer.parseInt(dateTime[0])) {
                        double currentPv = json.getPv() != null ? json.getPv() : 0;
                        currentPv += item.getEp();
                        json.setPv(currentPv);
                    }
                }
                for (DataRmuDrawer1 item : dataGrid) {
                    String dateTime[] = item.getViewTime()
                        .split(Constants.ES.HYPHEN_CHARACTER);
                    if (y == Integer.parseInt(dateTime[0])) {
                        double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                        currentGrid += item.getEpTotal();
                        json.setGrid(currentGrid);
                    }
                }
                json.setTime(year);
                if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                    || json.getWind() != null) {
                    dataChart.add(json);
                }
            });
        } else if (timeType == 2) {
            String from = fromDate;
            String to = toDate;

            map.put("fromDate", from);
            map.put("toDate", to);

            data = dataLoadFrame1Service.getDataChartPower(map);
            dataPv = dataInverterService.getDataChartPV(map);
            dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

            // CHECKSTYLE:OFF
            int daysInMonth = 28;
            // CHECKSTYLE:ON
            String[] date = fromDate.split(Constants.ES.HYPHEN_CHARACTER);
            YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]));
            daysInMonth = yearMonthObject.lengthOfMonth();

            from = fromDate + Constants.ES.HYPHEN_CHARACTER + "01";
            to = toDate + Constants.ES.HYPHEN_CHARACTER + daysInMonth;
            Calendar beginCalendar = Calendar.getInstance();
            Calendar finishCalendar = Calendar.getInstance();

            Date fDate = DateUtils.toDate(from, "yyyy-MM-dd");
            Date tDate = DateUtils.toDate(to, "yyyy-MM-dd");
            beginCalendar.setTime(fDate);
            finishCalendar.setTime(tDate);

            SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM");

            List<String> months = new ArrayList<>();

            while (beginCalendar.before(finishCalendar)) {
                // add one month to date per loop
                String dateMonth = formater.format(beginCalendar.getTime())
                    .toUpperCase();
                months.add(dateMonth);
                beginCalendar.add(Calendar.MONTH, 1);
            }

            // CHECKSTYLE:OFF
            months.forEach(m -> {
                String d[] = m.split(Constants.ES.HYPHEN_CHARACTER);
                int month = Integer.parseInt(d[1]);
                JsonElectricPower json = new JsonElectricPower();
                for (DataLoadFrame1 item : data) {
                    Date dateItem = DateUtils.toDate(item.getViewTime(), "yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateItem);
                    int monthItem = cal.get(Calendar.MONTH);
                    if (month == (monthItem + 1)) {
                        double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                        currentPower += item.getEpTotal();
                        json.setLoad(currentPower);
                    }
                }
                for (DataInverter1 item : dataPv) {
                    Date dateItem = DateUtils.toDate(item.getViewTime(), "yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateItem);
                    int monthItem = cal.get(Calendar.MONTH);
                    if (month == (monthItem + 1)) {
                        double currentPv = json.getPv() != null ? json.getPv() : 0;
                        currentPv += item.getEp();
                        json.setLoad(currentPv);
                    }
                }
                for (DataRmuDrawer1 item : dataGrid) {
                    Date dateItem = DateUtils.toDate(item.getViewTime(), "yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateItem);
                    int monthItem = cal.get(Calendar.MONTH);
                    if (month == (monthItem + 1)) {
                        double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                        currentGrid += item.getEpTotal();
                        json.setLoad(currentGrid);
                    }
                }
                json.setTime(m);
                if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                    || json.getWind() != null) {
                    dataChart.add(json);
                }
            });
        } else {
            map.put("fromDate", fromDate);
            map.put("toDate", toDate);

            data = dataLoadFrame1Service.getDataChartPower(map);
            dataPv = dataInverterService.getDataChartPV(map);
            dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

            LocalDate start = LocalDate.parse(fromDate);
            LocalDate end = LocalDate.parse(toDate);
            List<LocalDate> totalDates = new ArrayList<>();
            while (!start.isAfter(end)) {
                totalDates.add(start);
                start = start.plusDays(1);
            }

            totalDates.forEach(date -> {
                Date d = java.sql.Date.valueOf(date);
                JsonElectricPower json = new JsonElectricPower();
                for (DataLoadFrame1 loadFrame : data) {
                    // set data chart bar
                    if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                        DateUtils.toDate(loadFrame.getViewTime(), "yyyy-MM-dd"))) {
                        double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                        currentPower += loadFrame.getEpTotal();
                        json.setLoad(currentPower);
                    }
                }
                dataPv.forEach(pvFrame -> {
                    if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                        DateUtils.toDate(pvFrame.getViewTime(), "yyyy-MM-dd"))) {
                        double currentPv = json.getPv() != null ? json.getPv() : 0;
                        currentPv += pvFrame.getEp();
                        json.setPv(currentPv);
                    }
                });
                dataGrid.forEach(gridFrame -> {
                    if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                        DateUtils.toDate(gridFrame.getViewTime(), "yyyy-MM-dd"))) {
                        double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                        currentGrid += gridFrame.getEpTotal();
                        json.setGrid(currentGrid);
                    }
                });
                json.setTime(DateUtils.toString(d, "yyyy-MM-dd"));
                if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                    || json.getWind() != null) {
                    dataChart.add(json);
                }
            });

        }

        // tạo excel
        if (data != null && data.size() > 0) {
            Long id = Long.valueOf(superManagerId);

            SuperManager superMananager = superManagerService.getSuperManagerById(id);
            createExcelSuperManager(dataChart, fromDate, toDate, superMananager, path, imageData, miliseconds);

            // gửi zip qua client
            String contentType = "application/zip";
            String headerValue = "attachment; filename=" + miliseconds + ".zip";

            Path realPath = Paths.get(path + ".zip");
            Resource resource = null;
            try {
                resource = new UrlResource(realPath.toUri());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .body(resource);
        } else {
            return ResponseEntity.badRequest()
                .body(null);
        }
    }

    private void createExcelSuperManager(final List<JsonElectricPower> dataChart, final String fromDate,
        final String toDate, final SuperManager superManager, final String path, final byte[] imageData,
        final Long miliseconds) throws Exception {
        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(dataChart.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Sản lượng điện năng");
        Row row;
        org.apache.poi.ss.usermodel.Cell cell;
        DataFormat fmt = wb.createDataFormat();
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(fmt.getFormat("@"));

        // add image
        int pictureIdx = wb.addPicture(imageData, wb.PICTURE_TYPE_PNG);
        SXSSFDrawing drawingImg = sheet.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(6);
        anchorImg.setCol2(7);
        anchorImg.setRow1(1);
        anchorImg.setRow2(4);

        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);

        // Page orientation
        sheet.getPrintSetup()
            .setLandscape(false);

        // Page margins
        sheet.setMargin(Sheet.RightMargin, 0.5);
        sheet.setMargin(Sheet.LeftMargin, 0.5);
        sheet.setMargin(Sheet.TopMargin, 0.5);
        sheet.setMargin(Sheet.BottomMargin, 0.5);

        // Tạo sheet content
        for (int i = 0; i < 6; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 36; j++) {
                row.createCell(j);
            }
        }

        // set độ rộng của cột
        sheet.setColumnWidth(0, 1300);
        sheet.setColumnWidth(1, 5200);
        sheet.setColumnWidth(2, 5200);
        sheet.setColumnWidth(3, 5200);
        sheet.setColumnWidth(4, 5200);
        sheet.setColumnWidth(5, 5200);
        sheet.setColumnWidth(6, 5200);

        // set độ rộng của hàng
        Row row1 = sheet.getRow(1);
        row1.setHeight((short) -500);
        Row row2 = sheet.getRow(2);
        row2.setHeight((short) -500);
        Row row3 = sheet.getRow(3);
        row3.setHeight((short) -500);

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 6);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(0)
            .getCell(0);
        cell.setCellValue("SẢN LƯỢNG ĐIỆN NĂNG");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(0);
        cell.setCellValue("Mã khu vực");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(2, 2, 2, 3);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(2);
        cell.setCellValue(superManager.getSuperManagerId());
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(3, 3, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(0);
        cell.setCellValue("Tên khu vực");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(3, 3, 2, 3);
        cell = sheet.getRow(3)
            .getCell(2);
        cell.setCellValue(superManager != null && superManager.getSuperManagerName() != null
            ? superManager.getSuperManagerName()
                .toUpperCase()
            : "");
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(2, 3, 4, 4);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(4);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 5, 5);
        cell = sheet.getRow(2)
            .getCell(5);
        cell.setCellValue(fromDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        region = new CellRangeAddress(3, 3, 5, 5);
        cell = sheet.getRow(3)
            .getCell(5);
        cell.setCellValue(toDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        // bảng sản lượng điện năng
        region = new CellRangeAddress(5, 5, 0, 0);
        cell = sheet.getRow(5)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 1, 1);
        cell = sheet.getRow(5)
            .getCell(1);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 2, 2);
        cell = sheet.getRow(5)
            .getCell(2);
        cell.setCellValue("LOAD[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("PV[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("Grid[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("Wind[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 6, 6);
        cell = sheet.getRow(5)
            .getCell(6);
        cell.setCellValue("Battery[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        int index = 6;

        for (int m = 0; m < dataChart.size(); m++) {
            JsonElectricPower item = dataChart.get(m);

            for (int i = index; i < index + 1; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 36; j++) {
                    Cell c = row.createCell(j, CellType.BLANK);
                    c.setCellStyle(cs);
                }
            }

            // Cột thứ tự
            region = new CellRangeAddress(index, index, 0, 0);
            cell = sheet.getRow(index)
                .getCell(0);
            cell.setCellValue(m + 1);

            // Cột Thời gian
            region = new CellRangeAddress(index, index, 1, 1);
            cell = sheet.getRow(index)
                .getCell(1);
            cell.setCellValue(item.getTime());

            // Cột LOAD
            region = new CellRangeAddress(index, index, 2, 2);
            cell = sheet.getRow(index)
                .getCell(2);
            cell.setCellValue(item.getLoad() != null ? String.valueOf(item.getLoad()) : "-");

            // Cột PV
            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getPv() != null ? String.valueOf(item.getPv()) : "-");

            // Cột GRID
            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue(item.getGrid() != null ? String.valueOf(item.getGrid()) : "-");

            // Cột WIND
            region = new CellRangeAddress(index, index, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue(item.getWind() != null ? String.valueOf(item.getWind()) : "-");

            // Cột Battery
            region = new CellRangeAddress(index, index, 6, 6);
            cell = sheet.getRow(index)
                .getCell(6);
            cell.setCellValue(item.getEv() != null ? String.valueOf(item.getEv()) : "-");

            index += 1;
        }

        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // tạo file excel vào folder export
        String exportFilePath = path + File.separator + miliseconds + ".xlsx";

        File file = new File(exportFilePath);

        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("HomeController: Create file excel success");
        } catch (FileNotFoundException e) {
            log.error("HomeController: ERROR File Not Found while export file excel.");
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                wb.dispose();
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // zip folder
        ZipUtil.pack(folder, new File(path + ".zip"));

        log.info("HomeController.createExcel() end");
    }

    @GetMapping ("/chartManager/download")
    public ResponseEntity<Resource> downloadChartManager(@RequestParam ("customerId") final Integer customerId,
        @RequestParam ("superManagerId") final Integer superManagerId,
        @RequestParam ("managerId") final Integer managerId, @RequestParam final Integer timeType,
        @RequestParam final String fromDate, @RequestParam final String toDate, @RequestParam final String userName)
        throws Exception {
        // get url image
        User user = userService.getUserByUsername(userName);
        String pngImageURL = user.getImg();
        String encodingPrefix = "base64,";
        int contentStartIndex = pngImageURL.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData = org.apache.commons.codec.binary.Base64
            .decodeBase64(pngImageURL.substring(contentStartIndex));
        Map<String, String> condition = new HashMap<>();
        condition.put("customerId", String.valueOf(customerId));
        condition.put("superManagerId", String.valueOf(superManagerId));
        condition.put("managerId", String.valueOf(managerId));
        String[] deviceIds = deviceService.getDeviceByManager(condition);
        String deviceId = String.join(",", deviceIds);
        Map<String, String> map = new HashMap<>();
        map.put("schema", Schema.getSchemas(customerId));
        map.put("deviceId", deviceId);
        map.put("viewType", String.valueOf(timeType));

        List<JsonElectricPower> dataChart = new ArrayList<>();

        List<DataLoadFrame1> data;

        List<DataInverter1> dataPv;

        List<DataRmuDrawer1> dataGrid;

        // time miliseconds
        long miliseconds = new Date().getTime();

        // path folder
        String path = this.folderName + File.separator + miliseconds;

        if (timeType == 1) {
            String from = fromDate;
            String to = toDate;

            map.put("fromDate", from);
            map.put("toDate", to);

            data = dataLoadFrame1Service.getDataChartPower(map);
            dataPv = dataInverterService.getDataChartPV(map);
            dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

            List<String> years = new ArrayList<>();

            for (int i = Integer.parseInt(fromDate); i <= Integer.parseInt(toDate); i++) {
                years.add(String.valueOf(i));
            }

            years.forEach(year -> {
                int y = Integer.parseInt(year);
                // CHECKSTYLE:OFF
                JsonElectricPower json = new JsonElectricPower();
                for (DataLoadFrame1 item : data) {
                    String dateTime[] = item.getViewTime()
                        .split(Constants.ES.HYPHEN_CHARACTER);
                    if (y == Integer.parseInt(dateTime[0])) {
                        double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                        currentPower += item.getEpTotal();
                        json.setLoad(currentPower);
                    }
                }
                for (DataInverter1 item : dataPv) {
                    String dateTime[] = item.getViewTime()
                        .split(Constants.ES.HYPHEN_CHARACTER);
                    if (y == Integer.parseInt(dateTime[0])) {
                        double currentPv = json.getPv() != null ? json.getPv() : 0;
                        currentPv += item.getEp();
                        json.setPv(currentPv);
                    }
                }
                for (DataRmuDrawer1 item : dataGrid) {
                    String dateTime[] = item.getViewTime()
                        .split(Constants.ES.HYPHEN_CHARACTER);
                    if (y == Integer.parseInt(dateTime[0])) {
                        double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                        currentGrid += item.getEpTotal();
                        json.setGrid(currentGrid);
                    }
                }
                json.setTime(year);
                if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                    || json.getWind() != null) {
                    dataChart.add(json);
                }
            });
        } else if (timeType == 2) {
            String from = fromDate;
            String to = toDate;

            map.put("fromDate", from);
            map.put("toDate", to);

            data = dataLoadFrame1Service.getDataChartPower(map);
            dataPv = dataInverterService.getDataChartPV(map);
            dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

            // CHECKSTYLE:OFF
            int daysInMonth = 28;
            // CHECKSTYLE:ON
            String[] date = fromDate.split(Constants.ES.HYPHEN_CHARACTER);
            YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]));
            daysInMonth = yearMonthObject.lengthOfMonth();

            from = fromDate + Constants.ES.HYPHEN_CHARACTER + "01";
            to = toDate + Constants.ES.HYPHEN_CHARACTER + daysInMonth;
            Calendar beginCalendar = Calendar.getInstance();
            Calendar finishCalendar = Calendar.getInstance();

            Date fDate = DateUtils.toDate(from, "yyyy-MM-dd");
            Date tDate = DateUtils.toDate(to, "yyyy-MM-dd");
            beginCalendar.setTime(fDate);
            finishCalendar.setTime(tDate);

            SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM");

            List<String> months = new ArrayList<>();

            while (beginCalendar.before(finishCalendar)) {
                // add one month to date per loop
                String dateMonth = formater.format(beginCalendar.getTime())
                    .toUpperCase();
                months.add(dateMonth);
                beginCalendar.add(Calendar.MONTH, 1);
            }

            // CHECKSTYLE:OFF
            months.forEach(m -> {
                String d[] = m.split(Constants.ES.HYPHEN_CHARACTER);
                int month = Integer.parseInt(d[1]);
                JsonElectricPower json = new JsonElectricPower();
                for (DataLoadFrame1 item : data) {
                    Date dateItem = DateUtils.toDate(item.getViewTime(), "yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateItem);
                    int monthItem = cal.get(Calendar.MONTH);
                    if (month == (monthItem + 1)) {
                        double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                        currentPower += item.getEpTotal();
                        json.setLoad(currentPower);
                    }
                }
                for (DataInverter1 item : dataPv) {
                    Date dateItem = DateUtils.toDate(item.getViewTime(), "yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateItem);
                    int monthItem = cal.get(Calendar.MONTH);
                    if (month == (monthItem + 1)) {
                        double currentPv = json.getPv() != null ? json.getPv() : 0;
                        currentPv += item.getEp();
                        json.setLoad(currentPv);
                    }
                }
                for (DataRmuDrawer1 item : dataGrid) {
                    Date dateItem = DateUtils.toDate(item.getViewTime(), "yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateItem);
                    int monthItem = cal.get(Calendar.MONTH);
                    if (month == (monthItem + 1)) {
                        double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                        currentGrid += item.getEpTotal();
                        json.setLoad(currentGrid);
                    }
                }
                json.setTime(m);
                if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                    || json.getWind() != null) {
                    dataChart.add(json);
                }
            });
        } else {
            map.put("fromDate", fromDate);
            map.put("toDate", toDate);

            data = dataLoadFrame1Service.getDataChartPower(map);
            dataPv = dataInverterService.getDataChartPV(map);
            dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

            LocalDate start = LocalDate.parse(fromDate);
            LocalDate end = LocalDate.parse(toDate);
            List<LocalDate> totalDates = new ArrayList<>();
            while (!start.isAfter(end)) {
                totalDates.add(start);
                start = start.plusDays(1);
            }

            totalDates.forEach(date -> {
                Date d = java.sql.Date.valueOf(date);
                JsonElectricPower json = new JsonElectricPower();
                for (DataLoadFrame1 loadFrame : data) {
                    // set data chart bar
                    if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                        DateUtils.toDate(loadFrame.getViewTime(), "yyyy-MM-dd"))) {
                        double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                        currentPower += loadFrame.getEpTotal();
                        json.setLoad(currentPower);
                    }
                }
                dataPv.forEach(pvFrame -> {
                    if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                        DateUtils.toDate(pvFrame.getViewTime(), "yyyy-MM-dd"))) {
                        double currentPv = json.getPv() != null ? json.getPv() : 0;
                        currentPv += pvFrame.getEp();
                        json.setPv(currentPv);
                    }
                });
                dataGrid.forEach(gridFrame -> {
                    if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                        DateUtils.toDate(gridFrame.getViewTime(), "yyyy-MM-dd"))) {
                        double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                        currentGrid += gridFrame.getEpTotal();
                        json.setGrid(currentGrid);
                    }
                });
                json.setTime(DateUtils.toString(d, "yyyy-MM-dd"));
                if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                    || json.getWind() != null) {
                    dataChart.add(json);
                }
            });

        }

        // tạo excel
        if (data != null && data.size() > 0) {

            Manager manager = managerService.getManagerById(Integer.valueOf(managerId));

            createExcelManager(dataChart, fromDate, toDate, manager, path, imageData, miliseconds);

            // gửi zip qua client
            String contentType = "application/zip";
            String headerValue = "attachment; filename=" + miliseconds + ".zip";

            Path realPath = Paths.get(path + ".zip");
            Resource resource = null;
            try {
                resource = new UrlResource(realPath.toUri());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .body(resource);
        } else {
            return ResponseEntity.badRequest()
                .body(null);
        }
    }

    private void createExcelManager(final List<JsonElectricPower> dataChart, final String fromDate, final String toDate,
        final Manager manager, final String path, final byte[] imageData, final Long miliseconds) throws Exception {
        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(dataChart.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Sản lượng điện năng");
        Row row;
        org.apache.poi.ss.usermodel.Cell cell;
        DataFormat fmt = wb.createDataFormat();
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(fmt.getFormat("@"));

        // add image
        int pictureIdx = wb.addPicture(imageData, wb.PICTURE_TYPE_PNG);
        SXSSFDrawing drawingImg = sheet.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(6);
        anchorImg.setCol2(7);
        anchorImg.setRow1(1);
        anchorImg.setRow2(4);

        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);

        // Page orientation
        sheet.getPrintSetup()
            .setLandscape(false);

        // Page margins
        sheet.setMargin(Sheet.RightMargin, 0.5);
        sheet.setMargin(Sheet.LeftMargin, 0.5);
        sheet.setMargin(Sheet.TopMargin, 0.5);
        sheet.setMargin(Sheet.BottomMargin, 0.5);

        // Tạo sheet content
        for (int i = 0; i < 6; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 36; j++) {
                row.createCell(j);
            }
        }

        // set độ rộng của cột
        sheet.setColumnWidth(0, 1300);
        sheet.setColumnWidth(1, 5200);
        sheet.setColumnWidth(2, 5200);
        sheet.setColumnWidth(3, 5200);
        sheet.setColumnWidth(4, 5200);
        sheet.setColumnWidth(5, 5200);
        sheet.setColumnWidth(6, 5200);

        // set độ rộng của hàng
        Row row1 = sheet.getRow(1);
        row1.setHeight((short) -500);
        Row row2 = sheet.getRow(2);
        row2.setHeight((short) -500);
        Row row3 = sheet.getRow(3);
        row3.setHeight((short) -500);

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 6);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(0)
            .getCell(0);
        cell.setCellValue("SẢN LƯỢNG ĐIỆN NĂNG");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(0);
        cell.setCellValue("Mã tỉnh thành");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(2, 2, 2, 3);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(2);
        cell.setCellValue(manager.getManagerId());
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(3, 3, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(0);
        cell.setCellValue("Tên tỉnh thành");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(3, 3, 2, 3);
        cell = sheet.getRow(3)
            .getCell(2);
        cell.setCellValue(manager != null && manager.getManagerName() != null
            ? manager.getManagerName()
                .toUpperCase()
            : "");
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(2, 3, 4, 4);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(4);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 5, 5);
        cell = sheet.getRow(2)
            .getCell(5);
        cell.setCellValue(fromDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        region = new CellRangeAddress(3, 3, 5, 5);
        cell = sheet.getRow(3)
            .getCell(5);
        cell.setCellValue(toDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        // bảng sản lượng điện năng
        region = new CellRangeAddress(5, 5, 0, 0);
        cell = sheet.getRow(5)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 1, 1);
        cell = sheet.getRow(5)
            .getCell(1);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 2, 2);
        cell = sheet.getRow(5)
            .getCell(2);
        cell.setCellValue("LOAD[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("PV[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("Grid[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("Wind[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 6, 6);
        cell = sheet.getRow(5)
            .getCell(6);
        cell.setCellValue("Battery[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        int index = 6;

        for (int m = 0; m < dataChart.size(); m++) {
            JsonElectricPower item = dataChart.get(m);

            for (int i = index; i < index + 1; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 36; j++) {
                    Cell c = row.createCell(j, CellType.BLANK);
                    c.setCellStyle(cs);
                }
            }

            // Cột thứ tự
            region = new CellRangeAddress(index, index, 0, 0);
            cell = sheet.getRow(index)
                .getCell(0);
            cell.setCellValue(m + 1);

            // Cột Thời gian
            region = new CellRangeAddress(index, index, 1, 1);
            cell = sheet.getRow(index)
                .getCell(1);
            cell.setCellValue(item.getTime());

            // Cột LOAD
            region = new CellRangeAddress(index, index, 2, 2);
            cell = sheet.getRow(index)
                .getCell(2);
            cell.setCellValue(item.getLoad() != null ? String.valueOf(item.getLoad()) : "-");

            // Cột PV
            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getPv() != null ? String.valueOf(item.getPv()) : "-");

            // Cột GRID
            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue(item.getGrid() != null ? String.valueOf(item.getGrid()) : "-");

            // Cột WIND
            region = new CellRangeAddress(index, index, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue(item.getWind() != null ? String.valueOf(item.getWind()) : "-");

            // Cột Battery
            region = new CellRangeAddress(index, index, 6, 6);
            cell = sheet.getRow(index)
                .getCell(6);
            cell.setCellValue(item.getEv() != null ? String.valueOf(item.getEv()) : "-");

            index += 1;
        }

        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // tạo file excel vào folder export
        String exportFilePath = path + File.separator + miliseconds + ".xlsx";

        File file = new File(exportFilePath);

        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("HomeController: Create file excel success");
        } catch (FileNotFoundException e) {
            log.error("HomeController: ERROR File Not Found while export file excel.");
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                wb.dispose();
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // zip folder
        ZipUtil.pack(folder, new File(path + ".zip"));

        log.info("HomeController.createExcel() end");
    }

    @GetMapping ("/chartArea/download")
    public ResponseEntity<Resource> downloadChartArea(@RequestParam ("customerId") final Integer customerId,
        @RequestParam ("superManagerId") final Integer superManagerId,
        @RequestParam ("managerId") final Integer managerId, @RequestParam ("areaId") final Integer areaId,
        @RequestParam final Integer timeType, @RequestParam final String fromDate, @RequestParam final String toDate,
        @RequestParam final String userName) throws Exception {
        // get url image
        User user = userService.getUserByUsername(userName);
        String pngImageURL = user.getImg();
        String encodingPrefix = "base64,";
        int contentStartIndex = pngImageURL.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData = org.apache.commons.codec.binary.Base64
            .decodeBase64(pngImageURL.substring(contentStartIndex));

        Map<String, String> condition = new HashMap<>();
        condition.put("customerId", String.valueOf(customerId));
        condition.put("superManagerId", String.valueOf(superManagerId));
        condition.put("managerId", String.valueOf(managerId));
        condition.put("areaId", String.valueOf(areaId));
        String[] deviceIds = deviceService.getDeviceByArea(condition);
        String deviceId = String.join(",", deviceIds);
        Map<String, String> map = new HashMap<>();
        map.put("schema", Schema.getSchemas(customerId));
        map.put("deviceId", deviceId);
        map.put("viewType", String.valueOf(timeType));

        List<JsonElectricPower> dataChart = new ArrayList<>();

        List<DataLoadFrame1> data;

        List<DataInverter1> dataPv;

        List<DataRmuDrawer1> dataGrid;

        // time miliseconds
        long miliseconds = new Date().getTime();

        // path folder
        String path = this.folderName + File.separator + miliseconds;

        if (timeType == 1) {
            String from = fromDate;
            String to = toDate;

            map.put("fromDate", from);
            map.put("toDate", to);

            data = dataLoadFrame1Service.getDataChartPower(map);
            dataPv = dataInverterService.getDataChartPV(map);
            dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

            List<String> years = new ArrayList<>();

            for (int i = Integer.parseInt(fromDate); i <= Integer.parseInt(toDate); i++) {
                years.add(String.valueOf(i));
            }

            years.forEach(year -> {
                int y = Integer.parseInt(year);
                // CHECKSTYLE:OFF
                JsonElectricPower json = new JsonElectricPower();
                for (DataLoadFrame1 item : data) {
                    String dateTime[] = item.getViewTime()
                        .split(Constants.ES.HYPHEN_CHARACTER);
                    if (y == Integer.parseInt(dateTime[0])) {
                        double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                        currentPower += item.getEpTotal();
                        json.setLoad(currentPower);
                    }
                }
                for (DataInverter1 item : dataPv) {
                    String dateTime[] = item.getViewTime()
                        .split(Constants.ES.HYPHEN_CHARACTER);
                    if (y == Integer.parseInt(dateTime[0])) {
                        double currentPv = json.getPv() != null ? json.getPv() : 0;
                        currentPv += item.getEp();
                        json.setPv(currentPv);
                    }
                }
                for (DataRmuDrawer1 item : dataGrid) {
                    String dateTime[] = item.getViewTime()
                        .split(Constants.ES.HYPHEN_CHARACTER);
                    if (y == Integer.parseInt(dateTime[0])) {
                        double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                        currentGrid += item.getEpTotal();
                        json.setGrid(currentGrid);
                    }
                }
                json.setTime(year);
                if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                    || json.getWind() != null) {
                    dataChart.add(json);
                }
            });
        } else if (timeType == 2) {
            String from = fromDate;
            String to = toDate;

            map.put("fromDate", from);
            map.put("toDate", to);

            data = dataLoadFrame1Service.getDataChartPower(map);
            dataPv = dataInverterService.getDataChartPV(map);
            dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

            // CHECKSTYLE:OFF
            int daysInMonth = 28;
            // CHECKSTYLE:ON
            String[] date = fromDate.split(Constants.ES.HYPHEN_CHARACTER);
            YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]));
            daysInMonth = yearMonthObject.lengthOfMonth();

            from = fromDate + Constants.ES.HYPHEN_CHARACTER + "01";
            to = toDate + Constants.ES.HYPHEN_CHARACTER + daysInMonth;
            Calendar beginCalendar = Calendar.getInstance();
            Calendar finishCalendar = Calendar.getInstance();

            Date fDate = DateUtils.toDate(from, "yyyy-MM-dd");
            Date tDate = DateUtils.toDate(to, "yyyy-MM-dd");
            beginCalendar.setTime(fDate);
            finishCalendar.setTime(tDate);

            SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM");

            List<String> months = new ArrayList<>();

            while (beginCalendar.before(finishCalendar)) {
                // add one month to date per loop
                String dateMonth = formater.format(beginCalendar.getTime())
                    .toUpperCase();
                months.add(dateMonth);
                beginCalendar.add(Calendar.MONTH, 1);
            }

            // CHECKSTYLE:OFF
            months.forEach(m -> {
                String d[] = m.split(Constants.ES.HYPHEN_CHARACTER);
                int month = Integer.parseInt(d[1]);
                JsonElectricPower json = new JsonElectricPower();
                for (DataLoadFrame1 item : data) {
                    Date dateItem = DateUtils.toDate(item.getViewTime(), "yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateItem);
                    int monthItem = cal.get(Calendar.MONTH);
                    if (month == (monthItem + 1)) {
                        double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                        currentPower += item.getEpTotal();
                        json.setLoad(currentPower);
                    }
                }
                for (DataInverter1 item : dataPv) {
                    Date dateItem = DateUtils.toDate(item.getViewTime(), "yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateItem);
                    int monthItem = cal.get(Calendar.MONTH);
                    if (month == (monthItem + 1)) {
                        double currentPv = json.getPv() != null ? json.getPv() : 0;
                        currentPv += item.getEp();
                        json.setLoad(currentPv);
                    }
                }
                for (DataRmuDrawer1 item : dataGrid) {
                    Date dateItem = DateUtils.toDate(item.getViewTime(), "yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateItem);
                    int monthItem = cal.get(Calendar.MONTH);
                    if (month == (monthItem + 1)) {
                        double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                        currentGrid += item.getEpTotal();
                        json.setLoad(currentGrid);
                    }
                }
                json.setTime(m);
                if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                    || json.getWind() != null) {
                    dataChart.add(json);
                }
            });
        } else {
            map.put("fromDate", fromDate);
            map.put("toDate", toDate);

            data = dataLoadFrame1Service.getDataChartPower(map);
            dataPv = dataInverterService.getDataChartPV(map);
            dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

            LocalDate start = LocalDate.parse(fromDate);
            LocalDate end = LocalDate.parse(toDate);
            List<LocalDate> totalDates = new ArrayList<>();
            while (!start.isAfter(end)) {
                totalDates.add(start);
                start = start.plusDays(1);
            }

            totalDates.forEach(date -> {
                Date d = java.sql.Date.valueOf(date);
                JsonElectricPower json = new JsonElectricPower();
                for (DataLoadFrame1 loadFrame : data) {
                    // set data chart bar
                    if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                        DateUtils.toDate(loadFrame.getViewTime(), "yyyy-MM-dd"))) {
                        double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                        currentPower += loadFrame.getEpTotal();
                        json.setLoad(currentPower);
                    }
                }
                dataPv.forEach(pvFrame -> {
                    if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                        DateUtils.toDate(pvFrame.getViewTime(), "yyyy-MM-dd"))) {
                        double currentPv = json.getPv() != null ? json.getPv() : 0;
                        currentPv += pvFrame.getEp();
                        json.setPv(currentPv);
                    }
                });
                dataGrid.forEach(gridFrame -> {
                    if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                        DateUtils.toDate(gridFrame.getViewTime(), "yyyy-MM-dd"))) {
                        double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                        currentGrid += gridFrame.getEpTotal();
                        json.setGrid(currentGrid);
                    }
                });
                json.setTime(DateUtils.toString(d, "yyyy-MM-dd"));
                if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                    || json.getWind() != null) {
                    dataChart.add(json);
                }
            });

        }

        // tạo excel
        if (data != null && data.size() > 0) {
            Map<String, String> cond = new HashMap<>();
            cond.put("areaId", String.valueOf(areaId));
            Area area = areaService.getAreaDownload(cond);

            createExcelArea(dataChart, fromDate, toDate, area, path, imageData, miliseconds);

            // gửi zip qua client
            String contentType = "application/zip";
            String headerValue = "attachment; filename=" + miliseconds + ".zip";

            Path realPath = Paths.get(path + ".zip");
            Resource resource = null;
            try {
                resource = new UrlResource(realPath.toUri());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .body(resource);
        } else {
            return ResponseEntity.badRequest()
                .body(null);
        }
    }

    private void createExcelArea(final List<JsonElectricPower> dataChart, final String fromDate, final String toDate,
        final Area area, final String path, final byte[] imageData, final Long miliseconds) throws Exception {

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(dataChart.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Sản lượng điện năng");
        Row row;
        org.apache.poi.ss.usermodel.Cell cell;
        DataFormat fmt = wb.createDataFormat();
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(fmt.getFormat("@"));

        // add image
        int pictureIdx = wb.addPicture(imageData, wb.PICTURE_TYPE_PNG);
        SXSSFDrawing drawingImg = sheet.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(6);
        anchorImg.setCol2(7);
        anchorImg.setRow1(1);
        anchorImg.setRow2(4);

        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);

        // Page orientation
        sheet.getPrintSetup()
            .setLandscape(false);

        // Page margins
        sheet.setMargin(Sheet.RightMargin, 0.5);
        sheet.setMargin(Sheet.LeftMargin, 0.5);
        sheet.setMargin(Sheet.TopMargin, 0.5);
        sheet.setMargin(Sheet.BottomMargin, 0.5);

        // Tạo sheet content
        for (int i = 0; i < 6; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 36; j++) {
                row.createCell(j);
            }
        }

        // set độ rộng của cột
        sheet.setColumnWidth(0, 1300);
        sheet.setColumnWidth(1, 5200);
        sheet.setColumnWidth(2, 5200);
        sheet.setColumnWidth(3, 5200);
        sheet.setColumnWidth(4, 5200);
        sheet.setColumnWidth(5, 5200);
        sheet.setColumnWidth(6, 5200);

        // set độ rộng của hàng
        Row row1 = sheet.getRow(1);
        row1.setHeight((short) -500);
        Row row2 = sheet.getRow(2);
        row2.setHeight((short) -500);
        Row row3 = sheet.getRow(3);
        row3.setHeight((short) -500);

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 6);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(0)
            .getCell(0);
        cell.setCellValue("SẢN LƯỢNG ĐIỆN NĂNG");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(0);
        cell.setCellValue("Mã quận huyện");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(2, 2, 2, 3);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(2);
        cell.setCellValue(area.getAreaId());
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(3, 3, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(0);
        cell.setCellValue("Tên quận huyện");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(3, 3, 2, 3);
        cell = sheet.getRow(3)
            .getCell(2);
        cell.setCellValue(area != null && area.getAreaName() != null
            ? area.getAreaName()
                .toUpperCase()
            : "");
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(2, 3, 4, 4);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(4);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 5, 5);
        cell = sheet.getRow(2)
            .getCell(5);
        cell.setCellValue(fromDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        region = new CellRangeAddress(3, 3, 5, 5);
        cell = sheet.getRow(3)
            .getCell(5);
        cell.setCellValue(toDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        // bảng sản lượng điện năng
        region = new CellRangeAddress(5, 5, 0, 0);
        cell = sheet.getRow(5)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 1, 1);
        cell = sheet.getRow(5)
            .getCell(1);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 2, 2);
        cell = sheet.getRow(5)
            .getCell(2);
        cell.setCellValue("LOAD[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("PV[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("Grid[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("Wind[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 6, 6);
        cell = sheet.getRow(5)
            .getCell(6);
        cell.setCellValue("Battery[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        int index = 6;

        for (int m = 0; m < dataChart.size(); m++) {
            JsonElectricPower item = dataChart.get(m);

            for (int i = index; i < index + 1; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 36; j++) {
                    Cell c = row.createCell(j, CellType.BLANK);
                    c.setCellStyle(cs);
                }
            }

            // Cột thứ tự
            region = new CellRangeAddress(index, index, 0, 0);
            cell = sheet.getRow(index)
                .getCell(0);
            cell.setCellValue(m + 1);

            // Cột Thời gian
            region = new CellRangeAddress(index, index, 1, 1);
            cell = sheet.getRow(index)
                .getCell(1);
            cell.setCellValue(item.getTime());

            // Cột LOAD
            region = new CellRangeAddress(index, index, 2, 2);
            cell = sheet.getRow(index)
                .getCell(2);
            cell.setCellValue(item.getLoad() != null ? String.valueOf(item.getLoad()) : "-");

            // Cột PV
            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getPv() != null ? String.valueOf(item.getPv()) : "-");

            // Cột GRID
            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue(item.getGrid() != null ? String.valueOf(item.getGrid()) : "-");

            // Cột WIND
            region = new CellRangeAddress(index, index, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue(item.getWind() != null ? String.valueOf(item.getWind()) : "-");

            // Cột Battery
            region = new CellRangeAddress(index, index, 6, 6);
            cell = sheet.getRow(index)
                .getCell(6);
            cell.setCellValue(item.getEv() != null ? String.valueOf(item.getEv()) : "-");

            index += 1;
        }

        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // tạo file excel vào folder export
        String exportFilePath = path + File.separator + miliseconds + ".xlsx";

        File file = new File(exportFilePath);

        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("HomeController: Create file excel success");
        } catch (FileNotFoundException e) {
            log.error("HomeController: ERROR File Not Found while export file excel.");
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                wb.dispose();
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // zip folder
        ZipUtil.pack(folder, new File(path + ".zip"));

        log.info("HomeController.createExcel() end");
    }

    @GetMapping ("/chartAll/download")
    public ResponseEntity<Resource> downloadChartAll(@RequestParam final Integer timeType,
        @RequestParam final String fromDate, @RequestParam final String toDate, @RequestParam final String userName)
        throws Exception {
        // get url image
        User user = userService.getUserByUsername(userName);
        String pngImageURL = user.getImg();
        String encodingPrefix = "base64,";
        int contentStartIndex = pngImageURL.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData = org.apache.commons.codec.binary.Base64
            .decodeBase64(pngImageURL.substring(contentStartIndex));
        String[] deviceIds = deviceService.getAllDeviceByCalculate();
        String deviceId = String.join(",", deviceIds);
        Map<String, String> map = new HashMap<>();
        map.put("deviceId", deviceId);
        map.put("viewType", String.valueOf(timeType));
        List<JsonElectricPower> dataChart = new ArrayList<>();

        // time miliseconds
        long miliseconds = new Date().getTime();

        // path folder
        String path = this.folderName + File.separator + miliseconds;
        List<Customer> customer = customerService.getListCustomer();
        for (Customer cus : customer) {
            map.put("schema", Schema.getSchemas(cus.getCustomerId()));
            if (timeType == 1) {
                String from = fromDate;
                String to = toDate;

                map.put("fromDate", from);
                map.put("toDate", to);

                List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

                List<String> years = new ArrayList<>();

                for (int i = Integer.parseInt(fromDate); i <= Integer.parseInt(toDate); i++) {
                    years.add(String.valueOf(i));
                }

                years.forEach(year -> {
                    int y = Integer.parseInt(year);
                    // CHECKSTYLE:OFF
                    JsonElectricPower json = new JsonElectricPower();
                    for (DataLoadFrame1 item : data) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        if (y == Integer.parseInt(dateTime[0])) {
                            double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                            currentPower += item.getEpTotal();
                            json.setLoad(currentPower);
                        }
                    }
                    for (DataInverter1 item : dataPv) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        if (y == Integer.parseInt(dateTime[0])) {
                            double currentPv = json.getPv() != null ? json.getPv() : 0;
                            currentPv += item.getEp();
                            json.setPv(currentPv);
                        }
                    }
                    for (DataRmuDrawer1 item : dataGrid) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        if (y == Integer.parseInt(dateTime[0])) {
                            double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                            currentGrid += item.getEpTotal();
                            json.setGrid(currentGrid);
                        }
                    }
                    json.setTime(year);
                    if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                        || json.getWind() != null) {
                        dataChart.add(json);
                    }
                });

            } else if (timeType == 2) {
                String from = fromDate;
                String to = toDate;

                map.put("fromDate", from);
                map.put("toDate", to);

                List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

                // CHECKSTYLE:OFF
                int daysInMonth = 28;
                // CHECKSTYLE:ON
                String[] date = fromDate.split(Constants.ES.HYPHEN_CHARACTER);
                YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]));
                daysInMonth = yearMonthObject.lengthOfMonth();

                from = fromDate + Constants.ES.HYPHEN_CHARACTER + "01";
                to = toDate + Constants.ES.HYPHEN_CHARACTER + daysInMonth;

                Calendar beginCalendar = Calendar.getInstance();
                Calendar finishCalendar = Calendar.getInstance();

                Date fDate = DateUtils.toDate(from, "yyyy-MM-dd");
                Date tDate = DateUtils.toDate(to, "yyyy-MM-dd");
                beginCalendar.setTime(fDate);
                finishCalendar.setTime(tDate);

                SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM");

                List<String> months = new ArrayList<>();

                while (beginCalendar.before(finishCalendar)) {
                    // add one month to date per loop
                    String dateMonth = formater.format(beginCalendar.getTime())
                        .toUpperCase();
                    months.add(dateMonth);
                    beginCalendar.add(Calendar.MONTH, 1);
                }

                // CHECKSTYLE:OFF
                months.forEach(m -> {
                    String d[] = m.split(Constants.ES.HYPHEN_CHARACTER);
                    int year = Integer.parseInt(d[0]);
                    int month = Integer.parseInt(d[1]);
                    YearMonth firstYearMonth = YearMonth.of(year, month);
                    JsonElectricPower json = new JsonElectricPower();
                    for (DataLoadFrame1 item : data) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        int yearItem = Integer.parseInt(dateTime[0]);
                        int monthItem = Integer.parseInt(dateTime[1]);
                        YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                        if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                            double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                            currentPower += item.getEpTotal();
                            json.setLoad(currentPower);

                        }
                    }
                    for (DataInverter1 item : dataPv) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        int yearItem = Integer.parseInt(dateTime[0]);
                        int monthItem = Integer.parseInt(dateTime[1]);
                        YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                        if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                            double currentPv = json.getPv() != null ? json.getPv() : 0;
                            currentPv += item.getEp();
                            json.setLoad(currentPv);
                        }
                    }
                    for (DataRmuDrawer1 item : dataGrid) {
                        String dateTime[] = item.getViewTime()
                            .split(Constants.ES.HYPHEN_CHARACTER);
                        int yearItem = Integer.parseInt(dateTime[0]);
                        int monthItem = Integer.parseInt(dateTime[1]);
                        YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                        if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                            double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                            currentGrid += item.getEpTotal();
                            json.setGrid(currentGrid);
                        }
                    }
                    json.setTime(m);
                    if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                        || json.getWind() != null) {
                        dataChart.add(json);
                    }
                });

            } else {
                map.put("fromDate", fromDate);
                map.put("toDate", toDate);

                List<DataLoadFrame1> data = dataLoadFrame1Service.getDataChartPower(map);
                List<DataInverter1> dataPv = dataInverterService.getDataChartPV(map);
                List<DataRmuDrawer1> dataGrid = dataRmuDrawer1Service.getDataChartGrid(map);

                LocalDate start = LocalDate.parse(fromDate);
                LocalDate end = LocalDate.parse(toDate);
                List<LocalDate> totalDates = new ArrayList<>();
                while (!start.isAfter(end)) {
                    totalDates.add(start);
                    start = start.plusDays(1);
                }

                totalDates.forEach(date -> {
                    Date d = java.sql.Date.valueOf(date);
                    JsonElectricPower json = new JsonElectricPower();
                    data.forEach(loadFrame -> {
                        // set data chart bar
                        if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                            DateUtils.toDate(loadFrame.getViewTime(), "yyyy-MM-dd"))) {
                            double currentPower = json.getLoad() != null ? json.getLoad() : 0;
                            currentPower += loadFrame.getEpTotal();
                            json.setLoad(currentPower);
                        }
                    });
                    dataPv.forEach(pvFrame -> {
                        if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                            DateUtils.toDate(pvFrame.getViewTime(), "yyyy-MM-dd"))) {
                            double currentPv = json.getPv() != null ? json.getPv() : 0;
                            currentPv += pvFrame.getEp();
                            json.setPv(currentPv);
                        }
                    });
                    dataGrid.forEach(gridFrame -> {
                        if (org.apache.commons.lang.time.DateUtils.isSameDay(d,
                            DateUtils.toDate(gridFrame.getViewTime(), "yyyy-MM-dd"))) {
                            double currentGrid = json.getGrid() != null ? json.getGrid() : 0;
                            currentGrid += gridFrame.getEpTotal();
                            json.setGrid(currentGrid);
                        }
                    });
                    json.setTime(DateUtils.toString(d, "yyyy-MM-dd"));
                    if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                        || json.getWind() != null) {
                        dataChart.add(json);
                    }
                });

            }
        }
        List<JsonElectricPower> dataAll = new ArrayList<>();
        if (timeType == 1) {
            String from = fromDate;
            String to = toDate;

            map.put("fromDate", from);
            map.put("toDate", to);

            List<String> years = new ArrayList<>();

            for (int i = Integer.parseInt(fromDate); i <= Integer.parseInt(toDate); i++) {
                years.add(String.valueOf(i));
            }

            years.forEach(year -> {
                int y = Integer.parseInt(year);
                // CHECKSTYLE:OFF
                JsonElectricPower json = new JsonElectricPower();
                for (int i = 0; i < dataChart.size(); i++) {
                    String dateTime[] = dataChart.get(i)
                        .getTime()
                        .split(Constants.ES.HYPHEN_CHARACTER);
                    if (y == Integer.parseInt(dateTime[0])) {
                        if (dataChart.get(i)
                            .getLoad() != null) {
                            double totalLoad = json.getLoad() != null ? json.getLoad() : 0;
                            totalLoad += dataChart.get(i)
                                .getLoad();
                            json.setLoad(totalLoad);
                        } else if (dataChart.get(i)
                            .getPv() != null) {
                            double totalPv = json.getPv() != null ? json.getPv() : 0;
                            totalPv += dataChart.get(i)
                                .getPv();
                            json.setPv(totalPv);
                        } else if (dataChart.get(i)
                            .getGrid() != null) {
                            double totalGrid = json.getGrid() != null ? json.getGrid() : 0;
                            totalGrid += dataChart.get(i)
                                .getGrid();
                            json.setGrid(totalGrid);
                        } else if (dataChart.get(i)
                            .getEv() != null) {
                            double totalEv = json.getEv() != null ? json.getEv() : 0;
                            totalEv += dataChart.get(i)
                                .getEv();
                            json.setEv(totalEv);
                        } else {
                            dataAll.add(null);
                        }
                    }
                }

                json.setTime(year);
                if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                    || json.getWind() != null) {
                    dataAll.add(json);
                }
            });

        } else if (timeType == 2) {
            String from = fromDate;
            String to = toDate;

            map.put("fromDate", from);
            map.put("toDate", to);

            // CHECKSTYLE:OFF
            int daysInMonth = 28;
            // CHECKSTYLE:ON
            String[] date = fromDate.split(Constants.ES.HYPHEN_CHARACTER);
            YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]));
            daysInMonth = yearMonthObject.lengthOfMonth();

            from = fromDate + Constants.ES.HYPHEN_CHARACTER + "01";
            to = toDate + Constants.ES.HYPHEN_CHARACTER + daysInMonth;

            Calendar beginCalendar = Calendar.getInstance();
            Calendar finishCalendar = Calendar.getInstance();

            Date fDate = DateUtils.toDate(from, "yyyy-MM-dd");
            Date tDate = DateUtils.toDate(to, "yyyy-MM-dd");
            beginCalendar.setTime(fDate);
            finishCalendar.setTime(tDate);

            SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM");

            List<String> months = new ArrayList<>();

            while (beginCalendar.before(finishCalendar)) {
                // add one month to date per loop
                String dateMonth = formater.format(beginCalendar.getTime())
                    .toUpperCase();
                months.add(dateMonth);
                beginCalendar.add(Calendar.MONTH, 1);
            }

            // CHECKSTYLE:OFF
            months.forEach(m -> {
                String d[] = m.split(Constants.ES.HYPHEN_CHARACTER);
                int year = Integer.parseInt(d[0]);
                int month = Integer.parseInt(d[1]);
                YearMonth firstYearMonth = YearMonth.of(year, month);
                JsonElectricPower json = new JsonElectricPower();
                for (int i = 0; i < dataChart.size(); i++) {
                    String dateTime[] = dataChart.get(i)
                        .getTime()
                        .split(Constants.ES.HYPHEN_CHARACTER);
                    int yearItem = Integer.parseInt(dateTime[0]);
                    int monthItem = Integer.parseInt(dateTime[1]);
                    YearMonth secondYearMonth = YearMonth.of(yearItem, monthItem);
                    if (firstYearMonth.compareTo(secondYearMonth) == 0) {
                        if (dataChart.get(i)
                            .getLoad() != null) {
                            double totalLoad = json.getLoad() != null ? json.getLoad() : 0;
                            totalLoad += dataChart.get(i)
                                .getLoad();
                            json.setLoad(totalLoad);
                        } else if (dataChart.get(i)
                            .getPv() != null) {
                            double totalPv = json.getPv() != null ? json.getPv() : 0;
                            totalPv += dataChart.get(i)
                                .getPv();
                            json.setPv(totalPv);
                        } else if (dataChart.get(i)
                            .getGrid() != null) {
                            double totalGrid = json.getGrid() != null ? json.getGrid() : 0;
                            totalGrid += dataChart.get(i)
                                .getGrid();
                            json.setGrid(totalGrid);
                        } else if (dataChart.get(i)
                            .getEv() != null) {
                            double totalEv = json.getEv() != null ? json.getEv() : 0;
                            totalEv += dataChart.get(i)
                                .getEv();
                            json.setEv(totalEv);
                        } else {
                            dataAll.add(null);
                        }

                    }
                }
                json.setTime(m);
                if (json.getLoad() != null || json.getGrid() != null || json.getPv() != null
                    || json.getWind() != null) {
                    dataAll.add(json);
                }
            });

        } else {
            map.put("fromDate", fromDate);
            map.put("toDate", toDate);

            LocalDate start = LocalDate.parse(fromDate);
            LocalDate end = LocalDate.parse(toDate);
            List<LocalDate> totalDates = new ArrayList<>();
            while (!start.isAfter(end)) {
                totalDates.add(start);
                start = start.plusDays(1);
            }

            totalDates.forEach(date -> {
                Date d = java.sql.Date.valueOf(date);
                JsonElectricPower json = new JsonElectricPower();
                for (int i = 0; i < dataChart.size(); i++) {
                    // set data chart bar
                    if (org.apache.commons.lang.time.DateUtils.isSameDay(d, DateUtils.toDate(dataChart.get(i)
                        .getTime(), "yyyy-MM-dd"))) {
                        if (dataChart.get(i)
                            .getLoad() != null) {
                            double totalLoad = json.getLoad() != null ? json.getLoad() : 0;
                            totalLoad += dataChart.get(i)
                                .getLoad();
                            json.setLoad(totalLoad);
                        }
                        if (dataChart.get(i)
                            .getPv() != null) {
                            double totalPv = json.getPv() != null ? json.getPv() : 0;
                            totalPv += dataChart.get(i)
                                .getPv();
                            json.setPv(totalPv);
                        }
                        if (dataChart.get(i)
                            .getGrid() != null) {
                            double totalGrid = json.getGrid() != null ? json.getGrid() : 0;
                            totalGrid += dataChart.get(i)
                                .getGrid();
                            json.setGrid(totalGrid);
                        }
                        if (dataChart.get(i)
                            .getEv() != null) {
                            double totalEv = json.getEv() != null ? json.getEv() : 0;
                            totalEv += dataChart.get(i)
                                .getEv();
                            json.setEv(totalEv);
                        }

                    }
                }
                ;
                json.setTime(DateUtils.toString(d, "yyyy-MM-dd"));
                if (json.getLoad() != null || json.getPv() != null || json.getGrid() != null
                    || json.getWind() != null) {
                    dataAll.add(json);
                }
            });
            log.info("chartAll END");
        }

        // tạo excel

        createExcelAll(dataAll, fromDate, toDate, path, imageData, miliseconds);

        // gửi zip qua client
        String contentType = "application/zip";
        String headerValue = "attachment; filename=" + miliseconds + ".zip";

        Path realPath = Paths.get(path + ".zip");
        Resource resource = null;
        try {
            resource = new UrlResource(realPath.toUri());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            .body(resource);
    }

    private void createExcelAll(final List<JsonElectricPower> dataChart, final String fromDate, final String toDate,
        final String path, final byte[] imageData, final Long miliseconds) throws Exception {
        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(dataChart.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Sản lượng điện năng");
        Row row;
        org.apache.poi.ss.usermodel.Cell cell;
        DataFormat fmt = wb.createDataFormat();
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(fmt.getFormat("@"));

        // add image
        int pictureIdx = wb.addPicture(imageData, wb.PICTURE_TYPE_PNG);
        SXSSFDrawing drawingImg = sheet.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(6);
        anchorImg.setCol2(7);
        anchorImg.setRow1(1);
        anchorImg.setRow2(4);

        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);

        // Page orientation
        sheet.getPrintSetup()
            .setLandscape(false);

        // Page margins
        sheet.setMargin(Sheet.RightMargin, 0.5);
        sheet.setMargin(Sheet.LeftMargin, 0.5);
        sheet.setMargin(Sheet.TopMargin, 0.5);
        sheet.setMargin(Sheet.BottomMargin, 0.5);

        // Tạo sheet content
        for (int i = 0; i < 6; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 36; j++) {
                row.createCell(j);
            }
        }

        // set độ rộng của cột
        sheet.setColumnWidth(0, 1300);
        sheet.setColumnWidth(1, 5200);
        sheet.setColumnWidth(2, 5200);
        sheet.setColumnWidth(3, 5200);
        sheet.setColumnWidth(4, 5200);
        sheet.setColumnWidth(5, 5200);
        sheet.setColumnWidth(6, 5200);

        // set độ rộng của hàng
        Row row1 = sheet.getRow(1);
        row1.setHeight((short) -500);
        Row row2 = sheet.getRow(2);
        row2.setHeight((short) -500);
        Row row3 = sheet.getRow(3);
        row3.setHeight((short) -500);

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 6);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(0)
            .getCell(0);
        cell.setCellValue("SẢN LƯỢNG ĐIỆN NĂNG");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 3, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(0);
        cell.setCellValue("Tên khách hàng");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(2, 3, 2, 2);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(2);
        cell.setCellValue("All");
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(2, 3, 3, 4);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 1,
            true);

        region = new CellRangeAddress(2, 2, 5, 5);
        cell = sheet.getRow(2)
            .getCell(5);
        cell.setCellValue(fromDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        region = new CellRangeAddress(3, 3, 5, 5);
        cell = sheet.getRow(3)
            .getCell(5);
        cell.setCellValue(toDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        // bảng sản lượng điện năng
        region = new CellRangeAddress(5, 5, 0, 0);
        cell = sheet.getRow(5)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 1, 1);
        cell = sheet.getRow(5)
            .getCell(1);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 2, 2);
        cell = sheet.getRow(5)
            .getCell(2);
        cell.setCellValue("LOAD[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("PV[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("Grid[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("Wind[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 6, 6);
        cell = sheet.getRow(5)
            .getCell(6);
        cell.setCellValue("Battery[kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        int index = 6;

        for (int m = 0; m < dataChart.size(); m++) {
            JsonElectricPower item = dataChart.get(m);

            for (int i = index; i < index + 1; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 36; j++) {
                    Cell c = row.createCell(j, CellType.BLANK);
                    c.setCellStyle(cs);
                }
            }

            // Cột thứ tự
            region = new CellRangeAddress(index, index, 0, 0);
            cell = sheet.getRow(index)
                .getCell(0);
            cell.setCellValue(m + 1);

            // Cột Thời gian
            region = new CellRangeAddress(index, index, 1, 1);
            cell = sheet.getRow(index)
                .getCell(1);
            cell.setCellValue(item.getTime());

            // Cột LOAD
            region = new CellRangeAddress(index, index, 2, 2);
            cell = sheet.getRow(index)
                .getCell(2);
            cell.setCellValue(item.getLoad() != null ? String.valueOf(item.getLoad()) : "-");

            // Cột PV
            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getPv() != null ? String.valueOf(item.getPv()) : "-");

            // Cột GRID
            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue(item.getGrid() != null ? String.valueOf(item.getGrid()) : "-");

            // Cột WIND
            region = new CellRangeAddress(index, index, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue(item.getWind() != null ? String.valueOf(item.getWind()) : "-");

            // Cột Battery
            region = new CellRangeAddress(index, index, 6, 6);
            cell = sheet.getRow(index)
                .getCell(6);
            cell.setCellValue(item.getEv() != null ? String.valueOf(item.getEv()) : "-");

            index += 1;
        }

        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // tạo file excel vào folder export
        String exportFilePath = path + File.separator + miliseconds + ".xlsx";

        File file = new File(exportFilePath);

        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("HomeController: Create file excel success");
        } catch (FileNotFoundException e) {
            log.error("HomeController: ERROR File Not Found while export file excel.");
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                wb.dispose();
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // zip folder
        ZipUtil.pack(folder, new File(path + ".zip"));

        log.info("HomeController.createExcel() end");
    }
}
