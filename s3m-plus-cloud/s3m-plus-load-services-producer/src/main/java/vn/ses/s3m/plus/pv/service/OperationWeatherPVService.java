package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataWeather1;

public interface OperationWeatherPVService {

    DataWeather1 getInstantOperationWeatherPV(Map<String, Object> condition);

    List<DataWeather1> getOperationWeatherPV(Map<String, Object> condition);

    Integer countDataOperationWeatherPV(Map<String, Object> condition);

    DataWeather1 getInstantOperationWeatherInProjectId(Map<String, Object> condition);

}
