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
public class OperationCombinerPVController {

    @Autowired
    private LoadClient loadClient;

    @GetMapping ("/instant/combiner/{customerId}/{deviceId}")
    public ResponseEntity<?> getInstantOperationCombinerPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId) {
        return loadClient.getInstantOperationCombinerPV(customerId, deviceId);
    }

    /**
     * Lấy danh sách thông tin vận hành Combiner
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức truy vấn
     * @param page Trang lấy dữ liệu
     * @return Danh sách thông tin vận hànhs
     */
    @GetMapping ("/combiner/{customerId}/{deviceId}/{page}")
    public ResponseEntity<?> getOperationCombinerPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @PathVariable final Integer page) {
        return loadClient.getOperationCombinerPV(customerId, deviceId, fromDate, toDate, page);
    }

    /**
     * Lấy thông tin dữ liệu biểu đồ Combiner PV
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức tuy vấn
     * @return Danh sách dữ liệu biểu đồ điện áp
     */
    @GetMapping ("/chart/combiner/{customerId}/{deviceId}")
    public ResponseEntity<?> getChartCombinerPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate) {
        return loadClient.getCharCombinerPV(customerId, deviceId, fromDate, toDate);
    }

    /**
     * Dowload thông số Combiner PV.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/device-parameter/combiner/{customerId}/{deviceId}")
    public ResponseEntity<Resource> downloadDeviceParameterCombinerPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @RequestParam final String userName) {
        return loadClient.downloadDeviceParameterCombinerPV(customerId, deviceId, fromDate, toDate, userName);
    }

    /**
     * Dowload Dữ liệu biểu đồ Combiner PV.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/chart/combiner/{customerId}/{deviceId}/{type}")
    public ResponseEntity<Resource> downloadChartCombinerPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @PathVariable final Integer type, @RequestParam final String userName) {
        return loadClient.downloadChartCombinerPV(customerId, deviceId, fromDate, toDate, type, userName);
    }

}
