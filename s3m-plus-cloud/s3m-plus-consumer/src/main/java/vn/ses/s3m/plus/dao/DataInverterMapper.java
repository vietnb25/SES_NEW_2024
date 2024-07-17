package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.dto.DataInverter1;
import vn.ses.s3m.plus.dto.DataLoadFrame1;
import vn.ses.s3m.plus.dto.Device;

@Mapper
public interface DataInverterMapper {
    List<DataInverter1> getDataChartPV(Map<String, String> condition);

    List<DataInverter1> getTotalPowerPV(String schema, @Param ("deviceIds") String[] deviceIds, String today);

    Device getTotalPowerByProjectId(Map<String, String> condition);

    int countCurrentData(Map<String, String> condition);

    Float getMaxTotalPower(Map<String, String> condition);

    Float getMinTotalPower(Map<String, String> condition);

    Float getAvgTotalPower(Map<String, String> condition);

    Float getSumTotalPower(Map<String, Object> condition);

    List<DataInverter1> getPowerDeviceByProjectId(Map<String, String> condition);

    Long getSumEpInDay(Map<String, String> condition);

    Long getSumEpInMonth(Map<String, String> condition);

    Long getSumEp(Map<String, String> condition);

    Float getPtotalInDay(Map<String, String> condition);

    Float getMaxTotalPowerInDay(Map<String, String> condition);

    Float getMinTotalPowerInDay(Map<String, String> condition);

    Float getAvgTotalPowerInDay(Map<String, String> condition);

    // Warning: START
    List<DataInverter1> getWarningDataInverterByDeviceId(Map<String, Object> condition);
    // Warning: END
    
    DataInverter1 getInforDataInverterByTime(Map<String, Object> condition);

    List<DataInverter1> getListWarnedData(Map<String, Object> condition);

    // Chart: START
    List<DataInverter1> getChartSolarByCustomerId(Map<String, Object> condition);
    // Warning: END
}
