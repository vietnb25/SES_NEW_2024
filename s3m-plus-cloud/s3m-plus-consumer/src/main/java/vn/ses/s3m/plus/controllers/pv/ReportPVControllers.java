package vn.ses.s3m.plus.controllers.pv;

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
@RequestMapping ("/pv")
public class ReportPVControllers {
    @Autowired
    private LoadClient loadClient;

    /**
     * Lấy thông báo cáo
     *
     * @param userName Tên người dùng
     * @return Thông tin báo cáo
     */
    @GetMapping ("/report/{customerId}/{userName}/{projectId}")
    public ResponseEntity<Map<String, List>> loadReportPV(@PathVariable final String customerId,
        @PathVariable final String userName, @PathVariable final String projectId) {
        return loadClient.loadReportPV(customerId, userName, projectId);
    }

    /**
     * Thêm mới yêu cầu tạo báo cáo.
     *
     * @param Date Thời gian.
     * @param reportType Loại báo cáo.
     * @param projectId Mã dự án.
     * @param userName Tên người dùng.
     * @return Thêm mới báo cáo thành công.
     */
    @GetMapping ("/report/addReport/{customerId}/{reportType}/{date}/{userName}/{projectId}/{deviceId}")
    public ResponseEntity<Void> addReportPV(@PathVariable final String customerId,
        @PathVariable final String reportType, @PathVariable final String date, @PathVariable final String userName,
        @PathVariable final String projectId, @PathVariable final String deviceId) throws Exception {
        return loadClient.addReportPV(customerId, reportType, date, userName, projectId, deviceId);
    }

    /**
     * Tạo báo cáo và lưu vào trong máy.
     *
     * @param reportType Loại báo cáo.
     * @param date Thời gian.
     * @param userName Tên người dùng.
     * @param projectId Mã dự án.
     * @return Trả về 200(Tạo file báo cáo thành công) 404(Tạo báo cáo thất bại).
     * @throws Exception.
     */
    @PostMapping ("/report/generateReports/{customerId}/{reportType}/{date}/{userName}/{projectId}/{deviceId}")
    ResponseEntity<Void> generateReportsPV(@PathVariable final String customerId, @PathVariable final String reportType,
        @PathVariable final String date, @PathVariable final String userName, @PathVariable final String projectId,
        @PathVariable final String deviceId, @RequestBody final User user) throws Exception {
        return loadClient.generateReportsPV(customerId, reportType, date, userName, projectId, deviceId, user);
    }

    /**
     * Xóa thông tin báo cáo.
     *
     * @param id Mã thông báo.
     * @return Trả về 200(xóa báo cáo thành công).
     */
    @DeleteMapping ("/report/delete/{id}")
    public ResponseEntity<Void> deleteReportPV(@PathVariable final int id) {
        return loadClient.deleteReportPV(id);
    }

    /**
     * Tải báo cáo.
     *
     * @param path Đường dẫn file báo cáo.
     * @return Thông tin báo cáo.
     */
    @GetMapping ("/report/download")
    public ResponseEntity<Resource> downloadReportPV(@RequestParam final String path) {

        return loadClient.downloadReportPV(path);
    }
}
