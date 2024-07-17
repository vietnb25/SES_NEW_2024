package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.Forecast;
import vn.ses.s3m.plus.dto.OverviewLoadTotalPower;
import vn.ses.s3m.plus.dto.OverviewPVPower;
import vn.ses.s3m.plus.dto.OverviewPVTotalPower;

@Mapper
public interface OverviewPowerMapper {
    
    Forecast getForecast(Map<String, Object> condition);

    void insertForecast(Map<String, Object> condition);

    void updateForecast(Map<String, Object> condition);

    void insertForecastHistory(Map<String, Object> condition);

    OverviewPVPower getOverviewPowerPV(Map<String, Object> condition);

    OverviewLoadTotalPower getTotalPowerInDay(Map<String, Object> condition);

    OverviewPVTotalPower getTotalPowerPVInDay(Map<String, Object> condition);

    Map<String, String> getInformationProject(Map<String, Object> condition);

    List<Device> getListDeviceLoadByProjectId(Map<String, Object> condition);

    List<Forecast> getForecasts(Map<String, Object> condition);

    Integer countTotalForecasts(Map<String, Object> condition);

    Integer getDeviceHasWarning(Map<String, Object> condition);

    OverviewPVPower getLayerPV(Map<String, Object> condition);
}
