package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.DataLoadFrame2;

@Mapper
public interface DataLoadFrame2Mapper {

    DataLoadFrame2 getInstantPowerQuality(Map<String, Object> condition);

    List<DataLoadFrame2> getPowerQualities(Map<String, Object> condition);

    DataLoadFrame2 getDataHarmonic(Map<String, String> condition);

    Integer countDataFrame2(Map<String, Object> condition);

    DataLoadFrame2 getDataHarmonicByDay(Map<String, Object> condition);

}
