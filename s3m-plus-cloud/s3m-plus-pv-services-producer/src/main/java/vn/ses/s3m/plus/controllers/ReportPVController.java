package vn.ses.s3m.plus.controllers;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zeroturnaround.zip.ZipUtil;

import vn.ses.s3m.plus.dto.DataInverter1;
import vn.ses.s3m.plus.dto.Report;
import vn.ses.s3m.plus.service.ReportService;

@RestController
@RequestMapping ("/pv")
public class ReportPVController {

    @Autowired
    private ReportService reportService;
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
    @GetMapping ("/report/{userName}")
    public ResponseEntity<Map<String, List>> loadReport(@PathVariable String userName) {
        log.info("ReportController.loadReport() START");
        int userId = reportService.getUserId(userName);
        Map<String, List> data = new HashMap<>();
        List<Report> report = reportService.getReport(userId);
        data.put("listReport", report);
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
     * @param Date Thời gian.
     * @param reportType Loại báo cáo.
     * @param projectId Mã dự án.
     * @param userName Tên người dùng.
     * @return Trả về 200(Thêm mới thông tin thành công).
     */

    @GetMapping ("/report/addReport/{reportType}/{date}/{userName}/{projectId}")
    public ResponseEntity<Void> addReport(@PathVariable final String reportType, @PathVariable final String date,
        @PathVariable final String userName, @PathVariable final String projectId) throws Exception {
        log.info("ReportController.addRepot()  START");
        int userId = reportService.getUserId(userName);
        // conditions
        List<DataInverter1> dataInverter1 = new ArrayList<>();

        Map<String, String> condition = new HashMap<>();
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
        // check dữ liệu báo cáo
        if (reportType.equals("1")) {
            dataInverter1 = reportService.getDataInverter(date);
            // check trống dữ liệu của ngày
            if (dataInverter1.size() > 0) {
                reportService.addReport(report);
                log.info("ReportController.addReport() ADD REPORT SUCCESS");
                return new ResponseEntity<Void>(HttpStatus.OK);
            } else {
                log.info("ReportController.addReport() ADD REPORT ERROR");
                return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
            }
        } else if (reportType.equals("2")) {
            dataInverter1 = reportService.getDataInverter(date);
            // check trống dữ liệu của tháng
            if (dataInverter1.size() > 0) {
                reportService.addReport(report);
                log.info("ReportController.addReport() ADD REPORT SUCCESS");
                return new ResponseEntity<Void>(HttpStatus.OK);
            } else {
                log.info("ReportController.addReport() ADD REPORT ERROR");
                return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
            }
        } else {
            dataInverter1 = reportService.getDataInverter(date);
            System.out.println("dataInverter1:" + dataInverter1);
            // check trống dữ liệu của năm
            if (dataInverter1.size() > 0) {
                reportService.addReport(report);
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
    @GetMapping ("/report/generateReports/{reportType}/{date}/{userName}/{projectId}")
    public ResponseEntity<?> generateReports(@PathVariable final String reportType, @PathVariable final String date,
        @PathVariable final String userName, @PathVariable final String projectId) throws Exception {

        log.info("ReportController.generateReports() START");

        Map<String, String> condition = new HashMap<>();
        // load%
        Map<String, String> mapReport = new HashMap<>();
        // get userId
        int userId = reportService.getUserId(userName);
        // get projectName
        String projectName = reportService.getProjectName(projectId);
        //
        Map<String, Integer> map = new HashMap<>();
        map.put("userId", userId);
        map.put("limit", 5);
        List<Report> list = reportService.getListByLimit(map);
        Report report1 = list.get(0);
        // load %
        mapReport.put("id", String.valueOf(report1.getId()));
        String path = null;
        if (reportType.equals("1")) {
            // Đường dẫn lưu file báo cáo
            path = folderName + File.separator + report1.getUrl();
            // lấy dữ liệu trong db
            List<Map<String, String>> listData = new ArrayList<>();
            long ElectricityBill = 0;
            for (int i = 0; i < 24; i++) {
                DataInverter1 dataInverter1 = new DataInverter1();
                String fromDate = "";
                String toDate = "";
                Map<String, String> mapData = new HashMap<>();
                if (i < 10) {
                    fromDate = date + " 0" + i + ":00:00";
                    toDate = date + " 0" + i + ":59:59";
                } else {
                    fromDate = date + " " + i + ":00:00";
                    toDate = date + " " + i + ":59:59";
                }
                condition.put("fromDate", fromDate);
                condition.put("toDate", toDate);
                dataInverter1 = reportService.getDataInverterInDay(condition);
                mapData.put("wH", dataInverter1 != null ? String.valueOf(dataInverter1.getWh()) : "0");
                mapData.put("fromDate", fromDate);
                mapData.put("toDate", toDate);
                listData.add(mapData);

                if (dataInverter1 != null) {
                    ElectricityBill = ElectricityBill + dataInverter1.getWh();
                }

            }
            createReportSyntheticExcelInDay(listData, path, mapReport, projectName, date, ElectricityBill);
        } else if (reportType.equals("2")) {

            // Đường dẫn lưu file báo cáo
            path = folderName + File.separator + report1.getUrl();
            // lấy dữ liệu trong db
            List<Map<String, String>> listData = new ArrayList<>();
            long ElectricityBill = 0;
            for (int i = 1; i < 32; i++) {
                DataInverter1 dataInverter1 = new DataInverter1();
                String fromDate = "";
                String toDate = "";
                Map<String, String> mapData = new HashMap<>();
                if (i < 10) {
                    fromDate = date + "-" + "0" + i;
                    toDate = date + "-" + "0" + i + " 23:59:59";
                } else {
                    fromDate = date + "-" + i;
                    toDate = date + "-" + i + " 23:59:59";
                }
                condition.put("fromDate", fromDate);
                condition.put("toDate", toDate);
                dataInverter1 = reportService.getDataInverterInDay(condition);
                mapData.put("wH", dataInverter1 != null ? String.valueOf(dataInverter1.getWh()) : "0");
                mapData.put("fromDate", fromDate);
                mapData.put("toDate", toDate);
                listData.add(mapData);
                if (dataInverter1 != null) {
                    ElectricityBill = ElectricityBill + dataInverter1.getWh();
                }

            }
            createReportSyntheticExcelInMonth(listData, path, mapReport, projectName, date, ElectricityBill);

        } else {

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
        final Map<String, String> mapReport, final String projectName, final String date, final long ElectricityBill)
        throws FileNotFoundException, IOException {
        log.info("ReportController.createReportSyntheticExcel(): START");

        System.out.println("data;" + data);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Báo cáo tổng hợp");

        Row row;
        Cell cell;
        for (int z = 0; z < 10; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 36; j++) {
                row.createCell(j);
            }
        }
        row = sheet1.createRow(34);
        for (int j = 0; j < 36; j++) {
            row.createCell(j);
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
        cell.setCellValue("GIÁ ĐIỆN[kWh/kWh]");
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
        cell.setCellValue(String.valueOf(String.valueOf(totalMoney)));
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
        region = new CellRangeAddress(34, 34, 0, 1);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(34)
            .getCell(0);
        cell.setCellValue("TỔNG");
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(34, 34, 2, 2);
        cell = sheet1.getRow(34)
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
            for (int j = rowCount; j < rowCount + 1; j++) {
                row = sheet1.createRow(j);
                for (int k = 0; k < 36; k++) {
                    row.createCell(k);
                }
            }
            // put percent %
            mapReport.put("percent", String.valueOf(progress));

            // Cột thứ tự
            region = new CellRangeAddress(rowCount, rowCount, 0, 0);
            Cell cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(count);
            CellStyle cs = wb.createCellStyle();
            cs.setAlignment(HorizontalAlignment.CENTER);
            cellData.setCellStyle(cs);

            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 1, 1);
            cellData = sheet1.getRow(rowCount)
                .getCell(1);
            cellData.setCellValue(data.get(i)
                .get("fromDate"));

            // Cột giá trị
            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount)
                .getCell(2);
            cellData.setCellValue(Integer.valueOf(data.get(i)
                .get("wH")));
            cs.setAlignment(HorizontalAlignment.CENTER);
            cellData.setCellStyle(cs);

            rowCount += 1;
            count += 1;

            // update % load
            progress = progress + progressDevice;

            reportService.updatePercent(mapReport);
            if (sizeReport == Double.valueOf(i + 1)) {
                mapReport.put("percent", "100");
                reportService.updatePercent(mapReport);
            }
        }

        // Tạo chart
        // Create row and put some cells in it. Rows and cells are 0 based.
        // set kích thước cho biểu đồ
        XSSFDrawing drawing = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(8, 18, 8, 18, 3, 9, 16, 34);

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

        // in order to transform a bar chart into a column chart, you just need to change the bar direction
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

        IntStream.range(0, 10)
            .forEach(sheet1::autoSizeColumn);

        // export file
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        String exportFilePath = path + File.separator + "ReportSynthetic" + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;
        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.createElectricalExcel(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.createElectricalExcel(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
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
        ZipUtil.pack(folder, new File(path + ".zip"));
        reportService.updateStatus(Integer.valueOf(mapReport.get("id")));
        reportService.updateTimeFinish(Integer.valueOf(mapReport.get("id")));

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
        final Map<String, String> mapReport, final String projectName, final String date, final long ElectricityBill)
        throws FileNotFoundException, IOException {
        log.info("ReportController.createReportSyntheticExcel(): START");

        System.out.println("data;" + data);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Báo cáo tổng hợp");

        Row row;
        Cell cell;
        for (int z = 0; z < 10; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 36; j++) {
                row.createCell(j);
            }
        }
        row = sheet1.createRow(41);
        for (int j = 0; j < 36; j++) {
            row.createCell(j);
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
        cell.setCellValue(String.valueOf(ElectricityBill));
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá điện
        region = new CellRangeAddress(6, 6, 0, 0);
        cell = sheet1.getRow(6)
            .getCell(0);
        cell.setCellValue("GIÁ ĐIỆN[kWh/kWh]");
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
            for (int j = rowCount; j < rowCount + 1; j++) {
                row = sheet1.createRow(j);
                for (int k = 0; k < 36; k++) {
                    row.createCell(k);
                }
            }
            // put percent %
            mapReport.put("percent", String.valueOf(progress));

            // Cột thứ tự
            region = new CellRangeAddress(rowCount, rowCount, 0, 0);
            Cell cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(count);
            CellStyle cs = wb.createCellStyle();
            cs.setAlignment(HorizontalAlignment.CENTER);
            cellData.setCellStyle(cs);

            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 1, 1);
            cellData = sheet1.getRow(rowCount)
                .getCell(1);
            cellData.setCellValue(data.get(i)
                .get("fromDate"));

            // Cột giá trị
            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount)
                .getCell(2);
            cellData.setCellValue(Integer.valueOf(data.get(i)
                .get("wH")));
            cs.setAlignment(HorizontalAlignment.CENTER);
            cellData.setCellStyle(cs);

            rowCount += 1;
            count += 1;

            // update % load
            progress = progress + progressDevice;

            reportService.updatePercent(mapReport);
            if (sizeReport == Double.valueOf(i + 1)) {
                mapReport.put("percent", "100");
                reportService.updatePercent(mapReport);
            }
        }

        // Tạo chart
        // Create row and put some cells in it. Rows and cells are 0 based.
        // set kích thước cho biểu đồ
        XSSFDrawing drawing = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(8, 18, 8, 18, 3, 9, 16, 42);

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

        // in order to transform a bar chart into a column chart, you just need to change the bar direction
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

        IntStream.range(0, 10)
            .forEach(sheet1::autoSizeColumn);

        // export file
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        String exportFilePath = path + File.separator + "ReportSynthetic" + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;
        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.createElectricalExcel(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.createElectricalExcel(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
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
        ZipUtil.pack(folder, new File(path + ".zip"));
        reportService.updateStatus(Integer.valueOf(mapReport.get("id")));
        reportService.updateTimeFinish(Integer.valueOf(mapReport.get("id")));

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
        final Map<String, String> mapReport, final String projectName, final String date, final long ElectricityBill)
        throws FileNotFoundException, IOException {
        log.info("ReportController.createReportSyntheticExcel(): START");

        System.out.println("data;" + data);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("Báo cáo tổng hợp");

        Row row;
        Cell cell;
        for (int z = 0; z < 10; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 36; j++) {
                row.createCell(j);
            }
        }
        row = sheet1.createRow(41);
        for (int j = 0; j < 36; j++) {
            row.createCell(j);
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
        cell.setCellValue(String.valueOf(ElectricityBill));
        formatHeader(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá điện
        region = new CellRangeAddress(6, 6, 0, 0);
        cell = sheet1.getRow(6)
            .getCell(0);
        cell.setCellValue("GIÁ ĐIỆN[kWh/kWh]");
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
            for (int j = rowCount; j < rowCount + 1; j++) {
                row = sheet1.createRow(j);
                for (int k = 0; k < 36; k++) {
                    row.createCell(k);
                }
            }
            // put percent %
            mapReport.put("percent", String.valueOf(progress));

            // Cột thứ tự
            region = new CellRangeAddress(rowCount, rowCount, 0, 0);
            Cell cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(count);
            CellStyle cs = wb.createCellStyle();
            cs.setAlignment(HorizontalAlignment.CENTER);
            cellData.setCellStyle(cs);

            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 1, 1);
            cellData = sheet1.getRow(rowCount)
                .getCell(1);
            cellData.setCellValue(data.get(i)
                .get("fromDate"));

            // Cột giá trị
            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount)
                .getCell(2);
            cellData.setCellValue(Integer.valueOf(data.get(i)
                .get("wH")));
            cs.setAlignment(HorizontalAlignment.CENTER);
            cellData.setCellStyle(cs);

            rowCount += 1;
            count += 1;

            // update % load
            progress = progress + progressDevice;

            reportService.updatePercent(mapReport);
            if (sizeReport == Double.valueOf(i + 1)) {
                mapReport.put("percent", "100");
                reportService.updatePercent(mapReport);
            }
        }

        // Tạo chart
        // Create row and put some cells in it. Rows and cells are 0 based.
        // set kích thước cho biểu đồ
        XSSFDrawing drawing = sheet1.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(8, 18, 8, 18, 3, 9, 16, 42);

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

        // in order to transform a bar chart into a column chart, you just need to change the bar direction
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

        IntStream.range(0, 10)
            .forEach(sheet1::autoSizeColumn);

        // export file
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        String exportFilePath = path + File.separator + "ReportSynthetic" + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;
        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.createElectricalExcel(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.createElectricalExcel(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
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
        ZipUtil.pack(folder, new File(path + ".zip"));
        reportService.updateStatus(Integer.valueOf(mapReport.get("id")));
        reportService.updateTimeFinish(Integer.valueOf(mapReport.get("id")));

    }

    private void formatHeader(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet, final Cell cell,
        final short bgColor, final HorizontalAlignment hAlign, final int indent) {

        CellStyle cs = wb.createCellStyle();
        cs.setFillBackgroundColor(bgColor);
        cs.setFillForegroundColor(bgColor);
        cs.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);

        Font font = wb.createFont();
        font.setBold(true);
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
