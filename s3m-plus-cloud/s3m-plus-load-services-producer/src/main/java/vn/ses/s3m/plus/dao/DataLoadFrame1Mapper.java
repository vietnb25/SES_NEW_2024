package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.DataLoadFrame1;

@Mapper
public interface DataLoadFrame1Mapper {

    DataLoadFrame1 getTotalPowerByProjectId(String projectId);

    DataLoadFrame1 getInstantOperationInformation(Map<String, Object> condition);

    List<DataLoadFrame1> getDataLoadWarning(Map<String, Object> condition);

    List<DataLoadFrame1> getOperationInformation(Map<String, Object> condition);

    List<DataLoadFrame1> getDataPQSByMonth(Map<String, Object> condition);

    Integer countTotalData(Map<String, Object> condition);

    List<DataLoadFrame1> getHarmonicPeriod(Map<String, Object> condition);

}
