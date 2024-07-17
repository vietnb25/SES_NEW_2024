package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataLoadFrame1;
import vn.ses.s3m.plus.dto.DataLoadFrame2;

public interface OperationInformationService {

    DataLoadFrame1 getInstantOperationInformation(Map<String, Object> condition);

    List<DataLoadFrame1> getOperationInformation(Map<String, Object> condition);

    List<DataLoadFrame1> getDataPQSByMonth(Map<String, Object> condition);

    DataLoadFrame2 getInstantPowerQuality(Map<String, Object> condition);

    List<DataLoadFrame2> getPowerQualities(Map<String, Object> condition);

    DataLoadFrame2 getDataHarmonic(Map<String, String> condition);

    Integer countTotalData(Map<String, Object> condition);

    Integer countDataFrame2(Map<String, Object> condition);

    List<DataLoadFrame1> getHarmonicPeriod(Map<String, Object> condition);

    DataLoadFrame2 getDataHarmonicByDay(Map<String, Object> condition);

}
