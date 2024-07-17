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
public class OperationInverterPVController {

    @Autowired
    private LoadClient loadClient;

    /**
     * Lấy thông tin tức thời PV
     *
     * @param deviceId Mã thiết bị
     * @return Thông tin tức thời PV
     */
    @GetMapping ("/instant/inverter/{customerId}/{deviceId}")
    public ResponseEntity<?> getInstantOperationInverterPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId) {
        return loadClient.getInstantOperationInverterPV(customerId, deviceId);
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
        return loadClient.getOperationInverterPV(customerId, deviceId, fromDate, toDate, page);
    }

    /**
     * Lấy thông tin dữ liệu biểu đồ PV
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức tuy vấn
     * @return Danh sách dữ liệu biểu đồ điện áp
     */
    @GetMapping ("/chart/inverter/{customerId}/{deviceId}")
    public ResponseEntity<?> getChartInverter(@PathVariable final Integer customerId, @PathVariable final Long deviceId,
        @RequestParam final String fromDate, @RequestParam final String toDate) {
        return loadClient.getChartInverterPV(customerId, deviceId, fromDate, toDate);
    }

    /**
     * Lấy thông tin dữ liệu biểu đồ điện năng
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức tuy vấn
     * @return Danh sách dữ liệu biểu đồ điện năng
     */
    @GetMapping ("/chart/electrical-power/inverter/{customerId}/{deviceId}")
    public ResponseEntity<?> getChartElectricalPowerInverterPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String date, @RequestParam final Integer type) {
        return loadClient.getChartElectricalPowerInverterPV(customerId, deviceId, date, type);
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
        @RequestParam final String userName) {
        return loadClient.downloadDeviceParameterInverterPV(customerId, deviceId, fromDate, toDate, userName);
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
        @PathVariable final Integer type, @RequestParam final String userName) {
        return loadClient.downloadChartInverter(customerId, deviceId, fromDate, toDate, type, userName);
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
        return loadClient.downloadChartElectricalPowerInverter(customerId, deviceId, date, type, userName);
    }

    /**
     * Dowload Dữ liệu thông số cài đặt Inverter PV.
     *
     * @param deviceId Mã thiết bị
     * @return Dữ liệu thông số cài đặt
     */
    @GetMapping ("/setting/inverter/{customerId}/{deviceId}")
    public ResponseEntity<?> getOperationSettingInverter(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId) {
        return loadClient.getOperationSettingInverter(customerId, deviceId);
    }
}
