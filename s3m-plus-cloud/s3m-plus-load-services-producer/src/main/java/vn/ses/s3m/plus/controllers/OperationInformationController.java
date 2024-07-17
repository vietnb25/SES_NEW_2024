package vn.ses.s3m.plus.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.lang.StringUtils;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zeroturnaround.zip.ZipUtil;

import lombok.extern.slf4j.Slf4j;
import vn.ses.s3m.plus.common.CommonUtils;
import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.common.DateUtils;
import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dao.UserMapper;
import vn.ses.s3m.plus.dto.DataLoadFrame1;
import vn.ses.s3m.plus.dto.DataLoadFrame2;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.dto.Warning;
import vn.ses.s3m.plus.form.HarmonicForm;
import vn.ses.s3m.plus.form.UpdateWarningForm;
import vn.ses.s3m.plus.response.DataChartResponse;
import vn.ses.s3m.plus.response.DataHarmonicPeriod;
import vn.ses.s3m.plus.response.DataPQSResponse;
import vn.ses.s3m.plus.response.DataPowerIResponse;
import vn.ses.s3m.plus.response.DataPowerUResponse;
import vn.ses.s3m.plus.response.DeviceResponse;
import vn.ses.s3m.plus.response.OperationInformationResponse;
import vn.ses.s3m.plus.response.PowerQualityResponse;
import vn.ses.s3m.plus.service.DataLoadFrame1Service;
import vn.ses.s3m.plus.service.DeviceService;
import vn.ses.s3m.plus.service.OperationInformationService;
import vn.ses.s3m.plus.service.SettingService;
import vn.ses.s3m.plus.service.WarningService;

/**
 * Controller Xử lý thông tin vận hành
 *
 * @author Arius Vietnam JSC
 * @since 2022-11-28
 */
@RestController
@RequestMapping ("/operation")
@Slf4j
public class OperationInformationController {

    @Autowired
    private OperationInformationService operationInfoService;

    @Autowired
    private WarningService warningService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DataLoadFrame1Service dataLoadFrame1Service;

    @Autowired
    private SettingService settingService;

    @Autowired
    private UserMapper userMapper;

    @Value ("${load.producer.export-folder}")
    private String folderName;

    // Các tham số xét giá trị truy vấn
    private static final String DEVICE_ID = "deviceId";

    private static final String FROM_DATE = "fromDate";

    private static final String WARNING_ID = "warningId";

    private static final String WARNING_TYPE = "warningType";

    private static final String PROJECT_ID = "projectId";

    private static final String TO_DATE = "toDate";

    private static final String TIME_START = " 00:00:00";

    private static final String TIME_END = " 23:59:59";

    private static final String SORT = "sort";

    private static final String SORT_ASC = "ASC";

    private static final String SORT_DESC = "DESC";

    // Các giá trị xét dữ liệu biểu đồ

    private static final Integer PQS_VIEW_DAY = 1;

    private static final Integer PQS_VIEW_MONTH = 2;

    private static final Integer PQS_VIEW_YEAR = 3;

    private static final Integer PAGE_SIZE = 50;

    private static final String SCHEMA = "schema";

    /**
     * Lấy thông số điện và nhiệt độ tức thời
     *
     * @param deviceId Mã thiết bị
     * @return Thông số điện và nhiệt độ tức thời
     */
    @GetMapping ("/instant/{customerId}/{deviceId}")
    public ResponseEntity<OperationInformationResponse> getInstantOperationInformation(
        @PathVariable final Integer customerId, @PathVariable final Long deviceId) {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        condition.put(SCHEMA, Schema.getSchemas(customerId));

        DataLoadFrame1 dataInfo = operationInfoService.getInstantOperationInformation(condition);

        if (dataInfo != null) {

            OperationInformationResponse data = new OperationInformationResponse(dataInfo);

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<OperationInformationResponse>(data, HttpStatus.OK);
        } else {

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<OperationInformationResponse>(HttpStatus.OK);
        }
    }

    /**
     * Lấy thông số chất lượng điện năng tức thời
     *
     * @param deviceId Mã thiết bị
     * @return Thông số điện và nhiệt độ tức thời
     */
    @GetMapping ("/power-quality/instant/{customerId}/{deviceId}")
    public ResponseEntity<PowerQualityResponse> getInstantPowerQuality(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId) {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        condition.put(SCHEMA, Schema.getSchemas(customerId));

        DataLoadFrame2 dataInfo = operationInfoService.getInstantPowerQuality(condition);

        if (dataInfo != null) {

            PowerQualityResponse data = new PowerQualityResponse(dataInfo);

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<PowerQualityResponse>(data, HttpStatus.OK);
        } else {

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<PowerQualityResponse>(HttpStatus.OK);
        }
    }

    /**
     * Lấy danh sách thông số chất lượng điện năng
     *
     * @param deviceId Mã thiết bị
     * @return Danh sách thông số chất lượng điện năng
     */
    @GetMapping ("/power-quality/{customerId}/{deviceId}")
    public ResponseEntity<?> getPowerQualities(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @RequestParam final Integer page) {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        List<PowerQualityResponse> data = new ArrayList<>();

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

        List<DataLoadFrame2> dataInfo = new ArrayList<>();

        Map<String, Object> condition = new HashMap<>();
        int pageSize = PAGE_SIZE;
        int totalData = 0;
        int pageTable = page;
        String soft = SORT_DESC;
        for (int i = duration[1]; i >= duration[0]; i--) {
            String schema = Schema.getSchemas(customerId)
                + Constants.DATA.DATA_TABLES.get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.FRAME2));
            condition.put(SCHEMA, schema);
            condition.put(DEVICE_ID, deviceId);
            if (fromDate != null && toDate != null) {
                condition.put(FROM_DATE, fromDate.concat(TIME_START));
                condition.put(TO_DATE, toDate.concat(TIME_END));
            }
            condition.put(SORT, soft);

            condition.put("offset", (pageTable - 1) * PAGE_SIZE);
            condition.put("pageSize", pageSize);

            totalData = totalData + operationInfoService.countDataFrame2(condition);

            List<DataLoadFrame2> frame2s = operationInfoService.getPowerQualities(condition);
            if (frame2s.size() <= PAGE_SIZE) {
                pageSize = PAGE_SIZE - frame2s.size();
                pageTable = 1;
                soft = SORT_ASC;
            } else {
                pageSize = PAGE_SIZE;
                pageTable = page;
                soft = SORT_DESC;
            }
            dataInfo.addAll(frame2s);
        }

        double totalPage = Math.ceil((double) totalData / pageSize);
        // object to response to client
        Map<String, Object> dataResponse = new HashMap<>();

        dataResponse.put("totalPage", totalPage);
        dataResponse.put("currentPage", page);
        dataResponse.put("totalData", totalData);

        if (dataInfo.size() > 0) {
            for (DataLoadFrame2 frame2 : dataInfo) {
                PowerQualityResponse pqRes = new PowerQualityResponse(frame2);
                data.add(pqRes);
            }

            dataResponse.put("data", data);

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<>(dataResponse, HttpStatus.OK);
        } else {

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * Lấy danh sách thông số điện và nhiệt độ
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu
     * @param toDate Ngày kết thức truy vấn dữ liệu
     * @return Danh sách hông số điện và nhiệt độ
     */
    @GetMapping ("/{customerId}/{deviceId}/{page}")
    public ResponseEntity<?> getOperationInformation(@PathVariable Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @PathVariable final Integer page) {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        List<OperationInformationResponse> data = new ArrayList<>();

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

        List<DataLoadFrame1> dataInfo = new ArrayList<>();

        Map<String, Object> condition = new HashMap<>();
        int pageSize = PAGE_SIZE;
        int totalData = 0;
        int pageTable = page;
        String soft = SORT_DESC;
        for (int i = duration[1]; i >= duration[0]; i--) {
            String schema = Schema.getSchemas(customerId)
                + Constants.DATA.DATA_TABLES.get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.FRAME1));
            condition.put(DEVICE_ID, deviceId);
            if (fromDate != null && toDate != null) {
                condition.put(FROM_DATE, fromDate.concat(TIME_START));
                condition.put(TO_DATE, toDate.concat(TIME_END));
            }
            condition.put(SCHEMA, schema);
            condition.put(SORT, soft);

            condition.put("start", (pageTable - 1) * PAGE_SIZE);
            condition.put("end", pageSize);

            List<DataLoadFrame1> frame1s = operationInfoService.getOperationInformation(condition);

            totalData = totalData + operationInfoService.countTotalData(condition);

            if (frame1s.size() <= PAGE_SIZE) {
                pageSize = PAGE_SIZE - frame1s.size();
                pageTable = 1;
                soft = SORT_ASC;
            } else {
                pageSize = PAGE_SIZE;
                pageTable = page;
                soft = SORT_DESC;
            }

            dataInfo.addAll(frame1s);
        }

        double totalPage = Math.ceil((double) totalData / PAGE_SIZE);

        Map<String, Object> mapData = new HashMap<>();
        mapData.put("totalPage", totalPage);
        mapData.put("currentPage", page);

        if (dataInfo.size() > 0) {

            dataInfo.forEach(x -> {
                OperationInformationResponse oRes = new OperationInformationResponse(x);
                data.add(oRes);
            });

            mapData.put("data", data);

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<>(mapData, HttpStatus.OK);
        } else {

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<>(HttpStatus.OK);
        }

    }

    /**
     * Dowload thông số điện.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/electrical-param/{customerId}/{deviceId}")
    public ResponseEntity<Resource> downloadElectricalParam(@PathVariable final Integer customerId,
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

        List<DataLoadFrame1> data = new ArrayList<>();

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

        Map<String, Object> condition = new HashMap<>();
        int totalData = 0;
        String soft = SORT_DESC;
        for (int i = duration[1]; i >= duration[0]; i--) {
            String schema = Schema.getSchemas(customerId)
                + Constants.DATA.DATA_TABLES.get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.FRAME1));
            condition.put(DEVICE_ID, deviceId);
            if (fromDate != null && toDate != null) {
                condition.put(FROM_DATE, fromDate.concat(TIME_START));
                condition.put(TO_DATE, toDate.concat(TIME_END));
            }
            condition.put(SCHEMA, schema);
            condition.put(SORT, soft);

            List<DataLoadFrame1> frame1s = operationInfoService.getOperationInformation(condition);

            totalData = totalData + operationInfoService.countTotalData(condition);

            if (i == 1) {
                soft = SORT_DESC;
            } else {
                soft = SORT_ASC;
            }

            data.addAll(frame1s);
        }

        if (data.size() > 0) {
            // time miliseconds
            long miliseconds = new Date().getTime();

            Device device = deviceService.getDeviceByDeviceId(condition);

            // path folder
            String path = this.folderName + File.separator + miliseconds;

            // tạo excel
            createOperationExcel(data, fromDate, toDate, device, path, imageData, miliseconds);

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

            log.info("END");

            return new ResponseEntity<Resource>(HttpStatus.OK);
        }

    }

    /**
     * Dowload thông số nhiệt độ.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/temperature/{customerId}/{deviceId}")
    public ResponseEntity<Resource> downloadTemperature(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @RequestParam String userName) throws Exception {

        // get url image
        User user = userMapper.getUserByUsername(userName);
        String pngImageURL = user.getImg();
        String encodingPrefix = "base64,";
        int contentStartIndex = pngImageURL.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData = org.apache.commons.codec.binary.Base64
            .decodeBase64(pngImageURL.substring(contentStartIndex));

        List<DataLoadFrame1> data = new ArrayList<>();

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

        Map<String, Object> condition = new HashMap<>();
        int totalData = 0;
        String soft = SORT_DESC;
        for (int i = duration[1]; i >= duration[0]; i--) {
            String schema = Schema.getSchemas(customerId)
                + Constants.DATA.DATA_TABLES.get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.FRAME1));
            condition.put(DEVICE_ID, deviceId);
            if (fromDate != null && toDate != null) {
                condition.put(FROM_DATE, fromDate.concat(TIME_START));
                condition.put(TO_DATE, toDate.concat(TIME_END));
            }
            condition.put(SCHEMA, schema);
            condition.put(SORT, soft);

            List<DataLoadFrame1> frame1s = operationInfoService.getOperationInformation(condition);

            totalData = totalData + operationInfoService.countTotalData(condition);

            if (i == 1) {
                soft = SORT_DESC;
            } else {
                soft = SORT_ASC;
            }

            data.addAll(frame1s);
        }

        if (data.size() > 0) {
            // time miliseconds
            long miliseconds = new Date().getTime();

            Device device = deviceService.getDeviceByDeviceId(condition);

            // path folder
            String path = this.folderName + File.separator + miliseconds;

            // tạo excel
            createTemperatureExcel(data, fromDate, toDate, device, path, imageData, miliseconds);

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
     * Dowload thông số chất lượng điện năng.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/power-quality/{customerId}/{deviceId}")
    public ResponseEntity<Resource> downloadPowerQuality(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @RequestParam final String userName) throws Exception {

        // get url image
        User user = userMapper.getUserByUsername(userName);
        String pngImageURL = user.getImg();
        String encodingPrefix = "base64,";
        int contentStartIndex = pngImageURL.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData = org.apache.commons.codec.binary.Base64
            .decodeBase64(pngImageURL.substring(contentStartIndex));

        List<DataLoadFrame2> data = new ArrayList<>();

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

        Map<String, Object> condition = new HashMap<>();
        int totalData = 0;
        String soft = SORT_DESC;
        for (int i = duration[1]; i >= duration[0]; i--) {
            String schema = Schema.getSchemas(customerId)
                + Constants.DATA.DATA_TABLES.get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.FRAME2));
            condition.put(DEVICE_ID, deviceId);
            if (fromDate != null && toDate != null) {
                condition.put(FROM_DATE, fromDate.concat(TIME_START));
                condition.put(TO_DATE, toDate.concat(TIME_END));
            }
            condition.put(SCHEMA, schema);
            condition.put(SORT, soft);

            List<DataLoadFrame2> frame2s = operationInfoService.getPowerQualities(condition);

            totalData = totalData + operationInfoService.countTotalData(condition);

            if (i == 1) {
                soft = SORT_DESC;
            } else {
                soft = SORT_ASC;
            }

            data.addAll(frame2s);
        }

        if (data.size() > 0) {

            // time miliseconds
            long miliseconds = new Date().getTime();

            Device device = deviceService.getDeviceByDeviceId(condition);

            // path folder
            String path = this.folderName + File.separator + miliseconds;

            // tạo excel
            createPowerQualityExcel(data, fromDate, toDate, device, path, imageData, miliseconds);

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
     * Lấy danh sách cảnh báo vận hành
     *
     * @param deviceId Mã thiết bị
     * @param warningType Kiểu cảnh báo
     * @param fromDate Ngày bắt đầu tìm kiếm
     * @param toDate Ngày kết thúc tìm kiếm
     * @return Danh sách cảnh báo
     */
    @GetMapping ("/operating-warning/{customerId}/{deviceId}")
    public ResponseEntity<List<Warning>> getWarningOperation(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String warningType, @RequestParam final String fromDate,
        @RequestParam final String toDate) {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        if (warningType != null) {
            condition.put(WARNING_TYPE, warningType);
        }
        condition.put(SCHEMA, Schema.getSchemas(customerId));
        condition.put(FROM_DATE, fromDate + TIME_START);
        condition.put(TO_DATE, toDate + TIME_END);

        List<Warning> warnings = warningService.getWarningByDevice(condition);

        if (warnings.size() > 0) {

            List<Warning> countWarning = warningService.countWarnings(condition);

            for (Warning warning : warnings) {
                for (Warning warning2 : countWarning) {
                    warning.setTotalDevice(warning2.getTotalDevice());
                }
            }

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<List<Warning>>(warnings, HttpStatus.OK);
        } else {

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<List<Warning>>(HttpStatus.OK);
        }
    }

    /**
     * Hiển thị chi tiết các bản tin khi bị cảnh báo theo từng warning_type.
     *
     * @param warningType Kiểu cảnh báo.
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @param deviceId ID thiết bị.
     * @param page Page muốn hiển thị dữ liệu.
     * @return Danh sách chi tiết của cảnh báo theo warning type
     */
    @GetMapping ("/operating-warning/detail/{customerId}")
    public ResponseEntity<?> showDataWarning(@PathVariable final Integer customerId,
        @RequestParam ("warningType") final String warningType, @RequestParam ("fromDate") final String fromDate,
        @RequestParam ("toDate") final String toDate, @RequestParam ("deviceId") final String deviceId,
        @RequestParam ("page") final Integer page) {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        Map<String, Object> condition = new HashMap<>();

        List<DataLoadFrame1> data = new ArrayList<>();

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

        String settingValue = null;
        for (int i = duration[1]; i >= duration[0]; i--) {
            String schema = Schema.getSchemas(customerId)
                + Constants.DATA.DATA_TABLES.get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.FRAME1));
            condition.put(WARNING_TYPE, warningType);
            condition.put(DEVICE_ID, deviceId);
            condition.put(FROM_DATE, fromDate);
            condition.put(TO_DATE, toDate);
            condition.put(SCHEMA, schema);
            condition.put("warningType", warningType);

            settingValue = settingService.getSettingValue(condition);

            List<DataLoadFrame1> dataWarning = dataLoadFrame1Service.getDataLoadWarning(condition);
            data.addAll(dataWarning);
        }

        if (data.size() > 0) {

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            Map<String, Object> mapData = new HashMap<>();

            mapData.put("dataWarning", data);
            mapData.put("settingValue", settingValue);

            return new ResponseEntity<>(mapData, HttpStatus.OK);
        } else {

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<>(HttpStatus.OK);
        }

    }

    /**
     * Lấy thông tin chi tiết cảnh báo.
     *
     * @param warningId Id của cảnh báo.
     * @return Thông tin chi tiết của cảnh báo.
     */
    @GetMapping ("/operating-warning/update/{customerId}/{warningId}")
    public ResponseEntity<?> updateOperatingWarning(@PathVariable final Integer customerId,
        @PathVariable ("warningId") final Integer warningId) {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        Map<String, Object> condition = new HashMap<>();
        condition.put(WARNING_ID, warningId);
        condition.put(SCHEMA, Schema.getSchemas(customerId));

        Warning warning = warningService.getDetailWarningCache(condition);
        if (warning != null) {

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<>(warning, HttpStatus.OK);
        } else {

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * Cập nhật thông tin chi tiết cảnh báo.
     *
     * @param form Data cần cập nhật.
     * @return Trạng thái cập nhật cảnh báo.
     */
    @PostMapping ("/operating-warning/update/{customerId}/{warningId}")
    public ResponseEntity<?> updateOperatingWarning(@RequestBody final UpdateWarningForm form,
        @PathVariable final Integer customerId) {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        Map<String, Object> data = new HashMap<>();
        data.put("status", form.getStatus());
        data.put("username", form.getUsername());
        data.put("id", form.getId());

        data.put(SCHEMA, Schema.getSchemas(customerId));
        data.put("description", form.getDescription());

        boolean isUpdate = warningService.updateWarningCache(data);

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " END");

        return new ResponseEntity<>(null, isUpdate ? HttpStatus.OK : HttpStatus.OK);
    }

    /**
     * Download cảnh báo vận hành của thiết bị.
     *
     * @param warningType Loại cảnh báo.
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian mới nhất.
     * @param deviceId Id thiết bị.
     * @return Data download.
     * @throws Exception
     */
    @GetMapping ("/operating-warning/downloadWarning/{customerId}")
    public ResponseEntity<?> downloadWarning(@PathVariable Integer customerId,
        @RequestParam ("warningType") final String warningType, @RequestParam ("fromDate") final String fromDate,
        @RequestParam ("toDate") final String toDate, @RequestParam ("deviceId") final String deviceId,
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

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        if (warningType != null) {
            condition.put(WARNING_TYPE, warningType);
        }
        condition.put(SCHEMA, Schema.getSchemas(customerId));
        condition.put(FROM_DATE, fromDate + TIME_START);
        condition.put(TO_DATE, toDate + TIME_END);

        // device
        Device device = deviceService.getDeviceByDeviceId(condition);

        // danh sách cảnh báo thiết bị
        List<Warning> warnings = warningService.getWarningByDevice(condition);

        List<Warning> countWarning = warningService.countWarnings(condition);

        for (Warning warning : warnings) {
            for (Warning warning2 : countWarning) {
                warning.setTotalDevice(warning2.getTotalDevice());
            }
        }
        // time miliseconds
        long miliseconds = new Date().getTime();

        // path folder
        String path = this.folderName + File.separator + miliseconds;

        // tạo excel
        if (warnings.size() > 0) {
            createOperatingWarningExcel(warnings, fromDate, toDate, device, path, imageData, miliseconds);
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
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    // CHECKSTYLE:OFF
    @SuppressWarnings ({"unused", "static-access"})
    private void createOperatingWarningExcel(final List<Warning> dataWarning, final String fromDate,
        final String toDate, final Device device, final String path, final byte[] imageData, long miliseconds)
        throws Exception {

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(dataWarning.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Cảnh báo vận hành");
        Row row;
        Cell cell;

        // add image
        int pictureIdx = wb.addPicture(imageData, wb.PICTURE_TYPE_PNG);
        SXSSFDrawing drawingImg = sheet.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(7);
        anchorImg.setCol2(8);
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

        sheet.setColumnWidth(0, 1800);
        sheet.setColumnWidth(1, 5200);
        sheet.setColumnWidth(2, 5400);
        sheet.setColumnWidth(3, 5400);
        sheet.setColumnWidth(4, 5200);
        sheet.setColumnWidth(5, 5200);
        sheet.setColumnWidth(6, 5200);
        sheet.setColumnWidth(7, 5200);

        // set độ rộng của hàng
        Row row1 = sheet.getRow(1);
        row1.setHeight((short) -500);
        Row row2 = sheet.getRow(2);
        row2.setHeight((short) -500);
        Row row3 = sheet.getRow(3);
        row3.setHeight((short) -500);

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 7);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(0)
            .getCell(0);
        cell.setCellValue("CẢNH BÁO VẬN HÀNH");
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

        region = new CellRangeAddress(2, 2, 5, 6);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(5);
        cell.setCellValue(fromDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        region = new CellRangeAddress(3, 3, 5, 6);
        sheet.addMergedRegion(region);
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
        cell.setCellValue("Loại cảnh báo");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 2, 2);
        cell = sheet.getRow(5)
            .getCell(2);
        cell.setCellValue("Bắt đầu");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("Mới nhất");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("Số lần cảnh báo");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("Vị trí");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 6, 6);
        cell = sheet.getRow(5)
            .getCell(6);
        cell.setCellValue("Trạng thái");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 7, 7);
        cell = sheet.getRow(5)
            .getCell(7);
        cell.setCellValue("Người dùng");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        // set font style
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        int index = 6;
        for (int m = 0; m < dataWarning.size(); m++) {
            Warning item = dataWarning.get(m);
            for (int i = index; i < index + 1; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 36; j++) {
                    Cell c = row.createCell(j, CellType.BLANK);
                    c.setCellStyle(cs);
                }
            }

            // Cột thứ tự
            region = new CellRangeAddress(index, index, 0, 0);
            cell = sheet.getRow(index)
                .getCell(0);
            cell.setCellValue(m + 1);

            // Cột loại cảnh báo
            region = new CellRangeAddress(index, index, 1, 1);
            cell = sheet.getRow(index)
                .getCell(1);
            cell.setCellValue(
                getWarningName(item.getWarningType()) != null ? getWarningName(item.getWarningType()) : "-");

            region = new CellRangeAddress(index, index, 2, 2);
            cell = sheet.getRow(index)
                .getCell(2);
            cell.setCellValue(sdf.format(sdf.parse(item.getFromDate())));

            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(sdf.format(sdf.parse(item.getToDate())));

            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue(item.getTotalDevice());

            region = new CellRangeAddress(index, index, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue("-");

            region = new CellRangeAddress(index, index, 6, 6);
            cell = sheet.getRow(index)
                .getCell(6);
            cell.setCellValue(getStatusWarning(item.getHandleFlag()));

            region = new CellRangeAddress(index, index, 7, 7);
            cell = sheet.getRow(index)
                .getCell(7);
            cell.setCellValue(item.getStaffName() != null ? item.getStaffName() : "-");

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

    }
    // CHECKSTYLE:ON

    private String getWarningName(final Integer warningType) {

        String warningName = "";
        switch (warningType) {
            case Constants.WarningType.NGUONG_AP_CAO:
                warningName = "Ngưỡng áp cao";
                break;
            case Constants.WarningType.NGUONG_AP_THAP:
                warningName = "Ngưỡng áp thấp";
                break;
            case Constants.WarningType.NHIET_DO_TIEP_XUC:
                warningName = "Nhiệt độ tiếp xúc";
                break;
            case Constants.WarningType.COS_THAP_TONG:
                warningName = "Cos thấp tổng";
                break;
            case Constants.WarningType.QUA_TAI:
                warningName = "Quá tải";
                break;
            case Constants.WarningType.TAN_SO_THAP:
                warningName = "Tần số thấp";
                break;
            case Constants.WarningType.TAN_SO_CAO:
                warningName = "Tần số cao";
                break;
            case Constants.WarningType.MAT_NGUON_PHA:
                warningName = "Mất nguồn pha";
                break;
            case Constants.WarningType.LECH_PHA:
                warningName = "Lệch pha";
                break;
            case Constants.WarningType.NGUOC_PHA:
                warningName = "Ngược pha";
                break;
            case Constants.WarningType.NGUONG_HAI_BAC_N:
                warningName = "Ngưỡng hải bậc N";
                break;
            case Constants.WarningType.NGUONG_TONG_HAI:
                warningName = "Ngưỡng tổng hải";
                break;
            case Constants.WarningType.DONG_TRUNG_TINH:
                warningName = "Dòng trung tính";
                break;
            case Constants.WarningType.DONG_TIEP_DIA:
                warningName = "Dòng tiếp địa";
                break;
            case Constants.WarningType.CANH_BAO_1:
                warningName = "Cảnh báo 1";
                break;
            case Constants.WarningType.CANH_BAO_2:
                warningName = "Cảnh báo 2";
                break;
            case Constants.WarningType.LECH_AP_PHA:
                warningName = "Lệch áp pha";
                break;
            default:
                break;
        }

        return warningName;
    }

    private String getStatusWarning(final Integer warningStatus) {
        String statusName = "";

        switch (warningStatus) {
            case Constants.WarningType.STATUS_WARNING:
                statusName = "Mới";
                break;
            case Constants.WarningType.STATUS_WARNING_1:
                statusName = "Đã xác nhận";
                break;
            case Constants.WarningType.STATUS_WARNING_2:
                statusName = "Đang sửa";
                break;
            case Constants.WarningType.STATUS_WARNING_3:
                statusName = "Đã sửa";
                break;
            case Constants.WarningType.STATUS_WARNING_4:
                statusName = "Đã hủy";
                break;
            default:
                break;
        }
        return statusName;
    }

    /**
     * Download danh sách bản tin bị cảnh báo.
     *
     * @param warningType Kiểu cảnh báo.
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @param deviceId ID thiết bị.
     * @return Data download.
     * @throws Exception
     */
    @GetMapping ("/operating-warning/download/{customerId}")
    public ResponseEntity<?> downloadOperatingWarning(@PathVariable Integer customerId,
        @RequestParam ("warningType") final String warningType, @RequestParam ("fromDate") final String fromDate,
        @RequestParam ("toDate") final String toDate, @RequestParam ("deviceId") final String deviceId,
        @RequestParam ("userName") final String userName) throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        // get url image
        User user = userMapper.getUserByUsername(userName);
        String pngImageURL = user.getImg();
        String encodingPrefix = "base64,";
        int contentStartIndex = pngImageURL.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData = org.apache.commons.codec.binary.Base64
            .decodeBase64(pngImageURL.substring(contentStartIndex));

        // SQL query condition
        int[] duration = new int[2];
        duration = CommonUtils.calculateDataIndex(fromDate, toDate);
        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        condition.put(FROM_DATE, fromDate);
        condition.put(TO_DATE, toDate);
        // device
        Device device = deviceService.getDeviceByDeviceId(condition);

        // danh sách bản tin bị cảnh báo theo warning type
        List<DataLoadFrame1> dataWarning = new ArrayList<>();
        for (int index = duration[1]; index >= duration[0]; index--) {
            condition.put(SCHEMA, Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                .get(new MultiKey(Constants.DATA.tables[index], Constants.DATA.MESSAGE.FRAME1)));
            List<DataLoadFrame1> frame1s = dataLoadFrame1Service.getDataLoadWarning(condition);
            if (frame1s != null && frame1s.size() > 0) {
                for (DataLoadFrame1 dataItem : frame1s) {
                    dataWarning.add(dataItem);
                }
            }
            frame1s.clear();
        }

        // time miliseconds
        long miliseconds = new Date().getTime();

        // path folder
        String path = this.folderName + File.separator + miliseconds;

        // tạo excel
        if (dataWarning.size() > 0) {
            if (Integer.parseInt(warningType) != Constants.WarningType.NHIET_DO_TIEP_XUC) {
                createOperationExcel(dataWarning, fromDate, toDate, device, path, imageData, miliseconds);
            } else {
                createTemperatureExcel(dataWarning, fromDate, toDate, device, path, imageData, miliseconds);
            }

        }

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
    }

    /**
     * Tạo excel thông tin vận hành điện.
     *
     * @param data Danh sách thông tin vận hành.
     * @throws Exception
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"static-access", "unused"})
    private void createOperationExcel(final List<DataLoadFrame1> data, final String fromDate, final String toDate,
        final Device device, final String path, final byte[] imageData, final Long miliseconds) throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(data.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Thông số điện");
        Row row;
        Cell cell;

        // add image
        int pictureIdx = wb.addPicture(imageData, wb.PICTURE_TYPE_PNG);
        SXSSFDrawing drawingImg = sheet.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(16);
        anchorImg.setCol2(17);
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
        sheet.setColumnWidth(4, 4000);
        sheet.setColumnWidth(16, 4000);

        // set độ rộng của hàng
        Row row1 = sheet.getRow(1);
        row1.setHeight((short) -500);
        Row row2 = sheet.getRow(2);
        row2.setHeight((short) -500);
        Row row3 = sheet.getRow(3);
        row3.setHeight((short) -500);

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 16);
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

        region = new CellRangeAddress(2, 2, 2, 8);
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

        region = new CellRangeAddress(3, 3, 2, 8);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(2);
        cell.setCellValue(device.getDeviceName() != null
            ? device.getDeviceName()
                .toUpperCase()
            : "-");
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(2, 3, 9, 10);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(9);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 11, 15);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(11);
        cell.setCellValue(fromDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        region = new CellRangeAddress(3, 3, 11, 15);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(11);
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
        cell.setCellValue("Điện áp [V]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("Dòng điện [A]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("%");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 6, 6);
        cell = sheet.getRow(5)
            .getCell(6);
        cell.setCellValue("P [kW]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 7, 7);
        cell = sheet.getRow(5)
            .getCell(7);
        cell.setCellValue("Q [kWar]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 8, 8);
        cell = sheet.getRow(5)
            .getCell(8);
        cell.setCellValue("S [kVa]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 9, 9);
        cell = sheet.getRow(5)
            .getCell(9);
        cell.setCellValue("PF");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 10, 10);
        cell = sheet.getRow(5)
            .getCell(10);
        cell.setCellValue("THD U [%]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 11, 11);
        cell = sheet.getRow(5)
            .getCell(11);
        cell.setCellValue("THD I [%]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 12, 12);
        cell = sheet.getRow(5)
            .getCell(12);
        cell.setCellValue("Phase U");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 13, 13);
        cell = sheet.getRow(5)
            .getCell(13);
        cell.setCellValue("F [Hz]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 14, 14);
        cell = sheet.getRow(5)
            .getCell(14);
        cell.setCellValue("VU [%]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 15, 15);
        cell = sheet.getRow(5)
            .getCell(15);
        cell.setCellValue("IU [%]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 16, 16);
        cell = sheet.getRow(5)
            .getCell(16);
        cell.setCellValue("Active Energy [kWh]");
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
            DataLoadFrame1 item = data.get(m);
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
            cell.setCellValue(item.getUan() != null ? String.valueOf(item.getUan()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 3, 3);
            cell = sheet.getRow(index + 1)
                .getCell(3);
            cell.setCellValue(item.getUbn() != null ? String.valueOf(item.getUbn()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 3, 3);
            cell = sheet.getRow(index + 2)
                .getCell(3);
            cell.setCellValue(item.getUcn() != null ? String.valueOf(item.getUcn()) : "-");

            // cột dòng điện
            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue(item.getIa() != null ? String.valueOf(item.getIa()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 4, 4);
            cell = sheet.getRow(index + 1)
                .getCell(4);
            cell.setCellValue(item.getIb() != null ? String.valueOf(item.getIb()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 4, 4);
            cell = sheet.getRow(index + 2)
                .getCell(4);
            cell.setCellValue(item.getIc() != null ? String.valueOf(item.getIc()) : "-");

            // cột %
            region = new CellRangeAddress(index, index, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 1, index + 1, 5, 5);
            cell = sheet.getRow(index + 1)
                .getCell(5);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 2, index + 2, 5, 5);
            cell = sheet.getRow(index + 2)
                .getCell(5);
            cell.setCellValue("-");

            // cột P
            region = new CellRangeAddress(index, index, 6, 6);
            cell = sheet.getRow(index)
                .getCell(6);
            cell.setCellValue(item.getPa() != null ? String.valueOf(Math.round(item.getPa() / 1000)) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 6, 6);
            cell = sheet.getRow(index + 1)
                .getCell(6);
            cell.setCellValue(item.getPb() != null ? String.valueOf(Math.round(item.getPb() / 1000)) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 6, 6);
            cell = sheet.getRow(index + 2)
                .getCell(6);
            cell.setCellValue(item.getPc() != null ? String.valueOf(Math.round(item.getPc() / 1000)) : "-");

            // cột Q
            region = new CellRangeAddress(index, index, 7, 7);
            cell = sheet.getRow(index)
                .getCell(7);
            cell.setCellValue(item.getQa() != null ? String.valueOf(Math.round(item.getQa() / 1000)) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 7, 7);
            cell = sheet.getRow(index + 1)
                .getCell(7);
            cell.setCellValue(item.getQb() != null ? String.valueOf(Math.round(item.getQb() / 1000)) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 7, 7);
            cell = sheet.getRow(index + 2)
                .getCell(7);
            cell.setCellValue(item.getQc() != null ? String.valueOf(Math.round(item.getQc() / 1000)) : "-");

            // cột S
            region = new CellRangeAddress(index, index, 8, 8);
            cell = sheet.getRow(index)
                .getCell(8);
            cell.setCellValue(item.getSa() != null ? String.valueOf(Math.round(item.getSa() / 1000)) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 8, 8);
            cell = sheet.getRow(index + 1)
                .getCell(8);
            cell.setCellValue(item.getSb() != null ? String.valueOf(Math.round(item.getSb() / 1000)) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 8, 8);
            cell = sheet.getRow(index + 2)
                .getCell(8);
            cell.setCellValue(item.getSc() != null ? String.valueOf(Math.round(item.getSc() / 1000)) : "-");

            // cột PF
            region = new CellRangeAddress(index, index, 9, 9);
            cell = sheet.getRow(index)
                .getCell(9);
            cell.setCellValue(item.getPfa() != null ? String.valueOf(item.getPfa()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 9, 9);
            cell = sheet.getRow(index + 1)
                .getCell(9);
            cell.setCellValue(item.getPfb() != null ? String.valueOf(item.getPfb()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 9, 9);
            cell = sheet.getRow(index + 2)
                .getCell(9);
            cell.setCellValue(item.getPfc() != null ? String.valueOf(item.getPfc()) : "-");

            // cột THD U
            region = new CellRangeAddress(index, index, 10, 10);
            cell = sheet.getRow(index)
                .getCell(10);
            cell.setCellValue(item.getThdVab() != null ? String.valueOf(item.getThdVab()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 10, 10);
            cell = sheet.getRow(index + 1)
                .getCell(10);
            cell.setCellValue(item.getThdVbc() != null ? String.valueOf(item.getThdVbc()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 10, 10);
            cell = sheet.getRow(index + 2)
                .getCell(10);
            cell.setCellValue(item.getThdVca() != null ? String.valueOf(item.getThdVca()) : "-");

            // cột THD I
            region = new CellRangeAddress(index, index, 11, 11);
            cell = sheet.getRow(index)
                .getCell(11);
            cell.setCellValue(item.getThdIa() != null ? String.valueOf(item.getThdIa()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 11, 11);
            cell = sheet.getRow(index + 1)
                .getCell(11);
            cell.setCellValue(item.getThdIb() != null ? String.valueOf(item.getThdIb()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 11, 11);
            cell = sheet.getRow(index + 2)
                .getCell(11);
            cell.setCellValue(item.getThdIc() != null ? String.valueOf(item.getThdIc()) : "-");

            // cột Phase U
            region = new CellRangeAddress(index, index, 12, 12);
            cell = sheet.getRow(index)
                .getCell(12);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 1, index + 1, 12, 12);
            cell = sheet.getRow(index + 1)
                .getCell(12);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 2, index + 2, 12, 12);
            cell = sheet.getRow(index + 2)
                .getCell(12);
            cell.setCellValue("-");

            // cột f
            region = new CellRangeAddress(index, index, 13, 13);
            cell = sheet.getRow(index)
                .getCell(13);
            cell.setCellValue(item.getF() != null ? String.valueOf(item.getF()) : "-");

            // cột VU
            region = new CellRangeAddress(index, index, 14, 14);
            cell = sheet.getRow(index)
                .getCell(14);
            cell.setCellValue("-");

            // cột IU
            region = new CellRangeAddress(index, index, 15, 15);
            cell = sheet.getRow(index)
                .getCell(15);
            cell.setCellValue("-");

            // cột Active Energy
            region = new CellRangeAddress(index, index, 16, 16);
            cell = sheet.getRow(index)
                .getCell(16);
            cell.setCellValue(item.getEp() != null ? String.valueOf(Math.round(item.getEp() / 1000)) : "-");

            index += 3;
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

    /**
     * Lấy dữ liệu biểu đồ thông tin vận hành
     *
     * @param deviceId Mã thiết bị
     * @param chartType Kiểu biểu đồ
     * @param fromDate Ngày bắt đầu tìm kiếm
     * @param toDate Ngày kết thúc tìm kiếm
     * @param pqsViewType Kiểu truy vấn dữ liệu điện năng(ngày, tháng năm)
     * @return Dữ liệu biểu đồ
     */
    @GetMapping ("/chart/{customerId}/{deviceId}")
    public ResponseEntity<?> getDataChart(@PathVariable Integer customerId, @PathVariable final Long deviceId,
        @RequestParam (required = false) final String fromDate, @RequestParam (required = false) final String toDate,
        @RequestParam final Integer pqsViewType, @RequestParam final Integer chartType) throws ParseException {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        // Lấy dữ liệu biểu đồ dòng điện
        if (chartType == Constants.DATA.CHART.DONG_DIEN) {

            List<DataChartResponse> data = new ArrayList<>();

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

            List<DataLoadFrame1> dataInfo = new ArrayList<>();
            Map<String, Object> condition = new HashMap<>();
            for (int i = duration[0]; i <= duration[1]; i++) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.FRAME1));
                condition.put(DEVICE_ID, deviceId);
                if (fromDate != null && toDate != null) {
                    condition.put(FROM_DATE, fromDate.concat(TIME_START));
                    condition.put(TO_DATE, toDate.concat(TIME_END));
                }
                condition.put(SCHEMA, schema);
                condition.put(SORT, SORT_ASC);

                List<DataLoadFrame1> frame1s = operationInfoService.getOperationInformation(condition);

                dataInfo.addAll(frame1s);
            }

            for (DataLoadFrame1 frame1 : dataInfo) {
                DataChartResponse chartRes = new DataChartResponse(frame1);
                data.add(chartRes);
            }

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<List<DataChartResponse>>(data, HttpStatus.OK);
        }

        // Lấy dữ liệu biểu đồ điện áp
        if (chartType == Constants.DATA.CHART.DIEN_AP) {

            List<DataChartResponse> data = new ArrayList<>();

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

            List<DataLoadFrame1> dataInfo = new ArrayList<>();
            Map<String, Object> condition = new HashMap<>();
            for (int i = duration[1]; i >= duration[0]; i--) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.FRAME1));
                condition.put(DEVICE_ID, deviceId);
                if (fromDate != null && toDate != null) {
                    condition.put(FROM_DATE, fromDate.concat(TIME_START));
                    condition.put(TO_DATE, toDate.concat(TIME_END));
                }
                condition.put(SCHEMA, schema);
                condition.put(SORT, SORT_ASC);

                List<DataLoadFrame1> frame1s = operationInfoService.getOperationInformation(condition);

                dataInfo.addAll(frame1s);
            }

            for (DataLoadFrame1 frame1 : dataInfo) {
                DataChartResponse chartRes = new DataChartResponse(frame1);
                data.add(chartRes);
            }

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<List<DataChartResponse>>(data, HttpStatus.OK);
        }

        // Lấy dữ liệu biểu đồ công suất tác dụng
        if (chartType == Constants.DATA.CHART.CONG_SUAT_TAC_DUNG) {

            List<DataChartResponse> data = new ArrayList<>();

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

            List<DataLoadFrame1> dataInfo = new ArrayList<>();
            Map<String, Object> condition = new HashMap<>();
            for (int i = duration[1]; i >= duration[0]; i--) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.FRAME1));
                condition.put(DEVICE_ID, deviceId);
                if (fromDate != null && toDate != null) {
                    condition.put(FROM_DATE, fromDate.concat(TIME_START));
                    condition.put(TO_DATE, toDate.concat(TIME_END));
                }
                condition.put(SCHEMA, schema);
                condition.put(SORT, SORT_ASC);

                List<DataLoadFrame1> frame1s = operationInfoService.getOperationInformation(condition);

                dataInfo.addAll(frame1s);
            }

            for (DataLoadFrame1 frame1 : dataInfo) {
                DataChartResponse chartRes = new DataChartResponse(frame1);
                data.add(chartRes);
            }

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<List<DataChartResponse>>(data, HttpStatus.OK);
        }

        // Lấy dữ liệu biểu dồ điện năng
        if (chartType == Constants.DATA.CHART.DIEN_NANG_PQS) {

            List<DataPQSResponse> data = new ArrayList<>();

            // Dữ liệu biểu đồ điện năng trong ngày
            if (pqsViewType == PQS_VIEW_DAY) {

                String[] hours = new String[] {"00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00",
                    "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00",
                    "19:00", "20:00", "21:00", "22:00", "23:00"};

                String[] tLow = new String[] {"00:00 ~ 01:00", "01:00 ~ 02:00", "02:00 ~ 03:00", "03:00 ~ 04:00",
                    "22:00 ~ 23:00", "23:00 ~ 23:59"};

                String[] tNormal = new String[] {"04:00 ~ 05:00", "05:00 ~ 06:00", "06:00 ~ 07:00", "07:00 ~ 08:00",
                    "08:00 ~ 09:00", "12:00 ~ 13:00", "13:00 ~ 14:00", "14:00 ~ 15:00", "15:00 ~ 16:00",
                    "16:00 ~ 17:00", "20:00 ~ 21:00", "21:00 ~ 22:00"};

                String[] tHigh = new String[] {"09:00 ~ 10:00", "10:00 ~ 11:00", "11:00 ~ 12:00", "17:00 ~ 18:00",
                    "18:00 ~ 19:00", "19:00 ~ 20:00"};

                List<DataPQSResponse> dataPQS = getDataPQSByDay(hours, tLow, tNormal, tHigh, fromDate, deviceId,
                    customerId);
                data.addAll(dataPQS);
            }

            if (pqsViewType == PQS_VIEW_MONTH) {

                data = getDataPQSByMonth(customerId, deviceId, fromDate);

            }

            if (pqsViewType == PQS_VIEW_YEAR) {

                data = getDataPQSByYear(customerId, deviceId, fromDate);

            }

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<List<DataPQSResponse>>(data, HttpStatus.OK);
        }

        // Lấy dữ liệu biểu đồ nhiệt độ
        if (chartType == Constants.DATA.CHART.NHIET_DO) {

            List<DataChartResponse> data = new ArrayList<>();

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

            List<DataLoadFrame1> dataInfo = new ArrayList<>();
            Map<String, Object> condition = new HashMap<>();
            for (int i = duration[1]; i >= duration[0]; i--) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.FRAME1));
                condition.put(DEVICE_ID, deviceId);
                if (fromDate != null && toDate != null) {
                    condition.put(FROM_DATE, fromDate.concat(TIME_START));
                    condition.put(TO_DATE, toDate.concat(TIME_END));
                }
                condition.put(SCHEMA, schema);
                condition.put(SORT, SORT_ASC);

                List<DataLoadFrame1> frame1s = operationInfoService.getOperationInformation(condition);

                dataInfo.addAll(frame1s);
            }

            for (DataLoadFrame1 frame1 : dataInfo) {
                DataChartResponse chartRes = new DataChartResponse(frame1);
                data.add(chartRes);
            }

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<List<DataChartResponse>>(data, HttpStatus.OK);
        }

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName());

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * Lấy thiết bị theo dự án
     *
     * @param projectId Mã dự án
     * @return Danh sách thiết bị
     */
    @GetMapping ("/devices/{projectId}/{systemTypeId}")
    public ResponseEntity<List<DeviceResponse>> getDevicesByProject(@PathVariable final Long projectId,
        @PathVariable final Integer systemTypeId) {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        List<DeviceResponse> data = new ArrayList<DeviceResponse>();

        Map<String, Object> condition = new HashMap<>();
        condition.put(PROJECT_ID, projectId);
        condition.put("systemTypeId", systemTypeId);

        List<Device> dataInfo = deviceService.getDevicesByProjectIdAndSystemTypeId(condition);

        if (dataInfo.size() > 0) {
            for (Device device : dataInfo) {
                DeviceResponse res = new DeviceResponse(device);
                data.add(res);
            }

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<List<DeviceResponse>>(data, HttpStatus.OK);
        } else {

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<List<DeviceResponse>>(HttpStatus.OK);
        }

    }

    /**
     * Download dữ liệu biểu đồ thông tin vận hành
     *
     * @param deviceId Mã thiết bị
     * @param chartType Kiểu biểu đồ
     * @param fromDate Ngày bắt đầu tìm kiếm
     * @param toDate Ngày kết thúc tìm kiếm
     * @param pqsViewType Kiểu truy vấn dữ liệu điện năng(ngày, tháng năm)
     * @return Dữ liệu biểu đồ
     * @throws Exception
     */
    @GetMapping ("/chart/download/{customerId}/{deviceId}")
    public ResponseEntity<Resource> downloadDataChart(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam (required = false) final String fromDate,
        @RequestParam (required = false) final String toDate, @RequestParam final Integer pqsViewType,
        @RequestParam final Integer chartType, @RequestParam final String userName) throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        User user = userMapper.getUserByUsername(userName);
        // get url image
        String pngImageURL = user.getImg();
        String encodingPrefix = "base64,";
        int contentStartIndex = pngImageURL.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData = org.apache.commons.codec.binary.Base64
            .decodeBase64(pngImageURL.substring(contentStartIndex));

        String path = null;

        // time miliseconds
        long miliseconds = new Date().getTime();

        // gửi zip qua client
        String contentType = "application/zip";
        String headerValue = "attachment; filename=" + miliseconds + ".zip";

        // Lấy dữ liệu biểu đồ dòng điện
        if (chartType == Constants.DATA.CHART.DONG_DIEN) {

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

            List<DataLoadFrame1> dataInfo = new ArrayList<>();
            Map<String, Object> condition = new HashMap<>();
            for (int i = duration[1]; i >= duration[0]; i--) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.FRAME1));
                condition.put(DEVICE_ID, deviceId);
                if (fromDate != null && toDate != null) {
                    condition.put(FROM_DATE, fromDate.concat(TIME_START));
                    condition.put(TO_DATE, toDate.concat(TIME_END));
                }
                condition.put(SCHEMA, schema);
                condition.put(SORT, SORT_ASC);

                List<DataLoadFrame1> frame1s = operationInfoService.getOperationInformation(condition);

                dataInfo.addAll(frame1s);
            }

            Device device = deviceService.getDeviceByDeviceId(condition);

            // path folder
            path = this.folderName + File.separator + miliseconds;

            // tạo excel
            createPowerCircuitExcel(dataInfo, fromDate, toDate, device, path, imageData, miliseconds);
        }

        // Lấy dữ liệu biểu đồ điện áp
        if (chartType == Constants.DATA.CHART.DIEN_AP) {

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

            List<DataLoadFrame1> dataInfo = new ArrayList<>();
            Map<String, Object> condition = new HashMap<>();
            for (int i = duration[1]; i >= duration[0]; i--) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.FRAME1));
                condition.put(DEVICE_ID, deviceId);
                if (fromDate != null && toDate != null) {
                    condition.put(FROM_DATE, fromDate.concat(TIME_START));
                    condition.put(TO_DATE, toDate.concat(TIME_END));
                }
                condition.put(SCHEMA, schema);
                condition.put(SORT, SORT_ASC);

                List<DataLoadFrame1> frame1s = operationInfoService.getOperationInformation(condition);

                dataInfo.addAll(frame1s);
            }

            Device device = deviceService.getDeviceByDeviceId(condition);

            // path folder
            path = this.folderName + File.separator + miliseconds;

            // tạo excel
            createVoltageExcel(dataInfo, fromDate, toDate, device, path, imageData, miliseconds);
        }

        // Lấy dữ liệu biểu đồ công suất tác dụng
        if (chartType == Constants.DATA.CHART.CONG_SUAT_TAC_DUNG) {

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

            List<DataLoadFrame1> dataInfo = new ArrayList<>();
            Map<String, Object> condition = new HashMap<>();
            for (int i = duration[1]; i >= duration[0]; i--) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.FRAME1));
                condition.put(DEVICE_ID, deviceId);
                if (fromDate != null && toDate != null) {
                    condition.put(FROM_DATE, fromDate.concat(TIME_START));
                    condition.put(TO_DATE, toDate.concat(TIME_END));
                }
                condition.put(SCHEMA, schema);
                condition.put(SORT, SORT_ASC);

                List<DataLoadFrame1> frame1s = operationInfoService.getOperationInformation(condition);

                dataInfo.addAll(frame1s);
            }

            Device device = deviceService.getDeviceByDeviceId(condition);

            // path folder
            path = this.folderName + File.separator + miliseconds;

            // tạo excel
            createEffectivePowerExcel(dataInfo, fromDate, toDate, device, path, imageData, miliseconds);
        }

        // Lấy dữ liệu biểu dồ điện năng
        if (chartType == Constants.DATA.CHART.DIEN_NANG_PQS) {

            String[] hours = new String[] {"00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00",
                "08:00", "09:00", "09:30", "10:00", "11:00", "11:30", "12:00", "13:00", "14:00", "15:00", "16:00",
                "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"};

            String[] tLow = new String[] {"00:00 ~ 01:00", "01:00 ~ 02:00", "02:00 ~ 03:00", "03:00 ~ 04:00",
                "22:00 ~ 23:00", "23:00 ~ 23:59"};

            String[] tNormal = new String[] {"04:00 ~ 05:00", "05:00 ~ 06:00", "06:00 ~ 07:00", "07:00 ~ 08:00",
                "08:00 ~ 09:00", "09:00 ~ 09:30", "09:30 ~ 10:00", "11:30 ~ 12:00", "12:00 ~ 13:00", "13:00 ~ 14:00",
                "14:00 ~ 15:00", "15:00 ~ 16:00", "16:00 ~ 17:00", "20:00 ~ 21:00", "21:00 ~ 22:00"};

            String[] tHigh = new String[] {"09:30 ~ 10:00", "10:00 ~ 11:00", "11:00 ~ 11:30", "17:00 ~ 18:00",
                "18:00 ~ 19:00", "19:00 ~ 20:00"};

            Map<String, Object> condition = new HashMap<>();
            condition.put(DEVICE_ID, deviceId);

            Device device = deviceService.getDeviceByDeviceId(condition);

            // path folder
            path = this.folderName + File.separator + miliseconds;

            // Dữ liệu biểu đồ điện năng trong ngày
            if (pqsViewType == PQS_VIEW_DAY) {

                List<DataPQSResponse> dataPQS = getDataPQSByDay(hours, tLow, tNormal, tHigh, fromDate, deviceId,
                    customerId);

                // tạo excel
                createPQSExcel(dataPQS, fromDate, device, path, imageData, miliseconds);
            }

            if (pqsViewType == PQS_VIEW_MONTH) {

                List<DataPQSResponse> dataPQS = getDataPQSByMonth(customerId, deviceId, fromDate);

                createPQSExcel(dataPQS, fromDate, device, path, imageData, miliseconds);
            }

            if (pqsViewType == PQS_VIEW_YEAR) {

                List<DataPQSResponse> dataPQS = getDataPQSByYear(customerId, deviceId, fromDate);

                createPQSExcel(dataPQS, fromDate, device, path, imageData, miliseconds);
            }
        }

        // Lấy dữ liệu biểu đồ nhiệt độ
        if (chartType == Constants.DATA.CHART.NHIET_DO) {

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

            List<DataLoadFrame1> dataInfo = new ArrayList<>();
            Map<String, Object> condition = new HashMap<>();
            for (int i = duration[1]; i >= duration[0]; i--) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.FRAME1));
                condition.put(DEVICE_ID, deviceId);
                if (fromDate != null && toDate != null) {
                    condition.put(FROM_DATE, fromDate.concat(TIME_START));
                    condition.put(TO_DATE, toDate.concat(TIME_END));
                }
                condition.put(SCHEMA, schema);
                condition.put(SORT, SORT_ASC);

                List<DataLoadFrame1> frame1s = operationInfoService.getOperationInformation(condition);

                dataInfo.addAll(frame1s);
            }

            Device device = deviceService.getDeviceByDeviceId(condition);

            // path folder
            path = this.folderName + File.separator + miliseconds;

            // tạo excel
            createTemperatureExcel(dataInfo, fromDate, toDate, device, path, imageData, miliseconds);
        }

        // Lấy dữ liệu sóng hài
        if (chartType == Constants.DATA.CHART.SONG_HAI) {

            Map<String, Object> condition = new HashMap<>();
            condition.put(DEVICE_ID, deviceId);
            if (fromDate != null && toDate != null) {
                condition.put(FROM_DATE, fromDate.concat(TIME_START));
                condition.put(TO_DATE, toDate.concat(TIME_END));
            }
            condition.put(SCHEMA, Schema.getSchemas(customerId));
            condition.put(SORT, SORT_ASC);
            int[] duration = new int[2];
            duration = CommonUtils.calculateDataIndex(fromDate.concat(TIME_START), toDate.concat(TIME_END));

            // get Data
            List<DataLoadFrame1> data = new ArrayList<>();
            for (int i = duration[0]; i <= duration[1]; i++) {
                String tableName = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.FRAME1));
                condition.put("tableName", tableName);
                List<DataLoadFrame1> frame1s = operationInfoService.getHarmonicPeriod(condition);
                if (frame1s.size() > 0) {
                    frame1s.forEach(f -> {
                        data.add(f);
                    });
                    frame1s.clear();
                }
            }

            Device device = deviceService.getDeviceByDeviceId(condition);

            // path folder
            path = this.folderName + File.separator + miliseconds;

            // tạo excel
            createHarmonicExcel(data, fromDate, toDate, device, path, imageData, miliseconds);
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
     * Tạo excel thông số chất lượng điện năng.
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"static-access", "unused"})
    private void createPQSExcel(final List<DataPQSResponse> data, final String fromDate, final Device device,
        final String path, final byte[] imageData, final long miliseconds) throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(data.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Thông số chất lượng điện năng");
        Row row;
        Cell cell;

        // add image
        int pictureIdx = wb.addPicture(imageData, wb.PICTURE_TYPE_PNG);
        SXSSFDrawing drawingImg = sheet.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(5);
        anchorImg.setCol2(6);
        anchorImg.setRow1(1);
        anchorImg.setRow2(4);

        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);

        // Tạo sheet content
        for (int i = 0; i < 7; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 36; j++) {
                row.createCell(j);
            }
        }

        sheet.setColumnWidth(0, 1800);
        sheet.setColumnWidth(1, 5200);
        sheet.setColumnWidth(2, 5200);
        sheet.setColumnWidth(3, 5200);
        sheet.setColumnWidth(4, 5200);
        sheet.setColumnWidth(5, 5200);

        // set độ rộng của hàng
        Row row1 = sheet.getRow(1);
        row1.setHeight((short) -500);
        Row row2 = sheet.getRow(2);
        row2.setHeight((short) -500);
        Row row3 = sheet.getRow(3);
        row3.setHeight((short) -500);

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 5);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(0)
            .getCell(0);
        cell.setCellValue("BÁO CÁO CHẤT LƯỢNG ĐIỆN NĂNG [kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(0);
        cell.setCellValue("Mã thiết bị");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(2, 2, 2, 2);
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

        region = new CellRangeAddress(3, 3, 2, 2);
        cell = sheet.getRow(3)
            .getCell(2);
        cell.setCellValue(device != null && device.getDeviceName() != null
            ? device.getDeviceName()
                .toUpperCase()
            : "");
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(2, 3, 3, 3);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 3, 4, 4);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(4);
        cell.setCellValue(fromDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

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
        cell.setCellValue("Giờ thấp điểm");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("Giờ bình thường");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("Giờ cao điểm");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("Tổng");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataPQSResponse item = data.get(m);
            for (int i = index; i < index + 3; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 36; j++) {
                    Cell c = row.createCell(j, CellType.BLANK);
                    c.setCellStyle(cs);
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
            cell.setCellValue(item.getTime());

            region = new CellRangeAddress(index, index, 2, 2);
            cell = sheet.getRow(index)
                .getCell(2);
            cell.setCellValue(item.getLow() != null ? String.valueOf(item.getLow()) : "-");

            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getNormal() != null ? String.valueOf(item.getNormal()) : "-");

            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue(item.getHigh() != null ? String.valueOf(item.getHigh()) : "-");

            region = new CellRangeAddress(index, index, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue(item.getTotal() != null ? String.valueOf(item.getTotal()) : "-");

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
    private void createPowerCircuitExcel(final List<DataLoadFrame1> data, final String fromDate, final String toDate,
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

        // Tạo sheet content
        for (int i = 0; i < 7; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 36; j++) {
                row.createCell(j);
            }
        }

        sheet.setColumnWidth(0, 1800);
        sheet.setColumnWidth(1, 5200);
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
        cell.setCellValue("BÁO CÁO DÒNG ĐIỆN");
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
        cell.setCellValue("Pha");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("Dòng điện [A]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataLoadFrame1 item = data.get(m);
            for (int i = index; i < index + 3; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 36; j++) {
                    Cell c = row.createCell(j, CellType.BLANK);
                    c.setCellStyle(cs);
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
            cell.setCellValue("A");

            region = new CellRangeAddress(index + 1, index + 1, 2, 2);
            cell = sheet.getRow(index + 1)
                .getCell(2);
            cell.setCellValue("B");

            region = new CellRangeAddress(index + 2, index + 2, 2, 2);
            cell = sheet.getRow(index + 2)
                .getCell(2);
            cell.setCellValue("C");

            // Cột Vị trí 1
            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getIa() != null ? String.valueOf(item.getIa()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 3, 3);
            cell = sheet.getRow(index + 1)
                .getCell(3);
            cell.setCellValue(item.getIb() != null ? String.valueOf(item.getIb()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 3, 3);
            cell = sheet.getRow(index + 2)
                .getCell(3);
            cell.setCellValue(item.getIc() != null ? String.valueOf(item.getIc()) : "-");

            index += 3;
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
     * Tạo excel thông tin điện áp.
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"static-access", "unused"})
    private void createVoltageExcel(final List<DataLoadFrame1> data, final String fromDate, final String toDate,
        final Device device, final String path, final byte[] imageData, final long miliseconds) throws Exception {

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

        // Tạo sheet content
        for (int i = 0; i < 7; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 36; j++) {
                row.createCell(j);
            }
        }

        sheet.setColumnWidth(0, 1800);
        sheet.setColumnWidth(1, 5200);
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
        cell.setCellValue("BÁO CÁO ĐIỆN ÁP");
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
        cell.setCellValue("Pha");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("Điện áp [V]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataLoadFrame1 item = data.get(m);
            for (int i = index; i < index + 3; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 36; j++) {
                    Cell c = row.createCell(j, CellType.BLANK);
                    c.setCellStyle(cs);
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
            cell.setCellValue("A");

            region = new CellRangeAddress(index + 1, index + 1, 2, 2);
            cell = sheet.getRow(index + 1)
                .getCell(2);
            cell.setCellValue("B");

            region = new CellRangeAddress(index + 2, index + 2, 2, 2);
            cell = sheet.getRow(index + 2)
                .getCell(2);
            cell.setCellValue("C");

            // Cột Vị trí 1
            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getUan() != null ? String.valueOf(item.getUan()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 3, 3);
            cell = sheet.getRow(index + 1)
                .getCell(3);
            cell.setCellValue(item.getUbn() != null ? String.valueOf(item.getUbn()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 3, 3);
            cell = sheet.getRow(index + 2)
                .getCell(3);
            cell.setCellValue(item.getUcn() != null ? String.valueOf(item.getUcn()) : "-");

            index += 3;
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
     * Tạo excel thông tin công suất tác dụng.
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"static-access", "unused"})
    private void createEffectivePowerExcel(final List<DataLoadFrame1> data, final String fromDate, final String toDate,
        final Device device, final String path, final byte[] imageData, final long miliseconds) throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(data.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Thông số công suất tác dụng");
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

        // Tạo sheet content
        for (int i = 0; i < 7; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 36; j++) {
                row.createCell(j);
            }
        }

        sheet.setColumnWidth(0, 1800);
        sheet.setColumnWidth(1, 5200);
        sheet.setColumnWidth(3, 5200);
        sheet.setColumnWidth(4, 5200);
        sheet.setColumnWidth(5, 5200);
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
        cell.setCellValue("BÁO CÁO CÔNG SUẤT TÁC DỤNG");
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
        cell.setCellValue("Pha");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("Công suất [W]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataLoadFrame1 item = data.get(m);
            for (int i = index; i < index + 3; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 36; j++) {
                    Cell c = row.createCell(j, CellType.BLANK);
                    c.setCellStyle(cs);
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
            cell.setCellValue("A");

            region = new CellRangeAddress(index + 1, index + 1, 2, 2);
            cell = sheet.getRow(index + 1)
                .getCell(2);
            cell.setCellValue("B");

            region = new CellRangeAddress(index + 2, index + 2, 2, 2);
            cell = sheet.getRow(index + 2)
                .getCell(2);
            cell.setCellValue("C");

            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getPa() != null ? String.valueOf(item.getPa()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 3, 3);
            cell = sheet.getRow(index + 1)
                .getCell(3);
            cell.setCellValue(item.getPb() != null ? String.valueOf(item.getPb()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 3, 3);
            cell = sheet.getRow(index + 2)
                .getCell(3);
            cell.setCellValue(item.getPc() != null ? String.valueOf(item.getPc()) : "-");

            index += 3;
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
     * Lấy dữ liệu điện năng theo năm
     *
     * @param customerId Mã khách hàng
     * @param deviceId Mã thiết bị
     * @return Danh sách dữ liệu điện năng trong năm
     */
    private List<DataPQSResponse> getDataPQSByYear(final Integer customerId, final Long deviceId, final String date) {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        String[] tLow = {"00:00", "01:00", "02:00", "03:00", "22:00", "23:00"};
        String[] tNormal = {"04:00", "05:00", "06:00", "07:00", "08:00", "12:00", "13:00", "14:00", "15:00", "16:00",
            "20:00", "21:00"};
        String[] tHight = {"09:00", "10:00", "11:00", "17:00", "18:00", "19:00"};

        List<DataPQSResponse> data = new ArrayList<>();

        String fromDate = date + "-01 00:00:00";
        String toDate = date + "-31 23:59:59";

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        condition.put(FROM_DATE, fromDate);
        condition.put(TO_DATE, toDate);
        condition.put(SCHEMA, Schema.getSchemas(customerId));
        List<DataLoadFrame1> dataInfo = operationInfoService.getDataPQSByMonth(condition);

        String dateMonth = null;
        int daysInMonth = 28;
        // CHECKSTYLE:ON
        for (int f = 1; f < 13; f++) {
            if (f < 10) {
                dateMonth = date + "-0" + f;
            } else {
                dateMonth = date + "-" + f;
            }

            // CHECKSTYLE:ON
            String[] dateTime = dateMonth.split(Constants.ES.HYPHEN_CHARACTER);
            YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(dateTime[0]), Integer.parseInt(dateTime[1]));
            daysInMonth = yearMonthObject.lengthOfMonth();

            Integer day = Integer.valueOf(daysInMonth);

            // TỔNG điện năng theo giờ trong tháng
            long epTotal = 0;
            long epNonpeakHour = 0;
            long epPeakHour = 0;
            long epNormalHour = 0;
            DataPQSResponse js = new DataPQSResponse();
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

                for (int j = 0; j < dataInfo.size(); j++) {
                    DataLoadFrame1 dataLoadFrame1 = dataInfo.get(j);

                    String viewTime = dataLoadFrame1.getViewTime();
                    for (int k = 0; k < 24; k++) {
                        if (k < 10) {
                            currentDate = currentDay + "0" + k + ":00:00";
                        } else {
                            currentDate = currentDay + k + ":00:00";
                        }
                        if (StringUtils.equals(viewTime, currentDate)) {
                            js.setTime(viewTime);
                            String[] viewTimes = viewTime.split(" ");
                            String[] times = viewTimes[1].split(":");
                            String time = times[0] + ":" + times[1];
                            boolean checkLow = Arrays.stream(tLow)
                                .anyMatch(time::equals);
                            boolean checkNomal = Arrays.stream(tNormal)
                                .anyMatch(time::equals);
                            boolean checkHight = Arrays.stream(tHight)
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

                // long total = epLow + epNormal + epHight;

                epNonpeakHour = epNonpeakHour + epLow;
                epNormalHour = epNormalHour + epNormal;
                epPeakHour = epPeakHour + epHight;
                epTotal = epNonpeakHour + epNormalHour + epPeakHour;

            }
            js.setLow((int) epNonpeakHour);
            js.setNormal((int) epNormalHour);
            js.setHigh((int) epPeakHour);
            js.setTime(dateMonth);
            js.setTotal((int) epTotal);
            js.setParam(0);
            data.add(js);
        }

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName());

        return data;
    }

    /**
     * Lấy dữ liệu điện năng theo tháng
     *
     * @param customerId Mã khách hàng
     * @param deviceId Mã thiết bị
     * @return Danh sách dữ liệu điện năng trong tháng
     */
    private List<DataPQSResponse> getDataPQSByMonth(final Integer customerId, final Long deviceId, final String date) {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        String[] tLow = {"00:00", "01:00", "02:00", "03:00", "22:00", "23:00"};
        String[] tNormal = {"04:00", "05:00", "06:00", "07:00", "08:00", "12:00", "13:00", "14:00", "15:00", "16:00",
            "20:00", "21:00"};
        String[] tHight = {"09:00", "10:00", "11:00", "17:00", "18:00", "19:00"};

        List<DataPQSResponse> data = new ArrayList<>();

        String fromDate = date + "-01 00:00:00";
        String toDate = date + "-31 23:59:59";

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        condition.put(FROM_DATE, fromDate);
        condition.put(TO_DATE, toDate);
        condition.put(SCHEMA, Schema.getSchemas(customerId));
        List<DataLoadFrame1> dataInfo = operationInfoService.getDataPQSByMonth(condition);

        int daysInMonth = 28;
        // CHECKSTYLE:ON
        String[] dateTime = date.split(Constants.ES.HYPHEN_CHARACTER);
        YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(dateTime[0]), Integer.parseInt(dateTime[1]));
        daysInMonth = yearMonthObject.lengthOfMonth();

        Integer day = Integer.valueOf(daysInMonth);

        for (int i = 1; i < day + 1; i++) {
            DataPQSResponse js = new DataPQSResponse();
            String currentDay = null;
            String currentDate = null;
            if (i < 10) {
                currentDay = dateTime[0] + Constants.ES.HYPHEN_CHARACTER + dateTime[1] + Constants.ES.HYPHEN_CHARACTER
                    + "0" + i + " ";
            } else {
                currentDay = dateTime[0] + Constants.ES.HYPHEN_CHARACTER + dateTime[1] + Constants.ES.HYPHEN_CHARACTER
                    + i + " ";
            }

            List<Long> epLows = new ArrayList<>();
            List<Long> epNormals = new ArrayList<>();
            List<Long> epHights = new ArrayList<>();

            for (int j = 0; j < dataInfo.size(); j++) {
                DataLoadFrame1 dataLoadFrame1 = dataInfo.get(j);

                String viewTime = dataLoadFrame1.getViewTime();
                for (int k = 0; k < 24; k++) {
                    if (k < 10) {
                        currentDate = currentDay + "0" + k + ":00:00";
                    } else {
                        currentDate = currentDay + k + ":00:00";
                    }
                    if (StringUtils.equals(viewTime, currentDate)) {
                        js.setTime(viewTime);
                        String[] viewTimes = viewTime.split(" ");
                        String[] times = viewTimes[1].split(":");
                        String time = times[0] + ":" + times[1];
                        boolean checkLow = Arrays.stream(tLow)
                            .anyMatch(time::equals);
                        boolean checkNomal = Arrays.stream(tNormal)
                            .anyMatch(time::equals);
                        boolean checkHight = Arrays.stream(tHight)
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
            js.setLow((int) epLow);
            js.setNormal((int) epNormal);
            js.setHigh((int) epHight);
            js.setTime(currentDay);
            js.setTotal((int) total);
            js.setParam(0);
            data.add(js);
        }

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName());

        return data;
    }

    /**
     * Lấy dữ liệu điện năng theo ngày
     *
     * @param hours Danh sách giờ trong ngày
     * @param tLow Danh sách khoảng thời gian thấp điểm
     * @param tNormal Danh sách khoảng thời gian bìn thường
     * @param tHigh Danh sách khoảng thời gian cao điểm
     * @param fromDate Thời gian truy vấn dữ liệu
     * @param deviceId Mã thiết bị
     * @return Danh sách dữ liệu điện năng trong ngày
     */
    private List<DataPQSResponse> getDataPQSByDay(final String[] hours, final String[] tLow, final String[] tNormal,
        final String[] tHigh, final String fromDate, final Long deviceId, final Integer customerId) {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        List<DataPQSResponse> data = new ArrayList<>();

        for (int i = 0; i < hours.length; i++) {

            Map<String, Object> map = new HashMap<>();
            String t0 = fromDate + " " + hours[i] + ":00";
            String t1;
            if (i == hours.length - 1 || String.valueOf(hours[i]) == "23:00") {
                t1 = fromDate + " 23:59:00";
            } else {
                t1 = fromDate + " " + hours[i + 1] + ":00";
            }
            map.put(DEVICE_ID, deviceId);
            map.put(FROM_DATE, t0);
            map.put(TO_DATE, t1);
            map.put(SORT, SORT_DESC);

            int tableIndex = CommonUtils.calculateDataIndex(fromDate);
            String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                .get(new MultiKey(Constants.DATA.tables[tableIndex], Constants.DATA.MESSAGE.FRAME1));
            map.put(SCHEMA, schema);

            List<DataLoadFrame1> framePQS = operationInfoService.getOperationInformation(map);

            Integer ep;
            if (framePQS.size() == 0) {
                ep = 0;
            } else {
                Integer toEp = framePQS.get(0)
                    .getEp();
                Integer fromEp = framePQS.get(framePQS.size() - 1)
                    .getEp();
                ep = toEp - fromEp;
            }

            String time;
            if (i == hours.length - 1 || String.valueOf(hours[i]) == "23:00") {
                time = hours[i] + " ~ " + "23:59";
            } else {
                time = hours[i] + " ~ " + hours[i + 1];
            }

            DataPQSResponse pqsRes = new DataPQSResponse();
            if (Arrays.stream(tLow)
                .anyMatch(time::equals)) {
                pqsRes.setHigh(0);
                pqsRes.setLow(ep > 0 ? ep : 0);
                pqsRes.setNormal(0);
            }
            if (Arrays.stream(tNormal)
                .anyMatch(time::equals)) {
                pqsRes.setHigh(0);
                pqsRes.setLow(0);
                pqsRes.setNormal(ep > 0 ? ep : 0);
            }
            if (Arrays.stream(tHigh)
                .anyMatch(time::equals)) {
                pqsRes.setHigh(ep > 0 ? ep : 0);
                pqsRes.setLow(0);
                pqsRes.setNormal(0);
            }
            pqsRes.setTotal(pqsRes.getHigh() + pqsRes.getLow() + pqsRes.getNormal());
            pqsRes.setParam(0);
            pqsRes.setTime(time);

            data.add(pqsRes);
        }

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName());

        return data;
    }

    /**
     * Tạo excel thông tin sóng hài.
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"static-access", "unused"})
    private void createHarmonicExcel(final List<DataLoadFrame1> data, final String fromDate, final String toDate,
        final Device device, final String path, final byte[] imageData, final long miliseconds) throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(data.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Thông Tin Sóng Hài");
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

        // Tạo sheet content
        for (int i = 0; i < 7; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 36; j++) {
                row.createCell(j);
            }
        }

        sheet.setColumnWidth(0, 1800);
        sheet.setColumnWidth(1, 5200);
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
        cell.setCellValue("BÁO CÁO THÔNG TIN SÓNG HÀI");
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
        cell.setCellValue("Pha");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("Dòng Điện [%]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("Điện Áp [%]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataLoadFrame1 item = data.get(m);
            for (int i = index; i < index + 3; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 36; j++) {
                    Cell c = row.createCell(j, CellType.BLANK);
                    c.setCellStyle(cs);
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
            cell.setCellValue("A");

            region = new CellRangeAddress(index + 1, index + 1, 2, 2);
            cell = sheet.getRow(index + 1)
                .getCell(2);
            cell.setCellValue("B");

            region = new CellRangeAddress(index + 2, index + 2, 2, 2);
            cell = sheet.getRow(index + 2)
                .getCell(2);
            cell.setCellValue("C");

            // Cột Vị trí 1
            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getThdIa() != null ? String.valueOf(item.getThdIa()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 3, 3);
            cell = sheet.getRow(index + 1)
                .getCell(3);
            cell.setCellValue(item.getThdIb() != null ? String.valueOf(item.getThdIb()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 3, 3);
            cell = sheet.getRow(index + 2)
                .getCell(3);
            cell.setCellValue(item.getThdIc() != null ? String.valueOf(item.getThdIc()) : "-");

            // Cột Vị trí 2
            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue(item.getThdVab() != null ? String.valueOf(item.getThdVab()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 4, 4);
            cell = sheet.getRow(index + 1)
                .getCell(4);
            cell.setCellValue(item.getThdVbc() != null ? String.valueOf(item.getThdVbc()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 4, 4);
            cell = sheet.getRow(index + 2)
                .getCell(4);
            cell.setCellValue(item.getThdVca() != null ? String.valueOf(item.getThdVca()) : "-");

            index += 3;
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
     * Tạo excel thông tin nhiệt độ.
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"static-access", "unused"})
    private void createTemperatureExcel(final List<DataLoadFrame1> data, final String fromDate, final String toDate,
        final Device device, final String path, final byte[] imageData, final long miliseconds) throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(data.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Thông số nhiệt độ");
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

        // Tạo sheet content
        for (int i = 0; i < 7; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 36; j++) {
                row.createCell(j);
            }
        }

        sheet.setColumnWidth(0, 1800);
        sheet.setColumnWidth(1, 5400);
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
        cell.setCellValue("BÁO CÁO NHIỆT ĐỘ");
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
        cell.setCellValue("Pha");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("Vị trí 1 [°C]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("Vị trí 2 [°C]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("Vị trí 3 [°C]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataLoadFrame1 item = data.get(m);
            for (int i = index; i < index + 3; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 36; j++) {
                    Cell c = row.createCell(j, CellType.BLANK);
                    c.setCellStyle(cs);
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
            cell.setCellValue("A");

            region = new CellRangeAddress(index + 1, index + 1, 2, 2);
            cell = sheet.getRow(index + 1)
                .getCell(2);
            cell.setCellValue("B");

            region = new CellRangeAddress(index + 2, index + 2, 2, 2);
            cell = sheet.getRow(index + 2)
                .getCell(2);
            cell.setCellValue("C");

            // Cột Vị trí 1
            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getT1() != null ? String.valueOf(item.getT1()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 3, 3);
            cell = sheet.getRow(index + 1)
                .getCell(3);
            cell.setCellValue(item.getT2() != null ? String.valueOf(item.getT2()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 3, 3);
            cell = sheet.getRow(index + 2)
                .getCell(3);
            cell.setCellValue(item.getT3() != null ? String.valueOf(item.getT3()) : "-");

            // Cột Vị trí 2
            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 1, index + 1, 4, 4);
            cell = sheet.getRow(index + 1)
                .getCell(4);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 2, index + 2, 4, 4);
            cell = sheet.getRow(index + 2)
                .getCell(4);
            cell.setCellValue("-");

            // Cột Vị trí 3
            region = new CellRangeAddress(index, index, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 1, index + 1, 5, 5);
            cell = sheet.getRow(index + 1)
                .getCell(5);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 2, index + 2, 5, 5);
            cell = sheet.getRow(index + 2)
                .getCell(5);
            cell.setCellValue("-");

            index += 3;
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
     * Tạo excel thông tin chất lượng điện năng.
     *
     * @param data Danh sách thông tin chất lượng điện năng.
     * @param fromDate Ngày bắt đầu truy vấn.
     * @param toDate Ngày kết thức tìm kiếm.
     * @param device Thiết bị lấy dữ liệu.
     * @param path Nơi xuất file.
     * @throws Exception
     */
    @SuppressWarnings ({"static-access", "unused"})
    private void createPowerQualityExcel(final List<DataLoadFrame2> data, final String fromDate, final String toDate,
        final Device device, final String path, final byte[] imageData, final long miliseconds) throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(data.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Thông số chất lượng điện năng");
        Row row;
        Cell cell;

        // add image
        int pictureIdx = wb.addPicture(imageData, wb.PICTURE_TYPE_PNG);
        SXSSFDrawing drawingImg = sheet.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(32);
        anchorImg.setCol2(34);
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

        // set độ rộng của hàng
        Row row1 = sheet.getRow(1);
        row1.setHeight((short) -500);
        Row row2 = sheet.getRow(2);
        row2.setHeight((short) -500);
        Row row3 = sheet.getRow(3);
        row3.setHeight((short) -500);

        // set độ rộng của cột
        sheet.setColumnWidth(0, 1300);
        sheet.setColumnWidth(1, 5200);

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 33);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(0)
            .getCell(0);
        cell.setCellValue("BÁO CÁO CHẤT LƯỢNG ĐIỆN NĂNG [kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(0);
        cell.setCellValue("Mã thiết bị");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(2, 2, 2, 19);
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

        region = new CellRangeAddress(3, 3, 2, 19);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(2);
        cell.setCellValue(device.getDeviceName() != null
            ? device.getDeviceName()
                .toUpperCase()
            : "-");
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(2, 3, 20, 26);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(20);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 27, 31);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(27);
        cell.setCellValue(fromDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        region = new CellRangeAddress(3, 3, 27, 31);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(27);
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
        cell.setCellValue("H1");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("H2");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("H3");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 6, 6);
        cell = sheet.getRow(5)
            .getCell(6);
        cell.setCellValue("H4");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 7, 7);
        cell = sheet.getRow(5)
            .getCell(7);
        cell.setCellValue("H5");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 8, 8);
        cell = sheet.getRow(5)
            .getCell(8);
        cell.setCellValue("H6");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 9, 9);
        cell = sheet.getRow(5)
            .getCell(9);
        cell.setCellValue("H7");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 10, 10);
        cell = sheet.getRow(5)
            .getCell(10);
        cell.setCellValue("H8");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 11, 11);
        cell = sheet.getRow(5)
            .getCell(11);
        cell.setCellValue("H9");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 12, 12);
        cell = sheet.getRow(5)
            .getCell(12);
        cell.setCellValue("H10");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 13, 13);
        cell = sheet.getRow(5)
            .getCell(13);
        cell.setCellValue("H11");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 14, 14);
        cell = sheet.getRow(5)
            .getCell(14);
        cell.setCellValue("H12");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 15, 15);
        cell = sheet.getRow(5)
            .getCell(15);
        cell.setCellValue("H13");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 16, 16);
        cell = sheet.getRow(5)
            .getCell(16);
        cell.setCellValue("H14");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 17, 17);
        cell = sheet.getRow(5)
            .getCell(17);
        cell.setCellValue("H15");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 18, 18);
        cell = sheet.getRow(5)
            .getCell(18);
        cell.setCellValue("H16");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 19, 19);
        cell = sheet.getRow(5)
            .getCell(19);
        cell.setCellValue("H17");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 20, 20);
        cell = sheet.getRow(5)
            .getCell(20);
        cell.setCellValue("H18");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 21, 21);
        cell = sheet.getRow(5)
            .getCell(21);
        cell.setCellValue("H19");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 22, 22);
        cell = sheet.getRow(5)
            .getCell(22);
        cell.setCellValue("H20");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 23, 23);
        cell = sheet.getRow(5)
            .getCell(23);
        cell.setCellValue("H21");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 24, 24);
        cell = sheet.getRow(5)
            .getCell(24);
        cell.setCellValue("H22");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 25, 25);
        cell = sheet.getRow(5)
            .getCell(25);
        cell.setCellValue("H23");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 26, 26);
        cell = sheet.getRow(5)
            .getCell(26);
        cell.setCellValue("H24");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 27, 27);
        cell = sheet.getRow(5)
            .getCell(27);
        cell.setCellValue("H25");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 28, 28);
        cell = sheet.getRow(5)
            .getCell(28);
        cell.setCellValue("H26");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 29, 29);
        cell = sheet.getRow(5)
            .getCell(29);
        cell.setCellValue("H27");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 30, 30);
        cell = sheet.getRow(5)
            .getCell(30);
        cell.setCellValue("H28");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 31, 31);
        cell = sheet.getRow(5)
            .getCell(31);
        cell.setCellValue("H29");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 32, 32);
        cell = sheet.getRow(5)
            .getCell(32);
        cell.setCellValue("H30");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 33, 33);
        cell = sheet.getRow(5)
            .getCell(33);
        cell.setCellValue("H31");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataLoadFrame2 item = data.get(m);
            for (int i = index; i < index + 6; i++) {
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
            cell.setCellValue("UA");

            region = new CellRangeAddress(index + 1, index + 1, 2, 2);
            cell = sheet.getRow(index + 1)
                .getCell(2);
            cell.setCellValue("UB");

            region = new CellRangeAddress(index + 2, index + 2, 2, 2);
            cell = sheet.getRow(index + 2)
                .getCell(2);
            cell.setCellValue("UC");

            region = new CellRangeAddress(index + 3, index + 3, 2, 2);
            cell = sheet.getRow(index + 3)
                .getCell(2);
            cell.setCellValue("IA");

            region = new CellRangeAddress(index + 4, index + 4, 2, 2);
            cell = sheet.getRow(index + 4)
                .getCell(2);
            cell.setCellValue("IB");

            region = new CellRangeAddress(index + 5, index + 5, 2, 2);
            cell = sheet.getRow(index + 5)
                .getCell(2);
            cell.setCellValue("IC");

            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getVAN_H1() != null ? String.valueOf(item.getVAN_H1()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 3, 3);
            cell = sheet.getRow(index + 1)
                .getCell(3);
            cell.setCellValue(item.getVBN_H1() != null ? String.valueOf(item.getVBN_H1()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 3, 3);
            cell = sheet.getRow(index + 2)
                .getCell(3);
            cell.setCellValue(item.getVCN_H1() != null ? String.valueOf(item.getVBN_H1()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 3, 3);
            cell = sheet.getRow(index + 3)
                .getCell(3);
            cell.setCellValue(item.getIA_H1() != null ? String.valueOf(item.getIA_H1()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 3, 3);
            cell = sheet.getRow(index + 4)
                .getCell(3);
            cell.setCellValue(item.getIB_H1() != null ? String.valueOf(item.getIB_H1()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 3, 3);
            cell = sheet.getRow(index + 5)
                .getCell(3);
            cell.setCellValue(item.getIC_H1() != null ? String.valueOf(item.getIC_H1()) : "-");

            // H2
            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue(item.getVAN_H2() != null ? String.valueOf(item.getVAN_H2()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 4, 4);
            cell = sheet.getRow(index + 1)
                .getCell(4);
            cell.setCellValue(item.getVBN_H2() != null ? String.valueOf(item.getVBN_H2()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 4, 4);
            cell = sheet.getRow(index + 2)
                .getCell(4);
            cell.setCellValue(item.getVCN_H2() != null ? String.valueOf(item.getVBN_H2()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 4, 4);
            cell = sheet.getRow(index + 3)
                .getCell(4);
            cell.setCellValue(item.getIA_H2() != null ? String.valueOf(item.getIA_H2()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 4, 4);
            cell = sheet.getRow(index + 4)
                .getCell(4);
            cell.setCellValue(item.getIB_H2() != null ? String.valueOf(item.getIB_H2()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 4, 4);
            cell = sheet.getRow(index + 5)
                .getCell(4);
            cell.setCellValue(item.getIC_H2() != null ? String.valueOf(item.getIC_H2()) : "-");

            // H3
            region = new CellRangeAddress(index, index, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue(item.getVAN_H3() != null ? String.valueOf(item.getVAN_H3()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 5, 5);
            cell = sheet.getRow(index + 1)
                .getCell(5);
            cell.setCellValue(item.getVBN_H3() != null ? String.valueOf(item.getVBN_H3()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 5, 5);
            cell = sheet.getRow(index + 2)
                .getCell(5);
            cell.setCellValue(item.getVCN_H3() != null ? String.valueOf(item.getVBN_H3()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 5, 5);
            cell = sheet.getRow(index + 3)
                .getCell(5);
            cell.setCellValue(item.getIA_H3() != null ? String.valueOf(item.getIA_H3()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 5, 5);
            cell = sheet.getRow(index + 4)
                .getCell(5);
            cell.setCellValue(item.getIB_H3() != null ? String.valueOf(item.getIB_H3()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 5, 5);
            cell = sheet.getRow(index + 5)
                .getCell(5);
            cell.setCellValue(item.getIC_H3() != null ? String.valueOf(item.getIC_H3()) : "-");

            // H4
            region = new CellRangeAddress(index, index, 6, 6);
            cell = sheet.getRow(index)
                .getCell(6);
            cell.setCellValue(item.getVAN_H4() != null ? String.valueOf(item.getVAN_H4()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 6, 6);
            cell = sheet.getRow(index + 1)
                .getCell(6);
            cell.setCellValue(item.getVBN_H4() != null ? String.valueOf(item.getVBN_H4()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 6, 6);
            cell = sheet.getRow(index + 2)
                .getCell(6);
            cell.setCellValue(item.getVCN_H4() != null ? String.valueOf(item.getVBN_H4()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 6, 6);
            cell = sheet.getRow(index + 3)
                .getCell(6);
            cell.setCellValue(item.getIA_H4() != null ? String.valueOf(item.getIA_H4()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 6, 6);
            cell = sheet.getRow(index + 4)
                .getCell(6);
            cell.setCellValue(item.getIB_H4() != null ? String.valueOf(item.getIB_H4()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 6, 6);
            cell = sheet.getRow(index + 5)
                .getCell(6);
            cell.setCellValue(item.getIC_H4() != null ? String.valueOf(item.getIC_H4()) : "-");

            // H5
            region = new CellRangeAddress(index, index, 7, 7);
            cell = sheet.getRow(index)
                .getCell(7);
            cell.setCellValue(item.getVAN_H5() != null ? String.valueOf(item.getVAN_H5()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 7, 7);
            cell = sheet.getRow(index + 1)
                .getCell(7);
            cell.setCellValue(item.getVBN_H5() != null ? String.valueOf(item.getVBN_H5()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 7, 7);
            cell = sheet.getRow(index + 2)
                .getCell(7);
            cell.setCellValue(item.getVCN_H5() != null ? String.valueOf(item.getVBN_H5()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 7, 7);
            cell = sheet.getRow(index + 3)
                .getCell(7);
            cell.setCellValue(item.getIA_H5() != null ? String.valueOf(item.getIA_H5()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 7, 7);
            cell = sheet.getRow(index + 4)
                .getCell(7);
            cell.setCellValue(item.getIB_H5() != null ? String.valueOf(item.getIB_H5()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 7, 7);
            cell = sheet.getRow(index + 5)
                .getCell(7);
            cell.setCellValue(item.getIC_H5() != null ? String.valueOf(item.getIC_H5()) : "-");

            // H6
            region = new CellRangeAddress(index, index, 8, 8);
            cell = sheet.getRow(index)
                .getCell(8);
            cell.setCellValue(item.getVAN_H6() != null ? String.valueOf(item.getVAN_H6()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 8, 8);
            cell = sheet.getRow(index + 1)
                .getCell(8);
            cell.setCellValue(item.getVBN_H6() != null ? String.valueOf(item.getVBN_H6()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 8, 8);
            cell = sheet.getRow(index + 2)
                .getCell(8);
            cell.setCellValue(item.getVCN_H6() != null ? String.valueOf(item.getVBN_H6()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 8, 8);
            cell = sheet.getRow(index + 3)
                .getCell(8);
            cell.setCellValue(item.getIA_H6() != null ? String.valueOf(item.getIA_H6()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 8, 8);
            cell = sheet.getRow(index + 4)
                .getCell(8);
            cell.setCellValue(item.getIB_H6() != null ? String.valueOf(item.getIB_H6()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 8, 8);
            cell = sheet.getRow(index + 5)
                .getCell(8);
            cell.setCellValue(item.getIC_H6() != null ? String.valueOf(item.getIC_H6()) : "-");

            // H7
            region = new CellRangeAddress(index, index, 9, 9);
            cell = sheet.getRow(index)
                .getCell(9);
            cell.setCellValue(item.getVAN_H7() != null ? String.valueOf(item.getVAN_H7()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 9, 9);
            cell = sheet.getRow(index + 1)
                .getCell(9);
            cell.setCellValue(item.getVBN_H7() != null ? String.valueOf(item.getVBN_H7()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 9, 9);
            cell = sheet.getRow(index + 2)
                .getCell(9);
            cell.setCellValue(item.getVCN_H7() != null ? String.valueOf(item.getVBN_H7()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 9, 9);
            cell = sheet.getRow(index + 3)
                .getCell(9);
            cell.setCellValue(item.getIA_H7() != null ? String.valueOf(item.getIA_H7()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 9, 9);
            cell = sheet.getRow(index + 4)
                .getCell(9);
            cell.setCellValue(item.getIB_H7() != null ? String.valueOf(item.getIB_H7()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 9, 9);
            cell = sheet.getRow(index + 5)
                .getCell(9);
            cell.setCellValue(item.getIC_H7() != null ? String.valueOf(item.getIC_H7()) : "-");

            // H8
            region = new CellRangeAddress(index, index, 10, 10);
            cell = sheet.getRow(index)
                .getCell(10);
            cell.setCellValue(item.getVAN_H8() != null ? String.valueOf(item.getVAN_H8()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 10, 10);
            cell = sheet.getRow(index + 1)
                .getCell(10);
            cell.setCellValue(item.getVBN_H8() != null ? String.valueOf(item.getVBN_H8()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 10, 10);
            cell = sheet.getRow(index + 2)
                .getCell(10);
            cell.setCellValue(item.getVCN_H8() != null ? String.valueOf(item.getVBN_H8()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 10, 10);
            cell = sheet.getRow(index + 3)
                .getCell(10);
            cell.setCellValue(item.getIA_H8() != null ? String.valueOf(item.getIA_H8()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 10, 10);
            cell = sheet.getRow(index + 4)
                .getCell(10);
            cell.setCellValue(item.getIB_H8() != null ? String.valueOf(item.getIB_H8()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 10, 10);
            cell = sheet.getRow(index + 5)
                .getCell(10);
            cell.setCellValue(item.getIC_H8() != null ? String.valueOf(item.getIC_H8()) : "-");

            // H9
            region = new CellRangeAddress(index, index, 11, 11);
            cell = sheet.getRow(index)
                .getCell(11);
            cell.setCellValue(item.getVAN_H9() != null ? String.valueOf(item.getVAN_H9()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 11, 11);
            cell = sheet.getRow(index + 1)
                .getCell(11);
            cell.setCellValue(item.getVBN_H9() != null ? String.valueOf(item.getVBN_H9()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 11, 11);
            cell = sheet.getRow(index + 2)
                .getCell(11);
            cell.setCellValue(item.getVCN_H9() != null ? String.valueOf(item.getVBN_H9()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 11, 11);
            cell = sheet.getRow(index + 3)
                .getCell(11);
            cell.setCellValue(item.getIA_H9() != null ? String.valueOf(item.getIA_H9()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 11, 11);
            cell = sheet.getRow(index + 4)
                .getCell(11);
            cell.setCellValue(item.getIB_H9() != null ? String.valueOf(item.getIB_H9()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 11, 11);
            cell = sheet.getRow(index + 5)
                .getCell(11);
            cell.setCellValue(item.getIC_H9() != null ? String.valueOf(item.getIC_H9()) : "-");

            // H10
            region = new CellRangeAddress(index, index, 12, 12);
            cell = sheet.getRow(index)
                .getCell(12);
            cell.setCellValue(item.getVAN_H10() != null ? String.valueOf(item.getVAN_H10()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 12, 12);
            cell = sheet.getRow(index + 1)
                .getCell(12);
            cell.setCellValue(item.getVBN_H10() != null ? String.valueOf(item.getVBN_H10()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 12, 12);
            cell = sheet.getRow(index + 2)
                .getCell(12);
            cell.setCellValue(item.getVCN_H10() != null ? String.valueOf(item.getVBN_H10()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 12, 12);
            cell = sheet.getRow(index + 3)
                .getCell(12);
            cell.setCellValue(item.getIA_H10() != null ? String.valueOf(item.getIA_H10()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 12, 12);
            cell = sheet.getRow(index + 4)
                .getCell(12);
            cell.setCellValue(item.getIB_H10() != null ? String.valueOf(item.getIB_H10()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 12, 12);
            cell = sheet.getRow(index + 5)
                .getCell(12);
            cell.setCellValue(item.getIC_H10() != null ? String.valueOf(item.getIC_H10()) : "-");

            // H11
            region = new CellRangeAddress(index, index, 13, 13);
            cell = sheet.getRow(index)
                .getCell(13);
            cell.setCellValue(item.getVAN_H11() != null ? String.valueOf(item.getVAN_H11()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 13, 13);
            cell = sheet.getRow(index + 1)
                .getCell(13);
            cell.setCellValue(item.getVBN_H11() != null ? String.valueOf(item.getVBN_H11()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 13, 13);
            cell = sheet.getRow(index + 2)
                .getCell(13);
            cell.setCellValue(item.getVCN_H11() != null ? String.valueOf(item.getVBN_H11()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 13, 13);
            cell = sheet.getRow(index + 3)
                .getCell(13);
            cell.setCellValue(item.getIA_H11() != null ? String.valueOf(item.getIA_H11()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 13, 13);
            cell = sheet.getRow(index + 4)
                .getCell(13);
            cell.setCellValue(item.getIB_H11() != null ? String.valueOf(item.getIB_H11()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 13, 13);
            cell = sheet.getRow(index + 5)
                .getCell(13);
            cell.setCellValue(item.getIC_H11() != null ? String.valueOf(item.getIC_H11()) : "-");

            // H12
            region = new CellRangeAddress(index, index, 14, 14);
            cell = sheet.getRow(index)
                .getCell(14);
            cell.setCellValue(item.getVAN_H12() != null ? String.valueOf(item.getVAN_H12()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 14, 14);
            cell = sheet.getRow(index + 1)
                .getCell(14);
            cell.setCellValue(item.getVBN_H12() != null ? String.valueOf(item.getVBN_H12()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 14, 14);
            cell = sheet.getRow(index + 2)
                .getCell(14);
            cell.setCellValue(item.getVCN_H12() != null ? String.valueOf(item.getVBN_H12()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 14, 14);
            cell = sheet.getRow(index + 3)
                .getCell(14);
            cell.setCellValue(item.getIA_H12() != null ? String.valueOf(item.getIA_H12()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 14, 14);
            cell = sheet.getRow(index + 4)
                .getCell(14);
            cell.setCellValue(item.getIB_H12() != null ? String.valueOf(item.getIB_H12()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 14, 14);
            cell = sheet.getRow(index + 5)
                .getCell(14);
            cell.setCellValue(item.getIC_H12() != null ? String.valueOf(item.getIC_H12()) : "-");

            // H13
            region = new CellRangeAddress(index, index, 15, 15);
            cell = sheet.getRow(index)
                .getCell(15);
            cell.setCellValue(item.getVAN_H13() != null ? String.valueOf(item.getVAN_H13()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 15, 15);
            cell = sheet.getRow(index + 1)
                .getCell(15);
            cell.setCellValue(item.getVBN_H13() != null ? String.valueOf(item.getVBN_H13()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 15, 15);
            cell = sheet.getRow(index + 2)
                .getCell(15);
            cell.setCellValue(item.getVCN_H13() != null ? String.valueOf(item.getVBN_H13()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 15, 15);
            cell = sheet.getRow(index + 3)
                .getCell(15);
            cell.setCellValue(item.getIA_H13() != null ? String.valueOf(item.getIA_H13()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 15, 15);
            cell = sheet.getRow(index + 4)
                .getCell(15);
            cell.setCellValue(item.getIB_H13() != null ? String.valueOf(item.getIB_H13()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 15, 15);
            cell = sheet.getRow(index + 5)
                .getCell(15);
            cell.setCellValue(item.getIC_H13() != null ? String.valueOf(item.getIC_H13()) : "-");

            // H14
            region = new CellRangeAddress(index, index, 16, 16);
            cell = sheet.getRow(index)
                .getCell(16);
            cell.setCellValue(item.getVAN_H14() != null ? String.valueOf(item.getVAN_H14()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 16, 16);
            cell = sheet.getRow(index + 1)
                .getCell(16);
            cell.setCellValue(item.getVBN_H14() != null ? String.valueOf(item.getVBN_H14()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 16, 16);
            cell = sheet.getRow(index + 2)
                .getCell(16);
            cell.setCellValue(item.getVCN_H14() != null ? String.valueOf(item.getVBN_H14()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 16, 16);
            cell = sheet.getRow(index + 3)
                .getCell(16);
            cell.setCellValue(item.getIA_H14() != null ? String.valueOf(item.getIA_H14()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 16, 16);
            cell = sheet.getRow(index + 4)
                .getCell(16);
            cell.setCellValue(item.getIB_H14() != null ? String.valueOf(item.getIB_H14()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 16, 16);
            cell = sheet.getRow(index + 5)
                .getCell(16);
            cell.setCellValue(item.getIC_H14() != null ? String.valueOf(item.getIC_H14()) : "-");

            // H15
            region = new CellRangeAddress(index, index, 17, 17);
            cell = sheet.getRow(index)
                .getCell(17);
            cell.setCellValue(item.getVAN_H15() != null ? String.valueOf(item.getVAN_H15()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 17, 17);
            cell = sheet.getRow(index + 1)
                .getCell(17);
            cell.setCellValue(item.getVBN_H15() != null ? String.valueOf(item.getVBN_H15()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 17, 17);
            cell = sheet.getRow(index + 2)
                .getCell(17);
            cell.setCellValue(item.getVCN_H15() != null ? String.valueOf(item.getVBN_H15()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 17, 17);
            cell = sheet.getRow(index + 3)
                .getCell(17);
            cell.setCellValue(item.getIA_H15() != null ? String.valueOf(item.getIA_H15()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 17, 17);
            cell = sheet.getRow(index + 4)
                .getCell(17);
            cell.setCellValue(item.getIB_H15() != null ? String.valueOf(item.getIB_H15()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 17, 17);
            cell = sheet.getRow(index + 5)
                .getCell(17);
            cell.setCellValue(item.getIC_H15() != null ? String.valueOf(item.getIC_H15()) : "-");

            // H16
            region = new CellRangeAddress(index, index, 18, 18);
            cell = sheet.getRow(index)
                .getCell(18);
            cell.setCellValue(item.getVAN_H16() != null ? String.valueOf(item.getVAN_H16()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 18, 18);
            cell = sheet.getRow(index + 1)
                .getCell(18);
            cell.setCellValue(item.getVBN_H16() != null ? String.valueOf(item.getVBN_H16()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 18, 18);
            cell = sheet.getRow(index + 2)
                .getCell(18);
            cell.setCellValue(item.getVCN_H16() != null ? String.valueOf(item.getVBN_H16()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 18, 18);
            cell = sheet.getRow(index + 3)
                .getCell(18);
            cell.setCellValue(item.getIA_H16() != null ? String.valueOf(item.getIA_H16()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 18, 18);
            cell = sheet.getRow(index + 4)
                .getCell(18);
            cell.setCellValue(item.getIB_H16() != null ? String.valueOf(item.getIB_H16()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 18, 18);
            cell = sheet.getRow(index + 5)
                .getCell(18);
            cell.setCellValue(item.getIC_H16() != null ? String.valueOf(item.getIC_H16()) : "-");

            // H17
            region = new CellRangeAddress(index, index, 19, 19);
            cell = sheet.getRow(index)
                .getCell(19);
            cell.setCellValue(item.getVAN_H17() != null ? String.valueOf(item.getVAN_H17()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 19, 19);
            cell = sheet.getRow(index + 1)
                .getCell(19);
            cell.setCellValue(item.getVBN_H17() != null ? String.valueOf(item.getVBN_H17()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 19, 19);
            cell = sheet.getRow(index + 2)
                .getCell(19);
            cell.setCellValue(item.getVCN_H17() != null ? String.valueOf(item.getVBN_H17()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 19, 19);
            cell = sheet.getRow(index + 3)
                .getCell(19);
            cell.setCellValue(item.getIA_H17() != null ? String.valueOf(item.getIA_H17()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 19, 19);
            cell = sheet.getRow(index + 4)
                .getCell(19);
            cell.setCellValue(item.getIB_H17() != null ? String.valueOf(item.getIB_H17()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 19, 19);
            cell = sheet.getRow(index + 5)
                .getCell(19);
            cell.setCellValue(item.getIC_H17() != null ? String.valueOf(item.getIC_H17()) : "-");

            // H18
            region = new CellRangeAddress(index, index, 20, 20);
            cell = sheet.getRow(index)
                .getCell(20);
            cell.setCellValue(item.getVAN_H18() != null ? String.valueOf(item.getVAN_H18()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 20, 20);
            cell = sheet.getRow(index + 1)
                .getCell(20);
            cell.setCellValue(item.getVBN_H18() != null ? String.valueOf(item.getVBN_H18()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 20, 20);
            cell = sheet.getRow(index + 2)
                .getCell(20);
            cell.setCellValue(item.getVCN_H18() != null ? String.valueOf(item.getVBN_H18()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 20, 20);
            cell = sheet.getRow(index + 3)
                .getCell(20);
            cell.setCellValue(item.getIA_H18() != null ? String.valueOf(item.getIA_H18()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 20, 20);
            cell = sheet.getRow(index + 4)
                .getCell(20);
            cell.setCellValue(item.getIB_H18() != null ? String.valueOf(item.getIB_H18()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 20, 20);
            cell = sheet.getRow(index + 5)
                .getCell(20);
            cell.setCellValue(item.getIC_H18() != null ? String.valueOf(item.getIC_H18()) : "-");

            // H19
            region = new CellRangeAddress(index, index, 21, 21);
            cell = sheet.getRow(index)
                .getCell(21);
            cell.setCellValue(item.getVAN_H19() != null ? String.valueOf(item.getVAN_H19()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 21, 21);
            cell = sheet.getRow(index + 1)
                .getCell(21);
            cell.setCellValue(item.getVBN_H19() != null ? String.valueOf(item.getVBN_H19()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 21, 21);
            cell = sheet.getRow(index + 2)
                .getCell(21);
            cell.setCellValue(item.getVCN_H19() != null ? String.valueOf(item.getVBN_H19()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 21, 21);
            cell = sheet.getRow(index + 3)
                .getCell(21);
            cell.setCellValue(item.getIA_H19() != null ? String.valueOf(item.getIA_H19()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 21, 21);
            cell = sheet.getRow(index + 4)
                .getCell(21);
            cell.setCellValue(item.getIB_H19() != null ? String.valueOf(item.getIB_H19()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 21, 21);
            cell = sheet.getRow(index + 5)
                .getCell(21);
            cell.setCellValue(item.getIC_H19() != null ? String.valueOf(item.getIC_H19()) : "-");

            // H20
            region = new CellRangeAddress(index, index, 22, 22);
            cell = sheet.getRow(index)
                .getCell(22);
            cell.setCellValue(item.getVAN_H20() != null ? String.valueOf(item.getVAN_H20()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 22, 22);
            cell = sheet.getRow(index + 1)
                .getCell(22);
            cell.setCellValue(item.getVBN_H20() != null ? String.valueOf(item.getVBN_H20()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 22, 22);
            cell = sheet.getRow(index + 2)
                .getCell(22);
            cell.setCellValue(item.getVCN_H20() != null ? String.valueOf(item.getVBN_H20()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 22, 22);
            cell = sheet.getRow(index + 3)
                .getCell(22);
            cell.setCellValue(item.getIA_H20() != null ? String.valueOf(item.getIA_H20()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 22, 22);
            cell = sheet.getRow(index + 4)
                .getCell(22);
            cell.setCellValue(item.getIB_H20() != null ? String.valueOf(item.getIB_H20()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 22, 22);
            cell = sheet.getRow(index + 5)
                .getCell(22);
            cell.setCellValue(item.getIC_H20() != null ? String.valueOf(item.getIC_H20()) : "-");

            // H21
            region = new CellRangeAddress(index, index, 23, 23);
            cell = sheet.getRow(index)
                .getCell(23);
            cell.setCellValue(item.getVAN_H21() != null ? String.valueOf(item.getVAN_H21()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 23, 23);
            cell = sheet.getRow(index + 1)
                .getCell(23);
            cell.setCellValue(item.getVBN_H21() != null ? String.valueOf(item.getVBN_H21()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 23, 23);
            cell = sheet.getRow(index + 2)
                .getCell(23);
            cell.setCellValue(item.getVCN_H21() != null ? String.valueOf(item.getVBN_H21()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 23, 23);
            cell = sheet.getRow(index + 3)
                .getCell(23);
            cell.setCellValue(item.getIA_H21() != null ? String.valueOf(item.getIA_H21()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 23, 23);
            cell = sheet.getRow(index + 4)
                .getCell(23);
            cell.setCellValue(item.getIB_H21() != null ? String.valueOf(item.getIB_H21()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 23, 23);
            cell = sheet.getRow(index + 5)
                .getCell(23);
            cell.setCellValue(item.getIC_H21() != null ? String.valueOf(item.getIC_H21()) : "-");

            // H22
            region = new CellRangeAddress(index, index, 24, 24);
            cell = sheet.getRow(index)
                .getCell(24);
            cell.setCellValue(item.getVAN_H22() != null ? String.valueOf(item.getVAN_H22()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 24, 24);
            cell = sheet.getRow(index + 1)
                .getCell(24);
            cell.setCellValue(item.getVBN_H22() != null ? String.valueOf(item.getVBN_H22()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 24, 24);
            cell = sheet.getRow(index + 2)
                .getCell(24);
            cell.setCellValue(item.getVCN_H22() != null ? String.valueOf(item.getVBN_H22()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 24, 24);
            cell = sheet.getRow(index + 3)
                .getCell(24);
            cell.setCellValue(item.getIA_H22() != null ? String.valueOf(item.getIA_H22()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 24, 24);
            cell = sheet.getRow(index + 4)
                .getCell(24);
            cell.setCellValue(item.getIB_H22() != null ? String.valueOf(item.getIB_H22()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 24, 24);
            cell = sheet.getRow(index + 5)
                .getCell(24);
            cell.setCellValue(item.getIC_H22() != null ? String.valueOf(item.getIC_H22()) : "-");

            // H23
            region = new CellRangeAddress(index, index, 25, 25);
            cell = sheet.getRow(index)
                .getCell(25);
            cell.setCellValue(item.getVAN_H23() != null ? String.valueOf(item.getVAN_H23()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 25, 25);
            cell = sheet.getRow(index + 1)
                .getCell(25);
            cell.setCellValue(item.getVBN_H23() != null ? String.valueOf(item.getVBN_H23()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 25, 25);
            cell = sheet.getRow(index + 2)
                .getCell(25);
            cell.setCellValue(item.getVCN_H23() != null ? String.valueOf(item.getVBN_H23()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 25, 25);
            cell = sheet.getRow(index + 3)
                .getCell(25);
            cell.setCellValue(item.getIA_H23() != null ? String.valueOf(item.getIA_H23()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 25, 25);
            cell = sheet.getRow(index + 4)
                .getCell(25);
            cell.setCellValue(item.getIB_H23() != null ? String.valueOf(item.getIB_H23()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 25, 25);
            cell = sheet.getRow(index + 5)
                .getCell(25);
            cell.setCellValue(item.getIC_H23() != null ? String.valueOf(item.getIC_H23()) : "-");

            // H24
            region = new CellRangeAddress(index, index, 26, 26);
            cell = sheet.getRow(index)
                .getCell(26);
            cell.setCellValue(item.getVAN_H24() != null ? String.valueOf(item.getVAN_H24()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 26, 26);
            cell = sheet.getRow(index + 1)
                .getCell(26);
            cell.setCellValue(item.getVBN_H24() != null ? String.valueOf(item.getVBN_H24()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 26, 26);
            cell = sheet.getRow(index + 2)
                .getCell(26);
            cell.setCellValue(item.getVCN_H24() != null ? String.valueOf(item.getVBN_H24()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 26, 26);
            cell = sheet.getRow(index + 3)
                .getCell(26);
            cell.setCellValue(item.getIA_H24() != null ? String.valueOf(item.getIA_H24()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 26, 26);
            cell = sheet.getRow(index + 4)
                .getCell(26);
            cell.setCellValue(item.getIB_H24() != null ? String.valueOf(item.getIB_H24()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 26, 26);
            cell = sheet.getRow(index + 5)
                .getCell(26);
            cell.setCellValue(item.getIC_H24() != null ? String.valueOf(item.getIC_H24()) : "-");

            // H25
            region = new CellRangeAddress(index, index, 27, 27);
            cell = sheet.getRow(index)
                .getCell(27);
            cell.setCellValue(item.getVAN_H25() != null ? String.valueOf(item.getVAN_H25()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 27, 27);
            cell = sheet.getRow(index + 1)
                .getCell(27);
            cell.setCellValue(item.getVBN_H25() != null ? String.valueOf(item.getVBN_H25()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 27, 27);
            cell = sheet.getRow(index + 2)
                .getCell(27);
            cell.setCellValue(item.getVCN_H25() != null ? String.valueOf(item.getVBN_H25()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 27, 27);
            cell = sheet.getRow(index + 3)
                .getCell(27);
            cell.setCellValue(item.getIA_H25() != null ? String.valueOf(item.getIA_H25()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 27, 27);
            cell = sheet.getRow(index + 4)
                .getCell(27);
            cell.setCellValue(item.getIB_H25() != null ? String.valueOf(item.getIB_H25()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 27, 27);
            cell = sheet.getRow(index + 5)
                .getCell(27);
            cell.setCellValue(item.getIC_H25() != null ? String.valueOf(item.getIC_H25()) : "-");

            // H26
            region = new CellRangeAddress(index, index, 28, 28);
            cell = sheet.getRow(index)
                .getCell(28);
            cell.setCellValue(item.getVAN_H26() != null ? String.valueOf(item.getVAN_H26()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 28, 28);
            cell = sheet.getRow(index + 1)
                .getCell(28);
            cell.setCellValue(item.getVBN_H26() != null ? String.valueOf(item.getVBN_H26()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 28, 28);
            cell = sheet.getRow(index + 2)
                .getCell(28);
            cell.setCellValue(item.getVCN_H26() != null ? String.valueOf(item.getVBN_H26()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 28, 28);
            cell = sheet.getRow(index + 3)
                .getCell(28);
            cell.setCellValue(item.getIA_H26() != null ? String.valueOf(item.getIA_H26()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 28, 28);
            cell = sheet.getRow(index + 4)
                .getCell(28);
            cell.setCellValue(item.getIB_H26() != null ? String.valueOf(item.getIB_H26()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 28, 28);
            cell = sheet.getRow(index + 5)
                .getCell(28);
            cell.setCellValue(item.getIC_H26() != null ? String.valueOf(item.getIC_H26()) : "-");

            // H27
            region = new CellRangeAddress(index, index, 29, 29);
            cell = sheet.getRow(index)
                .getCell(29);
            cell.setCellValue(item.getVAN_H27() != null ? String.valueOf(item.getVAN_H27()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 29, 29);
            cell = sheet.getRow(index + 1)
                .getCell(29);
            cell.setCellValue(item.getVBN_H27() != null ? String.valueOf(item.getVBN_H27()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 29, 29);
            cell = sheet.getRow(index + 2)
                .getCell(29);
            cell.setCellValue(item.getVCN_H27() != null ? String.valueOf(item.getVBN_H27()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 29, 29);
            cell = sheet.getRow(index + 3)
                .getCell(29);
            cell.setCellValue(item.getIA_H27() != null ? String.valueOf(item.getIA_H27()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 29, 29);
            cell = sheet.getRow(index + 4)
                .getCell(29);
            cell.setCellValue(item.getIB_H27() != null ? String.valueOf(item.getIB_H27()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 29, 29);
            cell = sheet.getRow(index + 5)
                .getCell(29);
            cell.setCellValue(item.getIC_H27() != null ? String.valueOf(item.getIC_H27()) : "-");

            // H28
            region = new CellRangeAddress(index, index, 30, 30);
            cell = sheet.getRow(index)
                .getCell(30);
            cell.setCellValue(item.getVAN_H28() != null ? String.valueOf(item.getVAN_H28()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 30, 30);
            cell = sheet.getRow(index + 1)
                .getCell(30);
            cell.setCellValue(item.getVBN_H28() != null ? String.valueOf(item.getVBN_H28()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 30, 30);
            cell = sheet.getRow(index + 2)
                .getCell(30);
            cell.setCellValue(item.getVCN_H28() != null ? String.valueOf(item.getVBN_H28()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 30, 30);
            cell = sheet.getRow(index + 3)
                .getCell(30);
            cell.setCellValue(item.getIA_H28() != null ? String.valueOf(item.getIA_H28()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 30, 30);
            cell = sheet.getRow(index + 4)
                .getCell(30);
            cell.setCellValue(item.getIB_H28() != null ? String.valueOf(item.getIB_H28()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 30, 30);
            cell = sheet.getRow(index + 5)
                .getCell(30);
            cell.setCellValue(item.getIC_H28() != null ? String.valueOf(item.getIC_H28()) : "-");

            // H29
            region = new CellRangeAddress(index, index, 31, 31);
            cell = sheet.getRow(index)
                .getCell(31);
            cell.setCellValue(item.getVAN_H29() != null ? String.valueOf(item.getVAN_H29()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 31, 31);
            cell = sheet.getRow(index + 1)
                .getCell(31);
            cell.setCellValue(item.getVBN_H29() != null ? String.valueOf(item.getVBN_H29()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 31, 31);
            cell = sheet.getRow(index + 2)
                .getCell(31);
            cell.setCellValue(item.getVCN_H29() != null ? String.valueOf(item.getVBN_H29()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 31, 31);
            cell = sheet.getRow(index + 3)
                .getCell(31);
            cell.setCellValue(item.getIA_H29() != null ? String.valueOf(item.getIA_H29()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 31, 31);
            cell = sheet.getRow(index + 4)
                .getCell(31);
            cell.setCellValue(item.getIB_H29() != null ? String.valueOf(item.getIB_H29()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 31, 31);
            cell = sheet.getRow(index + 5)
                .getCell(31);
            cell.setCellValue(item.getIC_H29() != null ? String.valueOf(item.getIC_H29()) : "-");

            // H30
            region = new CellRangeAddress(index, index, 32, 32);
            cell = sheet.getRow(index)
                .getCell(32);
            cell.setCellValue(item.getVAN_H30() != null ? String.valueOf(item.getVAN_H30()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 32, 32);
            cell = sheet.getRow(index + 1)
                .getCell(32);
            cell.setCellValue(item.getVBN_H30() != null ? String.valueOf(item.getVBN_H30()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 32, 32);
            cell = sheet.getRow(index + 2)
                .getCell(32);
            cell.setCellValue(item.getVCN_H30() != null ? String.valueOf(item.getVBN_H30()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 32, 32);
            cell = sheet.getRow(index + 3)
                .getCell(32);
            cell.setCellValue(item.getIA_H30() != null ? String.valueOf(item.getIA_H30()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 32, 32);
            cell = sheet.getRow(index + 4)
                .getCell(32);
            cell.setCellValue(item.getIB_H30() != null ? String.valueOf(item.getIB_H30()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 32, 32);
            cell = sheet.getRow(index + 5)
                .getCell(32);
            cell.setCellValue(item.getIC_H30() != null ? String.valueOf(item.getIC_H30()) : "-");

            // H31
            region = new CellRangeAddress(index, index, 33, 33);
            cell = sheet.getRow(index)
                .getCell(33);
            cell.setCellValue(item.getVAN_H31() != null ? String.valueOf(item.getVAN_H31()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 33, 33);
            cell = sheet.getRow(index + 1)
                .getCell(33);
            cell.setCellValue(item.getVBN_H31() != null ? String.valueOf(item.getVBN_H31()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 33, 33);
            cell = sheet.getRow(index + 2)
                .getCell(33);
            cell.setCellValue(item.getVCN_H31() != null ? String.valueOf(item.getVBN_H31()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 33, 33);
            cell = sheet.getRow(index + 3)
                .getCell(33);
            cell.setCellValue(item.getIA_H31() != null ? String.valueOf(item.getIA_H31()) : "-");

            region = new CellRangeAddress(index + 4, index + 4, 33, 33);
            cell = sheet.getRow(index + 4)
                .getCell(33);
            cell.setCellValue(item.getIB_H31() != null ? String.valueOf(item.getIB_H31()) : "-");

            region = new CellRangeAddress(index + 5, index + 5, 33, 33);
            cell = sheet.getRow(index + 5)
                .getCell(33);
            cell.setCellValue(item.getIC_H31() != null ? String.valueOf(item.getIC_H31()) : "-");

            index += 6;
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

    @PostMapping ("/chart-harmonic/{customerId}/{deviceId}")
    public ResponseEntity<?> getDataPowerResponse(@PathVariable final Integer customerId,
        @PathVariable final String deviceId, @RequestBody final HarmonicForm harmonicForm) throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        Integer chartViewPointInt = Integer.parseInt(harmonicForm.getChartViewPoint());
        int year = Calendar.getInstance()
            .get(Calendar.YEAR);
        Map<String, String> condition = new HashMap<>();
        condition.put("deviceId", deviceId);
        condition.put(SCHEMA, Schema.getSchemas(customerId));
        DataLoadFrame2 dataHarmonic = new DataLoadFrame2();
        while (year >= 2022) {
            condition.put("year", String.valueOf(year));
            dataHarmonic = operationInfoService.getDataHarmonic(condition);
            if (dataHarmonic != null) {
                break;
            } else {
                year--;
            }
        }

        Map<String, Object> dataChart = new HashMap<>();

        List<DataPowerUResponse> dataChartU = getDataHarmonicUChart(dataHarmonic, chartViewPointInt,
            harmonicForm.getChartChanelU());

        List<DataPowerIResponse> dataChartI = getDataHarmonicIC_Hart(dataHarmonic, chartViewPointInt,
            harmonicForm.getChartChanelI());

        dataChart.put("dataChartU", dataChartU);
        dataChart.put("dataChartI", dataChartI);
        if (dataHarmonic != null) {
            dataChart.put("time", dataHarmonic.getSentDate());
        }
        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + "END");
        return new ResponseEntity<Object>(dataChart, HttpStatus.OK);
    }

    /**
     * Lấy dữ liệu sóng hài tại thời điểm
     *
     * @param deviceId Mã thiết bị
     * @param harmonicForm Điều kiện hiển thị
     * @param day Ngày truy vấn
     * @return Dữ liệu sóng hài
     * @throws Exception
     */
    @PostMapping ("/chart-harmonic/day/{customerId}/{deviceId}")
    public ResponseEntity<?> getDataHarmonicByDay(@PathVariable Integer customerId, @PathVariable final String deviceId,
        @RequestBody final HarmonicForm harmonicForm, @RequestParam final String day) throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        Integer chartViewPointInt = Integer.parseInt(harmonicForm.getChartViewPoint());

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        condition.put(SCHEMA, Schema.getSchemas(customerId));

        if (day != null) {
            String year = day.split("-")[0];
            condition.put(FROM_DATE, day.concat(":00"));
            condition.put(TO_DATE, day.concat(":59"));
            condition.put("year", year);
        }

        DataLoadFrame2 dataHarmonic = operationInfoService.getDataHarmonicByDay(condition);

        List<DataPowerUResponse> dataChartU = getDataHarmonicUChart(dataHarmonic, chartViewPointInt,
            harmonicForm.getChartChanelU());

        List<DataPowerIResponse> dataChartI = getDataHarmonicIC_Hart(dataHarmonic, chartViewPointInt,
            harmonicForm.getChartChanelI());

        Map<String, Object> dataChart = new HashMap<>();
        dataChart.put("dataChartU", dataChartU);
        dataChart.put("dataChartI", dataChartI);

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + "END");

        return new ResponseEntity<Map<String, Object>>(dataChart, HttpStatus.OK);
    }

    /**
     * Lấy dữ liệu sóng hài theo giai đoạn
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thúc truy vấn
     * @return Danh sách dữ liệu sóng hài
     */
    @GetMapping ("/chart-harmonic/period/{customerId}/{deviceId}")
    public ResponseEntity<List<DataHarmonicPeriod>> getDataHarmonicPeriod(@PathVariable final Integer customerId,
        @PathVariable final String deviceId, @RequestParam final String fromDate, @RequestParam final String toDate) {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");
        int[] duration = new int[2];

        long from = DateUtils.toDate(fromDate + " 00:00:00", Constants.ES.DATETIME_FORMAT_YMDHMS)
            .getTime() / 1000;
        long to = DateUtils.toDate(toDate + " 23:59:59", Constants.ES.DATETIME_FORMAT_YMDHMS)
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

        List<DataHarmonicPeriod> data = new ArrayList<>();
        Map<String, DataHarmonicPeriod> mapData = new HashMap<>();
        for (int i = duration[0]; i <= duration[1]; i++) {
            Map<String, Object> condition = new HashMap<>();
            String tableName = Schema.getSchemas(customerId)
                + Constants.DATA.DATA_TABLES.get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.FRAME1));
            condition.put("tableName", tableName);
            condition.put(DEVICE_ID, deviceId);
            if (fromDate != null && toDate != null) {
                condition.put(FROM_DATE, fromDate.concat(TIME_START));
                condition.put(TO_DATE, toDate.concat(TIME_END));
            }
            condition.put(SCHEMA, Schema.getSchemas(customerId));
            condition.put(SORT, SORT_ASC);
            List<DataLoadFrame1> frame1s = operationInfoService.getHarmonicPeriod(condition);
            if (frame1s.size() > 0) {
                frame1s.forEach(f -> {
                    DataHarmonicPeriod hp = null;
                    try {
                        hp = new DataHarmonicPeriod(f);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mapData.get(hp.getSentDate()) != null) {
                        if (mapData.get(hp.getSentDate())
                            .getSentDate()
                            .equals(hp.getSentDate())
                            && mapData.get(hp.getSentDate())
                                .getTransactionDate() < hp.getTransactionDate()) {
                            mapData.put(hp.getSentDate(), hp);
                        }
                    } else {
                        mapData.put(hp.getSentDate(), hp);
                    }
                });
                frame1s.clear();
            }
        }
        data = new ArrayList<DataHarmonicPeriod>(mapData.values());
        data.sort(Comparator.comparingLong(DataHarmonicPeriod::getTransactionDate));

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " END");

        return new ResponseEntity<List<DataHarmonicPeriod>>(data, HttpStatus.OK);

    }

    private List<DataPowerUResponse> getDataHarmonicUChart(DataLoadFrame2 dataHarmonic, Integer chartViewPointInt,
        String[] chanelU) throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        DataPowerUResponse dataJson = new DataPowerUResponse();
        List<DataPowerUResponse> dataUChart = new ArrayList<>();
        for (int i = 1; i <= chartViewPointInt; i++) {
            if (chanelU != null && chanelU.length > 0) {
                dataJson = new DataPowerUResponse();
                for (int j = 0; j < chanelU.length; j++) {
                    if (StringUtils.equals(chanelU[j], "1")) {
                        Integer Vabh = 0;
                        if (dataHarmonic != null && dataHarmonic.getClass()
                            .getDeclaredMethod("getVabH" + i)
                            .invoke(dataHarmonic) != null) {
                            Vabh = (Integer) dataHarmonic.getClass()
                                .getDeclaredMethod("getVabH" + i)
                                .invoke(dataHarmonic);
                        }
                        dataJson.setVabH(Vabh);
                    } else if (StringUtils.equals(chanelU[j], "2")) {
                        Integer Vbch = 0;
                        if (dataHarmonic != null && dataHarmonic.getClass()
                            .getDeclaredMethod("getVbcH" + i)
                            .invoke(dataHarmonic) != null) {
                            Vbch = (Integer) dataHarmonic.getClass()
                                .getDeclaredMethod("getVbcH" + i)
                                .invoke(dataHarmonic);
                        }
                        dataJson.setVbcH(Vbch);
                    } else if (StringUtils.equals(chanelU[j], "3")) {
                        Integer Vcah = 0;
                        if (dataHarmonic != null && dataHarmonic.getClass()
                            .getDeclaredMethod("getVcaH" + i)
                            .invoke(dataHarmonic) != null) {
                            Vcah = (Integer) dataHarmonic.getClass()
                                .getDeclaredMethod("getVcaH" + i)
                                .invoke(dataHarmonic);
                        }
                        dataJson.setVcaH(Vcah);
                    }
                }
                dataJson.setHarmonicsNo(ordinal(i));
                dataUChart.add(dataJson);
            }

        }
        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + "END");

        return dataUChart;
    }

    private List<DataPowerIResponse> getDataHarmonicIC_Hart(DataLoadFrame2 dataHarmonic, Integer chartViewPointInt,
        String[] chanelI) throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        DataPowerIResponse dataJson = new DataPowerIResponse();
        List<DataPowerIResponse> dataIC_Hart = new ArrayList<>();
        for (int i = 1; i <= chartViewPointInt; i++) {
            if (chanelI != null && chanelI.length > 0) {
                dataJson = new DataPowerIResponse();
                for (int j = 0; j < chanelI.length; j++) {
                    if (StringUtils.equals(chanelI[j], "1")) {
                        Integer IA_H = 0;
                        if (dataHarmonic != null && dataHarmonic.getClass()
                            .getDeclaredMethod("getIA_H" + i)
                            .invoke(dataHarmonic) != null) {
                            IA_H = (Integer) dataHarmonic.getClass()
                                .getDeclaredMethod("getIA_H" + i)
                                .invoke(dataHarmonic);
                        }
                        dataJson.setIaH(IA_H);
                    } else if (StringUtils.equals(chanelI[j], "2")) {
                        Integer IB_H = 0;
                        if (dataHarmonic != null && dataHarmonic.getClass()
                            .getDeclaredMethod("getIB_H" + i)
                            .invoke(dataHarmonic) != null) {
                            IB_H = (Integer) dataHarmonic.getClass()
                                .getDeclaredMethod("getIB_H" + i)
                                .invoke(dataHarmonic);
                        }
                        dataJson.setIbH(IB_H);
                    } else if (StringUtils.equals(chanelI[j], "3")) {
                        Integer IC_H = 0;
                        if (dataHarmonic != null && dataHarmonic.getClass()
                            .getDeclaredMethod("getIC_H" + i)
                            .invoke(dataHarmonic) != null) {
                            IC_H = (Integer) dataHarmonic.getClass()
                                .getDeclaredMethod("getIC_H" + i)
                                .invoke(dataHarmonic);
                        }
                        dataJson.setIcH(IC_H);
                    }
                }

                dataJson.setHarmonicsNo(ordinal(i));
                dataIC_Hart.add(dataJson);
            }

        }

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + "END");

        return dataIC_Hart;
    }

    private String ordinal(final Integer n) {

        String[] s = {"th", "st", "nd", "rd"};
        int v = n % 100;
        if ( (v - 20) % 10 >= 0 && (v - 20) % 10 <= 3) {

            return String.valueOf(n) + s[ (v - 20) % 10];
        } else if (v >= 0 && v <= 3) {

            return String.valueOf(n) + s[v];
        } else {

            return String.valueOf(n) + s[0];
        }
    }
}
