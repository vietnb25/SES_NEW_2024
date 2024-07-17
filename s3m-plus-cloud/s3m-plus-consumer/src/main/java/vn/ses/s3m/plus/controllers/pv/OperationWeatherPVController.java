package vn.ses.s3m.plus.controllers.pv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.client.LoadClient;

@RestController
@RequestMapping ("/pv/operation")
public class OperationWeatherPVController {

    @Autowired
    private LoadClient loadClient;

    /**
     * Lấy thông tin tức thời Weather PV
     *
     * @param deviceId Mã thiết bị
     * @return Thông tin tức thời PV
     */
    @GetMapping ("/instant/weather/{customerId}/{deviceId}")
    public ResponseEntity<?> getInstantOperationWeatherPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId) {
        return loadClient.getInstantOperationWeatherPV(customerId, deviceId);
    }

    /**
     * Lấy danh sách thông tin vận hành Weather
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức truy vấn
     * @param page Trang lấy dữ liệu
     * @return Danh sách thông tin vận hànhs
     */
    @GetMapping ("/weather/{customerId}/{deviceId}/{page}")
    public ResponseEntity<?> getOperationWeatherPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @PathVariable final Integer page) {
        return loadClient.getOperationWeatherPV(customerId, deviceId, fromDate, toDate, page);
    }

    /**
     * Lấy thông tin dữ liệu biểu đồ thời tiết
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức tuy vấn
     * @return Danh sách dữ liệu biểu đồ thời tiết
     */
    @GetMapping ("/chart/weather/{customerId}/{deviceId}")
    public ResponseEntity<?> getChartWeather(@PathVariable final Integer customerId, @PathVariable final Long deviceId,
        @RequestParam final String fromDate, @RequestParam final String toDate) {
        return loadClient.getChartWeatherPV(customerId, deviceId, fromDate, toDate);
    }

    /**
     * Dowload thông số Weather PV.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/device-parameter/weather/{customerId}/{deviceId}")
    public ResponseEntity<Resource> downloadDeviceParameterWeather(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @RequestParam final String userName) {
        return loadClient.downloadDeviceParameterWeatherPV(customerId, deviceId, fromDate, toDate, userName);
    }

    /**
     * Dowload Dữ liệu biểu đồ Weather PV.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/chart/weather/{customerId}/{deviceId}/{type}")
    public ResponseEntity<Resource> downloadChartWeather(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @PathVariable final Integer type, @RequestParam final String userName) {
        return loadClient.downloadChartWeather(customerId, deviceId, fromDate, toDate, type, userName);
    }

}
