package vn.ses.s3m.plus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ses.s3m.plus.dao.SettingShiftMapper;
import vn.ses.s3m.plus.dto.SettingShift;
import vn.ses.s3m.plus.form.SettingShiftForm;

import java.util.List;
import java.util.Map;

@Service
public class SettingShiftServiceImpl implements SettingShiftService {
    @Autowired
    private SettingShiftMapper mapper;

    @Override
    public List<SettingShift> getSettingShiftByProject(Map<String, Object> con) {
        return this.mapper.getSettingShiftByProject(con);
    }

    @Override
    public SettingShift getSettingShiftById(Integer id) {
        return this.mapper.getSettingShiftById(id);
    }

    @Override
    public void addSettingShift(SettingShiftForm form) {
        this.mapper.addSettingShift(form);
    }

    @Override
    public void updateSettingShift(SettingShift con) {
        mapper.updateSettingShift(con);
    }

    @Override
    public void updateStatusSettingShift(Integer status, Integer id) {
        this.mapper.updateStatusSettingShift(status, id);
    }

    @Override
    public void deleteSettingShift(Integer status, Integer id) {
        this.mapper.deleteSettingShift(status, id);
    }
}
