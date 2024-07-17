package vn.ses.s3m.plus.service;


import vn.ses.s3m.plus.dto.SettingShift;
import vn.ses.s3m.plus.form.SettingShiftForm;

import java.util.List;
import java.util.Map;

public interface SettingShiftService {

    List<SettingShift> getSettingShiftByProject(Map<String, Object> con);

    SettingShift getSettingShiftById(Integer id);

    void addSettingShift(SettingShiftForm form);

    void updateSettingShift(SettingShift con);

    void updateStatusSettingShift(Integer status, Integer id);

    void deleteSettingShift(Integer status, Integer id);

}
