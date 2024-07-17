package vn.ses.s3m.plus.pv.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.Forecast;
import vn.ses.s3m.plus.pv.service.ForecastService;

@RestController
@RequestMapping ("/pv")
public class ForecastController {

    private static final int PAGE_SIZE = 30;

    @Autowired
    private ForecastService forecastService;

    @GetMapping ("/forecasts/{customerId}/{projectId}/{systemTypeId}/{page}")
    public ResponseEntity<?> getForecastsPV(@PathVariable final Integer customerId, @PathVariable Long projectId,
        @PathVariable Long systemTypeId, @PathVariable Integer page) {

        Map<String, Object> condition = new HashMap<>();
        condition.put("projectId", projectId);
        condition.put("systemTypeId", systemTypeId);
        condition.put("start", (page - 1) * PAGE_SIZE);
        condition.put("end", PAGE_SIZE);
        condition.put("schema", Schema.getSchemas(customerId));
        List<Forecast> data = forecastService.getForecasts(condition);

        int totalData = forecastService.countTotalForecasts(condition);

        double totalPage = Math.ceil((double) totalData / PAGE_SIZE);

        Map<String, Object> mapData = new HashMap<>();
        mapData.put("totalPage", totalPage);
        mapData.put("currentPage", page);
        mapData.put("data", data);

        return new ResponseEntity<>(mapData, HttpStatus.OK);
    }

    @GetMapping ("/forecast/{customerId}/{projectId}/{systemTypeId}")
    public ResponseEntity<?> getForecastPV(@PathVariable Integer customerId, @PathVariable Long projectId,
        @PathVariable Long systemTypeId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("projectId", projectId);
        condition.put("systemTypeId", systemTypeId);
        condition.put("schema", Schema.getSchemas(customerId));
        Forecast forecast = forecastService.getForecast(condition);
        if (forecast != null) {
            return new ResponseEntity<Forecast>(forecast, HttpStatus.OK);
        } else {
            Forecast forecastNull = new Forecast();
            forecastNull.setProjectId(projectId);
            forecastNull.setSystemTypeId(Math.toIntExact(systemTypeId));
            forecastNull.setA0(0d);
            forecastNull.setA1(0d);
            forecastNull.setA2(0d);
            forecastNull.setA3(0d);
            forecastNull.setA4(0d);
            forecastNull.setA5(0d);
            forecastNull.setA6(0d);
            forecastNull.setA7(0d);
            forecastNull.setA8(0d);
            forecastNull.setA9(0d);
            return new ResponseEntity<>(forecastNull, HttpStatus.OK);
        }
    }

    @PostMapping ("/forecast/save/{customerId}")
    public ResponseEntity<?> saveForecastPV(@PathVariable final Integer customerId, @RequestBody Forecast forecast) {
        if (forecast.getId() != null) {
            Map<String, Object> condition = new HashMap<>();
            condition.put("id", forecast.getId());
            condition.put("projectId", forecast.getProjectId());
            condition.put("systemTypeId", forecast.getSystemTypeId());
            condition.put("a0", forecast.getA0());
            condition.put("a1", forecast.getA1());
            condition.put("a2", forecast.getA2());
            condition.put("a3", forecast.getA3());
            condition.put("a4", forecast.getA4());
            condition.put("a5", forecast.getA5());
            condition.put("a6", forecast.getA6());
            condition.put("a7", forecast.getA7());
            condition.put("a8", forecast.getA8());
            condition.put("a9", forecast.getA9());
            condition.put("schema", Schema.getSchemas(customerId));
            forecastService.updateForecast(condition);
            forecastService.insertForecastHistory(condition);
        } else {
            Map<String, Object> condition = new HashMap<>();
            condition.put("projectId", forecast.getProjectId());
            condition.put("systemTypeId", forecast.getSystemTypeId());
            condition.put("a0", forecast.getA0());
            condition.put("a1", forecast.getA1());
            condition.put("a2", forecast.getA2());
            condition.put("a3", forecast.getA3());
            condition.put("a4", forecast.getA4());
            condition.put("a5", forecast.getA5());
            condition.put("a6", forecast.getA6());
            condition.put("a7", forecast.getA7());
            condition.put("a8", forecast.getA8());
            condition.put("a9", forecast.getA9());
            condition.put("schema", Schema.getSchemas(customerId));
            forecastService.insertForecast(condition);
            forecastService.insertForecastHistory(condition);
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
