package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataString1;

public interface OperationStringPVService {

    DataString1 getInstantOperationStringPV(Map<String, Object> condition);

    List<DataString1> getOperationStringPV(Map<String, Object> condition);

    Integer countDataOperationStringPV(Map<String, Object> condition);

}
