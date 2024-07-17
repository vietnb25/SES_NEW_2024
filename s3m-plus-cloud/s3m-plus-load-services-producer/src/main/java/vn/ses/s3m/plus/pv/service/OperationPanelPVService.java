package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataPanel1;

public interface OperationPanelPVService {

    DataPanel1 getInstantOperationPanelPV(Map<String, Object> condition);

    List<DataPanel1> getOperationPanelPV(Map<String, Object> condition);

    Integer countDataOperationPanelPV(Map<String, Object> condition);

}
