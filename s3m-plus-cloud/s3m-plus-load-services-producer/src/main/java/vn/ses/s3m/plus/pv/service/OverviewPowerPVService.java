package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.Forecast;
import vn.ses.s3m.plus.dto.OverviewLoadTotalPower;
import vn.ses.s3m.plus.dto.OverviewPVPower;
import vn.ses.s3m.plus.dto.OverviewPVTotalPower;

public interface OverviewPowerPVService {
    List<OverviewPVPower> getPowerPV(Map<String, Object> condition);

    List<OverviewPVPower> getPowerCombiner(Map<String, Object> condition);

    List<OverviewPVPower> getPowerString(Map<String, Object> condition);

    OverviewLoadTotalPower getTotalPowerInDay(Map<String, Object> condition);

    OverviewPVTotalPower getTotalPowerPVInDay(Map<String, Object> condition);

    Map<String, String> getInformationProject(Map<String, Object> condition);

    List<Device> getListDeviceLoadByProjectId(Map<String, Object> condition);

    Forecast getForecast(Map<String, Object> condition);

    List<Forecast> getListForecast(Map<String, Object> condition);

    List<Forecast> getForecasts(Map<String, Object> condition);

    Integer countTotalForecasts(Map<String, Object> condition);

    void insertForecast(Map<String, Object> condition);

    void insertForecastHistory(Map<String, Object> condition);

    void updateForecast(Map<String, Object> condition);

    List<OverviewPVPower> getDeviceHasWarning(Map<String, Object> condition);

    List<OverviewPVPower> getLayer(Map<String, Object> condition);

    List<OverviewPVTotalPower> getListPowerInDay(Map<String, Object> condition);

    List<OverviewPVTotalPower> getListPowerCombinerInDay(Map<String, Object> condition);

    List<OverviewPVTotalPower> getListPowerStringInDay(Map<String, Object> condition);

    List<OverviewPVPower> getOverviewPowerWeather(Map<String, Object> condition);

}
