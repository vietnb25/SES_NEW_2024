package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.dto.DataRmuDrawer1;

@Mapper
public interface DataRmuDrawer1Mapper {

    List<DataRmuDrawer1> getTotalPowerGrid(String schema, @Param ("deviceIds") String[] deviceIds, String today);

    List<DataRmuDrawer1> getDataChartGrid(Map<String, String> condition);

    Integer countCurrentData(Map<String, String> condition);

    DataRmuDrawer1 getTotalPowerByProjectId(Map<String, String> condition);

    Float getMaxTotalPower(Map<String, String> condition);

    Float getMinTotalPowerInDay(Map<String, String> condition);

    Float getMaxTotalPowerInDay(Map<String, String> condition);

    Float getAvgTotalPower(Map<String, Object> condition);

    Float getAvgTotalPowerInDay(Map<String, String> condition);

    Float getSumTotalPower(Map<String, Object> condition);

    List<DataRmuDrawer1> getPowerDeviceByProjectId(Map<String, String> condition);

    Long getSumEpInDay(Map<String, String> condition);

    Long getSumEpInMonth(Map<String, String> condition);

    Long getSumEp(Map<String, String> condition);

    Long getPtotalInDay(Map<String, String> condition);

    // Warning: START
    List<DataRmuDrawer1> getWarningDataRMUByDeviceId(Map<String, Object> condition);

    // Warning: END

    // Chart: START
    List<DataRmuDrawer1> getChartRmuByCustomerId(Map<String, Object> condition);
    // Warning: END

    DataRmuDrawer1 getInforDataGridByTime(Map<String, Object> condition);

    List<DataRmuDrawer1> getListWarnedData(Map<String, Object> condition);
}
