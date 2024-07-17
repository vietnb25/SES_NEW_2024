package vn.ses.s3m.plus.service;

import vn.ses.s3m.plus.dto.SettingCost;
import vn.ses.s3m.plus.dto.SettingCostHistory;
import vn.ses.s3m.plus.form.SettingCostForm;

import java.util.List;
import java.util.Map;

public interface SettingCostService {
    public List<SettingCost> getListByProject(Map<String,Object> con);

    public SettingCost getById(Map<String, Object> con);
    public SettingCost getVat(String con);

    public void update(Map<String,Object> con);

    public void inset(SettingCost setting);
    
    void addSettingCost(Map<String, Object> condition);
    List<SettingCost> getListForReport(Map<String, Object> con);
    List<SettingCostHistory> getListForReport1(Map<String, Object> con);
    public void insertHistoryNew(SettingCostForm setting);

}
