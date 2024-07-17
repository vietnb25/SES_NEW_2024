package vn.ses.s3m.plus.controllers.grid;

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
@RequestMapping ("/grid/warning")
public class WarningGridController {

    /** Logging */
    private final Log log = LogFactory.getLog(WarningGridController.class);

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
    public ResponseEntity<?> getWarningsGrid(@RequestParam ("fromDate") final String fromDate,
        @RequestParam ("toDate") final String toDate, @RequestParam ("customerId") final Integer customerId,
        @RequestParam ("projectId") final String projectId) {
        return this.loadClient.getWarningsGrid(fromDate, toDate, customerId, projectId);
    }

    /**
     * Lấy ra danh sách cảnh báo theo WARNING_TYPE .
     *
     * @param fromDate Thời gian bắt đầu tìm kiếm.
     * @param toDate Thời gian kết thúc tìm kiếm.
     * @param projectId ID của Dự án.
     * @param projectId ID của Khách hàng.
     * @param warningType Kiểu cảnh báo cần tìm kiếm.
     * @param page Trang muốn hiển thị dữ liệu (Phân trang nếu dữ liệu nhiều).
     * @return Danh sách cảnh báo theo WARNING_TYPE.
     */
    @GetMapping ("/type/{warningType}")
    public ResponseEntity<?> detailWarningByTypeGrid(@PathVariable ("warningType") final String warningType,
        @RequestParam ("fromDate") final String fromDate, @RequestParam ("toDate") final String toDate,
        @RequestParam ("customerId") final Integer customerId, @RequestParam ("projectId") final String projectId,
        @RequestParam ("page") final Integer page) {

        return this.loadClient.detailWarningByTypeGrid(warningType, fromDate, toDate, customerId, projectId, page);
    }

    /**
     * Lấy ra danh sách cảnh báo theo WARNING_TYPE .
     *
     * @param fromDate Thời gian bắt đầu tìm kiếm.
     * @param toDate Thời gian kết thúc tìm kiếm.
     * @param projectId ID của Dự án.
     * @param projectId ID của Khách hàng.
     * @param warningType Kiểu cảnh báo cần tìm kiếm.
     * @param page Trang muốn hiển thị dữ liệu (Phân trang nếu dữ liệu nhiều).
     * @return Danh sách cảnh báo theo WARNING_TYPE.
     */
    @GetMapping ("/operation/type/{warningType}")
    public ResponseEntity<?> detailWarningOperationByTypeGrid(@PathVariable ("warningType") final String warningType,
        @RequestParam ("fromDate") final String fromDate, @RequestParam ("toDate") final String toDate,
        @RequestParam ("customerId") final Integer customerId, @RequestParam ("deviceId") final String deviceId,
        @RequestParam ("page") final Integer page) {

        return this.loadClient.detailWarningOperationInformationByTypeGrid(warningType, fromDate, toDate, customerId,
            deviceId, page);
    }

    /**
     * Lấy thông tin chi tiết cảnh báo.
     *
     * @param warningId Id của cảnh báo.
     * @return Thông tin chi tiết của cảnh báo.
     */
    @GetMapping ("/update/{warningType}/{deviceId}")
    public ResponseEntity<?> updateWarning(@PathVariable ("warningType") String warningType,
        @PathVariable ("deviceId") String deviceId, @RequestParam String fromDate, @RequestParam String toDate,
        @RequestParam Integer customerId) {

        return loadClient.getWarningCacheGrid(warningType, deviceId, fromDate, toDate, customerId);
    }

    /**
     * Cập nhật thông tin chi tiết cảnh báo.
     *
     * @param form Data cập nhật.
     * @param warningId Id cảnh báo.
     * @return Trạng thái cập nhật.
     */
    @PostMapping ("/update/{warningId}")
    public ResponseEntity<?> updateWarning(@RequestBody final UpdateWarningForm form,
        @PathVariable ("warningId") final Integer warningId) {

        return loadClient.updateWarningCacheGrid(form, warningId);
    }

    /**
     * Hiển thị chi tiết các bản tin khi bị cảnh báo theo từng thiết bị.
     *
     * @param warningType Kiểu cảnh báo.
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @param deviceId ID thiết bị.
     * @param page Page muốn hiển thị dữ liệu.
     * @return Danh sách chi tiết của cảnh báo theo warning type
     */
    @GetMapping ("/detail")
    public ResponseEntity<?> showDataWarningByDeviceGrid(@RequestParam ("warningType") final String warningType,
        @RequestParam ("fromDate") final String fromDate, @RequestParam ("toDate") final String toDate,
        @RequestParam ("projectId") final Integer projectId, @RequestParam ("customerId") final Integer customerId,
        @RequestParam ("deviceId") final String deviceId, @RequestParam ("page") final Integer page) {
        return this.loadClient.showDataWarningByDeviceGrid(warningType, fromDate, toDate, projectId, customerId,
            deviceId, page);
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
    public ResponseEntity<Resource> downloadWarningOperationGrid(@RequestParam ("warningType") final String warningType,
        @RequestParam ("fromDate") final String fromDate, @RequestParam ("toDate") final String toDate,
        @RequestParam ("customerId") final Integer customerId, @RequestParam ("deviceId") final String deviceId,
        @RequestParam final String userName) {
        log.info("Download Excel Warning Data");
        try {
            return this.loadClient.downloadWarningOperationGrid(warningType, fromDate, toDate, customerId, deviceId,
                userName);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(null);
        }
    }
}
