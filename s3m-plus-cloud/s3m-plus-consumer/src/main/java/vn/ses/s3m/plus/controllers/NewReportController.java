package vn.ses.s3m.plus.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.spire.xls.Workbook;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.poi.ss.SpreadsheetVersion;
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
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xddf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xddf.usermodel.text.TextContainer;
import org.apache.poi.xddf.usermodel.text.XDDFTextBody;
import org.apache.poi.xssf.usermodel.*;
import org.etsi.uri.x01903.v13.IntegerListType;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCatAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLegend;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScaling;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.STAxPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarDir;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLegendPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.STOrientation;
import org.openxmlformats.schemas.drawingml.x2006.chart.STTickLblPos;
import org.openxmlformats.schemas.drawingml.x2006.main.STSchemeColorVal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zeroturnaround.zip.ZipUtil;

import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.*;
import vn.ses.s3m.plus.form.ReportForm;
import vn.ses.s3m.plus.form.SettingCostForm;
import vn.ses.s3m.plus.service.*;

@RestController
@RequestMapping("/common/report")
public class NewReportController {

    /**
     * Logging
     */
    private final Log log = LogFactory.getLog(NewReportController.class);

    @Autowired
    private ReportService reportService;

    @Autowired
    private ProjectService projectService;


    @Autowired
    private DeviceService deviceService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private SettingCostService settingCostService;

    @Autowired
    private LoadTypeService loadTypeService;

    @Autowired
    private ObjectService objectService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ReportQuantityPowerService reportQuantityPowerService;

    @Autowired
    private SettingShiftService settingShiftService;


    @Autowired
    private ManufactureShiftService manufactureShiftService;

    @Autowired
    private ShiftSettingService shiftSettingService;
    @Value("${consumer.producer.export-folder}")
    private String folderName;


    /**
     * tải file excel.
     *
     * @param typeInfor       loại báo cáo.
     * @param reportInfor     mẫu - tên báo cáo.
     * @param customerInfor   id - tên - miêu tả khách hàng.
     * @param devicesInfor    danh sách điểm đo.
     * @param timeInfor       loại thời gian - to - from.
     * @param siteModuleInfor site-module báo cáo.
     * @throws Exception
     */

    @GetMapping("/export")
    public ResponseEntity<?> export(
            @RequestParam("prefix") String prefixs,
            @RequestParam(value = "devices", required = false) String devices,
            @RequestParam("time") String time,
            @RequestParam(value = "loadType", required = false) String loadType
    ) throws ParseException {
        log.info("ReportController.export Start");
        long miliseconds = new Date().getTime();
        String path = this.folderName + File.separator + miliseconds;

        String[] prefix = prefixs.split("@");
        Integer customerId = Integer.parseInt(prefix[0]);
        Integer projectId = Integer.parseInt(prefix[1]);
        Integer systemTypeId = Integer.parseInt(prefix[2]);
        Integer type = Integer.parseInt(prefix[3]);
        Integer typeTime = Integer.parseInt(prefix[4]);
        String reportName = prefix[5];
        String[] date = time.split("@");
        String fromDate = date[0];
        String toDate = date[1];
        String schema = Schema.getSchemas(customerId);
        Map<String, Object> condition = new HashMap<>();
        condition.put("schema", schema);
        condition.put("systemTypeId", systemTypeId);
        condition.put("project", projectId);
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put("deviceId", devices);
        condition.put("typeTime", typeTime);

        // get Customer
        Map<String, String> cus = new HashMap<>();
        cus.put("customerId", customerId + "");
        Customer custtomer = customerService.getCustomer(cus);
        // systemType
        String moduleName = "";
        if (systemTypeId == 1) {
            moduleName = "LOAD";
        } else if (systemTypeId == 2) {
            moduleName = "SOLAR";
        } else if (systemTypeId == 3) {
            moduleName = "WIND";
        } else if (systemTypeId == 4) {
            moduleName = "BATTERY";
        } else if (systemTypeId == 5) {
            moduleName = "GRID";
        }
        // getProject
        Map<String, String> pro = new HashMap<>();
        pro.put("projectId", projectId + "");
        Project project = projectService.getProject(pro);

        // get date now
        Date dateNow = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("ddMMyyyy");
        String strDate = formatter.format(dateNow);
        String strDate2 = sdf2.format(dateNow);

        List<Device> listDevice = new ArrayList<>();
        // get devices
        String deviceNameList = "";
        if (type != 8) {
            if (!devices.equals(null) || !devices.equals("")) {
                Map<String, String> dev = new HashMap<>();
                dev.put("listIdDevice", devices);
                listDevice = deviceService.getDeviceListByListId(dev);
                for (int i = 0; i < listDevice.size(); i++) {
                    if (i == 0) {
                        deviceNameList = listDevice.get(0).getDeviceName();
                    } else {
                        deviceNameList += ", " + listDevice.get(i).getDeviceName();
                    }
                }
            }
        }

        String path1 = StringUtils.stripAccents(this.folderName + File.separator + miliseconds + reportName + "-" + custtomer.getCustomerName() + "-" + moduleName + "-" + strDate2);

        if (type == 1) {
            List<DataPqs> data = reportService.getEnergyTotal(condition);
            if (data.size() <= 0) {
                log.info("ReportController.export NO CONTENT");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            try {
                createEnergyTotalExcel(data, custtomer.getCustomerName().toUpperCase(), custtomer.getDescription(),
                        typeTime, reportName, systemTypeId, moduleName, project.getProjectName(), deviceNameList,
                        fromDate, toDate, strDate, path1);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (type == 2) {
            Map<String, Object> conditionShift = new HashMap<>();
            conditionShift.put("project", projectId);
            List<SettingShift> lsShift = settingShiftService.getSettingShiftByProject(conditionShift);

            List<SettingShiftEp> lsEP = new ArrayList<>();
            List<Timestamp> lsTime = getListDateByFromDateAndToDate(fromDate, toDate);
            for (Timestamp t : lsTime) {
                for (int i = 0; i < lsShift.size(); i++) {
                    SettingShiftEp shiftEp = getListShiftEp(t, lsShift.get(i), schema, devices, String.valueOf(projectId));
                    if (shiftEp != null) {
                        lsEP.add(shiftEp);
                    }
                }
            }

//            if (data.size() <= 0) {
//                log.info("ReportController.export NO CONTENT");
//                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//            }
            try {
                createEnergyTotalExcelByShift(lsTime, lsEP, lsShift, custtomer.getCustomerName().toUpperCase(), custtomer.getDescription(), typeTime, reportName, systemTypeId,
                        moduleName, project.getProjectName(), deviceNameList, fromDate, toDate,
                        strDate, path1);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (type == 3) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
            SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
            Map<String, Object> con = new HashMap<>();
            con.put("project", projectId);
            con.put("fromDate", fromDate);
            con.put("toDate", toDate);
            con.put("typeTime", typeTime);
            con.put("schema", schema);
            List<SettingCostHistory> lsHis = new ArrayList<>();
            List<DataPqs> lsData = new ArrayList<>();
            int index = 0;
            if (typeTime == 1) {
                lsHis = this.settingCostService.getListForReport1(con);
                Map<String, Object> cond = new HashMap<>();
                System.out.println(lsHis.toString());
                cond.put("schema", schema);
                if (lsHis.size() == 0) {
                    List<SettingCost> s = this.settingCostService.getListByProject(con);
                    SettingCostHistory a = new SettingCostHistory();
                    for (SettingCost set : s) {
                        if (set.getSettingCostMstId() == 1) {
                            a.setPeakHour(set.getSettingValue());
                        }
                        if (set.getSettingCostMstId() == 2) {
                            a.setNormalHour(set.getSettingValue());
                        }
                        if (set.getSettingCostMstId() == 3) {
                            a.setNonPeakHour(set.getSettingValue());
                        }
                        if (set.getSettingCostMstId() == 4) {
                            a.setVat(set.getSettingValue());
                        }
                        a.setFromDate(Timestamp.valueOf(fromDate + " 00:00:00"));
                        a.setToDate(Timestamp.valueOf(toDate + " 00:00:00"));
                    }
                    lsHis.add(a);
                } else {
                    if (lsHis.size() > 1 && lsHis.size() != 0) {
                        lsHis.get(lsHis.size() - 1).setToDate(Timestamp.valueOf(toDate + " 00:00:00"));
                    } else {
                        lsHis.get(0).setToDate(Timestamp.valueOf(toDate + " 00:00:00"));
                    }
                }
                for (SettingCostHistory s : lsHis) {
                    cond.put("toDate", dateFormat.format(s.getToDate() != null ? s.getToDate() : new Date()));
                    if (index > 0) {
                        cond.put("fromDate", dateFormat.format(s.getFromDate() != null ? addDays(s.getFromDate(), 1) : new Date()));
                    } else {
                        cond.put("fromDate", dateFormat.format(s.getFromDate() != null ? s.getFromDate() : new Date()));
                    }
                    cond.put("viewTime", typeTime);
                    cond.put("devices", devices);
                    DataPqs data = this.reportService.getCostForCycle(cond);
                    if (data != null) {
                        lsData.add(data);
                    }
                    index++;
                }
            }
            if (typeTime == 2) {
                Map<String, Object> cond = new HashMap<>();
                cond.put("schema", schema);
                lsHis = this.settingCostService.getListForReport1(con);
                if (lsHis.size() == 0) {
                    List<SettingCost> s = this.settingCostService.getListByProject(con);
                    SettingCostHistory a = new SettingCostHistory();
                    for (SettingCost set : s) {
                        if (set.getSettingCostMstId() == 1) {
                            a.setPeakHour(set.getSettingValue());
                        }
                        if (set.getSettingCostMstId() == 2) {
                            a.setNormalHour(set.getSettingValue());
                        }
                        if (set.getSettingCostMstId() == 3) {
                            a.setNonPeakHour(set.getSettingValue());
                        }
                        if (set.getSettingCostMstId() == 4) {
                            a.setVat(set.getSettingValue());
                        }
                        a.setFromDate(Timestamp.valueOf(fromDate + "-01 00:00:00"));
                        a.setToDate(Timestamp.valueOf(fromDate + "-31 00:00:00"));
                    }
                    lsHis.add(a);
                } else {
                    if (Timestamp.valueOf(fromDate + "-01 00:00:00").getTime() < lsHis.get(0).getFromDate().getTime()) {
                        lsHis.get(0).setFromDate(Timestamp.valueOf(fromDate + "-01 00:00:00"));
                    }
                    if (lsHis.get(lsHis.size() - 1).getToDate() != null) {
                        if (Timestamp.valueOf(toDate + "-31 23:59:00").getTime() > lsHis.get(lsHis.size() - 1).getToDate().getTime()) {
                            lsHis.get(lsHis.size() - 1).setToDate(Timestamp.valueOf(toDate + "-31 23:59:00"));
                        }
                    } else {
                        lsHis.get(lsHis.size() - 1).setToDate(Timestamp.valueOf(toDate + "-31 23:59:00"));
                    }
                }

                for (SettingCostHistory s : lsHis) {
                    cond.put("toDate", dateFormat.format(s.getToDate() != null ? s.getToDate() : new Date()));
                    if (index > 0) {
                        cond.put("fromDate", dateFormat.format(s.getFromDate() != null ? addDays(s.getFromDate(), 1) : new Date()));
                    } else {
                        cond.put("fromDate", dateFormat.format(s.getFromDate() != null ? s.getFromDate() : new Date()));
                    }
                    cond.put("devices", devices);
                    DataPqs data = this.reportService.getCostForCycle(cond);
                    if (data != null) {
                        lsData.add(data);
                    }
                    index++;
                }
            }
            if (lsData.size() <= 0) {
                log.info("ReportController.export NO CONTENT");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            try {
                createCostTotalExcelNew(lsData, lsHis, custtomer.getCustomerName().toUpperCase(), custtomer.getDescription(),
                        typeTime, reportName, systemTypeId, moduleName, project.getProjectName(), deviceNameList,
                        fromDate, toDate, strDate, path1);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (type == 4) {
            Map<String, Object> con4 = new HashMap<>();
            con4.put("schema", schema);
            con4.put("fromDate", fromDate);
            con4.put("toDate", toDate);
            con4.put("manufactureId", prefix[8]);
            con4.put("typeTime", typeTime);
            con4.put("devices", devices);
            List<ManufactureShiftDetail> data = this.manufactureShiftService.getReportManufacture(con4);
            List<DataPqs> lsEp = this.reportService.getEpByDevicesAndViewTime(con4);
            List<ManufactureShiftDetail> dataNew = new ArrayList<>();
            for (DataPqs ep : lsEp) {
                dataNew = EditEpByViewTime(ep, data, typeTime);
            }
            if (data.size() <= 0) {
                log.info("ReportController.export type 4 NO CONTENT");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            try {
                createStrengthTotalExcel(type, dataNew, custtomer.getCustomerName().toUpperCase(),
                        custtomer.getDescription(), typeTime, reportName, systemTypeId, moduleName,
                        project.getProjectName(), null, null, null, "", deviceNameList, fromDate, toDate, strDate,
                        path1, prefix[6], prefix[7], prefix[9]);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (type == 5) {
            String[] str = devices.split(",");
            List<Integer> deviceIds = new ArrayList<>();
            for (String a : str) {
                deviceIds.add(Integer.parseInt(a));
            }
            String a = "";
            for (int i = 0; i < deviceIds.size(); i++) {
                if (i == 0) {
                    a = deviceIds.get(0) + "";
                } else {
                    a += "," + deviceIds.get(i);
                }
            }
            condition.put("deviceIds", a);
            List<DataPqs> data = reportService.getWarningTotal(condition);
            if (data.size() <= 0) {
                log.info("ReportController.export NO CONTENT");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            try {
                createWarningTotalExcel(data, custtomer.getCustomerName().toUpperCase(), custtomer.getDescription(),
                        typeTime, reportName, systemTypeId, moduleName, project.getProjectName(), deviceNameList,
                        fromDate, toDate, strDate, path1, null, null, null, null,
                        null, devices, schema, projectId, deviceNameList);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (type == 6) {
            Map<String, String> dev1 = new HashMap<>();
            dev1.put("listIdDevice", devices);
            Map<String, Object> dev = new HashMap<>();
            String[] str = devices.split(",");
            List<Integer> deviceIds = new ArrayList<>();
            for (String a : str) {
                deviceIds.add(Integer.parseInt(a));
            }
            for (int i = 0; i < deviceIds.size(); i++) {
                dev.put("device" + (i + 1), deviceIds.get(i));
            }
            dev.put("fromDate", fromDate);
            dev.put("toDate", toDate);
            dev.put("typeTime", typeTime);
            dev.put("schema", schema);
            List<DataPqs> data = reportService.getUseEnergyCompare(dev);
            if (data.size() <= 0) {
                log.info("ReportController.export NO CONTENT");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            try {
                createUseEnergyCompare1(data, custtomer.getCustomerName().toUpperCase(), custtomer.getDescription(),
                        typeTime, reportName, systemTypeId, moduleName, project.getProjectName(), fromDate, toDate,
                        strDate, path1, listDevice, deviceIds.size(), deviceNameList);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (type == 7) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            Map<String, String> con = new HashMap<>();
            String[] ymd = String.valueOf(fromDate).split("-");
            con.put("schema", schema);
            con.put("device", devices);
            con.put("toDate", toDate);
            con.put("fromDate", fromDate);
            con.put("year", ymd[0]);
            con.put("typeTime", String.valueOf(typeTime));
            System.out.println(con.toString());
            List<ReportQuantityPower> ls = this.reportQuantityPowerService.getReportFrame1(con);
            List<ReportQuantityPower> ls2 = this.reportQuantityPowerService.getReportFrame2(con);
            System.out.println("ls: " + ls.size() + "ls: " + ls2.size());
            List<Setting> iEEE = this.reportQuantityPowerService.iEEELimit(con);
            String Vn = "";
            String In = "";
            String ThdVn = "";
            String ThdIn = "";
            for (Setting s : iEEE) {
                if (s.getWarningType() == 108) {
                    In = s.getSettingValue();
                }
                if (s.getWarningType() == 109) {
                    Vn = s.getSettingValue();
                }
                if (s.getWarningType() == 110) {
                    ThdVn = s.getSettingValue();
                }
                if (s.getWarningType() == 111) {
                    ThdIn = s.getSettingValue();
                }
            }
            if (ls.size() <= 0) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            try {
                createQuantityPowerExcel(ls, ls2, custtomer.getCustomerName().toUpperCase(), custtomer.getDescription()
                        , reportName, typeTime, moduleName, project.getProjectName(), deviceNameList,
                        fromDate, toDate, strDate, path1, Vn, In, ThdVn, ThdIn);
            } catch (Exception e) {
                log.info("EXPORT REPORT QUANTITY POWER TO ERROR");
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        if (type == 8) {
            Map<String, Object> con = new HashMap<>();
            con.put("schema", schema);
            con.put("fromDate", fromDate);
            con.put("toDate", toDate);
            String[] loadTypes = loadType.split(",");
            List<List<DataPqs>> lsTotal = new ArrayList<>();
            Map<String, Object> cond = new HashMap<>();
            cond.put("loadTypeIds", loadType);
            List<LoadType> lsLoadType = this.loadTypeService.getListLoadTypeByListId(cond);
            for (String s : loadTypes) {
                con.put("loadType", s);
                List<DataPqs> ls = this.reportService.getComparingEnergyUsageByLoadType(con);
                lsTotal.add(ls);
            }

            if (lsTotal.size() <= 0) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            try {
                createExcelComparingEnergyUsageByLoadType(lsTotal, lsLoadType, custtomer.getCustomerName().toUpperCase(), custtomer.getDescription(),
                        typeTime, reportName, systemTypeId, moduleName, project.getProjectName(), getNameListLoadType(lsLoadType),
                        fromDate, toDate, strDate, path1);
            } catch (Exception e) {
                log.info("EXPORT REPORT QUANTITY POWER TO ERROR");
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        log.info("ReportController.downloadReport() START");
        File f = new File(path1);
        log.info(f.getName());
        if (f.exists()) {
            log.info("ReportController.downloadReport() check file exists");
            String contentType = "application/zip";
            String headerValue = "attachment; filename=" + f.getName() + ".zip";
            Path realPath = Paths.get(path1 + ".zip");
            Resource resource = null;
            try {
                resource = new UrlResource(realPath.toUri());
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("ReportController.downloadReport() END");
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION).body(resource);

        } else {
            log.info("ReportController.downloadReport() not exists");
            log.info("ReportController.downloadReport() error");
            return new ResponseEntity<Resource>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Tạo excel báo cáo tổng hợp.
     *
     * @param data Thông tin báo cáo tổng hợp.
     * @throws Exception
     */
    private void createEnergyTotalExcel(final List<DataPqs> listData, String customerName, String description,
                                        Integer typeTime, String reportName, Integer systemTypeId, String moduleName, String siteName,
                                        String deviceNameList, String fromDate, String toDate, final String dateTime, final String path)
            throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Báo cáo tổng hợp");
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
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 100; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 100; j++) {
                row.createCell(j, CellType.BLANK).setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);
        row2 = sheet1.getRow(14);
        row2.setHeight((short) 2000);
        row2 = sheet1.getRow(15);
        row2.setHeight((short) 2000);
        row2 = sheet1.getRow(16);
        row2.setHeight((short) 2000);
        row2 = sheet1.getRow(17);
        row2.setHeight((short) 2000);

        // set độ rộng của cột
        for (int i = 0; i < 26; i++) {
            sheet1.setColumnWidth(i, 6000);
        }

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0).getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1).getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2).getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
                HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4).getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5).getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7).getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7).getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7).getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8).getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8).getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8).getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8).getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột điểm đo
        region = new CellRangeAddress(10, 10, 0, 0);
        cell = sheet1.getRow(10).getCell(0);
        cell.setCellValue("Điểm đo");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột danh sách điểm đo
        region = new CellRangeAddress(11, 11, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(11).getCell(0);
        cell.setCellValue(deviceNameList);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột giá trị null
        region = new CellRangeAddress(12, 12, 0, 0);
        cell = sheet1.getRow(12).getCell(0);
        cell.setCellValue("");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(20, 20, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(20).getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột giờ thấp điểm
        region = new CellRangeAddress(20, 20, 2, 2);
        cell1 = sheet1.getRow(20).getCell(2);
        cell1.setCellValue("GIỜ THẤP ĐIỂM(kWh)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột giờ bình thường
        region = new CellRangeAddress(20, 20, 3, 3);
        cell1 = sheet1.getRow(20).getCell(3);
        cell1.setCellValue("GIỜ BÌNH THƯỜNG(kWh)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột giờ cao điểm
        region = new CellRangeAddress(20, 20, 4, 4);
        cell1 = sheet1.getRow(20).getCell(4);
        cell1.setCellValue("GIỜ CAO ĐIỂM(kWh)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột tổng tiêu thụ
        region = new CellRangeAddress(20, 20, 5, 5);
        cell1 = sheet1.getRow(20).getCell(5);
        cell1.setCellValue("TỔNG TIÊU THỤ(kWh)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Ghi dữ liệu vào bảng của excel
        int rowCount = 21;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;
        // Thông số load % tải báo cáo
        double sizeReport = listData.size();
        double progressDevice = 100 / sizeReport;
        double progress = progressDevice;
        for (DataPqs item : listData) {
            float low_cost = 0, normal_cost = 0, high_cost = 0;
            // if (systemTypeId == 1) {
            // low_cost = item.getLowCostIn();
            // normal_cost = item.getNormalCostIn();
            // high_cost = item.getHighCostIn();
            // } else if (systemTypeId == 2) {
            // low_cost = item.getLowCostOut();
            // normal_cost = item.getNormalCostOut();
            // high_cost = item.getHighCostOut();
            // } else if (systemTypeId == 5) {
            // low_cost = item.getLowCostIn();
            // normal_cost = item.getNormalCostIn();
            // high_cost = item.getHighCostIn();
            // }
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            low_cost = item.getLowEp();
            normal_cost = item.getNormalEp();
            high_cost = item.getHighEp();
            Cell cellData;
            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount).getCell(0);
            cellData.setCellValue(item.getViewTime());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");
            // Cột thấp điểm
            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount).getCell(2);
            cellData.setCellValue(low_cost);
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");
            // Cột giờ bình thường
            region = new CellRangeAddress(rowCount, rowCount, 3, 3);
            cellData = sheet1.getRow(rowCount).getCell(3);
            cellData.setCellValue(normal_cost);
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");
            // Cột cao điểm
            region = new CellRangeAddress(rowCount, rowCount, 4, 4);
            cellData = sheet1.getRow(rowCount).getCell(4);
            cellData.setCellValue(high_cost);
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");
            // Cột tổng
            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount).getCell(5);
            cellData.setCellValue((low_cost > 0 ? low_cost : 0) + (normal_cost > 0 ? normal_cost : 0)
                    + (high_cost > 0 ? high_cost : 0));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            rowCount += 1;
            count += 1;
            lowTotal += low_cost;
            normalTotal += normal_cost;
            highTotal += high_cost;
            total += low_cost + normal_cost + high_cost;
        }
        ;

        // Cột TỔNG
        region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(rowCount).getCell(0);
        cell1.setCellValue("TỔNG");
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.CENTER, 0, "");
        // Cột tổng giờ thấp điểm
        region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        cell1 = sheet1.getRow(rowCount).getCell(2);
        cell1.setCellValue(lowTotal);
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.RIGHT, 1, "");
        // Cột tổng giờ bình thường
        region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        cell1 = sheet1.getRow(rowCount).getCell(3);
        cell1.setCellValue(normalTotal);
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.RIGHT, 1, "");
        // Cột tổng giờ cao điểm
        region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        cell1 = sheet1.getRow(rowCount).getCell(4);
        cell1.setCellValue(highTotal);
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.RIGHT, 1, "");

        // Cột tổng tiêu thụ
        region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        cell1 = sheet1.getRow(rowCount).getCell(5);
        cell1.setCellValue(total);
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.RIGHT, 1, "");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(21, rowCount - 1, 0, 0));
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

        // draw chart
        XDDFNumericalDataSource<Double> low = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                new CellRangeAddress(21, rowCount - 1, 2, 2));
        XDDFNumericalDataSource<Double> medium = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                new CellRangeAddress(21, rowCount - 1, 3, 3));
        XDDFNumericalDataSource<Double> high = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                new CellRangeAddress(21, rowCount - 1, 4, 4));

        XSSFDrawing drawing = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 13, 6, 19);

        XSSFChart chart = drawing.createChart(anchor);
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.BOTTOM);

        // bar chart

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Sử dụng năng lượng tổng(kWh)");
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        // category axis crosses the value axis between the strokes and not midpoint the
        // strokes
        leftAxis.setCrossBetween(AxisCrossBetween.BETWEEN);

        XDDFChartData data = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        XDDFChartData.Series series1 = data.addSeries(date, low);
        series1.setTitle("Giờ thấp điểm", new CellReference(sheet1.getSheetName(), 20, 2, true, true));
        XDDFChartData.Series series2 = data.addSeries(date, medium);
        series2.setTitle("Giờ bình thường", new CellReference(sheet1.getSheetName(), 20, 3, true, true));
        XDDFChartData.Series series3 = data.addSeries(date, high);
        series3.setTitle("Giờ cao điểm", new CellReference(sheet1.getSheetName(), 20, 4, true, true));
        chart.plot(data);

        XDDFBarChartData bar = (XDDFBarChartData) data;
        bar.setBarDirection(BarDirection.COL);

        // looking for "Stacked Bar Chart"? uncomment the following line
        bar.setBarGrouping(BarGrouping.STACKED);

        CTPlotArea plotArea = chart.getCTChart().getPlotArea();
        // plotArea
        // plotArea.getCatAxArray()[0].addNewMinorGridlines();
        plotArea.getValAxArray()[0].addNewMajorGridlines();

        // correcting the overlap so bars really are stacked and not side by side
        chart.getCTChart().getPlotArea().getBarChartArray(0).addNewOverlap().setVal((byte) 100);

        solidFillSeries(data, 0, PresetColor.LIGHT_GREEN);
        solidFillSeries(data, 1, PresetColor.GOLD);
        solidFillSeries(data, 2, PresetColor.LIGHT_CORAL);


        byte[][] colors = new byte[][]{new byte[]{(byte) 102, (byte) 205, 0},
                new byte[]{(byte) 102, (byte) 205, 0}, new byte[]{(byte) 102, (byte) 205, 0},
                new byte[]{(byte) 102, (byte) 205, 0}, new byte[]{(byte) 255, (byte) 255, 0},
                new byte[]{(byte) 255, (byte) 255, 0}, new byte[]{(byte) 255, (byte) 255, 0},
                new byte[]{(byte) 255, (byte) 255, 0}, new byte[]{(byte) 255, (byte) 255, 0},
                new byte[]{(byte) 255, 0, 0}, new byte[]{(byte) 255, 0, 0}, new byte[]{(byte) 255, 0, 0},
                new byte[]{(byte) 255, (byte) 255, 0}, new byte[]{(byte) 255, (byte) 255, 0},
                new byte[]{(byte) 255, (byte) 255, 0}, new byte[]{(byte) 255, (byte) 255, 0},
                new byte[]{(byte) 255, (byte) 255, 0}, new byte[]{(byte) 255, 0, 0},
                new byte[]{(byte) 255, 0, 0}, new byte[]{(byte) 255, 0, 0},
                new byte[]{(byte) 255, (byte) 255, 0}, new byte[]{(byte) 255, (byte) 255, 0},
                new byte[]{(byte) 102, (byte) 205, 0}, new byte[]{(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
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
        Workbook workbook = new Workbook();
        workbook.loadFromFile(exportFilePath);
        String pdf = path + File.separator + StringUtils.stripAccents(reportName) + ".pdf";
        // Fit to page
        workbook.getConverterSetting().setSheetFitToPage(true);

//		 Save as PDF document
        workbook.saveToFile(pdf);
        ZipUtil.pack(folder, new File(path + ".zip"));

    }


    // type = 7
    private void createEnergyTotalExcelByShiftAndObjectType(final List<DataPqs> listData, String customerName,
                                                            String description, Integer typeTime, String reportName, Integer systemTypeId, String moduleName,
                                                            String siteName, String objectTypeName, String shift1, String shift2, String shift3, String deviceNameList,
                                                            String fromDate, String toDate, final String dateTime, final String path) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Báo cáo tổng hợp");
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
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 100; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 100; j++) {
                row.createCell(j, CellType.BLANK).setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);
        row2 = sheet1.getRow(16);
        row2.setHeight((short) 2500);
        row2 = sheet1.getRow(17);
        row2.setHeight((short) 2500);
        row2 = sheet1.getRow(18);
        row2.setHeight((short) 2500);

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
        cell = sheet1.getRow(0).getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1).getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2).getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
                HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4).getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5).getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7).getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7).getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột ca làm việc 1
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7).getCell(2);
        cell.setCellValue("Ca làm việc 1");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột ca làm việc 2
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7).getCell(3);
        cell.setCellValue("Ca làm việc 2");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột ca làm việc 3
        region = new CellRangeAddress(7, 7, 4, 4);
        cell = sheet1.getRow(7).getCell(4);
        cell.setCellValue("Ca làm việc 3");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8).getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8).getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột thời gian ca làm 1
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8).getCell(2);
        cell.setCellValue(shift1);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột thời gian ca làm 2
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8).getCell(3);
        cell.setCellValue(shift2);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột thời gian ca làm 3
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8).getCell(4);
        cell.setCellValue(shift3);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột khu vực
        region = new CellRangeAddress(10, 10, 0, 0);
        cell = sheet1.getRow(10).getCell(0);
        cell.setCellValue("Loại thiết bị");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột thời gian
        region = new CellRangeAddress(10, 10, 3, 3);
        cell = sheet1.getRow(10).getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột giá trị khu vực
        region = new CellRangeAddress(11, 11, 0, 0);
        cell = sheet1.getRow(11).getCell(0);
        cell.setCellValue(objectTypeName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột fromDate
        region = new CellRangeAddress(11, 11, 3, 3);
        cell = sheet1.getRow(11).getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột toDate
        region = new CellRangeAddress(11, 11, 4, 4);
        cell = sheet1.getRow(11).getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột điểm đo
        region = new CellRangeAddress(13, 13, 0, 0);
        cell = sheet1.getRow(13).getCell(0);
        cell.setCellValue("Điểm đo");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột danh sách điểm đo
        region = new CellRangeAddress(14, 14, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(14).getCell(0);
        cell.setCellValue(deviceNameList);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(20, 20, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(20).getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột giờ thấp điểm
        region = new CellRangeAddress(20, 20, 2, 2);
        cell1 = sheet1.getRow(20).getCell(2);
        cell1.setCellValue("CA LÀM VIỆC 1");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột giờ bình thường
        region = new CellRangeAddress(20, 20, 3, 3);
        cell1 = sheet1.getRow(20).getCell(3);
        cell1.setCellValue("CA LÀM VIỆC 2");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột giờ cao điểm
        region = new CellRangeAddress(20, 20, 4, 4);
        cell1 = sheet1.getRow(20).getCell(4);
        cell1.setCellValue("CA LÀM VIỆC 3");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột tổng tiêu thụ
        region = new CellRangeAddress(20, 20, 5, 5);
        cell1 = sheet1.getRow(20).getCell(5);
        cell1.setCellValue("TỔNG TIÊU THỤ");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Ghi dữ liệu vào bảng của excel
        int rowCount = 21;
        int count = 1;
        float shift1Total = 0;
        float shift2Total = 0;
        float shift3Total = 0;
        float total = 0;
        // Thông số load % tải báo cáo
        double sizeReport = listData.size();
        double progressDevice = 100 / sizeReport;
        double progress = progressDevice;
        for (DataPqs item : listData) {
            float shif1 = 0, shif2 = 0, shif3 = 0;
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            shif1 = item.getValueShift1();
            shif2 = item.getValueShift2();
            shif3 = item.getValueShift3();
            Cell cellData;
            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount).getCell(0);
            cellData.setCellValue(item.getViewTime());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "[$kWh]");
            // Cột ca 1
            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount).getCell(2);
            cellData.setCellValue(shif1);
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "[$kWh]");
            // Cột ca 2
            region = new CellRangeAddress(rowCount, rowCount, 3, 3);
            cellData = sheet1.getRow(rowCount).getCell(3);
            cellData.setCellValue(shif2);
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "[$kWh]");
            // Cột ca 3
            region = new CellRangeAddress(rowCount, rowCount, 4, 4);
            cellData = sheet1.getRow(rowCount).getCell(4);
            cellData.setCellValue(shif3);
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "[$kWh]");
            // Cột tổng
            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount).getCell(5);
            cellData.setCellValue((shif1 > 0 ? shif1 : 0) + (shif2 > 0 ? shif2 : 0) + (shif3 > 0 ? shif3 : 0));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "[$kWh]");

            rowCount += 1;
            count += 1;
            shift1Total += shif1;
            shift2Total += shif2;
            shift3Total += shif3;
            total += shif1 + shif2 + shif3;
        }
        ;

        // Cột TỔNG
        region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(rowCount).getCell(0);
        cell1.setCellValue("TỔNG");
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.CENTER, 0, "[$kWh]");
        // Cột tổng ca làm 1
        region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        cell1 = sheet1.getRow(rowCount).getCell(2);
        cell1.setCellValue(shift1Total);
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.RIGHT, 1, "[$kWh]");
        // Cột tổng ca làm 2
        region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        cell1 = sheet1.getRow(rowCount).getCell(3);
        cell1.setCellValue(shift2Total);
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.RIGHT, 1, "[$kWh]");
        // Cột tổng ca làm 3
        region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        cell1 = sheet1.getRow(rowCount).getCell(4);
        cell1.setCellValue(shift3Total);
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.RIGHT, 1, "[$kWh]");

        // Cột tổng tiêu thụ
        region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        cell1 = sheet1.getRow(rowCount).getCell(5);
        cell1.setCellValue(total);
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.RIGHT, 1, "[$kWh]");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(21, rowCount - 1, 0, 0));
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

        // draw chart
        XDDFNumericalDataSource<Double> low = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                new CellRangeAddress(21, rowCount - 1, 2, 2));
        XDDFNumericalDataSource<Double> medium = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                new CellRangeAddress(21, rowCount - 1, 3, 3));
        XDDFNumericalDataSource<Double> high = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                new CellRangeAddress(21, rowCount - 1, 4, 4));

        XSSFDrawing drawing = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 16, 6, 19);

        XSSFChart chart = drawing.createChart(anchor);
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.BOTTOM);

        // bar chart

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Sử dụng năng lượng tổng");
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        // category axis crosses the value axis between the strokes and not midpoint the
        // strokes
        leftAxis.setCrossBetween(AxisCrossBetween.BETWEEN);

        XDDFChartData data = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        XDDFChartData.Series series1 = data.addSeries(date, low);
        series1.setTitle("Giờ thấp điểm", new CellReference(sheet1.getSheetName(), 20, 2, true, true));
        XDDFChartData.Series series2 = data.addSeries(date, medium);
        series2.setTitle("Giờ bình thường", new CellReference(sheet1.getSheetName(), 20, 3, true, true));
        XDDFChartData.Series series3 = data.addSeries(date, high);
        series3.setTitle("Giờ cao điểm", new CellReference(sheet1.getSheetName(), 20, 4, true, true));
        chart.plot(data);

        XDDFBarChartData bar = (XDDFBarChartData) data;
        bar.setBarDirection(BarDirection.COL);

        // looking for "Stacked Bar Chart"? uncomment the following line
        bar.setBarGrouping(BarGrouping.STACKED);

        CTPlotArea plotArea = chart.getCTChart().getPlotArea();
        // plotArea
        // plotArea.getCatAxArray()[0].addNewMinorGridlines();
        plotArea.getValAxArray()[0].addNewMajorGridlines();

        // correcting the overlap so bars really are stacked and not side by side
        chart.getCTChart().getPlotArea().getBarChartArray(0).addNewOverlap().setVal((byte) 100);

        solidFillSeries(data, 0, PresetColor.ROYAL_BLUE);
        solidFillSeries(data, 1, PresetColor.GOLD);
        solidFillSeries(data, 2, PresetColor.LIGHT_GREEN);

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        String exportFilePath = path + File.separator + url + ".xlsx";
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
        Workbook workbook = new Workbook();
        workbook.loadFromFile(exportFilePath);
        String pdf = path + File.separator + url + ".pdf";
        // Fit to page
        workbook.getConverterSetting().setSheetFitToPage(true);

        // Save as PDF document
        workbook.saveToFile(pdf);
        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    // type = 4
    private void createEnergyTotalExcelByShift(List<Timestamp> lsTime, List<SettingShiftEp> lsEp, List<SettingShift> lsShift, String customerName, String description,
                                               Integer typeTime, String reportName, Integer systemTypeId, String moduleName, String siteName, String deviceNameList, String fromDate, String toDate,
                                               final String dateTime, final String path) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Báo cáo tổng hợp");
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
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 100; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 100; j++) {
                row.createCell(j, CellType.BLANK).setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);
        row2 = sheet1.getRow(16);
        row2.setHeight((short) 2500);
        row2 = sheet1.getRow(17);
        row2.setHeight((short) 2500);
        row2 = sheet1.getRow(18);
        row2.setHeight((short) 2500);

        // set độ rộng của cột
        for (int i = 0; i < 26; i++) {
            sheet1.setColumnWidth(i, 6000);
        }


        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0).getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1).getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2).getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
                HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4).getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5).getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7).getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7).getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột ca làm việc 1
        int col = 0;
        for (SettingShift shif : lsShift) {
            region = new CellRangeAddress(7, 7, 2 + col, 2 + col);
            cell = sheet1.getRow(7).getCell(2 + col);
            cell.setCellValue(shif.getShiftName());
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

            region = new CellRangeAddress(8, 8, 2 + col, 2 + col);
            cell = sheet1.getRow(8).getCell(2 + col);
            cell.setCellValue("Từ: " + shif.getStartTime() + "\nĐến: " + shif.getEndTime());
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
            col++;

        }
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8).getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8).getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột thời gian ca làm 1
//        region = new CellRangeAddress(8, 8, 2, 2);
//        cell = sheet1.getRow(8).getCell(2);
//        cell.setCellValue(shift1);
//        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
//        // cột thời gian ca làm 2
//        region = new CellRangeAddress(8, 8, 3, 3);
//        cell = sheet1.getRow(8).getCell(3);
//        cell.setCellValue(shift2);
//        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
//        // cột thời gian ca làm 3
//        region = new CellRangeAddress(8, 8, 4, 4);
//        cell = sheet1.getRow(8).getCell(4);
//        cell.setCellValue(shift3);
//        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột điểm đo
        region = new CellRangeAddress(10, 10, 0, 0);
        cell = sheet1.getRow(10).getCell(0);
        cell.setCellValue("Điểm đo");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột danh sách điểm đo
        region = new CellRangeAddress(11, 11, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(11).getCell(0);
        cell.setCellValue(deviceNameList);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột thời gian
        region = new CellRangeAddress(13, 13, 0, 0);
        cell = sheet1.getRow(13).getCell(0);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột fromDate
        region = new CellRangeAddress(14, 14, 0, 0);
        cell = sheet1.getRow(14).getCell(0);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột toDate
        region = new CellRangeAddress(14, 14, 1, 1);
        cell = sheet1.getRow(14).getCell(1);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(20, 20, 0, 0);
        cell1 = sheet1.getRow(20).getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        formatExcelBorder(region, sheet1);
        // Cột giờ thấp điểm
        Double epByTime = 0.0;
        Double epByShiftTotal = 0.0;
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.ES.DATE_FORMAT_DMY);
        for (int j = 0; j < lsTime.size(); j++) {
            epByTime = 0.0;
            region = new CellRangeAddress(21 + j, 21 + j, 0, 0);
            cell = sheet1.getRow(21 + j).getCell(0);
            cell.setCellValue(sdf.format(lsTime.get(j)));
            formatExcelTable(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            formatExcelBorder(region, sheet1);
            SettingShiftEp ep = null;
            // Cột Ca làm việc
            for (int i = 0; i < lsShift.size(); i++) {
                ep = getSettingShifEp(lsTime.get(j), lsShift.get(i).getId(), lsEp);
                region = new CellRangeAddress(20, 20, 1, i + 1);
                cell = sheet1.getRow(20).getCell(1 + i);
                cell.setCellValue(lsShift.get(i).getShiftName());
                formatExcelTable(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
                formatExcelBorder(region, sheet1);
                //   Giá trị EP theo ca làm việc
                region = new CellRangeAddress(21 + j, 21 + j, 1, i + 1);
                cell = sheet1.getRow(21 + j).getCell(1 + i);
                cell.setCellValue(ep != null ? ep.getEpTotal() : 0);
                formatExcelTable(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
                formatExcelBorder(region, sheet1);
                epByTime += ep != null ? ep.getEpTotal() : 0.0;
            }
            region = new CellRangeAddress(21 + j, 21 + j, lsShift.size() + 1, lsShift.size() + 1);
            cell = sheet1.getRow(21 + j).getCell(lsShift.size() + 1);
            cell.setCellValue(epByTime);
            formatExcelTable(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            formatExcelBorder(region, sheet1);
            epByShiftTotal += ep != null ? ep.getEpTotal() : 0.0;
        }

        // Cột tổng tiêu thụ
        region = new CellRangeAddress(20, 20, lsShift.size() + 1, lsShift.size() + 1);
        cell1 = sheet1.getRow(20).getCell(lsShift.size() + 1);
        cell1.setCellValue("TỔNG TIÊU THỤ");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        formatExcelBorder(region, sheet1);


//         draw chart
        XDDFDataSource<String> date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                new CellRangeAddress(21, 21 + lsTime.size() - 1, 0, 0));
        List<XDDFNumericalDataSource<Double>> lsf = new ArrayList<>();
        for (int i = 0; i < lsShift.size(); i++) {
            lsf.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet1, new CellRangeAddress(21, 21 + lsTime.size() - 1, 1 + i, 1 + i)));
        }

        XSSFDrawing drawing = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 16, 6, 19);

        XSSFChart chart = drawing.createChart(anchor);
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.BOTTOM);

        // bar chart

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Sử dụng năng lượng tổng");
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        // category axis crosses the value axis between the strokes and not midpoint the
        // strokes
        leftAxis.setCrossBetween(AxisCrossBetween.BETWEEN);

        XDDFChartData data = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
//        XDDFChartData.Series series1 = data.addSeries(date, low);
        List<XDDFChartData.Series> lsSeri = new ArrayList<>();
        for (int i = 0; i < lsShift.size(); i++) {
            lsSeri.add(data.addSeries(date, lsf.get(i)));
        }
        for (int i = 0; i < lsShift.size(); i++) {
            lsSeri.get(i).setTitle("Giờ thấp điểm", new CellReference(sheet1.getSheetName(), 20, 1 + i, true, true));
//            lsSeri.get(i).setLineProperties(lsSeri.get(i).getShapeProperties().getLineProperties());
            // Đặt màu của đường biên
        }

        chart.plot(data);

        XDDFBarChartData bar = (XDDFBarChartData) data;
        bar.setBarDirection(BarDirection.COL);

        // looking for "Stacked Bar Chart"? uncomment the following line
        bar.setBarGrouping(BarGrouping.STACKED);

        CTPlotArea plotArea = chart.getCTChart().getPlotArea();
        // plotArea
        // plotArea.getCatAxArray()[0].addNewMinorGridlines();
        plotArea.getValAxArray()[0].addNewMajorGridlines();

        // correcting the overlap so bars really are stacked and not side by side
        chart.getCTChart().getPlotArea().getBarChartArray(0).addNewOverlap().setVal((byte) 100);

//        solidFillSeries(data, 0, PresetColor.ROYAL_BLUE);
//        solidFillSeries(data, 1, PresetColor.GOLD);
//        solidFillSeries(data, 2, PresetColor.LIGHT_GREEN);


        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
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
        Workbook workbook = new Workbook();
        workbook.loadFromFile(exportFilePath);
        String pdf = path + File.separator + StringUtils.stripAccents(reportName) + ".pdf";
        // Fit to page
        workbook.getConverterSetting().setSheetFitToPage(true);

        // Save as PDF document
        workbook.saveToFile(pdf);
        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    // type == 8

    /**
     * Tạo excel báo cáo tổng hợp.
     *
     * @param data Thông tin báo cáo tổng hợp.
     * @throws Exception
     */
    private void createCostTotalExcel(final List<DataPqs> listData, String customerName, String description,
                                      Integer typeTime, String reportName, Integer systemTypeId, String moduleName, String siteName,
                                      String deviceNameList, String fromDate, String toDate, final String dateTime, final String path, SettingCost vat)
            throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Báo cáo tổng hợp");
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
        cs.setDataFormat(format.getFormat("##0,000 [$VND]"));
        for (int z = 0; z < 100; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 100; j++) {
                row.createCell(j, CellType.BLANK).setCellStyle(cs);
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
        cell = sheet1.getRow(0).getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1).getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2).getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
                HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4).getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5).getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7).getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7).getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8).getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8).getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột điểm đo
        region = new CellRangeAddress(10, 10, 0, 0);
        cell = sheet1.getRow(10).getCell(0);
        cell.setCellValue("Điểm đo");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột danh sách điểm đo
        region = new CellRangeAddress(11, 11, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(11).getCell(0);
        cell.setCellValue(deviceNameList);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột giá trị null
        region = new CellRangeAddress(12, 12, 0, 0);
        cell = sheet1.getRow(12).getCell(0);
        cell.setCellValue("");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // format header
        region = new CellRangeAddress(13, 13, 0, 2);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(13).getCell(0);
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.CENTER, 0, "");
        // Cột tiêu thụ
        region = new CellRangeAddress(13, 13, 3, 3);
        cell1 = sheet1.getRow(13).getCell(3);
        cell1.setCellValue("TIÊU THỤ");
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.CENTER, 0, "");
        // Cột đơn giá
        region = new CellRangeAddress(13, 13, 4, 4);
        cell1 = sheet1.getRow(13).getCell(4);
        cell1.setCellValue("ĐƠN GIÁ");
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.CENTER, 0, "");
        // Cột chi phí
        region = new CellRangeAddress(13, 13, 5, 5);
        cell1 = sheet1.getRow(13).getCell(5);
        cell1.setCellValue("CHI PHÍ");
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.CENTER, 0, "");

        // Ghi dữ liệu vào bảng của excel
        int rowCount = 14;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;

        for (DataPqs item : listData) {
            row2 = sheet1.getRow(rowCount);
            row2.setHeight((short) 1500);
            float lastTime = 0.0f;
            if (item.getEpAtATime() != null) {
                lastTime = (item.getEpAtATime() > 0 ? item.getEpAtATime() : 0) + item.getEp();
            }
            NumberFormat myFormat = NumberFormat.getInstance();
            myFormat.setGroupingUsed(true);
            // Cột chỉ số đầu kỳ
            region = new CellRangeAddress(rowCount, rowCount, 0, 2);
            sheet1.addMergedRegion(region);
            cell1 = sheet1.getRow(rowCount).getCell(0);
            cell1.setCellValue("Điểm đo: " + item.getDeviceName() + " \n" + "Chỉ số đầu kỳ: "
                    + myFormat.format(item.getEpAtATime() != null ? item.getEpAtATime() : 0.f) + " kWh @ " + fromDate
                    + "\n" + "Chỉ số Cuối kỳ: " + myFormat.format(lastTime) + " kWh @ " + toDate);
            formatExcel(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
            lowTotal += item.getLowEp();
            normalTotal += item.getNormalEp();
            highTotal += item.getHighEp();
            rowCount++;
        }
        total += lowTotal + normalTotal + highTotal;
        Long id = listData.get(0).getDeviceId();
        Map<String, Object> condition = new HashMap<>();
        condition.put("device_id", id);
        Setting lowCost = new Setting();
        Setting normalCost = new Setting();
        Setting highCost = new Setting();
        if (moduleName.equalsIgnoreCase("LOAD")) {
            condition.put("setting_mst_id", Constants.settingCostEnergy.LOAD_LOW_COST_IN);
            lowCost = settingService.getSettingByDeviceId(condition);
            condition.put("setting_mst_id", Constants.settingCostEnergy.LOAD_MEDIUM_COST_IN);
            normalCost = settingService.getSettingByDeviceId(condition);
            condition.put("setting_mst_id", Constants.settingCostEnergy.LOAD_HIGH_COST_IN);
            highCost = settingService.getSettingByDeviceId(condition);
        } else if (moduleName.equalsIgnoreCase("SOLAR")) {
            condition.put("setting_mst_id", Constants.settingCostEnergy.SOLAR_LOW_COST_OUT);
            lowCost = settingService.getSettingByDeviceId(condition);
            condition.put("setting_mst_id", Constants.settingCostEnergy.SOLAR_MEDIUM_COST_OUT);
            normalCost = settingService.getSettingByDeviceId(condition);
            condition.put("setting_mst_id", Constants.settingCostEnergy.SOLAR_HIGH_COST_OUT);
            highCost = settingService.getSettingByDeviceId(condition);
        } else if (moduleName.equalsIgnoreCase("GRID")) {
            condition.put("setting_mst_id", Constants.settingCostEnergy.GRID_LOW_COST_IN);
            lowCost = settingService.getSettingByDeviceId(condition);
            condition.put("setting_mst_id", Constants.settingCostEnergy.GRID_MEDIUM_COST_IN);
            normalCost = settingService.getSettingByDeviceId(condition);
            condition.put("setting_mst_id", Constants.settingCostEnergy.GRID_HIGH_COST_IN);
            highCost = settingService.getSettingByDeviceId(condition);
        }

        // cột thấp điểm
        short bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
        region = new CellRangeAddress(rowCount, rowCount, 0, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(rowCount).getCell(0);
        cell.setCellValue("GIỜ THẤP ĐIỂM");
        formatExcelTableBody(wb, region, sheet1, cell, bgColor, HorizontalAlignment.RIGHT, 1, "");

        // cột giá trị thấp điểm tiêu thụ
        region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        cell = sheet1.getRow(rowCount).getCell(3);
        cell.setCellValue(lowTotal);
        formatExcelTableBody(wb, region, sheet1, cell, bgColor, HorizontalAlignment.RIGHT, 1, "[$kWh]");

        // cột đơn giá thấp điểm tiêu thụ
        region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        cell = sheet1.getRow(rowCount).getCell(4);
        cell.setCellValue(Float.parseFloat(lowCost.getSettingValue()));
        formatExcelTableBody(wb, region, sheet1, cell, bgColor, HorizontalAlignment.RIGHT, 1, "[$VND]");

        // cột chi phí thấp điểm tiêu thụ
        region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        cell = sheet1.getRow(rowCount).getCell(5);
        float unit = Float.parseFloat(lowCost.getSettingValue()) * lowTotal;
        cell.setCellValue(unit);
        formatExcelTableBody(wb, region, sheet1, cell, bgColor, HorizontalAlignment.RIGHT, 1, "[$VND]");
        rowCount++;

        // cột bình thường
        bgColor = IndexedColors.WHITE.getIndex();
        region = new CellRangeAddress(rowCount, rowCount, 0, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(rowCount).getCell(0);
        cell.setCellValue("GIỜ BÌNH THƯỜNG");
        formatExcelTableBody(wb, region, sheet1, cell, bgColor, HorizontalAlignment.RIGHT, 1, "");

        // cột giá trị bình thường tiêu thụ
        region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        cell = sheet1.getRow(rowCount).getCell(3);
        cell.setCellValue(normalTotal);
        formatExcelTableBody(wb, region, sheet1, cell, bgColor, HorizontalAlignment.RIGHT, 1, "[$kWh]");

        // cột đơn giá bình thường tiêu thụ
        region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        cell = sheet1.getRow(rowCount).getCell(4);
        cell.setCellValue(Float.parseFloat(normalCost.getSettingValue()));
        formatExcelTableBody(wb, region, sheet1, cell, bgColor, HorizontalAlignment.RIGHT, 1, "[$VND]");

        // cột chi phí bình thường tiêu thụ
        region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        cell = sheet1.getRow(rowCount).getCell(5);
        unit = Float.parseFloat(normalCost.getSettingValue()) * normalTotal;
        cell.setCellValue(unit);
        formatExcelTableBody(wb, region, sheet1, cell, bgColor, HorizontalAlignment.RIGHT, 1, "[$VND]");
        rowCount++;

        // cột cao điểm
        bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
        region = new CellRangeAddress(rowCount, rowCount, 0, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(rowCount).getCell(0);
        cell.setCellValue("GIỜ CAO ĐIỂM");
        formatExcelTableBody(wb, region, sheet1, cell, bgColor, HorizontalAlignment.RIGHT, 1, "");

        // cột giá trị cao điểm tiêu thụ
        region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        cell = sheet1.getRow(rowCount).getCell(3);
        cell.setCellValue(highTotal);
        formatExcelTableBody(wb, region, sheet1, cell, bgColor, HorizontalAlignment.RIGHT, 1, "[$kWh]");

        // cột đơn giá cao điểm tiêu thụ
        region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        cell = sheet1.getRow(rowCount).getCell(4);
        cell.setCellValue(Float.parseFloat(highCost.getSettingValue()));
        formatExcelTableBody(wb, region, sheet1, cell, bgColor, HorizontalAlignment.RIGHT, 1, "[$VND]");

        // cột chi phí cao điểm tiêu thụ
        region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        cell = sheet1.getRow(rowCount).getCell(5);
        unit = Float.parseFloat(highCost.getSettingValue()) * highTotal;
        cell.setCellValue(unit);
        formatExcelTableBody(wb, region, sheet1, cell, bgColor, HorizontalAlignment.RIGHT, 1, "[$VND]");
        rowCount++;

        // cột tổng
        bgColor = IndexedColors.WHITE.getIndex();
        region = new CellRangeAddress(rowCount, rowCount, 0, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(rowCount).getCell(0);
        cell.setCellValue("TỔNG LƯỢNG ĐIỆN NĂNG TIÊU THỤ");
        formatExcelTableBody(wb, region, sheet1, cell, bgColor, HorizontalAlignment.RIGHT, 1, "");

        // cột giá trị tổng
        region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        cell = sheet1.getRow(rowCount).getCell(3);
        cell.setCellValue(total);
        formatExcelTableBody(wb, region, sheet1, cell, bgColor, HorizontalAlignment.RIGHT, 1, "[$kWh]");

        region = new CellRangeAddress(13, rowCount, 0, 5);
        RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet1);
        RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet1);
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet1);
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet1);
        rowCount = rowCount + 2;
        int frame2 = rowCount;

        float lowTotalCost = Float.parseFloat(lowCost.getSettingValue()) * lowTotal;
        float normalTotalCost = Float.parseFloat(normalCost.getSettingValue()) * normalTotal;
        float highTotalCost = Float.parseFloat(highCost.getSettingValue()) * highTotal;
        // cột tổng phụ
        bgColor = IndexedColors.WHITE.getIndex();
        region = new CellRangeAddress(rowCount, rowCount, 0, 4);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(rowCount).getCell(0);
        cell.setCellValue("TIỀN ĐIỆN TRƯỚC THUẾ");
        formatExcelTableBody(wb, region, sheet1, cell, bgColor, HorizontalAlignment.RIGHT, 1, "");

        // cột giá trị tổng
        region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        cell = sheet1.getRow(rowCount).getCell(5);
        cell.setCellValue(lowTotalCost + normalTotalCost + highTotalCost);
        formatExcelTableBody(wb, region, sheet1, cell, bgColor, HorizontalAlignment.RIGHT, 1, "[$VND]");
        rowCount++;

        // cột thuế
        bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
        region = new CellRangeAddress(rowCount, rowCount, 0, 4);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(rowCount).getCell(0);
        cell.setCellValue("THUẾ GTGT 10%");
        formatExcelTableBody(wb, region, sheet1, cell, bgColor, HorizontalAlignment.RIGHT, 1, "");

        // cột giá trị thuế
        Double gtgt;
        if (vat != null) {
            gtgt = vat.getSettingValue();
        } else {
            gtgt = 0.0;
        }
        region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        cell = sheet1.getRow(rowCount).getCell(5);
        cell.setCellValue(gtgt);
        formatExcelTableBody(wb, region, sheet1, cell, bgColor, HorizontalAlignment.RIGHT, 1, "[$VND]");
        rowCount++;

        // cột TỔNG
        bgColor = IndexedColors.WHITE.getIndex();
        region = new CellRangeAddress(rowCount, rowCount, 0, 4);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(rowCount).getCell(0);
        cell.setCellValue("TIỀN ĐIỆN TỔNG");
        formatExcelTableBody(wb, region, sheet1, cell, bgColor, HorizontalAlignment.RIGHT, 1, "");

        // cột giá trị TỔNG
        region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        cell = sheet1.getRow(rowCount).getCell(5);
        cell.setCellValue(gtgt + lowTotalCost + normalTotalCost + highTotalCost);
        formatExcelTableBody(wb, region, sheet1, cell, bgColor, HorizontalAlignment.RIGHT, 1, "[$VND]");

        region = new CellRangeAddress(frame2, frame2 + 2, 0, 5);
        RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet1);
        RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet1);
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet1);
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet1);

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
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
        Workbook workbook = new Workbook();
        workbook.loadFromFile(exportFilePath);
        String pdf = path + File.separator + StringUtils.stripAccents(reportName) + ".pdf";
        // Fit to page
        workbook.getConverterSetting().setSheetFitToPage(true);

        // Save as PDF document
        workbook.saveToFile(pdf);
        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createExcelComparingEnergyUsageByLoadType(final List<List<DataPqs>> listData, List<LoadType> lsLoadType, String customerName, String description,
                                                           Integer typeTime, String reportName, Integer systemTypeId, String moduleName, String siteName,
                                                           String deviceNameList, String fromDate, String toDate, final String dateTime, final String path)
            throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Báo cáo tổng hợp");
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
        cs.setDataFormat(format.getFormat("##0,000 [$VND]"));
        for (int z = 0; z < 1000; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 1000; j++) {
                row.createCell(j, CellType.BLANK).setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        // set độ rộng của cột
        for (int i = 0; i < 26; i++) {
            sheet1.setColumnWidth(i, 6000);
        }

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0).getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1).getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2).getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
                HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4).getCell(0);
        cell.setCellValue(reportName.toUpperCase());
        formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5).getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7).getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7).getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        region = new CellRangeAddress(7, 7, 4, 5);
        cell = sheet1.getRow(7).getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8).getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8).getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
//        Cột trị thời gian

        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8).getCell(4);
        cell.setCellValue("Từ: " + sdf.format(Timestamp.valueOf(fromDate + " 00:00:00")));
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        region = new CellRangeAddress(8, 8, 5, 5);
        cell = sheet1.getRow(8).getCell(5);
        cell.setCellValue("Đến: " + sdf.format(Timestamp.valueOf(toDate + " 00:00:00")));
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột điểm đo
        region = new CellRangeAddress(10, 10, 0, 0);
        cell = sheet1.getRow(10).getCell(0);
        cell.setCellValue("Loại phụ tải");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột danh sách điểm đo
        region = new CellRangeAddress(11, 11, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(11).getCell(0);
        cell.setCellValue(deviceNameList);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột giá trị null
        region = new CellRangeAddress(12, 12, 0, 0);
        cell = sheet1.getRow(12).getCell(0);
        cell.setCellValue("");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);


        // Ghi dữ liệu vào bảng của excel
        int rowTotal = 14;

//        Cột thời gian
        region = new CellRangeAddress(13, 13, 0, 0);
        cell = sheet1.getRow(13).getCell(0);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        for (int i = 0; i < lsLoadType.size(); i++) {
            region = new CellRangeAddress(13, 13, i + 1, i + 1);
            cell = sheet1.getRow(13).getCell(i + 1);
            cell.setCellValue(lsLoadType.get(i).getLoadTypeName());
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        }

//       set giá trị cột thời gian
        for (int i = 0; i < listData.get(0).size(); i++) {
            region = new CellRangeAddress(14 + i, 14 + i, 0, 0);
            cell = sheet1.getRow(14 + i).getCell(0);
            cell.setCellValue(listData.get(0).get(i).getViewTime());
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            rowTotal++;
        }
        // set giá trị ep

        for (int i = 0; i < listData.size(); i++) {
            for (int j = 0; j < listData.get(i).size(); j++) {
                region = new CellRangeAddress(14 + j, 14 + j, 1 + i, 1 + i);
                cell = sheet1.getRow(14 + j).getCell(1 + i);
                cell.setCellValue(listData.get(i).get(j).getEp());
                formatExcel1(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            }
        }

        region = new CellRangeAddress(rowTotal, rowTotal, 0, 0);
        cell = sheet1.getRow(rowTotal).getCell(0);
        cell.setCellValue("Tổng");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        for (int i = 0; i < listData.size(); i++) {
            Double total = 0.0;
            for (int j = 0; j < listData.get(i).size(); j++) {
                total += listData.get(i).get(j).getEp();
            }
            region = new CellRangeAddress(rowTotal, rowTotal, 1 + i, 1 + i);
            cell = sheet1.getRow(rowTotal).getCell(1 + i);
            cell.setCellValue(total);
            formatExcel1(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        }

//        Vẽ biểu đồ
        XSSFDrawing drawing = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, rowTotal + 2, 6, rowTotal + 17);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Biểu đồ điện năng theo loại phụ tải");
        chart.setTitleOverlay(false);
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.RIGHT);

        XDDFDataSource<String> cat = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                new CellRangeAddress(13, 13, 1, listData.size()));

        XDDFNumericalDataSource<Double> val = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                new CellRangeAddress(rowTotal, rowTotal, 1, listData.size()));

        XDDFDoughnutChartData data = (XDDFDoughnutChartData) chart.createData(ChartTypes.DOUGHNUT, null, null);
        data.setVaryColors(true);
        XDDFChartData.Series series = data.addSeries(cat, val);
        chart.plot(data);
        if (chart.getCTChart().getAutoTitleDeleted() == null) chart.getCTChart().addNewAutoTitleDeleted();
        chart.getCTChart().getAutoTitleDeleted().setVal(false);

        // Data point colors; is necessary for showing data points in Calc
        // Add data labels
        if (!chart.getCTChart().getPlotArea().getDoughnutChartArray(0).getSerArray(0).isSetDLbls()) {
            chart.getCTChart().getPlotArea().getDoughnutChartArray(0).getSerArray(0).addNewDLbls();
        }
        chart.getCTChart().getPlotArea().getDoughnutChartArray(0).getSerArray(0).getDLbls().addNewShowVal().setVal(true);
        chart.getCTChart().getPlotArea().getDoughnutChartArray(0).getSerArray(0).getDLbls().addNewShowSerName().setVal(false);
        chart.getCTChart().getPlotArea().getDoughnutChartArray(0).getSerArray(0).getDLbls().addNewShowCatName().setVal(false);
        chart.getCTChart().getPlotArea().getDoughnutChartArray(0).getSerArray(0).getDLbls().addNewShowPercent().setVal(false);
        chart.getCTChart().getPlotArea().getDoughnutChartArray(0).getSerArray(0).getDLbls().addNewShowLegendKey().setVal(false);


        // chart area (chartspace) without border line
        chart.getCTChartSpace().addNewSpPr().addNewLn().addNewNoFill();

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
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
        Workbook workbook = new Workbook();
        workbook.loadFromFile(exportFilePath);
        String pdf = path + File.separator + StringUtils.stripAccents(reportName) + ".pdf";
        // Fit to page
        workbook.getConverterSetting().setSheetFitToPage(true);

        // Save as PDF document
        workbook.saveToFile(pdf);
        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createCostTotalExcelNew(final List<DataPqs> lsData, List<SettingCostHistory> lsPrice, String customerName, String description,
                                         Integer typeTime, String reportName, Integer systemTypeId, String moduleName, String siteName,
                                         String deviceNameList, String fromDate, String toDate, final String dateTime, final String path)
            throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Báo cáo tổng hợp");
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
        cs.setDataFormat(format.getFormat("##0,000 [$VND]"));
        for (int z = 0; z < 10000; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 100; j++) {
                row.createCell(j, CellType.BLANK);
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
        sheet1.setColumnWidth(6, 5000);

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0).getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1).getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2).getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
                HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4).getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5).getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7).getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7).getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8).getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8).getCell(1);
        cell.setCellValue(siteName);

        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột điểm đo
        region = new CellRangeAddress(10, 10, 0, 0);
        cell = sheet1.getRow(10).getCell(0);
        cell.setCellValue("Điểm đo");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột danh sách điểm đo
        region = new CellRangeAddress(11, 11, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(11).getCell(0);
        cell.setCellValue(deviceNameList);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột giá trị null
        region = new CellRangeAddress(12, 12, 0, 0);
        cell = sheet1.getRow(12).getCell(0);
        cell.setCellValue("");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // format header
        // Cột tiêu thụ
        region = new CellRangeAddress(13, 14, 0, 1);
        sheet1.addMergedRegion(region);

        cell1 = sheet1.getRow(13).getCell(0);
        cell1.setCellValue("KHOẢNG THỜI GIAN");
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.CENTER, 0, "");
        formatExcelBorder(region, sheet1);
        region = new CellRangeAddress(13, 14, 2, 2);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(13).getCell(2);
        cell1.setCellValue("");
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.CENTER, 0, "");
        formatExcelBorder(region, sheet1);
        region = new CellRangeAddress(13, 14, 3, 3);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(13).getCell(3);
        cell1.setCellValue("TIÊU THỤ(kWh)");
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.CENTER, 0, "");
        formatExcelBorder(region, sheet1);
        // Cột đơn giá
        region = new CellRangeAddress(13, 14, 4, 4);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(13).getCell(4);
        cell1.setCellValue("ĐƠN GIÁ(VND)");
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.CENTER, 0, "");
        formatExcelBorder(region, sheet1);
        // Cột chi phí
        region = new CellRangeAddress(13, 14, 5, 5);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(13).getCell(5);
        cell1.setCellValue("CHI PHÍ(VND)");
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.CENTER, 0, "");
        formatExcelBorder(region, sheet1);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Double ToalCost = 0.0;
        Double ToalCostSauThue = 0.0;
        Double totalVat = 0.0;

        int countRow = 15;
        if (lsData.size() > 0) {
            for (int i = 0; i < lsData.size(); i++) {
                region = new CellRangeAddress(countRow + i, countRow + i + 4, 0, 1);
                sheet1.addMergedRegion(region);
                cell1 = sheet1.getRow(countRow + i).getCell(0);
                if (i == 0) {
                    cell1.setCellValue("Từ: " + sdf.format(lsPrice.get(i).getFromDate()) + " \nĐến: " + sdf.format(lsPrice.get(i).getToDate()));
                } else {
                    cell1.setCellValue("Từ: " + sdf.format(addDays(lsPrice.get(i).getFromDate(), 1)) + " \nĐến: " + sdf.format(lsPrice.get(i).getToDate()));
                }
                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");
                formatExcelBorder(region, sheet1);

                region = new CellRangeAddress(countRow + i, countRow + i, 2, 2);
                cell1 = sheet1.getRow(countRow + i).getCell(2);
                cell1.setCellValue("Giờ thấp điểm");
                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");
                formatExcelBorder(region, sheet1);
                // Tiêu thụ
                region = new CellRangeAddress(countRow + i, countRow + i, 3, 3);
                cell1 = sheet1.getRow(countRow + i).getCell(3);
                cell1.setCellValue(lsData.get(i).getLowEp());
                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");
                formatExcelBorder(region, sheet1);
                // Giá
                region = new CellRangeAddress(countRow + i, countRow + i, 4, 4);
                cell1 = sheet1.getRow(countRow + i).getCell(4);
                cell1.setCellValue(lsPrice.get(i).getNonPeakHour());
                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");
                formatExcelBorder(region, sheet1);
                // Chi phí
                region = new CellRangeAddress(countRow + i, countRow + i, 5, 5);
                cell1 = sheet1.getRow(countRow + i).getCell(5);
                cell1.setCellValue(lsPrice.get(i).getNonPeakHour() * lsData.get(i).getLowEp());
                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");
                formatExcelBorder(region, sheet1);


                region = new CellRangeAddress(countRow + i + 1, countRow + i + 1, 2, 2);
                cell1 = sheet1.getRow(countRow + i + 1).getCell(2);
                cell1.setCellValue("Giờ bình thường");
                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");
                formatExcelBorder(region, sheet1);

                // Tiêu thụ
                region = new CellRangeAddress(countRow + i + 1, countRow + i + 1, 3, 3);
                cell1 = sheet1.getRow(countRow + i + 1).getCell(3);
                cell1.setCellValue(lsData.get(i).getNormalEp());
                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");
                formatExcelBorder(region, sheet1);
                // Giá
                region = new CellRangeAddress(countRow + i + 1, countRow + i + 1, 4, 4);
                cell1 = sheet1.getRow(countRow + i + 1).getCell(4);
                cell1.setCellValue(lsPrice.get(i).getNormalHour());
                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");
                formatExcelBorder(region, sheet1);
                // Chi phí
                region = new CellRangeAddress(countRow + i + 1, countRow + i + 1, 5, 5);
                cell1 = sheet1.getRow(countRow + i + 1).getCell(5);
                cell1.setCellValue(lsPrice.get(i).getNormalHour() * lsData.get(i).getNormalEp());
                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");
                formatExcelBorder(region, sheet1);


                region = new CellRangeAddress(countRow + i + 2, countRow + i + 2, 2, 2);
                cell1 = sheet1.getRow(countRow + i + 2).getCell(2);
                cell1.setCellValue("Giờ cao điểm");
                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");
                formatExcelBorder(region, sheet1);


                // Tiêu thụ
                region = new CellRangeAddress(countRow + i + 2, countRow + i + 2, 3, 3);
                cell1 = sheet1.getRow(countRow + i + 2).getCell(3);
                cell1.setCellValue(lsData.get(i).getHighEp());
                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");
                formatExcelBorder(region, sheet1);
                // Giá
                region = new CellRangeAddress(countRow + i + 2, countRow + i + 2, 4, 4);
                cell1 = sheet1.getRow(countRow + i + 2).getCell(4);
                cell1.setCellValue(lsPrice.get(i).getPeakHour());
                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");
                formatExcelBorder(region, sheet1);
                // Chi phí
                region = new CellRangeAddress(countRow + i + 2, countRow + i + 2, 5, 5);
                cell1 = sheet1.getRow(countRow + i + 2).getCell(5);
                cell1.setCellValue(lsPrice.get(i).getPeakHour() * lsData.get(i).getHighEp());
                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");
                formatExcelBorder(region, sheet1);


                region = new CellRangeAddress(countRow + i + 3, countRow + i + 3, 2, 2);
                cell1 = sheet1.getRow(countRow + i + 3).getCell(2);
                cell1.setCellValue("Tổng");
                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");
                formatExcelBorder(region, sheet1);
                // Tiêu thụ
                region = new CellRangeAddress(countRow + i + 3, countRow + i + 3, 3, 3);
                cell1 = sheet1.getRow(countRow + i + 3).getCell(3);
                cell1.setCellValue(lsData.get(i).getEp());
                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");
                formatExcelBorder(region, sheet1);

                region = new CellRangeAddress(countRow + i + 3, countRow + i + 3, 5, 5);
                cell1 = sheet1.getRow(countRow + i + 3).getCell(5);
                cell1.setCellValue(lsData.get(i).getLowEp() * lsPrice.get(i).getNonPeakHour() + lsData.get(i).getNormalEp() * lsPrice.get(i).getNormalHour() + lsData.get(i).getHighEp() * lsPrice.get(i).getPeakHour());
                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");
                formatExcelBorder(region, sheet1);

                // Giá
                region = new CellRangeAddress(countRow + i + 3, countRow + i + 3, 4, 4);
                cell1 = sheet1.getRow(countRow + i + 3).getCell(4);
                cell1.setCellValue("");
                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");
                formatExcelBorder(region, sheet1);

                region = new CellRangeAddress(countRow + i + 4, countRow + i + 4, 2, 2);
                cell1 = sheet1.getRow(countRow + i + 4).getCell(2);
                cell1.setCellValue("Thuế GTGT(%)");
                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");
                formatExcelBorder(region, sheet1);
//                Giá trị thuế
                region = new CellRangeAddress(countRow + i + 4, countRow + i + 4, 3, 3);
                cell1 = sheet1.getRow(countRow + i + 4).getCell(3);
                cell1.setCellValue("");
                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");
                formatExcelBorder(region, sheet1);
                region = new CellRangeAddress(countRow + i + 4, countRow + i + 4, 4, 4);
                cell1 = sheet1.getRow(countRow + i + 4).getCell(4);
                cell1.setCellValue(lsPrice.get(i).getVat() == null ? (0 + "%") : (lsPrice.get(i).getVat() + "%"));
                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");

                double vat = (lsData.get(i).getLowEp() * lsPrice.get(i).getNonPeakHour() + lsData.get(i).getNormalEp() * lsPrice.get(i).getNormalHour() + lsData.get(i).getHighEp() * lsPrice.get(i).getPeakHour()) * (lsPrice.get(i).getVat() == null || lsPrice.get(i).getVat() == 0 ? 1 : lsPrice.get(i).getVat() / 100);
                formatExcelBorder(region, sheet1);
                region = new CellRangeAddress(countRow + i + 4, countRow + i + 4, 5, 5);
                cell1 = sheet1.getRow(countRow + i + 4).getCell(5);
                cell1.setCellValue(vat);

                formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                        HorizontalAlignment.CENTER, 0, "");
                formatExcelBorder(region, sheet1);

                ToalCost += lsData.get(i).getLowEp() * lsPrice.get(i).getNonPeakHour() + lsData.get(i).getNormalEp() * lsPrice.get(i).getNormalHour() + lsData.get(i).getHighEp() * lsPrice.get(i).getPeakHour();
                totalVat += vat;
                countRow = countRow + 4;
            }
        }
        int rowTotal = 15 + (lsData.size() * 5);
        region = new CellRangeAddress(rowTotal, rowTotal + 1, 3, 4);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(rowTotal).getCell(3);
        cell1.setCellValue("Tổng tiền trước thuế:");
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                HorizontalAlignment.CENTER, 0, "");
        formatExcelBorder(region, sheet1);
//
        region = new CellRangeAddress(rowTotal, rowTotal + 1, 5, 5);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(rowTotal).getCell(5);
        cell1.setCellValue(ToalCost);
        formatExcelTotalDecimal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                HorizontalAlignment.CENTER, 0, "VND");
        formatExcelBorder(region, sheet1);

        region = new CellRangeAddress(rowTotal + 2, rowTotal + 3, 3, 4);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(rowTotal + 2).getCell(3);
        cell1.setCellValue("Tổng tiền sau thuế: ");
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                HorizontalAlignment.CENTER, 0, "");
        formatExcelBorder(region, sheet1);

        region = new CellRangeAddress(rowTotal + 2, rowTotal + 3, 5, 5);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(rowTotal + 2).getCell(5);

        ToalCostSauThue = ToalCost + totalVat;
        cell1.setCellValue(ToalCostSauThue);
        formatExcelTotalDecimal(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
                HorizontalAlignment.CENTER, 0, "VND");
        formatExcelBorder(region, sheet1);


        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
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
        Workbook workbook = new Workbook();
        workbook.loadFromFile(exportFilePath);
        String pdf = path + File.separator + StringUtils.stripAccents(reportName) + ".pdf";
        // Fit to page
        workbook.getConverterSetting().setSheetFitToPage(true);

        // Save as PDF document
        workbook.saveToFile(pdf);
        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    // type = 9 Báo cáo tiền điện theo khu vực
    private void createStrengthTotalExcel(Integer typeInfor, final List<ManufactureShiftDetail> listData, String customerName,
                                          String description, Integer typeTime, String reportName, Integer systemTypeId, String moduleName,
                                          String siteName, Integer unitId, String unitName, Float enB, String another, String deviceNameList,
                                          String fromDate, String toDate, final String dateTime, final String path, String sp, String step, String intensity) {
        log.info("NewReportController.createEnergyTotalExcel(): START");
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Báo cáo tổng hợp");
        Row row;
        Cell cell;
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        for (int z = 0; z < 100; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 100; j++) {
                row.createCell(j, CellType.BLANK).setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);
        row2 = sheet1.getRow(16);
        row2.setHeight((short) 3000);
        row2 = sheet1.getRow(17);
        row2.setHeight((short) 3000);

        // set độ rộng của cột
        for (int i = 0; i < 26; i++) {
            sheet1.setColumnWidth(i, 6000);
        }

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0).getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1).getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2).getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
                HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4).getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5).getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7).getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7).getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
//		// Cột đơn vị
//		region = new CellRangeAddress(7, 7, 3, 3);
//		cell = sheet1.getRow(7).getCell(3);
//		cell.setCellValue("Đơn vị");
//		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8).getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8).getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị đơn vị
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8).getCell(3);
        cell.setCellValue(unitName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        int rowExcel = 8;
        rowExcel += 2;

        // cột thời gian
        region = new CellRangeAddress(rowExcel, rowExcel, 0, 0);
        cell = sheet1.getRow(rowExcel).getCell(0);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột sản phẩm
        region = new CellRangeAddress(rowExcel, rowExcel, 2, 2);
        cell = sheet1.getRow(rowExcel).getCell(2);
        cell.setCellValue("Sản phẩm");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột Công đoạn sản xuất
        region = new CellRangeAddress(rowExcel, rowExcel, 3, 3);
        cell = sheet1.getRow(rowExcel).getCell(3);
        cell.setCellValue("Công đoạn sản suất");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        if (typeInfor == 18) {
            // cột khu vực
            region = new CellRangeAddress(rowExcel, rowExcel, 3, 3);
            cell = sheet1.getRow(rowExcel).getCell(3);
            cell.setCellValue("Khu vực");
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        }

        if (typeInfor == 19) {
            // cột khu vực
            region = new CellRangeAddress(rowExcel, rowExcel, 3, 3);
            cell = sheet1.getRow(rowExcel).getCell(3);
            cell.setCellValue("Loại phụ tải");
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        }

        if (typeInfor == 20) {
            // cột khu vực
            region = new CellRangeAddress(rowExcel, rowExcel, 3, 3);
            cell = sheet1.getRow(rowExcel).getCell(3);
            cell.setCellValue("Loại thiết bị");
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        }
        // cột cường độ cơ sở
        region = new CellRangeAddress(rowExcel, rowExcel, 5, 5);
        cell = sheet1.getRow(rowExcel).getCell(5);
        cell.setCellValue("Cường độ cơ sở (EnB)");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        rowExcel++;

        // cột giá trị thời gian từ
        region = new CellRangeAddress(rowExcel, rowExcel, 0, 0);
        cell = sheet1.getRow(rowExcel).getCell(0);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột giá trị thời gian đến
        region = new CellRangeAddress(rowExcel, rowExcel, 1, 1);
        cell = sheet1.getRow(rowExcel).getCell(1);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị sanr phẩm
        region = new CellRangeAddress(rowExcel, rowExcel, 2, 2);
        cell = sheet1.getRow(rowExcel).getCell(2);
        cell.setCellValue(sp);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị công doạn sản xuất
        region = new CellRangeAddress(rowExcel, rowExcel, 3, 3);
        cell = sheet1.getRow(rowExcel).getCell(3);
        cell.setCellValue(step);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        if (typeInfor == 18 || typeInfor == 19 || typeInfor == 20) {
            region = new CellRangeAddress(rowExcel, rowExcel, 3, 3);
            cell = sheet1.getRow(rowExcel).getCell(3);
            cell.setCellValue(another);
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        }
        // cột giá trị cường độ cơ sở
        region = new CellRangeAddress(rowExcel, rowExcel, 5, 5);
        cell = sheet1.getRow(rowExcel).getCell(5);
        cell.setCellValue(intensity);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        rowExcel += 2;

        // cột tên điểm đo
        region = new CellRangeAddress(rowExcel, rowExcel, 0, 0);
        cell = sheet1.getRow(rowExcel).getCell(0);
        cell.setCellValue("Tên điểm đo:");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        rowExcel++;

        // cột danh sách điểm đo
        region = new CellRangeAddress(rowExcel, rowExcel, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(rowExcel).getCell(0);
        cell.setCellValue(deviceNameList);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        rowExcel += 2;

        int rowChart = rowExcel;
        int rowTable = rowExcel + 10;

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(rowTable, rowTable, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(rowTable).getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột tổng tiêu thụ
        region = new CellRangeAddress(rowTable, rowTable, 2, 2);
        cell1 = sheet1.getRow(rowTable).getCell(2);
        cell1.setCellValue("TỔNG TIÊU THỤ(kWh)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột số lượng đơn vị
        region = new CellRangeAddress(rowTable, rowTable, 3, 3);
        cell1 = sheet1.getRow(rowTable).getCell(3);
        cell1.setCellValue("SỐ LƯỢNG ĐƠN VỊ");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột cường độ sử dụng
        region = new CellRangeAddress(rowTable, rowTable, 4, 5);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(rowTable).getCell(4);
        cell1.setCellValue("CƯỜNG ĐỘ SỬ DỤNG (EnPI)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        rowTable++;

        // Ghi dữ liệu vào bảng của excel
        int rowCount = 27;
        Double kwhTotal = 0.0;
        Integer unitTotal = 0;
        Double enPITotal = 0.0;
        float total = 0;

        SimpleDateFormat sdf1 = new SimpleDateFormat(Constants.ES.DATE_FORMAT_DMY);
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM-yyyy");
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy");
        for (ManufactureShiftDetail item : listData) {
            Double kwh = 0.0, enPI = 0.0;
            Integer unit = 0;
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }
            kwh = item.getEpTotal() == null ? 0 : item.getEpTotal();
            unit = item.getProductionNumber() == null ? 0 : item.getProductionNumber();
            enPI = unit > 0 ? kwh / unit : 0;
            Cell cellData;
            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount).getCell(0);
            cellData.setCellValue(typeTime == 1 ? sdf1.format(item.getViewTime()) : typeTime == 2 ? sdf2.format(item.getViewTime()) : sdf3.format(item.getViewTime()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");
            // Cột tổng tiêu thụ
            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount).getCell(2);
            cellData.setCellValue(item.getEpTotal());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");
            // Cột số lượng đơn vị
            region = new CellRangeAddress(rowCount, rowCount, 3, 3);
            cellData = sheet1.getRow(rowCount).getCell(3);
            cellData.setCellValue(item.getProductionNumber() != null ? item.getProductionNumber() : 0);
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");
            // Cột cường độ EnPI
            region = new CellRangeAddress(rowCount, rowCount, 4, 5);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount).getCell(4);
            cellData.setCellValue(enPI);
            cs = wb.createCellStyle();
            formatExcelTableBodyDecimal(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1,
                    "");
            rowCount += 1;
            kwhTotal += kwh >= 0 ? kwh : 0;
            unitTotal += unit >= 0 ? unit : 0;
            enPITotal += enPI >= 0 ? enPI : 0;

        }
        if (listData.size() > 0) {
            enPITotal = enPITotal / listData.size();
        }


        // Cột TỔNG
        region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(rowCount).getCell(0);
        cell1.setCellValue("TỔNG");
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.CENTER, 0, "[$kWh]");
        // Cột tổng tiêu thụ
        region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        cell1 = sheet1.getRow(rowCount).getCell(2);
        cell1.setCellValue(kwhTotal);
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.RIGHT, 1, "[$kWh]");
        // Cột tổng đơn vị
        region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        cell1 = sheet1.getRow(rowCount).getCell(3);
        cell1.setCellValue(unitTotal);
        formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.RIGHT, 1, "[$Don vi]");
        // Cột tổng EnPI

        region = new CellRangeAddress(rowCount, rowCount, 4, 5);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(rowCount).getCell(4);
        cell1.setCellValue(enPITotal);
        formatExcelTotalDecimal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
                HorizontalAlignment.RIGHT, 1, "[$kWh/Don vi]");
//         draw chart
        XDDFNumericalDataSource<Double> totalEp = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                new CellRangeAddress(27, rowCount - 1, 2, 2));
        XDDFNumericalDataSource<Double> enpi = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                new CellRangeAddress(27, rowCount - 1, 4, 4));

        XDDFDataSource<String> date = XDDFDataSourcesFactory.fromStringCellRange(sheet1, new CellRangeAddress(27, rowCount - 1, 0, 0));

        XSSFDrawing drawing = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, rowChart, 6, rowChart + 9);

        XSSFChart chart = drawing.createChart(anchor);
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.BOTTOM);
//
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        XDDFValueAxis rightAxis = chart.createValueAxis(AxisPosition.RIGHT);
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        leftAxis.setCrossBetween(AxisCrossBetween.BETWEEN);

        XDDFChartData data = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        XDDFChartData.Series series1 = data.addSeries(date, totalEp);
        series1.setTitle("Tổng tiêu thụ", new CellReference(sheet1.getSheetName(), rowChart + 10, 2, true, true));
        chart.plot(data);

        XDDFBarChartData bar = (XDDFBarChartData) data;
        bar.setBarDirection(BarDirection.COL);

//         looking for "Stacked Bar Chart"? uncomment the following line

        CTPlotArea plotArea = chart.getCTChart().getPlotArea();
        // plotArea
        // plotArea.getCatAxArray()[0].addNewMinorGridlines();
        plotArea.getValAxArray()[0].addNewMajorGridlines();

        // correcting the overlap so bars really are stacked and not side by side
        chart.getCTChart().getPlotArea().getBarChartArray(0).addNewOverlap().setVal((byte) 100);

        solidFillSeries(data, 0, PresetColor.DARK_BLUE);

        rightAxis.setCrosses(AxisCrosses.MAX);
        rightAxis.crossAxis(bottomAxis);
        XDDFLineChartData data1 = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, rightAxis);
        XDDFLineChartData.Series series = (XDDFLineChartData.Series) data1.addSeries(date, enpi);
        series.setTitle("Xuất tiêu hao", null);
        series.setSmooth(false);
        series.setMarkerStyle(MarkerStyle.STAR);
        chart.plot(data1);

        solidLineSeries(data1, 0, PresetColor.DARK_GREEN);

        solidLineSeries(data1, 0, PresetColor.ORANGE);
        chart.getCTChart().getPlotArea().getLineChartArray(0).getSerArray(0).getIdx().setVal(3);
        chart.getCTChart().getPlotArea().getLineChartArray(0).getSerArray(0).getOrder().setVal(3);

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
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
//        log.info("NewReportContorlle.export() create file pdf");
        // convert pdf
        // Load the input Excel file
//        Workbook workbook = new Workbook();
//        workbook.loadFromFile(exportFilePath);
//        String pdf = path + File.separator + StringUtils.stripAccents(reportName) + ".pdf";
//        // Fit to page
//        workbook.getConverterSetting().setSheetFitToPage(true);
//
//        // Save as PDF document
//        workbook.saveToFile(pdf);
        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createUseEnergyCompare1(final List<DataPqs> listData, String customerName, String description,
                                         Integer typeTime, String reportName, Integer systemTypeId, String moduleName,
                                         String siteName, String fromDate, String toDate, final String dateTime, final String path, List<Device> ls, Integer quantity, String deviceNameList)
            throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Báo cáo tổng hợp");
        Row row;
        Cell cell;
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        for (int z = 0; z < 100; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 100; j++) {
                row.createCell(j, CellType.BLANK).setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);
        row2 = sheet1.getRow(16);
        row2.setHeight((short) 3000);
        row2 = sheet1.getRow(17);
        row2.setHeight((short) 3000);

        // set độ rộng của cột
        for (int i = 0; i < 26; i++) {
            sheet1.setColumnWidth(i, 6000);
        }

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0).getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1).getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2).getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
                HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4).getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5).getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7).getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7).getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7).getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột thiết bị
        region = new CellRangeAddress(9, 9, 0, 0);
        cell = sheet1.getRow(9).getCell(0);
        cell.setCellValue("Thiết bị");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8).getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8).getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị thời gian từ
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8).getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột giá trị thời gian đến
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8).getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
//		Cột giá trị thiết bị
        region = new CellRangeAddress(10, 10, 0, 4);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(10).getCell(0);
        cell.setCellValue(deviceNameList);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        //Giá trị EP
        // cột THỜI GIAN
        region = new CellRangeAddress(22, 22, 0, 0);
        cell = sheet1.getRow(22).getCell(0);
        cell.setCellValue("THỜI GIAN");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        for (int i = 0; i < ls.size(); i++) {
            region = new CellRangeAddress(22, 22, i + 1, i + 1);
            cell = sheet1.getRow(22).getCell(i + 1);
            cell.setCellValue(ls.get(i).getDeviceName());
//			formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        }
        Integer countRow = 23;
        for (int i = 0; i < listData.size(); i++) {
//			Cột giá trị thời gian
            region = new CellRangeAddress(countRow + i, countRow + i, 0, 0);
            cell = sheet1.getRow(countRow + i).getCell(0);
            cell.setCellValue(listData.get(i).getViewTime());
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
//			Cột giá trị ep1

            region = new CellRangeAddress(countRow + i, countRow + i, 1, 1);
            cell = sheet1.getRow(countRow + i).getCell(1);
            cell.setCellValue(listData.get(i).getEp1());
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
            if (quantity >= 2) {
                //			Cột giá trị ep2
                region = new CellRangeAddress(countRow + i, countRow + i, 2, 2);
                cell = sheet1.getRow(countRow + i).getCell(2);
                cell.setCellValue(listData.get(i).getEp2());
                formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
            }
            //			Cột giá trị ep3
            if (quantity >= 3) {
                region = new CellRangeAddress(countRow + i, countRow + i, 3, 3);
                cell = sheet1.getRow(countRow + i).getCell(3);
                cell.setCellValue(listData.get(i).getEp3());
                formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
            }
            //			Cột giá trị ep4
            if (quantity >= 4) {
                region = new CellRangeAddress(countRow + i, countRow + i, 4, 4);
                cell = sheet1.getRow(countRow + i).getCell(4);
                cell.setCellValue(listData.get(i).getEp4());
                formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
            }
            //			Cột giá trị ep5
            if (quantity >= 5) {
                region = new CellRangeAddress(countRow + i, countRow + i, 5, 5);
                cell = sheet1.getRow(countRow + i).getCell(5);
                cell.setCellValue(listData.get(i).getEp5());
                formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
            }

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
                            new CellRangeAddress(21, countRow - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(21, countRow - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                                new CellRangeAddress(21, countRow - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                                new CellRangeAddress(21, countRow - 1, 0, 0));
                    }
                }
            }
        }


        // draw chart
        XSSFDrawing drawing = sheet1.createDrawingPatriarch();

        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 12, 6, 22);
        XSSFChart chart = drawing.createChart(anchor);

        CellReference firstDataCell = new CellReference(sheet1.getSheetName(), 23, 0, true, true);
        CellReference lastDataCell = new CellReference(sheet1.getSheetName(), 23 + listData.size() - 1, 5, true, true);

        CTChart ctChart = chart.getCTChart();
        CTPlotArea ctPlotArea = ctChart.getPlotArea();
        CTBarChart ctBarChart = ctPlotArea.addNewBarChart();
        CTBoolean ctBoolean = ctBarChart.addNewVaryColors();
        ctBoolean.setVal(true);
        ctBarChart.addNewBarDir().setVal(STBarDir.COL);

        int firstDataRow = firstDataCell.getRow();
        int lastDataRow = lastDataCell.getRow();
        int firstDataCol = firstDataCell.getCol();
        int lastDataCol = lastDataCell.getCol();
        String dataSheet = firstDataCell.getSheetName();

        int idx = 0;

        if (true) { // the series are in the columns of the data cells

            for (int c = firstDataCol + 1; c < lastDataCol + 1; c++) {
                CTBarSer ctBarSer = ctBarChart.addNewSer();
                CTSerTx ctSerTx = ctBarSer.addNewTx();
                CTStrRef ctStrRef = ctSerTx.addNewStrRef();
                ctStrRef.setF(new CellReference(dataSheet, firstDataRow - 1, c, true, true).formatAsString());

                ctBarSer.addNewIdx().setVal(idx++);
                CTAxDataSource cttAxDataSource = ctBarSer.addNewCat();
                ctStrRef = cttAxDataSource.addNewStrRef();

                ctStrRef.setF(
                        new AreaReference(new CellReference(dataSheet, firstDataRow, firstDataCol, true, true),
                                new CellReference(dataSheet, lastDataRow, firstDataCol, true, true),
                                SpreadsheetVersion.EXCEL2007).formatAsString());

                CTNumDataSource ctNumDataSource = ctBarSer.addNewVal();
                CTNumRef ctNumRef = ctNumDataSource.addNewNumRef();

                ctNumRef.setF(new AreaReference(new CellReference(dataSheet, firstDataRow, c, true, true),
                        new CellReference(dataSheet, lastDataRow, c, true, true), SpreadsheetVersion.EXCEL2007)
                        .formatAsString());

                // at least the border lines in Libreoffice Calc ;-)
                ctBarSer.addNewSpPr().addNewLn().addNewSolidFill().addNewSrgbClr().setVal(new byte[]{0, 0, 0});

            }
        }

        // telling the BarChart that it has axes and giving them Ids
        ctBarChart.addNewAxId().setVal(123456);
        ctBarChart.addNewAxId().setVal(123457);

        // cat axis
        CTCatAx ctCatAx = ctPlotArea.addNewCatAx();
        ctCatAx.addNewAxId().setVal(123456); // id of the cat axis
        CTScaling ctScaling = ctCatAx.addNewScaling();
        ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
        ctCatAx.addNewDelete().setVal(false);
        ctCatAx.addNewAxPos().setVal(STAxPos.B);
        ctCatAx.addNewCrossAx().setVal(123457); // id of the val axis
        ctCatAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

        // val axis
        CTValAx ctValAx = ctPlotArea.addNewValAx();
        ctValAx.addNewAxId().setVal(123457); // id of the val axis
        ctScaling = ctValAx.addNewScaling();
        ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
        ctValAx.addNewDelete().setVal(false);
        ctValAx.addNewAxPos().setVal(STAxPos.L);
        ctValAx.addNewCrossAx().setVal(123456); // id of the cat axis
        ctValAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

        // legend
        CTLegend ctLegend = ctChart.addNewLegend();
        ctLegend.addNewLegendPos().setVal(STLegendPos.B);
        ctLegend.addNewOverlay().setVal(false);

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        String exportFilePath = path + File.separator + url + ".xlsx";
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
        Workbook workbook = new Workbook();
        workbook.loadFromFile(exportFilePath);
        String pdf = path + File.separator + url + ".pdf";
        // Fit to page
        workbook.getConverterSetting().setSheetFitToPage(true);

        // Save as PDF document
        workbook.saveToFile(pdf);
        ZipUtil.pack(folder, new File(path + ".zip"));
    }


    private static void solidFillSeries(XDDFChartData data, int index, PresetColor color) {
        XDDFSolidFillProperties fill = new XDDFSolidFillProperties(XDDFColor.from(color));
        XDDFChartData.Series series = data.getSeries().get(index);
        XDDFShapeProperties properties = series.getShapeProperties();
        if (properties == null) {
            properties = new XDDFShapeProperties();
        }
        properties.setFillProperties(fill);
        series.setShapeProperties(properties);
    }

    private static void solidLineSeries(XDDFChartData data, int index, PresetColor color) {
        XDDFSolidFillProperties fill = new XDDFSolidFillProperties(XDDFColor.from(color));
        XDDFLineProperties line = new XDDFLineProperties();
        line.setFillProperties(fill);
        XDDFChartData.Series series = data.getSeries().get(index);
        XDDFShapeProperties properties = series.getShapeProperties();
        if (properties == null) {
            properties = new XDDFShapeProperties();
        }
        properties.setLineProperties(line);
        series.setShapeProperties(properties);
    }

    private void createWarningTotalExcel(final List<DataPqs> listData, String customerName, String description,
                                         Integer typeTime, String reportName, Integer systemTypeId, String moduleName, String siteName,
                                         String deviceNameList, String fromDate, String toDate, final String dateTime, final String path,
                                         String area, String loadType, Integer deviceTypeId, String deviceTypeName, Integer device, String devices, String schema, Integer projectId, String devicesName)
            throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");
        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Báo cáo tổng hợp");
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
        for (int z = 0; z < 100; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 100; j++) {
                row.createCell(j, CellType.BLANK).setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);
        // Hàng biểu đồ
        // row2 = sheet1.getRow(14);
        // row2.setHeight((short) 2000);
        // row2 = sheet1.getRow(15);
        // row2.setHeight((short) 2000);
        // row2 = sheet1.getRow(16);
        // row2.setHeight((short) 2000);
        // row2 = sheet1.getRow(17);
        // row2.setHeight((short) 2000);

        // set độ rộng của cột
        for (int i = 0; i < 26; i++) {
            sheet1.setColumnWidth(i, 6000);
        }

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0).getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1).getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2).getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
                HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4).getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5).getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7).getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7).getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7).getCell(2);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
//		Cột thiết bị
        region = new CellRangeAddress(7, 7, 2, 4);
        cell = sheet1.getRow(7).getCell(4);
        cell.setCellValue("Thiết bị");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột Khu vực
        if (area != null) {
            region = new CellRangeAddress(7, 7, 5, 5);
            cell = sheet1.getRow(7).getCell(5);
            cell.setCellValue("Khu vực");
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            // Cột giá trị khu vực
            region = new CellRangeAddress(8, 8, 5, 5);
            cell = sheet1.getRow(8).getCell(5);
            cell.setCellValue(area);
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        }
        // cột Phụ tải
        if (loadType != null) {
            region = new CellRangeAddress(7, 7, 5, 5);
            cell = sheet1.getRow(7).getCell(5);
            cell.setCellValue("Phụ tải");
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            // Cột giá trị phụ tải
            region = new CellRangeAddress(8, 8, 5, 5);
            cell = sheet1.getRow(8).getCell(5);
            cell.setCellValue(loadType);
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        }
        // cột loại thiết bị
        if (deviceTypeId != null) {
            region = new CellRangeAddress(7, 7, 5, 5);
            cell = sheet1.getRow(7).getCell(5);
            cell.setCellValue("Loại thiết bị");
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            // Cột giá trị loại thiết bị
            region = new CellRangeAddress(8, 8, 5, 5);
            cell = sheet1.getRow(8).getCell(5);
            cell.setCellValue(deviceTypeName);
        }
        // Cột điểm do
        if (device != null) {
            region = new CellRangeAddress(7, 7, 5, 5);
            cell = sheet1.getRow(7).getCell(5);
            cell.setCellValue("Điểm đo");
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            // Cột giá trị điểm đo
            region = new CellRangeAddress(8, 8, 5, 5);
            cell = sheet1.getRow(8).getCell(5);
            cell.setCellValue(device);
        }
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8).getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8).getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8).getCell(2);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8).getCell(3);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
//		Cột giá trị danh sách thiết bị
        region = new CellRangeAddress(8, 8, 4, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(8).getCell(4);
        cell.setCellValue(deviceNameList);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột số thứ tự
        region = new CellRangeAddress(24, 24, 0, 0);
        cell = sheet1.getRow(24).getCell(0);
        cell.setCellValue("STT");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // Cột loại cảnh báo
        region = new CellRangeAddress(24, 24, 1, 1);
        cell = sheet1.getRow(24).getCell(1);
        cell.setCellValue("Loại Cảnh báo");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(24, 24, 2, 2);
        cell = sheet1.getRow(24).getCell(2);
        cell.setCellValue("Số điểm đo");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        region = new CellRangeAddress(24, 24, 3, 3);
        cell = sheet1.getRow(24).getCell(3);

        cell.setCellValue("Danh sách điểm đo");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        int countRow = 25;
        // set giá trị stt
        for (int i = 0; i < listData.size(); i++) {
            cell = sheet1.getRow(countRow).getCell(0);
            cell.setCellValue(i + 1);
            countRow++;
        }
        countRow = 25;
        for (DataPqs dataPqs : listData) {
            cell = sheet1.getRow(countRow).getCell(1);
            cell.setCellValue(dataPqs.getWarningTypeName());
            countRow++;
        }
        countRow = 25;
        for (DataPqs dataPqs : listData) {
            cell = sheet1.getRow(countRow).getCell(2);
            cell.setCellValue(dataPqs.getNumberOfEquiment());
            countRow++;
        }

        // set giá trị danh sách điểm đo
        countRow = 25;
        for (DataPqs dataPqs : listData) {
            region = new CellRangeAddress(countRow, countRow, 3, 4);
            sheet1.addMergedRegion(region);
            cell = sheet1.getRow(countRow).getCell(3);
            cell.setCellValue(printDeviceAndQuantity(
                    reportService.getDeviceByWarningType(fromDate, toDate, dataPqs.getWarningTypeId(), typeTime, devices, schema, projectId, systemTypeId)));
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 0);
            countRow++;
        }

        XSSFDrawing drawing = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 10, 6, 22);
        XSSFChart chart = drawing.createChart(anchor);
        if (typeTime == 1) {
            chart.setTitleText(reportName + " theo ngày");
        } else if (typeTime == 2) {
            chart.setTitleText(reportName + " theo tháng");
        } else if (typeTime == 3) {
            chart.setTitleText(reportName + " theo năm");
            chart.setTitleOverlay(false);
        }
//

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setCrosses(AxisCrosses.MIN);
        bottomAxis.setMajorUnit(1);
        bottomAxis.setMinorUnit(1);
        bottomAxis.setTitle("Loại báo cáo");
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Số điểm đo phát ra cảnh báo");
//        XDDFChartLegend legend = chart.getOrAddLegend();
//        legend.setPosition(LegendPosition.TOP_RIGHT);
        XDDFDataSource<String> countries = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                new CellRangeAddress(25, countRow, 0, 1));

        XDDFNumericalDataSource<Double> quantityDevices = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                new CellRangeAddress(25, countRow, 2, 2));

        XDDFChartData data = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        XDDFChartData.Series series1 = data.addSeries(countries, quantityDevices);
        series1.setTitle("", null);

        data.setVaryColors(true);

        chart.plot(data);

        // correcting the overlap so bars really are stacked and not side by side
        chart.getCTChart().getPlotArea().getBarChartArray(0).addNewOverlap().setVal((byte) 100);

        // in order to transform a bar chart into a column chart, you just need to
        // change the bar direction
        XDDFBarChartData bar = (XDDFBarChartData) data;
        bar.setBarGrouping(BarGrouping.STACKED);

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
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
        Workbook workbook = new Workbook();
        workbook.loadFromFile(exportFilePath);
        String pdf = path + File.separator + StringUtils.stripAccents(reportName) + ".pdf";
        // // Fit to page
        workbook.getConverterSetting().setSheetFitToPage(true);

        // Save as PDF document
        workbook.saveToFile(pdf);

        ZipUtil.pack(folder, new File(path + ".zip"));
    }

    private void createQuantityPowerExcel(final List<ReportQuantityPower> lsData, final List<ReportQuantityPower> lsData2, String customerName, String description,
                                          String reportName, Integer typeTime, String moduleName, String siteName,
                                          String deviceNameList, String fromDate, String toDate,
                                          final String dateTime, final String path, String Vn, String In, String ThdVn, String ThdIn)
            throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");
        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Báo cáo tổng hợp");
        XSSFSheet sheet2 = wb.createSheet("Dữ liệu điểm đo");
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
        for (int z = 0; z < 100; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 100; j++) {
                row.createCell(j, CellType.BLANK).setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);


        // set độ rộng của cột
        for (int i = 0; i < 26; i++) {
            sheet1.setColumnWidth(i, 6000);
        }

// set độ rộng của hàng sheet2
        Row row3 = sheet2.getRow(1);
        row1.setHeight((short) 1000);
        Row row4 = sheet2.getRow(4);
        row2.setHeight((short) 1000);


        // set độ rộng của cột
        sheet2.setColumnWidth(0, 5000);
        sheet2.setColumnWidth(1, 5000);
        sheet2.setColumnWidth(2, 5000);
        sheet2.setColumnWidth(3, 6000);
        sheet2.setColumnWidth(4, 6000);
        sheet2.setColumnWidth(5, 5000);
        sheet2.setColumnWidth(6, 5000);

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 6);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0).getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 6);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1).getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 6);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2).getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
                HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 6);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4).getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 6);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5).getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7).getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7).getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7).getCell(2);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
//		Cột thiết bị
        region = new CellRangeAddress(7, 7, 2, 4);
        cell = sheet1.getRow(7).getCell(4);
        cell.setCellValue("Thiết bị");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột Khu vực


        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8).getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8).getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8).getCell(2);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8).getCell(3);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
//		Cột giá trị danh sách thiết bị
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8).getCell(4);
        cell.setCellValue(deviceNameList);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

//		Cell cell1;
        Row rowShett2;
        Cell cellShett2;

//		THD_VAN
        region = new CellRangeAddress(0, 0, 0, 0);
        rowShett2 = sheet2.createRow(0);
        cellShett2 = rowShett2.createCell(0);
        cellShett2.setCellValue("THD_VAN");
        formatExcel(wb, region, sheet2, cellShett2, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
//		THD_VBN
        region = new CellRangeAddress(0, 0, 1, 1);
        cellShett2 = rowShett2.createCell(1);
        cellShett2.setCellValue("THD_VBN");
        formatExcel(wb, region, sheet2, cellShett2, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
//		THD_VCN
        region = new CellRangeAddress(0, 0, 2, 2);
        cellShett2 = rowShett2.createCell(2);
        cellShett2.setCellValue("THD_VCN");
        formatExcel(wb, region, sheet2, cellShett2, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
//		THD_IA
        region = new CellRangeAddress(0, 0, 3, 3);
        cellShett2 = rowShett2.createCell(3);
        cellShett2.setCellValue("THD_IA");
        formatExcel(wb, region, sheet2, cellShett2, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
//		THD_IB
        region = new CellRangeAddress(0, 0, 4, 4);
        cellShett2 = rowShett2.createCell(4);
        cellShett2.setCellValue("THD_IB");
        formatExcel(wb, region, sheet2, cellShett2, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
//		THD_IC
        region = new CellRangeAddress(0, 0, 5, 5);
        cellShett2 = rowShett2.createCell(5);
        cellShett2.setCellValue("THD_IC");
        formatExcel(wb, region, sheet2, cellShett2, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
//		Cột ngày
        region = new CellRangeAddress(0, 0, 6, 6);
        cellShett2 = rowShett2.createCell(6);
        cellShett2.setCellValue("Ngày gửi");
        formatExcel(wb, region, sheet2, cellShett2, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

//		Hàng giá trị

        for (int i = 0; i < 30; i++) {
//			IA
            sheet2.setColumnWidth(i + 7, 5000);
            region = new CellRangeAddress(0, 0, (i + 7), (i + 7));
            cellShett2 = rowShett2.createCell((i + 7));
            cellShett2.setCellValue("IA_H" + (i + 2));
            formatExcel(wb, region, sheet2, cellShett2, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
//			IB
            sheet2.setColumnWidth(i + 37, 5000);
            region = new CellRangeAddress(0, 0, (i + 37), (i + 37));
            cellShett2 = rowShett2.createCell((i + 37));
            cellShett2.setCellValue("IB_H" + (i + 2));
            formatExcel(wb, region, sheet2, cellShett2, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
//			IC
            sheet2.setColumnWidth(i + 67, 5000);
            region = new CellRangeAddress(0, 0, (i + 67), (i + 67));
            cellShett2 = rowShett2.createCell((i + 67));
            cellShett2.setCellValue("IC_H" + (i + 2));
            formatExcel(wb, region, sheet2, cellShett2, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
//			IA
            sheet2.setColumnWidth(i + 97, 5000);
            region = new CellRangeAddress(0, 0, (i + 97), (i + 97));
            cellShett2 = rowShett2.createCell((i + 97));
            cellShett2.setCellValue("VAN_H" + (i + 2));
            formatExcel(wb, region, sheet2, cellShett2, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
//			IB
            sheet2.setColumnWidth(i + 127, 5000);
            region = new CellRangeAddress(0, 0, (i + 127), (i + 127));
            cellShett2 = rowShett2.createCell((i + 127));
            cellShett2.setCellValue("VBN_H" + (i + 2));
            formatExcel(wb, region, sheet2, cellShett2, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
//			IC
            sheet2.setColumnWidth(i + 157, 5000);
            region = new CellRangeAddress(0, 0, (i + 157), (i + 157));
            cellShett2 = rowShett2.createCell((i + 157));
            cellShett2.setCellValue("VCN_H" + (i + 2));
            formatExcel(wb, region, sheet2, cellShett2, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        }


        for (int i = 0; i < lsData.size(); i++) {
            rowShett2 = sheet2.createRow(i + 1);
//			Giá tr VAN
            region = new CellRangeAddress(i + 1, i + 1, 0, 0);
            cellShett2 = rowShett2.createCell(0);
            cellShett2.setCellValue(lsData.get(i).getThdVan() != null ? lsData.get(i).getThdVan() : 0);
            //			Giá trị VBN
            region = new CellRangeAddress(i + 1, i + 1, 0, 0);
            cellShett2 = rowShett2.createCell(1);
            cellShett2.setCellValue(lsData.get(i).getThdVbn() != null ? lsData.get(i).getThdVbn() : 0);
//			Gia trị VCN
            region = new CellRangeAddress(i + 1, i + 1, 0, 0);
            cellShett2 = rowShett2.createCell(2);
            cellShett2.setCellValue(lsData.get(i).getThdVcn() != null ? lsData.get(i).getThdVcn() : 0);
//			Giá trị IA
            region = new CellRangeAddress(i + 1, i + 1, 0, 0);
            cellShett2 = rowShett2.createCell(3);
            cellShett2.setCellValue(lsData.get(i).getThdIa() != null ? lsData.get(i).getThdIa() : 0);
//			Giá trị IB
            region = new CellRangeAddress(i + 1, i + 1, 0, 0);
            cellShett2 = rowShett2.createCell(4);
            cellShett2.setCellValue(lsData.get(i).getThdIb() != null ? lsData.get(i).getThdIb() : 0);
//			Giá trị IC
            region = new CellRangeAddress(i + 1, i + 1, 0, 0);
            cellShett2 = rowShett2.createCell(5);
            cellShett2.setCellValue(lsData.get(i).getThdIc() != null ? lsData.get(i).getThdIc() : 0);
            //			Giá trị sent Date

            region = new CellRangeAddress(i + 1, i + 1, 0, 0);
            cellShett2 = rowShett2.createCell(6);
            cellShett2.setCellValue(lsData.get(i).getSentDate() + "");
            int a = lsData.size() - lsData2.size();
            if (lsData2.size() > 0) {
                if (i >= lsData2.size()) {
                    for (int j = 0; j < 30; j++) {
//				IA
                        cellShett2 = rowShett2.createCell((j + 7));
                        cellShett2.setCellValue(testNull(printValue(lsData2.get(lsData2.size()-1) != null ? lsData2.get(lsData2.size()-1) : null, "IA", (j + 2))));
//				IB
                        cellShett2 = rowShett2.createCell((j + 37));
                        cellShett2.setCellValue(testNull(printValue(lsData2.get(lsData2.size()-1) != null ? lsData2.get(lsData2.size()-1) : null, "IB", (j + 2))));
//				IC
                        cellShett2 = rowShett2.createCell((j + 67));
                        cellShett2.setCellValue(testNull(printValue(lsData2.get(lsData2.size()-1) != null ? lsData2.get(lsData2.size()-1) : null, "IC", (j + 2))));
//				VAN
                        cellShett2 = rowShett2.createCell((j + 97));
                        cellShett2.setCellValue(testNull(printValue(lsData2.get(lsData2.size()-1) != null ? lsData2.get(lsData2.size()-1) : null, "VAN", (j + 2))));
//				VBN
                        cellShett2 = rowShett2.createCell((j + 127));
                        cellShett2.setCellValue(testNull(printValue(lsData2.get(lsData2.size()-1) != null ? lsData2.get(lsData2.size()-1) : null, "VBN", (j + 2))));
//				VCN
                        cellShett2 = rowShett2.createCell((j + 157));
                        cellShett2.setCellValue(testNull(printValue(lsData2.get(lsData2.size()-1) != null ? lsData2.get(lsData2.size()-1) : null, "VCN", (j + 2))));
                    }
                } else {
                    for (int j = 0; j < 30; j++) {
//				IA
                        cellShett2 = rowShett2.createCell((j + 7));
                        cellShett2.setCellValue(testNull(printValue(lsData2.get(i) != null ? lsData2.get(i) : null, "IA", (j + 2))));
//				IB
                        cellShett2 = rowShett2.createCell((j + 37));
                        cellShett2.setCellValue(testNull(printValue(lsData2.get(i) != null ? lsData2.get(i) : null, "IB", (j + 2))));
//				IC
                        cellShett2 = rowShett2.createCell((j + 67));
                        cellShett2.setCellValue(testNull(printValue(lsData2.get(i) != null ? lsData2.get(i) : null, "IC", (j + 2))));
//				VAN
                        cellShett2 = rowShett2.createCell((j + 97));
                        cellShett2.setCellValue(testNull(printValue(lsData2.get(i) != null ? lsData2.get(i) : null, "VAN", (j + 2))));
//				VBN
                        cellShett2 = rowShett2.createCell((j + 127));
                        cellShett2.setCellValue(testNull(printValue(lsData2.get(i) != null ? lsData2.get(i) : null, "VBN", (j + 2))));
//				VCN
                        cellShett2 = rowShett2.createCell((j + 157));
                        cellShett2.setCellValue(testNull(printValue(lsData2.get(i) != null ? lsData2.get(i) : null, "VCN", (j + 2))));

                    }
                }
            }
        }


//		Bảng điện áp
        region = new CellRangeAddress(10, 10, 0, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(10).getCell(0);
        cell.setCellValue("Điện áp");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
//		Cột 1
        region = new CellRangeAddress(11, 11, 0, 0);
        cell = sheet1.getRow(11).getCell(0);
        cell.setCellValue("Cấp điện áp 0.4V");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(12, 12, 0, 0);
        cell = sheet1.getRow(12).getCell(0);
        cell.setCellValue("IEEE 519 limit(%)");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(13, 13, 0, 0);
        cell = sheet1.getRow(13).getCell(0);
        cell.setCellValue("Giá trị lớn nhất");
        formatExcelTable(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);


        //		Cột 2
        region = new CellRangeAddress(11, 11, 1, 1);
        cell = sheet1.getRow(11).getCell(1);
        cell.setCellValue("Sóng hài điện áp bậc riêng lẻ(%)");
        formatExcelTable(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(12, 12, 1, 1);
        cell = sheet1.getRow(12).getCell(1);
        cell.setCellValue(Double.valueOf(Vn));
        formatExcel1(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(13, 13, 1, 1);
        cell = sheet1.getRow(13).getCell(1);
        cell.setCellValue(getMaxValueFromRange(sheet2, 1, lsData.size(), 97, 186));
        formatExcel1(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

//		Cột 3

        region = new CellRangeAddress(11, 11, 2, 2);
        cell = sheet1.getRow(11).getCell(2);
        cell.setCellValue("Tổng sóng hài điện áp(%)");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(12, 12, 2, 2);
        cell = sheet1.getRow(12).getCell(2);
        cell.setCellValue(Double.valueOf(ThdVn));
        formatExcel1(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(13, 13, 2, 2);
        cell = sheet1.getRow(13).getCell(2);
        cell.setCellValue(getMaxValueFromRange(sheet2, 1, lsData.size(), 0, 2));
        formatExcel1(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        //		Bảng dòng điện
        region = new CellRangeAddress(15, 15, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(15).getCell(0);
        cell.setCellValue("Dòng điện");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(15, 15, 6, 6);
        cell = sheet1.getRow(15).getCell(6);
        cell.setCellValue("TDD(%)");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
//Cột 1
        region = new CellRangeAddress(16, 16, 0, 0);
        cell = sheet1.getRow(16).getCell(0);
        cell.setCellValue("Bậc");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(17, 17, 0, 0);
        cell = sheet1.getRow(17).getCell(0);
        cell.setCellValue("IEEE 519 Limit(%)");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(18, 18, 0, 0);
        cell = sheet1.getRow(18).getCell(0);
        cell.setCellValue("Giá trị lớn nhất");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        //Cột 2
        region = new CellRangeAddress(16, 16, 1, 1);
        cell = sheet1.getRow(16).getCell(1);
        cell.setCellValue("< 11");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        String[] iEEIn = In.split(",");

        region = new CellRangeAddress(17, 17, 1, 1);
        cell = sheet1.getRow(17).getCell(1);
        cell.setCellValue(Double.valueOf(iEEIn[0]));
        formatExcel1(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(18, 18, 1, 1);
        cell = sheet1.getRow(18).getCell(1);
        cell.setCellValue(compileMaxValue(getMaxValueFromRange(sheet2, 1, lsData.size(), 7, 15)
                , getMaxValueFromRange(sheet2, 1, lsData.size(), 37, 45), getMaxValueFromRange(sheet2, 1, lsData.size(), 67, 75)));
        formatExcel1(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);


        //Cột 3
        region = new CellRangeAddress(16, 16, 2, 2);
        cell = sheet1.getRow(16).getCell(2);
        cell.setCellValue("11 <= h < 17");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(17, 17, 2, 2);
        cell = sheet1.getRow(17).getCell(2);
        cell.setCellValue(Double.valueOf(iEEIn[1]));
        formatExcel1(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(18, 18, 2, 2);
        cell = sheet1.getRow(18).getCell(2);
        cell.setCellValue(compileMaxValue(getMaxValueFromRange(sheet2, 1, lsData.size(), 16, 21), getMaxValueFromRange(sheet2, 1, lsData.size(), 46, 51), getMaxValueFromRange(sheet2, 1, lsData.size(), 76, 81)));
        formatExcel1(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);


        //Cột 4
        region = new CellRangeAddress(16, 16, 3, 3);
        cell = sheet1.getRow(16).getCell(3);
        cell.setCellValue("17 <= h < 23");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(17, 17, 3, 3);
        cell = sheet1.getRow(17).getCell(3);
        cell.setCellValue(Double.valueOf(iEEIn[2]));
        formatExcel1(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(18, 18, 3, 3);
        cell = sheet1.getRow(18).getCell(3);
        cell.setCellValue(compileMaxValue(getMaxValueFromRange(sheet2, 1, lsData.size(), 22, 27), getMaxValueFromRange(sheet2, 1, lsData.size(), 52, 57), getMaxValueFromRange(sheet2, 1, lsData.size(), 82, 87)));
        formatExcel1(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        //Cột 5
        region = new CellRangeAddress(16, 16, 4, 4);
        cell = sheet1.getRow(16).getCell(4);
        cell.setCellValue("23 <= h < 31");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(17, 17, 4, 4);
        cell = sheet1.getRow(17).getCell(4);
        cell.setCellValue(Double.valueOf(iEEIn[3]));
        formatExcel1(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(18, 18, 4, 4);
        cell = sheet1.getRow(18).getCell(4);
        cell.setCellValue(compileMaxValue(getMaxValueFromRange(sheet2, 1, lsData.size(), 28, 36), getMaxValueFromRange(sheet2, 1, lsData.size(), 58, 66), getMaxValueFromRange(sheet2, 1, lsData.size(), 88, 96)));
        formatExcel1(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        //Cột 6
        region = new CellRangeAddress(16, 16, 5, 5);
        cell = sheet1.getRow(16).getCell(5);
        cell.setCellValue("35 <= h");
        formatExcel1(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(17, 17, 5, 5);
        cell = sheet1.getRow(17).getCell(5);
        cell.setCellValue(Double.valueOf(iEEIn[4]));
        formatExcel1(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(18, 18, 5, 5);
        cell = sheet1.getRow(18).getCell(5);
        cell.setCellValue(0);
        formatExcel1(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        //Cột 7
        region = new CellRangeAddress(16, 16, 6, 6);
        cell = sheet1.getRow(16).getCell(6);
        cell.setCellValue("");
        formatExcel1(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(17, 17, 6, 6);
        cell = sheet1.getRow(17).getCell(6);
        cell.setCellValue(Double.valueOf(ThdIn));
        formatExcel1(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(18, 18, 6, 6);
        cell = sheet1.getRow(18).getCell(6);
        cell.setCellValue(getMaxValueFromRange(sheet2, 1, lsData.size(), 3, 5));
        formatExcel1(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);


//		Vẽ biểu ở đây

//		THD VAN
        region = new CellRangeAddress(22, 22, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(22).getCell(0);
        cell.setCellValue("Biểu đồ tổng sóng hài điện áp pha A (THD_VAN)");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromStringCellRange(sheet2,
                new CellRangeAddress(1, lsData.size(), 6, 6));

        XDDFNumericalDataSource<Double> valueThdvan = XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                new CellRangeAddress(1, lsData.size(), 0, 0));

        DrawLineChart(categories, valueThdvan, sheet1, lsData, "Thd_Vbn", Double.valueOf(ThdVn), 24, 40, 0, 7);

//		Sóng hài pha B
        region = new CellRangeAddress(43, 43, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(43).getCell(0);
        cell.setCellValue("Biểu đồ tổng sóng hài điện áp pha B (THD_VBN)");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        XDDFNumericalDataSource<Double> valueThdVbn = XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                new CellRangeAddress(1, lsData.size(), 1, 1));

        DrawLineChart(categories, valueThdVbn, sheet1, lsData, "Thd_Vbn", Double.valueOf(ThdVn), 45, 60, 0, 7);

//		Sóng hài pha C
        region = new CellRangeAddress(63, 63, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(63).getCell(0);
        cell.setCellValue("Biểu đồ tổng sóng hài điện áp pha C (THD_VCN)");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        XDDFNumericalDataSource<Double> valueThdVcn = XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                new CellRangeAddress(1, lsData.size(), 2, 2));

        DrawLineChart(categories, valueThdVcn, sheet1, lsData, "Thd_Vbn", Double.valueOf(ThdVn), 65, 80, 0, 7);


//		Điện áp pha A
        region = new CellRangeAddress(83, 83, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(83).getCell(0);
        cell.setCellValue("Biểu đồ tổng sóng hài dòng điện pha A (THD_IA)");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        XDDFNumericalDataSource<Double> valueThdIa = XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                new CellRangeAddress(1, lsData.size(), 3, 3));

        DrawLineChart(categories, valueThdIa, sheet1, lsData, "Thd_Ia", Double.valueOf(ThdIn), 85, 100, 0, 7);

        //		Điện áp pha B
        region = new CellRangeAddress(103, 103, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(103).createCell(0);
        cell.setCellValue("Biểu đồ tổng sóng hài dòng điện pha B (THD_IB)");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        XDDFNumericalDataSource<Double> valueThdIb = XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                new CellRangeAddress(1, lsData.size(), 4, 4));

        DrawLineChart(categories, valueThdIb, sheet1, lsData, "Thd_Ib", Double.valueOf(ThdIn), 105, 120, 0, 7);

        //		Điện áp pha C
        region = new CellRangeAddress(123, 123, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(123).createCell(0);
        cell.setCellValue("Biểu đồ tổng sóng hài dòng điện pha C (THD_IC)");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        XDDFNumericalDataSource<Double> valueThdIc = XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                new CellRangeAddress(1, lsData.size(), 5, 5));

        DrawLineChart(categories, valueThdIc, sheet1, lsData, "Thd_Ic", Double.valueOf(ThdIn), 125, 140, 0, 7);

//  Biểu nhiều đường

        //		Biểu đồ sóng hài dòng điện pha A bậc h<11
        region = new CellRangeAddress(143, 143, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(143).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài dòng điện pha A bậc h<11");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        List<XDDFNumericalDataSource<Double>> lsIA = new ArrayList<>();
        List<String> sIA = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            lsIA.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 7 + i, 7 + i)));
            sIA.add("IA_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsIA, sheet1, lsData, sIA, Double.valueOf(iEEIn[0]), 145, 160, 0, 7);

//		Set title
        region = new CellRangeAddress(163, 163, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(163).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài dòng điện pha A bậc 11 <= h < 17");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
// Lấy dữ liệu
        List<XDDFNumericalDataSource<Double>> lsIA2 = new ArrayList<>();
        List<String> sIA2 = new ArrayList<>();
        for (int i = 9; i < 15; i++) {
            lsIA2.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 7 + i, 7 + i)));
            sIA2.add("IA_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsIA2, sheet1, lsData, sIA2, Double.valueOf(iEEIn[1]), 165, 180, 0, 7);

        region = new CellRangeAddress(183, 183, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(183).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài dòng điện pha A bậc 17 <= h < 23");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
// Lấy dữ liệu
        List<XDDFNumericalDataSource<Double>> lsIA3 = new ArrayList<>();
        List<String> sIA3 = new ArrayList<>();
        for (int i = 15; i < 21; i++) {
            lsIA3.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 7 + i, 7 + i)));
            sIA3.add("IA_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsIA3, sheet1, lsData, sIA3, Double.valueOf(iEEIn[2]), 185, 205, 0, 7);

        region = new CellRangeAddress(213, 213, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(213).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài dòng điện pha A bậc 23 <= h < 35");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
// Lấy dữ liệu
        List<XDDFNumericalDataSource<Double>> lsIA4 = new ArrayList<>();
        List<String> sIA4 = new ArrayList<>();
        for (int i = 21; i < 30; i++) {
            lsIA4.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 7 + i, 7 + i)));
            sIA4.add("IA_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsIA4, sheet1, lsData, sIA4, Double.valueOf(iEEIn[3]), 215, 235, 0, 7);

        region = new CellRangeAddress(238, 238, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(238).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài dòng điện pha B bậc h<11");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        List<XDDFNumericalDataSource<Double>> lsIB = new ArrayList<>();
        List<String> sIB = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            lsIB.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 37 + i, 37 + i)));
            sIB.add("IB_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsIB, sheet1, lsData, sIB, Double.valueOf(iEEIn[0]), 240, 260, 0, 7);

//		Set title
        region = new CellRangeAddress(263, 263, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(263).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài dòng điện pha B bậc 11 <= h < 17");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
// Lấy dữ liệu
        List<XDDFNumericalDataSource<Double>> lsIB2 = new ArrayList<>();
        List<String> sIB2 = new ArrayList<>();
        for (int i = 9; i < 15; i++) {
            lsIB2.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 37 + i, 37 + i)));
            sIB2.add("IB_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsIB2, sheet1, lsData, sIB2, Double.valueOf(iEEIn[1]), 265, 280, 0, 7);

        region = new CellRangeAddress(283, 283, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(283).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài dòng điện pha B bậc 17 <= h < 23");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
// Lấy dữ liệu
        List<XDDFNumericalDataSource<Double>> lsIB3 = new ArrayList<>();
        List<String> sIB3 = new ArrayList<>();
        for (int i = 15; i < 21; i++) {
            lsIB3.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 37 + i, 37 + i)));
            sIB3.add("IB_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsIB3, sheet1, lsData, sIB3, Double.valueOf(iEEIn[2]), 285, 305, 0, 7);

        region = new CellRangeAddress(313, 313, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(313).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài dòng điện pha B bậc 23 <= h < 31");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
// Lấy dữ liệu
        List<XDDFNumericalDataSource<Double>> lsIB4 = new ArrayList<>();
        List<String> sIB4 = new ArrayList<>();
        for (int i = 21; i < 30; i++) {
            lsIB4.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 37 + i, 37 + i)));
            sIB4.add("IB_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsIB4, sheet1, lsData, sIB4, Double.valueOf(iEEIn[3]), 315, 335, 0, 7);

//		IC
        //		Biểu đồ sóng hài dòng điện pha A bậc h<11
        region = new CellRangeAddress(343, 343, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(343).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài dòng điện pha C bậc h<11");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        List<XDDFNumericalDataSource<Double>> lsIC = new ArrayList<>();
        List<String> sIC = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            lsIC.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 67 + i, 67 + i)));
            sIC.add("IC_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsIC, sheet1, lsData, sIC, Double.valueOf(iEEIn[0]), 345, 360, 0, 7);

//		Set title
        region = new CellRangeAddress(363, 363, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(363).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài dòng điện pha C bậc 11 <= h < 17");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
// Lấy dữ liệu
        List<XDDFNumericalDataSource<Double>> lsIC2 = new ArrayList<>();
        List<String> sIC2 = new ArrayList<>();
        for (int i = 9; i < 15; i++) {
            lsIC2.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 67 + i, 67 + i)));
            sIC2.add("IC_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsIC2, sheet1, lsData, sIC2, Double.valueOf(iEEIn[1]), 365, 380, 0, 7);

        region = new CellRangeAddress(383, 383, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(383).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài dòng điện pha C bậc 17 <= h < 23");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
// Lấy dữ liệu
        List<XDDFNumericalDataSource<Double>> lsIC3 = new ArrayList<>();
        List<String> sIC3 = new ArrayList<>();
        for (int i = 15; i < 21; i++) {
            lsIC3.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 67 + i, 67 + i)));
            sIC3.add("IC_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsIC3, sheet1, lsData, sIC3, Double.valueOf(iEEIn[2]), 385, 405, 0, 7);

        region = new CellRangeAddress(413, 413, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(413).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài dòng điện pha C bậc 23 <= h < 35");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
// Lấy dữ liệu
        List<XDDFNumericalDataSource<Double>> lsIC4 = new ArrayList<>();
        List<String> sIC4 = new ArrayList<>();
        for (int i = 21; i < 30; i++) {
            lsIC4.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 67 + i, 67 + i)));
            sIC4.add("IC_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsIC4, sheet1, lsData, sIC4, Double.valueOf(iEEIn[3]), 415, 435, 0, 7);


//		Điện áp
        region = new CellRangeAddress(443, 443, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(443).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài Điện áp pha A bậc h<11");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        List<XDDFNumericalDataSource<Double>> lsVAN = new ArrayList<>();
        List<String> sVAN = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            lsVAN.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 97 + i, 97 + i)));
            sVAN.add("VAN_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsVAN, sheet1, lsData, sVAN, Double.valueOf(Vn), 445, 460, 0, 7);

//		Set title
        region = new CellRangeAddress(463, 463, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(463).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài điện áp pha A bậc 11 <= h < 17");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
// Lấy dữ liệu
        List<XDDFNumericalDataSource<Double>> lsVAN2 = new ArrayList<>();
        List<String> sVAN2 = new ArrayList<>();
        for (int i = 9; i < 15; i++) {
            lsVAN2.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 97 + i, 97 + i)));
            sVAN2.add("VAN_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsVAN2, sheet1, lsData, sVAN2, Double.valueOf(Vn), 465, 480, 0, 7);

        region = new CellRangeAddress(483, 483, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(483).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài điện áp pha A bậc 17 <= h < 23");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
// Lấy dữ liệu
        List<XDDFNumericalDataSource<Double>> lsVAN3 = new ArrayList<>();
        List<String> sVAN3 = new ArrayList<>();
        for (int i = 15; i < 21; i++) {
            lsVAN3.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 97 + i, 97 + i)));
            sVAN3.add("VAN_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsVAN3, sheet1, lsData, sVAN3, Double.valueOf(Vn), 485, 505, 0, 7);

        region = new CellRangeAddress(513, 513, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(513).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài điện áp pha A bậc 23 <= h < 31");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
// Lấy dữ liệu
        List<XDDFNumericalDataSource<Double>> lsVAN4 = new ArrayList<>();
        List<String> sVAN4 = new ArrayList<>();
        for (int i = 21; i < 30; i++) {
            lsVAN4.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 97 + i, 97 + i)));
            sVAN4.add("VAN_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsVAN4, sheet1, lsData, sVAN4, Double.valueOf(Vn), 515, 535, 0, 7);

//		Pha B
        region = new CellRangeAddress(543, 543, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(543).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài Điện áp pha B bậc h<11");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        List<XDDFNumericalDataSource<Double>> lsVBN = new ArrayList<>();
        List<String> sVBN = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            lsVBN.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 127 + i, 127 + i)));
            sVBN.add("VBN_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsVBN, sheet1, lsData, sVBN, Double.valueOf(Vn), 545, 560, 0, 7);

//		Set title
        region = new CellRangeAddress(563, 563, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(563).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài điện áp pha B bậc 11 <= h < 17");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
// Lấy dữ liệu
        List<XDDFNumericalDataSource<Double>> lsVBN2 = new ArrayList<>();
        List<String> sVBN2 = new ArrayList<>();
        for (int i = 9; i < 15; i++) {
            lsVBN2.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 127 + i, 127 + i)));
            sVBN2.add("VBN_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsVBN2, sheet1, lsData, sVBN2, Double.valueOf(Vn), 565, 580, 0, 7);

        region = new CellRangeAddress(583, 583, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(583).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài điện áp pha B bậc 17 <= h < 23");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
// Lấy dữ liệu
        List<XDDFNumericalDataSource<Double>> lsVBN3 = new ArrayList<>();
        List<String> sVBN3 = new ArrayList<>();
        for (int i = 15; i < 21; i++) {
            lsVBN3.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 127 + i, 127 + i)));
            sVBN3.add("VBN_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsVBN3, sheet1, lsData, sVBN3, Double.valueOf(Vn), 585, 605, 0, 7);

        region = new CellRangeAddress(613, 613, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(613).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài điện áp pha B bậc 23 <= h < 31");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
// Lấy dữ liệu
        List<XDDFNumericalDataSource<Double>> lsVBN4 = new ArrayList<>();
        List<String> sVBN4 = new ArrayList<>();
        for (int i = 21; i < 30; i++) {
            lsVBN4.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 127 + i, 127 + i)));
            sVBN4.add("VBN_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsVBN4, sheet1, lsData, sVBN4, Double.valueOf(Vn), 615, 635, 0, 7);

//		Pha C
        region = new CellRangeAddress(643, 643, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(643).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài Điện áp pha C bậc h<11");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        List<XDDFNumericalDataSource<Double>> lsVCN = new ArrayList<>();
        List<String> sVCN = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            lsVCN.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 157 + i, 157 + i)));
            sVCN.add("VCN_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsVCN, sheet1, lsData, sVCN, Double.valueOf(Vn), 645, 660, 0, 7);

//		Set title
        region = new CellRangeAddress(663, 663, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(663).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài điện áp pha C bậc 11 <= h < 17");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
// Lấy dữ liệu
        List<XDDFNumericalDataSource<Double>> lsVCN2 = new ArrayList<>();
        List<String> sVCN2 = new ArrayList<>();
        for (int i = 9; i < 15; i++) {
            lsVCN2.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 157 + i, 157 + i)));
            sVCN2.add("VCN_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsVCN2, sheet1, lsData, sVCN2, Double.valueOf(Vn), 665, 680, 0, 7);

        region = new CellRangeAddress(683, 683, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(683).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài điện áp pha C bậc 17 <= h < 23");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
// Lấy dữ liệu
        List<XDDFNumericalDataSource<Double>> lsVCN3 = new ArrayList<>();
        List<String> sVCN3 = new ArrayList<>();
        for (int i = 15; i < 21; i++) {
            lsVCN3.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 157 + i, 157 + i)));
            sVCN3.add("VCN_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsVCN3, sheet1, lsData, sVCN3, Double.valueOf(Vn), 685, 705, 0, 7);

        region = new CellRangeAddress(713, 713, 0, 7);
        sheet1.addMergedRegion(region);
        cell = sheet1.createRow(713).createCell(0);
        cell.setCellValue("Biểu đồ sóng hài điện áp pha C bậc 23 <= h < 31");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
// Lấy dữ liệu
        List<XDDFNumericalDataSource<Double>> lsVCN4 = new ArrayList<>();
        List<String> sVCN4 = new ArrayList<>();
        for (int i = 21; i < 30; i++) {
            lsVCN4.add(XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(1, lsData.size(), 157 + i, 157 + i)));
            sVCN4.add("VCN_H" + (i + 2));
        }

        DrawMuchLineChart(categories, lsVCN4, sheet1, lsData, sVCN4, Double.valueOf(Vn), 715, 735, 0, 7);


        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
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
        Workbook workbook = new Workbook();
        workbook.loadFromFile(exportFilePath);
        String pdf = path + File.separator + StringUtils.stripAccents(reportName) + ".pdf";
        // // Fit to page
        workbook.getConverterSetting().setSheetFitToPage(true);

        // Save as PDF document
        workbook.saveToFile(pdf);
        ZipUtil.pack(folder, new File(path + ".zip"));
    }

    private String printDeviceAndQuantity(List<DataPqs> ls) {
        String a = "";
        for (int i = 0; i < ls.size(); i++) {
            if (i == ls.size() - 1) {
                a += ls.get(i).getDeviceName() + "(" + ls.get(i).getQuantityWarning() + " lần)";
            } else {
                a += ls.get(i).getDeviceName() + "(" + ls.get(i).getQuantityWarning() + " lần)-";
            }
        }
        return a;
    }

    // Phương thức để tìm giá trị lớn nhất từ một phạm vi (nhiều cột và nhiều dòng) trong sheet
    private static double getMaxValueFromRange(Sheet sheet, int startRow, int endRow, int startColumn, int endColumn) {
        double max = Double.MIN_VALUE;
        for (int i = startRow; i <= endRow; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                for (int j = startColumn; j <= endColumn; j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                        double value = cell.getNumericCellValue();
                        if (value > max) {
                            max = value;
                        }
                    }
                }
            }
        }
        return max;
    }

    private static Double compileMaxValue(Double a, Double b, Double c) {
        Double max = 0.0;
        if (max <= a) {
            max = a;
        }
        if (max <= b) {
            max = b;
        }
        if (max <= c) {
            max = c;
        }
        return max;
    }


    private void DrawLineChart(XDDFDataSource<String> categories, XDDFNumericalDataSource<Double> value, XSSFSheet sheet, List<ReportQuantityPower> lsData, String tile, Double limitValue,
                               Integer firstRow, Integer lastRow, Integer firstCol, Integer lastCol
    ) {
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, firstCol, firstRow, lastCol, lastRow);
        XSSFChart chart = drawing.createChart(anchor);
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);

        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setCrossBetween(AxisCrossBetween.BETWEEN);

        XDDFChartData data = chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
        XDDFLineChartData.Series ser = (XDDFLineChartData.Series) data.addSeries(categories, value);
        ser.setTitle(tile, null);
        ser.setMarkerStyle(MarkerStyle.NONE);

//		Đường IEEE limit
        Double[] lineValues = new Double[lsData.size()];
        Arrays.fill(lineValues, limitValue);
        XDDFNumericalDataSource<Double> lineValue = XDDFDataSourcesFactory.fromArray(lineValues);

        XDDFLineChartData.Series series = (XDDFLineChartData.Series) data.addSeries(categories, lineValue);
        series.setTitle("IEEE Limit", null);
        series.setMarkerStyle(MarkerStyle.NONE);
//		Tô màu đường
        XDDFSolidFillProperties fill = new XDDFSolidFillProperties(XDDFColor.from(PresetColor.RED));
        XDDFLineProperties line = new XDDFLineProperties();
        line.setFillProperties(fill);
        XDDFShapeProperties properties = series.getShapeProperties();
        if (properties == null) {
            properties = new XDDFShapeProperties();
        }
        properties.setLineProperties(line);
        series.setShapeProperties(properties);

//		Vẽ
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP);

        chart.plot(data);
    }

    private void DrawMuchLineChart(XDDFDataSource<String> categories,
                                   List<XDDFNumericalDataSource<Double>> values,
                                   XSSFSheet sheet, List<ReportQuantityPower> lsData, List<String> tiles, Double limitValue,
                                   Integer firstRow, Integer lastRow, Integer firstCol, Integer lastCol
    ) {
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, firstCol, firstRow, lastCol, lastRow);
        XSSFChart chart = drawing.createChart(anchor);
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);

        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setCrossBetween(AxisCrossBetween.BETWEEN);
        XDDFChartData data = data = chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
        XDDFLineChartData.Series ser;
//		Biều nhiều đồ dường
        for (int i = 0; i < values.size(); i++) {
            ser = (XDDFLineChartData.Series) data.addSeries(categories, values.get(i));
            ser.setTitle(tiles.get(i), null);
            ser.setMarkerStyle(MarkerStyle.NONE);
        }
//		Đường IEEE limit
        Double[] lineValues = new Double[lsData.size()];
        Arrays.fill(lineValues, limitValue);
        XDDFNumericalDataSource<Double> lineValue = XDDFDataSourcesFactory.fromArray(lineValues);

        XDDFLineChartData.Series series = (XDDFLineChartData.Series) data.addSeries(categories, lineValue);
        series.setTitle("IEEE Limit", null);
        series.setMarkerStyle(MarkerStyle.NONE);


//		Tô màu đường
        XDDFSolidFillProperties fill = new XDDFSolidFillProperties(XDDFColor.from(PresetColor.RED));
        XDDFLineProperties line = new XDDFLineProperties();
        line.setFillProperties(fill);
        XDDFShapeProperties properties = series.getShapeProperties();
        if (properties == null) {
            properties = new XDDFShapeProperties();
        }
        properties.setLineProperties(line);
        series.setShapeProperties(properties);
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP);

        chart.plot(data);
    }

    /**
     * Format Header.
     */
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

    private String printValue(ReportQuantityPower rp, String type, Integer stt) {
        if (type == "IA") {
            if (stt == 2) {
                return String.valueOf(rp.getIA_H2());
            }
            if (stt == 3) {
                return String.valueOf(rp.getIA_H3());
            }
            if (stt == 4) {
                return String.valueOf(rp.getIA_H4());
            }
            if (stt == 5) {
                return String.valueOf(rp.getIA_H5());
            }
            if (stt == 6) {
                return String.valueOf(rp.getIA_H6());
            }
            if (stt == 7) {
                return String.valueOf(rp.getIA_H7());
            }
            if (stt == 8) {
                return String.valueOf(rp.getIA_H8());
            }
            if (stt == 9) {
                return String.valueOf(rp.getIA_H9());
            }
            if (stt == 10) {
                return String.valueOf(rp.getIA_H10());
            }
            if (stt == 11) {
                return String.valueOf(rp.getIA_H11());
            }
            if (stt == 12) {
                return String.valueOf(rp.getIA_H12());
            }
            if (stt == 13) {
                return String.valueOf(rp.getIA_H13());
            }
            if (stt == 14) {
                return String.valueOf(rp.getIA_H14());
            }
            if (stt == 15) {
                return String.valueOf(rp.getIA_H15());
            }
            if (stt == 16) {
                return String.valueOf(rp.getIA_H16());
            }
            if (stt == 17) {
                return String.valueOf(rp.getIA_H17());
            }
            if (stt == 18) {
                return String.valueOf(rp.getIA_H18());
            }
            if (stt == 19) {
                return String.valueOf(rp.getIA_H19());
            }
            if (stt == 20) {
                return String.valueOf(rp.getIA_H20());
            }
            if (stt == 21) {
                return String.valueOf(rp.getIA_H21());
            }
            if (stt == 22) {
                return String.valueOf(rp.getIA_H22());
            }
            if (stt == 19) {
                return String.valueOf(rp.getIA_H9());
            }
            if (stt == 23) {
                return String.valueOf(rp.getIA_H23());
            }
            if (stt == 24) {
                return String.valueOf(rp.getIA_H24());
            }
            if (stt == 25) {
                return String.valueOf(rp.getIA_H25());
            }
            if (stt == 26) {
                return String.valueOf(rp.getIA_H26());
            }
            if (stt == 27) {
                return String.valueOf(rp.getIA_H27());
            }
            if (stt == 28) {
                return String.valueOf(rp.getIA_H28());
            }
            if (stt == 29) {
                return String.valueOf(rp.getIA_H29());
            }
            if (stt == 30) {
                return String.valueOf(rp.getIA_H30());
            }
            if (stt == 31) {
                return String.valueOf(rp.getIA_H31());
            }
        }
        if (type == "IB") {
            if (stt == 2) {
                return String.valueOf(rp.getIB_H2());
            }
            if (stt == 3) {
                return String.valueOf(rp.getIB_H3());
            }
            if (stt == 4) {
                return String.valueOf(rp.getIB_H4());
            }
            if (stt == 5) {
                return String.valueOf(rp.getIB_H5());
            }
            if (stt == 6) {
                return String.valueOf(rp.getIB_H6());
            }
            if (stt == 7) {
                return String.valueOf(rp.getIB_H7());
            }
            if (stt == 8) {
                return String.valueOf(rp.getIB_H8());
            }
            if (stt == 9) {
                return String.valueOf(rp.getIB_H9());
            }
            if (stt == 10) {
                return String.valueOf(rp.getIB_H10());
            }
            if (stt == 11) {
                return String.valueOf(rp.getIB_H11());
            }
            if (stt == 12) {
                return String.valueOf(rp.getIB_H12());
            }
            if (stt == 13) {
                return String.valueOf(rp.getIB_H13());
            }
            if (stt == 14) {
                return String.valueOf(rp.getIB_H14());
            }
            if (stt == 15) {
                return String.valueOf(rp.getIB_H15());
            }
            if (stt == 16) {
                return String.valueOf(rp.getIB_H16());
            }
            if (stt == 17) {
                return String.valueOf(rp.getIB_H17());
            }
            if (stt == 18) {
                return String.valueOf(rp.getIB_H18());
            }
            if (stt == 19) {
                return String.valueOf(rp.getIB_H19());
            }
            if (stt == 20) {
                return String.valueOf(rp.getIB_H20());
            }
            if (stt == 21) {
                return String.valueOf(rp.getIB_H21());
            }
            if (stt == 22) {
                return String.valueOf(rp.getIB_H22());
            }
            if (stt == 19) {
                return String.valueOf(rp.getIB_H9());
            }
            if (stt == 23) {
                return String.valueOf(rp.getIB_H23());
            }
            if (stt == 24) {
                return String.valueOf(rp.getIB_H24());
            }
            if (stt == 25) {
                return String.valueOf(rp.getIB_H25());
            }
            if (stt == 26) {
                return String.valueOf(rp.getIB_H26());
            }
            if (stt == 27) {
                return String.valueOf(rp.getIB_H27());
            }
            if (stt == 28) {
                return String.valueOf(rp.getIB_H28());
            }
            if (stt == 29) {
                return String.valueOf(rp.getIB_H29());
            }
            if (stt == 30) {
                return String.valueOf(rp.getIB_H30());
            }
            if (stt == 31) {
                return String.valueOf(rp.getIB_H31());
            }
        }
        if (type == "IC") {
            if (stt == 2) {
                return String.valueOf(rp.getIC_H2());
            }
            if (stt == 3) {
                return String.valueOf(rp.getIC_H3());
            }
            if (stt == 4) {
                return String.valueOf(rp.getIC_H4());
            }
            if (stt == 5) {
                return String.valueOf(rp.getIC_H5());
            }
            if (stt == 6) {
                return String.valueOf(rp.getIC_H6());
            }
            if (stt == 7) {
                return String.valueOf(rp.getIC_H7());
            }
            if (stt == 8) {
                return String.valueOf(rp.getIC_H8());
            }
            if (stt == 9) {
                return String.valueOf(rp.getIC_H9());
            }
            if (stt == 10) {
                return String.valueOf(rp.getIC_H10());
            }
            if (stt == 11) {
                return String.valueOf(rp.getIC_H11());
            }
            if (stt == 12) {
                return String.valueOf(rp.getIC_H12());
            }
            if (stt == 13) {
                return String.valueOf(rp.getIC_H13());
            }
            if (stt == 14) {
                return String.valueOf(rp.getIC_H14());
            }
            if (stt == 15) {
                return String.valueOf(rp.getIC_H15());
            }
            if (stt == 16) {
                return String.valueOf(rp.getIC_H16());
            }
            if (stt == 17) {
                return String.valueOf(rp.getIC_H17());
            }
            if (stt == 18) {
                return String.valueOf(rp.getIC_H18());
            }
            if (stt == 19) {
                return String.valueOf(rp.getIC_H19());
            }
            if (stt == 20) {
                return String.valueOf(rp.getIC_H20());
            }
            if (stt == 21) {
                return String.valueOf(rp.getIC_H21());
            }
            if (stt == 22) {
                return String.valueOf(rp.getIC_H22());
            }
            if (stt == 19) {
                return String.valueOf(rp.getIC_H9());
            }
            if (stt == 23) {
                return String.valueOf(rp.getIC_H23());
            }
            if (stt == 24) {
                return String.valueOf(rp.getIC_H24());
            }
            if (stt == 25) {
                return String.valueOf(rp.getIC_H25());
            }
            if (stt == 26) {
                return String.valueOf(rp.getIC_H26());
            }
            if (stt == 27) {
                return String.valueOf(rp.getIC_H27());
            }
            if (stt == 28) {
                return String.valueOf(rp.getIC_H28());
            }
            if (stt == 29) {
                return String.valueOf(rp.getIC_H29());
            }
            if (stt == 30) {
                return String.valueOf(rp.getIC_H30());
            }
            if (stt == 31) {
                return String.valueOf(rp.getIC_H31());
            }
        }
        if (type == "VAN") {
            if (stt == 2) {
                return String.valueOf(rp.getVAN_H2());
            }
            if (stt == 3) {
                return String.valueOf(rp.getVAN_H3());
            }
            if (stt == 4) {
                return String.valueOf(rp.getVAN_H4());
            }
            if (stt == 5) {
                return String.valueOf(rp.getVAN_H5());
            }
            if (stt == 6) {
                return String.valueOf(rp.getVAN_H6());
            }
            if (stt == 7) {
                return String.valueOf(rp.getVAN_H7());
            }
            if (stt == 8) {
                return String.valueOf(rp.getVAN_H8());
            }
            if (stt == 9) {
                return String.valueOf(rp.getVAN_H9());
            }
            if (stt == 10) {
                return String.valueOf(rp.getVAN_H10());
            }
            if (stt == 11) {
                return String.valueOf(rp.getVAN_H11());
            }
            if (stt == 12) {
                return String.valueOf(rp.getVAN_H12());
            }
            if (stt == 13) {
                return String.valueOf(rp.getVAN_H13());
            }
            if (stt == 14) {
                return String.valueOf(rp.getVAN_H14());
            }
            if (stt == 15) {
                return String.valueOf(rp.getVAN_H15());
            }
            if (stt == 16) {
                return String.valueOf(rp.getVAN_H16());
            }
            if (stt == 17) {
                return String.valueOf(rp.getVAN_H17());
            }
            if (stt == 18) {
                return String.valueOf(rp.getVAN_H18());
            }
            if (stt == 19) {
                return String.valueOf(rp.getVAN_H19());
            }
            if (stt == 20) {
                return String.valueOf(rp.getVAN_H20());
            }
            if (stt == 21) {
                return String.valueOf(rp.getVAN_H21());
            }
            if (stt == 22) {
                return String.valueOf(rp.getVAN_H22());
            }
            if (stt == 19) {
                return String.valueOf(rp.getVAN_H9());
            }
            if (stt == 23) {
                return String.valueOf(rp.getVAN_H23());
            }
            if (stt == 24) {
                return String.valueOf(rp.getVAN_H24());
            }
            if (stt == 25) {
                return String.valueOf(rp.getVAN_H25());
            }
            if (stt == 26) {
                return String.valueOf(rp.getVAN_H26());
            }
            if (stt == 27) {
                return String.valueOf(rp.getVAN_H27());
            }
            if (stt == 28) {
                return String.valueOf(rp.getVAN_H28());
            }
            if (stt == 29) {
                return String.valueOf(rp.getVAN_H29());
            }
            if (stt == 30) {
                return String.valueOf(rp.getVAN_H30());
            }
            if (stt == 31) {
                return String.valueOf(rp.getVAN_H31());
            }
        }
        if (type == "VBN") {
            if (stt == 2) {
                return String.valueOf(rp.getVBN_H2());
            }
            if (stt == 3) {
                return String.valueOf(rp.getVBN_H3());
            }
            if (stt == 4) {
                return String.valueOf(rp.getVBN_H4());
            }
            if (stt == 5) {
                return String.valueOf(rp.getVBN_H5());
            }
            if (stt == 6) {
                return String.valueOf(rp.getVBN_H6());
            }
            if (stt == 7) {
                return String.valueOf(rp.getVBN_H7());
            }
            if (stt == 8) {
                return String.valueOf(rp.getVBN_H8());
            }
            if (stt == 9) {
                return String.valueOf(rp.getVBN_H9());
            }
            if (stt == 10) {
                return String.valueOf(rp.getVBN_H10());
            }
            if (stt == 11) {
                return String.valueOf(rp.getVBN_H11());
            }
            if (stt == 12) {
                return String.valueOf(rp.getVBN_H12());
            }
            if (stt == 13) {
                return String.valueOf(rp.getVBN_H13());
            }
            if (stt == 14) {
                return String.valueOf(rp.getVBN_H14());
            }
            if (stt == 15) {
                return String.valueOf(rp.getVBN_H15());
            }
            if (stt == 16) {
                return String.valueOf(rp.getVBN_H16());
            }
            if (stt == 17) {
                return String.valueOf(rp.getVBN_H17());
            }
            if (stt == 18) {
                return String.valueOf(rp.getVBN_H18());
            }
            if (stt == 19) {
                return String.valueOf(rp.getVBN_H19());
            }
            if (stt == 20) {
                return String.valueOf(rp.getVBN_H20());
            }
            if (stt == 21) {
                return String.valueOf(rp.getVBN_H21());
            }
            if (stt == 22) {
                return String.valueOf(rp.getVBN_H22());
            }
            if (stt == 19) {
                return String.valueOf(rp.getVBN_H9());
            }
            if (stt == 23) {
                return String.valueOf(rp.getVBN_H23());
            }
            if (stt == 24) {
                return String.valueOf(rp.getVBN_H24());
            }
            if (stt == 25) {
                return String.valueOf(rp.getVBN_H25());
            }
            if (stt == 26) {
                return String.valueOf(rp.getVBN_H26());
            }
            if (stt == 27) {
                return String.valueOf(rp.getVBN_H27());
            }
            if (stt == 28) {
                return String.valueOf(rp.getVBN_H28());
            }
            if (stt == 29) {
                return String.valueOf(rp.getVBN_H29());
            }
            if (stt == 30) {
                return String.valueOf(rp.getVBN_H30());
            }
            if (stt == 31) {
                return String.valueOf(rp.getVBN_H31());
            }
        }
        if (type == "VCN") {
            if (stt == 2) {
                return String.valueOf(rp.getVCN_H2());
            }
            if (stt == 3) {
                return String.valueOf(rp.getVCN_H3());
            }
            if (stt == 4) {
                return String.valueOf(rp.getVCN_H4());
            }
            if (stt == 5) {
                return String.valueOf(rp.getVCN_H5());
            }
            if (stt == 6) {
                return String.valueOf(rp.getVCN_H6());
            }
            if (stt == 7) {
                return String.valueOf(rp.getVCN_H7());
            }
            if (stt == 8) {
                return String.valueOf(rp.getVCN_H8());
            }
            if (stt == 9) {
                return String.valueOf(rp.getVCN_H9());
            }
            if (stt == 10) {
                return String.valueOf(rp.getVCN_H10());
            }
            if (stt == 11) {
                return String.valueOf(rp.getVCN_H11());
            }
            if (stt == 12) {
                return String.valueOf(rp.getVCN_H12());
            }
            if (stt == 13) {
                return String.valueOf(rp.getVCN_H13());
            }
            if (stt == 14) {
                return String.valueOf(rp.getVCN_H14());
            }
            if (stt == 15) {
                return String.valueOf(rp.getVCN_H15());
            }
            if (stt == 16) {
                return String.valueOf(rp.getVCN_H16());
            }
            if (stt == 17) {
                return String.valueOf(rp.getVCN_H17());
            }
            if (stt == 18) {
                return String.valueOf(rp.getVCN_H18());
            }
            if (stt == 19) {
                return String.valueOf(rp.getVCN_H19());
            }
            if (stt == 20) {
                return String.valueOf(rp.getVCN_H20());
            }
            if (stt == 21) {
                return String.valueOf(rp.getVCN_H21());
            }
            if (stt == 22) {
                return String.valueOf(rp.getVCN_H22());
            }
            if (stt == 19) {
                return String.valueOf(rp.getVCN_H9());
            }
            if (stt == 23) {
                return String.valueOf(rp.getVCN_H23());
            }
            if (stt == 24) {
                return String.valueOf(rp.getVCN_H24());
            }
            if (stt == 25) {
                return String.valueOf(rp.getVCN_H25());
            }
            if (stt == 26) {
                return String.valueOf(rp.getVCN_H26());
            }
            if (stt == 27) {
                return String.valueOf(rp.getVCN_H27());
            }
            if (stt == 28) {
                return String.valueOf(rp.getVCN_H28());
            }
            if (stt == 29) {
                return String.valueOf(rp.getVCN_H29());
            }
            if (stt == 30) {
                return String.valueOf(rp.getVCN_H30());
            }
            if (stt == 31) {
                return String.valueOf(rp.getVCN_H31());
            }
        }
        return "";
    }

    private Double testNull(String a) {
        if (a == "null") {
            return Double.valueOf(0);
        }
        return Double.valueOf(a);
    }

    private void formatExcel1(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet, final Cell cell,
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
        cs.setDataFormat(format.getFormat("0.0"));
        cell.setCellStyle(cs);
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

    private void formatExcelTableBody(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
                                      final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent,
                                      final String unit) {

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
        cs.setDataFormat(format.getFormat("###,000" + unit));
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setWrapText(true);
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);
    }

    private void formatExcelTableBodyDecimal(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
                                             final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent,
                                             final String unit) {

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
        cs.setDataFormat(format.getFormat("###,##0.00 " + unit));
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setWrapText(true);
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);
    }

    private void formatExcelTotal(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
                                  final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent,
                                  final String unit) {

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

    private void formatExcelTotalDecimal(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
                                         final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent,
                                         final String unit) {

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
        cs.setDataFormat(format.getFormat("###,##0.00 " + unit));
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);
    }

    private void formatExcelBorder(final CellRangeAddress region, final Sheet sheet) {
        RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
        RegionUtil.setBorderTop(BorderStyle.MEDIUM, region, sheet);
        RegionUtil.setBorderLeft(BorderStyle.MEDIUM, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.MEDIUM, region, sheet);
    }

    public static Timestamp addDays(Timestamp date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return new Timestamp(cal.getTime().getTime());

    }

    private List<ManufactureShiftDetail> EditEpByViewTime(DataPqs ep, List<ManufactureShiftDetail> lsData, Integer typeTime) throws ParseException {
        SimpleDateFormat sfd = new SimpleDateFormat(Constants.ES.DATE_FORMAT_YMD);
        SimpleDateFormat sdf2 = new SimpleDateFormat(Constants.ES.DATE_FORMAT_YM_02);
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy");
        if (typeTime == 1) {
            if (lsData.size() > 0) {
                for (ManufactureShiftDetail data : lsData) {
                    if (ep.getViewTime().equals(sfd.format(data.getViewTime()))) {
                        data.setEpTotal(Double.valueOf(ep.getEp()));
                    }
                }
            }
        } else if (typeTime == 2) {
            if (lsData.size() > 0) {
                for (ManufactureShiftDetail data : lsData) {
                    if (sdf2.format(Timestamp.valueOf(ep.getViewTime() + " 00:00:00")).equals(sdf2.format(data.getViewTime()))) {
                        data.setEpTotal(Double.valueOf(ep.getEp()));
                    }
                }
            }
        } else {
            if (lsData.size() > 0) {
                for (ManufactureShiftDetail data : lsData) {
                    if (sdf3.format(Timestamp.valueOf(ep.getViewTime() + " 00:00:00")).equals(sdf3.format(data.getViewTime()))) {
                        data.setEpTotal(Double.valueOf(ep.getEp()));
                    }
                }
            }
        }

        return lsData;
    }

    private String getNameListLoadType(List<LoadType> ls) {
        String name = "";
        for (int i = 0; i < ls.size(); i++) {
            if (i == 0) {
                name += ls.get(i).getLoadTypeName();
            } else {
                name += ", " + ls.get(i).getLoadTypeName();
            }
        }
        return name;
    }

    public static List<Timestamp> getListDateByFromDateAndToDate(String fromDate, String toDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Timestamp> timestampList = new ArrayList<>();
        try {
            long startTime = dateFormat.parse(fromDate).getTime();
            long endTime = dateFormat.parse(toDate).getTime();
            timestampList.add(new Timestamp(startTime));
            while (startTime < endTime) {
                startTime += 86400000; // Thêm 1 ngày (24 giờ)
                timestampList.add(new Timestamp(startTime));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestampList;
    }

    private SettingShiftEp getListShiftEp(Timestamp viewTime, SettingShift st, String schema, String devices, String projectId) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.ES.DATE_FORMAT_YMD);
        Map<String, Object> con = new HashMap<>();
        con.put("schema", schema);
        con.put("devices", devices);
        con.put("shiftId", st.getId());
        con.put("project", projectId);
        con.put("fromDate", sdf.format(viewTime) + " " + st.getStartTime());
        con.put("toDate", sdf.format(viewTime) + " " + st.getEndTime());
        SettingShiftEp shiftEp = this.reportService.getEpByShiftAndViewTime(con);
        return shiftEp;
    }

    private SettingShiftEp getSettingShifEp(Timestamp viewTime, Integer shiftId, List<SettingShiftEp> lsEp) {
        SettingShiftEp ep = null;
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.ES.DATE_FORMAT_YMD);
        for (SettingShiftEp a : lsEp) {
            if (sdf.format(a.getViewTime()).equals(sdf.format(viewTime)) && a.getShiftId() == shiftId) {
                ep = a;
            }
        }
        return ep;
    }
}