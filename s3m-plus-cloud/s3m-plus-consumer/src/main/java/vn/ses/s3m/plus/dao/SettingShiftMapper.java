package vn.ses.s3m.plus.dao;

import org.apache.ibatis.annotations.Mapper;
import vn.ses.s3m.plus.dto.SettingShift;
import vn.ses.s3m.plus.form.SettingShiftForm;

import java.util.List;
import java.util.Map;

@Mapper
public interface SettingShiftMapper {

    List<SettingShift> getSettingShiftByProject(Map<String, Object> con);

    SettingShift getSettingShiftById(Integer id);

    void addSettingShift(SettingShiftForm form);

    void updateSettingShift(SettingShift con);

    void updateStatusSettingShift(Integer status, Integer id);

    void deleteSettingShift(Integer status, Integer id);

}
