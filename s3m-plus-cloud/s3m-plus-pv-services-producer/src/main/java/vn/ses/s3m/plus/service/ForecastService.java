package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Forecast;

public interface ForecastService {
List<Forecast> getForecasts(Map<String, Object> condition);
Integer countTotalForecasts(Map<String, Object> condition);
Forecast getForecast(Map<String, Object> condition);
void insertForecast(Map<String, Object> condition);

void insertForecastHistory(Map<String, Object> condition);

void updateForecast(Map<String, Object> condition);

}
