package vn.ses.s3m.plus.controllers.grid;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
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
@RequestMapping ("/grid")
public class ReportGridController {

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
    public ResponseEntity<Map<String, List>> loadReportGrid(@PathVariable final String userName,
        @PathVariable final String projectId) {
        return loadClient.loadReportGrid(userName, projectId);
    }

    /**
     * Xóa thông tin báo cáo.
     *
     * @param id Mã thông báo.
     * @return Trả về 200(xóa báo cáo thành công).
     */
    @DeleteMapping ("/report/delete/{id}")
    public ResponseEntity<Void> deleteReportGrid(@PathVariable final int id) {
        return loadClient.deleteReportGrid(id);
    }

    /**
     * Tải thông tin báo cáo.
     *
     * @param path Đường dẫn của file báo cáo.
     * @return File chứa báo cáo định dạng .zip.
     */
    @GetMapping ("/report/download")
    public ResponseEntity<Resource> downloadReportGrid(@RequestParam final String path) throws Exception {
        return loadClient.downloadReportGrid(path);
    }

    /**
     * Thêm mới yêu cầu tạo báo cáo.
     *
     * @param deviceId Mã thiết bị.
     * @param reportType Loại báo cáo.
     * @param fromDate Từ ngày .
     * @param toDate Đến ngày.
     * @param projectId Mã dự án.
     * @param userName Tên người dùng.
     * @return Trả về 200(Thêm mới thông tin thành công).
     */
    @GetMapping ("/report/addReport/{customerId}/{deviceId}/{reportType}/{date}/{userName}/{projectId}/{dateType}")
    public ResponseEntity<Void> addReportGrid(@PathVariable final String customerId,
        @PathVariable final String deviceId, @PathVariable final String reportType, @PathVariable final String date,
        final @PathVariable
        String userName, @PathVariable final String projectId, @PathVariable final String dateType)
        throws ParseException {
        return loadClient.addReportGrid(customerId, deviceId, reportType, date, userName, projectId, dateType);
    }

    /**
     * Tạo báo cáo và lưu vào trong máy.
     *
     * @param deviceId Mã thiết bị.
     * @param reportType Loại báo cáo.
     * @param fromDate Từ ngày.
     * @param toDate Đến ngày.
     * @param projectId Mã dự án.
     * @return Trả về 200(Tạo file báo cáo thành công) 404(Tạo báo cáo thất bại).
     * @throws Exception.
     */
    // CHECKSTYLE:OFF
    @PostMapping ("/report/generateReports/{customerId}/{deviceId}/{reportType}/{date}/{dateType}"
        + "/{userName}/{projectId}")
    public ResponseEntity<?> generateReportsGrid(@PathVariable final String customerId,
        @PathVariable final String deviceId, @PathVariable final String reportType, @PathVariable final String date,
        @PathVariable final String dateType, final @PathVariable
        String userName, @PathVariable final String projectId, @RequestBody final User user) throws Exception {
        return loadClient.generateReportsGrid(customerId, deviceId, reportType, date, dateType, userName, projectId,
            user);
    }
    // CHECKSTYLE:ON

}
