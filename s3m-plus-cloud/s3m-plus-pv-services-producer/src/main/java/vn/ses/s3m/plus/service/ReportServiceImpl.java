package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.ReportMapperPV;
import vn.ses.s3m.plus.dto.DataInverter1;
import vn.ses.s3m.plus.dto.Report;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    ReportMapperPV reportMapper;

    @Override
    public List<DataInverter1> getDataInverter(String date) {
        return reportMapper.getDataInverter(date);
    }

    @Override
    public void addReport(Report report) {
        reportMapper.addReport(report);
    }

    @Override
    public Integer getUserId(String userName) {
        return reportMapper.getUserId(userName);
    }

    @Override
    public List<Report> getReport(Integer userId) {
        return reportMapper.getReport(userId);
    }

    @Override
    public DataInverter1 getDataInverterInDay(Map<String, String> condition) {
        return reportMapper.getDataInverterInDay(condition);
    }

    @Override
    public DataInverter1 getDataInverterInMonth(Map<String, String> condition) {
        return reportMapper.getDataInverterInMonth(condition);
    }

    @Override
    public DataInverter1 getDataInverterInYear(Map<String, String> condition) {
        return reportMapper.getDataInverterInYear(condition);
    }

    @Override
    public List<Report> getListByLimit(Map<String, Integer> map) {
        return reportMapper.getListByLimit(map);
    }

    @Override
    public String getProjectName(String projectId) {
        return reportMapper.getProjectName(projectId);
    }

    @Override
    public void updatePercent(Map<String, String> condition) {
        reportMapper.updatePercent(condition);

    }

    @Override
    public void updateStatus(Integer id) {
        reportMapper.updateStatus(id);
    }

    @Override
    public void updateTimeFinish(Integer id) {
        reportMapper.updateTimeFinish(id);
    }

    @Override
    public void deleteReport(Integer id) {
        reportMapper.deleteReport(id);

    }

}
