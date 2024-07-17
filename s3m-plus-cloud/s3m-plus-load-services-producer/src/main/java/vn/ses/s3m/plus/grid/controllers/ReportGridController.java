package vn.ses.s3m.plus.grid.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import org.apache.poi.xddf.usermodel.chart.AxisCrossBetween;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.BarDirection;
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
import vn.ses.s3m.plus.dto.DataRmuDrawer1;
import vn.ses.s3m.plus.dto.Report;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.dto.Warning;
import vn.ses.s3m.plus.form.JsonFormWarningGrid;
import vn.ses.s3m.plus.form.JsonGridForm;
import vn.ses.s3m.plus.grid.service.ReportGridService;
import vn.ses.s3m.plus.grid.service.WarningGridService;
import vn.ses.s3m.plus.service.ReportService;

@RestController
@RequestMapping ("/grid")
public class ReportGridController {

    /** Logging */
    private final Log log = LogFactory.getLog(ReportGridController.class);

    @Autowired
    private ReportGridService reportService;

    @Autowired
    private ReportService reportServiceLoad;

    @Autowired
    private WarningGridService warningGridService;

    @Value ("${grid.producer.export-folder}")
    private String folderName;

    /**
     * Lấy thông tin của báo cáo.
     *
     * @param userName Tên người dùng
     * @return Thông tin báo cáo.
     */
    @SuppressWarnings ("rawtypes")
    @GetMapping ("/report/{userName}/{projectId}")
    public ResponseEntity<Map<String, List>> loadReportGrid(@PathVariable String userName,
        @PathVariable String projectId) {
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
    public ResponseEntity<Void> deleteReportGrid(@PathVariable final int id) {
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
    public ResponseEntity<Resource> downloadReportGrid(@RequestParam final String path) throws Exception {
        // Gửi zip qua client
        log.info("ReportController.downloadReport() START");
        File f = new File(folderName + File.separator + path);
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
    public ResponseEntity<Void> addReportGrid(@PathVariable final String customerId,
        @PathVariable final String deviceId, @PathVariable final String reportType, @PathVariable final String date,
        final @PathVariable
        String userName, @PathVariable final String projectId, @PathVariable final String dateType)
        throws ParseException {
        log.info("ReportController.addReport() START");
        int userId = reportService.getUserId(userName);
        // conditions
        List<DataRmuDrawer1> dataRmuDrawer1 = new ArrayList<>();

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
        String tableRmu = (String) Constants.DATA.DATA_TABLES
            .get(new MultiKey(Constants.DATA.tables[tableIndex], Constants.DATA.MESSAGE.GRID_RMU_DRAWER1));
        // Điều kiện truy vấn
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put("s3mDataTableRmu", tableRmu);
        condition.put("projectId", projectId);
        condition.put("viewTime", date);
        if (!deviceId.equals("all")) {
            condition.put("deviceId", deviceId);
        }
        List<Warning> warning = reportService.getWarningGridByLimit(condition);
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
        report.setSystemType("3");

        if (reportType.equals("1")) {

            dataRmuDrawer1 = reportService.getdataRmuDrawer1Bylimit(condition);

            // check trống dữ liệu
            if (dataRmuDrawer1.size() > 0
            // || warning.size() > 0
            ) {
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
    public ResponseEntity<?> generateReportsGrid(@PathVariable final String customerId,
        @PathVariable final String deviceId, @PathVariable final String reportType, @PathVariable final String date,
        @PathVariable final String dateType, final @PathVariable
        String userName, @PathVariable final String projectId, @RequestBody final User user) throws Exception {

        log.info("ReportController.generateReportsGrid() START");

        Map<String, Object> condition = new HashMap<>();
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
        String tableRmu = (String) Constants.DATA.DATA_TABLES
            .get(new MultiKey(Constants.DATA.tables[tableIndex], Constants.DATA.MESSAGE.GRID_RMU_DRAWER1));

        // Điều kiện truy vấn
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put("s3mDataTableRmu", tableRmu);
        condition.put("viewTime", date);
        if (dateType.equals("1")) {
            condition.put("viewType", "3");
        } else if (dateType.equals("3")) {
            condition.put("viewType", "1");
        } else {
            condition.put("viewType", "2");
        }

        if (!deviceId.equals("all")) {
            condition.put("deviceId", deviceId);
        }
        condition.put("projectId", projectId);
        condition.put("schema", Schema.getSchemas(Integer.valueOf(customerId)));

        Map<String, Integer> map = new HashMap<>();
        map.put("userId", userId);
        map.put("limit", 5);
        List<Report> list = reportService.getListReportByLimit(map);
        Report report1 = list.get(0);
        // load %
        mapReport.put("id", String.valueOf(report1.getId()));
        // get projectName
        String projectName = reportService.getProjectName(projectId);
        String path = null;
        path = folderName + File.separator + report1.getUrl();
        // List warning
        List<Warning> totalWarning = reportService.getWarningGrid(condition);

        // Tổng số cảnh báo theo thời gian

        List<Long> listDeviceIdWarning = new ArrayList<>();
        List<Long> resultDeviceId = new ArrayList<>();
        for (int i = 0; i < totalWarning.size(); i++) {
            listDeviceIdWarning.add(totalWarning.get(i)
                .getDeviceId());
        }
        resultDeviceId = listDeviceIdWarning.stream()
            .distinct()
            .collect(Collectors.toList());

        List<JsonFormWarningGrid> dataWarnings = new ArrayList<>();
        for (int i = 0; i < resultDeviceId.size(); i++) {
            JsonFormWarningGrid jsonFormWarningGrid = new JsonFormWarningGrid();
            for (int j = 0; j < totalWarning.size(); j++) {
                long deviceId1 = resultDeviceId.get(i);
                long deviceId2 = totalWarning.get(j)
                    .getDeviceId();
                if (deviceId1 == deviceId2) {
                    int warningType = Integer.valueOf(totalWarning.get(j)
                        .getWarningType());
                    jsonFormWarningGrid.setDeviceName(totalWarning.get(j)
                        .getDeviceName());
                    jsonFormWarningGrid.setEp(totalWarning.get(j)
                        .getEp());
                    switch (warningType) {
                        case Constants.WARNING_RMU.QUA_TAI_TONG:
                            jsonFormWarningGrid.setQuaTaiTong(totalWarning.get(j)
                                .getTotal());
                            break;
                        case Constants.WARNING_RMU.QUA_TAI_NHANH:
                            jsonFormWarningGrid.setQuaTaiNhanh(totalWarning.get(j)
                                .getTotal());
                            break;
                        case Constants.WARNING_RMU.NHIET_DO:
                            jsonFormWarningGrid.setNhietDo(totalWarning.get(j)
                                .getTotal());
                            break;
                        case Constants.WARNING_RMU.DIEN_AP_CAO:
                            jsonFormWarningGrid.setDienApCao(totalWarning.get(j)
                                .getTotal());
                            break;
                        case Constants.WARNING_RMU.DIEN_AP_THAP:
                            jsonFormWarningGrid.setDienApThap(totalWarning.get(j)
                                .getTotal());
                            break;
                        case Constants.WARNING_RMU.COS_NHANH_THAP:
                            jsonFormWarningGrid.setCosPhiThap(totalWarning.get(j)
                                .getTotal());
                            break;
                        case Constants.WARNING_RMU.MAT_DIEN_TONG:
                            jsonFormWarningGrid.setMatDienTong(totalWarning.get(j)
                                .getTotal());
                            break;
                        default:
                            break;
                    }
                }
            }
            dataWarnings.add(jsonFormWarningGrid);
        }
        if (reportType.equals("1")) {
            // List warning
            List<DataRmuDrawer1> dataRmuDrawer1 = new ArrayList<>();
            dataRmuDrawer1 = reportService.getDataRmuDrawer1(condition);
            List<JsonGridForm> listData = new ArrayList<>();

            List<Long> listDeviceId = new ArrayList<>();
            List<Long> resultDevice = new ArrayList<>();
            for (int i = 0; i < dataRmuDrawer1.size(); i++) {
                listDeviceId.add(dataRmuDrawer1.get(i)
                    .getDeviceId());
            }
            resultDevice = listDeviceId.stream()
                .distinct()
                .collect(Collectors.toList());

            for (int j = 0; j < resultDevice.size(); j++) {
                JsonGridForm jsonGrid = new JsonGridForm();
                int warningHumidity = 0;// độ ẩm
                int warningTemperature = 0;// nhiệt độ
                String deviceName = "";
                for (int i = 0; i < dataRmuDrawer1.size(); i++) {
                    long _device1 = Long.valueOf(resultDevice.get(j));
                    long _device2 = Long.valueOf(dataRmuDrawer1.get(i)
                        .getDeviceId());

                    if (_device1 == _device2) {
                        if ( (dataRmuDrawer1.get(i)
                            .getT()) > 500) {
                            warningHumidity++;
                        }
                        if ( (dataRmuDrawer1.get(i)
                            .getH()) > 900) {
                            warningTemperature++;
                        }
                        deviceName = dataRmuDrawer1.get(i)
                            .getDeviceName();
                    }

                }
                jsonGrid.setT(warningTemperature);
                jsonGrid.setH(warningHumidity);
                jsonGrid.setDeviceName(deviceName);
                listData.add(jsonGrid);

            }
            createElectricalPowerExcels(listData, date, path, mapReport, projectName, imageData1, dateType,
                dataWarnings);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Tạo excel báo cáo tổng hợp theo ngày.
     *
     * @param data Thông tin báo cáo tổng hợp.
     * @throws Exception
     */
    private void createElectricalPowerExcels(final List<JsonGridForm> listData, final String date, final String path,
        final Map<String, String> mapReport, final String projectName, final byte[] imageData1, String dateType,
        final List<JsonFormWarningGrid> dataWarnings) throws Exception {
        log.info("ReportController.createElectricalPowerExcels(): START");

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
        cell.setCellValue("BÁO CÁO GRID " + date);
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
        CellStyle cs1 = wb.createCellStyle();
        Font font1 = wb.createFont();
        font1.setBold(true);
        font1.setFontName("Courier New");
        cs1.setFont(font1);

        region = new CellRangeAddress(3, 3, 0, 3);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(3)
            .getCell(0);
        if (dateType.equals("1")) {
            cell.setCellValue("BÁO CÁO [TỔNG HỢP] NGÀY " + date);
        } else if (dateType.equals("2")) {
            cell.setCellValue("BÁO CÁO [TỔNG HỢP] THÁNG " + date);
        } else {
            cell.setCellValue("BÁO CÁO [TỔNG HỢP] NĂM " + date);
        }

        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        region = new CellRangeAddress(4, 4, 0, 1);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        cell.setCellValue("I. TỦ TRUNG THẾ");
        cell.setCellStyle(cs1);
        // formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // TABLE tủ trung thế
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("TỦ RMU");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("Số lần cảnh báo quá nhiệt độ, độ ẩm");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Số lần cảnh báo phóng điện");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Ghi dữ liệu vào bảng của excel
        int rowCount = 8;
        int count = 1;
        // Thông số load % tải báo cáo
        double sizeReport = listData.size();
        double progressDevice = 100 / sizeReport;
        double progress = progressDevice;
        for (int i = 0; i < listData.size(); i++) {
            // put percent %
            mapReport.put("percent", String.valueOf(progress));
            // Cột thứ tự
            region = new CellRangeAddress(rowCount, rowCount, 0, 0);
            Cell cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(String.valueOf(count));
            formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

            // Cột Tủ rmu
            region = new CellRangeAddress(rowCount, rowCount, 1, 1);
            cellData = sheet1.getRow(rowCount)
                .getCell(1);
            cellData.setCellValue(listData.get(i)
                .getDeviceName());
            formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

            // Cột Số lần cảnh báo quá nhiệt độ, độ ẩm
            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount)
                .getCell(2);
            cellData.setCellValue(listData.get(i)
                .getT());
            formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

            // Cột Số lần cảnh báo phóng điện
            region = new CellRangeAddress(rowCount, rowCount, 3, 3);
            cellData = sheet1.getRow(rowCount)
                .getCell(3);
            cellData.setCellValue(listData.get(i)
                .getH());
            formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

            rowCount += 1;
            count += 1;

            // update % load
            progress = progress + progressDevice;

            reportServiceLoad.updatePercent(mapReport);
            if (sizeReport == Double.valueOf(i + 1)) {
                mapReport.put("percent", "95");
                reportServiceLoad.updatePercent(mapReport);
            }

        }

        // Cột tổng số lần cảnh báo
        region = new CellRangeAddress(5, 5, 0, 1);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("TỔNG SỐ LẦN CẢNH BÁO");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // =SUM(A2:A10)
        String sumWarning = "SUM(C" + (rowCount + 1) + ":" + "D" + (rowCount + 1) + ")";
        region = new CellRangeAddress(5, 5, 2, 2);
        cell = sheet1.getRow(5)
            .getCell(2);
        cell.setCellFormula(sumWarning);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột tổng của tủ rmu

        String sumWarningRmuH = "SUM(C9" + ":" + "C" + (rowCount) + ")";// NHIỆT ĐỘ
        String sumWarningRmuT = "SUM(D9" + ":" + "D" + (rowCount) + ")";// ĐỘ ẨM

        region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(rowCount)
            .getCell(0);
        cell.setCellValue("TỔNG");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột tổng cảnh báo độ ẩm
        region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        cell = sheet1.getRow(rowCount)
            .getCell(2);
        cell.setCellFormula(sumWarningRmuH);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột tổng cảnh báo nhiệt độ
        region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        cell = sheet1.getRow(rowCount)
            .getCell(3);
        cell.setCellFormula(sumWarningRmuT);
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        if (listData.size() > 0) {
            // Tạo biểu đồ số lần cảnh báo theo thời gian
            XSSFDrawing drawing = sheet1.createDrawingPatriarch();
            XSSFClientAnchor anchor = drawing.createAnchor(9, 19, 9, 19, 4, 7, 10, rowCount + 5);

            XSSFChart chart = drawing.createChart(anchor);

            if (dateType.equals("1")) {
                chart.setTitleText("BIỂU ĐỒ SỐ LẦN CẢNH BÁO LƯỢNG NGÀY " + date);
            } else if (dateType.equals("2")) {
                chart.setTitleText("BIỂU ĐỒ SỐ LẦN CẢNH BÁO LƯỢNG THÁNG " + date);
            } else {
                chart.setTitleText("BIỂU ĐỒ SỐ LẦN CẢNH BÁO LƯỢNG NĂM " + date);
            }
            chart.setTitleOverlay(false);

            // add legend(các kí hiệu của mốc dữ liệu bên phải)
            // XDDFChartLegend legend = chart.getOrAddLegend();
            // legend.setPosition(LegendPosition.TOP_RIGHT);

            XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            bottomAxis.setTitle("KHOANG");
            XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            leftAxis.setTitle("SỐ LẦN CẢN BÁO ");
            leftAxis.setCrossBetween(AxisCrossBetween.BETWEEN);

            XDDFDataSource<String> countries = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                new CellRangeAddress(8, rowCount - 1, 1, 1));

            XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                new CellRangeAddress(8, rowCount - 1, 2, 2));

            XDDFNumericalDataSource<Double> values_2 = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                new CellRangeAddress(8, rowCount - 1, 3, 3));

            // set tiêu đề của biểu đồ( dưới và trái)
            XDDFChartData dataChart = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
            XDDFChartData.Series series = dataChart.addSeries(countries, values);

            XDDFChartData.Series series1 = dataChart.addSeries(countries, values_2);
            series1.setTitle("Phóng điện", null);

            series.setTitle("Quá nhiệt độ, độ ẩm", null);
            dataChart.setVaryColors(true);
            chart.plot(dataChart);
            // create legend
            XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.TOP_RIGHT);

            // in order to transform a bar chart into a column chart, you just need to
            // change the bar direction
            XDDFBarChartData col = (XDDFBarChartData) dataChart;
            col.setBarDirection(BarDirection.COL);

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
        }
        // Tiêu đề báo cáo
        // set font style
        region = new CellRangeAddress(rowCount + 5, rowCount + 5, 0, 2);
        sheet1.addMergedRegion(region);

        cell = sheet1.getRow(rowCount + 5)
            .getCell(0);
        cell.setCellValue("II. MÁY BIẾN ÁP");

        cs1.setAlignment(HorizontalAlignment.LEFT);
        cell.setCellStyle(cs1);

        // Cột TỔNG SỐ LẦN CẢNH BÁO
        region = new CellRangeAddress(rowCount + 6, rowCount + 6, 0, 0);
        cell = sheet1.getRow(rowCount + 6)
            .getCell(0);
        cell.setCellValue("TỔNG SỐ LẦN CẢNH BÁO ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(rowCount + 6, rowCount + 6, 1, 1);
        cell = sheet1.getRow(rowCount + 6)
            .getCell(1);
        cell.setCellValue("0");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột ĐIỆN NĂNG TỔNG
        region = new CellRangeAddress(rowCount + 7, rowCount + 7, 0, 0);
        cell = sheet1.getRow(rowCount + 7)
            .getCell(0);
        cell.setCellValue("ĐIỆN NĂNG TỔNG");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(rowCount + 7, rowCount + 7, 1, 1);
        cell = sheet1.getRow(rowCount + 7)
            .getCell(1);
        cell.setCellValue("0");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Cột thứ tự
        region = new CellRangeAddress(rowCount + 9, rowCount + 9, 0, 0);
        cell = sheet1.getRow(rowCount + 9)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột thiết bị
        region = new CellRangeAddress(rowCount + 9, rowCount + 9, 1, 1);
        cell = sheet1.getRow(rowCount + 9)
            .getCell(1);
        cell.setCellValue("THIẾT BỊ");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột số lần cảnh báo quá tải tổng
        region = new CellRangeAddress(rowCount + 9, rowCount + 9, 2, 2);
        cell = sheet1.getRow(rowCount + 9)
            .getCell(2);
        cell.setCellValue("Số lần cảnh báo quá tải tổng");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột số lần cảnh báo quá tải nhánh
        region = new CellRangeAddress(rowCount + 9, rowCount + 9, 3, 3);
        cell = sheet1.getRow(rowCount + 9)
            .getCell(3);
        cell.setCellValue("Số lần cảnh báo quá tải nhánh");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột Số lần cảnh báo quá nhiệt độ tiếp xúc
        region = new CellRangeAddress(rowCount + 9, rowCount + 9, 4, 4);
        cell = sheet1.getRow(rowCount + 9)
            .getCell(4);
        cell.setCellValue("Số lần cảnh báo quá nhiệt độ tiếp xúc");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột Số lần cảnh báo điện áp cao
        region = new CellRangeAddress(rowCount + 9, rowCount + 9, 5, 5);
        cell = sheet1.getRow(rowCount + 9)
            .getCell(5);
        cell.setCellValue("Số lần cảnh báo điện áp cao");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột Số lần cảnh báo điện áp thấp
        region = new CellRangeAddress(rowCount + 9, rowCount + 9, 6, 6);
        cell = sheet1.getRow(rowCount + 9)
            .getCell(6);
        cell.setCellValue("Số lần cảnh báo điện áp thấp");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột Số lần cảnh báo CosPhi thấp
        region = new CellRangeAddress(rowCount + 9, rowCount + 9, 7, 7);
        cell = sheet1.getRow(rowCount + 9)
            .getCell(7);
        cell.setCellValue("Số lần cảnh báo CosPhi thấp");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột Số lần cảnh báo mất điện tổng
        region = new CellRangeAddress(rowCount + 9, rowCount + 9, 8, 8);
        cell = sheet1.getRow(rowCount + 9)
            .getCell(8);
        cell.setCellValue("Số lần cảnh báo mất điện tổng");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // Cột ĐIỆN NĂNG [kWh]
        region = new CellRangeAddress(rowCount + 9, rowCount + 9, 9, 9);
        cell = sheet1.getRow(rowCount + 9)
            .getCell(9);
        cell.setCellValue("ĐIỆN NĂNG [kWh]");
        formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        // ghi dữ liệu vào table máy biến áp
        int rowCount2 = rowCount + 10;
        int count2 = 1;
        // for (int i = 0; i < dataWarnings.size(); i++) {
        // // Cột thứ tự
        // region = new CellRangeAddress(rowCount2, rowCount2, 0, 0);
        // Cell cellData = sheet1.getRow(rowCount2)
        // .getCell(0);
        // cellData.setCellValue(String.valueOf(count2));
        // formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        // // Cột thiết bị
        // region = new CellRangeAddress(rowCount2, rowCount2, 1, 1);
        // cellData = sheet1.getRow(rowCount2)
        // .getCell(1);
        // cellData.setCellValue(String.valueOf(dataWarnings.get(i)
        // .getDeviceName()));
        // formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        //
        // // Cột số lần cảnh báo quá tải tổng
        // region = new CellRangeAddress(rowCount2, rowCount2, 2, 2);
        // cellData = sheet1.getRow(rowCount2)
        // .getCell(2);
        // cellData.setCellValue(Long.valueOf(dataWarnings.get(i)
        // .getQuaTaiTong()));
        // formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        //
        // // Cột số lần cảnh báo quá tải nhánh
        // region = new CellRangeAddress(rowCount2, rowCount2, 3, 3);
        // cellData = sheet1.getRow(rowCount2)
        // .getCell(3);
        // cellData.setCellValue(Long.valueOf(dataWarnings.get(i)
        // .getQuaTaiNhanh()));
        // formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        //
        // // Cột số lần cảnh báo nhiệt độ tiếp xúc
        // region = new CellRangeAddress(rowCount2, rowCount2, 4, 4);
        // cellData = sheet1.getRow(rowCount2)
        // .getCell(4);
        // cellData.setCellValue(Long.valueOf(dataWarnings.get(i)
        // .getNhietDo()));
        // formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        //
        // // Cột số lần cảnh báo điện áp cao
        // region = new CellRangeAddress(rowCount2, rowCount2, 5, 5);
        // cellData = sheet1.getRow(rowCount2)
        // .getCell(5);
        // cellData.setCellValue(Long.valueOf(dataWarnings.get(i)
        // .getDienApCao()));
        // formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        //
        // // Cột số lần cảnh báo điện áp thấp
        // region = new CellRangeAddress(rowCount2, rowCount2, 6, 6);
        // cellData = sheet1.getRow(rowCount2)
        // .getCell(6);
        // cellData.setCellValue(Long.valueOf(dataWarnings.get(i)
        // .getDienApThap()));
        // formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        //
        // // Cột số lần cảnh báo cos phi thấp
        // region = new CellRangeAddress(rowCount2, rowCount2, 7, 7);
        // cellData = sheet1.getRow(rowCount2)
        // .getCell(7);
        // cellData.setCellValue(Long.valueOf(dataWarnings.get(i)
        // .getCosPhiThap()));
        // formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        //
        // // Cột số lần cảnh báo mất điện tổng
        // region = new CellRangeAddress(rowCount2, rowCount2, 8, 8);
        // cellData = sheet1.getRow(rowCount2)
        // .getCell(8);
        // cellData.setCellValue(Long.valueOf(dataWarnings.get(i)
        // .getMatDienTong()));
        // formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        //
        // // Cột điện năng
        // region = new CellRangeAddress(rowCount2, rowCount2, 9, 9);
        // cellData = sheet1.getRow(rowCount2)
        // .getCell(9);
        // cellData.setCellValue(dataWarnings.get(i)
        // .getEp() != null
        // ? Long.valueOf(dataWarnings.get(i)
        // .getEp())
        // : 0);
        // formatBorder(wb, region, sheet1, cellData, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        //
        // rowCount2 += 1;
        // count2 += 1;
        // }
        // Cột Tổng

        // region = new CellRangeAddress(rowCount2, rowCount2, 0, 1);
        // sheet1.addMergedRegion(region);
        // cell = sheet1.getRow(rowCount2)
        // .getCell(0);
        // cell.setCellValue("TỔNG");
        // formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        //
        // // Cột tổng số lần cảnh báo quá tải tổng
        // int rowCount3 = rowCount + 11;
        // String sum1 = "SUM(C" + (rowCount3) + ":" + "C" + rowCount2 + ")";
        // region = new CellRangeAddress(rowCount2, rowCount2, 2, 2);
        // cell = sheet1.getRow(rowCount2)
        // .getCell(2);
        // cell.setCellFormula(sum1);
        // formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        //
        // // Cột tổng số lần cảnh báo quá tải nhánh
        // String sum2 = "SUM(D" + (rowCount3) + ":" + "D" + rowCount2 + ")";
        // region = new CellRangeAddress(rowCount2, rowCount2, 3, 3);
        // cell = sheet1.getRow(rowCount2)
        // .getCell(3);
        // cell.setCellFormula(sum2);
        // formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        //
        // // Cột tổng số lần cảnh báo quá nhiệt độ tiếp xúc
        // String sum3 = "SUM(E" + (rowCount3) + ":" + "E" + rowCount2 + ")";
        // region = new CellRangeAddress(rowCount2, rowCount2, 4, 4);
        // cell = sheet1.getRow(rowCount2)
        // .getCell(4);
        // cell.setCellFormula(sum3);
        // formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        //
        // // Cột tổng số lần cảnh báo quá tải tổng
        // String sum4 = "SUM(F" + (rowCount3) + ":" + "F" + rowCount2 + ")";
        // region = new CellRangeAddress(rowCount2, rowCount2, 5, 5);
        // cell = sheet1.getRow(rowCount2)
        // .getCell(5);
        // cell.setCellFormula(sum4);
        // formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        //
        // // Cột tổng số lần cảnh báo quá tải tổng
        // String sum5 = "SUM(G" + (rowCount3) + ":" + "G" + rowCount2 + ")";
        // region = new CellRangeAddress(rowCount2, rowCount2, 6, 6);
        // cell = sheet1.getRow(rowCount2)
        // .getCell(6);
        // cell.setCellFormula(sum5);
        // formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        //
        // // Cột tổng số lần cảnh báo quá tải tổng
        // String sum6 = "SUM(H" + (rowCount3) + ":" + "H" + rowCount2 + ")";
        // region = new CellRangeAddress(rowCount2, rowCount2, 7, 7);
        // cell = sheet1.getRow(rowCount2)
        // .getCell(7);
        // cell.setCellFormula(sum6);
        // formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        //
        // // Cột tổng số lần cảnh báo quá tải tổng
        // String sum7 = "SUM(I" + (rowCount3) + ":" + "I" + rowCount2 + ")";
        // region = new CellRangeAddress(rowCount2, rowCount2, 8, 8);
        // cell = sheet1.getRow(rowCount2)
        // .getCell(8);
        // cell.setCellFormula(sum7);
        // formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        //
        // // Cột tổng số lần cảnh báo quá tải tổng
        // String sum8 = "SUM(J" + (rowCount3) + ":" + "J" + rowCount2 + ")";
        // region = new CellRangeAddress(rowCount2, rowCount2, 9, 9);
        // cell = sheet1.getRow(rowCount2)
        // .getCell(9);
        // cell.setCellFormula(sum8);
        // formatHeaderEp(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        IntStream.range(0, 10)
            .forEach(sheet1::autoSizeColumn);

        sheet1.setColumnWidth(0, 5000);
        for (int i = 2; i < 10; i++) {
            sheet1.setColumnWidth(i, 5000);
        }
        sheet1.setColumnWidth(1, 7000);
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
            log.info("ReportController.ReportElectricalPower(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPower(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
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
        reportServiceLoad.updatePercent(mapReport);
        reportServiceLoad.updateStatus(Integer.valueOf(mapReport.get("id")));
        reportServiceLoad.updateTimeFinish(Integer.valueOf(mapReport.get("id")));

    }

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
        // cs.setDataFormat(format.getFormat("0.000"));
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
