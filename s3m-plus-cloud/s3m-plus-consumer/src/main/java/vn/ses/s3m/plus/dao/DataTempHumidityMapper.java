package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.dto.DataLoadFrame1;
import vn.ses.s3m.plus.dto.DataTempHumidity;

@Mapper
public interface DataTempHumidityMapper {

    DataTempHumidity getInforDataTempHumidityByTime(Map<String, Object> condition);

    List<DataTempHumidity> getListWarnedData(Map<String, Object> condition);
}
