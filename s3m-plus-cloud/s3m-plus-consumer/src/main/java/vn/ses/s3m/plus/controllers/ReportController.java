package vn.ses.s3m.plus.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.client.LoadClient;
import vn.ses.s3m.plus.dto.User;

@RestController
@RequestMapping ("/load")
public class ReportController {

    @Autowired
    private LoadClient loadClient;

    /**
     * Lấy thông tin cảnh báo.
     *
     * @param userName Tên người dùng.
     * @return Thông tin báo cáo.
     */
    @SuppressWarnings ("rawtypes")
    @GetMapping ("/report/{userName}/{projectId}")
    public ResponseEntity<Map<String, List>> loadReport(@PathVariable final String userName,
        @PathVariable final String projectId) {
        return loadClient.loadReport(userName, projectId);
    }

    /**
     * Tạo báo cáo.
     *
     * @param deviceId Mã thiết bị.
     * @param reportType Loại báo cáo.
     * @param fromDate Từ ngày.
     * @param toDate Đến ngày.
     * @param projectId Mã dự án.
     * @param customerId Mã khách hàng.
     * @return File báo cáo.
     */
    // CHECKSTYLE:OFF
    @PostMapping ("/report/generateReports/{customerId}/"
        + "{deviceId}/{reportType}/{date}/{dateType}/{userName}/{projectId}")
    public ResponseEntity<?> generateReports(@PathVariable final String customerId, @PathVariable final String deviceId,
        @PathVariable final String reportType, @PathVariable final String date, @PathVariable final String dateType,
        final @PathVariable
        String userName, final @PathVariable
        String projectId, @RequestBody User user) {

        return loadClient.generateReports(customerId, deviceId, reportType, date, dateType, userName, projectId, user);
    }

    // CHECKSTYLE:ON
    /**
     * Tải báo cáo.
     *
     * @param path Đường dẫn file báo cáo.
     * @return Thông tin báo cáo.
     */
    @GetMapping ("/report/download")
    public ResponseEntity<Resource> downloadReport(@RequestParam final String path) {

        return loadClient.downloadReport(path);
    }

    /**
     * Xóa báo cáo.
     *
     * @param id Mã báo cáo.
     * @return Thông tin báo cáo.
     */
    @DeleteMapping ("/report/delete/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable final Integer id) {
        return loadClient.deleteReport(id);
    }

    /**
     * Gửi yêu cầu tạo báo cáo.
     *
     * @param deviceId Mã thiết bị.
     * @param reportType Loại báo cáo.
     * @param fromDate Từ ngày.
     * @param toDate Đến ngày.
     * @param userName Tên người dùng.
     * @param projectId Mã dự án.
     * @return Thông tin báo cáo.
     */
    @GetMapping ("/report/addReport/{customerId}/{deviceId}/{reportType}/{date}/{userName}/{projectId}/{dateType}")
    public ResponseEntity<Void> addReport(@PathVariable final String customerId, @PathVariable final String deviceId,
        @PathVariable final String reportType, @PathVariable final String date, final @PathVariable
        String userName, @PathVariable final String projectId, @PathVariable final String dateType) {
        try {
            return loadClient.addReport(customerId, deviceId, reportType, date, userName, projectId, dateType);
        } catch (Exception e) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
    }
}
