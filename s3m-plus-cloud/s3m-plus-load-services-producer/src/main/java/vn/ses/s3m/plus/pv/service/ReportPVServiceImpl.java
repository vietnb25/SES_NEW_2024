package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dto.DataInverter1;
import vn.ses.s3m.plus.dto.Report;
import vn.ses.s3m.plus.pv.dao.ReportMapperPV;

@Service
public class ReportPVServiceImpl implements ReportPVService {
    @Autowired
    ReportMapperPV reportMapper;

    @Override
    public List<DataInverter1> getDataInverterPV(Map<String, String> condition) {
        return reportMapper.getDataInverterPV(condition);
    }

    @Override
    public void addReportPV(Report report) {
        reportMapper.addReportPV(report);
    }

    @Override
    public Integer getUserIdPV(String userName) {
        return reportMapper.getUserIdPV(userName);
    }

    @Override
    public List<Report> getReportPV(Integer userId) {
        return reportMapper.getReportPV(userId);
    }

    @Override
    public DataInverter1 getDataInverterInDayPV(Map<String, String> condition) {
        return reportMapper.getDataInverterInDayPV(condition);
    }

    @Override
    public DataInverter1 getDataInverterInMonthPV(Map<String, String> condition) {
        return reportMapper.getDataInverterInMonthPV(condition);
    }

    @Override
    public DataInverter1 getDataInverterInYearPV(Map<String, String> condition) {
        return reportMapper.getDataInverterInYearPV(condition);
    }

    @Override
    public List<Report> getListByLimitPV(Map<String, Integer> map) {
        return reportMapper.getListByLimitPV(map);
    }

    @Override
    public String getProjectNamePV(String projectId) {
        return reportMapper.getProjectNamePV(projectId);
    }

    @Override
    public void updatePercentPV(Map<String, String> condition) {
        reportMapper.updatePercentPV(condition);

    }

    @Override
    public void updateStatusPV(Integer id) {
        reportMapper.updateStatusPV(id);
    }

    @Override
    public void updateTimeFinishPV(Integer id) {
        reportMapper.updateTimeFinishPV(id);
    }

    @Override
    public void deleteReportPV(Integer id) {
        reportMapper.deleteReportPV(id);

    }

    @Override
    public List<Map<String, String>> getTotalWarningPv(Map<String, String> conditions) {
        return reportMapper.getTotalWarningPv(conditions);
    }

    @Override
    public String getTotalWarning(Map<String, String> conditions) {
        return reportMapper.getTotalWarning(conditions);
    }

    @Override
    public List<Map<String, Object>> getDevicePV(Map<String, String> condition) {
        return reportMapper.getDevicePV(condition);
    }

}
