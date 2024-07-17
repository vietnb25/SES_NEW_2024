package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataLoadFrame1;
import vn.ses.s3m.plus.dto.DataRmuDrawer1;
import vn.ses.s3m.plus.dto.DataTempHumidity;

public interface DataTempHumidityService {

	DataTempHumidity getInforDataTempHumidityByTime(Map<String, Object> condition);

    List<DataTempHumidity> getListWarnedData(Map<String, Object> condition);

}
