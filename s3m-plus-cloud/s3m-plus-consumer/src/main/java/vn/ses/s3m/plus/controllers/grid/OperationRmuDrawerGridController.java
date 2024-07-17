package vn.ses.s3m.plus.controllers.grid;

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
@RequestMapping ("/grid/operation")
public class OperationRmuDrawerGridController {

    @Autowired
    private LoadClient loadClient;

    /**
     * Lấy thông tin tức thời khoang tủ Rmu
     *
     * @param deviceId Mã thiết bị
     * @return Thông tin tức thời khoang tủ Rmu
     */
    @GetMapping ("/instant/rmu-drawer/{customerId}/{deviceId}")
    public ResponseEntity<?> getInstantOperationRmuDrawerGrid(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId) {
        return loadClient.getInstantOperationRmuDrawerGrid(customerId, deviceId);
    }

    /**
     * Lấy danh sách thông tin vận hành khoang tủ Rmu
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức truy vấn
     * @param page Trang lấy dữ liệu
     * @return Danh sách thông tin vận hànhs khoang tủ Rmu
     */
    @GetMapping ("/rmu-drawer/{customerId}/{deviceId}/{page}")
    public ResponseEntity<?> getOperationRmuDrawerGrid(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @PathVariable final Integer page) {
        return loadClient.getOperationRmuDrawerGrid(customerId, deviceId, fromDate, toDate, page);
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
        @PathVariable final Long deviceId, @PathVariable final Integer type, @RequestParam final String fromDate,
        @RequestParam final String toDate, @RequestParam final String userName) {
        return loadClient.downloadDeviceParameterRmuDrawerGrid(customerId, deviceId, type, fromDate, toDate, userName);
    }

    /**
     * Lấy dữ liệu biểu đồ khoang tủ Rmu
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thức truy vấn
     * @return Danh sách dữ liệu biểu đồ khoang tủ Rmu
     */
    @GetMapping ("/chart/rmu-drawer/{customerId}/{deviceId}")
    public ResponseEntity<?> getDataChartRmuDrawerGrid(@PathVariable Integer customerId,
        @PathVariable final Long deviceId, @RequestParam (required = false) final String fromDate,
        @RequestParam (required = false) final String toDate) {
        return loadClient.getDataChartRmuDrawerGrid(customerId, deviceId, fromDate, toDate);
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
        return loadClient.getChartElectricalPowerRmuDrawerGrid(customerId, deviceId, date, type);
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
        return loadClient.downloadDataChartRmuDrawerGrid(customerId, deviceId, fromDate, toDate, pqsViewType, chartType,
            userName);
    }

}
