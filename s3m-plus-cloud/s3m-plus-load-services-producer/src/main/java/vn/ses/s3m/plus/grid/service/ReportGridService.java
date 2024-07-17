package vn.ses.s3m.plus.grid.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataRmuDrawer1;
import vn.ses.s3m.plus.dto.Report;
import vn.ses.s3m.plus.dto.Warning;

public interface ReportGridService {

    Integer getUserId(String userName);

    List<Report> getReport(Map<String, Object> condition);

    List<Map<String, Object>> getDevice(Map<String, String> condition);

    void deleteReport(Integer id);

    void addReport(Report report);

    List<DataRmuDrawer1> getdataRmuDrawer1Bylimit(Map<String, String> condition);

    List<Report> getListReportByLimit(Map<String, Integer> map);

    String getProjectName(String projectId);

    List<DataRmuDrawer1> getDataRmuDrawer1(Map<String, Object> condition);

    List<Warning> getWarningGrid(Map<String, Object> condition);

    List<Warning> getWarningGridByLimit(Map<String, String> condition);
}
