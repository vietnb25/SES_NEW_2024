package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.dto.DataFlow;
import vn.ses.s3m.plus.dto.DataLoadFrame1;
import vn.ses.s3m.plus.dto.DataTempHumidity;

@Mapper
public interface DataFlowMapper {

    DataFlow getInforDataFlowByTime(Map<String, Object> condition);

    List<DataFlow> getListWarnedData(Map<String, Object> condition);
}
