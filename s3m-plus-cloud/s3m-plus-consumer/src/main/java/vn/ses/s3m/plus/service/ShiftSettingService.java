package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.ShiftSetting;

public interface ShiftSettingService {

    List<ShiftSetting> getShiftsettings(Map<String, Object> condition);

    List<ShiftSetting> getSettingShift(Map<String, Object> condition);

    void addSettingShift(Map<String, Object> condition);

    ShiftSetting getShiftHistorysLikeShiftHistoryCode(Map<String, Object> condition);

    ShiftSetting getShiftsettingById(Integer id);

    ShiftSetting getShiftsetting(Map<String, Object> condition);

    void addShiftSetting(ShiftSetting shiftSetting);

    void updateShiftSetting(ShiftSetting shiftSetting);

    void addShiftHistory(ShiftSetting shiftSetting);

    void updateShiftHistory(ShiftSetting shiftSetting);
}
