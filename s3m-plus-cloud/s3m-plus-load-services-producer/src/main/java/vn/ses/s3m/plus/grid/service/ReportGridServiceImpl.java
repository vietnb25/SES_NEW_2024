package vn.ses.s3m.plus.grid.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dto.DataRmuDrawer1;
import vn.ses.s3m.plus.dto.Report;
import vn.ses.s3m.plus.dto.Warning;
import vn.ses.s3m.plus.grid.dao.ReportMapperGrid;

@Service
public class ReportGridServiceImpl implements ReportGridService {

    @Autowired
    private ReportMapperGrid reportMapperGrid;

    @Override
    public Integer getUserId(String userName) {
        return reportMapperGrid.getUserId(userName);
    }

    @Override
    public List<Report> getReport(Map<String, Object> condition) {
        return reportMapperGrid.getReport(condition);
    }

    @Override
    public List<Map<String, Object>> getDevice(Map<String, String> condition) {
        return reportMapperGrid.getDevice(condition);
    }

    @Override
    public void deleteReport(Integer id) {
        reportMapperGrid.deleteReport(id);
    }

    @Override
    public void addReport(Report report) {
        reportMapperGrid.addReport(report);
    }

    @Override
    public List<DataRmuDrawer1> getdataRmuDrawer1Bylimit(Map<String, String> condition) {
        return reportMapperGrid.getdataRmuDrawer1Bylimit(condition);
    }

    @Override
    public List<Report> getListReportByLimit(Map<String, Integer> map) {
        return reportMapperGrid.getListReportByLimit(map);
    }

    @Override
    public String getProjectName(String projectId) {
        return reportMapperGrid.getProjectName(projectId);
    }

    @Override
    public List<DataRmuDrawer1> getDataRmuDrawer1(Map<String, Object> condition) {
        return reportMapperGrid.getDataRmuDrawer1(condition);
    }

    @Override
    public List<Warning> getWarningGrid(Map<String, Object> condition) {
        return reportMapperGrid.getWarningGrid(condition);
    }

    @Override
    public List<Warning> getWarningGridByLimit(Map<String, String> condition) {
        return reportMapperGrid.getWarningGridByLimit(condition);
    }

}
