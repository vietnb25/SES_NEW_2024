package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.dto.DataLoadFrame1;

@Mapper
public interface DataLoadFrame1Mapper {
    DataLoadFrame1 getTotalPowerByProjectId(Map<String, String> condition);

    DataLoadFrame1 getDateNewDevice(String schema);

    List<DataLoadFrame1> getDataChartPower(Map<String, String> condition);

    List<DataLoadFrame1> getTotalPower(String schema, @Param ("deviceIds") String[] deviceIds, String today);

    Integer countCurrentData(Map<String, String> condition);

    Float getMaxTotalPower(Map<String, String> condition);

    Float getMinTotalPower(Map<String, String> condition);

    Float getAvgTotalPower(Map<String, String> condition);

    Float getSumTotalPower(Map<String, Object> condition);

    List<DataLoadFrame1> getPowerDeviceByProjectId(Map<String, String> condition);

    Long getSumEpInDay(Map<String, String> condition);

    Long getSumEpInMonth(Map<String, String> condition);

    Long getSumEp(Map<String, String> condition);

    Float getPtotalInDay(Map<String, String> condition);

    Float getMaxTotalPowerInDay(Map<String, String> condition);

    Float getMinTotalPowerInDay(Map<String, String> condition);

    Float getAvgTotalPowerInDay(Map<String, String> condition);

    // Warning: START
    List<DataLoadFrame1> getWarningDataLoadByDeviceId(Map<String, Object> condition);
    // Warning: END

    // Chart: START
    List<DataLoadFrame1> getChartLoadByCustomerId(Map<String, Object> condition);
    // Warning: END

    DataLoadFrame1 getInforDataLoadByTime(Map<String, Object> condition);

    List<DataLoadFrame1> getListWarnedData(Map<String, Object> condition);
    
    List<DataLoadFrame1> getChartLoadCompare(Map<String, Object> condition);
}
