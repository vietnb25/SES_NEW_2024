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
public class OperationPanelPVController {

    @Autowired
    private LoadClient loadClient;

    @GetMapping ("/instant/panel/{customerId}/{deviceId}")
    public ResponseEntity<?> getInstantOperationPanelPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId) {
        return loadClient.getInstantOperationPanelPV(customerId, deviceId);
    }

    /**
     * Lấy danh sách thông tin vận hành Panel
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức truy vấn
     * @param page Trang lấy dữ liệu
     * @return Danh sách thông tin vận hànhs
     */
    @GetMapping ("/panel/{customerId}/{deviceId}/{page}")
    public ResponseEntity<?> getOperationPanelPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @PathVariable final Integer page) {
        return loadClient.getOperationPanelPV(customerId, deviceId, fromDate, toDate, page);
    }

    /**
     * Lấy thông tin dữ liệu biểu đồ Panel PV
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức tuy vấn
     * @return Danh sách dữ liệu biểu đồ điện áp
     */
    @GetMapping ("/chart/panel/{customerId}/{deviceId}")
    public ResponseEntity<?> getChartPanelPV(@PathVariable final Integer customerId, @PathVariable final Long deviceId,
        @RequestParam final String fromDate, @RequestParam final String toDate) {
        return loadClient.getCharPanelPV(customerId, deviceId, fromDate, toDate);
    }

    /**
     * Dowload thông số Panel PV.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/device-parameter/panel/{customerId}/{deviceId}")
    public ResponseEntity<Resource> downloadDeviceParameterPanelPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @RequestParam final String userName) {
        return loadClient.downloadDeviceParameterPanelPV(customerId, deviceId, fromDate, toDate, userName);
    }

    /**
     * Dowload Dữ liệu biểu đồ Panel PV.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/chart/panel/{customerId}/{deviceId}/{type}")
    public ResponseEntity<Resource> downloadChartPanelPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @PathVariable final Integer type, @RequestParam final String userName) {
        return loadClient.downloadChartPanelPV(customerId, deviceId, fromDate, toDate, type, userName);
    }
}
