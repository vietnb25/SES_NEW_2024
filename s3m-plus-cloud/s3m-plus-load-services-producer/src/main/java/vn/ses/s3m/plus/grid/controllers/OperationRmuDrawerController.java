package vn.ses.s3m.plus.grid.controllers;

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
import vn.ses.s3m.plus.dto.DataRmuDrawer1;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.grid.response.DataChartRmuDrawerResponse;
import vn.ses.s3m.plus.grid.response.OperationRmuDrawerResponse;
import vn.ses.s3m.plus.grid.service.OperationRmuDrawerService;
import vn.ses.s3m.plus.response.DataPQSResponse;
import vn.ses.s3m.plus.service.DeviceService;

@RestController
@Slf4j
@RequestMapping ("/grid/operation")
public class OperationRmuDrawerController {

    @Autowired
    private OperationRmuDrawerService operationRmuDrawerService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private UserMapper userMapper;

    @Value ("${grid.producer.export-folder}")
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
     * Lấy thông tin thông số tức thời khoang tủ RMU
     *
     * @param deviceId Mã thiết bị
     * @param customerId Mã khách hàng
     * @return Thông tin thông số khoang tủ RMU tức thời
     */
    @GetMapping ("/instant/rmu-drawer/{customerId}/{deviceId}")
    public ResponseEntity<?> getInstantOperationRmuDrawerGrid(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId) {

        log.info("getInstantOperationRmuDrawerGrid START");

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        condition.put(SCHEMA, Schema.getSchemas(customerId));

        DataRmuDrawer1 rmuDrawer = operationRmuDrawerService.getInstantOperationRmuDrawerGrid(condition);

        if (rmuDrawer != null) {
            OperationRmuDrawerResponse data = new OperationRmuDrawerResponse(rmuDrawer);

            log.info("getInstantOperationRmuDrawerGrid END");

            return new ResponseEntity<OperationRmuDrawerResponse>(data, HttpStatus.OK);
        } else {
            log.info("getInstantOperationRmuDrawerGrid END");

            return new ResponseEntity<OperationRmuDrawerResponse>(HttpStatus.OK);
        }

    }

    /**
     * Lấy danh sách thông tin vận hành khoang tủ RMU
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức truy vấn
     * @param page Trang lấy dữ liệu
     * @return Danh sách thông tin vận hànhs
     */
    @GetMapping ("/rmu-drawer/{customerId}/{deviceId}/{page}")
    public ResponseEntity<?> getOperationRmuDrawerGrid(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @PathVariable final Integer page) {

        log.info("getOperationRmuDrawerGrid START");

        List<OperationRmuDrawerResponse> data = new ArrayList<>();

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

        List<DataRmuDrawer1> dataInfo = new ArrayList<>();

        Map<String, Object> condition = new HashMap<>();
        int pageSize = PAGE_SIZE;
        int totalData = 0;
        int pageTable = page;
        String soft = SORT_DESC;
        for (int i = duration[1]; i >= duration[0]; i--) {
            String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.GRID_RMU_DRAWER1));
            condition.put(SCHEMA, schema);
            condition.put(DEVICE_ID, deviceId);
            condition.put(FROM_DATE, fromDate.concat(TIME_START));
            condition.put(TO_DATE, toDate.concat(TIME_END));
            condition.put(SORT, soft);
            condition.put(PAGE_START, (pageTable - 1) * PAGE_SIZE);
            condition.put(PAGE_END, pageSize);

            totalData = operationRmuDrawerService.countTotalDataRmuDrawerGrid(condition);

            List<DataRmuDrawer1> rmu1s = operationRmuDrawerService.getOperationRmuDrawerGrid(condition);

            if (rmu1s.size() <= PAGE_SIZE) {
                pageSize = PAGE_SIZE - rmu1s.size();
                pageTable = 1;
                soft = SORT_ASC;
            } else {
                pageSize = PAGE_SIZE;
                pageTable = page;
                soft = SORT_DESC;
            }
            dataInfo.addAll(rmu1s);
        }

        double totalPage = Math.ceil((double) totalData / PAGE_SIZE);
        // object to response to client
        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put(TOTAL_PAGE_STR, totalPage);
        dataMap.put(CURRENT_PAGE_STR, page);
        dataMap.put(TOTAL_DATA_STR, totalData);

        if (dataInfo.size() > 0) {
            dataInfo.forEach(i -> {
                OperationRmuDrawerResponse res = new OperationRmuDrawerResponse(i);
                data.add(res);
            });
            dataMap.put(DATA, dataInfo);

            log.info("getOperationRmuDrawerGrid END");

            return new ResponseEntity<>(dataMap, HttpStatus.OK);
        } else {

            log.info("getOperationRmuDrawerGrid END");
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * Lấy dữ liệu biểu đồ thông tin vận hành khoang tủ
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu tìm kiếm
     * @param toDate Ngày kết thúc tìm kiếm
     * @return Dữ liệu biểu đồ
     */
    @SuppressWarnings ("unused")
    @GetMapping ("/chart/rmu-drawer/{customerId}/{deviceId}")
    public ResponseEntity<?> getDataChartRmuDrawerGrid(@PathVariable Integer customerId,
        @PathVariable final Long deviceId, @RequestParam (required = false) final String fromDate,
        @RequestParam (required = false) final String toDate) {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        List<DataChartRmuDrawerResponse> data = new ArrayList<>();

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

        List<DataRmuDrawer1> dataInfo = new ArrayList<>();
        Map<String, Object> condition = new HashMap<>();
        for (int i = duration[0]; i <= duration[1]; i++) {
            String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.GRID_RMU_DRAWER1));
            condition.put(DEVICE_ID, deviceId);
            if (fromDate != null && toDate != null) {
                condition.put(FROM_DATE, fromDate.concat(TIME_START));
                condition.put(TO_DATE, toDate.concat(TIME_END));
            }
            condition.put(SCHEMA, schema);
            condition.put(SORT, SORT_ASC);

            List<DataRmuDrawer1> rmu1s = operationRmuDrawerService.getOperationRmuDrawerGrid(condition);

            dataInfo.addAll(rmu1s);
        }

        if (dataInfo.size() > 0) {
            for (DataRmuDrawer1 r : dataInfo) {
                DataChartRmuDrawerResponse chartRes = new DataChartRmuDrawerResponse(r);
                data.add(chartRes);
            }

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<List<DataChartRmuDrawerResponse>>(data, HttpStatus.OK);
        } else {
            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName());

            return new ResponseEntity<Void>(HttpStatus.OK);
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
    @GetMapping ("/chart/electrical-power/rmu-drawer/{customerId}/{deviceId}")
    public ResponseEntity<?> getChartElectricalPowerRmuDrawerGrid(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String date, @RequestParam final Integer type) {

        log.info("getChartElectricalPowerRmuDrawer START");

        List<DataPQSResponse> data = new ArrayList<>();

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
            List<DataPQSResponse> dataPQS = getDataPQSByDay(hours, tLow, tNormal, tHigh, date, deviceId, customerId);
            data.addAll(dataPQS);
        }

        if (type == PQS_VIEW_MONTH) {

            data = getDataPQSByMonth(customerId, deviceId, date);

        }

        if (type == PQS_VIEW_YEAR) {
            data = getDataPQSByYear(customerId, deviceId, date);
        }

        log.info("getChartElectricalPowerRmuDrawer END");

        return new ResponseEntity<List<DataPQSResponse>>(data, HttpStatus.OK);
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
    private List<DataPQSResponse> getDataPQSByDay(final String[] hours, final String[] tLow, final String[] tNormal,
        final String[] tHigh, final String date, final Long deviceId, final Integer customerId) {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        List<DataPQSResponse> data = new ArrayList<>();

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
                .get(new MultiKey(Constants.DATA.tables[tableIndex], Constants.DATA.MESSAGE.GRID_RMU_DRAWER1));
            map.put(SCHEMA, schema);

            List<DataRmuDrawer1> rmuPQS = operationRmuDrawerService.getOperationRmuDrawerGrid(map);

            Long ep;
            if (rmuPQS.size() == 0) {
                ep = (long) 0;
            } else {
                Integer toEp = rmuPQS.get(0)
                    .getEp();
                Integer fromEp = rmuPQS.get(rmuPQS.size() - 1)
                    .getEp();
                ep = (long) (toEp - fromEp);
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
                pqsRes.setLow((int) (ep > 0 ? ep : 0));
                pqsRes.setNormal(0);
            }
            if (Arrays.stream(tNormal)
                .anyMatch(time::equals)) {
                pqsRes.setHigh(0);
                pqsRes.setLow(0);
                pqsRes.setNormal((int) (ep > 0 ? ep : 0));
            }
            if (Arrays.stream(tHigh)
                .anyMatch(time::equals)) {
                pqsRes.setHigh((int) (ep > 0 ? ep : 0));
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
        List<DataRmuDrawer1> dataInfo = operationRmuDrawerService.getDataPQSByMonthRmuDrawerGrid(condition);

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
                    DataRmuDrawer1 rmu1 = dataInfo.get(j);

                    String viewTime = rmu1.getViewTime();
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
                                epLows.add(Long.valueOf(rmu1.getEp()));
                            } else if (checkNomal) {
                                epNormals.add(Long.valueOf(rmu1.getEp()));
                            } else if (checkHight) {
                                epHights.add(Long.valueOf(rmu1.getEp()));
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
        List<DataRmuDrawer1> dataInfo = operationRmuDrawerService.getDataPQSByMonthRmuDrawerGrid(condition);

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
                DataRmuDrawer1 rmu1 = dataInfo.get(j);

                String viewTime = rmu1.getViewTime();
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
                            epLows.add(Long.valueOf(rmu1.getEp()));
                        } else if (checkNomal) {
                            epNormals.add(Long.valueOf(rmu1.getEp()));
                        } else if (checkHight) {
                            epHights.add(Long.valueOf(rmu1.getEp()));
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
     * Dowload thông số khoang tủ RMU.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/device-parameter/rmu-drawer/{customerId}/{deviceId}/{type}")
    public ResponseEntity<Resource> downloadDeviceParameterRmuDrawerGrid(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @PathVariable final Long type, @RequestParam final String fromDate,
        @RequestParam final String toDate, @RequestParam final String userName) throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        // get url image
        User user = userMapper.getUserByUsername(userName);
        String pngImageURL = user.getImg();
        String encodingPrefix = "base64,";
        int contentStartIndex = pngImageURL.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData = org.apache.commons.codec.binary.Base64
            .decodeBase64(pngImageURL.substring(contentStartIndex));

        List<DataRmuDrawer1> data = new ArrayList<>();

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
                .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.GRID_RMU_DRAWER1));
            condition.put(DEVICE_ID, deviceId);
            condition.put(FROM_DATE, fromDate.concat(TIME_START));
            condition.put(TO_DATE, toDate.concat(TIME_END));
            condition.put(SCHEMA, schema);
            List<DataRmuDrawer1> inverter1s = operationRmuDrawerService.getOperationRmuDrawerGrid(condition);
            data.addAll(inverter1s);
        }

        if (data.size() > 0) {
            // time miliseconds
            long miliseconds = new Date().getTime();

            Device device = deviceService.getDeviceByDeviceId(condition);

            // path folder
            String path = this.folderName + File.separator + miliseconds;

            if (type == 1) {
                // tạo excel
                createElectricalParamRmuDrawerExcel(data, fromDate, toDate, device, path, imageData, miliseconds);
            }

            if (type == 2) {
                // tạo excel
                createTemperatureParamRmuDrawerExcel(data, fromDate, toDate, device, path, imageData, miliseconds);
            }

            if (type == 3) {
                // tạo excel
                createDischargeParamRmuDrawerExcel(data, fromDate, toDate, device, path, imageData, miliseconds);
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
        } else {

            log.info(Thread.currentThread()
                .getStackTrace()[1].getMethodName() + " END");

            return new ResponseEntity<Resource>(HttpStatus.OK);
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
    @GetMapping ("/chart/download/rmu-drawer/{customerId}/{deviceId}")
    public ResponseEntity<Resource> downloadDataChartRmuDrawerGrid(@PathVariable final Integer customerId,
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
        if (chartType == Constants.DATA.CHART_GRID.DONG_DIEN) {

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

            List<DataRmuDrawer1> dataInfo = new ArrayList<>();
            Map<String, Object> condition = new HashMap<>();
            for (int i = duration[1]; i >= duration[0]; i--) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.GRID_RMU_DRAWER1));
                condition.put(DEVICE_ID, deviceId);
                if (fromDate != null && toDate != null) {
                    condition.put(FROM_DATE, fromDate.concat(TIME_START));
                    condition.put(TO_DATE, toDate.concat(TIME_END));
                }
                condition.put(SCHEMA, schema);
                condition.put(SORT, SORT_ASC);

                List<DataRmuDrawer1> rmu1s = operationRmuDrawerService.getOperationRmuDrawerGrid(condition);

                dataInfo.addAll(rmu1s);
            }

            Device device = deviceService.getDeviceByDeviceId(condition);

            // path folder
            path = this.folderName + File.separator + miliseconds;

            // tạo excel
            createPowerCircuitExcel(dataInfo, fromDate, toDate, device, path, imageData, miliseconds);
        }

        // Lấy dữ liệu biểu đồ điện áp
        if (chartType == Constants.DATA.CHART_GRID.DIEN_AP) {

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

            List<DataRmuDrawer1> dataInfo = new ArrayList<>();
            Map<String, Object> condition = new HashMap<>();
            for (int i = duration[1]; i >= duration[0]; i--) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.GRID_RMU_DRAWER1));
                condition.put(DEVICE_ID, deviceId);
                if (fromDate != null && toDate != null) {
                    condition.put(FROM_DATE, fromDate.concat(TIME_START));
                    condition.put(TO_DATE, toDate.concat(TIME_END));
                }
                condition.put(SCHEMA, schema);
                condition.put(SORT, SORT_ASC);

                List<DataRmuDrawer1> rmu1s = operationRmuDrawerService.getOperationRmuDrawerGrid(condition);

                dataInfo.addAll(rmu1s);
            }

            Device device = deviceService.getDeviceByDeviceId(condition);

            // path folder
            path = this.folderName + File.separator + miliseconds;

            // tạo excel
            createVoltageExcel(dataInfo, fromDate, toDate, device, path, imageData, miliseconds);
        }

        // Lấy dữ liệu biểu đồ công suất tác dụng
        if (chartType == Constants.DATA.CHART_GRID.CONG_SUAT_TAC_DUNG) {

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

            List<DataRmuDrawer1> dataInfo = new ArrayList<>();
            Map<String, Object> condition = new HashMap<>();
            for (int i = duration[1]; i >= duration[0]; i--) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.GRID_RMU_DRAWER1));
                condition.put(DEVICE_ID, deviceId);
                if (fromDate != null && toDate != null) {
                    condition.put(FROM_DATE, fromDate.concat(TIME_START));
                    condition.put(TO_DATE, toDate.concat(TIME_END));
                }
                condition.put(SCHEMA, schema);
                condition.put(SORT, SORT_ASC);

                List<DataRmuDrawer1> rmu1s = operationRmuDrawerService.getOperationRmuDrawerGrid(condition);

                dataInfo.addAll(rmu1s);
            }

            Device device = deviceService.getDeviceByDeviceId(condition);

            // path folder
            path = this.folderName + File.separator + miliseconds;

            // tạo excel
            createEffectivePowerExcel(dataInfo, fromDate, toDate, device, path, imageData, miliseconds);
        }

        // Lấy dữ liệu biểu dồ điện năng
        if (chartType == Constants.DATA.CHART_GRID.DIEN_NANG) {

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
        if (chartType == Constants.DATA.CHART_GRID.NHIET_DO_CUC
            || chartType == Constants.DATA.CHART_GRID.NHIET_DO_KHOANG || chartType == Constants.DATA.CHART_GRID.DO_AM) {

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

            List<DataRmuDrawer1> dataInfo = new ArrayList<>();
            Map<String, Object> condition = new HashMap<>();
            for (int i = duration[1]; i >= duration[0]; i--) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.GRID_RMU_DRAWER1));
                condition.put(DEVICE_ID, deviceId);
                if (fromDate != null && toDate != null) {
                    condition.put(FROM_DATE, fromDate.concat(TIME_START));
                    condition.put(TO_DATE, toDate.concat(TIME_END));
                }
                condition.put(SCHEMA, schema);
                condition.put(SORT, SORT_ASC);

                List<DataRmuDrawer1> rmu1s = operationRmuDrawerService.getOperationRmuDrawerGrid(condition);

                dataInfo.addAll(rmu1s);
            }

            Device device = deviceService.getDeviceByDeviceId(condition);

            // path folder
            path = this.folderName + File.separator + miliseconds;

            // tạo excel
            createTemperatureParamRmuDrawerExcel(dataInfo, fromDate, toDate, device, path, imageData, miliseconds);
        }

        // Lấy dữ liệu biểu đồ phóng điện
        if (chartType == Constants.DATA.CHART_GRID.LFB_RATIO || chartType == Constants.DATA.CHART_GRID.LFB_EPPC
            || chartType == Constants.DATA.CHART_GRID.MFB_RATIO || chartType == Constants.DATA.CHART_GRID.MLFB_EPPC
            || chartType == Constants.DATA.CHART_GRID.HLFB_RATIO || chartType == Constants.DATA.CHART_GRID.HLFB_EPPC
            || chartType == Constants.DATA.CHART_GRID.INDICATOR) {

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

            List<DataRmuDrawer1> dataInfo = new ArrayList<>();
            Map<String, Object> condition = new HashMap<>();
            for (int i = duration[1]; i >= duration[0]; i--) {
                String schema = Schema.getSchemas(customerId) + Constants.DATA.DATA_TABLES
                    .get(new MultiKey(Constants.DATA.tables[i], Constants.DATA.MESSAGE.GRID_RMU_DRAWER1));
                condition.put(DEVICE_ID, deviceId);
                if (fromDate != null && toDate != null) {
                    condition.put(FROM_DATE, fromDate.concat(TIME_START));
                    condition.put(TO_DATE, toDate.concat(TIME_END));
                }
                condition.put(SCHEMA, schema);
                condition.put(SORT, SORT_ASC);

                List<DataRmuDrawer1> rmu1s = operationRmuDrawerService.getOperationRmuDrawerGrid(condition);

                dataInfo.addAll(rmu1s);
            }

            Device device = deviceService.getDeviceByDeviceId(condition);

            // path folder
            path = this.folderName + File.separator + miliseconds;

            // tạo excel
            createDischargeParamRmuDrawerExcel(dataInfo, fromDate, toDate, device, path, imageData, miliseconds);
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
     * Tạo excel thông số điện khoang tủ Rmu.
     *
     * @param data Danh sách dữ liệu thông số khoang tủ Rmu.
     * @throws Exception
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"static-access", "unused", "null"})
    private void createElectricalParamRmuDrawerExcel(final List<DataRmuDrawer1> data, final String fromDate,
        final String toDate, final Device device, final String path, final byte[] imageData, final long miliseconds)
        throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(data.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Thông số điện ");
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
        sheet.setColumnWidth(6, 3000);
        sheet.setColumnWidth(7, 3000);
        sheet.setColumnWidth(8, 3000);
        sheet.setColumnWidth(10, 5000);
        sheet.setColumnWidth(11, 5000);
        sheet.setColumnWidth(12, 5000);
        sheet.setColumnWidth(16, 5500);

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
        cell.setCellValue("Q [kVAr]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 8, 8);
        cell = sheet.getRow(5)
            .getCell(8);
        cell.setCellValue("S [kWA]");
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
        cell.setCellValue("Vu [%]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 15, 15);
        cell = sheet.getRow(5)
            .getCell(15);
        cell.setCellValue("Iu [%]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 16, 16);
        cell = sheet.getRow(5)
            .getCell(16);
        cell.setCellValue("Điện năng [kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataRmuDrawer1 item = data.get(m);
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
            cell.setCellValue(item.getUab() != null ? String.valueOf(item.getUab()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 3, 3);
            cell = sheet.getRow(index + 1)
                .getCell(3);
            cell.setCellValue(item.getUbc() != null ? String.valueOf(item.getUbc()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 3, 3);
            cell = sheet.getRow(index + 2)
                .getCell(3);
            cell.setCellValue(item.getUca() != null ? String.valueOf(item.getUca()) : "-");

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

            region = new CellRangeAddress(index, index, 6, 6);
            cell = sheet.getRow(index)
                .getCell(6);
            cell.setCellValue(item.getPa() != null ? String.valueOf(item.getPa()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 6, 6);
            cell = sheet.getRow(index + 1)
                .getCell(6);
            cell.setCellValue(item.getPb() != null ? String.valueOf(item.getPb()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 6, 6);
            cell = sheet.getRow(index + 2)
                .getCell(6);
            cell.setCellValue(item.getPc() != null ? String.valueOf(item.getPc()) : "-");

            region = new CellRangeAddress(index, index, 7, 7);
            cell = sheet.getRow(index)
                .getCell(7);
            cell.setCellValue(item.getQa() != null ? String.valueOf(item.getQa()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 7, 7);
            cell = sheet.getRow(index + 1)
                .getCell(7);
            cell.setCellValue(item.getQb() != null ? String.valueOf(item.getQb()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 7, 7);
            cell = sheet.getRow(index + 2)
                .getCell(7);
            cell.setCellValue(item.getQc() != null ? String.valueOf(item.getQc()) : "-");

            region = new CellRangeAddress(index, index, 8, 8);
            cell = sheet.getRow(index)
                .getCell(8);
            cell.setCellValue(item.getSa() != null ? String.valueOf(item.getSa()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 8, 8);
            cell = sheet.getRow(index + 1)
                .getCell(8);
            cell.setCellValue(item.getSb() != null ? String.valueOf(item.getSb()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 8, 8);
            cell = sheet.getRow(index + 2)
                .getCell(8);
            cell.setCellValue(item.getSc() != null ? String.valueOf(item.getSc()) : "-");

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
            cell.setCellValue(item.getThdVan() != null ? String.valueOf(item.getThdVan()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 10, 10);
            cell = sheet.getRow(index + 1)
                .getCell(10);
            cell.setCellValue(item.getThdVbn() != null ? String.valueOf(item.getThdVbn()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 10, 10);
            cell = sheet.getRow(index + 2)
                .getCell(10);
            cell.setCellValue(item.getThdVcn() != null ? String.valueOf(item.getThdVcn()) : "-");

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

            region = new CellRangeAddress(index, index, 13, 13);
            cell = sheet.getRow(index)
                .getCell(13);
            cell.setCellValue(item.getF() != null ? String.valueOf(item.getF()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 13, 13);
            cell = sheet.getRow(index + 1)
                .getCell(13);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 2, index + 2, 13, 13);
            cell = sheet.getRow(index + 2)
                .getCell(13);
            cell.setCellValue("-");

            region = new CellRangeAddress(index, index, 14, 14);
            cell = sheet.getRow(index)
                .getCell(14);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 1, index + 1, 14, 14);
            cell = sheet.getRow(index + 1)
                .getCell(14);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 2, index + 2, 14, 14);
            cell = sheet.getRow(index + 2)
                .getCell(14);
            cell.setCellValue("-");

            region = new CellRangeAddress(index, index, 15, 15);
            cell = sheet.getRow(index)
                .getCell(15);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 1, index + 1, 15, 15);
            cell = sheet.getRow(index + 1)
                .getCell(15);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 2, index + 2, 15, 15);
            cell = sheet.getRow(index + 2)
                .getCell(15);
            cell.setCellValue("-");

            region = new CellRangeAddress(index, index, 16, 16);
            cell = sheet.getRow(index)
                .getCell(16);
            cell.setCellValue(item.getEp() != null ? String.valueOf(item.getEp()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 16, 16);
            cell = sheet.getRow(index + 1)
                .getCell(16);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 2, index + 2, 16, 16);
            cell = sheet.getRow(index + 2)
                .getCell(16);
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
     * Tạo excel thông số nhiệt độ khoang tủ Rmu.
     *
     * @param data Danh sách dữ liệu thông số nhiệt độ khoang tủ Rmu.
     * @throws Exception
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"static-access", "unused", "null"})
    private void createTemperatureParamRmuDrawerExcel(final List<DataRmuDrawer1> data, final String fromDate,
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
        sheet.setColumnWidth(3, 8000);
        sheet.setColumnWidth(4, 8000);
        sheet.setColumnWidth(5, 7000);
        sheet.setColumnWidth(6, 7000);

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
        cell.setCellValue("Nhiệt độ cực trên [°C]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("Nhiệt độ cực dưới [°C]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("Nhiệt độ khoang [°C]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 6, 6);
        cell = sheet.getRow(5)
            .getCell(6);
        cell.setCellValue("Độ ẩm [%]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataRmuDrawer1 item = data.get(m);
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
            cell.setCellValue(item.getSawId1() != null ? String.valueOf(item.getSawId1()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 3, 3);
            cell = sheet.getRow(index + 1)
                .getCell(3);
            cell.setCellValue(item.getSawId2() != null ? String.valueOf(item.getSawId2()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 3, 3);
            cell = sheet.getRow(index + 2)
                .getCell(3);
            cell.setCellValue(item.getSawId3() != null ? String.valueOf(item.getSawId3()) : "-");

            // cột dòng điện
            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue(item.getSawId4() != null ? String.valueOf(item.getSawId4()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 4, 4);
            cell = sheet.getRow(index + 1)
                .getCell(4);
            cell.setCellValue(item.getSawId5() != null ? String.valueOf(item.getSawId5()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 4, 4);
            cell = sheet.getRow(index + 2)
                .getCell(4);
            cell.setCellValue(item.getSawId6() != null ? String.valueOf(item.getSawId6()) : "-");

            region = new CellRangeAddress(index, index, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue(item.getT() != null ? String.valueOf(item.getT()) : "-");

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
            cell.setCellValue(item.getH() != null ? String.valueOf(item.getH()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 6, 6);
            cell = sheet.getRow(index + 1)
                .getCell(6);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 2, index + 2, 6, 6);
            cell = sheet.getRow(index + 2)
                .getCell(6);
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
     * Tạo excel thông số phóng điện khoang tủ Rmu.
     *
     * @param data Danh sách dữ liệu thông số phóng điện khoang tủ Rmu.
     * @throws Exception
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"static-access", "unused", "null"})
    private void createDischargeParamRmuDrawerExcel(final List<DataRmuDrawer1> data, final String fromDate,
        final String toDate, final Device device, final String path, final byte[] imageData, final long miliseconds)
        throws Exception {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(data.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Thông số phóng điện");
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
        sheet.setColumnWidth(2, 8000);
        sheet.setColumnWidth(3, 6000);
        sheet.setColumnWidth(4, 6000);
        sheet.setColumnWidth(5, 6000);
        sheet.setColumnWidth(6, 6000);
        sheet.setColumnWidth(7, 6000);
        sheet.setColumnWidth(8, 6000);

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
        cell.setCellValue("");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("LFB Ratio [dB]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("LFB EPPC");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("MFB Ratio [dB]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 6, 6);
        cell = sheet.getRow(5)
            .getCell(6);
        cell.setCellValue("MFB EPPC");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 7, 7);
        cell = sheet.getRow(5)
            .getCell(7);
        cell.setCellValue("HFB Ratio [dB]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 8, 8);
        cell = sheet.getRow(5)
            .getCell(8);
        cell.setCellValue("HFB EPPC");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataRmuDrawer1 item = data.get(m);
            for (int i = index; i < index + 4; i++) {
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
            cell.setCellValue("Giá trị");

            region = new CellRangeAddress(index + 1, index + 1, 2, 2);
            cell = sheet.getRow(index + 1)
                .getCell(2);
            cell.setCellValue("Trung bình Ratio");

            region = new CellRangeAddress(index + 2, index + 2, 2, 2);
            cell = sheet.getRow(index + 2)
                .getCell(2);
            cell.setCellValue("Trung bình EPPC");

            region = new CellRangeAddress(index + 3, index + 3, 2, 2);
            cell = sheet.getRow(index + 3)
                .getCell(2);
            cell.setCellValue("Mức chỉ thị");

            // cột điện áp
            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getLfbRatio() != null ? String.valueOf(item.getLfbRatio()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 3, 8);
            sheet.addMergedRegion(region);
            cell = sheet.getRow(index + 1)
                .getCell(3);
            cell.setCellValue(item.getMeanRatio() != null ? String.valueOf(item.getMeanRatio()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 3, 8);
            sheet.addMergedRegion(region);
            cell = sheet.getRow(index + 2)
                .getCell(3);
            cell.setCellValue(item.getMeanEppc() != null ? String.valueOf(item.getMeanEppc()) : "-");

            region = new CellRangeAddress(index + 3, index + 3, 3, 8);
            sheet.addMergedRegion(region);
            cell = sheet.getRow(index + 3)
                .getCell(3);
            cell.setCellValue(item.getIndicator() != null ? String.valueOf(item.getIndicator()) : "-");

            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue(item.getLfbEppc() != null ? String.valueOf(item.getLfbEppc()) : "-");

            region = new CellRangeAddress(index, index, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue(item.getMfbRatio() != null ? String.valueOf(item.getMfbRatio()) : "-");

            region = new CellRangeAddress(index, index, 6, 6);
            cell = sheet.getRow(index)
                .getCell(6);
            cell.setCellValue(item.getMlfbEppc() != null ? String.valueOf(item.getMlfbEppc()) : "-");

            region = new CellRangeAddress(index, index, 7, 7);
            cell = sheet.getRow(index)
                .getCell(7);
            cell.setCellValue(item.getHlfbRatio() != null ? String.valueOf(item.getHlfbRatio()) : "-");

            region = new CellRangeAddress(index, index, 8, 8);
            cell = sheet.getRow(index)
                .getCell(8);
            cell.setCellValue(item.getHlfbEppc() != null ? String.valueOf(item.getHlfbEppc()) : "-");

            index += 4;
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
     * Tạo excel thông tin dòng điện.
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"static-access", "unused", "null"})
    private void createPowerCircuitExcel(final List<DataRmuDrawer1> data, final String fromDate, final String toDate,
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
            DataRmuDrawer1 item = data.get(m);
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
    @SuppressWarnings ({"static-access", "unused", "null"})
    private void createVoltageExcel(final List<DataRmuDrawer1> data, final String fromDate, final String toDate,
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
            DataRmuDrawer1 item = data.get(m);
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
    @SuppressWarnings ({"static-access", "unused", "null"})
    private void createEffectivePowerExcel(final List<DataRmuDrawer1> data, final String fromDate, final String toDate,
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
            DataRmuDrawer1 item = data.get(m);
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
     * Tạo excel thông số chất lượng điện năng.
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings ({"static-access", "unused", "null"})
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

}
