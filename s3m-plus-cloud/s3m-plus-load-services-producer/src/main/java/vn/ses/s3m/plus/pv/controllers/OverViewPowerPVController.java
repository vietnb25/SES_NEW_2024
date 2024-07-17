package vn.ses.s3m.plus.pv.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import vn.ses.s3m.plus.dto.DataEnergyPV;
import vn.ses.s3m.plus.dto.DataInverter1;
import vn.ses.s3m.plus.dto.DataString1;
import vn.ses.s3m.plus.dto.DataWeather1;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.Forecast;
import vn.ses.s3m.plus.dto.OverviewPVPower;
import vn.ses.s3m.plus.dto.OverviewPVTotalPower;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.pv.service.DevicePVService;
import vn.ses.s3m.plus.pv.service.OperationCombinerPVService;
import vn.ses.s3m.plus.pv.service.OperationInverterPVService;
import vn.ses.s3m.plus.pv.service.OperationStringPVService;
import vn.ses.s3m.plus.pv.service.OperationWeatherPVService;
import vn.ses.s3m.plus.pv.service.OverviewPowerPVService;

@RestController
@RequestMapping ("/pv")
public class OverViewPowerPVController {
    static final int MILLISECOND = 1000;

    static final int FIFTEEN_MINUTE = 900;

    static final int THIRTY_MINUTE = 1800;

    static final int DEVICE_INVERTER_TYPE = 1;

    static final int DEVICE_WEATHER_TYPE = 2;

    static final int DEVICE_COMBINER_TYPE = 3;

    static final int DEVICE_STRING_TYPE = 4;

    static final int DEVICE_PANEL_TYPE = 5;

    private String folderName;

    // Logging
    private final Log log = LogFactory.getLog(OverViewPowerPVController.class);

    @Autowired
    private OverviewPowerPVService overviewPowerService;

    @Autowired
    private DevicePVService deviceService;

    @Autowired
    private OperationInverterPVService inverterService;

    @Autowired
    private OperationWeatherPVService weatherService;

    @Autowired
    private OperationCombinerPVService combinerService;

    @Autowired
    private OperationStringPVService stringService;

    // Các tham số tính dự báo
    private static final int MAX_DAY = 7;

    private static final int MAX_SECOND = 60;

    private static final int MAX_MONH = 12;

    private static final int MAX_HOURS = 24;

    private static final int TRANSACTION_DATE_24_HOURS = 86400;

    /**
     * Lấy thông tin công suất của từng thiết bị trong dự án PV trong ngày
     *
     * @param projectId ID dự án
     * @return devicePowers thông tin công suất các thiết bị
     */
    @GetMapping ("/power/{customerId}/{projectId}")
    public ResponseEntity<List<OverviewPVPower>> getPowerPVInDay(@PathVariable final String customerId,
        @PathVariable final String projectId, @RequestParam (required = false) final String keyword) {

        log.info("getPowerInDay START");
        Map<String, Object> condition = new HashMap<>();
        condition.put("schema", Schema.getSchemas(Integer.parseInt(customerId)));
        condition.put("projectId", projectId);

        if (keyword != null) {
            condition.put("keyword", keyword);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date timeStamp = Calendar.getInstance()
            .getTime();
        Long to = timeStamp.getTime() / 1000;
        Long from = to - THIRTY_MINUTE;
        condition.put("from", from);
        condition.put("to", to);
        int currentYear = Year.now()
            .getValue();
        condition.put("year", currentYear);

        List<Device> devices = deviceService.getDeviceByProjectId(condition);
        List<String> deviceIds = new ArrayList<>();
        List<String> systemMapIds = new ArrayList<>();
        List<OverviewPVPower> devicePowers = new ArrayList<>();

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
        }
        List<OverviewPVPower> layers = new ArrayList<>();

        if (systemMapIds.size() > 0) {
            condition.put("systemMapIds", systemMapIds);
            layers = overviewPowerService.getLayer(condition);
        }

        condition.put("fromTime", formatter.format(new Date(from * 1000)));
        condition.put("toTime", formatter.format(new Date(to * 1000)));
        List<OverviewPVPower> warnings = overviewPowerService.getDeviceHasWarning(condition);

        for (Device device : devices) {
            OverviewPVPower devicePower = new OverviewPVPower();
            List<OverviewPVPower> overview = null;
            Float W = null;
            Float DCW = null;
            Float efficiency = null;
            Float Wh = null;
            Float Temp = null;
            Float H = null;
            Float Rad = null;
            condition.put("deviceId", device.getDeviceId());

            if (device.getDeviceType() == DEVICE_INVERTER_TYPE) {
                overview = overviewPowerService.getPowerPV(condition);
                if (overview != null) {
                    for (OverviewPVPower power : overview) {
                        W = power.getW() != null ? power.getW() : null;
                        DCW = power.getDCW() != null ? power.getDCW() : null;
                        Wh = power.getWh();
                        efficiency = W != null && DCW != null && DCW > 0 ? (W / DCW) : null;
                    }
                }
            } else if (device.getDeviceType() == DEVICE_COMBINER_TYPE) {
                overview = overviewPowerService.getPowerCombiner(condition);
                System.out.println("overview: " + overview);
                if (overview != null) {
                    for (OverviewPVPower power : overview) {
                        W = power.getW() != null ? power.getW() : null;
                        DCW = power.getDCW() != null ? power.getDCW() : null;
                        Wh = power.getWh();
                        efficiency = null;
                    }
                }
            } else if (device.getDeviceType() == DEVICE_STRING_TYPE) {
                overview = overviewPowerService.getPowerString(condition);
                System.out.println("overview: " + overview);
                if (overview != null) {
                    for (OverviewPVPower power : overview) {
                        W = power.getW() != null ? power.getW() : null;
                        DCW = power.getDCW() != null ? power.getDCW() : null;
                        Wh = power.getWh();
                        efficiency = null;
                    }
                }
            } else if (device.getDeviceType() == DEVICE_WEATHER_TYPE) {
                overview = overviewPowerService.getOverviewPowerWeather(condition);
                if (overview != null) {
                    for (OverviewPVPower power : overview) {
                        Temp = power.getTEMP() != null ? power.getTEMP() : null;
                        H = power.getH() != null ? power.getH() : null;
                        Rad = power.getRad() != null ? power.getRad() : null;
                    }
                }
            }

            devicePower.setDeviceId(device.getDeviceId());
            devicePower.setDeviceName(device.getDeviceName());
            devicePower.setSystemMapId(device.getSystemMapId());
            devicePower.setProjectId(Integer.parseInt(projectId));
            devicePower.setDeviceType(device.getDeviceType());
            devicePower.setTEMP(Temp);
            devicePower.setH(H);
            devicePower.setRad(Rad);

            if (layers.size() > 0) {
                for (OverviewPVPower layer : layers) {
                    if (layer.getSystemMapId() == device.getSystemMapId()) {
                        devicePower.setSystemMapId(device.getSystemMapId());
                        devicePower.setLayer(layer.getLayer());
                        devicePower.setSystemMapName(layer.getSystemMapName());
                    }
                }
            }

            if (W != null && DCW != null & Wh != null) {
                devicePower.setEfficiency(efficiency);
                devicePower.setW(W);
                devicePower.setDCW(DCW);
                devicePower.setWh(Wh);
            } else if (DCW != null & Wh != null) {
                devicePower.setEfficiency(efficiency);
                devicePower.setDCW(DCW);
                devicePower.setWh(Wh);
            }

            if (devicePower.getW() == null && devicePower.getDCW() == null && devicePower.getWh() == null
                && devicePower.getDeviceType() != DEVICE_WEATHER_TYPE) {
                devicePower.setLoadStatus("error");
            } else if (devicePower.getTEMP() == null && devicePower.getH() == null && devicePower.getRad() == null
                && devicePower.getDeviceType() == DEVICE_WEATHER_TYPE) {
                devicePower.setLoadStatus("error");
            } else if (warnings.size() > 0) {
                for (OverviewPVPower warning : warnings) {
                    if (warning.getDeviceId()
                        .equals(device.getDeviceId())) {
                        if (warning.getWarningCount() > 0) {
                            devicePower.setLoadStatus("warning");
                        }
                    } else {
                        String status = devicePower.getLoadStatus();
                        if (status == null) {
                            devicePower.setLoadStatus("active");
                        }
                    }
                }
            } else {
                devicePower.setLoadStatus("active");
            }
            devicePowers.add(devicePower);
        }

        log.info("getPowerInDay END");

        return new ResponseEntity<List<OverviewPVPower>>(devicePowers, HttpStatus.OK);
    }

    /**
     * Lấy thông tin tổng công suất, năng lượng các thiết bị trong ngày thuộc dự án PV
     *
     * @param projectId ID dự án
     * @param day Ngày để lấy dữ liệu
     * @return thông tin tổng công suất, dữ liệu theo các mốc thời gian trong ngày
     */
    @GetMapping ("/powerTotal/{customerId}/{projectId}")
    public ResponseEntity<List<OverviewPVTotalPower>> getTotalPowerEnergyPV(@PathVariable final String customerId,
        @PathVariable final String projectId, @RequestParam final String fromDate, @RequestParam final String toDate)
        throws Exception {

        log.info("getTotalPowerEnergy START");
        List<OverviewPVTotalPower> totalPowers = new ArrayList<>();

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

        // System.out.println("transactionEndDate: " + transactionEndDate);

        // get list power, enery
        condition.put("from", new Date( (transactionDate - TRANSACTION_DATE_24_HOURS) * 1000));
        condition.put("to", endDate);
        List<OverviewPVTotalPower> listPowerInverter = overviewPowerService.getListPowerInDay(condition);
        List<OverviewPVTotalPower> listPowerCombiner = overviewPowerService.getListPowerCombinerInDay(condition);
        List<OverviewPVTotalPower> listPowerString = overviewPowerService.getListPowerStringInDay(condition);
        List<OverviewPVTotalPower> listPower = new ArrayList<>();
        listPower.addAll(listPowerInverter);
        listPower.addAll(listPowerCombiner);
        listPower.addAll(listPowerString);

        Map<Long, OverviewPVTotalPower> mapPower = new HashMap<>();
        for (OverviewPVTotalPower item : listPower) {
            if (mapPower.get(item.getViewTime()
                .getTime() / MILLISECOND) == null) {
                mapPower.put(item.getViewTime()
                    .getTime() / MILLISECOND, item);
            } else {
                Long itemPower = item.getPower() != null ? item.getPower() : 0;
                Long itemEnergy = item.getEnergy() != null ? item.getEnergy() : 0;
                Long viewTimePower = mapPower.get(item.getViewTime()
                    .getTime() / MILLISECOND)
                    .getPower() != null
                        ? mapPower.get(item.getViewTime()
                            .getTime() / MILLISECOND)
                            .getPower()
                        : 0;
                Long viewTimeEnergy = mapPower.get(item.getViewTime()
                    .getTime() / MILLISECOND)
                    .getEnergy() != null
                        ? mapPower.get(item.getViewTime()
                            .getTime() / MILLISECOND)
                            .getEnergy()
                        : 0;
                long sumPower = viewTimePower + itemPower;
                long sumEnergy = viewTimeEnergy + itemEnergy;
                mapPower.get(item.getViewTime()
                    .getTime() / MILLISECOND)
                    .setEnergy(sumEnergy);
                mapPower.get(item.getViewTime()
                    .getTime() / MILLISECOND)
                    .setPower(sumPower);
            }
        }

        Map<Long,
            Forecast> mapForecast = getForecastPower(new Date(transactionDate * 1000), endDate, projectId, customerId);
        // truy vấn data theo các mốc thời gian 15p
        while (transactionDate <= transactionEndDate) {
            OverviewPVTotalPower newTotalPower = new OverviewPVTotalPower();
            OverviewPVTotalPower oldTotalPower = new OverviewPVTotalPower();

            Long sumPower = (long) 0;
            Long sumEnergy = (long) 0;
            Long sumOldPower = (long) 0;
            Long sumOldEnergy = (long) 0;
            if (mapPower.get(transactionDate) != null) {
                OverviewPVTotalPower itemPower = mapPower.get(transactionDate);
                sumPower = itemPower.getPower();
                sumEnergy = itemPower.getEnergy();
            }
            if (mapPower.get(transactionDate - TRANSACTION_DATE_24_HOURS) != null) {
                OverviewPVTotalPower itemPower = mapPower.get(transactionDate - TRANSACTION_DATE_24_HOURS);
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

        return new ResponseEntity<List<OverviewPVTotalPower>>(totalPowers, HttpStatus.OK);
    }

    /**
     * Lấy gía trị điện năng tổng của tất cả các thiết bị trong dự án PV trong ngày
     *
     * @param projectId ID dự án
     * @return inverter.Wh điện năng tổng của tất cả các thiết bị trong dự án
     */
    @GetMapping ("/energy/{customerId}/{projectId}")
    public ResponseEntity<DataEnergyPV> getTotalACEnergy(@PathVariable final String customerId,
        @PathVariable final String projectId) {
        Map<String, Object> condition = new HashMap<>();
        DataEnergyPV energy = new DataEnergyPV();
        condition.put("projectId", projectId);
        condition.put("schema", Schema.getSchemas(Integer.parseInt(customerId)));

        // get project infor
        Map<String, String> projectTree = overviewPowerService.getInformationProject(condition);
        String projectInfor = projectTree.get("customerName") + " / " + projectTree.get("superManagerName") + " / "
            + projectTree.get("managerName") + " / " + projectTree.get("areaName") + " / "
            + projectTree.get("projectName") + " / " + "PV";

        System.out.println("projectInfor: " + projectInfor);

        List<Device> devices = deviceService.getDeviceByProjectId(condition);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter yearFormat = DateTimeFormatter.ofPattern("yyyy");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime prevDayDate = now.minusDays(1);
        LocalDateTime prevMonthDate = now.minusMonths(1);
        LocalDateTime prevYearDate = now.minusYears(1);

        String day = dateFormat.format(now);
        String month = monthFormat.format(now);
        String year = yearFormat.format(now);
        String prevDay = dateFormat.format(prevDayDate);
        String prevMonth = monthFormat.format(prevMonthDate);
        String prevYear = yearFormat.format(prevYearDate);
        condition.put("day", day);
        condition.put("month", month);
        condition.put("year", year);
        condition.put("prevDay", prevDay);
        condition.put("prevMonth", prevMonth);
        condition.put("prevYear", prevYear);

        Float totalWh = (float) 0;
        Float totalWhDay = (float) 0;
        Float totalWhMonth = (float) 0;
        Float totalWhYear = (float) 0;
        Float totalWhPrevDay = (float) 0;
        Float totalWhPrevMonth = (float) 0;
        Float totalWhPrevYear = (float) 0;
        Float rad = (float) 0;
        Float temp = (float) 0;
        Float tStr = (float) 0;
        DataWeather1 dataWeather = weatherService.getInstantOperationWeatherInProjectId(condition);
        if (dataWeather != null) {
            rad = (float) dataWeather.getRad();
            temp = (float) dataWeather.getTemp();
        }

        DataString1 dataString = stringService.getInstantOperationStringInProjectId(condition);
        if (dataString != null) {
            tStr = dataString.getTstr();
        }
        for (Device device : devices) {
            condition.put("deviceId", device.getDeviceId());
            List<DataInverter1> inverters = inverterService.getInverterEveryYearByDeviceId(condition);
            if (inverters != null) {
                for (DataInverter1 inverter : inverters) {
                    totalWh += inverter.getEp();
                }
            }
            List<DataInverter1> invertersDay = inverterService.getInverterInDayByDeviceId(condition);
            if (invertersDay != null) {
                for (DataInverter1 inverter : invertersDay) {
                    totalWhDay += inverter.getEp();
                }
            }
            List<DataInverter1> invertersMonth = inverterService.getInverterInMonthByDeviceId(condition);
            if (invertersMonth != null) {
                for (DataInverter1 inverter : invertersMonth) {
                    totalWhMonth += inverter.getEp();
                }
            }
            List<DataInverter1> invertersYear = inverterService.getInverterInYearByDeviceId(condition);
            if (invertersYear != null) {
                for (DataInverter1 inverter : invertersYear) {
                    totalWhYear += inverter.getEp();
                }
            }
            List<DataInverter1> invertersPrevDay = inverterService.getInverterInPrevDayByDeviceId(condition);
            if (invertersPrevDay != null) {
                for (DataInverter1 inverter : invertersPrevDay) {
                    totalWhPrevDay += inverter.getEp();
                }
            }
            List<DataInverter1> invertersPrevMonth = inverterService.getInverterInPrevMonthByDeviceId(condition);
            if (invertersPrevMonth != null) {
                for (DataInverter1 inverter : invertersPrevMonth) {
                    totalWhPrevMonth += inverter.getEp();
                }
            }
            List<DataInverter1> invertersPrevYear = inverterService.getInverterInPrevYearByDeviceId(condition);
            if (invertersPrevYear != null) {
                for (DataInverter1 inverter : invertersPrevYear) {
                    totalWhPrevYear += inverter.getEp();
                }
            }
        }
        energy.setWh(totalWh);
        energy.setWhDay(totalWhDay);
        energy.setWhMonth(totalWhMonth);
        energy.setWhYear(totalWhYear);
        energy.setWhPrevDay(totalWhPrevDay);
        energy.setWhPrevMonth(totalWhPrevMonth);
        energy.setWhPrevYear(totalWhPrevYear);
        energy.setRad(rad);
        energy.setTemp(temp);
        energy.setTStr(tStr);
        energy.setInfor(projectInfor);
        return new ResponseEntity<DataEnergyPV>(energy, HttpStatus.OK);
    }

    public Map<Long, Forecast> getForecastPower(Date from, Date to, String projectId, String customerId)
        throws Exception {

        Map<String, Object> condition1 = new HashMap<>();
        Map<Long, Forecast> result = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        condition1.put("schema", Schema.getSchemas(Integer.parseInt(customerId)));
        condition1.put("systemTypeId", 2);
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
        final String day, final OverviewPVTotalPower item) {

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

        Long pforecast = (long) (f.getA0() + f.getA1() * Math.sin(2 * Math.PI * hourForecast / MAX_HOURS)
            + f.getA2() * Math.cos(2 * Math.PI * hourForecast / MAX_HOURS)
            + f.getA3() * Math.sin(2 * Math.PI * dayForecast / MAX_DAY)
            + f.getA4() * Math.cos(2 * Math.PI * dayForecast / MAX_DAY)
            + f.getA5() * Math.sin(2 * Math.PI * monthForecast / MAX_MONH)
            + f.getA6() * Math.cos(2 * Math.PI * monthForecast / MAX_MONH) + f.getA7() * yearForecast
            + f.getA8() * isWeekend + f.getA9() * priorDay);

        return pforecast;
    }

    @PostMapping ("/powerTotal/download/{customerId}/{projectId}")
    public ResponseEntity<Resource> downloadPowerTotalPV(@PathVariable String customerId,
        @PathVariable String projectId, @RequestParam (required = false) String fromDate,
        @RequestParam (required = false) String toDate, @RequestBody User user) throws Exception {

        log.info("exportTotalPowerEnergyToExcel START");

        // time miliseconds
        long miliseconds = new Date().getTime();

        // get url image
        String pngImageURL = user.getImg();
        String encodingPrefix = "base64,";
        int contentStartIndex = pngImageURL.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData1 = org.apache.commons.codec.binary.Base64
            .decodeBase64(pngImageURL.substring(contentStartIndex));

        // path folder
        String path = this.folderName + File.separator + miliseconds;

        Date now = new Date();
        List<OverviewPVTotalPower> totalPowers = new ArrayList<>();
        Map<String, Object> condition = new HashMap<>();
        condition.put("projectId", projectId);
        condition.put("schema", Schema.getSchemas(Integer.parseInt(customerId)));

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
        List<OverviewPVTotalPower> listPowerInverter = overviewPowerService.getListPowerInDay(condition);
        List<OverviewPVTotalPower> listPowerCombiner = overviewPowerService.getListPowerCombinerInDay(condition);
        List<OverviewPVTotalPower> listPowerString = overviewPowerService.getListPowerStringInDay(condition);
        List<OverviewPVTotalPower> listPower = new ArrayList<>();
        listPower.addAll(listPowerInverter);
        listPower.addAll(listPowerCombiner);
        listPower.addAll(listPowerString);

        Map<Long, OverviewPVTotalPower> mapPower = new HashMap<>();
        for (OverviewPVTotalPower item : listPower) {
            if (mapPower.get(item.getViewTime()
                .getTime() / MILLISECOND) == null) {
                mapPower.put(item.getViewTime()
                    .getTime() / MILLISECOND, item);
            } else {
                long sumPower = mapPower.get(item.getViewTime()
                    .getTime() / MILLISECOND)
                    .getPower() + item.getPower();
                long sumEnergy = mapPower.get(item.getViewTime()
                    .getTime() / MILLISECOND)
                    .getEnergy() + item.getEnergy();
                mapPower.get(item.getViewTime()
                    .getTime() / MILLISECOND)
                    .setEnergy(sumEnergy);
                mapPower.get(item.getViewTime()
                    .getTime() / MILLISECOND)
                    .setPower(sumPower);
            }
        }

        Map<Long,
            Forecast> mapForecast = getForecastPower(new Date(transactionDate * 1000), endDate, projectId, customerId);

        // truy vấn data theo các mốc thời gian 15p
        while (transactionDate <= transactionEndDate) {
            OverviewPVTotalPower newTotalPower = new OverviewPVTotalPower();
            OverviewPVTotalPower oldTotalPower = new OverviewPVTotalPower();

            Long sumPower = (long) 0;
            Long sumEnergy = (long) 0;
            Long sumOldPower = (long) 0;
            Long sumOldEnergy = (long) 0;
            if (mapPower.get(transactionDate) != null) {
                OverviewPVTotalPower itemPower = mapPower.get(transactionDate);
                sumPower = itemPower.getPower();
                sumEnergy = itemPower.getEnergy();
            }
            if (mapPower.get(transactionDate - TRANSACTION_DATE_24_HOURS) != null) {
                OverviewPVTotalPower itemPower = mapPower.get(transactionDate - TRANSACTION_DATE_24_HOURS);
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

            createExeclTotalPower(totalPowers, fromDate, toDate, path, imageData1);

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

    private void createExeclTotalPower(final List<OverviewPVTotalPower> data, final String fromDate,
        final String toDate, final String path, final byte[] imageData) {
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

        // set độ rộng của hàng
        Row row1 = sheet.getRow(1);
        row1.setHeight((short) -500);
        Row row2 = sheet.getRow(2);
        row2.setHeight((short) -500);
        Row row3 = sheet.getRow(3);
        row3.setHeight((short) -500);

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
            for (OverviewPVTotalPower data1 : data) {
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
                cellData.setCellValue(data1.getPower() / 1000);

                region = new CellRangeAddress(rowCount, rowCount, 2, 2);
                cellData = sheet.getRow(rowCount)
                    .getCell(2);
                cellData.setCellValue(data1.getEnergy() / 1000);

                region = new CellRangeAddress(rowCount, rowCount, 3, 3);
                cellData = sheet.getRow(rowCount)
                    .getCell(3);
                cellData.setCellValue(data1.getForecast() / 1000);
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
        String exportFilePath = path + File.separator + "CanhBaoVanHanh.xlsx";

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
        font.setFontName("Courier New");
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
