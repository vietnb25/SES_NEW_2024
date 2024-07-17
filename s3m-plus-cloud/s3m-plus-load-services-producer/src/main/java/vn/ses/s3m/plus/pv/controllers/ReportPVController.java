package vn.ses.s3m.plus.pv.controllers;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

import com.spire.xls.Workbook;

import org.apache.commons.collections.keyvalue.MultiKey;
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
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.XDDFBarChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
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
import vn.ses.s3m.plus.dto.DataInverter1;
import vn.ses.s3m.plus.dto.Report;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.pv.service.ReportPVService;

@RestController
@RequestMapping ("/pv")
public class ReportPVController {

    @Autowired
    private ReportPVService reportService;
    @Value ("${pv.producer.export-folder}")
    private String folderName;
    /** Logging */
    private final Log log = LogFactory.getLog(ReportPVController.class);

    /**
     * Lấy thông tin của báo cáo.
     *
     * @param userName Tên người dùng
     * @return Thông tin báo cáo.
     */
    @SuppressWarnings ("rawtypes")
    @GetMapping ("/report/{customerId}/{userName}/{projectId}")
    public ResponseEntity<Map<String, List>> loadReportPV(@PathVariable String customerId,
        @PathVariable String userName, @PathVariable String projectId) {

        log.info("ReportController.loadReport() START");

        Map<String, String> condition = new HashMap<>();
        condition.put("schema", Schema.getSchemas(Integer.valueOf(customerId)));
        condition.put("projectId", projectId);
        int userId = reportService.getUserIdPV(userName);
        Map<String, List> data = new HashMap<>();

        List<Report> report = reportService.getReportPV(userId);
        List<Map<String, Object>> device = reportService.getDevicePV(condition);
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
    public ResponseEntity<Void> deleteReportPV(@PathVariable final int id) {
        log.info("ReportController.deleteReport() START");
        reportService.deleteReportPV(id);
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
    public ResponseEntity<Resource> downloadReportPV(@RequestParam final String path) throws Exception {
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
     * @param Date Thời gian.
     * @param reportType Loại báo cáo.
     * @param projectId Mã dự án.
     * @param userName Tên người dùng.
     * @return Trả về 200(Thêm mới thông tin thành công).
     */

    @GetMapping ("/report/addReport/{customerId}/{reportType}/{date}/{userName}/{projectId}/{deviceId}")
    public ResponseEntity<Void> addReportPV(@PathVariable final String customerId,
        @PathVariable final String reportType, @PathVariable final String date, @PathVariable final String userName,
        @PathVariable final String projectId, @PathVariable final String deviceId) throws Exception {
        log.info("ReportController.addRepot()  START");

        int userId = reportService.getUserIdPV(userName);

        String fromDate;
        String toDate;
        String date_Time;
        if (reportType.equals("1")) {
            fromDate = date + " 00:00:00";
            toDate = date + " 23:59:59";
            date_Time = date;

        } else if (reportType.equals("2")) {
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
        String tableInverter = (String) Constants.DATA.DATA_TABLES
            .get(new MultiKey(Constants.DATA.tables[tableIndex], Constants.DATA.MESSAGE.INVERTER1));

        Map<String, String> condition = new HashMap<>();
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put("schema", Schema.getSchemas(Integer.valueOf(customerId)));
        condition.put("s3mDataTableInverter", tableInverter);
        if (!deviceId.equals("all")) {
            condition.put("deviceId", deviceId);
        }

        List<DataInverter1> dataInverter1 = new ArrayList<>();
        // Định dạng thời gian
        // insert Report
        long url = new Date().getTime();
        Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());
        Report report = new Report();
        report.setReportId(Integer.valueOf(reportType));
        report.setReportDate(timestamp);
        report.setUpdated(timestamp);
        report.setUrl(String.valueOf(url));
        report.setStatus(0);
        report.setPercent(0);
        report.setUserId(userId);
        report.setSystemType("2");
        report.setDeviceId(deviceId);
        report.setDateType(date);
        // check dữ liệu báo cáo
        dataInverter1 = reportService.getDataInverterPV(condition);
        List<Map<String, String>> totalWarningPv = reportService.getTotalWarningPv(condition);
        if (reportType.equals("1")) {
            // check trống dữ liệu của ngày
            if (dataInverter1.size() > 0 || totalWarningPv.size() > 0) {
                reportService.addReportPV(report);
                log.info("ReportController.addReport() ADD REPORT SUCCESS");
                return new ResponseEntity<Void>(HttpStatus.OK);
            } else {
                log.info("ReportController.addReport() ADD REPORT ERROR");
                return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
            }
        } else if (reportType.equals("2")) {
            // check trống dữ liệu của tháng
            if (dataInverter1.size() > 0 || totalWarningPv.size() > 0) {
                reportService.addReportPV(report);
                log.info("ReportController.addReport() ADD REPORT SUCCESS");
                return new ResponseEntity<Void>(HttpStatus.OK);
            } else {
                log.info("ReportController.addReport() ADD REPORT ERROR");
                return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
            }
        } else {
            // check trống dữ liệu của năm
            if (dataInverter1.size() > 0 || totalWarningPv.size() > 0) {
                reportService.addReportPV(report);
                log.info("ReportController.addReport() ADD REPORT SUCCESS");
                return new ResponseEntity<Void>(HttpStatus.OK);
            } else {
                log.info("ReportController.addReport() ADD REPORT ERROR");
                return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
            }
        }

    }

    /**
     * Tạo báo cáo và lưu vào trong máy.
     *
     * @param reportType Loại báo cáo.
     * @param date Thời gian.
     * @param userName Tên người dùng.
     * @param projectId Mã dự án.
     * @return Trả về 200(Tạo file báo cáo thành công) 404(Tạo báo cáo thất bại).
     * @throws Exception.
     */
    @PostMapping ("/report/generateReports/{customerId}/{reportType}/{date}/{userName}/{projectId}/{deviceId}")
    public ResponseEntity<?> generateReportsPV(@PathVariable final String customerId,
        @PathVariable final String reportType, @PathVariable final String date, @PathVariable final String userName,
        @PathVariable final String projectId, @PathVariable final String deviceId, @RequestBody final User user)
        throws Exception {

        log.info("ReportController.generateReports() START");

        String fromDate;
        String toDate;
        String date_Time;
        if (reportType.equals("1")) {
            fromDate = date + " 00:00:00";
            toDate = date + " 23:59:59";
            date_Time = date;

        } else if (reportType.equals("2")) {
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
        String tableInverter = (String) Constants.DATA.DATA_TABLES
            .get(new MultiKey(Constants.DATA.tables[tableIndex], Constants.DATA.MESSAGE.INVERTER1));

        Map<String, String> condition = new HashMap<>();
        Map<String, String> conditions = new HashMap<>();
        conditions.put("schema", Schema.getSchemas(Integer.valueOf(customerId)));
        conditions.put("date", date);
        conditions.put("s3mDataTableInverter", tableInverter);
        condition.put("projectId", projectId);
        if (!deviceId.equals("all")) {
            condition.put("deviceId", deviceId);
            conditions.put("deviceId", deviceId);
        }
        // load%
        Map<String, String> mapReport = new HashMap<>();
        // get url image
        String pngImageURL = user.getImg();
        String encodingPrefix = "base64,";
        int contentStartIndex = pngImageURL.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData1 = org.apache.commons.codec.binary.Base64
            .decodeBase64(pngImageURL.substring(contentStartIndex));

        // get userId
        int userId = reportService.getUserIdPV(userName);

        // get projectName
        String projectName = reportService.getProjectNamePV(projectId);

        Map<String, Integer> map = new HashMap<>();
        map.put("userId", userId);
        map.put("limit", 5);
        List<Report> list = reportService.getListByLimitPV(map);
        Report report1 = list.get(0);

        // load %
        mapReport.put("id", String.valueOf(report1.getId()));
        String path = null;
        //
        List<Map<String, String>> totalWarningPv = reportService.getTotalWarningPv(conditions);

        Map<String, String> dataWarning = new HashMap<>();

        // Tổng số cảnh báo theo thời gian
        String WarningTotalPV = reportService.getTotalWarning(conditions);
        for (int i = 0; i < totalWarningPv.size(); i++) {
            if (totalWarningPv.get(i)
                .get("Evt1") != null) {
                Integer warningType = Integer.valueOf(totalWarningPv.get(i)
                    .get("Evt1"));
                switch (warningType) {
                    case Constants.WarningTypeInverter.CHAM_DAT:
                        dataWarning.put("chamDat", String.valueOf(totalWarningPv.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningTypeInverter.DIEN_AP_CAO_AC:
                        dataWarning.put("dienApCaoAC", String.valueOf(totalWarningPv.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningTypeInverter.MAT_KET_NOI_AC:
                        dataWarning.put("matKetNoiAC", String.valueOf(totalWarningPv.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningTypeInverter.MAT_KET_NOI_DC:
                        dataWarning.put("matKetNoiDC", String.valueOf(totalWarningPv.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningTypeInverter.MAT_NGUON_LUOI:
                        dataWarning.put("matKetNoiLuoi", String.valueOf(totalWarningPv.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningTypeInverter.DONG_MO_CUA:
                        dataWarning.put("dongMoCua", String.valueOf(totalWarningPv.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningTypeInverter.NHIET_DO_CAO:
                        dataWarning.put("nhietDoCao", String.valueOf(totalWarningPv.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningTypeInverter.TAN_SO_CAO:
                        dataWarning.put("tanSoCao", String.valueOf(totalWarningPv.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningTypeInverter.TAN_SO_THAP:
                        dataWarning.put("tanSoThap", String.valueOf(totalWarningPv.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningTypeInverter.DIEN_AP_CAO_DC:
                        dataWarning.put("dienApCaoDC", String.valueOf(totalWarningPv.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningTypeInverter.DIEN_AP_THAP_AC:
                        dataWarning.put("dienApThapAC", String.valueOf(totalWarningPv.get(i)
                            .get("TOTAL")));
                        break;
                    case Constants.WarningTypeInverter.HONG_CAU_CHI:
                        dataWarning.put("cauChi", String.valueOf(totalWarningPv.get(i)
                            .get("TOTAL")));
                        break;
                    default:
                        break;
                }
            }

        }

        if (reportType.equals("1")) {
            // Đường dẫn lưu file báo cáo
            path = folderName + File.separator + report1.getUrl();
            // lấy dữ liệu trong db
            List<Map<String, String>> listData = new ArrayList<>();
            long ElectricityBill = 0;
            for (int i = 0; i < 24; i++) {
                DataInverter1 dataInverter1 = new DataInverter1();
                Map<String, String> mapData = new HashMap<>();
                String dateTime = "";
                if (i < 10) {
                    dateTime = date + " 0" + i + ":00:00";
                } else {
                    dateTime = date + " " + i + ":00:00";
                }
                // Truy vấn theo thời gian

                condition.put("schema", Schema.getSchemas(Integer.valueOf(customerId)));
                condition.put("date", dateTime);
                dataInverter1 = reportService.getDataInverterInDayPV(condition);
                // mapData.put("wH", dataInverter1 != null ? String.valueOf(dataInverter1.getWh()) : "0");
                mapData.put("dateTime", dateTime);
                listData.add(mapData);

                if (dataInverter1 != null) {
                    // ElectricityBill = ElectricityBill + dataInverter1.getWh();
                }

            }
            createReportSyntheticExcelInDay(listData, path, mapReport, projectName, date, ElectricityBill, dataWarning,
                WarningTotalPV, deviceId, imageData1);
        } else if (reportType.equals("2")) {

            // Đường dẫn lưu file báo cáo
            path = folderName + File.separator + report1.getUrl();
            // lấy dữ liệu trong db
            List<Map<String, String>> listData = new ArrayList<>();
            long ElectricityBill = 0;
            for (int i = 1; i < 32; i++) {
                DataInverter1 dataInverter1 = new DataInverter1();
                Map<String, String> mapData = new HashMap<>();
                String dateTime = "";
                if (i < 10) {
                    dateTime = date + "-0" + i;
                } else {
                    dateTime = date + "-" + i;
                }

                condition.put("date", dateTime);
                condition.put("schema", Schema.getSchemas(Integer.valueOf(customerId)));

                dataInverter1 = reportService.getDataInverterInMonthPV(condition);
                // mapData.put("wH", dataInverter1 != null ? String.valueOf(dataInverter1.getWh()) : "0");
                mapData.put("dateTime", dateTime);
                listData.add(mapData);
                if (dataInverter1 != null) {
                    // ElectricityBill = ElectricityBill + dataInverter1.getWh();
                }

            }
            createReportSyntheticExcelInMonth(listData, path, mapReport, projectName, date, ElectricityBill,
                dataWarning, WarningTotalPV, deviceId, imageData1);

        } else {
            // Đường dẫn lưu file báo cáo
            path = folderName + File.separator + report1.getUrl();
            // lấy dữ liệu trong db
            List<Map<String, String>> listData = new ArrayList<>();
            long ElectricityBill = 0;
            for (int i = 1; i < 13; i++) {
                DataInverter1 dataInverter1 = new DataInverter1();
                String dateTime = "";
                Map<String, String> mapData = new HashMap<>();
                if (i < 10) {
                    dateTime = date + "-0" + i;
                } else {
                    dateTime = date + "-" + i;
                }

                // condition.put("s3mDataTableInverter", tableInverter);
                condition.put("date", dateTime);
                condition.put("schema", Schema.getSchemas(Integer.valueOf(customerId)));
                dataInverter1 = reportService.getDataInverterInYearPV(condition);
                // mapData.put("wH", dataInverter1 != null ? String.valueOf(dataInverter1.getWh()) : "0");
                mapData.put("dateTime", dateTime);
                listData.add(mapData);
                if (dataInverter1 != null) {
                    // ElectricityBill = ElectricityBill + dataInverter1.getWh();
                }

            }
            createReportSyntheticExcelInYear(listData, path, mapReport, projectName, date, ElectricityBill, dataWarning,
                WarningTotalPV, deviceId, imageData1);
        }

        log.info("ReportController.generateReports() END");
        return new ResponseEntity<>(HttpStatus.OK);

    }

    /**
     * Tạo excel báo cáo tổng hợp trong ngày.
     *
     * @param data Thông tin báo cáo tổng hợp.
     * @param date Thời gian bị báo cáo.
     * @param path Đường dẫn tới file.
     * @return File báo cáo thông số điện cho tất cả thiết bị
     * @throws Exception
     */
    private void createReportSyntheticExcelInDay(List<Map<String, String>> data, final String path,
        final Map<String, String> mapReport, final String projectName, final String date, final long ElectricityBill,
        final Map<String, String> dataWarning, String WarningTotalPV, String deviceId, final byte[] imageData1)
        throws FileNotFoundException, IOException {
        log.info("ReportController.createReportSyntheticExcelInDay(): START");

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Báo cáo tổng hợp");

        // add image
        int pictureIdx = wb.addPicture(imageData1, wb.PICTURE_TYPE_PNG);
        XSSFDrawing drawingImg = sheet1.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(3);
        anchorImg.setCol2(6);
        anchorImg.setRow1(1);
        anchorImg.setRow2(8);

        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);

        Row row;
        Cell cell;

        // set font style
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);

        for (int z = 0; z < 70; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 36; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // Cột header
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 15);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        cell.setCellValue("BÁO CÁO TỔNG HỢP NGÀY " + date);
        formatHeader(wb, region, sheet1, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột tên dự án
        region = new CellRangeAddress(2, 2, 0, 0);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue("Tên dự án");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        region = new CellRangeAddress(2, 2, 1, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(1);
        cell.setCellValue(projectName);
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tiêu đề báo cáo
        region = new CellRangeAddress(3, 3, 0, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(3)
            .getCell(0);
        cell.setCellValue("I. BÁO CÁO NĂNG LƯỢNG NGÀY " + date);
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Côt Báo cáo số lượng cảnh báo
        region = new CellRangeAddress(36, 36, 0, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(36)
            .getCell(0);
        cell.setCellValue("II. BÁO CÁO SỐ LƯỢNG CẢNH BÁO " + date);
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột sản lượng điên năng
        region = new CellRangeAddress(5, 5, 0, 0);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("SẢN LƯỢNG ĐIỆN NĂNG[kWh]");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(5, 5, 1, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(1);
        cell.setCellValue(ElectricityBill);
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá điện
        region = new CellRangeAddress(6, 6, 0, 0);
        cell = sheet1.getRow(6)
            .getCell(0);
        cell.setCellValue("GIÁ ĐIỆN[VNĐ/kWh]");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(6, 6, 1, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(6)
            .getCell(1);
        cell.setCellValue(1200);
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // Cột tiền điện
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("TIỀN ĐIỆN[VNĐ]");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        String totalMoney = currencyVN.format(ElectricityBill * 1200);

        region = new CellRangeAddress(7, 7, 1, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue(String.valueOf(totalMoney));
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // Cột TT
        region = new CellRangeAddress(9, 9, 0, 0);
        cell = sheet1.getRow(9)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột THỜI GIAN
        region = new CellRangeAddress(9, 9, 1, 1);
        cell = sheet1.getRow(9)
            .getCell(1);
        cell.setCellValue("THỜI GIAN");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột giá trị
        region = new CellRangeAddress(9, 9, 2, 2);
        cell = sheet1.getRow(9)
            .getCell(2);
        cell.setCellValue("SẢN LƯỢNG ĐIỆN NĂNG");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Ghi dữ liệu vào sheet của excel
        int rowCount = 10;
        int count = 1;
        // Thông số load % tải báo cáo
        double sizeReport = data.size();
        double progressDevice = 100 / sizeReport;
        double progress = progressDevice;

        for (int i = 0; i < data.size(); i++) {

            // put percent %
            mapReport.put("percent", String.valueOf(progress));

            // Cột thứ tự
            region = new CellRangeAddress(rowCount, rowCount, 0, 0);

            Cell cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(String.valueOf(count));
            cs.setAlignment(HorizontalAlignment.CENTER);
            formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 1, 1);
            cellData = sheet1.getRow(rowCount)
                .getCell(1);
            cellData.setCellValue(data.get(i)
                .get("dateTime"));
            cs.setAlignment(HorizontalAlignment.CENTER);
            formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            // Cột giá trị
            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount)
                .getCell(2);
            cellData.setCellValue(Long.valueOf(data.get(i)
                .get("wH")));
            cs.setAlignment(HorizontalAlignment.CENTER);
            formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            rowCount += 1;
            count += 1;

            // update % load
            progress = progress + progressDevice;

            reportService.updatePercentPV(mapReport);
            if (sizeReport == Double.valueOf(i + 1)) {
                mapReport.put("percent", "95");
                reportService.updatePercentPV(mapReport);
            }
        }

        // Cột tổn sản lượng điện năng
        region = new CellRangeAddress(34, 34, 0, 1);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(34)
            .getCell(0);
        cell.setCellValue("TỔNG");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(34, 34, 2, 2);
        cell = sheet1.getRow(34)
            .getCell(2);
        cell.setCellValue(ElectricityBill);
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Tạo biểu đồ điện năng
        // Tiêu đề báo cáo
        XSSFDrawing drawing = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(8, 18, 8, 18, 3, 9, 16, 35);

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
            String> countries = XDDFDataSourcesFactory.fromStringCellRange(sheet1, new CellRangeAddress(10, 33, 1, 1));

        XDDFNumericalDataSource<
            Double> values = XDDFDataSourcesFactory.fromNumericCellRange(sheet1, new CellRangeAddress(10, 33, 2, 2));

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

        // set màu cho excel
        CTSRgbColor rgb = CTSRgbColor.Factory.newInstance();

        Color col1 = new Color(255, 106, 106);
        rgb.setVal(new byte[] {(byte) col1.getRed(), (byte) col1.getGreen(), (byte) col1.getBlue()});

        CTSolidColorFillProperties fillProp = CTSolidColorFillProperties.Factory.newInstance();
        fillProp.setSrgbClr(rgb);

        CTShapeProperties ctShapeProperties = CTShapeProperties.Factory.newInstance();
        ctShapeProperties.setSolidFill(fillProp);
        chart.getCTChart()
            .getPlotArea()
            .getBarChartList()
            .get(0)
            .getSerList()
            .get(0)
            .setSpPr(ctShapeProperties);

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

        // for (int z = 37; z < 60; z++) {
        // row = sheet1.createRow(z);
        // for (int j = 0; j < 36; j++) {
        // row.createCell(j, CellType.BLANK)
        // .setCellStyle(cs);
        // }
        // }

        // Cột TT
        region = new CellRangeAddress(38, 38, 0, 0);
        cell = sheet1.getRow(38)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột THỜI GIAN
        region = new CellRangeAddress(38, 38, 1, 1);
        cell = sheet1.getRow(38)
            .getCell(1);
        cell.setCellValue("CẢNH BÁO");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột giá trị
        region = new CellRangeAddress(38, 38, 2, 2);
        cell = sheet1.getRow(38)
            .getCell(2);
        cell.setCellValue("SỐ LẦN");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // dữ liệu số lượng cảnh báo
        // TT1
        region = new CellRangeAddress(39, 39, 0, 0);
        Cell celldata = sheet1.getRow(39)
            .getCell(0);
        celldata.setCellValue("1");
        cs.setAlignment(HorizontalAlignment.CENTER);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(39, 39, 1, 1);
        celldata = sheet1.getRow(39)
            .getCell(1);
        celldata.setCellValue("ĐIỆN ÁP CAO AC");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(39, 39, 2, 2);
        celldata = sheet1.getRow(39)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("dienApCaoAC") != null ? Integer.valueOf(dataWarning.get("dienApCaoAC")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT2
        region = new CellRangeAddress(40, 40, 0, 0);
        celldata = sheet1.getRow(40)
            .getCell(0);
        celldata.setCellValue("2");
        cs.setAlignment(HorizontalAlignment.CENTER);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(40, 40, 1, 1);
        celldata = sheet1.getRow(40)
            .getCell(1);
        celldata.setCellValue("ĐIỆN ÁP THẤP AC");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(40, 40, 2, 2);
        celldata = sheet1.getRow(40)
            .getCell(2);
        celldata.setCellValue(
            dataWarning.get("dienApThapAC") != null ? Integer.valueOf(dataWarning.get("dienApThapAC")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT3
        region = new CellRangeAddress(41, 41, 0, 0);
        celldata = sheet1.getRow(41)
            .getCell(0);
        celldata.setCellValue("3");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // Cột loại cảnh báo
        region = new CellRangeAddress(41, 41, 1, 1);
        celldata = sheet1.getRow(41)
            .getCell(1);
        celldata.setCellValue("NHIỆT ĐỘ CAO");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(41, 41, 2, 2);
        celldata = sheet1.getRow(41)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("nhietDoCao") != null ? Integer.valueOf(dataWarning.get("nhietDoCao")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT4
        region = new CellRangeAddress(42, 42, 0, 0);
        celldata = sheet1.getRow(42)
            .getCell(0);
        celldata.setCellValue("4");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(42, 42, 1, 1);
        celldata = sheet1.getRow(42)
            .getCell(1);
        celldata.setCellValue("MẤT KẾT NỐI AC");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(42, 42, 2, 2);
        celldata = sheet1.getRow(42)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("matKetNoiAC") != null ? Integer.valueOf(dataWarning.get("matKetNoiAC")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT5
        region = new CellRangeAddress(43, 43, 0, 0);
        celldata = sheet1.getRow(43)
            .getCell(0);
        celldata.setCellValue("5");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(43, 43, 1, 1);
        celldata = sheet1.getRow(43)
            .getCell(1);
        celldata.setCellValue("MẤT KẾT NỐI DC");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(43, 43, 2, 2);
        celldata = sheet1.getRow(43)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("matKetNoiDC") != null ? Integer.valueOf(dataWarning.get("matKetNoiDC")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT6
        region = new CellRangeAddress(44, 44, 0, 0);
        celldata = sheet1.getRow(44)
            .getCell(0);
        celldata.setCellValue("6");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(44, 44, 1, 1);
        celldata = sheet1.getRow(44)
            .getCell(1);
        celldata.setCellValue("TẦN SỐ CAO");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(44, 44, 2, 2);
        celldata = sheet1.getRow(44)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("tanSoCao") != null ? Integer.valueOf(dataWarning.get("tanSoCao")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT7
        region = new CellRangeAddress(45, 45, 0, 0);
        celldata = sheet1.getRow(45)
            .getCell(0);
        celldata.setCellValue("7");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(45, 45, 1, 1);
        celldata = sheet1.getRow(45)
            .getCell(1);
        celldata.setCellValue("TẦN SỐ THẤP");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(45, 45, 2, 2);
        celldata = sheet1.getRow(45)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("tanSoThap") != null ? Integer.valueOf(dataWarning.get("tanSoThap")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT8
        region = new CellRangeAddress(46, 46, 0, 0);
        celldata = sheet1.getRow(46)
            .getCell(0);
        celldata.setCellValue("8");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(46, 46, 1, 1);
        celldata = sheet1.getRow(46)
            .getCell(1);
        celldata.setCellValue("MẤT NGUỒN LƯỚI");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(46, 46, 2, 2);
        celldata = sheet1.getRow(46)
            .getCell(2);
        celldata.setCellValue(
            dataWarning.get("matKetNoiLuoi") != null ? Integer.valueOf(dataWarning.get("matKetNoiLuoi")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT9
        region = new CellRangeAddress(47, 47, 0, 0);
        celldata = sheet1.getRow(47)
            .getCell(0);
        celldata.setCellValue("9");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(47, 47, 1, 1);
        celldata = sheet1.getRow(47)
            .getCell(1);
        celldata.setCellValue("CHẠM ĐẤT");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(47, 47, 2, 2);
        celldata = sheet1.getRow(47)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("chamDat") != null ? Integer.valueOf(dataWarning.get("chamDat")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT10
        region = new CellRangeAddress(48, 48, 0, 0);
        celldata = sheet1.getRow(48)
            .getCell(0);
        celldata.setCellValue("10");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(48, 48, 1, 1);
        celldata = sheet1.getRow(48)
            .getCell(1);
        celldata.setCellValue("HỎNG CẦU CHÌ");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(48, 48, 2, 2);
        celldata = sheet1.getRow(48)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("hongCauChi") != null ? Integer.valueOf(dataWarning.get("hongCauChi")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT11
        region = new CellRangeAddress(49, 49, 0, 0);
        celldata = sheet1.getRow(49)
            .getCell(0);
        celldata.setCellValue("11");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(49, 49, 1, 1);
        celldata = sheet1.getRow(49)
            .getCell(1);
        celldata.setCellValue("ĐÓNG MỞ CỬA");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(49, 49, 2, 2);
        celldata = sheet1.getRow(49)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("dongMoCUA") != null ? Integer.valueOf(dataWarning.get("dongMoCUA")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT12
        region = new CellRangeAddress(50, 50, 0, 0);
        celldata = sheet1.getRow(50)
            .getCell(0);
        celldata.setCellValue("12");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(50, 50, 1, 1);
        celldata = sheet1.getRow(50)
            .getCell(1);
        celldata.setCellValue("ĐIỆN ÁP CAO DC");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(50, 50, 2, 2);
        celldata = sheet1.getRow(50)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("dienApCaoDC") != null ? Integer.valueOf(dataWarning.get("dienApCaoDC")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // Tổng
        region = new CellRangeAddress(51, 51, 0, 1);
        cell = sheet1.getRow(51)
            .getCell(0);
        cell.setCellValue("Tổng");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột số lần
        region = new CellRangeAddress(51, 51, 2, 2);
        cell = sheet1.getRow(51)
            .getCell(2);
        cell.setCellValue(WarningTotalPV != null ? WarningTotalPV : "0");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Tạo biểu đồ cảnh báo
        // set kích thước cho biểu đồ
        // Thông số:
        // dx1- tọa độ x trong EMU trong ô đầu tiên.
        // dy1- tọa độ y trong EMU trong ô đầu tiên.
        // dx2- tọa độ x trong EMU trong ô thứ hai.
        // dy2- tọa độ y trong EMU trong ô thứ hai.
        // col1- cột (dựa trên 0) của ô đầu tiên.
        // row1- hàng (dựa trên 0) của ô đầu tiên.
        // col2- cột (dựa trên 0) của ô thứ hai.
        // row2- hàng (dựa trên 0) của ô thứ hai.

        // XSSFClientAnchor anchor = drawing.createAnchor(8, 18, 8, 18, 3, 49, 16, 58);
        // // Create row and put some cells in it. Rows and cells are 0 based.
        // // set kích thước cho biểu đồ

        // Tạo chart
        // Create row and put some cells in it. Rows and cells are 0 based.
        // set kích thước cho biểu đồ
        XSSFDrawing drawingWarning = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchorWarning = drawingWarning.createAnchor(8, 18, 8, 18, 3, 38, 16, 55);

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
            new CellRangeAddress(39, 50, 1, 1));

        XDDFNumericalDataSource<Double> valuesWarning = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
            new CellRangeAddress(39, 50, 2, 2));

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

        // set màu cho chart column
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
        sheet1.setColumnWidth(1, 8000);

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
            log.info("ReportController.createReportSyntheticExcelInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info(
                "ReportController.createReportSyntheticExcelInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
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
        reportService.updatePercentPV(mapReport);
        reportService.updateStatusPV(Integer.valueOf(mapReport.get("id")));
        reportService.updateTimeFinishPV(Integer.valueOf(mapReport.get("id")));
        log.info("ReportController.createReportSyntheticExcelInDay(): END");

    }
    // Tạo báo cáo tổng hợp trong tháng

    /**
     * Tạo excel báo cáo tổng hợp trong tháng.
     *
     * @param data Thông tin báo cáo tổng hợp.
     * @param date Thời gian bị báo cáo.
     * @param path Đường dẫn tới file.
     * @return File báo cáo thông số điện cho tất cả thiết bị
     * @throws Exception
     */
    private void createReportSyntheticExcelInMonth(List<Map<String, String>> data, final String path,
        final Map<String, String> mapReport, final String projectName, final String date, final long ElectricityBill,
        final Map<String, String> dataWarning, final String WarningTotalPV, String deviceId, final byte[] imageData1)
        throws FileNotFoundException, IOException {
        log.info("ReportController.createReportSyntheticExcelInMonth(): START");

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Báo cáo tổng hợp");
        // add image
        int pictureIdx = wb.addPicture(imageData1, wb.PICTURE_TYPE_PNG);
        XSSFDrawing drawingImg = sheet1.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(3);
        anchorImg.setCol2(6);
        anchorImg.setRow1(1);
        anchorImg.setRow2(8);

        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);

        Row row;
        Cell cell;
        // set font style
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        for (int z = 0; z < 70; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 36; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // Cột header
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 15);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        cell.setCellValue("BÁO CÁO NĂNG LƯỢNG THÁNG " + date);
        formatHeader(wb, region, sheet1, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột tên dự án
        region = new CellRangeAddress(2, 2, 0, 0);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue("Tên dự án");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        region = new CellRangeAddress(2, 2, 1, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(1);
        cell.setCellValue(projectName);
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tiêu đề báo cáo
        region = new CellRangeAddress(3, 3, 0, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(3)
            .getCell(0);
        cell.setCellValue("I. BÁO CÁO NĂNG LƯỢNG THÁNG " + date);
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột sản lượng điên năng
        region = new CellRangeAddress(5, 5, 0, 0);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("SẢN LƯỢNG ĐIỆN NĂNG[kWh]");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(5, 5, 1, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(1);
        cell.setCellValue(ElectricityBill);
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá điện
        region = new CellRangeAddress(6, 6, 0, 0);
        cell = sheet1.getRow(6)
            .getCell(0);
        cell.setCellValue("GIÁ ĐIỆN[VNĐ/kWh]");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(6, 6, 1, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(6)
            .getCell(1);
        cell.setCellValue(1200);
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // Cột tiền điện
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("TIỀN ĐIỆN[VNĐ]");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        String totalMoney = currencyVN.format(ElectricityBill * 1200);

        region = new CellRangeAddress(7, 7, 1, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue(String.valueOf(totalMoney));
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // Cột TT
        region = new CellRangeAddress(9, 9, 0, 0);
        cell = sheet1.getRow(9)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột THỜI GIAN
        region = new CellRangeAddress(9, 9, 1, 1);
        cell = sheet1.getRow(9)
            .getCell(1);
        cell.setCellValue("THỜI GIAN");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột giá trị
        region = new CellRangeAddress(9, 9, 2, 2);
        cell = sheet1.getRow(9)
            .getCell(2);
        cell.setCellValue("SẢN LƯỢNG ĐIỆN NĂNG");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột tổn sản lượng điện năng
        region = new CellRangeAddress(41, 41, 0, 1);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(41)
            .getCell(0);
        cell.setCellValue("TỔNG");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(41, 41, 2, 2);
        cell = sheet1.getRow(41)
            .getCell(2);
        cell.setCellValue(ElectricityBill);
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Ghi dữ liệu vào sheet của excel
        int rowCount = 10;
        int count = 1;
        // Thông số load % tải báo cáo
        double sizeReport = data.size();
        double progressDevice = 100 / sizeReport;
        double progress = progressDevice;

        for (int i = 0; i < data.size(); i++) {

            // put percent %
            mapReport.put("percent", String.valueOf(progress));

            // Cột thứ tự
            region = new CellRangeAddress(rowCount, rowCount, 0, 0);
            Cell cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(String.valueOf(count));
            formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 1, 1);
            cellData = sheet1.getRow(rowCount)
                .getCell(1);
            cellData.setCellValue(data.get(i)
                .get("dateTime"));
            formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            // Cột giá trị
            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount)
                .getCell(2);
            cellData.setCellValue(Long.valueOf(data.get(i)
                .get("wH")));
            formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            rowCount += 1;
            count += 1;

            // update % load
            progress = progress + progressDevice;

            reportService.updatePercentPV(mapReport);
            if (sizeReport == Double.valueOf(i + 1)) {
                mapReport.put("percent", "95");
                reportService.updatePercentPV(mapReport);
            }
        }

        // Tạo chart
        // Create row and put some cells in it. Rows and cells are 0 based.
        // set kích thước cho biểu đồ
        XSSFDrawing drawing = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(8, 18, 8, 18, 3, 9, 16, 41);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("BIỂU ĐỒ SẢN LƯỢNG ĐIỆN NĂNG THÁNG " + date);
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
            String> countries = XDDFDataSourcesFactory.fromStringCellRange(sheet1, new CellRangeAddress(10, 40, 1, 1));

        XDDFNumericalDataSource<
            Double> values = XDDFDataSourcesFactory.fromNumericCellRange(sheet1, new CellRangeAddress(10, 40, 2, 2));

        // set tiêu đề của biểu đồ( dưới và trái)
        XDDFChartData dataChart = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        XDDFChartData.Series series1 = dataChart.addSeries(countries, values);
        series1.setTitle("THỜI GIAN", null);
        dataChart.setVaryColors(true);
        chart.plot(dataChart);
        ;
        // in order to transform a bar chart into a column chart, you just need to
        // change the bar direction
        XDDFBarChartData col = (XDDFBarChartData) dataChart;
        col.setBarDirection(BarDirection.COL);

        // set màu cho excel
        CTSRgbColor rgb = CTSRgbColor.Factory.newInstance();

        Color col1 = new Color(255, 106, 106);
        rgb.setVal(new byte[] {(byte) col1.getRed(), (byte) col1.getGreen(), (byte) col1.getBlue()});

        CTSolidColorFillProperties fillProp = CTSolidColorFillProperties.Factory.newInstance();
        fillProp.setSrgbClr(rgb);

        CTShapeProperties ctShapeProperties = CTShapeProperties.Factory.newInstance();
        ctShapeProperties.setSolidFill(fillProp);
        chart.getCTChart()
            .getPlotArea()
            .getBarChartList()
            .get(0)
            .getSerList()
            .get(0)
            .setSpPr(ctShapeProperties);

        // Tạo báo cáo số lượng cảnh báo

        for (int z = 42; z < 65; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 36; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }
        region = new CellRangeAddress(43, 43, 0, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(43)
            .getCell(0);
        cell.setCellValue("II. BÁO CÁO SỐ LƯỢNG CẢNH BÁO " + date);
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột TT
        region = new CellRangeAddress(45, 45, 0, 0);
        cell = sheet1.getRow(45)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột THỜI GIAN
        region = new CellRangeAddress(45, 45, 1, 1);
        cell = sheet1.getRow(45)
            .getCell(1);
        cell.setCellValue("CẢNH BÁO");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột giá trị
        region = new CellRangeAddress(45, 45, 2, 2);
        cell = sheet1.getRow(45)
            .getCell(2);
        cell.setCellValue("SỐ LẦN");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // formatBorderWarning(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // dữ liệu số lượng cảnh báo
        // TT1
        Cell celldata;
        region = new CellRangeAddress(46, 46, 0, 0);
        celldata = sheet1.getRow(46)
            .getCell(0);
        celldata.setCellValue("1");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(46, 46, 1, 1);
        celldata = sheet1.getRow(46)
            .getCell(1);
        celldata.setCellValue("ĐIỆN ÁP CAO AC");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(46, 46, 2, 2);
        celldata = sheet1.getRow(46)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("dienApCaoAC") != null ? Integer.valueOf(dataWarning.get("dienApCaoAC")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT2
        region = new CellRangeAddress(47, 47, 0, 0);
        celldata = sheet1.getRow(47)
            .getCell(0);
        celldata.setCellValue("2");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // Cột loại cảnh báo
        region = new CellRangeAddress(47, 47, 1, 1);
        celldata = sheet1.getRow(47)
            .getCell(1);
        celldata.setCellValue("ĐIỆN ÁP THẤP AC");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(47, 47, 2, 2);
        celldata = sheet1.getRow(47)
            .getCell(2);
        celldata.setCellValue(
            dataWarning.get("dienApThapAC") != null ? Integer.valueOf(dataWarning.get("dienApThapAC")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT3
        region = new CellRangeAddress(48, 48, 0, 0);
        celldata = sheet1.getRow(48)
            .getCell(0);
        celldata.setCellValue("3");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(48, 48, 1, 1);
        celldata = sheet1.getRow(48)
            .getCell(1);
        celldata.setCellValue("NHIỆT ĐỘ CAO");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(48, 48, 2, 2);
        celldata = sheet1.getRow(48)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("nhietDoCao") != null ? Integer.valueOf(dataWarning.get("nhietDoCao")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT4
        region = new CellRangeAddress(49, 49, 0, 0);
        celldata = sheet1.getRow(49)
            .getCell(0);
        celldata.setCellValue("4");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(49, 49, 1, 1);
        celldata = sheet1.getRow(49)
            .getCell(1);
        celldata.setCellValue("MẤT KẾT NỐI AC");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(49, 49, 2, 2);
        celldata = sheet1.getRow(49)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("matKetNoiAC") != null ? Integer.valueOf(dataWarning.get("matKetNoiAC")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT5
        region = new CellRangeAddress(50, 50, 0, 0);
        celldata = sheet1.getRow(50)
            .getCell(0);
        celldata.setCellValue("5");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(50, 50, 1, 1);
        celldata = sheet1.getRow(50)
            .getCell(1);
        celldata.setCellValue("MẤT KẾT NỐI DC");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(50, 50, 2, 2);
        celldata = sheet1.getRow(50)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("matKetNoiDC") != null ? Integer.valueOf(dataWarning.get("matKetNoiDC")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT6
        region = new CellRangeAddress(51, 51, 0, 0);
        celldata = sheet1.getRow(51)
            .getCell(0);
        celldata.setCellValue("6");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(51, 51, 1, 1);
        celldata = sheet1.getRow(51)
            .getCell(1);
        celldata.setCellValue("TẦN SỐ CAO");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(51, 51, 2, 2);
        celldata = sheet1.getRow(51)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("tanSoCao") != null ? Integer.valueOf(dataWarning.get("tanSoCao")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT7
        region = new CellRangeAddress(52, 52, 0, 0);
        celldata = sheet1.getRow(52)
            .getCell(0);
        celldata.setCellValue("7");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(52, 52, 1, 1);
        celldata = sheet1.getRow(52)
            .getCell(1);
        celldata.setCellValue("TẦN SỐ THẤP");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(52, 52, 2, 2);
        celldata = sheet1.getRow(52)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("tanSoThap") != null ? Integer.valueOf(dataWarning.get("tanSoThap")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT8
        region = new CellRangeAddress(53, 53, 0, 0);
        celldata = sheet1.getRow(53)
            .getCell(0);
        celldata.setCellValue("8");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(53, 53, 1, 1);
        celldata = sheet1.getRow(53)
            .getCell(1);
        celldata.setCellValue("MẤT NGUỒN LƯỚI");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(53, 53, 2, 2);
        celldata = sheet1.getRow(53)
            .getCell(2);
        celldata.setCellValue(
            dataWarning.get("matKetNoiLuoi") != null ? Integer.valueOf(dataWarning.get("matKetNoiLuoi")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT9
        region = new CellRangeAddress(54, 54, 0, 0);
        celldata = sheet1.getRow(54)
            .getCell(0);
        celldata.setCellValue("9");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(54, 54, 1, 1);
        celldata = sheet1.getRow(54)
            .getCell(1);
        celldata.setCellValue("CHẠM ĐẤT");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(54, 54, 2, 2);
        celldata = sheet1.getRow(54)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("chamDat") != null ? Integer.valueOf(dataWarning.get("chamDat")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT10
        region = new CellRangeAddress(55, 55, 0, 0);
        celldata = sheet1.getRow(55)
            .getCell(0);
        celldata.setCellValue("10");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(55, 55, 1, 1);
        celldata = sheet1.getRow(55)
            .getCell(1);
        celldata.setCellValue("HỎNG CẦU CHÌ");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(55, 55, 2, 2);
        celldata = sheet1.getRow(55)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("hongCauChi") != null ? Integer.valueOf(dataWarning.get("hongCauChi")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT11
        region = new CellRangeAddress(56, 56, 0, 0);
        celldata = sheet1.getRow(56)
            .getCell(0);
        celldata.setCellValue("11");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(56, 56, 1, 1);
        celldata = sheet1.getRow(56)
            .getCell(1);
        celldata.setCellValue("ĐÓNG MỞ CỬA");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(56, 56, 2, 2);
        celldata = sheet1.getRow(56)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("dongMoCUA") != null ? Integer.valueOf(dataWarning.get("dongMoCUA")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT12
        region = new CellRangeAddress(57, 57, 0, 0);
        celldata = sheet1.getRow(57)
            .getCell(0);
        celldata.setCellValue("12");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(57, 57, 1, 1);
        celldata = sheet1.getRow(57)
            .getCell(1);
        celldata.setCellValue("ĐIỆN ÁP CAO DC");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(57, 57, 2, 2);
        celldata = sheet1.getRow(57)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("dienApCaoDC") != null ? Integer.valueOf(dataWarning.get("dienApCaoDC")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // Tổng
        region = new CellRangeAddress(58, 58, 0, 1);
        cell = sheet1.getRow(58)
            .getCell(0);
        cell.setCellValue("Tổng");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột số lần
        region = new CellRangeAddress(58, 58, 2, 2);
        cell = sheet1.getRow(58)
            .getCell(2);
        cell.setCellValue(WarningTotalPV != null ? WarningTotalPV : "0");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tạo biểu đồ cảnh báo
        // set kích thước cho biểu đồ
        // Tạo chart
        XSSFDrawing drawingWarning = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchorWarning = drawingWarning.createAnchor(8, 18, 8, 18, 3, 45, 16, 59);

        XSSFChart chartWarning = drawingWarning.createChart(anchorWarning);
        chartWarning.setTitleText("BIỂU ĐỒ SỐ LƯỢNG CẢNH BÁO THÁNG  " + date);
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
            new CellRangeAddress(45, 56, 1, 1));

        XDDFNumericalDataSource<Double> valuesWarning = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
            new CellRangeAddress(45, 56, 2, 2));

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
            log.info("ReportController.createReportSyntheticExcelInMonth(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info(
                "ReportController.createReportSyntheticExcelInMonth(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
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
        reportService.updatePercentPV(mapReport);
        reportService.updateStatusPV(Integer.valueOf(mapReport.get("id")));
        reportService.updateTimeFinishPV(Integer.valueOf(mapReport.get("id")));
        log.info("ReportController.createReportSyntheticExcelInMonth(): END");

    }

    // Táo báo cáo trong năm

    /**
     * Tạo excel báo cáo tổng hợp trong năm.
     *
     * @param data Thông tin báo cáo tổng hợp.
     * @param date Thời gian bị báo cáo.
     * @param path Đường dẫn tới file.
     * @return File báo cáo thông số điện cho tất cả thiết bị
     * @throws Exception
     */
    private void createReportSyntheticExcelInYear(List<Map<String, String>> data, final String path,
        final Map<String, String> mapReport, final String projectName, final String date, final long ElectricityBill,
        final Map<String, String> dataWarning, String WarningTotalPV, String deviceId, final byte[] imageData1)
        throws FileNotFoundException, IOException {
        log.info("ReportController.createReportSyntheticExcelInYear(): START");

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Báo cáo tổng hợp");
        // add image
        int pictureIdx = wb.addPicture(imageData1, wb.PICTURE_TYPE_PNG);
        XSSFDrawing drawingImg = sheet1.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(3);
        anchorImg.setCol2(6);
        anchorImg.setRow1(1);
        anchorImg.setRow2(8);

        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);

        Row row;
        Cell cell;
        // set font style
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        for (int z = 0; z < 60; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 36; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // Cột header
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 15);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        cell.setCellValue("BÁO CÁO NĂNG LƯỢNG NĂM " + date);
        formatHeader(wb, region, sheet1, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột tên dự án
        region = new CellRangeAddress(2, 2, 0, 0);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue("Tên dự án");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        region = new CellRangeAddress(2, 2, 1, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(1);
        cell.setCellValue(projectName);
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tiêu đề báo cáo
        region = new CellRangeAddress(3, 3, 0, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(3)
            .getCell(0);
        cell.setCellValue("I. BÁO CÁO NĂNG LƯỢNG NĂM " + date);
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột sản lượng điên năng
        region = new CellRangeAddress(5, 5, 0, 0);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("SẢN LƯỢNG ĐIỆN NĂNG[kWh]");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(5, 5, 1, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(1);
        cell.setCellValue(String.valueOf(ElectricityBill));
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá điện
        region = new CellRangeAddress(6, 6, 0, 0);
        cell = sheet1.getRow(6)
            .getCell(0);
        cell.setCellValue("GIÁ ĐIỆN[VNĐ/kWh]");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(6, 6, 1, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(6)
            .getCell(1);
        cell.setCellValue(1200);
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // Cột tiền điện
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("TIỀN ĐIỆN[VNĐ]");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        String totalMoney = currencyVN.format(ElectricityBill * 1200);

        region = new CellRangeAddress(7, 7, 1, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue(String.valueOf(totalMoney));
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // Cột TT
        region = new CellRangeAddress(9, 9, 0, 0);
        cell = sheet1.getRow(9)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột THỜI GIAN
        region = new CellRangeAddress(9, 9, 1, 1);
        cell = sheet1.getRow(9)
            .getCell(1);
        cell.setCellValue("THỜI GIAN");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột giá trị
        region = new CellRangeAddress(9, 9, 2, 2);
        cell = sheet1.getRow(9)
            .getCell(2);
        cell.setCellValue("SẢN LƯỢNG ĐIỆN NĂNG");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột tổn sản lượng điện năng
        region = new CellRangeAddress(22, 22, 0, 1);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(22)
            .getCell(0);
        cell.setCellValue("TỔNG");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(22, 22, 2, 2);
        cell = sheet1.getRow(22)
            .getCell(2);
        cell.setCellValue(String.valueOf(ElectricityBill));
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // Ghi dữ liệu vào sheet của excel
        int rowCount = 10;
        int count = 1;
        // Thông số load % tải báo cáo
        double sizeReport = data.size();
        double progressDevice = 100 / sizeReport;
        double progress = progressDevice;

        for (int i = 0; i < data.size(); i++) {

            // put percent %
            mapReport.put("percent", String.valueOf(progress));

            // Cột thứ tự
            region = new CellRangeAddress(rowCount, rowCount, 0, 0);
            Cell cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(count);
            formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 1, 1);
            cellData = sheet1.getRow(rowCount)
                .getCell(1);
            cellData.setCellValue(data.get(i)
                .get("dateTime"));
            formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            // Cột giá trị
            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount)
                .getCell(2);
            cellData.setCellValue(Long.valueOf(data.get(i)
                .get("wH")));
            formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

            rowCount += 1;
            count += 1;

            // update % load
            progress = progress + progressDevice;

            reportService.updatePercentPV(mapReport);
            if (sizeReport == Double.valueOf(i + 1)) {
                mapReport.put("percent", "95");
                reportService.updatePercentPV(mapReport);
            }
        }

        // Tạo chart
        // Create row and put some cells in it. Rows and cells are 0 based.
        // set kích thước cho biểu đồ
        XSSFDrawing drawing = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(8, 18, 8, 18, 3, 9, 16, 23);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("BIỂU ĐỒ SẢN LƯỢNG ĐIỆN NĂNG NĂM " + date);
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
            String> countries = XDDFDataSourcesFactory.fromStringCellRange(sheet1, new CellRangeAddress(10, 21, 1, 1));

        XDDFNumericalDataSource<
            Double> values = XDDFDataSourcesFactory.fromNumericCellRange(sheet1, new CellRangeAddress(10, 21, 2, 2));

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

        // set màu cho excel
        CTSRgbColor rgb = CTSRgbColor.Factory.newInstance();

        Color col1 = new Color(255, 106, 106);
        rgb.setVal(new byte[] {(byte) col1.getRed(), (byte) col1.getGreen(), (byte) col1.getBlue()});

        CTSolidColorFillProperties fillProp = CTSolidColorFillProperties.Factory.newInstance();
        fillProp.setSrgbClr(rgb);

        CTShapeProperties ctShapeProperties = CTShapeProperties.Factory.newInstance();
        ctShapeProperties.setSolidFill(fillProp);
        chart.getCTChart()
            .getPlotArea()
            .getBarChartList()
            .get(0)
            .getSerList()
            .get(0)
            .setSpPr(ctShapeProperties);

        // TẠO BÁO CÁO CỦA CẢNH BÁO

        for (int z = 24; z < 60; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 36; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }
        region = new CellRangeAddress(24, 24, 0, 2);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(24)
            .getCell(0);
        cell.setCellValue("II. BÁO CÁO SỐ LƯỢNG CẢNH BÁO " + date);
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột TT
        region = new CellRangeAddress(26, 26, 0, 0);
        cell = sheet1.getRow(26)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột THỜI GIAN
        region = new CellRangeAddress(26, 26, 1, 1);
        cell = sheet1.getRow(26)
            .getCell(1);
        cell.setCellValue("CẢNH BÁO");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột giá trị
        region = new CellRangeAddress(26, 26, 2, 2);
        cell = sheet1.getRow(26)
            .getCell(2);
        cell.setCellValue("SỐ LẦN");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // dữ liệu số lượng cảnh báo
        // TT1
        region = new CellRangeAddress(27, 27, 0, 0);
        Cell celldata = sheet1.getRow(27)
            .getCell(0);
        celldata.setCellValue("1");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(27, 27, 1, 1);
        celldata = sheet1.getRow(27)
            .getCell(1);
        celldata.setCellValue("ĐIỆN ÁP CAO AC");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(27, 27, 2, 2);
        celldata = sheet1.getRow(27)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("dienApCaoAC") != null ? Integer.valueOf(dataWarning.get("dienApCaoAC")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // TT2
        region = new CellRangeAddress(28, 28, 0, 0);
        celldata = sheet1.getRow(28)
            .getCell(0);
        celldata.setCellValue("2");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(28, 28, 1, 1);
        celldata = sheet1.getRow(28)
            .getCell(1);
        celldata.setCellValue("ĐIỆN ÁP THẤP AC");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(28, 28, 2, 2);
        celldata = sheet1.getRow(28)
            .getCell(2);
        celldata.setCellValue(
            dataWarning.get("dienApThapAC") != null ? Integer.valueOf(dataWarning.get("dienApThapAC")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT3
        region = new CellRangeAddress(29, 29, 0, 0);
        celldata = sheet1.getRow(29)
            .getCell(0);
        celldata.setCellValue("3");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(29, 29, 1, 1);
        celldata = sheet1.getRow(29)
            .getCell(1);
        celldata.setCellValue("NHIỆT ĐỘ CAO");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(29, 29, 2, 2);
        celldata = sheet1.getRow(29)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("nhietDoCao") != null ? Integer.valueOf(dataWarning.get("nhietDoCao")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT4
        region = new CellRangeAddress(30, 30, 0, 0);
        celldata = sheet1.getRow(30)
            .getCell(0);
        celldata.setCellValue("4");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(30, 30, 1, 1);
        celldata = sheet1.getRow(30)
            .getCell(1);
        celldata.setCellValue("MẤT KẾT NỐI AC");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(30, 30, 2, 2);
        celldata = sheet1.getRow(30)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("matKetNoiAC") != null ? Integer.valueOf(dataWarning.get("matKetNoiAC")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT5
        region = new CellRangeAddress(31, 31, 0, 0);
        celldata = sheet1.getRow(31)
            .getCell(0);
        celldata.setCellValue("5");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(31, 31, 1, 1);
        celldata = sheet1.getRow(31)
            .getCell(1);
        celldata.setCellValue("MẤT KẾT NỐI DC");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(31, 31, 2, 2);
        celldata = sheet1.getRow(31)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("matKetNoiDC") != null ? Integer.valueOf(dataWarning.get("matKetNoiDC")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT6
        region = new CellRangeAddress(32, 32, 0, 0);
        celldata = sheet1.getRow(32)
            .getCell(0);
        celldata.setCellValue("6");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(32, 32, 1, 1);
        celldata = sheet1.getRow(32)
            .getCell(1);
        celldata.setCellValue("TẦN SỐ CAO");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(32, 32, 2, 2);
        celldata = sheet1.getRow(32)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("tanSoCao") != null ? Integer.valueOf(dataWarning.get("tanSoCao")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT7
        region = new CellRangeAddress(33, 33, 0, 0);
        celldata = sheet1.getRow(33)
            .getCell(0);
        celldata.setCellValue("7");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(33, 33, 1, 1);
        celldata = sheet1.getRow(33)
            .getCell(1);
        celldata.setCellValue("TẦN SỐ THẤP");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(33, 33, 2, 2);
        celldata = sheet1.getRow(33)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("tanSoThap") != null ? Integer.valueOf(dataWarning.get("tanSoThap")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT8
        region = new CellRangeAddress(34, 34, 0, 0);
        celldata = sheet1.getRow(34)
            .getCell(0);
        celldata.setCellValue("8");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(34, 34, 1, 1);
        celldata = sheet1.getRow(34)
            .getCell(1);
        celldata.setCellValue("MẤT NGUỒN LƯỚI");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(34, 34, 2, 2);
        celldata = sheet1.getRow(34)
            .getCell(2);
        celldata.setCellValue(
            dataWarning.get("matKetNoiLuoi") != null ? Integer.valueOf(dataWarning.get("matKetNoiLuoi")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT9
        region = new CellRangeAddress(35, 35, 0, 0);
        celldata = sheet1.getRow(35)
            .getCell(0);
        celldata.setCellValue("9");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(35, 35, 1, 1);
        celldata = sheet1.getRow(35)
            .getCell(1);
        celldata.setCellValue("CHẠM ĐẤT");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(35, 35, 2, 2);
        celldata = sheet1.getRow(35)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("chamDat") != null ? Integer.valueOf(dataWarning.get("chamDat")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT10
        region = new CellRangeAddress(36, 36, 0, 0);
        celldata = sheet1.getRow(36)
            .getCell(0);
        celldata.setCellValue("10");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(36, 36, 1, 1);
        celldata = sheet1.getRow(36)
            .getCell(1);
        celldata.setCellValue("HỎNG CẦU CHÌ");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(36, 36, 2, 2);
        celldata = sheet1.getRow(36)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("hongCauChi") != null ? Integer.valueOf(dataWarning.get("hongCauChi")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT11
        region = new CellRangeAddress(37, 37, 0, 0);
        celldata = sheet1.getRow(37)
            .getCell(0);
        celldata.setCellValue("11");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(37, 37, 1, 1);
        celldata = sheet1.getRow(37)
            .getCell(1);
        celldata.setCellValue("ĐÓNG MỞ CỬA");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(37, 37, 2, 2);
        celldata = sheet1.getRow(37)
            .getCell(2);
        celldata.setCellValue(dataWarning.get("dongMoCUA") != null ? Integer.valueOf(dataWarning.get("dongMoCUA")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // TT12
        region = new CellRangeAddress(38, 38, 0, 0);
        celldata = sheet1.getRow(38)
            .getCell(0);
        celldata.setCellValue("12");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột loại cảnh báo
        region = new CellRangeAddress(38, 38, 1, 1);
        celldata = sheet1.getRow(38)
            .getCell(1);
        celldata.setCellValue("ĐIỆN ÁP CAO DC");
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);
        // Cột số lần
        region = new CellRangeAddress(38, 38, 2, 2);
        celldata = sheet1.getRow(38)
            .getCell(2);
        celldata
            .setCellValue(dataWarning.get("dienApCaoDC") != null ? Integer.valueOf(dataWarning.get("dienApCaoDC")) : 0);
        formatBorderWarning(wb, region, sheet1, celldata, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER,
            0);

        // Tổng
        region = new CellRangeAddress(39, 39, 0, 1);
        cell = sheet1.getRow(39)
            .getCell(0);
        cell.setCellValue("Tổng");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột số lần
        region = new CellRangeAddress(39, 39, 2, 2);
        cell = sheet1.getRow(39)
            .getCell(2);
        cell.setCellValue(WarningTotalPV != null ? WarningTotalPV : "0");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Tạo biểu đồ cảnh báo
        // set kích thước cho biểu đồ
        // Tạo chart

        XSSFDrawing drawingWarning = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchorWarning = drawingWarning.createAnchor(8, 18, 8, 18, 3, 26, 16, 40);

        XSSFChart chartWarning = drawingWarning.createChart(anchorWarning);
        chartWarning.setTitleText("BIỂU ĐỒ SỐ LƯỢNG CẢNH BÁO NĂM " + date);
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
            new CellRangeAddress(27, 38, 1, 1));

        XDDFNumericalDataSource<Double> valuesWarning = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
            new CellRangeAddress(27, 38, 2, 2));

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
            log.info("ReportController.createReportSyntheticExcelInYear(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info(
                "ReportController.createReportSyntheticExcelInYear(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
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
        reportService.updatePercentPV(mapReport);
        reportService.updateStatusPV(Integer.valueOf(mapReport.get("id")));
        reportService.updateTimeFinishPV(Integer.valueOf(mapReport.get("id")));
        log.info("ReportController.createReportSyntheticExcelInYear(): END");

    }

    private void formatHeader(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet, final Cell cell,
        final short bgColor, final HorizontalAlignment hAlign, final int indent) {

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

    private void formatBorder(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet, final Cell cell,
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

    private void formatBorderWarning(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
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
