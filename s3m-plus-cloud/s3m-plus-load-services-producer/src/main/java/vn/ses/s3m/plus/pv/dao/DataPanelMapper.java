package vn.ses.s3m.plus.pv.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.DataPanel1;

@Mapper
public interface DataPanelMapper {

    DataPanel1 getInstantOperationPanelPV(Map<String, Object> condition);

    List<DataPanel1> getOperationPanelPV(Map<String, Object> condition);

    Integer countDataOperationPanelPV(Map<String, Object> condition);

}
