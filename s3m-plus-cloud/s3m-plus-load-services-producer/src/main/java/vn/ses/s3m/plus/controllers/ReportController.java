package vn.ses.s3m.plus.controllers;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

import com.spire.xls.Workbook;

import org.apache.commons.collections.keyvalue.MultiKey;
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
import org.apache.poi.xddf.usermodel.chart.AxisCrosses;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.BarDirection;
import org.apache.poi.xddf.usermodel.chart.BarGrouping;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.XDDFBarChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zeroturnaround.zip.ZipUtil;

import vn.ses.s3m.plus.common.CommonUtils;
import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.DataLoadFrame1;
import vn.ses.s3m.plus.dto.Report;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.form.JsonFormExcelDataLoadFrame1;
import vn.ses.s3m.plus.service.ReportService;

@RestController
@RequestMapping ("/load")

public class ReportController {
    @Autowired
    private ReportService reportService;
    @Value ("${load.producer.export-folder}")
    private String folderName;

    /** Logging */
    private final Log log = LogFactory.getLog(ReportController.class);

    /**
     * Lấy thông tin của báo cáo.
     *
     * @param userName Tên người dùng
     * @return Thông tin báo cáo.
     */
    @SuppressWarnings ("rawtypes")
    @GetMapping ("/report/{userName}/{projectId}")
    public ResponseEntity<Map<String, List>> loadReport(@PathVariable String userName, @PathVariable String projectId) {
        log.info("ReportController.loadReport() START");
        int userId = reportService.getUserId(userName);
        Map<String, List> data = new HashMap<>();
        Map<String, String> condition = new HashMap<>();
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("projectId", projectId);
        conditions.put("userId", userId);
        condition.put("projectId", projectId);
        List<Map<String, Object>> device = reportService.getDevice(condition);
        List<Report> report = reportService.getReport(conditions);
        data.put("listReport", report);
        data.put("listDevice", device);
        log.info("ReportController.loadReport() END");
        return new ResponseEntity<Map<String, List>>(data, HttpStatus.OK);
    }

    /**
     * Xóa thông tin báo cáo.
     *
     * @param id Mã thông báo.
     * @return Trả về 200(xóa báo cáo thành công).
     */
    @DeleteMapping ("/report/delete/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable final int id) {
        log.info("ReportController.deleteReport() START");
        reportService.deleteReport(id);
        log.info("ReportController.deleteReport() END");
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * Tải thông tin báo cáo.
     *
     * @param path Đường dẫn của file báo cáo.
     * @return File chứa báo cáo định dạng .zip.
     */
    @GetMapping ("/report/download")
    public ResponseEntity<Resource> downloadReport(@RequestParam final String path) throws Exception {
        // Gửi zip qua client
        log.info("ReportController.downloadReport() START");
        File f = new File(folderName + File.separator + path);
        System.out.println("file:" + path);
        if (f.exists()) {
            log.info("ReportController.downloadReport() check file exists");
            String contentType = "application/zip";
            String headerValue = "attachment; filename=" + f.getName() + ".zip";

            Path realPath = Paths.get(folderName + File.separator + path + ".zip");
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

    /**
     * Thêm mới yêu cầu tạo báo cáo.
     *
     * @param deviceId Mã thiết bị.
     * @param reportType Loại báo cáo.
     * @param fromDate Từ ngày .
     * @param toDate Đến ngày.
     * @param projectId Mã dự án.
     * @param userName Tên người dùng.
     * @return Trả về 200(Thêm mới thông tin thành công).
     */
    @GetMapping ("/report/addReport/{customerId}/{deviceId}/{reportType}/{date}/{userName}/{projectId}/{dateType}")
    public ResponseEntity<Void> addReport(@PathVariable final String customerId, @PathVariable final String deviceId,
        @PathVariable final String reportType, @PathVariable final String date, final @PathVariable
        String userName, @PathVariable final String projectId, @PathVariable final String dateType)
        throws ParseException {
        log.info("ReportController.addReport() START");
        int userId = reportService.getUserId(userName);
        // conditions
        List<DataLoadFrame1> infoFrame1 = new ArrayList<>();
        Map<String, String> condition = new HashMap<>();
        condition.put("schema", Schema.getSchemas(Integer.valueOf(customerId)));
        // Định dạng thời gian
        String fromDate;
        String toDate;
        String date_Time;
        if (dateType.equals("1")) {
            fromDate = date + " 00:00:00";
            toDate = date + " 23:59:59";
            date_Time = date;

        } else if (dateType.equals("2")) {
            fromDate = date + "-01 00:00:00";
            toDate = date + "-31 23:59:59";
            date_Time = date + "-01";
        } else {
            fromDate = date + "-01-01 00:00:00";
            toDate = date + "-12-31 23:59:59";
            date_Time = date + "-01-01";
        }

        // Truy vấn theo thời gian
        int tableIndex = CommonUtils.calculateDataIndex(date_Time);
        String tableFrame1 = (String) Constants.DATA.DATA_TABLES
            .get(new MultiKey(Constants.DATA.tables[tableIndex], Constants.DATA.MESSAGE.FRAME1));
        String tableFrame2 = (String) Constants.DATA.DATA_TABLES
            .get(new MultiKey(Constants.DATA.tables[tableIndex], Constants.DATA.MESSAGE.FRAME2));
        // Điều kiện truy vấn
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put("s3mDataTableFrame1", tableFrame1);
        condition.put("s3mDataTableFrame2", tableFrame2);
        condition.put("projectId", projectId);
        if (!deviceId.equals("all")) {
            condition.put("deviceId", deviceId);
        }

        // List warning
        List<Map<String, String>> totalWarning = reportService.getWarningLoad(condition);
        // Định dạng thời gian
        // insert report
        long url = new Date().getTime();
        Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());
        Report report = new Report();
        report.setDateType(date);
        report.setReportId(Integer.valueOf(reportType));
        report.setDeviceId(deviceId);
        report.setReportDate(timestamp);
        report.setUpdated(timestamp);
        report.setUrl(String.valueOf(url));
        report.setStatus(0);
        report.setPercent(0);
        report.setProjectId(Integer.valueOf(projectId));
        report.setUserId(userId);
        report.setSystemType("1");

        if (reportType.equals("1")) {

            infoFrame1 = reportService.getDataLoadFrame1Limit(condition);
            // check trống dữ liệu
            if (infoFrame1.size() > 0 || totalWarning.size() > 0) {
                reportService.addReport(report);
                log.info("ReportController.addReport() ADD REPORT SUCCESS");
                return new ResponseEntity<Void>(HttpStatus.OK);
            } else {
                log.info("ReportController.addReport() ADD REPORT ERROR");
                return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
            }
        }
        log.info("ReportController.addReport() END");
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * Tạo báo cáo và lưu vào trong máy.
     *
     * @param deviceId Mã thiết bị.
     * @param reportType Loại báo cáo.
     * @param fromDate Từ ngày.
     * @param toDate Đến ngày.
     * @param projectId Mã dự án.
     * @return Trả về 200(Tạo file báo cáo thành công) 404(Tạo báo cáo thất bại).
     * @throws Exception.
     */
    @PostMapping ("/report/generateReports/{customerId}/{deviceId}/{reportType}/{date}/{dateType}/{userName}/{projectId}")
    public ResponseEntity<?> generateReports(@PathVariable final String customerId, @PathVariable final String deviceId,
        @PathVariable final String reportType, @PathVariable final String date, @PathVariable final String dateType,
        final @PathVariable
        String userName, @PathVariable final String projectId, @RequestBody final User user) throws Exception {

        log.info("ReportController.generateReports() START");
        Map<String, String> condition = new HashMap<>();
        // get url image
        String pngImageURL = user.getImg();
        String encodingPrefix = "base64,";
        int contentStartIndex = pngImageURL.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData1 = org.apache.commons.codec.binary.Base64
            .decodeBase64(pngImageURL.substring(contentStartIndex));

        // load%
        Map<String, String> mapReport = new HashMap<>();
        int userId = reportService.getUserId(userName);
        // Định dạng thời gian
        String fromDate;
        String toDate;
        String date_Time;
        if (dateType.equals("1")) {
            fromDate = date + " 00:00:00";
            toDate = date + " 23:59:59";
            date_Time = date;

        } else if (dateType.equals("2")) {
            fromDate = date + "-01 00:00:00";
            toDate = date + "-31 23:59:59";
            date_Time = date + "-01";
        } else {
            fromDate = date + "-01-01 00:00:00";
            toDate = date + "-12-31 23:59:59";
            date_Time = date + "-01-01";
        }
        // Truy vấn theo thời gian
        int tableIndex = CommonUtils.calculateDataIndex(date_Time);
        String tableFrame1 = (String) Constants.DATA.DATA_TABLES
            .get(new MultiKey(Constants.DATA.tables[tableIndex], Constants.DATA.MESSAGE.FRAME1));
        String tableFrame2 = (String) Constants.DATA.DATA_TABLES
            .get(new MultiKey(Constants.DATA.tables[tableIndex], Constants.DATA.MESSAGE.FRAME2));
        // Điều kiện truy vấn
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put("s3mDataTableFrame1", tableFrame1);
        condition.put("s3mDataTableFrame2", tableFrame2);
        if (!deviceId.equals("all")) {
            condition.put("deviceId", deviceId);
        }
        condition.put("projectId", projectId);
        condition.put("schema", Schema.getSchemas(Integer.valueOf(customerId)));

        Map<String, Integer> map = new HashMap<>();
        map.put("userId", userId);
        map.put("limit", 5);
        List<Report> list = reportService.getListByLimit(map);
        Report report1 = list.get(0);
        // load %
        mapReport.put("id", String.valueOf(report1.getId()));

        if (reportType.equals("1")) {
            // Tất cả thiết bị
            // 0->5 22->23 giờ thấp điểm nonpeak hour
            // 6-> 11 17->21 giờ cao điểm peak hour
            // 12-> 16 -> giờ bình thường normal hour
            String path = null;
            path = folderName + File.separator + report1.getUrl();
            // get projectName
            String projectName = reportService.getProjectName(projectId);

            // List warning
            List<Map<String, String>> totalWarning = reportService.getWarningLoad(condition);

            Map<String, String> dataWarning = new HashMap<>();
            // Tổng số cảnh báo theo thời gian
            String WarningTotal = reportService.getTotalWarningLoad(condition);
            for (int i = 0; i < totalWarning.size(); i++) {
                int warningType = Integer.valueOf(totalWarning.get(i)
                    .get("warning_type"));
                switch (warningType) {
                    case Constants.WarningType.NGUONG_AP_CAO:
                        dataWarning.put("nguongApCao", String.valueOf(totalWarning.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningType.NGUONG_AP_THAP:
                        dataWarning.put("nguongApThap", String.valueOf(totalWarning.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningType.NHIET_DO_TIEP_XUC:
                        dataWarning.put("nhietDoTiepXuc", String.valueOf(totalWarning.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningType.LECH_PHA:
                        dataWarning.put("lechPha", String.valueOf(totalWarning.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningType.NGUOC_PHA:
                        dataWarning.put("nguocPha", String.valueOf(totalWarning.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningType.LECH_AP_PHA:
                        dataWarning.put("lechApPha", String.valueOf(totalWarning.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningType.COS_THAP_TONG:
                        dataWarning.put("heSoCongSuatThap", String.valueOf(totalWarning.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningType.QUA_TAI:
                        dataWarning.put("quaTai", String.valueOf(totalWarning.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningType.TAN_SO_THAP:
                        dataWarning.put("tanSoThap", String.valueOf(totalWarning.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningType.TAN_SO_CAO:
                        dataWarning.put("tanSoCao", String.valueOf(totalWarning.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningType.MAT_NGUON_PHA:
                        dataWarning.put("matNguon", String.valueOf(totalWarning.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningType.NGUONG_HAI_BAC_N:
                        dataWarning.put("nguongMeoSongN", String.valueOf(totalWarning.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningType.NGUONG_TONG_HAI:
                        dataWarning.put("nguongTongMeoSongHai", String.valueOf(totalWarning.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningType.DONG_TRUNG_TINH:
                        dataWarning.put("quaDongTrungTinh", String.valueOf(totalWarning.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningType.DONG_TIEP_DIA:
                        dataWarning.put("quaDongTiepDia", String.valueOf(totalWarning.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningType.CANH_BAO_1:
                        dataWarning.put("canhBao1", String.valueOf(totalWarning.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningType.CANH_BAO_2:
                        dataWarning.put("canhBao2", String.valueOf(totalWarning.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningType.DONG_MO_CUA:
                        dataWarning.put("dongMoCua", String.valueOf(totalWarning.get(i)
                            .get("TOTAL")));
                        break;
                    default:
                        break;
                }
            }

            // Tạo báo cáo điện năng tiêu thụ cho tất cả thiết bị
            // Khung giờ theo từng thời điểm
            String[] low = {"00:00", "01:00", "02:00", "03:00", "22:00", "23:00"};
            String[] normal = {"04:00", "05:00", "06:00", "07:00", "08:00", "12:00", "13:00", "14:00", "15:00", "16:00",
                "20:00", "21:00"};
            String[] hight = {"09:00", "10:00", "11:00", "17:00", "18:00", "19:00"};

            if (dateType.equals("1")) {

                List<DataLoadFrame1> listData = new ArrayList<>();
                listData = reportService.getElectricalPowerInDay(condition);
                List<JsonFormExcelDataLoadFrame1> jsonForms = new ArrayList<>();
                // TỔNG điện năng theo giờ trong ngay
                long epTotal = 0;
                long epNonpeakHour = 0;
                long epPeakHour = 0;
                long epNormalHour = 0;

                for (int k = 0; k < 24; k++) {
                    String currentDate = null;

                    List<Long> epLows = new ArrayList<>();
                    List<Long> epNormals = new ArrayList<>();
                    List<Long> epHights = new ArrayList<>();
                    JsonFormExcelDataLoadFrame1 js = new JsonFormExcelDataLoadFrame1();
                    if (k < 10) {
                        currentDate = date + " 0" + k + ":00:00";
                    } else {
                        currentDate = date + " " + k + ":00:00";
                    }
                    for (int j = 0; j < listData.size(); j++) {
                        DataLoadFrame1 dataLoadFrame1 = listData.get(j);
                        String viewTime = dataLoadFrame1.getViewTime();

                        if (StringUtils.equals(viewTime, currentDate)) {
                            js.setTimeView(viewTime);
                            String[] viewTimes = viewTime.split(" ");
                            String[] times = viewTimes[1].split(":");
                            String time = times[0] + ":" + times[1];
                            boolean checkLow = Arrays.stream(low)
                                .anyMatch(time::equals);
                            boolean checkNomal = Arrays.stream(normal)
                                .anyMatch(time::equals);
                            boolean checkHight = Arrays.stream(hight)
                                .anyMatch(time::equals);

                            if (checkLow) {
                                epLows.add(Long.valueOf(dataLoadFrame1.getEp()));
                            } else if (checkNomal) {
                                epNormals.add(Long.valueOf(dataLoadFrame1.getEp()));
                            } else if (checkHight) {
                                epHights.add(Long.valueOf(dataLoadFrame1.getEp()));
                            }
                        }

                    }
                    long epLow = epLows.stream()
                        .mapToLong(Long::longValue)
                        .sum();
                    long epNormal = epNormals.stream()
                        .mapToLong(Long::longValue)
                        .sum();
                    long epHight = epHights.stream()
                        .mapToLong(Long::longValue)
                        .sum();

                    long total = epLow + epNormal + epHight;
                    js.setLow(epLow);
                    js.setNormal(epNormal);
                    js.setHight(epHight);
                    js.setTimeView(currentDate);
                    js.setTotal(total);
                    jsonForms.add(js);
                    epNonpeakHour = epNonpeakHour + epLow;
                    epNormalHour = epNormalHour + epNormal;
                    epPeakHour = epPeakHour + epHight;

                }
                // Tổng điện năng trong tháng
                epTotal = epNonpeakHour + epNormalHour + epPeakHour;

                createElectricalPowerExcelsInDay(jsonForms, date, epTotal, epPeakHour, epNormalHour, epNonpeakHour,
                    path, mapReport, projectName, WarningTotal, dataWarning, imageData1);
            } else if (dateType.equals("2")) {
                List<DataLoadFrame1> listData = new ArrayList<>();
                listData = reportService.getElectricalPowerInMonth(condition);

                int daysInMonth = 28;
                // CHECKSTYLE:ON
                String[] dateTime = date.split(Constants.ES.HYPHEN_CHARACTER);
                YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(dateTime[0]), Integer.parseInt(dateTime[1]));
                daysInMonth = yearMonthObject.lengthOfMonth();

                Integer day = Integer.valueOf(daysInMonth);

                List<JsonFormExcelDataLoadFrame1> jsonForms = new ArrayList<>();
                // TỔNG điện năng theo giờ trong tháng
                long epTotal = 0;
                long epNonpeakHour = 0;
                long epPeakHour = 0;
                long epNormalHour = 0;
                for (int i = 1; i < day + 1; i++) {
                    JsonFormExcelDataLoadFrame1 js = new JsonFormExcelDataLoadFrame1();
                    String currentDay = null;
                    String currentDate = null;
                    if (i < 10) {
                        currentDay = dateTime[0] + Constants.ES.HYPHEN_CHARACTER + dateTime[1]
                            + Constants.ES.HYPHEN_CHARACTER + "0" + i + " ";
                    } else {
                        currentDay = dateTime[0] + Constants.ES.HYPHEN_CHARACTER + dateTime[1]
                            + Constants.ES.HYPHEN_CHARACTER + i + " ";
                    }

                    List<Long> epLows = new ArrayList<>();
                    List<Long> epNormals = new ArrayList<>();
                    List<Long> epHights = new ArrayList<>();

                    for (int j = 0; j < listData.size(); j++) {
                        DataLoadFrame1 dataLoadFrame1 = listData.get(j);

                        String viewTime = dataLoadFrame1.getViewTime();
                        for (int k = 0; k < 24; k++) {
                            if (k < 10) {
                                currentDate = currentDay + "0" + k + ":00:00";
                            } else {
                                currentDate = currentDay + k + ":00:00";
                            }
                            if (StringUtils.equals(viewTime, currentDate)) {
                                js.setTimeView(viewTime);
                                String[] viewTimes = viewTime.split(" ");
                                String[] times = viewTimes[1].split(":");
                                String time = times[0] + ":" + times[1];
                                boolean checkLow = Arrays.stream(low)
                                    .anyMatch(time::equals);
                                boolean checkNomal = Arrays.stream(normal)
                                    .anyMatch(time::equals);
                                boolean checkHight = Arrays.stream(hight)
                                    .anyMatch(time::equals);

                                if (checkLow) {
                                    epLows.add(Long.valueOf(dataLoadFrame1.getEp()));
                                } else if (checkNomal) {
                                    epNormals.add(Long.valueOf(dataLoadFrame1.getEp()));
                                } else if (checkHight) {
                                    epHights.add(Long.valueOf(dataLoadFrame1.getEp()));
                                }

                            }
                        }

                    }

                    long epLow = epLows.stream()
                        .mapToLong(Long::longValue)
                        .sum();
                    long epNormal = epNormals.stream()
                        .mapToLong(Long::longValue)
                        .sum();
                    long epHight = epHights.stream()
                        .mapToLong(Long::longValue)
                        .sum();

                    long total = epLow + epNormal + epHight;
                    js.setLow(epLow);
                    js.setNormal(epNormal);
                    js.setHight(epHight);
                    js.setTimeView(currentDay);
                    js.setTotal(total);
                    jsonForms.add(js);
                    epNonpeakHour = epNonpeakHour + epLow;
                    epNormalHour = epNormalHour + epNormal;
                    epPeakHour = epPeakHour + epHight;
                }
                // Tổng điện năng trong tháng
                epTotal = epNonpeakHour + epNormalHour + epPeakHour;
                // Tạo báo cáo thông số điện năng cho tất cả thiết bị theo tháng
                createElectricalPowerExcelsInMonth(date, path, mapReport, projectName, WarningTotal, dataWarning,
                    jsonForms, epNonpeakHour, epNormalHour, epPeakHour, epTotal, imageData1);

            } else {
                // Tạo báo cáo thông số điện năng cho tất cả thiết bị theo năm
                List<DataLoadFrame1> listData = new ArrayList<>();
                listData = reportService.getElectricalPowerInYear(condition);

                String dateMonth = null;
                int daysInMonth = 28;
                List<JsonFormExcelDataLoadFrame1> jsonForms = new ArrayList<>();
                long epTotalInYear = 0;
                long epNonpeakHourInYear = 0;
                long epPeakHourInYear = 0;
                long epNormalHourInYear = 0;
                for (int f = 1; f < 13; f++) {
                    if (f < 10) {
                        dateMonth = date + "-0" + f;
                    } else {
                        dateMonth = date + "-" + f;
                    }

                    // CHECKSTYLE:ON
                    String[] dateTime = dateMonth.split(Constants.ES.HYPHEN_CHARACTER);
                    YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(dateTime[0]),
                        Integer.parseInt(dateTime[1]));
                    daysInMonth = yearMonthObject.lengthOfMonth();

                    Integer day = Integer.valueOf(daysInMonth);
                    // TỔNG điện năng theo giờ trong năm
                    long epTotal = 0;
                    long epNonpeakHour = 0;
                    long epPeakHour = 0;
                    long epNormalHour = 0;
                    JsonFormExcelDataLoadFrame1 js = new JsonFormExcelDataLoadFrame1();
                    for (int i = 1; i < day + 1; i++) {

                        String currentDay = null;
                        String currentDate = null;
                        if (i < 10) {
                            currentDay = dateTime[0] + Constants.ES.HYPHEN_CHARACTER + dateTime[1]
                                + Constants.ES.HYPHEN_CHARACTER + "0" + i + " ";
                        } else {
                            currentDay = dateTime[0] + Constants.ES.HYPHEN_CHARACTER + dateTime[1]
                                + Constants.ES.HYPHEN_CHARACTER + i + " ";
                        }

                        List<Long> epLows = new ArrayList<>();
                        List<Long> epNormals = new ArrayList<>();
                        List<Long> epHights = new ArrayList<>();

                        for (int j = 0; j < listData.size(); j++) {
                            DataLoadFrame1 dataLoadFrame1 = listData.get(j);

                            String viewTime = dataLoadFrame1.getViewTime();
                            for (int k = 0; k < 24; k++) {
                                if (k < 10) {
                                    currentDate = currentDay + "0" + k + ":00:00";
                                } else {
                                    currentDate = currentDay + k + ":00:00";
                                }
                                if (StringUtils.equals(viewTime, currentDate)) {
                                    js.setTimeView(viewTime);
                                    String[] viewTimes = viewTime.split(" ");
                                    String[] times = viewTimes[1].split(":");
                                    String time = times[0] + ":" + times[1];
                                    boolean checkLow = Arrays.stream(low)
                                        .anyMatch(time::equals);
                                    boolean checkNomal = Arrays.stream(normal)
                                        .anyMatch(time::equals);
                                    boolean checkHight = Arrays.stream(hight)
                                        .anyMatch(time::equals);

                                    if (checkLow) {
                                        epLows.add(Long.valueOf(dataLoadFrame1.getEp()));
                                    } else if (checkNomal) {
                                        epNormals.add(Long.valueOf(dataLoadFrame1.getEp()));
                                    } else if (checkHight) {
                                        epHights.add(Long.valueOf(dataLoadFrame1.getEp()));
                                    }

                                }
                            }

                        }

                        long epLow = epLows.stream()
                            .mapToLong(Long::longValue)
                            .sum();
                        long epNormal = epNormals.stream()
                            .mapToLong(Long::longValue)
                            .sum();
                        long epHight = epHights.stream()
                            .mapToLong(Long::longValue)
                            .sum();

                        epNonpeakHour = epNonpeakHour + epLow;
                        epNormalHour = epNormalHour + epNormal;
                        epPeakHour = epPeakHour + epHight;
                        epTotal = epNonpeakHour + epNormalHour + epPeakHour;

                    }
                    js.setLow(epNonpeakHour);
                    js.setNormal(epNormalHour);
                    js.setHight(epPeakHour);
                    js.setTimeView(dateMonth);
                    js.setTotal(epTotal);
                    jsonForms.add(js);

                    // epTotalInYear = 0;
                    epNonpeakHourInYear = epNonpeakHourInYear + epNonpeakHour;
                    epPeakHourInYear = epPeakHourInYear + epPeakHour;
                    epNormalHourInYear = epNormalHourInYear + epNormalHour;
                }
                epTotalInYear = epNonpeakHourInYear + epPeakHourInYear + epNormalHourInYear;
                // Tạo báo cáo thông số điện năng cho tất cả thiết bị
                createElectricalPowerExcelsInYear(date, path, mapReport, projectName, WarningTotal, dataWarning,
                    jsonForms, epNonpeakHourInYear, epNormalHourInYear, epPeakHourInYear, epTotalInYear, imageData1);
            }

        }

        reportService.updateStatus(report1.getId());
        reportService.updateTimeFinish(report1.getId());
        log.info("ReportController.generateReports() END");
        return new ResponseEntity<>(HttpStatus.OK);

    }

    /**
     * Tạo excel báo cáo tổng hợp.
     *
     * @param data Thông tin báo cáo tổng hợp.
     * @throws Exception
     */
    private void createElectricalPowerExcelsInDay(final List<JsonFormExcelDataLoadFrame1> listData, final String date,
        final long epTotal, final long epPeakHour, final long epNormalHour, final long epNonpeakHour, final String path,
        final Map<String, String> mapReport, final String projectName, final String WarningTotal,
        final Map<String, String> dataWarning, final byte[] imageData1) throws Exception {
        log.info("ReportController.createElectricalPowerExcelsInDay(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Báo cáo tổng hợp");
        Row row;
        Cell cell;
        // add image
        int pictureIdx = wb.addPicture(imageData1, wb.PICTURE_TYPE_PNG);
        XSSFDrawing drawingImg = sheet1.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(4);
        anchorImg.setCol2(6);
        anchorImg.setRow1(1);
        anchorImg.setRow2(5);
        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("0.000"));
        for (int z = 0; z < 38; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 36; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // Cột header
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 9);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        cell.setCellValue("BÁO CÁO TỔNG HỢP NGÀY " + date);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột tên dự án
        region = new CellRangeAddress(2, 2, 0, 0);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue("Tên dự án");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        region = new CellRangeAddress(2, 2, 1, 3);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(1);
        cell.setCellValue(projectName);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tiêu đề báo cáo
        region = new CellRangeAddress(3, 3, 0, 3);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(3)
            .getCell(0);
        cell.setCellValue("I. BÁO CÁO NĂNG LƯỢNG NGÀY " + date);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột KHUNG GIỜ

        region = new CellRangeAddress(5, 5, 0, 0);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("KHUNG GIỜ ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Giờ thấp điểm
        region = new CellRangeAddress(5, 5, 1, 1);
        cell = sheet1.getRow(5)
            .getCell(1);
        cell.setCellValue("GIỜ THẤP ĐIỂM ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Giờ bình thường
        region = new CellRangeAddress(5, 5, 2, 2);
        cell = sheet1.getRow(5)
            .getCell(2);
        cell.setCellValue("GIỜ BÌNH THƯỜNG ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Giờ cao điểm
        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet1.getRow(5)
            .getCell(3);
        cell.setCellValue("GIỜ CAO ĐIỂM ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Tổng
        region = new CellRangeAddress(5, 5, 4, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(4);
        cell.setCellValue("TỔNG ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột tổng điện năng tiêu thụ
        region = new CellRangeAddress(6, 6, 0, 0);
        cell = sheet1.getRow(6)
            .getCell(0);
        cell.setCellValue("TỔNG ĐIỆN NĂNG TIÊU THỤ [kWh]");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        float epNon = Float.valueOf(epNonpeakHour);
        float epNormal = Float.valueOf(epNormalHour);
        float epNPeak = Float.valueOf(epPeakHour);
        float ep_Total = Float.valueOf(epTotal);

        region = new CellRangeAddress(6, 6, 1, 1);
        cell = sheet1.getRow(6)
            .getCell(1);
        cell.setCellValue(epNon);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(6, 6, 2, 2);
        cell = sheet1.getRow(6)
            .getCell(2);
        cell.setCellValue(epNormal);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(6, 6, 3, 3);
        cell = sheet1.getRow(6)
            .getCell(3);
        cell.setCellValue(epNPeak);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(6, 6, 4, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(6)
            .getCell(4);
        cell.setCellValue(ep_Total);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột giá điện
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("GIÁ ĐIỆN [VNĐ/kWh]");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("1024");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("1581");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("2908");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(7, 7, 4, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(7)
            .getCell(4);
        cell.setCellValue("");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột tiền điện
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue("TIỀN ĐIỆN [VNĐ]");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        String epNonpeakHourVND = currencyVN.format( (epNonpeakHour * 1024));
        String epNormalHourVND = currencyVN.format( (epNormalHour * 1581));
        String epPeakHourVND = currencyVN.format( (epPeakHour * 2908));
        String epTotalVND = currencyVN.format( (epNonpeakHour * 1024 + epNormalHour * 1581 + epPeakHour * 2908));

        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(epNonpeakHourVND);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(epNormalHourVND);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue(epPeakHourVND);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(8, 8, 4, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue(epTotalVND);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(8, 8, 6, 6);
        cell = sheet1.getRow(8)
            .getCell(6);
        cell.setCellValue("        ");

        Cell cell1;
        // Cột TT
        region = new CellRangeAddress(10, 10, 0, 0);
        cell1 = sheet1.getRow(10)
            .getCell(0);
        cell1.setCellValue("TT");
        formatHeaderEp(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột THỜI GIAN
        region = new CellRangeAddress(10, 10, 1, 1);
        cell1 = sheet1.getRow(10)
            .getCell(1);
        cell1.setCellValue("THỜI GIAN");
        formatHeaderEp(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột giá trị
        region = new CellRangeAddress(10, 10, 2, 2);
        cell1 = sheet1.getRow(10)
            .getCell(2);
        cell1.setCellValue("ĐIỆN NĂNG TIÊU THỤ [kWh]");
        formatHeaderEp(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột tổn sản lượng điện năng
        region = new CellRangeAddress(35, 35, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(35)
            .getCell(0);
        cell1.setCellValue("TỔNG");
        formatHeaderEp(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(35, 35, 2, 2);
        cell1 = sheet1.getRow(35)
            .getCell(2);
        cell1.setCellValue(ep_Total);
        formatHeaderEp(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Ghi dữ liệu vào bảng của excel
        int rowCount = 11;
        int count = 1;
        // Thông số load % tải báo cáo
        double sizeReport = listData.size();
        double progressDevice = 100 / sizeReport;
        double progress = progressDevice;
        for (int i = 0; i < listData.size(); i++) {
            // put percent %
            mapReport.put("percent", String.valueOf(progress));

            float ep_Hour_low = Float.valueOf(listData.get(i)
                .getLow());
            float ep_Hour_nomal = Float.valueOf(listData.get(i)
                .getNormal());
            float ep_Hour_hight = Float.valueOf(listData.get(i)
                .getHight());
            // Cột thứ tự
            region = new CellRangeAddress(rowCount, rowCount, 0, 0);
            Cell cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(String.valueOf(count));
            formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 1, 1);
            cellData = sheet1.getRow(rowCount)
                .getCell(1);
            cellData.setCellValue(listData.get(i)
                .getTimeView());
            formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
            // Cột giá trị
            if (i < 4 || i > 21) {
                region = new CellRangeAddress(rowCount, rowCount, 2, 2);
                cellData = sheet1.getRow(rowCount)
                    .getCell(2);
                cellData.setCellValue(ep_Hour_low);
                formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
            }
            if (i > 3 && i < 9 || i > 11 && i < 17 || i > 19 && i < 22) {
                region = new CellRangeAddress(rowCount, rowCount, 2, 2);
                cellData = sheet1.getRow(rowCount)
                    .getCell(2);
                cellData.setCellValue(ep_Hour_nomal);
                formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
            }
            if (i > 8 && i < 12 || i > 16 && i < 20) {
                region = new CellRangeAddress(rowCount, rowCount, 2, 2);
                cellData = sheet1.getRow(rowCount)
                    .getCell(2);
                cellData.setCellValue(ep_Hour_hight);
                formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
            }

            rowCount += 1;
            count += 1;

            // update % load
            progress = progress + progressDevice;

            reportService.updatePercent(mapReport);
            if (sizeReport == Double.valueOf(i + 1)) {
                mapReport.put("percent", "95");
                reportService.updatePercent(mapReport);
            }

        }
        // Tạo biểu đồ sản lượng điện năng theo ngày
        XSSFDrawing drawing = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(9, 19, 9, 19, 3, 10, 15, 36);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("BIỂU ĐỒ SẢN LƯỢNG ĐIỆN NĂNG NGÀY " + date);
        chart.setTitleOverlay(false);

        // add legend(các kí hiệu của mốc dữ liệu bên phải)
        // XDDFChartLegend legend = chart.getOrAddLegend();
        // legend.setPosition(LegendPosition.TOP_RIGHT);

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("THỜI GIAN");
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("SẢN LƯỢNG ĐIỆN NĂNG [kWh]");
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        XDDFDataSource<
            String> countries = XDDFDataSourcesFactory.fromStringCellRange(sheet1, new CellRangeAddress(11, 34, 1, 1));

        XDDFNumericalDataSource<
            Double> values = XDDFDataSourcesFactory.fromNumericCellRange(sheet1, new CellRangeAddress(11, 34, 2, 2));

        // set tiêu đề của biểu đồ( dưới và trái)
        XDDFChartData dataChart = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        XDDFChartData.Series series1 = dataChart.addSeries(countries, values);
        series1.setTitle("THỜI GIAN", null);
        dataChart.setVaryColors(true);
        chart.plot(dataChart);

        // in order to transform a bar chart into a column chart, you just need to
        // change the bar direction
        XDDFBarChartData col = (XDDFBarChartData) dataChart;
        col.setBarDirection(BarDirection.COL);
        //

        // data point colors; is necessary for showing data points in Calc
        // some rgb colors to choose

        // set data point colors
        // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
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
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}

        };

        int pointCount = series1.getCategoryData()
            .getPointCount();
        for (int p = 0; p < pointCount; p++) {
            chart.getCTChart()
                .getPlotArea()
                .getBarChartArray(0)
                .getSerArray(0)
                .addNewDPt()
                .addNewIdx()
                .setVal(p);

            chart.getCTChart()
                .getPlotArea()
                .getBarChartArray(0)
                .getSerArray(0)
                .getDPtArray(p)
                .addNewSpPr()
                .addNewSolidFill()
                .addNewSrgbClr()
                .setVal(colors[p]);
        }

        // set Font Style
        chart.getCTChart()
            .getTitle()
            .getTx()
            .getRich()
            .getPArray(0)
            .getRArray(0)
            .getRPr()
            .addNewLatin()
            .setTypeface("Courier New");

        // Tạo biểu đồ tròn điện năng
        // create drawing and anchor
        XSSFDrawing drawing1 = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchor1 = drawing1.createAnchor(9, 19, 9, 19, 15, 10, 23, 36);
        // create chart
        XSSFChart chart1 = drawing.createChart(anchor1);
        chart1.setTitleText("TỈ LỆ ĐIỆN NĂNG TIÊU THỤ THEO GIỜ NGÀY " + date);
        chart1.setTitleOverlay(false);
        XDDFChartLegend legend = chart1.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);
        XDDFDataSource<
            String> cat = XDDFDataSourcesFactory.fromStringCellRange(sheet1, new CellRangeAddress(5, 5, 1, 3));
        XDDFNumericalDataSource<
            Double> val = XDDFDataSourcesFactory.fromNumericCellRange(sheet1, new CellRangeAddress(6, 6, 1, 3));

        XDDFChartData chartData1 = chart1.createData(ChartTypes.PIE3D, null, null);
        chartData1.setVaryColors(true);
        XDDFChartData.Series series = chartData1.addSeries(cat, val);
        chart1.plot(chartData1);

        // do not auto delete the title; is necessary for showing title in Calc
        if (chart1.getCTChart()
            .getAutoTitleDeleted() == null)
            chart1.getCTChart()
                .addNewAutoTitleDeleted();
        chart1.getCTChart()
            .getAutoTitleDeleted()
            .setVal(false);

        // data point colors; is necessary for showing data points in Calc
        // some rgb colors to choose
        // set data point colors
        // // (144,238,144) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] color = new byte[][] {new byte[] {(byte) 144, (byte) 238, (byte) 144},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}};

        int pointCount1 = series.getCategoryData()
            .getPointCount();
        for (int p = 0; p < pointCount1; p++) {
            chart1.getCTChart()
                .getPlotArea()
                .getPie3DChartArray(0)
                .getSerArray(0)
                .addNewDPt()
                .addNewIdx()
                .setVal(p);
            chart1.getCTChart()
                .getPlotArea()
                .getPie3DChartArray(0)
                .getSerArray(0)
                .getDPtArray(p)
                .addNewSpPr()
                .addNewSolidFill()
                .addNewSrgbClr()
                .setVal(color[p]);
        }
        //

        // set Font Style
        chart1.getCTChart()
            .getTitle()
            .getTx()
            .getRich()
            .getPArray(0)
            .getRArray(0)
            .getRPr()
            .addNewLatin()
            .setTypeface("Courier New");

        // Tiêu đề báo cáo

        // set font style
        Cell cell2;
        CellStyle cs1 = wb.createCellStyle();
        Font font1 = wb.createFont();
        font1.setFontName("Courier New");
        cs1.setFont(font1);
        cs1.setAlignment(HorizontalAlignment.CENTER);
        for (int z = 38; z < 70; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 30; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs1);
            }
        }
        region = new CellRangeAddress(38, 38, 0, 2);
        sheet1.addMergedRegion(region);
        cell2 = sheet1.getRow(38)
            .getCell(0);
        cell2.setCellValue("II. BÁO CÁO SỐ LƯỢNG CẢNH BÁO ");
        formatHeaderEp(wb, region, sheet1, cell2, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột TT
        region = new CellRangeAddress(40, 40, 0, 0);
        cell2 = sheet1.getRow(40)
            .getCell(0);
        cell2.setCellValue("TT");
        formatHeaderEp(wb, region, sheet1, cell2, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột THỜI GIAN
        region = new CellRangeAddress(40, 40, 1, 1);
        cell2 = sheet1.getRow(40)
            .getCell(1);
        cell2.setCellValue("CẢNH BÁO");
        formatHeaderEp(wb, region, sheet1, cell2, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột giá trị
        region = new CellRangeAddress(40, 40, 2, 2);
        cell2 = sheet1.getRow(40)
            .getCell(2);
        cell2.setCellValue("SỐ LẦN");
        formatHeaderEp(wb, region, sheet1, cell2, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // dữ liệu số lượng cảnh báo
        // TT1
        region = new CellRangeAddress(41, 41, 0, 0);
        Cell celldata;
        celldata = sheet1.getRow(41)
            .getCell(0);
        celldata.setCellValue("1");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(41, 41, 1, 1);
        celldata = sheet1.getRow(41)
            .getCell(1);
        celldata.setCellValue("ĐIỆN ÁP CAO");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột số lần
        region = new CellRangeAddress(41, 41, 2, 2);
        celldata = sheet1.getRow(41)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("nguongApCao") != null ? Double.valueOf(dataWarning.get("nguongApCao")) : 0);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // TT2
        region = new CellRangeAddress(42, 42, 0, 0);
        celldata = sheet1.getRow(42)
            .getCell(0);
        celldata.setCellValue("2");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(42, 42, 1, 1);
        celldata = sheet1.getRow(42)
            .getCell(1);
        celldata.setCellValue("ĐIỆN ÁP THẤP");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột số lần
        region = new CellRangeAddress(42, 42, 2, 2);
        celldata = sheet1.getRow(42)
            .getCell(2);
        celldata.setCellValue(
            dataWarning.get("nguongApThap") != null ? Double.valueOf(dataWarning.get("nguongApThap")) : 0);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // TT3
        region = new CellRangeAddress(43, 43, 0, 0);
        celldata = sheet1.getRow(43)
            .getCell(0);
        celldata.setCellValue("3");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(43, 43, 1, 1);
        celldata = sheet1.getRow(43)
            .getCell(1);
        celldata.setCellValue("NHIỆT ĐỘ CAO");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột số lần
        region = new CellRangeAddress(43, 43, 2, 2);
        celldata = sheet1.getRow(43)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("nhietDoCao") != null ? Double.valueOf(dataWarning.get("nhietDoCao")) : 0);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // TT4
        region = new CellRangeAddress(44, 44, 0, 0);
        celldata = sheet1.getRow(44)
            .getCell(0);
        celldata.setCellValue("4");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(44, 44, 1, 1);
        celldata = sheet1.getRow(44)
            .getCell(1);
        celldata.setCellValue("LỆCH PHA");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột số lần
        region = new CellRangeAddress(44, 44, 2, 2);
        celldata = sheet1.getRow(44)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("lechPha") != null ? Double.valueOf(dataWarning.get("lechPha")) : 0);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // TT5
        region = new CellRangeAddress(45, 45, 0, 0);
        celldata = sheet1.getRow(45)
            .getCell(0);
        celldata.setCellValue("5");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(45, 45, 1, 1);
        celldata = sheet1.getRow(45)
            .getCell(1);
        celldata.setCellValue("QUÁ TẢI");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột số lần
        region = new CellRangeAddress(45, 45, 2, 2);
        celldata = sheet1.getRow(45)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("quaTai") != null ? Double.valueOf(dataWarning.get("quaTai")) : 0);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // TT6
        region = new CellRangeAddress(46, 46, 0, 0);
        celldata = sheet1.getRow(46)
            .getCell(0);
        celldata.setCellValue("6");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(46, 46, 1, 1);
        celldata = sheet1.getRow(46)
            .getCell(1);
        celldata.setCellValue("TẦN SỐ CAO");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột số lần
        region = new CellRangeAddress(46, 46, 2, 2);
        celldata = sheet1.getRow(46)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("tanSoCao") != null ? Double.valueOf(dataWarning.get("tanSoCao")) : 0);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // TT7
        region = new CellRangeAddress(47, 47, 0, 0);
        celldata = sheet1.getRow(47)
            .getCell(0);
        celldata.setCellValue("7");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(47, 47, 1, 1);
        celldata = sheet1.getRow(47)
            .getCell(1);
        celldata.setCellValue("TẦN SỐ THẤP");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột số lần
        region = new CellRangeAddress(47, 47, 2, 2);
        celldata = sheet1.getRow(47)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("tanSoThap") != null ? Double.valueOf(dataWarning.get("tanSoThap")) : 0);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // TT8
        region = new CellRangeAddress(48, 48, 0, 0);
        celldata = sheet1.getRow(48)
            .getCell(0);
        celldata.setCellValue("8");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(48, 48, 1, 1);
        celldata = sheet1.getRow(48)
            .getCell(1);
        celldata.setCellValue("MẤT NGUỒN");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột số lần
        region = new CellRangeAddress(48, 48, 2, 2);
        celldata = sheet1.getRow(48)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("matNguon") != null ? Double.valueOf(dataWarning.get("matNguon")) : 0);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // TT9
        region = new CellRangeAddress(49, 49, 0, 0);
        celldata = sheet1.getRow(49)
            .getCell(0);
        celldata.setCellValue("9");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(49, 49, 1, 1);
        celldata = sheet1.getRow(49)
            .getCell(1);
        celldata.setCellValue("SÓNG HÀI CAO");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột số lần
        region = new CellRangeAddress(49, 49, 2, 2);
        celldata = sheet1.getRow(49)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("nguongTongMeoSongHai") != null
            ? Double.valueOf(dataWarning.get("nguongTongMeoSongHai"))
            : 0);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // TT10
        region = new CellRangeAddress(50, 50, 0, 0);
        celldata = sheet1.getRow(50)
            .getCell(0);
        celldata.setCellValue("10");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(50, 50, 1, 1);
        celldata = sheet1.getRow(50)
            .getCell(1);
        celldata.setCellValue("QUÁ DÒNG TRUNG TÍNH");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột số lần
        region = new CellRangeAddress(50, 50, 2, 2);
        celldata = sheet1.getRow(50)
            .getCell(2);
        celldata.setCellValue(
            dataWarning.get("quaDongTrungTinh") != null ? Double.valueOf(dataWarning.get("quaDongTrungTinh")) : 0);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // TT11
        region = new CellRangeAddress(51, 51, 0, 0);
        celldata = sheet1.getRow(51)
            .getCell(0);
        celldata.setCellValue("11");
        cs.setAlignment(HorizontalAlignment.CENTER);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(51, 51, 1, 1);
        celldata = sheet1.getRow(51)
            .getCell(1);
        celldata.setCellValue("HỆ SỐ CÔNG SUẤT THẤP");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột số lần
        region = new CellRangeAddress(51, 51, 2, 2);
        celldata = sheet1.getRow(51)
            .getCell(2);
        celldata.setCellValue(
            dataWarning.get("heSoCongSuatThap") != null ? Double.valueOf(dataWarning.get("heSoCongSuatThap")) : 0);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // TT12
        region = new CellRangeAddress(52, 52, 0, 0);
        celldata = sheet1.getRow(52)
            .getCell(0);
        celldata.setCellValue("12");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(52, 52, 1, 1);
        celldata = sheet1.getRow(52)
            .getCell(1);
        celldata.setCellValue("ĐÓNG MỞ CỬA");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột số lần
        region = new CellRangeAddress(52, 52, 2, 2);
        celldata = sheet1.getRow(52)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("dongMoCua") != null ? Double.valueOf(dataWarning.get("dongMoCua")) : 0);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tổng
        region = new CellRangeAddress(53, 53, 0, 1);
        cell = sheet1.getRow(53)
            .getCell(0);
        cell.setCellValue("Tổng");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột số lần
        region = new CellRangeAddress(53, 53, 2, 2);
        cell = sheet1.getRow(53)
            .getCell(2);
        cell.setCellValue(WarningTotal != null ? WarningTotal : "0");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Tạo biểu đồ cảnh báo
        // Create row and put some cells in it. Rows and cells are 0 based.
        // set kích thước cho biểu đồ
        XSSFDrawing drawingWarning = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchorWarning = drawingWarning.createAnchor(8, 18, 8, 18, 3, 40, 15, 54);

        XSSFChart chartWarning = drawingWarning.createChart(anchorWarning);
        chartWarning.setTitleText("BIỂU ĐỒ SỐ LƯỢNG CẢNH BÁO NGÀY  " + date);
        chartWarning.setTitleOverlay(false);

        // add legend(các kí hiệu của mốc dữ liệu bên phải)
        // XDDFChartLegend legend = chart.getOrAddLegend();
        // legend.setPosition(LegendPosition.TOP_RIGHT);

        XDDFCategoryAxis bottomAxisWarning = chartWarning.createCategoryAxis(AxisPosition.BOTTOM);

        bottomAxisWarning.setTitle("CẢNH BÁO");
        XDDFValueAxis leftAxisWarning = chartWarning.createValueAxis(AxisPosition.LEFT);
        leftAxisWarning.setTitle("SỐ LẦN");
        leftAxisWarning.setCrosses(AxisCrosses.AUTO_ZERO);

        XDDFDataSource<String> countriesWarning = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
            new CellRangeAddress(41, 52, 1, 1));

        XDDFNumericalDataSource<Double> valuesWarning = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
            new CellRangeAddress(41, 52, 2, 2));

        // set tiêu đề của biểu đồ( dưới và trái)
        XDDFChartData dataChartWarning = chartWarning.createData(ChartTypes.BAR, bottomAxisWarning, leftAxisWarning);
        XDDFChartData.Series series1Warning = dataChartWarning.addSeries(countriesWarning, valuesWarning);
        series1Warning.setTitle("CẢNH BÁO", null);
        dataChartWarning.setVaryColors(true);
        chartWarning.plot(dataChartWarning);
        // in order to transform a bar chart into a column chart, you just need to
        // change the bar direction
        XDDFBarChartData colWarning = (XDDFBarChartData) dataChartWarning;
        colWarning.setBarDirection(BarDirection.COL);

        // set màu cho excel
        CTSRgbColor rgbWarning = CTSRgbColor.Factory.newInstance();

        Color col1Warning = new Color(255, 106, 106);
        rgbWarning.setVal(
            new byte[] {(byte) col1Warning.getRed(), (byte) col1Warning.getGreen(), (byte) col1Warning.getBlue()});

        CTSolidColorFillProperties fillPropWarning = CTSolidColorFillProperties.Factory.newInstance();
        fillPropWarning.setSrgbClr(rgbWarning);

        CTShapeProperties ctShapePropertiesWarning = CTShapeProperties.Factory.newInstance();
        ctShapePropertiesWarning.setSolidFill(fillPropWarning);
        chart.getCTChart()
            .getPlotArea()
            .getBarChartList()
            .get(0)
            .getSerList()
            .get(0)
            .setSpPr(ctShapePropertiesWarning);

        IntStream.range(0, 10)
            .forEach(sheet1::autoSizeColumn);
        sheet1.setColumnWidth(2, 5000);
        // export file
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
        workbook.getConverterSetting()
            .setSheetFitToPage(true);

        // Save as PDF document
        workbook.saveToFile(pdf);
        mapReport.put("percent", "100");
        reportService.updatePercent(mapReport);
        ZipUtil.pack(folder, new File(path + ".zip"));
        reportService.updateStatus(Integer.valueOf(mapReport.get("id")));
        reportService.updateTimeFinish(Integer.valueOf(mapReport.get("id")));

    }

    /**
     * Tạo excel báo cáo tổng hợp trong năm .
     *
     * @param data Thông tin báo cáo tổng hợp.
     * @throws Exception
     */

    private void createElectricalPowerExcelsInYear(final String date, final String path,
        final Map<String, String> mapReport, final String projectName, final String WarningTotal,
        final Map<String, String> dataWarning, final List<JsonFormExcelDataLoadFrame1> listData,
        final Long epNonpeakHour, final Long epNormalHour, final Long epPeakHour, final Long epTotal,
        final byte[] imageData1) throws Exception {
        log.info("ReportController.createElectricalPowerExcelsInYear(): START");

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Báo cáo tổng hợp");
        Row row;
        Cell cell;

        // add image
        int pictureIdx = wb.addPicture(imageData1, wb.PICTURE_TYPE_PNG);
        XSSFDrawing drawingImg = sheet1.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(4);
        anchorImg.setCol2(5);
        anchorImg.setRow1(1);
        anchorImg.setRow2(5);
        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);
        // set font style
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("0.000"));
        for (int z = 0; z < 27; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 36; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // Cột header
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 18);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        cell.setCellValue("BÁO CÁO TỔNG HỢP NĂM " + date);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột tên dự án
        region = new CellRangeAddress(2, 2, 0, 0);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue("Tên dự án");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        region = new CellRangeAddress(2, 2, 1, 3);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(1);
        cell.setCellValue(projectName);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tiêu đề báo cáo
        region = new CellRangeAddress(3, 3, 0, 3);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(3)
            .getCell(0);
        cell.setCellValue("I. BÁO CÁO NĂNG LƯỢNG NĂM " + date);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột KHUNG GIỜ

        region = new CellRangeAddress(5, 5, 0, 0);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("KHUNG GIỜ ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Giờ thấp điểm
        region = new CellRangeAddress(5, 5, 1, 1);
        cell = sheet1.getRow(5)
            .getCell(1);
        cell.setCellValue("GIỜ THẤP ĐIỂM ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Giờ bình thường
        region = new CellRangeAddress(5, 5, 2, 2);
        cell = sheet1.getRow(5)
            .getCell(2);
        cell.setCellValue("GIỜ BÌNH THƯỜNG ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Giờ cao điểm
        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet1.getRow(5)
            .getCell(3);
        cell.setCellValue("GIỜ CAO ĐIỂM ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Tổng
        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet1.getRow(5)
            .getCell(4);
        cell.setCellValue("TỔNG ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // Cột tổng điện năng tiêu thụ
        region = new CellRangeAddress(6, 6, 0, 0);
        cell = sheet1.getRow(6)
            .getCell(0);
        cell.setCellValue("TỔNG ĐIỆN NĂNG TIÊU THỤ [kWh]");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        float epNon = Float.valueOf(epNonpeakHour);
        float epNormal = Float.valueOf(epNormalHour);
        float epNPeak = Float.valueOf(epPeakHour);
        float ep_Total = Float.valueOf(epTotal);
        region = new CellRangeAddress(6, 6, 1, 1);
        cell = sheet1.getRow(6)
            .getCell(1);
        cell.setCellValue(epNon);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(6, 6, 2, 2);
        cell = sheet1.getRow(6)
            .getCell(2);
        cell.setCellValue(epNormal);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(6, 6, 3, 3);
        cell = sheet1.getRow(6)
            .getCell(3);
        cell.setCellValue(epNPeak);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(6, 6, 4, 4);
        cell = sheet1.getRow(6)
            .getCell(4);
        cell.setCellValue(ep_Total);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // Cột giá điện
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("GIÁ ĐIỆN [VNĐ/kWh]");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("1024");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("1581");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("2908");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(7, 7, 4, 4);
        cell = sheet1.getRow(7)
            .getCell(4);
        cell.setCellValue("");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // Cột tiền điện
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue("TIỀN ĐIỆN [VNĐ]");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        String epNonpeakHourVND = currencyVN.format( (epNonpeakHour * 1024));
        String epNormalHourVND = currencyVN.format( (epNormalHour * 1581));
        String epPeakHourVND = currencyVN.format( (epPeakHour * 2908));
        String epTotalVND = currencyVN.format( (epNonpeakHour * 1024 + epNormalHour * 1581 + epPeakHour * 2908));

        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(epNonpeakHourVND);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(epNormalHourVND);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue(epPeakHourVND);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue(epTotalVND);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(10, 10, 0, 4);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(10)
            .getCell(0);
        cell.setCellValue("ĐIỆN NĂNG TIÊU THỤ THEO GIỜ [kWh]");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 0);

        // Cột TT
        region = new CellRangeAddress(11, 11, 0, 0);
        cell = sheet1.getRow(11)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột THỜI GIAN
        region = new CellRangeAddress(11, 11, 1, 1);
        cell = sheet1.getRow(11)
            .getCell(1);
        cell.setCellValue("THỜI GIAN");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột giá trị
        region = new CellRangeAddress(11, 11, 2, 2);
        cell = sheet1.getRow(11)
            .getCell(2);
        cell.setCellValue("GIỜ CAO ĐIỂM ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(11, 11, 3, 3);
        cell = sheet1.getRow(11)
            .getCell(3);
        cell.setCellValue("GIỜ BÌNH THƯỜNG ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(11, 11, 4, 4);
        cell = sheet1.getRow(11)
            .getCell(4);
        cell.setCellValue("GIỜ THẤP ĐIỂM ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Ghi dữ liệu vào bảng của excel
        int rowCount = 12;
        int count = 1;
        // Thông số load % tải báo cáo
        double sizeReport = listData.size();
        double progressDevice = 100 / sizeReport;
        double progress = progressDevice;

        for (int i = 0; i < listData.size(); i++) {
            // put percent %
            mapReport.put("percent", String.valueOf(progress));

            float epNon_Hour = Float.valueOf(listData.get(i)
                .getLow());
            float epNormal_Hour = Float.valueOf(listData.get(i)
                .getNormal());
            float epPeak_Hour = Float.valueOf(listData.get(i)
                .getHight());
            // Cột thứ tự
            region = new CellRangeAddress(rowCount, rowCount, 0, 0);
            Cell cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(String.valueOf(count));
            formatHeader(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 1, 1);
            cellData = sheet1.getRow(rowCount)
                .getCell(1);
            cellData.setCellValue(listData.get(i)
                .getTimeView());
            formatHeader(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
            // Cột giá trị giờ thấp điểm
            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount)
                .getCell(2);
            cellData.setCellValue(epNon_Hour);
            formatHeader(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
            // Cột giá trị giờ bình thường
            region = new CellRangeAddress(rowCount, rowCount, 3, 3);
            cellData = sheet1.getRow(rowCount)
                .getCell(3);
            cellData.setCellValue(epNormal_Hour);
            formatHeader(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
            // Cột giá trị giờ cao điểm
            region = new CellRangeAddress(rowCount, rowCount, 4, 4);
            cellData = sheet1.getRow(rowCount)
                .getCell(4);
            cellData.setCellValue(epPeak_Hour);
            formatHeader(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
            rowCount += 1;
            count += 1;

            // update % load
            progress = progress + progressDevice;

            reportService.updatePercent(mapReport);
            if (sizeReport == Double.valueOf(i + 1)) {
                mapReport.put("percent", "95");
                reportService.updatePercent(mapReport);
            }

        }

        // Cột tổn sản lượng điện năng
        region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(rowCount)
            .getCell(0);
        cell.setCellValue("TỔNG");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(rowCount, rowCount, 2, 4);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(rowCount)
            .getCell(2);
        cell.setCellValue(ep_Total);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Tạo biểu đồ sản lượng điện năng theo năm
        XSSFDrawing drawing = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(8, 22, 18, 22, 5, 10, 12, 25);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("BIỂU ĐỒ ĐIỆN NĂNG TIÊU THỤ NĂM " + date);
        chart.setTitleOverlay(false);

        // add legend(các kí hiệu của mốc dữ liệu bên phải)
        // XDDFChartLegend legend = chart.getOrAddLegend();
        // legend.setPosition(LegendPosition.TOP_RIGHT);

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("THỜI GIAN");
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("ĐIỆN NĂNG TIÊU THỤ [kWh]");
        // leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        XDDFDataSource<String> countries = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
            new CellRangeAddress(12, rowCount - 1, 1, 1));

        XDDFNumericalDataSource<Double> value1 = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
            new CellRangeAddress(12, rowCount - 1, 2, 2));

        XDDFNumericalDataSource<Double> values2 = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
            new CellRangeAddress(12, rowCount - 1, 3, 3));

        XDDFNumericalDataSource<Double> values3 = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
            new CellRangeAddress(12, rowCount - 1, 4, 4));

        // set tiêu đề của biểu đồ( dưới và trái)
        XDDFChartData dataChart = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);

        XDDFChartData.Series series1 = dataChart.addSeries(countries, value1);
        series1.setTitle("GIỜ THẤP ĐIỂM ", null);

        XDDFChartData.Series series2 = dataChart.addSeries(countries, values2);
        series2.setTitle("GIỜ BÌNH THƯỜNG", null);
        XDDFChartData.Series series3 = dataChart.addSeries(countries, values3);
        series3.setTitle("GIỜ CAO ĐIỂM", null);

        dataChart.setVaryColors(true);
        chart.plot(dataChart);
        // correcting the overlap so bars really are stacked and not side by side

        chart.getCTChart()
            .getPlotArea()
            .getBarChartArray(0)
            .addNewOverlap()
            .setVal((byte) 100);

        // in order to transform a bar chart into a column chart, you just need to
        // change the bar direction
        XDDFBarChartData col = (XDDFBarChartData) dataChart;
        col.setBarGrouping(BarGrouping.STACKED);
        col.setBarDirection(BarDirection.COL);
        // set color
        // Giờ thấp điểm
        CTSRgbColor rgb1 = CTSRgbColor.Factory.newInstance();

        Color col1 = new Color(144, 238, 144);
        rgb1.setVal(new byte[] {(byte) col1.getRed(), (byte) col1.getGreen(), (byte) col1.getBlue()});

        CTSolidColorFillProperties fillProp1 = CTSolidColorFillProperties.Factory.newInstance();
        fillProp1.setSrgbClr(rgb1);

        CTShapeProperties ctShapeProperties1 = CTShapeProperties.Factory.newInstance();
        ctShapeProperties1.setSolidFill(fillProp1);
        chart.getCTChart()
            .getPlotArea()
            .getBarChartList()
            .get(0)
            .getSerList()
            .get(0)
            .setSpPr(ctShapeProperties1);
        // Giờ bình thường
        CTSRgbColor rgb2 = CTSRgbColor.Factory.newInstance();

        Color col2 = new Color(255, 255, 0);
        rgb2.setVal(new byte[] {(byte) col2.getRed(), (byte) col2.getGreen(), (byte) col2.getBlue()});

        CTSolidColorFillProperties fillProp2 = CTSolidColorFillProperties.Factory.newInstance();
        fillProp2.setSrgbClr(rgb2);

        CTShapeProperties ctShapeProperties2 = CTShapeProperties.Factory.newInstance();
        ctShapeProperties2.setSolidFill(fillProp2);
        chart.getCTChart()
            .getPlotArea()
            .getBarChartList()
            .get(0)
            .getSerList()
            .get(1)
            .setSpPr(ctShapeProperties2);
        // Giờ cap điểm
        CTSRgbColor rgb3 = CTSRgbColor.Factory.newInstance();

        Color col3 = new Color(255, 0, 0);
        rgb3.setVal(new byte[] {(byte) col3.getRed(), (byte) col3.getGreen(), (byte) col3.getBlue()});

        CTSolidColorFillProperties fillProp3 = CTSolidColorFillProperties.Factory.newInstance();
        fillProp3.setSrgbClr(rgb3);

        CTShapeProperties ctShapeProperties3 = CTShapeProperties.Factory.newInstance();
        ctShapeProperties3.setSolidFill(fillProp3);
        chart.getCTChart()
            .getPlotArea()
            .getBarChartList()
            .get(0)
            .getSerList()
            .get(2)
            .setSpPr(ctShapeProperties3);

        // set Font Style
        chart.getCTChart()
            .getTitle()
            .getTx()
            .getRich()
            .getPArray(0)
            .getRArray(0)
            .getRPr()
            .addNewLatin()
            .setTypeface("Courier New");

        // Tạo biểu đồ tròn điện năng
        // create drawing and anchor
        XSSFDrawing drawing1 = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchor1 = drawing1.createAnchor(8, 22, 18, 22, 12, 10, 19, 25);
        // create chart
        XSSFChart chart1 = drawing.createChart(anchor1);
        chart1.setTitleText("TỈ LỆ ĐIỆN NĂNG TIÊU THỤ THEO GIỜ NĂM " + date);
        chart1.setTitleOverlay(false);
        XDDFChartLegend legend = chart1.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);
        XDDFDataSource<
            String> cat = XDDFDataSourcesFactory.fromStringCellRange(sheet1, new CellRangeAddress(5, 5, 1, 3));
        XDDFNumericalDataSource<
            Double> val = XDDFDataSourcesFactory.fromNumericCellRange(sheet1, new CellRangeAddress(6, 6, 1, 3));

        XDDFChartData chartData1 = chart1.createData(ChartTypes.PIE3D, null, null);
        chartData1.setVaryColors(true);
        XDDFChartData.Series series = chartData1.addSeries(cat, val);
        chart1.plot(chartData1);

        // do not auto delete the title; is necessary for showing title in Calc
        if (chart1.getCTChart()
            .getAutoTitleDeleted() == null)
            chart1.getCTChart()
                .addNewAutoTitleDeleted();
        chart1.getCTChart()
            .getAutoTitleDeleted()
            .setVal(false);

        // data point colors; is necessary for showing data points in Calc
        // some rgb colors to choose
        // set data point colors
        // // (144,238,144) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] color = new byte[][] {new byte[] {(byte) 144, (byte) 238, (byte) 144},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}};

        int pointCount1 = series.getCategoryData()
            .getPointCount();
        for (int p = 0; p < pointCount1; p++) {
            chart1.getCTChart()
                .getPlotArea()
                .getPie3DChartArray(0)
                .getSerArray(0)
                .addNewDPt()
                .addNewIdx()
                .setVal(p);
            chart1.getCTChart()
                .getPlotArea()
                .getPie3DChartArray(0)
                .getSerArray(0)
                .getDPtArray(p)
                .addNewSpPr()
                .addNewSolidFill()
                .addNewSrgbClr()
                .setVal(color[p]);
        }
        //

        // set Font Style
        chart1.getCTChart()
            .getTitle()
            .getTx()
            .getRich()
            .getPArray(0)
            .getRArray(0)
            .getRPr()
            .addNewLatin()
            .setTypeface("Courier New");
        // Tiêu đề báo cáo

        // set font style
        CellStyle cs1 = wb.createCellStyle();
        Font font1 = wb.createFont();
        font1.setFontName("Courier New");
        cs1.setFont(font1);
        cs1.setAlignment(HorizontalAlignment.CENTER);
        for (int z = 27; z < 70; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 36; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs1);
            }
        }
        region = new CellRangeAddress(27, 27, 0, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(27)
            .getCell(0);
        cell.setCellValue("II. BÁO CÁO SỐ LƯỢNG CẢNH BÁO ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột TT
        region = new CellRangeAddress(29, 29, 0, 0);
        cell = sheet1.getRow(29)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột THỜI GIAN
        region = new CellRangeAddress(29, 29, 1, 1);
        cell = sheet1.getRow(29)
            .getCell(1);
        cell.setCellValue("CẢNH BÁO");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột giá trị
        region = new CellRangeAddress(29, 29, 2, 2);
        cell = sheet1.getRow(29)
            .getCell(2);
        cell.setCellValue("SỐ LẦN");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // dữ liệu số lượng cảnh báo
        // TT1
        region = new CellRangeAddress(30, 30, 0, 0);
        Cell celldata = sheet1.getRow(30)
            .getCell(0);
        celldata.setCellValue("1");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(30, 30, 1, 1);
        celldata = sheet1.getRow(30)
            .getCell(1);
        celldata.setCellValue("ĐIỆN ÁP CAO");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(30, 30, 2, 2);
        celldata = sheet1.getRow(30)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("nguongApCao") != null ? Double.valueOf(dataWarning.get("nguongApCao")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT2
        region = new CellRangeAddress(31, 31, 0, 0);
        celldata = sheet1.getRow(31)
            .getCell(0);
        celldata.setCellValue("2");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(31, 31, 1, 1);
        celldata = sheet1.getRow(31)
            .getCell(1);
        celldata.setCellValue("ĐIỆN ÁP THẤP");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(31, 31, 2, 2);
        celldata = sheet1.getRow(31)
            .getCell(2);
        celldata.setCellValue(
            dataWarning.get("nguongApThap") != null ? Double.valueOf(dataWarning.get("nguongApThap")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT3
        region = new CellRangeAddress(32, 32, 0, 0);
        celldata = sheet1.getRow(32)
            .getCell(0);
        celldata.setCellValue("3");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(32, 32, 1, 1);
        celldata = sheet1.getRow(32)
            .getCell(1);
        celldata.setCellValue("NHIỆT ĐỘ CAO");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(32, 32, 2, 2);
        celldata = sheet1.getRow(32)
            .getCell(2);
        celldata.setCellValue(
            dataWarning.get("nhietDoTiepXuc") != null ? Double.valueOf(dataWarning.get("nhietDoTiepXuc")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT4
        region = new CellRangeAddress(33, 33, 0, 0);
        celldata = sheet1.getRow(33)
            .getCell(0);
        celldata.setCellValue("4");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(33, 33, 1, 1);
        celldata = sheet1.getRow(33)
            .getCell(1);
        celldata.setCellValue("LỆCH PHA");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(33, 33, 2, 2);
        celldata = sheet1.getRow(33)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("lechPha") != null ? Double.valueOf(dataWarning.get("lechPha")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT5
        region = new CellRangeAddress(34, 34, 0, 0);
        celldata = sheet1.getRow(34)
            .getCell(0);
        celldata.setCellValue("5");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(34, 34, 1, 1);
        celldata = sheet1.getRow(34)
            .getCell(1);
        celldata.setCellValue("QUÁ TẢI");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(34, 34, 2, 2);
        celldata = sheet1.getRow(34)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("quaTai") != null ? Double.valueOf(dataWarning.get("quaTai")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT6
        region = new CellRangeAddress(35, 35, 0, 0);
        celldata = sheet1.getRow(35)
            .getCell(0);
        celldata.setCellValue("6");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(35, 35, 1, 1);
        celldata = sheet1.getRow(35)
            .getCell(1);
        celldata.setCellValue("TẦN SỐ CAO");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(35, 35, 2, 2);
        celldata = sheet1.getRow(35)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("tanSoCao") != null ? Double.valueOf(dataWarning.get("tanSoCao")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT7
        region = new CellRangeAddress(36, 36, 0, 0);
        celldata = sheet1.getRow(36)
            .getCell(0);
        celldata.setCellValue("7");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(36, 36, 1, 1);
        celldata = sheet1.getRow(36)
            .getCell(1);
        celldata.setCellValue("TẦN SỐ THẤP");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(36, 36, 2, 2);
        celldata = sheet1.getRow(36)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("tanSoThap") != null ? Double.valueOf(dataWarning.get("tanSoThap")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT8
        region = new CellRangeAddress(37, 37, 0, 0);
        celldata = sheet1.getRow(37)
            .getCell(0);
        celldata.setCellValue("8");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(37, 37, 1, 1);
        celldata = sheet1.getRow(37)
            .getCell(1);
        celldata.setCellValue("MẤT NGUỒN");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(37, 37, 2, 2);
        celldata = sheet1.getRow(37)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("matNguon") != null ? Double.valueOf(dataWarning.get("matNguon")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT9
        region = new CellRangeAddress(38, 38, 0, 0);
        celldata = sheet1.getRow(38)
            .getCell(0);
        celldata.setCellValue("9");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(38, 38, 1, 1);
        celldata = sheet1.getRow(38)
            .getCell(1);
        celldata.setCellValue("SÓNG HÀI CAO");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(38, 38, 2, 2);
        celldata = sheet1.getRow(38)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("nguongTongMeoSongHai") != null
            ? Double.valueOf(dataWarning.get("nguongTongMeoSongHai"))
            : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT10
        region = new CellRangeAddress(39, 39, 0, 0);
        celldata = sheet1.getRow(39)
            .getCell(0);
        celldata.setCellValue("10");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(39, 39, 1, 1);
        celldata = sheet1.getRow(39)
            .getCell(1);
        celldata.setCellValue("QUÁ DÒNG TRUNG TÍNH");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(39, 39, 2, 2);
        celldata = sheet1.getRow(39)
            .getCell(2);
        celldata.setCellValue(
            dataWarning.get("quaDongTrungTinh") != null ? Double.valueOf(dataWarning.get("quaDongTrungTinh")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT11
        region = new CellRangeAddress(40, 40, 0, 0);
        celldata = sheet1.getRow(40)
            .getCell(0);
        celldata.setCellValue("11");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(40, 40, 1, 1);
        celldata = sheet1.getRow(40)
            .getCell(1);
        celldata.setCellValue("HỆ SỐ CÔNG SUẤT THẤP");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(40, 40, 2, 2);
        celldata = sheet1.getRow(40)
            .getCell(2);
        celldata.setCellValue(
            dataWarning.get("heSoCongSuatThap") != null ? Double.valueOf(dataWarning.get("heSoCongSuatThap")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT12
        region = new CellRangeAddress(41, 41, 0, 0);
        celldata = sheet1.getRow(41)
            .getCell(0);
        celldata.setCellValue("12");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(41, 41, 1, 1);
        celldata = sheet1.getRow(41)
            .getCell(1);
        celldata.setCellValue("ĐÓNG MỞ CỬA");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(41, 41, 2, 2);
        celldata = sheet1.getRow(41)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("dongMoCua") != null ? Double.valueOf(dataWarning.get("dongMoCua")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Tổng
        region = new CellRangeAddress(42, 42, 0, 1);
        cell = sheet1.getRow(42)
            .getCell(0);
        cell.setCellValue("Tổng");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột số lần
        region = new CellRangeAddress(42, 42, 2, 2);
        cell = sheet1.getRow(42)
            .getCell(2);
        cell.setCellValue(WarningTotal != null ? WarningTotal : "0");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Tạo biểu đồ cảnh báo
        // Create row and put some cells in it. Rows and cells are 0 based.
        // set kích thước cho biểu đồ
        XSSFDrawing drawingWarning = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchorWarning = drawingWarning.createAnchor(8, 18, 8, 18, 3, 29, 10, 43);

        XSSFChart chartWarning = drawingWarning.createChart(anchorWarning);
        chartWarning.setTitleText("BIỂU ĐỒ SỐ LƯỢNG CẢNH BÁO THÁNG " + date);
        chartWarning.setTitleOverlay(false);

        // add legend(các kí hiệu của mốc dữ liệu bên phải)
        // XDDFChartLegend legend = chart.getOrAddLegend();
        // legend.setPosition(LegendPosition.TOP_RIGHT);

        XDDFCategoryAxis bottomAxisWarning = chartWarning.createCategoryAxis(AxisPosition.BOTTOM);

        bottomAxisWarning.setTitle("CẢNH BÁO");
        XDDFValueAxis leftAxisWarning = chartWarning.createValueAxis(AxisPosition.LEFT);
        leftAxisWarning.setTitle("SỐ LẦN");
        leftAxisWarning.setCrosses(AxisCrosses.AUTO_ZERO);

        XDDFDataSource<String> countriesWarning = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
            new CellRangeAddress(30, 41, 1, 1));

        XDDFNumericalDataSource<Double> valuesWarning = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
            new CellRangeAddress(30, 41, 2, 2));

        // set tiêu đề của biểu đồ( dưới và trái)
        XDDFChartData dataChartWarning = chartWarning.createData(ChartTypes.BAR, bottomAxisWarning, leftAxisWarning);
        XDDFChartData.Series series1Warning = dataChartWarning.addSeries(countriesWarning, valuesWarning);
        series1Warning.setTitle("CẢNH BÁO", null);
        dataChartWarning.setVaryColors(true);
        chartWarning.plot(dataChartWarning);
        // in order to transform a bar chart into a column chart, you just need to
        // change the bar direction
        XDDFBarChartData colWarning = (XDDFBarChartData) dataChartWarning;
        colWarning.setBarDirection(BarDirection.COL);

        IntStream.range(0, 10)
            .forEach(sheet1::autoSizeColumn);
        // export file
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
            log.info("ReportController.createElectricalPowerExcelsInYear(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info(
                "ReportController.createElectricalPowerExcelsInYear(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
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
        workbook.getConverterSetting()
            .setSheetFitToPage(true);

        // Save as PDF document
        workbook.saveToFile(pdf);

        ZipUtil.pack(folder, new File(path + ".zip"));
        mapReport.put("percent", "100");
        reportService.updatePercent(mapReport);
        reportService.updateStatus(Integer.valueOf(mapReport.get("id")));
        reportService.updateTimeFinish(Integer.valueOf(mapReport.get("id")));
        log.info("ReportController.createElectricalPowerExcelsInYear(): END ");
    }

    /**
     * Tạo excel báo cáo tổng hợp trong tháng .
     *
     * @param data Thông tin báo cáo tổng hợp.
     * @throws Exception
     */

    private void createElectricalPowerExcelsInMonth(final String date, final String path,
        final Map<String, String> mapReport, final String projectName, final String WarningTotal,
        final Map<String, String> dataWarning, final List<JsonFormExcelDataLoadFrame1> listData,
        final Long epNonpeakHour, final Long epNormalHour, final Long epPeakHour, final Long epTotal,
        final byte[] imageData1) throws Exception {
        log.info("ReportController.createElectricalPowerExcelsInMonth(): START");

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Báo cáo tổng hợp");
        Row row;
        Cell cell;

        // add image
        int pictureIdx = wb.addPicture(imageData1, wb.PICTURE_TYPE_PNG);
        XSSFDrawing drawingImg = sheet1.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(4);
        anchorImg.setCol2(5);
        anchorImg.setRow1(1);
        anchorImg.setRow2(5);
        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);
        // set font style
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("0.000"));
        for (int z = 0; z < 44; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 36; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // Cột header
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 19);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        cell.setCellValue("BÁO CÁO TỔNG HỢP THÁNG " + date);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột tên dự án
        region = new CellRangeAddress(2, 2, 0, 0);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue("Tên dự án");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        region = new CellRangeAddress(2, 2, 1, 3);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(1);
        cell.setCellValue(projectName);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tiêu đề báo cáo
        region = new CellRangeAddress(3, 3, 0, 3);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(3)
            .getCell(0);
        cell.setCellValue("I. BÁO CÁO NĂNG LƯỢNG THÁNG " + date);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột KHUNG GIỜ

        region = new CellRangeAddress(5, 5, 0, 0);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("KHUNG GIỜ ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Giờ thấp điểm
        region = new CellRangeAddress(5, 5, 1, 1);
        cell = sheet1.getRow(5)
            .getCell(1);
        cell.setCellValue("GIỜ THẤP ĐIỂM ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Giờ bình thường
        region = new CellRangeAddress(5, 5, 2, 2);
        cell = sheet1.getRow(5)
            .getCell(2);
        cell.setCellValue("GIỜ BÌNH THƯỜNG ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Giờ cao điểm
        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet1.getRow(5)
            .getCell(3);
        cell.setCellValue("GIỜ CAO ĐIỂM ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Tổng
        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet1.getRow(5)
            .getCell(4);
        cell.setCellValue("TỔNG ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // Cột tổng điện năng tiêu thụ
        region = new CellRangeAddress(6, 6, 0, 0);
        cell = sheet1.getRow(6)
            .getCell(0);
        cell.setCellValue("TỔNG ĐIỆN NĂNG TIÊU THỤ [kWh]");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        float epNon = Float.valueOf(epNonpeakHour);
        float epNormal = Float.valueOf(epNormalHour);
        float epNPeak = Float.valueOf(epPeakHour);
        float ep_Total = Float.valueOf(epTotal);

        region = new CellRangeAddress(6, 6, 1, 1);
        cell = sheet1.getRow(6)
            .getCell(1);
        cell.setCellValue(epNon);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(6, 6, 2, 2);
        cell = sheet1.getRow(6)
            .getCell(2);
        cell.setCellValue(epNormal);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(6, 6, 3, 3);
        cell = sheet1.getRow(6)
            .getCell(3);
        cell.setCellValue(epNPeak);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(6, 6, 4, 4);
        cell = sheet1.getRow(6)
            .getCell(4);
        cell.setCellValue(ep_Total);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // Cột giá điện
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("GIÁ ĐIỆN [VNĐ/kWh]");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("1024");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("1581");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("2908");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(7, 7, 4, 4);
        cell = sheet1.getRow(7)
            .getCell(4);
        cell.setCellValue("");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // Cột tiền điện
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue("TIỀN ĐIỆN [VNĐ]");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        String epNonpeakHourVND = currencyVN.format( (epNonpeakHour * 1024));
        String epNormalHourVND = currencyVN.format( (epNormalHour * 1581));
        String epPeakHourVND = currencyVN.format( (epPeakHour * 2908));
        String epTotalVND = currencyVN.format( (epNonpeakHour * 1024 + epNormalHour * 1581 + epPeakHour * 2908));

        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(epNonpeakHourVND);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(epNormalHourVND);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue(epPeakHourVND);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue(epTotalVND);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(10, 10, 0, 4);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(10)
            .getCell(0);
        cell.setCellValue("ĐIỆN NĂNG TIÊU THỤ THEO GIỜ [kWh]");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 0);

        // Cột TT
        region = new CellRangeAddress(11, 11, 0, 0);
        cell = sheet1.getRow(11)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột THỜI GIAN
        region = new CellRangeAddress(11, 11, 1, 1);
        cell = sheet1.getRow(11)
            .getCell(1);
        cell.setCellValue("THỜI GIAN");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột giá trị
        region = new CellRangeAddress(11, 11, 2, 2);
        cell = sheet1.getRow(11)
            .getCell(2);
        cell.setCellValue("GIỜ CAO ĐIỂM ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(11, 11, 3, 3);
        cell = sheet1.getRow(11)
            .getCell(3);
        cell.setCellValue("GIỜ BÌNH THƯỜNG ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(11, 11, 4, 4);
        cell = sheet1.getRow(11)
            .getCell(4);
        cell.setCellValue("GIỜ THẤP ĐIỂM ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Ghi dữ liệu vào bảng của excel
        int rowCount = 12;
        int count = 1;
        // Thông số load % tải báo cáo
        double sizeReport = listData.size();
        double progressDevice = 100 / sizeReport;
        double progress = progressDevice;

        for (int i = 0; i < listData.size(); i++) {
            // put percent %
            mapReport.put("percent", String.valueOf(progress));

            float epNon_Hour = Float.valueOf(listData.get(i)
                .getLow());
            float epNormal_Hour = Float.valueOf(listData.get(i)
                .getNormal());
            float epPeak_Hour = Float.valueOf(listData.get(i)
                .getHight());
            // Cột thứ tự
            region = new CellRangeAddress(rowCount, rowCount, 0, 0);
            Cell cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(String.valueOf(count));
            formatHeader(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 1, 1);
            cellData = sheet1.getRow(rowCount)
                .getCell(1);
            cellData.setCellValue(listData.get(i)
                .getTimeView());
            formatHeader(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
            // Cột giá trị giờ thấp điểm
            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount)
                .getCell(2);
            cellData.setCellValue(epNon_Hour);
            formatHeader(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

            // Cột giá trị giờ bình thường
            region = new CellRangeAddress(rowCount, rowCount, 3, 3);
            cellData = sheet1.getRow(rowCount)
                .getCell(3);
            cellData.setCellValue(epNormal_Hour);
            formatHeader(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
            // Cột giá trị giờ cao điểm
            region = new CellRangeAddress(rowCount, rowCount, 4, 4);
            cellData = sheet1.getRow(rowCount)
                .getCell(4);
            cellData.setCellValue(epPeak_Hour);
            formatHeader(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
            rowCount += 1;
            count += 1;

            // update % load
            progress = progress + progressDevice;

            reportService.updatePercent(mapReport);
            if (sizeReport == Double.valueOf(i + 1)) {
                mapReport.put("percent", "95");
                reportService.updatePercent(mapReport);
            }

        }

        // Cột tổn sản lượng điện năng
        region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(rowCount)
            .getCell(0);
        cell.setCellValue("TỔNG");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(rowCount, rowCount, 2, 4);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(rowCount)
            .getCell(2);
        cell.setCellValue(ep_Total);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Tạo biểu đồ sản lượng điện năng theo tháng
        XSSFDrawing drawing = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(9, 22, 9, 22, 5, 11, 15, 26);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("BIỂU ĐỒ ĐIỆN NĂNG TIÊU THỤ THÁNG " + date);
        chart.setTitleOverlay(false);

        // add legend(các kí hiệu của mốc dữ liệu bên phải)
        // XDDFChartLegend legend = chart.getOrAddLegend();
        // legend.setPosition(LegendPosition.TOP_RIGHT);

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("THỜI GIAN");
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("ĐIỆN NĂNG TIÊU THỤ [kWh]");
        // leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        XDDFDataSource<String> countries = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
            new CellRangeAddress(12, rowCount - 1, 1, 1));

        XDDFNumericalDataSource<Double> value1 = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
            new CellRangeAddress(12, rowCount - 1, 2, 2));

        XDDFNumericalDataSource<Double> values2 = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
            new CellRangeAddress(12, rowCount - 1, 3, 3));

        XDDFNumericalDataSource<Double> values3 = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
            new CellRangeAddress(12, rowCount - 1, 4, 4));

        // set tiêu đề của biểu đồ( dưới và trái)
        XDDFChartData dataChart = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);

        XDDFChartData.Series series1 = dataChart.addSeries(countries, value1);
        series1.setTitle("GIỜ THẤP ĐIỂM ", null);

        XDDFChartData.Series series2 = dataChart.addSeries(countries, values2);
        series2.setTitle("GIỜ BÌNH THƯỜNG", null);

        XDDFChartData.Series series3 = dataChart.addSeries(countries, values3);
        series3.setTitle("GIỜ CAO ĐIỂM", null);

        dataChart.setVaryColors(true);
        chart.plot(dataChart);
        // correcting the overlap so bars really are stacked and not side by side

        chart.getCTChart()
            .getPlotArea()
            .getBarChartArray(0)
            .addNewOverlap()
            .setVal((byte) 100);

        // in order to transform a bar chart into a column chart, you just need to
        // change the bar direction
        XDDFBarChartData col = (XDDFBarChartData) dataChart;
        col.setBarGrouping(BarGrouping.STACKED);
        col.setBarDirection(BarDirection.COL);
        // set color
        // Giờ thấp điểm
        CTSRgbColor rgb1 = CTSRgbColor.Factory.newInstance();

        Color col1 = new Color(144, 238, 144);
        rgb1.setVal(new byte[] {(byte) col1.getRed(), (byte) col1.getGreen(), (byte) col1.getBlue()});

        CTSolidColorFillProperties fillProp1 = CTSolidColorFillProperties.Factory.newInstance();
        fillProp1.setSrgbClr(rgb1);

        CTShapeProperties ctShapeProperties1 = CTShapeProperties.Factory.newInstance();
        ctShapeProperties1.setSolidFill(fillProp1);
        chart.getCTChart()
            .getPlotArea()
            .getBarChartList()
            .get(0)
            .getSerList()
            .get(0)
            .setSpPr(ctShapeProperties1);
        // Giờ bình thường
        CTSRgbColor rgb2 = CTSRgbColor.Factory.newInstance();

        Color col2 = new Color(255, 255, 0);
        rgb2.setVal(new byte[] {(byte) col2.getRed(), (byte) col2.getGreen(), (byte) col2.getBlue()});

        CTSolidColorFillProperties fillProp2 = CTSolidColorFillProperties.Factory.newInstance();
        fillProp2.setSrgbClr(rgb2);

        CTShapeProperties ctShapeProperties2 = CTShapeProperties.Factory.newInstance();
        ctShapeProperties2.setSolidFill(fillProp2);
        chart.getCTChart()
            .getPlotArea()
            .getBarChartList()
            .get(0)
            .getSerList()
            .get(1)
            .setSpPr(ctShapeProperties2);
        // Giờ cap điểm
        CTSRgbColor rgb3 = CTSRgbColor.Factory.newInstance();

        Color col3 = new Color(255, 0, 0);
        rgb3.setVal(new byte[] {(byte) col3.getRed(), (byte) col3.getGreen(), (byte) col3.getBlue()});

        CTSolidColorFillProperties fillProp3 = CTSolidColorFillProperties.Factory.newInstance();
        fillProp3.setSrgbClr(rgb3);

        CTShapeProperties ctShapeProperties3 = CTShapeProperties.Factory.newInstance();
        ctShapeProperties3.setSolidFill(fillProp3);
        chart.getCTChart()
            .getPlotArea()
            .getBarChartList()
            .get(0)
            .getSerList()
            .get(2)
            .setSpPr(ctShapeProperties3);

        // set Font Style
        chart.getCTChart()
            .getTitle()
            .getTx()
            .getRich()
            .getPArray(0)
            .getRArray(0)
            .getRPr()
            .addNewLatin()
            .setTypeface("Courier New");

        // Tạo biểu đồ tròn điện năng
        // create drawing and anchor
        XSSFDrawing drawing1 = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchor1 = drawing1.createAnchor(9, 22, 9, 22, 5, 26, 15, 43);

        // create chart
        XSSFChart chart1 = drawing.createChart(anchor1);
        chart1.setTitleText("TỈ LỆ ĐIỆN NĂNG TIÊU THỤ THEO GIỜ THÁNG " + date);
        chart1.setTitleOverlay(false);
        XDDFChartLegend legend = chart1.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);
        XDDFDataSource<
            String> cat = XDDFDataSourcesFactory.fromStringCellRange(sheet1, new CellRangeAddress(5, 5, 1, 3));
        XDDFNumericalDataSource<
            Double> val = XDDFDataSourcesFactory.fromNumericCellRange(sheet1, new CellRangeAddress(6, 6, 1, 3));

        XDDFChartData chartData1 = chart1.createData(ChartTypes.PIE3D, null, null);
        chartData1.setVaryColors(true);
        XDDFChartData.Series series = chartData1.addSeries(cat, val);
        chart1.plot(chartData1);

        // do not auto delete the title; is necessary for showing title in Calc
        if (chart1.getCTChart()
            .getAutoTitleDeleted() == null)
            chart1.getCTChart()
                .addNewAutoTitleDeleted();
        chart1.getCTChart()
            .getAutoTitleDeleted()
            .setVal(false);

        // data point colors; is necessary for showing data points in Calc
        // some rgb colors to choose
        // set data point colors
        // // (144,238,144) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] color = new byte[][] {new byte[] {(byte) 144, (byte) 238, (byte) 144},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}};

        int pointCount1 = series.getCategoryData()
            .getPointCount();
        for (int p = 0; p < pointCount1; p++) {
            chart1.getCTChart()
                .getPlotArea()
                .getPie3DChartArray(0)
                .getSerArray(0)
                .addNewDPt()
                .addNewIdx()
                .setVal(p);
            chart1.getCTChart()
                .getPlotArea()
                .getPie3DChartArray(0)
                .getSerArray(0)
                .getDPtArray(p)
                .addNewSpPr()
                .addNewSolidFill()
                .addNewSrgbClr()
                .setVal(color[p]);
        }
        //

        // set Font Style
        chart1.getCTChart()
            .getTitle()
            .getTx()
            .getRich()
            .getPArray(0)
            .getRArray(0)
            .getRPr()
            .addNewLatin()
            .setTypeface("Courier New");
        // Tiêu đề báo cáo

        // set font style
        CellStyle cs1 = wb.createCellStyle();
        Font font1 = wb.createFont();
        font1.setFontName("Courier New");
        cs1.setFont(font1);
        cs1.setAlignment(HorizontalAlignment.CENTER);
        for (int z = 44; z < 70; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 36; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs1);
            }
        }
        region = new CellRangeAddress(45, 45, 0, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(45)
            .getCell(0);
        cell.setCellValue("II. BÁO CÁO SỐ LƯỢNG CẢNH BÁO ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột TT
        region = new CellRangeAddress(47, 47, 0, 0);
        cell = sheet1.getRow(47)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột THỜI GIAN
        region = new CellRangeAddress(47, 47, 1, 1);
        cell = sheet1.getRow(47)
            .getCell(1);
        cell.setCellValue("CẢNH BÁO");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột giá trị
        region = new CellRangeAddress(47, 47, 2, 2);
        cell = sheet1.getRow(47)
            .getCell(2);
        cell.setCellValue("SỐ LẦN");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // dữ liệu số lượng cảnh báo
        // TT1
        region = new CellRangeAddress(48, 48, 0, 0);
        Cell celldata = sheet1.getRow(48)
            .getCell(0);
        celldata.setCellValue("1");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // Cột loại cảnh báo
        region = new CellRangeAddress(48, 48, 1, 1);
        celldata = sheet1.getRow(48)
            .getCell(1);
        celldata.setCellValue("ĐIỆN ÁP CAO");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // Cột số lần
        region = new CellRangeAddress(48, 48, 2, 2);
        celldata = sheet1.getRow(48)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("nguongApCao") != null ? Double.valueOf(dataWarning.get("nguongApCao")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT2
        region = new CellRangeAddress(49, 49, 0, 0);
        celldata = sheet1.getRow(49)
            .getCell(0);
        celldata.setCellValue("2");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(49, 49, 1, 1);
        celldata = sheet1.getRow(49)
            .getCell(1);
        celldata.setCellValue("ĐIỆN ÁP THẤP");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(49, 49, 2, 2);
        celldata = sheet1.getRow(49)
            .getCell(2);
        celldata.setCellValue(
            dataWarning.get("nguongApThap") != null ? Double.valueOf(dataWarning.get("nguongApThap")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT3
        region = new CellRangeAddress(50, 50, 0, 0);
        celldata = sheet1.getRow(50)
            .getCell(0);
        celldata.setCellValue("3");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(50, 50, 1, 1);
        celldata = sheet1.getRow(50)
            .getCell(1);
        celldata.setCellValue("NHIỆT ĐỘ CAO");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(50, 50, 2, 2);
        celldata = sheet1.getRow(50)
            .getCell(2);
        celldata.setCellValue(
            dataWarning.get("nhietDoTiepXuc") != null ? Double.valueOf(dataWarning.get("nhietDoTiepXuc")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT4
        region = new CellRangeAddress(51, 51, 0, 0);
        celldata = sheet1.getRow(51)
            .getCell(0);
        celldata.setCellValue("4");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(51, 51, 1, 1);
        celldata = sheet1.getRow(51)
            .getCell(1);
        celldata.setCellValue("LỆCH PHA");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(51, 51, 2, 2);
        celldata = sheet1.getRow(51)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("lechPha") != null ? Double.valueOf(dataWarning.get("lechPha")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT5
        region = new CellRangeAddress(52, 52, 0, 0);
        celldata = sheet1.getRow(52)
            .getCell(0);
        celldata.setCellValue("5");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(52, 52, 1, 1);
        celldata = sheet1.getRow(52)
            .getCell(1);
        celldata.setCellValue("QUÁ TẢI");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(52, 52, 2, 2);
        celldata = sheet1.getRow(52)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("quaTai") != null ? Double.valueOf(dataWarning.get("quaTai")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT6
        region = new CellRangeAddress(53, 53, 0, 0);
        celldata = sheet1.getRow(53)
            .getCell(0);
        celldata.setCellValue("6");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(53, 53, 1, 1);
        celldata = sheet1.getRow(53)
            .getCell(1);
        celldata.setCellValue("TẦN SỐ CAO");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(53, 53, 2, 2);
        celldata = sheet1.getRow(53)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("tanSoCao") != null ? Double.valueOf(dataWarning.get("tanSoCao")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT7
        region = new CellRangeAddress(54, 54, 0, 0);
        celldata = sheet1.getRow(54)
            .getCell(0);
        celldata.setCellValue("7");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(54, 54, 1, 1);
        celldata = sheet1.getRow(54)
            .getCell(1);
        celldata.setCellValue("TẦN SỐ THẤP");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(54, 54, 2, 2);
        celldata = sheet1.getRow(54)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("tanSoThap") != null ? Double.valueOf(dataWarning.get("tanSoThap")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT8
        region = new CellRangeAddress(55, 55, 0, 0);
        celldata = sheet1.getRow(55)
            .getCell(0);
        celldata.setCellValue("8");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(55, 55, 1, 1);
        celldata = sheet1.getRow(55)
            .getCell(1);
        celldata.setCellValue("MẤT NGUỒN");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(55, 55, 2, 2);
        celldata = sheet1.getRow(55)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("matNguon") != null ? Double.valueOf(dataWarning.get("matNguon")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT9
        region = new CellRangeAddress(56, 56, 0, 0);
        celldata = sheet1.getRow(56)
            .getCell(0);
        celldata.setCellValue("9");
        cs.setAlignment(HorizontalAlignment.CENTER);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(56, 56, 1, 1);
        celldata = sheet1.getRow(56)
            .getCell(1);
        celldata.setCellValue("SÓNG HÀI CAO");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(56, 56, 2, 2);
        celldata = sheet1.getRow(56)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("nguongTongMeoSongHai") != null
            ? Double.valueOf(dataWarning.get("nguongTongMeoSongHai"))
            : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT10
        region = new CellRangeAddress(57, 57, 0, 0);
        celldata = sheet1.getRow(57)
            .getCell(0);
        celldata.setCellValue("10");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(57, 57, 1, 1);
        celldata = sheet1.getRow(57)
            .getCell(1);
        celldata.setCellValue("QUÁ DÒNG TRUNG TÍNH");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(57, 57, 2, 2);
        celldata = sheet1.getRow(57)
            .getCell(2);
        celldata.setCellValue(
            dataWarning.get("quaDongTrungTinh") != null ? Double.valueOf(dataWarning.get("quaDongTrungTinh")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT11
        region = new CellRangeAddress(58, 58, 0, 0);
        celldata = sheet1.getRow(58)
            .getCell(0);
        celldata.setCellValue("11");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(58, 58, 1, 1);
        celldata = sheet1.getRow(58)
            .getCell(1);
        celldata.setCellValue("HỆ SỐ CÔNG SUẤT THẤP");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(58, 58, 2, 2);
        celldata = sheet1.getRow(58)
            .getCell(2);
        celldata.setCellValue(
            dataWarning.get("heSoCongSuatThap") != null ? Double.valueOf(dataWarning.get("heSoCongSuatThap")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT12
        region = new CellRangeAddress(59, 59, 0, 0);
        celldata = sheet1.getRow(59)
            .getCell(0);
        celldata.setCellValue("12");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(59, 59, 1, 1);
        celldata = sheet1.getRow(59)
            .getCell(1);
        celldata.setCellValue("ĐÓNG MỞ CỬA");
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(59, 59, 2, 2);
        celldata = sheet1.getRow(59)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("dongMoCua") != null ? Double.valueOf(dataWarning.get("dongMoCua")) : 0);
        formatHeaderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Tổng
        region = new CellRangeAddress(60, 60, 0, 1);
        cell = sheet1.getRow(60)
            .getCell(0);
        cell.setCellValue("Tổng");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột số lần
        region = new CellRangeAddress(60, 60, 2, 2);
        cell = sheet1.getRow(60)
            .getCell(2);
        cell.setCellValue(WarningTotal != null ? WarningTotal : "0");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Tạo biểu đồ cảnh báo
        // Create row and put some cells in it. Rows and cells are 0 based.
        // set kích thước cho biểu đồ
        XSSFDrawing drawingWarning = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchorWarning = drawingWarning.createAnchor(8, 18, 8, 18, 3, 47, 10, 61);

        XSSFChart chartWarning = drawingWarning.createChart(anchorWarning);
        chartWarning.setTitleText("BIỂU ĐỒ SỐ LƯỢNG CẢNH BÁO THÁNG " + date);
        chartWarning.setTitleOverlay(false);

        // add legend(các kí hiệu của mốc dữ liệu bên phải)
        // XDDFChartLegend legend = chart.getOrAddLegend();
        // legend.setPosition(LegendPosition.TOP_RIGHT);

        XDDFCategoryAxis bottomAxisWarning = chartWarning.createCategoryAxis(AxisPosition.BOTTOM);

        bottomAxisWarning.setTitle("CẢNH BÁO");
        XDDFValueAxis leftAxisWarning = chartWarning.createValueAxis(AxisPosition.LEFT);
        leftAxisWarning.setTitle("SỐ LẦN");
        leftAxisWarning.setCrosses(AxisCrosses.AUTO_ZERO);

        XDDFDataSource<String> countriesWarning = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
            new CellRangeAddress(48, 59, 1, 1));

        XDDFNumericalDataSource<Double> valuesWarning = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
            new CellRangeAddress(48, 59, 2, 2));

        // set tiêu đề của biểu đồ( dưới và trái)
        XDDFChartData dataChartWarning = chartWarning.createData(ChartTypes.BAR, bottomAxisWarning, leftAxisWarning);
        XDDFChartData.Series series1Warning = dataChartWarning.addSeries(countriesWarning, valuesWarning);
        series1Warning.setTitle("CẢNH BÁO", null);
        dataChartWarning.setVaryColors(true);
        chartWarning.plot(dataChartWarning);
        // in order to transform a bar chart into a column chart, you just need to
        // change the bar direction
        XDDFBarChartData colWarning = (XDDFBarChartData) dataChartWarning;
        colWarning.setBarDirection(BarDirection.COL);

        IntStream.range(0, 10)
            .forEach(sheet1::autoSizeColumn);
        // export file
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
            log.info("ReportController.createElectricalPowerExcelsInMonth(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info(
                "ReportController.createElectricalPowerExcelsInMonth(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
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
        workbook.getConverterSetting()
            .setSheetFitToPage(true);
        // Save as PDF document
        workbook.saveToFile(pdf);

        ZipUtil.pack(folder, new File(path + ".zip"));
        mapReport.put("percent", "100");
        reportService.updatePercent(mapReport);
        reportService.updateStatus(Integer.valueOf(mapReport.get("id")));
        reportService.updateTimeFinish(Integer.valueOf(mapReport.get("id")));
        log.info("ReportController.createElectricalPowerExcelsInMonth(): END");

    }

    /**
     * Tạo excel báo cáo tổng hợp.
     *
     * @param data Thông tin báo cáo tổng hợp.
     * @throws Exception
     */

    private void formatHeaderEp(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
        final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent) {

        CellStyle cs = wb.createCellStyle();
        cs.setFillBackgroundColor(bgColor);
        cs.setFillForegroundColor(bgColor);
        cs.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);

        DataFormat format = wb.createDataFormat();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(hAlign);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setIndention((short) indent);
        cs.setWrapText(true);
        cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);

        RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
    }

    private void formatHeader(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet, final Cell cell,
        final short bgColor, final HorizontalAlignment hAlign, final int indent) {

        CellStyle cs = wb.createCellStyle();
        cs.setFillBackgroundColor(bgColor);
        cs.setFillForegroundColor(bgColor);
        cs.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);

        DataFormat format = wb.createDataFormat();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(hAlign);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setIndention((short) indent);
        cs.setWrapText(true);
        cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);

        RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
    }

    private void formatHeaderWarning(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
        final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent) {

        CellStyle cs = wb.createCellStyle();
        cs.setFillBackgroundColor(bgColor);
        cs.setFillForegroundColor(bgColor);
        cs.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);

        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
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

}
