package vn.ses.s3m.plus.dao;

import org.apache.ibatis.annotations.Mapper;
import vn.ses.s3m.plus.dto.DataLoadFrame2;

import java.util.List;
import java.util.Map;

@Mapper
public interface DataLoadFrame2Mapper {
    DataLoadFrame2 getInforDataFrame2LoadByTime(Map<String, Object> condition);

    List<DataLoadFrame2> getListWarnedDataFrame2(Map<String, Object> condition);
}
