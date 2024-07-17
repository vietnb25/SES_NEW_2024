package vn.ses.s3m.plus.controllers;


import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xddf.usermodel.PresetColor;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeroturnaround.zip.ZipUtil;
import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.*;
import vn.ses.s3m.plus.form.ManufactureShiftForm;
import vn.ses.s3m.plus.response.ManufactureShiftResponse;
import vn.ses.s3m.plus.response.ProductionStepResponse;
import vn.ses.s3m.plus.service.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/common/manufacture-shift")
public class ManufactureShiftController {
    @Autowired
    private ManufactureShiftService service;
    @Autowired
    private SettingShiftService settingShiftService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ProductionService productionService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProjectService projectService;

    @Value(value = "${consumer.producer.export-folder}")
    private String folderName;
    Logger log = LoggerFactory.getLogger(ManufactureShiftController.class);

    @GetMapping("list")
    public ResponseEntity<List<ManufactureShiftResponse>> getListManufactureByProductionStep(
            @RequestParam("customer") Integer customer,
            @RequestParam("project") Integer project,
            @RequestParam(name = "production", required = false) Integer production,
            @RequestParam(name = "productionStep", required = false) Integer productionStep
    ) {
        log.info("ManufactureShiftController -> GET LIST MANUFACTURE START()");
        Map<String, Object> con = new HashMap<>();
        String schema = Schema.getSchemas(customer);
        con.put("schema", schema);
        con.put("project", project);
        con.put("production", production);
        con.put("productionStep", productionStep);
        List<Production> ls = this.service.getListManufactureByProductionStep(con);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<ManufactureShiftResponse> responses = new ArrayList<>();
        for (Production pr : ls) {
            responses.add(new ManufactureShiftResponse(pr, getListDeviceByStringDeviceIds(pr.getDeviceIds())));
        }

        log.info("ManufactureShiftController -> GET LIST MANUFACTURE END()");
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PostMapping("/add-manufacture/{id}")
    public ResponseEntity<?> addManufactureAndProductionStep(@RequestBody Production data, @PathVariable("id") Integer customer) {
        log.info("ManufactureShiftController -> POST ADD MANUFACTURE START()");
        String schema = Schema.getSchemas(customer);
        Map<String, Object> condition = new HashMap<>();
        condition.put("schema", schema);
        if (data.getProductionStepId() == null) {
            Production production = new Production();
            production.setProductionStepName(data.getProductionStepName());
            production.setProductionId(data.getProductionId());
            this.productionService.addProductionStep(schema, production);
//            Production productionNew = this.productionService.getNewProductionStep(condition);
//            condition.put("productionStepId", productionNew.getProductionStepId());
//            condition.put("devices", sortDevices(data.getDeviceIds().split(",")));
//            this.service.addManufacturre(condition);
        } else {
            condition.put("productionStepId", data.getProductionStepId());
            condition.put("devices", sortDevices(data.getDeviceIds().split(",")));
            this.service.addManufacturre(condition);
        }
        log.info("ManufactureShiftController -> POST ADD MANUFACTURE END()");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("list/ep-by-shift")
    public ResponseEntity<?> getEpByViewTimeAndShiftId(
            @RequestParam("shift") String shift,
            @RequestParam("project") Integer project,
            @RequestParam("customer") Integer customer,
            @RequestParam("devices") String devices,
            @RequestParam("fromDate") String fromDate,
            @RequestParam("toDate") String toDate
    ) throws ParseException {
        log.info("ManufactureShiftController -> GET LIST EP BY SHIFT START()");

        String[] shiftIds = shift.split(",");
        Map<String, Object> con = new HashMap<>();

        String schema = Schema.getSchemas(customer);
        con.put("project", project);
        con.put("devices", devices);
        con.put("schema", schema);
        List<SettingShiftEp> ls = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

//       Lấy ca làm việc
        Map<String, Object> conditionShift = new HashMap<>();
        conditionShift.put("project", project);
        List<SettingShift> lsShift = settingShiftService.getSettingShiftByProject(conditionShift);
        List<Timestamp> lsTime = getListDateByFromDateAndToDate(fromDate, toDate);
        for (Timestamp t : lsTime) {
            for (int i = 0; i < lsShift.size(); i++) {
                SettingShiftEp shiftEp = getShiftEp(t, lsShift.get(i), schema, devices, String.valueOf(project));
                if (shiftEp != null) {
                    ls.add(shiftEp);
                }
            }
        }
//        for (String id : shiftIds) {
//            SettingShift st = this.settingShiftService.getSettingShiftById(Integer.valueOf(id));
//            if (!compareTimes(st.getStartTime(), st.getEndTime())) {
//                con.put("shiftId", st.getId());
//                con.put("fromDate", getPreviousDay(viewTime) + " " + st.getStartTime());
//                con.put("toDate", viewTime + " " + st.getEndTime());
//                SettingShiftEp sep = this.reportService.getEpByShiftAndViewTime(con);
//                if (sep != null) {
//                    sep.setViewTime(Timestamp.valueOf(viewTime + " 00:00:00"));
//                    ls.add(sep);
//                }
//            } else {
//                con.put("shiftId", st.getId());
//                con.put("fromDate", viewTime + " " + st.getStartTime());
//                con.put("toDate", viewTime + " " + st.getEndTime());
//                SettingShiftEp sep = this.reportService.getEpByShiftAndViewTime(con);
//                if (sep != null) {
//                    ls.add(sep);
//                }
//            }
//        }
        log.info("ManufactureShiftController -> GET LIST EP BY SHIFT END()");
        return new ResponseEntity<>(ls, HttpStatus.OK);
    }

    @GetMapping("list/manufacture-detail")
    public ResponseEntity<?> getListManufactureDetail(
            @RequestParam(value = "customer", required = true) Integer customer,
            @RequestParam(value = "manufacture", required = true) String manufactureId,
            @RequestParam(value = "fromDate", required = true) String fromDate,
            @RequestParam(value = "toDate", required = true) String toDate
    ) throws ParseException {
        log.info("ManufactureShiftController -> GET LIST DETAIL START()");
        Map<String, Object> con = new HashMap<>();
        String schema = Schema.getSchemas(customer);
        con.put("schema", schema);
        con.put("manufactureId", manufactureId);
        con.put("fromDate", fromDate);
        con.put("toDate", toDate);
        List<ManufactureShiftDetail> ls = this.service.getListManufactureDetailByViewTimeAndManufacture(con);
        log.info("ManufactureShiftController -> GET LIST DETAIL END()");
        return new ResponseEntity<>(ls, HttpStatus.OK);
    }

    @PostMapping("add/manufacture-detail")
    public ResponseEntity<?> addManufactureDetail(@RequestBody List<ManufactureShiftDetail> details
            , @RequestParam("customer") Integer customer) {
        Map<String, Object> con = new HashMap<>();
        String schema = Schema.getSchemas(customer);
        con.put("schema", schema);
        for (ManufactureShiftDetail d : details) {
            if (d.getId() != 0 && d.getId() != null) {
                log.info("ManufactureShiftController -> UPDATE ManufactureDetail START()");
                con.put("productionNumber", d.getProductionNumber());
                if (d.getTotalRevenue() != null || d.getTotalRevenue() != 0) {
                    con.put("totalRevenue", d.getTotalRevenue());
                }
                con.put("id", d.getId());
                this.service.updateManufactureDetail(con);
            } else {
                log.info("ManufactureShiftController -> INSERT ManufactureDetail START()");
                this.service.insertManufactureDetail(d, schema);
            }
        }
        log.info("ManufactureShiftController -> INSERT,UPDATE ManufactureDetail SUCCESS");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("update/manufacture-detail/revenue")
    public ResponseEntity<?> updateManufactureDetailRevenue(@RequestBody List<ManufactureShiftDetail> details
            , @RequestParam("customer") Integer customer) {
        Map<String, Object> con = new HashMap<>();
        String schema = Schema.getSchemas(customer);
        con.put("schema", schema);
        for (ManufactureShiftDetail d : details) {
            con.put("viewTime", d.getViewTime());
            con.put("manufacture", d.getManufactureId());
            con.put("revenue", d.getTotalRevenue());
            this.service.updateManufactureDetailRevenue(con);
        }
        log.info("ManufactureShiftController -> UPDATE ManufactureDetail Revenue SUCCESS");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("delete/manufacture")
    public ResponseEntity<?> deleteManufacture(@RequestParam("customer") Integer customer, @RequestParam("id") Integer id) {
        log.info("ManufactureShiftController -> DELETE Manufacture START");
        Map<String, Object> con = new HashMap<>();
        String schema = Schema.getSchemas(customer);
        con.put("schema", schema);
        con.put("id", id);
        this.service.deleteManufacture(con);
        log.info("ManufactureShiftController -> DELETE Manufacture END");
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /*
     * prefixs: customerId@projectId@ProductionId@ProductionStepId@ManufactureId@DeviceId
     * time: formDate@toDate
     *
     * */
    @GetMapping("dowload-list-detail")
    public ResponseEntity<?> dowloadLisDetail(
            @RequestParam("prefix") String prefixs,
            @RequestParam("time") String time,
            @RequestParam("production") String production
    ) throws ParseException {
        log.info("ManufactureShiftController. DOWLOAD FILE Start");
        SimpleDateFormat sdf2 = new SimpleDateFormat("ddMMyyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String dateNow = sdf2.format(new Date());


//      Lấy dữ liệu
        String[] prefix = prefixs.split("@");
        String[] times = time.split("@");
        String[] names = production.split("@");
//      Thời gian
        String fromDate = times[0];
        String toDate = times[1];

//      Tiền tố cơ bản
        String customerId = prefix[0];
        String projectId = prefix[1];
        String productionId = prefix[2];
        String productionStepId = prefix[3];
        String manufactureId = prefix[4];
        String deviceIds = prefix[5];
        String schema = Schema.getSchemas(Integer.valueOf(customerId));

        Map<String, Object> conditionShift = new HashMap<>();
        conditionShift.put("project", projectId);
        List<SettingShift> lsShift = settingShiftService.getSettingShiftByProject(conditionShift);

        List<SettingShiftEp> lsEP = new ArrayList<>();
        List<Timestamp> lsTime = getListDateByFromDateAndToDate(fromDate, toDate);
        for (Timestamp t : lsTime) {
            for (int i = 0; i < lsShift.size(); i++) {
                SettingShiftEp shiftEp = getShiftEp(t, lsShift.get(i), schema, deviceIds, projectId);
                if (shiftEp != null) {
                    lsEP.add(shiftEp);
                }
            }
        }
        List<ManufactureShiftDetail> lsManufacture = getManufactureDetail(schema, manufactureId, fromDate, toDate);


        String path1 = StringUtils.stripAccents(this.folderName + File.separator + new Date().getTime() + "QLSX" + dateNow);

//        get customer
        Map<String, String> cus = new HashMap<>();
        cus.put("customerId", customerId + "");
        Customer custtomer = customerService.getCustomer(cus);
//        get project
        Map<String, String> pro = new HashMap<>();
        pro.put("projectId", projectId + "");
        Project project = projectService.getProject(pro);

        try {
            createFileExcell(custtomer.getCustomerName().toUpperCase(), custtomer.getDescription(), "Quản lý sản xuất", project.getProjectName(),
                    fromDate, toDate, sdf.format(new Date()), path1, lsManufacture, lsTime, lsEP, lsShift, names[0], names[1], names[2]);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        File f = new File(path1);
        log.info(f.getName());
        if (f.exists()) {
            log.info("ManufactureShiftController check file exists");
            String contentType = "application/zip";
            String headerValue = "attachment; filename=" + f.getName() + ".zip";
            Path realPath = Paths.get(path1 + ".zip");
            Resource resource = null;
            try {
                resource = new UrlResource(realPath.toUri());
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("ManufactureShiftController DOWLOAD FILE END");
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION).body(resource);

        } else {
            log.info("ManufactureShiftController NOT EXISTS");
            log.info("ManufactureShiftController ERROR");
            return new ResponseEntity<Resource>(HttpStatus.BAD_REQUEST);
        }
    }

    public List<DeviceName> getListDeviceByStringDeviceIds(String deviceIds) {
        List<DeviceName> ls = new ArrayList<>();
        if (deviceIds != null) {
            String[] lsDevice = deviceIds.split(",");
            Map<String, Object> con = new HashMap<>();
            for (String dv : lsDevice) {
                con.put("deviceId", dv);
                DeviceName d = this.deviceService.getNameDevice(con);
                ls.add(d);
            }
            return ls;
        } else {
            return ls;
        }
    }

    public Boolean compareTimes(String startTime, String endTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);
            return end.after(start);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getPreviousDay(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dateStr);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            return sdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public String sortDevices(String[] devices) {
        String a = "";
        String temp = "";
        if (devices.length < 1) {
            a = devices[0];
        } else {
            for (int i = 0; i < devices.length - 1; i++) {
                for (int j = i + 1; j < devices.length; j++) {
                    if (Integer.valueOf(devices[i]) > Integer.valueOf(devices[j])) {
                        temp = devices[i];
                        devices[i] = devices[j];
                        devices[j] = temp;
                    }
                }
            }
            for (int i = 0; i < devices.length; i++) {
                if (i == 0) {
                    a += devices[0];
                } else {
                    a += "," + devices[i];
                }
            }
        }
        return a;
    }

    private SettingShiftEp getShiftEp(Timestamp viewTime, SettingShift st, String schema, String devices, String projectId) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.ES.DATE_FORMAT_YMD);
        Map<String, Object> con = new HashMap<>();
        if (!compareTimes(st.getStartTime(), st.getEndTime())) {
            con.put("schema", schema);
            con.put("devices", devices);
            con.put("shiftId", st.getId());
            con.put("project", projectId);
            con.put("fromDate", getPreviousDay(sdf.format(viewTime)) + " " + st.getStartTime());
            con.put("toDate", sdf.format(viewTime) + " " + st.getEndTime());
            SettingShiftEp shiftEp = this.reportService.getEpByShiftAndViewTime(con);
            if(shiftEp != null) {
                shiftEp.setViewTime(viewTime);
            }
            return shiftEp;
        } else {
            con.put("schema", schema);
            con.put("devices", devices);
            con.put("shiftId", st.getId());
            con.put("project", projectId);
            con.put("fromDate", sdf.format(viewTime) + " " + st.getStartTime());
            con.put("toDate", sdf.format(viewTime) + " " + st.getEndTime());
            SettingShiftEp shiftEp = this.reportService.getEpByShiftAndViewTime(con);
            return shiftEp;
        }
    }

    private List<ManufactureShiftDetail> getManufactureDetail(String schema, String manufactureId, String fromDate, String toDate) {
        Map<String, Object> con = new HashMap<>();
        con.put("schema", schema);
        con.put("manufactureId", manufactureId);
        con.put("fromDate", fromDate);
        con.put("toDate", toDate);
        List<ManufactureShiftDetail> ls = this.service.getListManufactureDetailByViewTimeAndManufacture(con);
        return ls;
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


    private void createFileExcell(String customerName, String description, String reportName,
                                  String siteName,
                                  String fromDate,
                                  String toDate,
                                  final String dateTime,
                                  final String path,
                                  List<ManufactureShiftDetail> lsManufacture,
                                  List<Timestamp> lsViewTime,
                                  List<SettingShiftEp> lsShiftEp,
                                  List<SettingShift> lsShift,
                                  String productionName,
                                  String productionStepName,
                                  String unit
    )
            throws Exception {
        log.info("ManufactureShiftController: CREATE FILE EXCEL START");

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
        for (int i = 0; i < 24; i++) {
            sheet1.setColumnWidth(i, 5000);
        }


        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, lsShift.size() <= 4 ? 5 : lsShift.size() + 1);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0).getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, lsShift.size() <= 4 ? 5 : lsShift.size() + 1);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1).getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, lsShift.size() <= 4 ? 4 : lsShift.size() + 1);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2).getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
                HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, lsShift.size() <= 4 ? 4 : lsShift.size() + 1);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4).getCell(0);
        cell.setCellValue(reportName.toUpperCase());
        formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, lsShift.size() <= 4 ? 5 : lsShift.size() + 1);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5).getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7).getCell(0);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Sản phẩm
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7).getCell(1);
        cell.setCellValue("Sản phẩm");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // công đoạn
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7).getCell(2);
        cell.setCellValue("Công đoạn sản xuất");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Đơn vị
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7).getCell(3);
        cell.setCellValue("Đơn vị");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 4, 4);
        cell = sheet1.getRow(7).getCell(4);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8).getCell(0);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
//      Gias trij san pham
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8).getCell(1);
        cell.setCellValue(productionName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
//      gia tri cong doan san xuat
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8).getCell(2);
        cell.setCellValue(productionStepName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
//      gia tri don vi
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8).getCell(3);
        cell.setCellValue(unit);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);


        //     Cột giá trị thời gian
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8).getCell(4);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        region = new CellRangeAddress(8, 8, 5, 5);
        cell = sheet1.getRow(8).getCell(5);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);


//     Cột viewTime
        region = new CellRangeAddress(10, 10, 0, 0);
        cell = sheet1.getRow(10).getCell(0);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        formatExcelBorder(region, sheet1);
//       Cột doanh thu
        region = new CellRangeAddress(10, 10, 0, lsShift.size() + 1);
        cell = sheet1.getRow(10).getCell(lsShift.size() + 1);
        cell.setCellValue("Tổng");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        formatExcelBorder(region, sheet1);

        int countRowViewTime = 11;
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.ES.DATE_FORMAT_DMY);
        int totalNumber = 0;
        double totalEp = 0;
        double totalCost = 0;
        for (int j = 0; j < lsViewTime.size(); j++) {
            region = new CellRangeAddress(countRowViewTime, countRowViewTime + 2, 0, 0);
            sheet1.addMergedRegion(region);
            cell = sheet1.getRow(countRowViewTime).getCell(0);
            cell.setCellValue(sdf.format(lsViewTime.get(j)));
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            formatExcelBorder(region, sheet1);
            ManufactureShiftDetail mn = null;
            SettingShiftEp ep = null;
            // Cột Ca làm việc
            for (int i = 0; i < lsShift.size(); i++) {
                mn = getManufacureShiftDetail(lsViewTime.get(j), lsShift.get(i).getId(), lsManufacture);
                ep = getSettingShifEp(lsViewTime.get(j), lsShift.get(i).getId(), lsShiftEp);
                region = new CellRangeAddress(10, 10, 1, i + 1);
                cell = sheet1.getRow(10).getCell(1 + i);
                cell.setCellValue(lsShift.get(i).getShiftName());
                formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
                formatExcelBorder(region, sheet1);
                //   Giá trị số lượng sản phẩm
                region = new CellRangeAddress(countRowViewTime, countRowViewTime, 1, i + 1);
                cell = sheet1.getRow(countRowViewTime).getCell(1 + i);
                cell.setCellValue(mn != null ? mn.getProductionNumber() : 0);
                formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
                formatExcelBorder(region, sheet1);
                //   Giá trị EP theo ca làm việc
                region = new CellRangeAddress(countRowViewTime + 1, countRowViewTime + 1, 1, i + 1);
                cell = sheet1.getRow(countRowViewTime + 1).getCell(1 + i);
                cell.setCellValue(ep != null ? ep.getEpTotal() + " kWh" : 0 + " kWh");
                formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
                formatExcelBorder(region, sheet1);
                //   Giá trị EP theo ca làm việc
                region = new CellRangeAddress(countRowViewTime + 2, countRowViewTime + 2, 1, i + 1);
                cell = sheet1.getRow(countRowViewTime + 2).getCell(1 + i);
                cell.setCellValue(ep != null ? (ep.getHighCost() + ep.getNormalCost() + ep.getLowCost()) + " VND" : 0 + " VND");
                formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
                formatExcelBorder(region, sheet1);

                totalNumber+=(mn == null ? 0 :mn.getProductionNumber());
                totalEp+=(ep == null ? 0 :ep.getEpTotal());
                totalCost+=(ep == null ? 0 :(ep.getHighCost() + ep.getLowCost() + ep.getNormalCost()));
            }
//                Giá trị cột tổng


            region = new CellRangeAddress(countRowViewTime, countRowViewTime, lsShift.size() + 1, lsShift.size() + 1);
            cell = sheet1.getRow(countRowViewTime).getCell(lsShift.size() + 1);
            cell.setCellValue(totalNumber);
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            formatExcelBorder(region, sheet1);
            totalNumber = 0;
            region = new CellRangeAddress(countRowViewTime + 1, countRowViewTime+1, lsShift.size() + 1, lsShift.size() + 1);
            cell = sheet1.getRow(countRowViewTime+1).getCell(lsShift.size() + 1);
            cell.setCellValue(totalEp);
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            formatExcelBorder(region, sheet1);

            region = new CellRangeAddress(countRowViewTime+ 2, countRowViewTime+ 2, lsShift.size() + 1, lsShift.size() + 1);
            cell = sheet1.getRow(countRowViewTime+ 2).getCell(lsShift.size() + 1);
            cell.setCellValue(totalCost);
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            formatExcelBorder(region, sheet1);

            countRowViewTime += 3;
        }


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
            log.info("ManufactureShiftController: CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ManufactureShiftController: ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
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
        com.spire.xls.Workbook workbook = new com.spire.xls.Workbook();
        workbook.loadFromFile(exportFilePath);
        String pdf = path + File.separator + StringUtils.stripAccents(reportName) + ".pdf";
        // Fit to page
        workbook.getConverterSetting().setSheetFitToPage(true);

//		 Save as PDF document
        workbook.saveToFile(pdf);
        ZipUtil.pack(folder, new File(path + ".zip"));

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
        cs.setDataFormat(format.getFormat("##0"));
    }

    private ManufactureShiftDetail getManufacureShiftDetail(Timestamp viewTime, Integer shiftId, List<ManufactureShiftDetail> lsDetail) {
        ManufactureShiftDetail mn = null;
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.ES.DATE_FORMAT_YMD);
        for (ManufactureShiftDetail a : lsDetail) {
            if (sdf.format(a.getViewTime()).equals(sdf.format(viewTime)) && a.getShiftId() == shiftId) {
                mn = a;
            }
        }
        return mn;
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

    private void formatExcelBorder(final CellRangeAddress region, final Sheet sheet) {
        RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
        RegionUtil.setBorderTop(BorderStyle.MEDIUM, region, sheet);
        RegionUtil.setBorderLeft(BorderStyle.MEDIUM, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.MEDIUM, region, sheet);
    }


}
