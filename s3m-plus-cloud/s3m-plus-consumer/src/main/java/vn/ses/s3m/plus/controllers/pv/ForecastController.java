package vn.ses.s3m.plus.controllers.pv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.client.LoadClient;
import vn.ses.s3m.plus.dto.Forecast;

@RestController
@RequestMapping ("/pv")
public class ForecastController {

    @Autowired
    private LoadClient client;

    /**
     * Danh sách dữ liệu lịch sử cài đặt dự báo
     *
     * @param projectId Mã dự án
     * @param systemTypeId Mã hệ thống
     * @return Dữ liệu dự báo
     */
    @GetMapping ("/forecasts/{customerId}/{projectId}/{systemTypeId}/{page}")
    public ResponseEntity<?> getForecasts(@PathVariable Integer customerId, @PathVariable Long projectId,
        @PathVariable Long systemTypeId, @PathVariable final Integer page) {
        return client.getForecastsPV(customerId, projectId, systemTypeId, page);
    }

    @GetMapping ("/forecast/{customerId}/{projectId}/{systemTypeId}")
    public ResponseEntity<?> getForecast(@PathVariable Integer customerId,
        @PathVariable (required = false) String projectId, @PathVariable (required = false) String systemTypeId) {
        return client.getForecastPV(customerId, projectId, systemTypeId);
    }

    @PostMapping ("/forecast/save/{customerId}")
    ResponseEntity<?> saveForecast(@RequestBody Forecast forecast, @PathVariable Integer customerId) {
        return client.saveForecastPV(forecast, customerId);
    }

}
