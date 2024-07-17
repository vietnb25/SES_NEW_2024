package vn.ses.s3m.plus.pv.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.DataInverter1;

@Mapper
public interface DataInverterMapper {

    DataInverter1 getInstantOperationInverterPV(Map<String, Object> condition);

    List<DataInverter1> getOperationInverterPV(Map<String, Object> condition);

    Integer countDataOperationInverterPV(Map<String, Object> condition);

    List<DataInverter1> getDataInverterByDevice(Map<String, Object> condition);

    DataInverter1 getDataInverterByDeviceIdInFifMinute(Map<String, Object> condition);

    List<DataInverter1> getInverterEveryYearByDeviceId(Map<String, Object> condition);

    List<DataInverter1> getInverterInDayByDeviceId(Map<String, Object> condition);

    List<DataInverter1> getInverterInMonthByDeviceId(Map<String, Object> condition);

    List<DataInverter1> getInverterInYearByDeviceId(Map<String, Object> condition);

    List<DataInverter1> getDataPQSByMonthInverter(Map<String, Object> condition);

    List<DataInverter1> getInverterInPrevDayByDeviceId(Map<String, Object> condition);

    List<DataInverter1> getInverterInPrevMonthByDeviceId(Map<String, Object> condition);

    List<DataInverter1> getInverterInPrevYearByDeviceId(Map<String, Object> condition);

    DataInverter1 getOperationSettingInverter(Map<String, Object> condition);
}
