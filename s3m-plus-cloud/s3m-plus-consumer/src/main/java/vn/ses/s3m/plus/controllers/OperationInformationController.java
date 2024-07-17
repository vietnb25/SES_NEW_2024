package vn.ses.s3m.plus.controllers;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.client.LoadClient;
import vn.ses.s3m.plus.dto.Warning;
import vn.ses.s3m.plus.form.HarmonicForm;
import vn.ses.s3m.plus.form.UpdateWarningForm;
import vn.ses.s3m.plus.response.DataHarmonicPeriod;
import vn.ses.s3m.plus.response.DeviceResponse;
import vn.ses.s3m.plus.response.OperationInformationResponse;
import vn.ses.s3m.plus.response.PowerQualityResponse;

@RestController
@RequestMapping ("/common/operation")
public class OperationInformationController {

    @Autowired
    private LoadClient loadClient;

    /**
     * Lấy thông tin thiết bị tức thời
     *
     * @param deviceId Mã thiết bị
     * @return Thông tin thiết bị tức thời
     */
    @GetMapping ("/instant/{customerId}/{deviceId}")
    public ResponseEntity<OperationInformationResponse> getInstantOperationInformation(@PathVariable Integer customerId,
        @PathVariable final Long deviceId) {
        return loadClient.getInstantOperationInformation(customerId, deviceId);
    }

    /**
     * Lấy thông tin thiết bị
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu tìm kiếm
     * @param toDate Ngày kết thuc tìm kiếm
     * @return Thông tin thiết bị
     */
    @GetMapping ("/{customerId}/{deviceId}/{page}")
    public ResponseEntity<?> geOperationInformation(@PathVariable Integer customerId,
        @PathVariable ("deviceId") final Long deviceId, @RequestParam ("fromDate") final String fromDate,
        @RequestParam ("toDate") final String toDate, @PathVariable ("page") final String page) {
        return loadClient.getOperationInformation(customerId, deviceId, fromDate, toDate, page);
    }

    /**
     * Lấy danh sách cảnh báo vận hành
     *
     * @param deviceId Mã thiết bị
     * @param warningType Kiểu cảnh báo
     * @param fromDate Ngày bắt đầu tìm kiếm
     * @param toDate Ngày kết thúc cảnh báo
     * @return Danh sách cảnh báo vận hành
     */
    @GetMapping ("/operating-warning/{customerId}/{deviceId}")
    public ResponseEntity<List<Warning>> getWarningOperation(@PathVariable Integer customerId,
        @PathVariable final Long deviceId, @RequestParam (required = false) final String warningType,
        @RequestParam (required = false) final String fromDate, @RequestParam (required = false) final String toDate) {
        return loadClient.getWarningOperation(customerId, deviceId, warningType, fromDate, toDate);
    }

    /**
     * Hiển thị chi tiết các bản tin khi bị cảnh báo theo từng warning_type.
     *
     * @param warningType Kiểu cảnh báo.
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @param deviceId ID thiết bị.
     * @param page Page muốn hiển thị dữ liệu.
     * @return Danh sách chi tiết của cảnh báo theo warning type
     */
    @GetMapping ("/operating-warning/detail/{customerId}")
    public ResponseEntity<?> showDataWarning(@PathVariable Integer customerId,
        @RequestParam ("warningType") final String warningType, @RequestParam ("fromDate") final String fromDate,
        @RequestParam ("toDate") final String toDate, @RequestParam ("deviceId") final String deviceId,
        @RequestParam ("page") final Integer page) {
        return this.loadClient.showDataWarning(customerId, warningType, fromDate, toDate, deviceId, page);
    }

    /**
     * Lấy thông tin chi tiết cảnh báo.
     *
     * @param warningId Id của cảnh báo.
     * @return Thông tin chi tiết của cảnh báo.
     */
    @GetMapping ("/operating-warning/update/{customerId}/{warningId}")
    public ResponseEntity<?> updateOperatingWarning(@PathVariable Integer customerId,
        @PathVariable ("warningId") final Integer warningId) {
        return this.loadClient.getOperatingWarningCache(customerId, warningId);
    }

    /**
     * Cập nhật thông tin chi tiết cảnh báo.
     *
     * @param form Data cập nhật.
     * @param warningId Id cảnh báo.
     * @return Trạng thái cập nhật.
     */
    @PostMapping ("/operating-warning/update/{customerId}/{warningId}")
    public ResponseEntity<?> updateOperatingWarning(@PathVariable Integer customerId,
        @RequestBody final UpdateWarningForm form, @PathVariable ("warningId") final Integer warningId) {

        return this.loadClient.updateOperatingWarningCache(form, customerId, warningId);
    }

    /**
     * Download danh sách bản tin bị cảnh báo.
     *
     * @param warningType Kiểu cảnh báo.
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @param deviceId ID thiết bị.
     * @return Data download.
     */
    @GetMapping ("/operating-warning/download/{customerId}")
    public ResponseEntity<?> downloadOperatingWarning(@PathVariable final Integer customerId,
        @RequestParam ("warningType") final String warningType, @RequestParam ("fromDate") final String fromDate,
        @RequestParam ("toDate") final String toDate, @RequestParam ("deviceId") final String deviceId,
        @RequestParam ("userName") final String userName) {
        return this.loadClient.downloadOperatingWarning(customerId, warningType, fromDate, toDate, deviceId, userName);
    }

    /**
     * Download danh sách cảnh báo thiết bị.
     *
     * @param warningType Kiểu cảnh báo.
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @param deviceId ID thiết bị
     * @return Data download
     */
    @GetMapping ("/operating-warning/downloadWarning/{customerId}")
    public ResponseEntity<?> downloadWarning(@PathVariable Integer customerId,
        @RequestParam ("warningType") final String warningType, @RequestParam ("fromDate") final String fromDate,
        @RequestParam ("toDate") final String toDate, @RequestParam ("deviceId") final String deviceId,
        @RequestParam final String userName) {
        return this.loadClient.downloadWarningDevice(customerId, warningType, fromDate, toDate, deviceId, userName);
    }

    /**
     * Lấy dữ liệu biểu đồ thông tin vận hành
     *
     * @param deviceId Mã thiết bị
     * @param chartType Kiểu biểu đồ
     * @param fromDate Ngày bắt đầu tìm kiếm
     * @param toDate Ngày kết thúc tìm kiếm
     * @return Dữ liệu biểu đồ
     * @throws ParseException
     * @throws Exception
     */
    @GetMapping ("/chart/{customerId}/{deviceId}")
    public ResponseEntity<?> getDataChart(@PathVariable Integer customerId, @PathVariable final Long deviceId,
        @RequestParam (required = false) final String fromDate, @RequestParam (required = false) final String toDate,
        @RequestParam final Integer pqsViewType, @RequestParam final Integer chartType) throws ParseException {
        return loadClient.getDataChart(customerId, deviceId, fromDate, toDate, pqsViewType, chartType);
    }

    /**
     * Lấy dữ liệu biểu đồ Harmonic
     *
     * @param deviceId ID thiết bị
     * @param harmonicForm thông tin biểu đồ Harmonic
     * @return dữ liệu biểu đồ
     */
    @PostMapping ("/chart-harmonic/{customerId}/{deviceId}")
    public ResponseEntity<Map<String, Object>> getDataPowerHarmonic(@PathVariable Integer customerId,
        @PathVariable final String deviceId, @RequestBody final HarmonicForm harmonicForm) {
        return loadClient.getDataPowerResponse(customerId, deviceId, harmonicForm);
    }

    /**
     * Lấy dữ liệu sóng hài tại thời điểm
     *
     * @param deviceId Mã thiết bị
     * @param harmonicForm Kiểm hiển thị dữ liệu
     * @param day Ngày truy vấn
     * @return Dữ liệu sóng hài
     */
    @PostMapping ("/chart-harmonic/day/{customerId}/{deviceId}")
    public ResponseEntity<?> getDataHarmonicByDay(@PathVariable Integer customerId, @PathVariable final String deviceId,
        @RequestBody final HarmonicForm harmonicForm, @RequestParam final String day) {
        return loadClient.getDataHarmonicByDay(customerId, deviceId, harmonicForm, day);
    }

    /**
     * Lấy dữ liệu sóng hài theo giai đoạn
     *
     * @param deviceId Mã thiết bị
     * @param fromDate Ngày bắt đầu truy vấn
     * @param toDate Ngày kết thúc truy vấn
     * @return Danh sách dữ liệu sóng hài
     */
    @GetMapping ("/chart-harmonic/period/{customerId}/{deviceId}")
    public ResponseEntity<List<DataHarmonicPeriod>> getDataHarmonicPeriod(@PathVariable Integer customerId,
        @PathVariable final String deviceId, @RequestParam final String fromDate, @RequestParam final String toDate) {
        return loadClient.getDataHarmonicPeriod(customerId, deviceId, fromDate, toDate);
    }

    /**
     * Lấy danh sách thông số chất lượng điện năng
     *
     * @param deviceId Mã thiết bị
     * @return Danh sách thông số chất lượng điện năng
     */
    @GetMapping ("/power-quality/{customerId}/{deviceId}")
    public ResponseEntity<?> getPowerQualities(@PathVariable Integer customerId, @PathVariable final Long deviceId,
        @RequestParam (required = false) final String fromDate, @RequestParam (required = false) final String toDate,
        @RequestParam final String page) {
        return loadClient.getPowerQualities(customerId, deviceId, fromDate, toDate, page);
    }

    /**
     * Lấy dánh sách thông số chất lượng điện năng tức thời
     *
     * @param deviceId Mã thiết bị
     * @return Thông số chất lượng điện năng tức thời
     */
    @GetMapping ("/power-quality/instant/{customerId}/{deviceId}")
    public ResponseEntity<PowerQualityResponse> getInstantPowerQuality(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId) {
        return loadClient.getInstantPowerQuality(customerId, deviceId);
    }

    /**
     * Dowload thông số điện.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/electrical-param/{customerId}/{deviceId}")
    public ResponseEntity<Resource> downloadElectricalParam(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @RequestParam final String userName) {
        return loadClient.downloadElectricalParam(customerId, deviceId, fromDate, toDate, userName);
    }

    /**
     * Dowload thông số nhiệt độ.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/temperature/{customerId}/{deviceId}")
    public ResponseEntity<Resource> downloadTemperature(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @RequestParam final String userName) {
        return loadClient.downloadTemperature(customerId, deviceId, fromDate, toDate, userName);
    }

    /**
     * Dowload thông số chất lượng điện năng.
     *
     * @param deviceId Mã thiết bị.
     * @param fromDate Ngày bắt đầu truy vấn dữ liệu.
     * @param toDate Ngày kết thức truy vấn dữ liệu.
     * @return Data dowload.
     * @throws Exception
     */
    @GetMapping ("/download/power-quality/{customerId}/{deviceId}")
    public ResponseEntity<Resource> downloadPowerQuality(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @RequestParam final String userName) {
        return loadClient.downloadPowerQuality(customerId, deviceId, fromDate, toDate, userName);
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
    @GetMapping ("/chart/download/{customerId}/{deviceId}")
    public ResponseEntity<Resource> downloadDataChart(@PathVariable final Integer customerId,
        @PathVariable final Long deviceId, @RequestParam (required = false) final String fromDate,
        @RequestParam (required = false) final String toDate, @RequestParam final Integer pqsViewType,
        @RequestParam final Integer chartType, @RequestParam final String userName) throws Exception {
        return loadClient.downloadDataChart(customerId, deviceId, fromDate, toDate, pqsViewType, chartType, userName);
    }

    /**
     * Lấy danh sách thiết bị theo dự án
     *
     * @param projectId Mã dự án
     * @return Danh sách thiết bị
     */
    @GetMapping ("/devices/{projectId}/{systemTypeId}")
    public ResponseEntity<List<DeviceResponse>> getDevicesByProject(@PathVariable final Long projectId,
        @PathVariable final Integer systemTypeId) {
        return loadClient.getDevicesByProject(projectId, systemTypeId);
    }
}
