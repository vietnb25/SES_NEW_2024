package vn.ses.s3m.plus.controllers.pv;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.client.LoadClient;
import vn.ses.s3m.plus.dto.DataString1;

@RestController
@RequestMapping ("/pv/operation")
public class OperationStringPVController {

    @Autowired
    private LoadClient loadClient;

    @GetMapping ("/instant/string/{customerId}/{deviceId}")
    public ResponseEntity<?> getInstantOperationStringPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId) {
        return loadClient.getInstantOperationStringPV(customerId, deviceId);
    }

    @GetMapping ("/instant/string/combiner/{deviceId}")
    public ResponseEntity<List<DataString1>> getInstantOperationStringInCombinerPV(@PathVariable final Long deviceId) {
        return loadClient.getInstantOperationStringInCombinerPV(deviceId);
    }

    /**
     * Lấy danh sách thông tin vận hành String
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức truy vấn
     * @param page Trang lấy dữ liệu
     * @return Danh sách thông tin vận hànhs
     */
    @GetMapping ("/string/{customerId}/{deviceId}/{page}")
    public ResponseEntity<?> getOperationStringPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @PathVariable final Integer page) {
        return loadClient.getOperationStringPV(customerId, deviceId, fromDate, toDate, page);
    }

    /**
     * Lấy thông tin dữ liệu biểu đồ String PV
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức tuy vấn
     * @return Danh sách dữ liệu biểu đồ điện áp
     */
    @GetMapping ("/chart/string/{customerId}/{deviceId}")
    public ResponseEntity<?> getChartStringPV(@PathVariable final Integer customerId, @PathVariable final Long deviceId,
        @RequestParam final String fromDate, @RequestParam final String toDate) {
        return loadClient.getCharStringPV(customerId, deviceId, fromDate, toDate);
    }

    /**
     * Dowload thông số String PV.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/device-parameter/string/{customerId}/{deviceId}")
    public ResponseEntity<Resource> downloadDeviceParameterWeather(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @RequestParam final String userName) {
        return loadClient.downloadDeviceParameterStringPV(customerId, deviceId, fromDate, toDate, userName);
    }

    /**
     * Dowload Dữ liệu biểu đồ String PV.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/chart/string/{customerId}/{deviceId}/{type}")
    public ResponseEntity<Resource> downloadChartStringPV(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @PathVariable final Integer type, @RequestParam final String userName) {
        return loadClient.downloadChartStringPV(customerId, deviceId, fromDate, toDate, type, userName);
    }
}
