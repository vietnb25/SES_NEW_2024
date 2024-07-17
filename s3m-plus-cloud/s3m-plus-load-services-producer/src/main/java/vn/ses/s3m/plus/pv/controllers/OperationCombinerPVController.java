package vn.ses.s3m.plus.pv.controllers;

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

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.CreationHelper;
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
import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.common.DateUtils;
import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dao.UserMapper;
import vn.ses.s3m.plus.dto.DataCombiner1;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.pv.response.ChartOperationCombinerResponse;
import vn.ses.s3m.plus.pv.response.OperationCombinerResponse;
import vn.ses.s3m.plus.pv.service.OperationCombinerPVService;
import vn.ses.s3m.plus.service.DeviceService;

@RestController
@Slf4j
@RequestMapping ("/pv/operation")
public class OperationCombinerPVController {

    @Autowired
    private OperationCombinerPVService combinerPVService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private UserMapper userMapper;

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

    private static final String SORT_DESC = "DESC";

    private static final String PAGE_START = "start";

    private static final String PAGE_END = "end";

    private static final String TOTAL_PAGE_STR = "totalPage";

    private static final String CURRENT_PAGE_STR = "currentPage";

    private static final String TOTAL_DATA_STR = "totalData";

    private static final String DATA = "data";

    private static final String SCHEMA = "schema";

    private static final String SORT_ASC = "ASC";

    /**
     * Lấy thông tin thông số tức thời Combiner PV
     *
     * @param deviceId Mã thiết bị
     * @return Thông tin thông số điện tức thời
     */
    @GetMapping ("/instant/combiner/{customerId}/{deviceId}")
    public ResponseEntity<?> getInstantOperationCombinerPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId) {

        log.info("getInstantOperationCombinerPV START");

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        condition.put(SCHEMA, Schema.getSchemas(customerId));
        DataCombiner1 combiner = combinerPVService.getInstantOperationCombinerPV(condition);

        if (combiner != null) {

            OperationCombinerResponse data = new OperationCombinerResponse(combiner);

            log.info("getInstantOperationCombinerPV END");

            return new ResponseEntity<>(data, HttpStatus.OK);
        } else {

            log.info("getInstantOperationCombinerPV END");

            return new ResponseEntity<>(HttpStatus.OK);
        }

    }

    /**
     * Lấy danh sách thông tin vận hành Combiner
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức truy vấn
     * @param page Trang lấy dữ liệu
     * @return Danh sách thông tin vận hành
     */
    @GetMapping ("/combiner/{customerId}/{deviceId}/{page}")
    public ResponseEntity<?> getOperationCombinerPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @PathVariable final Integer page) {

        log.info("getOperationCombinerPV START");

        List<OperationCombinerResponse> data = new ArrayList<>();
        int[] duration = new int[2];

        long from = DateUtils.toDate(fromDate + TIME_START, Constants.ES.DATETIME_FORMAT_YMDHMS)
            .getTime() / 1000;
        long to = DateUtils.toDate(toDate + TIME_END, Constants.ES.DATETIME_FORMAT_YMDHMS)
            .getTime() / 1000;

        if (from <= to) {
            for (int i = 0; i < Constants.DATA.times.length; i++) {
                if (Long.compare(from, Constants.DATA.times[i]) > 0) {
                    duration[0] = i + 1;
                }

                if (Long.compare(to, Constants.DATA.times[i]) > 0) {
                    duration[1] = i + 1;
                }
            }
        }

        List<DataCombiner1> dataInfo = new ArrayList<>();

        Map<String, Object> condition = new HashMap<>();
        int pageSize = PAGE_SIZE;
        int totalData = 0;
        int pageTable = page;
        String soft = SORT_DESC;
        for (int i = duration[1]; i >= duration[0]; i--) {
            String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.COMBINER1));
            condition.put(SCHEMA, schema);
            condition.put(DEVICE_ID, deviceId);
            condition.put(FROM_DATE, fromDate.concat(TIME_START));
            condition.put(TO_DATE, toDate.concat(TIME_END));
            condition.put(SCHEMA, schema);
            condition.put(SORT, soft);
            condition.put(PAGE_START, (pageTable - 1) * PAGE_SIZE);
            condition.put(PAGE_END, pageSize);

            totalData = combinerPVService.countDataOperationCombinerPV(condition);

            List<DataCombiner1> combiner1s = combinerPVService.getOperationCombinerPV(condition);

            if (combiner1s.size() <= PAGE_SIZE) {
                pageSize = PAGE_SIZE - combiner1s.size();
                pageTable = 1;
                soft = SORT_ASC;
            } else {
                pageSize = PAGE_SIZE;
                pageTable = page;
                soft = SORT_DESC;
            }
            dataInfo.addAll(combiner1s);
        }

        double totalPage = Math.ceil((double) totalData / PAGE_SIZE);

        // object to response to client
        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put(TOTAL_PAGE_STR, totalPage);
        dataMap.put(CURRENT_PAGE_STR, page);
        dataMap.put(TOTAL_DATA_STR, totalData);

        if (dataInfo.size() > 0) {
            dataInfo.forEach(i -> {
                OperationCombinerResponse res = new OperationCombinerResponse(i);
                data.add(res);
            });
            dataMap.put(DATA, data);

            log.info("getOperationCombinerPV END");

            return new ResponseEntity<>(dataMap, HttpStatus.OK);
        } else {

            log.info("getOperationCombinerPV END");
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * Lấy thông tin dữ liệu biểu đồ Combiner
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức tuy vấn
     * @return Danh sách dữ liệu biểu đồ điện áp
     */
    @GetMapping ("/chart/combiner/{customerId}/{deviceId}")
    public ResponseEntity<?> getChartCombinerPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate) {

        log.info("getChartCombinerPV START");

        List<ChartOperationCombinerResponse> data = new ArrayList<>();

        int[] duration = new int[2];

        long from = DateUtils.toDate(fromDate + TIME_START, Constants.ES.DATETIME_FORMAT_YMDHMS)
            .getTime() / 1000;
        long to = DateUtils.toDate(toDate + TIME_END, Constants.ES.DATETIME_FORMAT_YMDHMS)
            .getTime() / 1000;

        if (from <= to) {
            for (int i = 0; i < Constants.DATA.times.length; i++) {
                if (Long.compare(from, Constants.DATA.times[i]) > 0) {
                    duration[0] = i + 1;
                }

                if (Long.compare(to, Constants.DATA.times[i]) > 0) {
                    duration[1] = i + 1;
                }
            }
        }

        List<DataCombiner1> dataInfo = new ArrayList<>();

        Map<String, Object> condition = new HashMap<>();
        for (int i = duration[0]; i <= duration[1]; i++) {
            String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.COMBINER1));
            condition.put(DEVICE_ID, deviceId);
            condition.put(FROM_DATE, fromDate.concat(TIME_START));
            condition.put(TO_DATE, toDate.concat(TIME_END));
            condition.put(SCHEMA, schema);
            List<DataCombiner1> combiner1s = combinerPVService.getOperationCombinerPV(condition);
            dataInfo.addAll(combiner1s);
        }

        if (dataInfo.size() > 0) {
            dataInfo.forEach(i -> {
                ChartOperationCombinerResponse res = new ChartOperationCombinerResponse(i);
                data.add(res);
            });

            log.info("getChartCombinerPV END");

            return new ResponseEntity<>(data, HttpStatus.OK);
        } else {

            log.info("getChartCombinerPV END");
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * Dowload thông số Combiner PV.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/device-parameter/combiner/{customerId}/{deviceId}")
    public ResponseEntity<Resource> downloadDeviceParameterCombinerPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @RequestParam final String userName) throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        // get url image
        User user = userMapper.getUserByUsername(userName);
        String pngImageURL = user.getImg();
        String encodingPrefix = "base64,";
        int contentStartIndex = pngImageURL.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData = org.apache.commons.codec.binary.Base64
            .decodeBase64(pngImageURL.substring(contentStartIndex));

        int[] duration = new int[2];

        long from = DateUtils.toDate(fromDate + TIME_START, Constants.ES.DATETIME_FORMAT_YMDHMS)
            .getTime() / 1000;
        long to = DateUtils.toDate(toDate + TIME_END, Constants.ES.DATETIME_FORMAT_YMDHMS)
            .getTime() / 1000;

        if (from <= to) {
            for (int i = 0; i < Constants.DATA.times.length; i++) {
                if (Long.compare(from, Constants.DATA.times[i]) > 0) {
                    duration[0] = i + 1;
                }

                if (Long.compare(to, Constants.DATA.times[i]) > 0) {
                    duration[1] = i + 1;
                }
            }
        }

        List<DataCombiner1> data = new ArrayList<>();

        Map<String, Object> condition = new HashMap<>();
        for (int i = duration[0]; i <= duration[1]; i++) {
            String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.COMBINER1));
            condition.put(DEVICE_ID, deviceId);
            condition.put(FROM_DATE, fromDate.concat(TIME_START));
            condition.put(TO_DATE, toDate.concat(TIME_END));
            condition.put(SCHEMA, schema);
            List<DataCombiner1> combiner1s = combinerPVService.getOperationCombinerPV(condition);
            data.addAll(combiner1s);
        }

        if (data.size() > 0) {
            // time miliseconds
            long miliseconds = new Date().getTime();

            Device device = deviceService.getDeviceByDeviceId(condition);

            // path folder
            String path = this.folderName + File.separator + miliseconds;

            // tạo excel
            createDeviceParameterCombinerExcel(data, fromDate, toDate, device, path, imageData, miliseconds);

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
     * Dowload Dữ liệu biểu đồ Combiner PV.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/chart/combiner/{customerId}/{deviceId}/{type}")
    public ResponseEntity<Resource> downloadChartCombinerPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @PathVariable final Integer type, @RequestParam final String userName) throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        // get url image
        User user = userMapper.getUserByUsername(userName);
        String pngImageURL = user.getImg();
        String encodingPrefix = "base64,";
        int contentStartIndex = pngImageURL.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData = org.apache.commons.codec.binary.Base64
            .decodeBase64(pngImageURL.substring(contentStartIndex));

        // time miliseconds
        long miliseconds = new Date().getTime();

        // path folder
        String path = this.folderName + File.separator + miliseconds;

        // gửi zip qua client
        String contentType = "application/zip";
        String headerValue = "attachment; filename=" + miliseconds + ".zip";

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        Device device = deviceService.getDeviceByDeviceId(condition);

        if (type == Constants.DATA.CHART_PV.DONG_DIEN) {

            int[] duration = new int[2];

            long from = DateUtils.toDate(fromDate + TIME_START, Constants.ES.DATETIME_FORMAT_YMDHMS)
                .getTime() / 1000;
            long to = DateUtils.toDate(toDate + TIME_END, Constants.ES.DATETIME_FORMAT_YMDHMS)
                .getTime() / 1000;

            if (from <= to) {
                for (int i = 0; i < Constants.DATA.times.length; i++) {
                    if (Long.compare(from, Constants.DATA.times[i]) > 0) {
                        duration[0] = i + 1;
                    }

                    if (Long.compare(to, Constants.DATA.times[i]) > 0) {
                        duration[1] = i + 1;
                    }
                }
            }

            List<DataCombiner1> data = new ArrayList<>();

            for (int i = duration[0]; i <= duration[1]; i++) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.COMBINER1));
                condition.put(FROM_DATE, fromDate.concat(TIME_START));
                condition.put(TO_DATE, toDate.concat(TIME_END));
                condition.put(SCHEMA, schema);
                List<DataCombiner1> weather1s = combinerPVService.getOperationCombinerPV(condition);
                data.addAll(weather1s);
            }

            // tạo excel
            createChartElectricCombinerExcel(data, fromDate, toDate, device, path, imageData, miliseconds);
        }

        if (type == Constants.DATA.CHART_PV.DIEN_AP) {

            int[] duration = new int[2];

            long from = DateUtils.toDate(fromDate + TIME_START, Constants.ES.DATETIME_FORMAT_YMDHMS)
                .getTime() / 1000;
            long to = DateUtils.toDate(toDate + TIME_END, Constants.ES.DATETIME_FORMAT_YMDHMS)
                .getTime() / 1000;

            if (from <= to) {
                for (int i = 0; i < Constants.DATA.times.length; i++) {
                    if (Long.compare(from, Constants.DATA.times[i]) > 0) {
                        duration[0] = i + 1;
                    }

                    if (Long.compare(to, Constants.DATA.times[i]) > 0) {
                        duration[1] = i + 1;
                    }
                }
            }

            List<DataCombiner1> data = new ArrayList<>();

            for (int i = duration[0]; i <= duration[1]; i++) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.COMBINER1));
                condition.put(FROM_DATE, fromDate.concat(TIME_START));
                condition.put(TO_DATE, toDate.concat(TIME_END));
                condition.put(SCHEMA, schema);
                List<DataCombiner1> weather1s = combinerPVService.getOperationCombinerPV(condition);
                data.addAll(weather1s);
            }

            // tạo excel
            createChartVoltageCombinerExcel(data, fromDate, toDate, device, path, imageData, miliseconds);
        }

        if (type == Constants.DATA.CHART_PV.NHIET_DO) {

            int[] duration = new int[2];

            long from = DateUtils.toDate(fromDate + TIME_START, Constants.ES.DATETIME_FORMAT_YMDHMS)
                .getTime() / 1000;
            long to = DateUtils.toDate(toDate + TIME_END, Constants.ES.DATETIME_FORMAT_YMDHMS)
                .getTime() / 1000;

            if (from <= to) {
                for (int i = 0; i < Constants.DATA.times.length; i++) {
                    if (Long.compare(from, Constants.DATA.times[i]) > 0) {
                        duration[0] = i + 1;
                    }

                    if (Long.compare(to, Constants.DATA.times[i]) > 0) {
                        duration[1] = i + 1;
                    }
                }
            }

            List<DataCombiner1> data = new ArrayList<>();

            for (int i = duration[0]; i <= duration[1]; i++) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.COMBINER1));
                condition.put(FROM_DATE, fromDate.concat(TIME_START));
                condition.put(TO_DATE, toDate.concat(TIME_END));
                condition.put(SCHEMA, schema);
                List<DataCombiner1> weather1s = combinerPVService.getOperationCombinerPV(condition);
                data.addAll(weather1s);
            }

            // tạo excel
            createChartTemperatureExcel(data, fromDate, toDate, device, path, imageData, miliseconds);
        }

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

    }

    /**
     * Tạo excel thông số thời tiết PV.
     *
     * @param data Danh sách dữ liệu thông số điện PV.
     * @throws Exception
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"static-access", "unused"})
    private void createDeviceParameterCombinerExcel(final List<DataCombiner1> data, final String fromDate,
        final String toDate, final Device device, final String path, final byte[] imageData, final long miliseconds)
        throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(data.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Thông số Combiner");
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
        for (int i = 0; i < 7; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 36; j++) {
                row.createCell(j);
            }
        }

        // set độ rộng của cột
        sheet.setColumnWidth(0, 1300);
        sheet.setColumnWidth(1, 5200);
        sheet.setColumnWidth(2, 5000);
        sheet.setColumnWidth(3, 5000);
        sheet.setColumnWidth(4, 5000);
        sheet.setColumnWidth(5, 5000);
        sheet.setColumnWidth(6, 5000);

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

        region = new CellRangeAddress(2, 2, 2, 3);
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

        region = new CellRangeAddress(3, 3, 2, 3);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(2);
        cell.setCellValue(device.getDeviceName() != null
            ? device.getDeviceName()
                .toUpperCase()
            : "-");
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
        cell.setCellValue("Nhiệt độ [°C]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("Bức xạ [W/m2]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("Độ ẩm [%]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("Tốc độ gió [m/s]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 6, 6);
        cell = sheet.getRow(5)
            .getCell(6);
        cell.setCellValue("Áp suất [atm]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataCombiner1 item = data.get(m);
            for (int i = index; i < index + 3; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 36; j++) {
                    Cell c = row.createCell(j, CellType.BLANK);
                    c.setCellStyle(cs);
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
            cell.setCellValue(item.getDCAh() != null ? String.valueOf(item.getDCAh()) : "-");

            // cột điện áp
            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getDCAh() != null ? String.valueOf(item.getDCAh()) : "-");

            // cột dòng điện
            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue(item.getDCAh() != null ? String.valueOf(item.getDCAh()) : "-");

            region = new CellRangeAddress(index, index, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue(item.getDCAh() != null ? String.valueOf(item.getDCAh()) : "-");

            region = new CellRangeAddress(index, index, 6, 6);
            cell = sheet.getRow(index)
                .getCell(6);
            cell.setCellValue(item.getDCAh() != null ? String.valueOf(item.getDCAh()) : "-");

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
     * Tạo excel biểu đồ điện áp.
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"unused", "static-access"})
    private void createChartVoltageCombinerExcel(final List<DataCombiner1> data, final String fromDate,
        final String toDate, final Device device, final String path, final byte[] imageData, final long miliseconds)
        throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(data.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Thông số điện áp");
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
        // set font style
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        // Tạo sheet content
        for (int i = 0; i < 7; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 36; j++) {
                Cell c = row.createCell(j, CellType.BLANK);
                c.setCellStyle(cs);
            }
        }

        sheet.setColumnWidth(0, 1800);
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
        cell.setCellValue("BÁO CÁO ĐIỆN ÁP COMBINER");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(0);
        cell.setCellValue("Mã thiết bị");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(2, 2, 2, 3);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(2);
        cell.setCellValue(device.getDeviceId());
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(3, 3, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(0);
        cell.setCellValue("Tên thiết bị");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(3, 3, 2, 3);
        cell = sheet.getRow(3)
            .getCell(2);
        cell.setCellValue(device != null && device.getDeviceName() != null
            ? device.getDeviceName()
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

        // bảng thông số nhiệt độ
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
        cell.setCellValue("Điện áp [V]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataCombiner1 item = data.get(m);
            for (int i = index; i < index + 3; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 36; j++) {
                    row.createCell(j);
                }
            }

            // Cột thứ tự
            region = new CellRangeAddress(index, index + 2, 0, 0);
            cell = sheet.getRow(index)
                .getCell(0);
            cell.setCellValue(m + 1);

            // Cột Thời gian
            region = new CellRangeAddress(index, index + 2, 1, 1);
            cell = sheet.getRow(index)
                .getCell(1);
            cell.setCellValue(sdf.format(sdf.parse(item.getSentDate())));

            region = new CellRangeAddress(index, index, 2, 2);
            cell = sheet.getRow(index)
                .getCell(2);
            cell.setCellValue(item.getVdcCombiner() != null ? String.valueOf(item.getVdcCombiner()) : "-");

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
            .getStackTrace()[1].getMethodName());
    }

    /**
     * Tạo excel thông tin dòng điện.
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"static-access", "unused"})
    private void createChartElectricCombinerExcel(final List<DataCombiner1> data, final String fromDate,
        final String toDate, final Device device, final String path, final byte[] imageData, final long miliseconds)
        throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(data.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Thông số dòng điện");
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

        // set font style
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        // Tạo sheet content
        for (int i = 0; i < 7; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 36; j++) {
                Cell c = row.createCell(j, CellType.BLANK);
                c.setCellStyle(cs);
            }
        }

        sheet.setColumnWidth(0, 1800);
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
        cell.setCellValue("BÁO CÁO DÒNG ĐIỆN COMBINER");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(0);
        cell.setCellValue("Mã thiết bị");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(2, 2, 2, 3);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(2);
        cell.setCellValue(device.getDeviceId());
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(3, 3, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(0);
        cell.setCellValue("Tên thiết bị");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(3, 3, 2, 3);
        cell = sheet.getRow(3)
            .getCell(2);
        cell.setCellValue(device.getDeviceName()
            .toUpperCase());
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

        // bảng thông số nhiệt độ
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
        cell.setCellValue("Dòng điện [A]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataCombiner1 item = data.get(m);
            for (int i = index; i < index + 3; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 36; j++) {
                    row.createCell(j);
                }
            }

            // Cột thứ tự
            region = new CellRangeAddress(index, index + 2, 0, 0);
            cell = sheet.getRow(index)
                .getCell(0);
            cell.setCellValue(m + 1);

            // Cột Thời gian
            region = new CellRangeAddress(index, index + 2, 1, 1);
            cell = sheet.getRow(index)
                .getCell(1);
            cell.setCellValue(sdf.format(sdf.parse(item.getSentDate())));

            // Cột Pha
            region = new CellRangeAddress(index, index, 2, 2);
            cell = sheet.getRow(index)
                .getCell(2);
            cell.setCellValue(item.getIdcCombiner() != null ? String.valueOf(item.getIdcCombiner()) : "-");

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
            .getStackTrace()[1].getMethodName());
    }

    /**
     * Tạo excel thông tin dòng điện.
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"static-access", "unused"})
    private void createChartTemperatureExcel(final List<DataCombiner1> data, final String fromDate, final String toDate,
        final Device device, final String path, final byte[] imageData, final long miliseconds) throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(data.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Thông số dòng điện");
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

        // set font style
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        // Tạo sheet content
        for (int i = 0; i < 7; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 36; j++) {
                Cell c = row.createCell(j, CellType.BLANK);
                c.setCellStyle(cs);
            }
        }

        sheet.setColumnWidth(0, 1800);
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
        cell.setCellValue("BÁO CÁO NHIỆT ĐỘ COMBINER");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(0);
        cell.setCellValue("Mã thiết bị");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(2, 2, 2, 3);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(2);
        cell.setCellValue(device.getDeviceId());
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(3, 3, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(0);
        cell.setCellValue("Tên thiết bị");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(3, 3, 2, 3);
        cell = sheet.getRow(3)
            .getCell(2);
        cell.setCellValue(device.getDeviceName()
            .toUpperCase());
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

        // bảng thông số nhiệt độ
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
        cell.setCellValue("Cabinet Temperature [°C]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataCombiner1 item = data.get(m);
            for (int i = index; i < index + 3; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 36; j++) {
                    row.createCell(j);
                }
            }

            // Cột thứ tự
            region = new CellRangeAddress(index, index + 2, 0, 0);
            cell = sheet.getRow(index)
                .getCell(0);
            cell.setCellValue(m + 1);

            // Cột Thời gian
            region = new CellRangeAddress(index, index + 2, 1, 1);
            cell = sheet.getRow(index)
                .getCell(1);
            cell.setCellValue(sdf.format(sdf.parse(item.getSentDate())));

            // Cột Pha
            region = new CellRangeAddress(index, index, 2, 2);
            cell = sheet.getRow(index)
                .getCell(2);
            cell.setCellValue(item.getT() != null ? String.valueOf(item.getT()) : "-");

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
            .getStackTrace()[1].getMethodName());
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
        cs.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);

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