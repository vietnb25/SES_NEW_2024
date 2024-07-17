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
import vn.ses.s3m.plus.dto.DataEnergyPV;
import vn.ses.s3m.plus.dto.OverviewPVPower;
import vn.ses.s3m.plus.dto.OverviewPVTotalPower;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.service.ProfileService;

@RestController
@RequestMapping ("/pv")
public class OverViewPowerPVController {

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
    public ResponseEntity<List<OverviewPVPower>> getPowerPVInDay(@PathVariable final String customerId,
        @PathVariable final String projectId, @RequestParam (required = false) final String keyword) {

        return loadClient.getPowerPVInDay(customerId, projectId, keyword);
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
    public ResponseEntity<List<OverviewPVTotalPower>> getTotalPowerEnergyPV(@PathVariable final String customerId,
        @PathVariable final String projectId, @RequestParam final String fromDate, @RequestParam final String toDate) {

        return loadClient.getTotalPowerEnergyPV(customerId, projectId, fromDate, toDate);
    }

    /**
     * Lấy gía trị điện năng tổng của tất cả các thiết bị trong dự án Load trong ngày
     *
     * @param projectId ID dự án
     * @return inverter.Wh điện năng tổng của tất cả các thiết bị trong dự án
     */
    @GetMapping ("/energy/{customerId}/{projectId}")
    public ResponseEntity<DataEnergyPV> getTotalACEnergy(@PathVariable final String customerId,
        @PathVariable final String projectId) {
        return loadClient.getTotalACEnergy(customerId, projectId);
    }

    @GetMapping ("/powerTotal/download/{customerId}/{projectId}")
    public ResponseEntity<Resource> downloadPowerTotalPV(@PathVariable final String customerId,
        @PathVariable final String projectId, @RequestParam final String fromDate, @RequestParam final String toDate,
        @RequestParam final String userName) {
        User user = profileService.getUser(userName);
        return loadClient.downloadPowerTotalPV(customerId, projectId, fromDate, toDate, user);
    }
}
