package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataCombiner1;

public interface OperationCombinerPVService {

    DataCombiner1 getInstantOperationCombinerPV(Map<String, Object> condition);

    List<DataCombiner1> getOperationCombinerPV(Map<String, Object> condition);

    Integer countDataOperationCombinerPV(Map<String, Object> condition);

}
