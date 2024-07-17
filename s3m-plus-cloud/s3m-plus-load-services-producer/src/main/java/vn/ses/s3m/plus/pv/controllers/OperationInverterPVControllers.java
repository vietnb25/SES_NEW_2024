package vn.ses.s3m.plus.pv.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
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
import vn.ses.s3m.plus.dto.DataInverter1;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.pv.response.ChartOperationInverterResponse;
import vn.ses.s3m.plus.pv.response.DataPQSPVResponse;
import vn.ses.s3m.plus.pv.response.OperationInverterResponse;
import vn.ses.s3m.plus.pv.response.OperationSettingInverterResponse;
import vn.ses.s3m.plus.pv.service.OperationInverterPVService;
import vn.ses.s3m.plus.service.DeviceService;

@RestController
@Slf4j
@RequestMapping ("/pv/operation")
public class OperationInverterPVControllers {

    @Autowired
    private OperationInverterPVService operationPVService;

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

    private static final String SORT_ASC = "ASC";

    private static final String SORT_DESC = "DESC";

    private static final String PAGE_START = "start";

    private static final String PAGE_END = "end";

    private static final String TOTAL_PAGE_STR = "totalPage";

    private static final String CURRENT_PAGE_STR = "currentPage";

    private static final String TOTAL_DATA_STR = "totalData";

    private static final String DATA = "data";

    private static final Integer PQS_VIEW_DAY = 1;

    private static final Integer PQS_VIEW_MONTH = 2;

    private static final Integer PQS_VIEW_YEAR = 3;

    private static final String SCHEMA = "schema";

    /**
     * Lấy thông tin thông số điện tức thời PV
     *
     * @param deviceId Mã thiết bị
     * @return Thông tin thông số điện tức thời
     */
    @GetMapping ("/instant/inverter/{customerId}/{deviceId}")
    public ResponseEntity<OperationInverterResponse> getInstantOperationInverterPV(
        @PathVariable final Integer customerId, @PathVariable final Long deviceId) {

        log.info("getInstantOperationInverterPV START");

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        condition.put(SCHEMA, Schema.getSchemas(customerId));

        DataInverter1 inverter = operationPVService.getInstantOperationInverterPV(condition);

        if (inverter != null) {
            OperationInverterResponse data = new OperationInverterResponse(inverter);

            log.info("getInstantOperationInverterPV END");

            return new ResponseEntity<OperationInverterResponse>(data, HttpStatus.OK);
        } else {
            log.info("getInstantOperationInverterPV END");

            return new ResponseEntity<OperationInverterResponse>(HttpStatus.OK);
        }

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
    @GetMapping ("/inverter/{customerId}/{deviceId}/{page}")
    public ResponseEntity<?> getOperationInverterPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @PathVariable final Integer page) {

        log.info("getOperationInverterPV START");

        List<OperationInverterResponse> data = new ArrayList<>();

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

        List<DataInverter1> dataInfo = new ArrayList<>();

        Map<String, Object> condition = new HashMap<>();
        int pageSize = PAGE_SIZE;
        int totalData = 0;
        int pageTable = page;
        String soft = SORT_DESC;
        for (int i = duration[1]; i >= duration[0]; i--) {
            String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.INVERTER1));
            System.out.println(schema);
            condition.put(SCHEMA, schema);
            condition.put(DEVICE_ID, deviceId);
            condition.put(FROM_DATE, fromDate.concat(TIME_START));
            condition.put(TO_DATE, toDate.concat(TIME_END));
            condition.put(SCHEMA, schema);
            condition.put(SORT, soft);
            condition.put(PAGE_START, (pageTable - 1) * PAGE_SIZE);
            condition.put(PAGE_END, pageSize);

            totalData = operationPVService.countDataOperationInverterPV(condition);

            List<DataInverter1> inverter1s = operationPVService.getOperationInverterPV(condition);

            if (inverter1s.size() <= PAGE_SIZE) {
                pageSize = PAGE_SIZE - inverter1s.size();
                pageTable = 1;
                soft = SORT_ASC;
            } else {
                pageSize = PAGE_SIZE;
                pageTable = page;
                soft = SORT_DESC;
            }
            dataInfo.addAll(inverter1s);
        }

        double totalPage = Math.ceil((double) totalData / PAGE_SIZE);
        // object to response to client
        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put(TOTAL_PAGE_STR, totalPage);
        dataMap.put(CURRENT_PAGE_STR, page);
        dataMap.put(TOTAL_DATA_STR, totalData);

        if (dataInfo.size() > 0) {
            dataInfo.forEach(i -> {
                OperationInverterResponse res = new OperationInverterResponse(i);
                data.add(res);
            });
            dataMap.put(DATA, dataInfo);

            log.info("getOperationInverterPV END");

            return new ResponseEntity<>(dataMap, HttpStatus.OK);
        } else {

            log.info("getOperationInverterPV END");
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * Lấy thông tin dữ liệu biểu đồ Inverter
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức tuy vấn
     * @return Danh sách dữ liệu biểu đồ điện áp
     */
    @GetMapping ("/chart/inverter/{customerId}/{deviceId}")
    public ResponseEntity<?> getChartInverterPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate) {

        log.info("getChartInverterPV START");

        List<ChartOperationInverterResponse> data = new ArrayList<>();

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

        List<DataInverter1> dataInfo = new ArrayList<>();

        Map<String, Object> condition = new HashMap<>();
        for (int i = duration[0]; i <= duration[1]; i++) {
            String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.INVERTER1));
            condition.put(DEVICE_ID, deviceId);
            condition.put(FROM_DATE, fromDate.concat(TIME_START));
            condition.put(TO_DATE, toDate.concat(TIME_END));
            condition.put(SCHEMA, schema);
            List<DataInverter1> inverter1s = operationPVService.getOperationInverterPV(condition);
            dataInfo.addAll(inverter1s);
        }

        if (dataInfo.size() > 0) {
            dataInfo.forEach(i -> {
                ChartOperationInverterResponse res = new ChartOperationInverterResponse(i);
                data.add(res);
            });

            log.info("getChartInverterPV END");

            return new ResponseEntity<>(data, HttpStatus.OK);
        } else {

            log.info("getChartInverterPV END");
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * Lấy thông số cài đặt thiết bị Inverter
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức tuy vấn
     * @return Danh sách dữ liệu biểu đồ công suất
     */
    @GetMapping ("/setting/inverter/{customerId}/{deviceId}")
    public ResponseEntity<?> getOperationSettingInverter(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId) {

        log.info("getOperationSettingInverter START");

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        condition.put(SCHEMA, Schema.getSchemas(customerId));
        DataInverter1 inverter1 = operationPVService.getOperationSettingInverter(condition);

        if (inverter1 != null) {
            OperationSettingInverterResponse data = new OperationSettingInverterResponse(inverter1);

            log.info("getOperationSettingInverter END");

            return new ResponseEntity<>(data, HttpStatus.OK);
        } else {

            log.info("getOperationSettingInverter END");

            return new ResponseEntity<>(HttpStatus.OK);
        }

    }

    /**
     * Lấy thông tin dữ liệu biểu đồ điện năng
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức tuy vấn
     * @return Danh sách dữ liệu biểu đồ công suất
     */
    @GetMapping ("/chart/electrical-power/inverter/{customerId}/{deviceId}")
    public ResponseEntity<?> getChartElectricalPowerInverter(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String date, @RequestParam final Integer type) {

        log.info("getChartElectricalPowerInverter START");

        List<DataPQSPVResponse> data = new ArrayList<>();

        String[] hours = new String[] {"00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00",
            "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00",
            "21:00", "22:00", "23:00"};

        String[] tLow = new String[] {"00:00 ~ 01:00", "01:00 ~ 02:00", "02:00 ~ 03:00", "03:00 ~ 04:00",
            "22:00 ~ 23:00", "23:00 ~ 23:59"};

        String[] tNormal = new String[] {"04:00 ~ 05:00", "05:00 ~ 06:00", "06:00 ~ 07:00", "07:00 ~ 08:00",
            "08:00 ~ 09:00", "12:00 ~ 13:00", "13:00 ~ 14:00", "14:00 ~ 15:00", "15:00 ~ 16:00", "16:00 ~ 17:00",
            "20:00 ~ 21:00", "21:00 ~ 22:00"};

        String[] tHigh = new String[] {"09:00 ~ 10:00", "10:00 ~ 11:00", "11:00 ~ 12:00", "17:00 ~ 18:00",
            "18:00 ~ 19:00", "19:00 ~ 20:00"};

        // Dữ liệu biểu đồ điện năng trong ngày
        if (type == PQS_VIEW_DAY) {
            List<DataPQSPVResponse> dataPQS = getDataPQSByDay(hours, tLow, tNormal, tHigh, date, deviceId, customerId);
            data.addAll(dataPQS);
        }

        if (type == PQS_VIEW_MONTH) {

            data = getDataPQSByMonth(customerId, deviceId, date);

        }

        if (type == PQS_VIEW_YEAR) {
            data = getDataPQSByYear(customerId, deviceId, date);
        }

        log.info("getChartElectricalPowerInverter END");

        return new ResponseEntity<List<DataPQSPVResponse>>(data, HttpStatus.OK);
    }

    /**
     * Lấy dữ liệu điện năng theo ngày
     *
     * @param hours Danh sách giờ trong ngày
     * @param tLow Danh sách khoảng thời gian thấp điểm
     * @param tNormal Danh sách khoảng thời gian bìn thường
     * @param tHigh Danh sách khoảng thời gian cao điểm
     * @param date Thời gian truy vấn dữ liệu
     * @param deviceId Mã thiết bị
     * @return Danh sách dữ liệu điện năng trong ngày
     */
    private List<DataPQSPVResponse> getDataPQSByDay(final String[] hours, final String[] tLow, final String[] tNormal,
        final String[] tHigh, final String date, final Long deviceId, final Integer customerId) {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        List<DataPQSPVResponse> data = new ArrayList<>();

        for (int i = 0; i < hours.length; i++) {

            Map<String, Object> map = new HashMap<>();
            String t0 = date + " " + hours[i] + ":00";
            String t1;
            if (i == hours.length - 1 || String.valueOf(hours[i]) == "23:00") {
                t1 = date + " 23:59:00";
            } else {
                t1 = date + " " + hours[i + 1] + ":00";
            }
            map.put(DEVICE_ID, deviceId);
            map.put(FROM_DATE, t0);
            map.put(TO_DATE, t1);
            map.put(SORT, SORT_DESC);

            int tableIndex = CommonUtils.calculateDataIndex(date);
            String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                .get(new MultiKey(Constants.DATA.tables[tableIndex], Constants.DATA.MESSAGE.INVERTER1));
            map.put(SCHEMA, schema);

            List<DataInverter1> framePQS = operationPVService.getOperationInverterPV(map);

            Float ep;
            if (framePQS.size() == 0) {
                ep = (float) 0;
            } else {
                Float toEp = framePQS.get(0)
                    .getEp();
                Float fromEp = framePQS.get(framePQS.size() - 1)
                    .getEp();
                ep = toEp - fromEp;
            }

            String time;
            if (i == hours.length - 1 || String.valueOf(hours[i]) == "23:00") {
                time = hours[i] + " ~ " + "23:59";
            } else {
                time = hours[i] + " ~ " + hours[i + 1];
            }

            DataPQSPVResponse pqsRes = new DataPQSPVResponse();
            if (Arrays.stream(tLow)
                .anyMatch(time::equals)) {
                pqsRes.setHigh((double) 0);
                pqsRes.setLow((double) (ep > 0 ? ep : 0));
                pqsRes.setNormal((double) 0);
            }
            if (Arrays.stream(tNormal)
                .anyMatch(time::equals)) {
                pqsRes.setHigh((double) 0);
                pqsRes.setLow((double) 0);
                pqsRes.setNormal((double) (ep > 0 ? ep : 0));
            }
            if (Arrays.stream(tHigh)
                .anyMatch(time::equals)) {
                pqsRes.setHigh((double) (ep > 0 ? ep : 0));
                pqsRes.setLow((double) 0);
                pqsRes.setNormal((double) 0);
            }
            pqsRes.setTotal(pqsRes.getHigh() + pqsRes.getLow() + pqsRes.getNormal());
            pqsRes.setParam((double) 0);
            pqsRes.setSentDate(time);

            data.add(pqsRes);
        }

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName());

        return data;
    }

    /**
     * Dowload thông số Inverter PV.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/device-parameter/inverter/{customerId}/{deviceId}")
    public ResponseEntity<Resource> downloadDeviceParameterInverter(@PathVariable final Integer customerId,
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

        List<DataInverter1> data = new ArrayList<>();

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
        for (int i = duration[0]; i <= duration[1]; i++) {
            String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.INVERTER1));
            condition.put(DEVICE_ID, deviceId);
            condition.put(FROM_DATE, fromDate.concat(TIME_START));
            condition.put(TO_DATE, toDate.concat(TIME_END));
            condition.put(SCHEMA, schema);
            List<DataInverter1> inverter1s = operationPVService.getOperationInverterPV(condition);
            data.addAll(inverter1s);
        }

        if (data.size() > 0) {
            // time miliseconds
            long miliseconds = new Date().getTime();

            Device device = deviceService.getDeviceByDeviceId(condition);

            // path folder
            String path = this.folderName + File.separator + miliseconds;

            // tạo excel
            createDeviceParameterInverterExcel(data, fromDate, toDate, device, path, imageData, miliseconds);

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
     * Dowload Dữ liệu biểu đồ Inverter PV.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/chart/inverter/{customerId}/{deviceId}/{type}")
    public ResponseEntity<Resource> downloadChartInverter(@PathVariable final Integer customerId,
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

        if (type == Constants.DATA.CHART_PV.DONG_DIEN) {

            List<DataInverter1> data = new ArrayList<>();

            for (int i = duration[0]; i <= duration[1]; i++) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.INVERTER1));
                condition.put(FROM_DATE, fromDate.concat(TIME_START));
                condition.put(TO_DATE, toDate.concat(TIME_END));
                condition.put(SCHEMA, schema);
                List<DataInverter1> inverter1s = operationPVService.getOperationInverterPV(condition);
                data.addAll(inverter1s);
            }

            // tạo excel
            createChartElectricInverterExcel(data, fromDate, toDate, device, path, imageData, miliseconds);
        }

        if (type == Constants.DATA.CHART_PV.DIEN_AP) {

            List<DataInverter1> data = new ArrayList<>();

            for (int i = duration[0]; i <= duration[1]; i++) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.INVERTER1));
                condition.put(FROM_DATE, fromDate.concat(TIME_START));
                condition.put(TO_DATE, toDate.concat(TIME_END));
                condition.put(SCHEMA, schema);
                List<DataInverter1> inverter1s = operationPVService.getOperationInverterPV(condition);
                data.addAll(inverter1s);
            }

            // tạo excel
            createChartVoltageInverterExcel(data, fromDate, toDate, device, path, imageData, miliseconds);
        }

        if (type == Constants.DATA.CHART_PV.CONG_SUAT_TAC_DUNG) {

            List<DataInverter1> data = new ArrayList<>();

            for (int i = duration[0]; i <= duration[1]; i++) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.INVERTER1));
                condition.put(FROM_DATE, fromDate.concat(TIME_START));
                condition.put(TO_DATE, toDate.concat(TIME_END));
                condition.put(SCHEMA, schema);
                List<DataInverter1> inverter1s = operationPVService.getOperationInverterPV(condition);
                data.addAll(inverter1s);
            }

            // tạo excel
            createChartEffectivePowerInverterExcel(data, fromDate, toDate, device, path, imageData, miliseconds);
        }

        if (type == Constants.DATA.CHART_PV.NHIET_DO) {

            List<DataInverter1> data = new ArrayList<>();

            for (int i = duration[0]; i <= duration[1]; i++) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.INVERTER1));
                condition.put(FROM_DATE, fromDate.concat(TIME_START));
                condition.put(TO_DATE, toDate.concat(TIME_END));
                condition.put(SCHEMA, schema);
                List<DataInverter1> inverter1s = operationPVService.getOperationInverterPV(condition);
                data.addAll(inverter1s);
            }

            // tạo excel
            createChartTemperatureInverterExcel(data, fromDate, toDate, device, path, imageData, miliseconds);
        }

        if (type == Constants.DATA.CHART_PV.HIEU_SUAT) {

            List<DataInverter1> data = new ArrayList<>();

            for (int i = duration[0]; i <= duration[1]; i++) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.INVERTER1));
                condition.put(FROM_DATE, fromDate.concat(TIME_START));
                condition.put(TO_DATE, toDate.concat(TIME_END));
                condition.put(SCHEMA, schema);
                List<DataInverter1> inverter1s = operationPVService.getOperationInverterPV(condition);
                data.addAll(inverter1s);
            }

            // tạo excel
            createChartEfficiencyInverterExcel(data, fromDate, toDate, device, path, imageData, miliseconds);
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
     * Dowload Dữ liệu biểu đồ điện năng Inverter PV.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/chart/inverter/electrical-power/{customerId}/{deviceId}/{type}")
    public ResponseEntity<Resource> downloadChartElectricalPowerInverter(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String date, @PathVariable final Integer type,
        @RequestParam final String userName) throws Exception {

        log.info("downloadChartElectricalPowerInverter START");

        // get url image
        User user = userMapper.getUserByUsername(userName);
        String pngImageURL = user.getImg();
        String encodingPrefix = "base64,";
        int contentStartIndex = pngImageURL.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData = org.apache.commons.codec.binary.Base64
            .decodeBase64(pngImageURL.substring(contentStartIndex));

        // time miliseconds
        long miliseconds = new Date().getTime();

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);

        Device device = deviceService.getDeviceByDeviceId(condition);

        // path folder
        String path = this.folderName + File.separator + miliseconds;

        // gửi zip qua client
        String contentType = "application/zip";
        String headerValue = "attachment; filename=" + miliseconds + ".zip";

        List<DataPQSPVResponse> data = new ArrayList<>();

        String[] hours = new String[] {"00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00",
            "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00",
            "21:00", "22:00", "23:00"};

        String[] tLow = new String[] {"00:00 ~ 01:00", "01:00 ~ 02:00", "02:00 ~ 03:00", "03:00 ~ 04:00",
            "22:00 ~ 23:00", "23:00 ~ 23:59"};

        String[] tNormal = new String[] {"04:00 ~ 05:00", "05:00 ~ 06:00", "06:00 ~ 07:00", "07:00 ~ 08:00",
            "08:00 ~ 09:00", "12:00 ~ 13:00", "13:00 ~ 14:00", "14:00 ~ 15:00", "15:00 ~ 16:00", "16:00 ~ 17:00",
            "20:00 ~ 21:00", "21:00 ~ 22:00"};

        String[] tHigh = new String[] {"09:00 ~ 10:00", "10:00 ~ 11:00", "11:00 ~ 12:00", "17:00 ~ 18:00",
            "18:00 ~ 19:00", "19:00 ~ 20:00"};

        // Dữ liệu biểu đồ điện năng trong ngày
        if (type == PQS_VIEW_DAY) {
            List<DataPQSPVResponse> dataPQS = getDataPQSByDay(hours, tLow, tNormal, tHigh, date, deviceId, customerId);
            data.addAll(dataPQS);

            createPQSExcel(data, date, device, path, imageData, miliseconds);
        }

        if (type == PQS_VIEW_MONTH) {

            List<DataPQSPVResponse> dataPQS = getDataPQSByMonth(customerId, deviceId, date);

            createPQSExcel(dataPQS, date, device, path, imageData, miliseconds);
        }

        if (type == PQS_VIEW_YEAR) {

            List<DataPQSPVResponse> dataPQS = getDataPQSByYear(customerId, deviceId, date);

            createPQSExcel(dataPQS, date, device, path, imageData, miliseconds);
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
     * Tạo excel biểu đồ điện áp.
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"static-access", "unused"})
    private void createChartEffectivePowerInverterExcel(final List<DataInverter1> data, final String fromDate,
        final String toDate, final Device device, final String path, final byte[] imageData, final long miliseconds)
        throws Exception {

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
        cell.setCellValue("BÁO CÁO CÔNG SUẤT TÁC DỤNG INVER");
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
        cell.setCellValue("Công suất tác dụng [W]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataInverter1 item = data.get(m);
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
            cell.setCellValue(item.getEp() != null ? String.valueOf(item.getEp()) : "-");

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
     * Tạo excel biểu đồ điện áp.
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"unused", "static-access"})
    private void createChartVoltageInverterExcel(final List<DataInverter1> data, final String fromDate,
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
        cell.setCellValue("BÁO CÁO ĐIỆN ÁP INVERTER");
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

        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataInverter1 item = data.get(m);
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
            cell.setCellValue(item.getIa() != null ? String.valueOf(item.getVa()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 3, 3);
            cell = sheet.getRow(index + 1)
                .getCell(3);
            cell.setCellValue(item.getIb() != null ? String.valueOf(item.getVb()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 3, 3);
            cell = sheet.getRow(index + 2)
                .getCell(3);
            cell.setCellValue(item.getIc() != null ? String.valueOf(item.getVc()) : "-");

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
     * Tạo excel thông tin dòng điện.
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"static-access", "unused"})
    private void createChartElectricInverterExcel(final List<DataInverter1> data, final String fromDate,
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
        cell.setCellValue("BÁO CÁO DÒNG ĐIỆN INVERTER");
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

        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataInverter1 item = data.get(m);
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
            cell.setCellValue(item.getVab() != null ? String.valueOf(item.getIa()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 3, 3);
            cell = sheet.getRow(index + 1)
                .getCell(3);
            cell.setCellValue(item.getVbc() != null ? String.valueOf(item.getIb()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 3, 3);
            cell = sheet.getRow(index + 2)
                .getCell(3);
            cell.setCellValue(item.getVca() != null ? String.valueOf(item.getIc()) : "-");

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
     * Tạo excel thông số Inverter PV.
     *
     * @param data Danh sách dữ liệu thông số Inverter PV.
     * @throws Exception
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"static-access", "unused"})
    private void createDeviceParameterInverterExcel(final List<DataInverter1> data, final String fromDate,
        final String toDate, final Device device, final String path, final byte[] imageData, final long miliseconds)
        throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(data.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Thông số Inverter");
        Row row;
        Cell cell;

        // add image
        int pictureIdx = wb.addPicture(imageData, wb.PICTURE_TYPE_PNG);
        SXSSFDrawing drawingImg = sheet.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(11);
        anchorImg.setCol2(12);
        anchorImg.setRow1(1);
        anchorImg.setRow2(4);

        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);

        // Page margins
        sheet.setMargin(Sheet.RightMargin, 0.5);
        sheet.setMargin(Sheet.LeftMargin, 0.5);
        sheet.setMargin(Sheet.TopMargin, 0.5);
        sheet.setMargin(Sheet.BottomMargin, 0.5);
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

        // set độ rộng của cột
        sheet.setColumnWidth(0, 1300);
        sheet.setColumnWidth(1, 5200);
        sheet.setColumnWidth(3, 5000);
        sheet.setColumnWidth(4, 5000);
        sheet.setColumnWidth(7, 5000);
        sheet.setColumnWidth(8, 5000);
        sheet.setColumnWidth(11, 5000);

        // set độ rộng của hàng
        Row row1 = sheet.getRow(1);
        row1.setHeight((short) -500);
        Row row2 = sheet.getRow(2);
        row2.setHeight((short) -500);
        Row row3 = sheet.getRow(3);
        row3.setHeight((short) -500);

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 11);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(0)
            .getCell(0);
        cell.setCellValue("BÁO CÁO VẬN HÀNH INVERTER");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(0);
        cell.setCellValue("Mã thiết bị");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(2, 2, 2, 6);
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

        region = new CellRangeAddress(3, 3, 2, 6);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(2);
        cell.setCellValue(device.getDeviceName() != null
            ? device.getDeviceName()
                .toUpperCase()
            : "-");
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(2, 3, 7, 7);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(7);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 8, 10);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(8);
        cell.setCellValue(fromDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        region = new CellRangeAddress(3, 3, 8, 10);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(8);
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
            cell.setCellValue(item.getIa() != null ? String.valueOf(item.getIa()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 3, 3);
            cell = sheet.getRow(index + 1)
                .getCell(3);
            cell.setCellValue(item.getIb() != null ? String.valueOf(item.getIb()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 3, 3);
            cell = sheet.getRow(index + 2)
                .getCell(3);
            cell.setCellValue(item.getIc() != null ? String.valueOf(item.getIc()) : "-");

            // cột dòng điện
            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue(item.getVab() != null ? String.valueOf(item.getVab()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 4, 4);
            cell = sheet.getRow(index + 1)
                .getCell(4);
            cell.setCellValue(item.getVbc() != null ? String.valueOf(item.getVbc()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 4, 4);
            cell = sheet.getRow(index + 2)
                .getCell(4);
            cell.setCellValue(item.getVca() != null ? String.valueOf(item.getVca()) : "-");

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
            cell.setCellValue(item.getEp() != null ? String.valueOf(item.getEp()) : "-");

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
            cell.setCellValue(item.getUdc() != null ? String.valueOf(item.getUdc()) : "-");

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
            cell.setCellValue(item.getIdc() != null ? String.valueOf(item.getIdc()) : "-");

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
            cell.setCellValue(item.getPdc() != null ? String.valueOf(item.getPdc()) : "-");

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
            cell.setCellValue(item.getF() != null ? String.valueOf(item.getF()) : "-");

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
            cell.setCellValue(item.getEp() != null ? String.valueOf(item.getEp()) : "-");

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

    /**
     * Tạo excel thông số chất lượng điện năng.
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"unused", "static-access"})
    private void createPQSExcel(final List<DataPQSPVResponse> data, final String fromDate, final Device device,
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

        // Page orientation
        sheet.getPrintSetup()
            .setLandscape(false);

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
        cell.setCellValue("BÁO CÁO CHẤT LƯỢNG ĐIỆN NĂNG INVERTER [kWh]");
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
            .getCell(5);
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

        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataPQSPVResponse item = data.get(m);
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
            cell.setCellValue(item.getSentDate());

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
     * Lấy dữ liệu điện năng theo năm
     *
     * @param customerId Mã khách hàng
     * @param deviceId Mã thiết bị
     * @return Danh sách dữ liệu điện năng trong năm
     */
    private List<DataPQSPVResponse> getDataPQSByYear(final Integer customerId, final Long deviceId, final String date) {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        String[] tLow = {"00:00", "01:00", "02:00", "03:00", "22:00", "23:00"};
        String[] tNormal = {"04:00", "05:00", "06:00", "07:00", "08:00", "12:00", "13:00", "14:00", "15:00", "16:00",
            "20:00", "21:00"};
        String[] tHight = {"09:00", "10:00", "11:00", "17:00", "18:00", "19:00"};

        List<DataPQSPVResponse> data = new ArrayList<>();

        String fromDate = date + "-01 00:00:00";
        String toDate = date + "-31 23:59:59";

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        condition.put(FROM_DATE, fromDate);
        condition.put(TO_DATE, toDate);
        condition.put(SCHEMA, Schema.getSchemas(customerId));
        List<DataInverter1> dataInfo = operationPVService.getDataPQSByMonthInverter(condition);

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
            Double epTotal = (double) 0;
            Double epNonpeakHour = (double) 0;
            Double epPeakHour = (double) 0;
            Double epNormalHour = (double) 0;
            DataPQSPVResponse js = new DataPQSPVResponse();
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

                List<Double> epLows = new ArrayList<>();
                List<Double> epNormals = new ArrayList<>();
                List<Double> epHights = new ArrayList<>();

                for (int j = 0; j < dataInfo.size(); j++) {
                    DataInverter1 dataInverter1 = dataInfo.get(j);

                    String viewTime = dataInverter1.getViewTime();
                    for (int k = 0; k < 24; k++) {
                        if (k < 10) {
                            currentDate = currentDay + "0" + k + ":00:00";
                        } else {
                            currentDate = currentDay + k + ":00:00";
                        }
                        if (StringUtils.equals(viewTime, currentDate)) {
                            js.setSentDate(viewTime);
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
                                epLows.add((double) dataInverter1.getEp());
                            } else if (checkNomal) {
                                epNormals.add((double) dataInverter1.getEp());
                            } else if (checkHight) {
                                epHights.add((double) dataInverter1.getEp());
                            }

                        }
                    }

                }

                Double epLow = epLows.stream()
                    .mapToDouble(Double::doubleValue)
                    .sum();
                Double epNormal = epNormals.stream()
                    .mapToDouble(Double::doubleValue)
                    .sum();
                Double epHight = epHights.stream()
                    .mapToDouble(Double::doubleValue)
                    .sum();

                // long total = epLow + epNormal + epHight;

                epNonpeakHour = epNonpeakHour + epLow;
                epNormalHour = epNormalHour + epNormal;
                epPeakHour = epPeakHour + epHight;
                epTotal = epNonpeakHour + epNormalHour + epPeakHour;

            }
            js.setLow((double) epNonpeakHour);
            js.setNormal((double) epNormalHour);
            js.setHigh((double) epPeakHour);
            js.setSentDate(dateMonth);
            js.setTotal((double) epTotal);
            js.setParam((double) 0);
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
    private List<DataPQSPVResponse> getDataPQSByMonth(final Integer customerId, final Long deviceId,
        final String date) {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        String[] tLow = {"00:00", "01:00", "02:00", "03:00", "22:00", "23:00"};
        String[] tNormal = {"04:00", "05:00", "06:00", "07:00", "08:00", "12:00", "13:00", "14:00", "15:00", "16:00",
            "20:00", "21:00"};
        String[] tHight = {"09:00", "10:00", "11:00", "17:00", "18:00", "19:00"};

        List<DataPQSPVResponse> data = new ArrayList<>();

        String fromDate = date + "-01 00:00:00";
        String toDate = date + "-31 23:59:59";

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        condition.put(FROM_DATE, fromDate);
        condition.put(TO_DATE, toDate);
        condition.put(SCHEMA, Schema.getSchemas(customerId));
        List<DataInverter1> dataInfo = operationPVService.getDataPQSByMonthInverter(condition);

        int daysInMonth = 28;
        // CHECKSTYLE:ON
        String[] dateTime = date.split(Constants.ES.HYPHEN_CHARACTER);
        YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(dateTime[0]), Integer.parseInt(dateTime[1]));
        daysInMonth = yearMonthObject.lengthOfMonth();

        Integer day = Integer.valueOf(daysInMonth);

        for (int i = 1; i < day + 1; i++) {
            DataPQSPVResponse js = new DataPQSPVResponse();
            String currentDay = null;
            String currentDate = null;
            if (i < 10) {
                currentDay = dateTime[0] + Constants.ES.HYPHEN_CHARACTER + dateTime[1] + Constants.ES.HYPHEN_CHARACTER
                    + "0" + i + " ";
            } else {
                currentDay = dateTime[0] + Constants.ES.HYPHEN_CHARACTER + dateTime[1] + Constants.ES.HYPHEN_CHARACTER
                    + i + " ";
            }

            List<Double> epLows = new ArrayList<>();
            List<Double> epNormals = new ArrayList<>();
            List<Double> epHights = new ArrayList<>();

            for (int j = 0; j < dataInfo.size(); j++) {
                DataInverter1 dataInverter1 = dataInfo.get(j);

                String viewTime = dataInverter1.getViewTime();
                for (int k = 0; k < 24; k++) {
                    if (k < 10) {
                        currentDate = currentDay + "0" + k + ":00:00";
                    } else {
                        currentDate = currentDay + k + ":00:00";
                    }
                    if (StringUtils.equals(viewTime, currentDate)) {
                        js.setSentDate(viewTime);
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
                            epLows.add((double) dataInverter1.getEp());
                        } else if (checkNomal) {
                            epNormals.add((double) dataInverter1.getEp());
                        } else if (checkHight) {
                            epHights.add((double) dataInverter1.getEp());
                        }

                    }
                }

            }

            Double epLow = epLows.stream()
                .mapToDouble(Double::doubleValue)
                .sum();
            Double epNormal = epNormals.stream()
                .mapToDouble(Double::doubleValue)
                .sum();
            Double epHight = epHights.stream()
                .mapToDouble(Double::doubleValue)
                .sum();

            Double total = epLow + epNormal + epHight;
            js.setLow(epLow);
            js.setNormal(epNormal);
            js.setHigh(epHight);
            js.setSentDate(currentDay);
            js.setTotal(total);
            js.setParam((double) 0);
            data.add(js);
        }

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName());

        return data;
    }

    /**
     * Tạo excel biểu đồ nhiệt độ.
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"unused", "static-access"})
    private void createChartTemperatureInverterExcel(final List<DataInverter1> data, final String fromDate,
        final String toDate, final Device device, final String path, final byte[] imageData, final long miliseconds)
        throws Exception {

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
        sheet.setColumnWidth(2, 10000);
        sheet.setColumnWidth(3, 10000);
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
        cell.setCellValue("BÁO CÁO NHIỆT ĐỘ INVERTER");
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
        cell.setCellValue("Cabinet Temperature [°C]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("Heat Sink Temperature [°C]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataInverter1 item = data.get(m);
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
            cell.setCellValue(item.getTmpCab() != null ? String.valueOf(item.getTmpCab()) : "-");

            // Cột Vị trí 1
            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getTmpSnk() != null ? String.valueOf(item.getTmpSnk()) : "-");

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
     * Tạo excel biểu đồ nhiệt độ.
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"unused", "static-access"})
    private void createChartEfficiencyInverterExcel(final List<DataInverter1> data, final String fromDate,
        final String toDate, final Device device, final String path, final byte[] imageData, final long miliseconds)
        throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(data.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Thông số hiệu suất");
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
        cell.setCellValue("BÁO CÁO HIỆU SUẤT INVERTER");
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
        cell.setCellValue("Nhiệt độ [°C]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataInverter1 item = data.get(m);
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
            cell.setCellValue("Cabinet Temperature");

            region = new CellRangeAddress(index + 1, index + 1, 2, 2);
            cell = sheet.getRow(index + 1)
                .getCell(2);
            cell.setCellValue("Heat Sink Temperature");

            // Cột Vị trí 1
            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getTmpCab() != null ? String.valueOf(item.getTmpCab()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 3, 3);
            cell = sheet.getRow(index + 1)
                .getCell(3);
            cell.setCellValue(item.getTmpSnk() != null ? String.valueOf(item.getTmpSnk()) : "-");

            index += 2;
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
}
