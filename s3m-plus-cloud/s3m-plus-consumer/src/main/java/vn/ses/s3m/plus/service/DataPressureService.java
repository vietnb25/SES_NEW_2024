package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataFlow;
import vn.ses.s3m.plus.dto.DataPressure;

public interface DataPressureService {

	DataPressure getInforDataPressureByTime(Map<String, Object> condition);

    List<DataPressure> getListWarnedData(Map<String, Object> condition);

}
