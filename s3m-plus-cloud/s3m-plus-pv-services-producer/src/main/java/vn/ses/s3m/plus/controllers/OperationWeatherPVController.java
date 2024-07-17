package vn.ses.s3m.plus.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import vn.ses.s3m.plus.dto.DataWeather1;
import vn.ses.s3m.plus.response.OperationWeatherResponse;
import vn.ses.s3m.plus.service.OperationWeatherPVService;

@RestController
@Slf4j
@RequestMapping ("/pv/operation")
public class OperationWeatherPVController {

    @Autowired
    private OperationWeatherPVService weatherPVService;

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

    /**
     * Lấy thông tin thông số điện tức thời PV
     *
     * @param deviceId Mã thiết bị
     * @return Thông tin thông số điện tức thời
     */
    @GetMapping ("/instant/weather/{deviceId}")
    public ResponseEntity<?> getInstantOperationWeatherPV(@PathVariable final Long deviceId) {

        log.info("getInstantOperationWeatherPV START");

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        DataWeather1 weather = weatherPVService.getInstantOperationWeatherPV(condition);

        if (weather != null) {

            OperationWeatherResponse data = new OperationWeatherResponse(weather);

            log.info("getInstantOperationWeatherPV END");

            return new ResponseEntity<>(data, HttpStatus.OK);
        } else {

            log.info("getInstantOperationWeatherPV END");

            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * Lấy danh sách thông tin vận hành thời tiết
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức truy vấn
     * @param page Trang lấy dữ liệu
     * @return Danh sách thông tin vận hành
     */
    @GetMapping ("/weather/{deviceId}/{page}")
    public ResponseEntity<?> getOperationWeatherPV(@PathVariable final Long deviceId,
        @RequestParam final String fromDate, @RequestParam final String toDate, @PathVariable final Integer page) {

        log.info("getOperationWeatherPV START");

        List<OperationWeatherResponse> dataInfo = new ArrayList<>();

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        condition.put(FROM_DATE, fromDate.concat(TIME_START));
        condition.put(TO_DATE, toDate.concat(TIME_END));
        condition.put(SORT, SORT_DESC);
        condition.put(PAGE_START, (page - 1) * PAGE_SIZE);
        condition.put(PAGE_END, PAGE_SIZE);

        int totalData = weatherPVService.countDataOperationWeatherPV(condition);

        double totalPage = Math.ceil((double) totalData / PAGE_SIZE);

        List<DataWeather1> weather1s = weatherPVService.getOperationWeatherPV(condition);

        // object to response to client
        Map<String, Object> data = new HashMap<>();

        data.put(TOTAL_PAGE_STR, totalPage);
        data.put(CURRENT_PAGE_STR, page);
        data.put(TOTAL_DATA_STR, totalData);

        if (weather1s.size() > 0) {
            weather1s.forEach(i -> {
                OperationWeatherResponse res = new OperationWeatherResponse(i);
                dataInfo.add(res);
            });
            data.put(DATA, dataInfo);

            log.info("getOperationWeatherPV END");

            return new ResponseEntity<>(data, HttpStatus.OK);
        } else {

            log.info("getOperationWeatherPV END");
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * Lấy thông tin dữ liệu biểu đồ thời tiết
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức tuy vấn
     * @return Danh sách dữ liệu biểu đồ điện áp
     */
    @GetMapping ("/chart/weather/{deviceId}")
    public ResponseEntity<?> getChartWeather(@PathVariable final Long deviceId, @RequestParam final String fromDate,
        @RequestParam final String toDate) {

        log.info("getChartWeather START");

        List<OperationWeatherResponse> data = new ArrayList<>();

        Map<String, Object> condition = new HashMap<>();
        condition.put(DEVICE_ID, deviceId);
        condition.put(FROM_DATE, fromDate);
        condition.put(TO_DATE, toDate);

        List<DataWeather1> inverter1s = weatherPVService.getOperationWeatherPV(condition);

        if (inverter1s.size() > 0) {
            inverter1s.forEach(i -> {
                OperationWeatherResponse res = new OperationWeatherResponse(i);
                data.add(res);
            });

            log.info("getChartWeather END");

            return new ResponseEntity<>(data, HttpStatus.OK);
        } else {

            log.info("getChartWeather END");
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

}
