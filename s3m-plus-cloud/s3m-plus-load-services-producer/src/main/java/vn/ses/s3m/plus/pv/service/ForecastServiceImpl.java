package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.OverviewPowerMapper;
import vn.ses.s3m.plus.dto.Forecast;

@Service
public class ForecastServiceImpl implements ForecastService {
	@Autowired
	OverviewPowerMapper forecastMapper;

	@Override
	public List<Forecast> getForecasts(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return forecastMapper.getForecasts(condition);
	}

	@Override
	public Integer countTotalForecasts(final Map<String, Object> condition) {
		return forecastMapper.countTotalForecasts(condition);
	}

	@Override
	public Forecast getForecast(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return forecastMapper.getForecast(condition);
	}

	@Override
	public void insertForecast(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		forecastMapper.insertForecast(condition);
	}

	@Override
	public void insertForecastHistory(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		forecastMapper.insertForecastHistory(condition);
	}

	@Override
	public void updateForecast(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		forecastMapper.updateForecast(condition);
	}
}
