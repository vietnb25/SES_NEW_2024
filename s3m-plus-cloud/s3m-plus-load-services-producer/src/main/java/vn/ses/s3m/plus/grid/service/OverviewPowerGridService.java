package vn.ses.s3m.plus.grid.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Forecast;
import vn.ses.s3m.plus.dto.OverviewGridPower;
import vn.ses.s3m.plus.dto.OverviewGridTotalPower;

public interface OverviewPowerGridService {

    OverviewGridPower getPowerRMU(Map<String, Object> condition);

    Map<String, String> getInformationProject(Map<String, Object> condition);

    List<OverviewGridPower> getDeviceHasWarning(Map<String, Object> condition);

    List<OverviewGridPower> getLayer(Map<String, Object> condition);

    List<OverviewGridTotalPower> getListPowerRMUInDay(Map<String, Object> condition);

    Forecast getForecast(Map<String, Object> condition);

    List<Forecast> getListForecast(Map<String, Object> condition);

}
