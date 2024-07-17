package vn.ses.s3m.plus.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.dto.DataWeather;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.Forecast;
import vn.ses.s3m.plus.dto.Inverter;
import vn.ses.s3m.plus.dto.OverviewPVPower;
import vn.ses.s3m.plus.dto.OverviewPVTotalPower;
import vn.ses.s3m.plus.service.DeviceService;
import vn.ses.s3m.plus.service.InverterService;
import vn.ses.s3m.plus.service.OverviewPowerService;

@RestController
@RequestMapping ("/pv")
public class OverViewPowerPVController {
    static final int MILLISECOND = 1000;

    static final int FIFTEEN_MINUTE = 900;

    // Logging
    private final Log log = LogFactory.getLog(OverViewPowerPVController.class);

    @Autowired
    private OverviewPowerService overviewPowerService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private InverterService inverterService;

    // Các tham số tính dự báo
    private static final int MAX_DAY = 7;

    private static final int MAX_SECOND = 60;

    private static final int MAX_MONH = 12;

    private static final int MAX_HOURS = 24;

    private static final int TRANSACTION_DATE_24_HOURS = 86400;

    /**
     * Lấy thông tin công suất của từng thiết bị trong dự án Load trong ngày
     *
     * @param projectId ID dự án
     * @return devicePowers thông tin công suất các thiết bị
     */
    @GetMapping ("/power/{projectId}")
    public ResponseEntity<List<OverviewPVPower>> getPowerInDay(@PathVariable final String projectId,
        @RequestParam (required = false) final String keyword) {

        log.info("getPowerInDay START");
        Map<String, Object> condition = new HashMap<>();
        Map<String, Object> conditions = new HashMap<>();
        condition.put("projectId", projectId);
        if (keyword != null) {
            condition.put("keyword", keyword);
        }
        List<Device> devices = deviceService.getDevicePowerPVByProjectId(condition);
        List<OverviewPVPower> devicePowers = new ArrayList<>();
        OverviewPVPower devicePower = null;
        for (Device device : devices) {
            condition.put("deviceId", device.getDeviceId());

            DataWeather dataWeather = deviceService.getPowerByDeviceId(condition);

            conditions.put("systemMapId", device.getSystemMapId());

            Integer warnings = overviewPowerService.getDeviceHasWarning(condition);

            OverviewPVPower layer = overviewPowerService.getLayer(conditions);

            OverviewPVPower overview = overviewPowerService.getPower(condition);

            Inverter inverter = inverterService.getDataInverterByDeviceId(condition);

            Integer dcPower = inverter.getWh();

            Integer acPower = inverter.getW();

            float efficiency = ((float) dcPower / (float) acPower);

            System.out.println("efficiency: " + efficiency);

            devicePower = new OverviewPVPower();
            devicePower.setDeviceId(device.getDeviceId());
            devicePower.setDeviceName(device.getDeviceName());
            devicePower.setSystemMapId(device.getSystemMapId());
            devicePower.setProjectId(device.getProjectId());
            devicePower.setEfficiency(efficiency);
            if (dataWeather != null) {
                devicePower.setRadiation(dataWeather.getRadiation());
                devicePower.setTemperature(dataWeather.getTemperature());
            }
            if (layer != null) {
                devicePower.setLayer(layer.getLayer());
                devicePower.setSystemMapName(layer.getSystemMapName());
            }
            if (overview == null) {
                devicePower.setLoadStatus("error");
            } else if (warnings > 0 && overview != null) {
                devicePower.setLoadStatus("warning");
            } else {
                devicePower.setLoadStatus("active");
            }

            if (overview != null) {
                devicePower.setEP(overview.getEP());
                devicePower.setPTotal(overview.getPTotal());
            }
            devicePowers.add(devicePower);
        }

        log.info("getPowerInDay END");

        return new ResponseEntity<List<OverviewPVPower>>(devicePowers, HttpStatus.OK);
    }

    /**
     * Lấy thông tin tổng công suất, năng lượng các thiết bị trong ngày thuộc dự án Load
     *
     * @param projectId ID dự án
     * @param day Ngày để lấy dữ liệu
     * @return thông tin tổng công suất, dữ liệu theo các mốc thời gian trong ngày
     */
    @GetMapping ("/powerTotal/{projectId}/{systemTypeId}/{calculate}")
    public ResponseEntity<List<OverviewPVTotalPower>> getTotalPowerEnergy(@PathVariable final String projectId,
        @PathVariable final String systemTypeId, @PathVariable final Integer calculate,
        @RequestParam final String fromDate, @RequestParam final String toDate) {

        log.info("getTotalPowerEnergy START");
        List<OverviewPVTotalPower> totalPowers = new ArrayList<>();

        Date now = new Date();
        Map<String, Object> condition = new HashMap<>();
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

        // truy vấn data theo các mốc thời gian 15p
        while (transactionDate <= transactionEndDate) {

            OverviewPVTotalPower newTotalPower = new OverviewPVTotalPower();
            condition.put("from", transactionDate);
            condition.put("to", transactionDate + FIFTEEN_MINUTE);

            OverviewPVTotalPower totalPower = overviewPowerService.getTotalPowerPVInDay(condition);

            String timeView = dateFormatView.format(new Date(transactionDate * MILLISECOND));
            String timeForecast = dateFormatToHourMinute.format(new Date(transactionDate * MILLISECOND));

            newTotalPower.setTime(timeView);
            newTotalPower.setEnergy(totalPower != null ? totalPower.getEnergy() : 0);
            newTotalPower.setPower(totalPower != null ? totalPower.getPower() : 0);

            Long transactionF = (transactionDate + FIFTEEN_MINUTE) * MILLISECOND;
            String dateForecast = dateFormatWithTime.format(new Date(transactionF));
            Map<String, Object> map = new HashMap<>();
            map.put("projectId", projectId);
            map.put("systemTypeId", systemTypeId);
            map.put("date", dateForecast);

            Forecast f = overviewPowerService.getForecast(map);

            // Tính công suất dự đoán
            Long pforecast = (long) 0;
            if (f != null) {
                pforecast = totalPowerForecast(projectId, systemTypeId, timeForecast, dateFormat, f, endDay,
                    transactionDate);
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
                pforecast = totalPowerForecast(projectId, systemTypeId, timeForecast, dateFormat, f, endDay,
                    transactionDate);
            }

            newTotalPower.setForecast(pforecast > 0 ? pforecast : 0);

            totalPowers.add(newTotalPower);

            transactionDate += FIFTEEN_MINUTE;

        }

        log.info("getTotalPowerEnergy END");

        return new ResponseEntity<List<OverviewPVTotalPower>>(totalPowers, HttpStatus.OK);
    }

    /**
     * Lấy gía trị điện năng tổng của tất cả các thiết bị trong dự án Load trong ngày
     *
     * @param projectId ID dự án
     * @return inverter.Wh điện năng tổng của tất cả các thiết bị trong dự án
     */
    @GetMapping ("/energy/{projectId}")
    public ResponseEntity<Inverter> getTotalACEnergy(@PathVariable final String projectId) {
        Map<String, Object> condition = new HashMap<>();
        Inverter totalEnergyInverter = new Inverter();
        condition.put("projectId", projectId);
        List<Device> devices = deviceService.getDevicePowerPVByProjectId(condition);
        Integer totalWh = 0;
        for (Device device : devices) {
            condition.put("deviceId", device.getDeviceId());
            List<Inverter> inverters = deviceService.getInverterByDeviceId(condition);
            for (Inverter inverter : inverters) {
                totalWh += inverter.getWh();
            }
        }
        totalEnergyInverter.setWh(totalWh);
        return new ResponseEntity<Inverter>(totalEnergyInverter, HttpStatus.OK);
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
    private Long totalPowerForecast(final String projectId, final String systemTypeId, final String time,
        final SimpleDateFormat dateFormat, final Forecast f, final String day, final Long transactionDate) {

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

        Long transactionDateForecast = transactionDate - TRANSACTION_DATE_24_HOURS;
        Map<String, Object> condition = new HashMap<>();
        condition.put("projectId", projectId);
        condition.put("systemTypeId", systemTypeId);
        condition.put("from", transactionDateForecast);
        condition.put("to", transactionDateForecast + FIFTEEN_MINUTE);

        OverviewPVTotalPower totalPowerForecast = overviewPowerService.getTotalPowerPVInDay(condition);

        Long priorDay = totalPowerForecast != null ? totalPowerForecast.getPower() : 0;

        Long pforecast = (long) (f.getA0() + f.getA1() * Math.sin(2 * Math.PI * hourForecast / MAX_HOURS)
            + f.getA2() * Math.cos(2 * Math.PI * hourForecast / MAX_HOURS)
            + f.getA3() * Math.sin(2 * Math.PI * dayForecast / MAX_DAY)
            + f.getA4() * Math.cos(2 * Math.PI * dayForecast / MAX_DAY)
            + f.getA5() * Math.sin(2 * Math.PI * monthForecast / MAX_MONH)
            + f.getA6() * Math.cos(2 * Math.PI * monthForecast / MAX_MONH) + f.getA7() * yearForecast
            + f.getA8() * isWeekend + f.getA9() * priorDay);

        return pforecast;
    }
}
