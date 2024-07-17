package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.DataCombiner1;

@Mapper
public interface DataCombinerMapper {

    // Chart: START
    List<DataCombiner1> getChartCombinerByCustomerId(Map<String, Object> condition);
    // Warning: END
}
