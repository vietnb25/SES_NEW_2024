package vn.ses.s3m.plus.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.Forecast;
import vn.ses.s3m.plus.dto.OverviewLoadPower;
import vn.ses.s3m.plus.dto.OverviewLoadTotalPower;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.service.DeviceService;
import vn.ses.s3m.plus.service.OverviewPowerService;

@RestController
@RequestMapping ("/load")
public class OverviewPowerController {

    static final int MILLISECOND = 1000;

    static final int FIFTEEN_MINUTE = 900;

    // Logging
    private final Log log = LogFactory.getLog(OverviewPowerController.class);

    @Value ("${load.producer.export-folder}")
    private String folderName;

    @Autowired
    private OverviewPowerService overviewPowerService;

    @Autowired
    private DeviceService deviceService;

    // Các tham số tính dự báo
    private static final int MAX_DAY = 7;

    private static final int MAX_SECOND = 60;

    private static final int MAX_MONH = 12;

    private static final int MAX_HOURS = 24;

    private static final int TRANSACTION_DATE_24_HOURS = 86400;

    private static final Integer PAGE_SIZE = 50;

    private static final String SCHEMA = "schema";

    private static final String PROJECT_ID = "projectId";

    private static final String SYSTEM_TYPE = "systemTypeId";

    /**
     * Lấy thông tin công suất của từng thiết bị trong dự án Load trong ngày
     *
     * @param projectId ID dự án
     * @return devicePowers thông tin công suất các thiết bị
     */
    @GetMapping ("/power/{customerId}/{projectId}")
    public ResponseEntity<?> getPowerInDay(@PathVariable final String customerId, @PathVariable final String projectId,
        @RequestParam (required = false) final String keyword) {

        log.info("getPowerInDay START");
        Map<String, Object> condition = new HashMap<>();
        condition.put("schema", Schema.getSchemas(Integer.parseInt(customerId)));
        condition.put("projectId", projectId);

        // get project infor
        Map<String, String> projectTree = overviewPowerService.getInformationProject(condition);
        String projectInfor = projectTree.get("customerName") + " / " + projectTree.get("superManagerName") + " / "
            + projectTree.get("managerName") + " / " + projectTree.get("areaName") + " / " + projectTree.get(
                "projectName") + " / ";

        if (keyword != null) {
            condition.put("keyword", keyword);
        }
        // get năng lượng theo ngày tháng năm
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = dateFormat.format(today);
        String previousDayString = dateFormat.format(new Date(today.getTime() - TRANSACTION_DATE_24_HOURS
            * MILLISECOND));
        int currentYear = Year.now()
            .getValue();
        int lastYear = Year.now()
            .minusYears(1)
            .getValue();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");
        String currentMonth = sdfMonth.format(today);
        cal.setTime(today);
        if (cal.get(Calendar.MONTH) == Calendar.JANUARY) {
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
        } else {
            cal.roll(Calendar.MONTH, false);
        }
        String previousMonth = sdfMonth.format(cal.getTime());
        condition.put("year", currentYear);
        Long sumEnergy = overviewPowerService.getSumEnergy(condition);
        Long sumEnergyCurrentYear = overviewPowerService.getSumEnergyByYear(condition);
        condition.put("year", lastYear);
        Long sumEnergyLastYear = overviewPowerService.getSumEnergyByYear(condition);
        condition.put("day", todayString);
        Long sumEnergyToday = overviewPowerService.getSumEnergyByDay(condition);
        condition.put("day", previousDayString);
        Long sumEnergyPreday = overviewPowerService.getSumEnergyByDay(condition);
        condition.put("month", currentMonth);
        Long sumEnergyCurMonth = overviewPowerService.getSumEnergyByMonth(condition);
        condition.put("month", previousMonth);
        Long sumEnergyPreMonth = overviewPowerService.getSumEnergyByMonth(condition);

        // get list device with info power
        List<Device> devices = deviceService.getDeviceByProjectId(condition);
        List<String> deviceIds = new ArrayList<>();
        List<String> systemMapIds = new ArrayList<>();

        if (devices.size() > 0) {
            for (Device device : devices) {
                String deviceId = String.valueOf(device.getDeviceId());
                deviceIds.add(deviceId);
            }
            for (Device device : devices) {
                if (device.getSystemMapId() != null) {
                    String systemMapId = String.valueOf(device.getSystemMapId());
                    systemMapIds.add(systemMapId);
                }
            }
            condition.put("deviceIds", deviceIds);
            condition.put("limit", devices.size());
        }
        List<OverviewLoadPower> layers = new ArrayList<>();

        if (systemMapIds.size() > 0) {
            condition.put("systemMapIds", systemMapIds);
            layers = overviewPowerService.getLayer(condition);
        }
        // thông tin cảnh báo
        List<OverviewLoadPower> warnings = overviewPowerService.getDeviceHasWarning(condition);

        List<OverviewLoadPower> results = new ArrayList<>();
        for (Device device : devices) {
            OverviewLoadPower item = new OverviewLoadPower();
            item.setProjectId(Integer.parseInt(projectId));
            item.setDeviceId(device.getDeviceId());
            item.setDeviceName(device.getDeviceName());
            condition.put("deviceId", device.getDeviceId());
            OverviewLoadPower devicePowers = overviewPowerService.getPowers(condition);
            if (devicePowers != null) {
                item.setPTotal(devicePowers.getPTotal());
            }

            if (layers.size() > 0) {
                for (OverviewLoadPower layer : layers) {
                    if (layer.getSystemMapId() == device.getSystemMapId()) {
                        item.setSystemMapId(device.getSystemMapId());
                        item.setLayer(layer.getLayer());
                        item.setSystemMapName(layer.getSystemMapName());
                    }
                }
            }

            if (item.getPTotal() == null) {
                item.setLoadStatus("error");
            } else {
                if (warnings.size() > 0) {
                    for (OverviewLoadPower warning : warnings) {
                        if (warning.getWarningCount() > 0) {
                            item.setLoadStatus("warning");
                        } else {
                            item.setLoadStatus("active");
                        }
                    }
                } else {
                    item.setLoadStatus("active");
                }
            }

            results.add(item);
        }
        // result
        Map<String, Object> dataResponse = new HashMap<>();
        dataResponse.put("sumEnergy", sumEnergy);
        dataResponse.put("deviceList", results);
        dataResponse.put("sumEnergyCurrentYear", sumEnergyCurrentYear);
        dataResponse.put("sumEnergyLastYear", sumEnergyLastYear);
        dataResponse.put("sumEnergyToday", sumEnergyToday);
        dataResponse.put("sumEnergyPreday", sumEnergyPreday);
        dataResponse.put("sumEnergyCurMonth", sumEnergyCurMonth);
        dataResponse.put("sumEnergyPreMonth", sumEnergyPreMonth);
        dataResponse.put("projectInfor", projectInfor);

        log.info("getPowerInDay END");

        return new ResponseEntity<Map<String, Object>>(dataResponse, HttpStatus.OK);
    }

    /**
     * Lấy thông tin tổng công suất, năng lượng các thiết bị trong ngày thuộc dự án Load
     *
     * @param projectId ID dự án
     * @param day Ngày để lấy dữ liệu
     * @return thông tin tổng công suất, dữ liệu theo các mốc thời gian trong ngày
     * @throws Exception
     */
    @GetMapping ("/powerTotal/{customerId}/{projectId}")
    public ResponseEntity<List<OverviewLoadTotalPower>> getTotalPowerEnergy(@PathVariable final String customerId,
        @PathVariable final String projectId, @RequestParam final String fromDate, @RequestParam final String toDate)
        throws Exception {

        log.info("getTotalPowerEnergy START");

        List<OverviewLoadTotalPower> totalPowers = new ArrayList<>();

        Date now = new Date();
        Map<String, Object> condition = new HashMap<>();
        condition.put("schema", Schema.getSchemas(Integer.parseInt(customerId)));
        condition.put("projectId", projectId);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormatView = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat dateFormatToHourMinute = new SimpleDateFormat("HH:mm");

        String endDay = toDate + " 23:59:59";
        Date date = null;
        Date endDate = null;
        try {
            date = dateFormatWithTime.parse(fromDate + " 00:00:00");
            endDate = dateFormatWithTime.parse(endDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Long transactionDate = date.getTime() / MILLISECOND;
        Long transactionEndDate = null;

        // so sánh ngày truy vấn với ngày hiện tại
        try {
            transactionEndDate = dateFormat.parse(toDate)
                .before(dateFormat.parse(dateFormat.format(now)))
                    ? endDate.getTime() / MILLISECOND
                    : now.getTime() / MILLISECOND;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // get list power, enery
        condition.put("from", dateFormatWithTime.format(new Date( (transactionDate - TRANSACTION_DATE_24_HOURS)
            * 1000)));
        condition.put("to", dateFormatWithTime.format(new Date(transactionEndDate * 1000)));
        List<OverviewLoadTotalPower> listPower = overviewPowerService.getListPowerInDay(condition);
        Map<Long, OverviewLoadTotalPower> mapPower = new HashMap<>();
        Map<MultiKey, OverviewLoadTotalPower> mapPowerCache = new HashMap<MultiKey, OverviewLoadTotalPower>();
        for (OverviewLoadTotalPower item : listPower) {
            MultiKey key = new MultiKey(item.getViewTime()
                .getTime() / MILLISECOND, item.getDeviceId());
            if (mapPowerCache.get(key) == null) {
                mapPowerCache.put(key, item);
            } else {
                if (mapPowerCache.get(key)
                    .getId() < item.getId()) {
                    mapPowerCache.put(key, item);
                }
            }
        }
        for (Map.Entry<MultiKey, OverviewLoadTotalPower> entry : mapPowerCache.entrySet()) {
            if (mapPower.get(entry.getKey()
                .getKey(0)) == null) {
                mapPower.put((Long) entry.getKey()
                    .getKey(0), entry.getValue());
            } else {
                Long power = mapPower.get(entry.getKey()
                    .getKey(0))
                    .getPower();
                Long energy = mapPower.get(entry.getKey()
                    .getKey(0))
                    .getEnergy();
                power += entry.getValue()
                    .getPower();
                energy += entry.getValue()
                    .getEnergy();
                mapPower.get(entry.getKey()
                    .getKey(0))
                    .setPower(power);
                mapPower.get(entry.getKey()
                    .getKey(0))
                    .setEnergy(energy);
            }
        }

        Map<Long, Forecast> mapForecast = getForecastPower(new Date(transactionDate * 1000), new Date(transactionEndDate
            * 1000), projectId, customerId);
        // truy vấn data theo các mốc thời gian 15p
        while (transactionDate <= transactionEndDate) {
            OverviewLoadTotalPower newTotalPower = new OverviewLoadTotalPower();
            OverviewLoadTotalPower oldTotalPower = new OverviewLoadTotalPower();

            Long sumPower = (long) 0;
            Long sumEnergy = (long) 0;
            Long sumOldPower = (long) 0;
            Long sumOldEnergy = (long) 0;
            if (mapPower.get(transactionDate) != null) {
                OverviewLoadTotalPower itemPower = mapPower.get(transactionDate);
                sumPower = itemPower.getPower();
                sumEnergy = itemPower.getEnergy();
            }
            if (mapPower.get(transactionDate - TRANSACTION_DATE_24_HOURS) != null) {
                OverviewLoadTotalPower itemPower = mapPower.get(transactionDate - TRANSACTION_DATE_24_HOURS);
                sumOldEnergy = itemPower.getEnergy();
                sumOldPower = itemPower.getPower();
            }

            String timeView = dateFormatView.format(new Date(transactionDate * MILLISECOND));
            newTotalPower.setTime(timeView);
            newTotalPower.setEnergy(sumEnergy != null ? sumEnergy : 0);
            newTotalPower.setPower(sumPower != null ? sumPower : 0);

            String timeForecast = dateFormatToHourMinute.format(new Date(transactionDate * MILLISECOND));
            Forecast f = mapForecast.get(transactionDate) != null ? mapForecast.get(transactionDate) : null;
            oldTotalPower.setPower(sumOldPower != null ? sumOldPower : 0);
            oldTotalPower.setEnergy(sumOldEnergy != null ? sumOldEnergy : 0);
            Long pforecast = (long) 0;
            if (f != null) {
                pforecast = totalPowerForecast(timeForecast, dateFormat, f, endDay, oldTotalPower);
            } else {
                f = new Forecast();
                f.setA0((double) 0);
                f.setA1((double) 0);
                f.setA2((double) 0);
                f.setA3((double) 0);
                f.setA4((double) 0);
                f.setA5((double) 0);
                f.setA6((double) 0);
                f.setA7((double) 0);
                f.setA8((double) 0);
                f.setA9((double) 1);
                pforecast = totalPowerForecast(timeForecast, dateFormat, f, endDay, oldTotalPower);
            }

            newTotalPower.setForecast(pforecast > 0 ? pforecast : 0);

            totalPowers.add(newTotalPower);

            transactionDate += FIFTEEN_MINUTE;
        }

        log.info("getTotalPowerEnergy END");

        return new ResponseEntity<List<OverviewLoadTotalPower>>(totalPowers, HttpStatus.OK);
    }

    public Map<Long, Forecast> getForecastPower(Date from, Date to, String projectId, String customerId)
        throws Exception {

        Map<String, Object> condition1 = new HashMap<>();
        Map<Long, Forecast> result = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        condition1.put("schema", Schema.getSchemas(Integer.parseInt(customerId)));
        condition1.put("systemTypeId", 1);
        condition1.put("projectId", projectId);
        condition1.put("date", from);
        Forecast forecastFirst = overviewPowerService.getForecast(condition1);
        if (forecastFirst != null) {
            condition1.put("from", forecastFirst.getUpdateDate());
        } else {
            condition1.put("from", from);
        }
        condition1.put("to", to);
        List<Forecast> forecasts = overviewPowerService.getListForecast(condition1);
        long transactionFrom = from.getTime() / MILLISECOND;
        long transactionTo = to.getTime() / MILLISECOND;
        while (transactionFrom <= transactionTo) {
            List<Forecast> listSort = new ArrayList<>();
            for (Forecast forecast : forecasts) {
                Date date = sdf.parse(forecast.getUpdateDate());
                if (transactionFrom * MILLISECOND >= date.getTime()) {
                    listSort.add(forecast);
                }
            }
            if (listSort != null && listSort.size() > 0) {
                result.put(transactionFrom, listSort.get(0));
            }
            transactionFrom += FIFTEEN_MINUTE;
        }

        return result;

    }

    /**
     * Lấy thông tin dự báo
     *
     * @param forecast Đối tượng thêm sửa dự báo
     */
    @GetMapping ("/powerTotal/forecast/{customerId}/{projectId}/{systemTypeId}")
    public ResponseEntity<Forecast> getForecast(@PathVariable final Integer customerId,
        @PathVariable final String projectId, @PathVariable final String systemTypeId) {

        log.info("getForecast START");

        Map<String, Object> condition = new HashMap<>();
        condition.put(PROJECT_ID, projectId);
        condition.put(SYSTEM_TYPE, systemTypeId);
        condition.put(SCHEMA, Schema.getSchemas(customerId));

        Forecast forecast = overviewPowerService.getForecast(condition);

        if (forecast != null) {

            log.info("getForecast END");

            return new ResponseEntity<Forecast>(forecast, HttpStatus.OK);
        } else {

            log.info("getForecast END");

            return new ResponseEntity<Forecast>(HttpStatus.OK);
        }
    }

    /**
     * Lấy danh sách cài đặt thông số dự báo
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu
     * @param toDate Ngày kết thức truy vấn dữ liệu
     * @return Danh sách hông số điện và nhiệt độ
     */
    @GetMapping ("/forecasts/{customerId}/{projectId}/{systemTypeId}/{page}")
    public ResponseEntity<?> getForecasts(@PathVariable final Integer customerId, @PathVariable final Long projectId,
        @PathVariable final Long systemTypeId, @PathVariable final Integer page) {

        log.info(Thread.currentThread()
            .getStackTrace()[1].getMethodName() + " START");

        Map<String, Object> condition = new HashMap<>();
        condition.put(PROJECT_ID, projectId);
        condition.put(SYSTEM_TYPE, systemTypeId);
        condition.put(SCHEMA, Schema.getSchemas(customerId));
        condition.put("start", (page - 1) * PAGE_SIZE);
        condition.put("end", PAGE_SIZE);

        List<Forecast> data = overviewPowerService.getForecasts(condition);

        int totalData = overviewPowerService.countTotalForecasts(condition);

        double totalPage = Math.ceil((double) totalData / PAGE_SIZE);

        Map<String, Object> mapData = new HashMap<>();
        mapData.put("totalPage", totalPage);
        mapData.put("currentPage", page);

        if (data.size() > 0) {

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
     * Thêm hoặc chỉnh sửa dự báo
     *
     * @param forecast Đối tượng thêm sửa dự báo
     */
    @PostMapping ("/powerTotal/forecast/save/{customerId}")
    public ResponseEntity<?> saveForecast(@RequestBody final Forecast forecast,
        @PathVariable final Integer customerId) {

        log.info("saveForecast START");

        Map<String, Object> conditionInfo = new HashMap<>();
        conditionInfo.put(PROJECT_ID, forecast.getProjectId());
        conditionInfo.put(SYSTEM_TYPE, forecast.getSystemTypeId());
        conditionInfo.put(SCHEMA, Schema.getSchemas(customerId));
        Forecast forecastInfo = overviewPowerService.getForecast(conditionInfo);
        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
        String now = dt1.format(new Date());

        Date dateForecast;
        String date = null;
        if (forecastInfo != null) {
            try {
                dateForecast = dt1.parse(forecastInfo.getUpdateDate());
                date = dt1.format(dateForecast);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Map<String, Object> condition = new HashMap<>();
        if (forecastInfo != null && date.equals(now)) {

            condition.put("id", forecastInfo.getId());
            condition.put("projectId", forecast.getProjectId());
            condition.put("systemTypeId", forecast.getSystemTypeId());
            condition.put("a0", forecast.getA0());
            condition.put("a1", forecast.getA1());
            condition.put("a2", forecast.getA2());
            condition.put("a3", forecast.getA3());
            condition.put("a4", forecast.getA4());
            condition.put("a5", forecast.getA5());
            condition.put("a6", forecast.getA6());
            condition.put("a7", forecast.getA7());
            condition.put("a8", forecast.getA8());
            condition.put("a9", forecast.getA9());
            condition.put(SCHEMA, Schema.getSchemas(customerId));
            if (forecastInfo.getA0()
                .equals(forecast.getA0()) == false || forecastInfo.getA1()
                    .equals(forecast.getA1()) == false || forecastInfo.getA2()
                        .equals(forecast.getA2()) == false || forecastInfo.getA3()
                            .equals(forecast.getA3()) == false || forecastInfo.getA4()
                                .equals(forecast.getA4()) == false || forecastInfo.getA5()
                                    .equals(forecast.getA5()) == false || forecastInfo.getA6()
                                        .equals(forecast.getA6()) == false || forecastInfo.getA7()
                                            .equals(forecast.getA7()) == false || forecastInfo.getA8()
                                                .equals(forecast.getA8()) == false || forecastInfo.getA9()
                                                    .equals(forecast.getA9()) == false) {
                overviewPowerService.insertForecastHistory(condition);
            }
            overviewPowerService.updateForecast(condition);
        } else {
            condition.put("projectId", forecast.getProjectId());
            condition.put("systemTypeId", forecast.getSystemTypeId());
            condition.put("a0", forecast.getA0());
            condition.put("a1", forecast.getA1());
            condition.put("a2", forecast.getA2());
            condition.put("a3", forecast.getA3());
            condition.put("a4", forecast.getA4());
            condition.put("a5", forecast.getA5());
            condition.put("a6", forecast.getA6());
            condition.put("a7", forecast.getA7());
            condition.put("a8", forecast.getA8());
            condition.put("a9", forecast.getA9());
            condition.put(SCHEMA, Schema.getSchemas(customerId));
            overviewPowerService.insertForecastHistory(condition);
            overviewPowerService.insertForecast(condition);
        }

        log.info("saveForecast END");

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * Tính giá trị công suất dự đoán
     *
     * @param projectId Mã dự án
     * @param systemTypeId Mã hệ thống
     * @param time Thời gian truy vấn
     * @param dateFormat Kiểu fomat thời gian
     * @param day Ngày truy vấn
     * @param transactionDate Thời gian truy vấn
     * @return Công suất dự đoán
     */
    private Long totalPowerForecast(final String time, final SimpleDateFormat dateFormat, final Forecast f,
        final String day, final OverviewLoadTotalPower item) {

        String[] hourAndMinute = time.split(":");
        Float hourForecast = Float.valueOf(hourAndMinute[0]) + Float.valueOf(hourAndMinute[1]) / MAX_SECOND;

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(dateFormat.parse(day));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Integer dayForecast = c.get(Calendar.DAY_OF_WEEK);
        Integer monthForecast = c.get(Calendar.MONTH) + 1;
        Integer yearForecast = c.get(Calendar.YEAR);
        Integer isWeekend = dayForecast == 1 || dayForecast == MAX_DAY ? 1 : 0;

        Long priorDay = item.getPower() != null ? item.getPower() : (long) 0;

        Long pforecast = (long) (f.getA0() + f.getA1() * Math.sin(2 * Math.PI * hourForecast / MAX_HOURS) + f.getA2()
            * Math.cos(2 * Math.PI * hourForecast / MAX_HOURS) + f.getA3() * Math.sin(2 * Math.PI * dayForecast
                / MAX_DAY) + f.getA4() * Math.cos(2 * Math.PI * dayForecast / MAX_DAY) + f.getA5() * Math.sin(2
                    * Math.PI * monthForecast / MAX_MONH) + f.getA6() * Math.cos(2 * Math.PI * monthForecast / MAX_MONH)
            + f.getA7() * yearForecast + f.getA8() * isWeekend + f.getA9() * priorDay);

        return pforecast;
    }

    /**
     * Xuất thông tin tổng công suất, năng lượng ra file excel
     *
     * @param projectId ID dự án
     * @param day Ngày truy vấn dữ liệu
     * @return
     * @throws Exception
     */
    // CHECKSTYLE:OFF
    @GetMapping (value = "/exportToExcel/{day}/{projectId}")
    public ResponseEntity<?> exportTotalPowerEnergyToExcel(@PathVariable final String day,
        @PathVariable final String projectId) throws Exception {

        log.info("exportTotalPowerEnergyToExcel START");

        Date now = new Date();
        List<OverviewLoadTotalPower> totalPowers = new ArrayList<>();
        Map<String, Object> condition = new HashMap<>();
        condition.put("projectId", projectId);

        // lấy thông tin dự án
        Map<String, String> inforProject = overviewPowerService.getInformationProject(condition);

        // lấy danh sách dự án
        List<Device> devices = deviceService.getDeviceByProjectId(condition);

        // format thời gian
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormatToHourMinute = new SimpleDateFormat("HH:mm");
        String endDay = day + " 23:59:59";
        Date date = null;
        Date endDate = null;
        try {
            date = dateFormatWithTime.parse(day + " 00:00:00");
            endDate = dateFormatWithTime.parse(endDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Long transactionDate = date.getTime() / MILLISECOND;
        Long transactionEndDate = null;
        // so sánh ngày truy vấn với ngày hiện tại
        try {
            transactionEndDate = dateFormat.parse(day)
                .before(dateFormat.parse(dateFormat.format(now)))
                    ? endDate.getTime() / MILLISECOND
                    : now.getTime() / MILLISECOND;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // truy vấn data theo các mốc thời gian 15p
        while (transactionDate <= transactionEndDate) {
            OverviewLoadTotalPower newTotalPower = new OverviewLoadTotalPower();
            condition.put("from", transactionDate);
            condition.put("to", transactionDate + FIFTEEN_MINUTE);
            OverviewLoadTotalPower totalPower = overviewPowerService.getTotalPowerInDay(condition);
            newTotalPower.setTime(dateFormatToHourMinute.format(new Date(transactionDate * MILLISECOND)));
            newTotalPower.setEnergy(totalPower != null ? totalPower.getEnergy() : 0);
            newTotalPower.setPower(totalPower != null ? totalPower.getPower() : 0);
            totalPowers.add(newTotalPower);
            transactionDate += FIFTEEN_MINUTE;
        }

        if (inforProject == null && devices.size() <= 0 && totalPowers.size() <= 0) {
            return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
        }

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(totalPowers.size() + 50);
        SXSSFSheet sheet1 = wb.createSheet("Danh sách thiết bị");
        SXSSFSheet sheet = wb.createSheet("Thông tin tổng công suất và năng lượng");
        Row row;
        Cell cell;

        // Tạo sheet danh sách thiết bị
        for (int i = 0; i < 8; i++) {
            row = sheet1.createRow(i);
            for (int j = 0; j < 5; j++) {
                row.createCell(j);
            }
        }

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 4);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        cell.setCellValue("DANH SÁCH THIẾT BỊ");
        formatHeader(wb, region, sheet1, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(2, 2, 0, 0);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue("Dự án");
        formatHeader(wb, region, sheet1, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0);

        // Thông tin dự án
        if (inforProject != null) {
            region = new CellRangeAddress(2, 2, 1, 4);
            sheet1.addMergedRegion(region);
            cell = sheet1.getRow(2)
                .getCell(1);
            cell.setCellValue(" " + inforProject.get("customerName") + " -> " + inforProject.get("superManagerName")
                + " -> " + inforProject.get("managerName") + " -> " + inforProject.get("areaName") + " -> "
                + inforProject.get("projectName"));
        }

        region = new CellRangeAddress(6, 6, 0, 1);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(6)
            .getCell(0);
        cell.setCellValue("Tên thiết bị");
        formatHeader(wb, region, sheet1, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(6, 6, 2, 4);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(6)
            .getCell(2);
        cell.setCellValue("Mã thiết bị");
        formatHeader(wb, region, sheet1, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0);

        // ghi data vào sheet1
        int index = 7;
        if (devices.size() > 0) {
            for (Device device : devices) {
                row = sheet1.createRow(index);
                for (int i = 0; i < 5; i++) {
                    cell = row.createCell(i);
                }
                region = new CellRangeAddress(index, index, 0, 1);
                sheet1.addMergedRegion(region);
                cell = sheet1.getRow(index)
                    .getCell(0);
                cell.setCellValue(device.getDeviceName());

                region = new CellRangeAddress(index, index, 2, 4);
                sheet1.addMergedRegion(region);
                cell = sheet1.getRow(index)
                    .getCell(2);
                cell.setCellValue(device.getDeviceCode());

                index++;
            }
        }

        // auto size column
        for (int i = 0; i < 5; i++) {
            sheet1.trackAllColumnsForAutoSizing();
            sheet1.autoSizeColumn(i, true);
        }

        // Tạo header báo cáo sheet báo cáo công suất năng lượng
        for (int i = 0; i < 8; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 3; j++) {
                row.createCell(j);
            }
        }

        region = new CellRangeAddress(0, 0, 0, 2);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(0)
            .getCell(0);
        cell.setCellValue("BÁO CÁO CÔNG SUẤT NĂNG LƯỢNG");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(3, 3, 0, 0);
        cell = sheet.getRow(3)
            .getCell(0);
        cell.setCellValue("Ngày");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(3, 3, 1, 2);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(1);
        cell.setCellValue(day);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(6, 6, 0, 0);
        // sheet.addMergedRegion(region);
        cell = sheet.getRow(6)
            .getCell(0);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(6, 6, 1, 1);
        // sheet.addMergedRegion(region);
        cell = sheet.getRow(6)
            .getCell(1);
        cell.setCellValue("Công suất [kW]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(6, 6, 2, 2);
        // sheet.addMergedRegion(region);
        cell = sheet.getRow(6)
            .getCell(2);
        cell.setCellValue("Điện năng [kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0);

        // ghi data vào excel
        int rowCount = 7;
        if (totalPowers.size() > 0) {
            for (OverviewLoadTotalPower data : totalPowers) {
                Row rowData = sheet.createRow(rowCount);
                for (int i = 0; i < 3; i++) {
                    rowData.createCell(i);
                }
                region = new CellRangeAddress(rowCount, rowCount, 0, 0);
                Cell cellData = sheet.getRow(rowCount)
                    .getCell(0);
                cellData.setCellValue(data.getTime());
                CellStyle cs = wb.createCellStyle();
                cs.setAlignment(HorizontalAlignment.RIGHT);
                cellData.setCellStyle(cs);

                region = new CellRangeAddress(rowCount, rowCount, 1, 1);
                cellData = sheet.getRow(rowCount)
                    .getCell(1);
                cellData.setCellValue(data.getPower());

                region = new CellRangeAddress(rowCount, rowCount, 2, 2);
                cellData = sheet.getRow(rowCount)
                    .getCell(2);
                cellData.setCellValue(data.getEnergy());
                rowCount++;
            }
        }

        // auto size column
        for (int i = 0; i < 3; i++) {
            sheet.trackAllColumnsForAutoSizing();
            sheet.autoSizeColumn(i, true);
        }

        File folder = new File(folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        } else {
            // Xóa file cũ
            deleteDir(folder);
        }

        // export file
        long currentTime = now.getTime();
        String exportFolderPath = folderName + File.separator + currentTime;
        File exportFolder = new File(exportFolderPath);
        if (!exportFolder.exists()) {
            exportFolder.mkdirs();
        }
        File file = new File(exportFolderPath + File.separator + "CanhBaoCongSuatNangLuong.xlsx");
        FileOutputStream outFile = new FileOutputStream(file);
        wb.write(outFile);
        outFile.close();
        wb.dispose();
        wb.close();

        // zip file
        ZipUtil.pack(exportFolder, new File(exportFolderPath + ".zip"));

        // gửi file lên client
        String contentType = "application/zip";
        String headerValue = "attachment; filename=" + currentTime + ".zip";

        Path path = Paths.get(exportFolderPath + ".zip");
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("exportTotalPowerEnergyToExcel END");

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            .body(resource);
    }
    // CHECKSTYLE:ON

    /**
     * Xóa tất cả file và thư mục trong thu mục cha, không xóa thu mục cha
     *
     * @param file
     */
    public static void deleteDir(final File file) {
        try {
            // Xóa tất cả file và thư mục trong thu mục cha, không xóa thu mục cha
            FileUtils.cleanDirectory(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping ("/powerTotal/download/{customerId}/{projectId}")
    public ResponseEntity<Resource> downloadPowerTotal(@PathVariable final String customerId,
        @PathVariable final String projectId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @RequestBody User user) throws Exception {

        log.info("exportTotalPowerEnergyToExcel START");

        // time miliseconds
        long miliseconds = new Date().getTime();

        // get url image
        String pngImageURL = user.getImg();
        String encodingPrefix = "base64,";
        int contentStartIndex = pngImageURL.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData1 = org.apache.commons.codec.binary.Base64.decodeBase64(pngImageURL.substring(
            contentStartIndex));

        // path folder
        String path = this.folderName + File.separator + miliseconds;

        Date now = new Date();
        List<OverviewLoadTotalPower> totalPowers = new ArrayList<>();
        Map<String, Object> condition = new HashMap<>();
        condition.put("schema", Schema.getSchemas(Integer.parseInt(customerId)));
        condition.put("projectId", projectId);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormatView = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat dateFormatToHourMinute = new SimpleDateFormat("HH:mm");
        String endDay = toDate + " 23:59:59";
        Date date = null;
        Date endDate = null;
        try {
            date = dateFormatWithTime.parse(fromDate + " 00:00:00");
            endDate = dateFormatWithTime.parse(endDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Long transactionDate = date.getTime() / MILLISECOND;
        Long transactionEndDate = null;
        // so sánh ngày truy vấn với ngày hiện tại
        try {
            transactionEndDate = dateFormat.parse(toDate)
                .before(dateFormat.parse(dateFormat.format(now)))
                    ? endDate.getTime() / MILLISECOND
                    : now.getTime() / MILLISECOND;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        condition.put("from", new Date( (transactionDate - TRANSACTION_DATE_24_HOURS) * 1000));
        condition.put("to", endDate);
        List<OverviewLoadTotalPower> listPower = overviewPowerService.getListPowerInDay(condition);
        Map<Long, OverviewLoadTotalPower> mapPower = new HashMap<>();
        Map<MultiKey, OverviewLoadTotalPower> mapPowerCache = new HashMap<MultiKey, OverviewLoadTotalPower>();
        for (OverviewLoadTotalPower item : listPower) {
            MultiKey key = new MultiKey(item.getViewTime()
                .getTime() / MILLISECOND, item.getDeviceId());
            if (mapPowerCache.get(key) == null) {
                mapPowerCache.put(key, item);
            } else {
                if (mapPowerCache.get(key)
                    .getId() < item.getId()) {
                    mapPowerCache.put(key, item);
                }
            }
        }
        for (Map.Entry<MultiKey, OverviewLoadTotalPower> entry : mapPowerCache.entrySet()) {
            if (mapPower.get(entry.getKey()
                .getKey(0)) == null) {
                mapPower.put((Long) entry.getKey()
                    .getKey(0), entry.getValue());
            } else {
                Long power = mapPower.get(entry.getKey()
                    .getKey(0))
                    .getPower();
                Long energy = mapPower.get(entry.getKey()
                    .getKey(0))
                    .getEnergy();
                power += entry.getValue()
                    .getPower();
                energy += entry.getValue()
                    .getEnergy();
                mapPower.get(entry.getKey()
                    .getKey(0))
                    .setPower(power);
                mapPower.get(entry.getKey()
                    .getKey(0))
                    .setEnergy(energy);
            }
        }

        Map<Long, Forecast> mapForecast = getForecastPower(new Date(transactionDate * 1000), endDate, projectId,
            customerId);
        // truy vấn data theo các mốc thời gian 15p
        while (transactionDate <= transactionEndDate) {
            OverviewLoadTotalPower newTotalPower = new OverviewLoadTotalPower();
            OverviewLoadTotalPower oldTotalPower = new OverviewLoadTotalPower();

            Long sumPower = (long) 0;
            Long sumEnergy = (long) 0;
            Long sumOldPower = (long) 0;
            Long sumOldEnergy = (long) 0;
            if (mapPower.get(transactionDate) != null) {
                OverviewLoadTotalPower itemPower = mapPower.get(transactionDate);
                sumPower = itemPower.getPower();
                sumEnergy = itemPower.getEnergy();
            }
            if (mapPower.get(transactionDate - TRANSACTION_DATE_24_HOURS) != null) {
                OverviewLoadTotalPower itemPower = mapPower.get(transactionDate - TRANSACTION_DATE_24_HOURS);
                sumOldEnergy = itemPower.getEnergy();
                sumOldPower = itemPower.getPower();
            }

            String timeView = dateFormatView.format(new Date(transactionDate * MILLISECOND));
            newTotalPower.setTime(timeView);
            newTotalPower.setEnergy(sumEnergy != null ? sumEnergy : 0);
            newTotalPower.setPower(sumPower != null ? sumPower : 0);

            String timeForecast = dateFormatToHourMinute.format(new Date(transactionDate * MILLISECOND));
            Forecast f = mapForecast.get(transactionDate) != null ? mapForecast.get(transactionDate) : null;
            oldTotalPower.setPower(sumOldPower != null ? sumOldPower : 0);
            oldTotalPower.setEnergy(sumOldEnergy != null ? sumOldEnergy : 0);
            Long pforecast = (long) 0;
            if (f != null) {
                pforecast = totalPowerForecast(timeForecast, dateFormat, f, endDay, oldTotalPower);
            } else {
                f = new Forecast();
                f.setA0((double) 0);
                f.setA1((double) 0);
                f.setA2((double) 0);
                f.setA3((double) 0);
                f.setA4((double) 0);
                f.setA5((double) 0);
                f.setA6((double) 0);
                f.setA7((double) 0);
                f.setA8((double) 0);
                f.setA9((double) 1);
                pforecast = totalPowerForecast(timeForecast, dateFormat, f, endDay, oldTotalPower);
            }

            newTotalPower.setForecast(pforecast > 0 ? pforecast : 0);

            totalPowers.add(newTotalPower);

            transactionDate += FIFTEEN_MINUTE;
        }

        if (totalPowers != null && totalPowers.size() > 0) {

            createExeclTotalPower(totalPowers, fromDate, toDate, path, imageData1, miliseconds);

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

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .body(resource);
        } else {
            return ResponseEntity.badRequest()
                .body(null);
        }

    }

    private void createExeclTotalPower(final List<OverviewLoadTotalPower> data, final String fromDate,
        final String toDate, final String path, final byte[] imageData, Long miliseconds) {
        log.info("createExeclTotalPower START");

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(data.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Thông tin tổng công suất và năng lượng");
        Row row;
        Cell cell;

        // add image
        int pictureIdx = wb.addPicture(imageData, wb.PICTURE_TYPE_PNG);
        SXSSFDrawing drawingImg = sheet.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(3);
        anchorImg.setCol2(4);
        anchorImg.setRow1(1);
        anchorImg.setRow2(4);

        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);

        // Tạo header báo cáo sheet báo cáo công suất năng lượng
        for (int i = 0; i < 8; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 4; j++) {
                row.createCell(j);
            }
        }

        // set độ rộng của cột
        sheet.setColumnWidth(0, 5200);

        // set độ rộng của hàng
        Row row1 = sheet.getRow(1);
        row1.setHeight((short) -500);
        Row row2 = sheet.getRow(2);
        row2.setHeight((short) -500);
        Row row3 = sheet.getRow(3);
        row3.setHeight((short) -500);
        Row row5 = sheet.getRow(5);
        row5.setHeight((short) -400);

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 3);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(0)
            .getCell(0);
        cell.setCellValue("BÁO CÁO CÔNG SUẤT NĂNG LƯỢNG");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(2, 3, 0, 0);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(0);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(2, 2, 1, 2);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(1);
        cell.setCellValue(fromDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(3, 3, 1, 2);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(1);
        cell.setCellValue(toDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(6, 6, 0, 0);
        // sheet.addMergedRegion(region);
        cell = sheet.getRow(6)
            .getCell(0);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(6, 6, 1, 1);
        // sheet.addMergedRegion(region);
        cell = sheet.getRow(6)
            .getCell(1);
        cell.setCellValue("Công suất [kW]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(6, 6, 2, 2);
        // sheet.addMergedRegion(region);
        cell = sheet.getRow(6)
            .getCell(2);
        cell.setCellValue("Điện năng [kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(6, 6, 3, 3);
        // sheet.addMergedRegion(region);
        cell = sheet.getRow(6)
            .getCell(3);
        cell.setCellValue("Dự báo [kW]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0);

        // ghi data vào excel
        int rowCount = 7;
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Courier New");
        cs.setFont(font);
        if (data.size() > 0) {
            for (OverviewLoadTotalPower data1 : data) {
                Row rowData = sheet.createRow(rowCount);
                for (int i = 0; i < 4; i++) {
                    rowData.createCell(i, CellType.BLANK)
                        .setCellStyle(cs);
                }
                region = new CellRangeAddress(rowCount, rowCount, 0, 0);
                Cell cellData = sheet.getRow(rowCount)
                    .getCell(0);
                cellData.setCellValue(data1.getTime());
                cs.setAlignment(HorizontalAlignment.RIGHT);
                cellData.setCellStyle(cs);

                region = new CellRangeAddress(rowCount, rowCount, 1, 1);
                cellData = sheet.getRow(rowCount)
                    .getCell(1);
                float power = (float) data1.getPower() / 1000;
                cellData.setCellValue(power != 0 ? String.valueOf(power) : String.valueOf(0));

                region = new CellRangeAddress(rowCount, rowCount, 2, 2);
                cellData = sheet.getRow(rowCount)
                    .getCell(2);
                cellData.setCellValue(String.valueOf(data1.getEnergy()));

                region = new CellRangeAddress(rowCount, rowCount, 3, 3);
                cellData = sheet.getRow(rowCount)
                    .getCell(3);
                float forecast = (float) data1.getForecast() / 1000;
                cellData.setCellValue(forecast != 0 ? String.valueOf(forecast) : String.valueOf(0));
                rowCount++;
            }
        }
        // auto size column
        for (int i = 0; i < 4; i++) {
            sheet.trackAllColumnsForAutoSizing();
            sheet.autoSizeColumn(i, true);
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
            log.info("WarningController: Create file excel success");
        } catch (FileNotFoundException e) {
            log.error("WarningController: ERROR File Not Found while export file excel.");
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

    /**
     * Format header cho file excel
     *
     * @param wb file excel
     * @param region
     * @param sheet
     * @param cell
     * @param bgColor
     * @param hAlign
     * @param indent
     */
    private void formatHeader(final SXSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet, final Cell cell,
        final short bgColor, final HorizontalAlignment hAlign, final int indent) {

        CellStyle cs = wb.createCellStyle();
        cs.setFillBackgroundColor(bgColor);
        cs.setFillForegroundColor(bgColor);
        cs.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);

        Font font = wb.createFont();
        font.setBold(true);
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
