package vn.ses.s3m.plus.pv.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.DataString1;

@Mapper
public interface DataStringMapper {

    DataString1 getInstantOperationStringPV(Map<String, Object> condition);

    List<DataString1> getOperationStringPV(Map<String, Object> condition);

    Integer countDataOperationStringPV(Map<String, Object> condition);

    List<DataString1> getInstantOperationStringInCombinerPV(Map<String, Object> condition);

    DataString1 getDataStringByDeviceIdInFifMinute(Map<String, Object> condition);

    DataString1 getInstantOperationStringInProjectId(Map<String, Object> condition);
}
