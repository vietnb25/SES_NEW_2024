package vn.ses.s3m.plus.controllers;

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
import vn.ses.s3m.plus.dto.Forecast;
import vn.ses.s3m.plus.dto.OverviewLoadTotalPower;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.service.ProfileService;

@RestController
@RequestMapping ("/load")
public class OverviewPowerController {

    @Autowired
    private LoadClient loadClient;

    @Autowired
    private ProfileService profileService;

    /**
     * Lấy thông tin công suất của từng thiết bị trong dự án Load trong ngày
     *
     * @param projectId ID dự án
     * @param systemTypeId ID kiểu dự án
     * @return devicePowers thông tin công suất các thiết bị
     */
    @GetMapping ("/power/{customerId}/{projectId}")
    public ResponseEntity<Map<String, Object>> getPowerInDay(@PathVariable final String customerId,
        @PathVariable ("projectId") final String projectId, @RequestParam (required = false) final String keyword) {

        return loadClient.getPowerInDay(customerId, projectId, keyword);
    }

    /**
     * Lấy thông tin tổng công suất, năng lượng các thiết bị trong ngày thuộc dự án Load
     *
     * @param projectId ID dự án
     * @param systemTypeId ID kiểu dự án
     * @param day Ngày để lấy dữ liệu
     * @return thông tin tổng công suất, dữ liệu theo các mốc thời gian trong ngày
     */
    @GetMapping ("/powerTotal/{customerId}/{projectId}")
    public ResponseEntity<List<OverviewLoadTotalPower>> getTotalPowerEnergy(@PathVariable final String customerId,
        @PathVariable final String projectId, @RequestParam final String fromDate, @RequestParam final String toDate) {

        return loadClient.getTotalPowerEnergy(customerId, projectId, fromDate, toDate);
    }

    /**
     * Dữ liệu dự báo công suất
     *
     * @param customerId Mã khách hàng
     * @param projectId Mã dự án
     * @param systemTypeId Mã hệ thống
     * @return Dữ liệu dự báo
     */
    @GetMapping ("/powerTotal/forecast/{customerId}/{projectId}/{systemTypeId}")
    public ResponseEntity<Forecast> getForecast(@PathVariable final Integer customerId,
        @PathVariable final String projectId, @PathVariable final String systemTypeId) {
        return loadClient.getForecast(customerId, projectId, systemTypeId);
    }

    /**
     * Danh sách dữ liệu lịch sử cài đặt dự báo
     *
     * @param customerId Mã khách hàng
     * @param projectId Mã dự án
     * @param systemTypeId Mã hệ thống
     * @return Dữ liệu dự báo
     */
    @GetMapping ("/forecasts/{customerId}/{projectId}/{systemTypeId}/{page}")
    public ResponseEntity<?> getForecasts(@PathVariable final Integer customerId, @PathVariable final Long projectId,
        @PathVariable final Long systemTypeId, @PathVariable final Integer page) {
        return loadClient.getForecasts(customerId, projectId, systemTypeId, page);
    }

    /**
     * Thêm hoặc chỉnh sửa dự báo
     *
     * @param customerId Mã khách hàng
     * @param forecast Đối tượng thêm sửa dự báo
     */
    @PostMapping ("/powerTotal/forecast/save/{customerId}")
    public ResponseEntity<?> saveForecast(@RequestBody final Forecast forecast,
        @PathVariable final Integer customerId) {
        return loadClient.saveForecast(forecast, customerId);
    }

    /**
     * Tải file excel báo cáo công suất năng lượng.
     *
     * @param projectId Mã dự án.
     * @param systemTypeId Mã thành phần.
     * @param fromDate Ngày bắt đầu.
     * @param toDate Ngày kết thúc.
     * @return File zip.
     */
    @GetMapping ("/powerTotal/download/{customerId}/{projectId}")
    public ResponseEntity<Resource> downloadPowerTotal(@PathVariable final String customerId,
        @PathVariable final String projectId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @RequestParam final String userName) {
        User user = profileService.getUser(userName);
        return loadClient.downloadPowerTotal(customerId, projectId, fromDate, toDate, user);
    }

}
