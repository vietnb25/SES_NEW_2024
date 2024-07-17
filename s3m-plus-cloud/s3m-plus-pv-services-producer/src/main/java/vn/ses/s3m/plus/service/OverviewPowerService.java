package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.Forecast;
import vn.ses.s3m.plus.dto.OverviewLoadTotalPower;
import vn.ses.s3m.plus.dto.OverviewPVPower;
import vn.ses.s3m.plus.dto.OverviewPVTotalPower;

public interface OverviewPowerService {

    OverviewPVPower getPower(Map<String, Object> condition);

    OverviewLoadTotalPower getTotalPowerInDay(Map<String, Object> condition);

    OverviewPVTotalPower getTotalPowerPVInDay(Map<String, Object> condition);

    Map<String, String> getInformationProject(Map<String, Object> condition);

    List<Device> getListDeviceLoadByProjectId(Map<String, Object> condition);

    Forecast getForecast(Map<String, Object> condition);

    List<Forecast> getForecasts(Map<String, Object> condition);

    Integer countTotalForecasts(Map<String, Object> condition);

    void insertForecast(Map<String, Object> condition);

    void insertForecastHistory(Map<String, Object> condition);

    void updateForecast(Map<String, Object> condition);

    Integer getDeviceHasWarning(Map<String, Object> condition);

    OverviewPVPower getLayer(Map<String, Object> condition);
}
