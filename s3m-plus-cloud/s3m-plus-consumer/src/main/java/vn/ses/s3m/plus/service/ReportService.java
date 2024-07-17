package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataPqs;
import vn.ses.s3m.plus.dto.SettingShiftEp;

public interface ReportService {

    List<DataPqs> getEnergyTotal(Map<String, Object> condition);

    List<DataPqs> getCostTotal(Map<String, Object> condition);

    List<DataPqs> getEnergyTotalByShift(Map<String, Object> condition);

    List<DataPqs> getStrengthTotal(Map<String, Object> condition);

    List<DataPqs> getUseEnergyCompare(Map<String, Object> condition);

    List<DataPqs> getWarningTotal(Map<String, Object> condition);

    List<DataPqs> getPTotalByDeviceId(Map<String, Object> condition);

    List<DataPqs> getListPTotalByDeviceIds(Map<String, Object> condition);

    List<DataPqs> getMaxPTotalByDeviceIds(Map<String, Object> condition);

    List<DataPqs> getMinPTotalByDeviceIds(Map<String, Object> condition);
    DataPqs getCostForCycle(Map<String, Object> condition);

    List<DataPqs> getDeviceByWarningType(String fromDate, String toDate, Integer warningType, Integer typeTime,String deviceId,String schema,Integer projectId, Integer systemTypeId);

    SettingShiftEp getEpByShiftAndViewTime(Map<String, Object> condition);
    List<DataPqs> getComparingEnergyUsageByLoadType(Map<String, Object> condition);

    List<DataPqs> getEpByDevicesAndViewTime(Map<String, Object> condition);


}
