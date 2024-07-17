package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataRmuDrawer1;

public interface DataRmuDrawer1Service {

    List<DataRmuDrawer1> getDataChartGrid(Map<String, String> condition);

    List<DataRmuDrawer1> getTotalPowerGrid(String schema, String[] deviceIds, String today);

    DataRmuDrawer1 getTotalPowerByProjectId(Map<String, String> condition);

    Integer countCurrentData(Map<String, String> condition);

    Float getSumTotalPower(Map<String, Object> condition);

    Float getAvgTotalPower(Map<String, Object> condition);

    Float getAvgTotalPowerInDay(Map<String, String> condition);

    Float getMaxTotalPower(Map<String, String> condition);

    Float getMinTotalPowerInDay(Map<String, String> condition);

    Float getMaxTotalPowerInDay(Map<String, String> condition);

    List<DataRmuDrawer1> getPowerDeviceByProjectId(Map<String, String> condition);

    Long getSumEpInDay(Map<String, String> condition);

    Long getSumEpInMonth(Map<String, String> condition);

    Long getSumEp(Map<String, String> condition);

    Long getPtotalInDay(Map<String, String> condition);

    // Warning: START
    List<DataRmuDrawer1> getWarningDataRMUByDeviceId(Map<String, Object> condition);

    // Warning: END
    DataRmuDrawer1 getInforDataGridByTime(Map<String, Object> condition);

    List<DataRmuDrawer1> getListWarnedData(Map<String, Object> condition);

}
