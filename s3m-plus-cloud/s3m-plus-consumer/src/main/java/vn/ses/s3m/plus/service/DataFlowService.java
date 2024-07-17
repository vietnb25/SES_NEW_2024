package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataFlow;

public interface DataFlowService {

	DataFlow getInforDataFlowByTime(Map<String, Object> condition);

    List<DataFlow> getListWarnedData(Map<String, Object> condition);

}
