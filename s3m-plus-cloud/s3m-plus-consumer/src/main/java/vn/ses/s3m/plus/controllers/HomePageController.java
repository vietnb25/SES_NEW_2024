package vn.ses.s3m.plus.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import vn.ses.s3m.plus.dto.Chart;
import vn.ses.s3m.plus.dto.Customer;
import vn.ses.s3m.plus.dto.DataInstant;
import vn.ses.s3m.plus.dto.DataInverter1;
import vn.ses.s3m.plus.dto.DataLoadFrame1;
import vn.ses.s3m.plus.dto.DataPower;
import vn.ses.s3m.plus.dto.DataPowerResult;
import vn.ses.s3m.plus.dto.DataRmuDrawer1;
import vn.ses.s3m.plus.dto.DataView;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.LandmarksPlansEnergy;
import vn.ses.s3m.plus.dto.OverviewGridPower;
import vn.ses.s3m.plus.dto.OverviewLoadPower;
import vn.ses.s3m.plus.dto.OverviewPVPower;
import vn.ses.s3m.plus.dto.Project;
import vn.ses.s3m.plus.dto.SystemMap;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.dto.Warning;
import vn.ses.s3m.plus.response.ProjectInfo;
import vn.ses.s3m.plus.response.ProjectLocation;
import vn.ses.s3m.plus.response.SystemTreeData;
import vn.ses.s3m.plus.service.AreaService;
import vn.ses.s3m.plus.service.ChartService;
import vn.ses.s3m.plus.service.CustomerService;
import vn.ses.s3m.plus.service.DataInverterService;
import vn.ses.s3m.plus.service.DataLoadFrame1Service;
import vn.ses.s3m.plus.service.DataRmuDrawer1Service;
import vn.ses.s3m.plus.service.DeviceService;
import vn.ses.s3m.plus.service.LandmarksPlanssEnergyService;
import vn.ses.s3m.plus.service.ManagerService;
import vn.ses.s3m.plus.service.ObjectService;
import vn.ses.s3m.plus.service.OverviewPowerConsumerService;
import vn.ses.s3m.plus.service.ProjectService;
import vn.ses.s3m.plus.service.SettingService;
import vn.ses.s3m.plus.service.SuperManagerService;
import vn.ses.s3m.plus.service.SystemMapService;
import vn.ses.s3m.plus.service.UserRoleService;
import vn.ses.s3m.plus.service.UserService;
import vn.ses.s3m.plus.service.WarningService;

@RestController
@RequestMapping ("/common/homePage")
public class HomePageController {

    private final Log log = LogFactory.getLog(HomeController.class);

    static final int MILLISECOND = 1000;

    static final int FIFTEEN_MINUTE = 900;

    static final int MINUTES_MILLIS = 5;

    private static final String SCHEMA = "schema";

    private static final String PROJECT_ID = "projectId";

    private static final String DEVICE_ID = "deviceId";

    private static final int TRANSACTION_DATE_24_HOURS = 86400;
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

    @Autowired
    private OverviewPowerConsumerService overviewPowerService;

    @Autowired
    private ObjectService objectService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private ChartService chartService;

    @Autowired
    private LandmarksPlanssEnergyService landmarksPlanEnergyService;

    @Value ("${consumer.producer.export-folder}")
    private String folderName;

    @Value ("${time-active-module}")
    private Long timeActiveModule;

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

    private List<SystemTreeData> systemTree(User user) {
        List<SystemTreeData> systemTree = new ArrayList<>();
        // data to create tree
        Map<String, String> condition = new HashMap<>();

        if (user != null && user.getCustomerId() != null) {
            condition.put("customerId", String.valueOf(user.getCustomerId()));
        }

        List<Customer> customers = customerService.getListCustomer(condition);

        if (user != null && user.getUserType() != null) {
            Integer userType = user.getUserType();

            if (userType == 1) {

                for (int i = 0; i < customers.size(); i++) {
                    Customer customer = customers.get(i);

                    SystemTreeData treeDataCustomer = new SystemTreeData();

                    treeDataCustomer.setKey("C-" + String.valueOf(customer.getCustomerId()));
                    treeDataCustomer.setType("customer");
                    treeDataCustomer.setIcon("pi pi-pw pi-user");
                    treeDataCustomer.setLabel(customer.getCustomerName());

                    Map<String, Object> dataCustomer = new HashMap<>();
                    dataCustomer.put("position", String.valueOf(i));
                    dataCustomer.put("customerId", String.valueOf(customer.getCustomerId()));

                    treeDataCustomer.setData(dataCustomer);

                    systemTree.add(treeDataCustomer);
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
    @GetMapping ("/project-marker-customer/{customerId}/{value}")
    public ResponseEntity<?> getProjectMarkerByCustomerId(@PathVariable ("customerId") final Integer customerId,
        @PathVariable ("value") final Integer value, @RequestParam final String fromDate,
        @RequestParam final String toDate, @RequestParam (value = "ids", required = false) final String idsPro) {

        String date = null;
        String type = null;
        String fDate = null;
        String tDate = null;
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter yearFormat = DateTimeFormatter.ofPattern("yyyy");
        LocalDateTime now = LocalDateTime.now();

        // from date to date
        if (value == 0) {
            type = "3";

        }
        // to day
        if (value == 1) {
            date = dateFormat.format(now);
            type = "3";
        }
        // previous day
        if (value == 2) {
            LocalDateTime prevDayDate = now.minusDays(1);
            date = dateFormat.format(prevDayDate);
            type = "3";
        }
        // to month
        if (value == 3) {
            date = monthFormat.format(now);
            type = "2";
        }
        // previous month
        if (value == 4) {
            LocalDateTime prevMonthDate = now.minusMonths(1);
            date = monthFormat.format(prevMonthDate);
            type = "2";
        }
        // previous 3 month
        if (value == 5) {
            LocalDateTime prevMonthDate = now.minusMonths(1);
            LocalDateTime prevThreeMonthDate = now.minusMonths(3);
            fDate = monthFormat.format(prevThreeMonthDate);
            tDate = monthFormat.format(prevMonthDate);
            type = "2";
        }
        // previous 6 month
        if (value == 6) {
            LocalDateTime prevMonthDate = now.minusMonths(1);
            LocalDateTime prevSixMonthDate = now.minusMonths(6);
            fDate = monthFormat.format(prevSixMonthDate);
            tDate = monthFormat.format(prevMonthDate);
            type = "2";
        }
        // to year
        if (value == 7) {
            date = yearFormat.format(now);
            type = "1";
        }
        // previous year
        if (value == 8) {
            LocalDateTime prevYearDate = now.minusYears(1);
            date = yearFormat.format(prevYearDate);
            type = "1";
        }

        // get user role
        Map<String, String> condition = new HashMap<>();

        // list projects
        List<Project> projects = new ArrayList<>();

        // list project response to client
        List<ProjectLocation> projectMarkers = new ArrayList<>();

        condition.put("customerId", String.valueOf(customerId));

        if (idsPro != "" && idsPro != "0") {
            condition.put("ids", idsPro);
        }

        projects = this.projectService.getProjectList(condition);

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

            String schema = Schema.getSchemas(projectMarkers.get(i)
                .getCustomerId());
            conditions.put("today", fifteenDate);
            conditions.put("currentTime", dateNow);
            conditions.put("schema", schema);
            conditions.put("view", type);
            conditions.put("date", date);
            if (value == 0) {
                conditions.put("fromDate", fromDate);
                conditions.put("toDate", toDate);
            }
            if (value == 5 || value == 6) {
                conditions.put("fromDate", fDate);
                conditions.put("toDate", tDate);
            }
            DataLoadFrame1 dataLoadFrame = this.dataLoadFrame1Service.getTotalPowerByProjectId(conditions);
            List<DataLoadFrame1> listDataLoadFrame = dataLoadFrame1Service.getPowerDeviceByProjectId(conditions);

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
            map.put("view", type);
            map.put("date", date);
            if (value == 0) {
                map.put("fromDate", fromDate);
                map.put("toDate", toDate);
            }
            if (value == 5 || value == 6) {
                map.put("fromDate", fDate);
                map.put("toDate", tDate);
            }
            Device dataPvFrame = this.dataInverterService.getTotalPowerByProjectId(map);
            List<DataInverter1> listDataPvFrame = dataInverterService.getPowerDeviceByProjectId(map);

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
            con.put("view", type);
            con.put("date", date);
            if (value == 0) {
                con.put("fromDate", fromDate);
                con.put("toDate", toDate);
            }
            if (value == 5 || value == 6) {
                con.put("fromDate", fDate);
                con.put("toDate", tDate);
            }
            DataRmuDrawer1 dataGridFrame = this.dataRmuDrawer1Service.getTotalPowerByProjectId(con);

            // LOAD MODULE
            List<SystemMap> systemMaps = systemMapService.checkSystemMap(conditions);

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
                if (listDataLoadFrame != null) {
                    Float pTotalLoad = (float) 0;
                    Float epTotalLoad = (float) 0;
                    for (DataLoadFrame1 loadFrame : listDataLoadFrame) {
                        pTotalLoad += loadFrame.getPTotal();
                        epTotalLoad += loadFrame.getEp();
                    }
                    projectMarkers.get(i)
                        .setPTotal(pTotalLoad);
                    projectMarkers.get(i)
                        .setQTotal(epTotalLoad);
                }
                if (dataLoadFrame != null) {
                    Integer deviceStatus = warningService.countProjectWarning(conditions);

                    if (deviceStatus > 0) {
                        projectMarkers.get(i)
                            .setLoadStatus(Constants.ModuleStatus.WARNING);
                    } else {
                        projectMarkers.get(i)
                            .setLoadStatus(Constants.ModuleStatus.ACTIVE);
                    }

                    if (dataLoadFrame != null) {
                        // CHECKSTYLE:OFF
                        Date lastSendDate = DateUtils.toDate(dataLoadFrame.getSentDate(),
                            Constants.ES.DATETIME_FORMAT_YMDHMS);
                        Long currentTimes = new Date().getTime();
                        if (currentTimes - (lastSendDate != null ? lastSendDate.getTime() : 0) >= timeActiveModule) {
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
                if (listDataPvFrame != null) {
                    Float pTotalSolar = (float) 0;
                    Float epTotalSolar = (float) 0;
                    for (DataInverter1 solarFrame : listDataPvFrame) {
                        pTotalSolar += solarFrame.getPtotal() != null ? solarFrame.getPtotal() : 0;
                        epTotalSolar += solarFrame.getEp();
                    }
                    projectMarkers.get(i)
                        .setPSolar(pTotalSolar);
                    projectMarkers.get(i)
                        .setQSolar(epTotalSolar);
                }
                if (dataPvFrame != null) {
                    Integer deviceStatus = warningService.countProjectWarning(map);

                    if (deviceStatus > 0) {
                        projectMarkers.get(i)
                            .setPvStatus(Constants.ModuleStatus.WARNING);
                    } else {
                        projectMarkers.get(i)
                            .setPvStatus(Constants.ModuleStatus.ACTIVE);
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
                    Integer deviceStatus = warningService.countProjectWarning(con);

                    if (deviceStatus > 0) {
                        projectMarkers.get(i)
                            .setGridStatus(Constants.ModuleStatus.WARNING);
                    } else {
                        projectMarkers.get(i)
                            .setGridStatus(Constants.ModuleStatus.ACTIVE);
                    }

                    if (dataGridFrame != null) {
                        // CHECKSTYLE:OFF
                        Date lastSendDate = DateUtils.toDate(dataGridFrame.getSentDate(),
                            Constants.ES.DATETIME_FORMAT_YMDHMS);
                        Long currentTimes = new Date().getTime();
                        if (currentTimes - (lastSendDate != null ? lastSendDate.getTime() : 0) >= timeActiveModule) {
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
            if (checkWindSystem) {
                if (dataLoadFrame != null) {
                    Integer deviceStatus = warningService.countProjectWarning(conditions);

                    if (deviceStatus > 0) {
                        projectMarkers.get(i)
                            .setWindStatus(Constants.ModuleStatus.WARNING);
                    } else {
                        projectMarkers.get(i)
                            .setWindStatus(Constants.ModuleStatus.ACTIVE);
                    }

                    if (dataLoadFrame != null) {
                        projectMarkers.get(i)
                            .setWindPower((double) (dataLoadFrame.getPTotal() != null ? dataLoadFrame.getPTotal() : 0));
                        projectMarkers.get(i)
                            .setCurrentTime(dataLoadFrame.getSentDate() != null ? dataLoadFrame.getSentDate() : null);
                        projectMarkers.get(i)
                            .setPWind(dataLoadFrame.getPTotal());
                        projectMarkers.get(i)
                            .setQWind(dataLoadFrame.getQTotal());
                        // CHECKSTYLE:OFF
                        Date lastSendDate = DateUtils.toDate(dataLoadFrame.getSentDate(),
                            Constants.ES.DATETIME_FORMAT_YMDHMS);
                        Long currentTimes = new Date().getTime();
                        if (currentTimes - (lastSendDate != null ? lastSendDate.getTime() : 0) >= timeActiveModule) {
                            projectMarkers.get(i)
                                .setWindStatus(Constants.ModuleStatus.OFFLINE);
                        }
                        // CHECKSTYLE:ON
                    }
                } else {
                    int count = dataLoadFrame1Service.countCurrentData(conditions);
                    if (count > 0) {
                        projectMarkers.get(i)
                            .setWindStatus(Constants.ModuleStatus.OFFLINE);
                    } else {
                        projectMarkers.get(i)
                            .setWindStatus(Constants.ModuleStatus.IN_ACTIVE);
                    }
                }
            } else {
                projectMarkers.get(i)
                    .setWindStatus(Constants.ModuleStatus.IN_ACTIVE);
            }

            if (checkBatterySystem) {
                if (dataLoadFrame != null) {
                    Integer deviceStatus = warningService.countProjectWarning(conditions);

                    if (deviceStatus > 0) {
                        projectMarkers.get(i)
                            .setBatteryStatus(Constants.ModuleStatus.WARNING);
                    } else {
                        projectMarkers.get(i)
                            .setBatteryStatus(Constants.ModuleStatus.ACTIVE);
                    }

                    if (dataLoadFrame != null) {
                        projectMarkers.get(i)
                            .setBatteryPower(
                                (double) (dataLoadFrame.getPTotal() != null ? dataLoadFrame.getPTotal() : 0));
                        projectMarkers.get(i)
                            .setCurrentTime(dataLoadFrame.getSentDate() != null ? dataLoadFrame.getSentDate() : null);
                        projectMarkers.get(i)
                            .setPBattery(dataLoadFrame.getPTotal());
                        projectMarkers.get(i)
                            .setQBattery(dataLoadFrame.getQTotal());
                        // CHECKSTYLE:OFF
                        Date lastSendDate = DateUtils.toDate(dataLoadFrame.getSentDate(),
                            Constants.ES.DATETIME_FORMAT_YMDHMS);
                        Long currentTimes = new Date().getTime();
                        if (currentTimes - (lastSendDate != null ? lastSendDate.getTime() : 0) >= timeActiveModule) {
                            projectMarkers.get(i)
                                .setLoadStatus(Constants.ModuleStatus.OFFLINE);
                        }
                        // CHECKSTYLE:ON
                    }
                } else {
                    int count = dataLoadFrame1Service.countCurrentData(conditions);
                    if (count > 0) {
                        projectMarkers.get(i)
                            .setBatteryStatus(Constants.ModuleStatus.OFFLINE);
                    } else {
                        projectMarkers.get(i)
                            .setBatteryStatus(Constants.ModuleStatus.IN_ACTIVE);
                    }
                }
            } else {
                projectMarkers.get(i)
                    .setBatteryStatus(Constants.ModuleStatus.IN_ACTIVE);
            }
        }

        return new ResponseEntity<>(projectMarkers, HttpStatus.OK);

    }

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
                        Integer deviceStatus = warningService.countProjectWarning(conditions);

                        if (deviceStatus > 0) {
                            projectMarkers.get(i)
                                .setLoadStatus(Constants.ModuleStatus.WARNING);
                        } else {
                            projectMarkers.get(i)
                                .setLoadStatus(Constants.ModuleStatus.ACTIVE);
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
                        Integer deviceStatus = warningService.countProjectWarning(map);

                        if (deviceStatus > 0) {
                            projectMarkers.get(i)
                                .setPvStatus(Constants.ModuleStatus.WARNING);
                        } else {
                            projectMarkers.get(i)
                                .setPvStatus(Constants.ModuleStatus.ACTIVE);
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
                        Integer deviceStatus = warningService.countProjectWarning(con);

                        if (deviceStatus > 0) {
                            projectMarkers.get(i)
                                .setGridStatus(Constants.ModuleStatus.WARNING);
                        } else {
                            projectMarkers.get(i)
                                .setGridStatus(Constants.ModuleStatus.ACTIVE);
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
                if (checkWindSystem) {
                    if (dataLoadFrame != null) {
                        Integer deviceStatus = warningService.countProjectWarning(conditions);

                        if (deviceStatus > 0) {
                            projectMarkers.get(i)
                                .setWindStatus(Constants.ModuleStatus.WARNING);
                        } else {
                            projectMarkers.get(i)
                                .setWindStatus(Constants.ModuleStatus.ACTIVE);
                        }

                        if (dataLoadFrame != null) {
                            projectMarkers.get(i)
                                .setWindPower(
                                    (double) (dataLoadFrame.getPTotal() != null ? dataLoadFrame.getPTotal() : 0));
                            projectMarkers.get(i)
                                .setCurrentTime(
                                    dataLoadFrame.getSentDate() != null ? dataLoadFrame.getSentDate() : null);
                            // CHECKSTYLE:OFF
                            Date lastSendDate = DateUtils.toDate(dataLoadFrame.getSentDate(),
                                Constants.ES.DATETIME_FORMAT_YMDHMS);
                            Long currentTimes = new Date().getTime();
                            if (currentTimes
                                - (lastSendDate != null ? lastSendDate.getTime() : 0) >= timeActiveModule) {
                                projectMarkers.get(i)
                                    .setWindStatus(Constants.ModuleStatus.OFFLINE);
                            }
                            // CHECKSTYLE:ON
                        }
                    } else {
                        int count = dataLoadFrame1Service.countCurrentData(conditions);
                        if (count > 0) {
                            projectMarkers.get(i)
                                .setWindStatus(Constants.ModuleStatus.OFFLINE);
                        } else {
                            projectMarkers.get(i)
                                .setWindStatus(Constants.ModuleStatus.IN_ACTIVE);
                        }
                    }
                } else {
                    projectMarkers.get(i)
                        .setWindStatus(Constants.ModuleStatus.IN_ACTIVE);
                }

                if (checkBatterySystem) {
                    if (dataLoadFrame != null) {
                        Integer deviceStatus = warningService.countProjectWarning(conditions);

                        if (deviceStatus > 0) {
                            projectMarkers.get(i)
                                .setBatteryStatus(Constants.ModuleStatus.WARNING);
                        } else {
                            projectMarkers.get(i)
                                .setBatteryStatus(Constants.ModuleStatus.ACTIVE);
                        }

                        if (dataLoadFrame != null) {
                            projectMarkers.get(i)
                                .setBatteryPower(
                                    (double) (dataLoadFrame.getPTotal() != null ? dataLoadFrame.getPTotal() : 0));
                            projectMarkers.get(i)
                                .setCurrentTime(
                                    dataLoadFrame.getSentDate() != null ? dataLoadFrame.getSentDate() : null);
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
                                .setBatteryStatus(Constants.ModuleStatus.OFFLINE);
                        } else {
                            projectMarkers.get(i)
                                .setBatteryStatus(Constants.ModuleStatus.IN_ACTIVE);
                        }
                    }
                } else {
                    projectMarkers.get(i)
                        .setBatteryStatus(Constants.ModuleStatus.IN_ACTIVE);
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
                } // c,x,d
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

    @GetMapping ("/getCustomerIdFirstTime")
    public ResponseEntity<?> getCustomerIdFirstTime() {
        log.info("getCustomerIdFirstTime START");

        Customer cus = new Customer();
        cus = customerService.getCustomerIdFirstTime();

        log.info("getCustomerIdFirstTime END");
        return new ResponseEntity<>(cus, HttpStatus.OK);

    }

    @GetMapping ("/getInfoSystemLoad/{customerId}")
    public ResponseEntity<?> getInfoSystemLoad(@PathVariable ("customerId") final Integer customerId) {
        log.info("getInfoSystemLoad START");
        List<OverviewLoadPower> dataResult = new ArrayList<>();
        if (customerId != null) {
            Map<String, Object> condition = new HashMap<>();
            condition.put("schema", Schema.getSchemas(customerId));
            // get năng lượng theo ngày tháng năm
            Date today = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String todayString = dateFormat.format(today);
            String previousDayString = dateFormat
                .format(new Date(today.getTime() - TRANSACTION_DATE_24_HOURS * MILLISECOND));
            int currentYear = Year.now()
                .getValue();
            int lastYear = Year.now()
                .minusYears(1)
                .getValue();
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");
            String currentMonth = sdfMonth.format(today);
            cal.setTime(today);
            if (cal.get(Calendar.MONTH) == Calendar.JANUARY) {
                cal.set(Calendar.MONTH, Calendar.DECEMBER);
                cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
            } else {
                cal.roll(Calendar.MONTH, false);
            }

            int minute = cal.get(Calendar.MINUTE);
            // CHECKSTYLE:OFF
            minute = ( (minute / 5) - 1) * 5 - 10;
            if (minute >= 60) {
                cal.add(Calendar.HOUR_OF_DAY, 1);
                cal.set(Calendar.MINUTE, 0);
            } else {
                cal.set(Calendar.MINUTE, minute);
            }
            condition.put("systemTypeId", 1);
            int loadCount = systemMapService.getCountModun(condition);

            String[] ids = deviceService.getDeviceIdBySystemMap(condition);
            String deviceId = String.join(",", ids);
            Map<String, String> cond = new HashMap<>();
            cond.put("deviceId", deviceId);
            cond.put("schema", Schema.getSchemas(customerId));
            cond.put("today", todayString);

            float maxPower = dataLoadFrame1Service.getMaxTotalPower(cond) != null
                ? dataLoadFrame1Service.getMaxTotalPower(cond)
                : 0;
            float minPower = dataLoadFrame1Service.getMinTotalPower(cond) != null
                ? dataLoadFrame1Service.getMinTotalPower(cond)
                : 0;
            float avgPower = dataLoadFrame1Service.getAvgTotalPower(cond) != null
                ? dataLoadFrame1Service.getAvgTotalPower(cond)
                : 0;

            String[] deviceIds = deviceService.getDeviceIdBySystemMap(condition);
            condition.put("deviceIds", deviceIds);
            String previousMonth = sdfMonth.format(cal.getTime());
            condition.put("year", currentYear);
            Long sumEnergy = overviewPowerService.getSumEnergy(condition);
            Long sumEnergyCurrentYear = overviewPowerService.getSumEnergyByYear(condition);
            condition.put("year", lastYear);
            Long sumEnergyLastYear = overviewPowerService.getSumEnergyByYear(condition);
            condition.put("day", todayString);
            Long sumEnergyToday = overviewPowerService.getSumEnergyByDay(condition);
            condition.put("day", previousDayString);
            Long sumEnergyPreday = overviewPowerService.getSumEnergyByDay(condition);
            condition.put("month", currentMonth);
            Long sumEnergyCurMonth = overviewPowerService.getSumEnergyByMonth(condition);
            condition.put("month", previousMonth);
            Long sumEnergyPreMonth = overviewPowerService.getSumEnergyByMonth(condition);

            SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:59");
            String fifteenDate = dateFormatWithTime.format(cal.getTime());
            String dateNow = dateFormatWithTime.format(new Date());
            condition.put("deviceId", deviceId);
            condition.put("startDate", fifteenDate);
            condition.put("now", dateNow);
            float sumPower = dataLoadFrame1Service.getSumTotalPower(condition) != null
                ? dataLoadFrame1Service.getSumTotalPower(condition)
                : 0;

            // thông tin cảnh báo
            int warnings = overviewPowerService.getCountWarningLoad(condition);

            // result
            OverviewLoadPower dataResponse = new OverviewLoadPower();
            dataResponse.setSumEnergy(sumEnergy);
            dataResponse.setSumEnergyCurMonth(sumEnergyCurMonth);
            dataResponse.setSumEnergyCurrentYear(sumEnergyCurrentYear);
            dataResponse.setSumEnergyLastYear(sumEnergyLastYear);
            dataResponse.setSumEnergyPreday(sumEnergyPreday);
            dataResponse.setSumEnergyPreMonth(sumEnergyPreMonth);
            dataResponse.setSumEnergyToday(sumEnergyToday);
            dataResponse.setLoadCount(loadCount);
            dataResponse.setWarningCount(warnings);
            dataResponse.setMaxPtotal(maxPower);
            dataResponse.setMinPtotal(minPower);
            dataResponse.setAvgTotal(avgPower);
            dataResponse.setRealTime(sumPower);

            dataResult.add(dataResponse);

        }
        log.info("getInfoSystemLoad END");
        return new ResponseEntity<>(dataResult, HttpStatus.OK);
    }

    @GetMapping ("/getInfoSystemPv/{customerId}")
    public ResponseEntity<?> getInfoSystemPv(@PathVariable ("customerId") final Integer customerId) {
        log.info("getInfoSystemPv START");

        Map<String, Object> condition = new HashMap<>();
        List<OverviewPVPower> dataResult = new ArrayList<>();
        condition.put("schema", Schema.getSchemas(customerId));
        // get năng lượng theo ngày tháng năm
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = dateFormat.format(today);
        String previousDayString = dateFormat
            .format(new Date(today.getTime() - TRANSACTION_DATE_24_HOURS * MILLISECOND));
        int currentYear = Year.now()
            .getValue();
        int lastYear = Year.now()
            .minusYears(1)
            .getValue();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");
        String currentMonth = sdfMonth.format(today);
        cal.setTime(today);
        if (cal.get(Calendar.MONTH) == Calendar.JANUARY) {
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
        } else {
            cal.roll(Calendar.MONTH, false);
        }
        int minute = cal.get(Calendar.MINUTE);
        // CHECKSTYLE:OFF
        minute = ( (minute / 5) - 1) * 5 - 10;
        if (minute >= 60) {
            cal.add(Calendar.HOUR_OF_DAY, 1);
            cal.set(Calendar.MINUTE, 0);
        } else {
            cal.set(Calendar.MINUTE, minute);
        }
        condition.put("systemTypeId", 2);
        int loadCount = systemMapService.getCountModun(condition);

        String[] ids = deviceService.getDeviceIdBySystemMap(condition);
        String deviceId = String.join(",", ids);
        Map<String, String> cond = new HashMap<>();
        cond.put("deviceId", deviceId);
        cond.put("schema", Schema.getSchemas(customerId));
        cond.put("today", todayString);

        float maxPower = dataInverterService.getMaxTotalPower(cond) != null
            ? dataInverterService.getMaxTotalPower(cond)
            : 0;
        float minPower = dataInverterService.getMinTotalPower(cond) != null
            ? dataInverterService.getMinTotalPower(cond)
            : 0;
        float avgPower = dataInverterService.getAvgTotalPower(cond) != null
            ? dataInverterService.getAvgTotalPower(cond)
            : 0;

        String[] deviceIds = deviceService.getDeviceIdBySystemMap(condition);
        condition.put("deviceIds", deviceIds);
        String previousMonth = sdfMonth.format(cal.getTime());
        condition.put("year", currentYear);
        Long sumEnergy = overviewPowerService.getSumEnergyPV(condition);
        Long sumEnergyCurrentYear = overviewPowerService.getSumEnergyByYearPV(condition);
        condition.put("year", lastYear);
        Long sumEnergyLastYear = overviewPowerService.getSumEnergyByYearPV(condition);
        condition.put("day", todayString);
        Long sumEnergyToday = overviewPowerService.getSumEnergyByDayPV(condition);
        condition.put("day", previousDayString);
        Long sumEnergyPreday = overviewPowerService.getSumEnergyByDayPV(condition);
        condition.put("month", currentMonth);
        Long sumEnergyCurMonth = overviewPowerService.getSumEnergyByMonthPV(condition);
        condition.put("month", previousMonth);
        Long sumEnergyPreMonth = overviewPowerService.getSumEnergyByMonthPV(condition);

        // thông tin cảnh báo
        int warnings = overviewPowerService.getCountWarningPV(condition);

        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:59");
        String fifteenDate = dateFormatWithTime.format(cal.getTime());
        String dateNow = dateFormatWithTime.format(new Date());
        condition.put("deviceId", deviceId);
        condition.put("startDate", fifteenDate);
        condition.put("now", dateNow);
        float sumPower = dataInverterService.getSumTotalPower(condition) != null
            ? dataInverterService.getSumTotalPower(condition)
            : 0;

        // result
        OverviewPVPower dataResponse = new OverviewPVPower();
        dataResponse.setSumEnergyPV(sumEnergy);
        dataResponse.setSumEnergyCurMonthPV(sumEnergyCurMonth);
        dataResponse.setSumEnergyCurrentYearPV(sumEnergyCurrentYear);
        dataResponse.setSumEnergyLastYearPV(sumEnergyLastYear);
        dataResponse.setSumEnergyPredayPV(sumEnergyPreday);
        dataResponse.setSumEnergyPreMonthPV(sumEnergyPreMonth);
        dataResponse.setSumEnergyTodayPV(sumEnergyToday);
        dataResponse.setPvCount(loadCount);
        dataResponse.setWarningCount(warnings);
        dataResponse.setMaxPtotal(maxPower);
        dataResponse.setMinPtotal(minPower);
        dataResponse.setAvgPtotal(avgPower);
        dataResponse.setRealTime(sumPower);

        dataResult.add(dataResponse);

        log.info("getInfoSystemPv END");
        return new ResponseEntity<>(dataResult, HttpStatus.OK);
    }

    @GetMapping ("/getInfoSystemGrid/{customerId}")
    public ResponseEntity<?> getInfoSystemGrid(@PathVariable ("customerId") final Integer customerId) {
        log.info("getInfoSystemGrid START");

        Map<String, Object> condition = new HashMap<>();
        List<OverviewGridPower> dataResult = new ArrayList<>();
        condition.put("schema", Schema.getSchemas(customerId));
        // get năng lượng theo ngày tháng năm
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = dateFormat.format(today);
        String previousDayString = dateFormat
            .format(new Date(today.getTime() - TRANSACTION_DATE_24_HOURS * MILLISECOND));
        int currentYear = Year.now()
            .getValue();
        int lastYear = Year.now()
            .minusYears(1)
            .getValue();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");
        String currentMonth = sdfMonth.format(today);
        cal.setTime(today);
        if (cal.get(Calendar.MONTH) == Calendar.JANUARY) {
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
        } else {
            cal.roll(Calendar.MONTH, false);
        }
        int minute = cal.get(Calendar.MINUTE);
        // CHECKSTYLE:OFF
        minute = ( (minute / 5) - 1) * 5 - 10;
        if (minute >= 60) {
            cal.add(Calendar.HOUR_OF_DAY, 1);
            cal.set(Calendar.MINUTE, 0);
        } else {
            cal.set(Calendar.MINUTE, minute);
        }
        condition.put("systemTypeId", 5);
        int loadCount = systemMapService.getCountModun(condition);

        String[] ids = deviceService.getDeviceIdBySystemMap(condition);

        Map<String, String> cond = new HashMap<>();
        if (ids.length == 0) {
            cond.put("deviceId", "0");
        } else {
            cond.put("deviceId", String.join(",", ids));
        }
        cond.put("schema", Schema.getSchemas(customerId));
        cond.put("today", todayString);

        float maxPower = dataRmuDrawer1Service.getMaxTotalPower(cond) != null
            ? dataRmuDrawer1Service.getMaxTotalPower(cond)
            : 0;

        String[] deviceIds = deviceService.getDeviceIdBySystemType(condition);
        condition.put("deviceIds", deviceIds);
        condition.put("today", todayString);
        float avgPower = dataRmuDrawer1Service.getAvgTotalPower(condition) != null
            ? dataRmuDrawer1Service.getAvgTotalPower(condition)
            : 0;
        String previousMonth = sdfMonth.format(cal.getTime());
        condition.put("year", currentYear);
        Long sumEnergy = overviewPowerService.getSumEnergyGrid(condition);
        Long sumEnergyCurrentYear = overviewPowerService.getSumEnergyByYearGrid(condition);
        condition.put("year", lastYear);
        Long sumEnergyLastYear = overviewPowerService.getSumEnergyByYearGrid(condition);
        condition.put("day", todayString);
        Long sumEnergyToday = overviewPowerService.getSumEnergyByDayGrid(condition);
        condition.put("day", previousDayString);
        Long sumEnergyPreday = overviewPowerService.getSumEnergyByDayGrid(condition);
        condition.put("month", currentMonth);
        Long sumEnergyCurMonth = overviewPowerService.getSumEnergyByMonthGrid(condition);
        condition.put("month", previousMonth);
        Long sumEnergyPreMonth = overviewPowerService.getSumEnergyByMonthGrid(condition);

        // thông tin cảnh báo
        int warnings = overviewPowerService.getCountWarningGrid(condition);

        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:59");
        String fifteenDate = dateFormatWithTime.format(cal.getTime());
        String dateNow = dateFormatWithTime.format(new Date());
        String deviceId = String.join(",", deviceIds);
        condition.put("deviceId", deviceId);
        condition.put("startDate", fifteenDate);
        condition.put("now", dateNow);
        float sumPower = dataRmuDrawer1Service.getSumTotalPower(condition) != null
            ? dataRmuDrawer1Service.getSumTotalPower(condition)
            : 0;

        // result
        OverviewGridPower dataResponse = new OverviewGridPower();
        dataResponse.setSumEnergyGrid(sumEnergy);
        dataResponse.setSumEnergyCurMonthGrid(sumEnergyCurMonth);
        dataResponse.setSumEnergyCurrentYearGrid(sumEnergyCurrentYear);
        dataResponse.setSumEnergyLastYearGrid(sumEnergyLastYear);
        dataResponse.setSumEnergyPredayGrid(sumEnergyPreday);
        dataResponse.setSumEnergyPreMonthGrid(sumEnergyPreMonth);
        dataResponse.setSumEnergyTodayGrid(sumEnergyToday);
        dataResponse.setGridCount(loadCount);
        dataResponse.setWarningCount(warnings);
        dataResponse.setMaxPtotal(maxPower);
        dataResponse.setAvgPtotal(avgPower);
        dataResponse.setRealTime(sumPower);

        dataResult.add(dataResponse);

        log.info("getInfoSystemGrid END");
        return new ResponseEntity<>(dataResult, HttpStatus.OK);

    }

    @GetMapping ("/getProjectBySystemType/{customerId}/{systemTypeId}")
    public ResponseEntity<?> getProjectBySystemType(@PathVariable ("systemTypeId") final Integer systemTypeId,
        @PathVariable ("customerId") final Integer customerId) {
        log.info("getProjectBySystemType START");

        Map<String, String> condition = new HashMap<>();
        condition.put("systemTypeId", String.valueOf(systemTypeId));
        condition.put("customerId", String.valueOf(customerId));
        // list projects
        List<Project> projects = new ArrayList<>();

        // list project response to client
        List<ProjectLocation> projectMarkers = new ArrayList<>();

        projects = projectService.getProjectBySystemType(condition);

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
                    Integer deviceStatus = warningService.countProjectWarning(conditions);

                    if (deviceStatus > 0) {
                        projectMarkers.get(i)
                            .setLoadStatus(Constants.ModuleStatus.WARNING);
                    } else {
                        projectMarkers.get(i)
                            .setLoadStatus(Constants.ModuleStatus.ACTIVE);
                    }

                    if (dataLoadFrame != null) {
                        // CHECKSTYLE:OFF
                        projectMarkers.get(i)
                            .setPTotal(dataLoadFrame.getPTotal());
                        projectMarkers.get(i)
                            .setQTotal(dataLoadFrame.getQTotal());
                        Date lastSendDate = DateUtils.toDate(dataLoadFrame.getSentDate(),
                            Constants.ES.DATETIME_FORMAT_YMDHMS);
                        Long currentTimes = new Date().getTime();
                        if (currentTimes - (lastSendDate != null ? lastSendDate.getTime() : 0) >= timeActiveModule) {
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
                    Integer deviceStatus = warningService.countProjectWarning(map);

                    if (deviceStatus > 0) {
                        projectMarkers.get(i)
                            .setPvStatus(Constants.ModuleStatus.WARNING);
                    } else {
                        projectMarkers.get(i)
                            .setPvStatus(Constants.ModuleStatus.ACTIVE);
                    }
                    projectMarkers.get(i)
                        .setPTotal(dataPvFrame.getPTotal());
                    projectMarkers.get(i)
                        .setQTotal(dataPvFrame.getQTotal());
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
                    Integer deviceStatus = warningService.countProjectWarning(con);

                    if (deviceStatus > 0) {
                        projectMarkers.get(i)
                            .setGridStatus(Constants.ModuleStatus.WARNING);
                    } else {
                        projectMarkers.get(i)
                            .setGridStatus(Constants.ModuleStatus.ACTIVE);
                    }

                    if (dataGridFrame != null) {
                        // CHECKSTYLE:OFF
                        Date lastSendDate = DateUtils.toDate(dataGridFrame.getSentDate(),
                            Constants.ES.DATETIME_FORMAT_YMDHMS);
                        projectMarkers.get(i)
                            .setPTotal(dataGridFrame.getPTotal());
                        projectMarkers.get(i)
                            .setQTotal(dataGridFrame.getQTotal());
                        Long currentTimes = new Date().getTime();
                        if (currentTimes - (lastSendDate != null ? lastSendDate.getTime() : 0) >= timeActiveModule) {
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
            if (checkWindSystem) {
                if (dataLoadFrame != null) {
                    Integer deviceStatus = warningService.countProjectWarning(conditions);

                    if (deviceStatus > 0) {
                        projectMarkers.get(i)
                            .setWindStatus(Constants.ModuleStatus.WARNING);
                    } else {
                        projectMarkers.get(i)
                            .setWindStatus(Constants.ModuleStatus.ACTIVE);
                    }

                    if (dataLoadFrame != null) {
                        projectMarkers.get(i)
                            .setWindPower((double) (dataLoadFrame.getPTotal() != null ? dataLoadFrame.getPTotal() : 0));
                        projectMarkers.get(i)
                            .setCurrentTime(dataLoadFrame.getSentDate() != null ? dataLoadFrame.getSentDate() : null);
                        projectMarkers.get(i)
                            .setPTotal(dataLoadFrame.getPTotal());
                        projectMarkers.get(i)
                            .setQTotal(dataLoadFrame.getQTotal());
                        // CHECKSTYLE:OFF
                        Date lastSendDate = DateUtils.toDate(dataLoadFrame.getSentDate(),
                            Constants.ES.DATETIME_FORMAT_YMDHMS);
                        Long currentTimes = new Date().getTime();
                        if (currentTimes - (lastSendDate != null ? lastSendDate.getTime() : 0) >= timeActiveModule) {
                            projectMarkers.get(i)
                                .setWindStatus(Constants.ModuleStatus.OFFLINE);
                        }
                        // CHECKSTYLE:ON
                    }
                } else {
                    int count = dataLoadFrame1Service.countCurrentData(conditions);
                    if (count > 0) {
                        projectMarkers.get(i)
                            .setWindStatus(Constants.ModuleStatus.OFFLINE);
                    } else {
                        projectMarkers.get(i)
                            .setWindStatus(Constants.ModuleStatus.IN_ACTIVE);
                    }
                }
            } else {
                projectMarkers.get(i)
                    .setWindStatus(Constants.ModuleStatus.IN_ACTIVE);
            }

            if (checkBatterySystem) {
                if (dataLoadFrame != null) {
                    Integer deviceStatus = warningService.countProjectWarning(conditions);

                    if (deviceStatus > 0) {
                        projectMarkers.get(i)
                            .setBatteryStatus(Constants.ModuleStatus.WARNING);
                    } else {
                        projectMarkers.get(i)
                            .setBatteryStatus(Constants.ModuleStatus.ACTIVE);
                    }

                    if (dataLoadFrame != null) {
                        projectMarkers.get(i)
                            .setBatteryPower(
                                (double) (dataLoadFrame.getPTotal() != null ? dataLoadFrame.getPTotal() : 0));
                        projectMarkers.get(i)
                            .setCurrentTime(dataLoadFrame.getSentDate() != null ? dataLoadFrame.getSentDate() : null);
                        projectMarkers.get(i)
                            .setPTotal(dataLoadFrame.getPTotal());
                        projectMarkers.get(i)
                            .setQTotal(dataLoadFrame.getQTotal());
                        // CHECKSTYLE:OFF
                        Date lastSendDate = DateUtils.toDate(dataLoadFrame.getSentDate(),
                            Constants.ES.DATETIME_FORMAT_YMDHMS);
                        Long currentTimes = new Date().getTime();
                        if (currentTimes - (lastSendDate != null ? lastSendDate.getTime() : 0) >= timeActiveModule) {
                            projectMarkers.get(i)
                                .setLoadStatus(Constants.ModuleStatus.OFFLINE);
                        }
                        // CHECKSTYLE:ON
                    }
                } else {
                    int count = dataLoadFrame1Service.countCurrentData(conditions);
                    if (count > 0) {
                        projectMarkers.get(i)
                            .setBatteryStatus(Constants.ModuleStatus.OFFLINE);
                    } else {
                        projectMarkers.get(i)
                            .setBatteryStatus(Constants.ModuleStatus.IN_ACTIVE);
                    }
                }
            } else {
                projectMarkers.get(i)
                    .setBatteryStatus(Constants.ModuleStatus.IN_ACTIVE);
            }

        }

        return new ResponseEntity<>(projectMarkers, HttpStatus.OK);
    }

    @GetMapping ("/getSystemTypeByProjectId/{projectId}")
    public ResponseEntity<?> getSystemTypeByProjectId(@PathVariable ("projectId") final Integer projectId) {
        log.info("getSystemTypeByProjectId START");
        Map<String, String> con = new HashMap<>();
        con.put("projectId", String.valueOf(projectId));
        List<SystemMap> systemMaps = systemMapService.getSystemMapByCustomerAndProject(con);
        Customer cus = customerService.getCustomerByProjectId(con);

        for (int i = 0; i < systemMaps.size(); i++) {
            Map<String, String> condition = new HashMap<>();
            condition.put("projectId", String.valueOf(projectId));
            condition.put("systemTypeId", String.valueOf(systemMaps.get(i)
                .getSystemTypeId()));
            if (systemMaps.get(i)
                .getSystemTypeId() == 1) {
                // CHECKSTYLE:OFF
                systemMaps.get(i)
                    .setDeviceNumber(deviceService.getCountDeviceBySystemType(condition));
                // CHECKSTYLE:ON
                // get năng lượng theo ngày tháng năm
                Date today = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String todayString = dateFormat.format(today);
                Long sumEnergy = (long) 0;
                Long sumEnergyCurrentYear = (long) 0;
                Long sumEnergyLastYear = (long) 0;
                Long sumEnergyToday = (long) 0;
                Long sumEnergyPreday = (long) 0;
                Long sumEnergyCurMonth = (long) 0;
                Long sumEnergyPreMonth = (long) 0;
                int warnings = 0;
                String previousDayString = dateFormat
                    .format(new Date(today.getTime() - TRANSACTION_DATE_24_HOURS * MILLISECOND));
                int currentYear = Year.now()
                    .getValue();
                int lastYear = Year.now()
                    .minusYears(1)
                    .getValue();
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");
                String currentMonth = sdfMonth.format(today);
                cal.setTime(today);
                if (cal.get(Calendar.MONTH) == Calendar.JANUARY) {
                    cal.set(Calendar.MONTH, Calendar.DECEMBER);
                    cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
                } else {
                    cal.roll(Calendar.MONTH, false);
                }
                int minute = cal.get(Calendar.MINUTE);
                // CHECKSTYLE:OFF
                minute = ( (minute / 5) - 1) * 5 - 10;
                if (minute >= 60) {
                    cal.add(Calendar.HOUR_OF_DAY, 1);
                    cal.set(Calendar.MINUTE, 0);
                } else {
                    cal.set(Calendar.MINUTE, minute);
                }

                Map<String, Object> condi = new HashMap<>();
                condi.put("systemTypeId", 1);
                String[] ids = deviceService.getDeviceIdBySystemMap(condi);
                String deviceId = String.join(",", ids);
                Map<String, String> cond = new HashMap<>();
                cond.put("deviceId", deviceId);
                cond.put("schema", Schema.getSchemas(cus.getCustomerId()));
                cond.put("today", todayString);

                float maxPower = dataLoadFrame1Service.getMaxTotalPower(cond) != null
                    ? dataLoadFrame1Service.getMaxTotalPower(cond)
                    : 0;
                float minPower = dataLoadFrame1Service.getMinTotalPower(cond) != null
                    ? dataLoadFrame1Service.getMinTotalPower(cond)
                    : 0;
                float avgPower = dataLoadFrame1Service.getAvgTotalPower(cond) != null
                    ? dataLoadFrame1Service.getAvgTotalPower(cond)
                    : 0;

                condition.put("projectId", String.valueOf(projectId));
                condition.put("systemTypeId", String.valueOf(systemMaps.get(i)
                    .getSystemTypeId()));
                Map<String, Object> conditions = new HashMap<>();
                conditions.put("schema", Schema.getSchemas(cus.getCustomerId()));
                String[] deviceIds = deviceService.getDeviceIdByProjectIdAndSystemTypeId(condition);
                conditions.put("deviceIds", deviceIds);
                String previousMonth = sdfMonth.format(cal.getTime());
                conditions.put("year", currentYear);
                if (deviceIds.length > 0) {
                    sumEnergy = overviewPowerService.getSumEnergy(conditions);

                    sumEnergyCurrentYear = overviewPowerService.getSumEnergyByYear(conditions);
                    conditions.put("year", lastYear);
                    sumEnergyLastYear = overviewPowerService.getSumEnergyByYear(conditions);
                    conditions.put("day", todayString);
                    sumEnergyToday = overviewPowerService.getSumEnergyByDay(conditions);
                    conditions.put("day", previousDayString);
                    sumEnergyPreday = overviewPowerService.getSumEnergyByDay(conditions);
                    conditions.put("month", currentMonth);
                    sumEnergyCurMonth = overviewPowerService.getSumEnergyByMonth(conditions);
                    conditions.put("month", previousMonth);
                    sumEnergyPreMonth = overviewPowerService.getSumEnergyByMonth(conditions);

                    // thông tin cảnh báo
                    warnings = overviewPowerService.getCountWarningLoad(conditions);
                }

                SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:59");
                String fifteenDate = dateFormatWithTime.format(cal.getTime());
                String dateNow = dateFormatWithTime.format(new Date());
                condi.put("deviceId", deviceId);
                condi.put("startDate", fifteenDate);
                condi.put("now", dateNow);
                condi.put("schema", Schema.getSchemas(cus.getCustomerId()));
                float sumPower = 0;
                if (dataLoadFrame1Service.getSumTotalPower(condi) != null) {
                    sumPower = dataLoadFrame1Service.getSumTotalPower(condi);
                }

                systemMaps.get(i)
                    .setSumEnergy(sumEnergy);
                systemMaps.get(i)
                    .setSumEnergyCurMonth(sumEnergyCurMonth);
                systemMaps.get(i)
                    .setSumEnergyCurrentYear(sumEnergyCurrentYear);
                systemMaps.get(i)
                    .setSumEnergyLastYear(sumEnergyLastYear);
                systemMaps.get(i)
                    .setSumEnergyPreday(sumEnergyPreday);
                systemMaps.get(i)
                    .setSumEnergyPreMonth(sumEnergyPreMonth);
                systemMaps.get(i)
                    .setSumEnergyToday(sumEnergyToday);
                systemMaps.get(i)
                    .setWarningCount(warnings);
                systemMaps.get(i)
                    .setMaxPtotal(maxPower);
                systemMaps.get(i)
                    .setMinPtotal(minPower);
                systemMaps.get(i)
                    .setAvgPtotal(avgPower);
                systemMaps.get(i)
                    .setRealTime(sumPower);

            }

            if (systemMaps.get(i)
                .getSystemTypeId() == 2) {
                condition.put("projectId", String.valueOf(projectId));
                condition.put("systemTypeId", String.valueOf(systemMaps.get(i)
                    .getSystemTypeId()));
                Map<String, Object> conditions = new HashMap<>();
                conditions.put("schema", Schema.getSchemas(cus.getCustomerId()));
                int pvCount = systemMapService.getCountModun(conditions);
                // get năng lượng theo ngày tháng năm
                Date today = new Date();
                Long sumEnergy = (long) 0;
                Long sumEnergyCurrentYear = (long) 0;
                Long sumEnergyLastYear = (long) 0;
                Long sumEnergyToday = (long) 0;
                Long sumEnergyPreday = (long) 0;
                Long sumEnergyCurMonth = (long) 0;
                Long sumEnergyPreMonth = (long) 0;
                int warnings = 0;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String todayString = dateFormat.format(today);
                String previousDayString = dateFormat
                    .format(new Date(today.getTime() - TRANSACTION_DATE_24_HOURS * MILLISECOND));
                int currentYear = Year.now()
                    .getValue();
                int lastYear = Year.now()
                    .minusYears(1)
                    .getValue();
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");
                String currentMonth = sdfMonth.format(today);
                cal.setTime(today);
                if (cal.get(Calendar.MONTH) == Calendar.JANUARY) {
                    cal.set(Calendar.MONTH, Calendar.DECEMBER);
                    cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
                } else {
                    cal.roll(Calendar.MONTH, false);
                }
                int minute = cal.get(Calendar.MINUTE);
                // CHECKSTYLE:OFF
                minute = ( (minute / 5) - 1) * 5 - 10;
                if (minute >= 60) {
                    cal.add(Calendar.HOUR_OF_DAY, 1);
                    cal.set(Calendar.MINUTE, 0);
                } else {
                    cal.set(Calendar.MINUTE, minute);
                }

                Map<String, Object> condi = new HashMap<>();
                condi.put("systemTypeId", 2);
                String[] ids = deviceService.getDeviceIdBySystemMap(condi);
                String deviceId = String.join(",", ids);
                Map<String, String> cond = new HashMap<>();
                cond.put("deviceId", deviceId);
                cond.put("schema", Schema.getSchemas(cus.getCustomerId()));
                cond.put("today", todayString);

                float maxPower = dataInverterService.getMaxTotalPower(cond) != null
                    ? dataInverterService.getMaxTotalPower(cond)
                    : 0;
                float minPower = dataInverterService.getMinTotalPower(cond) != null
                    ? dataInverterService.getMinTotalPower(cond)
                    : 0;
                float avgPower = dataInverterService.getAvgTotalPower(cond) != null
                    ? dataInverterService.getAvgTotalPower(cond)
                    : 0;

                String[] deviceIds = deviceService.getDeviceIdByProjectIdAndSystemTypeId(condition);
                conditions.put("deviceIds", deviceIds);
                String previousMonth = sdfMonth.format(cal.getTime());
                if (deviceIds.length > 0) {
                    conditions.put("year", currentYear);
                    sumEnergy = overviewPowerService.getSumEnergyPV(conditions);
                    sumEnergyCurrentYear = overviewPowerService.getSumEnergyByYearPV(conditions);
                    conditions.put("year", lastYear);
                    sumEnergyLastYear = overviewPowerService.getSumEnergyByYearPV(conditions);
                    conditions.put("day", todayString);
                    sumEnergyToday = overviewPowerService.getSumEnergyByDayPV(conditions);
                    conditions.put("day", previousDayString);
                    sumEnergyPreday = overviewPowerService.getSumEnergyByDayPV(conditions);
                    conditions.put("month", currentMonth);
                    sumEnergyCurMonth = overviewPowerService.getSumEnergyByMonthPV(conditions);
                    conditions.put("month", previousMonth);
                    sumEnergyPreMonth = overviewPowerService.getSumEnergyByMonthPV(conditions);

                    // thông tin cảnh báo
                    warnings = overviewPowerService.getCountWarningPV(conditions);

                }

                SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:59");
                String fifteenDate = dateFormatWithTime.format(cal.getTime());
                String dateNow = dateFormatWithTime.format(new Date());
                condi.put("deviceId", deviceId);
                condi.put("startDate", fifteenDate);
                condi.put("now", dateNow);
                condi.put("schema", Schema.getSchemas(cus.getCustomerId()));
                float sumPower = dataInverterService.getSumTotalPower(condi) != null
                    ? dataInverterService.getSumTotalPower(condi)
                    : 0;

                systemMaps.get(i)
                    .setSumEnergy(sumEnergy);
                systemMaps.get(i)
                    .setSumEnergyCurMonth(sumEnergyCurMonth);
                systemMaps.get(i)
                    .setSumEnergyCurrentYear(sumEnergyCurrentYear);
                systemMaps.get(i)
                    .setSumEnergyLastYear(sumEnergyLastYear);
                systemMaps.get(i)
                    .setSumEnergyPreday(sumEnergyPreday);
                systemMaps.get(i)
                    .setSumEnergyPreMonth(sumEnergyPreMonth);
                systemMaps.get(i)
                    .setSumEnergyToday(sumEnergyToday);
                systemMaps.get(i)
                    .setWarningCount(warnings);
                systemMaps.get(i)
                    .setDeviceNumber(pvCount);
                systemMaps.get(i)
                    .setMaxPtotal(maxPower);
                systemMaps.get(i)
                    .setMinPtotal(minPower);
                systemMaps.get(i)
                    .setAvgPtotal(avgPower);
                systemMaps.get(i)
                    .setRealTime(sumPower);
            }

            if (systemMaps.get(i)
                .getSystemTypeId() == 5) {
                // CHECKSTYLE:OFF
                systemMaps.get(i)
                    .setDeviceNumber(deviceService.getCountDeviceBySystemType(condition));
                // CHECKSTYLE:ON
                // get năng lượng theo ngày tháng năm
                Date today = new Date();
                Long sumEnergy = (long) 0;
                Long sumEnergyCurrentYear = (long) 0;
                Long sumEnergyLastYear = (long) 0;
                Long sumEnergyToday = (long) 0;
                Long sumEnergyPreday = (long) 0;
                Long sumEnergyCurMonth = (long) 0;
                Long sumEnergyPreMonth = (long) 0;
                int warnings = 0;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String todayString = dateFormat.format(today);
                String previousDayString = dateFormat
                    .format(new Date(today.getTime() - TRANSACTION_DATE_24_HOURS * MILLISECOND));
                int currentYear = Year.now()
                    .getValue();
                int lastYear = Year.now()
                    .minusYears(1)
                    .getValue();
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");
                String currentMonth = sdfMonth.format(today);
                cal.setTime(today);
                if (cal.get(Calendar.MONTH) == Calendar.JANUARY) {
                    cal.set(Calendar.MONTH, Calendar.DECEMBER);
                    cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
                } else {
                    cal.roll(Calendar.MONTH, false);
                }
                int minute = cal.get(Calendar.MINUTE);
                // CHECKSTYLE:OFF
                minute = ( (minute / 5) - 1) * 5 - 10;
                if (minute >= 60) {
                    cal.add(Calendar.HOUR_OF_DAY, 1);
                    cal.set(Calendar.MINUTE, 0);
                } else {
                    cal.set(Calendar.MINUTE, minute);
                }

                Map<String, Object> condi = new HashMap<>();
                condi.put("systemTypeId", 2);
                String[] ids = deviceService.getDeviceIdBySystemMap(condi);

                Map<String, String> cond = new HashMap<>();
                if (ids.length == 0) {
                    cond.put("deviceId", "0");
                } else {
                    cond.put("deviceId", String.join(",", ids));
                }
                cond.put("schema", Schema.getSchemas(cus.getCustomerId()));
                cond.put("today", todayString);

                float maxPower = dataRmuDrawer1Service.getMaxTotalPower(cond) != null
                    ? dataRmuDrawer1Service.getMaxTotalPower(cond)
                    : 0;

                condition.put("projectId", String.valueOf(projectId));
                condition.put("systemTypeId", String.valueOf(systemMaps.get(i)
                    .getSystemTypeId()));
                Map<String, Object> conditions = new HashMap<>();
                conditions.put("schema", Schema.getSchemas(cus.getCustomerId()));
                int gridCount = systemMapService.getCountModun(conditions);
                String[] deviceIds = deviceService.getDeviceIdByProjectIdAndSystemTypeId(condition);
                conditions.put("deviceIds", deviceIds);
                conditions.put("today", todayString);
                float avgPower = 0.f;
                float sumPower = 0.f;
                String previousMonth = sdfMonth.format(cal.getTime());
                conditions.put("year", currentYear);
                if (deviceIds.length > 0) {
                    SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:59");
                    String fifteenDate = dateFormatWithTime.format(cal.getTime());
                    String dateNow = dateFormatWithTime.format(new Date());
                    String deviceId = String.join(",", deviceIds);
                    condi.put("deviceId", deviceId);
                    condi.put("startDate", fifteenDate);
                    condi.put("now", dateNow);
                    condi.put("schema", Schema.getSchemas(cus.getCustomerId()));
                    avgPower = dataRmuDrawer1Service.getAvgTotalPower(conditions) != null
                        ? dataRmuDrawer1Service.getAvgTotalPower(conditions)
                        : 0;
                    conditions.put("year", currentYear);
                    sumEnergy = overviewPowerService.getSumEnergyPV(conditions);
                    sumEnergyCurrentYear = overviewPowerService.getSumEnergyByYearPV(conditions);
                    conditions.put("year", lastYear);
                    sumEnergyLastYear = overviewPowerService.getSumEnergyByYearPV(conditions);
                    conditions.put("day", todayString);
                    sumEnergyToday = overviewPowerService.getSumEnergyByDayPV(conditions);
                    conditions.put("day", previousDayString);
                    sumEnergyPreday = overviewPowerService.getSumEnergyByDayPV(conditions);
                    conditions.put("month", currentMonth);
                    sumEnergyCurMonth = overviewPowerService.getSumEnergyByMonthPV(conditions);
                    conditions.put("month", previousMonth);
                    sumEnergyPreMonth = overviewPowerService.getSumEnergyByMonthPV(conditions);

                    // thông tin cảnh báo
                    warnings = overviewPowerService.getCountWarningPV(conditions);

                    sumPower = dataRmuDrawer1Service.getSumTotalPower(condi) != null
                        ? dataRmuDrawer1Service.getSumTotalPower(condi)
                        : 0;

                }

                systemMaps.get(i)
                    .setSumEnergy(sumEnergy);
                systemMaps.get(i)
                    .setSumEnergyCurMonth(sumEnergyCurMonth);
                systemMaps.get(i)
                    .setSumEnergyCurrentYear(sumEnergyCurrentYear);
                systemMaps.get(i)
                    .setSumEnergyLastYear(sumEnergyLastYear);
                systemMaps.get(i)
                    .setSumEnergyPreday(sumEnergyPreday);
                systemMaps.get(i)
                    .setSumEnergyPreMonth(sumEnergyPreMonth);
                systemMaps.get(i)
                    .setSumEnergyToday(sumEnergyToday);
                systemMaps.get(i)
                    .setWarningCount(warnings);
                systemMaps.get(i)
                    .setDeviceNumber(gridCount);
                systemMaps.get(i)
                    .setMaxPtotal(maxPower);
                systemMaps.get(i)
                    .setAvgPtotal(avgPower);
                systemMaps.get(i)
                    .setRealTime(sumPower);
            }

        }
        log.info("getTreeChart END");
        return new ResponseEntity<List<SystemMap>>(systemMaps, HttpStatus.OK);
    }

    @GetMapping ("/getInfoSystemLoadByCustomerId/{customerId}")
    public ResponseEntity<?> getInfoSystemLoadByCustomerId(@PathVariable ("customerId") final Integer customerId) {

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("yyyy-MM");

        LocalDateTime now = LocalDateTime.now();
        Date today = new Date();

        String date = dateFormat.format(now);
        String month = monthFormat.format(now);

        List<OverviewLoadPower> dataResult = new ArrayList<>();
        if (customerId != null) {

            // LOAD
            Map<String, String> condition = new HashMap<>();

            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            if (cal.get(Calendar.MONTH) == Calendar.JANUARY) {
                cal.set(Calendar.MONTH, Calendar.DECEMBER);
                cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
            } else {
                cal.roll(Calendar.MONTH, false);
            }

            int minute = cal.get(Calendar.MINUTE);
            // CHECKSTYLE:OFF
            minute = ( (minute / 5) - 1) * 5 - 10;
            if (minute >= 60) {
                cal.add(Calendar.HOUR_OF_DAY, 1);
                cal.set(Calendar.MINUTE, 0);
            } else {
                cal.set(Calendar.MINUTE, minute);
            }

            SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:59");
            SimpleDateFormat startDateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
            SimpleDateFormat nowDateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
            String fifteenDate = dateFormatWithTime.format(cal.getTime());
            String dateNow = dateFormatWithTime.format(new Date());
            String start = startDateFormatWithTime.format(new Date());
            String end = nowDateFormatWithTime.format(new Date());

            condition.put("schema", Schema.getSchemas(customerId));
            condition.put("systemTypeId", String.valueOf(1));
            condition.put("customerId", String.valueOf(customerId));
            condition.put("date", date);
            condition.put("month", month);
            condition.put("startDate", fifteenDate);
            condition.put("now", dateNow);
            condition.put("start", start);
            condition.put("end", end);

            int deviceInModule = systemMapService.getCountDeviceModule(condition);
            String[] deviceIds = deviceService.getDeviceIdByCustomerId(condition);
            if (deviceIds.length == 0) {
                condition.put("deviceId", "0");
            } else {
                String deviceId = String.join(",", deviceIds);
                condition.put("deviceId", deviceId);
            }

            Long sumEpInDay = dataLoadFrame1Service.getSumEpInDay(condition);
            Long sumEpInMonth = dataLoadFrame1Service.getSumEpInMonth(condition);
            Long sumEp = dataLoadFrame1Service.getSumEp(condition);
            Float pTotal = dataLoadFrame1Service.getPtotalInDay(condition);
            Float maxPtotal = dataLoadFrame1Service.getMaxTotalPowerInDay(condition);
            Float minPtotal = dataLoadFrame1Service.getMinTotalPowerInDay(condition);
            Float avgPtotal = dataLoadFrame1Service.getAvgTotalPowerInDay(condition);

            OverviewLoadPower dataResponse = new OverviewLoadPower();
            dataResponse.setSumEnergyToday(sumEpInDay);
            dataResponse.setSumEnergyCurMonth(sumEpInMonth);
            dataResponse.setSumEnergy(sumEp);
            dataResponse.setPTotal(pTotal);
            dataResponse.setLoadCount(deviceInModule);
            dataResponse.setMaxPtotal(maxPtotal);
            dataResponse.setMinPtotal(minPtotal);
            dataResponse.setAvgTotal(avgPtotal);

            dataResult.add(dataResponse);
        }

        return new ResponseEntity<>(dataResult, HttpStatus.OK);
    }

    @GetMapping ("/getInfoSystemSolarByCustomerId/{customerId}")
    public ResponseEntity<?> getInfoSystemSolarByCustomerId(@PathVariable ("customerId") final Integer customerId) {

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("yyyy-MM");

        LocalDateTime now = LocalDateTime.now();
        Date today = new Date();

        String date = dateFormat.format(now);
        String month = monthFormat.format(now);

        List<OverviewPVPower> dataResult = new ArrayList<>();
        if (customerId != null) {

            // LOAD
            Map<String, String> condition = new HashMap<>();

            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            if (cal.get(Calendar.MONTH) == Calendar.JANUARY) {
                cal.set(Calendar.MONTH, Calendar.DECEMBER);
                cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
            } else {
                cal.roll(Calendar.MONTH, false);
            }

            int minute = cal.get(Calendar.MINUTE);
            // CHECKSTYLE:OFF
            minute = ( (minute / 5) - 1) * 5 - 10;
            if (minute >= 60) {
                cal.add(Calendar.HOUR_OF_DAY, 1);
                cal.set(Calendar.MINUTE, 0);
            } else {
                cal.set(Calendar.MINUTE, minute);
            }

            SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:59");
            String fifteenDate = dateFormatWithTime.format(cal.getTime());
            String dateNow = dateFormatWithTime.format(new Date());

            condition.put("schema", Schema.getSchemas(customerId));
            condition.put("systemTypeId", String.valueOf(2));
            condition.put("customerId", String.valueOf(customerId));
            condition.put("date", date);
            condition.put("month", month);
            condition.put("startDate", fifteenDate);
            condition.put("now", dateNow);
            int deviceInModule = systemMapService.getCountDeviceModule(condition);
            String[] deviceIds = deviceService.getDeviceIdByCustomerId(condition);
            if (deviceIds.length == 0) {
                condition.put("deviceId", "0");
            } else {
                String deviceId = String.join(",", deviceIds);
                condition.put("deviceId", deviceId);
            }

            Long sumEpInDay = dataInverterService.getSumEpInDay(condition);
            Long sumEpInMonth = dataInverterService.getSumEpInMonth(condition);
            Long sumEp = dataInverterService.getSumEp(condition);
            Float pTotal = dataInverterService.getPtotalInDay(condition);

            OverviewPVPower dataResponse = new OverviewPVPower();
            dataResponse.setSumEnergyTodayPV(sumEpInDay);
            dataResponse.setSumEnergyCurMonthPV(sumEpInMonth);
            dataResponse.setSumEnergyPV(sumEp);
            dataResponse.setRealTime(pTotal);
            dataResponse.setPvCount(deviceInModule);

            dataResult.add(dataResponse);
        }

        return new ResponseEntity<>(dataResult, HttpStatus.OK);
    }

    @GetMapping ("/getInfoSystemGridByCustomerId/{customerId}")
    public ResponseEntity<?> getInfoSystemGridByCustomerId(@PathVariable ("customerId") final Integer customerId) {

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("yyyy-MM");

        LocalDateTime now = LocalDateTime.now();
        Date today = new Date();

        String date = dateFormat.format(now);
        String month = monthFormat.format(now);

        List<OverviewGridPower> dataResult = new ArrayList<>();
        if (customerId != null) {

            // LOAD
            Map<String, String> condition = new HashMap<>();

            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            if (cal.get(Calendar.MONTH) == Calendar.JANUARY) {
                cal.set(Calendar.MONTH, Calendar.DECEMBER);
                cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
            } else {
                cal.roll(Calendar.MONTH, false);
            }

            int minute = cal.get(Calendar.MINUTE);
            // CHECKSTYLE:OFF
            minute = ( (minute / 5) - 1) * 5 - 10;
            if (minute >= 60) {
                cal.add(Calendar.HOUR_OF_DAY, 1);
                cal.set(Calendar.MINUTE, 0);
            } else {
                cal.set(Calendar.MINUTE, minute);
            }

            SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:59");
            String fifteenDate = dateFormatWithTime.format(cal.getTime());
            String dateNow = dateFormatWithTime.format(new Date());

            condition.put("schema", Schema.getSchemas(customerId));
            condition.put("systemTypeId", String.valueOf(5));
            condition.put("customerId", String.valueOf(customerId));
            condition.put("date", date);
            condition.put("month", month);
            condition.put("startDate", fifteenDate);
            condition.put("now", dateNow);
            int deviceInModule = systemMapService.getCountDeviceModule(condition);
            String[] deviceIds = deviceService.getDeviceIdByCustomerId(condition);
            if (deviceIds.length == 0) {
                condition.put("deviceId", "0");
            } else {
                String deviceId = String.join(",", deviceIds);
                condition.put("deviceId", deviceId);
            }

            Long sumEpInDay = dataRmuDrawer1Service.getSumEpInDay(condition);
            Long sumEpInMonth = dataRmuDrawer1Service.getSumEpInMonth(condition);
            Long sumEp = dataRmuDrawer1Service.getSumEp(condition);
            Long pTotal = dataRmuDrawer1Service.getPtotalInDay(condition);

            OverviewGridPower dataResponse = new OverviewGridPower();
            dataResponse.setSumEnergyTodayGrid(sumEpInDay);
            dataResponse.setSumEnergyCurMonthGrid(sumEpInMonth);
            dataResponse.setSumEnergyGrid(sumEp);
            dataResponse.setPTotal(pTotal);
            dataResponse.setGridCount(deviceInModule);

            dataResult.add(dataResponse);
        }

        return new ResponseEntity<>(dataResult, HttpStatus.OK);
    }

    @GetMapping ("/getWarningByProjectId")
    public ResponseEntity<?> getWarningByProjectId(@RequestParam ("projectId") final Integer projectId,
        @RequestParam ("customerId") final Integer customerId) {
        log.info("getWarningByProjectId STARTED (customerId: " + customerId + ", projectId: " + projectId);
        List<Warning> dataResult = new ArrayList<>();
        if (projectId != null) {
            Map<String, Object> condition = new HashMap<>();
            condition.put("schema", Schema.getSchemas(customerId));
            condition.put("time", java.time.LocalDate.now() + " 00:00:00");
            condition.put("projectId", projectId);
            dataResult = warningService.getWarningByProject(condition);

        }
        log.info("getInfoSystemLoad END: LIST: " + dataResult);
        return new ResponseEntity<>(dataResult, HttpStatus.OK);
    }

    @GetMapping ("/getDataByProject/{customerId}/{projectId}")
    public ResponseEntity<?> getDataByCustomerId(@PathVariable ("customerId") final Integer customerId,
        @PathVariable ("projectId") final Integer projectId) throws IOException {

        List<Object> dataResult = new ArrayList<>();

        OverviewLoadPower dataResponseLoad = new OverviewLoadPower();
        OverviewPVPower dataResponsePV = new OverviewPVPower();
        OverviewGridPower dataResponseGrid = new OverviewGridPower();

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("yyyy-MM");

        LocalDateTime now = LocalDateTime.now();
        Date today = new Date();

        String date = dateFormat.format(now);
        String month = monthFormat.format(now);

        Map<String, String> condition = new HashMap<>();

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        if (cal.get(Calendar.MONTH) == Calendar.JANUARY) {
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
        } else {
            cal.roll(Calendar.MONTH, false);
        }

        int minute = cal.get(Calendar.MINUTE);
        // CHECKSTYLE:OFF
        minute = ( (minute / 5) - 1) * 5 - 10;
        if (minute >= 60) {
            cal.add(Calendar.HOUR_OF_DAY, 1);
            cal.set(Calendar.MINUTE, 0);
        } else {
            cal.set(Calendar.MINUTE, minute);
        }

        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:59");
        SimpleDateFormat startDateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        SimpleDateFormat nowDateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
        String fifteenDate = dateFormatWithTime.format(cal.getTime());
        String dateNow = dateFormatWithTime.format(new Date());
        String start = startDateFormatWithTime.format(new Date());
        String end = nowDateFormatWithTime.format(new Date());

        condition.put("schema", Schema.getSchemas(customerId));
        condition.put("systemTypeId", String.valueOf(1));
        condition.put("customerId", String.valueOf(customerId));
        condition.put("projectId", String.valueOf(projectId));
        condition.put("date", date);
        condition.put("month", month);
        condition.put("startDate", fifteenDate);
        condition.put("now", dateNow);
        condition.put("start", start);
        condition.put("end", end);

        List<Device> deviceIds = deviceService.getDataByProjectId(condition);

        Integer countDeviceLoad = 0;
        Integer countDeviceSolar = 0;
        Integer countDeviceGrid = 0;

        if (deviceIds.size() != 0) {

            for (int i = 0; i < deviceIds.size(); i++) {
                if (deviceIds.get(i)
                    .getSystemTypeId() == 1) {
                    String deviceId = String.valueOf(deviceIds.get(i)
                        .getDeviceId());
                    countDeviceLoad++;
                    condition.put("deviceId", deviceId);
                    condition.put("module", "load");

                    Project info = projectService.getProjectByDeviceId(condition);

                    Long sumEpInDay = dataLoadFrame1Service.getSumEpInDay(condition) != null
                        && dataLoadFrame1Service.getSumEpInDay(condition) > 0
                            ? dataLoadFrame1Service.getSumEpInDay(condition)
                            : 0;
                    Long sumEpInMonth = dataLoadFrame1Service.getSumEpInMonth(condition) != null
                        && dataLoadFrame1Service.getSumEpInMonth(condition) > 0
                            ? dataLoadFrame1Service.getSumEpInMonth(condition)
                            : 0;
                    Long sumEp = dataLoadFrame1Service.getSumEp(condition) != null
                        && dataLoadFrame1Service.getSumEp(condition) > 0
                            ? dataLoadFrame1Service.getSumEp(condition)
                            : 0;
                    Float pTotal = dataLoadFrame1Service.getPtotalInDay(condition) != null
                        && dataLoadFrame1Service.getPtotalInDay(condition) > 0
                            ? dataLoadFrame1Service.getPtotalInDay(condition)
                            : 0;
                    Float maxPtotal = dataLoadFrame1Service.getMaxTotalPowerInDay(condition) != null
                        && dataLoadFrame1Service.getMaxTotalPowerInDay(condition) > 0
                            ? dataLoadFrame1Service.getMaxTotalPowerInDay(condition)
                            : 0;
                    Float minPtotal = dataLoadFrame1Service.getMinTotalPowerInDay(condition) != null
                        && dataLoadFrame1Service.getMinTotalPowerInDay(condition) > 0
                            ? dataLoadFrame1Service.getMinTotalPowerInDay(condition)
                            : 0;
                    Float avgPtotal = dataLoadFrame1Service.getAvgTotalPowerInDay(condition) != null
                        && dataLoadFrame1Service.getAvgTotalPowerInDay(condition) > 0
                            ? dataLoadFrame1Service.getAvgTotalPowerInDay(condition)
                            : 0;

                    dataResponseLoad.setSumEnergyToday(
                        dataResponseLoad.getSumEnergyToday() != null && dataResponseLoad.getSumEnergyToday() > 0
                            ? dataResponseLoad.getSumEnergyToday()
                            : 0 + sumEpInDay);
                    dataResponseLoad.setSumEnergyCurMonth(
                        dataResponseLoad.getSumEnergyCurMonth() != null && dataResponseLoad.getSumEnergyCurMonth() > 0
                            ? dataResponseLoad.getSumEnergyCurMonth()
                            : 0 + sumEpInMonth);
                    dataResponseLoad
                        .setSumEnergy(dataResponseLoad.getSumEnergy() != null && dataResponseLoad.getSumEnergy() > 0
                            ? dataResponseLoad.getSumEnergy()
                            : 0 + sumEp);
                    dataResponseLoad.setPTotal(dataResponseLoad.getPTotal() != null && dataResponseLoad.getPTotal() > 0
                        ? dataResponseLoad.getPTotal()
                        : 0 + pTotal);
                    dataResponseLoad
                        .setMaxPtotal(dataResponseLoad.getMaxPtotal() != null && dataResponseLoad.getMaxPtotal() > 0
                            ? dataResponseLoad.getMaxPtotal()
                            : 0 + maxPtotal);
                    dataResponseLoad
                        .setMinPtotal(dataResponseLoad.getMinPtotal() != null && dataResponseLoad.getMinPtotal() > 0
                            ? dataResponseLoad.getMinPtotal()
                            : 0 + minPtotal);
                    dataResponseLoad
                        .setAvgTotal(dataResponseLoad.getAvgTotal() != null && dataResponseLoad.getAvgTotal() > 0
                            ? dataResponseLoad.getAvgTotal()
                            : 0 + avgPtotal);
                    dataResponseLoad.setSystemTypeId(deviceIds.get(i)
                        .getSystemTypeId());
                    dataResponseLoad.setLoadCount(countDeviceLoad);
                    if (info.getAddress() != null) {
                        dataResponseLoad.setAddress(info.getAddress());
                    }
                    if (info.getImgLoad() != null) {
                        dataResponseLoad.setImage(info.getImgLoad());
                    }

                }
                if (deviceIds.get(i)
                    .getSystemTypeId() == 2) {
                    String deviceId = String.valueOf(deviceIds.get(i)
                        .getDeviceId());
                    countDeviceSolar++;
                    condition.put("deviceId", deviceId);
                    condition.put("module", "pv");

                    Project info = projectService.getProjectByDeviceId(condition);

                    Long sumEpInDay = dataInverterService.getSumEpInDay(condition) != null
                        && dataInverterService.getSumEpInDay(condition) > 0
                            ? dataInverterService.getSumEpInDay(condition)
                            : 0;
                    Long sumEpInMonth = dataInverterService.getSumEpInMonth(condition) != null
                        && dataInverterService.getSumEpInMonth(condition) > 0
                            ? dataInverterService.getSumEpInMonth(condition)
                            : 0;
                    Long sumEp = dataInverterService.getSumEp(condition) != null
                        && dataInverterService.getSumEp(condition) > 0 ? dataInverterService.getSumEp(condition) : 0;
                    Float pTotal = dataInverterService.getPtotalInDay(condition) != null
                        && dataInverterService.getPtotalInDay(condition) > 0
                            ? dataInverterService.getPtotalInDay(condition)
                            : 0;
                    Float maxPtotal = dataInverterService.getMaxTotalPowerInDay(condition) != null
                        && dataInverterService.getMaxTotalPowerInDay(condition) > 0
                            ? dataInverterService.getMaxTotalPowerInDay(condition)
                            : 0;
                    Float minPtotal = dataInverterService.getMinTotalPowerInDay(condition) != null
                        && dataInverterService.getMinTotalPowerInDay(condition) > 0
                            ? dataInverterService.getMinTotalPowerInDay(condition)
                            : 0;
                    Float avgPtotal = dataInverterService.getAvgTotalPowerInDay(condition) != null
                        && dataInverterService.getAvgTotalPowerInDay(condition) > 0
                            ? dataInverterService.getAvgTotalPowerInDay(condition)
                            : 0;

                    dataResponsePV.setSumEnergyTodayPV(
                        dataResponsePV.getSumEnergyTodayPV() != null && dataResponsePV.getSumEnergyTodayPV() > 0
                            ? dataResponsePV.getSumEnergyTodayPV()
                            : 0 + sumEpInDay);
                    dataResponsePV.setSumEnergyCurMonthPV(
                        dataResponsePV.getSumEnergyCurMonthPV() != null && dataResponsePV.getSumEnergyCurMonthPV() > 0
                            ? dataResponsePV.getSumEnergyCurMonthPV()
                            : 0 + sumEpInMonth);
                    dataResponsePV
                        .setSumEnergyPV(dataResponsePV.getSumEnergyPV() != null && dataResponsePV.getSumEnergyPV() > 0
                            ? dataResponsePV.getSumEnergyPV()
                            : 0 + sumEp);
                    dataResponsePV.setRealTime(dataResponsePV.getRealTime() != null && dataResponsePV.getRealTime() > 0
                        ? dataResponsePV.getRealTime()
                        : 0 + pTotal);
                    dataResponsePV
                        .setMaxPtotal(dataResponsePV.getMaxPtotal() != null && dataResponsePV.getMaxPtotal() > 0
                            ? dataResponsePV.getMaxPtotal()
                            : 0 + maxPtotal);
                    dataResponsePV
                        .setMinPtotal(dataResponsePV.getMinPtotal() != null && dataResponsePV.getMinPtotal() > 0
                            ? dataResponsePV.getMinPtotal()
                            : 0 + minPtotal);
                    dataResponsePV
                        .setAvgPtotal(dataResponsePV.getAvgPtotal() != null && dataResponsePV.getAvgPtotal() > 0
                            ? dataResponsePV.getAvgPtotal()
                            : 0 + avgPtotal);
                    dataResponsePV.setSystemTypeId(deviceIds.get(i)
                        .getSystemTypeId());
                    dataResponsePV.setPvCount(countDeviceSolar);
                    if (info.getAddress() != null) {
                        dataResponsePV.setAddress(info.getAddress());
                    }
                    if (info.getImgPv() != null) {
                        dataResponsePV.setImage(info.getImgPv());
                    }
                }
                if (deviceIds.get(i)
                    .getSystemTypeId() == 5) {
                    String deviceId = String.valueOf(deviceIds.get(i)
                        .getDeviceId());
                    countDeviceGrid++;
                    condition.put("deviceId", deviceId);
                    condition.put("module", "grid");

                    Project info = projectService.getProjectByDeviceId(condition);

                    Long sumEpInDay = dataRmuDrawer1Service.getSumEpInDay(condition) != null
                        && dataRmuDrawer1Service.getSumEpInDay(condition) > 0
                            ? dataRmuDrawer1Service.getSumEpInDay(condition)
                            : 0;
                    Long sumEpInMonth = dataRmuDrawer1Service.getSumEpInMonth(condition) != null
                        && dataRmuDrawer1Service.getSumEpInMonth(condition) > 0
                            ? dataRmuDrawer1Service.getSumEpInMonth(condition)
                            : 0;
                    Long sumEp = dataRmuDrawer1Service.getSumEp(condition) != null
                        && dataRmuDrawer1Service.getSumEp(condition) > 0
                            ? dataRmuDrawer1Service.getSumEp(condition)
                            : 0;
                    Long pTotal = dataRmuDrawer1Service.getPtotalInDay(condition) != null
                        && dataRmuDrawer1Service.getPtotalInDay(condition) > 0
                            ? dataRmuDrawer1Service.getPtotalInDay(condition)
                            : 0;
                    Float maxPtotal = dataRmuDrawer1Service.getMaxTotalPowerInDay(condition) != null
                        && dataRmuDrawer1Service.getMaxTotalPowerInDay(condition) > 0
                            ? dataRmuDrawer1Service.getMaxTotalPowerInDay(condition)
                            : 0;
                    Float minPtotal = dataRmuDrawer1Service.getMinTotalPowerInDay(condition) != null
                        && dataRmuDrawer1Service.getMinTotalPowerInDay(condition) > 0
                            ? dataRmuDrawer1Service.getMinTotalPowerInDay(condition)
                            : 0;
                    Float avgPtotal = dataRmuDrawer1Service.getAvgTotalPowerInDay(condition) != null
                        && dataRmuDrawer1Service.getAvgTotalPowerInDay(condition) > 0
                            ? dataLoadFrame1Service.getAvgTotalPowerInDay(condition)
                            : 0;

                    dataResponseGrid.setSumEnergyTodayGrid(
                        dataResponseGrid.getSumEnergyTodayGrid() != null && dataResponseGrid.getSumEnergyTodayGrid() > 0
                            ? dataResponseGrid.getSumEnergyTodayGrid()
                            : 0 + sumEpInDay);
                    dataResponseGrid.setSumEnergyCurMonthGrid(dataResponseGrid.getSumEnergyCurMonthGrid() != null
                        && dataResponseGrid.getSumEnergyCurMonthGrid() > 0
                            ? dataResponseGrid.getSumEnergyCurMonthGrid()
                            : 0 + sumEpInMonth);
                    dataResponseGrid.setSumEnergyGrid(
                        dataResponseGrid.getSumEnergyGrid() != null && dataResponseGrid.getSumEnergyGrid() > 0
                            ? dataResponseGrid.getSumEnergyGrid()
                            : 0 + sumEp);
                    dataResponseGrid
                        .setRealTime(dataResponseGrid.getRealTime() != null && dataResponseGrid.getRealTime() > 0
                            ? dataResponseGrid.getRealTime()
                            : 0 + pTotal);
                    dataResponseGrid
                        .setMaxPtotal(dataResponseGrid.getMaxPtotal() != null && dataResponseGrid.getMaxPtotal() > 0
                            ? dataResponseGrid.getMaxPtotal()
                            : 0 + maxPtotal);
                    dataResponseGrid
                        .setMinPtotal(dataResponseGrid.getMinPtotal() != null && dataResponseGrid.getMinPtotal() > 0
                            ? dataResponseGrid.getMinPtotal()
                            : 0 + minPtotal);
                    dataResponseGrid
                        .setAvgPtotal(dataResponseGrid.getAvgPtotal() != null && dataResponseGrid.getAvgPtotal() > 0
                            ? dataResponseGrid.getAvgPtotal()
                            : 0 + avgPtotal);
                    dataResponseGrid.setSystemTypeId(deviceIds.get(i)
                        .getSystemTypeId());
                    dataResponseGrid.setGridCount(countDeviceGrid);
                    if (info.getAddress() != null) {
                        dataResponseGrid.setAddress(info.getAddress());
                    }
                    if (info.getImgGrid() != null) {
                        dataResponseGrid.setImage(info.getImgGrid());
                    }
                }
            }
        }

        dataResult.add(dataResponseLoad);
        dataResult.add(dataResponsePV);
        dataResult.add(dataResponseGrid);

        return new ResponseEntity<>(dataResult, HttpStatus.OK);
    }

    // New Overview

    @GetMapping ("/getDataPowerAndEnergyInHomePage")
    public ResponseEntity<DataInstant> getDataPowerAndEnergyInHomePage(
        @RequestParam ("projectId") final Integer projectId, @RequestParam ("customerId") final Integer customerId,
        @RequestParam (value = "time", required = false) final String time,
        @RequestParam (value = "type", required = false) final Integer type,
        @RequestParam (value = "ids", required = false) final String ids) {

        DataInstant Data = new DataInstant();
        HashMap<String, String> condition = new HashMap<>();
        String proIds = "";
        if (ids == "") {
            proIds = null;
        } else {
            proIds = ids;
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

        String toDate = DateUtils.toString(new Date(), Constants.ES.DATETIME_FORMAT_YMDHMS);
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fromDate = dateFormatWithTime.format(currentTime.getTime());
        String day = DateUtils.toString(new Date(), Constants.ES.DATE_FORMAT_YMD);
        String month = DateUtils.toString(new Date(), Constants.ES.DATE_FORMAT_YM_02);
        String year = DateUtils.toString(new Date(), "yyyy");

        condition.put("schema", Schema.getSchemas(customerId));
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put("day", day);
        condition.put("month", month);
        condition.put("year", year);
        condition.put("customerId", String.valueOf(customerId));
        condition.put("ids", proIds);
        if (projectId != 0) {
            condition.put("ids", null);
            condition.put("projectId", String.valueOf(projectId));
        }
        if (time != null && type != null) {
            if (type == 1) {
                condition.put("day", time);
            }
            if (type == 2) {
                condition.put("month", time);
            }
            if (type == 3) {
                condition.put("year", time);
            }
        }

        Float sumPowerLoad = deviceService.getPowerInstanceLoad(condition);
        Float sumPowerInverter = deviceService.getPowerInstanceInverter(condition);
        // Float sumPowerGrid = deviceService.getPowerInstanceGrid(condition);
        String sentDateLoad = deviceService.getSentDateInstanceLoad(condition);

        Data.setPowerInstantLoad(sumPowerLoad != null ? sumPowerLoad : null);
        Data.setPowerInstantSolar(sumPowerInverter != null ? sumPowerInverter : null);
        Data.setPowerInstantGrid(sumPowerLoad != null ? sumPowerLoad : null);
        Data.setSentDate(sentDateLoad);
        // if (sumPowerGrid != null) {
        // Data.setPowerInstantGrid(sumPowerGrid);
        // } else {
        // if (sumPowerLoad != null) {
        // Float sumLoad = sumPowerLoad;
        // Float sumSolar = sumPowerInverter != null ? sumPowerInverter : 0;
        // Data.setPowerInstantGrid(sumLoad - sumSolar);
        // } else {
        // Data.setPowerInstantGrid(0f);
        // }
        // }

        Float sumEnergyInDayLoad = deviceService.getEnergyInDayLoad(condition);
        Float sumEnergyInDayInverter = deviceService.getEnergyInDayInverter(condition);
        // Float sumEnergyInDayGrid = deviceService.getEnergyInDayGrid(condition);

        Data.setEnergyInDayLoad(sumEnergyInDayLoad != null ? sumEnergyInDayLoad : null);
        Data.setEnergyInDaySolar(sumEnergyInDayInverter != null ? sumEnergyInDayInverter : null);
        // if (sumEnergyInDayGrid != null) {
        // Data.setEnergyInDayGrid(sumEnergyInDayGrid);
        // } else {
        // if (sumEnergyInDayLoad != null) {
        // Float sumLoad = sumEnergyInDayLoad;
        // Float sumSolar = sumEnergyInDayInverter != null ? sumEnergyInDayInverter : 0;
        // Data.setEnergyInDayGrid(sumLoad - sumSolar);
        // } else {
        // Data.setEnergyInDayGrid(0f);
        // }
        // }

        Float sumEnergyInMonthLoad = deviceService.getEnergyInMonthLoad(condition);
        Float sumEnergyInMonthInverter = deviceService.getEnergyInMonthInverter(condition);
        // Float sumEnergyInMonthGrid = deviceService.getEnergyInMonthGrid(condition);

        Data.setEnergyInMonthLoad(sumEnergyInMonthLoad != null ? sumEnergyInMonthLoad : null);
        Data.setEnergyInMonthSolar(sumEnergyInMonthInverter != null ? sumEnergyInMonthInverter : null);
        // if (sumEnergyInMonthGrid != null) {
        // Data.setEnergyInMonthGrid(sumEnergyInMonthGrid);
        // } else {
        // if (sumEnergyInMonthLoad != null) {
        // Float sumLoad = sumEnergyInMonthLoad;
        // Float sumSolar = sumEnergyInMonthInverter != null ? sumEnergyInMonthInverter
        // : 0;
        // Data.setEnergyInMonthGrid(sumLoad - sumSolar);
        // } else {
        // Data.setEnergyInMonthGrid(0f);
        // }
        // }

        Float sumEnergyInYearLoad = deviceService.getEnergyInYearLoad(condition);
        Float sumEnergyInYearInverter = deviceService.getEnergyInYearInverter(condition);
        // Float sumEnergyInYearGrid = deviceService.getEnergyInYearGrid(condition);

        Data.setEnergyInYearLoad(sumEnergyInYearLoad != null ? sumEnergyInYearLoad : null);
        Data.setEnergyInYearSolar(sumEnergyInYearInverter != null ? sumEnergyInYearInverter : null);
        // Data.setEnergyInYearGrid(sumEnergyInYearGrid != null ? sumEnergyInYearGrid :
        // null);
        // if (sumEnergyInYearGrid != null) {
        // Data.setEnergyInYearGrid(sumEnergyInYearGrid);
        // } else {
        // if (sumEnergyInYearLoad != null) {
        // Float sumLoad = sumEnergyInYearLoad;
        // Float sumSolar = sumEnergyInYearInverter != null ? sumEnergyInYearInverter :
        // 0;
        // Data.setEnergyInYearGrid(sumLoad - sumSolar);
        // } else {
        // Data.setEnergyInYearGrid(0f);
        // }
        // }

        List<Device> listDeviceLoad = deviceService.getListDeviceLoad(condition);
        Integer sumTotalLoadE = 0;
        for (Device dvLoad : listDeviceLoad) {
            condition.put("deviceId", String.valueOf(dvLoad.getDeviceId()));
            condition.put("type", "meter");
            Integer energy = deviceService.getEnergyTotalByDeviceId(condition);
            sumTotalLoadE += energy != null && energy > 0 ? energy : 0;
        }
        Data.setEnergyTotalLoad(sumTotalLoadE);

        List<Device> listDeviceInverter = deviceService.getListDeviceInverter(condition);
        Integer sumTotalInverterE = 0;
        for (Device dvInverter : listDeviceInverter) {
            condition.put("deviceId", String.valueOf(dvInverter.getDeviceId()));
            condition.put("type", "inverter");
            Integer energy = deviceService.getEnergyTotalByDeviceId(condition);
            sumTotalInverterE += energy != null && energy > 0 ? energy : 0;
        }
        Data.setEnergyTotalSolar(sumTotalInverterE);

        // List<Device> listDeviceRMU = deviceService.getListDeviceRMU(condition);
        // Integer sumTotalRMUE = 0;
        // for (Device dvRMU : listDeviceRMU) {
        // condition.put("deviceId", String.valueOf(dvRMU.getDeviceId()));
        // condition.put("type", "rmu_drawer");
        // Integer energy = deviceService.getEnergyTotalByDeviceId(condition);
        // sumTotalRMUE += energy != null && energy > 0 ? energy : 0;
        // }
        // if (sumTotalRMUE != 0) {
        // Data.setEnergyTotalGrid(sumTotalRMUE);
        // } else {
        // if (sumTotalLoadE != 0) {
        // Integer sumLoad = sumTotalLoadE;
        // Integer sumSolar = sumTotalInverterE != null ? sumTotalInverterE : 0;
        // Data.setEnergyTotalGrid(sumLoad - sumSolar);
        // } else {
        // Data.setEnergyTotalGrid(0);
        // }
        // }

        return new ResponseEntity<DataInstant>(Data, HttpStatus.OK);
    }

    @GetMapping ("/getListDataPowerByTime")
    public ResponseEntity<Object> getListDataPowerByTime(@RequestParam ("projectId") final Integer projectId,
        @RequestParam ("customerId") final Integer customerId,
        @RequestParam (value = "time", required = false) final String time,
        @RequestParam (value = "type", required = false) final Integer type) {

        List<Object> result = new ArrayList<>();

        String day = DateUtils.toString(new Date(), Constants.ES.DATE_FORMAT_YMD);
        String month = DateUtils.toString(new Date(), Constants.ES.DATE_FORMAT_YM_02);
        String year = DateUtils.toString(new Date(), "yyyy");

        HashMap<String, String> condition = new HashMap<>();
        condition.put("schema", Schema.getSchemas(customerId));
        condition.put("day", day);
        condition.put("month", month);
        condition.put("year", year);
        condition.put("customerId", String.valueOf(customerId));
        if (projectId != 0) {
            condition.put("projectId", String.valueOf(projectId));
        }
        if (time != null && type != null) {
            if (type == 1) {
                condition.put("day", time);
            }
            if (type == 2) {
                condition.put("month", time);
            }
            if (type == 3) {
                condition.put("year", time);
            }
        }

        List<DataPower> listPowerLoad = deviceService.getListDataPowerLoadByDay(condition);

        List<DataPower> listPowerSolar = deviceService.getListDataPowerSolarByDay(condition);

        List<DataPower> listPowerGrid = deviceService.getListDataPowerGridByDay(condition);

        List<DataPower> listPowerLoadMonth = deviceService.getListDataPowerLoadByMonth(condition);

        List<DataPower> listPowerSolarMonth = deviceService.getListDataPowerSolarByMonth(condition);

        List<DataPower> listPowerGridMonth = deviceService.getListDataPowerGridByMonth(condition);

        List<DataPower> listPowerLoadYear = deviceService.getListDataPowerLoadByYear(condition);

        List<DataPower> listPowerSolarYear = deviceService.getListDataPowerSolarByYear(condition);

        List<DataPower> listPowerGridYear = deviceService.getListDataPowerGridByYear(condition);

        List<DataPower> listPowerLoadAll = deviceService.getListDataPowerLoadAll(condition);

        List<DataPower> listPowerSolarAll = deviceService.getListDataPowerSolarAll(condition);

        List<DataPower> listPowerGridAll = deviceService.getListDataPowerGridAll(condition);

        result.add(listPowerLoad);
        result.add(listPowerSolar);
        result.add(listPowerGrid);
        result.add(listPowerLoadMonth);
        result.add(listPowerSolarMonth);
        result.add(listPowerGridMonth);
        result.add(listPowerLoadYear);
        result.add(listPowerSolarYear);
        result.add(listPowerGridYear);
        result.add(listPowerLoadAll);
        result.add(listPowerSolarAll);
        result.add(listPowerGridAll);

        List<DataView> listPowerDay = new ArrayList<>();
        List<DataView> listPowerMonth = new ArrayList<>();
        List<DataView> listPowerYear = new ArrayList<>();
        List<DataView> listPowerTotal = new ArrayList<>();

        String[] quarterHours = {"00", "15", "30", "45"};

        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 4; j++) {
                DataView data = new DataView();
                String time15Minute = i + ":" + quarterHours[j] + ":00";
                if (i < 10) {
                    time15Minute = "0" + time15Minute;
                }
                data.setTime(day + " " + time15Minute);
                listPowerDay.add(data);
            }
        }

        for (DataView data : listPowerDay) {
            for (DataPower dataL : listPowerLoad) {
                if (data.getTime()
                    .equals(dataL.getViewTime())) {
                    data.setPowerLoad(dataL.getPower());
                }
            }
        }

        for (DataView data : listPowerDay) {
            for (DataPower dataL : listPowerSolar) {
                if (data.getTime()
                    .equals(dataL.getViewTime())) {
                    data.setPowerPV(dataL.getPower());
                }
            }
        }

        String dayCurrent = DateUtils.toString(new Date(), "dd");
        Integer d = Integer.valueOf(dayCurrent);
        for (int i = 1; i <= d; i++) {
            String dayCur = "";
            if (i < 10) {
                dayCur = "0" + i;
            } else {
                dayCur = String.valueOf(i);
            }
            DataView data = new DataView();
            String timeDay = month + "-" + dayCur;
            data.setTime(timeDay);
            listPowerMonth.add(data);
        }

        for (DataView data : listPowerMonth) {
            for (DataPower dataL : listPowerLoadMonth) {
                if (data.getTime()
                    .equals(dataL.getViewTime())) {
                    data.setPowerLoad(dataL.getPower());
                }
            }
        }

        for (DataView data : listPowerMonth) {
            for (DataPower dataL : listPowerSolarMonth) {
                if (data.getTime()
                    .equals(dataL.getViewTime())) {
                    data.setPowerPV(dataL.getPower());
                }
            }
        }

        for (int i = 1; i <= 12; i++) {
            String monthCur = "";
            if (i < 10) {
                monthCur = "0" + i;
            } else {
                monthCur = String.valueOf(i);
            }
            DataView data = new DataView();
            String timeDay = year + "-" + monthCur;
            data.setTime(timeDay);
            listPowerYear.add(data);
        }

        for (DataView data : listPowerYear) {
            for (DataPower dataL : listPowerLoadYear) {
                if (data.getTime()
                    .equals(dataL.getViewTime())) {
                    data.setPowerLoad(dataL.getPower());
                }
            }
        }

        for (DataView data : listPowerYear) {
            for (DataPower dataL : listPowerSolarYear) {
                if (data.getTime()
                    .equals(dataL.getViewTime())) {
                    data.setPowerPV(dataL.getPower());
                }
            }
        }

        for (DataPower data : listPowerLoadAll) {
            DataView dataV = new DataView();
            dataV.setPowerLoad(data.getPower());
            dataV.setTime(data.getViewTime());
            listPowerTotal.add(dataV);
        }

        for (DataPower data : listPowerSolarAll) {
            for (DataView dataView : listPowerTotal) {
                if (dataView.getTime()
                    .equals(data.getViewTime())) {
                    dataView.setPowerPV(data.getPower());
                } else {
                    DataView dataV = new DataView();
                    dataV.setPowerPV(data.getPower());
                    dataV.setTime(data.getViewTime());
                    listPowerTotal.add(dataV);
                }
            }
        }

        result.add(listPowerDay);
        result.add(listPowerMonth);
        result.add(listPowerYear);
        result.add(listPowerTotal);

        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @GetMapping ("/getDataTab1")
    public ResponseEntity<Object> getDataTab1(@RequestParam ("customerId") final Integer customerId,
        @RequestParam (value = "projectId", required = false) final Integer projectId,
        @RequestParam (value = "time", required = false) final Integer time,
        @RequestParam (value = "type", required = false) final Integer type,
        @RequestParam (value = "fDate", required = false) final String fDate,
        @RequestParam (value = "tDate", required = false) final String tDate,
        @RequestParam (value = "ids", required = false) final String ids) {

        List<Object> result = new ArrayList<>();

        String currentDay = DateUtils.toString(new Date(), "dd");
        String currentMonth = DateUtils.toString(new Date(), "MM");
        String currentYear = DateUtils.toString(new Date(), "yyyy");
        String day = DateUtils.toString(new Date(), Constants.ES.DATE_FORMAT_YMD);
        String month = DateUtils.toString(new Date(), Constants.ES.DATE_FORMAT_YM_02);
        String year = DateUtils.toString(new Date(), "yyyy");

        HashMap<String, String> condition = new HashMap<>();
        String proIds = "";

        if (projectId == null) {

            condition.put("customerId", String.valueOf(customerId));
            if (ids == "") {
                proIds = null;
            } else {
                proIds = ids;
            }
            List<Project> listProject = projectService.getListProjectByCustomerId(String.valueOf(customerId), proIds);

            for (Project project : listProject) {

                DataPowerResult obj = new DataPowerResult();
                obj.setName(project.getProjectName());
                List<DataPower> listPower = new ArrayList<>();
                condition.put("projectId", String.valueOf(project.getProjectId()));
                condition.put("schema", Schema.getSchemas(customerId));
                condition.put("day", day);
                condition.put("month", month);
                condition.put("year", year);

                if (time == 0) {
                    if (fDate != null) {
                        condition.put("day", fDate);
                        day = fDate;
                    }
                    if (type == 1) {
                        listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListDataPowerSolarByDay(condition);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerBatteruByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerWindByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListDataPowerGridByDay(condition);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }

                    List<DataPower> listPowerResult = new ArrayList<>();

                    String[] quarterHours = {"00", "15", "30", "45"};

                    for (int i = 0; i < 24; i++) {
                        for (int j = 0; j < 4; j++) {
                            DataPower data = new DataPower();
                            String time15Minute = i + ":" + quarterHours[j] + ":00";
                            if (i < 10) {
                                time15Minute = "0" + time15Minute;
                            }
                            data.setViewTime(day + " " + time15Minute);
                            data.setPower(0f);
                            listPowerResult.add(data);
                        }
                    }

                    for (DataPower dataR : listPowerResult) {

                        for (DataPower data : listPower) {
                            if (dataR.getViewTime()
                                .equals(data.getViewTime())) {
                                dataR.setPower(data.getPower());
                            }
                        }

                    }
                    obj.setListDataPower(listPower);
                }

                if (time == 1) {
                    Boolean leapYear = false;
                    if (fDate != null) {
                        condition.put("month", fDate);
                        month = fDate;
                        Integer yearQ = Integer.valueOf(fDate.substring(0, 4));
                        if ( (yearQ % 4 == 0 && yearQ % 100 != 0) || yearQ % 400 == 0) {
                            leapYear = true;
                        }
                        String monthOfYear = fDate.substring(5);
                        switch (monthOfYear) {
                            case "01":
                            case "03":
                            case "05":
                            case "07":
                            case "08":
                            case "10":
                            case "12":
                                currentDay = String.valueOf(31);
                                break;
                            case "04":
                            case "06":
                            case "09":
                            case "11":
                                currentDay = String.valueOf(30);
                                break;
                            case "02":
                                if (leapYear) {
                                    currentDay = String.valueOf(29);
                                    break;
                                } else {
                                    currentDay = String.valueOf(28);
                                    break;
                                }
                        }
                    }

                    if (type == 1) {
                        listPower = deviceService.getListDataPowerLoadByMonth(condition);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListDataPowerSolarByMonth(condition);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerBatteruByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerWindByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListDataPowerGridByMonth(condition);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }

                    List<DataPower> listPowerResult = new ArrayList<>();

                    for (int i = 1; i <= Integer.valueOf(currentDay); i++) {
                        DataPower dataP = new DataPower();
                        String dayIn = "";
                        if (i < 10) {
                            dayIn = "0" + i;
                        } else {
                            dayIn = String.valueOf(i);
                        }
                        String dayOfMonth = month + "-" + dayIn;
                        dataP.setViewTime(dayOfMonth);
                        dataP.setPower(0f);
                        listPowerResult.add(dataP);
                    }

                    for (DataPower dataR : listPowerResult) {

                        for (DataPower data : listPower) {
                            if (dataR.getViewTime()
                                .equals(data.getViewTime())) {
                                dataR.setPower(data.getPower());
                            }
                        }

                    }

                    obj.setListDataPower(listPowerResult);
                }

                if (time == 2) {

                    if (fDate != null) {
                        condition.put("year", fDate);
                        year = fDate;
                    }
                    if (type == 1) {
                        listPower = deviceService.getListDataPowerLoadByYear(condition);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListDataPowerSolarByYear(condition);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerBatteruByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerWindByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListDataPowerGridByYear(condition);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }

                    List<DataPower> listPowerResult = new ArrayList<>();
                    for (int i = 1; i <= 12; i++) {
                        DataPower dataP = new DataPower();
                        String monthIn = "";
                        if (i < 10) {
                            monthIn = "0" + i;
                        } else {
                            monthIn = String.valueOf(i);
                        }
                        String monthOfYear = year + "-" + monthIn;
                        dataP.setViewTime(monthOfYear);
                        dataP.setPower(0f);
                        listPowerResult.add(dataP);
                    }

                    for (DataPower dataR : listPowerResult) {

                        for (DataPower data : listPower) {
                            if (dataR.getViewTime()
                                .equals(data.getViewTime())) {
                                dataR.setPower(data.getPower());
                            }
                        }

                    }

                    obj.setListDataPower(listPowerResult);
                }

                if (time == 3) {
                    if (type == 1) {
                        listPower = deviceService.getListDataPowerLoadAll(condition);
                        obj.setListDataPower(listPower);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListDataPowerSolarAll(condition);
                        obj.setListDataPower(listPower);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerBatteruByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerWindByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListDataPowerGridAll(condition);
                        obj.setListDataPower(listPower);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                }

                if (time == 4) {
                    if (tDate != null) {
                        String fromDate = fDate;
                        String toDate = tDate;
                        condition.put("fromDate", fromDate);
                        condition.put("toDate", toDate);
                        condition.put("energy", "energy");
                        condition.put("viewType", "3");
                    }
                    if (type == 1) {
                        listPower = deviceService.getListDataPowerLoadByDay(condition);
                        obj.setListDataPower(listPower);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListDataPowerSolarByDay(condition);
                        obj.setListDataPower(listPower);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerBatteruByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerWindByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListDataPowerGridByDay(condition);
                        obj.setListDataPower(listPower);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                }
                result.add(obj);
            }
        } else {
            condition.put("projectId", String.valueOf(projectId));
            condition.put("schema", Schema.getSchemas(customerId));
            condition.put("day", day);
            condition.put("month", month);
            condition.put("year", year);
            condition.put("calculateFlag", String.valueOf(1));
            condition.put("deleteFlag", String.valueOf(0));

            if (time == 0) {
                if (fDate != null) {
                    condition.put("day", fDate);
                    day = fDate;
                }
                condition.put("systemType", String.valueOf(type));
                List<Device> listDevice = deviceService.getDeviceByProjectId(condition);
                for (Device device : listDevice) {
                    DataPowerResult obj = new DataPowerResult();
                    condition.put("deviceId", String.valueOf(device.getDeviceId()));
                    List<DataPower> listPower = new ArrayList<>();
                    obj.setName(device.getDeviceName());
                    if (type == 1) {
                        listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListDataPowerSolarByDay(condition);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListDataPowerGridByDay(condition);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }

                    List<DataPower> listPowerResult = new ArrayList<>();

                    String[] quarterHours = {"00", "15", "30", "45"};

                    for (int i = 0; i < 24; i++) {
                        for (int j = 0; j < 4; j++) {
                            DataPower data = new DataPower();
                            String time15Minute = i + ":" + quarterHours[j] + ":00";
                            if (i < 10) {
                                time15Minute = "0" + time15Minute;
                            }
                            data.setViewTime(day + " " + time15Minute);
                            data.setPower(0f);
                            listPowerResult.add(data);
                        }
                    }

                    for (DataPower dataR : listPowerResult) {

                        for (DataPower data : listPower) {
                            if (dataR.getViewTime()
                                .equals(data.getViewTime())) {
                                dataR.setPower(data.getPower());
                            }
                        }

                    }
                    obj.setListDataPower(listPower);

                    result.add(obj);
                }
            }
            if (time == 1) {
                Boolean leapYear = false;
                if (fDate != null) {
                    condition.put("month", fDate);
                    month = fDate;
                    Integer yearQ = Integer.valueOf(fDate.substring(0, 4));
                    if ( (yearQ % 4 == 0 && yearQ % 100 != 0) || yearQ % 400 == 0) {
                        leapYear = true;
                    }
                    String monthOfYear = fDate.substring(5);
                    switch (monthOfYear) {
                        case "01":
                        case "03":
                        case "05":
                        case "07":
                        case "08":
                        case "10":
                        case "12":
                            currentDay = String.valueOf(31);
                            break;
                        case "04":
                        case "06":
                        case "09":
                        case "11":
                            currentDay = String.valueOf(30);
                            break;
                        case "02":
                            if (leapYear) {
                                currentDay = String.valueOf(29);
                                break;
                            } else {
                                currentDay = String.valueOf(28);
                                break;
                            }
                    }
                }

                condition.put("systemType", String.valueOf(type));
                List<Device> listDevice = deviceService.getDeviceByProjectId(condition);
                for (Device device : listDevice) {
                    DataPowerResult obj = new DataPowerResult();
                    condition.put("deviceId", String.valueOf(device.getDeviceId()));
                    List<DataPower> listPower = new ArrayList<>();
                    obj.setName(device.getDeviceName());
                    if (type == 1) {
                        listPower = deviceService.getListDataPowerLoadByMonth(condition);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListDataPowerSolarByMonth(condition);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListDataPowerGridByMonth(condition);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }

                    List<DataPower> listPowerResult = new ArrayList<>();

                    for (int i = 1; i <= Integer.valueOf(currentDay); i++) {
                        DataPower dataP = new DataPower();
                        String dayIn = "";
                        if (i < 10) {
                            dayIn = "0" + i;
                        } else {
                            dayIn = String.valueOf(i);
                        }
                        String dayOfMonth = month + "-" + dayIn;
                        dataP.setViewTime(dayOfMonth);
                        dataP.setPower(0f);
                        listPowerResult.add(dataP);
                    }

                    for (DataPower dataR : listPowerResult) {

                        for (DataPower data : listPower) {
                            if (dataR.getViewTime()
                                .equals(data.getViewTime())) {
                                dataR.setPower(data.getPower());
                            }
                        }

                    }

                    obj.setListDataPower(listPowerResult);
                    result.add(obj);
                }
            }
            if (time == 2) {
                if (fDate != null) {
                    condition.put("year", fDate);
                    year = fDate;
                }
                condition.put("systemType", String.valueOf(type));
                List<Device> listDevice = deviceService.getDeviceByProjectId(condition);
                for (Device device : listDevice) {
                    DataPowerResult obj = new DataPowerResult();
                    condition.put("deviceId", String.valueOf(device.getDeviceId()));
                    List<DataPower> listPower = new ArrayList<>();
                    obj.setName(device.getDeviceName());
                    if (type == 1) {
                        listPower = deviceService.getListDataPowerLoadByYear(condition);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListDataPowerSolarByYear(condition);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListDataPowerGridByYear(condition);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }

                    List<DataPower> listPowerResult = new ArrayList<>();
                    for (int i = 1; i <= 12; i++) {
                        DataPower dataP = new DataPower();
                        String monthIn = "";
                        if (i < 10) {
                            monthIn = "0" + i;
                        } else {
                            monthIn = String.valueOf(i);
                        }
                        String monthOfYear = year + "-" + monthIn;
                        dataP.setViewTime(monthOfYear);
                        dataP.setPower(0f);
                        listPowerResult.add(dataP);
                    }

                    for (DataPower dataR : listPowerResult) {

                        for (DataPower data : listPower) {
                            if (dataR.getViewTime()
                                .equals(data.getViewTime())) {
                                dataR.setPower(data.getPower());
                            }
                        }

                    }

                    obj.setListDataPower(listPowerResult);
                    result.add(obj);
                }
            }
            if (time == 3) {
                condition.put("systemType", String.valueOf(type));
                List<Device> listDevice = deviceService.getDeviceByProjectId(condition);
                for (Device device : listDevice) {
                    DataPowerResult obj = new DataPowerResult();
                    condition.put("deviceId", String.valueOf(device.getDeviceId()));
                    List<DataPower> listPower = new ArrayList<>();
                    obj.setName(device.getDeviceName());
                    if (type == 1) {
                        listPower = deviceService.getListDataPowerLoadAll(condition);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListDataPowerSolarAll(condition);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListDataPowerGridAll(condition);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    obj.setListDataPower(listPower);
                    result.add(obj);
                }
            }
            if (time == 4) {
                String fromDate = fDate;
                String toDate = tDate;

                if (tDate != null) {
                    condition.put("fromDate", fromDate);
                    condition.put("toDate", toDate);
                    condition.put("energy", "enegy");
                }
                condition.put("viewType", "3");
                condition.put("systemType", String.valueOf(type));
                List<Device> listDevice = deviceService.getDeviceByProjectId(condition);

                for (Device device : listDevice) {
                    List<String> dateList = new ArrayList<>();
                    List<DataPower> listPowerResult = new ArrayList<>();
                    // Chuyển đổi chuỗi thành đối tượng LocalDate
                    LocalDate startDate = LocalDate.parse(fromDate);
                    LocalDate endDate = LocalDate.parse(toDate);

                    // Thêm ngày đầu vào danh sách
                    dateList.add(fDate);

                    // Lặp qua từng ngày tiếp theo cho đến ngày cuối
                    LocalDate currentDate = startDate;
                    // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    while (currentDate.isBefore(endDate)) {
                        currentDate = currentDate.plus(1, ChronoUnit.DAYS);
                        dateList.add(currentDate.format(formatter));
                    }

                    for (String date : dateList) {
                        DataPower data = new DataPower();
                        data.setViewTime(date);
                        data.setPower(0f);
                        listPowerResult.add(data);
                    }
                    DataPowerResult obj = new DataPowerResult();
                    condition.put("deviceId", String.valueOf(device.getDeviceId()));
                    List<DataPower> listPower = new ArrayList<>();
                    obj.setName(device.getDeviceName());
                    if (type == 1) {
                        listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListDataPowerSolarByDay(condition);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListDataPowerGridByDay(condition);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }


                    for (DataPower dataR : listPowerResult) {

                        for (DataPower data : listPower) {
                            if (dataR.getViewTime()
                                .equals(data.getViewTime())) {
                                dataR.setPower(data.getPower());
                            }
                        }

                    }


                    obj.setListDataPower(listPowerResult);
                    result.add(obj);
                }
            }
        }

        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    public String switchViewTime(Integer value, String year) {
        switch (value) {
            case 1:
                return "01-" + year;
            case 2:
                return "02-" + year;
            case 3:
                return "03-" + year;
            case 4:
                return "04-" + year;
            case 5:
                return "05-" + year;
            case 6:
                return "06-" + year;
            case 7:
                return "07-" + year;
            case 8:
                return "08-" + year;
            case 9:
                return "09-" + year;
            case 10:
                return "10-" + year;
            case 11:
                return "11-" + year;
            case 12:
                return "12-" + year;
            default:
                return "Invalid input";
        }
    }

    public String switchViewYear(Integer value, String year) {
        switch (value) {
            case 1:
                return year + "-01";
            case 2:
                return year + "-02";
            case 3:
                return year + "-03";
            case 4:
                return year + "-04";
            case 5:
                return year + "-05";
            case 6:
                return year + "-06";
            case 7:
                return year + "-07";
            case 8:
                return year + "-08";
            case 9:
                return year + "-09";
            case 10:
                return year + "-10";
            case 11:
                return year + "-11";
            case 12:
                return year + "-12";
            default:
                return "Invalid input";
        }
    }
    
    @GetMapping ("/getDataFlowSensor")
    public ResponseEntity<Object> getDataTab7(@RequestParam ("customerId") final Integer customerId,
            @RequestParam (value = "projectId", required = false) final Integer projectId,
            @RequestParam (value = "time", required = false) final Integer time,
            @RequestParam (value = "type", required = false) final Integer type,
            @RequestParam (value = "fDate", required = false) final String fDate,
            @RequestParam (value = "tDate", required = false) final String tDate,
            @RequestParam (value = "fuelFormId", required = false) final String fuelFormId,
            @RequestParam (value = "ids", required = false) final String ids) {
            List<Object> result = new ArrayList<>();
        

            String day = DateUtils.toString(new Date(), Constants.ES.DATE_FORMAT_YMD);
            String month = DateUtils.toString(new Date(), Constants.ES.DATE_FORMAT_YM_02);
            String currMonth = DateUtils.toString(new Date(), "MM");
            String year = DateUtils.toString(new Date(), "yyyy");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatterMonth = new SimpleDateFormat("yyyy-MM");

            HashMap<String, String> condition = new HashMap<>();
            String proIds = "";
       
            if (projectId == null) {
            	   condition.put("customerId", String.valueOf(customerId));
                   if (ids == "") {
                       proIds = null;
                   } else {
                       proIds = ids;
                   }
                   List<Project> listProject = projectService.getListProjectByCustomerId(String.valueOf(customerId), proIds);
            	for (Project project : listProject) {
            		condition.put("projectId", String.valueOf(project.getProjectId()));
                    condition.put("schema", Schema.getSchemas(customerId));
                    condition.put("day", day);
                    condition.put("month", month);
                    condition.put("systemType", String.valueOf(type));
                    condition.put("year", year);
                    condition.put("calculateFlag", String.valueOf(1));
                    condition.put("deviceType", String.valueOf(10));
                    condition.put("deleteFlag", String.valueOf(0));
                    condition.put("fuelFormId", String.valueOf(fuelFormId));
                        condition.put("customerId", String.valueOf(customerId));
                        if (ids == "") {
                            proIds = null;
                        } else {
                            proIds = ids;
                        }
                        DataPowerResult obj = new DataPowerResult();
                        List<Object> result2 = new ArrayList<>();
                        obj.setName(project.getProjectName());
                        List<Device> listDevice = deviceService.getDeviceByProjectId(condition);
                        for (Device device : listDevice) {
                        	condition.put("deviceId", String.valueOf(device.getDeviceId()));
                       
                            DataPowerResult dataPower = new DataPowerResult();
                        
                            dataPower.setName(device.getDeviceName());
                       

                            List<DataPower> listPowerT = new ArrayList<>();
                            if (time == 0) {
                                Calendar calendar = Calendar.getInstance();
                                Date date = calendar.getTime();
                                String currDayOfWeek = new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime());
                                String currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
                                String currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
                                String dayOrFdate = new SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH).format(date.getTime());
                                List<DataPower> listPowerResult = new ArrayList<>();

                                if (fDate != null) {
                                    condition.put("day", fDate);
                                    day = fDate;
                                    try {
                                        date = formatter.parse(fDate);
                                        currDayOfWeek = new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime());
                                        currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
                                        currentYear = fDate.substring(0, 4);
                                        dayOrFdate = new SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH).format(date.getTime());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (type == 1) {
                                    listPowerT = deviceService.getListTLoadByDay(condition);
                                    String[] quarterHours = {"00", "15", "30", "45"};

                                 // Lấy thời gian hiện tại
                                    LocalTime currentTime = LocalTime.now();
                                    int currentHour = currentTime.getHour();

                                    // Vòng lặp để tạo ra các khoảng thời gian đến thời điểm hiện tại
                                    for (int i = 0; i <= currentHour; i++) {
                                        DataPower data = new DataPower();
                                        // Tạo chuỗi thời gian
                                        String hourTime = i + ":00:00";
                                        if (i < 10) {
                                            hourTime = "0" + hourTime;
                                        }
                                        data.setViewTime(day + " " + hourTime);
                                        data.setPower(0f);
                                        listPowerResult.add(data);
                                    }

      
                                    for (DataPower dataR : listPowerResult) {

                                        for (DataPower data : listPowerT) {
                                            if (dataR.getViewTime()
                                                .equals(data.getViewTime())) {
                                                dataR.setPower(data.getPower());
                                                dataR.setPowerAccumulated(data.getPowerAccumulated());
                                            }
                                        }

                                    }
                                    dataPower.setListDataPower(listPowerResult);
                                    
//                                    obj.setListDataPower(listPowerT);
                                 
                                    }

                            }
                            
                            if (time == 1) {

                                Calendar calendar = Calendar.getInstance();
                                Date date = calendar.getTime();
                                String currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
                                String currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
                                String currentDay = DateUtils.toString(new Date(), "dd");
                                Boolean leapYear = false;
                                if (fDate != null) {
                                    condition.put("month", fDate);
                                    month = fDate;
                                    Integer yearQ = Integer.valueOf(fDate.substring(0, 4));
                                    if ( (yearQ % 4 == 0 && yearQ % 100 != 0) || yearQ % 400 == 0) {
                                        leapYear = true;
                                    }
                                    String monthOfYear = fDate.substring(5);
                                    switch (monthOfYear) {
                                        case "01":
                                        case "03":
                                        case "05":
                                        case "07":
                                        case "08":
                                        case "10":
                                        case "12":
                                            currentDay = String.valueOf(31);
                                            break;
                                        case "04":
                                        case "06":
                                        case "09":
                                        case "11":
                                            currentDay = String.valueOf(30);
                                            break;
                                        case "02":
                                            if (leapYear) {
                                                currentDay = String.valueOf(29);
                                                break;
                                            } else {
                                                currentDay = String.valueOf(28);
                                                break;
                                            }
                                    }
                                    try {
                                        date = formatterMonth.parse(fDate);
                                        currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
                                        currMonth = DateUtils.toString(date, "MM");
                                        currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                List<DataPower> listPowerResult = new ArrayList<>();
                                if (type == 1) {
                          

                                    listPowerT = deviceService.getListTLoadByMonth(condition);   
                                    for (int i = 1; i <= Integer.valueOf(currentDay); i++) {
                                        DataPower dataP = new DataPower();
                                        String dayIn = "";
                                        if (i < 10) {
                                            dayIn = "0" + i;
                                        } else {
                                            dayIn = String.valueOf(i);
                                        }
                                        String dayOfMonth = month + "-" + dayIn;
                                        dataP.setViewTime(dayOfMonth);
                                        dataP.setPower(0f);
                                        listPowerResult.add(dataP);
                                    }
                                    
                                    for (DataPower dataR : listPowerResult) {

                                        for (DataPower data : listPowerT) {
                                            if (dataR.getViewTime()
                                                .equals(data.getViewTime())) {
                                                dataR.setPower(data.getPower());
                                                dataR.setPowerAccumulated(data.getPowerAccumulated());                                            }
                                        }

                                    }
//                					obj.setListDataPower(listPowerT);
                					dataPower.setListDataPower(listPowerResult);
//                            
                                }

                            }
                            
                            if (time == 2) {

                                Calendar calendar = Calendar.getInstance();
                                Date date = calendar.getTime();
                                String currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
                                String currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
                                String currentDay = DateUtils.toString(new Date(), "dd");
                                Boolean leapYear = false;
                                
                                if (fDate != null) {
                                  condition.put("year", fDate);
      
                                  year = fDate;
                                }
                                
                                List<DataPower> listPowerResult = new ArrayList<>();
                                if (type == 1) {
                          
                                    listPowerT = deviceService.getListTLoadByYear(condition);   
                                    for (int i = 1; i <= 12; i++) {
                                        DataPower dataP = new DataPower();
                                        String monthIn = "";
                                        if (i < 10) {
                                            monthIn = "0" + i;
                                        } else {
                                            monthIn = String.valueOf(i);
                                        }
                                        String monthOfYear = year + "-" + monthIn;
                                        dataP.setViewTime(monthOfYear);
                                        dataP.setPower(0f);
                                        listPowerResult.add(dataP);
                                    }

                                    for (DataPower dataR : listPowerResult) {

                                        for (DataPower data : listPowerT) {
                                            if (dataR.getViewTime()
                                                .equals(data.getViewTime())) {
                                                dataR.setPower(data.getPower());
                                                dataR.setPowerAccumulated(data.getPowerAccumulated());
                                            }
                                        }

                                    }

                					dataPower.setListDataPower(listPowerResult);
//                            
                                }
                            
                        }
                            result2.add(dataPower); 
                        }
                        obj.setDataPowerClass2(result2);
                        result.add(obj);                      
            	}


     
            }  else {
                condition.put("projectId", String.valueOf(projectId));
                condition.put("schema", Schema.getSchemas(customerId));
                condition.put("day", day);
                condition.put("month", month);
                condition.put("systemType", String.valueOf(type));
                condition.put("year", year);
                condition.put("calculateFlag", String.valueOf(1));
                condition.put("deviceType", String.valueOf(10));
                condition.put("deleteFlag", String.valueOf(0));
                    condition.put("customerId", String.valueOf(customerId));
                    condition.put("fuelFormId", String.valueOf(fuelFormId));
                    if (ids == "") {
                        proIds = null;
                    } else {
                        proIds = ids;
                    }
                    List<Device> listDevice = deviceService.getDeviceByProjectId(condition);
                    for (Device device : listDevice) {
                    	condition.put("deviceId", String.valueOf(device.getDeviceId()));
                        DataPowerResult obj = new DataPowerResult();
                        obj.setName(device.getDeviceName());
                        List<DataPower> listPower = new ArrayList<>();
                        List<DataPower> listPowerT = new ArrayList<>();

                        if (time == 0) {
                            Calendar calendar = Calendar.getInstance();
                            Date date = calendar.getTime();
                            String currDayOfWeek = new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime());
                            String currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
                            String currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
                            String dayOrFdate = new SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH).format(date.getTime());
                            List<DataPower> listPowerResult = new ArrayList<>();
                            if (fDate != null) {
                                condition.put("day", fDate);
                                try {
                                    date = formatter.parse(fDate);
                                    currDayOfWeek = new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime());
                                    currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
                                    currentYear = fDate.substring(0, 4);
                                    dayOrFdate = new SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH).format(date.getTime());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (type == 1) {
                                listPowerT = deviceService.getListTLoadByDay(condition);
                                LocalTime currentTime = LocalTime.now();
                                int currentHour = currentTime.getHour();

                                // Vòng lặp để tạo ra các khoảng thời gian đến thời điểm hiện tại
                                for (int i = 0; i <= currentHour; i++) {
                                    DataPower data = new DataPower();
                                    // Tạo chuỗi thời gian
                                    String hourTime = i + ":00:00";
                                    if (i < 10) {
                                        hourTime = "0" + hourTime;
                                    }
                                    data.setViewTime(day + " " + hourTime);
                                    data.setPower(0f);
                                    listPowerResult.add(data);
                                }


                                for (DataPower dataR : listPowerResult) {

                                    for (DataPower data : listPowerT) {
                                        if (dataR.getViewTime()
                                            .equals(data.getViewTime())) {
                                            dataR.setPower(data.getPower());
                                            dataR.setPowerAccumulated(data.getPowerAccumulated());
                                        }
                                    }

                                }
                                obj.setListDataPower(listPowerResult);
                            }

                        }
                        
                        if (time == 1) {

                            Calendar calendar = Calendar.getInstance();
                            Date date = calendar.getTime();
                            String currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
                            String currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
                            String currentDay = DateUtils.toString(new Date(), "dd");
                            Boolean leapYear = false;
                            if (fDate != null) {
                                condition.put("month", fDate);
                                month = fDate;
                                Integer yearQ = Integer.valueOf(fDate.substring(0, 4));
                                if ( (yearQ % 4 == 0 && yearQ % 100 != 0) || yearQ % 400 == 0) {
                                    leapYear = true;
                                }
                                String monthOfYear = fDate.substring(5);
                                switch (monthOfYear) {
                                    case "01":
                                    case "03":
                                    case "05":
                                    case "07":
                                    case "08":
                                    case "10":
                                    case "12":
                                        currentDay = String.valueOf(31);
                                        break;
                                    case "04":
                                    case "06":
                                    case "09":
                                    case "11":
                                        currentDay = String.valueOf(30);
                                        break;
                                    case "02":
                                        if (leapYear) {
                                            currentDay = String.valueOf(29);
                                            break;
                                        } else {
                                            currentDay = String.valueOf(28);
                                            break;
                                        }
                                }
                                try {
                                    date = formatterMonth.parse(fDate);
                                    currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
                                    currMonth = DateUtils.toString(date, "MM");
                                    currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } 
                            List<DataPower> listPowerResult = new ArrayList<>();
                            if (type == 1) {
                      

                                listPowerT = deviceService.getListTLoadByMonth(condition);
                                for (int i = 1; i <= Integer.valueOf(currentDay); i++) {
                                    DataPower dataP = new DataPower();
                                    String dayIn = "";
                                    if (i < 10) {
                                        dayIn = "0" + i;
                                    } else {
                                        dayIn = String.valueOf(i);
                                    }
                                    String dayOfMonth = month + "-" + dayIn;
                                    dataP.setViewTime(dayOfMonth);
                                    dataP.setPower(0f);
                                    listPowerResult.add(dataP);
                                }
                                
                                for (DataPower dataR : listPowerResult) {

                                    for (DataPower data : listPowerT) {
                                        if (dataR.getViewTime()
                                            .equals(data.getViewTime())) {
                                            dataR.setPower(data.getPower());
                                            dataR.setPowerAccumulated(data.getPowerAccumulated());                                            }
                                    }

                                }
                               
                               
            					obj.setListDataPower(listPowerResult);
//                        
                            }

                        }
                        
                        if (time == 2) {

                            Calendar calendar = Calendar.getInstance();
                            Date date = calendar.getTime();
                            String currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
                            String currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
                            String currentDay = DateUtils.toString(new Date(), "dd");
                            Boolean leapYear = false;
                            
                            if (fDate != null) {
                              condition.put("year", fDate);

                              year = fDate;
                            }
                            
                            List<DataPower> listPowerResult = new ArrayList<>();
                            if (type == 1) {
                      
                                listPowerT = deviceService.getListTLoadByYear(condition);   
                                for (int i = 1; i <= 12; i++) {
                                    DataPower dataP = new DataPower();
                                    String monthIn = "";
                                    if (i < 10) {
                                        monthIn = "0" + i;
                                    } else {
                                        monthIn = String.valueOf(i);
                                    }
                                    String monthOfYear = year + "-" + monthIn;
                                    dataP.setViewTime(monthOfYear);
                                    dataP.setPower(0f);
                                    listPowerResult.add(dataP);
                                }

                                for (DataPower dataR : listPowerResult) {

                                    for (DataPower data : listPowerT) {
                                        if (dataR.getViewTime()
                                            .equals(data.getViewTime())) {
                                            dataR.setPower(data.getPower());
                                            dataR.setPowerAccumulated(data.getPowerAccumulated());
                                        }
                                    }

                                }

                                obj.setListDataPower(listPowerResult);
//                                    
                            }
                        
                    }

                        result.add(obj);
                    }
               }
            
            

            return new ResponseEntity<Object>(result, HttpStatus.OK);

        }

    @GetMapping ("/getDataTab2")
    public ResponseEntity<Object> getDataTab2(@RequestParam ("customerId") final Integer customerId,
        @RequestParam (value = "projectId", required = false) final Integer projectId,
        @RequestParam (value = "time", required = false) final Integer time,
        @RequestParam (value = "type", required = false) final Integer type,
        @RequestParam (value = "fDate", required = false) final String fDate,
        @RequestParam (value = "tDate", required = false) final String tDate,
        @RequestParam (value = "ids", required = false) final String ids) {
        List<Object> result = new ArrayList<>();

        String day = DateUtils.toString(new Date(), Constants.ES.DATE_FORMAT_YMD);
        String month = DateUtils.toString(new Date(), Constants.ES.DATE_FORMAT_YM_02);
        String currMonth = DateUtils.toString(new Date(), "MM");
        String year = DateUtils.toString(new Date(), "yyyy");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatterMonth = new SimpleDateFormat("yyyy-MM");

        HashMap<String, String> condition = new HashMap<>();
        String proIds = "";

        if (projectId == null) {

            condition.put("customerId", String.valueOf(customerId));
            if (ids == "") {
                proIds = null;
            } else {
                proIds = ids;
            }
            List<Project> listProject = projectService.getListProjectByCustomerId(String.valueOf(customerId), proIds);

            for (Project project : listProject) {

                DataPowerResult obj = new DataPowerResult();
                obj.setName(project.getProjectName());
                List<DataPower> listPower = new ArrayList<>();
     
                // List<LandmarksPlansEnergy> listPower = new ArrayList<>();
                List<LandmarksPlansEnergy> listPowerLandmark = new ArrayList<>();
                List<LandmarksPlansEnergy> listEnergyPower = new ArrayList<>();
                condition.put("projectId", String.valueOf(project.getProjectId()));
                condition.put("schema", Schema.getSchemas(customerId));
                condition.put("day", day);
                condition.put("month", month);
                condition.put("year", year);
                condition.put("energy", "energy");

                if (time == 0) {
                    Calendar calendar = Calendar.getInstance();
                    Date date = calendar.getTime();
                    String currDayOfWeek = new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime());
                    String currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
                    String currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
                    String dayOrFdate = new SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH).format(date.getTime());

                    if (fDate != null) {
                        condition.put("day", fDate);
                        try {
                            date = formatter.parse(fDate);
                            currDayOfWeek = new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime());
                            currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
                            currentYear = fDate.substring(0, 4);
                            dayOrFdate = new SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH).format(date.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    List<DataPower> listTimePower = new ArrayList<>();
                    for (int hour = 0; hour < 24; hour++) {
                        LocalTime viewTime = LocalTime.of(hour, 0, 0);
                        DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm:ss");
                        DataPower data = new DataPower(dayOrFdate + " " + (viewTime).format(formatTime));
                        listTimePower.add(data);
                    }

                    if (type == 1) {
                        listPower = deviceService.getListEpLoadByDay(condition);
                
                        // get plan energy
                        /*-- Get data user input  */
                        HashMap<String, String> condi = new HashMap<>();
                        condi.put("schema", Schema.getSchemas(customerId));
                        condi.put("currDayOfWeek", currDayOfWeek);
                        condi.put("currentMonth", currentMonth);
                        condi.put("projectId", String.valueOf(project.getProjectId()));
                        condi.put("systemTypeId", String.valueOf(type));
                        condi.put("currentYear", currentYear);
                        Double planEnergyInput = landmarksPlanEnergyService.getEnergyByDayAndMonth(condi);
                        Double targetEnergyInput = landmarksPlanEnergyService.getLandmarksEnergyByDayAndMonth(condi);

                        if (planEnergyInput == null) {
                            planEnergyInput = 0.0;
                        }

                        if (targetEnergyInput == null) {
                            targetEnergyInput = 0.0;
                        }

                        if ( (planEnergyInput != null && planEnergyInput != 0)
                            || (targetEnergyInput != null && targetEnergyInput != 0)) {
                            if (targetEnergyInput != null && targetEnergyInput != 0) {
                                Double accumulationPlan = 0.0;
                                Double accumulationTarget = 0.0;

                                for (DataPower data : listTimePower) {
                                    if (listPower.size() > 0) {
                                        for (DataPower dataPower : listPower) {
                                            if (dataPower.getViewTime()
                                                .equals(data.getViewTime())) {
                                                data.setPower(dataPower.getPower());
                                            }
                                        }
                                    }
                                    data.setTargetEnergy((double) Math.round(accumulationTarget));
                                    if (targetEnergyInput != null && targetEnergyInput != 0) {
                                        data.setPlanEnergy((double) Math.round(accumulationPlan));
                                    }
                                    if (planEnergyInput == null || planEnergyInput == 0) {
                                        data.setPlanEnergy(accumulationTarget != null
                                            ? (double) Math.round(accumulationTarget * 0.95)
                                            : 0);
                                    }
                                    accumulationPlan = accumulationPlan + (planEnergyInput / 23);
                                    accumulationTarget = accumulationTarget + (targetEnergyInput / 23);
                                }
                            } else {
                                Double accumulationPlan = 0.0;
                                Double accumulationTarget = 0.0;

                                for (DataPower data : listTimePower) {
                                    for (DataPower dataPower : listPower) {
                                        if (dataPower.getViewTime()
                                            .equals(data.getViewTime())) {
                                            data.setPower(dataPower.getPower());
                                        }
                                    }
                                    data.setPlanEnergy((double) Math.round(accumulationPlan / 0.95));
                                    data.setTargetEnergy((double) Math.round(accumulationPlan));

                                    accumulationPlan = accumulationPlan + (planEnergyInput / 23);
                                    accumulationTarget = accumulationTarget + (targetEnergyInput / 23);
                                }

                            }
                        } else {
                            listTimePower = listPower;
                        }

                        obj.setListDataPower(listTimePower);
                    }

                }

                if (time == 1) {

                    Calendar calendar = Calendar.getInstance();
                    Date date = calendar.getTime();
                    String currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
                    String currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
                    if (fDate != null) {
                        condition.put("month", fDate);
                        try {
                            date = formatterMonth.parse(fDate);
                            currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
                            currMonth = DateUtils.toString(date, "MM");
                            currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    if (type == 1) {
                        HashMap<String, String> condi = new HashMap<>();
                        condi.put("schema", Schema.getSchemas(customerId));
                        condi.put("currentMonth", currentMonth);
                        condi.put("projectId", String.valueOf(project.getProjectId()));
                        condi.put("systemTypeId", String.valueOf(type));
                        condi.put("currentYear", currentYear);

                        List<LandmarksPlansEnergy> listEnergy = landmarksPlanEnergyService.getEnergyMonth(condi);
                        List<
                            LandmarksPlansEnergy> listEnergyPlan = landmarksPlanEnergyService.getEnergyMonthPlan(condi);
                        listPowerLandmark = deviceService.getListEpLoadByMonthLandmark(condition);
                        float accumulatedElectricity = 0.0f;
                        for (LandmarksPlansEnergy x : listPowerLandmark) {
                            if (x.getPower() != null && x.getPower() != 0) accumulatedElectricity += x.getPower();
                            x.setPower(accumulatedElectricity);
                        }
                        listEnergyPower = landmarksPlanEnergyService.getEnergyMonth(condi);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.set(Calendar.YEAR, Integer.parseInt(currentYear));
                        calendar1.set(Calendar.MONTH, Integer.parseInt(currMonth) - 1);
                        if (listEnergy.size() > 0) {
                            // 31 trường
                            for (int i = 0; i < listEnergyPower.size(); i++) {

                                LandmarksPlansEnergy data = listEnergyPower.get(i);
                                calendar1.set(Calendar.DAY_OF_MONTH, i + 1);
                                data.setDateOfMonth(dateFormat.format(calendar1.getTime()));

                                // 8 trường
                                for (int j = 0; j < listPowerLandmark.size(); j++) {
                                    LandmarksPlansEnergy landmarkPowerData = listPowerLandmark.get(j);
                                    if (data.getDateOfMonth()
                                        .equals(landmarkPowerData.getViewTime())) {
                                        data.setPower(landmarkPowerData.getPower());
                                        break;
                                    }
                                }

                                if (i < listEnergy.size()) {
                                    LandmarksPlansEnergy energy = listEnergy.get(i);
                                    Integer valueEnergy = energy.getValueEnergy();
                                    data.setTargetEnergy(Double.parseDouble(valueEnergy.toString()));
                                    Double planEnergy = 0.0;
                                    if (i < listEnergyPlan.size()) {
                                        LandmarksPlansEnergy energyPlan = listEnergyPlan.get(i);
                                        Integer valueEnergyPlan = energyPlan.getValueEnergy();
                                        if (valueEnergyPlan != null) {
                                            planEnergy = Double.parseDouble(valueEnergyPlan.toString());
                                        } else if (valueEnergy != null) {
                                            planEnergy = valueEnergy * 0.95;
                                        }
                                    } else if (valueEnergy != null) {
                                        planEnergy = valueEnergy * 0.95;
                                    }

                                    data.setPlanEnergy(planEnergy);
                                }
                            }
                        }
                        List<DataPower> listPowerPush = new ArrayList<>();
                        for (LandmarksPlansEnergy x : listEnergyPower) {
                            DataPower data = new DataPower();
                            data.setDateOfWeek(x.getDateOfWeek());
                            data.setPower(x.getPower());
                            data.setPlanEnergy(x.getPlanEnergy());
                            data.setTargetEnergy(x.getTargetEnergy());
                            data.setValueEnergy(x.getValueEnergy());
                            data.setViewTime(x.getDateOfMonth());
                            listPowerPush.add(data);
                        }
                        obj.setListDataPower(listPowerPush);
                        // obj.setListDataPowerTab2(listEnergyPower);
                    }

                }

                if (time == 2) {

                    Calendar calendar = Calendar.getInstance();
                    Date date = calendar.getTime();

                    if (fDate != null) {
                        condition.put("year", fDate);

                        year = fDate;
                    }

                    if (type == 1) {
                        HashMap<String, String> condi = new HashMap<>();
                        String currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
                        String currDayOfMonth = new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime());
                        String currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
                        condi.put("schema", Schema.getSchemas(customerId));
                        condi.put("projectId", String.valueOf(project.getProjectId()));
                        condi.put("systemTypeId", String.valueOf(type));
                        condi.put("currentMonth", currentMonth);
                        condi.put("currDayOfMonth", currDayOfMonth);
                        if (fDate != null) {
                            condi.put("year", fDate);
                        } else {
                            condi.put("year", currentYear);
                        }
                        List<LandmarksPlansEnergy> listEnergy = new ArrayList<>();
                        listEnergy = landmarksPlanEnergyService.getEnergyYear(condi);
                        List<LandmarksPlansEnergy> listEnergyPlan = new ArrayList<>();
                        listEnergyPlan = landmarksPlanEnergyService.getEnergyYearPlan(condi);
                        listPowerLandmark = deviceService.getListEpLoadByYear(condition);
                        float accumulatedElectricity = 0.0f;
                        for (LandmarksPlansEnergy x : listPowerLandmark) {
                            if (x.getPower() != null && x.getPower() != 0) accumulatedElectricity += x.getPower();
                            x.setPower(accumulatedElectricity);
                        }
                        List<LandmarksPlansEnergy> listPowerLandmarkX = new ArrayList<>();

                        if (listPowerLandmark.size() > -1) {
                            for (int i = 1; i <= 12; i++) {
                                LandmarksPlansEnergy x = new LandmarksPlansEnergy();
                                // if(i <= listPowerLandmark.size()){
                                // x.setPower(listPowerLandmark.get(0).getPower());
                                // }else{
                                // x.setPower(null);
                                // }
                                // if(switchViewTime(i, year) == )
                                x.setViewTime(switchViewTime(i, year));
                                for (LandmarksPlansEnergy y : listPowerLandmark) {
                                    if (y.getViewTime()
                                        .contains(switchViewYear(i, year))) {
                                        x.setPower(y.getPower());
                                    }
                                }
                                // if (listEnergyPlan.get(0) != null) {
                                switch (i) {
                                    case 1:
                                        if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                            if (listEnergyPlan.get(0)
                                                .getSumT1Plan() == 0) {
                                                x.setSumEnergy(listEnergy.get(0)
                                                    .getSumT1() * 0.95);
                                            } else {
                                                x.setSumEnergy(listEnergyPlan.get(0)
                                                    .getSumT1Plan());
                                            }
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT1());
                                        } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                            x.setSumEnergy(listEnergyPlan.get(0)
                                                .getSumT1Plan());
                                            x.setSumLandmark(listEnergyPlan.get(0)
                                                .getSumT1Plan() / 0.95);
                                        } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                            x.setSumEnergy(listEnergy.get(0)
                                                .getSumT1() * 0.95);
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT1());
                                        }

                                        break;
                                    case 2:
                                        if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                            if (listEnergyPlan.get(0)
                                                .getSumT2Plan()
                                                .equals(listEnergyPlan.get(0)
                                                    .getSumT1Plan())) {
                                                x.setSumEnergy(listEnergy.get(0)
                                                    .getSumT2() * 0.95);
                                            } else {
                                                x.setSumEnergy(listEnergyPlan.get(0)
                                                    .getSumT2Plan());
                                            }
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT2());
                                        } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                            x.setSumEnergy(listEnergyPlan.get(0)
                                                .getSumT2Plan());
                                            x.setSumLandmark(listEnergyPlan.get(0)
                                                .getSumT2Plan() / 0.95);
                                        } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                            x.setSumEnergy(listEnergy.get(0)
                                                .getSumT2() * 0.95);
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT2());
                                        }
                                        break;

                                    case 3:
                                        if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                            if (listEnergyPlan.get(0)
                                                .getSumT3Plan()
                                                .equals(listEnergyPlan.get(0)
                                                    .getSumT2Plan())) {
                                                x.setSumEnergy(listEnergy.get(0)
                                                    .getSumT3() * 0.95);
                                            } else {
                                                x.setSumEnergy(listEnergyPlan.get(0)
                                                    .getSumT3Plan());
                                            }
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT3());
                                        } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                            x.setSumEnergy(listEnergyPlan.get(0)
                                                .getSumT3Plan());
                                            x.setSumLandmark(listEnergyPlan.get(0)
                                                .getSumT3Plan() / 0.95);
                                        } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                            x.setSumEnergy(listEnergy.get(0)
                                                .getSumT3() * 0.95);
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT3());
                                        }
                                        break;

                                    case 4:
                                        if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                            if (listEnergyPlan.get(0)
                                                .getSumT4Plan()
                                                .equals(listEnergyPlan.get(0)
                                                    .getSumT3Plan())) {
                                                x.setSumEnergy(listEnergy.get(0)
                                                    .getSumT4() * 0.95);
                                            } else {
                                                x.setSumEnergy(listEnergyPlan.get(0)
                                                    .getSumT4Plan());
                                            }
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT4());
                                        } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                            x.setSumEnergy(listEnergyPlan.get(0)
                                                .getSumT4Plan());
                                            x.setSumLandmark(listEnergyPlan.get(0)
                                                .getSumT4Plan() / 0.95);
                                        } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                            x.setSumEnergy(listEnergy.get(0)
                                                .getSumT4() * 0.95);
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT4());
                                        }
                                        break;

                                    case 5:
                                        if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                            if (listEnergyPlan.get(0)
                                                .getSumT5Plan()
                                                .equals(listEnergyPlan.get(0)
                                                    .getSumT4Plan())) {
                                                x.setSumEnergy(listEnergy.get(0)
                                                    .getSumT5() * 0.95);
                                            } else {
                                                x.setSumEnergy(listEnergyPlan.get(0)
                                                    .getSumT5Plan());
                                            }
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT5());
                                        } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                            x.setSumEnergy(listEnergyPlan.get(0)
                                                .getSumT5Plan());
                                            x.setSumLandmark(listEnergyPlan.get(0)
                                                .getSumT5Plan() / 0.95);
                                        } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                            x.setSumEnergy(listEnergy.get(0)
                                                .getSumT5() * 0.95);
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT5());
                                        }
                                        break;
                                    // // Các case từ 6 đến 11
                                    case 6:
                                        if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                            if (listEnergyPlan.get(0)
                                                .getSumT6Plan()
                                                .equals(listEnergyPlan.get(0)
                                                    .getSumT5Plan())) {
                                                x.setSumEnergy(listEnergy.get(0)
                                                    .getSumT6() * 0.95);
                                            } else {
                                                x.setSumEnergy(listEnergyPlan.get(0)
                                                    .getSumT6Plan());
                                            }
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT6());
                                        } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                            x.setSumEnergy(listEnergyPlan.get(0)
                                                .getSumT6Plan());
                                            x.setSumLandmark(listEnergyPlan.get(0)
                                                .getSumT6Plan() / 0.95);
                                        } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                            x.setSumEnergy(listEnergy.get(0)
                                                .getSumT6() * 0.95);
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT6());
                                        }
                                        break;

                                    case 7:
                                        if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                            if (listEnergyPlan.get(0)
                                                .getSumT7Plan()
                                                .equals(listEnergyPlan.get(0)
                                                    .getSumT6Plan())) {
                                                x.setSumEnergy(listEnergy.get(0)
                                                    .getSumT7() * 0.95);
                                            } else {
                                                x.setSumEnergy(listEnergyPlan.get(0)
                                                    .getSumT7Plan());
                                            }
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT7());
                                        } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                            x.setSumEnergy(listEnergyPlan.get(0)
                                                .getSumT7Plan());
                                            x.setSumLandmark(listEnergyPlan.get(0)
                                                .getSumT7Plan() / 0.95);
                                        } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                            x.setSumEnergy(listEnergy.get(0)
                                                .getSumT7() * 0.95);
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT7());
                                        }
                                        break;

                                    case 8:
                                        if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                            if (listEnergyPlan.get(0)
                                                .getSumT8Plan()
                                                .equals(listEnergyPlan.get(0)
                                                    .getSumT7Plan())) {
                                                x.setSumEnergy(listEnergy.get(0)
                                                    .getSumT8() * 0.95);
                                            } else {
                                                x.setSumEnergy(listEnergyPlan.get(0)
                                                    .getSumT8Plan());
                                            }
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT8());
                                        } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                            x.setSumEnergy(listEnergyPlan.get(0)
                                                .getSumT8Plan());
                                            x.setSumLandmark(listEnergyPlan.get(0)
                                                .getSumT8Plan() / 0.95);
                                        } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                            x.setSumEnergy(listEnergy.get(0)
                                                .getSumT8() * 0.95);
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT8());
                                        }
                                        break;

                                    case 9:
                                        if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                            if (listEnergyPlan.get(0)
                                                .getSumT9Plan()
                                                .equals(listEnergyPlan.get(0)
                                                    .getSumT8Plan())) {
                                                x.setSumEnergy(listEnergy.get(0)
                                                    .getSumT9() * 0.95);
                                            } else {
                                                x.setSumEnergy(listEnergyPlan.get(0)
                                                    .getSumT9Plan());
                                            }
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT9());
                                        } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                            x.setSumEnergy(listEnergyPlan.get(0)
                                                .getSumT9Plan());
                                            x.setSumLandmark(listEnergyPlan.get(0)
                                                .getSumT9Plan() / 0.95);
                                        } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                            x.setSumEnergy(listEnergy.get(0)
                                                .getSumT9() * 0.95);
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT9());
                                        }
                                        break;

                                    case 10:
                                        if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                            if (listEnergyPlan.get(0)
                                                .getSumT10Plan()
                                                .equals(listEnergyPlan.get(0)
                                                    .getSumT9Plan())) {
                                                x.setSumEnergy(listEnergy.get(0)
                                                    .getSumT10() * 0.95);
                                            } else {
                                                x.setSumEnergy(listEnergyPlan.get(0)
                                                    .getSumT10Plan());
                                            }
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT10());
                                        } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                            x.setSumEnergy(listEnergyPlan.get(0)
                                                .getSumT10Plan());
                                            x.setSumLandmark(listEnergyPlan.get(0)
                                                .getSumT10Plan() / 0.95);
                                        } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                            x.setSumEnergy(listEnergy.get(0)
                                                .getSumT10() * 0.95);
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT10());
                                        }
                                        break;

                                    case 11:
                                        if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                            if (listEnergyPlan.get(0)
                                                .getSumT11Plan()
                                                .equals(listEnergyPlan.get(0)
                                                    .getSumT10Plan())) {
                                                x.setSumEnergy(listEnergy.get(0)
                                                    .getSumT11() * 0.95);
                                            } else {
                                                x.setSumEnergy(listEnergyPlan.get(0)
                                                    .getSumT11Plan());
                                            }
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT11());
                                        } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                            x.setSumEnergy(listEnergyPlan.get(0)
                                                .getSumT11Plan());
                                            x.setSumLandmark(listEnergyPlan.get(0)
                                                .getSumT11Plan() / 0.95);
                                        } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                            x.setSumEnergy(listEnergy.get(0)
                                                .getSumT11() * 0.95);
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT11());
                                        }
                                        break;

                                    case 12:
                                        if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                            if (listEnergyPlan.get(0)
                                                .getSumT12Plan()
                                                .equals(listEnergyPlan.get(0)
                                                    .getSumT11Plan())) {
                                                x.setSumEnergy(listEnergy.get(0)
                                                    .getSumT12() * 0.95);
                                            } else {
                                                x.setSumEnergy(listEnergyPlan.get(0)
                                                    .getSumT12Plan());
                                            }
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT12());
                                        } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                            x.setSumEnergy(listEnergyPlan.get(0)
                                                .getSumT12Plan());
                                            x.setSumLandmark(listEnergyPlan.get(0)
                                                .getSumT12Plan() / 0.95);
                                        } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                            x.setSumEnergy(listEnergy.get(0)
                                                .getSumT12() * 0.95);
                                            x.setSumLandmark(listEnergy.get(0)
                                                .getSumT12());
                                        }
                                        break;
                                }
                                // }

                                listPowerLandmarkX.add(x);
                            }
                        }
                        List<DataPower> listPowerPush = new ArrayList<>();
                        for (LandmarksPlansEnergy x : listPowerLandmarkX) {
                            DataPower data = new DataPower();
                            data.setDateOfWeek(x.getDateOfWeek());
                            data.setPower(x.getPower());
                            data.setPlanEnergy(x.getPlanEnergy());
                            data.setTargetEnergy(x.getTargetEnergy());
                            data.setValueEnergy(x.getValueEnergy());
                            data.setViewTime(x.getViewTime());
                            listPowerPush.add(data);
                        }
                        obj.setListDataPower(listPowerPush);
                        // obj.setListDataPowerTab2(listPowerLandmarkX);
                    }
                }

                if (time == 3) {
                    if (type == 1) {
                        listPower = deviceService.getListEpLoadAll(condition);
                        obj.setListDataPower(listPower);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListEpSolarAll(condition);
                        obj.setListDataPower(listPower);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerBatteruByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerWindByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListEpGridAll(condition);
                        obj.setListDataPower(listPower);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                }

                result.add(obj);
            }

            // Project != null
        } else {
            condition.put("projectId", String.valueOf(projectId));
            condition.put("schema", Schema.getSchemas(customerId));
            condition.put("day", day);
            condition.put("month", month);
            condition.put("year", year);
            condition.put("calculateFlag", String.valueOf(1));
            condition.put("deleteFlag", String.valueOf(0));
            condition.put("energy", "energy");

            Project project = projectService.getProject(condition);
            DataPowerResult obj = new DataPowerResult();
            obj.setName(project.getProjectName());

            condition.put("customerId", String.valueOf(customerId));
            if (ids == "") {
                proIds = null;
            } else {
                proIds = ids;
            }

            List<DataPower> listPower = new ArrayList<>();
            // List<LandmarksPlansEnergy> listPower = new ArrayList<>();z
            List<LandmarksPlansEnergy> listPowerLandmark = new ArrayList<>();
            List<LandmarksPlansEnergy> listEnergyPower = new ArrayList<>();
            condition.put("projectId", String.valueOf(project.getProjectId()));
            condition.put("schema", Schema.getSchemas(customerId));
            condition.put("day", day);
            condition.put("month", month);
            condition.put("year", year);
            condition.put("energy", "energy");

            if (time == 0) {
                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
                String currDayOfWeek = new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime());
                String currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
                String currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
                String dayOrFdate = new SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH).format(date.getTime());

                if (fDate != null) {
                    condition.put("day", fDate);
                    try {
                        date = formatter.parse(fDate);
                        currDayOfWeek = new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime());
                        currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
                        currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
                        dayOrFdate = new SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH).format(date.getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                List<DataPower> listTimePower = new ArrayList<>();
                for (int hour = 0; hour < 24; hour++) {
                    LocalTime viewTime = LocalTime.of(hour, 0, 0);
                    DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm:ss");
                    DataPower data = new DataPower(dayOrFdate + " " + (viewTime).format(formatTime));
                    listTimePower.add(data);
                }

                if (type == 1) {
                    listPower = deviceService.getListEpLoadByDay(condition);
                    HashMap<String, String> condi = new HashMap<>();
                    condi.put("schema", Schema.getSchemas(customerId));
                    condi.put("currDayOfWeek", currDayOfWeek);
                    condi.put("currentMonth", currentMonth);
                    condi.put("projectId", String.valueOf(project.getProjectId()));
                    condi.put("systemTypeId", String.valueOf(type));
                    condi.put("currentYear", currentYear);
                    Double planEnergyInput = landmarksPlanEnergyService.getEnergyByDayAndMonth(condi);
                    Double targetEnergyInput = landmarksPlanEnergyService.getLandmarksEnergyByDayAndMonth(condi);

                    if (planEnergyInput == null) {
                        planEnergyInput = 0.0;
                    }

                    if (targetEnergyInput == null) {
                        targetEnergyInput = 0.0;
                    }

                    if ( (planEnergyInput != null && planEnergyInput != 0)
                        || (targetEnergyInput != null && targetEnergyInput != 0)) {
                        if (targetEnergyInput != null && targetEnergyInput != 0) {
                            Double accumulationPlan = 0.0;
                            Double accumulationTarget = 0.0;

                            for (DataPower data : listTimePower) {
                                if (listPower.size() > 0) {
                                    for (DataPower dataPower : listPower) {
                                        if (dataPower.getViewTime()
                                            .equals(data.getViewTime())) {
                                            data.setPower(dataPower.getPower());
                                        }
                                    }
                                }
                                data.setTargetEnergy((double) Math.round(accumulationTarget));
                                if (targetEnergyInput != null && targetEnergyInput != 0) {
                                    data.setPlanEnergy((double) Math.round(accumulationPlan));
                                }
                                if (planEnergyInput == null || planEnergyInput == 0) {
                                    data.setPlanEnergy(accumulationTarget != null
                                        ? (double) Math.round(accumulationTarget * 0.95)
                                        : 0);
                                }
                                accumulationPlan = accumulationPlan + (planEnergyInput / 23);
                                accumulationTarget = accumulationTarget + (targetEnergyInput / 23);
                            }
                        } else {
                            Double accumulationPlan = 0.0;
                            Double accumulationTarget = 0.0;

                            for (DataPower data : listTimePower) {
                                for (DataPower dataPower : listPower) {
                                    if (dataPower.getViewTime()
                                        .equals(data.getViewTime())) {
                                        data.setPower(dataPower.getPower());
                                    }
                                }
                                data.setPlanEnergy((double) Math.round(accumulationPlan / 0.95));
                                data.setTargetEnergy((double) Math.round(accumulationPlan));

                                accumulationPlan = accumulationPlan + (planEnergyInput / 23);
                                accumulationTarget = accumulationTarget + (targetEnergyInput / 23);
                            }

                        }
                    } else {
                        listTimePower = listPower;
                    }

                    obj.setListDataPower(listTimePower);
                }

            }

            if (time == 1) {

                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
                String currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
                String currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
                if (fDate != null) {
                    condition.put("month", fDate);
                    try {
                        date = formatterMonth.parse(fDate);
                        currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
                        currMonth = DateUtils.toString(date, "MM");
                        currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (type == 1) {
                    HashMap<String, String> condi = new HashMap<>();
                    condi.put("schema", Schema.getSchemas(customerId));
                    condi.put("currentMonth", currentMonth);
                    condi.put("projectId", String.valueOf(project.getProjectId()));
                    condi.put("systemTypeId", String.valueOf(type));
                    condi.put("currentYear", currentYear);

                    List<LandmarksPlansEnergy> listEnergy = landmarksPlanEnergyService.getEnergyMonth(condi);
                    List<LandmarksPlansEnergy> listEnergyPlan = landmarksPlanEnergyService.getEnergyMonthPlan(condi);
                    listPowerLandmark = deviceService.getListEpLoadByMonthLandmark(condition);
                    float accumulatedElectricity = 0.0f;
                    for (LandmarksPlansEnergy x : listPowerLandmark) {
                        if (x.getPower() != null && x.getPower() != 0) accumulatedElectricity += x.getPower();
                        x.setPower(accumulatedElectricity);
                    }
                    listEnergyPower = landmarksPlanEnergyService.getEnergyMonth(condi);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.set(Calendar.YEAR, Integer.parseInt(currentYear));
                    calendar1.set(Calendar.MONTH, Integer.parseInt(currMonth) - 1);
                    if (listEnergy.size() > 0) {
                        // 31 trường
                        for (int i = 0; i < listEnergyPower.size(); i++) {

                            LandmarksPlansEnergy data = listEnergyPower.get(i);
                            calendar1.set(Calendar.DAY_OF_MONTH, i + 1);
                            data.setDateOfMonth(dateFormat.format(calendar1.getTime()));

                            // 8 trường
                            for (int j = 0; j < listPowerLandmark.size(); j++) {
                                LandmarksPlansEnergy landmarkPowerData = listPowerLandmark.get(j);
                                if (data.getDateOfMonth()
                                    .equals(landmarkPowerData.getViewTime())) {
                                    data.setPower(landmarkPowerData.getPower());
                                    break;
                                }
                            }

                            if (i < listEnergy.size()) {
                                LandmarksPlansEnergy energy = listEnergy.get(i);
                                Integer valueEnergy = energy.getValueEnergy();
                                data.setTargetEnergy(Double.parseDouble(valueEnergy.toString()));
                                Double planEnergy = 0.0;
                                if (i < listEnergyPlan.size()) {
                                    LandmarksPlansEnergy energyPlan = listEnergyPlan.get(i);
                                    Integer valueEnergyPlan = energyPlan.getValueEnergy();
                                    if (valueEnergyPlan != null) {
                                        planEnergy = Double.parseDouble(valueEnergyPlan.toString());
                                    } else if (valueEnergy != null) {
                                        planEnergy = valueEnergy * 0.95;
                                    }
                                } else if (valueEnergy != null) {
                                    planEnergy = valueEnergy * 0.95;
                                }

                                data.setPlanEnergy(planEnergy);
                            }
                        }
                    }
                    List<DataPower> listPowerPush = new ArrayList<>();
                    for (LandmarksPlansEnergy x : listEnergyPower) {
                        DataPower data = new DataPower();
                        data.setDateOfWeek(x.getDateOfWeek());
                        data.setPower(x.getPower());
                        data.setPlanEnergy(x.getPlanEnergy());
                        data.setTargetEnergy(x.getTargetEnergy());
                        data.setValueEnergy(x.getValueEnergy());
                        data.setViewTime(x.getDateOfMonth());
                        listPowerPush.add(data);
                    }
                    obj.setListDataPower(listPowerPush);
                    // obj.setListDataPowerTab2(listEnergyPower);
                }

            }

            if (time == 2) {

                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();

                if (fDate != null) {
                    condition.put("year", fDate);

                    year = fDate;
                }

                if (type == 1) {
                    HashMap<String, String> condi = new HashMap<>();
                    String currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
                    String currDayOfMonth = new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime());
                    String currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
                    condi.put("schema", Schema.getSchemas(customerId));
                    condi.put("projectId", String.valueOf(project.getProjectId()));
                    condi.put("systemTypeId", String.valueOf(type));
                    condi.put("currentMonth", currentMonth);
                    condi.put("currDayOfMonth", currDayOfMonth);
                    if (fDate != null) {
                        condi.put("year", fDate);
                    } else {
                        condi.put("year", currentYear);
                    }
                    List<LandmarksPlansEnergy> listEnergy = new ArrayList<>();
                    listEnergy = landmarksPlanEnergyService.getEnergyYear(condi);
                    List<LandmarksPlansEnergy> listEnergyPlan = new ArrayList<>();
                    listEnergyPlan = landmarksPlanEnergyService.getEnergyYearPlan(condi);

                    listPowerLandmark = deviceService.getListEpLoadByYear(condition);
                    float accumulatedElectricity = 0.0f;
                    for (LandmarksPlansEnergy x : listPowerLandmark) {
                        if (x.getPower() != null && x.getPower() != 0) accumulatedElectricity += x.getPower();
                        x.setPower(accumulatedElectricity);
                    }
                    List<LandmarksPlansEnergy> listPowerLandmarkX = new ArrayList<>();

                    for (int i = 1; i <= 12; i++) {
                        LandmarksPlansEnergy x = new LandmarksPlansEnergy();
                        x.setViewTime(switchViewTime(i, year));
                        for (LandmarksPlansEnergy y : listPowerLandmark) {
                            if (y.getViewTime()
                                .contains(switchViewYear(i, year))) {
                                x.setPower(y.getPower());
                            }
                        }
                        // if (listEnergyPlan.get(0) != null) {
                        switch (i) {
                            case 1:
                                if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                    if (listEnergyPlan.get(0)
                                        .getSumT1Plan() == 0) {
                                        x.setSumEnergy(listEnergy.get(0)
                                            .getSumT1() * 0.95);
                                    } else {
                                        x.setSumEnergy(listEnergyPlan.get(0)
                                            .getSumT1Plan());
                                    }
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT1());
                                } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                    x.setSumEnergy(listEnergyPlan.get(0)
                                        .getSumT1Plan());
                                    x.setSumLandmark(listEnergyPlan.get(0)
                                        .getSumT1Plan() / 0.95);
                                } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                    x.setSumEnergy(listEnergy.get(0)
                                        .getSumT1() * 0.95);
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT1());
                                }

                                break;
                            case 2:
                                if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                    if (listEnergyPlan.get(0)
                                        .getSumT2Plan()
                                        .equals(listEnergyPlan.get(0)
                                            .getSumT1Plan())) {
                                        x.setSumEnergy(listEnergy.get(0)
                                            .getSumT2() * 0.95);
                                    } else {
                                        x.setSumEnergy(listEnergyPlan.get(0)
                                            .getSumT2Plan());
                                    }
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT2());
                                } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                    x.setSumEnergy(listEnergyPlan.get(0)
                                        .getSumT2Plan());
                                    x.setSumLandmark(listEnergyPlan.get(0)
                                        .getSumT2Plan() / 0.95);
                                } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                    x.setSumEnergy(listEnergy.get(0)
                                        .getSumT2() * 0.95);
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT2());
                                }
                                break;

                            case 3:
                                if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                    if (listEnergyPlan.get(0)
                                        .getSumT3Plan()
                                        .equals(listEnergyPlan.get(0)
                                            .getSumT2Plan())) {
                                        x.setSumEnergy(listEnergy.get(0)
                                            .getSumT3() * 0.95);
                                    } else {
                                        x.setSumEnergy(listEnergyPlan.get(0)
                                            .getSumT3Plan());
                                    }
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT3());
                                } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                    x.setSumEnergy(listEnergyPlan.get(0)
                                        .getSumT3Plan());
                                    x.setSumLandmark(listEnergyPlan.get(0)
                                        .getSumT3Plan() / 0.95);
                                } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                    x.setSumEnergy(listEnergy.get(0)
                                        .getSumT3() * 0.95);
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT3());
                                }
                                break;

                            case 4:
                                if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                    if (listEnergyPlan.get(0)
                                        .getSumT4Plan()
                                        .equals(listEnergyPlan.get(0)
                                            .getSumT3Plan())) {
                                        x.setSumEnergy(listEnergy.get(0)
                                            .getSumT4() * 0.95);
                                    } else {
                                        x.setSumEnergy(listEnergyPlan.get(0)
                                            .getSumT4Plan());
                                    }
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT4());
                                } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                    x.setSumEnergy(listEnergyPlan.get(0)
                                        .getSumT4Plan());
                                    x.setSumLandmark(listEnergyPlan.get(0)
                                        .getSumT4Plan() / 0.95);
                                } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                    x.setSumEnergy(listEnergy.get(0)
                                        .getSumT4() * 0.95);
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT4());
                                }
                                break;

                            case 5:
                                if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                    if (listEnergyPlan.get(0)
                                        .getSumT5Plan()
                                        .equals(listEnergyPlan.get(0)
                                            .getSumT4Plan())) {
                                        x.setSumEnergy(listEnergy.get(0)
                                            .getSumT5() * 0.95);
                                    } else {
                                        x.setSumEnergy(listEnergyPlan.get(0)
                                            .getSumT5Plan());
                                    }
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT5());
                                } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                    x.setSumEnergy(listEnergyPlan.get(0)
                                        .getSumT5Plan());
                                    x.setSumLandmark(listEnergyPlan.get(0)
                                        .getSumT5Plan() / 0.95);
                                } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                    x.setSumEnergy(listEnergy.get(0)
                                        .getSumT5() * 0.95);
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT5());
                                }
                                break;
                            // // Các case từ 6 đến 11
                            case 6:
                                if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                    if (listEnergyPlan.get(0)
                                        .getSumT6Plan()
                                        .equals(listEnergyPlan.get(0)
                                            .getSumT5Plan())) {
                                        x.setSumEnergy(listEnergy.get(0)
                                            .getSumT6() * 0.95);
                                    } else {
                                        x.setSumEnergy(listEnergyPlan.get(0)
                                            .getSumT6Plan());
                                    }
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT6());
                                } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                    x.setSumEnergy(listEnergyPlan.get(0)
                                        .getSumT6Plan());
                                    x.setSumLandmark(listEnergyPlan.get(0)
                                        .getSumT6Plan() / 0.95);
                                } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                    x.setSumEnergy(listEnergy.get(0)
                                        .getSumT6() * 0.95);
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT6());
                                }
                                break;

                            case 7:
                                if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                    if (listEnergyPlan.get(0)
                                        .getSumT7Plan()
                                        .equals(listEnergyPlan.get(0)
                                            .getSumT6Plan())) {
                                        x.setSumEnergy(listEnergy.get(0)
                                            .getSumT7() * 0.95);
                                    } else {
                                        x.setSumEnergy(listEnergyPlan.get(0)
                                            .getSumT7Plan());
                                    }
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT7());
                                } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                    x.setSumEnergy(listEnergyPlan.get(0)
                                        .getSumT7Plan());
                                    x.setSumLandmark(listEnergyPlan.get(0)
                                        .getSumT7Plan() / 0.95);
                                } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                    x.setSumEnergy(listEnergy.get(0)
                                        .getSumT7() * 0.95);
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT7());
                                }
                                break;

                            case 8:
                                if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                    if (listEnergyPlan.get(0)
                                        .getSumT8Plan()
                                        .equals(listEnergyPlan.get(0)
                                            .getSumT7Plan())) {
                                        x.setSumEnergy(listEnergy.get(0)
                                            .getSumT8() * 0.95);
                                    } else {
                                        x.setSumEnergy(listEnergyPlan.get(0)
                                            .getSumT8Plan());
                                    }
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT8());
                                } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                    x.setSumEnergy(listEnergyPlan.get(0)
                                        .getSumT8Plan());
                                    x.setSumLandmark(listEnergyPlan.get(0)
                                        .getSumT8Plan() / 0.95);
                                } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                    x.setSumEnergy(listEnergy.get(0)
                                        .getSumT8() * 0.95);
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT8());
                                }
                                break;

                            case 9:
                                if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                    if (listEnergyPlan.get(0)
                                        .getSumT9Plan()
                                        .equals(listEnergyPlan.get(0)
                                            .getSumT8Plan())) {
                                        x.setSumEnergy(listEnergy.get(0)
                                            .getSumT9() * 0.95);
                                    } else {
                                        x.setSumEnergy(listEnergyPlan.get(0)
                                            .getSumT9Plan());
                                    }
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT9());
                                } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                    x.setSumEnergy(listEnergyPlan.get(0)
                                        .getSumT9Plan());
                                    x.setSumLandmark(listEnergyPlan.get(0)
                                        .getSumT9Plan() / 0.95);
                                } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                    x.setSumEnergy(listEnergy.get(0)
                                        .getSumT9() * 0.95);
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT9());
                                }
                                break;

                            case 10:
                                if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                    if (listEnergyPlan.get(0)
                                        .getSumT10Plan()
                                        .equals(listEnergyPlan.get(0)
                                            .getSumT9Plan())) {
                                        x.setSumEnergy(listEnergy.get(0)
                                            .getSumT10() * 0.95);
                                    } else {
                                        x.setSumEnergy(listEnergyPlan.get(0)
                                            .getSumT10Plan());
                                    }
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT10());
                                } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                    x.setSumEnergy(listEnergyPlan.get(0)
                                        .getSumT10Plan());
                                    x.setSumLandmark(listEnergyPlan.get(0)
                                        .getSumT10Plan() / 0.95);
                                } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                    x.setSumEnergy(listEnergy.get(0)
                                        .getSumT10() * 0.95);
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT10());
                                }
                                break;

                            case 11:
                                if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                    if (listEnergyPlan.get(0)
                                        .getSumT11Plan()
                                        .equals(listEnergyPlan.get(0)
                                            .getSumT10Plan())) {
                                        x.setSumEnergy(listEnergy.get(0)
                                            .getSumT11() * 0.95);
                                    } else {
                                        x.setSumEnergy(listEnergyPlan.get(0)
                                            .getSumT11Plan());
                                    }
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT11());
                                } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                    x.setSumEnergy(listEnergyPlan.get(0)
                                        .getSumT11Plan());
                                    x.setSumLandmark(listEnergyPlan.get(0)
                                        .getSumT11Plan() / 0.95);
                                } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                    x.setSumEnergy(listEnergy.get(0)
                                        .getSumT11() * 0.95);
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT11());
                                }
                                break;

                            case 12:
                                if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
                                    if (listEnergyPlan.get(0)
                                        .getSumT12Plan()
                                        .equals(listEnergyPlan.get(0)
                                            .getSumT11Plan())) {
                                        x.setSumEnergy(listEnergy.get(0)
                                            .getSumT12() * 0.95);
                                    } else {
                                        x.setSumEnergy(listEnergyPlan.get(0)
                                            .getSumT12Plan());
                                    }
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT12());
                                } else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
                                    x.setSumEnergy(listEnergyPlan.get(0)
                                        .getSumT12Plan());
                                    x.setSumLandmark(listEnergyPlan.get(0)
                                        .getSumT12Plan() / 0.95);
                                } else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
                                    x.setSumEnergy(listEnergy.get(0)
                                        .getSumT12() * 0.95);
                                    x.setSumLandmark(listEnergy.get(0)
                                        .getSumT12());
                                }
                                break;
                        }
                        // }

                        listPowerLandmarkX.add(x);
                    }
                    List<DataPower> listPowerPush = new ArrayList<>();
                    for (LandmarksPlansEnergy x : listPowerLandmarkX) {
                        DataPower data = new DataPower();
                        data.setDateOfWeek(x.getDateOfWeek());
                        data.setPower(x.getPower());
                        data.setPlanEnergy(x.getPlanEnergy());
                        data.setTargetEnergy(x.getTargetEnergy());
                        data.setValueEnergy(x.getValueEnergy());
                        data.setViewTime(x.getViewTime());
                        listPowerPush.add(data);
                    }
                    obj.setListDataPower(listPowerPush);
                    // obj.setListDataPowerTab2(listPowerLandmarkX);
                }
            }

            if (time == 3) {
                if (type == 1) {
                    listPower = deviceService.getListEpLoadAll(condition);
                    obj.setListDataPower(listPower);
                }
                if (type == 2) {
                    listPower = deviceService.getListEpSolarAll(condition);
                    obj.setListDataPower(listPower);
                }
                if (type == 3) {
                    // listPower = deviceService.getListDataPowerBatteruByDay(condition);
                }
                if (type == 4) {
                    // listPower = deviceService.getListDataPowerWindByDay(condition);
                }
                if (type == 5) {
                    listPower = deviceService.getListEpGridAll(condition);
                    obj.setListDataPower(listPower);
                }
                if (type == 6) {
                    // listPower = deviceService.getListDataPowerLoadByDay(condition);
                }
            }

            result.add(obj);

        }
        // else {
        // condition.put("projectId", String.valueOf(projectId));
        // condition.put("schema", Schema.getSchemas(customerId));
        // condition.put("day", day);
        // condition.put("month", month);
        // condition.put("year", year);
        // condition.put("calculateFlag", String.valueOf(1));
        // condition.put("deleteFlag", String.valueOf(0));
        // condition.put("energy", "energy");
        //
        // Project project = projectService.getProject(condition);
        // DataPowerResult obj = new DataPowerResult();
        // obj.setName(project.getProjectName());
        //
        // if (time == 0) {
        // Calendar calendar = Calendar.getInstance();
        // Date date = calendar.getTime();
        // String currDayOfWeek = new SimpleDateFormat("EE",
        // Locale.ENGLISH).format(date.getTime());
        // String currentMonth = new SimpleDateFormat("MMM",
        // Locale.ENGLISH).format(date.getTime());
        //
        // if (fDate != null) {
        // condition.put("day", fDate);
        // try {
        // date = formatter.parse(fDate);
        // currDayOfWeek = new SimpleDateFormat("EE",
        // Locale.ENGLISH).format(date.getTime());
        // currentMonth = new SimpleDateFormat("MMM",
        // Locale.ENGLISH).format(date.getTime());
        // } catch (ParseException e) {
        // e.printStackTrace();
        // }
        // }
        //
        // List<DataPower> listPower = new ArrayList<>();
        // if (type == 1) {
        // listPower = deviceService.getListEpLoadByDay(condition);
        // // get plan energy
        // /*-- Get data user input */
        // HashMap<String, String> condi = new HashMap<>();
        // condi.put("schema", Schema.getSchemas(customerId));
        // condi.put("currDayOfWeek", currDayOfWeek);
        // condi.put("currentMonth", currentMonth);
        // condi.put("projectId", String.valueOf(projectId));
        // condi.put("systemTypeId", String.valueOf(type));
        // Double planEnergyInput =
        // landmarksPlanEnergyService.getEnergyByDayAndMonth(condi);
        // Double targetEnergyInput =
        // landmarksPlanEnergyService.getLandmarksEnergyByDayAndMonth(condi);
        // if (targetEnergyInput != null && targetEnergyInput != 0) {
        // if (listPower.size() > 0) {
        // for (DataPower data : listPower) {
        // data.setTargetEnergy(targetEnergyInput);
        // if (targetEnergyInput != null && targetEnergyInput != 0) {
        // data.setPlanEnergy(planEnergyInput);
        // }
        // if (targetEnergyInput == null || targetEnergyInput == 0) {
        // data.setPlanEnergy(targetEnergyInput != null ? targetEnergyInput * 0.95 : 0);
        // }
        // }
        // }
        // }
        //
        // /*-- Get data batch input */
        // if (planEnergyInput == null || planEnergyInput == 0) {
        // Double totalEnergy =
        // projectService.getDataPlanEnergyBatch(Schema.getSchemas(customerId),
        // projectId, Constants.System_type.LOAD);
        // if (listPower.size() > 0) {
        // for (DataPower data : listPower) {
        // data.setTargetEnergy(totalEnergy != null ? totalEnergy : 0);
        // data.setPlanEnergy(totalEnergy != null ? totalEnergy * 0.95 : 0);
        // }
        // }
        // }
        // obj.setListDataPower(listPower);
        // }
        // if (type == 2) {
        // // listPower = deviceService.getListEpLoadByDay(condition);
        // // get plan energy
        // /*-- Get data user input */
        // HashMap<String, String> condi = new HashMap<>();
        // condi.put("schema", Schema.getSchemas(customerId));
        // condi.put("currDayOfWeek", currDayOfWeek);
        // condi.put("currentMonth", currentMonth);
        // condi.put("projectId", String.valueOf(projectId));
        // condi.put("systemTypeId", String.valueOf(type));
        // Double planEnergyInput =
        // landmarksPlanEnergyService.getEnergyByDayAndMonth(condi);
        // Double targetEnergyInput =
        // landmarksPlanEnergyService.getLandmarksEnergyByDayAndMonth(condi);
        // if (planEnergyInput != null && planEnergyInput != 0) {
        // if (listPower.size() > 0) {
        // for (DataPower data : listPower) {
        // data.setTargetEnergy(planEnergyInput != null ? planEnergyInput : 0);
        // if (targetEnergyInput != null && targetEnergyInput != 0) {
        // data.setPlanEnergy(planEnergyInput);
        // }
        // if (targetEnergyInput == null || targetEnergyInput == 0) {
        // data.setPlanEnergy(planEnergyInput != null ? planEnergyInput * 0.95 : 0);
        // }
        // }
        // }
        // }
        //
        // /*-- Get data batch input */
        // if (planEnergyInput == null || planEnergyInput == 0) {
        // Double totalEnergy =
        // projectService.getDataPlanEnergyBatch(Schema.getSchemas(customerId),
        // projectId, Constants.System_type.SOLAR);
        // if (listPower.size() > 0) {
        // for (DataPower data : listPower) {
        // data.setTargetEnergy(totalEnergy != null ? totalEnergy : 0);
        // data.setPlanEnergy(totalEnergy != null ? totalEnergy * 0.95 : 0);
        // }
        // }
        // }
        // obj.setListDataPower(listPower);
        // }
        // if (type == 3) {
        // obj.setListDataPower(listPower);
        // }
        // if (type == 4) {
        // obj.setListDataPower(listPower);
        // }
        // if (type == 5) {
        // // listPower = deviceService.getListEpLoadByDay(condition);
        // // get plan energy
        // /*-- Get data user input */
        // HashMap<String, String> condi = new HashMap<>();
        // condi.put("schema", Schema.getSchemas(customerId));
        // condi.put("currDayOfWeek", currDayOfWeek);
        // condi.put("currentMonth", currentMonth);
        // condi.put("projectId", String.valueOf(projectId));
        // condi.put("systemTypeId", String.valueOf(type));
        // Double planEnergyInput =
        // landmarksPlanEnergyService.getEnergyByDayAndMonth(condi);
        // Double targetEnergyInput =
        // landmarksPlanEnergyService.getLandmarksEnergyByDayAndMonth(condi);
        // if (planEnergyInput != null && planEnergyInput != 0) {
        // if (listPower.size() > 0) {
        // for (DataPower data : listPower) {
        // data.setTargetEnergy(planEnergyInput != null ? planEnergyInput : 0);
        // if (targetEnergyInput != null && targetEnergyInput != 0) {
        // data.setPlanEnergy(planEnergyInput);
        // }
        // if (targetEnergyInput == null || targetEnergyInput == 0) {
        // data.setPlanEnergy(planEnergyInput != null ? planEnergyInput * 0.95 : 0);
        // }
        // }
        // }
        // }
        //
        // /*-- Get data batch input */
        // if (planEnergyInput == null || planEnergyInput == 0) {
        // Double totalEnergy =
        // projectService.getDataPlanEnergyBatch(Schema.getSchemas(customerId),
        // projectId, Constants.System_type.GRID);
        // if (listPower.size() > 0) {
        // for (DataPower data : listPower) {
        // data.setTargetEnergy(totalEnergy != null ? totalEnergy : 0);
        // data.setPlanEnergy(totalEnergy != null ? totalEnergy * 0.95 : 0);
        // }
        // }
        // }
        // obj.setListDataPower(listPower);
        // }
        // if (type == 6) {
        // obj.setListDataPower(listPower);
        // }
        // obj.setListDataPower(listPower);
        // result.add(obj);
        // }
        // if (time == 1) {
        // Calendar calendar = Calendar.getInstance();
        // Date date = calendar.getTime();
        // String currentMonth = new SimpleDateFormat("MMM",
        // Locale.ENGLISH).format(date.getTime());
        //
        // if (fDate != null) {
        // condition.put("month", fDate);
        // try {
        // date = formatterMonth.parse(fDate);
        // currentMonth = new SimpleDateFormat("MMM",
        // Locale.ENGLISH).format(date.getTime());
        // currMonth = DateUtils.toString(date, "MM");
        // } catch (ParseException e) {
        // e.printStackTrace();
        // }
        // }
        //
        // double countMon = countDayOccurence(Integer.valueOf(year),
        // Integer.valueOf(currMonth), Calendar.MONDAY);
        // double countTue = countDayOccurence(Integer.valueOf(year),
        // Integer.valueOf(currMonth),
        // Calendar.TUESDAY);
        // double countWed = countDayOccurence(Integer.valueOf(year),
        // Integer.valueOf(currMonth),
        // Calendar.WEDNESDAY);
        // double countThu = countDayOccurence(Integer.valueOf(year),
        // Integer.valueOf(currMonth),
        // Calendar.THURSDAY);
        // double countFri = countDayOccurence(Integer.valueOf(year),
        // Integer.valueOf(currMonth), Calendar.FRIDAY);
        // double countSat = countDayOccurence(Integer.valueOf(year),
        // Integer.valueOf(currMonth),
        // Calendar.SATURDAY);
        // double countSun = countDayOccurence(Integer.valueOf(year),
        // Integer.valueOf(currMonth), Calendar.SUNDAY);
        //
        // Double sumEnergyLandMark = (double) 0;
        // Double sumEnergyPlan = (double) 0;
        //
        // List<DataPower> listPower = new ArrayList<>();
        // if (type == 1) {
        // HashMap<String, String> condi = new HashMap<>();
        // condi.put("schema", Schema.getSchemas(customerId));
        // condi.put("currentMonth", currentMonth);
        // condi.put("projectId", String.valueOf(projectId));
        // condi.put("systemTypeId", String.valueOf(type));
        //
        // List<LandmarksPlansEnergy> listEnergy =
        // landmarksPlanEnergyService.getEnergyMonth(condi);
        // List<LandmarksPlansEnergy> listEnergyPlan =
        // landmarksPlanEnergyService.getEnergyMonthPlan(condi);
        // Double targetEnergyInput =
        // landmarksPlanEnergyService.getLandmarksEnergyByDayAndMonth(condi);
        // Double totalEnergy =
        // projectService.getDataPlanEnergyBatch(Schema.getSchemas(customerId),
        // projectId,
        // Constants.System_type.LOAD);
        // totalEnergy = totalEnergy != null ? totalEnergy : 0;
        // targetEnergyInput = targetEnergyInput != null ? totalEnergy : 0;
        // if (listEnergy.size() > 0) {
        // for (LandmarksPlansEnergy data : listEnergy) {
        // if (data.getValueEnergy() != 0) {
        // switch (data.getDateOfWeek()) {
        // case "MON":
        // sumEnergyLandMark += data.getValueEnergy() * countMon;
        // break;
        // case "TUE":
        // sumEnergyLandMark += data.getValueEnergy() * countTue;
        // break;
        // case "WED":
        // sumEnergyLandMark += data.getValueEnergy() * countWed;
        // break;
        // case "THU":
        // sumEnergyLandMark += data.getValueEnergy() * countThu;
        // break;
        // case "FRI":
        // sumEnergyLandMark += data.getValueEnergy() * countFri;
        // break;
        // case "SAT":
        // sumEnergyLandMark += data.getValueEnergy() * countSat;
        // break;
        // case "SUN":
        // sumEnergyLandMark += data.getValueEnergy() * countSun;
        // break;
        // }
        // } else {
        // switch (data.getDateOfWeek()) {
        // case "MON":
        // sumEnergyLandMark += totalEnergy * countMon;
        // break;
        // case "TUE":
        // sumEnergyLandMark += totalEnergy * countTue;
        // break;
        // case "WED":
        // sumEnergyLandMark += totalEnergy * countWed;
        // break;
        // case "THU":
        // sumEnergyLandMark += totalEnergy * countThu;
        // break;
        // case "FRI":
        // sumEnergyLandMark += totalEnergy * countFri;
        // break;
        // case "SAT":
        // sumEnergyLandMark += totalEnergy * countSat;
        // break;
        // case "SUN":
        // sumEnergyLandMark += totalEnergy * countSun;
        // break;
        // }
        // }
        // }
        // } else {
        // double countDay = countMon + countTue + countWed + countThu + countFri +
        // countSat + countSun;
        // sumEnergyLandMark = totalEnergy * countDay;
        // }
        // if (listEnergyPlan.size() > 0) {
        // for (LandmarksPlansEnergy data : listEnergyPlan) {
        // if (data.getValueEnergy() != 0) {
        // switch (data.getDateOfWeek()) {
        // case "MON":
        // sumEnergyPlan += data.getValueEnergy() * countMon;
        // break;
        // case "TUE":
        // sumEnergyPlan += data.getValueEnergy() * countTue;
        // break;
        // case "WED":
        // sumEnergyPlan += data.getValueEnergy() * countWed;
        // break;
        // case "THU":
        // sumEnergyPlan += data.getValueEnergy() * countThu;
        // break;
        // case "FRI":
        // sumEnergyPlan += data.getValueEnergy() * countFri;
        // break;
        // case "SAT":
        // sumEnergyPlan += data.getValueEnergy() * countSat;
        // break;
        // case "SUN":
        // sumEnergyPlan += data.getValueEnergy() * countSun;
        // break;
        // }
        // } else {
        // switch (data.getDateOfWeek()) {
        // case "MON":
        // sumEnergyPlan += totalEnergy * countMon * 0.95;
        // break;
        // case "TUE":
        // sumEnergyPlan += totalEnergy * countTue * 0.95;
        // break;
        // case "WED":
        // sumEnergyPlan += totalEnergy * countWed * 0.95;
        // break;
        // case "THU":
        // sumEnergyPlan += totalEnergy * countThu * 0.95;
        // break;
        // case "FRI":
        // sumEnergyPlan += totalEnergy * countFri * 0.95;
        // break;
        // case "SAT":
        // sumEnergyPlan += totalEnergy * countSat * 0.95;
        // break;
        // case "SUN":
        // sumEnergyPlan += totalEnergy * countSun * 0.95;
        // break;
        // }
        // }
        // }
        // } else {
        // double countDay = countMon + countTue + countWed + countThu + countFri +
        // countSat + countSun;
        // sumEnergyPlan = totalEnergy * countDay * 0.95;
        // }
        //
        // listPower = deviceService.getListEpLoadByMonth(condition);
        // if (listPower.size() > 0) {
        // for (DataPower data : listPower) {
        // data.setTargetEnergy(sumEnergyLandMark);
        // data.setPlanEnergy(sumEnergyPlan);
        // }
        // }
        // obj.setListDataPower(listPower);
        // }
        // if (type == 2) {
        // HashMap<String, String> condi = new HashMap<>();
        // condi.put("schema", Schema.getSchemas(customerId));
        // condi.put("currentMonth", currentMonth);
        // condi.put("projectId", String.valueOf(projectId));
        // condi.put("systemTypeId", String.valueOf(type));
        //
        // List<LandmarksPlansEnergy> listEnergy =
        // landmarksPlanEnergyService.getEnergyMonth(condi);
        // List<LandmarksPlansEnergy> listEnergyPlan =
        // landmarksPlanEnergyService.getEnergyMonthPlan(condi);
        // Double targetEnergyInput =
        // landmarksPlanEnergyService.getLandmarksEnergyByDayAndMonth(condi);
        // Double totalEnergy =
        // projectService.getDataPlanEnergyBatch(Schema.getSchemas(customerId),
        // projectId,
        // Constants.System_type.SOLAR);
        // totalEnergy = totalEnergy != null ? totalEnergy : 0;
        // targetEnergyInput = targetEnergyInput != null ? totalEnergy : 0;
        // if (listEnergy.size() > 0) {
        // for (LandmarksPlansEnergy data : listEnergy) {
        // if (data.getValueEnergy() != 0) {
        // switch (data.getDateOfWeek()) {
        // case "MON":
        // sumEnergyLandMark += data.getValueEnergy() * countMon;
        // break;
        // case "TUE":
        // sumEnergyLandMark += data.getValueEnergy() * countTue;
        // break;
        // case "WED":
        // sumEnergyLandMark += data.getValueEnergy() * countWed;
        // break;
        // case "THU":
        // sumEnergyLandMark += data.getValueEnergy() * countThu;
        // break;
        // case "FRI":
        // sumEnergyLandMark += data.getValueEnergy() * countFri;
        // break;
        // case "SAT":
        // sumEnergyLandMark += data.getValueEnergy() * countSat;
        // break;
        // case "SUN":
        // sumEnergyLandMark += data.getValueEnergy() * countSun;
        // break;
        // }
        // } else {
        // switch (data.getDateOfWeek()) {
        // case "MON":
        // sumEnergyLandMark += totalEnergy * countMon;
        // break;
        // case "TUE":
        // sumEnergyLandMark += totalEnergy * countTue;
        // break;
        // case "WED":
        // sumEnergyLandMark += totalEnergy * countWed;
        // break;
        // case "THU":
        // sumEnergyLandMark += totalEnergy * countThu;
        // break;
        // case "FRI":
        // sumEnergyLandMark += totalEnergy * countFri;
        // break;
        // case "SAT":
        // sumEnergyLandMark += totalEnergy * countSat;
        // break;
        // case "SUN":
        // sumEnergyLandMark += totalEnergy * countSun;
        // break;
        // }
        // }
        // }
        // } else {
        // double countDay = countMon + countTue + countWed + countThu + countFri +
        // countSat + countSun;
        // sumEnergyLandMark = totalEnergy * countDay;
        // }
        // if (listEnergyPlan.size() > 0) {
        // for (LandmarksPlansEnergy data : listEnergyPlan) {
        // if (data.getValueEnergy() != 0) {
        // switch (data.getDateOfWeek()) {
        // case "MON":
        // sumEnergyPlan += data.getValueEnergy() * countMon;
        // break;
        // case "TUE":
        // sumEnergyPlan += data.getValueEnergy() * countTue;
        // break;
        // case "WED":
        // sumEnergyPlan += data.getValueEnergy() * countWed;
        // break;
        // case "THU":
        // sumEnergyPlan += data.getValueEnergy() * countThu;
        // break;
        // case "FRI":
        // sumEnergyPlan += data.getValueEnergy() * countFri;
        // break;
        // case "SAT":
        // sumEnergyPlan += data.getValueEnergy() * countSat;
        // break;
        // case "SUN":
        // sumEnergyPlan += data.getValueEnergy() * countSun;
        // break;
        // }
        // } else {
        // switch (data.getDateOfWeek()) {
        // case "MON":
        // sumEnergyPlan += totalEnergy * countMon * 0.95;
        // break;
        // case "TUE":
        // sumEnergyPlan += totalEnergy * countTue * 0.95;
        // break;
        // case "WED":
        // sumEnergyPlan += totalEnergy * countWed * 0.95;
        // break;
        // case "THU":
        // sumEnergyPlan += totalEnergy * countThu * 0.95;
        // break;
        // case "FRI":
        // sumEnergyPlan += totalEnergy * countFri * 0.95;
        // break;
        // case "SAT":
        // sumEnergyPlan += totalEnergy * countSat * 0.95;
        // break;
        // case "SUN":
        // sumEnergyPlan += totalEnergy * countSun * 0.95;
        // break;
        // }
        // }
        // }
        // } else {
        // double countDay = countMon + countTue + countWed + countThu + countFri +
        // countSat + countSun;
        // sumEnergyPlan = totalEnergy * countDay * 0.95;
        // }
        //
        // // listPower = deviceService.getListEpLoadByMonth(condition);
        // if (listPower.size() > 0) {
        // for (DataPower data : listPower) {
        // data.setTargetEnergy(sumEnergyLandMark);
        // data.setPlanEnergy(sumEnergyPlan);
        // }
        // }
        // obj.setListDataPower(listPower);
        // }
        // if (type == 3) {
        // obj.setListDataPower(listPower);
        // }
        // if (type == 4) {
        // obj.setListDataPower(listPower);
        // }
        // if (type == 5) {
        // HashMap<String, String> condi = new HashMap<>();
        // condi.put("schema", Schema.getSchemas(customerId));
        // condi.put("currentMonth", currentMonth);
        // condi.put("projectId", String.valueOf(projectId));
        // condi.put("systemTypeId", String.valueOf(type));
        //
        // List<LandmarksPlansEnergy> listEnergy =
        // landmarksPlanEnergyService.getEnergyMonth(condi);
        // List<LandmarksPlansEnergy> listEnergyPlan =
        // landmarksPlanEnergyService.getEnergyMonthPlan(condi);
        // Double targetEnergyInput =
        // landmarksPlanEnergyService.getLandmarksEnergyByDayAndMonth(condi);
        // Double totalEnergy =
        // projectService.getDataPlanEnergyBatch(Schema.getSchemas(customerId),
        // projectId,
        // Constants.System_type.GRID);
        // totalEnergy = totalEnergy != null ? totalEnergy : 0;
        // targetEnergyInput = targetEnergyInput != null ? totalEnergy : 0;
        // if (listEnergy.size() > 0) {
        // for (LandmarksPlansEnergy data : listEnergy) {
        // if (data.getValueEnergy() != 0) {
        // switch (data.getDateOfWeek()) {
        // case "MON":
        // sumEnergyLandMark += data.getValueEnergy() * countMon;
        // break;
        // case "TUE":
        // sumEnergyLandMark += data.getValueEnergy() * countTue;
        // break;
        // case "WED":
        // sumEnergyLandMark += data.getValueEnergy() * countWed;
        // break;
        // case "THU":
        // sumEnergyLandMark += data.getValueEnergy() * countThu;
        // break;
        // case "FRI":
        // sumEnergyLandMark += data.getValueEnergy() * countFri;
        // break;
        // case "SAT":
        // sumEnergyLandMark += data.getValueEnergy() * countSat;
        // break;
        // case "SUN":
        // sumEnergyLandMark += data.getValueEnergy() * countSun;
        // break;
        // }
        // } else {
        // switch (data.getDateOfWeek()) {
        // case "MON":
        // sumEnergyLandMark += totalEnergy * countMon;
        // break;
        // case "TUE":
        // sumEnergyLandMark += totalEnergy * countTue;
        // break;
        // case "WED":
        // sumEnergyLandMark += totalEnergy * countWed;
        // break;
        // case "THU":
        // sumEnergyLandMark += totalEnergy * countThu;
        // break;
        // case "FRI":
        // sumEnergyLandMark += totalEnergy * countFri;
        // break;
        // case "SAT":
        // sumEnergyLandMark += totalEnergy * countSat;
        // break;
        // case "SUN":
        // sumEnergyLandMark += totalEnergy * countSun;
        // break;
        // }
        // }
        // }
        // } else {
        // double countDay = countMon + countTue + countWed + countThu + countFri +
        // countSat + countSun;
        // sumEnergyLandMark = totalEnergy * countDay;
        // }
        // if (listEnergyPlan.size() > 0) {
        // for (LandmarksPlansEnergy data : listEnergyPlan) {
        // if (data.getValueEnergy() != 0) {
        // switch (data.getDateOfWeek()) {
        // case "MON":
        // sumEnergyPlan += data.getValueEnergy() * countMon;
        // break;
        // case "TUE":
        // sumEnergyPlan += data.getValueEnergy() * countTue;
        // break;
        // case "WED":
        // sumEnergyPlan += data.getValueEnergy() * countWed;
        // break;
        // case "THU":
        // sumEnergyPlan += data.getValueEnergy() * countThu;
        // break;
        // case "FRI":
        // sumEnergyPlan += data.getValueEnergy() * countFri;
        // break;
        // case "SAT":
        // sumEnergyPlan += data.getValueEnergy() * countSat;
        // break;
        // case "SUN":
        // sumEnergyPlan += data.getValueEnergy() * countSun;
        // break;
        // }
        // } else {
        // switch (data.getDateOfWeek()) {
        // case "MON":
        // sumEnergyPlan += totalEnergy * countMon * 0.95;
        // break;
        // case "TUE":
        // sumEnergyPlan += totalEnergy * countTue * 0.95;
        // break;
        // case "WED":
        // sumEnergyPlan += totalEnergy * countWed * 0.95;
        // break;
        // case "THU":
        // sumEnergyPlan += totalEnergy * countThu * 0.95;
        // break;
        // case "FRI":
        // sumEnergyPlan += totalEnergy * countFri * 0.95;
        // break;
        // case "SAT":
        // sumEnergyPlan += totalEnergy * countSat * 0.95;
        // break;
        // case "SUN":
        // sumEnergyPlan += totalEnergy * countSun * 0.95;
        // break;
        // }
        // }
        // }
        // } else {
        // double countDay = countMon + countTue + countWed + countThu + countFri +
        // countSat + countSun;
        // sumEnergyPlan = totalEnergy * countDay * 0.95;
        // }
        //
        // // listPower = deviceService.getListEpLoadByMonth(condition);
        // if (listPower.size() > 0) {
        // for (DataPower data : listPower) {
        // data.setTargetEnergy(sumEnergyLandMark);
        // data.setPlanEnergy(sumEnergyPlan);
        // }
        // }
        // obj.setListDataPower(listPower);
        // }
        // if (type == 6) {
        // obj.setListDataPower(listPower);
        // }
        // obj.setListDataPower(listPower);
        // result.add(obj);
        // }
        // if (time == 2) {
        // Calendar calendar = Calendar.getInstance();
        // Date date = calendar.getTime();
        //
        // if (fDate != null) {
        // condition.put("year", fDate);
        // year = fDate;
        // }
        //
        // Double sumEnergyLandMark = (double) 0;
        // Double sumEnergyPlan = (double) 0;
        //
        // List<DataPower> listPower = new ArrayList<>();
        // if (type == 1) {
        // listPower = deviceService.getListEpLoadByYear(condition);
        //
        // String monthOfYear = "";
        //
        // for (int i = 1; i <= 12; i++) {
        // if (i < 10) {
        // monthOfYear = "0" + i;
        // } else {
        // monthOfYear = "" + i;
        // }
        //
        // String monthYear = year + "-" + monthOfYear;
        // try {
        // date = formatterMonth.parse(monthYear);
        // monthYear = new SimpleDateFormat("MMM",
        // Locale.ENGLISH).format(date.getTime());
        // } catch (ParseException e) {
        // e.printStackTrace();
        // }
        //
        // double countMon = countDayOccurence(Integer.valueOf(year), i,
        // Calendar.MONDAY);
        // double countTue = countDayOccurence(Integer.valueOf(year), i,
        // Calendar.TUESDAY);
        // double countWed = countDayOccurence(Integer.valueOf(year), i,
        // Calendar.WEDNESDAY);
        // double countThu = countDayOccurence(Integer.valueOf(year), i,
        // Calendar.THURSDAY);
        // double countFri = countDayOccurence(Integer.valueOf(year), i,
        // Calendar.FRIDAY);
        // double countSat = countDayOccurence(Integer.valueOf(year), i,
        // Calendar.SATURDAY);
        // double countSun = countDayOccurence(Integer.valueOf(year), i,
        // Calendar.SUNDAY);
        //
        // HashMap<String, String> condi = new HashMap<>();
        // condi.put("schema", Schema.getSchemas(customerId));
        // condi.put("currentMonth", monthYear);
        // condi.put("projectId", String.valueOf(project.getProjectId()));
        // condi.put("systemTypeId", String.valueOf(type));
        //
        // List<LandmarksPlansEnergy> listEnergy =
        // landmarksPlanEnergyService.getEnergyMonth(condi);
        // List<
        // LandmarksPlansEnergy> listEnergyPlan =
        // landmarksPlanEnergyService.getEnergyMonthPlan(condi);
        // Double totalEnergy =
        // projectService.getDataPlanEnergyBatch(Schema.getSchemas(customerId),
        // project.getProjectId(), Constants.System_type.LOAD);
        // totalEnergy = totalEnergy != null ? totalEnergy : 0;
        //
        // if (listEnergy.size() > 0) {
        // for (LandmarksPlansEnergy data : listEnergy) {
        // if (data.getValueEnergy() != 0) {
        // switch (data.getDateOfWeek()) {
        // case "MON":
        // sumEnergyLandMark += data.getValueEnergy() * countMon;
        // break;
        // case "TUE":
        // sumEnergyLandMark += data.getValueEnergy() * countTue;
        // break;
        // case "WED":
        // sumEnergyLandMark += data.getValueEnergy() * countWed;
        // break;
        // case "THU":
        // sumEnergyLandMark += data.getValueEnergy() * countThu;
        // break;
        // case "FRI":
        // sumEnergyLandMark += data.getValueEnergy() * countFri;
        // break;
        // case "SAT":
        // sumEnergyLandMark += data.getValueEnergy() * countSat;
        // break;
        // case "SUN":
        // sumEnergyLandMark += data.getValueEnergy() * countSun;
        // break;
        // }
        // } else {
        // switch (data.getDateOfWeek()) {
        // case "MON":
        // sumEnergyLandMark += totalEnergy * countMon;
        // break;
        // case "TUE":
        // sumEnergyLandMark += totalEnergy * countTue;
        // break;
        // case "WED":
        // sumEnergyLandMark += totalEnergy * countWed;
        // break;
        // case "THU":
        // sumEnergyLandMark += totalEnergy * countThu;
        // break;
        // case "FRI":
        // sumEnergyLandMark += totalEnergy * countFri;
        // break;
        // case "SAT":
        // sumEnergyLandMark += totalEnergy * countSat;
        // break;
        // case "SUN":
        // sumEnergyLandMark += totalEnergy * countSun;
        // break;
        // }
        // }
        // }
        // } else {
        // double countDay = countMon + countTue + countWed + countThu + countFri +
        // countSat
        // + countSun;
        // sumEnergyLandMark = totalEnergy * countDay;
        // }
        // if (listEnergyPlan.size() > 0) {
        // for (LandmarksPlansEnergy data : listEnergyPlan) {
        // if (data.getValueEnergy() != 0) {
        // switch (data.getDateOfWeek()) {
        // case "MON":
        // sumEnergyPlan += data.getValueEnergy() * countMon;
        // break;
        // case "TUE":
        // sumEnergyPlan += data.getValueEnergy() * countTue;
        // break;
        // case "WED":
        // sumEnergyPlan += data.getValueEnergy() * countWed;
        // break;
        // case "THU":
        // sumEnergyPlan += data.getValueEnergy() * countThu;
        // break;
        // case "FRI":
        // sumEnergyPlan += data.getValueEnergy() * countFri;
        // break;
        // case "SAT":
        // sumEnergyPlan += data.getValueEnergy() * countSat;
        // break;
        // case "SUN":
        // sumEnergyPlan += data.getValueEnergy() * countSun;
        // break;
        // }
        // } else {
        // switch (data.getDateOfWeek()) {
        // case "MON":
        // sumEnergyPlan += totalEnergy * countMon * 0.95;
        // break;
        // case "TUE":
        // sumEnergyPlan += totalEnergy * countTue * 0.95;
        // break;
        // case "WED":
        // sumEnergyPlan += totalEnergy * countWed * 0.95;
        // break;
        // case "THU":
        // sumEnergyPlan += totalEnergy * countThu * 0.95;
        // break;
        // case "FRI":
        // sumEnergyPlan += totalEnergy * countFri * 0.95;
        // break;
        // case "SAT":
        // sumEnergyPlan += totalEnergy * countSat * 0.95;
        // break;
        // case "SUN":
        // sumEnergyPlan += totalEnergy * countSun * 0.95;
        // break;
        // }
        // }
        // }
        // } else {
        // double countDay = countMon + countTue + countWed + countThu + countFri +
        // countSat
        // + countSun;
        // sumEnergyPlan = totalEnergy * countDay * 0.95;
        // }
        //
        // if (listPower.size() > 0) {
        // for (DataPower data : listPower) {
        // data.setTargetEnergy(sumEnergyLandMark);
        // data.setPlanEnergy(sumEnergyPlan);
        // }
        // }
        // }
        //
        // }
        // if (type == 2) {
        // listPower = deviceService.getListEpSolarByYear(condition);
        // }
        // if (type == 3) {
        // obj.setListDataPower(listPower);
        // }
        // if (type == 4) {
        // obj.setListDataPower(listPower);
        // }
        // if (type == 5) {
        // listPower = deviceService.getListEpGridByYear(condition);
        // }
        // if (type == 6) {
        // obj.setListDataPower(listPower);
        // }
        // obj.setListDataPower(listPower);
        // result.add(obj);
        // }
        // if (time == 3) {
        // List<DataPower> listPower = new ArrayList<>();
        // if (type == 1) {
        // listPower = deviceService.getListEpLoadAll(condition);
        // }
        // if (type == 2) {
        // listPower = deviceService.getListEpSolarAll(condition);
        // }
        // if (type == 3) {
        // // listPower = deviceService.getListDataPowerLoadByDay(condition);
        // }
        // if (type == 4) {
        // // listPower = deviceService.getListDataPowerLoadByDay(condition);
        // }
        // if (type == 5) {
        // listPower = deviceService.getListEpGridAll(condition);
        // }
        // if (type == 6) {
        // // listPower = deviceService.getListDataPowerLoadByDay(condition);
        // }
        // obj.setListDataPower(listPower);
        // result.add(obj);
        // }
        // }

        return new ResponseEntity<Object>(result, HttpStatus.OK);

    }

    @GetMapping ("/getDataTab3")
    public ResponseEntity<Object> getDataTab3(@RequestParam ("customerId") final Integer customerId,
        @RequestParam (value = "projectId", required = false) final Integer projectId,
        @RequestParam (value = "time", required = false) final Integer time,
        @RequestParam (value = "fDate", required = false) final String fDate,
        @RequestParam (value = "ids", required = false) final String ids) {

        List<Object> result = new ArrayList<>();

        String day = DateUtils.toString(new Date(), Constants.ES.DATE_FORMAT_YMD);
        String month = DateUtils.toString(new Date(), Constants.ES.DATE_FORMAT_YM_02);
        String year = DateUtils.toString(new Date(), "yyyy");

        HashMap<String, String> condition = new HashMap<>();

        String proIds = "";

        if (projectId == null) {

            condition.put("customerId", String.valueOf(customerId));
            if (ids == "") {
                proIds = null;
            } else {
                proIds = ids;
            }
            List<Project> listProject = projectService.getListProjectByCustomerId(String.valueOf(customerId), proIds);

            for (Project project : listProject) {
                DataPowerResult obj = new DataPowerResult();
                obj.setName(project.getProjectName());
                List<DataPowerResult> listDataModule = new ArrayList<>();
                condition.put("projectId", String.valueOf(project.getProjectId()));
                condition.put("schema", Schema.getSchemas(customerId));
                condition.put("day", day);
                condition.put("month", month);
                condition.put("year", year);
                condition.put("energy", "energy");

                if (time == 0) {
                    if (fDate != null) {
                        condition.put("day", fDate);
                    }
                    condition.put("viewType", "3");

                    List<DataPower> listPowerL = deviceService.getListDataPowerLoadByDay(condition);
                    if (listPowerL.size() > 0) {
                        obj.setDataPower(listPowerL.get(0)
                            .getPower());
                    }
                    DataPowerResult objS = new DataPowerResult();
                    objS.setName("SOLAR");
                    List<DataPower> listPowerS = deviceService.getListDataPowerSolarByDay(condition);
                    if (listPowerS.size() > 0) {
                        objS.setDataPower(listPowerS.get(0)
                            .getPower());
                    }
                    listDataModule.add(objS);
                    DataPowerResult objG = new DataPowerResult();
                    objG.setName("GRID");
                    List<DataPower> listPowerG = deviceService.getListDataPowerGridByDay(condition);
                    if (listPowerG.size() > 0) {
                        objG.setDataPower(listPowerG.get(0)
                            .getPower());
                    } else {
                        if (obj.getDataPower() != null && objS.getDataPower() == null) {
                            objG.setDataPower(obj.getDataPower());
                        }
                        if (obj.getDataPower() != null && objS.getDataPower() != null) {
                            objG.setDataPower(obj.getDataPower() - objS.getDataPower());
                        }
                    }
                    listDataModule.add(objG);
                    // // List<DataPower> listPowerB =
                    // deviceService.getListDataPowerBatteruByDay(condition);
                    // // List<DataPower> listPowerW =
                    // deviceService.getListDataPowerWindByDay(condition);
                    // // List<DataPower> listPowerM =
                    // deviceService.getListDataPowerLoadByDay(condition);

                    obj.setListDataModule(listDataModule);
                }

                if (time == 1) {
                    if (fDate != null) {
                        condition.put("month", fDate);
                    }
                    condition.put("viewType", "2");
                    List<DataPower> listPowerL = deviceService.getListDataPowerLoadByMonth(condition);
                    if (listPowerL.size() > 0) {
                        obj.setDataPower(listPowerL.get(0)
                            .getPower());
                    }
                    DataPowerResult objS = new DataPowerResult();
                    objS.setName("SOLAR");
                    List<DataPower> listPowerS = deviceService.getListDataPowerSolarByMonth(condition);
                    if (listPowerS.size() > 0) {
                        objS.setDataPower(listPowerS.get(0)
                            .getPower());
                    }
                    listDataModule.add(objS);
                    DataPowerResult objG = new DataPowerResult();
                    objG.setName("GRID");
                    List<DataPower> listPowerG = deviceService.getListDataPowerGridByMonth(condition);
                    if (listPowerG.size() > 0) {
                        objG.setDataPower(listPowerG.get(0)
                            .getPower());
                    } else {
                        if (obj.getDataPower() != null && objS.getDataPower() == null) {
                            objG.setDataPower(obj.getDataPower());
                        }
                        if (obj.getDataPower() != null && objS.getDataPower() != null) {
                            objG.setDataPower(obj.getDataPower() - objS.getDataPower());
                        }
                    }
                    listDataModule.add(objG);
                    // // List<DataPower> listPowerB =
                    // deviceService.getListDataPowerBatteruByDay(condition);
                    // // List<DataPower> listPowerW =
                    // deviceService.getListDataPowerWindByDay(condition);
                    // // List<DataPower> listPowerM =
                    // deviceService.getListDataPowerLoadByDay(condition);

                    obj.setListDataModule(listDataModule);
                }

                if (time == 2) {
                    if (fDate != null) {
                        condition.put("year", fDate);
                    }
                    condition.put("viewType", "1");
                    List<DataPower> listPowerL = deviceService.getListDataPowerLoadByYear(condition);
                    if (listPowerL.size() > 0) {
                        obj.setDataPower(listPowerL.get(0)
                            .getPower());
                    }
                    DataPowerResult objS = new DataPowerResult();
                    objS.setName("SOLAR");
                    List<DataPower> listPowerS = deviceService.getListDataPowerSolarByYear(condition);
                    if (listPowerS.size() > 0) {
                        objS.setDataPower(listPowerS.get(0)
                            .getPower());
                    }
                    listDataModule.add(objS);
                    DataPowerResult objG = new DataPowerResult();
                    objG.setName("GRID");
                    List<DataPower> listPowerG = deviceService.getListDataPowerGridByYear(condition);
                    if (listPowerG.size() > 0) {
                        objG.setDataPower(listPowerG.get(0)
                            .getPower());
                    } else {
                        if (obj.getDataPower() != null && objS.getDataPower() == null) {
                            objG.setDataPower(obj.getDataPower());
                        }
                        if (obj.getDataPower() != null && objS.getDataPower() != null) {
                            objG.setDataPower(obj.getDataPower() - objS.getDataPower());
                        }
                    }
                    listDataModule.add(objG);
                    // // List<DataPower> listPowerB =
                    // deviceService.getListDataPowerBatteruByDay(condition);
                    // // List<DataPower> listPowerW =
                    // deviceService.getListDataPowerWindByDay(condition);
                    // // List<DataPower> listPowerM =
                    // deviceService.getListDataPowerLoadByDay(condition);

                    obj.setListDataModule(listDataModule);
                }

                if (time == 3) {
                    List<DataPower> listPowerL = deviceService.getListEpLoadAll(condition);
                    if (listPowerL.size() > 0) {
                        obj.setDataPower(listPowerL.get(0)
                            .getPower());
                    }
                    DataPowerResult objS = new DataPowerResult();
                    objS.setName("SOLAR");
                    List<DataPower> listPowerS = deviceService.getListEpSolarAll(condition);
                    if (listPowerS.size() > 0) {
                        objS.setDataPower(listPowerS.get(0)
                            .getPower());
                    }
                    listDataModule.add(objS);
                    DataPowerResult objG = new DataPowerResult();
                    objG.setName("GRID");
                    List<DataPower> listPowerG = deviceService.getListEpGridAll(condition);
                    if (listPowerG.size() > 0) {
                        objG.setDataPower(listPowerG.get(0)
                            .getPower());
                    } else {
                        if (obj.getDataPower() != null && objS.getDataPower() == null) {
                            objG.setDataPower(obj.getDataPower());
                        }
                        if (obj.getDataPower() != null && objS.getDataPower() != null) {
                            objG.setDataPower(obj.getDataPower() - objS.getDataPower());
                        }
                    }
                    listDataModule.add(objG);
                    // // List<DataPower> listPowerB =
                    // deviceService.getListDataPowerBatteruByDay(condition);
                    // // List<DataPower> listPowerW =
                    // deviceService.getListDataPowerWindByDay(condition);
                    // // List<DataPower> listPowerM =
                    // deviceService.getListDataPowerLoadByDay(condition);

                    obj.setListDataModule(listDataModule);
                }

                result.add(obj);
            }

        } else {
            condition.put("projectId", String.valueOf(projectId));
            condition.put("schema", Schema.getSchemas(customerId));
            condition.put("day", day);
            condition.put("month", month);
            condition.put("year", year);
            condition.put("calculateFlag", String.valueOf(1));
            condition.put("deleteFlag", String.valueOf(0));
            condition.put("energy", "energy");

            Project project = projectService.getProject(condition);
            List<DataPowerResult> listDataModule = new ArrayList<>();
            DataPowerResult obj = new DataPowerResult();
            obj.setName(project.getProjectName());

            if (time == 0) {
                if (fDate != null) {
                    condition.put("day", fDate);
                }
                condition.put("viewType", "3");
                List<DataPower> listPowerL = deviceService.getListDataPowerLoadByDay(condition);
                if (listPowerL.size() > 0) {
                    obj.setDataPower(listPowerL.get(0)
                        .getPower());
                }
                DataPowerResult objS = new DataPowerResult();
                objS.setName("SOLAR");
                List<DataPower> listPowerS = deviceService.getListDataPowerSolarByDay(condition);
                if (listPowerS.size() > 0) {
                    objS.setDataPower(listPowerS.get(0)
                        .getPower());
                }
                listDataModule.add(objS);
                DataPowerResult objG = new DataPowerResult();
                objG.setName("GRID");
                List<DataPower> listPowerG = deviceService.getListDataPowerGridByDay(condition);
                if (listPowerG.size() > 0) {
                    objG.setDataPower(listPowerG.get(0)
                        .getPower());
                } else {
                    if (obj.getDataPower() != null && objS.getDataPower() == null) {
                        objG.setDataPower(obj.getDataPower());
                    }
                    if (obj.getDataPower() != null && objS.getDataPower() != null) {
                        objG.setDataPower(obj.getDataPower() - objS.getDataPower());
                    }
                }
                listDataModule.add(objG);
                // // List<DataPower> listPowerB =
                // deviceService.getListDataPowerBatteruByDay(condition);
                // // List<DataPower> listPowerW =
                // deviceService.getListDataPowerWindByDay(condition);
                // // List<DataPower> listPowerM =
                // deviceService.getListDataPowerLoadByDay(condition);

                obj.setListDataModule(listDataModule);
            }

            if (time == 1) {
                if (fDate != null) {
                    condition.put("month", fDate);
                }
                condition.put("viewType", "2");
                List<DataPower> listPowerL = deviceService.getListDataPowerLoadByMonth(condition);
                if (listPowerL.size() > 0) {
                    obj.setDataPower(listPowerL.get(0)
                        .getPower());
                }
                DataPowerResult objS = new DataPowerResult();
                objS.setName("SOLAR");
                List<DataPower> listPowerS = deviceService.getListDataPowerSolarByMonth(condition);
                if (listPowerS.size() > 0) {
                    objS.setDataPower(listPowerS.get(0)
                        .getPower());
                }
                listDataModule.add(objS);
                DataPowerResult objG = new DataPowerResult();
                objG.setName("GRID");
                List<DataPower> listPowerG = deviceService.getListDataPowerGridByMonth(condition);
                if (listPowerG.size() > 0) {
                    objG.setDataPower(listPowerG.get(0)
                        .getPower());
                } else {
                    if (obj.getDataPower() != null && objS.getDataPower() == null) {
                        objG.setDataPower(obj.getDataPower());
                    }
                    if (obj.getDataPower() != null && objS.getDataPower() != null) {
                        objG.setDataPower(obj.getDataPower() - objS.getDataPower());
                    }
                }
                listDataModule.add(objG);
                // // List<DataPower> listPowerB =
                // deviceService.getListDataPowerBatteruByDay(condition);
                // // List<DataPower> listPowerW =
                // deviceService.getListDataPowerWindByDay(condition);
                // // List<DataPower> listPowerM =
                // deviceService.getListDataPowerLoadByDay(condition);

                obj.setListDataModule(listDataModule);
            }

            if (time == 2) {
                if (fDate != null) {
                    condition.put("year", fDate);
                }
                condition.put("viewType", "1");
                List<DataPower> listPowerL = deviceService.getListDataPowerLoadByYear(condition);
                if (listPowerL.size() > 0) {
                    obj.setDataPower(listPowerL.get(0)
                        .getPower());
                }
                DataPowerResult objS = new DataPowerResult();
                objS.setName("SOLAR");
                List<DataPower> listPowerS = deviceService.getListDataPowerSolarByYear(condition);
                if (listPowerS.size() > 0) {
                    objS.setDataPower(listPowerS.get(0)
                        .getPower());
                }
                listDataModule.add(objS);
                DataPowerResult objG = new DataPowerResult();
                objG.setName("GRID");
                List<DataPower> listPowerG = deviceService.getListDataPowerGridByYear(condition);
                if (listPowerG.size() > 0) {
                    objG.setDataPower(listPowerG.get(0)
                        .getPower());
                } else {
                    if (obj.getDataPower() != null && objS.getDataPower() == null) {
                        objG.setDataPower(obj.getDataPower());
                    }
                    if (obj.getDataPower() != null && objS.getDataPower() != null) {
                        objG.setDataPower(obj.getDataPower() - objS.getDataPower());
                    }
                }
                listDataModule.add(objG);
                // // List<DataPower> listPowerB =
                // deviceService.getListDataPowerBatteruByDay(condition);
                // // List<DataPower> listPowerW =
                // deviceService.getListDataPowerWindByDay(condition);
                // // List<DataPower> listPowerM =
                // deviceService.getListDataPowerLoadByDay(condition);

                obj.setListDataModule(listDataModule);
            }

            if (time == 3) {
                List<DataPower> listPowerL = deviceService.getListEpLoadAll(condition);
                if (listPowerL.size() > 0) {
                    obj.setDataPower(listPowerL.get(0)
                        .getPower());
                }
                DataPowerResult objS = new DataPowerResult();
                objS.setName("SOLAR");
                List<DataPower> listPowerS = deviceService.getListEpSolarAll(condition);
                if (listPowerS.size() > 0) {
                    objS.setDataPower(listPowerS.get(0)
                        .getPower());
                }
                listDataModule.add(objS);
                DataPowerResult objG = new DataPowerResult();
                objG.setName("GRID");
                List<DataPower> listPowerG = deviceService.getListEpGridAll(condition);
                if (listPowerG.size() > 0) {
                    objG.setDataPower(listPowerG.get(0)
                        .getPower());
                } else {
                    if (obj.getDataPower() != null && objS.getDataPower() == null) {
                        objG.setDataPower(obj.getDataPower());
                    }
                    if (obj.getDataPower() != null && objS.getDataPower() != null) {
                        objG.setDataPower(obj.getDataPower() - objS.getDataPower());
                    }
                }
                listDataModule.add(objG);
                // // List<DataPower> listPowerB =
                // deviceService.getListDataPowerBatteruByDay(condition);
                // // List<DataPower> listPowerW =
                // deviceService.getListDataPowerWindByDay(condition);
                // // List<DataPower> listPowerM =
                // deviceService.getListDataPowerLoadByDay(condition);

                obj.setListDataModule(listDataModule);
            }

            result.add(obj);
        }

        return new ResponseEntity<Object>(result, HttpStatus.OK);

    }

    @GetMapping ("/getDataTab4")
    public ResponseEntity<Object> getDataTab4(@RequestParam ("customerId") final Integer customerId,
        @RequestParam (value = "projectId", required = false) final Integer projectId,
        @RequestParam (value = "time", required = false) final Integer time,
        @RequestParam (value = "type", required = false) final Integer type,
        @RequestParam (value = "fDate", required = false) final String fDate,
        @RequestParam (value = "tDate", required = false) final String tDate,
        @RequestParam (value = "ids", required = false) final String ids) {

        List<Object> result = new ArrayList<>();

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

        String toDate = DateUtils.toString(new Date(), Constants.ES.DATETIME_FORMAT_YMDHMS);
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fromDate = dateFormatWithTime.format(currentTime.getTime());
        String day = DateUtils.toString(new Date(), Constants.ES.DATE_FORMAT_YMD);
        String month = DateUtils.toString(new Date(), Constants.ES.DATE_FORMAT_YM_02);

        HashMap<String, String> condition = new HashMap<>();

        String proIds = "";

        if (projectId == null) {

            condition.put("customerId", String.valueOf(customerId));
            if (ids == "") {
                proIds = null;
            } else {
                proIds = ids;
            }
            List<Project> listProject = projectService.getListProjectByCustomerId(String.valueOf(customerId), proIds);
            condition.put("schema", Schema.getSchemas(customerId));
            condition.put("systemType", String.valueOf(type));
            condition.put("deleteFlag", String.valueOf(0));
            for (Project project : listProject) {
                DataPowerResult obj = new DataPowerResult();
                obj.setName(project.getProjectName());
                Integer countWarning = 0;
                Integer countOffline = 0;
                Integer countOnline = 0;
                condition.put("projectId", String.valueOf(project.getProjectId()));
                List<Device> listDevice = deviceService.getDeviceByProjectId(condition);
                if (listDevice.size() > 0) {
                    obj.setCountDevice(listDevice.size());

                    for (Device device : listDevice) {
                        if (time == 1) {
                            condition.put("day", null);
                            condition.put("month", null);
                            condition.put("fromDate", fromDate);
                            condition.put("toDate", toDate);
                            condition.put("deviceId", String.valueOf(device.getDeviceId()));
                            Device dataInDay = deviceService.getDataInstance(condition);
                            if (dataInDay != null) {
                                if (dataInDay.getStatus() == 1) {
                                    countWarning++;
                                } else {
                                    countOnline++;
                                }
                            } else {
                                countOffline++;
                            }
                        }
                    }

                    obj.setCountDeviceWarning(countWarning);
                    obj.setCountDeviceOnline(countOnline);
                    obj.setCountDeviceOffline(countOffline);

                    result.add(obj);
                }
            }
        } else {
            condition.put("schema", Schema.getSchemas(customerId));
            condition.put("day", day);
            condition.put("month", month);
            condition.put("systemType", String.valueOf(type));
            condition.put("deleteFlag", String.valueOf(0));
            condition.put("projectId", String.valueOf(projectId));
            DataPowerResult obj = new DataPowerResult();

            Project project = projectService.getProject(condition);
            obj.setName(project.getProjectName());
            Integer countWarning = 0;
            Integer countOffline = 0;
            Integer countOnline = 0;
            List<Device> listDevice = deviceService.getDeviceByProjectId(condition);
            if (listDevice.size() > 0) {
                obj.setCountDevice(listDevice.size());

                for (Device device : listDevice) {
                    if (time == 0) {
                        if (fDate != null) {
                            condition.put("day", fDate);
                        }
                        condition.put("deviceId", String.valueOf(device.getDeviceId()));
                        Device dataInDay = deviceService.getDataInstance(condition);
                        if (dataInDay != null) {
                            if (dataInDay.getStatus() == 1) {
                                countWarning++;
                            } else {
                                countOnline++;
                            }
                        } else {
                            countOffline++;
                        }
                    }
                    if (time == 1) {
                        if (fDate != null) {
                            condition.put("month", fDate);
                        }
                        condition.put("deviceId", String.valueOf(device.getDeviceId()));
                        Device dataInMonth = deviceService.getDataInstance(condition);
                        if (dataInMonth != null) {
                            if (dataInMonth.getStatus() == 1) {
                                countWarning++;
                            } else {
                                countOnline++;
                            }
                        } else {
                            countOffline++;
                        }
                    }
                    if (time == 4) {
                        condition.put("day", null);
                        condition.put("month", null);
                        condition.put("fromDate", fromDate);
                        condition.put("toDate", toDate);
                        condition.put("deviceId", String.valueOf(device.getDeviceId()));
                        Device dataInTime = deviceService.getDataInstance(condition);
                        if (dataInTime != null) {
                            Warning warning = warningService.getListWarning(condition);
                            if (warning != null) {
                                countWarning++;
                            } else {
                                countOnline++;
                            }
                        } else {
                            countOffline++;
                        }
                    }
                }

                obj.setCountDeviceWarning(countWarning);
                obj.setCountDeviceOnline(countOnline);
                obj.setCountDeviceOffline(countOffline);

                result.add(obj);
            }
        }

        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @GetMapping ("/getDataTab5")
    public ResponseEntity<Object> getDataTab5(@RequestParam ("customerId") final Integer customerId,
        @RequestParam (value = "projectId", required = false) final Integer projectId,
        @RequestParam (value = "typeFil", required = false) final Integer typeFil,
        @RequestParam (value = "type", required = false) final Integer typeModule,
        @RequestParam (value = "ids", required = false) final String ids) {

        List<Object> result = new ArrayList<>();

        HashMap<String, String> condition = new HashMap<>();

        String proIds = "";

        String schema = Schema.getSchemas(Integer.valueOf(customerId));

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

        String toDate = DateUtils.toString(new Date(), Constants.ES.DATETIME_FORMAT_YMDHMS);
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fromDate = dateFormatWithTime.format(currentTime.getTime());

        if (projectId == null) {

            condition.put("customerId", String.valueOf(customerId));
            if (ids == "") {
                proIds = null;
            } else {
                proIds = ids;
            }

            List<Project> listProject = projectService.getListProjectByCustomerId(String.valueOf(customerId), proIds);
            condition.put("systemType", String.valueOf(typeModule));
            for (Project project : listProject) {
                DataPowerResult obj = new DataPowerResult();
                List<DataPowerResult> list = new ArrayList<>();
                obj.setName(project.getProjectName());
                condition.put("projectId", String.valueOf(project.getProjectId()));
                List<Device> listDevice = deviceService.getDeviceByProjectId(condition);
                List<Integer> objectIds = new ArrayList<>();
                List<Integer> objectTypeIds = new ArrayList<>();
                if (listDevice.size() > 0) {
                    for (Device device : listDevice) {
                        if (device.getObjectTypeId() != null) {
                            objectIds.add(device.getObjectTypeId());
                        }
                    }
                    List<Integer> objIds = objectIds.stream()
                        .distinct()
                        .collect(Collectors.toList());

                    for (Integer id : objIds) {
                        HashMap<String, String> con = new HashMap<>();
                        con.put("objectId", String.valueOf(id));
                        Integer objectTypeId = deviceService.getObjectTypeIdByObjId(con);
                        if (objectTypeId != null) {
                            objectTypeIds.add(objectTypeId);
                        }
                    }

                    objectTypeIds = objectTypeIds.stream()
                        .distinct()
                        .collect(Collectors.toList());

                    for (Integer id : objectTypeIds) {
                        Integer countWar = 0;
                        Integer countOnl = 0;
                        Integer countOff = 0;
                        DataPowerResult objT = new DataPowerResult();
                        condition.put("objectTypeId", String.valueOf(id));
                        objT = deviceService.getCountObjectByObjectTypeId(condition);
                        List<Integer> listObjIds = deviceService.getListObjByObjectTypeId(condition);

                        List<Integer> listObjIdsResult = listObjIds.stream()
                            .distinct()
                            .collect(Collectors.toList());

                        for (Integer idObj : listObjIdsResult) {
                            Integer cOnl = 0;
                            Integer cOff = 0;
                            Map<String, String> con = new HashMap<>();
                            con.put("objectTypeId", String.valueOf(idObj));
                            con.put("systemTypeId", String.valueOf(typeModule));
                            List<Device> listDv = deviceService.getDeviceByObjectTypeId(con);
                            if (listDv.size() > 0) {
                                for (int i = 0; i < listDv.size(); i++) {
                                    Map<String, String> condi = new HashMap<>();
                                    condi.put("deviceId", String.valueOf(listDv.get(i)
                                        .getDeviceId()));
                                    condi.put("fromDate", fromDate);
                                    condi.put("toDate", toDate);
                                    condi.put("schema", schema);
                                    condi.put("day", null);
                                    condi.put("month", null);
                                    Device device = deviceService.getDataInstance(condi);
                                    if (device != null) {
                                        if (device.getStatus() == 1) {
                                            countWar++;
                                            cOnl = 0;
                                            break;
                                        } else {
                                            cOnl++;
                                        }
                                    } else {
                                        cOff++;
                                    }
                                }
                            } else {
                            }
                            if (cOff == listDv.size()) {
                                countOff++;
                            }

                            if (cOnl > 0) {
                                countOnl++;
                            }

                        }
                        objT.setCountDeviceWarning(countWar);
                        objT.setCountDeviceOffline(countOff);
                        objT.setCountDeviceOnline(countOnl);
                        list.add(objT);
                    }
                }
                list = list.stream()
                    .distinct()
                    .collect(Collectors.toList());
                obj.setListDataModule(list);

                result.add(obj);
            }

        } else {
            DataPowerResult obj = new DataPowerResult();
            List<DataPowerResult> list = new ArrayList<>();
            condition.put("projectId", String.valueOf(projectId));
            condition.put("systemType", String.valueOf(typeModule));

            Project project = projectService.getProject(condition);
            obj.setName(project.getProjectName());
            List<Device> listDevice = deviceService.getDeviceByProjectId(condition);
            List<Integer> objectIds = new ArrayList<>();
            List<Integer> objectTypeIds = new ArrayList<>();
            if (listDevice.size() > 0) {
                for (Device device : listDevice) {
                    if (device.getObjectTypeId() != null) {
                        objectIds.add(device.getObjectTypeId());
                    }
                }
                List<Integer> objIds = objectIds.stream()
                    .distinct()
                    .collect(Collectors.toList());

                for (Integer id : objIds) {
                    HashMap<String, String> con = new HashMap<>();
                    con.put("objectId", String.valueOf(id));
                    Integer objectTypeId = deviceService.getObjectTypeIdByObjId(con);
                    if (objectTypeId != null) {
                        objectTypeIds.add(objectTypeId);
                    }
                }

                objectTypeIds = objectTypeIds.stream()
                    .distinct()
                    .collect(Collectors.toList());

                Integer count = 0;
                for (Integer id : objectTypeIds) {
                    Integer countWar = 0;
                    Integer countOnl = 0;
                    Integer countOff = 0;
                    DataPowerResult objT = new DataPowerResult();
                    condition.put("objectTypeId", String.valueOf(id));
                    objT = deviceService.getCountObjectByObjectTypeId(condition);
                    List<Integer> listObjIds = deviceService.getListObjByObjectTypeId(condition);

                    List<Integer> listObjIdsResult = listObjIds.stream()
                        .distinct()
                        .collect(Collectors.toList());

                    for (Integer idObj : listObjIdsResult) {
                        Integer cOnl = 0;
                        Integer cOff = 0;
                        Map<String, String> con = new HashMap<>();
                        con.put("objectTypeId", String.valueOf(idObj));
                        con.put("systemTypeId", String.valueOf(typeModule));
                        List<Device> listDv = deviceService.getDeviceByObjectTypeId(con);
                        if (listDv.size() > 0) {
                            for (int i = 0; i < listDv.size(); i++) {
                                Map<String, String> condi = new HashMap<>();
                                condi.put("deviceId", String.valueOf(listDv.get(i)
                                    .getDeviceId()));
                                condi.put("fromDate", fromDate);
                                condi.put("toDate", toDate);
                                condi.put("schema", schema);
                                condi.put("day", null);
                                condi.put("month", null);
                                Device device = deviceService.getDataInstance(condi);
                                if (device != null) {
                                    if (device.getStatus() == 1) {
                                        countWar++;
                                        cOnl = 0;
                                        break;
                                    } else {
                                        cOnl++;
                                    }
                                } else {
                                    cOff++;
                                }
                            }
                        } else {
                        }
                        if (cOff == listDv.size()) {
                            countOff++;
                        }

                        if (cOnl > 0) {
                            countOnl++;
                        }

                    }

                    objT.setCountDeviceWarning(countWar);
                    objT.setCountDeviceOffline(countOff);
                    objT.setCountDeviceOnline(countOnl);
                    list.add(objT);
                }
                list = list.stream()
                    .distinct()
                    .collect(Collectors.toList());
                obj.setListDataModule(list);

                result.add(obj);
            }
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @GetMapping ("/getDataTab6")
    public ResponseEntity<?> getChartLoadCost(
        @RequestParam (value = "customerId", required = false) final Integer customerId,
        @RequestParam (value = "projectId", required = false) final Integer projectId,
        @RequestParam (value = "type", required = false) final Integer type,
        @RequestParam (value = "time", required = false) final Integer time,
        @RequestParam (value = "fromDate", required = false) final String fromDate,
        @RequestParam (value = "toDate", required = false) final String toDate,
        @RequestParam (value = "ids", required = false) final String ids) {

        log.info("getHomePageCost START");
        String schema = Schema.getSchemas(customerId);
        Map<String, Object> condition = new HashMap<>();
        List<Chart> chart = new ArrayList<>();
        condition.put(SCHEMA, schema);

        condition.put("systemTypeId", type);

        List<Object> result = new ArrayList<>();

        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);

        String proIds = "";

        if (projectId == null) {

            condition.put("customerId", String.valueOf(customerId));
            if (ids == "") {
                proIds = null;
            } else {
                proIds = ids;
            }
            List<Project> listProject = projectService.getListProjectByCustomerId(String.valueOf(customerId), proIds);

            for (Project project : listProject) {

                DataPowerResult obj = new DataPowerResult();
                obj.setName(project.getProjectName());
                List<Chart> listCost = new ArrayList<>();
                condition.put("projectId", String.valueOf(project.getProjectId()));

                if (time == 1) {
                    condition.put("viewType", 5);
                    listCost = chartService.getChartLoadCostHomePage(condition);
                    if (listCost != null) {
                        obj.setListDataCost(listCost);
                    }
                } else if (time == 2) {
                    condition.put("viewType", 3);
                    listCost = chartService.getChartLoadSumCostHomePage(condition);
                    if (listCost != null) {
                        obj.setListDataCost(listCost);
                    }
                } else if (time == 3) {
                    condition.put("viewType", 2);
                    listCost = chartService.getChartLoadSumCostHomePage(condition);
                    if (listCost != null) {
                        obj.setListDataCost(listCost);
                    }
                } else if (time == 5) {
                    condition.put("viewType", 3);
                    condition.put("typeTime", 5);
                    listCost = chartService.getChartLoadSumCostHomePage(condition);
                    if (listCost != null) {
                        obj.setListDataCost(listCost);
                    }
                } else {
                    condition.put("viewType", 1);
                    listCost = chartService.getChartLoadSumCostHomePage(condition);
                    if (listCost != null) {
                        obj.setListDataCost(listCost);
                    }
                }
                result.add(obj);
            }
        } else {
            HashMap<String, String> conditionProject = new HashMap<>();
            condition.put("customerId", String.valueOf(customerId));
            condition.put("projectId", String.valueOf(projectId));
            conditionProject.put("projectId", String.valueOf(projectId));
            conditionProject.put("customerId", String.valueOf(customerId));

            DataPowerResult obj = new DataPowerResult();
            List<Chart> listCost = new ArrayList<>();

            Project project = projectService.getProject(conditionProject);
            obj.setName(project.getProjectName());

            if (time == 1) {
                condition.put("viewType", 5);
                listCost = chartService.getChartLoadCostHomePage(condition);
                if (listCost != null) {
                    obj.setListDataCost(listCost);
                }
            } else if (time == 2) {
                condition.put("viewType", 3);
                listCost = chartService.getChartLoadSumCostHomePage(condition);
                if (listCost != null) {
                    obj.setListDataCost(listCost);
                }
            } else if (time == 3) {
                condition.put("viewType", 2);
                listCost = chartService.getChartLoadSumCostHomePage(condition);
                if (listCost != null) {
                    obj.setListDataCost(listCost);
                }
            } else if (time == 5) {
                condition.put("viewType", 3);
                condition.put("typeTime", 5);
                listCost = chartService.getChartLoadSumCostHomePage(condition);
                if (listCost != null) {
                    obj.setListDataCost(listCost);
                }
            } else {
                condition.put("viewType", 1);
                listCost = chartService.getChartLoadSumCostHomePage(condition);
                if (listCost != null) {
                    obj.setListDataCost(listCost);
                }
            }
            result.add(obj);
        }

        log.info("getChartCostLoad END");

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping ("/exportDataTab6")
    public ResponseEntity<?> exportDataTab6(
        @RequestParam (value = "customerId", required = false) final Integer customerId,
        @RequestParam (value = "projectId", required = false) final Integer projectId,
        @RequestParam (value = "type", required = false) final Integer type,
        @RequestParam (value = "time", required = false) final Integer time,
        @RequestParam (value = "fromDate", required = false) final String fromDate,
        @RequestParam (value = "toDate", required = false) final String toDate,
        @RequestParam (value = "ids", required = false) final String ids) {

        log.info("getHomePageCost START");
        String schema = Schema.getSchemas(customerId);
        Map<String, Object> condition = new HashMap<>();
        List<Chart> chart = new ArrayList<>();
        condition.put(SCHEMA, schema);

        condition.put("systemTypeId", type);

        List<DataPowerResult> result = new ArrayList<>();

        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);

        String proIds = "";
        Integer systemTypeId = type;
        String reportName = "Dữ liệu tiền điện và năng lượng";
        String projectNameExport = "";
        // String fromTimeExport = "";
        // String toTimeExport = "";
        String timeNameFile = fromDate + toDate;

        // get Customer
        Map<String, String> cus = new HashMap<>();
        cus.put("customerId", customerId + "");
        Customer custtomer = customerService.getCustomer(cus);
        // systemType
        String moduleName = "";
        if (systemTypeId == 1) {
            moduleName = "TẢI ĐIỆN";
        } else if (systemTypeId == 2) {
            moduleName = "ĐIỆN MẶT TRỜI";
        } else if (systemTypeId == 3) {
            moduleName = "ĐIỆN GIÓ";
        } else if (systemTypeId == 4) {
            moduleName = "PIN LƯU TRỮ";
        } else if (systemTypeId == 5) {
            moduleName = "LƯỚI ĐIỆN";
        }
        // getProject
        Map<String, String> pro = new HashMap<>();
        pro.put("projectId", projectId + "");
        if (projectId == null) {
            projectNameExport = "";
        } else {
            Project projectExport = projectService.getProject(pro);
            projectNameExport = projectExport.getProjectName();
        }

        // get date now
        Date dateNow = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String strDate = formatter.format(dateNow);

        long miliseconds = new Date().getTime();
        String path = this.folderName + File.separator + "DLNL" + "_"
            + convertToCamelCase(custtomer.getCustomerName()).toUpperCase() + "_" + moduleName + "_"
            + convertToCamelCase(timeNameFile) + "_" + miliseconds;

        String fileNameExcel = "DLNLTD" + "_" + convertToCamelCase(custtomer.getCustomerName()).toUpperCase() + "_"
            + moduleName + "_" + convertToCamelCase(timeNameFile) + "_" + miliseconds;

        if (projectId == null) {

            condition.put("customerId", String.valueOf(customerId));
            if (ids == "") {
                proIds = null;
            } else {
                proIds = ids;
            }
            List<Project> listProject = projectService.getListProjectByCustomerId(String.valueOf(customerId), proIds);

            for (Project project : listProject) {

                DataPowerResult obj = new DataPowerResult();
                obj.setName(project.getProjectName());
                List<Chart> listCost = new ArrayList<>();
                condition.put("projectId", String.valueOf(project.getProjectId()));

                if (time == 1) {
                    condition.put("viewType", 5);
                    listCost = chartService.getChartLoadCostHomePage(condition);
                    if (listCost != null) {
                        obj.setListDataCost(listCost);
                    }
                } else if (time == 2) {
                    condition.put("viewType", 3);
                    listCost = chartService.getChartLoadSumCostHomePage(condition);
                    if (listCost != null) {
                        obj.setListDataCost(listCost);
                    }
                } else if (time == 3) {
                    condition.put("viewType", 2);
                    listCost = chartService.getChartLoadSumCostHomePage(condition);
                    if (listCost != null) {
                        obj.setListDataCost(listCost);
                    }
                } else if (time == 5) {
                    condition.put("viewType", 3);
                    condition.put("typeTime", 5);
                    listCost = chartService.getChartLoadSumCostHomePage(condition);
                    if (listCost != null) {
                        obj.setListDataCost(listCost);
                    }
                } else {
                    condition.put("viewType", 1);
                    listCost = chartService.getChartLoadSumCostHomePage(condition);
                    if (listCost != null) {
                        obj.setListDataCost(listCost);
                    }
                }
                result.add(obj);
            }
        } else {
            HashMap<String, String> conditionProject = new HashMap<>();
            condition.put("customerId", String.valueOf(customerId));
            condition.put("projectId", String.valueOf(projectId));
            conditionProject.put("projectId", String.valueOf(projectId));
            conditionProject.put("customerId", String.valueOf(customerId));

            DataPowerResult obj = new DataPowerResult();
            List<Chart> listCost = new ArrayList<>();

            Project project = projectService.getProject(conditionProject);
            obj.setName(project.getProjectName());

            if (time == 1) {
                condition.put("viewType", 5);
                listCost = chartService.getChartLoadCostHomePage(condition);
                if (listCost != null) {
                    obj.setListDataCost(listCost);
                }
            } else if (time == 2) {
                condition.put("viewType", 3);
                listCost = chartService.getChartLoadSumCostHomePage(condition);
                if (listCost != null) {
                    obj.setListDataCost(listCost);
                }
            } else if (time == 3) {
                condition.put("viewType", 2);
                listCost = chartService.getChartLoadSumCostHomePage(condition);
                if (listCost != null) {
                    obj.setListDataCost(listCost);
                }
            } else if (time == 5) {
                condition.put("viewType", 3);
                condition.put("typeTime", 5);
                listCost = chartService.getChartLoadSumCostHomePage(condition);
                if (listCost != null) {
                    obj.setListDataCost(listCost);
                }
            } else {
                condition.put("viewType", 1);
                listCost = chartService.getChartLoadSumCostHomePage(condition);
                if (listCost != null) {
                    obj.setListDataCost(listCost);
                }
            }
            result.add(obj);
        }

        log.info("getChartCostLoad END");
        // if (result.size() <= 0) {
        // return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        // }
        try {
            createDataExcelTab6(result, custtomer.getCustomerName()
                .toUpperCase(), custtomer.getDescription(), time, reportName, systemTypeId, moduleName,
                projectNameExport, fromDate, toDate, strDate, path, fileNameExcel);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        log.info("HomePageController.downLoadTab6() START");
        File f = new File(path);
        if (f.exists()) {
            log.info("HomePageController.downLoadTab6() check file exists");
            String contentType = "application/zip";
            String headerValue = "attachment; filename=" + f.getName() + ".zip";
            Path realPath = Paths.get(path + ".zip");
            Resource resource = null;
            try {
                resource = new UrlResource(realPath.toUri());

            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("HomePageController.downLoadTab6() END");
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .body(resource);

        } else {
            log.info("ReportController.downloadReport() error");
            return new ResponseEntity<Resource>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping ("/exportDataTab1")
    public ResponseEntity<?> export(@RequestParam ("customerId") final Integer customerId,
        @RequestParam (value = "projectId", required = false) final Integer projectId,
        @RequestParam (value = "time", required = false) final Integer time,
        @RequestParam (value = "type", required = false) final Integer type,
        @RequestParam (value = "fDate", required = false) final String fDate,
        @RequestParam (value = "tDate", required = false) final String tDate,
        @RequestParam (value = "ids", required = false) final String ids) {

        List<DataPowerResult> result = new ArrayList<>();

        String currentDay = DateUtils.toString(new Date(), "dd");
        String currentMonth = DateUtils.toString(new Date(), "MM");
        String currentYear = DateUtils.toString(new Date(), "yyyy");
        String day = DateUtils.toString(new Date(), Constants.ES.DATE_FORMAT_YMD);
        String month = DateUtils.toString(new Date(), Constants.ES.DATE_FORMAT_YM_02);
        String year = DateUtils.toString(new Date(), "yyyy");

        HashMap<String, String> condition = new HashMap<>();

        Integer systemTypeId = type;
        String reportName = "Dữ liệu năng lượng";
        String fromDate = fDate;
        String toDate = tDate;
        String schema = Schema.getSchemas(customerId);
        String projectNameExport = "";
        String fromTimeExport = "";
        String toTimeExport = "";
        String timeNameFile = "";
        if (fDate == null) {
            if (time == 0) {
                fromTimeExport = day + " " + "00:00:00";
                toTimeExport = day + " " + "23:59:59";
                timeNameFile = day;
            }
            if (time == 1) {
                fromTimeExport = month;
                toTimeExport = month;
                timeNameFile = month;
            }
            if (time == 2) {
                fromTimeExport = year;
                toTimeExport = year;
                timeNameFile = year;
            }
            if (time == 3) {
                fromTimeExport = year;
                toTimeExport = year;
                timeNameFile = year;
            }
        } else {
            fromTimeExport = fDate;
            if (tDate == null) {
                toTimeExport = fDate;
            } else {
                toTimeExport = tDate;
            }
            timeNameFile = fDate + toTimeExport;
        }

        // get Customer
        Map<String, String> cus = new HashMap<>();
        cus.put("customerId", customerId + "");
        Customer custtomer = customerService.getCustomer(cus);
        // systemType
        String moduleName = "";
        if (systemTypeId == 1) {
            moduleName = "TẢI ĐIỆN";
        } else if (systemTypeId == 2) {
            moduleName = "ĐIỆN MẶT TRỜI";
        } else if (systemTypeId == 3) {
            moduleName = "ĐIỆN GIÓ";
        } else if (systemTypeId == 4) {
            moduleName = "PIN LƯU TRỮ";
        } else if (systemTypeId == 5) {
            moduleName = "LƯỚI ĐIỆN";
        }
        // getProject
        Map<String, String> pro = new HashMap<>();
        pro.put("projectId", projectId + "");
        if (projectId == null) {
            projectNameExport = "";
        } else {
            Project projectExport = projectService.getProject(pro);
            projectNameExport = projectExport.getProjectName();
        }

        // get date now
        Date dateNow = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String strDate = formatter.format(dateNow);

        long miliseconds = new Date().getTime();
        String path = this.folderName + File.separator + "DLNL" + "_"
            + convertToCamelCase(custtomer.getCustomerName()).toUpperCase() + "_" + moduleName + "_"
            + convertToCamelCase(timeNameFile) + "_" + miliseconds;

        String fileNameExcel = "DLNL" + "_" + convertToCamelCase(custtomer.getCustomerName()).toUpperCase() + "_"
            + moduleName + "_" + convertToCamelCase(timeNameFile) + "_" + miliseconds;

        String proIds = "";
        if (projectId == null) {
            condition.put("customerId", String.valueOf(customerId));
            if (ids == "") {
                proIds = null;
            } else {
                proIds = ids;
            }
            List<Project> listProject = projectService.getListProjectByCustomerId(String.valueOf(customerId), proIds);

            for (Project project : listProject) {

                DataPowerResult obj = new DataPowerResult();
                obj.setName(project.getProjectName());
                List<DataPower> listPower = new ArrayList<>();
                condition.put("projectId", String.valueOf(project.getProjectId()));
                condition.put("schema", Schema.getSchemas(customerId));
                condition.put("day", day);
                condition.put("month", month);
                condition.put("year", year);

                if (time == 0) {
                    if (fDate != null) {
                        condition.put("day", fDate);
                        day = fDate;
                    }
                    if (type == 1) {
                        listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListDataPowerSolarByDay(condition);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerBatteruByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerWindByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListDataPowerGridByDay(condition);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }

                    List<DataPower> listPowerResult = new ArrayList<>();

                    String[] quarterHours = {"00", "15", "30", "45"};

                    for (int i = 0; i < 24; i++) {
                        for (int j = 0; j < 4; j++) {
                            DataPower data = new DataPower();
                            String time15Minute = i + ":" + quarterHours[j] + ":00";
                            if (i < 10) {
                                time15Minute = "0" + time15Minute;
                            }
                            data.setViewTime(day + " " + time15Minute);
                            data.setPower(0f);
                            listPowerResult.add(data);
                        }
                    }

                    for (DataPower dataR : listPowerResult) {

                        for (DataPower data : listPower) {
                            if (dataR.getViewTime()
                                .equals(data.getViewTime())) {
                                dataR.setPower(data.getPower());
                            }
                        }

                    }
                    obj.setListDataPower(listPowerResult);
                }

                if (time == 1) {
                    Boolean leapYear = false;
                    if (fDate != null) {
                        condition.put("month", fDate);
                        month = fDate;
                        Integer yearQ = Integer.valueOf(fDate.substring(0, 4));
                        if ( (yearQ % 4 == 0 && yearQ % 100 != 0) || yearQ % 400 == 0) {
                            leapYear = true;
                        }
                        String monthOfYear = fDate.substring(5);
                        switch (monthOfYear) {
                            case "01":
                            case "03":
                            case "05":
                            case "07":
                            case "08":
                            case "10":
                            case "12":
                                currentDay = String.valueOf(31);
                                break;
                            case "04":
                            case "06":
                            case "09":
                            case "11":
                                currentDay = String.valueOf(30);
                                break;
                            case "02":
                                if (leapYear) {
                                    currentDay = String.valueOf(29);
                                    break;
                                } else {
                                    currentDay = String.valueOf(28);
                                    break;
                                }
                        }
                    }

                    if (type == 1) {
                        listPower = deviceService.getListDataPowerLoadByMonth(condition);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListDataPowerSolarByMonth(condition);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerBatteruByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerWindByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListDataPowerGridByMonth(condition);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }

                    List<DataPower> listPowerResult = new ArrayList<>();

                    for (int i = 1; i <= Integer.valueOf(currentDay); i++) {
                        DataPower dataP = new DataPower();
                        String dayIn = "";
                        if (i < 10) {
                            dayIn = "0" + i;
                        } else {
                            dayIn = String.valueOf(i);
                        }
                        String dayOfMonth = month + "-" + dayIn;
                        dataP.setViewTime(dayOfMonth);
                        dataP.setPower(0f);
                        listPowerResult.add(dataP);
                    }

                    for (DataPower dataR : listPowerResult) {

                        for (DataPower data : listPower) {
                            if (dataR.getViewTime()
                                .equals(data.getViewTime())) {
                                dataR.setPower(data.getPower());
                            }
                        }

                    }

                    obj.setListDataPower(listPowerResult);
                }

                if (time == 2) {

                    if (fDate != null) {
                        condition.put("year", fDate);
                        year = fDate;
                    }
                    if (type == 1) {
                        listPower = deviceService.getListDataPowerLoadByYear(condition);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListDataPowerSolarByYear(condition);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerBatteruByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerWindByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListDataPowerGridByYear(condition);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }

                    List<DataPower> listPowerResult = new ArrayList<>();
                    for (int i = 1; i <= 12; i++) {
                        DataPower dataP = new DataPower();
                        String monthIn = "";
                        if (i < 10) {
                            monthIn = "0" + i;
                        } else {
                            monthIn = String.valueOf(i);
                        }
                        String monthOfYear = year + "-" + monthIn;
                        dataP.setViewTime(monthOfYear);
                        dataP.setPower(0f);
                        listPowerResult.add(dataP);
                    }

                    for (DataPower dataR : listPowerResult) {

                        for (DataPower data : listPower) {
                            if (dataR.getViewTime()
                                .equals(data.getViewTime())) {
                                dataR.setPower(data.getPower());
                            }
                        }

                    }

                    obj.setListDataPower(listPowerResult);
                }

                if (time == 3) {
                    if (type == 1) {
                        listPower = deviceService.getListDataPowerLoadAll(condition);
                        obj.setListDataPower(listPower);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListDataPowerSolarAll(condition);
                        obj.setListDataPower(listPower);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerBatteruByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerWindByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListDataPowerGridAll(condition);
                        obj.setListDataPower(listPower);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                }

                if (time == 4) {
                    fromDate = fDate;
                    toDate = tDate;
                    if (tDate != null) {
                        condition.put("fromDate", fromDate);
                        condition.put("toDate", toDate);
                        condition.put("energy", "energy");
                        condition.put("viewType", "3");
                    }
                    if (type == 1) {
                        listPower = deviceService.getListDataPowerLoadByDay(condition);
                        obj.setListDataPower(listPower);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListDataPowerSolarByDay(condition);
                        obj.setListDataPower(listPower);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerBatteruByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerWindByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListDataPowerGridByDay(condition);
                        obj.setListDataPower(listPower);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                }
                result.add(obj);
            }
        } else {
            condition.put("projectId", String.valueOf(projectId));
            condition.put("schema", Schema.getSchemas(customerId));
            condition.put("day", day);
            condition.put("month", month);
            condition.put("year", year);
            condition.put("calculateFlag", String.valueOf(1));
            condition.put("deleteFlag", String.valueOf(0));

            if (time == 0) {
                if (fDate != null) {
                    condition.put("day", fDate);
                    day = fDate;
                }
                condition.put("systemType", String.valueOf(type));
                List<Device> listDevice = deviceService.getDeviceByProjectId(condition);
                for (Device device : listDevice) {
                    DataPowerResult obj = new DataPowerResult();
                    condition.put("deviceId", String.valueOf(device.getDeviceId()));
                    List<DataPower> listPower = new ArrayList<>();
                    obj.setName(device.getDeviceName());
                    if (type == 1) {
                        listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListDataPowerSolarByDay(condition);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListDataPowerGridByDay(condition);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }

                    List<DataPower> listPowerResult = new ArrayList<>();

                    String[] quarterHours = {"00", "15", "30", "45"};

                    for (int i = 0; i < 24; i++) {
                        for (int j = 0; j < 4; j++) {
                            DataPower data = new DataPower();
                            String time15Minute = i + ":" + quarterHours[j] + ":00";
                            if (i < 10) {
                                time15Minute = "0" + time15Minute;
                            }
                            data.setViewTime(day + " " + time15Minute);
                            data.setPower(0f);
                            listPowerResult.add(data);
                        }
                    }

                    for (DataPower dataR : listPowerResult) {

                        for (DataPower data : listPower) {
                            if (dataR.getViewTime()
                                .equals(data.getViewTime())) {
                                dataR.setPower(data.getPower());
                            }
                        }

                    }
                    obj.setListDataPower(listPowerResult);

                    result.add(obj);
                }
            }
            if (time == 1) {
                Boolean leapYear = false;
                if (fDate != null) {
                    condition.put("month", fDate);
                    month = fDate;
                    Integer yearQ = Integer.valueOf(fDate.substring(0, 4));
                    if ( (yearQ % 4 == 0 && yearQ % 100 != 0) || yearQ % 400 == 0) {
                        leapYear = true;
                    }
                    String monthOfYear = fDate.substring(5);
                    switch (monthOfYear) {
                        case "01":
                        case "03":
                        case "05":
                        case "07":
                        case "08":
                        case "10":
                        case "12":
                            currentDay = String.valueOf(31);
                            break;
                        case "04":
                        case "06":
                        case "09":
                        case "11":
                            currentDay = String.valueOf(30);
                            break;
                        case "02":
                            if (leapYear) {
                                currentDay = String.valueOf(29);
                                break;
                            } else {
                                currentDay = String.valueOf(28);
                                break;
                            }
                    }
                }

                condition.put("systemType", String.valueOf(type));
                List<Device> listDevice = deviceService.getDeviceByProjectId(condition);
                for (Device device : listDevice) {
                    DataPowerResult obj = new DataPowerResult();
                    condition.put("deviceId", String.valueOf(device.getDeviceId()));
                    List<DataPower> listPower = new ArrayList<>();
                    obj.setName(device.getDeviceName());
                    if (type == 1) {
                        listPower = deviceService.getListDataPowerLoadByMonth(condition);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListDataPowerSolarByMonth(condition);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListDataPowerGridByMonth(condition);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }

                    List<DataPower> listPowerResult = new ArrayList<>();

                    for (int i = 1; i <= Integer.valueOf(currentDay); i++) {
                        DataPower dataP = new DataPower();
                        String dayIn = "";
                        if (i < 10) {
                            dayIn = "0" + i;
                        } else {
                            dayIn = String.valueOf(i);
                        }
                        String dayOfMonth = month + "-" + dayIn;
                        dataP.setViewTime(dayOfMonth);
                        dataP.setPower(0f);
                        listPowerResult.add(dataP);
                    }

                    for (DataPower dataR : listPowerResult) {

                        for (DataPower data : listPower) {
                            if (dataR.getViewTime()
                                .equals(data.getViewTime())) {
                                dataR.setPower(data.getPower());
                            }
                        }

                    }

                    obj.setListDataPower(listPowerResult);
                    result.add(obj);
                }
            }
            if (time == 2) {
                if (fDate != null) {
                    condition.put("year", fDate);
                    year = fDate;
                }
                condition.put("systemType", String.valueOf(type));
                List<Device> listDevice = deviceService.getDeviceByProjectId(condition);
                for (Device device : listDevice) {
                    DataPowerResult obj = new DataPowerResult();
                    condition.put("deviceId", String.valueOf(device.getDeviceId()));
                    List<DataPower> listPower = new ArrayList<>();
                    obj.setName(device.getDeviceName());
                    if (type == 1) {
                        listPower = deviceService.getListDataPowerLoadByYear(condition);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListDataPowerSolarByYear(condition);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListDataPowerGridByYear(condition);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }

                    List<DataPower> listPowerResult = new ArrayList<>();
                    for (int i = 1; i <= 12; i++) {
                        DataPower dataP = new DataPower();
                        String monthIn = "";
                        if (i < 10) {
                            monthIn = "0" + i;
                        } else {
                            monthIn = String.valueOf(i);
                        }
                        String monthOfYear = year + "-" + monthIn;
                        dataP.setViewTime(monthOfYear);
                        dataP.setPower(0f);
                        listPowerResult.add(dataP);
                    }

                    for (DataPower dataR : listPowerResult) {

                        for (DataPower data : listPower) {
                            if (dataR.getViewTime()
                                .equals(data.getViewTime())) {
                                dataR.setPower(data.getPower());
                            }
                        }

                    }

                    obj.setListDataPower(listPowerResult);
                    result.add(obj);
                }
            }
            if (time == 3) {
                condition.put("systemType", String.valueOf(type));
                List<Device> listDevice = deviceService.getDeviceByProjectId(condition);
                for (Device device : listDevice) {
                    DataPowerResult obj = new DataPowerResult();
                    condition.put("deviceId", String.valueOf(device.getDeviceId()));
                    List<DataPower> listPower = new ArrayList<>();
                    obj.setName(device.getDeviceName());
                    if (type == 1) {
                        listPower = deviceService.getListDataPowerLoadAll(condition);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListDataPowerSolarAll(condition);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListDataPowerGridAll(condition);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    obj.setListDataPower(listPower);
                    result.add(obj);
                }
            }
            if (time == 4) {
                fromDate = fDate;
                toDate = tDate;
                if (tDate != null) {
                    condition.put("fromDate", fromDate);
                    condition.put("toDate", toDate);
                    condition.put("energy", "enegy");
                }
                condition.put("viewType", "3");
                condition.put("systemType", String.valueOf(type));
                List<Device> listDevice = deviceService.getDeviceByProjectId(condition);

                for (Device device : listDevice) {
                    List<String> dateList = new ArrayList<>();
                    List<DataPower> listPowerResult = new ArrayList<>();
                    // Chuyển đổi chuỗi thành đối tượng LocalDate
                    LocalDate startDate = LocalDate.parse(fromDate);
                    LocalDate endDate = LocalDate.parse(toDate);

                    // Thêm ngày đầu vào danh sách
                    dateList.add(fDate);

                    // Lặp qua từng ngày tiếp theo cho đến ngày cuối
                    LocalDate currentDate = startDate;
                    // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    while (currentDate.isBefore(endDate)) {
                        currentDate = currentDate.plus(1, ChronoUnit.DAYS);
                        dateList.add(currentDate.format(formatter2));
                    }

                    for (String date : dateList) {
                        DataPower data = new DataPower();
                        data.setViewTime(date);
                        data.setPower(0f);
                        listPowerResult.add(data);
                    }
                    DataPowerResult obj = new DataPowerResult();
                    condition.put("deviceId", String.valueOf(device.getDeviceId()));
                    List<DataPower> listPower = new ArrayList<>();
                    obj.setName(device.getDeviceName());
                    if (type == 1) {
                        listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 2) {
                        listPower = deviceService.getListDataPowerSolarByDay(condition);
                    }
                    if (type == 3) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 4) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }
                    if (type == 5) {
                        listPower = deviceService.getListDataPowerGridByDay(condition);
                    }
                    if (type == 6) {
                        // listPower = deviceService.getListDataPowerLoadByDay(condition);
                    }


                    for (DataPower dataR : listPowerResult) {

                        for (DataPower data : listPower) {
                            if (dataR.getViewTime()
                                .equals(data.getViewTime())) {
                                dataR.setPower(data.getPower());
                            }
                        }

                    }


                    obj.setListDataPower(listPowerResult);
                    result.add(obj);
                }
            }
        }

        if (result.size() <= 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        try {
            createEnergyTotalExcel(result, custtomer.getCustomerName()
                .toUpperCase(), custtomer.getDescription(), time, reportName, systemTypeId, moduleName,
                projectNameExport, fromTimeExport, toTimeExport, strDate, path, fileNameExcel);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        log.info("ReportController.downloadReport() START");
        File f = new File(path);
        if (f.exists()) {
            log.info("ReportController.downloadReport() check file exists");
            String contentType = "application/zip";
            String headerValue = "attachment; filename=" + f.getName() + ".zip";
            Path realPath = Paths.get(path + ".zip");
            Resource resource = null;
            try {
                resource = new UrlResource(realPath.toUri());

            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("ReportController.downloadReport() END");
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .body(resource);

        } else {
            log.info("ReportController.downloadReport() error");
            return new ResponseEntity<Resource>(HttpStatus.BAD_REQUEST);
        }
    }

    private static String convertToCamelCase(String input) {
        // Xóa dấu và chuyển sang chữ thường
        String normalized = removeDiacriticalMarks(input).toLowerCase();

        // Chuyển đổi sang kiểu Camel Case
        StringBuilder camelCase = new StringBuilder();
        boolean capitalizeNext = false;
        for (char c : normalized.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                if (capitalizeNext) {
                    camelCase.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    camelCase.append(c);
                }

                // Nếu là khoảng trắng, đánh dấu để chuyển ký tự tiếp theo sang chữ in hoa
                if (c == ' ') {
                    capitalizeNext = true;
                }
            }
        }

        return camelCase.toString();
    }

    private static String removeDiacriticalMarks(String input) {
        // Loại bỏ dấu và chuyển về chữ thường
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String withoutDiacriticalMarks = pattern.matcher(normalized)
            .replaceAll("");

        // Loại bỏ dấu '-' và ':'
        return withoutDiacriticalMarks.replaceAll("[-:]", "");
    }

    private void formatBorder(final CellRangeAddress region, final Sheet sheet) {
        RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
        RegionUtil.setBorderTop(BorderStyle.MEDIUM, region, sheet);
        RegionUtil.setBorderLeft(BorderStyle.MEDIUM, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.MEDIUM, region, sheet);
    }

    private void createDataExcelTab6(final List<DataPowerResult> listData, String customerName, String description,
        Integer typeTime, String reportName, Integer systemTypeId, String moduleName, String siteName, String fromDate,
        String toDate, final String dateTime, final String path, final String fileNameExcel) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Dữ liệu tiền điện và năng lượng");
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        String unitTitle = "";
        if (typeTime == 0) {
            // cs.setDataFormat(format.getFormat("##0,000 [$kW]"));
            unitTitle = " (kW)";
        } else {
            // cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
            unitTitle = " (kWh)";
        }
        for (int z = 0; z < 2000; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 120; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        // set độ rộng của cột
        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        sheet1.setColumnWidth(5, 5000);

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 4) {
            cell.setCellValue(reportName.toUpperCase() + " THEO TỔNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Thành phần");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Dự án");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        region = new CellRangeAddress(9, 9, 0, 0);
        cell = sheet1.getRow(9)
            .getCell(0);
        cell.setCellValue("");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(12, 12, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(12)
            .getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(12, 12, 2, 2);
        cell1 = sheet1.getRow(12)
            .getCell(2);
        cell1.setCellValue("GIỜ THẤP ĐIỂM");

        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(12, 12, 3, 3);
        cell1 = sheet1.getRow(12)
            .getCell(3);
        cell1.setCellValue("GIỜ BÌNH THƯỜNG");

        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(12, 12, 4, 4);
        cell1 = sheet1.getRow(12)
            .getCell(4);
        cell1.setCellValue("GIỜ CAO ĐIỂM");

        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(12, 12, 5, 5);
        cell1 = sheet1.getRow(12)
            .getCell(5);
        cell1.setCellValue("TỔNG" + unitTitle);
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        int rowCount = 13;
        int count = 13;
        int countViewTime = 13;
        // Thông số load % tải báo cáo
        double sizeReport = listData.size();
        double progressDevice = 100 / sizeReport;
        double progress = progressDevice;
        Cell cellData;

        // Cột time
        for (Chart item : listData.get(0)
            .getListDataCost()) {
            final short bgColor;
            if (count % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            region = new CellRangeAddress(countViewTime, countViewTime + 1, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(countViewTime)
                .getCell(0);
            cellData.setCellValue(String.valueOf(item.getViewTime()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");
            formatBorder(region, sheet1);
            count += 1;
            countViewTime += 2;
        }
        ;

        int rowCountX = 13;
        for (Chart item : listData.get(0)
            .getListDataCost()) {
            final short bgColor;
            if (rowCountX % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            region = new CellRangeAddress(rowCountX, rowCountX, 2, 2);
            cellData = sheet1.getRow(rowCountX)
                .getCell(2);
            cellData.setCellValue(item.getCostLowIn() == null ? 0 : item.getCostLowIn());
            formatBorder(region, sheet1);
            formatExcelTotalDecimal(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "[$VNĐ]");
            rowCountX += 2;
        }

        rowCountX = 14;
        for (Chart item : listData.get(0)
            .getListDataCost()) {
            final short bgColor;
            if (rowCountX % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            region = new CellRangeAddress(rowCountX, rowCountX, 2, 2);
            cellData = sheet1.getRow(rowCountX)
                .getCell(2);
            cellData.setCellValue(item.getLowEp() == null ? 0 : item.getLowEp());
            formatBorder(region, sheet1);
            formatExcelTotalDecimal(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "[$kWh]");
            rowCountX += 2;
        }

        int rowCountY = 13;
        for (Chart item : listData.get(0)
            .getListDataCost()) {
            final short bgColor;
            if (rowCountY % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            region = new CellRangeAddress(rowCountY, rowCountY, 3, 3);
            cellData = sheet1.getRow(rowCountY)
                .getCell(3);
            cellData.setCellValue(item.getCostMediumIn() == null ? 0 : item.getCostMediumIn());
            formatBorder(region, sheet1);
            formatExcelTotalDecimal(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "[$VNĐ]");
            rowCountY += 2;
        }
        rowCountY = 14;
        for (Chart item : listData.get(0)
            .getListDataCost()) {
            final short bgColor;
            if (rowCountY % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            region = new CellRangeAddress(rowCountY, rowCountY, 3, 3);
            cellData = sheet1.getRow(rowCountY)
                .getCell(3);
            cellData.setCellValue(item.getNormalEp() == null ? 0 : item.getNormalEp());
            formatBorder(region, sheet1);
            formatExcelTotalDecimal(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "[$kWh]");
            rowCountY += 2;
        }

        int rowCountZ = 13;
        for (Chart item : listData.get(0)
            .getListDataCost()) {
            final short bgColor;
            if (rowCountZ % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            region = new CellRangeAddress(rowCountZ, rowCountZ, 4, 4);
            cellData = sheet1.getRow(rowCountZ)
                .getCell(4);
            cellData.setCellValue(item.getCostHighIn() == null ? 0 : item.getCostHighIn());
            formatBorder(region, sheet1);
            formatExcelTotalDecimal(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "[$VNĐ]");
            rowCountZ += 2;
        }
        rowCountZ = 14;
        for (Chart item : listData.get(0)
            .getListDataCost()) {
            final short bgColor;
            if (rowCountZ % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            region = new CellRangeAddress(rowCountZ, rowCountZ, 4, 4);
            cellData = sheet1.getRow(rowCountZ)
                .getCell(4);
            cellData.setCellValue(item.getHighEp() == null ? 0 : item.getHighEp());
            formatBorder(region, sheet1);
            formatExcelTotalDecimal(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "[$kWh]");
            rowCountZ += 2;
        }

        int rowCountF = 13;
        for (Chart item : listData.get(0)
            .getListDataCost()) {
            final short bgColor;
            if (rowCountF % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            region = new CellRangeAddress(rowCountF, rowCountF, 5, 5);
            cellData = sheet1.getRow(rowCountF)
                .getCell(5);
            cellData.setCellValue( (item.getCostHighIn() == null ? 0 : item.getCostHighIn())
                + (item.getCostMediumIn() == null ? 0 : item.getCostMediumIn())
                + (item.getCostLowIn() == null ? 0 : item.getCostLowIn()));
            formatBorder(region, sheet1);
            formatExcelTotalDecimal(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "[$VNĐ]");
            rowCountF += 2;
        }
        rowCountF = 14;
        for (Chart item : listData.get(0)
            .getListDataCost()) {
            final short bgColor;
            if (rowCountF % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            region = new CellRangeAddress(rowCountF, rowCountF, 5, 5);
            cellData = sheet1.getRow(rowCountF)
                .getCell(5);
            cellData.setCellValue( (item.getHighEp() == null ? 0 : item.getHighEp())
                + (item.getNormalEp() == null ? 0 : item.getNormalEp())
                + (item.getLowEp() == null ? 0 : item.getLowEp()));
            formatBorder(region, sheet1);
            formatExcelTotalDecimal(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "[$kWh]");
            rowCountF += 2;
        }

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export

        long url = new Date().getTime();
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";

        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        // convert pdf
        // Load the input Excel file
        // Workbook workbook = new Workbook();
        // workbook.loadFromFile(exportFilePath);
        // String pdf = path + File.separator + url + ".pdf";
        // Fit to page
        // workbook.getConverterSetting().setSheetFitToPage(true);

        // Save as PDF document
        // workbook.saveToFile(pdf);
        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void formatExcelTotalDecimal(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
        final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent, final String unit) {

        CellStyle cs = wb.createCellStyle();
        cs.setFillBackgroundColor(bgColor);
        cs.setFillForegroundColor(bgColor);
        cs.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);

        DataFormat format = wb.createDataFormat();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontName("Times New Roman");
        cs.setFont(font);
        // font.setColor(IndexedColors.DARK_BLUE.getIndex());
        cs.setAlignment(hAlign);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setIndention((short) indent);
        cs.setWrapText(true);
        cs.setDataFormat(format.getFormat("###,##0.00 " + unit));
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);
    }

    private void createEnergyTotalExcel(final List<DataPowerResult> listData, String customerName, String description,
        Integer typeTime, String reportName, Integer systemTypeId, String moduleName, String siteName, String fromDate,
        String toDate, final String dateTime, final String path, final String fileNameExcel) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Dữ liệu năng lượng");
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        String unitTitle = "";
        if (typeTime == 0) {
            cs.setDataFormat(format.getFormat("##0,000 [$kW]"));
            unitTitle = " (kW)";
        } else {
            cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
            unitTitle = " (kWh)";
        }
        for (int z = 0; z < 2000; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 120; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        // set độ rộng của cột
        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        sheet1.setColumnWidth(5, 5000);

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 0) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO TỔNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Thành phần");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Dự án");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        region = new CellRangeAddress(9, 9, 0, 0);
        cell = sheet1.getRow(9)
            .getCell(0);
        cell.setCellValue("");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(12, 12, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(12)
            .getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        for (int i = 0; i <= listData.size(); i++) {
            if (i != listData.size()) {
                DataPowerResult data = listData.get(i);
                region = new CellRangeAddress(12, 12, i + 2, i + 2);
                cell1 = sheet1.getRow(12)
                    .getCell(i + 2);
                cell1.setCellValue(data.getName() + unitTitle);

                formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
                    0);
            } else {
                region = new CellRangeAddress(12, 12, i + 2, i + 2);
                cell1 = sheet1.getRow(12)
                    .getCell(i + 2);
                cell1.setCellValue("TỔNG" + unitTitle);
                formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
                    0);
            }
        }

        int rowCount = 13;
        int count = 1;
        int countViewTime = 13;
        // Thông số load % tải báo cáo
        double sizeReport = listData.size();
        double progressDevice = 100 / sizeReport;
        double progress = progressDevice;
        Cell cellData;

        // Cột time
        for (DataPower item : listData.get(0)
            .getListDataPower()) {
            final short bgColor;
            if (countViewTime % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            region = new CellRangeAddress(countViewTime, countViewTime, 0, 1);
            sheet1.addMergedRegion(region);

            cellData = sheet1.getRow(countViewTime)
                .getCell(0);
            cellData.setCellValue(String.valueOf(item.getViewTime()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

            count += 1;
            countViewTime += 1;
        }
        ;

        for (int i = 0; i < listData.size(); i++) {
            int rowCountX = 13;

            for (DataPower item : listData.get(i)
                .getListDataPower()) {
                final short bgColor;
                if (rowCountX % 2 != 0) {
                    bgColor = IndexedColors.WHITE.getIndex();
                } else {
                    bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
                }

                region = new CellRangeAddress(rowCountX, rowCountX, 2 + i, 2 + i);
                cellData = sheet1.getRow(rowCountX)
                    .getCell(2 + i);
                cellData.setCellValue(item.getPower() == null ? 0 : item.getPower());
                formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");
                rowCountX += 1;
            }
        }

        List<DataPower> result = sumPower(listData);
        Integer rowTotal = listData.size() + 2;
        for (int i = 0; i < result.size(); i++) {
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            region = new CellRangeAddress(rowCount, rowCount, rowTotal, rowTotal);
            cellData = sheet1.getRow(rowCount)
                .getCell(rowTotal);
            cellData.setCellValue(result.get(i)
                .getPower() == null
                    ? 0
                    : result.get(i)
                        .getPower());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");
            rowCount += 1;
        }

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                        new CellRangeAddress(13, rowCount - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                        new CellRangeAddress(21, rowCount - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(21, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(21, rowCount - 1, 0, 0));
                    }
                }
            }
        }

        // set data point colors
        // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export

        long url = new Date().getTime();
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";

        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        // convert pdf
        // Load the input Excel file
        // Workbook workbook = new Workbook();
        // workbook.loadFromFile(exportFilePath);
        // String pdf = path + File.separator + url + ".pdf";
        // Fit to page
        // workbook.getConverterSetting().setSheetFitToPage(true);

        // Save as PDF document
        // workbook.saveToFile(pdf);
        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private static List<DataPower> sumPower(List<DataPowerResult> listDataResults) {
        List<DataPower> result = new ArrayList<>();

        for (DataPowerResult dataResult : listDataResults) {
            List<DataPower> dataList = dataResult.getListDataPower();

            for (int i = 0; i < dataList.size(); i++) {
                if (result.size() <= i) {
                    result.add(dataList.get(i));
                } else {
                    DataPower existingData = result.get(i);
                    float totalPower = existingData.getPower() + dataList.get(i)
                        .getPower();
                    existingData.setPower(totalPower);
                }
            }
        }

        return result;
    }

    private void formatExcelReport(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
        final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent) {

        CellStyle cs = wb.createCellStyle();

        DataFormat format = wb.createDataFormat();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.ORANGE.getIndex());
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 20);
        cs.setFont(font);
        cs.setAlignment(hAlign);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setIndention((short) indent);
        cs.setWrapText(true);
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);
    }

    private void formatExcelTableBody(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
        final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent, final String unit) {

        CellStyle cs = wb.createCellStyle();

        DataFormat format = wb.createDataFormat();
        cs.setFillBackgroundColor(bgColor);
        cs.setFillForegroundColor(bgColor);
        cs.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(hAlign);
        cs.setIndention((short) indent);
        cs.setDataFormat(format.getFormat("0.00 " + unit));
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setWrapText(true);
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);
    }

    private void formatExcelTotal(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
        final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent, final String unit) {

        CellStyle cs = wb.createCellStyle();
        cs.setFillBackgroundColor(bgColor);
        cs.setFillForegroundColor(bgColor);
        cs.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);

        DataFormat format = wb.createDataFormat();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontName("Times New Roman");
        cs.setFont(font);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        cs.setAlignment(hAlign);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setIndention((short) indent);
        cs.setWrapText(true);
        cs.setDataFormat(format.getFormat("###,000 " + unit));
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);
    }

    private void formatExcelTable(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
        final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent) {

        CellStyle cs = wb.createCellStyle();

        DataFormat format = wb.createDataFormat();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(hAlign);
        cs.setIndention((short) indent);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setWrapText(true);
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);
        RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
    }

    private void formatExcel(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet, final Cell cell,
        final short bgColor, final HorizontalAlignment hAlign, final int indent) {

        CellStyle cs = wb.createCellStyle();

        DataFormat format = wb.createDataFormat();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(hAlign);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setIndention((short) indent);
        cs.setWrapText(true);
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);
        cs.setDataFormat(format.getFormat("##0,##0"));
        // RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
        // RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
        // RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
        // RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
    }

    private void formatExcelFill(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
        final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent) {

        CellStyle cs = wb.createCellStyle();
        cs.setFillBackgroundColor(bgColor);
        cs.setFillForegroundColor(bgColor);
        cs.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);

        DataFormat format = wb.createDataFormat();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(hAlign);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setIndention((short) indent);
        cs.setWrapText(true);
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);

        // RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
        // RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
        // RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
        // RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
    }

    private void formatExcelCustomerName(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
        final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent) {

        CellStyle cs = wb.createCellStyle();

        DataFormat format = wb.createDataFormat();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 20);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(hAlign);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setIndention((short) indent);
        cs.setWrapText(true);
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);
    }

    private void formatExcelCustomerDescription(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
        final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent) {

        CellStyle cs = wb.createCellStyle();

        DataFormat format = wb.createDataFormat();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(hAlign);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setIndention((short) indent);
        cs.setWrapText(true);
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);
    }

    public double countDayOccurence(int year, int month, int dayToFindCount) {
        Calendar calendar = Calendar.getInstance();
        // Note that month is 0-based in calendar, bizarrely.
        calendar.set(year, month - 1, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        double count = 0;
        for (int day = 1; day <= daysInMonth; day++) {
            calendar.set(year, month - 1, day);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == dayToFindCount) {
                count++;
                // Or do whatever you need to with the result.
            }
        }
        return count;
    }
}
