package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.Forecast;
import vn.ses.s3m.plus.dto.OverviewGridPower;
import vn.ses.s3m.plus.dto.OverviewGridTotalPower;
import vn.ses.s3m.plus.dto.OverviewLoadPower;
import vn.ses.s3m.plus.dto.OverviewLoadTotalPower;
import vn.ses.s3m.plus.dto.OverviewPVPower;
import vn.ses.s3m.plus.dto.OverviewPVTotalPower;

@Mapper
public interface OverviewPowerMapper {

    OverviewLoadPower getOverviewPowers(Map<String, Object> condition);

    List<OverviewPVPower> getOverviewPowerPV(Map<String, Object> condition);

    List<OverviewPVPower> getOverviewPowerCombiner(Map<String, Object> condition);

    List<OverviewPVPower> getOverviewPowerString(Map<String, Object> condition);

    List<OverviewPVPower> getOverviewPowerWeather(Map<String, Object> condition);

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

    Integer getDeviceHasWarning(Map<String, Object> condition);

    List<OverviewLoadPower> getDevicesHasWarning(Map<String, Object> condition);

    List<OverviewPVPower> getDevicesHasWarningPV(Map<String, Object> condition);

    List<OverviewLoadPower> getLayer(Map<String, Object> condition);

    List<OverviewPVPower> getLayerPV(Map<String, Object> condition);

    List<OverviewLoadTotalPower> getListPowerInDay(Map<String, Object> condition);

    List<OverviewPVTotalPower> getListPowerPVInDay(Map<String, Object> condition);

    List<OverviewPVTotalPower> getListPowerCombinerInDay(Map<String, Object> condition);

    List<OverviewPVTotalPower> getListPowerStringInDay(Map<String, Object> condition);

    Long getSumEnergy(Map<String, Object> condition);

    Long getSumEnergyByYear(Map<String, Object> condition);

    Long getSumEnergyByMonth(Map<String, Object> condition);

    Long getSumEnergyByDay(Map<String, Object> condition);

    OverviewGridPower getOverviewPowerRMU(Map<String, Object> condition);

    List<OverviewGridPower> getDevicesHasWarningGrid(Map<String, Object> condition);

    List<OverviewGridPower> getLayerGrid(Map<String, Object> condition);

    List<OverviewGridTotalPower> getListPowerRMUInDay(Map<String, Object> condition);
}
