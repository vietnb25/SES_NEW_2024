package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataInverter1;

public interface OperationPVService {

    DataInverter1 getInstantOperationInverterPV(Map<String, Object> condition);

    List<DataInverter1> getOperationInverterPV(Map<String, Object> condition);

    Integer countDataOperationInverterPV(Map<String, Object> condition);
}
