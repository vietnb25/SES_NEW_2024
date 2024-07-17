package vn.ses.s3m.plus.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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

import lombok.extern.slf4j.Slf4j;
import vn.ses.s3m.plus.dto.DataInverter1;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.response.ChartOperationResponse;
import vn.ses.s3m.plus.response.OperationElectricalResponse;
import vn.ses.s3m.plus.service.DeviceService;
import vn.ses.s3m.plus.service.OperationPVService;

@RestController
@Slf4j
@RequestMapping ("/pv/operation")
public class OperationInverterPVController {

    @Autowired
    private OperationPVService operationPVService;

    @Autowired
    private DeviceService deviceService;

    @Value ("${pv.producer.export-folder}")
    private String folderName;

    private static final Integer PAGE_SIZE = 50;

    // Các tham số truy vấn
    private static final String DEVICE_ID = "deviceId";

    private static final String FROM_DATE = "fromDate";

    private static final String TO_DATE = "toDate";

    private static final String TIME_START = " 00:00:00";

    private static final String TIME_END = " 23:59:59";

    private static final String SORT = "sort";

    // private static final String SORT_ASC = "ASC";

    private static final String SORT_DESC = "DESC";

    private static final String PAGE_START = "start";

    private static final String PAGE_END = "end";

    private static final String TOTAL_PAGE_STR = "totalPage";

    private static final String CURRENT_PAGE_STR = "currentPage";

    private static final String TOTAL_DATA_STR = "totalData";

    private static final String DATA = "data";

    /**
     * Lấy thông tin thông số điện tức thời PV
     *
     * @param deviceId Mã thiết bị
     * @return Thông tin thông số điện tức thời
     */
    @GetMapping ("/instant/inverter/{deviceId}")
    public ResponseEntity<OperationElectricalResponse> getInstantOperationInverterPV(
        @PathVariable final Long deviceId) {

        log.info("getInstantOperationInverterPV START");

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        DataInverter1 inverter = operationPVService.getInstantOperationInverterPV(condition);

        OperationElectricalResponse data = new OperationElectricalResponse(inverter);

        log.info("getInstantOperationInverterPV END");

        return new ResponseEntity<OperationElectricalResponse>(data, HttpStatus.OK);
    }

    /**
     * Lấy danh sách thông tin vận hành
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức truy vấn
     * @param page Trang lấy dữ liệu
     * @return Danh sách thông tin vận hànhs
     */
    @GetMapping ("/inverter/{deviceId}/{page}")
    public ResponseEntity<?> getOperationInverterPV(@PathVariable final Long deviceId,
        @RequestParam final String fromDate, @RequestParam final String toDate, @PathVariable final Integer page) {

        log.info("getOperationInverterPV START");

        List<OperationElectricalResponse> dataInfo = new ArrayList<>();

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        condition.put(FROM_DATE, fromDate.concat(TIME_START));
        condition.put(TO_DATE, toDate.concat(TIME_END));
        condition.put(SORT, SORT_DESC);
        condition.put(PAGE_START, (page - 1) * PAGE_SIZE);
        condition.put(PAGE_END, PAGE_SIZE);

        int totalData = operationPVService.countDataOperationInverterPV(condition);

        double totalPage = Math.ceil((double) totalData / PAGE_SIZE);

        List<DataInverter1> inverter1s = operationPVService.getOperationInverterPV(condition);

        // object to response to client
        Map<String, Object> data = new HashMap<>();

        data.put(TOTAL_PAGE_STR, totalPage);
        data.put(CURRENT_PAGE_STR, page);
        data.put(TOTAL_DATA_STR, totalData);

        if (inverter1s.size() > 0) {
            inverter1s.forEach(i -> {
                OperationElectricalResponse res = new OperationElectricalResponse(i);
                dataInfo.add(res);
            });
            data.put(DATA, dataInfo);

            log.info("getOperationInverterPV END");

            return new ResponseEntity<>(data, HttpStatus.OK);
        } else {

            log.info("getOperationInverterPV END");
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * Lấy thông tin dữ liệu biểu đồ điện áp
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức tuy vấn
     * @return Danh sách dữ liệu biểu đồ điện áp
     */
    @GetMapping ("/chart/voltage/inverter/{deviceId}")
    public ResponseEntity<?> getChartVontageInverter(@PathVariable final Long deviceId,
        @RequestParam final String fromDate, @RequestParam final String toDate) {

        log.info("getChartVontageInverter START");

        List<ChartOperationResponse> data = new ArrayList<>();

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        condition.put(FROM_DATE, fromDate);
        condition.put(TO_DATE, toDate);

        List<DataInverter1> inverter1s = operationPVService.getOperationInverterPV(condition);

        if (inverter1s.size() > 0) {
            inverter1s.forEach(i -> {
                ChartOperationResponse res = new ChartOperationResponse(i);
                data.add(res);
            });

            log.info("getChartVontageInverter END");

            return new ResponseEntity<>(data, HttpStatus.OK);
        } else {

            log.info("getChartVontageInverter END");
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * Lấy thông tin dữ liệu biểu đồ dòng điện
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức tuy vấn
     * @return Danh sách dữ liệu biểu đồ dòng điện
     */
    @GetMapping ("/chart/electric/inverter/{deviceId}")
    public ResponseEntity<?> getChartElectricInverter(@PathVariable final Long deviceId,
        @RequestParam final String fromDate, @RequestParam final String toDate) {

        log.info("getChartElectricInverter START");

        List<ChartOperationResponse> data = new ArrayList<>();

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        condition.put(FROM_DATE, fromDate);
        condition.put(TO_DATE, toDate);

        List<DataInverter1> inverter1s = operationPVService.getOperationInverterPV(condition);

        if (inverter1s.size() > 0) {
            inverter1s.forEach(i -> {
                ChartOperationResponse res = new ChartOperationResponse(i);
                data.add(res);
            });

            log.info("getChartElectricInverter END");

            return new ResponseEntity<>(data, HttpStatus.OK);
        } else {

            log.info("getChartElectricInverter END");

            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * Lấy thông tin dữ liệu biểu đồ công suất
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức tuy vấn
     * @return Danh sách dữ liệu biểu đồ công suất
     */
    @GetMapping ("/chart/effective-power/inverter/{deviceId}")
    public ResponseEntity<?> getChartEffectivePowerInverter(@PathVariable final Long deviceId,
        @RequestParam final String fromDate, @RequestParam final String toDate) {

        log.info("getChartEffectivePowerInverter START");

        List<ChartOperationResponse> data = new ArrayList<>();

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        condition.put(FROM_DATE, fromDate);
        condition.put(TO_DATE, toDate);

        List<DataInverter1> inverter1s = operationPVService.getOperationInverterPV(condition);

        if (inverter1s.size() > 0) {
            inverter1s.forEach(i -> {
                ChartOperationResponse res = new ChartOperationResponse(i);
                data.add(res);
            });

            log.info("getChartEffectivePowerInverter END");

            return new ResponseEntity<>(data, HttpStatus.OK);
        } else {

            log.info("getChartEffectivePowerInverter END");

            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * Dowload thông số điện PV.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/device-parameter/inverter/{deviceId}/{type}")
    public ResponseEntity<Resource> downloadElectricalParamInverter(@PathVariable final Long deviceId,
        @RequestParam final String fromDate, @RequestParam final String toDate, @PathVariable final Integer type)
        throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        condition.put(FROM_DATE, fromDate);
        condition.put(TO_DATE, toDate);

        List<DataInverter1> data = operationPVService.getOperationInverterPV(condition);

        if (data.size() > 0) {
            // time miliseconds
            long miliseconds = new Date().getTime();

            Device device = deviceService.getDeviceByDeviceId(condition);

            // path folder
            String path = this.folderName + File.separator + miliseconds;

            // tạo excel
            createElectricalParamExcel(data, fromDate, toDate, device, path);

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

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .body(resource);
        } else {

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<Resource>(HttpStatus.OK);
        }

    }

    /**
     * Tạo excel thông số điện PV.
     *
     * @param data Danh sách dữ liệu thông số điện PV.
     * @throws Exception
     */
    // CHECKSTYLE:OFF
    private void createElectricalParamExcel(final List<DataInverter1> data, final String fromDate, final String toDate,
        final Device device, final String path) throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(data.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Thông số điện");
        Row row;
        Cell cell;

        // Page orientation
        sheet.getPrintSetup()
            .setLandscape(false);

        // Page margins
        sheet.setMargin(Sheet.RightMargin, 0.5);
        sheet.setMargin(Sheet.LeftMargin, 0.5);
        sheet.setMargin(Sheet.TopMargin, 0.5);
        sheet.setMargin(Sheet.BottomMargin, 0.5);

        // Tạo sheet content
        for (int i = 0; i < 7; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 36; j++) {
                row.createCell(j);
            }
        }

        // set độ rộng của cột
        sheet.setColumnWidth(0, 1300);
        sheet.setColumnWidth(1, 5200);
        sheet.setColumnWidth(3, 5000);
        sheet.setColumnWidth(4, 5000);
        sheet.setColumnWidth(7, 5000);
        sheet.setColumnWidth(8, 5000);
        sheet.setColumnWidth(11, 5000);

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 11);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(0)
            .getCell(0);
        cell.setCellValue("BÁO CÁO VẬN HÀNH");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(0);
        cell.setCellValue("Mã thiết bị");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(2, 2, 2, 4);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(2);
        cell.setCellValue(device.getDeviceId() != null ? String.valueOf(device.getDeviceId()) : "-");
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(3, 3, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(0);
        cell.setCellValue("Tên thiết bị");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(3, 3, 2, 4);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(2);
        cell.setCellValue(device.getDeviceName() != null
            ? device.getDeviceName()
                .toUpperCase()
            : "-");
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(2, 3, 8, 9);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(8);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 10, 11);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(10);
        cell.setCellValue(fromDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        region = new CellRangeAddress(3, 3, 10, 11);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(10);
        cell.setCellValue(toDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        // bảng thông số vận hành
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
        cell.setCellValue("Pha");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("Điện áp AC [V]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("Dòng điện AC [A]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("PF");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 6, 6);
        cell = sheet.getRow(5)
            .getCell(6);
        cell.setCellValue("PAC [kW]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 7, 7);
        cell = sheet.getRow(5)
            .getCell(7);
        cell.setCellValue("Điện áp DC [V]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 8, 8);
        cell = sheet.getRow(5)
            .getCell(8);
        cell.setCellValue("Dòng điện DC [A]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 9, 9);
        cell = sheet.getRow(5)
            .getCell(9);
        cell.setCellValue("PDC [kW]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 10, 10);
        cell = sheet.getRow(5)
            .getCell(10);
        cell.setCellValue("F [Hz]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 11, 11);
        cell = sheet.getRow(5)
            .getCell(11);
        cell.setCellValue("Yield [kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataInverter1 item = data.get(m);
            for (int i = index; i < index + 3; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 36; j++) {
                    row.createCell(j);
                }
            }

            // thứ tự
            region = new CellRangeAddress(index, index, 0, 0);
            cell = sheet.getRow(index)
                .getCell(0);
            cell.setCellValue(m + 1);

            // Cột thời gian
            region = new CellRangeAddress(index, index, 1, 1);
            cell = sheet.getRow(index)
                .getCell(1);
            cell.setCellValue(sdf.format(sdf.parse(item.getSentDate())));

            // Cột Pha
            region = new CellRangeAddress(index, index, 2, 2);
            cell = sheet.getRow(index)
                .getCell(2);
            cell.setCellValue("A");

            region = new CellRangeAddress(index + 1, index + 1, 2, 2);
            cell = sheet.getRow(index + 1)
                .getCell(2);
            cell.setCellValue("B");

            region = new CellRangeAddress(index + 2, index + 2, 2, 2);
            cell = sheet.getRow(index + 2)
                .getCell(2);
            cell.setCellValue("C");

            // cột điện áp
            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getPPVphA() != null ? String.valueOf(item.getPPVphA()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 3, 3);
            cell = sheet.getRow(index + 1)
                .getCell(3);
            cell.setCellValue(item.getPPVphB() != null ? String.valueOf(item.getPPVphB()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 3, 3);
            cell = sheet.getRow(index + 2)
                .getCell(3);
            cell.setCellValue(item.getPPVphC() != null ? String.valueOf(item.getPPVphC()) : "-");

            // cột dòng điện
            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue(item.getAphaA() != null ? String.valueOf(item.getAphaA()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 4, 4);
            cell = sheet.getRow(index + 1)
                .getCell(4);
            cell.setCellValue(item.getAphaB() != null ? String.valueOf(item.getAphaB()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 4, 4);
            cell = sheet.getRow(index + 2)
                .getCell(4);
            cell.setCellValue(item.getAphaC() != null ? String.valueOf(item.getAphaC()) : "-");

            region = new CellRangeAddress(index, index, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue(item.getPF() != null ? String.valueOf(item.getPF()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 5, 5);
            cell = sheet.getRow(index + 1)
                .getCell(5);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 2, index + 2, 5, 5);
            cell = sheet.getRow(index + 2)
                .getCell(5);
            cell.setCellValue("-");

            region = new CellRangeAddress(index, index, 6, 6);
            cell = sheet.getRow(index)
                .getCell(6);
            cell.setCellValue(item.getW() != null ? String.valueOf(Math.round(item.getW() / 1000)) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 6, 6);
            cell = sheet.getRow(index + 1)
                .getCell(6);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 2, index + 2, 6, 6);
            cell = sheet.getRow(index + 2)
                .getCell(6);
            cell.setCellValue("-");

            region = new CellRangeAddress(index, index, 7, 7);
            cell = sheet.getRow(index)
                .getCell(7);
            cell.setCellValue(item.getDCV() != null ? String.valueOf(item.getDCV()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 7, 7);
            cell = sheet.getRow(index + 1)
                .getCell(7);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 2, index + 2, 7, 7);
            cell = sheet.getRow(index + 2)
                .getCell(7);
            cell.setCellValue("-");

            region = new CellRangeAddress(index, index, 8, 8);
            cell = sheet.getRow(index)
                .getCell(8);
            cell.setCellValue(item.getDCA() != null ? String.valueOf(item.getDCA()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 8, 8);
            cell = sheet.getRow(index + 1)
                .getCell(8);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 2, index + 2, 8, 8);
            cell = sheet.getRow(index + 2)
                .getCell(8);
            cell.setCellValue("-");

            region = new CellRangeAddress(index, index, 9, 9);
            cell = sheet.getRow(index)
                .getCell(9);
            cell.setCellValue(item.getDCW() != null ? String.valueOf(Math.round(item.getDCW() / 1000)) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 9, 9);
            cell = sheet.getRow(index + 1)
                .getCell(9);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 2, index + 2, 9, 9);
            cell = sheet.getRow(index + 2)
                .getCell(9);
            cell.setCellValue("-");

            // cột THD U
            region = new CellRangeAddress(index, index, 10, 10);
            cell = sheet.getRow(index)
                .getCell(10);
            cell.setCellValue(item.getHz() != null ? String.valueOf(item.getHz()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 10, 10);
            cell = sheet.getRow(index + 1)
                .getCell(10);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 2, index + 2, 10, 10);
            cell = sheet.getRow(index + 2)
                .getCell(10);
            cell.setCellValue("-");

            region = new CellRangeAddress(index, index, 11, 11);
            cell = sheet.getRow(index)
                .getCell(11);
            cell.setCellValue(item.getWh() != null ? String.valueOf(Math.round(item.getWh() / 1000)) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 11, 11);
            cell = sheet.getRow(index + 1)
                .getCell(11);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 2, index + 2, 11, 11);
            cell = sheet.getRow(index + 2)
                .getCell(11);
            cell.setCellValue("-");

            index += 3;
        }

        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // tạo file excel vào folder export
        String exportFilePath = path + File.separator + "thongsodien.xlsx";

        File file = new File(exportFilePath);

        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + ": Create file excel success");
        } catch (FileNotFoundException e) {
            log.error(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + ": ERROR File Not Found while export file excel.");
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

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " END");
    }
    // CHECKSTYLE:ON

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
    // CHECKSTYLE:ON
}
