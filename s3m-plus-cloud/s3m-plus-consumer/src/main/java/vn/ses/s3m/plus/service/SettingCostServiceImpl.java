package vn.ses.s3m.plus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ses.s3m.plus.dao.SettingCostMapper;
import vn.ses.s3m.plus.dto.SettingCost;
import vn.ses.s3m.plus.dto.SettingCostHistory;
import vn.ses.s3m.plus.form.SettingCostForm;

import java.util.List;
import java.util.Map;

@Service
public class SettingCostServiceImpl implements  SettingCostService{

    @Autowired
    private SettingCostMapper mapper;

    @Override
    public List<SettingCost> getListByProject(Map<String, Object> con) {
        return this.mapper.getListByProject(con);
    }

    @Override
    public SettingCost getById(Map<String, Object> con) {
        return this.mapper.getById(con);
    }

    @Override
    public SettingCost getVat(String con) {
        return this.mapper.getVat(con);
    }

    @Override
    public void update(Map<String, Object> con) {
        this.mapper.update(con);
    }


    @Override
    public void inset(SettingCost setting) {
        this.mapper.insertHistory(setting);
    }
    
    /**
     * Thêm thông tin setting cosst
     *
     * @param condition thông tin setting cost
     */
    @Override
    public void addSettingCost(final Map<String, Object> condition) {
    	this.mapper.addSettingCost(condition);
    }

    @Override
    public List<SettingCost> getListForReport(Map<String, Object> con) {
        return this.mapper.getListForReport(con);
    }

    @Override
    public List<SettingCostHistory> getListForReport1(Map<String, Object> con) {
        return this.mapper.getListForReport1(con);
    }

    @Override
    public void insertHistoryNew(SettingCostForm setting) {
        this.mapper.insertHistoryNew(setting);
    }
}
