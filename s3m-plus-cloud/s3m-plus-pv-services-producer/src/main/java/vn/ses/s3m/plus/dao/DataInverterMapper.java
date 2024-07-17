package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.DataInverter1;

@Mapper
public interface DataInverterMapper {

    DataInverter1 getInstantOperationInverterPV(Map<String, Object> condition);

    List<DataInverter1> getOperationInverterPV(Map<String, Object> condition);

    Integer countDataOperationInverterPV(Map<String, Object> condition);
}
