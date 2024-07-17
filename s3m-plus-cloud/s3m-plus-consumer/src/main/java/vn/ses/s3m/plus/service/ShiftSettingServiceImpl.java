package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.ShiftSettingMapper;
import vn.ses.s3m.plus.dto.ShiftSetting;

@Service
public class ShiftSettingServiceImpl implements ShiftSettingService {

    @Autowired
    private ShiftSettingMapper shiftsettingMapper;

    /**
     * Lấy ra danh sách setting ca làm việc
     *
     * @return danh sách setting ca làm việc.
     */
    @Override
    public List<ShiftSetting> getShiftsettings(final Map<String, Object> condition) {
        return shiftsettingMapper.getShiftsettings(condition);
    }

    @Override
    public List<ShiftSetting> getSettingShift(Map<String, Object> condition) {
        return shiftsettingMapper.getSettingShift(condition);
    }

    @Override
    public void addSettingShift(Map<String, Object> condition) {
        shiftsettingMapper.addSettingShift(condition);
    }

    /**
     * Lấy ra danh sách thời gian ca làm việc.
     *
     * @return danh sách thời gian ca làm việc.
     */
    @Override
    public ShiftSetting getShiftHistorysLikeShiftHistoryCode(Map<String, Object> condition) {
        return shiftsettingMapper.getShiftHistorysLikeShiftHistoryCode(condition);
    }

    @Override
    public ShiftSetting getShiftsettingById(final Integer id) {
        return shiftsettingMapper.getShiftsettingById(id);
    }

    @Override
    public ShiftSetting getShiftsetting(Map<String, Object> condition) {
        return shiftsettingMapper.getShiftsetting(condition);
    }

    /**
     * Thêm setting ca làm việc.
     *
     * @return setting ca làm việc đã được thêm vào db.
     */
    @Override
    public void addShiftSetting(ShiftSetting shiftSetting) {
        shiftsettingMapper.addShiftSetting(shiftSetting);
    }

    /**
     * Cập nhật setting ca làm việc.
     *
     * @return setting ca làm việc đã được cập nhật vào db.
     */
    @Override
    public void updateShiftSetting(ShiftSetting shiftSetting) {
        shiftsettingMapper.updateShiftSetting(shiftSetting);
    }

    /**
     * Thêm setting ca làm việc.
     *
     * @return setting ca làm việc đã được thêm vào db.
     */
    @Override
    public void addShiftHistory(ShiftSetting shiftSetting) {
        shiftsettingMapper.addShiftHistory(shiftSetting);
    }

    /**
     * Cập nhật setting ca làm việc.
     *
     * @return setting ca làm việc đã được cập nhật vào db.
     */
    @Override
    public void updateShiftHistory(ShiftSetting shiftSetting) {
        shiftsettingMapper.updateShiftHistory(shiftSetting);
    }
}
