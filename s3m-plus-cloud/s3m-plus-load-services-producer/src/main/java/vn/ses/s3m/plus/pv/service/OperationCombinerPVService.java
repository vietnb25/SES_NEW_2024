package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataCombiner1;

public interface OperationCombinerPVService {

    DataCombiner1 getInstantOperationCombinerPV(Map<String, Object> condition);

    List<DataCombiner1> getOperationCombinerPV(Map<String, Object> condition);

    Integer countDataOperationCombinerPV(Map<String, Object> condition);

    DataCombiner1 getDataCombinerByDeviceIdInFifMinute(Map<String, Object> condition);

    List<DataCombiner1> getDataCombinerPV(Map<String, Object> condition);

}
