package vn.ses.s3m.plus.controllers.load;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import vn.ses.s3m.plus.form.UpdateWarningForm;

@RestController
@RequestMapping ("/load/warning")
public class WarningController {

    /** Logging */
    private final Log log = LogFactory.getLog(WarningController.class);

    @Autowired
    private LoadClient loadClient;

    /**
     * Lấy ra danh sách cảnh báo .
     *
     * @param fromDate Thời gian bắt đầu tìm kiếm.
     * @param toDate Thời gian kết thúc tìm kiếm.
     * @param projectId ID của Dự án.
     * @return Danh sách cảnh báo.
     */
    @GetMapping ("")
    public ResponseEntity<?> getWarnings(@RequestParam ("fromDate") final String fromDate,
        @RequestParam ("toDate") final String toDate, @RequestParam ("projectId") final String projectId,
        @RequestParam ("customerId") final String customerId) {
        return this.loadClient.getWarnings(fromDate, toDate, projectId, customerId);
    }

    /**
     * Lấy ra danh sách cảnh báo theo WARNING_TYPE .
     *
     * @param fromDate Thời gian bắt đầu tìm kiếm.
     * @param toDate Thời gian kết thúc tìm kiếm.
     * @param projectId ID của Dự án.
     * @param warningType Kiểu cảnh báo cần tìm kiếm.
     * @param page Trang muốn hiển thị dữ liệu (Phân trang nếu dữ liệu nhiều).
     * @return Danh sách cảnh báo theo WARNING_TYPE.
     */
    @GetMapping ("/type/{warningType}")
    public ResponseEntity<?> detailWarningByType(@PathVariable ("warningType") final String warningType,
        @RequestParam ("fromDate") final String fromDate, @RequestParam ("toDate") final String toDate,
        @RequestParam ("projectId") final String projectId, @RequestParam ("customerId") final String customerId,
        @RequestParam ("page") final Integer page) {

        return this.loadClient.detailWarningByType(warningType, fromDate, toDate, projectId, customerId, page);
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
    @GetMapping ("/detail")
    public ResponseEntity<?> showDataWarningByDevice(@RequestParam ("warningType") final String warningType,
        @RequestParam ("fromDate") final String fromDate, @RequestParam ("toDate") final String toDate,
        @RequestParam ("deviceId") final String deviceId, @RequestParam ("customerId") final String customerId,
        @RequestParam ("page") final Integer page) {
        log.info("test log");
        return this.loadClient.showDataWarningByDevice(warningType, fromDate, toDate, deviceId, customerId, page);
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
    @GetMapping ("/download")
    public ResponseEntity<Resource> downloadWarningOperation(@RequestParam ("warningType") final String warningType,
        @RequestParam ("fromDate") final String fromDate, @RequestParam ("toDate") final String toDate,
        @RequestParam ("deviceId") final String deviceId, @RequestParam ("customerId") final String customerId,
        @RequestParam ("userName") final String userName) {

        log.info("LOAD-CONSUMER - WarningController: Download Excel Warning Data");
        try {
            return this.loadClient.downloadWarningOperation(warningType, fromDate, toDate, deviceId, customerId,
                userName);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(null);
        }
    }

    /**
     * Lấy thông tin chi tiết cảnh báo.
     *
     * @param warningId Id của cảnh báo.
     * @return Thông tin chi tiết của cảnh báo.
     */
    @GetMapping ("/update/{warningId}")
    public ResponseEntity<?> updateWarning(@PathVariable ("warningId") final Integer warningId,
        @RequestParam ("customerId") final String customerId) {

        return loadClient.getWarningCache(warningId, customerId);
    }

    /**
     * Cập nhật thông tin chi tiết cảnh báo.
     *
     * @param form Data cập nhật.
     * @param warningId Id cảnh báo.
     * @return Trạng thái cập nhật.
     */
    @PostMapping ("/update/{warningId}/{customerId}")
    public ResponseEntity<?> updateWarning(@RequestBody final UpdateWarningForm form,
        @PathVariable ("warningId") final Integer warningId, @PathVariable ("customerId") final String customerId) {

        return loadClient.updateWarningCache(form, warningId, customerId);
    }
}
