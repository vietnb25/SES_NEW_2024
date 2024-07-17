package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.ShiftSetting;

@Mapper
public interface ShiftSettingMapper {
    List<ShiftSetting> getShiftsettings(Map<String, Object> condition);

    List<ShiftSetting> getSettingShift(Map<String, Object> condition);

    ShiftSetting getShiftHistorysLikeShiftHistoryCode(Map<String, Object> condition);

    ShiftSetting getShiftsettingById(Integer id);

    ShiftSetting getShiftsetting(Map<String, Object> condition);

    void addShiftSetting(ShiftSetting shiftSetting);

    void addSettingShift(Map<String, Object> condition);

    void updateShiftSetting(ShiftSetting shiftSetting);

    void addShiftHistory(ShiftSetting shiftSetting);

    void updateShiftHistory(ShiftSetting shiftSetting);
}
