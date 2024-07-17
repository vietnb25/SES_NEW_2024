package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataString1;

public interface OperationStringPVService {

    DataString1 getInstantOperationStringPV(Map<String, Object> condition);

    List<DataString1> getOperationStringPV(Map<String, Object> condition);

    Integer countDataOperationStringPV(Map<String, Object> condition);

    List<DataString1> getInstantOperationStringInCombinerPV(Map<String, Object> condition);

    DataString1 getDataStringByDeviceIdInFifMinute(Map<String, Object> condition);

    DataString1 getInstantOperationStringInProjectId(Map<String, Object> condition);

}
