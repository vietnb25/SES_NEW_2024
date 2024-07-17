package vn.ses.s3m.plus.controllers;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.text.ParseException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xddf.usermodel.PresetColor;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.zeroturnaround.zip.ZipUtil;
import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.*;
import vn.ses.s3m.plus.service.*;

@RestController
@RequestMapping ("/common/manufacture")
public class ManufactureController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ManufactureService manufactureService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ShiftSettingService shiftSettingService;
    private final Log log = LogFactory.getLog(ManufactureController.class);
    @Value("${consumer.producer.export-folder}")
    private String folderName;
    /**
     * Lấy danh sách theo dõi sản xuất
     *
     * @return Danh sách theo dõi sản xuất
     */
    // CHECKSTYLE:OFF
    @GetMapping ("/getDataPqs")
    public ResponseEntity<List<DataPqs>> getDataPqs(@RequestParam ("customerId") final Integer customerId,

                                                    @RequestParam ("fromDate") final String fromDate, @RequestParam ("toDate") final String toDate,
                                                    @RequestParam ("deviceId") final String deviceId) {

        List<DataPqs> data = new ArrayList<>();

        Map<String, Object> condition = new HashMap<>();
        String schema = Schema.getSchemas(customerId);
        condition.put("schema", schema);

        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put("deviceId", deviceId);

        List<DataPqs> listDataPqs = manufactureService.getDataPqsManufactures(condition);
//        System.out.println(listDataPqs);
        return new ResponseEntity<List<DataPqs>>(listDataPqs, HttpStatus.OK);
    }
    @GetMapping ("")
    public ResponseEntity<List<Manufacture>> getManufactures(@RequestParam ("customerId") final Integer customerId,
        @RequestParam ("systemTypeId") final Integer systemTypeId, @RequestParam ("projectId") final Integer projectId,
        @RequestParam ("fromDate") final String fromDate, @RequestParam ("toDate") final String toDate,
        @RequestParam ("deviceIds") final String deviceIds, @RequestParam ("productionId") final Integer productionId,
        @RequestParam ("productionStepId") final Integer productionStepId) {

        List<Manufacture> data = new ArrayList<>();
        List<Manufacture> dataShow = new ArrayList<>();
        Map<String, Object> condition = new HashMap<>();
        String schema = Schema.getSchemas(customerId);
        condition.put("schema", schema);
        condition.put("projectId", projectId);
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put("deviceIds", deviceIds);
        condition.put("productionId", productionId);
        condition.put("productionStepId", productionStepId);
        data = manufactureService.getManufactures(condition);

        Date startDate = new Date();
        Date endDate = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat();
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            startDate = formatter.parse(fromDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            endDate = formatter.parse(toDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(startDate);
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(endDate);
        for (Date date = startTime.getTime(); !startTime.after(endTime); startTime.add(Calendar.DATE,
            1), date = startTime.getTime()) {

            Manufacture manufactureItem = new Manufacture();
            Boolean isNull = true;
            SimpleDateFormat format = new SimpleDateFormat();
            format = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = format.format(date);
            condition.put("time", format.format(date));
            String shiftHistoryCode = customerId + "-" + systemTypeId + "-" + projectId;
            condition.put("shiftHistoryCode", shiftHistoryCode);
            ShiftSetting TimeShift = shiftSettingService.getShiftHistorysLikeShiftHistoryCode(condition);
            if (TimeShift == null) {
                TimeShift = new ShiftSetting();
            }
            for (Manufacture item : data) {
                if (item.getViewTime()
                    .equals(strDate)) {
                    isNull = false;
                    item.setShift1Date(TimeShift.getShift1());
                    item.setShift2Date(TimeShift.getShift2());
                    item.setShift3Date(TimeShift.getShift3());
                    dataShow.add(item);
                    System.out.println("dataShow" +item);
                    break;
                }
            }

            if (isNull) {
                manufactureItem.setSystemTypeId(systemTypeId);
                manufactureItem.setProjectId(projectId);
                manufactureItem.setDeviceIds(deviceIds);
                manufactureItem.setProductionId(productionId);
                manufactureItem.setProductionStepId(productionStepId);
                manufactureItem.setShift1Date(TimeShift.getShift1());
                manufactureItem.setShift2Date(TimeShift.getShift2());
                manufactureItem.setShift3Date(TimeShift.getShift3());
                manufactureItem.setViewTime(strDate);
                System.out.println("manufactureItem inNull: "+manufactureItem);
                manufactureService.addViewTimeManufactures(schema, manufactureItem);
                dataShow.add(manufactureItem);
                System.out.println("isnull la "+ manufactureItem);
            }
        }
        System.out.println("fromdate"+ fromDate);
        System.out.println("toDate"+ toDate);
        return new ResponseEntity<List<Manufacture>>(dataShow, HttpStatus.OK);
    }

    /**
     * Thêm bản tin theo dõi sản xuất vào DB.
     *
     * @param new manufacture or update manufacture
     * @return Thông báo kết quả thêm mới (200: Thành công, Other: Thất bại).
     */
    @PostMapping ("/add/{customerId}")
    public ResponseEntity<?> addManufactures(@PathVariable final String customerId,
        @RequestBody final List<Manufacture> data) throws Exception {
        String schema = Schema.getSchemas(Integer.parseInt(customerId));
        if (data.size() > 0) {
            try {
                for (Manufacture item : data) {
                    // Kiểm tra dữ liệu đã có trong data
                    if (item.getId() != null) {
                        // có dữ liệu thì update
                        LocalDateTime ldt = LocalDateTime.now();
                        item.setUpdateDate(DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH)
                            .format(ldt));
                        manufactureService.updateManufactures(schema, item);
                    } else {
                        // không có dữ liệu thì insert vào data
                        LocalDateTime ldt = LocalDateTime.now();
                        item.setUpdateDate(DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH)
                            .format(ldt));
                        manufactureService.addManufactures(schema, item);
                        System.out.println("add ne "+ item);
                    }
                }
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    private Double testNull(String a) {
        if(a == "null") {
            return Double.valueOf(0);
        }
        return Double.valueOf(a);
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
        cs.setDataFormat(format.getFormat("###,000 " + unit));
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
    private void formatBorder(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet, final Cell cell, final HorizontalAlignment hAlign) {

        Font font = wb.createFont();
        DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        cs.setAlignment(hAlign);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setWrapText(true);
        font.setFontName("Times New Roman");
        cs.setDataFormat(format.getFormat("###,##0.00 "));
        cell.setCellStyle(cs);
        RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
        RegionUtil.setBorderTop(BorderStyle.MEDIUM, region, sheet);
        RegionUtil.setBorderLeft(BorderStyle.MEDIUM, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.MEDIUM, region, sheet);
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

    }

    private void formatExcelTableMain(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet, final Cell cell,
                             final short bgColor, final HorizontalAlignment hAlign, final int indent) {


        CellStyle cs = wb.createCellStyle();
        cs.setFillBackgroundColor(bgColor);
        cs.setFillForegroundColor(bgColor);
        cs.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);
        Row row = sheet.getRow(10); // Replace 0 with the row index you want to modify
        row.setHeightInPoints(40);

        DataFormat format = wb.createDataFormat();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 15);
        font.setFontName("Times New Roman");
        font.setColor(IndexedColors.WHITE.getIndex());
        cs.setFont(font);
        cs.setAlignment(hAlign);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setIndention((short) indent);
        cs.setWrapText(true);
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);
        cs.setDataFormat(format.getFormat("##0,##0"));
        RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
        RegionUtil.setBorderTop(BorderStyle.MEDIUM, region, sheet);
        RegionUtil.setBorderLeft(BorderStyle.MEDIUM, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.MEDIUM, region, sheet);

    }

    @GetMapping("/export")
    public ResponseEntity<?> export(@RequestParam("prefix") String prefixs,
                                    @RequestParam("devices") String devices, @RequestParam("time") String time) {
        long miliseconds = new Date().getTime();
        String path = this.folderName + File.separator + miliseconds;

        System.out.println(time);
        String[] prefix = prefixs.split("@");
        Integer customerId = Integer.parseInt(prefix[0]);
        Integer projectId = Integer.parseInt(prefix[1]);
        Integer systemTypeId = Integer.parseInt(prefix[2]);

        String reportName = prefix[3];
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

            String[] str = devices.split(",");
            List<Integer> deviceIds = new ArrayList<>();
            for (String a : str) {
                deviceIds.add(Integer.parseInt(a));
            }
            String a = "";
            for (int i = 0; i < deviceIds.size(); i++) {
                if(i == 0) {
                    a = deviceIds.get(0) + "";
                }else {
                    a += "," + deviceIds.get(i);
                }
            }

            condition.put("deviceIds", a);
        String path1 = StringUtils.stripAccents(this.folderName + File.separator + miliseconds + reportName + "-" + custtomer.getCustomerName() + "-" + moduleName + "-" + strDate2);
        System.out.println("path 1"+ path1);
        List<Manufacture> data = manufactureService.exportManufactures(condition);
        System.out.println("data la: "+data);
        System.out.println("condition la: "+condition);
            if (data.size() <= 0) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            try {
                exportManufactures(data, custtomer.getCustomerName().toUpperCase(), custtomer.getDescription(),
                        reportName, systemTypeId, moduleName, project.getProjectName(), deviceNameList,
                        fromDate, toDate, strDate, path1, null, null,
                        null, devices,schema,projectId,deviceNameList
                );
//                System.out.println(data);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        log.info("ReportController.downloadReport() START");
        File f = new File(path1);
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
            log.info("ReportController.downloadReport() error");
            return new ResponseEntity<Resource>(HttpStatus.BAD_REQUEST);
        }
    }
    private void exportManufactures(final List<Manufacture> listData, String customerName, String description,
                                         String reportName, Integer systemTypeId, String moduleName,
                                         String siteName, String deviceNameList, String fromDate, String toDate,
                                         final String dateTime, final String path, Integer deviceTypeId, String deviceTypeName,
                                         Integer device,String devices,String schema, Integer projectId,String devicesName)
            throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");
        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Manufactures");
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

        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);



        // set độ rộng của cột
        sheet1.setColumnWidth(0, 8000);
        sheet1.setColumnWidth(1, 8000);
        sheet1.setColumnWidth(2, 8000);
        sheet1.setColumnWidth(3, 8000);
        sheet1.setColumnWidth(4, 8000);
        sheet1.setColumnWidth(5, 8000);

        int startRow = 11;
        int endRow = 100;
        int rowHeight = 800; // Replace 1000 with your desired row height in twips

        for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
            Row row11 = sheet1.getRow(rowNum);

            // Create the row if it doesn't exist
            if (row11 == null) {
                row11 = sheet1.createRow(rowNum);
            }

            // Set the row height
            row11.setHeight((short) rowHeight);
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

        //NGÀY TẠO
        cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
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


        //TABLE CHÍNH
        // cột số thứ tự
//        region = new CellRangeAddress(10, 10, 0, 0);
//        cell = sheet1.getRow(10).getCell(0);
//        cell.setCellValue("STT");
//        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(10, 10, 0, 0);
        cell = sheet1.getRow(10).getCell(0);
        cell.setCellValue("View Time");
        formatExcelTableMain(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(10, 10, 1, 1);
        cell = sheet1.getRow(10).getCell(1);
        cell.setCellValue("Ca làm việc 1");
        formatExcelTableMain(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(10, 10, 2, 2);
        cell = sheet1.getRow(10).getCell(2);
        cell.setCellValue("Ca làm việc 2");
        formatExcelTableMain(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(10, 10, 3, 3);
        cell = sheet1.getRow(10).getCell(3);
        cell.setCellValue("Ca làm việc 3");
        formatExcelTableMain(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(10, 10, 4, 4);
        cell = sheet1.getRow(10).getCell(4);
        cell.setCellValue("Đơn vị tổng");
        formatExcelTableMain(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(10, 10, 5, 5);
        cell = sheet1.getRow(10).getCell(5);
        cell.setCellValue("Doanh thu");
        formatExcelTableMain(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 1);

//        region = new CellRangeAddress(10, 10, 5, 5);
//        cell = sheet1.getRow(10).getCell(5);
//        cell.setCellValue("EP 1");
//        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
//
//        region = new CellRangeAddress(10, 10, 6, 6);
//        cell = sheet1.getRow(10).getCell(6);
//        cell.setCellValue("EP 2");
//        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
//
//        region = new CellRangeAddress(10, 10, 7, 7);
//        cell = sheet1.getRow(10).getCell(7);
//        cell.setCellValue("EP 3");
//        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        int countRow = 11;
//        for (int i = 0; i < listData.size(); i++) {
//            cell = sheet1.getRow(countRow).getCell(0);
//            cell.setCellValue(i + 1);
//            countRow++;
//        }
        countRow = 11;

        for (Manufacture dataPqs : listData) {
            int rowMerge = countRow;
            region = new CellRangeAddress(countRow, countRow + 1, 0, 0);
            sheet1.addMergedRegion(region);
            cell = sheet1.getRow(countRow).getCell(0);
            cell.setCellValue(dataPqs.getViewTime());
            formatBorder(wb, region, sheet1, cell, HorizontalAlignment.CENTER);
//            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            countRow = countRow + 2;
        }

//        Cột Đơn Vị Và EP đầu tiên
        countRow = 11;
        for (Manufacture dataPqs : listData) {
            region = new CellRangeAddress(countRow, countRow, 1, 1);
            cell = sheet1.getRow(countRow).getCell(1);
            cell.setCellValue(dataPqs.getShift1() == null ? "" : dataPqs.getShift1()+" Đơn vị");
            formatBorder(wb, region, sheet1, cell, HorizontalAlignment.CENTER);
            countRow++;
            countRow++;
        }

        countRow = 12;
        for (Manufacture dataPqs : listData) {
            region = new CellRangeAddress(countRow, countRow, 1, 1);
            cell = sheet1.getRow(countRow).getCell(1);
            cell.setCellValue(dataPqs.getEp1() == null ? "" :dataPqs.getEp1()+" kWH");
            formatBorder(wb, region, sheet1, cell, HorizontalAlignment.CENTER);
            countRow++;
            countRow++;
        }


//
//        Cột Đơn Vị Và EP thứ 2
        countRow = 11;
        for (Manufacture dataPqs : listData) {
            region = new CellRangeAddress(countRow, countRow, 2, 2);
            cell = sheet1.getRow(countRow).getCell(2);
            cell.setCellValue(dataPqs.getShift2() == null ? "" : dataPqs.getShift2()+" Đơn vị");
            formatBorder(wb, region, sheet1, cell, HorizontalAlignment.CENTER);
            countRow++;
            countRow++;
        }

        countRow = 12;
        for (Manufacture dataPqs : listData) {
            region = new CellRangeAddress(countRow, countRow, 2, 2);
            cell = sheet1.getRow(countRow).getCell(2);
            cell.setCellValue(dataPqs.getEp2() == null ? "" :dataPqs.getEp2()+" kWH");
            formatBorder(wb, region, sheet1, cell, HorizontalAlignment.CENTER);
            countRow++;
            countRow++;
        }


//        Cột Đơn Vị Và EP thứ 3
        countRow = 11;
        for (Manufacture dataPqs : listData) {
            region = new CellRangeAddress(countRow, countRow, 3, 3);
            cell = sheet1.getRow(countRow).getCell(3);
            cell.setCellValue(dataPqs.getShift3() == null ? "" : dataPqs.getShift3()+" Đơn vị");
            formatBorder(wb, region, sheet1, cell, HorizontalAlignment.CENTER);
            countRow++;
            countRow++;
        }

        countRow = 12;
        for (Manufacture dataPqs : listData) {
            region = new CellRangeAddress(countRow, countRow, 3, 3);
            cell = sheet1.getRow(countRow).getCell(3);
            cell.setCellValue(dataPqs.getEp3() == null ? "" :dataPqs.getEp3()+" kWH");

            formatBorder(wb, region, sheet1, cell, HorizontalAlignment.CENTER);
            countRow++;
            countRow++;
        }


        //Cột đơn vị tổng
        countRow = 11;
        for (Manufacture dataPqs : listData) {
            region = new CellRangeAddress(countRow, countRow, 4, 4);
            cell = sheet1.getRow(countRow).getCell(4);
            cell.setCellValue(dataPqs.getTotalUnit() == null  ? "" : dataPqs.getTotalUnit() + " Đơn vị");
            formatBorder(wb, region, sheet1, cell, HorizontalAlignment.CENTER);
            countRow++;
            countRow++;
        }

        countRow = 12;
        for (Manufacture dataPqs : listData) {
            region = new CellRangeAddress(countRow, countRow, 4, 4);
            cell = sheet1.getRow(countRow).getCell(4);
            cell.setCellValue(dataPqs.getEp1()+dataPqs.getEp2()+dataPqs.getEp3()+" kWh");
            formatBorder(wb, region, sheet1, cell, HorizontalAlignment.CENTER);
            countRow++;
            countRow++;
        }

        //Cột đơn Doanh thu
        countRow = 11;
        for (Manufacture dataPqs : listData) {
            System.out.println(dataPqs);
            region = new CellRangeAddress(countRow, countRow, 5, 5);
            cell = sheet1.getRow(countRow).getCell(5);
            cell.setCellValue(dataPqs.getRavenue() == null ? "VNĐ" :dataPqs.getRavenue()+" VNĐ");
            formatBorder(wb, region, sheet1, cell, HorizontalAlignment.CENTER);
            countRow++;
            countRow++;
        }

        countRow = 12;
        for (Manufacture dataPqs : listData) {

            region = new CellRangeAddress(countRow, countRow, 5, 5);
            cell = sheet1.getRow(countRow).getCell(5);
            cell.setCellValue("-");
            formatBorder(wb, region, sheet1, cell, HorizontalAlignment.CENTER);
            countRow++;
            countRow++;
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
//        com.spire.xls.Workbook workbook = new com.spire.xls.Workbook();
//        workbook.loadFromFile(exportFilePath);
//        String pdf = path + File.separator + url + ".pdf";
//        // // Fit to page
//        workbook.getConverterSetting().setSheetFitToPage(true);
//
//        // Save as PDF document
//        workbook.saveToFile(pdf);
        ZipUtil.pack(folder, new File(path + ".zip"));
    }
}